package festival.dev.global.security.oauth;

import festival.dev.global.security.oauth.provider.*;

import java.util.Map;

public class OAuth2UserInfoFactory {
    public static OAuth2UserInfo getOAuth2UserInfo(String provider, Map<String, Object> attributes) {
        switch (provider.toLowerCase()) {
            case "google":
                return new GoogleUserInfo(attributes);
            case "kakao":
                return new KakaoUserInfo(attributes);
            case "naver":
                Map<String, Object> responseAttributes = (Map<String, Object>) attributes.get("response");
                return new NaverUserInfo(responseAttributes);
            default:
                throw new IllegalArgumentException("지원하지 않는 OAuth 제공자: " + provider);
        }
    }
}
