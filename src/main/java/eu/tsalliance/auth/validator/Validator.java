package eu.tsalliance.auth.validator;

import eu.tsalliance.auth.validator.rules.EmailRule;
import eu.tsalliance.auth.validator.rules.NumberRule;
import eu.tsalliance.auth.validator.rules.PasswordRule;
import eu.tsalliance.auth.validator.rules.TextRule;
import org.springframework.stereotype.Component;
import org.springframework.web.context.annotation.RequestScope;

@Component
@RequestScope
public class Validator {

    public TextRule validateTextAndThrow(String subject, String fieldname, boolean required) {
        return new TextRule(fieldname, subject, required, true);
    }

    public EmailRule validateEmailAndThrow(String subject, String fieldname, boolean required) {
        return new EmailRule(fieldname, subject, required, true);
    }

    public NumberRule validateNumberAndThrow(String subject, String fieldname, boolean required) {
        return new NumberRule(fieldname, subject, required, true);
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
