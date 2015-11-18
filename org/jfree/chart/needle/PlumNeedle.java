package org.jfree.chart.needle;

import java.awt.Graphics2D;
import java.awt.geom.Arc2D;
import java.awt.geom.Arc2D.Double;
import java.awt.geom.Area;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.io.Serializable;
import org.jfree.chart.axis.DateAxis;

public class PlumNeedle extends MeterNeedle implements Cloneable, Serializable {
    private static final long serialVersionUID = -3082660488660600718L;

    protected void drawNeedle(Graphics2D g2, Rectangle2D plotArea, Point2D rotate, double angle) {
        Arc2D shape = new Double(2);
        double radius = plotArea.getHeight();
        double halfX = plotArea.getWidth() / DateAxis.DEFAULT_AUTO_RANGE_MINIMUM_SIZE_IN_MILLISECONDS;
        double diameter = DateAxis.DEFAULT_AUTO_RANGE_MINIMUM_SIZE_IN_MILLISECONDS * radius;
        shape.setFrame((plotArea.getMinX() + halfX) - radius, plotArea.getMinY() - radius, diameter, diameter);
        radius = Math.toDegrees(Math.asin(halfX / radius));
        shape.setAngleStart(270.0d - radius);
        shape.setAngleExtent(DateAxis.DEFAULT_AUTO_RANGE_MINIMUM_SIZE_IN_MILLISECONDS * radius);
        Area s = new Area(shape);
        if (!(rotate == null || angle == 0.0d)) {
            getTransform().setToRotation(angle, rotate.getX(), rotate.getY());
            s.transform(getTransform());
        }
        defaultDisplay(g2, s);
    }

    public boolean equals(Object obj) {
        if (obj == this) {
            return true;
        }
        if (!(obj instanceof PlumNeedle)) {
            return false;
        }
        if (super.equals(obj)) {
            return true;
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
