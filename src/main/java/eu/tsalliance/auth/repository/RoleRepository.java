package eu.tsalliance.auth.repository;

import eu.tsalliance.auth.model.user.Role;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface RoleRepository extends JpaRepository<Role, String> {

    boolean existsByName(String name);
    Optional<Role> findByName(String name);

}
