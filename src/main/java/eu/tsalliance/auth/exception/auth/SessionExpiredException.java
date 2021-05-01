package eu.tsalliance.auth.exception.auth;

import org.springframework.security.core.AuthenticationException;

public class SessionExpiredException extends AuthenticationException {

    public SessionExpiredException() {
        super("Your session expired.");
    }

}
