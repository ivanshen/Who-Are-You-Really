package org.jfree.chart.annotations;

import java.awt.Graphics2D;
import java.awt.geom.AffineTransform;
import java.awt.geom.Rectangle2D;
import java.awt.geom.Rectangle2D.Double;
import java.io.Serializable;
import org.jfree.chart.axis.DateAxis;
import org.jfree.chart.axis.ValueAxis;
import org.jfree.chart.plot.Plot;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.plot.PlotRenderingInfo;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.util.ParamChecks;
import org.jfree.data.xy.NormalizedMatrixSeries;
import org.jfree.ui.Drawable;
import org.jfree.ui.RectangleEdge;
import org.jfree.util.ObjectUtilities;
import org.jfree.util.PublicCloneable;

public class XYDrawableAnnotation extends AbstractXYAnnotation implements Cloneable, PublicCloneable, Serializable {
    private static final long serialVersionUID = -6540812859722691020L;
    private double displayHeight;
    private double displayWidth;
    private double drawScaleFactor;
    private Drawable drawable;
    private double x;
    private double y;

    public XYDrawableAnnotation(double x, double y, double width, double height, Drawable drawable) {
        this(x, y, width, height, NormalizedMatrixSeries.DEFAULT_SCALE_FACTOR, drawable);
    }

    public XYDrawableAnnotation(double x, double y, double displayWidth, double displayHeight, double drawScaleFactor, Drawable drawable) {
        ParamChecks.nullNotPermitted(drawable, "drawable");
        this.x = x;
        this.y = y;
        this.displayWidth = displayWidth;
        this.displayHeight = displayHeight;
        this.drawScaleFactor = drawScaleFactor;
        this.drawable = drawable;
    }

    public void draw(Graphics2D g2, XYPlot plot, Rectangle2D dataArea, ValueAxis domainAxis, ValueAxis rangeAxis, int rendererIndex, PlotRenderingInfo info) {
        PlotOrientation orientation = plot.getOrientation();
        RectangleEdge domainEdge = Plot.resolveDomainAxisLocation(plot.getDomainAxisLocation(), orientation);
        RectangleEdge rangeEdge = Plot.resolveRangeAxisLocation(plot.getRangeAxisLocation(), orientation);
        float j2DX = (float) domainAxis.valueToJava2D(this.x, dataArea, domainEdge);
        float j2DY = (float) rangeAxis.valueToJava2D(this.y, dataArea, rangeEdge);
        Rectangle2D displayArea = new Double(((double) j2DX) - (this.displayWidth / DateAxis.DEFAULT_AUTO_RANGE_MINIMUM_SIZE_IN_MILLISECONDS), ((double) j2DY) - (this.displayHeight / DateAxis.DEFAULT_AUTO_RANGE_MINIMUM_SIZE_IN_MILLISECONDS), this.displayWidth, this.displayHeight);
        AffineTransform savedTransform = g2.getTransform();
        Rectangle2D drawArea = new Double(0.0d, 0.0d, this.displayWidth * this.drawScaleFactor, this.displayHeight * this.drawScaleFactor);
        g2.scale(NormalizedMatrixSeries.DEFAULT_SCALE_FACTOR / this.drawScaleFactor, NormalizedMatrixSeries.DEFAULT_SCALE_FACTOR / this.drawScaleFactor);
        g2.translate((((double) j2DX) - (this.displayWidth / DateAxis.DEFAULT_AUTO_RANGE_MINIMUM_SIZE_IN_MILLISECONDS)) * this.drawScaleFactor, (((double) j2DY) - (this.displayHeight / DateAxis.DEFAULT_AUTO_RANGE_MINIMUM_SIZE_IN_MILLISECONDS)) * this.drawScaleFactor);
        this.drawable.draw(g2, drawArea);
        g2.setTransform(savedTransform);
        String toolTip = getToolTipText();
        String url = getURL();
        if (toolTip != null || url != null) {
            addEntity(info, displayArea, rendererIndex, toolTip, url);
        }
    }

    public boolean equals(Object obj) {
        if (obj == this) {
            return true;
        }
        if (!super.equals(obj)) {
            return false;
        }
        if (!(obj instanceof XYDrawableAnnotation)) {
            return false;
        }
        XYDrawableAnnotation that = (XYDrawableAnnotation) obj;
        if (this.x != that.x) {
            return false;
        }
        if (this.y != that.y) {
            return false;
        }
        if (this.displayWidth != that.displayWidth) {
            return false;
        }
        if (this.displayHeight != that.displayHeight) {
            return false;
        }
        if (this.drawScaleFactor != that.drawScaleFactor) {
            return false;
        }
        if (ObjectUtilities.equal(this.drawable, that.drawable)) {
            return true;
        }
        return false;
    }

    public int hashCode() {
        long temp = Double.doubleToLongBits(this.x);
        int result = (int) ((temp >>> 32) ^ temp);
        temp = Double.doubleToLongBits(this.y);
        result = (result * 29) + ((int) ((temp >>> 32) ^ temp));
        temp = Double.doubleToLongBits(this.displayWidth);
        result = (result * 29) + ((int) ((temp >>> 32) ^ temp));
        temp = Double.doubleToLongBits(this.displayHeight);
        return (result * 29) + ((int) ((temp >>> 32) ^ temp));
    }

    public Object clone() throws CloneNotSupportedException {
        return super.clone();
    }
}
