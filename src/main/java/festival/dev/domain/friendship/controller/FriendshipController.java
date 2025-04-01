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
    private final UserRepository userRepository;



    @PostMapping
    public ResponseEntity<?> addFriend(@AuthenticationPrincipal User currentUser,
                                       @RequestBody AddFriendRequest request) {
        friendshipService.addFriend(currentUser, request.getUserCode());
        return ResponseEntity.ok("친구 추가 완료");
    }


    //
    @GetMapping
    public ResponseEntity<List<FriendInfoResponse>> getFriends(@AuthenticationPrincipal User currentUser) {
        List<FriendInfoResponse> friends = friendshipService.getMyFriends(currentUser);
        return ResponseEntity.ok(friends);
    }
}
