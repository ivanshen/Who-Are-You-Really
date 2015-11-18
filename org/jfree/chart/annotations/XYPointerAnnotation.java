package org.jfree.chart.annotations;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Paint;
import java.awt.Shape;
import java.awt.Stroke;
import java.awt.geom.GeneralPath;
import java.awt.geom.Line2D.Double;
import java.awt.geom.Rectangle2D;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import org.jfree.chart.HashUtilities;
import org.jfree.chart.axis.ValueAxis;
import org.jfree.chart.plot.Plot;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.plot.PlotRenderingInfo;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.util.ParamChecks;
import org.jfree.io.SerialUtilities;
import org.jfree.text.TextUtilities;
import org.jfree.ui.RectangleEdge;
import org.jfree.util.ObjectUtilities;
import org.jfree.util.PublicCloneable;

public class XYPointerAnnotation extends XYTextAnnotation implements Cloneable, PublicCloneable, Serializable {
    public static final double DEFAULT_ARROW_LENGTH = 5.0d;
    public static final double DEFAULT_ARROW_WIDTH = 3.0d;
    public static final double DEFAULT_BASE_RADIUS = 30.0d;
    public static final double DEFAULT_LABEL_OFFSET = 3.0d;
    public static final double DEFAULT_TIP_RADIUS = 10.0d;
    private static final long serialVersionUID = -4031161445009858551L;
    private double angle;
    private double arrowLength;
    private transient Paint arrowPaint;
    private transient Stroke arrowStroke;
    private double arrowWidth;
    private double baseRadius;
    private double labelOffset;
    private double tipRadius;

    public XYPointerAnnotation(String label, double x, double y, double angle) {
        super(label, x, y);
        this.angle = angle;
        this.tipRadius = DEFAULT_TIP_RADIUS;
        this.baseRadius = DEFAULT_BASE_RADIUS;
        this.arrowLength = DEFAULT_ARROW_LENGTH;
        this.arrowWidth = DEFAULT_LABEL_OFFSET;
        this.labelOffset = DEFAULT_LABEL_OFFSET;
        this.arrowStroke = new BasicStroke(Plot.DEFAULT_FOREGROUND_ALPHA);
        this.arrowPaint = Color.black;
    }

    public double getAngle() {
        return this.angle;
    }

    public void setAngle(double angle) {
        this.angle = angle;
        fireAnnotationChanged();
    }

    public double getTipRadius() {
        return this.tipRadius;
    }

    public void setTipRadius(double radius) {
        this.tipRadius = radius;
        fireAnnotationChanged();
    }

    public double getBaseRadius() {
        return this.baseRadius;
    }

    public void setBaseRadius(double radius) {
        this.baseRadius = radius;
        fireAnnotationChanged();
    }

    public double getLabelOffset() {
        return this.labelOffset;
    }

    public void setLabelOffset(double offset) {
        this.labelOffset = offset;
        fireAnnotationChanged();
    }

    public double getArrowLength() {
        return this.arrowLength;
    }

    public void setArrowLength(double length) {
        this.arrowLength = length;
        fireAnnotationChanged();
    }

    public double getArrowWidth() {
        return this.arrowWidth;
    }

    public void setArrowWidth(double width) {
        this.arrowWidth = width;
        fireAnnotationChanged();
    }

    public Stroke getArrowStroke() {
        return this.arrowStroke;
    }

    public void setArrowStroke(Stroke stroke) {
        ParamChecks.nullNotPermitted(stroke, "stroke");
        this.arrowStroke = stroke;
        fireAnnotationChanged();
    }

    public Paint getArrowPaint() {
        return this.arrowPaint;
    }

    public void setArrowPaint(Paint paint) {
        ParamChecks.nullNotPermitted(paint, "paint");
        this.arrowPaint = paint;
        fireAnnotationChanged();
    }

