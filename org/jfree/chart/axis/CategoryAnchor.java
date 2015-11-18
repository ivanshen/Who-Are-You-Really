package org.jfree.chart.axis;

import java.io.ObjectStreamException;
import java.io.Serializable;

public final class CategoryAnchor implements Serializable {
    public static final CategoryAnchor END;
    public static final CategoryAnchor MIDDLE;
    public static final CategoryAnchor START;
    private static final long serialVersionUID = -2604142742210173810L;
    private String name;

    static {
        START = new CategoryAnchor("CategoryAnchor.START");
        MIDDLE = new CategoryAnchor("CategoryAnchor.MIDDLE");
        END = new CategoryAnchor("CategoryAnchor.END");
    }

    private CategoryAnchor(String name) {
        this.name = name;
    }

    public String toString() {
        return this.name;
    }

    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (!(obj instanceof CategoryAnchor)) {
            return false;
        }
        if (this.name.equals(((CategoryAnchor) obj).toString())) {
            return true;
        }
        return false;
    }

    private Object readResolve() throws ObjectStreamException {
        if (equals(START)) {
            return START;
        }
        if (equals(MIDDLE)) {
            return MIDDLE;
        }
        if (equals(END)) {
            return END;
        }
        return null;
    }
}
