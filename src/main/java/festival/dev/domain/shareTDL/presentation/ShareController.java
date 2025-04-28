package festival.dev.domain.shareTDL.presentation;

import festival.dev.domain.shareTDL.presentation.dto.request.ShareCreateReq;
import festival.dev.domain.shareTDL.presentation.dto.request.ShareInsertReq;
import festival.dev.domain.shareTDL.presentation.dto.request.ShareInviteReq;
import festival.dev.domain.shareTDL.presentation.dto.request.ShareModifyReq;
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

    @PostMapping("/invite")
    public ResponseEntity<?> invite(@AuthenticationPrincipal CustomUserDetails user, @Valid @RequestBody ShareInviteReq request) {
        try{
            return ResponseEntity.ok(shareService.inviteShare(user.getUserID(),request));
        }
        catch (Exception e){
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PutMapping("/modify")
    public ResponseEntity<?> modify(@AuthenticationPrincipal CustomUserDetails user, @Valid @RequestBody ShareModifyReq request){
        try{
            return ResponseEntity.ok(shareService.modifyShare(user.getUserID(),request));
        }
        catch (Exception e){
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PostMapping("/insert")
    public ResponseEntity<?> insert(@AuthenticationPrincipal CustomUserDetails user, @Valid @RequestBody ShareInsertReq request){
        try{
            return ResponseEntity.ok(shareService.insertShare(user.getUserID(),request));
        }
        catch (Exception e){
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
}
