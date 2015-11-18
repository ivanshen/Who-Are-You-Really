package org.jfree.chart.plot.dial;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.Paint;
import java.awt.Stroke;
import java.awt.geom.Arc2D;
import java.awt.geom.Arc2D.Double;
import java.awt.geom.Line2D;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import org.jfree.chart.annotations.XYPointerAnnotation;
import org.jfree.chart.plot.MeterPlot;
import org.jfree.chart.plot.Plot;
import org.jfree.chart.plot.SpiderWebPlot;
import org.jfree.chart.util.ParamChecks;
import org.jfree.io.SerialUtilities;
import org.jfree.text.TextUtilities;
import org.jfree.ui.TextAnchor;
import org.jfree.util.PaintUtilities;
import org.jfree.util.PublicCloneable;

public class StandardDialScale extends AbstractDialLayer implements DialScale, Cloneable, PublicCloneable, Serializable {
    static final long serialVersionUID = 3715644629665918516L;
    private double extent;
    private boolean firstTickLabelVisible;
    private double lowerBound;
    private double majorTickIncrement;
    private double majorTickLength;
    private transient Paint majorTickPaint;
    private transient Stroke majorTickStroke;
    private int minorTickCount;
    private double minorTickLength;
    private transient Paint minorTickPaint;
    private transient Stroke minorTickStroke;
    private double startAngle;
    private Font tickLabelFont;
    private NumberFormat tickLabelFormatter;
    private double tickLabelOffset;
    private transient Paint tickLabelPaint;
    private boolean tickLabelsVisible;
    private double tickRadius;
    private double upperBound;

    public StandardDialScale() {
        this(0.0d, 100.0d, 175.0d, -170.0d, XYPointerAnnotation.DEFAULT_TIP_RADIUS, 4);
    }

    public StandardDialScale(double lowerBound, double upperBound, double startAngle, double extent, double majorTickIncrement, int minorTickCount) {
        if (majorTickIncrement <= 0.0d) {
            throw new IllegalArgumentException("Requires 'majorTickIncrement' > 0.");
        }
        this.startAngle = startAngle;
        this.extent = extent;
        this.lowerBound = lowerBound;
        this.upperBound = upperBound;
        this.tickRadius = 0.7d;
        this.tickLabelsVisible = true;
        this.tickLabelFormatter = new DecimalFormat("0.0");
        this.firstTickLabelVisible = true;
        this.tickLabelFont = new Font("Dialog", 1, 16);
        this.tickLabelPaint = Color.blue;
        this.tickLabelOffset = SpiderWebPlot.DEFAULT_AXIS_LABEL_GAP;
        this.majorTickIncrement = majorTickIncrement;
        this.majorTickLength = 0.04d;
        this.majorTickPaint = Color.black;
        this.majorTickStroke = new BasicStroke(MeterPlot.DEFAULT_BORDER_SIZE);
        this.minorTickCount = minorTickCount;
        this.minorTickLength = 0.02d;
        this.minorTickPaint = Color.black;
        this.minorTickStroke = new BasicStroke(Plot.DEFAULT_FOREGROUND_ALPHA);
    }

    public double getLowerBound() {
        return this.lowerBound;
    }

    public void setLowerBound(double lower) {
        this.lowerBound = lower;
        notifyListeners(new DialLayerChangeEvent(this));
    }

    public double getUpperBound() {
        return this.upperBound;
    }

    public void setUpperBound(double upper) {
        this.upperBound = upper;
        notifyListeners(new DialLayerChangeEvent(this));
    }

    public double getStartAngle() {
        return this.startAngle;
    }

    public void setStartAngle(double angle) {
        this.startAngle = angle;
        notifyListeners(new DialLayerChangeEvent(this));
    }

    public double getExtent() {
        return this.extent;
    }

    public void setExtent(double extent) {
        this.extent = extent;
        notifyListeners(new DialLayerChangeEvent(this));
    }

    public double getTickRadius() {
        return this.tickRadius;
    }

    public void setTickRadius(double radius) {
        if (radius <= 0.0d) {
            throw new IllegalArgumentException("The 'radius' must be positive.");
        }
        this.tickRadius = radius;
        notifyListeners(new DialLayerChangeEvent(this));
    }

    public double getMajorTickIncrement() {
        return this.majorTickIncrement;
    }

    public void setMajorTickIncrement(double increment) {
        if (increment <= 0.0d) {
            throw new IllegalArgumentException("The 'increment' must be positive.");
        }
        this.majorTickIncrement = increment;
        notifyListeners(new DialLayerChangeEvent(this));
    }

    public double getMajorTickLength() {
        return this.majorTickLength;
    }

    public void setMajorTickLength(double length) {
        if (length < 0.0d) {
            throw new IllegalArgumentException("Negative 'length' argument.");
        }
        this.majorTickLength = length;
        notifyListeners(new DialLayerChangeEvent(this));
    }

    public Paint getMajorTickPaint() {
        return this.majorTickPaint;
    }

