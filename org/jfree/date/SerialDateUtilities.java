package org.jfree.date;

import java.text.DateFormatSymbols;

public class SerialDateUtilities {
    private DateFormatSymbols dateFormatSymbols;
    private String[] months;
    private String[] weekdays;

    public SerialDateUtilities() {
        this.dateFormatSymbols = new DateFormatSymbols();
        this.weekdays = this.dateFormatSymbols.getWeekdays();
        this.months = this.dateFormatSymbols.getMonths();
    }

    public String[] getWeekdays() {
        return this.weekdays;
    }

    public String[] getMonths() {
        return this.months;
    }

    public int stringToWeekday(String s) {
        if (s.equals(this.weekdays[7])) {
            return 7;
        }
        if (s.equals(this.weekdays[1])) {
            return 1;
        }
        if (s.equals(this.weekdays[2])) {
            return 2;
        }
        if (s.equals(this.weekdays[3])) {
            return 3;
        }
        if (s.equals(this.weekdays[4])) {
            return 4;
        }
        if (s.equals(this.weekdays[5])) {
            return 5;
        }
        return 6;
    }

    public static int dayCountActual(SerialDate start, SerialDate end) {
        return end.compare(start);
    }

    public static int dayCount30(SerialDate start, SerialDate end) {
        if (!start.isBefore(end)) {
            return -dayCount30(end, start);
        }
        int d1 = start.getDayOfMonth();
        int m1 = start.getMonth();
        int y1 = start.getYYYY();
        int d2 = end.getDayOfMonth();
        return (((end.getYYYY() - y1) * 360) + ((end.getMonth() - m1) * 30)) + (d2 - d1);
    }

    public static int dayCount30ISDA(SerialDate start, SerialDate end) {
        if (start.isBefore(end)) {
            int d1 = start.getDayOfMonth();
            int m1 = start.getMonth();
            int y1 = start.getYYYY();
            if (d1 == 31) {
                d1 = 30;
            }
            int d2 = end.getDayOfMonth();
            int m2 = end.getMonth();
            int y2 = end.getYYYY();
            if (d2 == 31 && d1 == 30) {
                d2 = 30;
            }
            return (((y2 - y1) * 360) + ((m2 - m1) * 30)) + (d2 - d1);
        } else if (start.isAfter(end)) {
            return -dayCount30ISDA(end, start);
        } else {
            return 0;
        }
    }

    public static int dayCount30PSA(SerialDate start, SerialDate end) {
        if (!start.isOnOrBefore(end)) {
            return -dayCount30PSA(end, start);
        }
        int d1 = start.getDayOfMonth();
        int m1 = start.getMonth();
        int y1 = start.getYYYY();
        if (isLastDayOfFebruary(start)) {
            d1 = 30;
        }
        if (d1 == 31 || isLastDayOfFebruary(start)) {
            d1 = 30;
        }
        int d2 = end.getDayOfMonth();
        int m2 = end.getMonth();
        int y2 = end.getYYYY();
        if (d2 == 31 && d1 == 30) {
            d2 = 30;
        }
        return (((y2 - y1) * 360) + ((m2 - m1) * 30)) + (d2 - d1);
    }

    public static int dayCount30E(SerialDate start, SerialDate end) {
        if (start.isBefore(end)) {
            int d1 = start.getDayOfMonth();
            int m1 = start.getMonth();
            int y1 = start.getYYYY();
            if (d1 == 31) {
                d1 = 30;
            }
            int d2 = end.getDayOfMonth();
            int m2 = end.getMonth();
            int y2 = end.getYYYY();
            if (d2 == 31) {
                d2 = 30;
            }
            return (((y2 - y1) * 360) + ((m2 - m1) * 30)) + (d2 - d1);
        } else if (start.isAfter(end)) {
            return -dayCount30E(end, start);
        } else {
            return 0;
        }
    }

    public static boolean isLastDayOfFebruary(SerialDate d) {
        if (d.getMonth() != 2) {
            return false;
        }
        int dom = d.getDayOfMonth();
        if (SerialDate.isLeapYear(d.getYYYY())) {
            if (dom == 29) {
                return true;
            }
            return false;
        } else if (dom != 28) {
            return false;
        } else {
            return true;
        }
    }

    public static int countFeb29s(SerialDate start, SerialDate end) {
        int count = 0;
        if (!start.isBefore(end)) {
            return countFeb29s(end, start);
        }
        int y1 = start.getYYYY();
        int y2 = end.getYYYY();
        int year = y1;
        while (year == y2) {
            if (SerialDate.isLeapYear(year) && SerialDate.createInstance(29, 2, year).isInRange(start, end, 2)) {
                count++;
            }
            year++;
        }
        return count;
    }
}
