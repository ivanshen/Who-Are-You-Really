package org.jfree.chart.plot;

import java.io.ObjectStreamException;
import java.io.Serializable;

public final class DialShape implements Serializable {
    public static final DialShape CHORD;
    public static final DialShape CIRCLE;
    public static final DialShape PIE;
    private static final long serialVersionUID = -3471933055190251131L;
    private String name;

    static {
        CIRCLE = new DialShape("DialShape.CIRCLE");
        CHORD = new DialShape("DialShape.CHORD");
        PIE = new DialShape("DialShape.PIE");
    }

    private DialShape(String name) {
        this.name = name;
    }

    public String toString() {
        return this.name;
    }

    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (!(obj instanceof DialShape)) {
            return false;
        }
        if (this.name.equals(((DialShape) obj).toString())) {
            return true;
        }
        return false;
    }

    public int hashCode() {
        return this.name.hashCode();
    }

    private Object readResolve() throws ObjectStreamException {
        if (equals(CIRCLE)) {
            return CIRCLE;
        }
        if (equals(CHORD)) {
            return CHORD;
        }
        if (equals(PIE)) {
            return PIE;
        }
        return null;
    }
}
