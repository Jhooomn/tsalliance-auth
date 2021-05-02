package eu.tsalliance.validator.rules;

public class TextRule extends ValidationRule<String, TextRule> {

    private int maxLen = -1;
    private int minLen = -1;
    private boolean alpha = false;
    private boolean alphaNum = false;

    public TextRule(String fieldname, String subject, boolean required) {
        super(fieldname, subject, required);
    }

    /**
     * Set maximum length of subject
     *
     * @param len Maximum length
     */
    public TextRule maxLen(int len) {
        this.maxLen = len;
        return this;
    }

    /**
     * Set minimum length of subject
     *
     * @param len Minimum length
     */
    public TextRule minLen(int len) {
        this.minLen = len;
        return this;
    }

    /**
     * Check if subject consists only of letters
     */
    public TextRule alpha() {
        this.alpha = true;
        this.alphaNum = false;
        return this;
    }

    /**
     * Check if subject consists only of letters and digits
     */
    public TextRule alphaNum() {
        this.alpha = false;
        this.alphaNum = true;
        return this;
    }

    @Override
    protected void test() {
            if(this.getSubject().isEmpty() || this.getSubject().isBlank()) {
                if(this.isRequired()) {
                    putFailedTest("required", false, true);
                    return;
                }
            }

            if (this.maxLen != -1 && this.getSubject().length() > this.maxLen) {
                putFailedTest("maxlen", this.getSubject().length(), this.maxLen);
            }

            if (this.minLen != -1 && this.getSubject().length() < this.minLen) {
                putFailedTest("minlen", this.getSubject().length(), this.minLen);
            }

            if (this.alpha && !this.getSubject().matches("^[a-zA-Z]*$")) {
                putFailedTest("alpha", false, true);
            }
            if (this.alphaNum && !this.getSubject().matches("^[a-zA-Z0-9]*$")) {
                putFailedTest("alphaNum", false, true);
            }
    }
}
