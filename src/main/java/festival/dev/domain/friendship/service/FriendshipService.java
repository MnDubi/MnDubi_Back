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
import java.util.stream.Stream;

@Service
@RequiredArgsConstructor
public class FriendshipService {
    private final UserRepository userRepository;
    private final FriendshipRepository friendshipRepository;

    public boolean isAlreadyFriend(User currentUser, String userCode) {
        User target = userRepository.findByUserCode(userCode)
                .orElseThrow(() -> new IllegalArgumentException("해당 userCode를 가진 사용자가 없습니다."));

        return friendshipRepository.existsByRequesterAndAddressee(currentUser, target) ||
                friendshipRepository.existsByRequesterAndAddressee(target, currentUser);
    }


    public void deleteFriend(User currentUser, String userCode) {
        User target = userRepository.findByUserCode(userCode)
                .orElseThrow(() -> new IllegalArgumentException("해당 userCode를 가진 사용자가 없습니다."));

        Friendship friendship = friendshipRepository
                .findByRequesterAndAddressee(currentUser, target)
                .orElseGet(() -> friendshipRepository
                        .findByRequesterAndAddressee(target, currentUser)
                        .orElseThrow(() -> new IllegalArgumentException("친구 관계가 존재하지 않습니다.")));

        friendshipRepository.delete(friendship);
    }

    public List<FriendInfoResponse> getMyFriends(User currentUser) {
        List<Friendship> asRequester = friendshipRepository.findAllByRequester(currentUser);
        List<Friendship> asAddressee = friendshipRepository.findAllByAddressee(currentUser);

        return Stream.concat(asRequester.stream(), asAddressee.stream())
                .map(f -> {
                    User friend = f.getRequester().equals(currentUser)
                            ? f.getAddressee()
                            : f.getRequester();
                    return new FriendInfoResponse(friend.getId(), friend.getName(), friend.getEmail(), friend.getUserCode());
                })
                .collect(Collectors.toList());
    }


}

