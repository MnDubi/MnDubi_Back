package festival.dev.domain.auth.service;

import festival.dev.domain.auth.dto.AuthRequestDto;
import festival.dev.domain.auth.dto.AuthResponseDto;
import festival.dev.domain.user.entity.User;
import festival.dev.domain.user.repository.UserRepository;
import festival.dev.global.security.jwt.JwtUtil;
import festival.dev.global.security.config.CookieProperties;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.RandomStringUtils;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseCookie;
import java.time.Duration;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;
    private final CookieProperties cookieProperties;

    @Value("${jwt.access}")
    private long accessTokenValidity;

    @Value("${jwt.refresh}")
    private long refreshTokenValidity;

    // 자체 회원가입
    public void register(String email, String password, String name, HttpServletResponse response) {
        if (userRepository.findByEmail(email).isPresent()) {
            throw new RuntimeException("이미 존재하는 이메일입니다.");
        }

        User newUser = User.builder()
                .email(email)
                .password(passwordEncoder.encode(password))
                .name(name)
                .provider("LOCAL")
                .userCode(generateUserCode())
                .role("USER")
                .build();

        userRepository.save(newUser);


    }


    // 자체 로그인
    public AuthResponseDto login(AuthRequestDto request, HttpServletResponse response) {
        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new RuntimeException("존재하지 않는 이메일입니다."));

        if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            throw new RuntimeException("비밀번호가 일치하지 않습니다.");
        }

        // JWT는 HttpOnly 쿠키로 발급
        issueJwtCookies(response, user);

        return new AuthResponseDto(
                user.getId(),
                user.getEmail(),
                user.getName(),
                user.getUserCode()
        );
    }


//    // JWT 생성
//    private AuthResponseDto generateTokenResponse(User user) {
//        String accessToken = jwtUtil.generateAccessToken(user.getEmail(), user.getRole(), user.getId());
//        String refreshToken = jwtUtil.generateRefreshToken(user.getEmail());
//        return new AuthResponseDto(accessToken, refreshToken);
//    }

    // Refresh 토큰을 통한 재발급
    public void refreshTokenFromCookie(String refreshToken, HttpServletResponse response) {
        if (!jwtUtil.isRefreshTokenValid(refreshToken)) {
            throw new RuntimeException("유효하지 않은 Refresh Token입니다.");
        }

        String email = jwtUtil.validateToken(refreshToken);
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("사용자를 찾을 수 없습니다."));

        String newAccessToken = jwtUtil.generateAccessToken(user.getEmail(), user.getRole(), user.getId());
        setJwtCookie(response, "access_token", newAccessToken, (int) accessTokenValidity);
    }

    public void logout(HttpServletResponse response) {
        expireJwtCookie(response, "access_token");
        expireJwtCookie(response, "refresh_token");
    }

    public void changePassword(Long userId, String currentPassword, String newPassword) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("사용자를 찾을 수 없습니다."));

        if (!passwordEncoder.matches(currentPassword, user.getPassword())) {
            throw new RuntimeException("기존 비밀번호가 일치하지 않습니다.");
        }

        user.setPassword(passwordEncoder.encode(newPassword));
        userRepository.save(user);
    }

    private void issueJwtCookies(HttpServletResponse response, User user) {
        String accessToken = jwtUtil.generateAccessToken(user.getEmail(), user.getRole(), user.getId());
        String refreshToken = jwtUtil.generateRefreshToken(user.getEmail());

        setJwtCookie(response, "access_token", accessToken, (int) accessTokenValidity);
        setJwtCookie(response, "refresh_token", refreshToken, (int) refreshTokenValidity);
    }

    private void setJwtCookie(HttpServletResponse response, String name, String value, long maxAgeMs) {
        System.out.println(" 쿠키 발급 시도됨 → 이름: " + name + ", 길이: " + value.length());

        ResponseCookie.ResponseCookieBuilder builder = ResponseCookie.from(name, value)
                .httpOnly(true)
                // 개발환경(localhost)일 땐 secure false, 배포환경일 땐 true
                .secure(!cookieProperties.getDomain().equals("localhost"))
                // SameSite 옵션도 환경에 따라 분기 처리
                .sameSite(cookieProperties.getDomain().equals("localhost") ? "Lax" : "None")
                .path("/")
                .maxAge(Duration.ofMillis(maxAgeMs));

        if (!cookieProperties.getDomain().equals("localhost")) {
            builder.domain(cookieProperties.getDomain());
        }

        ResponseCookie cookie = builder.build();
        response.addHeader("Set-Cookie", cookie.toString());
    }


    private void expireJwtCookie(HttpServletResponse response, String name) {
        ResponseCookie.ResponseCookieBuilder builder = ResponseCookie.from(name, "")
                .path("/")
                .httpOnly(true)
                .secure(false)
                .sameSite("None")
                .maxAge(0); // 즉시 만료

        // 도메인 설정도 발급할 때와 일치하게 설정
        if (!cookieProperties.getDomain().equals("localhost")) {
            builder.domain(cookieProperties.getDomain());
        }

        ResponseCookie expiredCookie = builder.build();
        response.addHeader("Set-Cookie", expiredCookie.toString());
    }


    private String generateUserCode() {
        String code;
        do {
            code = org.apache.commons.lang3.RandomStringUtils.randomAlphanumeric(8).toUpperCase(); // 예: AB12CD34
        } while (userRepository.existsByUserCode(code));
        return code;
    }


}
