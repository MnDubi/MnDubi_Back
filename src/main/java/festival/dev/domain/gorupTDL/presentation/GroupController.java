package festival.dev.domain.gorupTDL.presentation;

import festival.dev.domain.gorupTDL.presentation.dto.request.*;
import festival.dev.domain.gorupTDL.service.GroupService;
import festival.dev.domain.user.entity.CustomUserDetails;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("group/toDoList")
@RequiredArgsConstructor
public class GroupController {

//    @Value("${jwt.secret}")
//    private String secret;

    private final GroupService groupService;

    @PostMapping("/invite")
    public ResponseEntity<?> invite(@Valid @RequestBody GInviteReq request, @AuthenticationPrincipal CustomUserDetails user) {
        try {
            groupService.invite(request,user.getUserID());
            return ResponseEntity.ok("success");
        }
        catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PostMapping("/insert")
    public ResponseEntity<?> insert(@Valid @RequestBody GInsertRequest request, /*@RequestHeader String authorization*/@AuthenticationPrincipal CustomUserDetails user) {
        try {
//            Long userID = getUserID(authorization);

            return ResponseEntity.ok(groupService.invite(request,/*userID*/user.getUserID())    );
        }catch (Exception e){
            groupService.numberDelete();
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PutMapping("/accept")
    public ResponseEntity<?> accept(@Valid @RequestBody GChoiceRequest request,/*@RequestHeader String authorization*/@AuthenticationPrincipal CustomUserDetails user) {
        try{
//            Long userID = getUserID(authorization);
            groupService.acceptInvite(request,/*userID*/user.getUserID());
            return ResponseEntity.ok("success");
        }catch (Exception e){
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @DeleteMapping("/refuse")
    public ResponseEntity<?> refuse(@Valid @RequestBody GChoiceRequest request,/*@RequestHeader String authorization,*/ @AuthenticationPrincipal CustomUserDetails user) {
        try{
//            Long userID = getUserID(authorization);
            groupService.refuseInvite(request,/*userID*/user.getUserID());
            return ResponseEntity.ok("success");
        }
        catch (Exception e){
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PutMapping("/modify")
    public ResponseEntity<?> modify(@Valid @RequestBody GUpdateRequest request/*, @RequestHeader String authorization*/, @AuthenticationPrincipal CustomUserDetails user) {
        try{
//            Long userID = getUserID(authorization);
            return ResponseEntity.ok(groupService.update(request,user.getUserID()/*userID*/));
        }
        catch (Exception e){
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @DeleteMapping("/delete")
    public ResponseEntity<?> delete(@Valid @RequestBody GDeleteRequest request, @AuthenticationPrincipal CustomUserDetails user) {
        try{
            groupService.delete(request,user.getUserID());
            return ResponseEntity.ok("success");
        }
        catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

//    public Long getUserID(String auth){
//        String token = auth.replace("Bearer ","");
//
//        Claims claims = Jwts.parserBuilder()
//                .setSigningKey(secret.getBytes())
//                .build()
//                .parseClaimsJws(token).getBody();
//
//        return claims.get("userId",Long.class);
//    }
}
