package org.jfree.date;

import java.util.Calendar;
import java.util.Date;

public class SpreadsheetDate extends SerialDate {
    private static final long serialVersionUID = -2039586705374454461L;
    private final int day;
    private final int month;
    private final int serial;
    private final int year;

    public SpreadsheetDate(int day, int month, int year) {
        if (year < SerialDate.MINIMUM_YEAR_SUPPORTED || year > SerialDate.MAXIMUM_YEAR_SUPPORTED) {
            throw new IllegalArgumentException("The 'year' argument must be in range 1900 to 9999.");
        }
        this.year = year;
        if (month < 1 || month > 12) {
            throw new IllegalArgumentException("The 'month' argument must be in the range 1 to 12.");
        }
        this.month = month;
        if (day < 1 || day > SerialDate.lastDayOfMonth(month, year)) {
            throw new IllegalArgumentException("Invalid 'day' argument.");
        }
        this.day = day;
        this.serial = calcSerial(day, month, year);
    }

    public SpreadsheetDate(int serial) {
        if (serial < 2 || serial > SerialDate.SERIAL_UPPER_BOUND) {
            throw new IllegalArgumentException("SpreadsheetDate: Serial must be in range 2 to 2958465.");
        }
        this.serial = serial;
        int days = this.serial - 2;
        int overestimatedYYYY = (days / 365) + SerialDate.MINIMUM_YEAR_SUPPORTED;
        int underestimatedYYYY = ((days - SerialDate.leapYearCount(overestimatedYYYY)) / 365) + SerialDate.MINIMUM_YEAR_SUPPORTED;
        if (underestimatedYYYY == overestimatedYYYY) {
            this.year = underestimatedYYYY;
        } else {
            int ss1 = calcSerial(1, 1, underestimatedYYYY);
            while (ss1 <= this.serial) {
                underestimatedYYYY++;
                ss1 = calcSerial(1, 1, underestimatedYYYY);
            }
            this.year = underestimatedYYYY - 1;
        }
        int ss2 = calcSerial(1, 1, this.year);
        int[] daysToEndOfPrecedingMonth = AGGREGATE_DAYS_TO_END_OF_PRECEDING_MONTH;
        if (SerialDate.isLeapYear(this.year)) {
            daysToEndOfPrecedingMonth = LEAP_YEAR_AGGREGATE_DAYS_TO_END_OF_PRECEDING_MONTH;
        }
        int mm = 1;
        int sss = (daysToEndOfPrecedingMonth[1] + ss2) - 1;
        while (sss < this.serial) {
            mm++;
            sss = (daysToEndOfPrecedingMonth[mm] + ss2) - 1;
        }
        this.month = mm - 1;
        this.day = ((this.serial - ss2) - daysToEndOfPrecedingMonth[this.month]) + 1;
    }

    public int toSerial() {
        return this.serial;
    }

    public Date toDate() {
        Calendar calendar = Calendar.getInstance();
        calendar.set(getYYYY(), getMonth() - 1, getDayOfMonth(), 0, 0, 0);
        return calendar.getTime();
    }

    public int getYYYY() {
        return this.year;
    }

    public int getMonth() {
        return this.month;
    }

    public int getDayOfMonth() {
        return this.day;
    }

    public int getDayOfWeek() {
        return ((this.serial + 6) % 7) + 1;
    }

    public boolean equals(Object object) {
        if ((object instanceof SerialDate) && ((SerialDate) object).toSerial() == toSerial()) {
            return true;
        }
        return false;
    }

    public int hashCode() {
        return toSerial();
    }

    public int compare(SerialDate other) {
        return this.serial - other.toSerial();
    }

    public int compareTo(Object other) {
        return compare((SerialDate) other);
    }

    public boolean isOn(SerialDate other) {
        return this.serial == other.toSerial();
    }

    public boolean isBefore(SerialDate other) {
        return this.serial < other.toSerial();
    }

    public boolean isOnOrBefore(SerialDate other) {
        return this.serial <= other.toSerial();
    }

    public boolean isAfter(SerialDate other) {
        return this.serial > other.toSerial();
    }

    public boolean isOnOrAfter(SerialDate other) {
        return this.serial >= other.toSerial();
    }

    public boolean isInRange(SerialDate d1, SerialDate d2) {
        return isInRange(d1, d2, 3);
    }

    public boolean isInRange(SerialDate d1, SerialDate d2, int include) {
        int s1 = d1.toSerial();
        int s2 = d2.toSerial();
        int start = Math.min(s1, s2);
        int end = Math.max(s1, s2);
        int s = toSerial();
        if (include == 3) {
            if (s < start || s > end) {
                return false;
            }
            return true;
        } else if (include == 1) {
            if (s < start || s >= end) {
                return false;
            }
            return true;
        } else if (include == 2) {
            if (s <= start || s > end) {
                return false;
            }
            return true;
        } else if (s <= start || s >= end) {
            return false;
        } else {
            return true;
        }
    }

    private int calcSerial(int d, int m, int y) {
        int yy = ((y - 1900) * 365) + SerialDate.leapYearCount(y - 1);
        int mm = SerialDate.AGGREGATE_DAYS_TO_END_OF_PRECEDING_MONTH[m];
        if (m > 2 && SerialDate.isLeapYear(y)) {
            mm++;
        }
        return ((yy + mm) + d) + 1;
    }
}
