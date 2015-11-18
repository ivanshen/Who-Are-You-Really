package org.jfree.data.time;

import java.io.Serializable;
import org.jfree.chart.util.ParamChecks;
import org.jfree.util.ObjectUtilities;

public class TimeSeriesDataItem implements Cloneable, Comparable, Serializable {
    private static final long serialVersionUID = -2235346966016401302L;
    private RegularTimePeriod period;
    private Number value;

    public TimeSeriesDataItem(RegularTimePeriod period, Number value) {
        ParamChecks.nullNotPermitted(period, "period");
        this.period = period;
        this.value = value;
    }

    public TimeSeriesDataItem(RegularTimePeriod period, double value) {
        this(period, new Double(value));
    }

    public RegularTimePeriod getPeriod() {
        return this.period;
    }

    public Number getValue() {
        return this.value;
    }

    public void setValue(Number value) {
        this.value = value;
    }

    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (!(obj instanceof TimeSeriesDataItem)) {
            return false;
        }
        TimeSeriesDataItem that = (TimeSeriesDataItem) obj;
        if (!ObjectUtilities.equal(this.period, that.period)) {
            return false;
        }
        if (ObjectUtilities.equal(this.value, that.value)) {
            return true;
        }
        return false;
    }

    public int hashCode() {
        int result;
        int i = 0;
        if (this.period != null) {
            result = this.period.hashCode();
        } else {
            result = 0;
        }
        int i2 = result * 29;
        if (this.value != null) {
            i = this.value.hashCode();
        }
        return i2 + i;
    }

    public int compareTo(Object o1) {
        if (!(o1 instanceof TimeSeriesDataItem)) {
            return 1;
        }
        return getPeriod().compareTo(((TimeSeriesDataItem) o1).getPeriod());
    }

    public Object clone() {
        Object clone = null;
        try {
            clone = super.clone();
        } catch (CloneNotSupportedException e) {
            e.printStackTrace();
        }
        return clone;
    }
}
