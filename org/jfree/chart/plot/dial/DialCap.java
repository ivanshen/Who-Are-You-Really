package org.jfree.chart.plot.dial;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Paint;
import java.awt.Stroke;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Ellipse2D.Double;
import java.awt.geom.Rectangle2D;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import org.jfree.chart.HashUtilities;
import org.jfree.chart.axis.Axis;
import org.jfree.chart.axis.ValueAxis;
import org.jfree.chart.util.ParamChecks;
import org.jfree.io.SerialUtilities;
import org.jfree.util.PaintUtilities;
import org.jfree.util.PublicCloneable;

public class DialCap extends AbstractDialLayer implements DialLayer, Cloneable, PublicCloneable, Serializable {
    static final long serialVersionUID = -2929484264982524463L;
    private transient Paint fillPaint;
    private transient Paint outlinePaint;
    private transient Stroke outlineStroke;
    private double radius;

    public DialCap() {
        this.radius = ValueAxis.DEFAULT_UPPER_MARGIN;
        this.fillPaint = Color.white;
        this.outlinePaint = Color.black;
        this.outlineStroke = new BasicStroke(Axis.DEFAULT_TICK_MARK_OUTSIDE_LENGTH);
    }

    public double getRadius() {
        return this.radius;
    }

    public void setRadius(double radius) {
        if (radius <= 0.0d) {
            throw new IllegalArgumentException("Requires radius > 0.0.");
        }
        this.radius = radius;
        notifyListeners(new DialLayerChangeEvent(this));
    }

    public Paint getFillPaint() {
        return this.fillPaint;
    }

    public void setFillPaint(Paint paint) {
        ParamChecks.nullNotPermitted(paint, "paint");
        this.fillPaint = paint;
        notifyListeners(new DialLayerChangeEvent(this));
    }

    public Paint getOutlinePaint() {
        return this.outlinePaint;
    }

    public void setOutlinePaint(Paint paint) {
        ParamChecks.nullNotPermitted(paint, "paint");
        this.outlinePaint = paint;
        notifyListeners(new DialLayerChangeEvent(this));
    }

    public Stroke getOutlineStroke() {
        return this.outlineStroke;
    }

    public void setOutlineStroke(Stroke stroke) {
        ParamChecks.nullNotPermitted(stroke, "stroke");
        this.outlineStroke = stroke;
        notifyListeners(new DialLayerChangeEvent(this));
    }

    public boolean isClippedToWindow() {
        return true;
    }

    public void draw(Graphics2D g2, DialPlot plot, Rectangle2D frame, Rectangle2D view) {
        g2.setPaint(this.fillPaint);
        Rectangle2D f = DialPlot.rectangleByRadius(frame, this.radius, this.radius);
        Ellipse2D e = new Double(f.getX(), f.getY(), f.getWidth(), f.getHeight());
        g2.fill(e);
        g2.setPaint(this.outlinePaint);
        g2.setStroke(this.outlineStroke);
        g2.draw(e);
    }

    public boolean equals(Object obj) {
        if (obj == this) {
            return true;
        }
        if (!(obj instanceof DialCap)) {
            return false;
        }
        DialCap that = (DialCap) obj;
        if (this.radius == that.radius && PaintUtilities.equal(this.fillPaint, that.fillPaint) && PaintUtilities.equal(this.outlinePaint, that.outlinePaint) && this.outlineStroke.equals(that.outlineStroke)) {
            return super.equals(obj);
        }
        return false;
    }

    public int hashCode() {
        return ((((HashUtilities.hashCodeForPaint(this.fillPaint) + 7141) * 37) + HashUtilities.hashCodeForPaint(this.outlinePaint)) * 37) + this.outlineStroke.hashCode();
    }

    public Object clone() throws CloneNotSupportedException {
        return super.clone();
    }

    private void writeObject(ObjectOutputStream stream) throws IOException {
        stream.defaultWriteObject();
        SerialUtilities.writePaint(this.fillPaint, stream);
        SerialUtilities.writePaint(this.outlinePaint, stream);
        SerialUtilities.writeStroke(this.outlineStroke, stream);
    }

    private void readObject(ObjectInputStream stream) throws IOException, ClassNotFoundException {
        stream.defaultReadObject();
        this.fillPaint = SerialUtilities.readPaint(stream);
        this.outlinePaint = SerialUtilities.readPaint(stream);
        this.outlineStroke = SerialUtilities.readStroke(stream);
    }
}
