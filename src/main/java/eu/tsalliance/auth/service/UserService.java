package eu.tsalliance.auth.service;

import eu.tsalliance.auth.config.AllianceProperties;
import eu.tsalliance.auth.exception.EmailExistsException;
import eu.tsalliance.auth.exception.InviteInvalidException;
import eu.tsalliance.auth.exception.NameExistsException;
import eu.tsalliance.auth.exception.NotFoundException;
import eu.tsalliance.auth.model.mail.UserCreatedMailModel;
import eu.tsalliance.auth.model.mail.WelcomeMailModel;
import eu.tsalliance.auth.model.user.Registration;
import eu.tsalliance.auth.model.user.User;
import eu.tsalliance.auth.repository.UserRepository;
import eu.tsalliance.auth.validator.Validator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private InviteService inviteService;

    @Autowired
    private Validator validator;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private EmailService emailService;

    /**
     * Get user's database entry. Only public information is exposed
     * @param id User's id
     * @return User Optional
     */
    public Optional<User> findProfileById(String id) {
        Optional<User> user = this.findUserById(id);
        user.ifPresent(value -> {
            value.setPassword(null);
            value.setAccessableApps(null);
            value.setEmail(null);
            value.setRecoveryToken(null);
        });

        return user;
    }

    /**
     * Get user's database entry. Sensible data is removed
     * @param id User's id
     * @return User Optional
     */
    public Optional<User> findUserById(String id) {
        Optional<User> user = this.userRepository.findById(id);
        user.ifPresent(value -> {
            value.setPassword(null);
            value.setRecoveryToken(null);
        });
        return user;
    }

    /**
     * Get a page of user entries from the database. Sensible data is removed
     * @param pageable Page settings
     * @return User Page
     */
    public Page<User> findUsers(Pageable pageable) {
        Page<User> result = this.userRepository.findAll(pageable);
        result.get().forEach(user -> {
            user.setPassword(null);
            user.setRecoveryToken(null);
        });
        return result;
    }

    /**
     * Create new user. Sensible data is removed from the returned user object
     * @param user User to create
     * @param sendCredentialsMail Should a mail with credentials be sent or default welcome message
     * @return User
     * @throws Exception Exception
     */
    public User createUser(User user, boolean sendCredentialsMail) throws Exception {

        validator.validateTextAndThrow(user.getUsername(), "username", true).minLen(3).maxLen(32).alphaNum().check();
        validator.validateEmailAndThrow(user.getEmail(), "email", true).check();
        validator.validatePasswordAndThrow(user.getPassword(), "password", true).check();

        if(this.existsByUsernameAndIdNot(user.getUsername(), user.getId())) throw new NameExistsException();
        if(this.existsByEmailAndIdNot(user.getEmail(), user.getId())) throw new EmailExistsException();

        String rawPassword = user.getPassword();
        user.setPassword(this.passwordEncoder.encode(user.getPassword()));

        user = this.userRepository.saveAndFlush(user);
        if(sendCredentialsMail) {
            this.emailService.sendMail(new UserCreatedMailModel(user.getEmail(), user.getUsername(), rawPassword));
            // TODO: Fire off an event to send info mail to user containing the credentials
        } else {
            // TODO: Fire off an event to send welcome mail to user
            this.emailService.sendMail(new WelcomeMailModel(user.getEmail(), user.getUsername()));
        }

        user.setPassword(null);
        user.setRecoveryToken(null);
        return user;
    }

    /**
     * Register a new user
     * @param registration Registration data
     * @return Registration
     */
    public Registration registerUser(Registration registration) throws Exception {
        if(!this.inviteService.isInviteValidAndDeleteById(registration.getInviteCode())) {
            throw new InviteInvalidException();
        }

        User user = new User();
        user.setEmail(registration.getEmail());
        user.setPassword(registration.getPassword());
        user.setUsername(registration.getUsername());

        // The validation is done inside this method
        // Email is also handled by this method
        this.createUser(user, false);
        return registration;
    }

    /**
     * Update an existing user. Sensible data is removed from the returned user object
     * @param id User's id
     * @param updated Updated data
     * @return User
     * @throws Exception Exception
     */
    public User updateUser(String id, User updated) throws Exception {
        Optional<User> optionalUser = this.findUserById(id);

        if(optionalUser.isEmpty()) {
            throw new NotFoundException();
        }

        User oldUser = optionalUser.get();

        if(this.validator.validateTextAndThrow(updated.getUsername(), "username", false).minLen(3).maxLen(32).alphaNum().check()) {
            if(updated.getUsername() != null) {
                if(this.existsByUsernameAndIdNot(updated.getUsername(), oldUser.getId())) throw new NameExistsException();
                oldUser.setUsername(updated.getUsername());
            }
        }

        if(this.validator.validateEmailAndThrow(updated.getEmail(), "email", false).check()) {
            if(updated.getEmail() != null) {
                if(this.existsByEmailAndIdNot(updated.getUsername(), oldUser.getId())) throw new EmailExistsException();
                oldUser.setEmail(updated.getEmail());
            }
        }

        if(this.validator.validatePasswordAndThrow(updated.getPassword(), "password", false).check()) {
            if(updated.getPassword() != null) {
                oldUser.setPassword(this.passwordEncoder.encode(updated.getPassword()));
                // TODO: Fire password change event to send email to recover account using generated AccountRecoveryToken
            }
        }

        oldUser = this.userRepository.saveAndFlush(oldUser);
        oldUser.setPassword(null);
        oldUser.setRecoveryToken(null);
        return oldUser;
    }

    /**
     * Delete an user entry by id
     * @param id User's id
     */
    public void deleteUser(String id) {
        this.userRepository.deleteById(id);
    }

    /**
     * Check if an user with username already exists
     * @param username Username to lookup
     * @param id User's id to check if it is not the same user
     * @return True or False
     */
    public boolean existsByUsernameAndIdNot(String username, String id) {
        return this.userRepository.existsByUsernameAndIdNot(username, id);
    }

    /**
     * Check if an user with email already exists
     * @param email Email to lookup
     * @param id User's id to check if it is not the same user
     * @return True or False
     */
    public boolean existsByEmailAndIdNot(String email, String id) {
        return this.userRepository.existsByEmailAndIdNot(email, id);
    }

}
