package org.jfree.util;

import java.awt.Graphics2D;
import java.awt.Polygon;
import java.awt.Shape;
import java.awt.geom.AffineTransform;
import java.awt.geom.Arc2D;
import java.awt.geom.Ellipse2D;
import java.awt.geom.GeneralPath;
import java.awt.geom.Line2D;
import java.awt.geom.PathIterator;
import java.awt.geom.Point2D;
import java.awt.geom.Point2D.Double;
import java.awt.geom.Rectangle2D;
import java.util.Arrays;
import org.jfree.chart.axis.Axis;
import org.jfree.chart.axis.DateAxis;
import org.jfree.ui.RectangleAnchor;

public class ShapeUtilities {
    private static final float SQRT2;

    private ShapeUtilities() {
    }

    public static Shape clone(Shape shape) {
        if (shape instanceof Cloneable) {
            try {
                return (Shape) ObjectUtilities.clone(shape);
            } catch (CloneNotSupportedException e) {
            }
        }
        return null;
    }

    public static boolean equal(Shape s1, Shape s2) {
        if ((s1 instanceof Line2D) && (s2 instanceof Line2D)) {
            return equal((Line2D) s1, (Line2D) s2);
        }
        if ((s1 instanceof Ellipse2D) && (s2 instanceof Ellipse2D)) {
            return equal((Ellipse2D) s1, (Ellipse2D) s2);
        }
        if ((s1 instanceof Arc2D) && (s2 instanceof Arc2D)) {
            return equal((Arc2D) s1, (Arc2D) s2);
        }
        if ((s1 instanceof Polygon) && (s2 instanceof Polygon)) {
            return equal((Polygon) s1, (Polygon) s2);
        }
        if ((s1 instanceof GeneralPath) && (s2 instanceof GeneralPath)) {
            return equal((GeneralPath) s1, (GeneralPath) s2);
        }
        return ObjectUtilities.equal(s1, s2);
    }

    public static boolean equal(Line2D l1, Line2D l2) {
        boolean z = true;
        if (l1 == null) {
            if (l2 != null) {
                z = false;
            }
            return z;
        } else if (l2 != null && l1.getP1().equals(l2.getP1()) && l1.getP2().equals(l2.getP2())) {
            return true;
        } else {
            return false;
        }
    }

    public static boolean equal(Ellipse2D e1, Ellipse2D e2) {
        if (e1 == null) {
            if (e2 == null) {
                return true;
            }
            return false;
        } else if (e2 == null) {
            return false;
        } else {
            if (e1.getFrame().equals(e2.getFrame())) {
                return true;
            }
            return false;
        }
    }

    public static boolean equal(Arc2D a1, Arc2D a2) {
        boolean z = true;
        if (a1 == null) {
            if (a2 != null) {
                z = false;
            }
            return z;
        } else if (a2 != null && a1.getFrame().equals(a2.getFrame()) && a1.getAngleStart() == a2.getAngleStart() && a1.getAngleExtent() == a2.getAngleExtent() && a1.getArcType() == a2.getArcType()) {
            return true;
        } else {
            return false;
        }
    }

    public static boolean equal(Polygon p1, Polygon p2) {
        boolean z = true;
        if (p1 == null) {
            if (p2 != null) {
                z = false;
            }
            return z;
        } else if (p2 != null && p1.npoints == p2.npoints && Arrays.equals(p1.xpoints, p2.xpoints) && Arrays.equals(p1.ypoints, p2.ypoints)) {
            return true;
        } else {
            return false;
        }
    }

    public static boolean equal(GeneralPath p1, GeneralPath p2) {
        boolean z = true;
        if (p1 == null) {
            if (p2 != null) {
                z = false;
            }
            return z;
        } else if (p2 == null || p1.getWindingRule() != p2.getWindingRule()) {
            return false;
        } else {
            boolean done;
            PathIterator iterator1 = p1.getPathIterator(null);
            PathIterator iterator2 = p2.getPathIterator(null);
            double[] d1 = new double[6];
            double[] d2 = new double[6];
            if (iterator1.isDone() && iterator2.isDone()) {
                done = true;
            } else {
                done = false;
            }
            while (!done) {
                if (iterator1.isDone() != iterator2.isDone() || iterator1.currentSegment(d1) != iterator2.currentSegment(d2) || !Arrays.equals(d1, d2)) {
                    return false;
                }
                iterator1.next();
                iterator2.next();
                if (iterator1.isDone() && iterator2.isDone()) {
                    done = true;
                } else {
                    done = false;
                }
            }
            return true;
        }
    }

    public static Shape createTranslatedShape(Shape shape, double transX, double transY) {
        if (shape != null) {
            return AffineTransform.getTranslateInstance(transX, transY).createTransformedShape(shape);
        }
        throw new IllegalArgumentException("Null 'shape' argument.");
    }

    public static Shape createTranslatedShape(Shape shape, RectangleAnchor anchor, double locationX, double locationY) {
        if (shape == null) {
            throw new IllegalArgumentException("Null 'shape' argument.");
        } else if (anchor == null) {
            throw new IllegalArgumentException("Null 'anchor' argument.");
        } else {
            Point2D anchorPoint = RectangleAnchor.coordinates(shape.getBounds2D(), anchor);
            return AffineTransform.getTranslateInstance(locationX - anchorPoint.getX(), locationY - anchorPoint.getY()).createTransformedShape(shape);
        }
    }

