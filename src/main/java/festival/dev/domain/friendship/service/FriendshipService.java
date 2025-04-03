package festival.dev.domain.friendship.service;

import festival.dev.domain.friendship.dto.FriendInfoResponse;
import festival.dev.domain.friendship.entity.Friendship;
import festival.dev.domain.friendship.repository.FriendshipRepository;
import festival.dev.domain.user.entity.User;
import festival.dev.domain.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class FriendshipService {
    private final UserRepository userRepository;
    private final FriendshipRepository friendshipRepository;

    public boolean isAlreadyFriend(User currentUser, String userCode) {
        User friend = userRepository.findByUserCode(userCode)
                .orElseThrow(() -> new IllegalArgumentException("해당 userCode를 가진 사용자가 없습니다."));
        return friendshipRepository.existsByRequesterAndAddressee(currentUser, friend);
    }

    public void deleteFriend(User currentUser, String userCode) {
        User friend = userRepository.findByUserCode(userCode)
                .orElseThrow(() -> new IllegalArgumentException("해당 userCode를 가진 사용자가 없습니다."));

        Friendship friendship = friendshipRepository
                .findByRequesterAndAddressee(currentUser, friend)
                .orElseThrow(() -> new IllegalArgumentException("친구 관계가 존재하지 않습니다."));

        friendshipRepository.delete(friendship);
    }

    public List<FriendInfoResponse> getMyFriends(User currentUser) {
        List<Friendship> friendships = friendshipRepository.findAllByRequester(currentUser);

        return friendships.stream()
                .map(f -> {
                    User friend = f.getAddressee();
                    return new FriendInfoResponse(friend.getId(), friend.getName(), friend.getEmail(), friend.getUserCode());
                })
                .collect(Collectors.toList());
    }
}

