package festival.dev.domain.calendar.repository;

import festival.dev.domain.calendar.entity.Calendar;
import festival.dev.domain.calendar.presentation.dto.Response.MonthResponse;
import festival.dev.domain.user.entity.User;
import jakarta.persistence.Tuple;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Transactional
@Repository
public interface CalendarRepository extends JpaRepository<Calendar, Long> {
    Calendar findByYearMonthDayAndUser(String formattedDate, User user);
    Calendar findByUserAndYearMonthDay(User user,String yearMonthDay);
    @Modifying
    @Query("UPDATE Calendar c SET c.every = :every, c.part = :part , c.toDoListId = :tdlID WHERE c.user.id = :userID AND c.yearMonthDay = :yearMonthDay")
    void updateEveryAndPart(@Param("every") int every, @Param("part") int part, @Param("userID") Long userID, @Param("yearMonthDay") String yearMonthDay, @Param("tdlID") List<Long> tdlID);

    @Query("SELECT SUM(c.every) as monthEvery, SUM(c.part) as monthPart FROM Calendar c WHERE c.user.id = :userID AND SUBSTRING(c.formattedDate, 1, 2) = :month")
    List<Tuple> findByMonth(@Param("month") String month, @Param("userID") Long userID);
}