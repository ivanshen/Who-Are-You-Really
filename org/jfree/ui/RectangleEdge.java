package org.jfree.ui;

import java.awt.geom.Rectangle2D;
import java.io.ObjectStreamException;
import java.io.Serializable;

public final class RectangleEdge implements Serializable {
    public static final RectangleEdge BOTTOM;
    public static final RectangleEdge LEFT;
    public static final RectangleEdge RIGHT;
    public static final RectangleEdge TOP;
    private static final long serialVersionUID = -7400988293691093548L;
    private String name;

    static {
        TOP = new RectangleEdge("RectangleEdge.TOP");
        BOTTOM = new RectangleEdge("RectangleEdge.BOTTOM");
        LEFT = new RectangleEdge("RectangleEdge.LEFT");
        RIGHT = new RectangleEdge("RectangleEdge.RIGHT");
    }

    private RectangleEdge(String name) {
        this.name = name;
    }

    public String toString() {
        return this.name;
    }

    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof RectangleEdge)) {
            return false;
        }
        if (this.name.equals(((RectangleEdge) o).name)) {
            return true;
        }
        return false;
    }

    public int hashCode() {
        return this.name.hashCode();
    }

    public static boolean isTopOrBottom(RectangleEdge edge) {
        return edge == TOP || edge == BOTTOM;
    }

    public static boolean isLeftOrRight(RectangleEdge edge) {
        return edge == LEFT || edge == RIGHT;
    }

    public static RectangleEdge opposite(RectangleEdge edge) {
        if (edge == TOP) {
            return BOTTOM;
        }
        if (edge == BOTTOM) {
            return TOP;
        }
        if (edge == LEFT) {
            return RIGHT;
        }
        if (edge == RIGHT) {
            return LEFT;
        }
        return null;
    }

    public static double coordinate(Rectangle2D rectangle, RectangleEdge edge) {
        if (edge == TOP) {
            return rectangle.getMinY();
        }
        if (edge == BOTTOM) {
            return rectangle.getMaxY();
        }
        if (edge == LEFT) {
            return rectangle.getMinX();
        }
        if (edge == RIGHT) {
            return rectangle.getMaxX();
        }
        return 0.0d;
    }

    private Object readResolve() throws ObjectStreamException {
        if (equals(TOP)) {
            return TOP;
        }
        if (equals(BOTTOM)) {
            return BOTTOM;
        }
        if (equals(LEFT)) {
            return LEFT;
        }
        if (equals(RIGHT)) {
            return RIGHT;
        }
        return null;
    }
}
