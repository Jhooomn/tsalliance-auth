package eu.tsalliance.auth.validator;

import eu.tsalliance.auth.validator.rules.NumberRule;
import eu.tsalliance.auth.validator.rules.TextRule;
import org.springframework.stereotype.Component;
import org.springframework.web.context.annotation.RequestScope;

@Component
@RequestScope
public class Validator {

    public TextRule validateTextAndThrow(String subject, String fieldname, boolean required) {
        return new TextRule(fieldname, subject, required, true);
    }

    public NumberRule validateNumberAndThrow(String subject, String fieldname, boolean required) {
        return new NumberRule(fieldname, subject, required, true);
    }

    public TextRule validateText(String subject, String fieldname, boolean required) {
        return new TextRule(fieldname, subject, required, false);
    }

    public boolean isNotEmptyString(String subject) {
        return subject != null && !subject.isEmpty() && !subject.isBlank();
    }

}
