package org.jfree.chart.annotations;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Paint;
import java.awt.Stroke;
import java.awt.geom.GeneralPath;
import java.awt.geom.Line2D.Double;
import java.awt.geom.Rectangle2D;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import org.jfree.chart.HashUtilities;
import org.jfree.chart.axis.CategoryAxis;
import org.jfree.chart.axis.ValueAxis;
import org.jfree.chart.plot.CategoryPlot;
import org.jfree.chart.plot.Plot;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.util.ParamChecks;
import org.jfree.data.category.CategoryDataset;
import org.jfree.io.SerialUtilities;
import org.jfree.text.TextUtilities;
import org.jfree.ui.RectangleEdge;
import org.jfree.util.ObjectUtilities;
import org.jfree.util.PublicCloneable;

public class CategoryPointerAnnotation extends CategoryTextAnnotation implements Cloneable, PublicCloneable, Serializable {
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

    public CategoryPointerAnnotation(String label, Comparable key, double value, double angle) {
        super(label, key, value);
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

    public void draw(Graphics2D g2, CategoryPlot plot, Rectangle2D dataArea, CategoryAxis domainAxis, ValueAxis rangeAxis) {
        PlotOrientation orientation = plot.getOrientation();
        RectangleEdge domainEdge = Plot.resolveDomainAxisLocation(plot.getDomainAxisLocation(), orientation);
        RectangleEdge rangeEdge = Plot.resolveRangeAxisLocation(plot.getRangeAxisLocation(), orientation);
        CategoryDataset dataset = plot.getDataset();
        double j2DX = domainAxis.getCategoryMiddle(dataset.getColumnIndex(getCategory()), dataset.getColumnCount(), dataArea, domainEdge);
        double j2DY = rangeAxis.valueToJava2D(getValue(), dataArea, rangeEdge);
        if (orientation == PlotOrientation.HORIZONTAL) {
            double temp = j2DX;
            j2DX = j2DY;
            j2DY = temp;
        }
        double d = this.angle;
        double startX = j2DX + (Math.cos(r0) * this.baseRadius);
        d = this.angle;
        double startY = j2DY + (Math.sin(r0) * this.baseRadius);
        d = this.angle;
        double endX = j2DX + (Math.cos(r0) * this.tipRadius);
        d = this.angle;
        double endY = j2DY + (Math.sin(r0) * this.tipRadius);
        d = this.angle;
        double arrowBaseX = endX + (Math.cos(r0) * this.arrowLength);
        d = this.angle;
        double arrowBaseY = endY + (Math.sin(r0) * this.arrowLength);
        d = this.angle;
        double arrowLeftX = arrowBaseX + (Math.cos(r0 + 1.5707963267948966d) * this.arrowWidth);
        d = this.angle;
        double arrowLeftY = arrowBaseY + (Math.sin(r0 + 1.5707963267948966d) * this.arrowWidth);
        d = this.angle;
        double arrowRightX = arrowBaseX - (Math.cos(r0 + 1.5707963267948966d) * this.arrowWidth);
        d = this.angle;
        double arrowRightY = arrowBaseY - (Math.sin(r0 + 1.5707963267948966d) * this.arrowWidth);
        GeneralPath arrow = new GeneralPath();
        arrow.moveTo((float) endX, (float) endY);
        arrow.lineTo((float) arrowLeftX, (float) arrowLeftY);
        arrow.lineTo((float) arrowRightX, (float) arrowRightY);
        arrow.closePath();
        g2.setStroke(this.arrowStroke);
        g2.setPaint(this.arrowPaint);
        g2.draw(new Double(startX, startY, arrowBaseX, arrowBaseY));
        g2.fill(arrow);
        g2.setFont(getFont());
        g2.setPaint(getPaint());
        double labelX = j2DX + (Math.cos(this.angle) * (this.baseRadius + this.labelOffset));
        double labelY = j2DY + (Math.sin(this.angle) * (this.baseRadius + this.labelOffset));
        TextUtilities.drawAlignedString(getText(), g2, (float) labelX, (float) labelY, getTextAnchor());
    }

    public boolean equals(Object obj) {
        if (obj == this) {
            return true;
        }
        if (!(obj instanceof CategoryPointerAnnotation)) {
            return false;
        }
        if (!super.equals(obj)) {
            return false;
        }
        CategoryPointerAnnotation that = (CategoryPointerAnnotation) obj;
        if (this.angle != that.angle) {
            return false;
        }
        if (this.tipRadius != that.tipRadius) {
            return false;
        }
        if (this.baseRadius != that.baseRadius) {
            return false;
        }
        if (this.arrowLength != that.arrowLength) {
            return false;
        }
        if (this.arrowWidth != that.arrowWidth) {
            return false;
        }
        if (!this.arrowPaint.equals(that.arrowPaint)) {
            return false;
        }
        if (!ObjectUtilities.equal(this.arrowStroke, that.arrowStroke)) {
            return false;
        }
        if (this.labelOffset != that.labelOffset) {
            return false;
        }
        return true;
    }

    public int hashCode() {
        long temp = Double.doubleToLongBits(this.angle);
        int result = ((int) ((temp >>> 32) ^ temp)) + 7141;
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
