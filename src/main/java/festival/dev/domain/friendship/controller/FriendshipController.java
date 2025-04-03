package festival.dev.domain.friendship.controller;

import festival.dev.domain.friendship.dto.FriendInfoResponse;
import festival.dev.domain.friendship.dto.FriendRequestActionDto;
import festival.dev.domain.friendship.dto.FriendRequestDto;
import festival.dev.domain.friendship.dto.FriendRequestResponse;
import festival.dev.domain.friendship.service.FriendRequestService;
import festival.dev.domain.friendship.service.FriendshipService;
import festival.dev.domain.user.entity.User;
import festival.dev.domain.user.repository.UserRepository;
import com.auth0.jwt.JWT;
import com.auth0.jwt.interfaces.DecodedJWT;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/friends")
@RequiredArgsConstructor
public class FriendshipController {

    private final FriendshipService friendshipService;
    private final FriendRequestService friendRequestService;
    private final UserRepository userRepository;

    @Value("${jwt.secret}")
    private String secret;

    //  친구 목록 조회 (수락된 친구들)
    @GetMapping
    public ResponseEntity<List<FriendInfoResponse>> getFriends(@RequestHeader("Authorization") String authorization) {
        User currentUser = getUserFromToken(authorization);
        return ResponseEntity.ok(friendshipService.getMyFriends(currentUser));
    }

    // 친구 여부 확인
    @GetMapping("/check/{userCode}")
    public ResponseEntity<Boolean> checkFriend(@RequestHeader("Authorization") String authorization,
                                               @PathVariable String userCode) {
        User currentUser = getUserFromToken(authorization);
        return ResponseEntity.ok(friendshipService.isAlreadyFriend(currentUser, userCode));
    }

    // 친구 삭제
    @DeleteMapping("/{userCode}")
    public ResponseEntity<String> deleteFriend(@RequestHeader("Authorization") String authorization,
                                               @PathVariable String userCode) {
        User currentUser = getUserFromToken(authorization);
        friendshipService.deleteFriend(currentUser, userCode);
        return ResponseEntity.ok("친구 삭제 완료");
    }

    // 친구 요청 전송
    @PostMapping("/request")
    public ResponseEntity<String> request(@RequestHeader("Authorization") String authorization,
                                          @RequestBody FriendRequestDto request) {
        User sender = getUserFromToken(authorization);
        friendRequestService.sendFriendRequest(sender, request.getUserCode());
        return ResponseEntity.ok("친구 요청 전송 완료");
    }

    // 친구 요청 수락
    @PostMapping("/accept")
    public ResponseEntity<String> accept(@RequestHeader("Authorization") String authorization,
                                         @RequestBody FriendRequestActionDto dto) {
        User receiver = getUserFromToken(authorization);
        friendRequestService.acceptRequest(receiver, dto.getRequestId());
        return ResponseEntity.ok("친구 요청 수락 완료");
    }

    //  친구 요청 거절
    @PostMapping("/reject")
    public ResponseEntity<String> reject(@RequestHeader("Authorization") String authorization,
                                         @RequestBody FriendRequestActionDto dto) {
        User receiver = getUserFromToken(authorization);
        friendRequestService.rejectRequest(receiver, dto.getRequestId());
        return ResponseEntity.ok("친구 요청 거절 완료");
    }

    // 내가 받은 요청 목록
    @GetMapping("/requests")
    public ResponseEntity<List<FriendRequestResponse>> getPending(@RequestHeader("Authorization") String authorization) {
        User receiver = getUserFromToken(authorization);
        return ResponseEntity.ok(friendRequestService.getPendingRequests(receiver));
    }


    //  내가 보낸 요청 여부 확인
    @GetMapping("/requested/{userCode}")
    public ResponseEntity<Boolean> checkRequested(@RequestHeader("Authorization") String authorization,
                                                  @PathVariable String userCode) {
        User sender = getUserFromToken(authorization);
        return ResponseEntity.ok(friendRequestService.hasPendingRequest(sender, userCode));
    }

    //  친구 요청 취소
    @DeleteMapping("/cancel")
    public ResponseEntity<String> cancelRequest(@RequestHeader("Authorization") String authorization,
                                                @RequestBody FriendRequestActionDto dto) {
        User sender = getUserFromToken(authorization);
        friendRequestService.cancelRequestById(sender, dto.getRequestId());
        return ResponseEntity.ok("친구 요청이 취소되었습니다.");
    }

    private User getUserFromToken(String authHeader) {
        String token = authHeader.replace("Bearer ", "");
        DecodedJWT jwt = JWT.decode(token);
        Long userId = jwt.getClaim("userId").asLong();
        return userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("유저를 찾을 수 없습니다."));
    }
}