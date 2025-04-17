package festival.dev.domain.gorupTDL.repository;

import festival.dev.domain.gorupTDL.entity.Group;
import festival.dev.domain.gorupTDL.entity.GroupNumber;
import festival.dev.domain.user.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

@Repository
@Transactional
public interface GroupRepository extends JpaRepository<Group, Long> {
    boolean existsByUserAndTitle(User sender, String title);
    Optional<Group>  findByUserAndTitle(User sender, String title);
    void deleteByUserAndTitle(User user, String title);
    List<Group> findByGroupNumber(GroupNumber groupNumber);

    List<Group> findByIdIn(Collection<Long> ids);

    void deleteAllByGroupNumberAndUser(GroupNumber groupNumber, User user);

    @Modifying
    @Query("UPDATE Group g set g.title = :change WHERE g.title = :title AND g.user.id = :userID")
    void changeTitle(@Param("change") String change, @Param("title") String title, @Param("userID") Long userID);
}
