package org.jfree.data.time;

import java.io.Serializable;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

public class Millisecond extends RegularTimePeriod implements Serializable {
    public static final int FIRST_MILLISECOND_IN_SECOND = 0;
    public static final int LAST_MILLISECOND_IN_SECOND = 999;
    static final long serialVersionUID = -5316836467277638485L;
    private Day day;
    private long firstMillisecond;
    private byte hour;
    private int millisecond;
    private byte minute;
    private byte second;

    public Millisecond() {
        this(new Date());
    }

    public Millisecond(int millisecond, Second second) {
        this.millisecond = millisecond;
        this.second = (byte) second.getSecond();
        this.minute = (byte) second.getMinute().getMinute();
        this.hour = (byte) second.getMinute().getHourValue();
        this.day = second.getMinute().getDay();
        peg(Calendar.getInstance());
    }

    public Millisecond(int millisecond, int second, int minute, int hour, int day, int month, int year) {
        this(millisecond, new Second(second, minute, hour, day, month, year));
    }

    public Millisecond(Date time) {
        this(time, TimeZone.getDefault(), Locale.getDefault());
    }

    public Millisecond(Date time, TimeZone zone) {
        this(time, zone, Locale.getDefault());
    }

    public Millisecond(Date time, TimeZone zone, Locale locale) {
        Calendar calendar = Calendar.getInstance(zone, locale);
        calendar.setTime(time);
        this.millisecond = calendar.get(14);
        this.second = (byte) calendar.get(13);
        this.minute = (byte) calendar.get(12);
        this.hour = (byte) calendar.get(11);
        this.day = new Day(time, zone, locale);
        peg(calendar);
    }

    public Second getSecond() {
        return new Second(this.second, this.minute, this.hour, this.day.getDayOfMonth(), this.day.getMonth(), this.day.getYear());
    }

    public long getMillisecond() {
        return (long) this.millisecond;
    }

    public long getFirstMillisecond() {
        return this.firstMillisecond;
    }

    public long getLastMillisecond() {
        return this.firstMillisecond;
    }

    public void peg(Calendar calendar) {
        this.firstMillisecond = getFirstMillisecond(calendar);
    }

    public RegularTimePeriod previous() {
        if (this.millisecond != 0) {
            return new Millisecond(this.millisecond - 1, getSecond());
        }
        Second previous = (Second) getSecond().previous();
        if (previous != null) {
            return new Millisecond((int) LAST_MILLISECOND_IN_SECOND, previous);
        }
        return null;
    }

    public RegularTimePeriod next() {
        if (this.millisecond != LAST_MILLISECOND_IN_SECOND) {
            return new Millisecond(this.millisecond + 1, getSecond());
        }
        Second next = (Second) getSecond().next();
        if (next != null) {
            return new Millisecond((int) FIRST_MILLISECOND_IN_SECOND, next);
        }
        return null;
    }

    public long getSerialIndex() {
        return (1000 * ((((((this.day.getSerialIndex() * 24) + ((long) this.hour)) * 60) + ((long) this.minute)) * 60) + ((long) this.second))) + ((long) this.millisecond);
    }

    public boolean equals(Object obj) {
        if (obj == this) {
            return true;
        }
        if (!(obj instanceof Millisecond)) {
            return false;
        }
        Millisecond that = (Millisecond) obj;
        if (this.millisecond != that.millisecond) {
            return false;
        }
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
        return ((this.millisecond + 629) * 37) + getSecond().hashCode();
    }

    public int compareTo(Object obj) {
        if (obj instanceof Millisecond) {
            long difference = getFirstMillisecond() - ((Millisecond) obj).getFirstMillisecond();
            if (difference > 0) {
                return 1;
            }
            if (difference < 0) {
                return -1;
            }
            return FIRST_MILLISECOND_IN_SECOND;
        } else if (!(obj instanceof RegularTimePeriod)) {
            return 1;
        } else {
            RegularTimePeriod rtp = (RegularTimePeriod) obj;
            long thisVal = getFirstMillisecond();
            long anotherVal = rtp.getFirstMillisecond();
            int result = thisVal < anotherVal ? -1 : thisVal == anotherVal ? FIRST_MILLISECOND_IN_SECOND : 1;
            return result;
        }
    }

    public long getFirstMillisecond(Calendar calendar) {
        int year = this.day.getYear();
        int month = this.day.getMonth() - 1;
        int d = this.day.getDayOfMonth();
        calendar.clear();
        calendar.set(year, month, d, this.hour, this.minute, this.second);
        calendar.set(14, this.millisecond);
        return calendar.getTimeInMillis();
    }

    public long getLastMillisecond(Calendar calendar) {
        return getFirstMillisecond(calendar);
    }
}
