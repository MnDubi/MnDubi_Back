package festival.dev.domain.auth.service;

import festival.dev.domain.auth.dto.AuthRequestDto;
import festival.dev.domain.auth.dto.AuthResponseDto;
import festival.dev.domain.user.entity.User;
import festival.dev.domain.user.repository.UserRepository;
import festival.dev.global.security.jwt.JwtUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;

    public AuthService(UserRepository userRepository, PasswordEncoder passwordEncoder, JwtUtil jwtUtil) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtUtil = jwtUtil;
    }

    // 자체 회원가입
    public User registerUser(String email, String password, String name) {
        if (userRepository.findByEmail(email).isPresent()) {
            throw new RuntimeException("이미 존재하는 이메일입니다.");
        }

        User newUser = User.builder()
                .email(email)
                .password(passwordEncoder.encode(password))
                .name(name)
                .provider("LOCAL") // 자체 회원가입은 "LOCAL"
                .userCode(generateUserCode())
                .role("USER")
                .build();

        return userRepository.save(newUser);
    }

    // 자체 로그인
    public AuthResponseDto login(AuthRequestDto request) {
        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new RuntimeException("존재하지 않는 이메일입니다."));

        if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            throw new RuntimeException("비밀번호가 일치하지 않습니다.");
        }

        return generateTokenResponse(user);
    }

    // JWT 생성
    private AuthResponseDto generateTokenResponse(User user) {
        String accessToken = jwtUtil.generateAccessToken(user.getEmail(), user.getRole(), user.getId());
        String refreshToken = jwtUtil.generateRefreshToken(user.getEmail());
        return new AuthResponseDto(accessToken, refreshToken);
    }

    // Refresh 토큰을 통한 재발급
    public AuthResponseDto refreshAccessToken(String refreshToken) {
        if (!jwtUtil.isRefreshTokenValid(refreshToken)) {
            throw new RuntimeException("유효하지 않은 Refresh Token입니다.");
        }

        String email = jwtUtil.validateToken(refreshToken);
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("사용자를 찾을 수 없습니다."));

        String newAccessToken = jwtUtil.generateAccessToken(user.getEmail(), user.getRole(), user.getId());
        return new AuthResponseDto(newAccessToken, refreshToken);
    }

    private String generateUserCode() {
        String code;
        do {
            code = org.apache.commons.lang3.RandomStringUtils.randomAlphanumeric(8).toUpperCase(); // 예: AB12CD34
        } while (userRepository.existsByUserCode(code));
        return code;
    }
}
