package festival.dev.domain.calendar.presentation;

import festival.dev.domain.calendar.service.CalendarService;
import festival.dev.domain.user.entity.CustomUserDetails;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/calendar")
@RequiredArgsConstructor
public class CalendarController {

    private final CalendarService calendarService;

//    @Value("${jwt.secret}")
//    private String secret;

    @GetMapping("/private")
    public ResponseEntity<?> findDate(@RequestParam String date ,/*@RequestHeader String authorization*/@AuthenticationPrincipal CustomUserDetails user){
        try{
//            Long userID = getUserID(authorization);
            return ResponseEntity.ok(calendarService.getDateCalendarWithPrivate(date, /*userID*/user.getUserID()));
        }catch (Exception e){
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @GetMapping("/private/month")
    public ResponseEntity<?> findMonth(/*@RequestHeader String authorization*/@AuthenticationPrincipal CustomUserDetails user){
        try{
//            Long userID = getUserID(authorization);
            return ResponseEntity.ok(calendarService.getByMonthWithPrivate(/*userID*/user.getUserID()));
        }
        catch (Exception e){
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

//    public Long getUserID(String auth) {
//        String token = auth.replace("Bearer ", "");
//
//        Claims claims = Jwts.parserBuilder()
//                .setSigningKey(secret.getBytes())
//                .build()
//                .parseClaimsJws(token).getBody();
//
//        return claims.get("userId", Long.class);
//    }
}