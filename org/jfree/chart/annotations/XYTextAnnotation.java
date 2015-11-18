package org.jfree.chart.annotations;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.Paint;
import java.awt.Shape;
import java.awt.Stroke;
import java.awt.geom.Rectangle2D;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import org.jfree.chart.HashUtilities;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.ValueAxis;
import org.jfree.chart.plot.Plot;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.plot.PlotRenderingInfo;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.util.ParamChecks;
import org.jfree.io.SerialUtilities;
import org.jfree.text.TextUtilities;
import org.jfree.ui.RectangleEdge;
import org.jfree.ui.TextAnchor;
import org.jfree.util.PaintUtilities;
import org.jfree.util.PublicCloneable;

public class XYTextAnnotation extends AbstractXYAnnotation implements Cloneable, PublicCloneable, Serializable {
    public static final Font DEFAULT_FONT;
    public static final Paint DEFAULT_PAINT;
    public static final TextAnchor DEFAULT_ROTATION_ANCHOR;
    public static final double DEFAULT_ROTATION_ANGLE = 0.0d;
    public static final TextAnchor DEFAULT_TEXT_ANCHOR;
    private static final long serialVersionUID = -2946063342782506328L;
    private transient Paint backgroundPaint;
    private Font font;
    private transient Paint outlinePaint;
    private transient Stroke outlineStroke;
    private boolean outlineVisible;
    private transient Paint paint;
    private TextAnchor rotationAnchor;
    private double rotationAngle;
    private String text;
    private TextAnchor textAnchor;
    private double x;
    private double y;

    static {
        DEFAULT_FONT = new Font("SansSerif", 0, 10);
        DEFAULT_PAINT = Color.black;
        DEFAULT_TEXT_ANCHOR = TextAnchor.CENTER;
        DEFAULT_ROTATION_ANCHOR = TextAnchor.CENTER;
    }

    public XYTextAnnotation(String text, double x, double y) {
        ParamChecks.nullNotPermitted(text, "text");
        this.text = text;
        this.font = DEFAULT_FONT;
        this.paint = DEFAULT_PAINT;
        this.x = x;
        this.y = y;
        this.textAnchor = DEFAULT_TEXT_ANCHOR;
        this.rotationAnchor = DEFAULT_ROTATION_ANCHOR;
        this.rotationAngle = DEFAULT_ROTATION_ANGLE;
        this.backgroundPaint = null;
        this.outlineVisible = false;
        this.outlinePaint = Color.black;
        this.outlineStroke = new BasicStroke(JFreeChart.DEFAULT_BACKGROUND_IMAGE_ALPHA);
    }

    public String getText() {
        return this.text;
    }

    public void setText(String text) {
        ParamChecks.nullNotPermitted(text, "text");
        this.text = text;
        fireAnnotationChanged();
    }

    public Font getFont() {
        return this.font;
    }

    public void setFont(Font font) {
        ParamChecks.nullNotPermitted(font, "font");
        this.font = font;
        fireAnnotationChanged();
    }

    public Paint getPaint() {
        return this.paint;
    }

    public void setPaint(Paint paint) {
        ParamChecks.nullNotPermitted(paint, "paint");
        this.paint = paint;
        fireAnnotationChanged();
    }

    public TextAnchor getTextAnchor() {
        return this.textAnchor;
    }

    public void setTextAnchor(TextAnchor anchor) {
        ParamChecks.nullNotPermitted(anchor, "anchor");
        this.textAnchor = anchor;
        fireAnnotationChanged();
    }

    public TextAnchor getRotationAnchor() {
        return this.rotationAnchor;
    }

    public void setRotationAnchor(TextAnchor anchor) {
        ParamChecks.nullNotPermitted(anchor, "anchor");
        this.rotationAnchor = anchor;
        fireAnnotationChanged();
    }

    public double getRotationAngle() {
        return this.rotationAngle;
    }

    public void setRotationAngle(double angle) {
        this.rotationAngle = angle;
        fireAnnotationChanged();
    }

    public double getX() {
        return this.x;
    }

    public void setX(double x) {
        this.x = x;
        fireAnnotationChanged();
    }

    public double getY() {
        return this.y;
    }

    public void setY(double y) {
        this.y = y;
        fireAnnotationChanged();
    }

    public Paint getBackgroundPaint() {
        return this.backgroundPaint;
    }

    public void setBackgroundPaint(Paint paint) {
        this.backgroundPaint = paint;
        fireAnnotationChanged();
    }

    public Paint getOutlinePaint() {
        return this.outlinePaint;
    }

    public void setOutlinePaint(Paint paint) {
        ParamChecks.nullNotPermitted(paint, "paint");
        this.outlinePaint = paint;
        fireAnnotationChanged();
    }

