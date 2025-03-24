package festival.dev.global.security.oauth;


import festival.dev.domain.user.entity.User;
import festival.dev.domain.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.DefaultOAuth2User;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;
import java.util.HashMap;

import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class CustomOAuth2UserService extends DefaultOAuth2UserService {

    private final UserRepository userRepository;

    @Override
    public OAuth2User loadUser(OAuth2UserRequest request) throws OAuth2AuthenticationException {
        OAuth2User oAuth2User = super.loadUser(request);
        String provider = request.getClientRegistration().getRegistrationId();
        Map<String, Object> rawAttributes = oAuth2User.getAttributes();
        Map<String, Object> attributes = new HashMap<>();

        String email;
        String name;
        String id;

        switch (provider) {
            case "kakao" -> {
                Map<String, Object> kakaoAccount = (Map<String, Object>) rawAttributes.get("kakao_account");
                Map<String, Object> profile = (Map<String, Object>) kakaoAccount.get("profile");

                email = kakaoAccount.get("email").toString();
                name = profile.get("nickname").toString();
                id = rawAttributes.get("id").toString();

                attributes.put("id", id);
                attributes.put("email", email);
                attributes.put("name", name);
            }

            case "naver" -> {
                Map<String, Object> response = (Map<String, Object>) rawAttributes.get("response");

                email = response.get("email").toString();
                name = response.get("name").toString();
                id = response.get("id").toString();

                attributes.put("id", id);
                attributes.put("email", email);
                attributes.put("name", name);
            }
            default -> { // google
                email = rawAttributes.get("email").toString();
                name = rawAttributes.get("name").toString();
                id = rawAttributes.get("sub").toString();

                attributes.put("id", id);
                attributes.put("email", email);
                attributes.put("name", name);
            }
        }

        userRepository.findByEmail(email).orElseGet(() ->
                userRepository.save(User.builder()
                        .email(email)
                        .name(name)
                        .provider(provider)
                        .build())
        );

        return new DefaultOAuth2User(
                List.of(new SimpleGrantedAuthority("ROLE_USER")),
                attributes,
                "id" // 💡 위에서 명시적으로 넣어줬기 때문에 안전
        );
    }


    private String extractEmail(String provider, Map<String, Object> attributes) {
        if (provider.equals("kakao")) {
            Map<String, Object> account = (Map<String, Object>) attributes.get("kakao_account");
            return account.get("email").toString();
        } else if (provider.equals("naver")) {
            Map<String, Object> response = (Map<String, Object>) attributes.get("response");
            return response.get("email").toString();
        } else {
            return attributes.get("email").toString();
        }
    }

    private String extractName(String provider, Map<String, Object> attributes) {
        if (provider.equals("kakao")) {
            Map<String, Object> profile = (Map<String, Object>) ((Map<String, Object>) attributes.get("kakao_account")).get("profile");
            return profile.get("nickname").toString();
        } else if (provider.equals("naver")) {
            Map<String, Object> response = (Map<String, Object>) attributes.get("response");
            return response.get("name").toString();
        } else {
            return attributes.get("name").toString();
        }
    }

//    private String getNameAttributeKey(String provider) {
//        return switch (provider) {
//            case "kakao" -> "id";
//            case "naver" -> "id";
//            default -> "sub"; // Google
//        };
//    }
}
