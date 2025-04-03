package festival.dev.domain.friendship.repository;

import festival.dev.domain.friendship.entity.FriendRequest;
import festival.dev.domain.user.entity.User;
import festival.dev.domain.friendship.entity.RequestStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface FriendRequestRepository extends JpaRepository<FriendRequest, Long> {
    Optional<FriendRequest> findBySenderAndReceiver(User sender, User receiver);
    List<FriendRequest> findAllByReceiverAndStatus(User receiver, RequestStatus status);
    List<FriendRequest> findAllBySenderAndStatus(User sender, RequestStatus status);
    boolean existsBySenderAndReceiver(User sender, User receiver);
    void deleteBySenderAndReceiver(User sender, User receiver);
}