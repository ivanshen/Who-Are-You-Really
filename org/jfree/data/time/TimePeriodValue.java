package org.jfree.data.time;

import java.io.Serializable;
import org.jfree.chart.util.ParamChecks;

public class TimePeriodValue implements Cloneable, Serializable {
    private static final long serialVersionUID = 3390443360845711275L;
    private TimePeriod period;
    private Number value;

    public TimePeriodValue(TimePeriod period, Number value) {
        ParamChecks.nullNotPermitted(period, "period");
        this.period = period;
        this.value = value;
    }

    public TimePeriodValue(TimePeriod period, double value) {
        this(period, new Double(value));
    }

    public TimePeriod getPeriod() {
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
        if (!(obj instanceof TimePeriodValue)) {
            return false;
        }
        TimePeriodValue timePeriodValue = (TimePeriodValue) obj;
        if (this.period == null ? timePeriodValue.period != null : !this.period.equals(timePeriodValue.period)) {
            return false;
        }
        if (this.value != null) {
            if (this.value.equals(timePeriodValue.value)) {
                return true;
            }
        } else if (timePeriodValue.value == null) {
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

    public Object clone() {
        try {
            return super.clone();
        } catch (CloneNotSupportedException e) {
            throw new RuntimeException(e);
        }
    }

    public String toString() {
        return "TimePeriodValue[" + getPeriod() + "," + getValue() + "]";
    }
}
