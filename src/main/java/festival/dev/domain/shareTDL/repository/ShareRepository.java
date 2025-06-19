package festival.dev.domain.shareTDL.repository;

import festival.dev.domain.shareTDL.entity.Share;
import festival.dev.domain.shareTDL.entity.ShareNumber;
import festival.dev.domain.user.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ShareRepository extends JpaRepository<Share, Long> {
    boolean existsByUser(User user);
    Optional<Share> findByShareNumberAndUserAndAcceptedFalse(ShareNumber shareNumber,User user);
    Share findByShareNumberAndOwnerIsTrue(ShareNumber shareNumber);
    Optional<Share> findByUser(User user);
    Optional<Share> findByUserAndAcceptedTrue(User user);
    List<Share> findByUserAndAcceptedFalse(User user);
    List<Share> findByShareNumberAndAcceptedTrue(ShareNumber shareNumber);
    void deleteAllByShareNumber(ShareNumber shareNumber);
}
