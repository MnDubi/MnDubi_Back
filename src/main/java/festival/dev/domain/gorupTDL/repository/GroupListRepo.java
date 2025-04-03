package festival.dev.domain.gorupTDL.repository;

import festival.dev.domain.gorupTDL.entity.GroupList;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface GroupListRepo extends JpaRepository<GroupList,Long> {
}
