package org.jfree.chart.axis;

import java.io.ObjectStreamException;
import java.io.Serializable;

public final class TickType implements Serializable {
    public static final TickType MAJOR;
    public static final TickType MINOR;
    private String name;

    static {
        MAJOR = new TickType("MAJOR");
        MINOR = new TickType("MINOR");
    }

    private TickType(String name) {
        this.name = name;
    }

    public String toString() {
        return this.name;
    }

    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (!(obj instanceof TickType)) {
            return false;
        }
        if (this.name.equals(((TickType) obj).name)) {
            return true;
        }
        return false;
    }

    private Object readResolve() throws ObjectStreamException {
        if (equals(MAJOR)) {
            return MAJOR;
        }
        if (equals(MINOR)) {
            return MINOR;
        }
        return null;
    }
}
