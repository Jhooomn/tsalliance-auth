package eu.tsalliance.auth.repository;

import eu.tsalliance.auth.model.user.Profile;
import eu.tsalliance.auth.model.user.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRepository extends JpaRepository<User, String> {

    @Query("select new eu.tsalliance.auth.model.user.Profile(u.id, u.username, u.etag, u.createdAt) from User u where u.id = ?1")
    Profile findProfileById(String id);

}
