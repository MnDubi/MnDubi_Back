package festival.dev.domain.gorupTDL.presentation;

import festival.dev.domain.gorupTDL.service.GroupService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.stereotype.Controller;

@Controller
@RequiredArgsConstructor
public class GroupWsController {
    private final GroupService groupService;

//    @MessageMapping("/group/tdl")
//    @SendTo("/group/tdl")
//    public void websocket(){
//        return ;
//    }
}