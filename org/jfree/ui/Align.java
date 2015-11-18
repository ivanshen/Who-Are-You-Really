package org.jfree.ui;

import java.awt.geom.Rectangle2D;
import org.jfree.chart.axis.DateAxis;

public final class Align {
    public static final int BOTTOM = 2;
    public static final int BOTTOM_LEFT = 6;
    public static final int BOTTOM_RIGHT = 10;
    public static final int CENTER = 0;
    public static final int EAST = 8;
    public static final int FIT = 15;
    public static final int FIT_HORIZONTAL = 12;
    public static final int FIT_VERTICAL = 3;
    public static final int LEFT = 4;
    public static final int NORTH = 1;
    public static final int NORTH_EAST = 9;
    public static final int NORTH_WEST = 5;
    public static final int RIGHT = 8;
    public static final int SOUTH = 2;
    public static final int SOUTH_EAST = 10;
    public static final int SOUTH_WEST = 6;
    public static final int TOP = 1;
    public static final int TOP_LEFT = 5;
    public static final int TOP_RIGHT = 9;
    public static final int WEST = 4;

    private Align() {
    }

    public static void align(Rectangle2D rect, Rectangle2D frame, int align) {
        double x = frame.getCenterX() - (rect.getWidth() / DateAxis.DEFAULT_AUTO_RANGE_MINIMUM_SIZE_IN_MILLISECONDS);
        double y = frame.getCenterY() - (rect.getHeight() / DateAxis.DEFAULT_AUTO_RANGE_MINIMUM_SIZE_IN_MILLISECONDS);
        double w = rect.getWidth();
        double h = rect.getHeight();
        if ((align & FIT_VERTICAL) == FIT_VERTICAL) {
            h = frame.getHeight();
        }
        if ((align & FIT_HORIZONTAL) == FIT_HORIZONTAL) {
            w = frame.getWidth();
        }
        if ((align & TOP) == TOP) {
            y = frame.getMinY();
        }
        if ((align & SOUTH) == SOUTH) {
            y = frame.getMaxY() - h;
        }
        if ((align & WEST) == WEST) {
            x = frame.getX();
        }
        if ((align & RIGHT) == RIGHT) {
            x = frame.getMaxX() - w;
        }
        rect.setRect(x, y, w, h);
    }
}
