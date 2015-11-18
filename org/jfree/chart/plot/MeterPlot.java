package org.jfree.chart.plot;

import java.awt.AlphaComposite;
import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Composite;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.Paint;
import java.awt.Polygon;
import java.awt.Shape;
import java.awt.Stroke;
import java.awt.geom.Arc2D;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Line2D;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.awt.geom.Rectangle2D.Double;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.ResourceBundle;
import org.jfree.chart.LegendItem;
import org.jfree.chart.LegendItemCollection;
import org.jfree.chart.annotations.XYPointerAnnotation;
import org.jfree.chart.axis.Axis;
import org.jfree.chart.axis.DateAxis;
import org.jfree.chart.renderer.category.BarRenderer;
import org.jfree.chart.renderer.xy.XYLine3DRenderer;
import org.jfree.chart.util.ParamChecks;
import org.jfree.chart.util.ResourceBundleWrapper;
import org.jfree.data.Range;
import org.jfree.data.general.DatasetChangeEvent;
import org.jfree.data.general.ValueDataset;
import org.jfree.io.SerialUtilities;
import org.jfree.text.TextUtilities;
import org.jfree.ui.TextAnchor;
import org.jfree.util.ObjectUtilities;
import org.jfree.util.PaintUtilities;

public class MeterPlot extends Plot implements Serializable, Cloneable {
    public static final float DEFAULT_BORDER_SIZE = 3.0f;
    public static final float DEFAULT_CIRCLE_SIZE = 10.0f;
    static final Paint DEFAULT_DIAL_BACKGROUND_PAINT;
    public static final Font DEFAULT_LABEL_FONT;
    public static final int DEFAULT_METER_ANGLE = 270;
    static final Paint DEFAULT_NEEDLE_PAINT;
    static final Font DEFAULT_VALUE_FONT;
    static final Paint DEFAULT_VALUE_PAINT;
    protected static ResourceBundle localizationResources = null;
    private static final long serialVersionUID = 2987472457734470962L;
    private ValueDataset dataset;
    private transient Paint dialBackgroundPaint;
    private transient Paint dialOutlinePaint;
    private boolean drawBorder;
    private List intervals;
    private int meterAngle;
    private transient Paint needlePaint;
    private Range range;
    private DialShape shape;
    private Font tickLabelFont;
    private NumberFormat tickLabelFormat;
    private transient Paint tickLabelPaint;
    private boolean tickLabelsVisible;
    private transient Paint tickPaint;
    private double tickSize;
    private String units;
    private Font valueFont;
    private transient Paint valuePaint;

    static {
        DEFAULT_DIAL_BACKGROUND_PAINT = Color.black;
        DEFAULT_NEEDLE_PAINT = Color.green;
        DEFAULT_VALUE_FONT = new Font("SansSerif", 1, 12);
        DEFAULT_VALUE_PAINT = Color.yellow;
        DEFAULT_LABEL_FONT = new Font("SansSerif", 1, 10);
        localizationResources = ResourceBundleWrapper.getBundle("org.jfree.chart.plot.LocalizationBundle");
    }

    public MeterPlot() {
        this(null);
    }

    public MeterPlot(ValueDataset dataset) {
        this.shape = DialShape.CIRCLE;
        this.meterAngle = DEFAULT_METER_ANGLE;
        this.range = new Range(0.0d, 100.0d);
        this.tickSize = XYPointerAnnotation.DEFAULT_TIP_RADIUS;
        this.tickPaint = Color.white;
        this.units = "Units";
        this.needlePaint = DEFAULT_NEEDLE_PAINT;
        this.tickLabelsVisible = true;
        this.tickLabelFont = DEFAULT_LABEL_FONT;
        this.tickLabelPaint = Color.black;
        this.tickLabelFormat = NumberFormat.getInstance();
        this.valueFont = DEFAULT_VALUE_FONT;
        this.valuePaint = DEFAULT_VALUE_PAINT;
        this.dialBackgroundPaint = DEFAULT_DIAL_BACKGROUND_PAINT;
        this.intervals = new ArrayList();
        setDataset(dataset);
    }

    public DialShape getDialShape() {
        return this.shape;
    }

    public void setDialShape(DialShape shape) {
        ParamChecks.nullNotPermitted(shape, "shape");
        this.shape = shape;
        fireChangeEvent();
    }

