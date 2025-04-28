package festival.dev.domain.shareTDL.repository;

import festival.dev.domain.shareTDL.entity.Share;
import festival.dev.domain.user.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ShareRepository extends JpaRepository<Share, Long> {
    boolean existsByUser(User user);
    Optional<Share> findByUser(User user);
    Optional<Share> findByUserAndAcceptedTrue(User user);
    Optional<Share> findByUserAndAcceptedFalse(User user);
}
