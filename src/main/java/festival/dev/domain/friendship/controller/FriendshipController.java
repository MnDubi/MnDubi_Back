package festival.dev.domain.friendship.controller;

import festival.dev.domain.friendship.dto.AddFriendRequest;
import festival.dev.domain.friendship.dto.FriendInfoResponse;
import festival.dev.domain.friendship.service.FriendshipService;
import festival.dev.domain.user.entity.User;
import festival.dev.domain.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/friends")
@RequiredArgsConstructor
public class FriendshipController {

    private final FriendshipService friendshipService;

    @PostMapping
    public ResponseEntity<String> addFriend(@AuthenticationPrincipal User currentUser,
                                            @RequestBody AddFriendRequest request) {
        friendshipService.addFriend(currentUser, request.getUserCode());
        return ResponseEntity.ok("친구 추가 완료");
    }

    @GetMapping
    public ResponseEntity<List<FriendInfoResponse>> getFriends(@AuthenticationPrincipal User currentUser) {
        return ResponseEntity.ok(friendshipService.getMyFriends(currentUser));
    }

    @GetMapping("/check/{userCode}")
    public ResponseEntity<Boolean> checkFriend(@AuthenticationPrincipal User currentUser,
                                               @PathVariable String userCode) {
        return ResponseEntity.ok(friendshipService.isAlreadyFriend(currentUser, userCode));
    }

    @DeleteMapping("/{userCode}")
    public ResponseEntity<String> deleteFriend(@AuthenticationPrincipal User currentUser,
                                               @PathVariable String userCode) {
        friendshipService.deleteFriend(currentUser, userCode);
        return ResponseEntity.ok("친구 삭제 완료");
    }
}
