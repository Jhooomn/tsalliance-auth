package eu.tsalliance.auth.service;

import eu.tsalliance.auth.exception.ValidationException;
import eu.tsalliance.auth.model.user.Profile;
import eu.tsalliance.auth.model.user.User;
import eu.tsalliance.auth.repository.UserRepository;
import eu.tsalliance.auth.validator.Validator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private Validator validator;

    public Profile findProfileById(String id) {
        return this.userRepository.findProfileById(id);
    }

    public User createUser(User user) throws ValidationException {

        validator.validateTextAndThrow(user.getUsername(), "username", true).minLen(3).maxLen(32).alphaNum().check();
        validator.validateEmailAndThrow(user.getEmail(), "email", true).check();

        return this.userRepository.saveAndFlush(user);
    }

}
