package org.jfree.chart.axis;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.FontMetrics;
import java.awt.Graphics2D;
import java.awt.Paint;
import java.awt.Stroke;
import java.awt.geom.Line2D;
import java.awt.geom.Line2D.Double;
import java.awt.geom.Line2D.Float;
import java.awt.geom.Rectangle2D;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.TimeZone;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.annotations.XYPointerAnnotation;
import org.jfree.chart.plot.Plot;
import org.jfree.chart.plot.PlotRenderingInfo;
import org.jfree.chart.plot.ValueAxisPlot;
import org.jfree.chart.util.ParamChecks;
import org.jfree.data.Range;
import org.jfree.data.time.Day;
import org.jfree.data.time.Month;
import org.jfree.data.time.RegularTimePeriod;
import org.jfree.data.time.Year;
import org.jfree.data.xy.NormalizedMatrixSeries;
import org.jfree.io.SerialUtilities;
import org.jfree.text.TextUtilities;
import org.jfree.ui.RectangleEdge;
import org.jfree.ui.TextAnchor;
import org.jfree.util.PublicCloneable;

public class PeriodAxis extends ValueAxis implements Cloneable, PublicCloneable, Serializable {
    private static final long serialVersionUID = 8353295532075872069L;
    private Class autoRangeTimePeriodClass;
    private Calendar calendar;
    private RegularTimePeriod first;
    private PeriodAxisLabelInfo[] labelInfo;
    private RegularTimePeriod last;
    private Locale locale;
    private Class majorTickTimePeriodClass;
    private float minorTickMarkInsideLength;
    private float minorTickMarkOutsideLength;
    private transient Paint minorTickMarkPaint;
    private transient Stroke minorTickMarkStroke;
    private boolean minorTickMarksVisible;
    private Class minorTickTimePeriodClass;
    private TimeZone timeZone;

    public PeriodAxis(String label) {
        this(label, new Day(), new Day());
    }

    public PeriodAxis(String label, RegularTimePeriod first, RegularTimePeriod last) {
        this(label, first, last, TimeZone.getDefault(), Locale.getDefault());
    }

    public PeriodAxis(String label, RegularTimePeriod first, RegularTimePeriod last, TimeZone timeZone) {
        this(label, first, last, timeZone, Locale.getDefault());
    }

    public PeriodAxis(String label, RegularTimePeriod first, RegularTimePeriod last, TimeZone timeZone, Locale locale) {
        super(label, null);
        this.minorTickMarkInsideLength = 0.0f;
        this.minorTickMarkOutsideLength = Axis.DEFAULT_TICK_MARK_OUTSIDE_LENGTH;
        this.minorTickMarkStroke = new BasicStroke(JFreeChart.DEFAULT_BACKGROUND_IMAGE_ALPHA);
        this.minorTickMarkPaint = Color.black;
        ParamChecks.nullNotPermitted(timeZone, "timeZone");
        ParamChecks.nullNotPermitted(locale, "locale");
        this.first = first;
        this.last = last;
        this.timeZone = timeZone;
        this.locale = locale;
        this.calendar = Calendar.getInstance(timeZone, locale);
        this.first.peg(this.calendar);
        this.last.peg(this.calendar);
        this.autoRangeTimePeriodClass = first.getClass();
        this.majorTickTimePeriodClass = first.getClass();
        this.minorTickMarksVisible = false;
        this.minorTickTimePeriodClass = RegularTimePeriod.downsize(this.majorTickTimePeriodClass);
        setAutoRange(true);
        this.labelInfo = new PeriodAxisLabelInfo[2];
        SimpleDateFormat df0 = new SimpleDateFormat("MMM", locale);
        df0.setTimeZone(timeZone);
        this.labelInfo[0] = new PeriodAxisLabelInfo(Month.class, df0);
        SimpleDateFormat df1 = new SimpleDateFormat("yyyy", locale);
        df1.setTimeZone(timeZone);
        this.labelInfo[1] = new PeriodAxisLabelInfo(Year.class, df1);
    }

    public RegularTimePeriod getFirst() {
        return this.first;
    }

    public void setFirst(RegularTimePeriod first) {
        ParamChecks.nullNotPermitted(first, "first");
        this.first = first;
        this.first.peg(this.calendar);
        fireChangeEvent();
    }

    public RegularTimePeriod getLast() {
        return this.last;
    }

