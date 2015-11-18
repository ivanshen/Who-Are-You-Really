package org.jfree.chart.axis;

import java.io.ObjectStreamException;
import java.io.Serializable;

public class DateTickUnitType implements Serializable {
    public static final DateTickUnitType DAY;
    public static final DateTickUnitType HOUR;
    public static final DateTickUnitType MILLISECOND;
    public static final DateTickUnitType MINUTE;
    public static final DateTickUnitType MONTH;
    public static final DateTickUnitType SECOND;
    public static final DateTickUnitType YEAR;
    private int calendarField;
    private String name;

    static {
        YEAR = new DateTickUnitType("DateTickUnitType.YEAR", 1);
        MONTH = new DateTickUnitType("DateTickUnitType.MONTH", 2);
        DAY = new DateTickUnitType("DateTickUnitType.DAY", 5);
        HOUR = new DateTickUnitType("DateTickUnitType.HOUR", 11);
        MINUTE = new DateTickUnitType("DateTickUnitType.MINUTE", 12);
        SECOND = new DateTickUnitType("DateTickUnitType.SECOND", 13);
        MILLISECOND = new DateTickUnitType("DateTickUnitType.MILLISECOND", 14);
    }

    private DateTickUnitType(String name, int calendarField) {
        this.name = name;
        this.calendarField = calendarField;
    }

    public int getCalendarField() {
        return this.calendarField;
    }

    public String toString() {
        return this.name;
    }

    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (!(obj instanceof DateTickUnitType)) {
            return false;
        }
        if (this.name.equals(((DateTickUnitType) obj).toString())) {
            return true;
        }
        return false;
    }

    private Object readResolve() throws ObjectStreamException {
        if (equals(YEAR)) {
            return YEAR;
        }
        if (equals(MONTH)) {
            return MONTH;
        }
        if (equals(DAY)) {
            return DAY;
        }
        if (equals(HOUR)) {
            return HOUR;
        }
        if (equals(MINUTE)) {
            return MINUTE;
        }
        if (equals(SECOND)) {
            return SECOND;
        }
        if (equals(MILLISECOND)) {
            return MILLISECOND;
        }
        return null;
    }
}
