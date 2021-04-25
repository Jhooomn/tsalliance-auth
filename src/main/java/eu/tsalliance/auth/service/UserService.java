package eu.tsalliance.auth.service;

import eu.tsalliance.auth.exception.ValidationException;
import eu.tsalliance.auth.model.user.Profile;
import eu.tsalliance.auth.model.user.User;
import eu.tsalliance.auth.repository.UserRepository;
import eu.tsalliance.auth.validator.Validator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private Validator validator;

    @Autowired
    private PasswordEncoder passwordEncoder;

    public Profile findProfileById(String id) {
        return this.userRepository.findProfileById(id);
    }
    public User findUserById(String id) {
        return this.userRepository.getOne(id);
    }
    public Page<User> findUsers(Pageable pageable) {
        return this.userRepository.findAll(pageable);
    }

    public User createUser(User user) throws ValidationException {

        validator.validateTextAndThrow(user.getUsername(), "username", true).minLen(3).maxLen(32).alphaNum().check();
        validator.validateEmailAndThrow(user.getEmail(), "email", true).check();
        validator.validatePasswordAndThrow(user.getPassword(), "password", true).check();

        user.setPassword(this.passwordEncoder.encode(user.getPassword()));

        return this.userRepository.saveAndFlush(user);
    }

    public User updateUser(String id, User updated) throws ValidationException {
        User oldUser = this.findUserById(id);

        if(oldUser == null) {
            // TODO: Throw NotFoundException
            return null;
        }

        if(this.validator.validateTextAndThrow(updated.getUsername(), "username", false).minLen(3).maxLen(32).alphaNum().check()) {
            if(updated.getUsername() != null) oldUser.setUsername(updated.getUsername());
        }

        if(this.validator.validateEmailAndThrow(updated.getEmail(), "email", false).check()) {
            if(updated.getEmail() != null) oldUser.setEmail(updated.getEmail());
        }

        if(this.validator.validatePasswordAndThrow(updated.getPassword(), "password", false).check()) {
            if(updated.getPassword() != null) {
                oldUser.setPassword(this.passwordEncoder.encode(updated.getPassword()));
                // TODO: Fire password change event to send email to recover account using generated AccountRecoveryToken
            }
        }

        return this.userRepository.saveAndFlush(oldUser);
    }

    public void deleteUser(String id) {
        this.userRepository.deleteById(id);
    }

}