    public int getMeterAngle() {
        return this.meterAngle;
    }

    public void setMeterAngle(int angle) {
        if (angle < 1 || angle > 360) {
            throw new IllegalArgumentException("Invalid 'angle' (" + angle + ")");
        }
        this.meterAngle = angle;
        fireChangeEvent();
    }

    public Range getRange() {
        return this.range;
    }

    public void setRange(Range range) {
        ParamChecks.nullNotPermitted(range, "range");
        if (range.getLength() <= 0.0d) {
            throw new IllegalArgumentException("Range length must be positive.");
        }
        this.range = range;
        fireChangeEvent();
    }

    public double getTickSize() {
        return this.tickSize;
    }

    public void setTickSize(double size) {
        if (size <= 0.0d) {
            throw new IllegalArgumentException("Requires 'size' > 0.");
        }
        this.tickSize = size;
        fireChangeEvent();
    }

    public Paint getTickPaint() {
        return this.tickPaint;
    }

    public void setTickPaint(Paint paint) {
        ParamChecks.nullNotPermitted(paint, "paint");
        this.tickPaint = paint;
        fireChangeEvent();
    }

    public String getUnits() {
        return this.units;
    }

    public void setUnits(String units) {
        this.units = units;
        fireChangeEvent();
    }

    public Paint getNeedlePaint() {
        return this.needlePaint;
    }

    public void setNeedlePaint(Paint paint) {
        ParamChecks.nullNotPermitted(paint, "paint");
        this.needlePaint = paint;
        fireChangeEvent();
    }

    public boolean getTickLabelsVisible() {
        return this.tickLabelsVisible;
    }

    public void setTickLabelsVisible(boolean visible) {
        if (this.tickLabelsVisible != visible) {
            this.tickLabelsVisible = visible;
            fireChangeEvent();
        }
    }

    public Font getTickLabelFont() {
        return this.tickLabelFont;
    }

    public void setTickLabelFont(Font font) {
        ParamChecks.nullNotPermitted(font, "font");
        if (!this.tickLabelFont.equals(font)) {
            this.tickLabelFont = font;
            fireChangeEvent();
        }
    }

    public Paint getTickLabelPaint() {
        return this.tickLabelPaint;
    }

    public void setTickLabelPaint(Paint paint) {
        ParamChecks.nullNotPermitted(paint, "paint");
        if (!this.tickLabelPaint.equals(paint)) {
            this.tickLabelPaint = paint;
            fireChangeEvent();
        }
    }

    public NumberFormat getTickLabelFormat() {
        return this.tickLabelFormat;
    }

    public void setTickLabelFormat(NumberFormat format) {
        ParamChecks.nullNotPermitted(format, "format");
        this.tickLabelFormat = format;
        fireChangeEvent();
    }

    public Font getValueFont() {
        return this.valueFont;
    }

    public void setValueFont(Font font) {
        ParamChecks.nullNotPermitted(font, "font");
        this.valueFont = font;
        fireChangeEvent();
    }

    public Paint getValuePaint() {
        return this.valuePaint;
    }

    public void setValuePaint(Paint paint) {
        ParamChecks.nullNotPermitted(paint, "paint");
        this.valuePaint = paint;
        fireChangeEvent();
    }

    public Paint getDialBackgroundPaint() {
        return this.dialBackgroundPaint;
    }

    public void setDialBackgroundPaint(Paint paint) {
        this.dialBackgroundPaint = paint;
        fireChangeEvent();
    }

    public boolean getDrawBorder() {
        return this.drawBorder;
    }

    public void setDrawBorder(boolean draw) {
        this.drawBorder = draw;
        fireChangeEvent();
    }

    public Paint getDialOutlinePaint() {
        return this.dialOutlinePaint;
    }

    public void setDialOutlinePaint(Paint paint) {
        this.dialOutlinePaint = paint;
        fireChangeEvent();
    }

    public ValueDataset getDataset() {
        return this.dataset;
    }

    public void setDataset(ValueDataset dataset) {
        ValueDataset existing = this.dataset;
        if (existing != null) {
            existing.removeChangeListener(this);
        }
        this.dataset = dataset;
        if (dataset != null) {
            setDatasetGroup(dataset.getGroup());
            dataset.addChangeListener(this);
        }
        datasetChanged(new DatasetChangeEvent(this, dataset));
    }

