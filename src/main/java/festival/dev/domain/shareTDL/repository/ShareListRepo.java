package festival.dev.domain.shareTDL.repository;

import festival.dev.domain.shareTDL.entity.ShareList;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ShareListRepo extends JpaRepository<ShareList,Long> {
}
