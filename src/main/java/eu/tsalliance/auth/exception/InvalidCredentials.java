package eu.tsalliance.auth.exception;

import org.springframework.http.HttpStatus;

public class InvalidCredentials extends ApiException {
    public InvalidCredentials() {
        super("Password and Email or Username do not match.", HttpStatus.BAD_REQUEST);
    }

    @Override
    protected String getErrorCode() {
        return "INVALID_CREDENTIALS";
    }
}
