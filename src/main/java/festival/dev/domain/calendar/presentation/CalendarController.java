package festival.dev.domain.calendar.presentation;

import festival.dev.domain.calendar.presentation.dto.CalendarInsertRequest;
import festival.dev.domain.calendar.service.CalendarService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping
@RequiredArgsConstructor
public class CalendarController {

    private final CalendarService calendarService;

    @PostMapping
    public ResponseEntity<?> insert(@RequestBody CalendarInsertRequest request){
        try {
            return ResponseEntity.ok(calendarService.insert(request));
        }catch (Exception e){
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @GetMapping
    public ResponseEntity<?> findDate(@RequestParam String date ,@RequestParam String userID){
        try{
            return ResponseEntity.ok(calendarService.getDateCalendar(date, userID));
        }catch (Exception e){
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
}