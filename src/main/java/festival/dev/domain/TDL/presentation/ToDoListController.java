package festival.dev.domain.TDL.presentation;

import com.auth0.jwt.JWT;
import com.auth0.jwt.interfaces.DecodedJWT;
import festival.dev.domain.TDL.presentation.dto.request.*;
import festival.dev.domain.TDL.service.ToDoListService;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/toDoList")
@RequiredArgsConstructor
public class ToDoListController {
    private final ToDoListService toDoListService;

    @Value("${jwt.secret}")
    private String secret;

    @PostMapping("/insert")
    public ResponseEntity<String> input(@Valid @RequestBody InsertRequest request, @RequestHeader String authorization) {
        Long userID = getUserID(authorization);

        try {
            toDoListService.input(request,userID);
            return ResponseEntity.ok("Success");
        }catch (Exception e){
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PutMapping("/modify")
    public ResponseEntity<?> modify(@Valid @RequestBody UpdateRequest request, @RequestHeader String authorization) {
        Long userID = getUserID(authorization);

        try{
            return ResponseEntity.ok(toDoListService.update(request, userID));
        }
        catch (Exception e){
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @DeleteMapping("/delete")
    public ResponseEntity<String> delete(@Valid @RequestBody DeleteRequest request, @RequestHeader String authorization){
        Long userID = getUserID(authorization);

        try {
            toDoListService.delete(request,userID);
            return ResponseEntity.ok("Success");
        }
        catch (Exception e){
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @GetMapping("/get")
    public ResponseEntity<?> get(@RequestHeader String authorization/*@RequestParam String userID*/){
        Long userID = getUserID(authorization);
        try{
            return ResponseEntity.ok(toDoListService.get(userID));
        }catch (Exception e){
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PutMapping("/success")
    public ResponseEntity<?> success(@Valid @RequestBody SuccessRequest request,@RequestHeader String authorization){
        Long userID = getUserID(authorization);

        try{
            return ResponseEntity.ok(toDoListService.success(request,userID));
        }catch (Exception e){
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PostMapping("/finish")
    public ResponseEntity<String> finish(@RequestHeader String authorization){
        Long userID = getUserID(authorization);
        try {
            toDoListService.finish(userID);
            return ResponseEntity.ok("Success");
        }catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PostMapping("/insert/until")
    public ResponseEntity<?> until(@RequestHeader String authorization, @Valid @RequestBody InsertUntilRequest request){
        Long userID = getUserID(authorization);
        try {
            toDoListService.input(request,userID);
            return ResponseEntity.ok("Success");
        }
        catch (Exception e){
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    public Long getUserID(String auth){
        String token = auth.replace("Bearer ","");
//
//        Claims claims = Jwts.parserBuilder()
//                .setSigningKey(secret.getBytes())
//                .build()
//                .parseClaimsJws(token).getBody();

        DecodedJWT jwt = JWT.decode(token);

        return jwt.getClaim("userId").asLong();
    }
}
