package festival.dev.domain.auth.dto;

import lombok.Getter;
import lombok.Setter;

import java.util.Map;

@Getter
@Setter
public class OAuthUserInfoDto {
    private String email;
    private String name;
    private String password;


}
