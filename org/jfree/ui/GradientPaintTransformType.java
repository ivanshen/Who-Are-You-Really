package org.jfree.ui;

import java.io.ObjectStreamException;
import java.io.Serializable;

public final class GradientPaintTransformType implements Serializable {
    public static final GradientPaintTransformType CENTER_HORIZONTAL;
    public static final GradientPaintTransformType CENTER_VERTICAL;
    public static final GradientPaintTransformType HORIZONTAL;
    public static final GradientPaintTransformType VERTICAL;
    private static final long serialVersionUID = 8331561784933982450L;
    private String name;

    static {
        VERTICAL = new GradientPaintTransformType("GradientPaintTransformType.VERTICAL");
        HORIZONTAL = new GradientPaintTransformType("GradientPaintTransformType.HORIZONTAL");
        CENTER_VERTICAL = new GradientPaintTransformType("GradientPaintTransformType.CENTER_VERTICAL");
        CENTER_HORIZONTAL = new GradientPaintTransformType("GradientPaintTransformType.CENTER_HORIZONTAL");
    }

    private GradientPaintTransformType(String name) {
        this.name = name;
    }

    public String toString() {
        return this.name;
    }

    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof GradientPaintTransformType)) {
            return false;
        }
        if (this.name.equals(((GradientPaintTransformType) o).name)) {
            return true;
        }
        return false;
    }

    public int hashCode() {
        return this.name.hashCode();
    }

    private Object readResolve() throws ObjectStreamException {
        if (equals(HORIZONTAL)) {
            return HORIZONTAL;
        }
        if (equals(VERTICAL)) {
            return VERTICAL;
        }
        if (equals(CENTER_HORIZONTAL)) {
            return CENTER_HORIZONTAL;
        }
        if (equals(CENTER_VERTICAL)) {
            return CENTER_VERTICAL;
        }
        return null;
    }
}
