package eu.tsalliance.auth.validator.rules;

import java.util.HashMap;
import java.util.Map;

public class NumberRule extends ValidationRule<String> {

    private int max = -1;
    private int min = -1;

    public NumberRule(String fieldname, String subject, boolean required, boolean throwException) {
        super(fieldname, subject, required, throwException);
    }

    /**
     * Set maximum of subject
     *
     * @param len Maximum
     */
    public NumberRule max(int len) {
        this.max = len;
        return this;
    }

    /**
     * Set minimum of subject
     *
     * @param len Minimum
     */
    public NumberRule min(int len) {
        this.min = len;
        return this;
    }

    @Override
    protected void test() {
        try {
            int subjectInt = Integer.parseInt(this.getSubject());

            if (this.max != -1 && subjectInt > this.max) {
                putFailedTest("max", subjectInt, this.max);
            }

            if (this.min != -1 && subjectInt < this.min) {
                putFailedTest("min", subjectInt, this.min);
            }

        } catch (NumberFormatException exception) {
            putFailedTest("isNumber", this.getSubject(), true);
        }
    }

    @Override
    public Map<String, Object> getRequirements() {
        Map<String, Object> requirements = new HashMap<>();

        if(this.max != -1) requirements.put("max", this.max);
        if(this.min != -1) requirements.put("min", this.min);

        requirements.put("isNumber", true);
        requirements.put("required", this.isRequired());

        return requirements;
    }
}
