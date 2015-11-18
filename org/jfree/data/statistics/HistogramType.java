package org.jfree.data.statistics;

import java.io.ObjectStreamException;
import java.io.Serializable;

public class HistogramType implements Serializable {
    public static final HistogramType FREQUENCY;
    public static final HistogramType RELATIVE_FREQUENCY;
    public static final HistogramType SCALE_AREA_TO_1;
    private static final long serialVersionUID = 2618927186251997727L;
    private String name;

    static {
        FREQUENCY = new HistogramType("FREQUENCY");
        RELATIVE_FREQUENCY = new HistogramType("RELATIVE_FREQUENCY");
        SCALE_AREA_TO_1 = new HistogramType("SCALE_AREA_TO_1");
    }

    private HistogramType(String name) {
        this.name = name;
    }

    public String toString() {
        return this.name;
    }

    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (obj == this) {
            return true;
        }
        if (!(obj instanceof HistogramType)) {
            return false;
        }
        if (this.name.equals(((HistogramType) obj).name)) {
            return true;
        }
        return false;
    }

    public int hashCode() {
        return this.name.hashCode();
    }

    private Object readResolve() throws ObjectStreamException {
        if (equals(FREQUENCY)) {
            return FREQUENCY;
        }
        if (equals(RELATIVE_FREQUENCY)) {
            return RELATIVE_FREQUENCY;
        }
        if (equals(SCALE_AREA_TO_1)) {
            return SCALE_AREA_TO_1;
        }
        return null;
    }
}
