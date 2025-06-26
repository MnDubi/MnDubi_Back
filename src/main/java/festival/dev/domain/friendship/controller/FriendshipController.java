package festival.dev.domain.friendship.controller;

import festival.dev.domain.friendship.dto.FriendInfoResponse;
import festival.dev.domain.friendship.dto.FriendRequestActionDto;
import festival.dev.domain.friendship.dto.FriendRequestDto;
import festival.dev.domain.friendship.dto.FriendRequestResponse;
import festival.dev.domain.friendship.service.FriendRequestService;
import festival.dev.domain.friendship.service.FriendshipService;
import festival.dev.domain.user.entity.User;
import festival.dev.domain.user.repository.UserRepository;
import festival.dev.domain.user.entity.CustomUserDetails;
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
    private final FriendRequestService friendRequestService;
    private final UserRepository userRepository;


    //  친구 목록 조회 (수락된 친구들)
    // 친구 목록 조회
    @GetMapping
    public ResponseEntity<List<FriendInfoResponse>> getFriends(@AuthenticationPrincipal CustomUserDetails user) {
        return ResponseEntity.ok(friendshipService.getMyFriends(user.getUser()));
    }

    // 친구 여부 확인
    @GetMapping("/check/{userCode}")
    public ResponseEntity<Boolean> checkFriend(@AuthenticationPrincipal CustomUserDetails user,
                                               @PathVariable String userCode) {
        return ResponseEntity.ok(friendshipService.isAlreadyFriend(user.getUser(), userCode));
    }

    // 친구 삭제
    @DeleteMapping("/{userCode}")
    public ResponseEntity<String> deleteFriend(@AuthenticationPrincipal CustomUserDetails user,
                                               @PathVariable String userCode) {
        friendshipService.deleteFriend(user.getUser(), userCode);
        return ResponseEntity.ok("친구 삭제 완료");
    }

    // 친구 요청 전송
    @PostMapping("/request")
    public ResponseEntity<String> request(@AuthenticationPrincipal CustomUserDetails user,
                                          @RequestBody FriendRequestDto request) {
        friendRequestService.sendFriendRequest(user.getUser(), request.getUserCode());
        return ResponseEntity.ok("친구 요청 전송 완료");
    }

    // 친구 요청 수락
    @PostMapping("/accept")
    public ResponseEntity<String> accept(@AuthenticationPrincipal CustomUserDetails user,
                                         @RequestBody FriendRequestActionDto dto) {
        friendRequestService.acceptRequest(user.getUser(), dto.getRequestId());
        return ResponseEntity.ok("친구 요청 수락 완료");
    }

    // 친구 요청 거절
    @PostMapping("/reject")
    public ResponseEntity<String> reject(@AuthenticationPrincipal CustomUserDetails user,
                                         @RequestBody FriendRequestActionDto dto) {
        friendRequestService.rejectRequest(user.getUser(), dto.getRequestId());
        return ResponseEntity.ok("친구 요청 거절 완료");
    }

    // 받은 요청 목록
    @GetMapping("/requests")
    public ResponseEntity<List<FriendRequestResponse>> getPending(@AuthenticationPrincipal CustomUserDetails user) {
        return ResponseEntity.ok(friendRequestService.getPendingRequests(user.getUser()));
    }

    // 보낸 요청 여부 확인
    @GetMapping("/requested/{userCode}")
    public ResponseEntity<Boolean> checkRequested(@AuthenticationPrincipal CustomUserDetails user,
                                                  @PathVariable String userCode) {
        return ResponseEntity.ok(friendRequestService.hasPendingRequest(user.getUser(), userCode));
    }

    // 친구 요청 취소
    @DeleteMapping("/cancel")
    public ResponseEntity<String> cancelRequest(@AuthenticationPrincipal CustomUserDetails user,
                                                @RequestBody FriendRequestActionDto dto) {
        friendRequestService.cancelRequestById(user.getUser(), dto.getRequestId());
        return ResponseEntity.ok("친구 요청이 취소되었습니다.");
    }


}