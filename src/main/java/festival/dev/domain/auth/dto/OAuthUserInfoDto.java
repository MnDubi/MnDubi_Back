package festival.dev.domain.auth.dto;

import lombok.Getter;

import java.util.Map;

@Getter
public class OAuthUserInfoDto {
    private final String email;
    private final String name;

    public OAuthUserInfoDto(String provider, Map<String, Object> attributes) {
        if ("google".equals(provider)) {
            this.email = (String) attributes.get("email");
            this.name = (String) attributes.get("name");
        } else if ("naver".equals(provider)) {
            Map<String, Object> response = (Map<String, Object>) attributes.get("response");
            this.email = (String) response.get("email");
            this.name = (String) response.get("name");
        } else if ("kakao".equals(provider)) {
            Map<String, Object> account = (Map<String, Object>) attributes.get("kakao_account");
            this.email = (String) account.get("email");
            this.name = (String) ((Map<String, Object>) attributes.get("properties")).get("nickname");
        } else {
            throw new RuntimeException("지원되지 않는 OAuth Provider: " + provider);
        }
    }
}
