package org.jfree.util;

import java.io.ObjectStreamException;
import java.io.Serializable;

public final class UnitType implements Serializable {
    public static final UnitType ABSOLUTE;
    public static final UnitType RELATIVE;
    private static final long serialVersionUID = 6531925392288519884L;
    private String name;

    static {
        ABSOLUTE = new UnitType("UnitType.ABSOLUTE");
        RELATIVE = new UnitType("UnitType.RELATIVE");
    }

    private UnitType(String name) {
        this.name = name;
    }

    public String toString() {
        return this.name;
    }

    public boolean equals(Object obj) {
        if (obj == this) {
            return true;
        }
        if (!(obj instanceof UnitType)) {
            return false;
        }
        if (this.name.equals(((UnitType) obj).name)) {
            return true;
        }
        return false;
    }

    public int hashCode() {
        return this.name.hashCode();
    }

    private Object readResolve() throws ObjectStreamException {
        if (equals(ABSOLUTE)) {
            return ABSOLUTE;
        }
        if (equals(RELATIVE)) {
            return RELATIVE;
        }
        return null;
    }
}
