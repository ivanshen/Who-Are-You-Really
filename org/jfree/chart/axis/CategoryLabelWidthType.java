package org.jfree.chart.axis;

import java.io.ObjectStreamException;
import java.io.Serializable;
import org.jfree.chart.util.ParamChecks;

public final class CategoryLabelWidthType implements Serializable {
    public static final CategoryLabelWidthType CATEGORY;
    public static final CategoryLabelWidthType RANGE;
    private static final long serialVersionUID = -6976024792582949656L;
    private String name;

    static {
        CATEGORY = new CategoryLabelWidthType("CategoryLabelWidthType.CATEGORY");
        RANGE = new CategoryLabelWidthType("CategoryLabelWidthType.RANGE");
    }

    private CategoryLabelWidthType(String name) {
        ParamChecks.nullNotPermitted(name, "name");
        this.name = name;
    }

    public String toString() {
        return this.name;
    }

    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (!(obj instanceof CategoryLabelWidthType)) {
            return false;
        }
        if (this.name.equals(((CategoryLabelWidthType) obj).toString())) {
            return true;
        }
        return false;
    }

    private Object readResolve() throws ObjectStreamException {
        if (equals(CATEGORY)) {
            return CATEGORY;
        }
        if (equals(RANGE)) {
            return RANGE;
        }
        return null;
    }
}
