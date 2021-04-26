package eu.tsalliance.auth.controller;

import eu.tsalliance.auth.model.response.JwtTokenResponse;
import eu.tsalliance.auth.model.user.Credentials;
import eu.tsalliance.auth.model.user.Registration;
import eu.tsalliance.auth.model.user.User;
import eu.tsalliance.auth.service.AuthenticationService;
import eu.tsalliance.auth.service.UserService;
import eu.tsalliance.auth.utils.RandomUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;

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

}
