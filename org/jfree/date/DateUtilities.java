package org.jfree.date;

import java.util.Calendar;
import java.util.Date;

public class DateUtilities {
    private static final Calendar CALENDAR;

    private DateUtilities() {
    }

    static {
        CALENDAR = Calendar.getInstance();
    }

    public static synchronized Date createDate(int yyyy, int month, int day) {
        Date time;
        synchronized (DateUtilities.class) {
            CALENDAR.clear();
            CALENDAR.set(yyyy, month - 1, day);
            time = CALENDAR.getTime();
        }
        return time;
    }

    public static synchronized Date createDate(int yyyy, int month, int day, int hour, int min) {
        Date time;
        synchronized (DateUtilities.class) {
            CALENDAR.clear();
            CALENDAR.set(yyyy, month - 1, day, hour, min);
            time = CALENDAR.getTime();
        }
        return time;
    }
}