    public void setMajorTickPaint(Paint paint) {
        ParamChecks.nullNotPermitted(paint, "paint");
        this.majorTickPaint = paint;
        notifyListeners(new DialLayerChangeEvent(this));
    }

    public Stroke getMajorTickStroke() {
        return this.majorTickStroke;
    }

    public void setMajorTickStroke(Stroke stroke) {
        ParamChecks.nullNotPermitted(stroke, "stroke");
        this.majorTickStroke = stroke;
        notifyListeners(new DialLayerChangeEvent(this));
    }

    public int getMinorTickCount() {
        return this.minorTickCount;
    }

    public void setMinorTickCount(int count) {
        if (count < 0) {
            throw new IllegalArgumentException("The 'count' cannot be negative.");
        }
        this.minorTickCount = count;
        notifyListeners(new DialLayerChangeEvent(this));
    }

    public double getMinorTickLength() {
        return this.minorTickLength;
    }

    public void setMinorTickLength(double length) {
        if (length < 0.0d) {
            throw new IllegalArgumentException("Negative 'length' argument.");
        }
        this.minorTickLength = length;
        notifyListeners(new DialLayerChangeEvent(this));
    }

    public Paint getMinorTickPaint() {
        return this.minorTickPaint;
    }

    public void setMinorTickPaint(Paint paint) {
        ParamChecks.nullNotPermitted(paint, "paint");
        this.minorTickPaint = paint;
        notifyListeners(new DialLayerChangeEvent(this));
    }

    public Stroke getMinorTickStroke() {
        return this.minorTickStroke;
    }

    public void setMinorTickStroke(Stroke stroke) {
        ParamChecks.nullNotPermitted(stroke, "stroke");
        this.minorTickStroke = stroke;
        notifyListeners(new DialLayerChangeEvent(this));
    }

    public double getTickLabelOffset() {
        return this.tickLabelOffset;
    }

    public void setTickLabelOffset(double offset) {
        this.tickLabelOffset = offset;
        notifyListeners(new DialLayerChangeEvent(this));
    }

    public Font getTickLabelFont() {
        return this.tickLabelFont;
    }

    public void setTickLabelFont(Font font) {
        ParamChecks.nullNotPermitted(font, "font");
        this.tickLabelFont = font;
        notifyListeners(new DialLayerChangeEvent(this));
    }

    public Paint getTickLabelPaint() {
        return this.tickLabelPaint;
    }

    public void setTickLabelPaint(Paint paint) {
        ParamChecks.nullNotPermitted(paint, "paint");
        this.tickLabelPaint = paint;
        notifyListeners(new DialLayerChangeEvent(this));
    }

    public boolean getTickLabelsVisible() {
        return this.tickLabelsVisible;
    }

    public void setTickLabelsVisible(boolean visible) {
        this.tickLabelsVisible = visible;
        notifyListeners(new DialLayerChangeEvent(this));
    }

    public NumberFormat getTickLabelFormatter() {
        return this.tickLabelFormatter;
    }

    public void setTickLabelFormatter(NumberFormat formatter) {
        ParamChecks.nullNotPermitted(formatter, "formatter");
        this.tickLabelFormatter = formatter;
        notifyListeners(new DialLayerChangeEvent(this));
    }

    public boolean getFirstTickLabelVisible() {
        return this.firstTickLabelVisible;
    }

    public void setFirstTickLabelVisible(boolean visible) {
        this.firstTickLabelVisible = visible;
        notifyListeners(new DialLayerChangeEvent(this));
    }

    public boolean isClippedToWindow() {
        return true;
    }

