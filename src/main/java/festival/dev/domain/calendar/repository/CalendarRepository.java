package festival.dev.domain.calendar.repository;

import festival.dev.domain.calendar.entity.Calendar;
import festival.dev.domain.user.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CalendarRepository extends JpaRepository<Calendar, Long> {
    Calendar findByYearMonthDayAndUser(String formattedDate, User user);
}