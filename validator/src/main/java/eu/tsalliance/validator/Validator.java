package eu.tsalliance.validator;

import eu.tsalliance.validator.exception.ValidationException;
import eu.tsalliance.validator.rules.*;
import org.springframework.stereotype.Component;
import org.springframework.web.context.annotation.RequestScope;

import java.util.*;

@Component
@RequestScope
public class Validator {

    private List<ValidationRule> rules = new ArrayList<>();

    public TextRule text(String subject, String fieldname, boolean required) {
        TextRule rule = new TextRule(fieldname, subject, required);
        this.rules.add(rule);
        return rule;
    }

    public UrlRule url(String subject, String fieldname, boolean required) {
        UrlRule rule = new UrlRule(fieldname, subject, required);
        this.rules.add(rule);
        return rule;
    }

    public EmailRule email(String subject, String fieldname, boolean required) {
        EmailRule rule = new EmailRule(fieldname, subject, required);
        this.rules.add(rule);
        return rule;
    }

    public NumberRule number(Integer subject, String fieldname, boolean required) {
        NumberRule rule = new NumberRule(fieldname, subject, required);
        this.rules.add(rule);
        return rule;
    }

    public DateRule date(Date subject, String fieldname, boolean required) {
        DateRule rule = new DateRule(fieldname, subject, required);
        this.rules.add(rule);
        return rule;
    }

    public PasswordRule password(String subject, String fieldname, boolean required) {
        PasswordRule rule = new PasswordRule(fieldname, subject, required);
        this.rules.add(rule);
        return rule;
    }

    public void throwErrors() throws ValidationException {
        List<Object> errors = new ArrayList<>();

        for(ValidationRule rule : this.rules) {
            if(rule.getFailedTests().size() > 0) {
                Map<String, Object> details = new HashMap<>();
                details.put("fieldname", rule.getFieldname());
                details.put("failedRules", rule.getFailedTests());
                errors.add(details);
            }
        }

        if(!errors.isEmpty()) {
            throw new ValidationException(errors);
        }
    }

}
