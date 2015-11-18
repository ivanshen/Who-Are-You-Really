package org.jfree.chart.needle;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Paint;
import java.awt.Shape;
import java.awt.Stroke;
import java.awt.geom.AffineTransform;
import java.awt.geom.Point2D;
import java.awt.geom.Point2D.Double;
import java.awt.geom.Rectangle2D;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import org.jfree.chart.HashUtilities;
import org.jfree.chart.axis.Axis;
import org.jfree.io.SerialUtilities;
import org.jfree.util.ObjectUtilities;
import org.jfree.util.PaintUtilities;

public abstract class MeterNeedle implements Serializable {
    private static final long serialVersionUID = 5203064851510951052L;
    protected static AffineTransform transform;
    private transient Paint fillPaint;
    private transient Paint highlightPaint;
    private transient Paint outlinePaint;
    private transient Stroke outlineStroke;
    private double rotateX;
    private double rotateY;
    private int size;

    protected abstract void drawNeedle(Graphics2D graphics2D, Rectangle2D rectangle2D, Point2D point2D, double d);

    static {
        transform = new AffineTransform();
    }

    public MeterNeedle() {
        this(null, null, null);
    }

    public MeterNeedle(Paint outline, Paint fill, Paint highlight) {
        this.outlinePaint = Color.black;
        this.outlineStroke = new BasicStroke(Axis.DEFAULT_TICK_MARK_OUTSIDE_LENGTH);
        this.fillPaint = null;
        this.highlightPaint = null;
        this.size = 5;
        this.rotateX = 0.5d;
        this.rotateY = 0.5d;
        this.fillPaint = fill;
        this.highlightPaint = highlight;
        this.outlinePaint = outline;
    }

    public Paint getOutlinePaint() {
        return this.outlinePaint;
    }

    public void setOutlinePaint(Paint p) {
        if (p != null) {
            this.outlinePaint = p;
        }
    }

    public Stroke getOutlineStroke() {
        return this.outlineStroke;
    }

    public void setOutlineStroke(Stroke s) {
        if (s != null) {
            this.outlineStroke = s;
        }
    }

    public Paint getFillPaint() {
        return this.fillPaint;
    }

    public void setFillPaint(Paint p) {
        if (p != null) {
            this.fillPaint = p;
        }
    }

    public Paint getHighlightPaint() {
        return this.highlightPaint;
    }

    public void setHighlightPaint(Paint p) {
        if (p != null) {
            this.highlightPaint = p;
        }
    }

    public double getRotateX() {
        return this.rotateX;
    }

    public void setRotateX(double x) {
        this.rotateX = x;
    }

    public void setRotateY(double y) {
        this.rotateY = y;
    }

    public double getRotateY() {
        return this.rotateY;
    }

    public void draw(Graphics2D g2, Rectangle2D plotArea) {
        draw(g2, plotArea, 0.0d);
    }

    public void draw(Graphics2D g2, Rectangle2D plotArea, double angle) {
        Double pt = new Double();
        pt.setLocation(plotArea.getMinX() + (this.rotateX * plotArea.getWidth()), plotArea.getMinY() + (this.rotateY * plotArea.getHeight()));
        draw(g2, plotArea, pt, angle);
    }

    public void draw(Graphics2D g2, Rectangle2D plotArea, Point2D rotate, double angle) {
        Paint savePaint = g2.getColor();
        Stroke saveStroke = g2.getStroke();
        drawNeedle(g2, plotArea, rotate, Math.toRadians(angle));
        g2.setStroke(saveStroke);
        g2.setPaint(savePaint);
    }

    protected void defaultDisplay(Graphics2D g2, Shape shape) {
        if (this.fillPaint != null) {
            g2.setPaint(this.fillPaint);
            g2.fill(shape);
        }
        if (this.outlinePaint != null) {
            g2.setStroke(this.outlineStroke);
            g2.setPaint(this.outlinePaint);
            g2.draw(shape);
        }
    }

    public int getSize() {
        return this.size;
    }

    public void setSize(int pixels) {
        this.size = pixels;
    }

    public AffineTransform getTransform() {
        return transform;
    }

    public boolean equals(Object obj) {
        if (obj == this) {
            return true;
        }
        if (!(obj instanceof MeterNeedle)) {
            return false;
        }
        MeterNeedle that = (MeterNeedle) obj;
        if (!PaintUtilities.equal(this.outlinePaint, that.outlinePaint)) {
            return false;
        }
        if (!ObjectUtilities.equal(this.outlineStroke, that.outlineStroke)) {
            return false;
        }
        if (!PaintUtilities.equal(this.fillPaint, that.fillPaint)) {
            return false;
        }
        if (!PaintUtilities.equal(this.highlightPaint, that.highlightPaint)) {
            return false;
        }
        if (this.size != that.size) {
            return false;
        }
        if (this.rotateX != that.rotateX) {
            return false;
        }
        if (this.rotateY != that.rotateY) {
            return false;
        }
        return true;
    }

    public int hashCode() {
        return HashUtilities.hashCode(HashUtilities.hashCode(HashUtilities.hashCode(HashUtilities.hashCode(HashUtilities.hashCode(HashUtilities.hashCode(HashUtilities.hashCode(193, this.fillPaint), this.highlightPaint), this.outlinePaint), this.outlineStroke), this.rotateX), this.rotateY), this.size);
    }

    private void writeObject(ObjectOutputStream stream) throws IOException {
        stream.defaultWriteObject();
        SerialUtilities.writeStroke(this.outlineStroke, stream);
        SerialUtilities.writePaint(this.outlinePaint, stream);
        SerialUtilities.writePaint(this.fillPaint, stream);
        SerialUtilities.writePaint(this.highlightPaint, stream);
    }

    private void readObject(ObjectInputStream stream) throws IOException, ClassNotFoundException {
        stream.defaultReadObject();
        this.outlineStroke = SerialUtilities.readStroke(stream);
        this.outlinePaint = SerialUtilities.readPaint(stream);
        this.fillPaint = SerialUtilities.readPaint(stream);
        this.highlightPaint = SerialUtilities.readPaint(stream);
    }
}
