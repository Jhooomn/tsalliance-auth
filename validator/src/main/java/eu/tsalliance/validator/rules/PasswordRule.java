package eu.tsalliance.validator.rules;

import java.util.regex.Pattern;

public class PasswordRule extends ValidationRule<String, PasswordRule> {

    private final int MIN_LEN = 6;
    private final int MAX_LEN = 255;

    public PasswordRule(String fieldname, String subject, boolean required) {
        super(fieldname, subject, required);
    }

    @Override
    protected void test() {

        Pattern pattern = Pattern.compile("^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=.*[@#$%^&+=])(?=\\S+$).{" + MIN_LEN + "," + MAX_LEN + "}$");

        if(!this.getSubject().matches(pattern.toString())) {
            putFailedTest("password", true, false);
        }
    }
}
