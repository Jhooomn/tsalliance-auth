package eu.tsalliance.auth;

import eu.tsalliance.auth.exception.ValidationException;
import eu.tsalliance.auth.validator.Validator;
import eu.tsalliance.auth.validator.rules.PasswordRule;
import eu.tsalliance.auth.validator.rules.ValidationRule;
import org.junit.jupiter.api.Test;

import java.util.regex.Pattern;

public class PasswordValidatorTest {

    @Test
    public void testPasswordValidation() throws ValidationException {
        Validator validator = new Validator();

        String passwordWithoutNumber = "#Password";
        String passwordWithoutSymbol = "Password123";
        String passwordWithoutUpper = "#password123";
        String passwordWithoutLower = "#PASSWORD123";
        String passwordWrongLength = "#Pa1";
        String passwordValid = "#Password123";

        PasswordRule rule = validator.validatePassword(passwordWithoutNumber, "password", true);
        rule.check();
        this.printFailedTests(rule, "passwordWithoutNumber");


        rule = validator.validatePassword(passwordWithoutSymbol, "password", true);
        rule.check();
        this.printFailedTests(rule, "passwordWithoutSymbol");


        rule = validator.validatePassword(passwordWithoutUpper, "password", true);
        rule.check();
        this.printFailedTests(rule, "passwordWithoutUpper");


        rule = validator.validatePassword(passwordWithoutLower, "password", true);
        rule.check();
        this.printFailedTests(rule, "passwordWithoutLower");


        rule = validator.validatePassword(passwordWrongLength, "password", true);
        rule.check();
        this.printFailedTests(rule, "passwordWrongLength");

        rule = validator.validatePassword(passwordValid, "password", true);
        rule.check();
        this.printFailedTests(rule, "passwordValid");

    }

    private void printFailedTests(ValidationRule rule, String testName) {
        System.out.println("###########");
        System.out.println(testName);
        System.out.println("Failed: " + rule.getFailedTests().size());
        System.out.println(" ");

        for(Object key : rule.getFailedTests().keySet()) {
            System.out.println(key + ": " + rule.getFailedTests().get(key));
        }

        System.out.println(" ");
    }

}
