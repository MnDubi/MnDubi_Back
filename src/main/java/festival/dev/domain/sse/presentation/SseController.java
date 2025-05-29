package festival.dev.domain.sse.presentation;

import festival.dev.domain.TDL.service.ToDoListService;
import festival.dev.domain.gorupTDL.service.GroupService;
import festival.dev.domain.shareTDL.service.ShareService;
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
//    private final ShareService shareService;
    private final ToDoListService toDoListService;

    @GetMapping("/group")
    public SseEmitter group(@RequestParam Long groupNum) {
        return groupService.sseConnect(groupNum);
    }

    @GetMapping("/share")
    public SseEmitter share(@RequestParam Long shareNum) {
        return toDoListService.sseConnect(shareNum);
    }

}
