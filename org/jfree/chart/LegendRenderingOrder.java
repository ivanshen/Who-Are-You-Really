package org.jfree.chart;

import java.io.ObjectStreamException;
import java.io.Serializable;

public final class LegendRenderingOrder implements Serializable {
    public static final LegendRenderingOrder REVERSE;
    public static final LegendRenderingOrder STANDARD;
    private static final long serialVersionUID = -3832486612685808616L;
    private String name;

    static {
        STANDARD = new LegendRenderingOrder("LegendRenderingOrder.STANDARD");
        REVERSE = new LegendRenderingOrder("LegendRenderingOrder.REVERSE");
    }

    private LegendRenderingOrder(String name) {
        this.name = name;
    }

    public String toString() {
        return this.name;
    }

    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (!(obj instanceof LegendRenderingOrder)) {
            return false;
        }
        if (this.name.equals(((LegendRenderingOrder) obj).toString())) {
            return true;
        }
        return false;
    }

    private Object readResolve() throws ObjectStreamException {
        if (equals(STANDARD)) {
            return STANDARD;
        }
        if (equals(REVERSE)) {
            return REVERSE;
        }
        return null;
    }
}