    public List getIntervals() {
        return Collections.unmodifiableList(this.intervals);
    }

    public void addInterval(MeterInterval interval) {
        ParamChecks.nullNotPermitted(interval, "interval");
        this.intervals.add(interval);
        fireChangeEvent();
    }

    public void clearIntervals() {
        this.intervals.clear();
        fireChangeEvent();
    }

    public LegendItemCollection getLegendItems() {
        LegendItemCollection result = new LegendItemCollection();
        for (MeterInterval mi : this.intervals) {
            Paint color = mi.getBackgroundPaint();
            if (color == null) {
                color = mi.getOutlinePaint();
            }
            LegendItem legendItem = new LegendItem(mi.getLabel(), mi.getLabel(), null, null, new Double(-4.0d, -4.0d, XYLine3DRenderer.DEFAULT_Y_OFFSET, XYLine3DRenderer.DEFAULT_Y_OFFSET), color);
            legendItem.setDataset(getDataset());
            result.add(legendItem);
        }
        return result;
    }

    public void draw(Graphics2D g2, Rectangle2D area, Point2D anchor, PlotState parentState, PlotRenderingInfo info) {
        if (info != null) {
            info.setPlotArea(area);
        }
        getInsets().trim(area);
        area.setRect(area.getX() + 4.0d, area.getY() + 4.0d, area.getWidth() - XYLine3DRenderer.DEFAULT_Y_OFFSET, area.getHeight() - XYLine3DRenderer.DEFAULT_Y_OFFSET);
        if (this.drawBorder) {
            drawBackground(g2, area);
        }
        double meterX = area.getX() + (6.0d / DateAxis.DEFAULT_AUTO_RANGE_MINIMUM_SIZE_IN_MILLISECONDS);
        double meterY = area.getY() + (6.0d / DateAxis.DEFAULT_AUTO_RANGE_MINIMUM_SIZE_IN_MILLISECONDS);
        double meterW = area.getWidth() - 6.0d;
        double height = area.getHeight() - 6.0d;
        int i = this.meterAngle;
        double height2 = (r0 > 180 || this.shape == DialShape.CIRCLE) ? 0.0d : area.getHeight() / 1.25d;
        double meterH = height + height2;
        double min = Math.min(meterW, meterH) / DateAxis.DEFAULT_AUTO_RANGE_MINIMUM_SIZE_IN_MILLISECONDS;
        Rectangle2D meterArea = new Double((((meterX + meterX) + meterW) / DateAxis.DEFAULT_AUTO_RANGE_MINIMUM_SIZE_IN_MILLISECONDS) - min, (((meterY + meterY) + meterH) / DateAxis.DEFAULT_AUTO_RANGE_MINIMUM_SIZE_IN_MILLISECONDS) - min, DateAxis.DEFAULT_AUTO_RANGE_MINIMUM_SIZE_IN_MILLISECONDS * min, DateAxis.DEFAULT_AUTO_RANGE_MINIMUM_SIZE_IN_MILLISECONDS * min);
        Double originalArea = new Double(meterArea.getX() - 4.0d, meterArea.getY() - 4.0d, meterArea.getWidth() + XYLine3DRenderer.DEFAULT_Y_OFFSET, meterArea.getHeight() + XYLine3DRenderer.DEFAULT_Y_OFFSET);
        double meterMiddleX = meterArea.getCenterX();
        double meterMiddleY = meterArea.getCenterY();
        ValueDataset data = getDataset();
        if (data != null) {
            double dataMin = this.range.getLowerBound();
            double dataMax = this.range.getUpperBound();
            Shape savedClip = g2.getClip();
            g2.clip(originalArea);
            Composite originalComposite = g2.getComposite();
            g2.setComposite(AlphaComposite.getInstance(3, getForegroundAlpha()));
            if (this.dialBackgroundPaint != null) {
                fillArc(g2, originalArea, dataMin, dataMax, this.dialBackgroundPaint, true);
            }
            drawTicks(g2, meterArea, dataMin, dataMax);
            drawArcForInterval(g2, meterArea, new MeterInterval("", this.range, this.dialOutlinePaint, new BasicStroke(Plot.DEFAULT_FOREGROUND_ALPHA), null));
            for (MeterInterval drawArcForInterval : this.intervals) {
                drawArcForInterval(g2, meterArea, drawArcForInterval);
            }
            Number n = data.getValue();
            if (n != null) {
                double value = n.doubleValue();
                drawValueLabel(g2, meterArea);
                if (this.range.contains(value)) {
                    g2.setPaint(this.needlePaint);
                    g2.setStroke(new BasicStroke(Axis.DEFAULT_TICK_MARK_OUTSIDE_LENGTH));
                    double radius = ((meterArea.getWidth() / DateAxis.DEFAULT_AUTO_RANGE_MINIMUM_SIZE_IN_MILLISECONDS) + BarRenderer.BAR_OUTLINE_WIDTH_THRESHOLD) + 15.0d;
                    double valueAngle = valueToAngle(value);
                    double valueP1 = meterMiddleX + (Math.cos(3.141592653589793d * (valueAngle / 180.0d)) * radius);
                    double valueP2 = meterMiddleY - (Math.sin(3.141592653589793d * (valueAngle / 180.0d)) * radius);
                    Polygon arrow = new Polygon();
                    if ((valueAngle <= 135.0d || valueAngle >= 225.0d) && (valueAngle >= PolarPlot.DEFAULT_ANGLE_TICK_UNIT_SIZE || valueAngle <= -45.0d)) {
                        arrow.addPoint((int) (meterMiddleX - 2.5d), (int) meterMiddleY);
                        arrow.addPoint((int) (2.5d + meterMiddleX), (int) meterMiddleY);
                    } else {
                        double valueP4 = meterMiddleY + 2.5d;
                        arrow.addPoint((int) meterMiddleX, (int) (meterMiddleY - 2.5d));
                        arrow.addPoint((int) meterMiddleX, (int) valueP4);
                    }
                    arrow.addPoint((int) valueP1, (int) valueP2);
                    g2.fill(arrow);
                    g2.fill(new Ellipse2D.Double(meterMiddleX - XYPointerAnnotation.DEFAULT_ARROW_LENGTH, meterMiddleY - XYPointerAnnotation.DEFAULT_ARROW_LENGTH, XYPointerAnnotation.DEFAULT_TIP_RADIUS, XYPointerAnnotation.DEFAULT_TIP_RADIUS));
                }
            }
            g2.setClip(savedClip);
            g2.setComposite(originalComposite);
        }
        if (this.drawBorder) {
            drawOutline(g2, area);
        }
    }

