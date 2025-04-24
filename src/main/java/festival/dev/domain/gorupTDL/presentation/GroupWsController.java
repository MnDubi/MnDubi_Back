package festival.dev.domain.gorupTDL.presentation;

import festival.dev.domain.gorupTDL.presentation.dto.request.GCreateWsReq;
import festival.dev.domain.gorupTDL.service.GroupService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.stereotype.Controller;

@Controller
@RequiredArgsConstructor
@Slf4j
public class GroupWsController {
    private final GroupService groupService;
    private final Logger logger = LoggerFactory.getLogger(GroupWsController.class);

    @MessageMapping("/group/friends")
    public void websocket(GCreateWsReq request){
        logger.info("websocket request" + request.getEmail() + request.getFriend());
        groupService.createWs(request);
        logger.info("websocket response");
    }
}