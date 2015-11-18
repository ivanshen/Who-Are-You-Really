package org.jfree.chart.plot.dial;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Paint;
import java.awt.Shape;
import java.awt.Stroke;
import java.awt.geom.Area;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Ellipse2D.Double;
import java.awt.geom.Rectangle2D;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import org.jfree.chart.HashUtilities;
import org.jfree.chart.axis.Axis;
import org.jfree.chart.util.ParamChecks;
import org.jfree.io.SerialUtilities;
import org.jfree.util.PaintUtilities;
import org.jfree.util.PublicCloneable;

public class StandardDialFrame extends AbstractDialLayer implements DialFrame, Cloneable, PublicCloneable, Serializable {
    static final long serialVersionUID = 1016585407507121596L;
    private transient Paint backgroundPaint;
    private transient Paint foregroundPaint;
    private double radius;
    private transient Stroke stroke;

    public StandardDialFrame() {
        this.backgroundPaint = Color.gray;
        this.foregroundPaint = Color.black;
        this.stroke = new BasicStroke(Axis.DEFAULT_TICK_MARK_OUTSIDE_LENGTH);
        this.radius = 0.95d;
    }

    public double getRadius() {
        return this.radius;
    }

    public void setRadius(double radius) {
        if (radius <= 0.0d) {
            throw new IllegalArgumentException("The 'radius' must be positive.");
        }
        this.radius = radius;
        notifyListeners(new DialLayerChangeEvent(this));
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

    public Shape getWindow(Rectangle2D frame) {
        Rectangle2D f = DialPlot.rectangleByRadius(frame, this.radius, this.radius);
        return new Double(f.getX(), f.getY(), f.getWidth(), f.getHeight());
    }

    public boolean isClippedToWindow() {
        return false;
    }

    public void draw(Graphics2D g2, DialPlot plot, Rectangle2D frame, Rectangle2D view) {
        Shape window = getWindow(frame);
        Rectangle2D f = DialPlot.rectangleByRadius(frame, this.radius + 0.02d, this.radius + 0.02d);
        Ellipse2D e = new Double(f.getX(), f.getY(), f.getWidth(), f.getHeight());
        Area area = new Area(e);
        area.subtract(new Area(window));
        g2.setPaint(this.backgroundPaint);
        g2.fill(area);
        g2.setStroke(this.stroke);
        g2.setPaint(this.foregroundPaint);
        g2.draw(window);
        g2.draw(e);
    }

    public boolean equals(Object obj) {
        if (obj == this) {
            return true;
        }
        if (!(obj instanceof StandardDialFrame)) {
            return false;
        }
        StandardDialFrame that = (StandardDialFrame) obj;
        if (PaintUtilities.equal(this.backgroundPaint, that.backgroundPaint) && PaintUtilities.equal(this.foregroundPaint, that.foregroundPaint) && this.radius == that.radius && this.stroke.equals(that.stroke)) {
            return super.equals(obj);
        }
        return false;
    }

    public int hashCode() {
        long temp = Double.doubleToLongBits(this.radius);
        return ((((((((int) ((temp >>> 32) ^ temp)) + 7141) * 37) + HashUtilities.hashCodeForPaint(this.backgroundPaint)) * 37) + HashUtilities.hashCodeForPaint(this.foregroundPaint)) * 37) + this.stroke.hashCode();
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
