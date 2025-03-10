package festival.dev.domain.TDL.presentation;

import festival.dev.domain.TDL.entity.ToDoList;
import festival.dev.domain.TDL.presentation.dto.request.ToDoListRequest;
import festival.dev.domain.TDL.service.ToDoListService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/toDoList")
@RequiredArgsConstructor
public class ToDoListController {
    private final ToDoListService toDoListService;

    @PostMapping("/input")
    public ResponseEntity<String> input(@RequestBody ToDoListRequest request) {
        try {
            toDoListService.input(request);
            return ResponseEntity.ok("Success");
        }catch (Exception e){
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
}
