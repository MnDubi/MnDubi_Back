package festival.dev.domain.gorupTDL.repository;

import festival.dev.domain.gorupTDL.entity.GroupCalendar;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface GroupCalendarRepo extends JpaRepository<GroupCalendar, Long> {
}
