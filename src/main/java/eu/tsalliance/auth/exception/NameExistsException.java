package eu.tsalliance.auth.exception;

import eu.tsalliance.auth.exception.ApiException;
import org.springframework.http.HttpStatus;

public class NameExistsException extends ApiException {
    public NameExistsException() {
        super("An entry using the same name already exists.", HttpStatus.BAD_REQUEST);
    }

    @Override
    protected String getErrorCode() {
        return "UNIQUE_VIOLATION_ERROR";
    }
}
