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
        System.out.println("🔐 쿠키 발급 시도됨 → 이름: " + name + ", 길이: " + value.length());

        // 도메인 지정은 실제 배포 도메인과 맞지 않으면 저장 안 됨 (로컬에서는 생략)
        ResponseCookie.ResponseCookieBuilder builder = ResponseCookie.from(name, value)
                .httpOnly(true)
                .secure(true) // HTTPS 환경 필수, 로컬 개발 중이면 false로 변경
                .sameSite("None") // 크로스 도메인 대응
                .path("/")
                .maxAge(Duration.ofMillis(maxAgeMs));

        // 배포 환경인 경우에만 domain 설정
        if (!cookieProperties.getDomain().equals("localhost")) {
            builder.domain(cookieProperties.getDomain());
        }

        ResponseCookie cookie = builder.build();
        response.addHeader("Set-Cookie", cookie.toString());
    }

}