    public void draw(Graphics2D g2, XYPlot plot, Rectangle2D dataArea, ValueAxis domainAxis, ValueAxis rangeAxis, int rendererIndex, PlotRenderingInfo info) {
        PlotOrientation orientation = plot.getOrientation();
        RectangleEdge domainEdge = Plot.resolveDomainAxisLocation(plot.getDomainAxisLocation(), orientation);
        RectangleEdge rangeEdge = Plot.resolveRangeAxisLocation(plot.getRangeAxisLocation(), orientation);
        double j2DX = domainAxis.valueToJava2D(getX(), dataArea, domainEdge);
        double j2DY = rangeAxis.valueToJava2D(getY(), dataArea, rangeEdge);
        if (orientation == PlotOrientation.HORIZONTAL) {
            double temp = j2DX;
            j2DX = j2DY;
            j2DY = temp;
        }
        double startX = j2DX + (Math.cos(this.angle) * this.baseRadius);
        double startY = j2DY + (Math.sin(this.angle) * this.baseRadius);
        double endX = j2DX + (Math.cos(this.angle) * this.tipRadius);
        double endY = j2DY + (Math.sin(this.angle) * this.tipRadius);
        double arrowBaseX = endX + (Math.cos(this.angle) * this.arrowLength);
        double arrowBaseY = endY + (Math.sin(this.angle) * this.arrowLength);
        double arrowLeftX = arrowBaseX + (Math.cos(this.angle + 1.5707963267948966d) * this.arrowWidth);
        double arrowLeftY = arrowBaseY + (Math.sin(this.angle + 1.5707963267948966d) * this.arrowWidth);
        double arrowRightX = arrowBaseX - (Math.cos(this.angle + 1.5707963267948966d) * this.arrowWidth);
        double arrowRightY = arrowBaseY - (Math.sin(this.angle + 1.5707963267948966d) * this.arrowWidth);
        GeneralPath arrow = new GeneralPath();
        arrow.moveTo((float) endX, (float) endY);
        arrow.lineTo((float) arrowLeftX, (float) arrowLeftY);
        arrow.lineTo((float) arrowRightX, (float) arrowRightY);
        arrow.closePath();
        g2.setStroke(this.arrowStroke);
        g2.setPaint(this.arrowPaint);
        g2.draw(new Double(startX, startY, arrowBaseX, arrowBaseY));
        g2.fill(arrow);
        double labelX = j2DX + (Math.cos(this.angle) * (this.baseRadius + this.labelOffset));
        double labelY = j2DY + (Math.sin(this.angle) * (this.baseRadius + this.labelOffset));
        g2.setFont(getFont());
        Graphics2D graphics2D = g2;
        Shape hotspot = TextUtilities.calculateRotatedStringBounds(getText(), graphics2D, (float) labelX, (float) labelY, getTextAnchor(), getRotationAngle(), getRotationAnchor());
        if (getBackgroundPaint() != null) {
            g2.setPaint(getBackgroundPaint());
            g2.fill(hotspot);
        }
        g2.setPaint(getPaint());
        graphics2D = g2;
        TextUtilities.drawRotatedString(getText(), graphics2D, (float) labelX, (float) labelY, getTextAnchor(), getRotationAngle(), getRotationAnchor());
        if (isOutlineVisible()) {
            g2.setStroke(getOutlineStroke());
            g2.setPaint(getOutlinePaint());
            g2.draw(hotspot);
        }
        String toolTip = getToolTipText();
        String url = getURL();
        if (toolTip != null || url != null) {
            addEntity(info, hotspot, rendererIndex, toolTip, url);
        }
    }

    public boolean equals(Object obj) {
        if (obj == this) {
            return true;
        }
        if (!(obj instanceof XYPointerAnnotation)) {
            return false;
        }
        XYPointerAnnotation that = (XYPointerAnnotation) obj;
        if (this.angle == that.angle && this.tipRadius == that.tipRadius && this.baseRadius == that.baseRadius && this.arrowLength == that.arrowLength && this.arrowWidth == that.arrowWidth && this.arrowPaint.equals(that.arrowPaint) && ObjectUtilities.equal(this.arrowStroke, that.arrowStroke) && this.labelOffset == that.labelOffset) {
            return super.equals(obj);
        }
        return false;
    }

    public int hashCode() {
        int result = super.hashCode();
        long temp = Double.doubleToLongBits(this.angle);
        result = (result * 37) + ((int) ((temp >>> 32) ^ temp));
        temp = Double.doubleToLongBits(this.tipRadius);
        result = (result * 37) + ((int) ((temp >>> 32) ^ temp));
        temp = Double.doubleToLongBits(this.baseRadius);
        result = (result * 37) + ((int) ((temp >>> 32) ^ temp));
        temp = Double.doubleToLongBits(this.arrowLength);
        result = (result * 37) + ((int) ((temp >>> 32) ^ temp));
        temp = Double.doubleToLongBits(this.arrowWidth);
        result = (((((result * 37) + ((int) ((temp >>> 32) ^ temp))) * 37) + HashUtilities.hashCodeForPaint(this.arrowPaint)) * 37) + this.arrowStroke.hashCode();
        temp = Double.doubleToLongBits(this.labelOffset);
        return (result * 37) + ((int) ((temp >>> 32) ^ temp));
    }

    public Object clone() throws CloneNotSupportedException {
        return super.clone();
    }

    private void writeObject(ObjectOutputStream stream) throws IOException {
        stream.defaultWriteObject();
        SerialUtilities.writePaint(this.arrowPaint, stream);
        SerialUtilities.writeStroke(this.arrowStroke, stream);
    }

    private void readObject(ObjectInputStream stream) throws IOException, ClassNotFoundException {
        stream.defaultReadObject();
        this.arrowPaint = SerialUtilities.readPaint(stream);
        this.arrowStroke = SerialUtilities.readStroke(stream);
    }
}
