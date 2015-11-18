package org.jfree.date;

import java.io.Serializable;
import java.text.DateFormatSymbols;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.GregorianCalendar;
import org.jfree.ui.Align;
import org.jfree.util.AbstractObjectList;

public abstract class SerialDate implements Comparable, Serializable, MonthConstants {
    static final int[] AGGREGATE_DAYS_TO_END_OF_MONTH;
    static final int[] AGGREGATE_DAYS_TO_END_OF_PRECEDING_MONTH;
    public static final DateFormatSymbols DATE_FORMAT_SYMBOLS;
    public static final int FIRST_WEEK_IN_MONTH = 1;
    public static final int FOLLOWING = 1;
    public static final int FOURTH_WEEK_IN_MONTH = 4;
    public static final int FRIDAY = 6;
    public static final int INCLUDE_BOTH = 3;
    public static final int INCLUDE_FIRST = 1;
    public static final int INCLUDE_NONE = 0;
    public static final int INCLUDE_SECOND = 2;
    static final int[] LAST_DAY_OF_MONTH;
    public static final int LAST_WEEK_IN_MONTH = 0;
    static final int[] LEAP_YEAR_AGGREGATE_DAYS_TO_END_OF_MONTH;
    static final int[] LEAP_YEAR_AGGREGATE_DAYS_TO_END_OF_PRECEDING_MONTH;
    public static final int MAXIMUM_YEAR_SUPPORTED = 9999;
    public static final int MINIMUM_YEAR_SUPPORTED = 1900;
    public static final int MONDAY = 2;
    public static final int NEAREST = 0;
    public static final int PRECEDING = -1;
    public static final int SATURDAY = 7;
    public static final int SECOND_WEEK_IN_MONTH = 2;
    public static final int SERIAL_LOWER_BOUND = 2;
    public static final int SERIAL_UPPER_BOUND = 2958465;
    public static final int SUNDAY = 1;
    public static final int THIRD_WEEK_IN_MONTH = 3;
    public static final int THURSDAY = 5;
    public static final int TUESDAY = 3;
    public static final int WEDNESDAY = 4;
    private static final long serialVersionUID = -293716040467423637L;
    private String description;

    public abstract int compare(SerialDate serialDate);

    public abstract int getDayOfMonth();

    public abstract int getDayOfWeek();

    public abstract int getMonth();

    public abstract int getYYYY();

    public abstract boolean isAfter(SerialDate serialDate);

    public abstract boolean isBefore(SerialDate serialDate);

    public abstract boolean isInRange(SerialDate serialDate, SerialDate serialDate2);

    public abstract boolean isInRange(SerialDate serialDate, SerialDate serialDate2, int i);

    public abstract boolean isOn(SerialDate serialDate);

    public abstract boolean isOnOrAfter(SerialDate serialDate);

    public abstract boolean isOnOrBefore(SerialDate serialDate);

    public abstract Date toDate();

    public abstract int toSerial();

    static {
        DATE_FORMAT_SYMBOLS = new SimpleDateFormat().getDateFormatSymbols();
        LAST_DAY_OF_MONTH = new int[]{NEAREST, 31, 28, 31, 30, 31, 30, 31, 31, 30, 31, 30, 31};
        AGGREGATE_DAYS_TO_END_OF_MONTH = new int[]{NEAREST, 31, 59, 90, 120, 151, 181, 212, 243, 273, 304, 334, 365};
        AGGREGATE_DAYS_TO_END_OF_PRECEDING_MONTH = new int[]{NEAREST, NEAREST, 31, 59, 90, 120, 151, 181, 212, 243, 273, 304, 334, 365};
        LEAP_YEAR_AGGREGATE_DAYS_TO_END_OF_MONTH = new int[]{NEAREST, 31, 60, 91, 121, 152, 182, 213, 244, 274, 305, 335, 366};
        LEAP_YEAR_AGGREGATE_DAYS_TO_END_OF_PRECEDING_MONTH = new int[]{NEAREST, NEAREST, 31, 60, 91, 121, 152, 182, 213, 244, 274, 305, 335, 366};
    }

    protected SerialDate() {
    }

    public static boolean isValidWeekdayCode(int code) {
        switch (code) {
            case SUNDAY /*1*/:
            case SERIAL_LOWER_BOUND /*2*/:
            case TUESDAY /*3*/:
            case WEDNESDAY /*4*/:
            case THURSDAY /*5*/:
            case FRIDAY /*6*/:
            case SATURDAY /*7*/:
                return true;
            default:
                return false;
        }
    }

