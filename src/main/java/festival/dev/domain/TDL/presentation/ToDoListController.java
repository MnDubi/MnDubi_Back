package festival.dev.domain.TDL.presentation;

import festival.dev.domain.TDL.presentation.dto.request.*;
import festival.dev.domain.TDL.service.ToDoListService;
import festival.dev.domain.user.entity.CustomUserDetails;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.parameters.P;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/toDoList")
@RequiredArgsConstructor
public class ToDoListController {
    private final ToDoListService toDoListService;

//    @Value("${jwt.secret}")
//    private String secret;

    @PostMapping("/insert")
    public ResponseEntity<String> input(@Valid @RequestBody InsertRequest request, /*@RequestHeader String authorization*/@AuthenticationPrincipal CustomUserDetails user) {
//        Long userID = getUserID(authorization);

        try {
            toDoListService.input(request,/*userID*/user.getUserID());
            return ResponseEntity.ok("Success");
        }catch (Exception e){
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PutMapping("/modify")
    public ResponseEntity<?> modify(@Valid @RequestBody UpdateRequest request, /*@RequestHeader String authorization*/@AuthenticationPrincipal CustomUserDetails user) {
//        Long userID = getUserID(authorization);

        try{
            return ResponseEntity.ok(toDoListService.update(request, /*userID*/user.getUserID()));
        }
        catch (Exception e){
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @DeleteMapping("/delete")
    public ResponseEntity<String> delete(@Valid @RequestBody DeleteRequest request, /*@RequestHeader String authorization*/@AuthenticationPrincipal CustomUserDetails user){
//        Long userID = getUserID(authorization);

        try {
            toDoListService.delete(request,/*userID*/user.getUserID());
            return ResponseEntity.ok("Success");
        }
        catch (Exception e){
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @GetMapping("/get")
    public ResponseEntity<?> get(/*@RequestHeader String authorization*/@AuthenticationPrincipal CustomUserDetails user){
//        Long userID = getUserID(authorization);
        try{
            return ResponseEntity.ok(toDoListService.get(/*userID*/user.getUserID()));
        }catch (Exception e){
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PutMapping("/success")
    public ResponseEntity<?> success(@Valid @RequestBody SuccessRequest request,/*@RequestHeader String authorization*/@AuthenticationPrincipal CustomUserDetails user){
//        Long userID = getUserID(authorization);
        try{
            return ResponseEntity.ok(toDoListService.success(request,/*userID*/user.getUserID()));
        }catch (Exception e){
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PutMapping("/shared")
    public ResponseEntity<?> shared(@Valid @RequestBody ShareRequest request, @AuthenticationPrincipal CustomUserDetails user){
        try{
            toDoListService.shared(request,user.getUserID());
            return ResponseEntity.ok("success");
        }
        catch (Exception e){
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PostMapping("/insert/until")
    public ResponseEntity<?> until(/*@RequestHeader String authorization*/@AuthenticationPrincipal CustomUserDetails user, @Valid @RequestBody InsertUntilRequest request){
//        Long userID = getUserID(authorization);
        try {
            toDoListService.input(request,/*userID*/user.getUserID());
            return ResponseEntity.ok("Success");
        }
        catch (Exception e){
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

}
