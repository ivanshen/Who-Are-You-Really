package org.jfree.chart.plot;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Paint;
import java.awt.Stroke;
import java.io.Serializable;
import org.jfree.chart.event.MarkerChangeEvent;
import org.jfree.chart.util.ParamChecks;
import org.jfree.ui.LengthAdjustmentType;

public class CategoryMarker extends Marker implements Cloneable, Serializable {
    private boolean drawAsLine;
    private Comparable key;

    public CategoryMarker(Comparable key) {
        this(key, Color.gray, new BasicStroke(Plot.DEFAULT_FOREGROUND_ALPHA));
    }

    public CategoryMarker(Comparable key, Paint paint, Stroke stroke) {
        this(key, paint, stroke, paint, stroke, Plot.DEFAULT_FOREGROUND_ALPHA);
    }

    public CategoryMarker(Comparable key, Paint paint, Stroke stroke, Paint outlinePaint, Stroke outlineStroke, float alpha) {
        super(paint, stroke, outlinePaint, outlineStroke, alpha);
        this.drawAsLine = false;
        this.key = key;
        setLabelOffsetType(LengthAdjustmentType.EXPAND);
    }

    public Comparable getKey() {
        return this.key;
    }

    public void setKey(Comparable key) {
        ParamChecks.nullNotPermitted(key, "key");
        this.key = key;
        notifyListeners(new MarkerChangeEvent(this));
    }

    public boolean getDrawAsLine() {
        return this.drawAsLine;
    }

    public void setDrawAsLine(boolean drawAsLine) {
        this.drawAsLine = drawAsLine;
        notifyListeners(new MarkerChangeEvent(this));
    }

    public boolean equals(Object obj) {
        if (obj == null || !(obj instanceof CategoryMarker) || !super.equals(obj)) {
            return false;
        }
        CategoryMarker that = (CategoryMarker) obj;
        if (this.key.equals(that.key) && this.drawAsLine == that.drawAsLine) {
            return true;
        }
        return false;
    }
}
