package festival.dev.domain.calendar.repository;

import festival.dev.domain.calendar.entity.Calendar;
import festival.dev.domain.calendar.entity.CTdlKind;
import jakarta.persistence.Tuple;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Transactional
@Repository
public interface CalendarRepository extends JpaRepository<Calendar, Long> {
    @Query("select c FROM Calendar c LEFT JOIN FETCH c.toDoListId t WHERE c.user.id = :userID AND c.yearMonthDay = :date AND t.kind = :kind")
    Optional<Calendar> findWithTDLIDsByUserDateKind(@Param("userID") Long userID,@Param("date") String date, @Param("kind") CTdlKind kind);

    @Query("SELECT AVG(c.every) as monthEvery, AVG(c.part) as monthPart FROM Calendar c JOIN c.toDoListId t WHERE c.user.id = :userID AND SUBSTRING(c.formattedDate, 1, 2) = :month AND t.kind = :kind")
    List<Tuple> findByMonth(@Param("month") String month, @Param("userID") Long userID, @Param("kind") CTdlKind kind);
}