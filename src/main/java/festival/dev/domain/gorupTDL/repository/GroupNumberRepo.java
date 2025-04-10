package festival.dev.domain.gorupTDL.repository;

import festival.dev.domain.gorupTDL.entity.GroupNumber;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Repository
@Transactional
public interface GroupNumberRepo extends JpaRepository<GroupNumber, Long> {
    @Query("SELECT MAX(g.groupNumber) FROM GroupNumber g")
    Long getMaxGroupNumber();
}