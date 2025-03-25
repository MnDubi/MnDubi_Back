package festival.dev.global.security.oauth;

import festival.dev.domain.user.entity.User;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.oauth2.core.user.OAuth2User;
import java.util.Collection;
import java.util.List;
import java.util.Map;


public class CustomOAuth2User implements OAuth2User, UserDetails {
    private final User user;
    private final Map<String, Object> attributes;
    private final String token;

    public CustomOAuth2User(User user, Map<String, Object> attributes, String token) {
        this.user = user;
        this.attributes = attributes;
        this.token = token;
    }

    //  OAuth2User 인터페이스 구현
    @Override
    public Map<String, Object> getAttributes() {
        return attributes;
    }

    @Override
    public String getName() {
        return user.getName();
    }

    //  추가: JWT 토큰 반환 메서드
    public String getToken() {
        return token;
    }

    public String getEmail() {
        return user.getEmail();
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return List.of(new SimpleGrantedAuthority("ROLE_" + user.getRole()));
    }

    @Override
    public String getPassword() {
        return user.getPassword();
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
}
