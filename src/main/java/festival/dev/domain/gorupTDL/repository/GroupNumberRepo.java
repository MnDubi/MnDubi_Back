package festival.dev.domain.gorupTDL.repository;

import festival.dev.domain.gorupTDL.entity.GroupNumber;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
@Transactional
public interface GroupNumberRepo extends JpaRepository<GroupNumber, Long> {
}