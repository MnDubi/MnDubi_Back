package festival.dev.domain.gorupTDL.service.impl;

import festival.dev.domain.calendar.entity.CTdlKind;
import festival.dev.domain.calendar.entity.Calendar;
import festival.dev.domain.calendar.entity.Calendar_tdl_ids;
import festival.dev.domain.calendar.repository.CalendarRepository;
import festival.dev.domain.category.entity.Category;
import festival.dev.domain.category.repository.CategoryRepository;
import festival.dev.domain.friendship.repository.FriendshipRepository;
import festival.dev.domain.gorupTDL.entity.*;
import festival.dev.domain.gorupTDL.presentation.dto.request.*;
import festival.dev.domain.gorupTDL.presentation.dto.response.*;
import festival.dev.domain.gorupTDL.repository.GroupJoinRepo;
import festival.dev.domain.gorupTDL.repository.GroupListRepo;
import festival.dev.domain.gorupTDL.repository.GroupNumberRepo;
import festival.dev.domain.gorupTDL.repository.GroupRepository;
import festival.dev.domain.gorupTDL.service.GroupService;
import festival.dev.domain.user.entity.User;
import festival.dev.domain.user.repository.UserRepository;
import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.*;

@Service
@RequiredArgsConstructor
public class GroupServiceImpl implements GroupService {
    private final Map<Long, CopyOnWriteArrayList<SseEmitter>> groupEmitters = new ConcurrentHashMap<>();
    private final Map<String, CopyOnWriteArrayList<SseEmitter>> groupInviteEmitters = new ConcurrentHashMap<>();
    private final FriendshipRepository friendshipRepository;
    private final UserRepository userRepository;
    private final GroupRepository groupRepository;
    private final GroupListRepo groupListRepo;
    private final CategoryRepository categoryRepository;
    private final GroupNumberRepo groupNumberRepo;
    private final GroupJoinRepo groupJoinRepo;
    private final CalendarRepository calendarRepository;
    private final Logger logger = LoggerFactory.getLogger(GroupServiceImpl.class);
    private final ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(2);

    @Transactional
    public GInsertRes invite(GCreateRequest request, Long userID){
        User sender = getUser(userID);
        Long groupNum = create(request,userID);
        inviteFor(sender,groupNum,request.getReceivers());
        return GInsertRes.builder()
                .groupNumber(groupNum)
                .build();
    }

    @Transactional
    public void invite(GInviteReq request, Long userID){
        User sender = getUser(userID);
        Long groupId = request.getGroupNumber();
        inviteFor(sender,groupId,request.getReceivers());
    }

    // SSE 필요
    @Transactional
    public void acceptInvite(Long userID, GChoiceReq req){
        User receiver = getUser(userID);
        GroupNumber groupNum = groupNumberRepo.findById(req.getGroupNumber()).orElseThrow(()-> new IllegalArgumentException("없는 그룹방입니다."));
        checkInvite(groupNum, receiver);
        groupListRepo.updateAccept(groupNum.getId(), receiver.getId());
        List<Group> tdls = groupRepository.findByGroupNumber(groupNum);
        for (Group tdl: tdls){
            GroupJoin groupJoin = GroupJoin.builder()
                    .group(tdl)
                    .user(receiver)
                    .completed(false)
                    .groupNumber(groupNum)
                    .build();

            groupJoinRepo.save(groupJoin);
        }
        List<SseEmitter> emitters = groupEmitters.get(groupNum.getId());
        MemberDto response = MemberDto.builder()
                .userCode(receiver.getUserCode())
                .email(receiver.getEmail())
                .name(receiver.getName())
                .build();
        for (SseEmitter emitter : emitters) {
            try {
                emitter.send(SseEmitter.event()
                        .name("group-member")
                        .data(response));
            } catch (IOException e) {
                emitters.remove(emitter);
            }
        }

    }

    // SSE 필요
    @Transactional
    public void refuseInvite(Long userID, GChoiceReq req){
        User receiver = getUser(userID);
        GroupList groupList = getGroupListByUser(receiver);
        GroupNumber groupNumber = groupNumberRepo.findById(req.getGroupNumber()).orElseThrow(()-> new IllegalArgumentException("없는 그룹방입니다."));
        checkInvite(groupNumber,receiver);
        groupListRepo.findByGroupNumberAndUserAndAccept(groupNumber, receiver, true)
                .ifPresent(GroupList -> {
                    throw new IllegalArgumentException("이미 수락한 요청입니다.");
                });
        groupListRepo.deleteByGroupNumberAndUser(groupNumber,receiver);
    }

