package org.jfree.chart.plot.dial;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Paint;
import java.awt.Shape;
import java.awt.Stroke;
import java.awt.geom.Arc2D;
import java.awt.geom.Arc2D.Double;
import java.awt.geom.Area;
import java.awt.geom.GeneralPath;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import org.jfree.chart.HashUtilities;
import org.jfree.chart.axis.Axis;
import org.jfree.chart.axis.DateAxis;
import org.jfree.chart.plot.SpiderWebPlot;
import org.jfree.chart.util.ParamChecks;
import org.jfree.io.SerialUtilities;
import org.jfree.util.PaintUtilities;
import org.jfree.util.PublicCloneable;

public class ArcDialFrame extends AbstractDialLayer implements DialFrame, Cloneable, PublicCloneable, Serializable {
    static final long serialVersionUID = -4089176959553523499L;
    private transient Paint backgroundPaint;
    private double extent;
    private transient Paint foregroundPaint;
    private double innerRadius;
    private double outerRadius;
    private double startAngle;
    private transient Stroke stroke;

    public ArcDialFrame() {
        this(0.0d, 180.0d);
    }

    public ArcDialFrame(double startAngle, double extent) {
        this.backgroundPaint = Color.gray;
        this.foregroundPaint = new Color(100, 100, 150);
        this.stroke = new BasicStroke(Axis.DEFAULT_TICK_MARK_OUTSIDE_LENGTH);
        this.innerRadius = SpiderWebPlot.DEFAULT_INTERIOR_GAP;
        this.outerRadius = 0.75d;
        this.startAngle = startAngle;
        this.extent = extent;
    }

    public Paint getBackgroundPaint() {
        return this.backgroundPaint;
    }

    public void setBackgroundPaint(Paint paint) {
        ParamChecks.nullNotPermitted(paint, "paint");
        this.backgroundPaint = paint;
        notifyListeners(new DialLayerChangeEvent(this));
    }

    public Paint getForegroundPaint() {
        return this.foregroundPaint;
    }

    public void setForegroundPaint(Paint paint) {
        ParamChecks.nullNotPermitted(paint, "paint");
        this.foregroundPaint = paint;
        notifyListeners(new DialLayerChangeEvent(this));
    }

    public Stroke getStroke() {
        return this.stroke;
    }

    public void setStroke(Stroke stroke) {
        ParamChecks.nullNotPermitted(stroke, "stroke");
        this.stroke = stroke;
        notifyListeners(new DialLayerChangeEvent(this));
    }

    public double getInnerRadius() {
        return this.innerRadius;
    }

    public void setInnerRadius(double radius) {
        if (radius < 0.0d) {
            throw new IllegalArgumentException("Negative 'radius' argument.");
        }
        this.innerRadius = radius;
        notifyListeners(new DialLayerChangeEvent(this));
    }

    public double getOuterRadius() {
        return this.outerRadius;
    }

    public void setOuterRadius(double radius) {
        if (radius < 0.0d) {
            throw new IllegalArgumentException("Negative 'radius' argument.");
        }
        this.outerRadius = radius;
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

    public Shape getWindow(Rectangle2D frame) {
        Rectangle2D innerFrame = DialPlot.rectangleByRadius(frame, this.innerRadius, this.innerRadius);
        Rectangle2D outerFrame = DialPlot.rectangleByRadius(frame, this.outerRadius, this.outerRadius);
        Arc2D inner = new Double(innerFrame, this.startAngle, this.extent, 0);
        Arc2D outer = new Double(outerFrame, this.startAngle + this.extent, -this.extent, 0);
        GeneralPath p = new GeneralPath();
        Point2D point1 = inner.getStartPoint();
        p.moveTo((float) point1.getX(), (float) point1.getY());
        p.append(inner, true);
        p.append(outer, true);
        p.closePath();
        return p;
    }

    protected Shape getOuterWindow(Rectangle2D frame) {
        Rectangle2D innerFrame = DialPlot.rectangleByRadius(frame, this.innerRadius - 0.02d, this.innerRadius - 0.02d);
        Rectangle2D outerFrame = DialPlot.rectangleByRadius(frame, this.outerRadius + 0.02d, this.outerRadius + 0.02d);
        Arc2D inner = new Double(innerFrame, this.startAngle - 1.5d, this.extent + (DateAxis.DEFAULT_AUTO_RANGE_MINIMUM_SIZE_IN_MILLISECONDS * 1.5d), 0);
        Arc2D outer = new Double(outerFrame, (this.startAngle + 1.5d) + this.extent, (-this.extent) - (DateAxis.DEFAULT_AUTO_RANGE_MINIMUM_SIZE_IN_MILLISECONDS * 1.5d), 0);
        GeneralPath p = new GeneralPath();
        Point2D point1 = inner.getStartPoint();
        p.moveTo((float) point1.getX(), (float) point1.getY());
        p.append(inner, true);
        p.append(outer, true);
        p.closePath();
        return p;
    }

    public void draw(Graphics2D g2, DialPlot plot, Rectangle2D frame, Rectangle2D view) {
        Shape window = getWindow(frame);
        Shape outerWindow = getOuterWindow(frame);
        Area area1 = new Area(outerWindow);
        area1.subtract(new Area(window));
        g2.setPaint(Color.lightGray);
        g2.fill(area1);
        g2.setStroke(this.stroke);
        g2.setPaint(this.foregroundPaint);
        g2.draw(window);
        g2.draw(outerWindow);
    }

    public boolean isClippedToWindow() {
        return false;
    }

    public boolean equals(Object obj) {
        if (obj == this) {
            return true;
        }
        if (!(obj instanceof ArcDialFrame)) {
            return false;
        }
        ArcDialFrame that = (ArcDialFrame) obj;
        if (PaintUtilities.equal(this.backgroundPaint, that.backgroundPaint) && PaintUtilities.equal(this.foregroundPaint, that.foregroundPaint) && this.startAngle == that.startAngle && this.extent == that.extent && this.innerRadius == that.innerRadius && this.outerRadius == that.outerRadius && this.stroke.equals(that.stroke)) {
            return super.equals(obj);
        }
        return false;
    }

    public int hashCode() {
        long temp = Double.doubleToLongBits(this.startAngle);
        int result = ((int) ((temp >>> 32) ^ temp)) + 7141;
        temp = Double.doubleToLongBits(this.extent);
        result = (result * 37) + ((int) ((temp >>> 32) ^ temp));
        temp = Double.doubleToLongBits(this.innerRadius);
        result = (result * 37) + ((int) ((temp >>> 32) ^ temp));
        temp = Double.doubleToLongBits(this.outerRadius);
        return (((((((result * 37) + ((int) ((temp >>> 32) ^ temp))) * 37) + HashUtilities.hashCodeForPaint(this.backgroundPaint)) * 37) + HashUtilities.hashCodeForPaint(this.foregroundPaint)) * 37) + this.stroke.hashCode();
    }

    public Object clone() throws CloneNotSupportedException {
        return super.clone();
    }

    private void writeObject(ObjectOutputStream stream) throws IOException {
        stream.defaultWriteObject();
        SerialUtilities.writePaint(this.backgroundPaint, stream);
        SerialUtilities.writePaint(this.foregroundPaint, stream);
        SerialUtilities.writeStroke(this.stroke, stream);
    }

    private void readObject(ObjectInputStream stream) throws IOException, ClassNotFoundException {
        stream.defaultReadObject();
        this.backgroundPaint = SerialUtilities.readPaint(stream);
        this.foregroundPaint = SerialUtilities.readPaint(stream);
        this.stroke = SerialUtilities.readStroke(stream);
    }
}
