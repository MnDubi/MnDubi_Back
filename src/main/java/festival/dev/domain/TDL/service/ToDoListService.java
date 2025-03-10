package festival.dev.domain.TDL.service;

import festival.dev.domain.TDL.presentation.dto.request.ToDoListRequest;

public interface ToDoListService {
    void input(ToDoListRequest request);
}