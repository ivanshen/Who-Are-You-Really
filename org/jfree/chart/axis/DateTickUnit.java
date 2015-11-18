package org.jfree.chart.axis;

import java.io.Serializable;
import java.text.DateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;
import org.jfree.chart.util.ParamChecks;
import org.jfree.util.ObjectUtilities;

public class DateTickUnit extends TickUnit implements Serializable {
    public static final int DAY = 2;
    public static final int HOUR = 3;
    public static final int MILLISECOND = 6;
    public static final int MINUTE = 4;
    public static final int MONTH = 1;
    public static final int SECOND = 5;
    public static final int YEAR = 0;
    private static final long serialVersionUID = -7289292157229621901L;
    private int count;
    private DateFormat formatter;
    private int rollCount;
    private int rollUnit;
    private DateTickUnitType rollUnitType;
    private int unit;
    private DateTickUnitType unitType;

    public DateTickUnit(DateTickUnitType unitType, int multiple) {
        this(unitType, multiple, DateFormat.getDateInstance(HOUR));
    }

    public DateTickUnit(DateTickUnitType unitType, int multiple, DateFormat formatter) {
        this(unitType, multiple, unitType, multiple, formatter);
    }

    public DateTickUnit(DateTickUnitType unitType, int multiple, DateTickUnitType rollUnitType, int rollMultiple, DateFormat formatter) {
        super((double) getMillisecondCount(unitType, multiple));
        ParamChecks.nullNotPermitted(formatter, "formatter");
        if (multiple <= 0) {
            throw new IllegalArgumentException("Requires 'multiple' > 0.");
        } else if (rollMultiple <= 0) {
            throw new IllegalArgumentException("Requires 'rollMultiple' > 0.");
        } else {
            this.unitType = unitType;
            this.count = multiple;
            this.rollUnitType = rollUnitType;
            this.rollCount = rollMultiple;
            this.formatter = formatter;
            this.unit = unitTypeToInt(unitType);
            this.rollUnit = unitTypeToInt(rollUnitType);
        }
    }

    public DateTickUnitType getUnitType() {
        return this.unitType;
    }

    public int getMultiple() {
        return this.count;
    }

    public DateTickUnitType getRollUnitType() {
        return this.rollUnitType;
    }

    public int getRollMultiple() {
        return this.rollCount;
    }

    public String valueToString(double milliseconds) {
        return this.formatter.format(new Date((long) milliseconds));
    }

    public String dateToString(Date date) {
        return this.formatter.format(date);
    }

    public Date addToDate(Date base, TimeZone zone) {
        Calendar calendar = Calendar.getInstance(zone);
        calendar.setTime(base);
        calendar.add(this.unitType.getCalendarField(), this.count);
        return calendar.getTime();
    }

    public Date rollDate(Date base) {
        return rollDate(base, TimeZone.getDefault());
    }

    public Date rollDate(Date base, TimeZone zone) {
        Calendar calendar = Calendar.getInstance(zone);
        calendar.setTime(base);
        calendar.add(this.rollUnitType.getCalendarField(), this.rollCount);
        return calendar.getTime();
    }

    public int getCalendarField() {
        return this.unitType.getCalendarField();
    }

    private static long getMillisecondCount(DateTickUnitType unit, int count) {
        if (unit.equals(DateTickUnitType.YEAR)) {
            return 31536000000L * ((long) count);
        }
        if (unit.equals(DateTickUnitType.MONTH)) {
            return 2678400000L * ((long) count);
        }
        if (unit.equals(DateTickUnitType.DAY)) {
            return SegmentedTimeline.DAY_SEGMENT_SIZE * ((long) count);
        }
        if (unit.equals(DateTickUnitType.HOUR)) {
            return SegmentedTimeline.HOUR_SEGMENT_SIZE * ((long) count);
        }
        if (unit.equals(DateTickUnitType.MINUTE)) {
            return SegmentedTimeline.MINUTE_SEGMENT_SIZE * ((long) count);
        }
        if (unit.equals(DateTickUnitType.SECOND)) {
            return 1000 * ((long) count);
        }
        if (unit.equals(DateTickUnitType.MILLISECOND)) {
            return (long) count;
        }
        throw new IllegalArgumentException("The 'unit' argument has a value that is not recognised.");
    }

    private static DateTickUnitType intToUnitType(int unit) {
        switch (unit) {
            case YEAR /*0*/:
                return DateTickUnitType.YEAR;
            case MONTH /*1*/:
                return DateTickUnitType.MONTH;
            case DAY /*2*/:
                return DateTickUnitType.DAY;
            case HOUR /*3*/:
                return DateTickUnitType.HOUR;
            case MINUTE /*4*/:
                return DateTickUnitType.MINUTE;
            case SECOND /*5*/:
                return DateTickUnitType.SECOND;
            case MILLISECOND /*6*/:
                return DateTickUnitType.MILLISECOND;
            default:
                throw new IllegalArgumentException("Unrecognised 'unit' value " + unit + ".");
        }
    }

    private static int unitTypeToInt(DateTickUnitType unitType) {
        ParamChecks.nullNotPermitted(unitType, "unitType");
        if (unitType.equals(DateTickUnitType.YEAR)) {
            return YEAR;
        }
        if (unitType.equals(DateTickUnitType.MONTH)) {
            return MONTH;
        }
        if (unitType.equals(DateTickUnitType.DAY)) {
            return DAY;
        }
        if (unitType.equals(DateTickUnitType.HOUR)) {
            return HOUR;
        }
        if (unitType.equals(DateTickUnitType.MINUTE)) {
            return MINUTE;
        }
        if (unitType.equals(DateTickUnitType.SECOND)) {
            return SECOND;
        }
        if (unitType.equals(DateTickUnitType.MILLISECOND)) {
            return MILLISECOND;
        }
        throw new IllegalArgumentException("The 'unitType' is not recognised");
    }

    private static DateFormat notNull(DateFormat formatter) {
        if (formatter == null) {
            return DateFormat.getDateInstance(HOUR);
        }
        return formatter;
    }

    public boolean equals(Object obj) {
        if (obj == this) {
            return true;
        }
        if (!(obj instanceof DateTickUnit)) {
            return false;
        }
        if (!super.equals(obj)) {
            return false;
        }
        DateTickUnit that = (DateTickUnit) obj;
        if (!this.unitType.equals(that.unitType)) {
            return false;
        }
        if (this.count != that.count) {
            return false;
        }
        if (ObjectUtilities.equal(this.formatter, that.formatter)) {
            return true;
        }
        return false;
    }

    public int hashCode() {
        return ((((this.unitType.hashCode() + 703) * 37) + this.count) * 37) + this.formatter.hashCode();
    }

    public String toString() {
        return "DateTickUnit[" + this.unitType.toString() + ", " + this.count + "]";
    }

    public DateTickUnit(int unit, int count, DateFormat formatter) {
        this(unit, count, unit, count, formatter);
    }

    public DateTickUnit(int unit, int count) {
        this(unit, count, null);
    }

    public DateTickUnit(int unit, int count, int rollUnit, int rollCount, DateFormat formatter) {
        this(intToUnitType(unit), count, intToUnitType(rollUnit), rollCount, notNull(formatter));
    }

    public int getUnit() {
        return this.unit;
    }

    public int getCount() {
        return this.count;
    }

    public int getRollUnit() {
        return this.rollUnit;
    }

    public int getRollCount() {
        return this.rollCount;
    }

    public Date addToDate(Date base) {
        return addToDate(base, TimeZone.getDefault());
    }
}
