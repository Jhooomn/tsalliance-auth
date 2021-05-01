package eu.tsalliance.auth.validator.rules;

import eu.tsalliance.auth.exception.ValidationException;

import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @param <T> Data type of subject
 * @param <D> Data type of the rule (usually it is the rule itself)
 */
public abstract class ValidationRule<T, D> {

    private final T subject;
    private final String fieldname;
    private final boolean required;
    private UniqueValidatorFunction uniqueValidatorFunction;

    private final List<Object> failedTests = new ArrayList<>();

    public ValidationRule(String fieldname, T subject, boolean required) {
        this.fieldname = fieldname;
        this.subject = subject;
        this.required = required;
    }

    /**
     * Execute test
     */
    protected abstract void test();
    protected boolean isRequired() {
        return required;
    }
    protected T getSubject() {
        return subject;
    }

    public String getFieldname() {
        return fieldname;
    }
    public List<Object> getFailedTests() {
        return failedTests;
    }

    /**
     * Run all required tests on a subject
     * @return True or False
     * @throws ValidationException ValidationException
     */
    public boolean check() throws ValidationException {
        if(this.needsValidation()) {
            this.testInternal();
            this.test();
        } else {
            if(this.isRequired()) {
                putFailedTest("required", false, this.isRequired());
                return false;
            }
        }

        return getFailedTests().size() <= 0;
    }



    /**
     * Register a failed test in the registry
     * @param testName Name of the test
     * @param foundValue Value that is present
     * @param expectedValue Value that was expected
     */
    protected void putFailedTest(String testName, Object foundValue, Object expectedValue) {
        Map<String, Object> value = new HashMap<>();
        value.put("name", testName);
        value.put("expected", expectedValue);
        value.put("found", foundValue);

        this.failedTests.add(value);
    }

    /**
     * Check if a value exists and therefor if validation is required or not
     * @return True or False
     */
    protected boolean needsValidation() {
        boolean needsValidation;

        if(this.isRequired()) {
            return true;
        }

        // Check if subject is null, return false
        if(this.getSubject() == null) {
            return false;
        } else {
            if(this.getSubject() instanceof String) {
                String subj = (String) this.getSubject();
                needsValidation = !(subj.isBlank() || subj.isEmpty());
            } else if(this.getSubject() instanceof URL) {
                String subj = this.getSubject().toString();
                needsValidation = !(subj.isBlank() || subj.isEmpty());
            } else {
                needsValidation = true;
            }
        }

        return needsValidation;
    }

    /**
     * Check if a value is unique
     * @param validatorFunction Function for validation
     * @return Rule instance
     */
    public D unique(UniqueValidatorFunction validatorFunction) {
        this.uniqueValidatorFunction = validatorFunction;
        return (D) this;
    }

    /**
     * Perform internal tests like testing for null or unique
     */
    private void testInternal() {
        if(this.getSubject() == null) {
            putFailedTest("isNull", true, false);
            return;
        }

        if(this.uniqueValidatorFunction != null && !this.uniqueValidatorFunction.validate()) {
            putFailedTest("unique", false, true);
        }
    }

    @FunctionalInterface
    public interface UniqueValidatorFunction {
        boolean validate();
    }
}
