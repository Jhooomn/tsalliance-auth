package eu.tsalliance.auth.exception;

import eu.tsalliance.auth.exception.ApiException;
import org.springframework.http.HttpStatus;

public class EmailExistsException extends ApiException {
    public EmailExistsException() {
        super("An entry using the same email already exists.", HttpStatus.BAD_REQUEST);
    }

    @Override
    protected String getErrorCode() {
        return "UNIQUE_VIOLATION_ERROR";
    }
}
