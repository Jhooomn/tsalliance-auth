package eu.tsalliance.exception;

import org.springframework.http.HttpStatus;

public class BadOperationException extends ApiException {

    public BadOperationException() {
        super("Bad operation", HttpStatus.BAD_REQUEST);
    }

    @Override
    protected String getErrorCode() {
        return "BAD_OPERATION";
    }
}
