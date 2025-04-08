package festival.dev.domain.shareTDL.repository;

import festival.dev.domain.shareTDL.entity.Share;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ShareRepository extends JpaRepository<Share, Long> {
}
