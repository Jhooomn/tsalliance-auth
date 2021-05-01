package eu.tsalliance.auth.handler;

import eu.tsalliance.auth.exception.ApiException;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(value = ApiException.class)
    protected ResponseEntity<Object> handleApiException(ApiException exception) {
        return ResponseEntity.status(exception.getHttpStatus()).body(exception.getResponse());
    }

}
