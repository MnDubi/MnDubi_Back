package festival.dev.global.security.jwt;

import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.nio.charset.StandardCharsets;
import java.util.Date;

@Component
public class JwtTokenProvider {

    private final String secretKey;
    private final long accessTokenValidity;
    private final long refreshTokenValidity;

    public JwtTokenProvider(
            @Value("${jwt.secret}") String secretKey,
            @Value("${jwt.access-token-expiration}") long accessTokenValidity,
            @Value("${jwt.refresh-token-expiration}") long refreshTokenValidity) {
        this.secretKey = secretKey;
        this.accessTokenValidity = accessTokenValidity;
        this.refreshTokenValidity = refreshTokenValidity;
    }

    public String createAccessToken(String email) {
        return createToken(email, accessTokenValidity);
    }

    public String createRefreshToken(String email) {
        return createToken(email, refreshTokenValidity);
    }

    private String createToken(String email, long validity) {
        Date now = new Date();
        Date expiration = new Date(now.getTime() + validity);

        return Jwts.builder()
                .setSubject(email)
                .setIssuedAt(now)
                .setExpiration(expiration)
                .signWith(Keys.hmacShaKeyFor(secretKey.getBytes(StandardCharsets.UTF_8)), SignatureAlgorithm.HS256)
                .compact();
    }

    public boolean validateToken(String token) {
        try {
            Jwts.parserBuilder().setSigningKey(secretKey.getBytes(StandardCharsets.UTF_8)).build().parseClaimsJws(token);
            return true;
        } catch (JwtException | IllegalArgumentException e) {
            return false;
        }
    }
}
