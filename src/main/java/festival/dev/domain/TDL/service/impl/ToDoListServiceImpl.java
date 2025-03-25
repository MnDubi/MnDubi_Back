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

    public void input(InsertRequest request) {
        String userID = request.getUserID();
        String title = request.getTitle();

        checkExist(userID, title, request.getFromDate());

        Category category = categoryRepository.findByCategoryName(request.getCategory());
        if (category == null) {
            throw new IllegalArgumentException("존재하지 않은 카테고리입니다.");
        }
        ToDoList toDoList = ToDoList.builder()
                .title(request.getTitle())
                .completed(false)
                .userID(request.getUserID())
                .startDate(request.getFromDate())
                .fromDate(request.getFromDate())
                .category(category)
                .build();
        toDoListRepository.save(toDoList);
    }

    public ToDoListResponse update(UpdateRequest request) {
        checkNotExist(request.getUserID(), request.getTitle(), request.getFromDate());

        toDoListRepository.changeTitle(request.getChange(), request.getTitle(), request.getUserID(), request.getChangeDate(), request.getFromDate());

        ToDoList toDoList = toDoListRepository.findByUserIDAndTitleAndFromDate(request.getUserID(), request.getChange(), request.getChangeDate());

        return ToDoListResponse.builder()
                .title(toDoList.getTitle())
                .completed(toDoList.getCompleted())
                .category(toDoList.getCategory().getCategoryName())
                .formattedDate(toDoList.getFromDate())
                .build();
    }

    public void delete(DeleteRequest request) {
        checkNotExist(request.getUserID(), request.getTitle(), request.getFromDate());

        toDoListRepository.deleteByUserIDAndTitleAndFromDate(request.getUserID(),request.getTitle(), request.getFromDate());
    }

    public void checkNotExist(String userID, String title, String fromDate){
        if (!toDoListRepository.existsByUserIDAndTitleAndFromDate(userID,title, fromDate)){
            throw new IllegalArgumentException("존재하지 않는 TDL입니다.");
        }
    }

    public void checkExist(String userID, String title,String fromDate){
        if (toDoListRepository.existsByUserIDAndTitleAndFromDate(userID,title, fromDate)){
            throw new IllegalArgumentException("이미 존재하는 TDL입니다.");
        }
    }

    public List<ToDoListResponse> get(String userID){
        List<ToDoList> toDoList = toDoListRepository.findByUserID(userID);
        return toDoList.stream()
                .map(tdl -> ToDoListResponse.builder()
                        .title(tdl.getTitle())
                        .completed(tdl.getCompleted())
                        .category(tdl.getCategory().getCategoryName())  // 카테고리 이름을 포함
                        .build())
                .collect(Collectors.toList());
    }

    public ToDoListResponse success(SuccessRequest request) {
        LocalDateTime createAt;
        createAt = ZonedDateTime.now(ZoneId.of("Asia/Seoul")).toLocalDateTime();
        DateTimeFormatter yearMonthDayFormatter = DateTimeFormatter.ofPattern("yyyy.MM.dd");
        String yearMonthDay = createAt.format(yearMonthDayFormatter);
        System.out.println(yearMonthDay);

        toDoListRepository.changeCompleted(request.getCompleted(), request.getTitle(), request.getUserID(), yearMonthDay);
        ToDoList toDoList = toDoListRepository.findByUserIDAndTitleAndFromDate(request.getUserID(),request.getTitle(), yearMonthDay);

        return ToDoListResponse.builder()
                .title(toDoList.getTitle())
                .completed(toDoList.getCompleted())
                .category(toDoList.getCategory().getCategoryName())
                .formattedDate(toDoList.getFromDate())
                .build();
    }

    public void finish(FinishRequest request){
        Calendar calendar = Calendar.builder()
                .userID(request.getUserID())
                .every(request.getEvery())
                .part(request.getPart())
                .build();

        calendarRepository.save(calendar);
    }
}