    @Transactional
    public GToDoListResponse update(GUpdateRequest request, Long userID) {
        User user = getUser(userID);
        checkNotExist(user, request.getTitle());
        checkExist(user, request.getChange());

        groupRepository.changeTitle(request.getChange(), request.getTitle(), userID);
        Group toDoList = getGroupByTitleUser(request.getChange(), user);
        Long groupNum = toDoList.getGroupNumber().getId();

        List<SseEmitter> emitters = groupEmitters.get(groupNum);
        GToDoListResponse response = GToDoListResponse.builder()
                .title(toDoList.getTitle())
                .category(toDoList.getCategory().getCategoryName())
                .ownerName(user.getName())
                .groupNumber(groupNum)
                .build();
        for (SseEmitter emitter : emitters) {
            try {
                emitter.send(SseEmitter.event()
                        .name("group")
                        .data(response));
            } catch (IOException e) {
                emitters.remove(emitter); // 전송 실패하면 제거
            }
        }

        return  response;
    }

    @Transactional
    public void delete(GDeleteRequest request, Long userID){
        User user = getUser(userID);
        checkNotExist(user, request.getTitle());
        Group group = getGroupByTitleUser(request.getTitle(),user);
        Long groupNumber = group.getGroupNumber().getId();
        groupRepository.deleteByUserAndTitle(user, request.getTitle());
        GToDoListResponse response = GToDoListResponse.builder()
                .title(group.getTitle())
                .category(group.getCategory().getCategoryName())
                .ownerName(user.getName())
                .groupNumber(groupNumber)
                .build();

        List<SseEmitter> emitters = groupEmitters.get(groupNumber);
        for (SseEmitter emitter : emitters) {
            try {
                emitter.send(SseEmitter.event()
                        .name("group-delete")
                        .data(response));
            } catch (IOException e) {
                emitters.remove(emitter); // 전송 실패하면 제거
            }
        }
    }

    @Transactional
    public GResponse success(GSuccessRequest request, Long userID){
        User sender = userRepository.findByUserCode(request.getOwnerID()).orElseThrow(()->new IllegalArgumentException("그 유저는 없는 유저입니다."));
        GroupList groupList = getGroupListByUser(sender);
        GroupNumber groupNumber = getGroupNum(groupList.getGroupNumber().getId());
        User user = getUser(userID);
        Group group = getGroupByTitleUser(request.getTitle(),user);
        GroupJoin groupJoin = groupJoinRepo.findByGroupAndGroupNumberAndUser(group,groupNumber,user).orElseThrow(()-> new IllegalArgumentException("TDL이 없습니다."));
        groupJoinRepo.save(groupJoin.toBuilder().completed(request.getCompleted()).build());

        GResponse response = GResponse.builder()
                .groupNumber(groupJoin.getGroupNumber().getId())
                .category(groupJoin.getGroup().getCategory().getCategoryName())
                .ownerName(groupJoin.getGroup().getUser().getName())
                .title(groupJoin.getGroup().getTitle())
                .completed(groupJoin.isCompleted())
                .memberName(user.getName())
                .build();
        List<SseEmitter> emitters = groupEmitters.get(groupNumber.getId());
        for (SseEmitter emitter : emitters) {
            try {
                emitter.send(SseEmitter.event()
                        .name("group-detail")
                        .data(response));
            } catch (IOException e) {
                emitters.remove(emitter);
            }
        }
        return response;
    }

