package festival.dev.domain.sse.presentation;

import festival.dev.domain.gorupTDL.presentation.dto.request.GSseTest;
import festival.dev.domain.gorupTDL.service.GroupService;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

@RestController
@RequestMapping("/sse")
@RequiredArgsConstructor
public class SseController {
    private final Logger logger = LoggerFactory.getLogger(SseController.class);
    private final GroupService groupService;

    @GetMapping("/sse")
    public SseEmitter connect(@RequestParam Long groupNum) {
        return groupService.sseConnect(groupNum);
    }

    @PostMapping("/send-to-group")
    public void sendToGroup(@RequestBody GSseTest test) {
        groupService.sseSend(test);
    }
}
