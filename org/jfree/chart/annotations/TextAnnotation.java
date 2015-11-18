package org.jfree.chart.annotations;

import java.awt.Color;
import java.awt.Font;
import java.awt.Paint;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import org.jfree.chart.HashUtilities;
import org.jfree.chart.util.ParamChecks;
import org.jfree.io.SerialUtilities;
import org.jfree.ui.TextAnchor;
import org.jfree.util.ObjectUtilities;
import org.jfree.util.PaintUtilities;

public class TextAnnotation extends AbstractAnnotation implements Serializable {
    public static final Font DEFAULT_FONT;
    public static final Paint DEFAULT_PAINT;
    public static final TextAnchor DEFAULT_ROTATION_ANCHOR;
    public static final double DEFAULT_ROTATION_ANGLE = 0.0d;
    public static final TextAnchor DEFAULT_TEXT_ANCHOR;
    private static final long serialVersionUID = 7008912287533127432L;
    private Font font;
    private transient Paint paint;
    private TextAnchor rotationAnchor;
    private double rotationAngle;
    private String text;
    private TextAnchor textAnchor;

    static {
        DEFAULT_FONT = new Font("SansSerif", 0, 10);
        DEFAULT_PAINT = Color.black;
        DEFAULT_TEXT_ANCHOR = TextAnchor.CENTER;
        DEFAULT_ROTATION_ANCHOR = TextAnchor.CENTER;
    }

    protected TextAnnotation(String text) {
        ParamChecks.nullNotPermitted(text, "text");
        this.text = text;
        this.font = DEFAULT_FONT;
        this.paint = DEFAULT_PAINT;
        this.textAnchor = DEFAULT_TEXT_ANCHOR;
        this.rotationAnchor = DEFAULT_ROTATION_ANCHOR;
        this.rotationAngle = DEFAULT_ROTATION_ANGLE;
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

    public boolean equals(Object obj) {
        if (obj == this) {
            return true;
        }
        if (!(obj instanceof TextAnnotation)) {
            return false;
        }
        TextAnnotation that = (TextAnnotation) obj;
        if (!ObjectUtilities.equal(this.text, that.getText())) {
            return false;
        }
        if (!ObjectUtilities.equal(this.font, that.getFont())) {
            return false;
        }
        if (!PaintUtilities.equal(this.paint, that.getPaint())) {
            return false;
        }
        if (!ObjectUtilities.equal(this.textAnchor, that.getTextAnchor())) {
            return false;
        }
        if (!ObjectUtilities.equal(this.rotationAnchor, that.getRotationAnchor())) {
            return false;
        }
        if (this.rotationAngle != that.getRotationAngle()) {
            return false;
        }
        return true;
    }

    public int hashCode() {
        int result = ((((this.font.hashCode() + 7141) * 37) + HashUtilities.hashCodeForPaint(this.paint)) * 37) + this.rotationAnchor.hashCode();
        long temp = Double.doubleToLongBits(this.rotationAngle);
        return (((((result * 37) + ((int) ((temp >>> 32) ^ temp))) * 37) + this.text.hashCode()) * 37) + this.textAnchor.hashCode();
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