    public void setLast(RegularTimePeriod last) {
        ParamChecks.nullNotPermitted(last, "last");
        this.last = last;
        this.last.peg(this.calendar);
        fireChangeEvent();
    }

    public TimeZone getTimeZone() {
        return this.timeZone;
    }

    public void setTimeZone(TimeZone zone) {
        ParamChecks.nullNotPermitted(zone, "zone");
        this.timeZone = zone;
        this.calendar = Calendar.getInstance(zone, this.locale);
        this.first.peg(this.calendar);
        this.last.peg(this.calendar);
        fireChangeEvent();
    }

    public Locale getLocale() {
        return this.locale;
    }

    public Class getAutoRangeTimePeriodClass() {
        return this.autoRangeTimePeriodClass;
    }

    public void setAutoRangeTimePeriodClass(Class c) {
        ParamChecks.nullNotPermitted(c, "c");
        this.autoRangeTimePeriodClass = c;
        fireChangeEvent();
    }

    public Class getMajorTickTimePeriodClass() {
        return this.majorTickTimePeriodClass;
    }

    public void setMajorTickTimePeriodClass(Class c) {
        ParamChecks.nullNotPermitted(c, "c");
        this.majorTickTimePeriodClass = c;
        fireChangeEvent();
    }

    public boolean isMinorTickMarksVisible() {
        return this.minorTickMarksVisible;
    }

    public void setMinorTickMarksVisible(boolean visible) {
        this.minorTickMarksVisible = visible;
        fireChangeEvent();
    }

    public Class getMinorTickTimePeriodClass() {
        return this.minorTickTimePeriodClass;
    }

    public void setMinorTickTimePeriodClass(Class c) {
        ParamChecks.nullNotPermitted(c, "c");
        this.minorTickTimePeriodClass = c;
        fireChangeEvent();
    }

    public Stroke getMinorTickMarkStroke() {
        return this.minorTickMarkStroke;
    }

    public void setMinorTickMarkStroke(Stroke stroke) {
        ParamChecks.nullNotPermitted(stroke, "stroke");
        this.minorTickMarkStroke = stroke;
        fireChangeEvent();
    }

    public Paint getMinorTickMarkPaint() {
        return this.minorTickMarkPaint;
    }

    public void setMinorTickMarkPaint(Paint paint) {
        ParamChecks.nullNotPermitted(paint, "paint");
        this.minorTickMarkPaint = paint;
        fireChangeEvent();
    }

    public float getMinorTickMarkInsideLength() {
        return this.minorTickMarkInsideLength;
    }

    public void setMinorTickMarkInsideLength(float length) {
        this.minorTickMarkInsideLength = length;
        fireChangeEvent();
    }

    public float getMinorTickMarkOutsideLength() {
        return this.minorTickMarkOutsideLength;
    }

    public void setMinorTickMarkOutsideLength(float length) {
        this.minorTickMarkOutsideLength = length;
        fireChangeEvent();
    }

    public PeriodAxisLabelInfo[] getLabelInfo() {
        return this.labelInfo;
    }

    public void setLabelInfo(PeriodAxisLabelInfo[] info) {
        this.labelInfo = info;
        fireChangeEvent();
    }

    public void setRange(Range range, boolean turnOffAutoRange, boolean notify) {
        long upper = Math.round(range.getUpperBound());
        this.first = createInstance(this.autoRangeTimePeriodClass, new Date(Math.round(range.getLowerBound())), this.timeZone, this.locale);
        this.last = createInstance(this.autoRangeTimePeriodClass, new Date(upper), this.timeZone, this.locale);
        super.setRange(new Range((double) this.first.getFirstMillisecond(), ((double) this.last.getLastMillisecond()) + NormalizedMatrixSeries.DEFAULT_SCALE_FACTOR), turnOffAutoRange, notify);
    }

    public void configure() {
        if (isAutoRange()) {
            autoAdjustRange();
        }
    }

