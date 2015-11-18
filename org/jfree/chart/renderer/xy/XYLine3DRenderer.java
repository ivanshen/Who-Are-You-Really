package org.jfree.chart.renderer.xy;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Paint;
import java.awt.Shape;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import org.jfree.chart.Effect3D;
import org.jfree.io.SerialUtilities;
import org.jfree.util.PaintUtilities;

public class XYLine3DRenderer extends XYLineAndShapeRenderer implements Effect3D, Serializable {
    public static final Paint DEFAULT_WALL_PAINT;
    public static final double DEFAULT_X_OFFSET = 12.0d;
    public static final double DEFAULT_Y_OFFSET = 8.0d;
    private static final long serialVersionUID = 588933208243446087L;
    private transient Paint wallPaint;
    private double xOffset;
    private double yOffset;

    static {
        DEFAULT_WALL_PAINT = new Color(221, 221, 221);
    }

    public XYLine3DRenderer() {
        this.wallPaint = DEFAULT_WALL_PAINT;
        this.xOffset = DEFAULT_X_OFFSET;
        this.yOffset = DEFAULT_Y_OFFSET;
    }

    public double getXOffset() {
        return this.xOffset;
    }

    public double getYOffset() {
        return this.yOffset;
    }

    public void setXOffset(double xOffset) {
        this.xOffset = xOffset;
        fireChangeEvent();
    }

    public void setYOffset(double yOffset) {
        this.yOffset = yOffset;
        fireChangeEvent();
    }

    public Paint getWallPaint() {
        return this.wallPaint;
    }

    public void setWallPaint(Paint paint) {
        this.wallPaint = paint;
        fireChangeEvent();
    }

    public int getPassCount() {
        return 3;
    }

    protected boolean isLinePass(int pass) {
        return pass == 0 || pass == 1;
    }

    protected boolean isItemPass(int pass) {
        return pass == 2;
    }

    protected boolean isShadowPass(int pass) {
        return pass == 0;
    }

    protected void drawFirstPassShape(Graphics2D g2, int pass, int series, int item, Shape shape) {
        if (!isShadowPass(pass)) {
            super.drawFirstPassShape(g2, pass, series, item, shape);
        } else if (getWallPaint() != null) {
            g2.setStroke(getItemStroke(series, item));
            g2.setPaint(getWallPaint());
            g2.translate(getXOffset(), getYOffset());
            g2.draw(shape);
            g2.translate(-getXOffset(), -getYOffset());
        }
    }

    public boolean equals(Object obj) {
        if (obj == this) {
            return true;
        }
        if (!(obj instanceof XYLine3DRenderer)) {
            return false;
        }
        XYLine3DRenderer that = (XYLine3DRenderer) obj;
        if (this.xOffset == that.xOffset && this.yOffset == that.yOffset && PaintUtilities.equal(this.wallPaint, that.wallPaint)) {
            return super.equals(obj);
        }
        return false;
    }

    private void readObject(ObjectInputStream stream) throws IOException, ClassNotFoundException {
        stream.defaultReadObject();
        this.wallPaint = SerialUtilities.readPaint(stream);
    }

    private void writeObject(ObjectOutputStream stream) throws IOException {
        stream.defaultWriteObject();
        SerialUtilities.writePaint(this.wallPaint, stream);
    }
}
