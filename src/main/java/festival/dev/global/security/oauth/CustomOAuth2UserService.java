package festival.dev.global.security.oauth;


import festival.dev.domain.user.entity.User;
import festival.dev.domain.user.repository.UserRepository;
import festival.dev.global.security.jwt.JwtUtil;
import festival.dev.global.security.oauth.provider.OAuth2UserInfo;
import lombok.RequiredArgsConstructor;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.Optional;

@Service
public class CustomOAuth2UserService extends DefaultOAuth2UserService {

    private final UserRepository userRepository;
    private final JwtUtil jwtUtil;

    public CustomOAuth2UserService(UserRepository userRepository, JwtUtil jwtUtil) {
        this.userRepository = userRepository;
        this.jwtUtil = jwtUtil;
    }

    @Override
    public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {
        OAuth2User oAuth2User = super.loadUser(userRequest);
        String provider = userRequest.getClientRegistration().getRegistrationId();
        Map<String, Object> attributes = oAuth2User.getAttributes();
        OAuth2UserInfo userInfo = OAuth2UserInfoFactory.getOAuth2UserInfo(provider, attributes);

        Optional<User> existingUser = userRepository.findByEmail(userInfo.getEmail());

        if (existingUser.isPresent()) {
            User user = existingUser.get();
            if (!user.getProvider().equalsIgnoreCase(provider)) {
                // 처음 로그인한 provider와 다름
                throw new OAuth2AuthenticationException("이미 가입된 이메일입니다. 기존 로그인 방식: " + user.getProvider());
            }

            return new CustomOAuth2User(user, attributes, jwtUtil.generateAccessToken(user.getEmail(), user.getRole(), user.getId()));
        }

        User newUser = registerOAuthUser(provider, userInfo);

        //  CustomOAuth2User를 OAuth2User 타입으로 반환
        return new CustomOAuth2User(newUser, attributes,
                jwtUtil.generateAccessToken(newUser.getEmail(), newUser.getRole(), newUser.getId()));
    }

    private User registerOAuthUser(String provider, OAuth2UserInfo userInfo) {
        String email = userInfo.getEmail();

        //  Google은 이메일 앞부분을 username으로 설정
        //  Naver/Kakao는 제공된 name을 username으로 설정
        String username;
        if ("google".equalsIgnoreCase(provider)) {
            username = (email != null && !email.isEmpty()) ? email.split("@")[0] : "google_user";
        } else {
            username = (userInfo.getName() != null && !userInfo.getName().isEmpty())
                    ? userInfo.getName()
                    : "user" + System.currentTimeMillis(); // 네이버에서 name이 없을 경우 대비
        }

        //  name이 null이면 username을 name으로 설정
        String name = (userInfo.getName() == null || userInfo.getName().isEmpty()) ? username : userInfo.getName();

        return userRepository.save(User.builder()
                .email(email)
                .name(name) //  name이 항상 존재
                .password("") //  OAuth 사용자는 비밀번호 필요 없음
                .provider(provider.toUpperCase())
                .userCode(generateUserCode())
                .role("USER")
                .build());
    }
    private String generateUserCode() {
        String code;
        do {
            code = org.apache.commons.lang3.RandomStringUtils.randomAlphanumeric(8).toUpperCase(); // 예: AB12CD34
        } while (userRepository.existsByUserCode(code));
        return code;
    }


}

