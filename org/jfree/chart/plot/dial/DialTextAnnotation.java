package org.jfree.chart.plot.dial;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.Paint;
import java.awt.geom.Arc2D.Double;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import org.jfree.chart.HashUtilities;
import org.jfree.chart.plot.PolarPlot;
import org.jfree.chart.util.ParamChecks;
import org.jfree.io.SerialUtilities;
import org.jfree.text.TextUtilities;
import org.jfree.ui.TextAnchor;
import org.jfree.util.PaintUtilities;
import org.jfree.util.PublicCloneable;

public class DialTextAnnotation extends AbstractDialLayer implements DialLayer, Cloneable, PublicCloneable, Serializable {
    static final long serialVersionUID = 3065267524054428071L;
    private TextAnchor anchor;
    private double angle;
    private Font font;
    private String label;
    private transient Paint paint;
    private double radius;

    public DialTextAnnotation(String label) {
        ParamChecks.nullNotPermitted(label, "label");
        this.angle = PolarPlot.DEFAULT_ANGLE_OFFSET;
        this.radius = 0.3d;
        this.font = new Font("Dialog", 1, 14);
        this.paint = Color.black;
        this.label = label;
        this.anchor = TextAnchor.TOP_CENTER;
    }

    public String getLabel() {
        return this.label;
    }

    public void setLabel(String label) {
        ParamChecks.nullNotPermitted(label, "label");
        this.label = label;
        notifyListeners(new DialLayerChangeEvent(this));
    }

    public Font getFont() {
        return this.font;
    }

    public void setFont(Font font) {
        ParamChecks.nullNotPermitted(font, "font");
        this.font = font;
        notifyListeners(new DialLayerChangeEvent(this));
    }

    public Paint getPaint() {
        return this.paint;
    }

    public void setPaint(Paint paint) {
        ParamChecks.nullNotPermitted(paint, "paint");
        this.paint = paint;
        notifyListeners(new DialLayerChangeEvent(this));
    }

    public double getAngle() {
        return this.angle;
    }

    public void setAngle(double angle) {
        this.angle = angle;
        notifyListeners(new DialLayerChangeEvent(this));
    }

    public double getRadius() {
        return this.radius;
    }

    public void setRadius(double radius) {
        if (radius < 0.0d) {
            throw new IllegalArgumentException("The 'radius' cannot be negative.");
        }
        this.radius = radius;
        notifyListeners(new DialLayerChangeEvent(this));
    }

    public TextAnchor getAnchor() {
        return this.anchor;
    }

    public void setAnchor(TextAnchor anchor) {
        ParamChecks.nullNotPermitted(anchor, "anchor");
        this.anchor = anchor;
        notifyListeners(new DialLayerChangeEvent(this));
    }

    public boolean isClippedToWindow() {
        return true;
    }

    public void draw(Graphics2D g2, DialPlot plot, Rectangle2D frame, Rectangle2D view) {
        Point2D pt = new Double(DialPlot.rectangleByRadius(frame, this.radius, this.radius), this.angle, 0.0d, 0).getStartPoint();
        g2.setPaint(this.paint);
        g2.setFont(this.font);
        TextUtilities.drawAlignedString(this.label, g2, (float) pt.getX(), (float) pt.getY(), this.anchor);
    }

    public boolean equals(Object obj) {
        if (obj == this) {
            return true;
        }
        if (!(obj instanceof DialTextAnnotation)) {
            return false;
        }
        DialTextAnnotation that = (DialTextAnnotation) obj;
        if (this.label.equals(that.label) && this.font.equals(that.font) && PaintUtilities.equal(this.paint, that.paint) && this.radius == that.radius && this.angle == that.angle && this.anchor.equals(that.anchor)) {
            return super.equals(obj);
        }
        return false;
    }

    public int hashCode() {
        int result = ((((((HashUtilities.hashCodeForPaint(this.paint) + 7141) * 37) + this.font.hashCode()) * 37) + this.label.hashCode()) * 37) + this.anchor.hashCode();
        long temp = Double.doubleToLongBits(this.angle);
        result = (result * 37) + ((int) ((temp >>> 32) ^ temp));
        temp = Double.doubleToLongBits(this.radius);
        return (result * 37) + ((int) ((temp >>> 32) ^ temp));
    }

    public Object clone() throws CloneNotSupportedException {
        return super.clone();
    }

    private void writeObject(ObjectOutputStream stream) throws IOException {
        stream.defaultWriteObject();
        SerialUtilities.writePaint(this.paint, stream);
    }

    private void readObject(ObjectInputStream stream) throws IOException, ClassNotFoundException {
        stream.defaultReadObject();
        this.paint = SerialUtilities.readPaint(stream);
    }
}
