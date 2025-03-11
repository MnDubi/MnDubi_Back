package festival.dev.domain.TDL.service;

import festival.dev.domain.TDL.presentation.dto.request.InsertRequest;
import festival.dev.domain.TDL.presentation.dto.request.UpdateRequest;

public interface ToDoListService {
    void input(InsertRequest request);
    void update(UpdateRequest request);
}