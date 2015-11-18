package org.jfree.data.time;

import java.io.Serializable;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;
import org.jfree.chart.util.ParamChecks;

public class Hour extends RegularTimePeriod implements Serializable {
    public static final int FIRST_HOUR_IN_DAY = 0;
    public static final int LAST_HOUR_IN_DAY = 23;
    private static final long serialVersionUID = -835471579831937652L;
    private Day day;
    private long firstMillisecond;
    private byte hour;
    private long lastMillisecond;

    public Hour() {
        this(new Date());
    }

    public Hour(int hour, Day day) {
        ParamChecks.nullNotPermitted(day, "day");
        this.hour = (byte) hour;
        this.day = day;
        peg(Calendar.getInstance());
    }

    public Hour(int hour, int day, int month, int year) {
        this(hour, new Day(day, month, year));
    }

    public Hour(Date time) {
        this(time, TimeZone.getDefault(), Locale.getDefault());
    }

    public Hour(Date time, TimeZone zone) {
        this(time, zone, Locale.getDefault());
    }

    public Hour(Date time, TimeZone zone, Locale locale) {
        ParamChecks.nullNotPermitted(time, "time");
        ParamChecks.nullNotPermitted(zone, "zone");
        ParamChecks.nullNotPermitted(locale, "locale");
        Calendar calendar = Calendar.getInstance(zone, locale);
        calendar.setTime(time);
        this.hour = (byte) calendar.get(11);
        this.day = new Day(time, zone, locale);
        peg(calendar);
    }

    public int getHour() {
        return this.hour;
    }

    public Day getDay() {
        return this.day;
    }

    public int getYear() {
        return this.day.getYear();
    }

    public int getMonth() {
        return this.day.getMonth();
    }

    public int getDayOfMonth() {
        return this.day.getDayOfMonth();
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
        if (this.hour != null) {
            return new Hour(this.hour - 1, this.day);
        }
        Day prevDay = (Day) this.day.previous();
        if (prevDay != null) {
            return new Hour((int) LAST_HOUR_IN_DAY, prevDay);
        }
        return null;
    }

    public RegularTimePeriod next() {
        if (this.hour != LAST_HOUR_IN_DAY) {
            return new Hour(this.hour + 1, this.day);
        }
        Day nextDay = (Day) this.day.next();
        if (nextDay != null) {
            return new Hour((int) FIRST_HOUR_IN_DAY, nextDay);
        }
        return null;
    }

    public long getSerialIndex() {
        return (this.day.getSerialIndex() * 24) + ((long) this.hour);
    }

    public long getFirstMillisecond(Calendar calendar) {
        calendar.set(this.day.getYear(), this.day.getMonth() - 1, this.day.getDayOfMonth(), this.hour, FIRST_HOUR_IN_DAY, FIRST_HOUR_IN_DAY);
        calendar.set(14, FIRST_HOUR_IN_DAY);
        return calendar.getTimeInMillis();
    }

    public long getLastMillisecond(Calendar calendar) {
        calendar.set(this.day.getYear(), this.day.getMonth() - 1, this.day.getDayOfMonth(), this.hour, 59, 59);
        calendar.set(14, Millisecond.LAST_MILLISECOND_IN_SECOND);
        return calendar.getTimeInMillis();
    }

    public boolean equals(Object obj) {
        if (obj == this) {
            return true;
        }
        if (!(obj instanceof Hour)) {
            return false;
        }
        Hour that = (Hour) obj;
        if (this.hour != that.hour) {
            return false;
        }
        if (this.day.equals(that.day)) {
            return true;
        }
        return false;
    }

    public String toString() {
        return "[" + this.hour + "," + getDayOfMonth() + "/" + getMonth() + "/" + getYear() + "]";
    }

    public int hashCode() {
        return ((this.hour + 629) * 37) + this.day.hashCode();
    }

    public int compareTo(Object o1) {
        if (o1 instanceof Hour) {
            Hour h = (Hour) o1;
            int result = getDay().compareTo(h.getDay());
            return result == 0 ? this.hour - h.getHour() : result;
        } else if (o1 instanceof RegularTimePeriod) {
            return FIRST_HOUR_IN_DAY;
        } else {
            return 1;
        }
    }

    public static Hour parseHour(String s) {
        s = s.trim();
        String daystr = s.substring(FIRST_HOUR_IN_DAY, Math.min(10, s.length()));
        Day day = Day.parseDay(daystr);
        if (day == null) {
            return null;
        }
        int hour = Integer.parseInt(s.substring(Math.min(daystr.length() + 1, s.length()), s.length()).trim());
        if (hour < 0 || hour > LAST_HOUR_IN_DAY) {
            return null;
        }
        return new Hour(hour, day);
    }
}
