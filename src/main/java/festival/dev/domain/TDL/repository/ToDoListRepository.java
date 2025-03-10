package festival.dev.domain.TDL.repository;

import festival.dev.domain.TDL.entity.ToDoList;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ToDoListRepository extends JpaRepository<ToDoList, Long> {

}
