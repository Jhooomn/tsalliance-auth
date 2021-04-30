package eu.tsalliance.auth.repository;

import eu.tsalliance.auth.model.Invite;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface InviteRepository extends JpaRepository<Invite, String> {
}
