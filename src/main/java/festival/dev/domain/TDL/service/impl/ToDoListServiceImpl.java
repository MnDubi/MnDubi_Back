package festival.dev.domain.TDL.service.impl;

import festival.dev.domain.TDL.entity.ToDoList;
import festival.dev.domain.TDL.presentation.dto.request.*;
import festival.dev.domain.TDL.presentation.dto.response.ToDoListResponse;
import festival.dev.domain.TDL.repository.ToDoListRepository;
import festival.dev.domain.TDL.service.ToDoListService;
import festival.dev.domain.calendar.entity.Calendar;
import festival.dev.domain.calendar.entity.Calendar_tdl_ids;
import festival.dev.domain.calendar.repository.CalendarRepository;
import festival.dev.domain.category.entity.Category;
import festival.dev.domain.category.service.CategoryService;
import festival.dev.domain.category.repository.CategoryRepository;
import festival.dev.domain.user.entity.User;
import festival.dev.domain.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.core.ParameterizedTypeReference;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ToDoListServiceImpl implements ToDoListService {

    private final ToDoListRepository toDoListRepository;
    private final CalendarRepository calendarRepository;
    private final CategoryRepository categoryRepository;
    private final RestTemplate restTemplate;
    private final CategoryService categoryService;
    private final UserRepository userRepository;

    public void input(InsertRequest request, Long id) {
        String title = request.getTitle();
        User user = getUser(id);

        checkEndDate(request.getEndDate());

        //  AI 서버에 투두 내용(title) 보내서 카테고리 자동 분류
        String categoryName = classifyCategoryWithAI(title);
        //  해당 이름의 카테고리가 없으면 새로 생성, 있으면 재사용
        Category category = categoryService.findOrCreateByName(categoryName);

        checkExist(user, title, request.getEndDate());

        ToDoList toDoList = ToDoList.builder()
                .title(request.getTitle())
                .completed(false)
                .user(user)
                .startDate(request.getEndDate())
                .endDate(request.getEndDate())
                .category(category)
                .build();
        toDoListRepository.save(toDoList);
    }

    public void input(InsertUntilRequest request, Long id) {
        String title = request.getTitle();
        User user = getUser(id);

        if (request.getStartDate().compareTo(request.getEndDate()) > 0) {
            throw new IllegalArgumentException("시작하는 날짜가 끝나는 날짜보다 늦을 수 없습니다.");
        }

        checkEndDate(request.getEndDate());

        //  AI 카테고리 분류 + 저장/재사용
        String categoryName = classifyCategoryWithAI(title);
        Category category = categoryService.findOrCreateByName(categoryName);

        checkExist(user, title, request.getEndDate());

        toDoListRepository.save(ToDoList.builder()
                .title(title)
                .completed(false)
                .user(user)
                .startDate(request.getStartDate())
                .endDate(request.getEndDate())
                .category(category)
                .build());
    }

    private String classifyCategoryWithAI(String title) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        HttpEntity<Map<String, String>> entity = new HttpEntity<>(Map.of("todo", title), headers);

        ResponseEntity<Map<String, String>> response = restTemplate.exchange(
                "http://localhost:8000/classify",
                HttpMethod.POST,
                entity,
                new ParameterizedTypeReference<>() {}
        );

        return response.getBody().get("category");
    }



    public ToDoListResponse update(UpdateRequest request, Long userID) {
        User user = getUser(userID);
        checkNotExist(user, request.getTitle(), request.getEndDate());
        checkExist(user, request.getChange(), request.getChangeDate());
        if(toDay().compareTo(request.getEndDate()) > 0)
            throw new IllegalArgumentException("이미 끝난 TDL은 변경이 불가능합니다.");

        toDoListRepository.changeTitle(request.getChange(), request.getTitle(), userID, request.getChangeDate(), request.getEndDate());

        ToDoList toDoList = toDoListRepository.findByUserAndTitleAndEndDate(user, request.getChange(), request.getChangeDate());

        return ToDoListResponse.builder()
                .title(toDoList.getTitle())
                .completed(toDoList.getCompleted())
                .category(toDoList.getCategory().getCategoryName())
                .userID(user.getName())
                .endDate(toDoList.getEndDate())
                .startDate(toDoList.getStartDate())
                .build();
    }

    public void delete(DeleteRequest request,Long id) {
        User user = getUser(id);
        checkNotExist(user, request.getTitle(), request.getEndDate());

        toDoListRepository.deleteByUserAndTitleAndEndDate(user,request.getTitle(), request.getEndDate());
    }

    public List<ToDoListResponse> get(Long userID){
        User user = getUser(userID);
        List<ToDoList> toDoList = toDoListRepository.findByCurrentDateAndUserID(toDay(), userID);
        if(toDoList.isEmpty()){
            throw new IllegalArgumentException("오늘과 관련된 ToDoList가 없습니다.");
        }
        return toDoList.stream()
                .map(tdl -> ToDoListResponse.builder()
                        .title(tdl.getTitle())
                        .completed(tdl.getCompleted())
                        .category(tdl.getCategory().getCategoryName())  // 카테고리 이름을 포함
                        .endDate(tdl.getEndDate())
                        .startDate(tdl.getStartDate())
                        .userID(user.getName())
                        .build())
                .collect(Collectors.toList());
    }

    public ToDoListResponse success(SuccessRequest request, Long userID) {
        String yearMonthDay = toDay();
        User user = getUser(userID);

        toDoListRepository.changeCompleted(request.getCompleted(), request.getTitle(), userID, yearMonthDay);
        ToDoList toDoList = toDoListRepository.findByUserAndTitleAndEndDate(user,request.getTitle(), yearMonthDay);

        return ToDoListResponse.builder()
                .title(toDoList.getTitle())
                .completed(toDoList.getCompleted())
                .category(toDoList.getCategory().getCategoryName())
                .endDate(toDoList.getEndDate())
                .startDate(toDoList.getStartDate())
                .userID(user.getName())
                .build();
    }

    public void finish(Long userID){
        User user = getUser(userID);
        List<ToDoList> tdls = toDoListRepository.findByUserAndEndDate(user,toDay());
        int part = toDoListRepository.findByUserAndEndDateAndCompleted(user,toDay(),true).size();
        List<Calendar_tdl_ids> tdlIDs = tdls.stream()
                .map(tdl -> Calendar_tdl_ids.builder()
                        .toDoListId(tdl.getId())
                        .build())
                .collect(Collectors.toList());

        if (calendarRepository.findByUserAndYearMonthDay(user,toDay()) == null) {
            Calendar calendar = Calendar.builder()
                    .user(user)
                    .every(tdlIDs.size())
                    .part(part)
                    .toDoListId(tdlIDs)
                    .build();
            calendarRepository.save(calendar);
        }
        else{
            throw new IllegalArgumentException("하루에 두 번 이상 요청을 보내실 수 없습니다.");
        }
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
        if (!toDoListRepository.existsByUserAndTitleAndEndDate(user,title, endDate)){
            throw new IllegalArgumentException("존재하지 않는 TDL입니다.");
        }
    }

    public void checkExist(User user, String title,String endDate){
        if (toDoListRepository.existsByUserAndTitleAndEndDate(user,title, endDate)){
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