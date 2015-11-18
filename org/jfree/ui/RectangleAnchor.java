package org.jfree.ui;

import java.awt.geom.Point2D;
import java.awt.geom.Point2D.Double;
import java.awt.geom.Rectangle2D;
import java.io.ObjectStreamException;
import java.io.Serializable;
import org.jfree.chart.axis.DateAxis;

public final class RectangleAnchor implements Serializable {
    public static final RectangleAnchor BOTTOM;
    public static final RectangleAnchor BOTTOM_LEFT;
    public static final RectangleAnchor BOTTOM_RIGHT;
    public static final RectangleAnchor CENTER;
    public static final RectangleAnchor LEFT;
    public static final RectangleAnchor RIGHT;
    public static final RectangleAnchor TOP;
    public static final RectangleAnchor TOP_LEFT;
    public static final RectangleAnchor TOP_RIGHT;
    private static final long serialVersionUID = -2457494205644416327L;
    private String name;

    static {
        CENTER = new RectangleAnchor("RectangleAnchor.CENTER");
        TOP = new RectangleAnchor("RectangleAnchor.TOP");
        TOP_LEFT = new RectangleAnchor("RectangleAnchor.TOP_LEFT");
        TOP_RIGHT = new RectangleAnchor("RectangleAnchor.TOP_RIGHT");
        BOTTOM = new RectangleAnchor("RectangleAnchor.BOTTOM");
        BOTTOM_LEFT = new RectangleAnchor("RectangleAnchor.BOTTOM_LEFT");
        BOTTOM_RIGHT = new RectangleAnchor("RectangleAnchor.BOTTOM_RIGHT");
        LEFT = new RectangleAnchor("RectangleAnchor.LEFT");
        RIGHT = new RectangleAnchor("RectangleAnchor.RIGHT");
    }

    private RectangleAnchor(String name) {
        this.name = name;
    }

    public String toString() {
        return this.name;
    }

    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (!(obj instanceof RectangleAnchor)) {
            return false;
        }
        if (this.name.equals(((RectangleAnchor) obj).name)) {
            return true;
        }
        return false;
    }

    public int hashCode() {
        return this.name.hashCode();
    }

    public static Point2D coordinates(Rectangle2D rectangle, RectangleAnchor anchor) {
        Point2D result = new Double();
        if (anchor == CENTER) {
            result.setLocation(rectangle.getCenterX(), rectangle.getCenterY());
        } else if (anchor == TOP) {
            result.setLocation(rectangle.getCenterX(), rectangle.getMinY());
        } else if (anchor == BOTTOM) {
            result.setLocation(rectangle.getCenterX(), rectangle.getMaxY());
        } else if (anchor == LEFT) {
            result.setLocation(rectangle.getMinX(), rectangle.getCenterY());
        } else if (anchor == RIGHT) {
            result.setLocation(rectangle.getMaxX(), rectangle.getCenterY());
        } else if (anchor == TOP_LEFT) {
            result.setLocation(rectangle.getMinX(), rectangle.getMinY());
        } else if (anchor == TOP_RIGHT) {
            result.setLocation(rectangle.getMaxX(), rectangle.getMinY());
        } else if (anchor == BOTTOM_LEFT) {
            result.setLocation(rectangle.getMinX(), rectangle.getMaxY());
        } else if (anchor == BOTTOM_RIGHT) {
            result.setLocation(rectangle.getMaxX(), rectangle.getMaxY());
        }
        return result;
    }

    public static Rectangle2D createRectangle(Size2D dimensions, double anchorX, double anchorY, RectangleAnchor anchor) {
        double w = dimensions.getWidth();
        double h = dimensions.getHeight();
        if (anchor == CENTER) {
            return new Rectangle2D.Double(anchorX - (w / DateAxis.DEFAULT_AUTO_RANGE_MINIMUM_SIZE_IN_MILLISECONDS), anchorY - (h / DateAxis.DEFAULT_AUTO_RANGE_MINIMUM_SIZE_IN_MILLISECONDS), w, h);
        }
        if (anchor == TOP) {
            return new Rectangle2D.Double(anchorX - (w / DateAxis.DEFAULT_AUTO_RANGE_MINIMUM_SIZE_IN_MILLISECONDS), anchorY - (h / DateAxis.DEFAULT_AUTO_RANGE_MINIMUM_SIZE_IN_MILLISECONDS), w, h);
        }
        if (anchor == BOTTOM) {
            return new Rectangle2D.Double(anchorX - (w / DateAxis.DEFAULT_AUTO_RANGE_MINIMUM_SIZE_IN_MILLISECONDS), anchorY - (h / DateAxis.DEFAULT_AUTO_RANGE_MINIMUM_SIZE_IN_MILLISECONDS), w, h);
        }
        if (anchor == LEFT) {
            return new Rectangle2D.Double(anchorX, anchorY - (h / DateAxis.DEFAULT_AUTO_RANGE_MINIMUM_SIZE_IN_MILLISECONDS), w, h);
        } else if (anchor == RIGHT) {
            return new Rectangle2D.Double(anchorX - w, anchorY - (h / DateAxis.DEFAULT_AUTO_RANGE_MINIMUM_SIZE_IN_MILLISECONDS), w, h);
        } else {
            if (anchor == TOP_LEFT) {
                return new Rectangle2D.Double(anchorX - (w / DateAxis.DEFAULT_AUTO_RANGE_MINIMUM_SIZE_IN_MILLISECONDS), anchorY - (h / DateAxis.DEFAULT_AUTO_RANGE_MINIMUM_SIZE_IN_MILLISECONDS), w, h);
            }
            if (anchor == TOP_RIGHT) {
                return new Rectangle2D.Double(anchorX - (w / DateAxis.DEFAULT_AUTO_RANGE_MINIMUM_SIZE_IN_MILLISECONDS), anchorY - (h / DateAxis.DEFAULT_AUTO_RANGE_MINIMUM_SIZE_IN_MILLISECONDS), w, h);
            }
            if (anchor == BOTTOM_LEFT) {
                return new Rectangle2D.Double(anchorX - (w / DateAxis.DEFAULT_AUTO_RANGE_MINIMUM_SIZE_IN_MILLISECONDS), anchorY - (h / DateAxis.DEFAULT_AUTO_RANGE_MINIMUM_SIZE_IN_MILLISECONDS), w, h);
            }
            if (anchor == BOTTOM_RIGHT) {
                return new Rectangle2D.Double(anchorX - (w / DateAxis.DEFAULT_AUTO_RANGE_MINIMUM_SIZE_IN_MILLISECONDS), anchorY - (h / DateAxis.DEFAULT_AUTO_RANGE_MINIMUM_SIZE_IN_MILLISECONDS), w, h);
            }
            return null;
        }
    }

    private Object readResolve() throws ObjectStreamException {
        if (equals(CENTER)) {
            return CENTER;
        }
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
        if (equals(TOP_LEFT)) {
            return TOP_LEFT;
        }
        if (equals(TOP_RIGHT)) {
            return TOP_RIGHT;
        }
        if (equals(BOTTOM_LEFT)) {
            return BOTTOM_LEFT;
        }
        if (equals(BOTTOM_RIGHT)) {
            return BOTTOM_RIGHT;
        }
        return null;
    }
}
