package org.jfree.chart.block;

import java.io.ObjectStreamException;
import java.io.Serializable;

public final class LengthConstraintType implements Serializable {
    public static final LengthConstraintType FIXED;
    public static final LengthConstraintType NONE;
    public static final LengthConstraintType RANGE;
    private static final long serialVersionUID = -1156658804028142978L;
    private String name;

    static {
        NONE = new LengthConstraintType("LengthConstraintType.NONE");
        RANGE = new LengthConstraintType("RectangleConstraintType.RANGE");
        FIXED = new LengthConstraintType("LengthConstraintType.FIXED");
    }

    private LengthConstraintType(String name) {
        this.name = name;
    }

    public String toString() {
        return this.name;
    }

    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (!(obj instanceof LengthConstraintType)) {
            return false;
        }
        if (this.name.equals(((LengthConstraintType) obj).toString())) {
            return true;
        }
        return false;
    }

    public int hashCode() {
        return this.name.hashCode();
    }

    private Object readResolve() throws ObjectStreamException {
        if (equals(NONE)) {
            return NONE;
        }
        if (equals(RANGE)) {
            return RANGE;
        }
        if (equals(FIXED)) {
            return FIXED;
        }
        return null;
    }
}
