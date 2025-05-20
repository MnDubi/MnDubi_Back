package festival.dev.global.security.oauth;

import festival.dev.global.security.oauth.dto.OAuthCodeInfo;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.core.oidc.user.DefaultOidcUser;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.UUID;

@Component
@RequiredArgsConstructor
public class OAuth2SuccessHandler implements AuthenticationSuccessHandler {

    private final OAuthCodeStore codeStore;

    @Value("${frontend.url}")
    private String frontendUrl;

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException {
        Object principal = authentication.getPrincipal();

        String email;
        String role = "USER";
        Long userId = null;

        if (principal instanceof CustomOAuth2User customUser) {
            email = customUser.getEmail();
            role = customUser.getAuthorities().iterator().next().getAuthority().replace("ROLE_", "");
            userId = customUser.getUser().getId();

        } else if (principal instanceof DefaultOidcUser oidcUser) {
            email = oidcUser.getAttribute("email");
            // ID나 role은 필요 시 추가로 처리 가능
        } else {
            response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "지원되지 않는 사용자 유형입니다.");
            return;
        }

        String code = UUID.randomUUID().toString();
        codeStore.save(code, new OAuthCodeInfo(email, role, userId));

        response.sendRedirect(frontendUrl + "/oauth/callback?code=" + code);
    }
}
