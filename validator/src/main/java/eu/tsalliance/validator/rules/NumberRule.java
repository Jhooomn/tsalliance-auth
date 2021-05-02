package eu.tsalliance.validator.rules;

public class NumberRule extends ValidationRule<Integer, NumberRule> {

    private int max = -1;
    private int min = -1;

    public NumberRule(String fieldname, Integer subject, boolean required) {
        super(fieldname, subject, required);
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
            int subjectInt = this.getSubject();

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
}
