package festival.dev.domain.friendship.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class FriendRequestResponse {
    private Long requestId;
    private String senderName;
    private String senderEmail;
    private String senderUserCode;
    private String status;
    private String requestedAt;
}
