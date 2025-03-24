package festival.dev.domain.category.presentation;

import festival.dev.domain.category.presentation.dto.CategoryCreateDeleteRequest;
import festival.dev.domain.category.presentation.dto.CategoryModifyRequest;
import festival.dev.domain.category.service.CategoryService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/category")
public class CategoryController {

    private final CategoryService categoryService;

    @PostMapping("/create")
    public ResponseEntity<?> create(@RequestBody CategoryCreateDeleteRequest request) {
        try {
            return ResponseEntity.ok(categoryService.save(request));
        }
        catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PutMapping("/modify")
    public ResponseEntity<?> modify(@RequestBody CategoryModifyRequest request) {
        try{
            return ResponseEntity.ok(categoryService.modify(request));
        }
        catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @GetMapping("/get")
    public ResponseEntity<?> get() {
        try {
            return ResponseEntity.ok(categoryService.findAll());
        }
        catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @DeleteMapping("/delete")
    public ResponseEntity<?> delete(@RequestBody CategoryCreateDeleteRequest request){
        try{
            categoryService.delete(request.getCategory());
            return ResponseEntity.ok("success");
        }
        catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
}
