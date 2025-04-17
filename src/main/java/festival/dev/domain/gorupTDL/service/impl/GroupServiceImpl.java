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
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class GroupServiceImpl implements GroupService {

    private final FriendshipRepository friendshipRepository;
    private final UserRepository userRepository;
    private final GroupRepository groupRepository;
    private final GroupListRepo groupListRepo;
    private final CategoryRepository categoryRepository;
    private final GroupNumberRepo groupNumberRepo;
    private final GroupJoinRepo groupJoinRepo;
    private final SimpMessagingTemplate messagingTemplate;
    private final CalendarRepository calendarRepository;

    @Transactional
    public GInsertRes invite(GCreateRequest request, Long userID){
        User sender = getUser(userID);
        Long groupNum = create(request,userID);
        inviteFor(sender,groupNum,request.getReceivers());
        return GInsertRes.builder()
                .id(groupNum)
                .build();
    }

    @Transactional
    public void invite(GInviteReq request, Long userID){
        User sender = getUser(userID);
        Long groupId = request.getGroupID();
        inviteFor(sender,groupId,request.getReceivers());
    }

    //초대를 응답하는 것이기 때문에 받은 사람은 나
    @Transactional
    public void acceptInvite(GChoiceRequest request, Long userID){
        User receiver = getUser(userID);
        GroupNumber groupNum = getGroupNum(request.getGroupNumber());
        checkInvite(groupNum, receiver);
        groupListRepo.updateAccept(groupNum.getId(), receiver.getId());
        GroupNumber groupNumber = getGroupNum(request.getGroupNumber());
        List<Group> tdls = groupRepository.findByGroupNumber(groupNumber);
        for (Group tdl: tdls){
            GroupJoin groupJoin = GroupJoin.builder()
                    .group(tdl)
                    .user(receiver)
                    .completed(false)
                    .groupNumber(groupNumber)
                    .build();

            GResponse response = GResponse.builder()
                    .groupNumber(groupNumber.getId())
                    .ownerID(tdl.getUser().getName())
                    .memberID(receiver.getName())
                    .title(tdl.getTitle())
                    .category(tdl.getCategory().getCategoryName())
                    .completed(false)
                    .build();
            messagingTemplate.convertAndSend("/topic/group/" + groupNumber.getId(), response);
            groupJoinRepo.save(groupJoin);
        }
    }

    //초대를 응답하는 것이기 때문에 받은 사람은 나
    @Transactional
    public void refuseInvite(GChoiceRequest request, Long userID){
        User receiver = getUser(userID);
        GroupNumber group = getGroupNum(request.getGroupNumber());
        checkInvite(group,receiver);
        groupListRepo.findByGroupNumberAndUserAndAccept(group, receiver, true)
                .ifPresent(GroupList -> {
                    throw new IllegalArgumentException("이미 수락한 요청입니다.");
                });
        groupListRepo.deleteByGroupNumberAndUser(group,receiver);
    }

    //바뀐 TDL이랑 관련된 모든 데이터를 보내야 할 듯? web socket으로
    @Transactional
    public GToDoListResponse update(GUpdateRequest request, Long userID) {
        User user = getUser(userID);
        checkNotExist(user, request.getTitle());
        checkExist(user, request.getChange());

        groupRepository.changeTitle(request.getChange(), request.getTitle(), userID);

        Group toDoList = getGroupByTitleUser(request.getChange(), user);

        return GToDoListResponse.builder()
                .title(toDoList.getTitle())
                .category(toDoList.getCategory().getCategoryName())
                .userID(user.getName())
                .groupNumber(toDoList.getGroupNumber().getGroupNumber())
                .build();
    }

    @Transactional
    public void delete(GDeleteRequest request, Long userID){
        User user = getUser(userID);
        checkNotExist(user, request.getTitle());
        Group group = getGroupByTitleUser(request.getTitle(),user);
        messagingTemplate.convertAndSend("/topic/group/" + group.getGroupNumber()+"/deleted", group.getId());
        groupRepository.deleteByUserAndTitle(user, request.getTitle());
    }

    @Transactional
    public GResponse success(GSuccessRequest request, Long userID){
        User sender = userRepository.findByUserCode(request.getOwnerID()).orElseThrow(()->new IllegalArgumentException("그 유저는 없는 유저입니다."));
        GroupNumber groupNumber = getGroupNum(request.getGroupNumber());
        Group group = getGroupByTitleUser(request.getTitle(),sender);
        User user = getUser(userID);
        GroupJoin groupJoin = groupJoinRepo.findByGroupAndGroupNumberAndUser(group,groupNumber,user).orElseThrow(()-> new IllegalArgumentException("TDL이 없습니다."));
        groupJoinRepo.save(groupJoin.toBuilder().completed(request.getCompleted()).build());

        GResponse response = GResponse.builder()
                .groupNumber(groupJoin.getGroupNumber().getId())
                .category(groupJoin.getGroup().getCategory().getCategoryName())
                .ownerID(groupJoin.getGroup().getUser().getName())
                .title(groupJoin.getGroup().getTitle())
                .completed(groupJoin.isCompleted())
                .memberID(user.getName())
                .build();
        messagingTemplate.convertAndSend("/topic/group/"+response.getGroupNumber(), response);
        return response;
    }

    @Transactional
    public Long insert(GInsertRequest request, Long userID){
        User user = getUser(userID);
        checkExist(user, request.getTitle());
        GroupNumber groupNumber = getGroupNum(request.getGroupNumber());
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
                .ownerID(user.getName())
                .memberID(user.getName())
                .completed(false)
                .build();
        messagingTemplate.convertAndSend("/topic/group/" + groupNumber.getId(), response);
        return groupNumber.getId();
    }

    public GGetRes get(Long userID){
        User user = getUser(userID);
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
            Long tdlAll = groupJoinRepo.countByGroup(group);
            Long tdlPart = groupJoinRepo.countByCompletedAndGroup(true,group);
            GetSup getSup = GetSup.builder()
                    .title(group.getTitle())
                    .category(group.getCategory().getCategoryName())
                    .userID(group.getUser().getName())
                    .groupNumber(groupNumber.getId())
                    .all(tdlAll)
                    .part(tdlPart)
                    .tdlID(group.getId())
                    .build();
            getSups.add(getSup);
        }
        return response.toBuilder()
                .getSups(getSups)
                .build();
    }

    @Transactional
    public void finish(Long userID,Long groupNumber){
        GroupNumber groupNum = getGroupNum(groupNumber);
        User user = getUser(userID);
        List<GroupJoin> groupJoins = groupJoinRepo.findByGroupNumberAndUser(groupNum,user);
        List<Calendar_tdl_ids> tdlIds = new ArrayList<>();
        List<GroupCalendar> groupCalendars = new ArrayList<>();
        Long all = groupJoinRepo.countByUserAndGroupNumber(user,groupNum);
        Long part = groupJoinRepo.countByCompletedAndUserAndGroupNumber(true,user,groupNum);

        for(GroupJoin groupjoin: groupJoins){
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
                .groupCalendarId(groupCalendars)
                .build();
        calendarRepository.save(calendar);
    }

    @Transactional
    public void deleteAll(Long userID, GChoiceRequest request){
        User user = getUser(userID);
        GroupNumber groupNumber = getGroupNum(request.getGroupNumber());
        groupNumberRepo.deleteById(groupNumber.getId());
        groupRepository.deleteAllByGroupNumberAndUser(groupNumber,user);
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

    //----------------------------------------------------------------------------------------------------------------------------------------------비즈니스 로직을 위한 메소드들

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
            if (friendshipRepository.findByRequesterAndAddressee(sender, receiver).isEmpty())
                friendshipRepository.findByRequesterAndAddressee(receiver, sender).orElseThrow(()-> new IllegalArgumentException("친구로 추가가 안 되어있습니다."));
            if (groupListRepo.existsByUserAndGroupNumber(receiver,groupNumber)){
                throw new IllegalArgumentException("이미 초대한 사람은 초대가 불가합니다.");
            }
            GroupList groupList = GroupList.builder()
                    .accept(false)
                    .groupNumber(groupNumber)
                    .user(receiver)
                    .build();
            messagingTemplate.convertAndSend("/topic/invite/"+req_receiver,groupNumber.getId());
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
}