package eu.tsalliance.auth.controller;

import eu.tsalliance.apiutils.validator.exception.ValidationException;
import eu.tsalliance.auth.model.forms.PasswordRecovery;
import eu.tsalliance.auth.model.response.JwtTokenResponse;
import eu.tsalliance.auth.model.forms.Credentials;
import eu.tsalliance.auth.model.forms.Registration;
import eu.tsalliance.auth.service.account.AuthenticationService;
import eu.tsalliance.auth.service.account.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping(value = "/api/v1/auth")
public class AuthenticationController {

    @Autowired
    private AuthenticationService authenticationService;

    @Autowired
    private UserService userService;

    @RequestMapping(value = "signin", method = RequestMethod.POST)
    public JwtTokenResponse signin(@RequestBody Credentials credentials) throws Exception {
        return this.authenticationService.signin(credentials);
    }

    @RequestMapping(value = "signup", method = RequestMethod.POST)
    public Registration signup(@RequestBody Registration registration) throws Exception {
        return this.userService.registerUser(registration);
    }

    /**
     * For credentials, only the identifier field is required
     */
    @RequestMapping(value = "recover", method = RequestMethod.POST)
    public ResponseEntity<Object> requestRecovery(@RequestBody Credentials credentials) throws ValidationException {
        this.authenticationService.requestAccountRecovery(credentials);
        return ResponseEntity.ok().build();
    }

    @RequestMapping(value = "recover", method = RequestMethod.PUT)
    public ResponseEntity<Object> requestRecovery(@RequestBody PasswordRecovery passwordRecovery) throws Exception {
        this.authenticationService.recoverAccount(passwordRecovery);
        return ResponseEntity.ok().build();
    }

}
