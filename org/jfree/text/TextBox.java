package org.jfree.text;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.Paint;
import java.awt.Stroke;
import java.awt.geom.Rectangle2D;
import java.awt.geom.Rectangle2D.Double;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import org.jfree.chart.axis.DateAxis;
import org.jfree.chart.plot.Plot;
import org.jfree.chart.renderer.category.BarRenderer;
import org.jfree.data.xy.NormalizedMatrixSeries;
import org.jfree.io.SerialUtilities;
import org.jfree.ui.RectangleAnchor;
import org.jfree.ui.RectangleInsets;
import org.jfree.ui.Size2D;
import org.jfree.util.ObjectUtilities;

public class TextBox implements Serializable {
    private static final long serialVersionUID = 3360220213180203706L;
    private transient Paint backgroundPaint;
    private RectangleInsets interiorGap;
    private transient Paint outlinePaint;
    private transient Stroke outlineStroke;
    private transient Paint shadowPaint;
    private double shadowXOffset;
    private double shadowYOffset;
    private TextBlock textBlock;

    public TextBox() {
        this((TextBlock) null);
    }

    public TextBox(String text) {
        this((TextBlock) null);
        if (text != null) {
            this.textBlock = new TextBlock();
            this.textBlock.addLine(text, new Font("SansSerif", 0, 10), Color.black);
        }
    }

    public TextBox(TextBlock block) {
        this.shadowXOffset = DateAxis.DEFAULT_AUTO_RANGE_MINIMUM_SIZE_IN_MILLISECONDS;
        this.shadowYOffset = DateAxis.DEFAULT_AUTO_RANGE_MINIMUM_SIZE_IN_MILLISECONDS;
        this.outlinePaint = Color.black;
        this.outlineStroke = new BasicStroke(Plot.DEFAULT_FOREGROUND_ALPHA);
        this.interiorGap = new RectangleInsets(NormalizedMatrixSeries.DEFAULT_SCALE_FACTOR, BarRenderer.BAR_OUTLINE_WIDTH_THRESHOLD, NormalizedMatrixSeries.DEFAULT_SCALE_FACTOR, BarRenderer.BAR_OUTLINE_WIDTH_THRESHOLD);
        this.backgroundPaint = new Color(255, 255, 192);
        this.shadowPaint = Color.gray;
        this.shadowXOffset = DateAxis.DEFAULT_AUTO_RANGE_MINIMUM_SIZE_IN_MILLISECONDS;
        this.shadowYOffset = DateAxis.DEFAULT_AUTO_RANGE_MINIMUM_SIZE_IN_MILLISECONDS;
        this.textBlock = block;
    }

    public Paint getOutlinePaint() {
        return this.outlinePaint;
    }

    public void setOutlinePaint(Paint paint) {
        this.outlinePaint = paint;
    }

    public Stroke getOutlineStroke() {
        return this.outlineStroke;
    }

    public void setOutlineStroke(Stroke stroke) {
        this.outlineStroke = stroke;
    }

    public RectangleInsets getInteriorGap() {
        return this.interiorGap;
    }

    public void setInteriorGap(RectangleInsets gap) {
        this.interiorGap = gap;
    }

    public Paint getBackgroundPaint() {
        return this.backgroundPaint;
    }

    public void setBackgroundPaint(Paint paint) {
        this.backgroundPaint = paint;
    }

    public Paint getShadowPaint() {
        return this.shadowPaint;
    }

    public void setShadowPaint(Paint paint) {
        this.shadowPaint = paint;
    }

    public double getShadowXOffset() {
        return this.shadowXOffset;
    }

    public void setShadowXOffset(double offset) {
        this.shadowXOffset = offset;
    }

    public double getShadowYOffset() {
        return this.shadowYOffset;
    }

    public void setShadowYOffset(double offset) {
        this.shadowYOffset = offset;
    }

    public TextBlock getTextBlock() {
        return this.textBlock;
    }

    public void setTextBlock(TextBlock block) {
        this.textBlock = block;
    }

