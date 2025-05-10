package festival.dev.domain.invite.presentation;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.stereotype.Controller;

@Controller
@RequiredArgsConstructor
@Slf4j
public class InviteWs {

    @MessageMapping("/invite")
    public void inviteWs(){

    }
}