    protected void drawArcForInterval(Graphics2D g2, Rectangle2D meterArea, MeterInterval interval) {
        double minValue = interval.getRange().getLowerBound();
        double maxValue = interval.getRange().getUpperBound();
        Paint outlinePaint = interval.getOutlinePaint();
        Stroke outlineStroke = interval.getOutlineStroke();
        Paint backgroundPaint = interval.getBackgroundPaint();
        if (backgroundPaint != null) {
            fillArc(g2, meterArea, minValue, maxValue, backgroundPaint, false);
        }
        if (outlinePaint != null) {
            if (outlineStroke != null) {
                drawArc(g2, meterArea, minValue, maxValue, outlinePaint, outlineStroke);
            }
            drawTick(g2, meterArea, minValue, true);
            drawTick(g2, meterArea, maxValue, true);
        }
    }

    protected void drawArc(Graphics2D g2, Rectangle2D area, double minValue, double maxValue, Paint paint, Stroke stroke) {
        double startAngle = valueToAngle(maxValue);
        double extent = valueToAngle(minValue) - startAngle;
        double x = area.getX();
        double y = area.getY();
        double w = area.getWidth();
        double h = area.getHeight();
        g2.setPaint(paint);
        g2.setStroke(stroke);
        if (paint != null && stroke != null) {
            Arc2D.Double arc = new Arc2D.Double(x, y, w, h, startAngle, extent, 0);
            g2.setPaint(paint);
            g2.setStroke(stroke);
            g2.draw(arc);
        }
    }

