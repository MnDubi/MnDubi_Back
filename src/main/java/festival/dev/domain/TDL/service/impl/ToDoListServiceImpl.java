package festival.dev.domain.TDL.service.impl;

import festival.dev.domain.TDL.entity.ToDoList;
import festival.dev.domain.TDL.presentation.dto.request.InsertRequest;
import festival.dev.domain.TDL.presentation.dto.request.UpdateRequest;
import festival.dev.domain.TDL.repository.ToDoListRepository;
import festival.dev.domain.TDL.service.ToDoListService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ToDoListServiceImpl implements ToDoListService {

    private final ToDoListRepository toDoListRepository;

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

    public void update(UpdateRequest request) {
        if (!toDoListRepository.existsByUserIDAndTitle(request.getUserID(),request.getTitle())){
            throw new IllegalArgumentException("존재하지 않는 TDL입니다.");
        }

        toDoListRepository.changeTitle(request.getChange(), request.getTitle(), request.getUserID());
    }
}