package eu.tsalliance.auth.service.account;

import eu.tsalliance.auth.model.user.RecoveryToken;
import eu.tsalliance.auth.model.user.User;
import eu.tsalliance.auth.repository.RecoveryRespository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class RecoveryService {

    @Autowired
    private RecoveryRespository recoveryRespository;

    /**
     * Create recovery token for an user
     * @param user User
     * @return RecoveryToken
     */
    public RecoveryToken createRecoveryForUser(User user) {
        RecoveryToken token = new RecoveryToken();
        token.setUser(user);

        return this.recoveryRespository.saveAndFlush(token);
    }

    /**
     * Delete recovery token by id
     * @param id Token's id
     */
    public void deleteRecoveryById(String id) {
        this.recoveryRespository.deleteById(id);
    }

}