    public AxisSpace reserveSpace(Graphics2D g2, Plot plot, Rectangle2D plotArea, RectangleEdge edge, AxisSpace space) {
        if (space == null) {
            space = new AxisSpace();
        }
        if (isVisible()) {
            double dimension = getFixedDimension();
            if (dimension > 0.0d) {
                space.ensureAtLeast(dimension, edge);
            }
            Rectangle2D labelEnclosure = getLabelEnclosure(g2, edge);
            double tickLabelBandsDimension = 0.0d;
            int i = 0;
            while (true) {
                int length = this.labelInfo.length;
                if (i >= r0) {
                    break;
                }
                PeriodAxisLabelInfo info = this.labelInfo[i];
                FontMetrics fm = g2.getFontMetrics(info.getLabelFont());
                tickLabelBandsDimension += info.getPadding().extendHeight((double) fm.getHeight());
                i++;
            }
            if (RectangleEdge.isTopOrBottom(edge)) {
                space.add(labelEnclosure.getHeight() + tickLabelBandsDimension, edge);
            } else if (RectangleEdge.isLeftOrRight(edge)) {
                space.add(labelEnclosure.getWidth() + tickLabelBandsDimension, edge);
            }
            double tickMarkSpace = 0.0d;
            if (isTickMarksVisible()) {
                tickMarkSpace = (double) getTickMarkOutsideLength();
            }
            if (this.minorTickMarksVisible) {
                tickMarkSpace = Math.max(tickMarkSpace, (double) this.minorTickMarkOutsideLength);
            }
            space.add(tickMarkSpace, edge);
        }
        return space;
    }

    public AxisState draw(Graphics2D g2, double cursor, Rectangle2D plotArea, Rectangle2D dataArea, RectangleEdge edge, PlotRenderingInfo plotState) {
        AxisState axisState;
        AxisState axisState2 = new AxisState(cursor);
        if (isAxisLineVisible()) {
            drawAxisLine(g2, cursor, dataArea, edge);
        }
        if (isTickMarksVisible()) {
            drawTickMarks(g2, axisState2, dataArea, edge);
        }
        if (isTickLabelsVisible()) {
            axisState = axisState2;
            for (int band = 0; band < this.labelInfo.length; band++) {
                axisState = drawTickLabels(band, g2, axisState, dataArea, edge);
            }
        } else {
            axisState = axisState2;
        }
        if (getAttributedLabel() != null) {
            return drawAttributedLabel(getAttributedLabel(), g2, plotArea, dataArea, edge, axisState);
        }
        return drawLabel(getLabel(), g2, plotArea, dataArea, edge, axisState);
    }

    protected void drawTickMarks(Graphics2D g2, AxisState state, Rectangle2D dataArea, RectangleEdge edge) {
        if (RectangleEdge.isTopOrBottom(edge)) {
            drawTickMarksHorizontal(g2, state, dataArea, edge);
        } else if (RectangleEdge.isLeftOrRight(edge)) {
            drawTickMarksVertical(g2, state, dataArea, edge);
        }
    }

