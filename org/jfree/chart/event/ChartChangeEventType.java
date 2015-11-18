package org.jfree.chart.event;

import java.io.ObjectStreamException;
import java.io.Serializable;

public final class ChartChangeEventType implements Serializable {
    public static final ChartChangeEventType DATASET_UPDATED;
    public static final ChartChangeEventType GENERAL;
    public static final ChartChangeEventType NEW_DATASET;
    private static final long serialVersionUID = 5481917022435735602L;
    private String name;

    static {
        GENERAL = new ChartChangeEventType("ChartChangeEventType.GENERAL");
        NEW_DATASET = new ChartChangeEventType("ChartChangeEventType.NEW_DATASET");
        DATASET_UPDATED = new ChartChangeEventType("ChartChangeEventType.DATASET_UPDATED");
    }

    private ChartChangeEventType(String name) {
        this.name = name;
    }

    public String toString() {
        return this.name;
    }

    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (!(obj instanceof ChartChangeEventType)) {
            return false;
        }
        if (this.name.equals(((ChartChangeEventType) obj).toString())) {
            return true;
        }
        return false;
    }

    public int hashCode() {
        return this.name.hashCode();
    }

    private Object readResolve() throws ObjectStreamException {
        if (equals(GENERAL)) {
            return GENERAL;
        }
        if (equals(NEW_DATASET)) {
            return NEW_DATASET;
        }
        if (equals(DATASET_UPDATED)) {
            return DATASET_UPDATED;
        }
        return null;
    }
}
