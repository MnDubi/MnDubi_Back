package festival.dev.domain.TDL.repository;

import festival.dev.domain.TDL.entity.ToDoList;
import festival.dev.domain.user.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Repository
@Transactional
public interface ToDoListRepository extends JpaRepository<ToDoList, Long> {
    boolean existsByUserAndTitleAndFromDate(User user, String title,String fromDate);
    void deleteByUserAndTitleAndFromDate(User user, String title, String fromDate);
    List<ToDoList> findByUser(User user);
    ToDoList findByUserAndTitleAndFromDate(User user, String title, String fromDate);
    List<ToDoList> findByUserAndFromDate(User user, String fromDate);

    @Modifying
    @Query("UPDATE ToDoList t set t.title = :change, t.startDate = :changeDate, t.fromDate = :changeDate  WHERE t.title = :title AND t.user.id = :userID AND t.fromDate = :fromDate")
    void changeTitle(@Param("change") String change, @Param("title") String title, @Param("userID") Long userID, @Param("changeDate") String changeDate, @Param("fromDate") String fromDate);

    @Modifying
    @Query("UPDATE ToDoList t set t.completed = :completed WHERE t.title = :title AND t.user.id = :userID AND t.fromDate = :fromDate")
    void changeCompleted(@Param("completed") Boolean completed, @Param("title") String title, @Param("userID") Long userID,@Param("fromDate") String fromDate);
}
