package festival.dev.domain.friendship.repository;

import festival.dev.domain.friendship.entity.Friendship;
import festival.dev.domain.user.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
public interface FriendshipRepository extends JpaRepository<Friendship, Long> {
    boolean existsByRequesterAndAddressee(User requester, User addressee);
    List<Friendship> findAllByRequester(User requester);
}