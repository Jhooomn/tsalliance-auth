package eu.tsalliance.auth.exception.account;

import org.springframework.security.core.AuthenticationException;

public class SessionExpiredException extends AuthenticationException {

    public SessionExpiredException() {
        super("Your session expired.");
    }

}