    @Transactional
    public Long insert(GInsertRequest request, Long userID){
        User user = getUser(userID);
        checkExist(user, request.getTitle());
        GroupList groupList = getGroupListByUser(user);
        GroupNumber groupNumber = getGroupNum(groupList.getGroupNumber().getId());
        Category category = categoryRepository.findByCategoryName(request.getCategory());
        List<GroupList> GroupLists = groupListRepo.findByGroupNumberAndAccept(groupNumber,true);
        Group group = Group.builder()
                .user(user)
                .category(category)
                .title(request.getTitle())
                .groupNumber(groupNumber)
                .build();
        groupRepository.save(group);
        for(GroupList GroupList : GroupLists){
            GroupJoin groupJoin = GroupJoin.builder()
                    .group(group)
                    .user(GroupList.getUser())
                    .completed(false)
                    .groupNumber(groupNumber)
                    .build();
            groupJoinRepo.save(groupJoin);
        }
        GResponse response = GResponse.builder()
                .title(group.getTitle())
                .category(group.getCategory().getCategoryName())
                .ownerName(user.getName())
                .memberName(user.getName())
                .completed(false)
                .build();
        List<SseEmitter> emitters = groupEmitters.get(groupNumber.getId());
        for (SseEmitter emitter : emitters) {
            try {
                emitter.send(SseEmitter.event()
                        .name("group-detail")
                        .data(response));
            } catch (IOException e) {
                emitters.remove(emitter);
            }
        }
        return groupNumber.getId();
    }

    public GGetRes get(Long userID){
        User user = getUser(userID);
        String ownerName = "";
        String ownerCode = "";
        GroupList GroupList = groupListRepo.findByUserAndAcceptTrue(user).orElseThrow(()-> new IllegalArgumentException("그룹에 참가하지 않은 유저입니다."));
        GroupNumber groupNumber = GroupList.getGroupNumber();
        Long all = groupJoinRepo.countByUserAndGroupNumber(user,groupNumber);
        Long part = groupJoinRepo.countByCompletedAndUserAndGroupNumber(true,user,groupNumber);
        List<Group> groups = groupRepository.findByGroupNumber(groupNumber);
        GGetRes response = GGetRes.builder()
                .all(all)
                .part(part)
                .name(user.getName()).build();
        List<GetSup> getSups = new ArrayList<>();
        for(Group group: groups){
            GroupJoin groupJoin = groupJoinRepo.findByGroupAndGroupNumberAndUser(group,groupNumber,user).orElseThrow(()-> new IllegalArgumentException("없는 TDL입니다."));
            Long tdlAll = groupJoinRepo.countByGroup(group);
            Long tdlPart = groupJoinRepo.countByCompletedAndGroup(true,group);
            GetSup getSup = GetSup.builder()
                    .title(group.getTitle())
                    .category(group.getCategory().getCategoryName())
                    .completed(groupJoin.isCompleted())
                    .groupNumber(groupNumber.getId())
                    .all(tdlAll)
                    .part(tdlPart)
                    .tdlID(group.getId())
                    .build();
            getSups.add(getSup);
            ownerName = group.getUser().getName();
            ownerCode = group.getUser().getUserCode();
        }
        return response.toBuilder()
                .getSups(getSups)
                .ownerName(ownerName)
                .ownerCode(ownerCode).build();
    }

    public void finish(){
        List<User> users = userRepository.findAll();
        for(User user : users) {
            Optional<GroupList> optionalGroupList = groupListRepo.findByUserAndAcceptTrue(user);
            if(optionalGroupList.isEmpty()){
                continue;
            }
            GroupList groupList = optionalGroupList.get();
            GroupNumber groupNum = getGroupNum(groupList.getGroupNumber().getId());
            List<GroupJoin> groupJoins = groupJoinRepo.findByGroupNumberAndUser(groupNum, user);
            List<Calendar_tdl_ids> tdlIds = new ArrayList<>();
            List<GroupCalendar> groupCalendars = new ArrayList<>();
            Long all = groupJoinRepo.countByUserAndGroupNumber(user, groupNum);
            Long part = groupJoinRepo.countByCompletedAndUserAndGroupNumber(true, user, groupNum);

            for (GroupJoin groupjoin : groupJoins) {
                Group group = groupjoin.getGroup();
                Calendar_tdl_ids tdlId = Calendar_tdl_ids.builder()
                        .kind(CTdlKind.GROUP)
                        .tdlID(group.getId())
                        .build();
                tdlIds.add(tdlId);

                GroupCalendar groupCalendar = GroupCalendar.builder()
                        .title(group.getTitle())
                        .category(group.getCategory().getId())
                        .completed(groupjoin.isCompleted())
                        .build();
                groupCalendars.add(groupCalendar);
            }
            Calendar calendar = Calendar.builder()
                    .toDoListId(tdlIds)
                    .user(user)
                    .every(all.intValue())
                    .part(part.intValue())
                    .kind("GROUP")
                    .groupCalendarId(groupCalendars)
                    .build();
            calendarRepository.save(calendar);
        }
    }

