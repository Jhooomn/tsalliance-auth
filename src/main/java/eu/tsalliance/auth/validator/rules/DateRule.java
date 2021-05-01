package eu.tsalliance.auth.validator.rules;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class DateRule extends ValidationRule<Date, DateRule> {

    private Date before;
    private Date after;
    private Date equals;

    public DateRule(String fieldname, Date subject, boolean required, boolean throwException) {
        super(fieldname, subject, required, throwException);
    }

    public DateRule after(Date after) {
        this.after = after;
        return this;
    }

    public DateRule before(Date before) {
        this.before = before;
        return this;
    }

    public DateRule equals(Date equals) {
        this.equals = equals;
        return this;
    }

    @Override
    protected void test() {
        if(this.after != null && !this.getSubject().after(this.after)) {
            putFailedTest("after", this.getSubject(), this.after);
        }

        if(this.before != null && !this.getSubject().before(this.before)) {
            putFailedTest("before", this.getSubject(), this.before);
        }

        if(this.equals != null && this.getSubject().getTime() != this.equals.getTime()) {
            putFailedTest("equals", this.getSubject(), this.equals);
        }
    }

    @Override
    public Map<String, Object> getRequirements() {
        Map<String, Object> requirements = new HashMap<>();

        if(this.after != null) requirements.put("after", this.after);
        if(this.before != null) requirements.put("before", this.before);
        if(this.equals != null) requirements.put("equals", this.equals);

        requirements.put("required", this.isRequired());
        return requirements;
    }
}
