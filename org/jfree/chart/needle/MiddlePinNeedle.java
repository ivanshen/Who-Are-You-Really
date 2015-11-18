package org.jfree.chart.needle;

import java.awt.Graphics2D;
import java.awt.geom.Area;
import java.awt.geom.Ellipse2D.Double;
import java.awt.geom.GeneralPath;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.io.Serializable;
import org.jfree.chart.annotations.XYPointerAnnotation;
import org.jfree.chart.axis.DateAxis;

public class MiddlePinNeedle extends MeterNeedle implements Cloneable, Serializable {
    private static final long serialVersionUID = 6237073996403125310L;

    protected void drawNeedle(Graphics2D g2, Rectangle2D plotArea, Point2D rotate, double angle) {
        GeneralPath pointer = new GeneralPath();
        int minY = (int) plotArea.getMinY();
        int midY = ((((int) plotArea.getMaxY()) - minY) / 2) + minY;
        int midX = (int) (plotArea.getMinX() + (plotArea.getWidth() / DateAxis.DEFAULT_AUTO_RANGE_MINIMUM_SIZE_IN_MILLISECONDS));
        int lenX = (int) (plotArea.getWidth() / XYPointerAnnotation.DEFAULT_TIP_RADIUS);
        if (lenX < 2) {
            lenX = 2;
        }
        pointer.moveTo((float) (midX - lenX), (float) (midY - lenX));
        pointer.lineTo((float) (midX + lenX), (float) (midY - lenX));
        GeneralPath generalPath = pointer;
        generalPath.lineTo((float) midX, (float) minY);
        pointer.closePath();
        lenX *= 4;
        Area area = new Area(new Double((double) (midX - (lenX / 2)), (double) (midY - lenX), (double) lenX, (double) lenX));
        area.add(new Area(pointer));
        if (!(rotate == null || angle == 0.0d)) {
            getTransform().setToRotation(angle, rotate.getX(), rotate.getY());
            area.transform(getTransform());
        }
        defaultDisplay(g2, area);
    }

    public boolean equals(Object object) {
        if (object == null) {
            return false;
        }
        if (object == this) {
            return true;
        }
        if (super.equals(object) && (object instanceof MiddlePinNeedle)) {
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
