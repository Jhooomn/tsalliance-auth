package eu.tsalliance.validator.exception;

import eu.tsalliance.exception.ApiException;
import eu.tsalliance.validator.rules.ValidationRule;
import org.springframework.http.HttpStatus;

import java.util.List;

public class ValidationException extends ApiException {

    public ValidationException(ValidationRule rule) {
        super("Value for field '" + rule.getFieldname() + "' does not match the requirements", HttpStatus.BAD_REQUEST);

        this.putDetail("fieldname", rule.getFieldname());
        this.putDetail("failedTests", rule.getFailedTests());
        //this.putDetail("requirements", rule.getRequirements());
    }

    public ValidationException(List<Object> failedTests) {
        super("Failed validating request input", HttpStatus.BAD_REQUEST);
        this.setDetailsAsList(failedTests);
    }

    @Override
    protected String getErrorCode() {
        return "VALIDATION_ERROR";
    }
}
