package festival.dev.global.security.oauth.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class OAuthCodeInfo {
    private String email;
    private String role;
    private Long userId;
}
