package org.jfree.chart.renderer;

import java.io.ObjectStreamException;
import java.io.Serializable;

public final class AreaRendererEndType implements Serializable {
    public static final AreaRendererEndType LEVEL;
    public static final AreaRendererEndType TAPER;
    public static final AreaRendererEndType TRUNCATE;
    private static final long serialVersionUID = -1774146392916359839L;
    private String name;

    static {
        TAPER = new AreaRendererEndType("AreaRendererEndType.TAPER");
        TRUNCATE = new AreaRendererEndType("AreaRendererEndType.TRUNCATE");
        LEVEL = new AreaRendererEndType("AreaRendererEndType.LEVEL");
    }

    private AreaRendererEndType(String name) {
        this.name = name;
    }

    public String toString() {
        return this.name;
    }

    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (!(obj instanceof AreaRendererEndType)) {
            return false;
        }
        if (this.name.equals(((AreaRendererEndType) obj).toString())) {
            return true;
        }
        return false;
    }

    private Object readResolve() throws ObjectStreamException {
        if (equals(LEVEL)) {
            return LEVEL;
        }
        if (equals(TAPER)) {
            return TAPER;
        }
        if (equals(TRUNCATE)) {
            return TRUNCATE;
        }
        return null;
    }
}
