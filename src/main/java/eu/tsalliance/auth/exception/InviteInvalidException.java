package eu.tsalliance.auth.exception;

import org.springframework.http.HttpStatus;

public class InviteInvalidException extends ApiException {
    public InviteInvalidException() {
        super("Invite is invalid", HttpStatus.BAD_REQUEST);
    }

    @Override
    protected String getErrorCode() {
        return "INVALID_INVITE";
    }
}
