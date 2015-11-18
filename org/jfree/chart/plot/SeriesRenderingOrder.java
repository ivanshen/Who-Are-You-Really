package org.jfree.chart.plot;

import java.io.ObjectStreamException;
import java.io.Serializable;

public final class SeriesRenderingOrder implements Serializable {
    public static final SeriesRenderingOrder FORWARD;
    public static final SeriesRenderingOrder REVERSE;
    private static final long serialVersionUID = 209336477448807735L;
    private String name;

    static {
        FORWARD = new SeriesRenderingOrder("SeriesRenderingOrder.FORWARD");
        REVERSE = new SeriesRenderingOrder("SeriesRenderingOrder.REVERSE");
    }

    private SeriesRenderingOrder(String name) {
        this.name = name;
    }

    public String toString() {
        return this.name;
    }

    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (!(obj instanceof SeriesRenderingOrder)) {
            return false;
        }
        if (this.name.equals(((SeriesRenderingOrder) obj).toString())) {
            return true;
        }
        return false;
    }

    public int hashCode() {
        return this.name.hashCode();
    }

    private Object readResolve() throws ObjectStreamException {
        if (equals(FORWARD)) {
            return FORWARD;
        }
        if (equals(REVERSE)) {
            return REVERSE;
        }
        return null;
    }
}
