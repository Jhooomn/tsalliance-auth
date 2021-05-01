package eu.tsalliance.auth.repository;

import eu.tsalliance.auth.model.user.RecoveryToken;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface RecoveryRespository extends JpaRepository<RecoveryToken, String> {
}
