package festival.dev.domain.gorupTDL.service.impl;

import festival.dev.domain.category.entity.Category;
import festival.dev.domain.category.repository.CategoryRepository;
import festival.dev.domain.friendship.repository.FriendshipRepository;
import festival.dev.domain.gorupTDL.entity.Group;
import festival.dev.domain.gorupTDL.entity.GroupList;
import festival.dev.domain.gorupTDL.entity.GroupNumber;
import festival.dev.domain.gorupTDL.presentation.dto.request.*;
import festival.dev.domain.gorupTDL.presentation.dto.response.GInsertRes;
import festival.dev.domain.gorupTDL.presentation.dto.response.GListDto;
import festival.dev.domain.gorupTDL.presentation.dto.response.GToDoListResponse;
import festival.dev.domain.gorupTDL.repository.GroupListRepo;
import festival.dev.domain.gorupTDL.repository.GroupNumberRepo;
import festival.dev.domain.gorupTDL.repository.GroupRepository;
import festival.dev.domain.gorupTDL.service.GroupService;
import festival.dev.domain.user.entity.User;
import festival.dev.domain.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;

@Service
@RequiredArgsConstructor
public class GroupServiceImpl implements GroupService {

    private final FriendshipRepository friendshipRepository;
    private final UserRepository userRepository;
    private final GroupRepository groupRepository;
    private final GroupListRepo groupListRepo;
    private final CategoryRepository categoryRepository;
    private final GroupNumberRepo groupNumberRepo;

    public void invite(GInsertRequest request, Long userID){
        User sender = getUser(userID);
        for (String req_receiver: request.getReceivers()) {
            User receiver = userRepository.findByUserCode(req_receiver).orElseThrow(() -> new IllegalArgumentException("없는 존재 입니다."));

            friendshipRepository.findByRequesterAndAddressee(sender, receiver).orElseThrow(() -> new IllegalArgumentException("친구로 추가가 안 되어있습니다."));

            Group group = getGroup(insert(request,userID).getId());
            GroupList groupList = GroupList.builder()
                    .accept(false)
                    .group(group)
                    .user(receiver)
                    .build();
            groupListRepo.save(groupList);
        }
    }


    //초대를 응답하는 것이기 때문에 받은 사람은 나
    public void acceptInvite(GChoiceRequest request, Long userID){
        User receiver = getUser(userID);
        Group group = getGroup(request.getGroupID());
        checkInvite(group, receiver);
        groupListRepo.updateAccept(group.getId(), receiver.getId());
    }

    //초대를 응답하는 것이기 때문에 받은 사람은 나
    public void refuseInvite(GChoiceRequest request, Long userID){
        User receiver = getUser(userID);
        Group group = getGroup(request.getGroupID());
        checkInvite(group,receiver);
        groupListRepo.findByGroupAndUserAndAccept(group, receiver, true)
                .ifPresent(groupList -> {
                    throw new IllegalArgumentException("이미 수락한 요청입니다.");
                });
        groupListRepo.deleteByGroupAndUser(group,receiver);
    }

    public GToDoListResponse update(GUpdateRequest request, Long userID) {
        User user = getUser(userID);
        checkNotExist(user, request.getTitle(), request.getEndDate());
        checkExist(user, request.getChange(), request.getChangeDate());
        if(toDay().compareTo(request.getEndDate()) > 0)
            throw new IllegalArgumentException("이미 끝난 TDL은 변경이 불가능합니다.");

        groupRepository.changeTitle(request.getChange(), request.getTitle(), userID, request.getChangeDate(), request.getEndDate());

        Group toDoList = groupRepository.findByUserAndTitleAndEndDate(user, request.getChange(), request.getChangeDate());

        return GToDoListResponse.builder()
                .title(toDoList.getTitle())
                .completed(toDoList.getCompleted())
                .category(toDoList.getCategory().getCategoryName())
                .userID(user.getName())
                .endDate(toDoList.getEndDate())
                .startDate(toDoList.getStartDate())
                .build();
    }

    public void delete(GDeleteRequest request, Long userID){
        User user = getUser(userID);
        checkNotExist(user, request.getTitle(), request.getEndDate());
        groupRepository.deleteByUserAndTitleAndEndDate(user, request.getTitle(), request.getEndDate());
    }

    public void numberDelete(){
        groupNumberRepo.deleteUnusedGroupNumbers();
    }

    //비즈니스 로직을 위한 메소드들
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
            Category category = categoryRepository.findByCategoryName(request.getCategory());
            inputSetting(title, user, request.getEndDate(), category);

            checkEndDate(request.getEndDate());

            Group group = Group.builder()
                    .groupNumber(groupNumberBuild)
                    .category(category)
                    .title(title)
                    .endDate(request.getEndDate())
                    .user(user)
                    .completed(false)
                    .startDate(request.getEndDate())
                    .build();

            response = groupRepository.save(group);
        }
        return GInsertRes.builder().id(response.getGroupNumber().getId())
                .build();
    }

    void checkInvite(Group group, User receiver){
        groupListRepo.findByGroupAndUser(group, receiver).orElseThrow(()-> new IllegalArgumentException("초대받은 적이 없습니다."));
    }

    Group getGroup(Long groupID){
        return groupRepository.findById(groupID).orElseThrow(()-> new IllegalArgumentException("존재하지 않는 그룹입니다."));
    }

    public String toDay(){
        LocalDateTime createAt = ZonedDateTime.now(ZoneId.of("Asia/Seoul")).toLocalDateTime();
        DateTimeFormatter yearMonthDayFormatter = DateTimeFormatter.ofPattern("yyyy.MM.dd");
        return createAt.format(yearMonthDayFormatter);
    }

    public User getUser(Long id){
        return userRepository.findById(id).orElseThrow(() -> new IllegalArgumentException("존재하지 않은 UserID"));
    }

    public void checkNotExist(User user, String title, String endDate){
        if (!groupRepository.existsByUserAndTitleAndEndDate(user,title, endDate)){
            throw new IllegalArgumentException("존재하지 않는 TDL입니다.");
        }
    }

    public void checkExist(User user, String title,String endDate){
        if (groupRepository.existsByUserAndTitleAndEndDate(user,title, endDate)){
            throw new IllegalArgumentException("이미 존재하는 TDL입니다.");
        }
    }

    public void inputSetting(String title, User user, String endDate, Category category) {
        checkExist(user, title, endDate);
        checkCategory(category);
    }

    public void checkCategory(Category category){
        if (category == null) {
            throw new IllegalArgumentException("존재하지 않은 카테고리입니다.");
        }
    }

    public void checkEndDate(String endDate){
        if (toDay().compareTo(endDate) > 0){
            throw new IllegalArgumentException("끝나는 날짜는 현재 날짜보다 빠를 수 없습니다.");
        }
    }
}
