package eu.tsalliance.auth.handler;

import eu.tsalliance.apiutils.exception.ApiException;
import eu.tsalliance.auth.exception.account.AccountNotFoundException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(value = ApiException.class)
    protected ResponseEntity<Object> handleApiException(ApiException exception) {
        return ResponseEntity.status(exception.getHttpStatus()).body(exception.getResponse());
    }

    @ExceptionHandler(value = AccountNotFoundException.class)
    protected ResponseEntity<Object> handleApiException(AccountNotFoundException exception) {
        // TODO: Better print error
        return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
    }

    // TODO: Catch SizeLimitExceededException

}
