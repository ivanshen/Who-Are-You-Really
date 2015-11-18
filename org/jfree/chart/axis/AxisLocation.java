package org.jfree.chart.axis;

import java.io.ObjectStreamException;
import java.io.Serializable;
import org.jfree.chart.util.ParamChecks;

public final class AxisLocation implements Serializable {
    public static final AxisLocation BOTTOM_OR_LEFT;
    public static final AxisLocation BOTTOM_OR_RIGHT;
    public static final AxisLocation TOP_OR_LEFT;
    public static final AxisLocation TOP_OR_RIGHT;
    private static final long serialVersionUID = -3276922179323563410L;
    private String name;

    static {
        TOP_OR_LEFT = new AxisLocation("AxisLocation.TOP_OR_LEFT");
        TOP_OR_RIGHT = new AxisLocation("AxisLocation.TOP_OR_RIGHT");
        BOTTOM_OR_LEFT = new AxisLocation("AxisLocation.BOTTOM_OR_LEFT");
        BOTTOM_OR_RIGHT = new AxisLocation("AxisLocation.BOTTOM_OR_RIGHT");
    }

    private AxisLocation(String name) {
        this.name = name;
    }

    public AxisLocation getOpposite() {
        return getOpposite(this);
    }

    public String toString() {
        return this.name;
    }

    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (!(obj instanceof AxisLocation)) {
            return false;
        }
        if (this.name.equals(((AxisLocation) obj).toString())) {
            return true;
        }
        return false;
    }

    public int hashCode() {
        return this.name.hashCode() + 415;
    }

    public static AxisLocation getOpposite(AxisLocation location) {
        ParamChecks.nullNotPermitted(location, "location");
        if (location == TOP_OR_LEFT) {
            return BOTTOM_OR_RIGHT;
        }
        if (location == TOP_OR_RIGHT) {
            return BOTTOM_OR_LEFT;
        }
        if (location == BOTTOM_OR_LEFT) {
            return TOP_OR_RIGHT;
        }
        if (location == BOTTOM_OR_RIGHT) {
            return TOP_OR_LEFT;
        }
        throw new IllegalStateException("AxisLocation not recognised.");
    }

    private Object readResolve() throws ObjectStreamException {
        if (equals(TOP_OR_RIGHT)) {
            return TOP_OR_RIGHT;
        }
        if (equals(BOTTOM_OR_RIGHT)) {
            return BOTTOM_OR_RIGHT;
        }
        if (equals(TOP_OR_LEFT)) {
            return TOP_OR_LEFT;
        }
        if (equals(BOTTOM_OR_LEFT)) {
            return BOTTOM_OR_LEFT;
        }
        return null;
    }
}
