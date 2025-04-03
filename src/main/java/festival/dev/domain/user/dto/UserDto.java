package festival.dev.domain.user.dto;

import festival.dev.domain.user.entity.User;
import lombok.Getter;

@Getter
public class UserDto {
    private Long id;
    private String email;
    private String name;
    private String userCode;

    public UserDto(User user) {
        this.id = user.getId();
        this.email = user.getEmail();
        this.name = user.getName();
        this.userCode = user.getUserCode();
    }
}
