package org.jfree.text;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.Paint;
import java.awt.font.LineMetrics;
import java.awt.geom.Rectangle2D;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import org.jfree.chart.axis.Axis;
import org.jfree.io.SerialUtilities;
import org.jfree.ui.Size2D;
import org.jfree.ui.TextAnchor;
import org.jfree.util.Log;
import org.jfree.util.LogContext;

public class TextFragment implements Serializable {
    public static final Font DEFAULT_FONT;
    public static final Paint DEFAULT_PAINT;
    protected static final LogContext logger;
    private static final long serialVersionUID = 4465945952903143262L;
    private float baselineOffset;
    private Font font;
    private transient Paint paint;
    private String text;

    static {
        DEFAULT_FONT = new Font("Serif", 0, 12);
        DEFAULT_PAINT = Color.black;
        logger = Log.createContext(TextFragment.class);
    }

    public TextFragment(String text) {
        this(text, DEFAULT_FONT, DEFAULT_PAINT);
    }

    public TextFragment(String text, Font font) {
        this(text, font, DEFAULT_PAINT);
    }

    public TextFragment(String text, Font font, Paint paint) {
        this(text, font, paint, 0.0f);
    }

    public TextFragment(String text, Font font, Paint paint, float baselineOffset) {
        if (text == null) {
            throw new IllegalArgumentException("Null 'text' argument.");
        } else if (font == null) {
            throw new IllegalArgumentException("Null 'font' argument.");
        } else if (paint == null) {
            throw new IllegalArgumentException("Null 'paint' argument.");
        } else {
            this.text = text;
            this.font = font;
            this.paint = paint;
            this.baselineOffset = baselineOffset;
        }
    }

    public String getText() {
        return this.text;
    }

    public Font getFont() {
        return this.font;
    }

    public Paint getPaint() {
        return this.paint;
    }

    public float getBaselineOffset() {
        return this.baselineOffset;
    }

    public void draw(Graphics2D g2, float anchorX, float anchorY, TextAnchor anchor, float rotateX, float rotateY, double angle) {
        g2.setFont(this.font);
        g2.setPaint(this.paint);
        TextUtilities.drawRotatedString(this.text, g2, anchorX, anchorY + this.baselineOffset, anchor, angle, rotateX, rotateY);
    }

    public Size2D calculateDimensions(Graphics2D g2) {
        Rectangle2D bounds = TextUtilities.getTextBounds(this.text, g2, g2.getFontMetrics(this.font));
        return new Size2D(bounds.getWidth(), bounds.getHeight());
    }

    public float calculateBaselineOffset(Graphics2D g2, TextAnchor anchor) {
        LineMetrics lm = g2.getFontMetrics(this.font).getLineMetrics("ABCxyz", g2);
        if (anchor.isTop()) {
            return lm.getAscent();
        }
        if (anchor.isHalfAscent()) {
            return lm.getAscent() / Axis.DEFAULT_TICK_MARK_OUTSIDE_LENGTH;
        }
        if (anchor.isVerticalCenter()) {
            return (lm.getAscent() / Axis.DEFAULT_TICK_MARK_OUTSIDE_LENGTH) - (lm.getDescent() / Axis.DEFAULT_TICK_MARK_OUTSIDE_LENGTH);
        }
        if (anchor.isBottom()) {
            return (-lm.getDescent()) - lm.getLeading();
        }
        return 0.0f;
    }

    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (obj == this) {
            return true;
        }
        if (!(obj instanceof TextFragment)) {
            return false;
        }
        TextFragment tf = (TextFragment) obj;
        if (this.text.equals(tf.text) && this.font.equals(tf.font) && this.paint.equals(tf.paint)) {
            return true;
        }
        return false;
    }

    public int hashCode() {
        int result;
        int hashCode;
        int i = 0;
        if (this.text != null) {
            result = this.text.hashCode();
        } else {
            result = 0;
        }
        int i2 = result * 29;
        if (this.font != null) {
            hashCode = this.font.hashCode();
        } else {
            hashCode = 0;
        }
        hashCode = (i2 + hashCode) * 29;
        if (this.paint != null) {
            i = this.paint.hashCode();
        }
        return hashCode + i;
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
