//package festival.dev.domain.gorupTDL.presentation;
//
//import com.auth0.jwt.JWT;
//import com.auth0.jwt.interfaces.DecodedJWT;
//import festival.dev.domain.gorupTDL.presentation.dto.request.GInsertRequest;
//import festival.dev.domain.gorupTDL.presentation.dto.request.GInviteReq;
//import festival.dev.domain.gorupTDL.presentation.dto.request.GUpdateRequest;
//import festival.dev.domain.gorupTDL.service.GroupService;
//import io.jsonwebtoken.Claims;
//import io.jsonwebtoken.Jwts;
//import lombok.RequiredArgsConstructor;
//import org.springframework.beans.factory.annotation.Value;
//import org.springframework.http.ResponseEntity;
//import org.springframework.web.bind.annotation.*;
//
//@RestController
//@RequestMapping("group/toDoList")
//@RequiredArgsConstructor
//public class GroupController {
//    @Value("${jwt.secret}")
//    private String secret;
//
//    private final GroupService groupService;
//
//    @PostMapping("/invite")
//    public ResponseEntity<?> invite(@RequestBody GInviteReq request, @RequestHeader String authorization) {
//        try {
//            Long userID = getUserID(authorization);
//            return ResponseEntity.ok(groupService.invite(request,userID));
//        }
//        catch (Exception e) {
//            return ResponseEntity.badRequest().body(e.getMessage());
//        }
//    }
//
//    @PostMapping("/insert")
//    public ResponseEntity<?> insert(@RequestBody GInsertRequest request, @RequestHeader String authorization) {
//        try {
//            Long userID = getUserID(authorization);
//            return ResponseEntity.ok(groupService.insert(request,userID));
//        }catch (Exception e){
//            return ResponseEntity.badRequest().body(e.getMessage());
//        }
//    }
//
//    @PutMapping("/accept")
//    public ResponseEntity<?> accept(@RequestBody GInviteReq request,@RequestHeader String authorization) {
//        try{
//            Long userID = getUserID(authorization);
//            groupService.acceptInvite(request,userID);
//            return ResponseEntity.ok("success");
//        }catch (Exception e){
//            return ResponseEntity.badRequest().body(e.getMessage());
//        }
//    }
//
//    @DeleteMapping("/refuse")
//    public ResponseEntity<?> refuse(@RequestBody GInviteReq request,@RequestHeader String authorization) {
//        try{
//            Long userID = getUserID(authorization);
//            groupService.refuseInvite(request,userID);
//            return ResponseEntity.ok("success");
//        }
//        catch (Exception e){
//            return ResponseEntity.badRequest().body(e.getMessage());
//        }
//    }
//
//    @PutMapping("/modify")
//    public ResponseEntity<?> modify(@RequestBody GUpdateRequest request, @RequestHeader String authorization) {
//        try{
//            Long userID = getUserID(authorization);
//            return ResponseEntity.ok(groupService.update(request,userID));
//        }
//        catch (Exception e){
//            return ResponseEntity.badRequest().body(e.getMessage());
//        }
//    }
//
//    public Long getUserID(String auth){
//        String token = auth.replace("Bearer ","");
//
//        Claims claims = Jwts.parserBuilder()
//                .setSigningKey(secret.getBytes())
//                .build()
//                .parseClaimsJws(token).getBody();
//
//        return claims.get("userId",Long.class);
////        DecodedJWT jwt = JWT.decode(token);
////
////        return jwt.getClaim("userId").asLong();
//    }
//}
