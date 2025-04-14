package festival.dev.domain.gorupTDL.repository;

import festival.dev.domain.gorupTDL.entity.GroupList;
import festival.dev.domain.gorupTDL.entity.GroupNumber;
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
    @Query("UPDATE GroupList g SET g.accept = true WHERE g.groupNumber.id = :group AND g.user.id = :receiver")
    void updateAccept(@Param("group") long group,@Param("receiver") long receiver);

    boolean existsByUserAndGroupNumber(User user, GroupNumber groupNumber);

    @Modifying
    void deleteByGroupNumberAndUser(GroupNumber groupNumber, User receiver);

    List<GroupList> findByGroupNumberAndAccept(GroupNumber groupNumber, boolean accept);
    Optional<GroupList> findByUserAndAccept(User user, boolean accept);
    Optional<GroupList> findByGroupNumberAndUser(GroupNumber groupNumber, User receiver);
    Optional<GroupList> findByGroupNumberAndUserAndAccept(GroupNumber groupNumber, User receiver, boolean accept);
}
