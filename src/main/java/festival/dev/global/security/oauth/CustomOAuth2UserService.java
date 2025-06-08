package festival.dev.global.security.oauth;

import festival.dev.domain.user.entity.User;
import festival.dev.domain.user.repository.UserRepository;
import festival.dev.global.security.jwt.JwtUtil;
import festival.dev.global.security.oauth.dto.KakaoTokenResponse;
import festival.dev.global.security.oauth.dto.KakaoUserInfoResponse;
import festival.dev.global.security.oauth.dto.NaverTokenResponse;
import festival.dev.global.security.oauth.dto.NaverUserInfoResponse;
import festival.dev.global.security.oauth.provider.KakaoUserInfo;
import festival.dev.global.security.oauth.provider.NaverUserInfo;
import festival.dev.global.security.oauth.provider.OAuth2UserInfo;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.RandomStringUtils;
import org.springframework.http.*;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import java.util.Map;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class CustomOAuth2UserService extends DefaultOAuth2UserService {

    private final UserRepository userRepository;
    private final JwtUtil jwtUtil;

    @Override
    public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {
        String provider = userRequest.getClientRegistration().getRegistrationId();

        if (provider.equals("naver")) {
            return handleNaver(userRequest);
        } else if (provider.equals("kakao")) {
            return handleKakao(userRequest);
        }

        // Google 전용 처리
        OAuth2User oAuth2User = super.loadUser(userRequest);
        Map<String, Object> attributes = oAuth2User.getAttributes();
        OAuth2UserInfo userInfo = OAuth2UserInfoFactory.getOAuth2UserInfo(provider, attributes);

        if (userInfo.getEmail() == null || userInfo.getEmail().isEmpty()) {
            throw new OAuth2AuthenticationException("이메일 정보를 가져올 수 없습니다.");
        }

        User user = saveOrUpdate(userInfo, provider);
        String jwtToken = jwtUtil.generateAccessToken(user.getEmail(), user.getRole(), user.getId());

        return new CustomOAuth2User(user, attributes);
    }

    private OAuth2User handleNaver(OAuth2UserRequest userRequest) {
        String accessToken = userRequest.getAccessToken().getTokenValue();

        HttpHeaders headers = new HttpHeaders();
        headers.add("Authorization", "Bearer " + accessToken);
        HttpEntity<Void> entity = new HttpEntity<>(headers);

        RestTemplate restTemplate = new RestTemplate();
        ResponseEntity<NaverUserInfoResponse> userInfoResponse = restTemplate.exchange(
                "https://openapi.naver.com/v1/nid/me",
                HttpMethod.GET,
                entity,
                NaverUserInfoResponse.class
        );

        Map<String, Object> attributes = Map.of(
                "id", userInfoResponse.getBody().getResponse().getId(),
                "email", userInfoResponse.getBody().getResponse().getEmail(),
                "name", userInfoResponse.getBody().getResponse().getName()
        );

        OAuth2UserInfo userInfo = new NaverUserInfo(attributes);
        User user = saveOrUpdate(userInfo, "naver");

        return new CustomOAuth2User(user, attributes);
    }


    private OAuth2User handleKakao(OAuth2UserRequest userRequest) {
        String accessToken = userRequest.getAccessToken().getTokenValue();

        HttpHeaders headers = new HttpHeaders();
        headers.add("Authorization", "Bearer " + accessToken);
        HttpEntity<Void> entity = new HttpEntity<>(headers);

        RestTemplate restTemplate = new RestTemplate();
        ResponseEntity<Map> userInfoResponse = restTemplate.exchange(
                "https://kapi.kakao.com/v2/user/me",
                HttpMethod.GET,
                entity,
                Map.class
        );

        Map<String, Object> kakaoAttributes = userInfoResponse.getBody();

        // 안전하게 파싱
        Map<String, Object> kakaoAccount = (Map<String, Object>) kakaoAttributes.get("kakao_account");
        Map<String, Object> profile = kakaoAccount != null ? (Map<String, Object>) kakaoAccount.get("profile") : null;

        String id = kakaoAttributes.get("id").toString();
        String email = kakaoAccount != null ? (String) kakaoAccount.get("email") : null;
        String name = profile != null ? (String) profile.get("nickname") : null;

        Map<String, Object> attributes = Map.of(
                "id", id,
                "email", email,
                "name", name
        );

        OAuth2UserInfo userInfo = new KakaoUserInfo(attributes);
        User user = saveOrUpdate(userInfo, "kakao");

        return new CustomOAuth2User(user, attributes);
    }


    private User saveOrUpdate(OAuth2UserInfo userInfo, String provider) {
        Optional<User> existingUser = userRepository.findByEmail(userInfo.getEmail());

        if (existingUser.isPresent()) {
            User user = existingUser.get();
            if (!user.getProvider().equalsIgnoreCase(provider)) {
                throw new OAuth2AuthenticationException("이미 가입된 이메일입니다. 기존 로그인 방식: " + user.getProvider());
            }
            return user;
        }

        String email = userInfo.getEmail();
        String username = (email != null && !email.isEmpty()) ? email.split("@")[0] : "user_" + System.currentTimeMillis();
        String name = (userInfo.getName() == null || userInfo.getName().isEmpty()) ? username : userInfo.getName();

        return userRepository.save(User.builder()
                .email(email)
                .name(name)
                .password("")
                .provider(provider.toUpperCase())
                .userCode(generateUserCode())
                .role("USER")
                .build());
    }

    private String generateUserCode() {
        String code;
        do {
            code = RandomStringUtils.randomAlphanumeric(8).toUpperCase();
        } while (userRepository.existsByUserCode(code));
        return code;
    }
}