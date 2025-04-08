package festival.dev.domain.gorupTDL.repository;

import festival.dev.domain.gorupTDL.entity.Group;
import festival.dev.domain.gorupTDL.entity.GroupList;
import festival.dev.domain.user.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Repository
@Transactional
public interface GroupListRepo extends JpaRepository<GroupList,Long> {
    @Modifying
    @Query("UPDATE GroupList g SET g.accept = true WHERE g.group.id = :group AND g.user.id = :receiver")
    void updateAccept(@Param("group") long group,@Param("receiver") long receiver);

    boolean existsByUserAndGroup(User user, Group group);

    List<GroupList> findByGroupId(Long groupId);

    @Modifying
    void deleteByGroupAndUser(Group group, User receiver);

    Optional<GroupList> findByGroupAndUser(Group group, User receiver);
    Optional<GroupList> findByGroupAndUserAndAccept(Group group, User receiver, boolean accept);
}
