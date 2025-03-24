package festival.dev.global.security.oauth;

import lombok.Getter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.oauth2.core.user.OAuth2User;

import java.util.Collection;
import java.util.Map;

@Getter
public class OAuth2UserWithToken implements OAuth2User {
    private final OAuth2User oAuth2User;
    private final String accessToken;
    private final String refreshToken;

    public OAuth2UserWithToken(OAuth2User oAuth2User, String accessToken, String refreshToken) {
        this.oAuth2User = oAuth2User;
        this.accessToken = accessToken;
        this.refreshToken = refreshToken;
    }

    @Override
    public Map<String, Object> getAttributes() {
        return oAuth2User.getAttributes();
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return oAuth2User.getAuthorities();
    }

    @Override
    public String getName() {
        return oAuth2User.getName();
    }
}
