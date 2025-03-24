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

        checkExist(userID, title);

        Category category = categoryRepository.findByCategoryName(request.getCategory());
        if (category == null) {
            throw new IllegalArgumentException("존재하지 않은 카테고리입니다.");
        }
        ToDoList toDoList = ToDoList.builder()
                .title(request.getTitle())
                .completed(false)
                .userID(request.getUserID())
                .category(category)
                .build();
        toDoListRepository.save(toDoList);
    }

    public ToDoListResponse update(UpdateRequest request) {
        checkNotExist(request.getUserID(), request.getTitle());

        toDoListRepository.changeTitle(request.getChange(), request.getTitle(), request.getUserID());

        ToDoList toDoList = toDoListRepository.findByUserIDAndTitle(request.getUserID(), request.getChange());

        return ToDoListResponse.builder()
                .title(toDoList.getTitle())
                .completed(toDoList.getCompleted())
                .category(toDoList.getCategory().getCategoryName())
                .formattedDate(toDoList.getFormattedDate())
                .dayOfWeek(toDoList.getDayOfWeek())
                .build();
    }

    public void delete(DeleteRequest request) {
        checkNotExist(request.getUserID(), request.getTitle());

        toDoListRepository.deleteByUserIDAndTitle(request.getUserID(),request.getTitle());
    }

    public void checkNotExist(String userID, String title){
        if (!toDoListRepository.existsByUserIDAndTitle(userID,title)){
            throw new IllegalArgumentException("존재하지 않는 TDL입니다.");
        }
    }

    public void checkExist(String userID, String title){
        if (toDoListRepository.existsByUserIDAndTitle(userID,title)){
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
                        .formattedDate(tdl.getFormattedDate())
                        .dayOfWeek(tdl.getDayOfWeek())
                        .build())
                .collect(Collectors.toList());
    }

    public ToDoListResponse success(SuccessRequest request) {
        toDoListRepository.changeCompleted(request.getCompleted(), request.getTitle(), request.getUserID());
        ToDoList toDoList = toDoListRepository.findByUserIDAndTitle(request.getUserID(),request.getTitle());

        return ToDoListResponse.builder()
                .title(toDoList.getTitle())
                .completed(toDoList.getCompleted())
                .category(toDoList.getCategory().getCategoryName())
                .formattedDate(toDoList.getFormattedDate())
                .dayOfWeek(toDoList.getDayOfWeek())
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