package eu.tsalliance.auth.exception.account;

import org.springframework.security.core.AuthenticationException;

public class InvalidSessionException extends AuthenticationException {
    public InvalidSessionException() {
        super("Your session is invalid.");
    }
}
