package eu.tsalliance.auth.validator.rules;

import java.util.regex.Pattern;

public class EmailRule extends ValidationRule<String, EmailRule> {

    public EmailRule(String fieldname, String subject, boolean required) {
        super(fieldname, subject, required);
    }

    @Override
    public void test() {
        Pattern pattern = Pattern.compile("[a-z0-9!#$%&'*+/=?^_`{|}~-]+(?:.[a-z0-9!#$%&'*+/=?^_`{|}~-]+)*@(?:[a-z0-9](?:[a-z0-9-]*[a-z0-9])?.)+[a-z0-9](?:[a-z0-9-]*[a-z0-9])?");

        if(!this.getSubject().matches(pattern.pattern())) {
            putFailedTest("email", false, true);
        }
    }
}
