package festival.dev.domain.friendship.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class FriendInfoResponse {
    private Long id;
    private String name;
    private String email;
    private String userCode;
}