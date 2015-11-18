package org.jfree.chart.plot;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Font;
import java.awt.Paint;
import java.awt.Stroke;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.EventListener;
import javax.swing.event.EventListenerList;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.event.MarkerChangeEvent;
import org.jfree.chart.event.MarkerChangeListener;
import org.jfree.chart.renderer.category.BarRenderer;
import org.jfree.chart.util.ParamChecks;
import org.jfree.io.SerialUtilities;
import org.jfree.ui.LengthAdjustmentType;
import org.jfree.ui.RectangleAnchor;
import org.jfree.ui.RectangleInsets;
import org.jfree.ui.TextAnchor;
import org.jfree.util.ObjectUtilities;
import org.jfree.util.PaintUtilities;

public abstract class Marker implements Cloneable, Serializable {
    private static final long serialVersionUID = -734389651405327166L;
    private float alpha;
    private String label;
    private RectangleAnchor labelAnchor;
    private Color labelBackgroundColor;
    private Font labelFont;
    private RectangleInsets labelOffset;
    private LengthAdjustmentType labelOffsetType;
    private transient Paint labelPaint;
    private TextAnchor labelTextAnchor;
    private transient EventListenerList listenerList;
    private transient Paint outlinePaint;
    private transient Stroke outlineStroke;
    private transient Paint paint;
    private transient Stroke stroke;

    protected Marker() {
        this(Color.gray);
    }

    protected Marker(Paint paint) {
        this(paint, new BasicStroke(JFreeChart.DEFAULT_BACKGROUND_IMAGE_ALPHA), Color.gray, new BasicStroke(JFreeChart.DEFAULT_BACKGROUND_IMAGE_ALPHA), 0.8f);
    }

    protected Marker(Paint paint, Stroke stroke, Paint outlinePaint, Stroke outlineStroke, float alpha) {
        this.label = null;
        ParamChecks.nullNotPermitted(paint, "paint");
        ParamChecks.nullNotPermitted(stroke, "stroke");
        if (alpha < 0.0f || alpha > Plot.DEFAULT_FOREGROUND_ALPHA) {
            throw new IllegalArgumentException("The 'alpha' value must be in the range 0.0f to 1.0f");
        }
        this.paint = paint;
        this.stroke = stroke;
        this.outlinePaint = outlinePaint;
        this.outlineStroke = outlineStroke;
        this.alpha = alpha;
        this.labelFont = new Font("SansSerif", 0, 9);
        this.labelPaint = Color.black;
        this.labelBackgroundColor = new Color(100, 100, 100, 100);
        this.labelAnchor = RectangleAnchor.TOP_LEFT;
        this.labelOffset = new RectangleInsets(BarRenderer.BAR_OUTLINE_WIDTH_THRESHOLD, BarRenderer.BAR_OUTLINE_WIDTH_THRESHOLD, BarRenderer.BAR_OUTLINE_WIDTH_THRESHOLD, BarRenderer.BAR_OUTLINE_WIDTH_THRESHOLD);
        this.labelOffsetType = LengthAdjustmentType.CONTRACT;
        this.labelTextAnchor = TextAnchor.CENTER;
        this.listenerList = new EventListenerList();
    }

    public Paint getPaint() {
        return this.paint;
    }

    public void setPaint(Paint paint) {
        ParamChecks.nullNotPermitted(paint, "paint");
        this.paint = paint;
        notifyListeners(new MarkerChangeEvent(this));
    }

    public Stroke getStroke() {
        return this.stroke;
    }

    public void setStroke(Stroke stroke) {
        ParamChecks.nullNotPermitted(stroke, "stroke");
        this.stroke = stroke;
        notifyListeners(new MarkerChangeEvent(this));
    }

    public Paint getOutlinePaint() {
        return this.outlinePaint;
    }

    public void setOutlinePaint(Paint paint) {
        this.outlinePaint = paint;
        notifyListeners(new MarkerChangeEvent(this));
    }

    public Stroke getOutlineStroke() {
        return this.outlineStroke;
    }

    public void setOutlineStroke(Stroke stroke) {
        this.outlineStroke = stroke;
        notifyListeners(new MarkerChangeEvent(this));
    }

    public float getAlpha() {
        return this.alpha;
    }

    public void setAlpha(float alpha) {
        if (alpha < 0.0f || alpha > Plot.DEFAULT_FOREGROUND_ALPHA) {
            throw new IllegalArgumentException("The 'alpha' value must be in the range 0.0f to 1.0f");
        }
        this.alpha = alpha;
        notifyListeners(new MarkerChangeEvent(this));
    }

    public String getLabel() {
        return this.label;
    }

    public void setLabel(String label) {
        this.label = label;
        notifyListeners(new MarkerChangeEvent(this));
    }

    public Font getLabelFont() {
        return this.labelFont;
    }

    public void setLabelFont(Font font) {
        ParamChecks.nullNotPermitted(font, "font");
        this.labelFont = font;
        notifyListeners(new MarkerChangeEvent(this));
    }

