package org.jfree.chart.needle;

import java.awt.Graphics2D;
import java.awt.geom.Arc2D.Double;
import java.awt.geom.GeneralPath;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.io.Serializable;
import org.jfree.chart.annotations.XYPointerAnnotation;
import org.jfree.chart.renderer.category.BarRenderer;

public class ShipNeedle extends MeterNeedle implements Cloneable, Serializable {
    private static final long serialVersionUID = 149554868169435612L;

    protected void drawNeedle(Graphics2D g2, Rectangle2D plotArea, Point2D rotate, double angle) {
        GeneralPath shape = new GeneralPath();
        shape.append(new Double(-9.0d, -7.0d, XYPointerAnnotation.DEFAULT_TIP_RADIUS, 14.0d, 0.0d, 25.5d, 0), true);
        shape.append(new Double(0.0d, -7.0d, XYPointerAnnotation.DEFAULT_TIP_RADIUS, 14.0d, 154.5d, 25.5d, 0), true);
        shape.closePath();
        getTransform().setToTranslation(plotArea.getMinX(), plotArea.getMaxY());
        getTransform().scale(plotArea.getWidth(), plotArea.getHeight() / BarRenderer.BAR_OUTLINE_WIDTH_THRESHOLD);
        shape.transform(getTransform());
        if (!(rotate == null || angle == 0.0d)) {
            getTransform().setToRotation(angle, rotate.getX(), rotate.getY());
            shape.transform(getTransform());
        }
        defaultDisplay(g2, shape);
    }

    public boolean equals(Object object) {
        if (object == null) {
            return false;
        }
        if (object == this) {
            return true;
        }
        if (super.equals(object) && (object instanceof ShipNeedle)) {
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