    protected void fillArc(Graphics2D g2, Rectangle2D area, double minValue, double maxValue, Paint paint, boolean dial) {
        int joinType;
        ParamChecks.nullNotPermitted(paint, "paint");
        double startAngle = valueToAngle(maxValue);
        double extent = valueToAngle(minValue) - startAngle;
        double x = area.getX();
        double y = area.getY();
        double w = area.getWidth();
        double h = area.getHeight();
        if (this.shape == DialShape.PIE) {
            joinType = 2;
        } else if (this.shape == DialShape.CHORD) {
            joinType = (!dial || this.meterAngle <= 180) ? 2 : 1;
        } else if (this.shape == DialShape.CIRCLE) {
            joinType = 2;
            if (dial) {
                extent = 360.0d;
            }
        } else {
            throw new IllegalStateException("DialShape not recognised.");
        }
        g2.setPaint(paint);
        g2.fill(new Arc2D.Double(x, y, w, h, startAngle, extent, joinType));
    }

    public double valueToAngle(double value) {
        return ((double) (((this.meterAngle - 180) / 2) + 180)) - (((value - this.range.getLowerBound()) / this.range.getLength()) * ((double) this.meterAngle));
    }

    protected void drawTicks(Graphics2D g2, Rectangle2D meterArea, double minValue, double maxValue) {
        double v = minValue;
        while (v <= maxValue) {
            drawTick(g2, meterArea, v);
            v += this.tickSize;
        }
    }

    protected void drawTick(Graphics2D g2, Rectangle2D meterArea, double value) {
        drawTick(g2, meterArea, value, false);
    }

    protected void drawTick(Graphics2D g2, Rectangle2D meterArea, double value, boolean label) {
        double valueAngle = valueToAngle(value);
        double meterMiddleX = meterArea.getCenterX();
        double meterMiddleY = meterArea.getCenterY();
        g2.setPaint(this.tickPaint);
        g2.setStroke(new BasicStroke(Axis.DEFAULT_TICK_MARK_OUTSIDE_LENGTH));
        double radius = (meterArea.getWidth() / DateAxis.DEFAULT_AUTO_RANGE_MINIMUM_SIZE_IN_MILLISECONDS) + BarRenderer.BAR_OUTLINE_WIDTH_THRESHOLD;
        double radius1 = radius - 15.0d;
        double valueP2X = meterMiddleX + (Math.cos(3.141592653589793d * (valueAngle / 180.0d)) * radius1);
        double valueP2Y = meterMiddleY - (Math.sin(3.141592653589793d * (valueAngle / 180.0d)) * radius1);
        Graphics2D graphics2D = g2;
        graphics2D.draw(new Line2D.Double(meterMiddleX + (Math.cos(3.141592653589793d * (valueAngle / 180.0d)) * radius), meterMiddleY - (Math.sin(3.141592653589793d * (valueAngle / 180.0d)) * radius), valueP2X, valueP2Y));
        if (this.tickLabelsVisible && label) {
            String tickLabel = this.tickLabelFormat.format(value);
            g2.setFont(this.tickLabelFont);
            g2.setPaint(this.tickLabelPaint);
            Rectangle2D tickLabelBounds = TextUtilities.getTextBounds(tickLabel, g2, g2.getFontMetrics());
            double x = valueP2X;
            double y = valueP2Y;
            if (valueAngle == SpiderWebPlot.DEFAULT_START_ANGLE || valueAngle == 270.0d) {
                x -= tickLabelBounds.getWidth() / DateAxis.DEFAULT_AUTO_RANGE_MINIMUM_SIZE_IN_MILLISECONDS;
            } else if (valueAngle < SpiderWebPlot.DEFAULT_START_ANGLE || valueAngle > 270.0d) {
                x -= tickLabelBounds.getWidth();
            }
            if ((valueAngle <= 135.0d || valueAngle >= 225.0d) && valueAngle <= 315.0d && valueAngle >= PolarPlot.DEFAULT_ANGLE_TICK_UNIT_SIZE) {
                y += tickLabelBounds.getHeight() / DateAxis.DEFAULT_AUTO_RANGE_MINIMUM_SIZE_IN_MILLISECONDS;
            } else {
                y -= tickLabelBounds.getHeight() / DateAxis.DEFAULT_AUTO_RANGE_MINIMUM_SIZE_IN_MILLISECONDS;
            }
            g2.drawString(tickLabel, (float) x, (float) y);
        }
    }

