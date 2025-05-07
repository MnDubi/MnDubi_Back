package festival.dev.domain.gorupTDL.repository;

import festival.dev.domain.gorupTDL.entity.Group;
import festival.dev.domain.gorupTDL.entity.GroupJoin;
import festival.dev.domain.gorupTDL.entity.GroupNumber;
import festival.dev.domain.user.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Repository
@Transactional
public interface GroupJoinRepo extends JpaRepository<GroupJoin, Long> {
    Optional<GroupJoin> findByGroupAndGroupNumberAndUser(Group group, GroupNumber groupNumber, User user);
    Long countByCompletedAndUserAndGroupNumber(boolean completed, User user, GroupNumber groupNumber);
    Long countByUserAndGroupNumber(User user, GroupNumber groupNumber);
    Long countByCompletedAndGroup(boolean completed, Group group);
    Long countByGroup(Group group);
    List<GroupJoin> findByGroupNumberAndUser(GroupNumber groupNumber , User user);

    @Modifying
    @Query("UPDATE GroupJoin g SET g.completed=false")
    void updateAllFalse();
}
