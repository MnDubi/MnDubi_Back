package festival.dev.domain.TDL.service.impl;

import festival.dev.domain.TDL.entity.ToDoList;
import festival.dev.domain.TDL.presentation.dto.request.*;
import festival.dev.domain.TDL.repository.ToDoListRepository;
import festival.dev.domain.TDL.service.ToDoListService;
import festival.dev.domain.calendar.entity.Calendar;
import festival.dev.domain.calendar.repository.CalendarRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ToDoListServiceImpl implements ToDoListService {

    private final ToDoListRepository toDoListRepository;
    private final CalendarRepository calendarRepository;

    public void input(InsertRequest request) {
        String userID = request.getUserID();
        String title = request.getTitle();
        if (toDoListRepository.existsByUserIDAndTitle(userID,title)){
            throw new IllegalArgumentException("같은 내용의 TDL이 있습니다.");
        }
        ToDoList toDoList = ToDoList.builder()
                .title(request.getTitle())
                .completed(false)
                .userID(request.getUserID())
                .category(request.getCategory())
                .build();
        toDoListRepository.save(toDoList);
    }

    public ToDoList update(UpdateRequest request) {
        checkNotExist(request.getUserID(), request.getTitle());

        toDoListRepository.changeTitle(request.getChange(), request.getTitle(), request.getUserID());
        return   toDoListRepository.findByUserIDAndTitle(request.getUserID(), request.getChange());
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

    public List<ToDoList> get(String userID){
        return toDoListRepository.findByUserID(userID);
    }

    public ToDoList success(SuccessRequest request) {
        toDoListRepository.changeCompleted(request.getCompleted(), request.getTitle(), request.getUserID());
        return toDoListRepository.findByUserIDAndTitle(request.getUserID(),request.getTitle());
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