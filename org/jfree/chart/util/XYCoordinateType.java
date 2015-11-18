package org.jfree.chart.util;

import java.io.ObjectStreamException;
import java.io.Serializable;

public final class XYCoordinateType implements Serializable {
    public static final XYCoordinateType DATA;
    public static final XYCoordinateType INDEX;
    public static final XYCoordinateType RELATIVE;
    private String name;

    static {
        DATA = new XYCoordinateType("XYCoordinateType.DATA");
        RELATIVE = new XYCoordinateType("XYCoordinateType.RELATIVE");
        INDEX = new XYCoordinateType("XYCoordinateType.INDEX");
    }

    private XYCoordinateType(String name) {
        this.name = name;
    }

    public String toString() {
        return this.name;
    }

    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (!(obj instanceof XYCoordinateType)) {
            return false;
        }
        if (this.name.equals(((XYCoordinateType) obj).toString())) {
            return true;
        }
        return false;
    }

    private Object readResolve() throws ObjectStreamException {
        if (equals(DATA)) {
            return DATA;
        }
        if (equals(RELATIVE)) {
            return RELATIVE;
        }
        if (equals(INDEX)) {
            return INDEX;
        }
        return null;
    }
}
