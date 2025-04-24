package festival.dev.domain.shareTDL.presentation;

import festival.dev.domain.shareTDL.presentation.dto.ShareCreateReq;
import festival.dev.domain.shareTDL.service.ShareService;
import festival.dev.domain.user.entity.CustomUserDetails;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/share/toDoList")
public class ShareController {
    private final ShareService shareService;

    @PostMapping("/create")
    public ResponseEntity<?> create(@Valid @RequestBody ShareCreateReq request, @AuthenticationPrincipal CustomUserDetails user) {
        try{
            shareService.createShare(request,user.getUserID());
            return ResponseEntity.ok("success");
        }
        catch(Exception e){
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    
}