    public void draw(Graphics2D g2, DialPlot plot, Rectangle2D frame, Rectangle2D view) {
        Rectangle2D arcRect = DialPlot.rectangleByRadius(frame, this.tickRadius, this.tickRadius);
        Rectangle2D arcRectMajor = DialPlot.rectangleByRadius(frame, this.tickRadius - this.majorTickLength, this.tickRadius - this.majorTickLength);
        Rectangle2D arcRectMinor = arcRect;
        if (this.minorTickCount > 0 && this.minorTickLength > 0.0d) {
            arcRectMinor = DialPlot.rectangleByRadius(frame, this.tickRadius - this.minorTickLength, this.tickRadius - this.minorTickLength);
        }
        Rectangle2D arcRectForLabels = DialPlot.rectangleByRadius(frame, this.tickRadius - this.tickLabelOffset, this.tickRadius - this.tickLabelOffset);
        boolean firstLabel = true;
        Arc2D arc = new Double();
        Line2D workingLine = new Line2D.Double();
        double v = this.lowerBound;
        while (v <= this.upperBound) {
            arc.setArc(arcRect, this.startAngle, valueToAngle(v) - this.startAngle, 0);
            Point2D pt0 = arc.getEndPoint();
            arc.setArc(arcRectMajor, this.startAngle, valueToAngle(v) - this.startAngle, 0);
            Point2D pt1 = arc.getEndPoint();
            g2.setPaint(this.majorTickPaint);
            g2.setStroke(this.majorTickStroke);
            workingLine.setLine(pt0, pt1);
            g2.draw(workingLine);
            arc.setArc(arcRectForLabels, this.startAngle, valueToAngle(v) - this.startAngle, 0);
            Point2D pt2 = arc.getEndPoint();
            if (this.tickLabelsVisible && (!firstLabel || this.firstTickLabelVisible)) {
                g2.setFont(this.tickLabelFont);
                g2.setPaint(this.tickLabelPaint);
                TextUtilities.drawAlignedString(this.tickLabelFormatter.format(v), g2, (float) pt2.getX(), (float) pt2.getY(), TextAnchor.CENTER);
            }
            firstLabel = false;
            if (this.minorTickCount > 0 && this.minorTickLength > 0.0d) {
                double minorTickIncrement = this.majorTickIncrement / ((double) (this.minorTickCount + 1));
                for (int i = 0; i < this.minorTickCount; i++) {
                    double vv = v + (((double) (i + 1)) * minorTickIncrement);
                    if (vv >= this.upperBound) {
                        break;
                    }
                    double angle = valueToAngle(vv);
                    arc.setArc(arcRect, this.startAngle, angle - this.startAngle, 0);
                    pt0 = arc.getEndPoint();
                    arc.setArc(arcRectMinor, this.startAngle, angle - this.startAngle, 0);
                    Point2D pt3 = arc.getEndPoint();
                    g2.setStroke(this.minorTickStroke);
                    g2.setPaint(this.minorTickPaint);
                    workingLine.setLine(pt0, pt3);
                    g2.draw(workingLine);
                }
            }
            v += this.majorTickIncrement;
        }
    }

    public double valueToAngle(double value) {
        return this.startAngle + ((value - this.lowerBound) * (this.extent / (this.upperBound - this.lowerBound)));
    }

    public double angleToValue(double angle) {
        return (angle - this.startAngle) * ((this.upperBound - this.lowerBound) / this.extent);
    }

    public boolean equals(Object obj) {
        if (obj == this) {
            return true;
        }
        if (!(obj instanceof StandardDialScale)) {
            return false;
        }
        StandardDialScale that = (StandardDialScale) obj;
        if (this.lowerBound == that.lowerBound && this.upperBound == that.upperBound && this.startAngle == that.startAngle && this.extent == that.extent && this.tickRadius == that.tickRadius && this.majorTickIncrement == that.majorTickIncrement && this.majorTickLength == that.majorTickLength && PaintUtilities.equal(this.majorTickPaint, that.majorTickPaint) && this.majorTickStroke.equals(that.majorTickStroke) && this.minorTickCount == that.minorTickCount && this.minorTickLength == that.minorTickLength && PaintUtilities.equal(this.minorTickPaint, that.minorTickPaint) && this.minorTickStroke.equals(that.minorTickStroke) && this.tickLabelsVisible == that.tickLabelsVisible && this.tickLabelOffset == that.tickLabelOffset && this.tickLabelFont.equals(that.tickLabelFont) && PaintUtilities.equal(this.tickLabelPaint, that.tickLabelPaint)) {
            return super.equals(obj);
        }
        return false;
    }

    public int hashCode() {
        long temp = Double.doubleToLongBits(this.lowerBound);
        int result = ((int) ((temp >>> 32) ^ temp)) + 7141;
        temp = Double.doubleToLongBits(this.upperBound);
        result = (result * 37) + ((int) ((temp >>> 32) ^ temp));
        temp = Double.doubleToLongBits(this.startAngle);
        result = (result * 37) + ((int) ((temp >>> 32) ^ temp));
        temp = Double.doubleToLongBits(this.extent);
        result = (result * 37) + ((int) ((temp >>> 32) ^ temp));
        temp = Double.doubleToLongBits(this.tickRadius);
        return (result * 37) + ((int) ((temp >>> 32) ^ temp));
    }

    public Object clone() throws CloneNotSupportedException {
        return super.clone();
    }

    private void writeObject(ObjectOutputStream stream) throws IOException {
        stream.defaultWriteObject();
        SerialUtilities.writePaint(this.majorTickPaint, stream);
        SerialUtilities.writeStroke(this.majorTickStroke, stream);
        SerialUtilities.writePaint(this.minorTickPaint, stream);
        SerialUtilities.writeStroke(this.minorTickStroke, stream);
        SerialUtilities.writePaint(this.tickLabelPaint, stream);
    }

    private void readObject(ObjectInputStream stream) throws IOException, ClassNotFoundException {
        stream.defaultReadObject();
        this.majorTickPaint = SerialUtilities.readPaint(stream);
        this.majorTickStroke = SerialUtilities.readStroke(stream);
        this.minorTickPaint = SerialUtilities.readPaint(stream);
        this.minorTickStroke = SerialUtilities.readStroke(stream);
        this.tickLabelPaint = SerialUtilities.readPaint(stream);
    }
}
