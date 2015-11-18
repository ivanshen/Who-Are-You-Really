package org.jfree.chart.axis;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.Paint;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.HashMap;
import java.util.Map;
import org.jfree.chart.event.AxisChangeEvent;
import org.jfree.chart.util.ParamChecks;
import org.jfree.io.SerialUtilities;
import org.jfree.text.TextBlock;
import org.jfree.text.TextFragment;
import org.jfree.text.TextLine;
import org.jfree.ui.RectangleEdge;
import org.jfree.util.PaintUtilities;

public class ExtendedCategoryAxis extends CategoryAxis {
    static final long serialVersionUID = -3004429093959826567L;
    private Font sublabelFont;
    private transient Paint sublabelPaint;
    private Map sublabels;

    public ExtendedCategoryAxis(String label) {
        super(label);
        this.sublabels = new HashMap();
        this.sublabelFont = new Font("SansSerif", 0, 10);
        this.sublabelPaint = Color.black;
    }

    public Font getSubLabelFont() {
        return this.sublabelFont;
    }

    public void setSubLabelFont(Font font) {
        ParamChecks.nullNotPermitted(font, "font");
        this.sublabelFont = font;
        notifyListeners(new AxisChangeEvent(this));
    }

    public Paint getSubLabelPaint() {
        return this.sublabelPaint;
    }

    public void setSubLabelPaint(Paint paint) {
        ParamChecks.nullNotPermitted(paint, "paint");
        this.sublabelPaint = paint;
        notifyListeners(new AxisChangeEvent(this));
    }

    public void addSubLabel(Comparable category, String label) {
        this.sublabels.put(category, label);
    }

    protected TextBlock createLabel(Comparable category, float width, RectangleEdge edge, Graphics2D g2) {
        TextBlock label = super.createLabel(category, width, edge, g2);
        String s = (String) this.sublabels.get(category);
        if (s != null) {
            if (edge == RectangleEdge.TOP || edge == RectangleEdge.BOTTOM) {
                label.addLine(new TextLine(s, this.sublabelFont, this.sublabelPaint));
            } else if (edge == RectangleEdge.LEFT || edge == RectangleEdge.RIGHT) {
                TextLine line = label.getLastLine();
                if (line != null) {
                    line.addFragment(new TextFragment("  " + s, this.sublabelFont, this.sublabelPaint));
                }
            }
        }
        return label;
    }

    public boolean equals(Object obj) {
        if (obj == this) {
            return true;
        }
        if (!(obj instanceof ExtendedCategoryAxis)) {
            return false;
        }
        ExtendedCategoryAxis that = (ExtendedCategoryAxis) obj;
        if (this.sublabelFont.equals(that.sublabelFont) && PaintUtilities.equal(this.sublabelPaint, that.sublabelPaint) && this.sublabels.equals(that.sublabels)) {
            return super.equals(obj);
        }
        return false;
    }

    public Object clone() throws CloneNotSupportedException {
        ExtendedCategoryAxis clone = (ExtendedCategoryAxis) super.clone();
        clone.sublabels = new HashMap(this.sublabels);
        return clone;
    }

    private void writeObject(ObjectOutputStream stream) throws IOException {
        stream.defaultWriteObject();
        SerialUtilities.writePaint(this.sublabelPaint, stream);
    }

    private void readObject(ObjectInputStream stream) throws IOException, ClassNotFoundException {
        stream.defaultReadObject();
        this.sublabelPaint = SerialUtilities.readPaint(stream);
    }
}
