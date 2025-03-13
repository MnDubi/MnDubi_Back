package festival.dev.domain.TDL.service;

import festival.dev.domain.TDL.entity.ToDoList;
import festival.dev.domain.TDL.presentation.dto.request.*;

import java.util.List;

public interface ToDoListService {
    void input(InsertRequest request);
    ToDoList update(UpdateRequest request);
    void delete(DeleteRequest request);
    List<ToDoList> get(String userID);
    ToDoList success(SuccessRequest request);
    void finish(FinishRequest request);
}