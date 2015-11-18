package org.jfree.data.time;

import java.io.Serializable;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;
import org.jfree.chart.util.ParamChecks;

public class Week extends RegularTimePeriod implements Serializable {
    public static final int FIRST_WEEK_IN_YEAR = 1;
    public static final int LAST_WEEK_IN_YEAR = 53;
    private static final long serialVersionUID = 1856387786939865061L;
    private long firstMillisecond;
    private long lastMillisecond;
    private byte week;
    private short year;

    public Week() {
        this(new Date());
    }

    public Week(int week, int year) {
        if (week >= FIRST_WEEK_IN_YEAR || week <= LAST_WEEK_IN_YEAR) {
            this.week = (byte) week;
            this.year = (short) year;
            peg(Calendar.getInstance());
            return;
        }
        throw new IllegalArgumentException("The 'week' argument must be in the range 1 - 53.");
    }

    public Week(int week, Year year) {
        if (week >= FIRST_WEEK_IN_YEAR || week <= LAST_WEEK_IN_YEAR) {
            this.week = (byte) week;
            this.year = (short) year.getYear();
            peg(Calendar.getInstance());
            return;
        }
        throw new IllegalArgumentException("The 'week' argument must be in the range 1 - 53.");
    }

    public Week(Date time) {
        this(time, TimeZone.getDefault(), Locale.getDefault());
    }

    public Week(Date time, TimeZone zone) {
        this(time, zone, Locale.getDefault());
    }

    public Week(Date time, TimeZone zone, Locale locale) {
        ParamChecks.nullNotPermitted(time, "time");
        ParamChecks.nullNotPermitted(zone, "zone");
        ParamChecks.nullNotPermitted(locale, "locale");
        Calendar calendar = Calendar.getInstance(zone, locale);
        calendar.setTime(time);
        int tempWeek = calendar.get(3);
        if (tempWeek == FIRST_WEEK_IN_YEAR && calendar.get(2) == 11) {
            this.week = (byte) 1;
            this.year = (short) (calendar.get(FIRST_WEEK_IN_YEAR) + FIRST_WEEK_IN_YEAR);
        } else {
            this.week = (byte) Math.min(tempWeek, LAST_WEEK_IN_YEAR);
            int yyyy = calendar.get(FIRST_WEEK_IN_YEAR);
            if (calendar.get(2) == 0 && this.week >= 52) {
                yyyy--;
            }
            this.year = (short) yyyy;
        }
        peg(calendar);
    }

    public Year getYear() {
        return new Year(this.year);
    }

    public int getYearValue() {
        return this.year;
    }

    public int getWeek() {
        return this.week;
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
        if (this.week != FIRST_WEEK_IN_YEAR) {
            return new Week(this.week - 1, this.year);
        }
        if (this.year <= (short) 1900) {
            return null;
        }
        int yy = this.year - 1;
        Calendar prevYearCalendar = Calendar.getInstance();
        prevYearCalendar.set(yy, 11, 31);
        return new Week(prevYearCalendar.getActualMaximum(3), yy);
    }

    public RegularTimePeriod next() {
        if (this.week < 52) {
            return new Week(this.week + FIRST_WEEK_IN_YEAR, this.year);
        }
        Calendar calendar = Calendar.getInstance();
        calendar.set(this.year, 11, 31);
        if (this.week < calendar.getActualMaximum(3)) {
            return new Week(this.week + FIRST_WEEK_IN_YEAR, this.year);
        }
        if (this.year < (short) 9999) {
            return new Week((int) FIRST_WEEK_IN_YEAR, this.year + FIRST_WEEK_IN_YEAR);
        }
        return null;
    }

    public long getSerialIndex() {
        return (((long) this.year) * 53) + ((long) this.week);
    }

    public long getFirstMillisecond(Calendar calendar) {
        Calendar c = (Calendar) calendar.clone();
        c.clear();
        c.set(FIRST_WEEK_IN_YEAR, this.year);
        c.set(3, this.week);
        c.set(7, c.getFirstDayOfWeek());
        c.set(10, 0);
        c.set(12, 0);
        c.set(13, 0);
        c.set(14, 0);
        return c.getTimeInMillis();
    }

    public long getLastMillisecond(Calendar calendar) {
        Calendar c = (Calendar) calendar.clone();
        c.clear();
        c.set(FIRST_WEEK_IN_YEAR, this.year);
        c.set(3, this.week + FIRST_WEEK_IN_YEAR);
        c.set(7, c.getFirstDayOfWeek());
        c.set(10, 0);
        c.set(12, 0);
        c.set(13, 0);
        c.set(14, 0);
        return c.getTimeInMillis() - 1;
    }

    public String toString() {
        return "Week " + this.week + ", " + this.year;
    }

    public boolean equals(Object obj) {
        if (obj == this) {
            return true;
        }
        if (!(obj instanceof Week)) {
            return false;
        }
        Week that = (Week) obj;
        if (this.week != that.week) {
            return false;
        }
        if (this.year != that.year) {
            return false;
        }
        return true;
    }

    public int hashCode() {
        return ((this.week + 629) * 37) + this.year;
    }

    public int compareTo(Object o1) {
        if (o1 instanceof Week) {
            Week w = (Week) o1;
            int result = this.year - w.getYear().getYear();
            return result == 0 ? this.week - w.getWeek() : result;
        } else if (o1 instanceof RegularTimePeriod) {
            return 0;
        } else {
            return FIRST_WEEK_IN_YEAR;
        }
    }

    public static Week parseWeek(String s) {
        if (s == null) {
            return null;
        }
        s = s.trim();
        int i = findSeparator(s);
        if (i != -1) {
            String s1 = s.substring(0, i).trim();
            String s2 = s.substring(i + FIRST_WEEK_IN_YEAR, s.length()).trim();
            Year y = evaluateAsYear(s1);
            int w;
            if (y != null) {
                w = stringToWeek(s2);
                if (w != -1) {
                    return new Week(w, y);
                }
                throw new TimePeriodFormatException("Can't evaluate the week.");
            }
            y = evaluateAsYear(s2);
            if (y != null) {
                w = stringToWeek(s1);
                if (w != -1) {
                    return new Week(w, y);
                }
                throw new TimePeriodFormatException("Can't evaluate the week.");
            }
            throw new TimePeriodFormatException("Can't evaluate the year.");
        }
        throw new TimePeriodFormatException("Could not find separator.");
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

    private static int stringToWeek(String s) {
        int result = -1;
        try {
            result = Integer.parseInt(s.replace('W', ' ').trim());
            if (result < FIRST_WEEK_IN_YEAR || result > LAST_WEEK_IN_YEAR) {
                return -1;
            }
            return result;
        } catch (NumberFormatException e) {
            return result;
        }
    }
}