    protected void drawValueLabel(Graphics2D g2, Rectangle2D area) {
        g2.setFont(this.valueFont);
        g2.setPaint(this.valuePaint);
        String valueStr = "No value";
        if (this.dataset != null) {
            Number n = this.dataset.getValue();
            if (n != null) {
                valueStr = this.tickLabelFormat.format(n.doubleValue()) + " " + this.units;
            }
        }
        TextUtilities.drawAlignedString(valueStr, g2, (float) area.getCenterX(), ((float) area.getCenterY()) + DEFAULT_CIRCLE_SIZE, TextAnchor.TOP_CENTER);
    }

    public String getPlotType() {
        return localizationResources.getString("Meter_Plot");
    }

    public void zoom(double percent) {
    }

    public boolean equals(Object obj) {
        if (obj == this) {
            return true;
        }
        if (!(obj instanceof MeterPlot)) {
            return false;
        }
        if (!super.equals(obj)) {
            return false;
        }
        MeterPlot that = (MeterPlot) obj;
        if (!ObjectUtilities.equal(this.units, that.units)) {
            return false;
        }
        if (!ObjectUtilities.equal(this.range, that.range)) {
            return false;
        }
        if (!ObjectUtilities.equal(this.intervals, that.intervals)) {
            return false;
        }
        if (!PaintUtilities.equal(this.dialOutlinePaint, that.dialOutlinePaint)) {
            return false;
        }
        if (this.shape != that.shape) {
            return false;
        }
        if (!PaintUtilities.equal(this.dialBackgroundPaint, that.dialBackgroundPaint)) {
            return false;
        }
        if (!PaintUtilities.equal(this.needlePaint, that.needlePaint)) {
            return false;
        }
        if (!ObjectUtilities.equal(this.valueFont, that.valueFont)) {
            return false;
        }
        if (!PaintUtilities.equal(this.valuePaint, that.valuePaint)) {
            return false;
        }
        if (!PaintUtilities.equal(this.tickPaint, that.tickPaint)) {
            return false;
        }
        if (this.tickSize != that.tickSize) {
            return false;
        }
        if (this.tickLabelsVisible != that.tickLabelsVisible) {
            return false;
        }
        if (!ObjectUtilities.equal(this.tickLabelFont, that.tickLabelFont)) {
            return false;
        }
        if (!PaintUtilities.equal(this.tickLabelPaint, that.tickLabelPaint)) {
            return false;
        }
        if (!ObjectUtilities.equal(this.tickLabelFormat, that.tickLabelFormat)) {
            return false;
        }
        if (this.drawBorder != that.drawBorder) {
            return false;
        }
        if (this.meterAngle != that.meterAngle) {
            return false;
        }
        return true;
    }

    private void writeObject(ObjectOutputStream stream) throws IOException {
        stream.defaultWriteObject();
        SerialUtilities.writePaint(this.dialBackgroundPaint, stream);
        SerialUtilities.writePaint(this.dialOutlinePaint, stream);
        SerialUtilities.writePaint(this.needlePaint, stream);
        SerialUtilities.writePaint(this.valuePaint, stream);
        SerialUtilities.writePaint(this.tickPaint, stream);
        SerialUtilities.writePaint(this.tickLabelPaint, stream);
    }

    private void readObject(ObjectInputStream stream) throws IOException, ClassNotFoundException {
        stream.defaultReadObject();
        this.dialBackgroundPaint = SerialUtilities.readPaint(stream);
        this.dialOutlinePaint = SerialUtilities.readPaint(stream);
        this.needlePaint = SerialUtilities.readPaint(stream);
        this.valuePaint = SerialUtilities.readPaint(stream);
        this.tickPaint = SerialUtilities.readPaint(stream);
        this.tickLabelPaint = SerialUtilities.readPaint(stream);
        if (this.dataset != null) {
            this.dataset.addChangeListener(this);
        }
    }

    public Object clone() throws CloneNotSupportedException {
        MeterPlot clone = (MeterPlot) super.clone();
        clone.tickLabelFormat = (NumberFormat) this.tickLabelFormat.clone();
        clone.intervals = new ArrayList(this.intervals);
        if (clone.dataset != null) {
            clone.dataset.addChangeListener(clone);
        }
        return clone;
    }
}
