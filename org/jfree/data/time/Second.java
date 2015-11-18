package org.jfree.data.time;

import java.io.Serializable;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;
import org.jfree.chart.util.ParamChecks;

public class Second extends RegularTimePeriod implements Serializable {
    public static final int FIRST_SECOND_IN_MINUTE = 0;
    public static final int LAST_SECOND_IN_MINUTE = 59;
    private static final long serialVersionUID = -6536564190712383466L;
    private Day day;
    private long firstMillisecond;
    private byte hour;
    private byte minute;
    private byte second;

    public Second() {
        this(new Date());
    }

    public Second(int second, Minute minute) {
        ParamChecks.nullNotPermitted(minute, "minute");
        this.day = minute.getDay();
        this.hour = (byte) minute.getHourValue();
        this.minute = (byte) minute.getMinute();
        this.second = (byte) second;
        peg(Calendar.getInstance());
    }

    public Second(int second, int minute, int hour, int day, int month, int year) {
        this(second, new Minute(minute, hour, day, month, year));
    }

    public Second(Date time) {
        this(time, TimeZone.getDefault(), Locale.getDefault());
    }

    public Second(Date time, TimeZone zone) {
        this(time, zone, Locale.getDefault());
    }

    public Second(Date time, TimeZone zone, Locale locale) {
        Calendar calendar = Calendar.getInstance(zone, locale);
        calendar.setTime(time);
        this.second = (byte) calendar.get(13);
        this.minute = (byte) calendar.get(12);
        this.hour = (byte) calendar.get(11);
        this.day = new Day(time, zone, locale);
        peg(calendar);
    }

    public int getSecond() {
        return this.second;
    }

    public Minute getMinute() {
        return new Minute(this.minute, new Hour(this.hour, this.day));
    }

    public long getFirstMillisecond() {
        return this.firstMillisecond;
    }

    public long getLastMillisecond() {
        return this.firstMillisecond + 999;
    }

    public void peg(Calendar calendar) {
        this.firstMillisecond = getFirstMillisecond(calendar);
    }

    public RegularTimePeriod previous() {
        if (this.second != null) {
            return new Second(this.second - 1, getMinute());
        }
        Minute previous = (Minute) getMinute().previous();
        if (previous != null) {
            return new Second((int) LAST_SECOND_IN_MINUTE, previous);
        }
        return null;
    }

    public RegularTimePeriod next() {
        if (this.second != LAST_SECOND_IN_MINUTE) {
            return new Second(this.second + 1, getMinute());
        }
        Minute next = (Minute) getMinute().next();
        if (next != null) {
            return new Second((int) FIRST_SECOND_IN_MINUTE, next);
        }
        return null;
    }

    public long getSerialIndex() {
        return (((((this.day.getSerialIndex() * 24) + ((long) this.hour)) * 60) + ((long) this.minute)) * 60) + ((long) this.second);
    }

    public long getFirstMillisecond(Calendar calendar) {
        int year = this.day.getYear();
        int month = this.day.getMonth() - 1;
        int d = this.day.getDayOfMonth();
        calendar.clear();
        calendar.set(year, month, d, this.hour, this.minute, this.second);
        calendar.set(14, FIRST_SECOND_IN_MINUTE);
        return calendar.getTimeInMillis();
    }

    public long getLastMillisecond(Calendar calendar) {
        return getFirstMillisecond(calendar) + 999;
    }

    public boolean equals(Object obj) {
        if (obj == this) {
            return true;
        }
        if (!(obj instanceof Second)) {
            return false;
        }
        Second that = (Second) obj;
        if (this.second != that.second) {
            return false;
        }
        if (this.minute != that.minute) {
            return false;
        }
        if (this.hour != that.hour) {
            return false;
        }
        if (this.day.equals(that.day)) {
            return true;
        }
        return false;
    }

    public int hashCode() {
        return ((((((this.second + 629) * 37) + this.minute) * 37) + this.hour) * 37) + this.day.hashCode();
    }

    public int compareTo(Object o1) {
        if (o1 instanceof Second) {
            Second s = (Second) o1;
            if (this.firstMillisecond < s.firstMillisecond) {
                return -1;
            }
            if (this.firstMillisecond > s.firstMillisecond) {
                return 1;
            }
            return FIRST_SECOND_IN_MINUTE;
        } else if (o1 instanceof RegularTimePeriod) {
            return FIRST_SECOND_IN_MINUTE;
        } else {
            return 1;
        }
    }

    public static Second parseSecond(String s) {
        s = s.trim();
        String daystr = s.substring(FIRST_SECOND_IN_MINUTE, Math.min(10, s.length()));
        Day day = Day.parseDay(daystr);
        if (day == null) {
            return null;
        }
        String hmsstr = s.substring(Math.min(daystr.length() + 1, s.length()), s.length()).trim();
        int l = hmsstr.length();
        String hourstr = hmsstr.substring(FIRST_SECOND_IN_MINUTE, Math.min(2, l));
        String minstr = hmsstr.substring(Math.min(3, l), Math.min(5, l));
        String secstr = hmsstr.substring(Math.min(6, l), Math.min(8, l));
        int hour = Integer.parseInt(hourstr);
        if (hour < 0 || hour > 23) {
            return null;
        }
        int minute = Integer.parseInt(minstr);
        if (minute < 0 || minute > LAST_SECOND_IN_MINUTE) {
            return null;
        }
        Minute m = new Minute(minute, new Hour(hour, day));
        int second = Integer.parseInt(secstr);
        if (second < 0 || second > LAST_SECOND_IN_MINUTE) {
            return null;
        }
        return new Second(second, m);
    }
}
