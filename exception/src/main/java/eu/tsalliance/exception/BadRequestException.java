package eu.tsalliance.exception;

import org.springframework.http.HttpStatus;

public class BadRequestException extends ApiException {

    public BadRequestException() {
        super("Bad request", HttpStatus.BAD_REQUEST);
    }

    @Override
    protected String getErrorCode() {
        return "BAD_REQUEST";
    }
}