    public Paint getLabelPaint() {
        return this.labelPaint;
    }

    public void setLabelPaint(Paint paint) {
        ParamChecks.nullNotPermitted(paint, "paint");
        this.labelPaint = paint;
        notifyListeners(new MarkerChangeEvent(this));
    }

    public Color getLabelBackgroundColor() {
        return this.labelBackgroundColor;
    }

    public void setLabelBackgroundColor(Color color) {
        ParamChecks.nullNotPermitted(color, "color");
        this.labelBackgroundColor = color;
    }

    public RectangleAnchor getLabelAnchor() {
        return this.labelAnchor;
    }

    public void setLabelAnchor(RectangleAnchor anchor) {
        ParamChecks.nullNotPermitted(anchor, "anchor");
        this.labelAnchor = anchor;
        notifyListeners(new MarkerChangeEvent(this));
    }

    public RectangleInsets getLabelOffset() {
        return this.labelOffset;
    }

    public void setLabelOffset(RectangleInsets offset) {
        ParamChecks.nullNotPermitted(offset, "offset");
        this.labelOffset = offset;
        notifyListeners(new MarkerChangeEvent(this));
    }

    public LengthAdjustmentType getLabelOffsetType() {
        return this.labelOffsetType;
    }

    public void setLabelOffsetType(LengthAdjustmentType adj) {
        ParamChecks.nullNotPermitted(adj, "adj");
        this.labelOffsetType = adj;
        notifyListeners(new MarkerChangeEvent(this));
    }

    public TextAnchor getLabelTextAnchor() {
        return this.labelTextAnchor;
    }

    public void setLabelTextAnchor(TextAnchor anchor) {
        ParamChecks.nullNotPermitted(anchor, "anchor");
        this.labelTextAnchor = anchor;
        notifyListeners(new MarkerChangeEvent(this));
    }

    public void addChangeListener(MarkerChangeListener listener) {
        this.listenerList.add(MarkerChangeListener.class, listener);
    }

    public void removeChangeListener(MarkerChangeListener listener) {
        this.listenerList.remove(MarkerChangeListener.class, listener);
    }

    public void notifyListeners(MarkerChangeEvent event) {
        Object[] listeners = this.listenerList.getListenerList();
        for (int i = listeners.length - 2; i >= 0; i -= 2) {
            if (listeners[i] == MarkerChangeListener.class) {
                ((MarkerChangeListener) listeners[i + 1]).markerChanged(event);
            }
        }
    }

    public EventListener[] getListeners(Class listenerType) {
        return this.listenerList.getListeners(listenerType);
    }

    public boolean equals(Object obj) {
        if (obj == this) {
            return true;
        }
        if (!(obj instanceof Marker)) {
            return false;
        }
        Marker that = (Marker) obj;
        if (!PaintUtilities.equal(this.paint, that.paint)) {
            return false;
        }
        if (!ObjectUtilities.equal(this.stroke, that.stroke)) {
            return false;
        }
        if (!PaintUtilities.equal(this.outlinePaint, that.outlinePaint)) {
            return false;
        }
        if (!ObjectUtilities.equal(this.outlineStroke, that.outlineStroke)) {
            return false;
        }
        if (this.alpha != that.alpha) {
            return false;
        }
        if (!ObjectUtilities.equal(this.label, that.label)) {
            return false;
        }
        if (!ObjectUtilities.equal(this.labelFont, that.labelFont)) {
            return false;
        }
        if (!PaintUtilities.equal(this.labelPaint, that.labelPaint)) {
            return false;
        }
        if (!this.labelBackgroundColor.equals(that.labelBackgroundColor)) {
            return false;
        }
        if (this.labelAnchor != that.labelAnchor) {
            return false;
        }
        if (this.labelTextAnchor != that.labelTextAnchor) {
            return false;
        }
        if (!ObjectUtilities.equal(this.labelOffset, that.labelOffset)) {
            return false;
        }
        if (this.labelOffsetType.equals(that.labelOffsetType)) {
            return true;
        }
        return false;
    }

    public Object clone() throws CloneNotSupportedException {
        return super.clone();
    }

    private void writeObject(ObjectOutputStream stream) throws IOException {
        stream.defaultWriteObject();
        SerialUtilities.writePaint(this.paint, stream);
        SerialUtilities.writeStroke(this.stroke, stream);
        SerialUtilities.writePaint(this.outlinePaint, stream);
        SerialUtilities.writeStroke(this.outlineStroke, stream);
        SerialUtilities.writePaint(this.labelPaint, stream);
    }

    private void readObject(ObjectInputStream stream) throws IOException, ClassNotFoundException {
        stream.defaultReadObject();
        this.paint = SerialUtilities.readPaint(stream);
        this.stroke = SerialUtilities.readStroke(stream);
        this.outlinePaint = SerialUtilities.readPaint(stream);
        this.outlineStroke = SerialUtilities.readStroke(stream);
        this.labelPaint = SerialUtilities.readPaint(stream);
        this.listenerList = new EventListenerList();
    }
}
