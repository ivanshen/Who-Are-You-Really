package org.jfree.chart.needle;

import java.awt.Graphics2D;
import java.awt.Shape;
import java.awt.geom.GeneralPath;
import java.awt.geom.Line2D;
import java.awt.geom.Line2D.Float;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.io.Serializable;
import org.jfree.chart.HashUtilities;
import org.jfree.chart.axis.DateAxis;

public class ArrowNeedle extends MeterNeedle implements Cloneable, Serializable {
    private static final long serialVersionUID = -5334056511213782357L;
    private boolean isArrowAtTop;

    public ArrowNeedle(boolean isArrowAtTop) {
        this.isArrowAtTop = true;
        this.isArrowAtTop = isArrowAtTop;
    }

    protected void drawNeedle(Graphics2D g2, Rectangle2D plotArea, Point2D rotate, double angle) {
        Shape d;
        Line2D shape = new Float();
        float x = (float) (plotArea.getMinX() + (plotArea.getWidth() / DateAxis.DEFAULT_AUTO_RANGE_MINIMUM_SIZE_IN_MILLISECONDS));
        float minY = (float) plotArea.getMinY();
        float maxY = (float) plotArea.getMaxY();
        shape.setLine((double) x, (double) minY, (double) x, (double) maxY);
        GeneralPath shape1 = new GeneralPath();
        if (this.isArrowAtTop) {
            shape1.moveTo(x, minY);
            minY += (float) (getSize() * 4);
        } else {
            shape1.moveTo(x, maxY);
            minY = maxY - ((float) (getSize() * 4));
        }
        shape1.lineTo(((float) getSize()) + x, minY);
        shape1.lineTo(x - ((float) getSize()), minY);
        shape1.closePath();
        if (rotate == null || angle == 0.0d) {
            d = shape;
        } else {
            getTransform().setToRotation(angle, rotate.getX(), rotate.getY());
            d = getTransform().createTransformedShape(shape);
        }
        defaultDisplay(g2, d);
        if (rotate == null || angle == 0.0d) {
            d = shape1;
        } else {
            d = getTransform().createTransformedShape(shape1);
        }
        defaultDisplay(g2, d);
    }

    public boolean equals(Object obj) {
        if (obj == this) {
            return true;
        }
        if (!(obj instanceof ArrowNeedle)) {
            return false;
        }
        if (!super.equals(obj)) {
            return false;
        }
        if (this.isArrowAtTop != ((ArrowNeedle) obj).isArrowAtTop) {
            return false;
        }
        return true;
    }

    public int hashCode() {
        return HashUtilities.hashCode(super.hashCode(), this.isArrowAtTop);
    }

    public Object clone() throws CloneNotSupportedException {
        return super.clone();
    }
}
