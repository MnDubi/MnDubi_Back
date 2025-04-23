package festival.dev.domain.shareTDL.presentation;

import festival.dev.domain.shareTDL.presentation.dto.ShareCreateReq;
import festival.dev.domain.shareTDL.service.ShareService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
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
    public ResponseEntity<?> create(@RequestBody ShareCreateReq request) {
        try{
            shareService.createShare(request);
            return ResponseEntity.ok().build();
        }
        catch(Exception e){
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
}
