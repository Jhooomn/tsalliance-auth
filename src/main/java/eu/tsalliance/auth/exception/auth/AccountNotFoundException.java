package eu.tsalliance.auth.exception.auth;

import org.springframework.security.core.AuthenticationException;

public class AccountNotFoundException extends AuthenticationException {

    public AccountNotFoundException() {
        super("Could not find account to authenticate the request with.");
    }

}
