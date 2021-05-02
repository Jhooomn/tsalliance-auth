package eu.tsalliance.exception;

import org.springframework.http.HttpStatus;

public class NotFoundException extends ApiException {
    public NotFoundException() {
        super("Requested resource not found", HttpStatus.NOT_FOUND);
    }

    @Override
    protected String getErrorCode() {
        return "NOT_FOUND";
    }
}
