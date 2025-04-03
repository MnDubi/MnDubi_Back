package festival.dev.domain.gorupTDL.presentation;

import com.auth0.jwt.JWT;
import com.auth0.jwt.interfaces.DecodedJWT;
import festival.dev.domain.gorupTDL.presentation.dto.request.GInsertRequest;
import festival.dev.domain.gorupTDL.presentation.dto.request.GInviteReq;
import festival.dev.domain.gorupTDL.service.GroupService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("group/toDoList")
@RequiredArgsConstructor
public class GroupController {
    private final GroupService groupService;

    @PostMapping("/invite")
    public ResponseEntity<?> invite(@RequestBody GInviteReq gInviteReq, @RequestHeader String authorization) {
        try {
            Long userID = getUserID(authorization);
            groupService.invite(gInviteReq,userID);
            return ResponseEntity.ok("success");
        }
        catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PostMapping("/insert")
    public ResponseEntity<?> insert(@RequestBody GInsertRequest request, @RequestHeader String authorization) {
        try {
            Long userID = getUserID(authorization);
            return ResponseEntity.ok(groupService.insert(request,userID));
        }catch (Exception e){
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    public Long getUserID(String auth){
        String token = auth.replace("Bearer ","");

//        Claims claims = Jwts.parserBuilder()
//                .setSigningKey(secret.getBytes())
//                .build()
//                .parseClaimsJws(token).getBody();

        DecodedJWT jwt = JWT.decode(token);

        return jwt.getClaim("userId").asLong();
    }
}