    protected void drawTickMarksHorizontal(Graphics2D g2, AxisState state, Rectangle2D dataArea, RectangleEdge edge) {
        List ticks = new ArrayList();
        double y0 = state.getCursor();
        double insideLength = (double) getTickMarkInsideLength();
        double outsideLength = (double) getTickMarkOutsideLength();
        RegularTimePeriod t = createInstance(this.majorTickTimePeriodClass, this.first.getStart(), getTimeZone(), this.locale);
        long t0 = t.getFirstMillisecond();
        long firstOnAxis = getFirst().getFirstMillisecond();
        long lastOnAxis = getLast().getLastMillisecond() + 1;
        Line2D outside = null;
        Line2D inside = null;
        while (t0 <= lastOnAxis) {
            Line2D inside2;
            Line2D outside2;
            ticks.add(new NumberTick(Double.valueOf((double) t0), "", TextAnchor.CENTER, TextAnchor.CENTER, 0.0d));
            double x0 = valueToJava2D((double) t0, dataArea, edge);
            if (edge == RectangleEdge.TOP) {
                inside2 = new Double(x0, y0, x0, y0 + insideLength);
                outside2 = new Double(x0, y0, x0, y0 - outsideLength);
            } else if (edge == RectangleEdge.BOTTOM) {
                Double doubleR = new Double(x0, y0, x0, y0 - insideLength);
                outside2 = new Double(x0, y0, x0, y0 + outsideLength);
            } else {
                outside2 = outside;
                inside2 = inside;
            }
            if (t0 >= firstOnAxis) {
                g2.setPaint(getTickMarkPaint());
                g2.setStroke(getTickMarkStroke());
                g2.draw(inside2);
                g2.draw(outside2);
            }
            if (this.minorTickMarksVisible) {
                RegularTimePeriod tminor = createInstance(this.minorTickTimePeriodClass, new Date(t0), getTimeZone(), this.locale);
                long tt0 = tminor.getFirstMillisecond();
                while (tt0 < t.getLastMillisecond() && tt0 < lastOnAxis) {
                    double xx0 = valueToJava2D((double) tt0, dataArea, edge);
                    if (edge == RectangleEdge.TOP) {
                        doubleR = new Double(xx0, y0, xx0, y0 + ((double) this.minorTickMarkInsideLength));
                        doubleR = new Double(xx0, y0, xx0, y0 - ((double) this.minorTickMarkOutsideLength));
                    } else if (edge == RectangleEdge.BOTTOM) {
                        doubleR = new Double(xx0, y0, xx0, y0 - ((double) this.minorTickMarkInsideLength));
                        doubleR = new Double(xx0, y0, xx0, y0 + ((double) this.minorTickMarkOutsideLength));
                    }
                    if (tt0 >= firstOnAxis) {
                        g2.setPaint(this.minorTickMarkPaint);
                        g2.setStroke(this.minorTickMarkStroke);
                        g2.draw(inside2);
                        g2.draw(outside2);
                    }
                    tminor = tminor.next();
                    tminor.peg(this.calendar);
                    tt0 = tminor.getFirstMillisecond();
                }
            }
            t = t.next();
            t.peg(this.calendar);
            t0 = t.getFirstMillisecond();
            outside = outside2;
            inside = inside2;
        }
        if (edge == RectangleEdge.TOP) {
            state.cursorUp(Math.max(outsideLength, (double) this.minorTickMarkOutsideLength));
        } else if (edge == RectangleEdge.BOTTOM) {
            state.cursorDown(Math.max(outsideLength, (double) this.minorTickMarkOutsideLength));
        }
        state.setTicks(ticks);
    }

    protected void drawTickMarksVertical(Graphics2D g2, AxisState state, Rectangle2D dataArea, RectangleEdge edge) {
    }

