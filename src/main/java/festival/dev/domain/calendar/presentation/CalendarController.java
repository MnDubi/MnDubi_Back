package festival.dev.domain.calendar.presentation;

import com.auth0.jwt.JWT;
import com.auth0.jwt.interfaces.DecodedJWT;
import festival.dev.domain.calendar.service.CalendarService;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/calendar")
@RequiredArgsConstructor
public class CalendarController {

    private final CalendarService calendarService;

    @Value("${jwt.secret}")
    private String secret;

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

    public Long getUserID(String auth) {
        String token = auth.replace("Bearer ", "");

        Claims claims = Jwts.parserBuilder()
                .setSigningKey(secret.getBytes())
                .build()
                .parseClaimsJws(token).getBody();

        return claims.get("userId", Long.class);
    }
}