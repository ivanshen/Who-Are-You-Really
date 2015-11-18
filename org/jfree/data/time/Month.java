package org.jfree.data.time;

import java.io.Serializable;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;
import org.jfree.date.SerialDate;

public class Month extends RegularTimePeriod implements Serializable {
    private static final long serialVersionUID = -5090216912548722570L;
    private long firstMillisecond;
    private long lastMillisecond;
    private int month;
    private int year;

    public Month() {
        this(new Date());
    }

    public Month(int month, int year) {
        if (month < 1 || month > 12) {
            throw new IllegalArgumentException("Month outside valid range.");
        }
        this.month = month;
        this.year = year;
        peg(Calendar.getInstance());
    }

    public Month(int month, Year year) {
        if (month < 1 || month > 12) {
            throw new IllegalArgumentException("Month outside valid range.");
        }
        this.month = month;
        this.year = year.getYear();
        peg(Calendar.getInstance());
    }

    public Month(Date time) {
        this(time, TimeZone.getDefault());
    }

    public Month(Date time, TimeZone zone) {
        this(time, zone, Locale.getDefault());
    }

    public Month(Date time, TimeZone zone, Locale locale) {
        Calendar calendar = Calendar.getInstance(zone, locale);
        calendar.setTime(time);
        this.month = calendar.get(2) + 1;
        this.year = calendar.get(1);
        peg(calendar);
    }

    public Year getYear() {
        return new Year(this.year);
    }

    public int getYearValue() {
        return this.year;
    }

    public int getMonth() {
        return this.month;
    }

    public long getFirstMillisecond() {
        return this.firstMillisecond;
    }

    public long getLastMillisecond() {
        return this.lastMillisecond;
    }

    public void peg(Calendar calendar) {
        this.firstMillisecond = getFirstMillisecond(calendar);
        this.lastMillisecond = getLastMillisecond(calendar);
    }

    public RegularTimePeriod previous() {
        if (this.month != 1) {
            return new Month(this.month - 1, this.year);
        }
        if (this.year > SerialDate.MINIMUM_YEAR_SUPPORTED) {
            return new Month(12, this.year - 1);
        }
        return null;
    }

    public RegularTimePeriod next() {
        if (this.month != 12) {
            return new Month(this.month + 1, this.year);
        }
        if (this.year < SerialDate.MAXIMUM_YEAR_SUPPORTED) {
            return new Month(1, this.year + 1);
        }
        return null;
    }

    public long getSerialIndex() {
        return (((long) this.year) * 12) + ((long) this.month);
    }

    public String toString() {
        return SerialDate.monthCodeToString(this.month) + " " + this.year;
    }

    public boolean equals(Object obj) {
        if (obj == this) {
            return true;
        }
        if (!(obj instanceof Month)) {
            return false;
        }
        Month that = (Month) obj;
        if (this.month != that.month) {
            return false;
        }
        if (this.year != that.year) {
            return false;
        }
        return true;
    }

    public int hashCode() {
        return ((this.month + 629) * 37) + this.year;
    }

    public int compareTo(Object o1) {
        if (o1 instanceof Month) {
            Month m = (Month) o1;
            int result = this.year - m.getYearValue();
            return result == 0 ? this.month - m.getMonth() : result;
        } else if (o1 instanceof RegularTimePeriod) {
            return 0;
        } else {
            return 1;
        }
    }

    public long getFirstMillisecond(Calendar calendar) {
        calendar.set(this.year, this.month - 1, 1, 0, 0, 0);
        calendar.set(14, 0);
        return calendar.getTimeInMillis();
    }

    public long getLastMillisecond(Calendar calendar) {
        int eom = SerialDate.lastDayOfMonth(this.month, this.year);
        calendar.set(this.year, this.month - 1, eom, 23, 59, 59);
        calendar.set(14, Millisecond.LAST_MILLISECOND_IN_SECOND);
        return calendar.getTimeInMillis();
    }

    public static Month parseMonth(String s) {
        if (s == null) {
            return null;
        }
        boolean yearIsFirst;
        String s1;
        String s2;
        Year year;
        int month;
        s = s.trim();
        int i = findSeparator(s);
        if (i == -1) {
            yearIsFirst = true;
            s1 = s.substring(0, 5);
            s2 = s.substring(5);
        } else {
            s1 = s.substring(0, i).trim();
            s2 = s.substring(i + 1, s.length()).trim();
            if (evaluateAsYear(s1) == null) {
                yearIsFirst = false;
            } else if (evaluateAsYear(s2) == null) {
                yearIsFirst = true;
            } else {
                yearIsFirst = s1.length() > s2.length();
            }
        }
        if (yearIsFirst) {
            year = evaluateAsYear(s1);
            month = SerialDate.stringToMonthCode(s2);
        } else {
            year = evaluateAsYear(s2);
            month = SerialDate.stringToMonthCode(s1);
        }
        if (month == -1) {
            throw new TimePeriodFormatException("Can't evaluate the month.");
        } else if (year != null) {
            return new Month(month, year);
        } else {
            throw new TimePeriodFormatException("Can't evaluate the year.");
        }
    }

    private static int findSeparator(String s) {
        int result = s.indexOf(45);
        if (result == -1) {
            result = s.indexOf(44);
        }
        if (result == -1) {
            result = s.indexOf(32);
        }
        if (result == -1) {
            return s.indexOf(46);
        }
        return result;
    }

    private static Year evaluateAsYear(String s) {
        Year result = null;
        try {
            result = Year.parseYear(s);
        } catch (TimePeriodFormatException e) {
        }
        return result;
    }
}
