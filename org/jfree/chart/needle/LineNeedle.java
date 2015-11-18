package org.jfree.chart.needle;

import java.awt.Graphics2D;
import java.awt.Shape;
import java.awt.geom.Line2D;
import java.awt.geom.Line2D.Double;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.io.Serializable;
import org.jfree.chart.axis.DateAxis;

public class LineNeedle extends MeterNeedle implements Cloneable, Serializable {
    private static final long serialVersionUID = 6215321387896748945L;

    protected void drawNeedle(Graphics2D g2, Rectangle2D plotArea, Point2D rotate, double angle) {
        Line2D shape = new Double();
        double x = plotArea.getMinX() + (plotArea.getWidth() / DateAxis.DEFAULT_AUTO_RANGE_MINIMUM_SIZE_IN_MILLISECONDS);
        shape.setLine(x, plotArea.getMinY(), x, plotArea.getMaxY());
        Shape s = shape;
        if (!(rotate == null || angle == 0.0d)) {
            getTransform().setToRotation(angle, rotate.getX(), rotate.getY());
            s = getTransform().createTransformedShape(s);
        }
        defaultDisplay(g2, s);
    }

    public boolean equals(Object obj) {
        if (obj == this) {
            return true;
        }
        if (obj instanceof LineNeedle) {
            return super.equals(obj);
        }
        return false;
    }

    public int hashCode() {
        return super.hashCode();
    }

    public Object clone() throws CloneNotSupportedException {
        return super.clone();
    }
}
