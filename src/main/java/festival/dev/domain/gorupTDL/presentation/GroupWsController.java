package festival.dev.domain.gorupTDL.presentation;

import festival.dev.domain.gorupTDL.presentation.dto.request.GCreateWsReq;
import festival.dev.domain.gorupTDL.service.GroupService;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.stereotype.Controller;

@Controller
@RequiredArgsConstructor
public class GroupWsController {
    private final GroupService groupService;

    @MessageMapping("/group/friends")
    public void websocket(GCreateWsReq request){
        groupService.createWs(request);
    }
}