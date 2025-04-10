package festival.dev.global.security.oauth;

import festival.dev.domain.user.entity.User;
import festival.dev.domain.user.repository.UserRepository;
import festival.dev.global.security.jwt.JwtUtil;
import festival.dev.global.security.oauth.provider.OAuth2UserInfo;
import festival.dev.global.security.oauth.OAuth2UserInfoFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class CustomOAuth2UserService extends DefaultOAuth2UserService {

    private final UserRepository userRepository;
    private final JwtUtil jwtUtil;

    @Override
    public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {
        OAuth2User oAuth2User = super.loadUser(userRequest);
        String provider = userRequest.getClientRegistration().getRegistrationId();
        Map<String, Object> attributes = oAuth2User.getAttributes();

        OAuth2UserInfo userInfo = OAuth2UserInfoFactory.getOAuth2UserInfo(provider, attributes);

        if (userInfo.getEmail() == null || userInfo.getEmail().isEmpty()) {
            throw new OAuth2AuthenticationException("이메일 정보를 가져올 수 없습니다.");
        }

        Optional<User> existingUser = userRepository.findByEmail(userInfo.getEmail());

        User user;
        if (existingUser.isPresent()) {
            user = existingUser.get();

            if (!user.getProvider().equalsIgnoreCase(provider)) {
                throw new OAuth2AuthenticationException(
                        "이미 가입된 이메일입니다. 기존 로그인 방식: " + user.getProvider()
                );
            }
        } else {
            user = registerOAuthUser(provider, userInfo);
        }

        String jwtToken = jwtUtil.generateAccessToken(user.getEmail(), user.getRole(), user.getId());

        return new CustomOAuth2User(user, attributes, jwtToken);
    }

    private User registerOAuthUser(String provider, OAuth2UserInfo userInfo) {
        String email = userInfo.getEmail();
        String username = (email != null && !email.isEmpty()) ? email.split("@")[0] : "user_" + System.currentTimeMillis();
        String name = (userInfo.getName() == null || userInfo.getName().isEmpty()) ? username : userInfo.getName();

        return userRepository.save(User.builder()
                .email(email)
                .name(name)
                .password("") // OAuth 사용자: 비밀번호 없음
                .provider(provider.toUpperCase())
                .userCode(generateUserCode())
                .role("USER")
                .build());
    }

    private String generateUserCode() {
        String code;
        do {
            code = org.apache.commons.lang3.RandomStringUtils.randomAlphanumeric(8).toUpperCase(); // 예: A1B2C3D4
        } while (userRepository.existsByUserCode(code));
        return code;
    }
}
