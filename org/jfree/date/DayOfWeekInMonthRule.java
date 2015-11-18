package org.jfree.date;

public class DayOfWeekInMonthRule extends AnnualDateRule {
    private int count;
    private int dayOfWeek;
    private int month;

    public DayOfWeekInMonthRule() {
        this(1, 2, 1);
    }

    public DayOfWeekInMonthRule(int count, int dayOfWeek, int month) {
        this.count = count;
        this.dayOfWeek = dayOfWeek;
        this.month = month;
    }

    public int getCount() {
        return this.count;
    }

    public void setCount(int count) {
        this.count = count;
    }

    public int getDayOfWeek() {
        return this.dayOfWeek;
    }

    public void setDayOfWeek(int dayOfWeek) {
        this.dayOfWeek = dayOfWeek;
    }

    public int getMonth() {
        return this.month;
    }

    public void setMonth(int month) {
        this.month = month;
    }

    public SerialDate getDate(int year) {
        SerialDate result;
        if (this.count != 0) {
            result = SerialDate.createInstance(1, this.month, year);
            while (result.getDayOfWeek() != this.dayOfWeek) {
                result = SerialDate.addDays(1, result);
            }
            return SerialDate.addDays((this.count - 1) * 7, result);
        }
        result = SerialDate.createInstance(1, this.month, year);
        result = result.getEndOfCurrentMonth(result);
        while (result.getDayOfWeek() != this.dayOfWeek) {
            result = SerialDate.addDays(-1, result);
        }
        return result;
    }
}
