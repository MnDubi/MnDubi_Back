package festival.dev.domain.gorupTDL.service.impl;

import festival.dev.domain.category.entity.Category;
import festival.dev.domain.category.repository.CategoryRepository;
import festival.dev.domain.friendship.repository.FriendshipRepository;
import festival.dev.domain.gorupTDL.entity.Group;
import festival.dev.domain.gorupTDL.entity.GroupJoin;
import festival.dev.domain.gorupTDL.entity.GroupList;
import festival.dev.domain.gorupTDL.entity.GroupNumber;
import festival.dev.domain.gorupTDL.presentation.dto.request.*;
import festival.dev.domain.gorupTDL.presentation.dto.response.GInsertRes;
import festival.dev.domain.gorupTDL.presentation.dto.response.GSuccessResponse;
import festival.dev.domain.gorupTDL.presentation.dto.response.GToDoListResponse;
import festival.dev.domain.gorupTDL.repository.GroupJoinRepo;
import festival.dev.domain.gorupTDL.repository.GroupListRepo;
import festival.dev.domain.gorupTDL.repository.GroupNumberRepo;
import festival.dev.domain.gorupTDL.repository.GroupRepository;
import festival.dev.domain.gorupTDL.service.GroupService;
import festival.dev.domain.user.entity.User;
import festival.dev.domain.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
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

    @Transactional
    public GInsertRes invite(GInsertRequest request, Long userID){
        User sender = getUser(userID);
        Long groupNum = insert(request,userID).getId();
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
        GroupNumber group = getGroupNum(request.getGroupNumber());
        checkInvite(group, receiver);
        groupListRepo.updateAccept(group.getId(), receiver.getId());
        GroupNumber groupNumber = getGroupNum(request.getGroupNumber());
        List<Group> tdls = groupRepository.findByGroupNumber(groupNumber);
        for (Group tdl: tdls){
            GroupJoin groupJoin = GroupJoin.builder()
                    .group(tdl)
                    .user(receiver)
                    .completed(false)
                    .groupNumber(groupNumber)
                    .build();
            groupJoinRepo.save(groupJoin);
        }
    }

    //초대를 응답하는 것이기 때문에 받은 사람은 나
    public void refuseInvite(GChoiceRequest request, Long userID){
        User receiver = getUser(userID);
        GroupNumber group = getGroupNum(request.getGroupNumber());
        checkInvite(group,receiver);
        groupListRepo.findByGroupNumberAndUserAndAccept(group, receiver, true)
                .ifPresent(groupList -> {
                    throw new IllegalArgumentException("이미 수락한 요청입니다.");
                });
        groupListRepo.deleteByGroupNumberAndUser(group,receiver);
    }

    public GToDoListResponse update(GUpdateRequest request, Long userID) {
        User user = getUser(userID);
        checkNotExist(user, request.getTitle(), request.getEndDate());
        checkExist(user, request.getChange(), request.getChangeDate());
        if(toDay().compareTo(request.getEndDate()) > 0)
            throw new IllegalArgumentException("이미 끝난 TDL은 변경이 불가능합니다.");

        groupRepository.changeTitle(request.getChange(), request.getTitle(), userID, request.getChangeDate(), request.getEndDate());

        Group toDoList = getGroupByTitleUserEndDate(request.getChange(), user, request.getChangeDate());

        return GToDoListResponse.builder()
                .title(toDoList.getTitle())
                .category(toDoList.getCategory().getName())
                .userID(user.getName())
                .endDate(toDoList.getEndDate())
                .groupNumber(toDoList.getGroupNumber().getGroupNumber())
                .startDate(toDoList.getStartDate())
                .build();
    }

    public void delete(GDeleteRequest request, Long userID){
        User user = getUser(userID);
        checkNotExist(user, request.getTitle(), request.getEndDate());
        groupRepository.deleteByUserAndTitleAndEndDate(user, request.getTitle(), request.getEndDate());
    }

    public GSuccessResponse success(GSuccessRequest request, Long userID){
        User sender = userRepository.findByUserCode(request.getSenderID()).orElseThrow(()->new IllegalArgumentException("그 유저는 없는 유저입니다."));
        String date = toDay();
        GroupNumber groupNumber = getGroupNum(request.getGroupNumber());
        Group group = getGroupByTitleUserEndDate(request.getTitle(),sender,date);
        User user = getUser(userID);
        GroupJoin groupJoin = groupJoinRepo.findByGroupAndGroupNumberAndUser(group,groupNumber,user).orElseThrow(()-> new IllegalArgumentException("TDL이 없습니다."));
        //test 해봐야함.
        groupJoinRepo.save(groupJoin.toBuilder().completed(request.getCompleted()).build());
        return GSuccessResponse.builder()
                .groupNumber(groupJoin.getGroupNumber().getGroupNumber())
                .endDate(groupJoin.getGroup().getEndDate())
                .category(groupJoin.getGroup().getCategory().getName())
                .ownerID(groupJoin.getGroup().getUser().getName())
                .title(groupJoin.getGroup().getTitle())
                .startDate(groupJoin.getGroup().getStartDate())
                .completed(groupJoin.isCompleted())
                .receiverID(user.getName())
                .build();
    }

    //----------------------------------------------------------------------------------------------------------------------------------------------비즈니스 로직을 위한 메소드들

    Group getGroupByTitleUserEndDate(String title, User user, String endDate){
        return groupRepository.findByUserAndTitleAndEndDate(user, title, endDate).orElseThrow(()-> new IllegalArgumentException("관련된 TDL이 없습니다."));
    }

    GroupNumber getGroupNum(Long groupID){
        return groupNumberRepo.findById(groupID).orElseThrow(()-> new IllegalArgumentException("그룹이 없습니다."));
    }

    public GInsertRes insert(GInsertRequest request, Long userID){
        User user = getUser(userID);
        Group response = new Group();

        Long groupNumber = groupNumberRepo.getMaxGroupNumber();
        if (groupNumber == null){
            groupNumber = 1L;
        }
        else{
            groupNumber += 1;
        }
        GroupNumber groupNumberBuild = GroupNumber.builder()
                .groupNumber(groupNumber)
                .build();
        groupNumberRepo.save(groupNumberBuild);
        for (String title: request.getTitles()) {
            Category category = categoryRepository.findByName(request.getCategory())
                    .orElseThrow(() -> new IllegalArgumentException("존재하지 않은 카테고리입니다."));
            inputSetting(title, user, request.getEndDate(), category);

            checkEndDate(request.getEndDate());

            Group group = Group.builder()
                    .groupNumber(groupNumberBuild)
                    .category(category)
                    .title(title)
                    .endDate(request.getEndDate())
                    .user(user)
                    .startDate(request.getEndDate())
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

        return GInsertRes.builder()
                .id(response.getGroupNumber().getId())
                .build();
    }

    void inviteFor(User sender, Long groupNum, List<String> receivers){
        GroupNumber groupNumber = getGroupNum(groupNum);
        for (String req_receiver : receivers){
            User receiver = userRepository.findByUserCode(req_receiver).orElseThrow(() -> new IllegalArgumentException("없는 유저 입니다."));
            friendshipRepository.findByRequesterAndAddressee(sender, receiver).orElseThrow(() -> new IllegalArgumentException("친구로 추가가 안 되어있습니다."));

            if (groupListRepo.existsByUserAndGroupNumber(receiver,groupNumber)){
                throw new IllegalArgumentException("이미 초대한 사람은 초대가 불가합니다.");
            }
            GroupList groupList = GroupList.builder()
                    .accept(false)
                    .groupNumber(groupNumber)
                    .user(receiver)
                    .build();
            groupListRepo.save(groupList);
        }
    }

    void checkInvite(GroupNumber groupNum, User receiver){
        groupListRepo.findByGroupNumberAndUser(groupNum, receiver).orElseThrow(()-> new IllegalArgumentException("초대받은 적이 없습니다."));
    }

    Group getGroup(Long groupID){
        return groupRepository.findById(groupID).orElseThrow(()-> new IllegalArgumentException("존재하지 않는 그룹입니다."));
    }

    String toDay(){
        LocalDateTime createAt = ZonedDateTime.now(ZoneId.of("Asia/Seoul")).toLocalDateTime();
        DateTimeFormatter yearMonthDayFormatter = DateTimeFormatter.ofPattern("yyyy.MM.dd");
        return createAt.format(yearMonthDayFormatter);
    }

    User getUser(Long id){
        return userRepository.findById(id).orElseThrow(() -> new IllegalArgumentException("존재하지 않은 UserID"));
    }

    void checkNotExist(User user, String title, String endDate){
        if (!groupRepository.existsByUserAndTitleAndEndDate(user,title, endDate)){
            throw new IllegalArgumentException("존재하지 않는 TDL입니다.");
        }
    }

    void checkExist(User user, String title,String endDate){
        if (groupRepository.existsByUserAndTitleAndEndDate(user,title, endDate)){
            throw new IllegalArgumentException("이미 존재하는 TDL입니다.");
        }
    }

    void inputSetting(String title, User user, String endDate, Category category) {
        checkExist(user, title, endDate);
        checkCategory(category);
    }

    void checkCategory(Category category){
        if (category == null) {
            throw new IllegalArgumentException("존재하지 않은 카테고리입니다.");
        }
    }

    void checkEndDate(String endDate){
        if (toDay().compareTo(endDate) > 0){
            throw new IllegalArgumentException("끝나는 날짜는 현재 날짜보다 빠를 수 없습니다.");
        }
    }
}