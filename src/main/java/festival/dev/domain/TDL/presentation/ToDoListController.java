package festival.dev.domain.TDL.presentation;

import festival.dev.domain.TDL.entity.ToDoList;
import festival.dev.domain.TDL.presentation.dto.request.DeleteRequest;
import festival.dev.domain.TDL.presentation.dto.request.FinishRequest;
import festival.dev.domain.TDL.presentation.dto.request.InsertRequest;
import festival.dev.domain.TDL.presentation.dto.request.UpdateRequest;
import festival.dev.domain.TDL.service.ToDoListService;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.apache.coyote.Response;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/toDoList")
@RequiredArgsConstructor
public class ToDoListController {
    private final ToDoListService toDoListService;

    @Value("${jwt.secret}")
    private String secret;

    @PostMapping("/input")
    public ResponseEntity<String> input(@RequestBody InsertRequest request/*, @RequestHeader String authorization*/) {
//        String userID = getUserID(authorization);

        try {
            toDoListService.input(request);
            return ResponseEntity.ok("Success");
        }catch (Exception e){
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PutMapping("/modify")
    public ResponseEntity<ToDoList> modify(@Valid @RequestBody UpdateRequest request/*, @RequestHeader String authorization*/) {
//        String userID = getUserID(authorization);

        try{
            return ResponseEntity.ok(toDoListService.update(request));
        }
        catch (Exception e){
            return ResponseEntity.badRequest().body(null);
        }
    }

    @DeleteMapping("/delete")
    public ResponseEntity<String> delete(@RequestBody DeleteRequest request/*, @RequestHeader String authorization*/){
//        String userID = getUserID(authorization);

        try {
            toDoListService.delete(request);
            return ResponseEntity.ok("Success");
        }
        catch (Exception e){
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @GetMapping("/get")
    public ResponseEntity<List<ToDoList>> get(/*@RequestHeader String authorization*/@RequestParam String userID){
//        String userID = getUserID(authorization);
        try{
            return ResponseEntity.ok(toDoListService.get(userID));
        }catch (Exception e){
            return ResponseEntity.badRequest().body(null);
        }
    }

    @PutMapping("/success")
    public ResponseEntity<ToDoList> success(@RequestBody FinishRequest request/*,@RequestHeader String authorization*/){

        try{
            return ResponseEntity.ok(toDoListService.success(request));
        }catch (Exception e){
            return ResponseEntity.badRequest().body(null);
        }
    }

//    @PostMapping("/finish")
//    public ResponseEntity<String> finish(@RequestBody FinishRequest request){
//    }

    public String getUserID(String auth){
        String token = auth.replace("Bearer ","");

        Claims claims = Jwts.parserBuilder()
                .setSigningKey(secret.getBytes())
                .build()
                .parseClaimsJws(token).getBody();

        return claims.getSubject();
    }
}
