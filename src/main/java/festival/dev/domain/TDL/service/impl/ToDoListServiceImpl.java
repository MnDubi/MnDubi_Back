package festival.dev.domain.TDL.service.impl;

import festival.dev.domain.TDL.entity.ToDoList;
import festival.dev.domain.TDL.presentation.dto.request.*;
import festival.dev.domain.TDL.presentation.dto.response.ToDoListResponse;
import festival.dev.domain.TDL.repository.ToDoListRepository;
import festival.dev.domain.TDL.service.ToDoListService;
import festival.dev.domain.calendar.entity.Calendar;
import festival.dev.domain.calendar.repository.CalendarRepository;
import festival.dev.domain.category.entity.Category;
import festival.dev.domain.category.repository.CategoryRepository;
import festival.dev.domain.user.entity.User;
import festival.dev.domain.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ToDoListServiceImpl implements ToDoListService {

    private final ToDoListRepository toDoListRepository;
    private final CalendarRepository calendarRepository;
    private final CategoryRepository categoryRepository;
    private final UserRepository userRepository;

    public void input(InsertRequest request, Long id) {
        String title = request.getTitle();
        User user = getUser(id);

        checkExist(user, title, request.getFromDate());

        Category category = categoryRepository.findByCategoryName(request.getCategory());

        if (category == null) {
            throw new IllegalArgumentException("존재하지 않은 카테고리입니다.");
        }

        ToDoList toDoList = ToDoList.builder()
                .title(request.getTitle())
                .completed(false)
                .user(user)
                .startDate(request.getFromDate())
                .fromDate(request.getFromDate())
                .category(category)
                .build();
        toDoListRepository.save(toDoList);
    }

    public ToDoListResponse update(UpdateRequest request, Long userID) {
        User user = getUser(userID);
        checkNotExist(user, request.getTitle(), request.getFromDate());

        toDoListRepository.changeTitle(request.getChange(), request.getTitle(), userID, request.getChangeDate(), request.getFromDate());

        ToDoList toDoList = toDoListRepository.findByUserAndTitleAndFromDate(user, request.getChange(), request.getChangeDate());

        return ToDoListResponse.builder()
                .title(toDoList.getTitle())
                .completed(toDoList.getCompleted())
                .category(toDoList.getCategory().getCategoryName())
                .userID(user.getName())
                .formattedDate(toDoList.getFromDate())
                .build();
    }

    public void delete(DeleteRequest request,Long id) {
        User user = getUser(id);
        checkNotExist(user, request.getTitle(), request.getFromDate());

        toDoListRepository.deleteByUserAndTitleAndFromDate(user,request.getTitle(), request.getFromDate());
    }

    public void checkNotExist(User user, String title, String fromDate){
        if (!toDoListRepository.existsByUserAndTitleAndFromDate(user,title, fromDate)){
            throw new IllegalArgumentException("존재하지 않는 TDL입니다.");
        }
    }

    public void checkExist(User user, String title,String fromDate){
        if (toDoListRepository.existsByUserAndTitleAndFromDate(user,title, fromDate)){
            throw new IllegalArgumentException("이미 존재하는 TDL입니다.");
        }
    }

    public List<ToDoListResponse> get(Long userID){
        User user = getUser(userID);
        List<ToDoList> toDoList = toDoListRepository.findByUser(user);
        return toDoList.stream()
                .map(tdl -> ToDoListResponse.builder()
                        .title(tdl.getTitle())
                        .completed(tdl.getCompleted())
                        .category(tdl.getCategory().getCategoryName())  // 카테고리 이름을 포함
                        .formattedDate(tdl.getFromDate())
                        .userID(tdl.getUser().getName())
                        .build())
                .collect(Collectors.toList());
    }

    public ToDoListResponse success(SuccessRequest request, Long userID) {
        LocalDateTime createAt;
        createAt = ZonedDateTime.now(ZoneId.of("Asia/Seoul")).toLocalDateTime();
        DateTimeFormatter yearMonthDayFormatter = DateTimeFormatter.ofPattern("yyyy.MM.dd");
        String yearMonthDay = createAt.format(yearMonthDayFormatter);
        User user = getUser(userID);

        toDoListRepository.changeCompleted(request.getCompleted(), request.getTitle(), userID, yearMonthDay);
        ToDoList toDoList = toDoListRepository.findByUserAndTitleAndFromDate(user,request.getTitle(), yearMonthDay);

        return ToDoListResponse.builder()
                .title(toDoList.getTitle())
                .completed(toDoList.getCompleted())
                .category(toDoList.getCategory().getCategoryName())
                .formattedDate(toDoList.getFromDate())
                .userID(user.getName())
                .build();
    }

    public void finish(FinishRequest request, Long userID){
        User user = getUser(userID);
        Calendar calendar = Calendar.builder()
                .user(user)
                .every(request.getEvery())
                .part(request.getPart())
                .build();

        calendarRepository.save(calendar);
    }

    public User getUser(Long id){
        return userRepository.findById(id).orElseThrow(() -> new IllegalArgumentException("존재하지 않은 UserID"));
    }
}