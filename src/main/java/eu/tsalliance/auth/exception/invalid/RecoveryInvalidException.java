package eu.tsalliance.auth.exception.invalid;

import eu.tsalliance.apiutils.exception.ApiException;
import org.springframework.http.HttpStatus;

public class RecoveryInvalidException extends ApiException {
    public RecoveryInvalidException() {
        super("Recovery token is invalid", HttpStatus.BAD_REQUEST.value());
    }

    @Override
    protected String getErrorCode() {
        return "INVALID_RECOVERY";
    }
}
