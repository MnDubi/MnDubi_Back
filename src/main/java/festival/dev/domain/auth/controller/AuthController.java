package festival.dev.domain.auth.controller;

import festival.dev.domain.auth.dto.AuthRequestDto;
import festival.dev.domain.auth.dto.AuthResponseDto;
import festival.dev.domain.auth.dto.OAuthUserInfoDto;
import festival.dev.global.security.oauth.CustomOAuth2User;
import festival.dev.domain.auth.service.AuthService;
import festival.dev.global.security.oauth.dto.OAuthCodeInfo;
import festival.dev.global.security.oauth.OAuthCodeStore;
import festival.dev.global.security.jwt.JwtUtil;
import festival.dev.domain.auth.dto.RefreshRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.Cookie;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.web.bind.annotation.*;
import java.util.Map;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;
    private final OAuthCodeStore codeStore;
    private final JwtUtil jwtUtil;




    // 자체 회원가입
    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody OAuthUserInfoDto request, HttpServletResponse response) {
        authService.register(request.getEmail(), request.getPassword(), request.getName(), response);
        return ResponseEntity.ok("회원가입 완료");
    }

    // 자체 로그인 (이메일 & 비밀번호)
    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody AuthRequestDto request, HttpServletResponse response) {
        authService.login(request, response);
        return ResponseEntity.ok("로그인 성공");
    }

    @PostMapping("/refresh")
    public ResponseEntity<?> refresh(HttpServletRequest request, HttpServletResponse response) {
        String refreshToken = extractTokenFromCookies(request, "refresh_token");
        authService.refreshTokenFromCookie(refreshToken, response);
        return ResponseEntity.ok("Access Token 재발급 완료");
    }

    @PostMapping("/logout")
    public ResponseEntity<?> logout(HttpServletResponse response) {
        authService.logout(response);
        return ResponseEntity.ok("로그아웃 완료");
    }


    @PostMapping("/token")
    public ResponseEntity<?> issueToken(@RequestBody Map<String, String> body) {
        String code = body.get("code");
        OAuthCodeInfo info = codeStore.get(code);

        if (info == null) {
            return ResponseEntity.badRequest().body("유효하지 않거나 만료된 code입니다.");
        }

        String token = jwtUtil.generateAccessToken(info.getEmail(), info.getRole(), info.getUserId());
        return ResponseEntity.ok(Map.of("accessToken", token));
    }

    // OAuth 로그인 후 사용자 정보 반환 (JWT 포함)
    @GetMapping("/oauth2/user")
    public ResponseEntity<?> getOAuthUser(@AuthenticationPrincipal CustomOAuth2User oAuth2User) {
        if (oAuth2User == null) {
            return ResponseEntity.badRequest().body("OAuth 인증 정보가 없습니다.");
        }
        return ResponseEntity.ok(oAuth2User);
    }

    private String extractTokenFromCookies(HttpServletRequest request, String cookieName) {
        if (request.getCookies() != null) {
            for (Cookie cookie : request.getCookies()) {
                if (cookie.getName().equals(cookieName)) {
                    return cookie.getValue();
                }
            }
        }
        return null;
    }
}

