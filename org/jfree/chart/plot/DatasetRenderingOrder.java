package org.jfree.chart.plot;

import java.io.ObjectStreamException;
import java.io.Serializable;

public final class DatasetRenderingOrder implements Serializable {
    public static final DatasetRenderingOrder FORWARD;
    public static final DatasetRenderingOrder REVERSE;
    private static final long serialVersionUID = -600593412366385072L;
    private String name;

    static {
        FORWARD = new DatasetRenderingOrder("DatasetRenderingOrder.FORWARD");
        REVERSE = new DatasetRenderingOrder("DatasetRenderingOrder.REVERSE");
    }

    private DatasetRenderingOrder(String name) {
        this.name = name;
    }

    public String toString() {
        return this.name;
    }

    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (!(obj instanceof DatasetRenderingOrder)) {
            return false;
        }
        if (this.name.equals(((DatasetRenderingOrder) obj).toString())) {
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