    protected AxisState drawTickLabels(int band, Graphics2D g2, AxisState state, Rectangle2D dataArea, RectangleEdge edge) {
        PeriodAxisLabelInfo[] periodAxisLabelInfoArr;
        double delta1 = 0.0d;
        FontMetrics fm = g2.getFontMetrics(this.labelInfo[band].getLabelFont());
        if (edge == RectangleEdge.BOTTOM) {
            periodAxisLabelInfoArr = this.labelInfo;
            delta1 = r0[band].getPadding().calculateTopOutset((double) fm.getHeight());
        } else if (edge == RectangleEdge.TOP) {
            periodAxisLabelInfoArr = this.labelInfo;
            delta1 = r0[band].getPadding().calculateBottomOutset((double) fm.getHeight());
        }
        state.moveCursor(delta1, edge);
        long axisMin = this.first.getFirstMillisecond();
        long axisMax = this.last.getLastMillisecond();
        g2.setFont(this.labelInfo[band].getLabelFont());
        g2.setPaint(this.labelInfo[band].getLabelPaint());
        periodAxisLabelInfoArr = this.labelInfo;
        RegularTimePeriod p1 = r0[band].createInstance(new Date(axisMin), this.timeZone, this.locale);
        periodAxisLabelInfoArr = this.labelInfo;
        RegularTimePeriod p2 = r0[band].createInstance(new Date(axisMax), this.timeZone, this.locale);
        DateFormat df = this.labelInfo[band].getDateFormat();
        df.setTimeZone(this.timeZone);
        String label1 = df.format(new Date(p1.getMiddleMillisecond()));
        String label2 = df.format(new Date(p2.getMiddleMillisecond()));
        Rectangle2D b1 = TextUtilities.getTextBounds(label1, g2, g2.getFontMetrics());
        long ww = Math.round(java2DToValue((dataArea.getX() + Math.max(b1.getWidth(), TextUtilities.getTextBounds(label2, g2, g2.getFontMetrics()).getWidth())) + XYPointerAnnotation.DEFAULT_ARROW_LENGTH, dataArea, edge));
        if (isInverted()) {
            ww = axisMax - ww;
        } else {
            ww -= axisMin;
        }
        int periods = ((int) (ww / (p1.getLastMillisecond() - p1.getFirstMillisecond()))) + 1;
        periodAxisLabelInfoArr = this.labelInfo;
        RegularTimePeriod p = r0[band].createInstance(new Date(axisMin), this.timeZone, this.locale);
        Rectangle2D b = null;
        long lastXX = 0;
        float y = (float) state.getCursor();
        TextAnchor anchor = TextAnchor.TOP_CENTER;
        float yDelta = (float) b1.getHeight();
        if (edge == RectangleEdge.TOP) {
            anchor = TextAnchor.BOTTOM_CENTER;
            yDelta = -yDelta;
        }
        while (p.getFirstMillisecond() <= axisMax) {
            Rectangle2D bb;
            float x = (float) valueToJava2D((double) p.getMiddleMillisecond(), dataArea, edge);
            String label = df.format(new Date(p.getMiddleMillisecond()));
            long first = p.getFirstMillisecond();
            long last = p.getLastMillisecond();
            if (last > axisMax) {
                bb = TextUtilities.getTextBounds(label, g2, g2.getFontMetrics());
                if (((double) x) + (bb.getWidth() / DateAxis.DEFAULT_AUTO_RANGE_MINIMUM_SIZE_IN_MILLISECONDS) > dataArea.getMaxX()) {
                    float xstart = (float) valueToJava2D((double) Math.max(first, axisMin), dataArea, edge);
                    if (bb.getWidth() < dataArea.getMaxX() - ((double) xstart)) {
                        x = (((float) dataArea.getMaxX()) + xstart) / Axis.DEFAULT_TICK_MARK_OUTSIDE_LENGTH;
                    } else {
                        label = null;
                    }
                }
            }
            if (first < axisMin) {
                bb = TextUtilities.getTextBounds(label, g2, g2.getFontMetrics());
                if (((double) x) - (bb.getWidth() / DateAxis.DEFAULT_AUTO_RANGE_MINIMUM_SIZE_IN_MILLISECONDS) < dataArea.getX()) {
                    float xlast = (float) valueToJava2D((double) Math.min(last, axisMax), dataArea, edge);
                    if (bb.getWidth() < ((double) xlast) - dataArea.getX()) {
                        x = (((float) dataArea.getX()) + xlast) / Axis.DEFAULT_TICK_MARK_OUTSIDE_LENGTH;
                    } else {
                        label = null;
                    }
                }
            }
            if (label != null) {
                g2.setPaint(this.labelInfo[band].getLabelPaint());
                b = TextUtilities.drawAlignedString(label, g2, x, y, anchor);
            }
            if (lastXX > 0) {
                if (this.labelInfo[band].getDrawDividers()) {
                    float mid2d = (float) valueToJava2D((double) ((lastXX + p.getFirstMillisecond()) / 2), dataArea, edge);
                    g2.setStroke(this.labelInfo[band].getDividerStroke());
                    g2.setPaint(this.labelInfo[band].getDividerPaint());
                    g2.draw(new Float(mid2d, y, mid2d, y + yDelta));
                }
            }
            lastXX = last;
            for (int i = 0; i < periods; i++) {
                p = p.next();
            }
            p.peg(this.calendar);
        }
        double used = 0.0d;
        if (b != null) {
            used = b.getHeight();
            if (edge == RectangleEdge.BOTTOM) {
                periodAxisLabelInfoArr = this.labelInfo;
                used += r0[band].getPadding().calculateBottomOutset((double) fm.getHeight());
            } else if (edge == RectangleEdge.TOP) {
                periodAxisLabelInfoArr = this.labelInfo;
                used += r0[band].getPadding().calculateTopOutset((double) fm.getHeight());
            }
        }
        state.moveCursor(used, edge);
        return state;
    }

    public List refreshTicks(Graphics2D g2, AxisState state, Rectangle2D dataArea, RectangleEdge edge) {
        return Collections.EMPTY_LIST;
    }

    public double valueToJava2D(double value, Rectangle2D area, RectangleEdge edge) {
        double axisMin = (double) this.first.getFirstMillisecond();
        double axisMax = (double) this.last.getLastMillisecond();
        if (RectangleEdge.isTopOrBottom(edge)) {
            double minX = area.getX();
            double maxX = area.getMaxX();
            if (isInverted()) {
                return maxX + (((value - axisMin) / (axisMax - axisMin)) * (minX - maxX));
            }
            return minX + (((value - axisMin) / (axisMax - axisMin)) * (maxX - minX));
        } else if (!RectangleEdge.isLeftOrRight(edge)) {
            return Double.NaN;
        } else {
            double minY = area.getMinY();
            double maxY = area.getMaxY();
            if (isInverted()) {
                return minY + (((value - axisMin) / (axisMax - axisMin)) * (maxY - minY));
            }
            return maxY - (((value - axisMin) / (axisMax - axisMin)) * (maxY - minY));
        }
    }