    public static Shape rotateShape(Shape base, double angle, float x, float y) {
        if (base == null) {
            return null;
        }
        return AffineTransform.getRotateInstance(angle, (double) x, (double) y).createTransformedShape(base);
    }

    public static void drawRotatedShape(Graphics2D g2, Shape shape, double angle, float x, float y) {
        AffineTransform saved = g2.getTransform();
        g2.transform(AffineTransform.getRotateInstance(angle, (double) x, (double) y));
        g2.draw(shape);
        g2.setTransform(saved);
    }

    static {
        SQRT2 = (float) Math.pow(DateAxis.DEFAULT_AUTO_RANGE_MINIMUM_SIZE_IN_MILLISECONDS, 0.5d);
    }

    public static Shape createDiagonalCross(float l, float t) {
        GeneralPath p0 = new GeneralPath();
        p0.moveTo((-l) - t, (-l) + t);
        p0.lineTo((-l) + t, (-l) - t);
        p0.lineTo(0.0f, (-t) * SQRT2);
        p0.lineTo(l - t, (-l) - t);
        p0.lineTo(l + t, (-l) + t);
        p0.lineTo(SQRT2 * t, 0.0f);
        p0.lineTo(l + t, l - t);
        p0.lineTo(l - t, l + t);
        p0.lineTo(0.0f, SQRT2 * t);
        p0.lineTo((-l) + t, l + t);
        p0.lineTo((-l) - t, l - t);
        p0.lineTo((-t) * SQRT2, 0.0f);
        p0.closePath();
        return p0;
    }

    public static Shape createRegularCross(float l, float t) {
        GeneralPath p0 = new GeneralPath();
        p0.moveTo(-l, t);
        p0.lineTo(-t, t);
        p0.lineTo(-t, l);
        p0.lineTo(t, l);
        p0.lineTo(t, t);
        p0.lineTo(l, t);
        p0.lineTo(l, -t);
        p0.lineTo(t, -t);
        p0.lineTo(t, -l);
        p0.lineTo(-t, -l);
        p0.lineTo(-t, -t);
        p0.lineTo(-l, -t);
        p0.closePath();
        return p0;
    }

    public static Shape createDiamond(float s) {
        GeneralPath p0 = new GeneralPath();
        p0.moveTo(0.0f, -s);
        p0.lineTo(s, 0.0f);
        p0.lineTo(0.0f, s);
        p0.lineTo(-s, 0.0f);
        p0.closePath();
        return p0;
    }

    public static Shape createUpTriangle(float s) {
        GeneralPath p0 = new GeneralPath();
        p0.moveTo(0.0f, -s);
        p0.lineTo(s, s);
        p0.lineTo(-s, s);
        p0.closePath();
        return p0;
    }

    public static Shape createDownTriangle(float s) {
        GeneralPath p0 = new GeneralPath();
        p0.moveTo(0.0f, s);
        p0.lineTo(s, -s);
        p0.lineTo(-s, -s);
        p0.closePath();
        return p0;
    }

    public static Shape createLineRegion(Line2D line, float width) {
        GeneralPath result = new GeneralPath();
        float x1 = (float) line.getX1();
        float x2 = (float) line.getX2();
        float y1 = (float) line.getY1();
        float y2 = (float) line.getY2();
        if (((double) (x2 - x1)) != 0.0d) {
            double theta = Math.atan((double) ((y2 - y1) / (x2 - x1)));
            float dx = ((float) Math.sin(theta)) * width;
            float dy = ((float) Math.cos(theta)) * width;
            result.moveTo(x1 - dx, y1 + dy);
            result.lineTo(x1 + dx, y1 - dy);
            result.lineTo(x2 + dx, y2 - dy);
            result.lineTo(x2 - dx, y2 + dy);
            result.closePath();
        } else {
            result.moveTo(x1 - (width / Axis.DEFAULT_TICK_MARK_OUTSIDE_LENGTH), y1);
            result.lineTo((width / Axis.DEFAULT_TICK_MARK_OUTSIDE_LENGTH) + x1, y1);
            result.lineTo((width / Axis.DEFAULT_TICK_MARK_OUTSIDE_LENGTH) + x2, y2);
            result.lineTo(x2 - (width / Axis.DEFAULT_TICK_MARK_OUTSIDE_LENGTH), y2);
            result.closePath();
        }
        return result;
    }

    public static Point2D getPointInRectangle(double x, double y, Rectangle2D area) {
        return new Double(Math.max(area.getMinX(), Math.min(x, area.getMaxX())), Math.max(area.getMinY(), Math.min(y, area.getMaxY())));
    }

    public static boolean contains(Rectangle2D rect1, Rectangle2D rect2) {
        double x0 = rect1.getX();
        double y0 = rect1.getY();
        double x = rect2.getX();
        double y = rect2.getY();
        return x >= x0 && y >= y0 && x + rect2.getWidth() <= rect1.getWidth() + x0 && y + rect2.getHeight() <= rect1.getHeight() + y0;
    }

    public static boolean intersects(Rectangle2D rect1, Rectangle2D rect2) {
        double x0 = rect1.getX();
        double y0 = rect1.getY();
        double x = rect2.getX();
        double width = rect2.getWidth();
        double y = rect2.getY();
        return x + width >= x0 && y + rect2.getHeight() >= y0 && x <= rect1.getWidth() + x0 && y <= rect1.getHeight() + y0;
    }
}
