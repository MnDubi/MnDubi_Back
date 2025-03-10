package festival.dev.domain.TDL.service.impl;

import festival.dev.domain.TDL.entity.ToDoList;
import festival.dev.domain.TDL.presentation.dto.request.ToDoListRequest;
import festival.dev.domain.TDL.repository.ToDoListRepository;
import festival.dev.domain.TDL.service.ToDoListService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ToDoListServiceImpl implements ToDoListService {

    private final ToDoListRepository toDoListRepository;

    public void input(ToDoListRequest request) {
        ToDoList toDoList = ToDoList.builder()
                .title(request.getTitle())
                .completed(request.getCompleted())
                .userID(request.getUserID())
                .category(request.getCategory())
                .build();
        toDoListRepository.save(toDoList);
    }
}