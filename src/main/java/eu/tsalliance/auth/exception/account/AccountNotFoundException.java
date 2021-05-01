package eu.tsalliance.auth.exception.account;

import org.springframework.security.core.AuthenticationException;

public class AccountNotFoundException extends AuthenticationException {

    public AccountNotFoundException() {
        super("Could not find account to authenticate the request with.");
    }

}
