package eu.tsalliance.auth.service.account;

import eu.tsalliance.auth.model.user.Profile;
import eu.tsalliance.auth.model.user.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class ProfileService {

    @Autowired
    private UserService userService;

    /**
     * Get user's database entry. Only public information is exposed
     * @param id User's id
     * @return Optional of type Profile
     */
    public Optional<Profile> findProfileById(String id) {
        Optional<User> user = this.userService.findUserById(id);
        if(user.isEmpty()) {
            return Optional.empty();
        }

        return Optional.of(user.get().getProfile());
    }

    /**
     * Get user's database entries. Only public information is exposed
     * @param pageable Paging settings
     * @return Page of type Profile
     */
    public Page<Profile> listProfiles(Pageable pageable) {
        Page<User> users = this.userService.findUsers(pageable);
        return users.map(User::getProfile);
    }

}
