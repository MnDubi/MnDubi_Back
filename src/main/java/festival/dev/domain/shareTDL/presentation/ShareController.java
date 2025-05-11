package festival.dev.domain.shareTDL.presentation;

import festival.dev.domain.shareTDL.presentation.dto.request.*;
import festival.dev.domain.shareTDL.service.ShareService;
import festival.dev.domain.user.entity.CustomUserDetails;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/share/toDoList")
public class ShareController {
    private final ShareService shareService;

    @PostMapping("/create")
    public ResponseEntity<?> create(@Valid @RequestBody ShareCreateReq request, @AuthenticationPrincipal CustomUserDetails user) {
        try{
            return ResponseEntity.ok(shareService.createShare(request,user.getUserID()));
        }
        catch(Exception e){
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    //websocket으로 보내기
    @PostMapping("/invite")
    public ResponseEntity<?> invite(@AuthenticationPrincipal CustomUserDetails user, @Valid @RequestBody ShareInviteReq request) {
        try{
            return ResponseEntity.ok(shareService.inviteShare(user.getUserID(),request));
        }
        catch (Exception e){
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @GetMapping("/get")
    public ResponseEntity<?> get(@AuthenticationPrincipal CustomUserDetails user){
        try{
            return ResponseEntity.ok(shareService.get(user.getUserID()));
        }
        catch (Exception e){
            return  ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @GetMapping("/user-list")
    public ResponseEntity<?> getUserList(@AuthenticationPrincipal CustomUserDetails user){
        try{
            return ResponseEntity.ok(shareService.getUserList(user.getUserID()));
        }
        catch (Exception e){
            return  ResponseEntity.badRequest().body(e.getMessage());
        }
    }
}
