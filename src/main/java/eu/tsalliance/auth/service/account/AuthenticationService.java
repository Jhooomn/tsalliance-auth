package eu.tsalliance.auth.service.account;

import eu.tsalliance.auth.exception.ValidationException;
import eu.tsalliance.auth.exception.account.AccountNotFoundException;
import eu.tsalliance.auth.exception.account.InvalidCredentialsException;
import eu.tsalliance.auth.exception.account.InvalidSessionException;
import eu.tsalliance.auth.exception.NotFoundException;
import eu.tsalliance.auth.exception.account.SessionExpiredException;
import eu.tsalliance.auth.exception.invalid.RecoveryInvalidException;
import eu.tsalliance.auth.model.forms.PasswordRecovery;
import eu.tsalliance.auth.model.response.JwtTokenResponse;
import eu.tsalliance.auth.model.forms.Credentials;
import eu.tsalliance.auth.model.user.RecoveryToken;
import eu.tsalliance.auth.model.user.User;
import eu.tsalliance.auth.service.EmailService;
import eu.tsalliance.auth.utils.CryptUtil;
import eu.tsalliance.auth.validator.Validator;
import io.jsonwebtoken.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.Optional;

@Service
public class AuthenticationService {

    @Autowired
    private EmailService emailService;

    @Autowired
    private UserService userService;

    @Autowired
    private RecoveryService recoveryService;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private Validator validator;

    /**
     * Sign in user using their credentials
     * @param credentials Credentials object
     * @return JwtTokenResponse
     * @throws Exception Exception
     */
    public JwtTokenResponse signin(Credentials credentials) throws Exception {

        this.validator.text(credentials.getIdentifier(), "identifier", true).check();
        this.validator.text(credentials.getPassword(), "password", true).check();
        this.validator.throwErrors();

        Optional<User> user = this.userService.findUserByEmailOrUsername(credentials.getIdentifier(), credentials.getIdentifier());

        if(user.isEmpty()) {
            throw new NotFoundException();
        }

        if(!passwordEncoder.matches(credentials.getPassword(), user.get().getPassword())) {
            throw new InvalidCredentialsException();
        }

        Date createdAt = new Date();

        String token = Jwts.builder()
                .claim("id", user.get().getId())
                .claim("etag", user.get().getEtag())
                .setIssuedAt(createdAt)
                .signWith(CryptUtil.getJwtSecretKey())
                .compact();

        return new JwtTokenResponse(user.get().getId(), token, null, createdAt);
    }

    /**
     * Find user by parsing jwt and search for user with id
     * @param jwt Jwt token
     * @return Optional of type User
     */
    public Optional<User> findUserDetailsByJwt(String jwt) {
        if(jwt == null) return Optional.empty();

        try {
            Claims claims = Jwts.parserBuilder().setSigningKey(CryptUtil.getJwtSecretKey()).build().parseClaimsJws(jwt).getBody();

            String userId = String.valueOf(claims.get("id"));
            String etag = String.valueOf(claims.get("etag"));

            Optional<User> user = this.userService.findUserById(userId);
            if(user.isEmpty()) {
                throw new AccountNotFoundException();
            }

            if(!user.get().getEtag().equals(etag)) {
                throw new SessionExpiredException();
            }

            return user;
        } catch (JwtException exception) {
            throw new InvalidSessionException();
        }
    }

    /**
     * Request account recovery for email. Only identifier field of Credentials is required.
     * @param credentials Credentials of user
     */
    public void requestAccountRecovery(Credentials credentials) throws ValidationException {

        this.validator.text(credentials.getIdentifier(), "identifier", true).check();
        this.validator.throwErrors();

        Optional<User> user = this.userService.findUserByEmailOrUsername(credentials.getIdentifier(), credentials.getIdentifier());

        if(user.isPresent()) {
            RecoveryToken token = this.recoveryService.createRecoveryForUser(user.get());
            user.get().setRecoveryToken(token);
            this.userService.saveUser(user.get());

            this.emailService.sendRecoveryMail(user.get().getEmail(), user.get().getUsername(), token.getId());
        }

    }

    /**
     * Request account recovery for email. Only identifier field of Credentials is required.
     * @param passwordRecovery Recovery data
     */
    public void recoverAccount(PasswordRecovery passwordRecovery) throws Exception {

        this.validator.text(passwordRecovery.getIdentifier(), "identifier", true).check();
        this.validator.password(passwordRecovery.getNewPassword(), "newPassword", true).check();
        this.validator.throwErrors();

        Optional<User> user = this.userService.findUserByEmailOrUsername(passwordRecovery.getIdentifier(), passwordRecovery.getIdentifier());

        if(user.isPresent()) {
            Optional<RecoveryToken> token = this.recoveryService.findTokenById(passwordRecovery.getRecoveryToken());

            if(token.isEmpty() || !token.get().getUser().getId().equals(user.get().getId())) {
                throw new RecoveryInvalidException();
            }

            user.get().setPassword(this.passwordEncoder.encode(passwordRecovery.getNewPassword()));
            this.userService.saveUser(user.get());
            this.recoveryService.deleteRecoveryById(passwordRecovery.getRecoveryToken());
        } else {
            throw new AccountNotFoundException();
        }
    }

}
