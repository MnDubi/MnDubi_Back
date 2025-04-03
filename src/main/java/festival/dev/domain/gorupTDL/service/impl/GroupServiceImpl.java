package festival.dev.domain.gorupTDL.service.impl;

import festival.dev.domain.category.entity.Category;
import festival.dev.domain.category.repository.CategoryRepository;
import festival.dev.domain.friendship.entity.Friendship;
import festival.dev.domain.friendship.repository.FriendshipRepository;
import festival.dev.domain.gorupTDL.entity.Group;
import festival.dev.domain.gorupTDL.entity.GroupList;
import festival.dev.domain.gorupTDL.presentation.dto.request.GInsertRequest;
import festival.dev.domain.gorupTDL.presentation.dto.request.GInviteReq;
import festival.dev.domain.gorupTDL.repository.GroupListRepo;
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

    public void invite(GInviteReq gInviteReq, Long userID){
        User sender = getUser(userID);
        User receiver = userRepository.findByUserCode(gInviteReq.getReceiver()).orElseThrow(()-> new IllegalArgumentException("없는 존재 입니다."));

    }

    public void insert(GInsertRequest request, Long userID){
        User user = getUser(userID);

        Category category = categoryRepository.findByCategoryName(request.getCategory());
        inputSetting(request.getTitle(), user, request.getEndDate(), category);

        checkEndDate(request.getEndDate());

        Group group = Group.builder()
                .category(category)
                .title(request.getTitle())
                .endDate(request.getEndDate())
                .completed(false)
                .startDate(request.getEndDate())
                .build();
        groupRepository.save(group);
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
