package festival.dev.domain.TDL.repository;

import festival.dev.domain.TDL.entity.ToDoList;
import festival.dev.domain.calendar.entity.Calendar_tdl_ids;
import festival.dev.domain.user.entity.User;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collection;
import java.util.List;

@Repository
@Transactional
public interface ToDoListRepository extends JpaRepository<ToDoList, Long> {
    boolean existsByUserAndTitleAndEndDate(User user, String title,String fromDate);
    void deleteByUserAndTitleAndEndDate(User user, String title, String fromDate);
    ToDoList findByUserAndTitleAndEndDate(User user, String title, String fromDate);
    List<ToDoList> findByUserAndEndDate(User user, String endDate);
    List<ToDoList> findByUserAndEndDateAndCompleted(User user, String endDate, boolean completed);

    @Query("SELECT t FROM ToDoList t WHERE t.startDate <= :currentDate AND t.endDate >= :currentDate AND t.user.id = :userID")
    List<ToDoList> findByCurrentDateAndUserID(@Param("currentDate") String currentDate, @Param("userID") Long userID);

    @Query("SELECT t FROM ToDoList t WHERE t.startDate <= :currentDate AND t.endDate >= :currentDate AND t.user.id = :userID And t.shared = true")
    List<ToDoList> findByCurrentDateAndUserIDAndSharedIsTrue(@Param("currentDate") String currentDate, @Param("userID") Long userID);

    @Modifying
    @Query("UPDATE ToDoList t set t.title = :change, t.startDate = :changeDate, t.endDate = :changeDate  WHERE t.title = :title AND t.user.id = :userID AND t.endDate = :fromDate")
    void changeTitle(@Param("change") String change, @Param("title") String title, @Param("userID") Long userID, @Param("changeDate") String changeDate, @Param("fromDate") String fromDate);

    @Modifying
    @Query("UPDATE ToDoList t set t.completed = :completed WHERE t.title = :title AND t.user.id = :userID AND t.endDate = :fromDate")
    void changeCompleted(@Param("completed") Boolean completed, @Param("title") String title, @Param("userID") Long userID,@Param("fromDate") String fromDate);

    @EntityGraph(attributePaths = {"category"})
    List<ToDoList> findByIdIn(List<Long> ids);
}
