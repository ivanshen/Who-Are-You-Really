package org.jfree.chart.axis;

import java.io.ObjectStreamException;
import java.io.Serializable;

public final class AxisLabelLocation implements Serializable {
    public static final AxisLabelLocation HIGH_END;
    public static final AxisLabelLocation LOW_END;
    public static final AxisLabelLocation MIDDLE;
    private static final long serialVersionUID = 1;
    private String name;

    static {
        HIGH_END = new AxisLabelLocation("HIGH_END");
        MIDDLE = new AxisLabelLocation("MIDDLE");
        LOW_END = new AxisLabelLocation("LOW_END");
    }

    private AxisLabelLocation(String name) {
        this.name = name;
    }

    public String toString() {
        return this.name;
    }

    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (!(obj instanceof AxisLabelLocation)) {
            return false;
        }
        if (this.name.equals(((AxisLabelLocation) obj).toString())) {
            return true;
        }
        return false;
    }

    public int hashCode() {
        return this.name.hashCode() + 415;
    }

    private Object readResolve() throws ObjectStreamException {
        if (equals(HIGH_END)) {
            return HIGH_END;
        }
        if (equals(MIDDLE)) {
            return MIDDLE;
        }
        if (equals(LOW_END)) {
            return LOW_END;
        }
        return null;
    }
}