    public static int stringToWeekdayCode(String s) {
        String[] shortWeekdayNames = DATE_FORMAT_SYMBOLS.getShortWeekdays();
        String[] weekDayNames = DATE_FORMAT_SYMBOLS.getWeekdays();
        s = s.trim();
        for (int i = NEAREST; i < weekDayNames.length; i += SUNDAY) {
            if (s.equals(shortWeekdayNames[i])) {
                return i;
            }
            if (s.equals(weekDayNames[i])) {
                return i;
            }
        }
        return PRECEDING;
    }

    public static String weekdayCodeToString(int weekday) {
        return DATE_FORMAT_SYMBOLS.getWeekdays()[weekday];
    }

    public static String[] getMonths() {
        return getMonths(false);
    }

    public static String[] getMonths(boolean shortened) {
        if (shortened) {
            return DATE_FORMAT_SYMBOLS.getShortMonths();
        }
        return DATE_FORMAT_SYMBOLS.getMonths();
    }

    public static boolean isValidMonthCode(int code) {
        switch (code) {
            case SUNDAY /*1*/:
            case SERIAL_LOWER_BOUND /*2*/:
            case TUESDAY /*3*/:
            case WEDNESDAY /*4*/:
            case THURSDAY /*5*/:
            case FRIDAY /*6*/:
            case SATURDAY /*7*/:
            case AbstractObjectList.DEFAULT_INITIAL_CAPACITY /*8*/:
            case Align.TOP_RIGHT /*9*/:
            case Align.SOUTH_EAST /*10*/:
            case MonthConstants.NOVEMBER /*11*/:
            case Align.FIT_HORIZONTAL /*12*/:
                return true;
            default:
                return false;
        }
    }

    public static int monthCodeToQuarter(int code) {
        switch (code) {
            case SUNDAY /*1*/:
            case SERIAL_LOWER_BOUND /*2*/:
            case TUESDAY /*3*/:
                return SUNDAY;
            case WEDNESDAY /*4*/:
            case THURSDAY /*5*/:
            case FRIDAY /*6*/:
                return SERIAL_LOWER_BOUND;
            case SATURDAY /*7*/:
            case AbstractObjectList.DEFAULT_INITIAL_CAPACITY /*8*/:
            case Align.TOP_RIGHT /*9*/:
                return TUESDAY;
            case Align.SOUTH_EAST /*10*/:
            case MonthConstants.NOVEMBER /*11*/:
            case Align.FIT_HORIZONTAL /*12*/:
                return WEDNESDAY;
            default:
                throw new IllegalArgumentException("SerialDate.monthCodeToQuarter: invalid month code.");
        }
    }

    public static String monthCodeToString(int month) {
        return monthCodeToString(month, false);
    }

    public static String monthCodeToString(int month, boolean shortened) {
        if (isValidMonthCode(month)) {
            String[] months;
            if (shortened) {
                months = DATE_FORMAT_SYMBOLS.getShortMonths();
            } else {
                months = DATE_FORMAT_SYMBOLS.getMonths();
            }
            return months[month + PRECEDING];
        }
        throw new IllegalArgumentException("SerialDate.monthCodeToString: month outside valid range.");
    }

    public static int stringToMonthCode(String s) {
        String[] shortMonthNames = DATE_FORMAT_SYMBOLS.getShortMonths();
        String[] monthNames = DATE_FORMAT_SYMBOLS.getMonths();
        int result = PRECEDING;
        s = s.trim();
        try {
            result = Integer.parseInt(s);
        } catch (NumberFormatException e) {
        }
        if (result >= SUNDAY && result <= 12) {
            return result;
        }
        for (int i = NEAREST; i < monthNames.length; i += SUNDAY) {
            if (s.equals(shortMonthNames[i])) {
                return i + SUNDAY;
            }
            if (s.equals(monthNames[i])) {
                return i + SUNDAY;
            }
        }
        return result;
    }

    public static boolean isValidWeekInMonthCode(int code) {
        switch (code) {
            case NEAREST /*0*/:
            case SUNDAY /*1*/:
            case SERIAL_LOWER_BOUND /*2*/:
            case TUESDAY /*3*/:
            case WEDNESDAY /*4*/:
                return true;
            default:
                return false;
        }
    }

    public static boolean isLeapYear(int yyyy) {
        if (yyyy % WEDNESDAY != 0) {
            return false;
        }
        if (yyyy % 400 == 0) {
            return true;
        }
        if (yyyy % 100 != 0) {
            return true;
        }
        return false;
    }

    public static int leapYearCount(int yyyy) {
        return (((yyyy - 1896) / WEDNESDAY) - ((yyyy - 1800) / 100)) + ((yyyy - 1600) / 400);
    }

    public static int lastDayOfMonth(int month, int yyyy) {
        int result = LAST_DAY_OF_MONTH[month];
        if (month == SERIAL_LOWER_BOUND && isLeapYear(yyyy)) {
            return result + SUNDAY;
        }
        return result;
    }

