package festival.dev.domain.user.repository;

import festival.dev.domain.user.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByEmail(String email);
    boolean existsByUserCode(String userCode);
    Optional<User> findByUserCode(String userCode);
    List<User> findByName(String name);

}
