package eu.tsalliance.auth.repository;

import eu.tsalliance.auth.model.user.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, String> {
    boolean existsByEmailAndIdNot(String email, String id);
    boolean existsByUsernameAndIdNot(String username, String id);

    Optional<User> findUserByEmailOrUsername(String email, String username);
}
