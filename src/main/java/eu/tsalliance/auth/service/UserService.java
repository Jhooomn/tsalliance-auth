package eu.tsalliance.auth.service;

import eu.tsalliance.auth.model.user.Profile;
import eu.tsalliance.auth.model.user.User;
import eu.tsalliance.auth.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;

    public Profile findProfileById(String id) {
        return this.userRepository.findProfileById(id);
    }

    public User createUser(User user) {

        return this.userRepository.saveAndFlush(user);
    }

}
