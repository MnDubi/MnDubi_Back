package festival.dev.domain.shareTDL.repository;

import festival.dev.domain.shareTDL.entity.ShareNumber;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ShareNumberRepo extends JpaRepository<ShareNumber, Long> {
}
