package festival.dev.domain.auth.service;

import festival.dev.domain.auth.dto.OAuthUserInfoDto;
import festival.dev.domain.user.entity.User;
import festival.dev.domain.user.repository.UserRepository;
import festival.dev.global.security.jwt.JwtTokenProvider;
import festival.dev.global.security.oauth.OAuth2UserWithToken;
import lombok.RequiredArgsConstructor;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class OAuth2UserService extends DefaultOAuth2UserService {

    private final UserRepository userRepository;
    private final JwtTokenProvider jwtTokenProvider;

    @Override
    public OAuth2UserWithToken loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {
        OAuth2User oAuth2User = super.loadUser(userRequest);
        String provider = userRequest.getClientRegistration().getRegistrationId();

        OAuthUserInfoDto userInfo = new OAuthUserInfoDto(provider, oAuth2User.getAttributes());

        if (userInfo.getEmail() == null) {
            throw new RuntimeException("OAuth 로그인 실패: 이메일 정보를 가져올 수 없습니다.");
        }

        Optional<User> existingUser = userRepository.findByEmail(userInfo.getEmail());

        User user;
        if (existingUser.isPresent()) {
            user = existingUser.get();
        } else {
            user = User.builder()
                    .email(userInfo.getEmail())
                    .name(userInfo.getName())
                    .provider(provider)
                    .build();
            userRepository.save(user);
        }

        // JWT 발급
        String accessToken = jwtTokenProvider.createAccessToken(user.getEmail());
        String refreshToken = jwtTokenProvider.createRefreshToken(user.getEmail());

        return new OAuth2UserWithToken(oAuth2User, accessToken, refreshToken);
    }


}
