package eu.tsalliance.validator.rules;

import java.util.Date;

public class DateRule extends ValidationRule<Date, DateRule> {

    private Date before;
    private Date after;
    private Date equals;

    public DateRule(String fieldname, Date subject, boolean required) {
        super(fieldname, subject, required);
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
}
