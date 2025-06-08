package festival.dev.global.security.oauth;

import festival.dev.global.security.jwt.JwtUtil;
import festival.dev.global.security.config.CookieProperties;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;
import org.springframework.http.ResponseCookie;


import java.io.IOException;
import java.time.Duration;

@Component
@RequiredArgsConstructor
public class OAuth2SuccessHandler implements AuthenticationSuccessHandler {

    private final JwtUtil jwtUtil;
    private final CookieProperties cookieProperties;

    @Value("${frontend.url}")
    private String frontendUrl;

    @Value("${jwt.access}")
    private long accessTokenValidity;

    @Value("${jwt.refresh}")
    private long refreshTokenValidity;

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request,
                                        HttpServletResponse response,
                                        Authentication authentication) throws IOException {

        Object principal = authentication.getPrincipal();

        String email;
        String role = "USER";
        Long userId;

        if (principal instanceof CustomOAuth2User customUser) {
            email = customUser.getEmail();
            role = customUser.getAuthorities().iterator().next().getAuthority().replace("ROLE_", "");
            userId = customUser.getUser().getId();
        } else {
            response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "인증 실패");
            return;
        }

        String accessToken = jwtUtil.generateAccessToken(email, role, userId);
        String refreshToken = jwtUtil.generateRefreshToken(email);

        setJwtCookie(response, "access_token", accessToken, accessTokenValidity);
        setJwtCookie(response, "refresh_token", refreshToken, refreshTokenValidity);

        response.sendRedirect(frontendUrl + "/oauth/success");
    }

    private void setJwtCookie(HttpServletResponse response, String name, String value, long maxAgeMs) {
        ResponseCookie cookie = ResponseCookie.from(name, value)
                .httpOnly(true)
                .secure(true)
                .sameSite("None")
                .path("/")
                .domain(cookieProperties.getDomain())
                .maxAge(Duration.ofMillis(maxAgeMs))
                .build();

        response.addHeader("Set-Cookie", cookie.toString());
    }
}
