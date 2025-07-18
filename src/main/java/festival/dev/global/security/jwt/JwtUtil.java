package festival.dev.global.security.jwt;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTVerificationException;
import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.Date;

@Slf4j
@Component
public class JwtUtil {

    private Algorithm algorithm;

    @Value("${jwt.secret}")
    private String secret;

    @Value("${jwt.access}")
    private long accessTokenValidity;

    @Value("${jwt.refresh}")
    private long refreshTokenValidity;

    @PostConstruct
    public void init() {
        this.algorithm = Algorithm.HMAC256(secret);
    }

    public String generateAccessToken(String email, String role,Long userId) {
        return JWT.create()
                .withSubject(email)
                .withClaim("role", role)
                .withClaim("userId", userId)
                .withIssuedAt(new Date())
                .withExpiresAt(new Date(System.currentTimeMillis() + accessTokenValidity))
                .sign(algorithm);
    }

    public String generateRefreshToken(String email) {
        return JWT.create()
                .withSubject(email)
                .withIssuedAt(new Date())
                .withExpiresAt(new Date(System.currentTimeMillis() + refreshTokenValidity))
                .sign(algorithm);
    }


    public String validateToken(String token) {
        try {
            return JWT.require(algorithm)
                    .build()
                    .verify(token)
                    .getSubject();
        } catch (JWTVerificationException e) {
            log.warn("JWT 검증 실패: {}", e.getMessage());
            throw new RuntimeException("Invalid JWT token: " + e.getMessage());
        }
    }

    public boolean isRefreshTokenValid(String token) {
        try {
            Date expiration = JWT.require(algorithm)
                    .build()
                    .verify(token)
                    .getExpiresAt();
            return expiration.after(new Date());
        } catch (Exception e) {
            return false;
        }
    }

    public Long getUserIdFromToken(String token) {
        try {
            return JWT.require(algorithm)
                    .build()
                    .verify(token)
                    .getClaim("userId").asLong();
        } catch (JWTVerificationException e) {
            log.warn("userId 추출 실패: {}", e.getMessage());
            throw new RuntimeException("Invalid JWT token: " + e.getMessage());
        }
    }


}
