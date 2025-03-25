package festival.dev.global.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.server.ResponseStatusException;

@RestControllerAdvice
public class GlobalExceptionHandler {

    //  리프레시 토큰이 유효하지 않을 때 (403 Forbidden)
    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<String> handleInvalidTokenException(RuntimeException e) {
        return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Error: " + e.getMessage());
    }

    //  JWT 검증 실패 (401 Unauthorized)
    @ExceptionHandler(SecurityException.class)
    public ResponseEntity<String> handleSecurityException(SecurityException e) {
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("JWT Error: " + e.getMessage());
    }

    // 기타 예외 (500 Internal Server Error)
    @ExceptionHandler(Exception.class)
    public ResponseEntity<String> handleGeneralException(Exception e) {
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Internal Error: " + e.getMessage());
    }

    @ExceptionHandler(ResponseStatusException.class)
    public ResponseEntity<String> handleJwtException(ResponseStatusException ex) {
        return ResponseEntity.status(ex.getStatusCode()).body(ex.getReason());
    }
}
