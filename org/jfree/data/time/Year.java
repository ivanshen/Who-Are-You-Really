package org.jfree.data.time;

import java.io.Serializable;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

public class Year extends RegularTimePeriod implements Serializable {
    public static final int MAXIMUM_YEAR = 9999;
    public static final int MINIMUM_YEAR = -9999;
    private static final long serialVersionUID = -7659990929736074836L;
    private long firstMillisecond;
    private long lastMillisecond;
    private short year;

    public Year() {
        this(new Date());
    }

    public Year(int year) {
        if (year < MINIMUM_YEAR || year > MAXIMUM_YEAR) {
            throw new IllegalArgumentException("Year constructor: year (" + year + ") outside valid range.");
        }
        this.year = (short) year;
        peg(Calendar.getInstance());
    }

    public Year(Date time) {
        this(time, TimeZone.getDefault());
    }

    public Year(Date time, TimeZone zone) {
        this(time, zone, Locale.getDefault());
    }

    public Year(Date time, TimeZone zone, Locale locale) {
        Calendar calendar = Calendar.getInstance(zone, locale);
        calendar.setTime(time);
        this.year = (short) calendar.get(1);
        peg(calendar);
    }

    public int getYear() {
        return this.year;
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
        if (this.year > (short) -9999) {
            return new Year(this.year - 1);
        }
        return null;
    }

    public RegularTimePeriod next() {
        if (this.year < (short) 9999) {
            return new Year(this.year + 1);
        }
        return null;
    }

    public long getSerialIndex() {
        return (long) this.year;
    }

    public long getFirstMillisecond(Calendar calendar) {
        calendar.set(this.year, 0, 1, 0, 0, 0);
        calendar.set(14, 0);
        return calendar.getTimeInMillis();
    }

    public long getLastMillisecond(Calendar calendar) {
        calendar.set(this.year, 11, 31, 23, 59, 59);
        calendar.set(14, Millisecond.LAST_MILLISECOND_IN_SECOND);
        return calendar.getTimeInMillis();
    }

    public boolean equals(Object obj) {
        if (obj == this) {
            return true;
        }
        if (!(obj instanceof Year)) {
            return false;
        }
        if (this.year != ((Year) obj).year) {
            return false;
        }
        return true;
    }

    public int hashCode() {
        return this.year + 629;
    }

    public int compareTo(Object o1) {
        if (o1 instanceof Year) {
            return this.year - ((Year) o1).getYear();
        } else if (o1 instanceof RegularTimePeriod) {
            return 0;
        } else {
            return 1;
        }
    }

    public String toString() {
        return Integer.toString(this.year);
    }

    public static Year parseYear(String s) {
        try {
            try {
                return new Year(Integer.parseInt(s.trim()));
            } catch (IllegalArgumentException e) {
                throw new TimePeriodFormatException("Year outside valid range.");
            }
        } catch (NumberFormatException e2) {
            throw new TimePeriodFormatException("Cannot parse string.");
        }
    }
}
