package festival.dev.domain.sse.presentation;

import festival.dev.domain.TDL.service.ToDoListService;
import festival.dev.domain.gorupTDL.service.GroupService;
import festival.dev.domain.shareTDL.service.ShareService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

@RestController
@RequestMapping("/sse")
@RequiredArgsConstructor
@CrossOrigin(
        origins = "http://localhost:3000",
        allowCredentials = "true"
)
public class SseController {
    private final GroupService groupService;
    private final ToDoListService toDoListService;
    private final ShareService shareService;

    @GetMapping("/group")
    public SseEmitter group(@RequestParam Long groupNum) {
        return groupService.sseConnect(groupNum);
    }

    @GetMapping("/share")
    public SseEmitter share(@RequestParam Long shareNum) {
        return toDoListService.sseConnect(shareNum);
    }

    @GetMapping("/group-invite")
    public SseEmitter groupInvite(@RequestParam String userCode) {
        return groupService.groupInviteSseConnect(userCode);
    }

    @GetMapping("/share-invite")
    public SseEmitter shareInvite(@RequestParam String userCode) {
        return shareService.shareInviteSseConnect(userCode);
    }
}