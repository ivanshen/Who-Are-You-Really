package org.jfree.chart.labels;

import java.io.ObjectStreamException;
import java.io.Serializable;

public final class ItemLabelAnchor implements Serializable {
    public static final ItemLabelAnchor CENTER;
    public static final ItemLabelAnchor INSIDE1;
    public static final ItemLabelAnchor INSIDE10;
    public static final ItemLabelAnchor INSIDE11;
    public static final ItemLabelAnchor INSIDE12;
    public static final ItemLabelAnchor INSIDE2;
    public static final ItemLabelAnchor INSIDE3;
    public static final ItemLabelAnchor INSIDE4;
    public static final ItemLabelAnchor INSIDE5;
    public static final ItemLabelAnchor INSIDE6;
    public static final ItemLabelAnchor INSIDE7;
    public static final ItemLabelAnchor INSIDE8;
    public static final ItemLabelAnchor INSIDE9;
    public static final ItemLabelAnchor OUTSIDE1;
    public static final ItemLabelAnchor OUTSIDE10;
    public static final ItemLabelAnchor OUTSIDE11;
    public static final ItemLabelAnchor OUTSIDE12;
    public static final ItemLabelAnchor OUTSIDE2;
    public static final ItemLabelAnchor OUTSIDE3;
    public static final ItemLabelAnchor OUTSIDE4;
    public static final ItemLabelAnchor OUTSIDE5;
    public static final ItemLabelAnchor OUTSIDE6;
    public static final ItemLabelAnchor OUTSIDE7;
    public static final ItemLabelAnchor OUTSIDE8;
    public static final ItemLabelAnchor OUTSIDE9;
    private static final long serialVersionUID = -1233101616128695658L;
    private String name;

    static {
        CENTER = new ItemLabelAnchor("ItemLabelAnchor.CENTER");
        INSIDE1 = new ItemLabelAnchor("ItemLabelAnchor.INSIDE1");
        INSIDE2 = new ItemLabelAnchor("ItemLabelAnchor.INSIDE2");
        INSIDE3 = new ItemLabelAnchor("ItemLabelAnchor.INSIDE3");
        INSIDE4 = new ItemLabelAnchor("ItemLabelAnchor.INSIDE4");
        INSIDE5 = new ItemLabelAnchor("ItemLabelAnchor.INSIDE5");
        INSIDE6 = new ItemLabelAnchor("ItemLabelAnchor.INSIDE6");
        INSIDE7 = new ItemLabelAnchor("ItemLabelAnchor.INSIDE7");
        INSIDE8 = new ItemLabelAnchor("ItemLabelAnchor.INSIDE8");
        INSIDE9 = new ItemLabelAnchor("ItemLabelAnchor.INSIDE9");
        INSIDE10 = new ItemLabelAnchor("ItemLabelAnchor.INSIDE10");
        INSIDE11 = new ItemLabelAnchor("ItemLabelAnchor.INSIDE11");
        INSIDE12 = new ItemLabelAnchor("ItemLabelAnchor.INSIDE12");
        OUTSIDE1 = new ItemLabelAnchor("ItemLabelAnchor.OUTSIDE1");
        OUTSIDE2 = new ItemLabelAnchor("ItemLabelAnchor.OUTSIDE2");
        OUTSIDE3 = new ItemLabelAnchor("ItemLabelAnchor.OUTSIDE3");
        OUTSIDE4 = new ItemLabelAnchor("ItemLabelAnchor.OUTSIDE4");
        OUTSIDE5 = new ItemLabelAnchor("ItemLabelAnchor.OUTSIDE5");
        OUTSIDE6 = new ItemLabelAnchor("ItemLabelAnchor.OUTSIDE6");
        OUTSIDE7 = new ItemLabelAnchor("ItemLabelAnchor.OUTSIDE7");
        OUTSIDE8 = new ItemLabelAnchor("ItemLabelAnchor.OUTSIDE8");
        OUTSIDE9 = new ItemLabelAnchor("ItemLabelAnchor.OUTSIDE9");
        OUTSIDE10 = new ItemLabelAnchor("ItemLabelAnchor.OUTSIDE10");
        OUTSIDE11 = new ItemLabelAnchor("ItemLabelAnchor.OUTSIDE11");
        OUTSIDE12 = new ItemLabelAnchor("ItemLabelAnchor.OUTSIDE12");
    }

    private ItemLabelAnchor(String name) {
        this.name = name;
    }

    public String toString() {
        return this.name;
    }

    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (!(obj instanceof ItemLabelAnchor)) {
            return false;
        }
        if (this.name.equals(((ItemLabelAnchor) obj).toString())) {
            return true;
        }
        return false;
    }

    private Object readResolve() throws ObjectStreamException {
        if (equals(CENTER)) {
            return CENTER;
        }
        if (equals(INSIDE1)) {
            return INSIDE1;
        }
        if (equals(INSIDE2)) {
            return INSIDE2;
        }
        if (equals(INSIDE3)) {
            return INSIDE3;
        }
        if (equals(INSIDE4)) {
            return INSIDE4;
        }
        if (equals(INSIDE5)) {
            return INSIDE5;
        }
        if (equals(INSIDE6)) {
            return INSIDE6;
        }
        if (equals(INSIDE7)) {
            return INSIDE7;
        }
        if (equals(INSIDE8)) {
            return INSIDE8;
        }
        if (equals(INSIDE9)) {
            return INSIDE9;
        }
        if (equals(INSIDE10)) {
            return INSIDE10;
        }
        if (equals(INSIDE11)) {
            return INSIDE11;
        }
        if (equals(INSIDE12)) {
            return INSIDE12;
        }
        if (equals(OUTSIDE1)) {
            return OUTSIDE1;
        }
        if (equals(OUTSIDE2)) {
            return OUTSIDE2;
        }
        if (equals(OUTSIDE3)) {
            return OUTSIDE3;
        }
        if (equals(OUTSIDE4)) {
            return OUTSIDE4;
        }
        if (equals(OUTSIDE5)) {
            return OUTSIDE5;
        }
        if (equals(OUTSIDE6)) {
            return OUTSIDE6;
        }
        if (equals(OUTSIDE7)) {
            return OUTSIDE7;
        }
        if (equals(OUTSIDE8)) {
            return OUTSIDE8;
        }
        if (equals(OUTSIDE9)) {
            return OUTSIDE9;
        }
        if (equals(OUTSIDE10)) {
            return OUTSIDE10;
        }
        if (equals(OUTSIDE11)) {
            return OUTSIDE11;
        }
        if (equals(OUTSIDE12)) {
            return OUTSIDE12;
        }
        return null;
    }
}
