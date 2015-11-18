package org.jfree.chart.renderer.xy;

import java.awt.BasicStroke;
import java.awt.Graphics2D;
import java.awt.Paint;
import java.awt.geom.Rectangle2D;
import java.awt.geom.Rectangle2D.Double;
import java.io.Serializable;
import org.jfree.chart.axis.DateAxis;
import org.jfree.chart.axis.ValueAxis;
import org.jfree.chart.entity.EntityCollection;
import org.jfree.chart.plot.CrosshairState;
import org.jfree.chart.plot.Plot;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.plot.PlotRenderingInfo;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.renderer.LookupPaintScale;
import org.jfree.chart.renderer.PaintScale;
import org.jfree.chart.util.ParamChecks;
import org.jfree.data.Range;
import org.jfree.data.general.DatasetUtilities;
import org.jfree.data.xy.NormalizedMatrixSeries;
import org.jfree.data.xy.XYDataset;
import org.jfree.data.xy.XYZDataset;
import org.jfree.ui.RectangleAnchor;
import org.jfree.util.PublicCloneable;

public class XYBlockRenderer extends AbstractXYItemRenderer implements XYItemRenderer, Cloneable, PublicCloneable, Serializable {
    private RectangleAnchor blockAnchor;
    private double blockHeight;
    private double blockWidth;
    private PaintScale paintScale;
    private double xOffset;
    private double yOffset;

    public XYBlockRenderer() {
        this.blockWidth = NormalizedMatrixSeries.DEFAULT_SCALE_FACTOR;
        this.blockHeight = NormalizedMatrixSeries.DEFAULT_SCALE_FACTOR;
        this.blockAnchor = RectangleAnchor.CENTER;
        updateOffsets();
        this.paintScale = new LookupPaintScale();
    }

    public double getBlockWidth() {
        return this.blockWidth;
    }

    public void setBlockWidth(double width) {
        if (width <= 0.0d) {
            throw new IllegalArgumentException("The 'width' argument must be > 0.0");
        }
        this.blockWidth = width;
        updateOffsets();
        fireChangeEvent();
    }

    public double getBlockHeight() {
        return this.blockHeight;
    }

    public void setBlockHeight(double height) {
        if (height <= 0.0d) {
            throw new IllegalArgumentException("The 'height' argument must be > 0.0");
        }
        this.blockHeight = height;
        updateOffsets();
        fireChangeEvent();
    }

    public RectangleAnchor getBlockAnchor() {
        return this.blockAnchor;
    }

    public void setBlockAnchor(RectangleAnchor anchor) {
        ParamChecks.nullNotPermitted(anchor, "anchor");
        if (!this.blockAnchor.equals(anchor)) {
            this.blockAnchor = anchor;
            updateOffsets();
            fireChangeEvent();
        }
    }

    public PaintScale getPaintScale() {
        return this.paintScale;
    }

    public void setPaintScale(PaintScale scale) {
        ParamChecks.nullNotPermitted(scale, "scale");
        this.paintScale = scale;
        fireChangeEvent();
    }

    private void updateOffsets() {
        if (this.blockAnchor.equals(RectangleAnchor.BOTTOM_LEFT)) {
            this.xOffset = 0.0d;
            this.yOffset = 0.0d;
        } else if (this.blockAnchor.equals(RectangleAnchor.BOTTOM)) {
            this.xOffset = (-this.blockWidth) / DateAxis.DEFAULT_AUTO_RANGE_MINIMUM_SIZE_IN_MILLISECONDS;
            this.yOffset = 0.0d;
        } else if (this.blockAnchor.equals(RectangleAnchor.BOTTOM_RIGHT)) {
            this.xOffset = -this.blockWidth;
            this.yOffset = 0.0d;
        } else if (this.blockAnchor.equals(RectangleAnchor.LEFT)) {
            this.xOffset = 0.0d;
            this.yOffset = (-this.blockHeight) / DateAxis.DEFAULT_AUTO_RANGE_MINIMUM_SIZE_IN_MILLISECONDS;
        } else if (this.blockAnchor.equals(RectangleAnchor.CENTER)) {
            this.xOffset = (-this.blockWidth) / DateAxis.DEFAULT_AUTO_RANGE_MINIMUM_SIZE_IN_MILLISECONDS;
            this.yOffset = (-this.blockHeight) / DateAxis.DEFAULT_AUTO_RANGE_MINIMUM_SIZE_IN_MILLISECONDS;
        } else if (this.blockAnchor.equals(RectangleAnchor.RIGHT)) {
            this.xOffset = -this.blockWidth;
            this.yOffset = (-this.blockHeight) / DateAxis.DEFAULT_AUTO_RANGE_MINIMUM_SIZE_IN_MILLISECONDS;
        } else if (this.blockAnchor.equals(RectangleAnchor.TOP_LEFT)) {
            this.xOffset = 0.0d;
            this.yOffset = -this.blockHeight;
        } else if (this.blockAnchor.equals(RectangleAnchor.TOP)) {
            this.xOffset = (-this.blockWidth) / DateAxis.DEFAULT_AUTO_RANGE_MINIMUM_SIZE_IN_MILLISECONDS;
            this.yOffset = -this.blockHeight;
        } else if (this.blockAnchor.equals(RectangleAnchor.TOP_RIGHT)) {
            this.xOffset = -this.blockWidth;
            this.yOffset = -this.blockHeight;
        }
    }

