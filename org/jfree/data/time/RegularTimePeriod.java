package org.jfree.data.time;

import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;
import org.jfree.date.MonthConstants;

public abstract class RegularTimePeriod implements TimePeriod, Comparable, MonthConstants {
    public static final TimeZone DEFAULT_TIME_ZONE;
    public static final Calendar WORKING_CALENDAR;

    public abstract long getFirstMillisecond();

    public abstract long getFirstMillisecond(Calendar calendar);

    public abstract long getLastMillisecond();

    public abstract long getLastMillisecond(Calendar calendar);

    public abstract long getSerialIndex();

    public abstract RegularTimePeriod next();

    public abstract void peg(Calendar calendar);

    public abstract RegularTimePeriod previous();

    public static RegularTimePeriod createInstance(Class c, Date millisecond, TimeZone zone) {
        RegularTimePeriod result = null;
        try {
            return (RegularTimePeriod) c.getDeclaredConstructor(new Class[]{Date.class, TimeZone.class}).newInstance(new Object[]{millisecond, zone});
        } catch (Exception e) {
            return result;
        }
    }

    public static Class downsize(Class c) {
        if (c.equals(Year.class)) {
            return Quarter.class;
        }
        if (c.equals(Quarter.class)) {
            return Month.class;
        }
        if (c.equals(Month.class)) {
            return Day.class;
        }
        if (c.equals(Day.class)) {
            return Hour.class;
        }
        if (c.equals(Hour.class)) {
            return Minute.class;
        }
        if (c.equals(Minute.class)) {
            return Second.class;
        }
        if (c.equals(Second.class)) {
            return Millisecond.class;
        }
        return Millisecond.class;
    }

    static {
        DEFAULT_TIME_ZONE = TimeZone.getDefault();
        WORKING_CALENDAR = Calendar.getInstance(DEFAULT_TIME_ZONE);
    }

    public Date getStart() {
        return new Date(getFirstMillisecond());
    }

    public Date getEnd() {
        return new Date(getLastMillisecond());
    }

    public long getFirstMillisecond(TimeZone zone) {
        return getFirstMillisecond(Calendar.getInstance(zone));
    }

    public long getLastMillisecond(TimeZone zone) {
        return getLastMillisecond(Calendar.getInstance(zone));
    }

    public long getMiddleMillisecond() {
        long m1 = getFirstMillisecond();
        return ((getLastMillisecond() - m1) / 2) + m1;
    }

    public long getMiddleMillisecond(TimeZone zone) {
        Calendar calendar = Calendar.getInstance(zone);
        long m1 = getFirstMillisecond(calendar);
        return ((getLastMillisecond(calendar) - m1) / 2) + m1;
    }

    public long getMiddleMillisecond(Calendar calendar) {
        long m1 = getFirstMillisecond(calendar);
        return ((getLastMillisecond(calendar) - m1) / 2) + m1;
    }

    public long getMillisecond(TimePeriodAnchor anchor, Calendar calendar) {
        if (anchor.equals(TimePeriodAnchor.START)) {
            return getFirstMillisecond(calendar);
        }
        if (anchor.equals(TimePeriodAnchor.MIDDLE)) {
            return getMiddleMillisecond(calendar);
        }
        if (anchor.equals(TimePeriodAnchor.END)) {
            return getLastMillisecond(calendar);
        }
        throw new IllegalStateException("Unrecognised anchor: " + anchor);
    }

    public String toString() {
        return String.valueOf(getStart());
    }
}
