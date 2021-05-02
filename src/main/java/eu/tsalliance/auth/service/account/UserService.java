package eu.tsalliance.auth.service.account;

import eu.tsalliance.auth.exception.invalid.InviteInvalidException;
import eu.tsalliance.auth.model.Invite;
import eu.tsalliance.auth.model.forms.Registration;
import eu.tsalliance.auth.model.user.User;
import eu.tsalliance.auth.repository.UserRepository;
import eu.tsalliance.auth.service.EmailService;
import eu.tsalliance.auth.service.InviteService;
import eu.tsalliance.auth.utils.RandomUtil;
import eu.tsalliance.exception.NotFoundException;
import eu.tsalliance.validator.Validator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
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
     * Get user's database entry. Sensible data is removed
     * @param id User's id
     * @return User Optional
     */
    public Optional<User> findUserById(String id) {
        Optional<User> user = this.userRepository.findById(id);
        return user.map(User::censored);
    }

    public Optional<User> findCurrentUser(Authentication authentication) throws NotFoundException {
        return this.userRepository.findById(Optional.ofNullable((User) authentication.getPrincipal()).orElseThrow(NotFoundException::new).getId());
    }

    /**
     * Get a page of user entries from the database. Sensible data is removed
     * @param pageable Page settings
     * @return User Page
     */
    public Page<User> findUsers(Pageable pageable) {
        Page<User> result = this.userRepository.findAll(pageable);
        return result.map((User::censored));
    }

    /**
     * Create new user. Sensible data is removed from the returned user object
     * @param user User to create
     * @param sendCredentialsMail Should a mail with credentials be sent or default welcome message
     * @return User
     * @throws Exception Exception
     */
    public User createUser(User user, boolean sendCredentialsMail) throws Exception {
        user.setLinkedAccounts(new ArrayList<>());
        user.setDiscriminator(RandomUtil.generateRandomNumber(4));

        User finalUser = user;
        this.validator.text(user.getUsername(), "username", true).minLen(3).maxLen(32).alphaNum().unique(() -> !this.existsByUsernameAndIdNot(finalUser.getUsername(), finalUser.getId())).check();
        this.validator.email(user.getEmail(), "email", true).unique(() -> !this.existsByEmailAndIdNot(finalUser.getEmail(), finalUser.getId())).check();
        this.validator.password(user.getPassword(), "password", true).check();
        this.validator.throwErrors();

        String rawPassword = user.getPassword();
        user.setPassword(this.passwordEncoder.encode(user.getPassword()));

        user = this.userRepository.saveAndFlush(user);
        if(sendCredentialsMail) {
            this.emailService.sendUserCreatedMail(user.getEmail(), user.getUsername(), rawPassword);
        } else {
            this.emailService.sendWelcomeMail(user.getEmail(), user.getUsername());
        }

        return user.censored();
    }

    /**
     * Register a new user
     * @param registration Registration data
     * @return Registration
     */
    public Registration registerUser(Registration registration) throws Exception {
        Optional<Invite> invite = this.inviteService.findById(registration.getInviteCode());
        if(invite.isEmpty() || !this.inviteService.isInviteValidAndDelete(invite.get())){
            throw new InviteInvalidException();
        }

        User user = new User();
        user.setEmail(registration.getEmail());
        user.setPassword(registration.getPassword());
        user.setUsername(registration.getUsername());
        user.setAccessableApps(invite.get().getAccessableApps());

        // The validation is done inside this method
        // Email is also handled by this method
        this.createUser(user, false);

        // Check if invite is valid and can be used by others. Otherwise delete it
        this.inviteService.isInviteValidAndDeleteById(registration.getInviteCode());
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

        if(this.validator.text(updated.getUsername(), "username", false).minLen(3).maxLen(32).alphaNum().unique(() -> !this.existsByUsernameAndIdNot(updated.getUsername(), updated.getId())).check()) {
            if(updated.getUsername() != null) {
                oldUser.setUsername(updated.getUsername());
            }
        }

        if(this.validator.text(updated.getEmail(), "email", false).unique(() -> !this.existsByEmailAndIdNot(updated.getEmail(), updated.getId())).check()) {
            if(updated.getEmail() != null) {
                oldUser.setEmail(updated.getEmail());
            }
        }

        if(this.validator.password(updated.getPassword(), "password", false).check()) {
            if(updated.getPassword() != null) {
                oldUser.setPassword(this.passwordEncoder.encode(updated.getPassword()));
                // TODO: Fire password change event to send email to recover account using generated AccountRecoveryToken
            }
        }

        this.validator.throwErrors();
        return this.userRepository.saveAndFlush(oldUser).censored();
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

    /**
     * Find user by their email or username
     * @param email Email to look for
     * @param username Username to look for
     * @return Optional of type User
     */
    public Optional<User> findUserByEmailOrUsername(String email, String username) {
        return this.userRepository.findUserByEmailOrUsername(email, username);
    }

    /**
     * Save user data
     * @param user User's data
     * @return User
     */
    public User saveUser(User user){
        return this.userRepository.saveAndFlush(user);
    }
}
