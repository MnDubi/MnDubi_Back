package festival.dev.domain.TDL.repository;

import festival.dev.domain.TDL.entity.ToDoList;
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
    boolean existsByUserIDAndTitle(String userId, String title);
    void deleteByUserIDAndTitle(String userId, String title);
    List<ToDoList> findByUserID(String userID);

    @Modifying
    @Query("UPDATE ToDoList t set t.title = :change WHERE t.title = :title AND t.userID = :userID")
    void changeTitle(@Param("change") String change, @Param("title") String title, @Param("userID") String userID);
}
