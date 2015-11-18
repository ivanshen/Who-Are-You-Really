package org.jfree.util;

import java.io.ObjectStreamException;
import java.io.Serializable;
import org.jfree.chart.plot.SpiderWebPlot;
import org.jfree.data.xy.NormalizedMatrixSeries;

public final class Rotation implements Serializable {
    public static final Rotation ANTICLOCKWISE;
    public static final Rotation CLOCKWISE;
    private static final long serialVersionUID = -4662815260201591676L;
    private double factor;
    private String name;

    static {
        CLOCKWISE = new Rotation("Rotation.CLOCKWISE", SpiderWebPlot.DEFAULT_MAX_VALUE);
        ANTICLOCKWISE = new Rotation("Rotation.ANTICLOCKWISE", NormalizedMatrixSeries.DEFAULT_SCALE_FACTOR);
    }

    private Rotation(String name, double factor) {
        this.name = name;
        this.factor = factor;
    }

    public String toString() {
        return this.name;
    }

    public double getFactor() {
        return this.factor;
    }

    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Rotation)) {
            return false;
        }
        if (this.factor != ((Rotation) o).factor) {
            return false;
        }
        return true;
    }

    public int hashCode() {
        long temp = Double.doubleToLongBits(this.factor);
        return (int) ((temp >>> 32) ^ temp);
    }

    private Object readResolve() throws ObjectStreamException {
        if (equals(CLOCKWISE)) {
            return CLOCKWISE;
        }
        if (equals(ANTICLOCKWISE)) {
            return ANTICLOCKWISE;
        }
        return null;
    }
}
