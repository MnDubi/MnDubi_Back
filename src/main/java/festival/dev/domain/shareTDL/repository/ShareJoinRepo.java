package festival.dev.domain.shareTDL.repository;

import festival.dev.domain.shareTDL.entity.ShareJoin;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ShareJoinRepo extends JpaRepository<ShareJoin, Long> {
}
