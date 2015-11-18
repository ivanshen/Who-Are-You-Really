package org.jfree.data.time;

import java.io.Serializable;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;
import org.jfree.chart.util.ParamChecks;
import org.jfree.date.SerialDate;

public class Day extends RegularTimePeriod implements Serializable {
    protected static final DateFormat DATE_FORMAT;
    protected static final DateFormat DATE_FORMAT_LONG;
    protected static final DateFormat DATE_FORMAT_MEDIUM;
    protected static final DateFormat DATE_FORMAT_SHORT;
    private static final long serialVersionUID = -7082667380758962755L;
    private long firstMillisecond;
    private long lastMillisecond;
    private SerialDate serialDate;

    static {
        DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd");
        DATE_FORMAT_SHORT = DateFormat.getDateInstance(3);
        DATE_FORMAT_MEDIUM = DateFormat.getDateInstance(2);
        DATE_FORMAT_LONG = DateFormat.getDateInstance(1);
    }

    public Day() {
        this(new Date());
    }

    public Day(int day, int month, int year) {
        this.serialDate = SerialDate.createInstance(day, month, year);
        peg(Calendar.getInstance());
    }

    public Day(SerialDate serialDate) {
        ParamChecks.nullNotPermitted(serialDate, "serialDate");
        this.serialDate = serialDate;
        peg(Calendar.getInstance());
    }

    public Day(Date time) {
        this(time, TimeZone.getDefault(), Locale.getDefault());
    }

    public Day(Date time, TimeZone zone) {
        this(time, zone, Locale.getDefault());
    }

    public Day(Date time, TimeZone zone, Locale locale) {
        ParamChecks.nullNotPermitted(time, "time");
        ParamChecks.nullNotPermitted(zone, "zone");
        ParamChecks.nullNotPermitted(locale, "locale");
        Calendar calendar = Calendar.getInstance(zone, locale);
        calendar.setTime(time);
        this.serialDate = SerialDate.createInstance(calendar.get(5), calendar.get(2) + 1, calendar.get(1));
        peg(calendar);
    }

    public SerialDate getSerialDate() {
        return this.serialDate;
    }

    public int getYear() {
        return this.serialDate.getYYYY();
    }

    public int getMonth() {
        return this.serialDate.getMonth();
    }

    public int getDayOfMonth() {
        return this.serialDate.getDayOfMonth();
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
        int serial = this.serialDate.toSerial();
        if (serial > 2) {
            return new Day(SerialDate.createInstance(serial - 1));
        }
        return null;
    }

    public RegularTimePeriod next() {
        int serial = this.serialDate.toSerial();
        if (serial < SerialDate.SERIAL_UPPER_BOUND) {
            return new Day(SerialDate.createInstance(serial + 1));
        }
        return null;
    }

    public long getSerialIndex() {
        return (long) this.serialDate.toSerial();
    }

    public long getFirstMillisecond(Calendar calendar) {
        int year = this.serialDate.getYYYY();
        int month = this.serialDate.getMonth();
        int day = this.serialDate.getDayOfMonth();
        calendar.clear();
        calendar.set(year, month - 1, day, 0, 0, 0);
        calendar.set(14, 0);
        return calendar.getTimeInMillis();
    }

    public long getLastMillisecond(Calendar calendar) {
        int year = this.serialDate.getYYYY();
        int month = this.serialDate.getMonth();
        int day = this.serialDate.getDayOfMonth();
        calendar.clear();
        calendar.set(year, month - 1, day, 23, 59, 59);
        calendar.set(14, Millisecond.LAST_MILLISECOND_IN_SECOND);
        return calendar.getTimeInMillis();
    }

    public boolean equals(Object obj) {
        if (obj == this) {
            return true;
        }
        if (!(obj instanceof Day)) {
            return false;
        }
        if (this.serialDate.equals(((Day) obj).getSerialDate())) {
            return true;
        }
        return false;
    }

    public int hashCode() {
        return this.serialDate.hashCode();
    }

    public int compareTo(Object o1) {
        if (o1 instanceof Day) {
            return -((Day) o1).getSerialDate().compare(this.serialDate);
        }
        if (o1 instanceof RegularTimePeriod) {
            return 0;
        }
        return 1;
    }

    public String toString() {
        return this.serialDate.toString();
    }

    public static Day parseDay(String s) {
        try {
            return new Day(DATE_FORMAT.parse(s));
        } catch (ParseException e) {
            try {
                return new Day(DATE_FORMAT_SHORT.parse(s));
            } catch (ParseException e2) {
                return null;
            }
        }
    }
}
