package festival.dev.domain.friendship.dto;

import lombok.Getter;

@Getter
public class AddFriendRequest {
    private String userCode; // 친구로 추가할 대상의 userCode
}