    @Transactional
    public void deleteAll(Long userID){
        User user = getUser(userID);
        GroupList groupList = getGroupListByUser(user);
        GroupNumber groupNumber = getGroupNum(groupList.getGroupNumber().getId());
        groupNumberRepo.deleteById(groupNumber.getId());
        groupRepository.deleteAllByGroupNumberAndUser(groupNumber,user);

        List<SseEmitter> emitters = groupEmitters.get(groupNumber.getId());
        for (SseEmitter emitter : emitters) {
            try {
                emitter.send(SseEmitter.event()
                        .name("group-delete-all")
                        .data(groupNumber.getId()));
            } catch (IOException e) {
                emitters.remove(emitter);
            }
        }
    }

    public List<GInviteGet> inviteGet(Long userID){
        User user = getUser(userID);
        List<GroupList> invitedList = groupListRepo.findByUserAndAcceptFalse(user);
        List<GInviteGet> response = new ArrayList<>();
        for(GroupList groupList: invitedList){
            GroupNumber groupNumber = groupList.getGroupNumber();
            User sender = groupRepository.findUserByGroupNumber(groupNumber);

            response.add(GInviteGet.builder()
                    .accepted(groupList.isAccept())
                    .receiver(user.getName())
                    .sender(sender.getName())
                    .groupNumber(groupNumber.getId())
                    .build());
        }
        return response;
    }

    @Transactional
    @Scheduled(cron = "59 59 23 * * *")
    public void reset(){
        finish();
        groupJoinRepo.updateAllFalse();
    }

    public List<GCreateWsRes> userList(Long userid){
        User user = getUser(userid);
        List<GCreateWsRes> userList = new ArrayList<>();
        GroupList GroupList = groupListRepo.findByUserAndAcceptTrue(user).orElseThrow(()-> new IllegalArgumentException("그룹에 참가하지 않은 유저입니다."));
        GroupNumber groupNumber = getGroupNum(GroupList.getGroupNumber().getId());
        List<GroupList> groupLists = groupListRepo.findByGroupNumberAndAccept(groupNumber,true);
        for (GroupList groupList : groupLists) {
            GCreateWsRes gCreateWsRes = GCreateWsRes.builder()
                    .userCode(groupList.getUser().getUserCode())
                    .email(groupList.getUser().getEmail())
                    .name(groupList.getUser().getName())
                    .build();
            userList.add(gCreateWsRes);
        }

        return userList;
    }

    public SseEmitter sseConnect(Long groupNumber){
        SseEmitter emitter = new SseEmitter(300 * 1000L);
        groupEmitters.putIfAbsent(groupNumber, new CopyOnWriteArrayList<>());
        groupEmitters.get(groupNumber).add(emitter);

        emitter.onCompletion(() -> groupEmitters.get(groupNumber).remove(emitter));
        emitter.onTimeout(() -> groupEmitters.get(groupNumber).remove(emitter));
        emitter.onError(e -> groupEmitters.get(groupNumber).remove(emitter));

        try {
            emitter.send(SseEmitter.event().name("connected").data("SSE 연결됨 (그룹: " + groupNumber + ")"));
        } catch (IOException e) {
            groupEmitters.get(groupNumber).remove(emitter);
        }
        return emitter;
    }

    //----------------------------------------------------------------------------------------------------------------------------------------------비즈니스 로직을 위한 메소드들
    GroupList getGroupListByUser(User user){
        return groupListRepo.findByUser(user).orElseThrow(()-> new IllegalArgumentException("GroupList에 없습니다."));
    }

    Group getGroupByTitleUser(String title, User user){
        return groupRepository.findByUserAndTitle(user, title).orElseThrow(()-> new IllegalArgumentException("관련된 TDL이 없습니다."));
    }

