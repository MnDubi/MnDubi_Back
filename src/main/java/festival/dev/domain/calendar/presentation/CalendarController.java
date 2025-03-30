package festival.dev.domain.calendar.presentation;

import com.auth0.jwt.JWT;
import com.auth0.jwt.interfaces.DecodedJWT;
import festival.dev.domain.calendar.presentation.dto.Request.CalendarInsertRequest;
import festival.dev.domain.calendar.service.CalendarService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/calendar")
@RequiredArgsConstructor
public class CalendarController {

    private final CalendarService calendarService;

    @PostMapping
    public ResponseEntity<?> insert(@RequestBody CalendarInsertRequest request,@RequestHeader String authorization){
        try {
            Long userID = getUserID(authorization);
            return ResponseEntity.ok(calendarService.insert(request, userID));
        }catch (Exception e){
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @GetMapping
    public ResponseEntity<?> findDate(@RequestParam String date ,@RequestHeader String authorization){
        try{
            Long userID = getUserID(authorization);
            return ResponseEntity.ok(calendarService.getDateCalendar(date, userID));
        }catch (Exception e){
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @GetMapping("/month")
    public ResponseEntity<?> findMonth(@RequestHeader String authorization){
        try{
            Long userID = getUserID(authorization);
            return ResponseEntity.ok(calendarService.getByMonth(userID));
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