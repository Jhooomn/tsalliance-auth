package eu.tsalliance.auth.exception.account;

import eu.tsalliance.exception.ApiException;
import org.springframework.http.HttpStatus;

public class AccessDeniedException extends ApiException {

    public AccessDeniedException() {
        super("Access denied.", HttpStatus.FORBIDDEN);
    }

    @Override
    protected String getErrorCode() {
        return "ACCESS_DENIED";
    }
}
