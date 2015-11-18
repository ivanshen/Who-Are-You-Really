package org.jfree.data;

import java.io.ObjectStreamException;
import java.io.Serializable;

public final class RangeType implements Serializable {
    public static final RangeType FULL;
    public static final RangeType NEGATIVE;
    public static final RangeType POSITIVE;
    private static final long serialVersionUID = -9073319010650549239L;
    private String name;

    static {
        FULL = new RangeType("RangeType.FULL");
        POSITIVE = new RangeType("RangeType.POSITIVE");
        NEGATIVE = new RangeType("RangeType.NEGATIVE");
    }

    private RangeType(String name) {
        this.name = name;
    }

    public String toString() {
        return this.name;
    }

    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (!(obj instanceof RangeType)) {
            return false;
        }
        if (this.name.equals(((RangeType) obj).toString())) {
            return true;
        }
        return false;
    }

    public int hashCode() {
        return this.name.hashCode();
    }

    private Object readResolve() throws ObjectStreamException {
        if (equals(FULL)) {
            return FULL;
        }
        if (equals(POSITIVE)) {
            return POSITIVE;
        }
        if (equals(NEGATIVE)) {
            return NEGATIVE;
        }
        return null;
    }
}
