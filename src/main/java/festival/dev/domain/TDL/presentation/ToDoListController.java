package festival.dev.domain.TDL.presentation;

import festival.dev.domain.TDL.entity.ToDoList;
import festival.dev.domain.TDL.presentation.dto.request.DeleteRequest;
import festival.dev.domain.TDL.presentation.dto.request.InsertRequest;
import festival.dev.domain.TDL.presentation.dto.request.UpdateRequest;
import festival.dev.domain.TDL.repository.ToDoListRepository;
import festival.dev.domain.TDL.service.ToDoListService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.apache.coyote.Response;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/toDoList")
@RequiredArgsConstructor
public class ToDoListController {
    private final ToDoListService toDoListService;
    private final ToDoListRepository toDoListRepository;

    @PostMapping("/input")
    public ResponseEntity<String> input(@RequestBody InsertRequest request) {
        try {
            toDoListService.input(request);
            return ResponseEntity.ok("Success");
        }catch (Exception e){
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PutMapping("/modify")
    public ResponseEntity<String> modify(@Valid @RequestBody UpdateRequest request) {
        try{
            toDoListService.update(request);
            return ResponseEntity.ok("Success");
        }
        catch (Exception e){
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @DeleteMapping("/delete")
    public ResponseEntity<String> delete(@RequestBody DeleteRequest request){
        try {
            toDoListService.delete(request);
            return ResponseEntity.ok("Success");
        }
        catch (Exception e){
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
}