    public static SerialDate addDays(int days, SerialDate base) {
        return createInstance(base.toSerial() + days);
    }

    public static SerialDate addMonths(int months, SerialDate base) {
        int yy = ((((base.getYYYY() * 12) + base.getMonth()) + months) + PRECEDING) / 12;
        int mm = (((((base.getYYYY() * 12) + base.getMonth()) + months) + PRECEDING) % 12) + SUNDAY;
        return createInstance(Math.min(base.getDayOfMonth(), lastDayOfMonth(mm, yy)), mm, yy);
    }

    public static SerialDate addYears(int years, SerialDate base) {
        int baseY = base.getYYYY();
        int baseM = base.getMonth();
        int targetY = baseY + years;
        return createInstance(Math.min(base.getDayOfMonth(), lastDayOfMonth(baseM, targetY)), baseM, targetY);
    }

    public static SerialDate getPreviousDayOfWeek(int targetWeekday, SerialDate base) {
        if (isValidWeekdayCode(targetWeekday)) {
            int adjust;
            int baseDOW = base.getDayOfWeek();
            if (baseDOW > targetWeekday) {
                adjust = Math.min(NEAREST, targetWeekday - baseDOW);
            } else {
                adjust = Math.max(NEAREST, targetWeekday - baseDOW) - 7;
            }
            return addDays(adjust, base);
        }
        throw new IllegalArgumentException("Invalid day-of-the-week code.");
    }

    public static SerialDate getFollowingDayOfWeek(int targetWeekday, SerialDate base) {
        if (isValidWeekdayCode(targetWeekday)) {
            int adjust;
            int baseDOW = base.getDayOfWeek();
            if (baseDOW > targetWeekday) {
                adjust = Math.min(NEAREST, targetWeekday - baseDOW) + SATURDAY;
            } else {
                adjust = Math.max(NEAREST, targetWeekday - baseDOW);
            }
            return addDays(adjust, base);
        }
        throw new IllegalArgumentException("Invalid day-of-the-week code.");
    }

    public static SerialDate getNearestDayOfWeek(int targetDOW, SerialDate base) {
        if (isValidWeekdayCode(targetDOW)) {
            int adjust = -Math.abs(targetDOW - base.getDayOfWeek());
            if (adjust >= WEDNESDAY) {
                adjust = 7 - adjust;
            }
            if (adjust <= -4) {
                adjust += SATURDAY;
            }
            return addDays(adjust, base);
        }
        throw new IllegalArgumentException("Invalid day-of-the-week code.");
    }

    public SerialDate getEndOfCurrentMonth(SerialDate base) {
        return createInstance(lastDayOfMonth(base.getMonth(), base.getYYYY()), base.getMonth(), base.getYYYY());
    }

    public static String weekInMonthToString(int count) {
        switch (count) {
            case NEAREST /*0*/:
                return "Last";
            case SUNDAY /*1*/:
                return "First";
            case SERIAL_LOWER_BOUND /*2*/:
                return "Second";
            case TUESDAY /*3*/:
                return "Third";
            case WEDNESDAY /*4*/:
                return "Fourth";
            default:
                return "SerialDate.weekInMonthToString(): invalid code.";
        }
    }

    public static String relativeToString(int relative) {
        switch (relative) {
            case PRECEDING /*-1*/:
                return "Preceding";
            case NEAREST /*0*/:
                return "Nearest";
            case SUNDAY /*1*/:
                return "Following";
            default:
                return "ERROR : Relative To String";
        }
    }

    public static SerialDate createInstance(int day, int month, int yyyy) {
        return new SpreadsheetDate(day, month, yyyy);
    }

    public static SerialDate createInstance(int serial) {
        return new SpreadsheetDate(serial);
    }

    public static SerialDate createInstance(Date date) {
        GregorianCalendar calendar = new GregorianCalendar();
        calendar.setTime(date);
        return new SpreadsheetDate(calendar.get(THURSDAY), calendar.get(SERIAL_LOWER_BOUND) + SUNDAY, calendar.get(SUNDAY));
    }

    public String getDescription() {
        return this.description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String toString() {
        return getDayOfMonth() + "-" + monthCodeToString(getMonth()) + "-" + getYYYY();
    }

    public SerialDate getPreviousDayOfWeek(int targetDOW) {
        return getPreviousDayOfWeek(targetDOW, this);
    }

    public SerialDate getFollowingDayOfWeek(int targetDOW) {
        return getFollowingDayOfWeek(targetDOW, this);
    }

    public SerialDate getNearestDayOfWeek(int targetDOW) {
        return getNearestDayOfWeek(targetDOW, this);
    }
}
