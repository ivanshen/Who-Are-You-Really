package org.jfree.data.time;

import java.io.Serializable;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;
import org.jfree.date.SerialDate;

public class Quarter extends RegularTimePeriod implements Serializable {
    public static final int[] FIRST_MONTH_IN_QUARTER;
    public static final int FIRST_QUARTER = 1;
    public static final int[] LAST_MONTH_IN_QUARTER;
    public static final int LAST_QUARTER = 4;
    private static final long serialVersionUID = 3810061714380888671L;
    private long firstMillisecond;
    private long lastMillisecond;
    private byte quarter;
    private short year;

    static {
        FIRST_MONTH_IN_QUARTER = new int[]{0, FIRST_QUARTER, LAST_QUARTER, 7, 10};
        LAST_MONTH_IN_QUARTER = new int[]{0, 3, 6, 9, 12};
    }

    public Quarter() {
        this(new Date());
    }

    public Quarter(int quarter, int year) {
        if (quarter < FIRST_QUARTER || quarter > LAST_QUARTER) {
            throw new IllegalArgumentException("Quarter outside valid range.");
        }
        this.year = (short) year;
        this.quarter = (byte) quarter;
        peg(Calendar.getInstance());
    }

    public Quarter(int quarter, Year year) {
        if (quarter < FIRST_QUARTER || quarter > LAST_QUARTER) {
            throw new IllegalArgumentException("Quarter outside valid range.");
        }
        this.year = (short) year.getYear();
        this.quarter = (byte) quarter;
        peg(Calendar.getInstance());
    }

    public Quarter(Date time) {
        this(time, TimeZone.getDefault());
    }

    public Quarter(Date time, TimeZone zone) {
        this(time, zone, Locale.getDefault());
    }

    public Quarter(Date time, TimeZone zone, Locale locale) {
        Calendar calendar = Calendar.getInstance(zone, locale);
        calendar.setTime(time);
        this.quarter = (byte) SerialDate.monthCodeToQuarter(calendar.get(2) + FIRST_QUARTER);
        this.year = (short) calendar.get(FIRST_QUARTER);
        peg(calendar);
    }

    public int getQuarter() {
        return this.quarter;
    }

    public Year getYear() {
        return new Year(this.year);
    }

    public int getYearValue() {
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
        if (this.quarter > FIRST_QUARTER) {
            return new Quarter(this.quarter - 1, this.year);
        }
        if (this.year > (short) 1900) {
            return new Quarter((int) LAST_QUARTER, this.year - 1);
        }
        return null;
    }

    public RegularTimePeriod next() {
        if (this.quarter < LAST_QUARTER) {
            return new Quarter(this.quarter + FIRST_QUARTER, this.year);
        }
        if (this.year < (short) 9999) {
            return new Quarter((int) FIRST_QUARTER, this.year + FIRST_QUARTER);
        }
        return null;
    }

    public long getSerialIndex() {
        return (((long) this.year) * 4) + ((long) this.quarter);
    }

    public boolean equals(Object obj) {
        if (obj == null || !(obj instanceof Quarter)) {
            return false;
        }
        Quarter target = (Quarter) obj;
        if (this.quarter == target.getQuarter() && this.year == target.getYearValue()) {
            return true;
        }
        return false;
    }

    public int hashCode() {
        return ((this.quarter + 629) * 37) + this.year;
    }

    public int compareTo(Object o1) {
        if (o1 instanceof Quarter) {
            Quarter q = (Quarter) o1;
            int result = this.year - q.getYearValue();
            return result == 0 ? this.quarter - q.getQuarter() : result;
        } else if (o1 instanceof RegularTimePeriod) {
            return 0;
        } else {
            return FIRST_QUARTER;
        }
    }

    public String toString() {
        return "Q" + this.quarter + "/" + this.year;
    }

    public long getFirstMillisecond(Calendar calendar) {
        Calendar calendar2 = calendar;
        calendar2.set(this.year, FIRST_MONTH_IN_QUARTER[this.quarter] - 1, FIRST_QUARTER, 0, 0, 0);
        calendar.set(14, 0);
        return calendar.getTimeInMillis();
    }

    public long getLastMillisecond(Calendar calendar) {
        int month = LAST_MONTH_IN_QUARTER[this.quarter];
        Calendar calendar2 = calendar;
        calendar2.set(this.year, month - 1, SerialDate.lastDayOfMonth(month, this.year), 23, 59, 59);
        calendar.set(14, Millisecond.LAST_MILLISECOND_IN_SECOND);
        return calendar.getTimeInMillis();
    }

    public static Quarter parseQuarter(String s) {
        int i = s.indexOf("Q");
        if (i == -1) {
            throw new TimePeriodFormatException("Missing Q.");
        } else if (i != s.length() - 1) {
            return new Quarter(Integer.parseInt(s.substring(i + FIRST_QUARTER, i + 2)), Year.parseYear((s.substring(0, i) + s.substring(i + 2, s.length())).replace('/', ' ').replace(',', ' ').replace('-', ' ').trim()));
        } else {
            throw new TimePeriodFormatException("Q found at end of string.");
        }
    }
}
