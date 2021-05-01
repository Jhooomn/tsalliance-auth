package eu.tsalliance.auth.service.account;

import eu.tsalliance.auth.model.user.Profile;
import eu.tsalliance.auth.model.user.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class ProfileService {

    @Autowired
    private UserService userService;

    /**
     * Get user's database entry. Only public information is exposed
     * @param id User's id
     * @return User Optional
     */
    public Optional<Profile> findProfileById(String id) {
        Optional<User> user = this.userService.findUserById(id);
        if(user.isEmpty()) {
            return Optional.empty();
        }

        return Optional.of(user.get().getProfile());
    }

}
