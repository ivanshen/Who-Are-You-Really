package org.jfree.chart.axis;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Font;
import java.awt.Paint;
import java.awt.Stroke;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.text.DateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.util.ParamChecks;
import org.jfree.data.time.RegularTimePeriod;
import org.jfree.io.SerialUtilities;
import org.jfree.ui.RectangleInsets;

public class PeriodAxisLabelInfo implements Cloneable, Serializable {
    public static final Paint DEFAULT_DIVIDER_PAINT;
    public static final Stroke DEFAULT_DIVIDER_STROKE;
    public static final Font DEFAULT_FONT;
    public static final RectangleInsets DEFAULT_INSETS;
    public static final Paint DEFAULT_LABEL_PAINT;
    private static final long serialVersionUID = 5710451740920277357L;
    private DateFormat dateFormat;
    private transient Paint dividerPaint;
    private transient Stroke dividerStroke;
    private boolean drawDividers;
    private Font labelFont;
    private transient Paint labelPaint;
    private RectangleInsets padding;
    private Class periodClass;

    static {
        DEFAULT_INSETS = new RectangleInsets(DateAxis.DEFAULT_AUTO_RANGE_MINIMUM_SIZE_IN_MILLISECONDS, DateAxis.DEFAULT_AUTO_RANGE_MINIMUM_SIZE_IN_MILLISECONDS, DateAxis.DEFAULT_AUTO_RANGE_MINIMUM_SIZE_IN_MILLISECONDS, DateAxis.DEFAULT_AUTO_RANGE_MINIMUM_SIZE_IN_MILLISECONDS);
        DEFAULT_FONT = new Font("SansSerif", 0, 10);
        DEFAULT_LABEL_PAINT = Color.black;
        DEFAULT_DIVIDER_STROKE = new BasicStroke(JFreeChart.DEFAULT_BACKGROUND_IMAGE_ALPHA);
        DEFAULT_DIVIDER_PAINT = Color.gray;
    }

    public PeriodAxisLabelInfo(Class periodClass, DateFormat dateFormat) {
        this(periodClass, dateFormat, DEFAULT_INSETS, DEFAULT_FONT, DEFAULT_LABEL_PAINT, true, DEFAULT_DIVIDER_STROKE, DEFAULT_DIVIDER_PAINT);
    }

    public PeriodAxisLabelInfo(Class periodClass, DateFormat dateFormat, RectangleInsets padding, Font labelFont, Paint labelPaint, boolean drawDividers, Stroke dividerStroke, Paint dividerPaint) {
        ParamChecks.nullNotPermitted(periodClass, "periodClass");
        ParamChecks.nullNotPermitted(dateFormat, "dateFormat");
        ParamChecks.nullNotPermitted(padding, "padding");
        ParamChecks.nullNotPermitted(labelFont, "labelFont");
        ParamChecks.nullNotPermitted(labelPaint, "labelPaint");
        ParamChecks.nullNotPermitted(dividerStroke, "dividerStroke");
        ParamChecks.nullNotPermitted(dividerPaint, "dividerPaint");
        this.periodClass = periodClass;
        this.dateFormat = (DateFormat) dateFormat.clone();
        this.padding = padding;
        this.labelFont = labelFont;
        this.labelPaint = labelPaint;
        this.drawDividers = drawDividers;
        this.dividerStroke = dividerStroke;
        this.dividerPaint = dividerPaint;
    }

    public Class getPeriodClass() {
        return this.periodClass;
    }

    public DateFormat getDateFormat() {
        return (DateFormat) this.dateFormat.clone();
    }

    public RectangleInsets getPadding() {
        return this.padding;
    }

    public Font getLabelFont() {
        return this.labelFont;
    }

    public Paint getLabelPaint() {
        return this.labelPaint;
    }

    public boolean getDrawDividers() {
        return this.drawDividers;
    }

    public Stroke getDividerStroke() {
        return this.dividerStroke;
    }

    public Paint getDividerPaint() {
        return this.dividerPaint;
    }

    public RegularTimePeriod createInstance(Date millisecond, TimeZone zone) {
        return createInstance(millisecond, zone, Locale.getDefault());
    }

    public RegularTimePeriod createInstance(Date millisecond, TimeZone zone, Locale locale) {
        RegularTimePeriod result = null;
        try {
            return (RegularTimePeriod) this.periodClass.getDeclaredConstructor(new Class[]{Date.class, TimeZone.class, Locale.class}).newInstance(new Object[]{millisecond, zone, locale});
        } catch (Exception e) {
            return result;
        }
    }

    public boolean equals(Object obj) {
        if (obj == this) {
            return true;
        }
        if (!(obj instanceof PeriodAxisLabelInfo)) {
            return false;
        }
        PeriodAxisLabelInfo info = (PeriodAxisLabelInfo) obj;
        if (!info.periodClass.equals(this.periodClass)) {
            return false;
        }
        if (!info.dateFormat.equals(this.dateFormat)) {
            return false;
        }
        if (!info.padding.equals(this.padding)) {
            return false;
        }
        if (!info.labelFont.equals(this.labelFont)) {
            return false;
        }
        if (!info.labelPaint.equals(this.labelPaint)) {
            return false;
        }
        if (info.drawDividers != this.drawDividers) {
            return false;
        }
        if (!info.dividerStroke.equals(this.dividerStroke)) {
            return false;
        }
        if (info.dividerPaint.equals(this.dividerPaint)) {
            return true;
        }
        return false;
    }

    public int hashCode() {
        return (41 + (this.periodClass.hashCode() * 37)) + (this.dateFormat.hashCode() * 37);
    }

    public Object clone() throws CloneNotSupportedException {
        return (PeriodAxisLabelInfo) super.clone();
    }

    private void writeObject(ObjectOutputStream stream) throws IOException {
        stream.defaultWriteObject();
        SerialUtilities.writePaint(this.labelPaint, stream);
        SerialUtilities.writeStroke(this.dividerStroke, stream);
        SerialUtilities.writePaint(this.dividerPaint, stream);
    }

    private void readObject(ObjectInputStream stream) throws IOException, ClassNotFoundException {
        stream.defaultReadObject();
        this.labelPaint = SerialUtilities.readPaint(stream);
        this.dividerStroke = SerialUtilities.readStroke(stream);
        this.dividerPaint = SerialUtilities.readPaint(stream);
    }
}
