package eu.tsalliance.auth.validator.rules;

import eu.tsalliance.auth.exception.ValidationException;

import java.util.HashMap;
import java.util.Map;

public abstract class ValidationRule<T> {

    private final T subject;
    private final String fieldname;
    private final boolean required;
    private boolean throwException = false;

    private final Map<String, Object> failedTests = new HashMap<>();

    public ValidationRule(String fieldname, T subject, boolean required) {
        this.fieldname = fieldname;
        this.subject = subject;
        this.required = required;
    }

    public ValidationRule(String fieldname, T subject, boolean required, boolean throwException) {
        this.fieldname = fieldname;
        this.subject = subject;
        this.required = required;
        this.throwException = throwException;
    }

    /**
     * Execute test
     */
    protected abstract void test();
    public abstract Map<String, Object> getRequirements();

    protected boolean isRequired() {
        return required;
    }

    protected T getSubject() {
        return subject;
    }

    protected boolean shouldThrowException() {
        return throwException;
    }

    public String getFieldname() {
        return fieldname;
    }

    public Map<String, Object> getFailedTests() {
        return failedTests;
    }

    /**
     * Run all required tests on a subject
     * @return True or False
     * @throws ValidationException ValidationException
     */
    public boolean check() throws ValidationException {
        if(this.needsValidation()) {
            this.test();
        } else {
            if(this.isRequired()) {
                putFailedTest("required", false, this.isRequired());

                if (this.shouldThrowException()) throw new ValidationException(this);
                else return false;
            }
        }

        boolean passed = getFailedTests().size() <= 0;

        if (this.shouldThrowException() && !passed) {
            throw new ValidationException(this);
        }

        return passed;
    }

    /**
     * Register a failed test in the registry
     * @param testName Name of the test
     * @param foundValue Value that is present
     * @param expectedValue Value that was expected
     */
    protected void putFailedTest(String testName, Object foundValue, Object expectedValue) {
        Map<String, Object> value = new HashMap<>();
        value.put("expected", expectedValue);
        value.put("value", foundValue);

        this.failedTests.put(testName, value);
    }

    /**
     * Check if a value exists and therefor if validation is required or not
     * @return True or False
     */
    protected boolean needsValidation() {
        boolean needsValidation;

        // Check if subject is null, return false
        if(this.getSubject() == null) {
            return false;
        } else {
            if(this.getSubject() instanceof String) {
                String subj = (String) this.getSubject();
                needsValidation = !(subj.isBlank() || !subj.isEmpty());
            } else {
                needsValidation = true;
            }
        }

        // Check if rule needs the field to be not optionally required
        return this.isRequired() && !needsValidation;
    }
}