    public void draw(Graphics2D g2, float x, float y, RectangleAnchor anchor) {
        Size2D d1 = this.textBlock.calculateDimensions(g2);
        double w = this.interiorGap.extendWidth(d1.getWidth());
        double h = this.interiorGap.extendHeight(d1.getHeight());
        Rectangle2D bounds = RectangleAnchor.createRectangle(new Size2D(w, h), (double) x, (double) y, anchor);
        double xx = bounds.getX();
        double yy = bounds.getY();
        if (this.shadowPaint != null) {
            Rectangle2D shadow = new Double(this.shadowXOffset + xx, this.shadowYOffset + yy, bounds.getWidth(), bounds.getHeight());
            g2.setPaint(this.shadowPaint);
            g2.fill(shadow);
        }
        if (this.backgroundPaint != null) {
            g2.setPaint(this.backgroundPaint);
            g2.fill(bounds);
        }
        if (!(this.outlinePaint == null || this.outlineStroke == null)) {
            g2.setPaint(this.outlinePaint);
            g2.setStroke(this.outlineStroke);
            g2.draw(bounds);
        }
        this.textBlock.draw(g2, (float) (this.interiorGap.calculateLeftInset(w) + xx), (float) (this.interiorGap.calculateTopInset(h) + yy), TextBlockAnchor.TOP_LEFT);
    }

    public double getHeight(Graphics2D g2) {
        return this.interiorGap.extendHeight(this.textBlock.calculateDimensions(g2).getHeight());
    }

    public boolean equals(Object obj) {
        if (obj == this) {
            return true;
        }
        if (!(obj instanceof TextBox)) {
            return false;
        }
        TextBox that = (TextBox) obj;
        if (!ObjectUtilities.equal(this.outlinePaint, that.outlinePaint)) {
            return false;
        }
        if (!ObjectUtilities.equal(this.outlineStroke, that.outlineStroke)) {
            return false;
        }
        if (!ObjectUtilities.equal(this.interiorGap, that.interiorGap)) {
            return false;
        }
        if (!ObjectUtilities.equal(this.backgroundPaint, that.backgroundPaint)) {
            return false;
        }
        if (!ObjectUtilities.equal(this.shadowPaint, that.shadowPaint)) {
            return false;
        }
        if (this.shadowXOffset != that.shadowXOffset) {
            return false;
        }
        if (this.shadowYOffset != that.shadowYOffset) {
            return false;
        }
        if (ObjectUtilities.equal(this.textBlock, that.textBlock)) {
            return true;
        }
        return false;
    }

    public int hashCode() {
        int result;
        int hashCode;
        long temp;
        int i = 0;
        if (this.outlinePaint != null) {
            result = this.outlinePaint.hashCode();
        } else {
            result = 0;
        }
        int i2 = result * 29;
        if (this.outlineStroke != null) {
            hashCode = this.outlineStroke.hashCode();
        } else {
            hashCode = 0;
        }
        i2 = (i2 + hashCode) * 29;
        if (this.interiorGap != null) {
            hashCode = this.interiorGap.hashCode();
        } else {
            hashCode = 0;
        }
        i2 = (i2 + hashCode) * 29;
        if (this.backgroundPaint != null) {
            hashCode = this.backgroundPaint.hashCode();
        } else {
            hashCode = 0;
        }
        i2 = (i2 + hashCode) * 29;
        if (this.shadowPaint != null) {
            hashCode = this.shadowPaint.hashCode();
        } else {
            hashCode = 0;
        }
        result = i2 + hashCode;
        if (this.shadowXOffset != 0.0d) {
            temp = Double.doubleToLongBits(this.shadowXOffset);
        } else {
            temp = 0;
        }
        result = (result * 29) + ((int) ((temp >>> 32) ^ temp));
        if (this.shadowYOffset != 0.0d) {
            temp = Double.doubleToLongBits(this.shadowYOffset);
        } else {
            temp = 0;
        }
        hashCode = ((result * 29) + ((int) ((temp >>> 32) ^ temp))) * 29;
        if (this.textBlock != null) {
            i = this.textBlock.hashCode();
        }
        return hashCode + i;
    }

    private void writeObject(ObjectOutputStream stream) throws IOException {
        stream.defaultWriteObject();
        SerialUtilities.writePaint(this.outlinePaint, stream);
        SerialUtilities.writeStroke(this.outlineStroke, stream);
        SerialUtilities.writePaint(this.backgroundPaint, stream);
        SerialUtilities.writePaint(this.shadowPaint, stream);
    }

    private void readObject(ObjectInputStream stream) throws IOException, ClassNotFoundException {
        stream.defaultReadObject();
        this.outlinePaint = SerialUtilities.readPaint(stream);
        this.outlineStroke = SerialUtilities.readStroke(stream);
        this.backgroundPaint = SerialUtilities.readPaint(stream);
        this.shadowPaint = SerialUtilities.readPaint(stream);
    }
}
