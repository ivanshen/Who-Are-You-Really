package org.jfree.date;

import org.jfree.util.LineBreakIterator;
import org.jfree.util.LogTarget;

public class RelativeDayOfWeekRule extends AnnualDateRule {
    private int dayOfWeek;
    private int relative;
    private AnnualDateRule subrule;

    public RelativeDayOfWeekRule() {
        this(new DayAndMonthRule(), 2, 1);
    }

    public RelativeDayOfWeekRule(AnnualDateRule subrule, int dayOfWeek, int relative) {
        this.subrule = subrule;
        this.dayOfWeek = dayOfWeek;
        this.relative = relative;
    }

    public AnnualDateRule getSubrule() {
        return this.subrule;
    }

    public void setSubrule(AnnualDateRule subrule) {
        this.subrule = subrule;
    }

    public int getDayOfWeek() {
        return this.dayOfWeek;
    }

    public void setDayOfWeek(int dayOfWeek) {
        this.dayOfWeek = dayOfWeek;
    }

    public int getRelative() {
        return this.relative;
    }

    public void setRelative(int relative) {
        this.relative = relative;
    }

    public Object clone() throws CloneNotSupportedException {
        RelativeDayOfWeekRule duplicate = (RelativeDayOfWeekRule) super.clone();
        duplicate.subrule = (AnnualDateRule) duplicate.getSubrule().clone();
        return duplicate;
    }

    public SerialDate getDate(int year) {
        if (year < SerialDate.MINIMUM_YEAR_SUPPORTED || year > SerialDate.MAXIMUM_YEAR_SUPPORTED) {
            throw new IllegalArgumentException("RelativeDayOfWeekRule.getDate(): year outside valid range.");
        }
        SerialDate base = this.subrule.getDate(year);
        if (base == null) {
            return null;
        }
        switch (this.relative) {
            case LineBreakIterator.DONE /*-1*/:
                return SerialDate.getPreviousDayOfWeek(this.dayOfWeek, base);
            case LogTarget.ERROR /*0*/:
                return SerialDate.getNearestDayOfWeek(this.dayOfWeek, base);
            case LogTarget.WARN /*1*/:
                return SerialDate.getFollowingDayOfWeek(this.dayOfWeek, base);
            default:
                return null;
        }
    }
}