    public Stroke getOutlineStroke() {
        return this.outlineStroke;
    }

    public void setOutlineStroke(Stroke stroke) {
        ParamChecks.nullNotPermitted(stroke, "stroke");
        this.outlineStroke = stroke;
        fireAnnotationChanged();
    }

    public boolean isOutlineVisible() {
        return this.outlineVisible;
    }

    public void setOutlineVisible(boolean visible) {
        this.outlineVisible = visible;
        fireAnnotationChanged();
    }

    public void draw(Graphics2D g2, XYPlot plot, Rectangle2D dataArea, ValueAxis domainAxis, ValueAxis rangeAxis, int rendererIndex, PlotRenderingInfo info) {
        PlotOrientation orientation = plot.getOrientation();
        RectangleEdge domainEdge = Plot.resolveDomainAxisLocation(plot.getDomainAxisLocation(), orientation);
        RectangleEdge rangeEdge = Plot.resolveRangeAxisLocation(plot.getRangeAxisLocation(), orientation);
        float anchorX = (float) domainAxis.valueToJava2D(this.x, dataArea, domainEdge);
        float anchorY = (float) rangeAxis.valueToJava2D(this.y, dataArea, rangeEdge);
        if (orientation == PlotOrientation.HORIZONTAL) {
            float tempAnchor = anchorX;
            anchorX = anchorY;
            anchorY = tempAnchor;
        }
        g2.setFont(getFont());
        Shape hotspot = TextUtilities.calculateRotatedStringBounds(getText(), g2, anchorX, anchorY, getTextAnchor(), getRotationAngle(), getRotationAnchor());
        if (this.backgroundPaint != null) {
            g2.setPaint(this.backgroundPaint);
            g2.fill(hotspot);
        }
        g2.setPaint(getPaint());
        TextUtilities.drawRotatedString(getText(), g2, anchorX, anchorY, getTextAnchor(), getRotationAngle(), getRotationAnchor());
        if (this.outlineVisible) {
            g2.setStroke(this.outlineStroke);
            g2.setPaint(this.outlinePaint);
            g2.draw(hotspot);
        }
        String toolTip = getToolTipText();
        String url = getURL();
        if (toolTip != null || url != null) {
            addEntity(info, hotspot, rendererIndex, toolTip, url);
        }
    }

    public boolean equals(Object obj) {
        if (obj == this) {
            return true;
        }
        if (!(obj instanceof XYTextAnnotation)) {
            return false;
        }
        XYTextAnnotation that = (XYTextAnnotation) obj;
        if (this.text.equals(that.text) && this.x == that.x && this.y == that.y && this.font.equals(that.font) && PaintUtilities.equal(this.paint, that.paint) && this.rotationAnchor.equals(that.rotationAnchor) && this.rotationAngle == that.rotationAngle && this.textAnchor.equals(that.textAnchor) && this.outlineVisible == that.outlineVisible && PaintUtilities.equal(this.backgroundPaint, that.backgroundPaint) && PaintUtilities.equal(this.outlinePaint, that.outlinePaint) && this.outlineStroke.equals(that.outlineStroke)) {
            return super.equals(obj);
        }
        return false;
    }

    public int hashCode() {
        int result = ((((this.text.hashCode() + 7141) * 37) + this.font.hashCode()) * 37) + HashUtilities.hashCodeForPaint(this.paint);
        long temp = Double.doubleToLongBits(this.x);
        result = (result * 37) + ((int) ((temp >>> 32) ^ temp));
        temp = Double.doubleToLongBits(this.y);
        result = (((((result * 37) + ((int) ((temp >>> 32) ^ temp))) * 37) + this.textAnchor.hashCode()) * 37) + this.rotationAnchor.hashCode();
        temp = Double.doubleToLongBits(this.rotationAngle);
        return (result * 37) + ((int) ((temp >>> 32) ^ temp));
    }

    public Object clone() throws CloneNotSupportedException {
        return super.clone();
    }

    private void writeObject(ObjectOutputStream stream) throws IOException {
        stream.defaultWriteObject();
        SerialUtilities.writePaint(this.paint, stream);
        SerialUtilities.writePaint(this.backgroundPaint, stream);
        SerialUtilities.writePaint(this.outlinePaint, stream);
        SerialUtilities.writeStroke(this.outlineStroke, stream);
    }

    private void readObject(ObjectInputStream stream) throws IOException, ClassNotFoundException {
        stream.defaultReadObject();
        this.paint = SerialUtilities.readPaint(stream);
        this.backgroundPaint = SerialUtilities.readPaint(stream);
        this.outlinePaint = SerialUtilities.readPaint(stream);
        this.outlineStroke = SerialUtilities.readStroke(stream);
    }
}
