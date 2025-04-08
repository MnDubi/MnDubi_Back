package festival.dev.domain.gorupTDL.repository;

import festival.dev.domain.gorupTDL.entity.Group;
import festival.dev.domain.user.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
@Transactional
public interface GroupRepository extends JpaRepository<Group, Long> {
    boolean existsByUserAndTitleAndEndDate(User sender, String title, String endDate);
    Group findByUserAndTitleAndEndDate(User sender, String title, String endDate);

    @Modifying
    @Query("UPDATE Group g set g.title = :change, g.startDate = :changeDate, g.endDate = :changeDate  WHERE g.title = :title AND g.user.id = :userID AND g.endDate = :fromDate")
    void changeTitle(@Param("change") String change, @Param("title") String title, @Param("userID") Long userID, @Param("changeDate") String changeDate, @Param("fromDate") String fromDate);
}
