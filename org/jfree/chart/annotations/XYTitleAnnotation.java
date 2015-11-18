package org.jfree.chart.annotations;

import java.awt.Graphics2D;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.awt.geom.Rectangle2D.Double;
import java.awt.geom.Rectangle2D.Float;
import java.io.Serializable;
import org.jfree.chart.HashUtilities;
import org.jfree.chart.axis.AxisLocation;
import org.jfree.chart.axis.ValueAxis;
import org.jfree.chart.block.BlockParams;
import org.jfree.chart.block.EntityBlockResult;
import org.jfree.chart.block.RectangleConstraint;
import org.jfree.chart.plot.Plot;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.plot.PlotRenderingInfo;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.title.Title;
import org.jfree.chart.util.ParamChecks;
import org.jfree.chart.util.XYCoordinateType;
import org.jfree.data.Range;
import org.jfree.ui.RectangleAnchor;
import org.jfree.ui.RectangleEdge;
import org.jfree.ui.Size2D;
import org.jfree.util.ObjectUtilities;
import org.jfree.util.PublicCloneable;

public class XYTitleAnnotation extends AbstractXYAnnotation implements Cloneable, PublicCloneable, Serializable {
    private static final long serialVersionUID = -4364694501921559958L;
    private RectangleAnchor anchor;
    private XYCoordinateType coordinateType;
    private double maxHeight;
    private double maxWidth;
    private Title title;
    private double x;
    private double y;

    public XYTitleAnnotation(double x, double y, Title title) {
        this(x, y, title, RectangleAnchor.CENTER);
    }

    public XYTitleAnnotation(double x, double y, Title title, RectangleAnchor anchor) {
        ParamChecks.nullNotPermitted(title, "title");
        ParamChecks.nullNotPermitted(anchor, "anchor");
        this.coordinateType = XYCoordinateType.RELATIVE;
        this.x = x;
        this.y = y;
        this.maxWidth = 0.0d;
        this.maxHeight = 0.0d;
        this.title = title;
        this.anchor = anchor;
    }

    public XYCoordinateType getCoordinateType() {
        return this.coordinateType;
    }

    public double getX() {
        return this.x;
    }

    public double getY() {
        return this.y;
    }

    public Title getTitle() {
        return this.title;
    }

    public RectangleAnchor getTitleAnchor() {
        return this.anchor;
    }

    public double getMaxWidth() {
        return this.maxWidth;
    }

    public void setMaxWidth(double max) {
        this.maxWidth = max;
        fireAnnotationChanged();
    }

    public double getMaxHeight() {
        return this.maxHeight;
    }

    public void setMaxHeight(double max) {
        this.maxHeight = max;
        fireAnnotationChanged();
    }

    public void draw(Graphics2D g2, XYPlot plot, Rectangle2D dataArea, ValueAxis domainAxis, ValueAxis rangeAxis, int rendererIndex, PlotRenderingInfo info) {
        double anchorX;
        double anchorY;
        PlotOrientation orientation = plot.getOrientation();
        AxisLocation domainAxisLocation = plot.getDomainAxisLocation();
        AxisLocation rangeAxisLocation = plot.getRangeAxisLocation();
        RectangleEdge domainEdge = Plot.resolveDomainAxisLocation(domainAxisLocation, orientation);
        RectangleEdge rangeEdge = Plot.resolveRangeAxisLocation(rangeAxisLocation, orientation);
        Range xRange = domainAxis.getRange();
        Range yRange = rangeAxis.getRange();
        if (this.coordinateType == XYCoordinateType.RELATIVE) {
            anchorX = xRange.getLowerBound() + (this.x * xRange.getLength());
            anchorY = yRange.getLowerBound() + (this.y * yRange.getLength());
        } else {
            anchorX = domainAxis.valueToJava2D(this.x, dataArea, domainEdge);
            anchorY = rangeAxis.valueToJava2D(this.y, dataArea, rangeEdge);
        }
        float j2DX = (float) domainAxis.valueToJava2D(anchorX, dataArea, domainEdge);
        float j2DY = (float) rangeAxis.valueToJava2D(anchorY, dataArea, rangeEdge);
        float xx = 0.0f;
        float yy = 0.0f;
        if (orientation == PlotOrientation.HORIZONTAL) {
            xx = j2DY;
            yy = j2DX;
        } else if (orientation == PlotOrientation.VERTICAL) {
            xx = j2DX;
            yy = j2DY;
        }
        double maxW = dataArea.getWidth();
        double maxH = dataArea.getHeight();
        if (this.coordinateType == XYCoordinateType.RELATIVE) {
            if (this.maxWidth > 0.0d) {
                maxW *= this.maxWidth;
            }
            if (this.maxHeight > 0.0d) {
                maxH *= this.maxHeight;
            }
        }
        if (this.coordinateType == XYCoordinateType.DATA) {
            maxW = this.maxWidth;
            maxH = this.maxHeight;
        }
        Size2D size = this.title.arrange(g2, new RectangleConstraint(new Range(0.0d, maxW), new Range(0.0d, maxH)));
        Rectangle2D titleRect = new Double(0.0d, 0.0d, size.width, size.height);
        Point2D anchorPoint = RectangleAnchor.coordinates(titleRect, this.anchor);
        xx -= (float) anchorPoint.getX();
        yy -= (float) anchorPoint.getY();
        titleRect.setRect((double) xx, (double) yy, titleRect.getWidth(), titleRect.getHeight());
        BlockParams p = new BlockParams();
        if (!(info == null || info.getOwner().getEntityCollection() == null)) {
            p.setGenerateEntities(true);
        }
        EntityBlockResult result = this.title.draw(g2, titleRect, p);
        if (info != null) {
            if (result instanceof EntityBlockResult) {
                info.getOwner().getEntityCollection().addAll(result.getEntityCollection());
            }
            String toolTip = getToolTipText();
            String url = getURL();
            if (toolTip != null || url != null) {
                addEntity(info, new Float(xx, yy, (float) size.width, (float) size.height), rendererIndex, toolTip, url);
            }
        }
    }

    public boolean equals(Object obj) {
        if (obj == this) {
            return true;
        }
        if (!(obj instanceof XYTitleAnnotation)) {
            return false;
        }
        XYTitleAnnotation that = (XYTitleAnnotation) obj;
        if (this.coordinateType == that.coordinateType && this.x == that.x && this.y == that.y && this.maxWidth == that.maxWidth && this.maxHeight == that.maxHeight && ObjectUtilities.equal(this.title, that.title) && this.anchor.equals(that.anchor)) {
            return super.equals(obj);
        }
        return false;
    }

    public int hashCode() {
        return HashUtilities.hashCode(HashUtilities.hashCode(HashUtilities.hashCode(HashUtilities.hashCode(HashUtilities.hashCode(HashUtilities.hashCode(HashUtilities.hashCode(193, this.anchor), this.coordinateType), this.x), this.y), this.maxWidth), this.maxHeight), this.title);
    }

    public Object clone() throws CloneNotSupportedException {
        return super.clone();
    }
}
