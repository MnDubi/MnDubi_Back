package festival.dev.domain.TDL.service;

import festival.dev.domain.TDL.presentation.dto.request.*;
import festival.dev.domain.TDL.presentation.dto.response.ToDoListResponse;
import festival.dev.domain.shareTDL.presentation.dto.request.ShareChoiceRequest;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.util.List;

public interface ToDoListService {
    void input(InsertRequest request, Long id);
    ToDoListResponse update(UpdateRequest request, Long id);
    void delete(DeleteRequest request, Long id);
    List<ToDoListResponse> get(Long id);
    ToDoListResponse success(SuccessRequest request, Long id);
    void input(InsertUntilRequest request, Long id);
    void shared(ShareRequest request, Long id);
    SseEmitter sseConnect(Long shareNumber);
    void refuse(Long userId, ShareChoiceRequest request);
    void accept(Long userId, ShareChoiceRequest request);
}