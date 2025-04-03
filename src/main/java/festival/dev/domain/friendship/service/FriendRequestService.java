package festival.dev.domain.friendship.service;

import festival.dev.domain.friendship.entity.FriendRequest;
import festival.dev.domain.friendship.entity.Friendship;
import festival.dev.domain.friendship.entity.RequestStatus;
import festival.dev.domain.friendship.dto.FriendRequestResponse;
import festival.dev.domain.friendship.repository.FriendRequestRepository;
import festival.dev.domain.friendship.repository.FriendshipRepository;
import festival.dev.domain.user.entity.User;
import festival.dev.domain.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;


@Service
@RequiredArgsConstructor
public class FriendRequestService {

    private final FriendRequestRepository friendRequestRepository;
    private final FriendshipRepository friendshipRepository;
    private final UserRepository userRepository;

    public void sendFriendRequest(User sender, String userCode) {
        User receiver = userRepository.findByUserCode(userCode)
                .orElseThrow(() -> new IllegalArgumentException("userCode에 해당하는 사용자가 없습니다."));

        if (sender.equals(receiver)) throw new IllegalArgumentException("자기 자신에게 요청할 수 없습니다.");

        if (friendRequestRepository.existsBySenderAndReceiver(sender, receiver)) {
            throw new IllegalArgumentException("이미 요청을 보냈습니다.");
        }

        FriendRequest request = FriendRequest.builder()
                .sender(sender)
                .receiver(receiver)
                .status(RequestStatus.PENDING)
                .requestedAt(LocalDateTime.now())
                .build();
        friendRequestRepository.save(request);
    }

    public void acceptRequest(User receiver, Long requestId) {
        FriendRequest request = friendRequestRepository.findById(requestId)
                .orElseThrow(() -> new IllegalArgumentException("요청을 찾을 수 없습니다."));

        if (!request.getReceiver().equals(receiver)) {
            throw new IllegalArgumentException("수락 권한이 없습니다.");
        }

        request.setStatus(RequestStatus.ACCEPTED);
        friendRequestRepository.save(request);

        friendshipRepository.save(Friendship.builder()
                .requester(request.getSender())
                .addressee(receiver)
                .createdAt(LocalDateTime.now())
                .build());
    }

    public void rejectRequest(User receiver, Long requestId) {
        FriendRequest request = friendRequestRepository.findById(requestId)
                .orElseThrow(() -> new IllegalArgumentException("요청을 찾을 수 없습니다."));

        if (!request.getReceiver().equals(receiver)) {
            throw new IllegalArgumentException("거절 권한이 없습니다.");
        }

        request.setStatus(RequestStatus.REJECTED);
        friendRequestRepository.save(request);
    }

    public List<FriendRequestResponse> getPendingRequests(User receiver) {
        return friendRequestRepository.findAllByReceiverAndStatus(receiver, RequestStatus.PENDING)
                .stream()
                .map(req -> new FriendRequestResponse(
                        req.getId(),
                        req.getSender().getName(),
                        req.getSender().getEmail(),
                        req.getSender().getUserCode(),
                        req.getStatus().name(),
                        req.getRequestedAt().toString()
                ))
                .collect(Collectors.toList());
    }

    public boolean hasPendingRequest(User sender, String userCode) {
        User receiver = userRepository.findByUserCode(userCode)
                .orElseThrow(() -> new IllegalArgumentException("userCode에 해당하는 사용자가 없습니다."));
        return friendRequestRepository.existsBySenderAndReceiver(sender, receiver);
    }

    public void cancelRequestById(User sender, Long requestId) {
        FriendRequest request = friendRequestRepository.findById(requestId)
                .orElseThrow(() -> new IllegalArgumentException("요청을 찾을 수 없습니다."));

        if (!request.getSender().equals(sender)) {
            throw new IllegalArgumentException("취소 권한이 없습니다.");
        }

        friendRequestRepository.delete(request);
    }

}
