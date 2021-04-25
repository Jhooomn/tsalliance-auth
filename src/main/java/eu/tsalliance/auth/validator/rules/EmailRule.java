package eu.tsalliance.auth.validator.rules;

import java.util.HashMap;
import java.util.Map;
import java.util.regex.Pattern;

public class EmailRule extends ValidationRule<String> {

    public EmailRule(String fieldname, String subject, boolean required) {
        super(fieldname, subject, required);
    }
    public EmailRule(String fieldname, String subject, boolean required, boolean throwException) {
        super(fieldname, subject, required, throwException);
    }

    @Override
    public void test() {
        Pattern pattern = Pattern.compile("[a-z0-9!#$%&'*+/=?^_`{|}~-]+(?:.[a-z0-9!#$%&'*+/=?^_`{|}~-]+)*@(?:[a-z0-9](?:[a-z0-9-]*[a-z0-9])?.)+[a-z0-9](?:[a-z0-9-]*[a-z0-9])?");

        if(!this.getSubject().matches(pattern.pattern())) {
            putFailedTest("email", false, true);
        }
    }

    @Override
    public Map<String, Object> getRequirements() {
        Map<String, Object> requirements = new HashMap<>();
        requirements.put("email", true);
        requirements.put("required", this.isRequired());
        return requirements;
    }
}
