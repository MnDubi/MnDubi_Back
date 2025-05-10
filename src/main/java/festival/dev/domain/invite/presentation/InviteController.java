package festival.dev.domain.invite.presentation;

import festival.dev.domain.invite.service.InviteService;
import festival.dev.domain.user.entity.CustomUserDetails;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class InviteController {
    private final InviteService inviteService;

    @GetMapping("/invite")
    public ResponseEntity<?> get(@AuthenticationPrincipal CustomUserDetails user){
        try{
            return ResponseEntity.ok(inviteService.inviteListGet(user.getUserID()));
        }
        catch(Exception e){
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
}
