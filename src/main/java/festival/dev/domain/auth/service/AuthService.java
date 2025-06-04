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
    public void login(AuthRequestDto request, HttpServletResponse response) {
        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new RuntimeException("존재하지 않는 이메일입니다."));

        if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            throw new RuntimeException("비밀번호가 일치하지 않습니다.");
        }

        issueJwtCookies(response, user);
    }

    // JWT 생성
    private AuthResponseDto generateTokenResponse(User user) {
        String accessToken = jwtUtil.generateAccessToken(user.getEmail(), user.getRole(), user.getId());
        String refreshToken = jwtUtil.generateRefreshToken(user.getEmail());
        return new AuthResponseDto(accessToken, refreshToken);
    }

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

    private void issueJwtCookies(HttpServletResponse response, User user) {
        String accessToken = jwtUtil.generateAccessToken(user.getEmail(), user.getRole(), user.getId());
        String refreshToken = jwtUtil.generateRefreshToken(user.getEmail());

        setJwtCookie(response, "access_token", accessToken, (int) accessTokenValidity);
        setJwtCookie(response, "refresh_token", refreshToken, (int) refreshTokenValidity);
    }

    private void setJwtCookie(HttpServletResponse response, String name, String value, int maxAge) {
        String cookie = name + "=" + value +
                "; Path=/; Max-Age=" + maxAge +
                "; HttpOnly; Secure; SameSite=None; Domain=" + cookieProperties.getDomain();

        response.addHeader("Set-Cookie", cookie);
    }

    private void expireJwtCookie(HttpServletResponse response, String name) {
        String cookie = name + "=; Path=/; Max-Age=0" +
                "; HttpOnly; Secure; SameSite=None; Domain=" + cookieProperties.getDomain();

        response.addHeader("Set-Cookie", cookie);
    }

    private String generateUserCode() {
        String code;
        do {
            code = org.apache.commons.lang3.RandomStringUtils.randomAlphanumeric(8).toUpperCase(); // 예: AB12CD34
        } while (userRepository.existsByUserCode(code));
        return code;
    }

//    //로그아웃
//    public void logout(String refreshToken) {
//        if (!jwtUtil.isRefreshTokenValid(refreshToken)) {
//            throw new RuntimeException("유효하지 않은 Refresh Token입니다.");
//        }
//        String email = jwtUtil.validateToken(refreshToken);
//        jwtUtil.invalidateToken(refreshToken);
//
//
//    }
}