    public Range findDomainBounds(XYDataset dataset) {
        if (dataset == null) {
            return null;
        }
        Range r = DatasetUtilities.findDomainBounds(dataset, false);
        if (r != null) {
            return new Range(r.getLowerBound() + this.xOffset, (r.getUpperBound() + this.blockWidth) + this.xOffset);
        }
        return null;
    }

    public Range findRangeBounds(XYDataset dataset) {
        if (dataset == null) {
            return null;
        }
        Range r = DatasetUtilities.findRangeBounds(dataset, false);
        if (r == null) {
            return null;
        }
        return new Range(r.getLowerBound() + this.yOffset, (r.getUpperBound() + this.blockHeight) + this.yOffset);
    }

    public void drawItem(Graphics2D g2, XYItemRendererState state, Rectangle2D dataArea, PlotRenderingInfo info, XYPlot plot, ValueAxis domainAxis, ValueAxis rangeAxis, XYDataset dataset, int series, int item, CrosshairState crosshairState, int pass) {
        Rectangle2D block;
        double x = dataset.getXValue(series, item);
        double y = dataset.getYValue(series, item);
        double z = 0.0d;
        if (dataset instanceof XYZDataset) {
            z = ((XYZDataset) dataset).getZValue(series, item);
        }
        Paint p = this.paintScale.getPaint(z);
        double xx0 = domainAxis.valueToJava2D(this.xOffset + x, dataArea, plot.getDomainAxisEdge());
        double yy0 = rangeAxis.valueToJava2D(this.yOffset + y, dataArea, plot.getRangeAxisEdge());
        double xx1 = domainAxis.valueToJava2D((this.blockWidth + x) + this.xOffset, dataArea, plot.getDomainAxisEdge());
        double yy1 = rangeAxis.valueToJava2D((this.blockHeight + y) + this.yOffset, dataArea, plot.getRangeAxisEdge());
        if (plot.getOrientation().equals(PlotOrientation.HORIZONTAL)) {
            block = new Double(Math.min(yy0, yy1), Math.min(xx0, xx1), Math.abs(yy1 - yy0), Math.abs(xx0 - xx1));
        } else {
            block = new Double(Math.min(xx0, xx1), Math.min(yy0, yy1), Math.abs(xx1 - xx0), Math.abs(yy1 - yy0));
        }
        g2.setPaint(p);
        g2.fill(block);
        g2.setStroke(new BasicStroke(Plot.DEFAULT_FOREGROUND_ALPHA));
        g2.draw(block);
        EntityCollection entities = state.getEntityCollection();
        if (entities != null) {
            addEntity(entities, block, dataset, series, item, 0.0d, 0.0d);
        }
    }

    public boolean equals(Object obj) {
        if (obj == this) {
            return true;
        }
        if (!(obj instanceof XYBlockRenderer)) {
            return false;
        }
        XYBlockRenderer that = (XYBlockRenderer) obj;
        if (this.blockHeight == that.blockHeight && this.blockWidth == that.blockWidth && this.blockAnchor.equals(that.blockAnchor) && this.paintScale.equals(that.paintScale)) {
            return super.equals(obj);
        }
        return false;
    }

    public Object clone() throws CloneNotSupportedException {
        XYBlockRenderer clone = (XYBlockRenderer) super.clone();
        if (this.paintScale instanceof PublicCloneable) {
            clone.paintScale = (PaintScale) this.paintScale.clone();
        }
        return clone;
    }
}
