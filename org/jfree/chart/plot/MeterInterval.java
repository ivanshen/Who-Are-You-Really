package org.jfree.chart.plot;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Paint;
import java.awt.Stroke;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import org.jfree.chart.axis.Axis;
import org.jfree.chart.util.ParamChecks;
import org.jfree.data.Range;
import org.jfree.io.SerialUtilities;
import org.jfree.util.ObjectUtilities;
import org.jfree.util.PaintUtilities;

public class MeterInterval implements Serializable {
    private static final long serialVersionUID = 1530982090622488257L;
    private transient Paint backgroundPaint;
    private String label;
    private transient Paint outlinePaint;
    private transient Stroke outlineStroke;
    private Range range;

    public MeterInterval(String label, Range range) {
        this(label, range, Color.yellow, new BasicStroke(Axis.DEFAULT_TICK_MARK_OUTSIDE_LENGTH), null);
    }

    public MeterInterval(String label, Range range, Paint outlinePaint, Stroke outlineStroke, Paint backgroundPaint) {
        ParamChecks.nullNotPermitted(label, "label");
        ParamChecks.nullNotPermitted(range, "range");
        this.label = label;
        this.range = range;
        this.outlinePaint = outlinePaint;
        this.outlineStroke = outlineStroke;
        this.backgroundPaint = backgroundPaint;
    }

    public String getLabel() {
        return this.label;
    }

    public Range getRange() {
        return this.range;
    }

    public Paint getBackgroundPaint() {
        return this.backgroundPaint;
    }

    public Paint getOutlinePaint() {
        return this.outlinePaint;
    }

    public Stroke getOutlineStroke() {
        return this.outlineStroke;
    }

    public boolean equals(Object obj) {
        if (obj == this) {
            return true;
        }
        if (!(obj instanceof MeterInterval)) {
            return false;
        }
        MeterInterval that = (MeterInterval) obj;
        if (!this.label.equals(that.label)) {
            return false;
        }
        if (!this.range.equals(that.range)) {
            return false;
        }
        if (!PaintUtilities.equal(this.outlinePaint, that.outlinePaint)) {
            return false;
        }
        if (!ObjectUtilities.equal(this.outlineStroke, that.outlineStroke)) {
            return false;
        }
        if (PaintUtilities.equal(this.backgroundPaint, that.backgroundPaint)) {
            return true;
        }
        return false;
    }

    private void writeObject(ObjectOutputStream stream) throws IOException {
        stream.defaultWriteObject();
        SerialUtilities.writePaint(this.outlinePaint, stream);
        SerialUtilities.writeStroke(this.outlineStroke, stream);
        SerialUtilities.writePaint(this.backgroundPaint, stream);
    }

    private void readObject(ObjectInputStream stream) throws IOException, ClassNotFoundException {
        stream.defaultReadObject();
        this.outlinePaint = SerialUtilities.readPaint(stream);
        this.outlineStroke = SerialUtilities.readStroke(stream);
        this.backgroundPaint = SerialUtilities.readPaint(stream);
    }
}
