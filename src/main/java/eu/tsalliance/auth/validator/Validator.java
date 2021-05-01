package eu.tsalliance.auth.validator;

import eu.tsalliance.auth.validator.rules.*;
import org.springframework.stereotype.Component;
import org.springframework.web.context.annotation.RequestScope;

import java.util.Date;

@Component
@RequestScope
public class Validator {

    public TextRule validateTextAndThrow(String subject, String fieldname, boolean required) {
        return new TextRule(fieldname, subject, required, true);
    }

    public UrlRule validateUrlAndThrow(String subject, String fieldname, boolean required) {
        return new UrlRule(fieldname, subject, required, true);
    }

    public EmailRule validateEmailAndThrow(String subject, String fieldname, boolean required) {
        return new EmailRule(fieldname, subject, required, true);
    }

    public NumberRule validateNumberAndThrow(Integer subject, String fieldname, boolean required) {
        return new NumberRule(fieldname, subject, required, true);
    }

    public DateRule validateDateAndThrow(Date subject, String fieldname, boolean required) {
        return new DateRule(fieldname, subject, required, true);
    }

    public PasswordRule validatePasswordAndThrow(String subject, String fieldname, boolean required) {
        return new PasswordRule(fieldname, subject, required, true);
    }

    public PasswordRule validatePassword(String subject, String fieldname, boolean required) {
        return new PasswordRule(fieldname, subject, required, false);
    }

    public TextRule validateText(String subject, String fieldname, boolean required) {
        return new TextRule(fieldname, subject, required, false);
    }

    public boolean isNotEmptyString(String subject) {
        return subject != null && !subject.isEmpty() && !subject.isBlank();
    }

}
