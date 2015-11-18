package org.jfree.ui;

import java.io.ObjectStreamException;
import java.io.Serializable;

public final class LengthAdjustmentType implements Serializable {
    public static final LengthAdjustmentType CONTRACT;
    public static final LengthAdjustmentType EXPAND;
    public static final LengthAdjustmentType NO_CHANGE;
    private static final long serialVersionUID = -6097408511380545010L;
    private String name;

    static {
        NO_CHANGE = new LengthAdjustmentType("NO_CHANGE");
        EXPAND = new LengthAdjustmentType("EXPAND");
        CONTRACT = new LengthAdjustmentType("CONTRACT");
    }

    private LengthAdjustmentType(String name) {
        this.name = name;
    }

    public String toString() {
        return this.name;
    }

    public boolean equals(Object obj) {
        if (obj == this) {
            return true;
        }
        if (!(obj instanceof LengthAdjustmentType)) {
            return false;
        }
        if (this.name.equals(((LengthAdjustmentType) obj).name)) {
            return true;
        }
        return false;
    }

    public int hashCode() {
        return this.name.hashCode();
    }

    private Object readResolve() throws ObjectStreamException {
        if (equals(NO_CHANGE)) {
            return NO_CHANGE;
        }
        if (equals(EXPAND)) {
            return EXPAND;
        }
        if (equals(CONTRACT)) {
            return CONTRACT;
        }
        return null;
    }
}
