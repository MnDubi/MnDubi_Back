package festival.dev.domain.TDL.presentation;

import festival.dev.domain.TDL.entity.ToDoList;
import festival.dev.domain.TDL.presentation.dto.request.DeleteRequest;
import festival.dev.domain.TDL.presentation.dto.request.InsertRequest;
import festival.dev.domain.TDL.presentation.dto.request.UpdateRequest;
import festival.dev.domain.TDL.service.ToDoListService;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
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
//        String token = authorization.replace("Bearer ", "");

        try {
            toDoListService.input(request);
            return ResponseEntity.ok("Success");
        }catch (Exception e){
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PutMapping("/modify")
    public ResponseEntity<String> modify(@Valid @RequestBody UpdateRequest request/*, @RequestHeader String authorization*/) {
//        String token = authorization.replace("Bearer ", "");

        try{
            toDoListService.update(request);
            return ResponseEntity.ok("Success");
        }
        catch (Exception e){
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @DeleteMapping("/delete")
    public ResponseEntity<String> delete(@RequestBody DeleteRequest request/*, @RequestHeader String authorization*/){
//        String token = authorization.replace("Bearer ", "");

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
//        String token = authorization.replace("Bearer ", "");
//        Claims claims = Jwts.parserBuilder()
//                .setSigningKey(secret.getBytes())
//                .build()
//                .parseClaimsJws(token).getBody();

//        String userID = claims.getSubject();
        try{
            return ResponseEntity.ok(toDoListService.get(userID));
        }catch (Exception e){
            return ResponseEntity.badRequest().body(null);
        }
    }
}
