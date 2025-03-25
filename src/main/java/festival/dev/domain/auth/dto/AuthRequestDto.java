package festival.dev.domain.auth.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class AuthRequestDto {
    private String email;
//    private String name;
    private String password;


}
