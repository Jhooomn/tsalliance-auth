package eu.tsalliance.auth.service.account;

import eu.tsalliance.auth.model.user.Profile;
import eu.tsalliance.auth.model.user.User;
import eu.tsalliance.auth.service.ProfileService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@Slf4j
public class ProfileServiceImpl implements ProfileService {

    @Autowired
    private UserService userService;

    /**
     * Get user's database entry. Only public information is exposed
     * @param id User's id
     * @return Optional of type Profile
     */
    @Override
    public Optional<Profile> findProfileById(String id) {
        Optional<User> user = this.userService.findUserById(id);
        if(user.isEmpty()) {
            log.warn("[findProfileById] {} was not found [findProfileById]", id);
            return Optional.empty();
        }

        return Optional.of(user.get().getProfile());
    }

    /**
     * Get user's database entries. Only public information is exposed
     * @param pageable Paging settings
     * @return Page of type Profile
     */
    @Override
    public Page<Profile> listProfiles(Pageable pageable) {
        Page<User> users = this.userService.findUsers(pageable);
        return users.map(User::getProfile);
    }

}