    public double java2DToValue(double java2DValue, Rectangle2D area, RectangleEdge edge) {
        double min = 0.0d;
        double max = 0.0d;
        double axisMin = (double) this.first.getFirstMillisecond();
        double axisMax = (double) this.last.getLastMillisecond();
        if (RectangleEdge.isTopOrBottom(edge)) {
            min = area.getX();
            max = area.getMaxX();
        } else if (RectangleEdge.isLeftOrRight(edge)) {
            min = area.getMaxY();
            max = area.getY();
        }
        if (isInverted()) {
            return axisMax - (((java2DValue - min) / (max - min)) * (axisMax - axisMin));
        }
        return axisMin + (((java2DValue - min) / (max - min)) * (axisMax - axisMin));
    }

    protected void autoAdjustRange() {
        Plot plot = getPlot();
        if (plot != null && (plot instanceof ValueAxisPlot)) {
            Range r = ((ValueAxisPlot) plot).getDataRange(this);
            if (r == null) {
                r = getDefaultAutoRange();
            }
            long upper = Math.round(r.getUpperBound());
            this.first = createInstance(this.autoRangeTimePeriodClass, new Date(Math.round(r.getLowerBound())), this.timeZone, this.locale);
            this.last = createInstance(this.autoRangeTimePeriodClass, new Date(upper), this.timeZone, this.locale);
            setRange(r, false, false);
        }
    }

    public boolean equals(Object obj) {
        if (obj == this) {
            return true;
        }
        if (!(obj instanceof PeriodAxis)) {
            return false;
        }
        PeriodAxis that = (PeriodAxis) obj;
        if (this.first.equals(that.first) && this.last.equals(that.last) && this.timeZone.equals(that.timeZone) && this.locale.equals(that.locale) && this.autoRangeTimePeriodClass.equals(that.autoRangeTimePeriodClass) && isMinorTickMarksVisible() == that.isMinorTickMarksVisible() && this.majorTickTimePeriodClass.equals(that.majorTickTimePeriodClass) && this.minorTickTimePeriodClass.equals(that.minorTickTimePeriodClass) && this.minorTickMarkPaint.equals(that.minorTickMarkPaint) && this.minorTickMarkStroke.equals(that.minorTickMarkStroke) && Arrays.equals(this.labelInfo, that.labelInfo)) {
            return super.equals(obj);
        }
        return false;
    }

    public int hashCode() {
        return super.hashCode();
    }

    public Object clone() throws CloneNotSupportedException {
        PeriodAxis clone = (PeriodAxis) super.clone();
        clone.timeZone = (TimeZone) this.timeZone.clone();
        clone.labelInfo = (PeriodAxisLabelInfo[]) this.labelInfo.clone();
        return clone;
    }

    private RegularTimePeriod createInstance(Class periodClass, Date millisecond, TimeZone zone, Locale locale) {
        RegularTimePeriod result = null;
        try {
            return (RegularTimePeriod) periodClass.getDeclaredConstructor(new Class[]{Date.class, TimeZone.class, Locale.class}).newInstance(new Object[]{millisecond, zone, locale});
        } catch (Exception e) {
            try {
                return (RegularTimePeriod) periodClass.getDeclaredConstructor(new Class[]{Date.class}).newInstance(new Object[]{millisecond});
            } catch (Exception e2) {
                return result;
            }
        }
    }

    private void writeObject(ObjectOutputStream stream) throws IOException {
        stream.defaultWriteObject();
        SerialUtilities.writeStroke(this.minorTickMarkStroke, stream);
        SerialUtilities.writePaint(this.minorTickMarkPaint, stream);
    }

    private void readObject(ObjectInputStream stream) throws IOException, ClassNotFoundException {
        stream.defaultReadObject();
        this.minorTickMarkStroke = SerialUtilities.readStroke(stream);
        this.minorTickMarkPaint = SerialUtilities.readPaint(stream);
    }
}
