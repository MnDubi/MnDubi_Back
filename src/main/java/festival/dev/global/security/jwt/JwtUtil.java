package festival.dev.global.security.jwt;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class JwtUtil {

    private final Algorithm algorithm;
    private final long expirationTime;
    private final long refreshExpirationTime;

    private final Set<String> invalidatedTokens = ConcurrentHashMap.newKeySet();

    public JwtUtil(@Value("${jwt.secret}") String secret,
                   @Value("${jwt.access}") long expirationTime,
                   @Value("${jwt.refresh}") long refreshExpirationTime) {
        this.algorithm = Algorithm.HMAC256(secret);
        this.expirationTime = expirationTime;
        this.refreshExpirationTime = refreshExpirationTime;
    }

    public String generateAccessToken(String email, String role,Long userId) {
        return JWT.create()
                .withSubject(email)
                .withClaim("role", role)
                .withClaim("userId", userId)
                .withIssuedAt(new Date())
                .withExpiresAt(new Date(System.currentTimeMillis() + expirationTime))
                .sign(algorithm);
    }

    public String generateRefreshToken(String email) {
        return JWT.create()
                .withSubject(email)
                .withIssuedAt(new Date())
                .withExpiresAt(new Date(System.currentTimeMillis() + refreshExpirationTime))
                .sign(algorithm);
    }


    public String validateToken(String token) {
        try {
            return JWT.require(algorithm)
                    .build()
                    .verify(token)
                    .getSubject(); // 정상적인 토큰이면 이메일 반환
        } catch (Exception e) {
            throw new RuntimeException("Invalid JWT token: " + e.getMessage());
        }
    }

    public boolean isRefreshTokenValid(String token) {
        try {
            Date expiration = JWT.require(algorithm)
                    .build()
                    .verify(token)
                    .getExpiresAt();
            return expiration.before(new Date());
        } catch (Exception e) {
            return true; // 토큰 검증 실패 시 만료된 것으로 간주
        }
    }
}
