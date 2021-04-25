package eu.tsalliance.auth.validator.rules;

import java.util.HashMap;
import java.util.Map;
import java.util.regex.Pattern;

public class PasswordRule extends ValidationRule<String> {

    private final int MIN_LEN = 6;
    private final int MAX_LEN = 255;

    public PasswordRule(String fieldname, String subject, boolean required, boolean throwException) {
        super(fieldname, subject, required, throwException);
    }

    @Override
    protected void test() {

        Pattern pattern = Pattern.compile("^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=.*[@#$%^&+=])(?=\\S+$).{" + MIN_LEN + "," + MAX_LEN + "}$");

        if(!this.getSubject().matches(pattern.toString())) {
            putFailedTest("password", true, false);
        }
    }

    @Override
    public Map<String, Object> getRequirements() {
        Map<String, Object> requirements = new HashMap<>();

        requirements.put("minLen", MIN_LEN);
        requirements.put("maxLen", MAX_LEN);
        requirements.put("uppercase", true);
        requirements.put("lowercase", true);
        requirements.put("symbol", true);
        requirements.put("containsDigit", true);
        requirements.put("required", this.isRequired());

        return requirements;
    }
}
