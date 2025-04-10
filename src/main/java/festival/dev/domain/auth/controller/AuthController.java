package festival.dev.domain.auth.controller;

import festival.dev.domain.auth.dto.AuthRequestDto;
import festival.dev.domain.auth.dto.AuthResponseDto;
import festival.dev.domain.auth.dto.OAuthUserInfoDto;
import festival.dev.global.security.oauth.CustomOAuth2User;
import festival.dev.domain.auth.service.AuthService;
import festival.dev.global.security.oauth.dto.OAuthCodeInfo;
import festival.dev.global.security.oauth.OAuthCodeStore;
import festival.dev.global.security.jwt.JwtUtil;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.web.bind.annotation.*;
import java.util.Map;

@RestController
@RequestMapping("/auth")
public class AuthController {

    private final AuthService authService;
    private final OAuthCodeStore codeStore;
    private final JwtUtil jwtUtil;

    public AuthController(AuthService authService, OAuthCodeStore codeStore, JwtUtil jwtUtil) {
        this.authService = authService;
        this.codeStore = codeStore;
        this.jwtUtil = jwtUtil;
    }

    // 자체 회원가입
    @PostMapping("/register")
    public ResponseEntity<?> registerUser(@RequestBody OAuthUserInfoDto request) {
        authService.registerUser(request.getEmail(), request.getPassword(), request.getName());
        return ResponseEntity.ok("회원가입이 완료되었습니다.");
    }

    // 자체 로그인 (이메일 & 비밀번호)
    @PostMapping("/login")
    public ResponseEntity<AuthResponseDto> loginUser(@RequestBody AuthRequestDto request, HttpServletResponse response) {
        AuthResponseDto tokenResponse = authService.login(request);
        return ResponseEntity.ok(tokenResponse);
    }

    @PostMapping("/refresh")
    public ResponseEntity<AuthResponseDto> refreshAccessToken(@RequestHeader("Authorization") String refreshToken) {
        AuthResponseDto tokenResponse = authService.refreshAccessToken(refreshToken);
        return ResponseEntity.ok(tokenResponse);
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

    // OAuth 로그인 후 JWT 발급
    @GetMapping("/oauth2/callback")
    public ResponseEntity<AuthResponseDto> oauthCallback(OAuth2AuthenticationToken authToken) {
        CustomOAuth2User oAuth2User = (CustomOAuth2User) authToken.getPrincipal();
        AuthResponseDto tokenResponse = new AuthResponseDto(oAuth2User.getToken(), null);
        return ResponseEntity.ok(tokenResponse);
    }
}