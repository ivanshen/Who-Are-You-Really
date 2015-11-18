package org.jfree.chart;

import java.awt.AlphaComposite;
import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Composite;
import java.awt.Graphics2D;
import java.awt.Paint;
import java.awt.Stroke;
import java.awt.geom.GeneralPath;
import java.awt.geom.Rectangle2D;
import org.jfree.chart.axis.ValueAxis;
import org.jfree.chart.plot.Plot;
import org.jfree.ui.RectangleEdge;

public class ClipPath implements Cloneable {
    private boolean clip;
    private Composite composite;
    private Paint drawPaint;
    private boolean drawPath;
    private Stroke drawStroke;
    private Paint fillPaint;
    private boolean fillPath;
    private double[] xValue;
    private double[] yValue;

    public ClipPath() {
        this.xValue = null;
        this.yValue = null;
        this.clip = true;
        this.drawPath = false;
        this.fillPath = false;
        this.fillPaint = null;
        this.drawPaint = null;
        this.drawStroke = null;
        this.composite = null;
    }

    public ClipPath(double[] xValue, double[] yValue) {
        this(xValue, yValue, true, false, true);
    }

    public ClipPath(double[] xValue, double[] yValue, boolean clip, boolean fillPath, boolean drawPath) {
        this.xValue = null;
        this.yValue = null;
        this.clip = true;
        this.drawPath = false;
        this.fillPath = false;
        this.fillPaint = null;
        this.drawPaint = null;
        this.drawStroke = null;
        this.composite = null;
        this.xValue = xValue;
        this.yValue = yValue;
        this.clip = clip;
        this.fillPath = fillPath;
        this.drawPath = drawPath;
        this.fillPaint = Color.gray;
        this.drawPaint = Color.blue;
        this.drawStroke = new BasicStroke(Plot.DEFAULT_FOREGROUND_ALPHA);
        this.composite = AlphaComposite.Src;
    }

    public ClipPath(double[] xValue, double[] yValue, boolean fillPath, boolean drawPath, Paint fillPaint, Paint drawPaint, Stroke drawStroke, Composite composite) {
        this.xValue = null;
        this.yValue = null;
        this.clip = true;
        this.drawPath = false;
        this.fillPath = false;
        this.fillPaint = null;
        this.drawPaint = null;
        this.drawStroke = null;
        this.composite = null;
        this.xValue = xValue;
        this.yValue = yValue;
        this.fillPath = fillPath;
        this.drawPath = drawPath;
        this.fillPaint = fillPaint;
        this.drawPaint = drawPaint;
        this.drawStroke = drawStroke;
        this.composite = composite;
    }

    public GeneralPath draw(Graphics2D g2, Rectangle2D dataArea, ValueAxis horizontalAxis, ValueAxis verticalAxis) {
        GeneralPath generalPath = generateClipPath(dataArea, horizontalAxis, verticalAxis);
        if (this.fillPath || this.drawPath) {
            Composite saveComposite = g2.getComposite();
            Paint savePaint = g2.getPaint();
            Stroke saveStroke = g2.getStroke();
            if (this.fillPaint != null) {
                g2.setPaint(this.fillPaint);
            }
            if (this.composite != null) {
                g2.setComposite(this.composite);
            }
            if (this.fillPath) {
                g2.fill(generalPath);
            }
            if (this.drawStroke != null) {
                g2.setStroke(this.drawStroke);
            }
            if (this.drawPath) {
                g2.draw(generalPath);
            }
            g2.setPaint(savePaint);
            g2.setComposite(saveComposite);
            g2.setStroke(saveStroke);
        }
        return generalPath;
    }

    public GeneralPath generateClipPath(Rectangle2D dataArea, ValueAxis horizontalAxis, ValueAxis verticalAxis) {
        GeneralPath generalPath = new GeneralPath();
        generalPath.moveTo((float) horizontalAxis.valueToJava2D(this.xValue[0], dataArea, RectangleEdge.BOTTOM), (float) verticalAxis.valueToJava2D(this.yValue[0], dataArea, RectangleEdge.LEFT));
        for (int k = 0; k < this.yValue.length; k++) {
            generalPath.lineTo((float) horizontalAxis.valueToJava2D(this.xValue[k], dataArea, RectangleEdge.BOTTOM), (float) verticalAxis.valueToJava2D(this.yValue[k], dataArea, RectangleEdge.LEFT));
        }
        generalPath.closePath();
        return generalPath;
    }

    public Composite getComposite() {
        return this.composite;
    }

    public Paint getDrawPaint() {
        return this.drawPaint;
    }

    public boolean isDrawPath() {
        return this.drawPath;
    }

    public Stroke getDrawStroke() {
        return this.drawStroke;
    }

    public Paint getFillPaint() {
        return this.fillPaint;
    }

    public boolean isFillPath() {
        return this.fillPath;
    }

    public double[] getXValue() {
        return this.xValue;
    }

    public double[] getYValue() {
        return this.yValue;
    }

    public void setComposite(Composite composite) {
        this.composite = composite;
    }

    public void setDrawPaint(Paint drawPaint) {
        this.drawPaint = drawPaint;
    }

    public void setDrawPath(boolean drawPath) {
        this.drawPath = drawPath;
    }

    public void setDrawStroke(Stroke drawStroke) {
        this.drawStroke = drawStroke;
    }

    public void setFillPaint(Paint fillPaint) {
        this.fillPaint = fillPaint;
    }

    public void setFillPath(boolean fillPath) {
        this.fillPath = fillPath;
    }

    public void setXValue(double[] xValue) {
        this.xValue = xValue;
    }

    public void setYValue(double[] yValue) {
        this.yValue = yValue;
    }

    public boolean isClip() {
        return this.clip;
    }

    public void setClip(boolean clip) {
        this.clip = clip;
    }

    public Object clone() throws CloneNotSupportedException {
        ClipPath clone = (ClipPath) super.clone();
        clone.xValue = (double[]) this.xValue.clone();
        clone.yValue = (double[]) this.yValue.clone();
        return clone;
    }
}
