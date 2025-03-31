package festival.dev.domain.TDL.repository;

import festival.dev.domain.TDL.entity.ToDoList;
import festival.dev.domain.user.entity.User;
import org.springframework.data.jpa.repository.EntityGraph;
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
    @EntityGraph(attributePaths = {"user"}, type = EntityGraph.EntityGraphType.LOAD)
    boolean existsByUserAndTitleAndEndDate(User user, String title,String fromDate);
    @EntityGraph(attributePaths = {"user"}, type = EntityGraph.EntityGraphType.LOAD)
    void deleteByUserAndTitleAndEndDate(User user, String title, String fromDate);
    @EntityGraph(attributePaths = {"user"}, type = EntityGraph.EntityGraphType.LOAD)
    boolean existsByUserAndTitleAndEndDateAndStartDate(User user, String title, String fromDate, String startDate);
    @EntityGraph(attributePaths = {"user"}, type = EntityGraph.EntityGraphType.LOAD)
    ToDoList findByUserAndTitleAndEndDate(User user, String title, String fromDate);
    @EntityGraph(attributePaths = {"user"}, type = EntityGraph.EntityGraphType.LOAD)
    List<ToDoList> findByUserAndEndDate(User user, String endDate);
    @EntityGraph(attributePaths = {"user"}, type = EntityGraph.EntityGraphType.LOAD)
    List<ToDoList> findByUserAndEndDateAndCompleted(User user, String endDate, boolean completed);

    @EntityGraph(attributePaths = {"user"}, type = EntityGraph.EntityGraphType.LOAD)
    @Query("SELECT t FROM ToDoList t WHERE t.startDate <= :currentDate AND t.endDate >= :currentDate AND t.user.id = :userID")
    List<ToDoList> findByCurrentDateAndUserID(@Param("currentDate") String currentDate, @Param("userID") Long userID);

    @Modifying
    @Query("UPDATE ToDoList t set t.title = :change, t.startDate = :changeDate, t.endDate = :changeDate  WHERE t.title = :title AND t.user.id = :userID AND t.endDate = :fromDate")
    void changeTitle(@Param("change") String change, @Param("title") String title, @Param("userID") Long userID, @Param("changeDate") String changeDate, @Param("fromDate") String fromDate);

    @Modifying
    @Query("UPDATE ToDoList t set t.completed = :completed WHERE t.title = :title AND t.user.id = :userID AND t.endDate = :fromDate")
    void changeCompleted(@Param("completed") Boolean completed, @Param("title") String title, @Param("userID") Long userID,@Param("fromDate") String fromDate);
}
