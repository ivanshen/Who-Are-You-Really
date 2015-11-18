package org.jfree.chart.plot;

import java.io.ObjectStreamException;
import java.io.Serializable;

public final class PlotOrientation implements Serializable {
    public static final PlotOrientation HORIZONTAL;
    public static final PlotOrientation VERTICAL;
    private static final long serialVersionUID = -2508771828190337782L;
    private String name;

    static {
        HORIZONTAL = new PlotOrientation("PlotOrientation.HORIZONTAL");
        VERTICAL = new PlotOrientation("PlotOrientation.VERTICAL");
    }

    private PlotOrientation(String name) {
        this.name = name;
    }

    public boolean isHorizontal() {
        return equals(HORIZONTAL);
    }

    public boolean isVertical() {
        return equals(VERTICAL);
    }

    public String toString() {
        return this.name;
    }

    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (!(obj instanceof PlotOrientation)) {
            return false;
        }
        if (this.name.equals(((PlotOrientation) obj).toString())) {
            return true;
        }
        return false;
    }

    public int hashCode() {
        return this.name.hashCode();
    }

    private Object readResolve() throws ObjectStreamException {
        if (equals(HORIZONTAL)) {
            return HORIZONTAL;
        }
        if (equals(VERTICAL)) {
            return VERTICAL;
        }
        return null;
    }
}
