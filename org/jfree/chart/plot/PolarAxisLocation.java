package org.jfree.chart.plot;

import java.io.ObjectStreamException;
import java.io.Serializable;

public final class PolarAxisLocation implements Serializable {
    public static final PolarAxisLocation EAST_ABOVE;
    public static final PolarAxisLocation EAST_BELOW;
    public static final PolarAxisLocation NORTH_LEFT;
    public static final PolarAxisLocation NORTH_RIGHT;
    public static final PolarAxisLocation SOUTH_LEFT;
    public static final PolarAxisLocation SOUTH_RIGHT;
    public static final PolarAxisLocation WEST_ABOVE;
    public static final PolarAxisLocation WEST_BELOW;
    private static final long serialVersionUID = -3276922179323563410L;
    private String name;

    static {
        NORTH_LEFT = new PolarAxisLocation("PolarAxisLocation.NORTH_LEFT");
        NORTH_RIGHT = new PolarAxisLocation("PolarAxisLocation.NORTH_RIGHT");
        SOUTH_LEFT = new PolarAxisLocation("PolarAxisLocation.SOUTH_LEFT");
        SOUTH_RIGHT = new PolarAxisLocation("PolarAxisLocation.SOUTH_RIGHT");
        EAST_ABOVE = new PolarAxisLocation("PolarAxisLocation.EAST_ABOVE");
        EAST_BELOW = new PolarAxisLocation("PolarAxisLocation.EAST_BELOW");
        WEST_ABOVE = new PolarAxisLocation("PolarAxisLocation.WEST_ABOVE");
        WEST_BELOW = new PolarAxisLocation("PolarAxisLocation.WEST_BELOW");
    }

    private PolarAxisLocation(String name) {
        this.name = name;
    }

    public String toString() {
        return this.name;
    }

    public boolean equals(Object obj) {
        if (obj == this) {
            return true;
        }
        if (!(obj instanceof PolarAxisLocation)) {
            return false;
        }
        if (this.name.equals(((PolarAxisLocation) obj).toString())) {
            return true;
        }
        return false;
    }

    private Object readResolve() throws ObjectStreamException {
        if (equals(NORTH_RIGHT)) {
            return NORTH_RIGHT;
        }
        if (equals(NORTH_LEFT)) {
            return NORTH_LEFT;
        }
        if (equals(SOUTH_RIGHT)) {
            return SOUTH_RIGHT;
        }
        if (equals(SOUTH_LEFT)) {
            return SOUTH_LEFT;
        }
        if (equals(EAST_ABOVE)) {
            return EAST_ABOVE;
        }
        if (equals(EAST_BELOW)) {
            return EAST_BELOW;
        }
        if (equals(WEST_ABOVE)) {
            return WEST_ABOVE;
        }
        if (equals(WEST_BELOW)) {
            return WEST_BELOW;
        }
        return null;
    }
}
