package eu.tsalliance.auth.validator.rules;

import java.util.regex.Pattern;

public class UrlRule extends ValidationRule<String, UrlRule> {

    public UrlRule(String fieldname, String subject, boolean required) {
        super(fieldname, subject, required);
    }

    @Override
    public void test() {
        Pattern pattern = Pattern.compile("https?://(www\\.)?[-a-zA-Z0-9@:%._+~#=]{1,256}\\.[a-zA-Z0-9()]{1,6}\\b([-a-zA-Z0-9()@:%_+.~#?&/=]*)");

        if(!this.getSubject().matches(pattern.pattern())) {
            putFailedTest("url", false, true);
        }
    }
}
