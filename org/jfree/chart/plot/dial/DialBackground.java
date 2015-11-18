package org.jfree.chart.plot.dial;

import java.awt.Color;
import java.awt.GradientPaint;
import java.awt.Graphics2D;
import java.awt.Paint;
import java.awt.geom.Rectangle2D;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import org.jfree.chart.HashUtilities;
import org.jfree.chart.util.ParamChecks;
import org.jfree.io.SerialUtilities;
import org.jfree.ui.GradientPaintTransformer;
import org.jfree.ui.StandardGradientPaintTransformer;
import org.jfree.util.PaintUtilities;
import org.jfree.util.PublicCloneable;

public class DialBackground extends AbstractDialLayer implements DialLayer, Cloneable, PublicCloneable, Serializable {
    static final long serialVersionUID = -9019069533317612375L;
    private GradientPaintTransformer gradientPaintTransformer;
    private transient Paint paint;

    public DialBackground() {
        this(Color.white);
    }

    public DialBackground(Paint paint) {
        ParamChecks.nullNotPermitted(paint, "paint");
        this.paint = paint;
        this.gradientPaintTransformer = new StandardGradientPaintTransformer();
    }

    public Paint getPaint() {
        return this.paint;
    }

    public void setPaint(Paint paint) {
        ParamChecks.nullNotPermitted(paint, "paint");
        this.paint = paint;
        notifyListeners(new DialLayerChangeEvent(this));
    }

    public GradientPaintTransformer getGradientPaintTransformer() {
        return this.gradientPaintTransformer;
    }

    public void setGradientPaintTransformer(GradientPaintTransformer t) {
        ParamChecks.nullNotPermitted(t, "t");
        this.gradientPaintTransformer = t;
        notifyListeners(new DialLayerChangeEvent(this));
    }

    public boolean isClippedToWindow() {
        return true;
    }

    public void draw(Graphics2D g2, DialPlot plot, Rectangle2D frame, Rectangle2D view) {
        Paint p = this.paint;
        if (p instanceof GradientPaint) {
            p = this.gradientPaintTransformer.transform((GradientPaint) p, view);
        }
        g2.setPaint(p);
        g2.fill(view);
    }

    public boolean equals(Object obj) {
        if (obj == this) {
            return true;
        }
        if (!(obj instanceof DialBackground)) {
            return false;
        }
        DialBackground that = (DialBackground) obj;
        if (PaintUtilities.equal(this.paint, that.paint) && this.gradientPaintTransformer.equals(that.gradientPaintTransformer)) {
            return super.equals(obj);
        }
        return false;
    }

    public int hashCode() {
        return ((HashUtilities.hashCodeForPaint(this.paint) + 7141) * 37) + this.gradientPaintTransformer.hashCode();
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
