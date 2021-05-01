package eu.tsalliance.auth.exception.account;

import eu.tsalliance.auth.exception.ApiException;
import org.springframework.http.HttpStatus;

public class InvalidCredentialsException extends ApiException {
    public InvalidCredentialsException() {
        super("Password and Email or Username do not match.", HttpStatus.BAD_REQUEST);
    }

    @Override
    protected String getErrorCode() {
        return "INVALID_CREDENTIALS";
    }
}
