package festival.dev.domain.shareTDL.repository;

import festival.dev.domain.shareTDL.entity.ShareJoin;
import festival.dev.domain.shareTDL.entity.ShareNumber;
import festival.dev.domain.user.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ShareJoinRepo extends JpaRepository<ShareJoin, Long> {
    Optional<ShareJoin> findByTitleAndUser(String title,User user);
    boolean existsByTitleAndUser(String title, User user);
    void deleteByTitleAndUser(String title,User user);
}