    GroupNumber getGroupNum(Long groupID){
        return groupNumberRepo.findById(groupID).orElseThrow(()-> new IllegalArgumentException("그룹이 없습니다."));
    }

    Long create(GCreateRequest request, Long userID){
        User user = getUser(userID);
        Group response = new Group();

        Long groupNumber = 1L;

        GroupNumber groupNumberBuild = GroupNumber.builder()
                .groupNumber(groupNumber)
                .build();

        GroupList groupList = GroupList.builder()
                .user(user)
                .accept(true)
                .groupNumber(groupNumberBuild)
                .build();
        groupListRepo.save(groupList);

        groupNumberRepo.save(groupNumberBuild);
        for (String title: request.getTitles()) {
            Category category = categoryRepository.findByCategoryName(request.getCategory());
            inputSetting(title, user, category);

            Group group = Group.builder()
                    .groupNumber(groupNumberBuild)
                    .category(category)
                    .title(title)
                    .user(user)
                    .build();
            GroupJoin groupJoin = GroupJoin.builder()
                    .groupNumber(groupNumberBuild)
                    .completed(false)
                    .user(user)
                    .group(group)
                    .build();
            groupJoinRepo.save(groupJoin);
            response = groupRepository.save(group);
        }

        return response.getGroupNumber().getId();
    }

    void inviteFor(User sender, Long groupNum, List<String> receivers){
        GroupNumber groupNumber = getGroupNum(groupNum);

        for (String req_receiver : receivers){
            User receiver = userRepository.findByUserCode(req_receiver).orElseThrow(() -> new IllegalArgumentException("없는 유저 입니다."));
            logger.info(receiver.getName());

//            if (!friendshipRepository.existsByRequesterAndAddressee(sender, receiver) && !friendshipRepository.existsByRequesterAndAddressee(receiver, sender)) {
//                throw new IllegalArgumentException("친구로 추가가 안 되어있습니다.");
//            }
//
//            if (groupListRepo.existsByGroupNumberAndUser(groupNumber,receiver)){
//                throw new IllegalArgumentException("이미 초대한 사람은 초대가 불가합니다.");
//            }
//
//            if (groupListRepo.existsByAcceptTrueAndUser(receiver)){
//                throw new IllegalArgumentException("이미 그룹에 포함된 사람은 초대가 불가합니다.");
//            }

            validateInviteConditions(sender, receiver, groupNumber);

            GroupList groupList = GroupList.builder()
                    .accept(false)
                    .groupNumber(groupNumber)
                    .user(receiver)
                    .build();
            GroupInviteDto groupInviteDto = GroupInviteDto.builder()
                    .userName(sender.getName())
                    .groupNumber(groupNumber.getId())
                    .accept(false)
                    .build();

            List<SseEmitter> emitters = groupInviteEmitters.get(req_receiver);
            for (SseEmitter emitter : emitters) {
                try {
                    emitter.send(SseEmitter.event()
                            .name("group-invite")
                            .data(groupInviteDto));
                } catch (IOException e) {
                    emitters.remove(emitter);
                }
            }
            groupListRepo.save(groupList);
        }
    }

    void checkInvite(GroupNumber groupNum, User receiver){
        groupListRepo.findByGroupNumberAndUser(groupNum, receiver).orElseThrow(()-> new IllegalArgumentException("초대받은 적이 없습니다."));
    }

    User getUser(Long id){
        return userRepository.findById(id).orElseThrow(() -> new IllegalArgumentException("존재하지 않은 UserID"));
    }

    void checkNotExist(User user, String title){
        if (!groupRepository.existsByUserAndTitle(user,title)){
            throw new IllegalArgumentException("존재하지 않는 TDL입니다.");
        }
    }

    public SseEmitter groupInviteSseConnect(String userCode) {
        SseEmitter emitter = new SseEmitter(300 * 1000L);
        groupInviteEmitters.putIfAbsent(userCode, new CopyOnWriteArrayList<>());
        groupInviteEmitters.get(userCode).add(emitter);

        emitter.onCompletion(() -> groupInviteEmitters.get(userCode).remove(emitter));
        emitter.onTimeout(() -> groupInviteEmitters.get(userCode).remove(emitter));
        emitter.onError(e -> groupInviteEmitters.get(userCode).remove(emitter));

        try {
            emitter.send(SseEmitter.event().name("connected").data("SSE 연결됨 (유저 코드: " + userCode + ")"));
        } catch (IOException e) {
            groupInviteEmitters.get(userCode).remove(emitter);
        }
        return emitter;
    }

