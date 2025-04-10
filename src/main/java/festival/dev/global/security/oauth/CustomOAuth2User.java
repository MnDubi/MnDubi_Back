package festival.dev.global.security.oauth;

import festival.dev.domain.user.entity.User;
import lombok.Getter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.oauth2.core.user.OAuth2User;

import java.util.Collection;
import java.util.List;
import java.util.Map;

@Getter
public class CustomOAuth2User implements OAuth2User, UserDetails {

    private final User user;
    private final Map<String, Object> attributes;
    private final String token;

    public CustomOAuth2User(User user, Map<String, Object> attributes, String token) {
        this.user = user;
        this.attributes = attributes;
        this.token = token;
    }

    // OAuth2User
    @Override
    public Map<String, Object> getAttributes() {
        return attributes;
    }

    public String getEmail() {
        return user.getEmail();
    }


    @Override
    public String getName() {
        return user.getName(); // 또는 String.valueOf(user.getId())
    }

    // UserDetails
    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return List.of(new SimpleGrantedAuthority("ROLE_" + user.getRole()));
    }

    @Override
    public String getPassword() {
        return user.getPassword(); // OAuth 사용자일 경우 null 또는 ""
    }

    @Override
    public String getUsername() {
        return user.getEmail();
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }

    // JWT 토큰 반환 (Optional)
    public String getToken() {
        return token;
    }
}
