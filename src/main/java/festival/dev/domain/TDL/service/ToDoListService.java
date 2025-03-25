package festival.dev.domain.TDL.service;

import festival.dev.domain.TDL.entity.ToDoList;
import festival.dev.domain.TDL.presentation.dto.request.*;
import festival.dev.domain.TDL.presentation.dto.response.ToDoListResponse;

import java.util.List;

public interface ToDoListService {
    void input(InsertRequest request);
    ToDoListResponse update(UpdateRequest request);
    void delete(DeleteRequest request);
    List<ToDoListResponse> get(String userID);
    ToDoListResponse success(SuccessRequest request);
    void finish(FinishRequest request);
}