    public void findByUsername(String userCode, String friendUsername) {
        List<User> friends = userRepository.findByName(friendUsername);
        User user = userRepository.findByUserCode(userCode).orElseThrow(() -> new IllegalArgumentException("존재하지 않는 유저입니다."));
        List<String> userCodes = new ArrayList<>();
        for (User friend : friends) {
            if (friendshipRepository.existsByRequesterAndAddressee(user, friend)) {
                userCodes.add(friend.getUserCode());
            } else if (friendshipRepository.existsByRequesterAndAddressee(friend, user)) {
                userCodes.add(friend.getUserCode());
            }
        }
        List<SseEmitter> emitters = groupInviteEmitters.get(userCode);

        if (userCodes.isEmpty()) {
            for (SseEmitter emitter : emitters) {
                try {
                    emitter.send(SseEmitter.event()
                            .name("error")
                            .data("그런 친구는 존재하지 않습니다."));
                } catch (IOException e) {
                    emitters.remove(emitter);
                }
            }        }
        else {
            for (SseEmitter emitter : emitters) {
                try {
                    emitter.send(SseEmitter.event()
                            .name("friend-list")
                            .data(userCodes));
                } catch (IOException e) {
                    emitters.remove(emitter);
                }
            }
        }
    }

    void checkExist(User user, String title){
        if (groupRepository.existsByUserAndTitle(user,title)){
            throw new IllegalArgumentException("이미 존재하는 TDL입니다.");
        }
    }

    void inputSetting(String title, User user, Category category) {
        checkExist(user, title);
        checkCategory(category);
    }

    void checkCategory(Category category){
        if (category == null) {
            throw new IllegalArgumentException("존재하지 않은 카테고리입니다.");
        }
    }

    @PostConstruct
    public void startPingTask() {
        scheduler.scheduleAtFixedRate(() -> {
            for (Map.Entry<Long, CopyOnWriteArrayList<SseEmitter>> entry : groupEmitters.entrySet()) {
                List<SseEmitter> emitters = entry.getValue();
                for (SseEmitter emitter : emitters) {
                    try {
                        emitter.send(SseEmitter.event()
                                .name("ping")
                                .data("keepalive"));
                    } catch (IOException e) {
                        logger.info("Ping 실패로 emitter 제거: {}", e.getMessage());
                        emitter.completeWithError(e);
                        emitters.remove(emitter);
                    }
                }
            }
        }, 10, 30, TimeUnit.SECONDS);

        scheduler.scheduleAtFixedRate(() -> {
            for (Map.Entry<String, CopyOnWriteArrayList<SseEmitter>> entry : groupInviteEmitters.entrySet()) {
                List<SseEmitter> emitters = entry.getValue();
                for (SseEmitter emitter : emitters) {
                    try {
                        emitter.send(SseEmitter.event()
                                .name("ping")
                                .data("keepalive"));
                    } catch (IOException e) {
                        logger.info("Ping 실패로 emitter 제거: {}", e.getMessage());
                        emitter.completeWithError(e);
                        emitters.remove(emitter);
                    }
                }
            }
        }, 10, 30, TimeUnit.SECONDS);
    }

    @PreDestroy
    public void shutdown() {
        scheduler.shutdown();
    }

    private void validateInviteConditions(User sender, User receiver, GroupNumber groupNumber) {
        if (!friendshipRepository.existsByRequesterAndAddressee(sender, receiver) &&
                !friendshipRepository.existsByRequesterAndAddressee(receiver, sender)) {
            throw new IllegalArgumentException("친구로 추가가 안 되어있습니다.");
        }

        if (groupListRepo.existsByGroupNumberAndUser(groupNumber, receiver)) {
            throw new IllegalArgumentException("이미 초대한 사람은 초대가 불가합니다.");
        }

        if (groupListRepo.existsByAcceptTrueAndUser(receiver)) {
            throw new IllegalArgumentException("이미 그룹에 포함된 사람은 초대가 불가합니다.");
        }
    }
}