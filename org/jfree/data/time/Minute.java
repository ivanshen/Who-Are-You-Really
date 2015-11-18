package org.jfree.data.time;

import java.io.Serializable;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;
import org.jfree.chart.util.ParamChecks;

public class Minute extends RegularTimePeriod implements Serializable {
    public static final int FIRST_MINUTE_IN_HOUR = 0;
    public static final int LAST_MINUTE_IN_HOUR = 59;
    private static final long serialVersionUID = 2144572840034842871L;
    private Day day;
    private long firstMillisecond;
    private byte hour;
    private long lastMillisecond;
    private byte minute;

    public Minute() {
        this(new Date());
    }

    public Minute(int minute, Hour hour) {
        ParamChecks.nullNotPermitted(hour, "hour");
        this.minute = (byte) minute;
        this.hour = (byte) hour.getHour();
        this.day = hour.getDay();
        peg(Calendar.getInstance());
    }

    public Minute(Date time) {
        this(time, TimeZone.getDefault(), Locale.getDefault());
    }

    public Minute(Date time, TimeZone zone) {
        this(time, zone, Locale.getDefault());
    }

    public Minute(Date time, TimeZone zone, Locale locale) {
        ParamChecks.nullNotPermitted(time, "time");
        ParamChecks.nullNotPermitted(zone, "zone");
        ParamChecks.nullNotPermitted(locale, "locale");
        Calendar calendar = Calendar.getInstance(zone, locale);
        calendar.setTime(time);
        this.minute = (byte) calendar.get(12);
        this.hour = (byte) calendar.get(11);
        this.day = new Day(time, zone, locale);
        peg(calendar);
    }

    public Minute(int minute, int hour, int day, int month, int year) {
        this(minute, new Hour(hour, new Day(day, month, year)));
    }

    public Day getDay() {
        return this.day;
    }

    public Hour getHour() {
        return new Hour(this.hour, this.day);
    }

    public int getHourValue() {
        return this.hour;
    }

    public int getMinute() {
        return this.minute;
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
        if (this.minute != null) {
            return new Minute(this.minute - 1, getHour());
        }
        Hour h = (Hour) getHour().previous();
        if (h != null) {
            return new Minute((int) LAST_MINUTE_IN_HOUR, h);
        }
        return null;
    }

    public RegularTimePeriod next() {
        if (this.minute != LAST_MINUTE_IN_HOUR) {
            return new Minute(this.minute + 1, getHour());
        }
        Hour nextHour = (Hour) getHour().next();
        if (nextHour != null) {
            return new Minute((int) FIRST_MINUTE_IN_HOUR, nextHour);
        }
        return null;
    }

    public long getSerialIndex() {
        return (60 * ((this.day.getSerialIndex() * 24) + ((long) this.hour))) + ((long) this.minute);
    }

    public long getFirstMillisecond(Calendar calendar) {
        int year = this.day.getYear();
        int month = this.day.getMonth() - 1;
        int d = this.day.getDayOfMonth();
        calendar.clear();
        calendar.set(year, month, d, this.hour, this.minute, FIRST_MINUTE_IN_HOUR);
        calendar.set(14, FIRST_MINUTE_IN_HOUR);
        return calendar.getTimeInMillis();
    }

    public long getLastMillisecond(Calendar calendar) {
        int year = this.day.getYear();
        int month = this.day.getMonth() - 1;
        int d = this.day.getDayOfMonth();
        calendar.clear();
        calendar.set(year, month, d, this.hour, this.minute, LAST_MINUTE_IN_HOUR);
        calendar.set(14, Millisecond.LAST_MILLISECOND_IN_SECOND);
        return calendar.getTimeInMillis();
    }

    public boolean equals(Object obj) {
        if (obj == this) {
            return true;
        }
        if (!(obj instanceof Minute)) {
            return false;
        }
        Minute that = (Minute) obj;
        if (this.minute != that.minute) {
            return false;
        }
        if (this.hour != that.hour) {
            return false;
        }
        return true;
    }

    public int hashCode() {
        return ((((this.minute + 629) * 37) + this.hour) * 37) + this.day.hashCode();
    }

    public int compareTo(Object o1) {
        if (o1 instanceof Minute) {
            Minute m = (Minute) o1;
            int result = getHour().compareTo(m.getHour());
            return result == 0 ? this.minute - m.getMinute() : result;
        } else if (o1 instanceof RegularTimePeriod) {
            return FIRST_MINUTE_IN_HOUR;
        } else {
            return 1;
        }
    }

    public static Minute parseMinute(String s) {
        s = s.trim();
        String daystr = s.substring(FIRST_MINUTE_IN_HOUR, Math.min(10, s.length()));
        Day day = Day.parseDay(daystr);
        if (day == null) {
            return null;
        }
        String hmstr = s.substring(Math.min(daystr.length() + 1, s.length()), s.length()).trim();
        String hourstr = hmstr.substring(FIRST_MINUTE_IN_HOUR, Math.min(2, hmstr.length()));
        int hour = Integer.parseInt(hourstr);
        if (hour < 0 || hour > 23) {
            return null;
        }
        int minute = Integer.parseInt(hmstr.substring(Math.min(hourstr.length() + 1, hmstr.length()), hmstr.length()));
        if (minute < 0 || minute > LAST_MINUTE_IN_HOUR) {
            return null;
        }
        return new Minute(minute, new Hour(hour, day));
    }
}
