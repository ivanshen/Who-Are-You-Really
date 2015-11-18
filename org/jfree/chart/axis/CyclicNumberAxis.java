package org.jfree.chart.axis;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.FontMetrics;
import java.awt.Graphics2D;
import java.awt.Paint;
import java.awt.Stroke;
import java.awt.geom.Line2D;
import java.awt.geom.Line2D.Double;
import java.awt.geom.Rectangle2D;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;
import org.jfree.chart.plot.Plot;
import org.jfree.chart.plot.PlotRenderingInfo;
import org.jfree.chart.util.ParamChecks;
import org.jfree.data.Range;
import org.jfree.io.SerialUtilities;
import org.jfree.text.TextUtilities;
import org.jfree.ui.RectangleEdge;
import org.jfree.ui.TextAnchor;
import org.jfree.util.ObjectUtilities;
import org.jfree.util.PaintUtilities;

public class CyclicNumberAxis extends NumberAxis {
    public static final Paint DEFAULT_ADVANCE_LINE_PAINT;
    public static Stroke DEFAULT_ADVANCE_LINE_STROKE = null;
    static final long serialVersionUID = -7514160997164582554L;
    protected transient Paint advanceLinePaint;
    protected transient Stroke advanceLineStroke;
    protected boolean advanceLineVisible;
    protected boolean boundMappedToLastCycle;
    private transient Tick internalMarkerCycleBoundTick;
    private transient boolean internalMarkerWhenTicksOverlap;
    protected double offset;
    protected double period;

    protected static class CycleBoundTick extends NumberTick {
        public boolean mapToLastCycle;

        public CycleBoundTick(boolean mapToLastCycle, Number number, String label, TextAnchor textAnchor, TextAnchor rotationAnchor, double angle) {
            super(number, label, textAnchor, rotationAnchor, angle);
            this.mapToLastCycle = mapToLastCycle;
        }
    }

    static {
        DEFAULT_ADVANCE_LINE_STROKE = new BasicStroke(Plot.DEFAULT_FOREGROUND_ALPHA);
        DEFAULT_ADVANCE_LINE_PAINT = Color.gray;
    }

    public CyclicNumberAxis(double period) {
        this(period, 0.0d);
    }

    public CyclicNumberAxis(double period, double offset) {
        this(period, offset, null);
    }

    public CyclicNumberAxis(double period, String label) {
        this(0.0d, period, label);
    }

    public CyclicNumberAxis(double period, double offset, String label) {
        super(label);
        this.advanceLineStroke = DEFAULT_ADVANCE_LINE_STROKE;
        this.period = period;
        this.offset = offset;
        setFixedAutoRange(period);
        this.advanceLineVisible = true;
        this.advanceLinePaint = DEFAULT_ADVANCE_LINE_PAINT;
    }

    public boolean isAdvanceLineVisible() {
        return this.advanceLineVisible;
    }

    public void setAdvanceLineVisible(boolean visible) {
        this.advanceLineVisible = visible;
    }

    public Paint getAdvanceLinePaint() {
        return this.advanceLinePaint;
    }

    public void setAdvanceLinePaint(Paint paint) {
        ParamChecks.nullNotPermitted(paint, "paint");
        this.advanceLinePaint = paint;
    }

    public Stroke getAdvanceLineStroke() {
        return this.advanceLineStroke;
    }

    public void setAdvanceLineStroke(Stroke stroke) {
        ParamChecks.nullNotPermitted(stroke, "stroke");
        this.advanceLineStroke = stroke;
    }

    public boolean isBoundMappedToLastCycle() {
        return this.boundMappedToLastCycle;
    }

    public void setBoundMappedToLastCycle(boolean boundMappedToLastCycle) {
        this.boundMappedToLastCycle = boundMappedToLastCycle;
    }

    protected void selectHorizontalAutoTickUnit(Graphics2D g2, Rectangle2D drawArea, Rectangle2D dataArea, RectangleEdge edge) {
        setTickUnit((NumberTickUnit) getStandardTickUnits().getCeilingTickUnit((getRange().getLength() * estimateMaximumTickLabelWidth(g2, getTickUnit())) / dataArea.getWidth()), false, false);
    }

    protected void selectVerticalAutoTickUnit(Graphics2D g2, Rectangle2D drawArea, Rectangle2D dataArea, RectangleEdge edge) {
        setTickUnit((NumberTickUnit) getStandardTickUnits().getCeilingTickUnit((getRange().getLength() * estimateMaximumTickLabelWidth(g2, getTickUnit())) / dataArea.getHeight()), false, false);
    }

    protected float[] calculateAnchorPoint(ValueTick tick, double cursor, Rectangle2D dataArea, RectangleEdge edge) {
        if (!(tick instanceof CycleBoundTick)) {
            return super.calculateAnchorPoint(tick, cursor, dataArea, edge);
        }
        boolean mapsav = this.boundMappedToLastCycle;
        this.boundMappedToLastCycle = ((CycleBoundTick) tick).mapToLastCycle;
        float[] ret = super.calculateAnchorPoint(tick, cursor, dataArea, edge);
        this.boundMappedToLastCycle = mapsav;
        return ret;
    }

    protected List refreshTicksHorizontal(Graphics2D g2, Rectangle2D dataArea, RectangleEdge edge) {
        List result = new ArrayList();
        g2.setFont(getTickLabelFont());
        if (isAutoTickUnitSelection()) {
            selectAutoTickUnit(g2, dataArea, edge);
        }
        double unit = getTickUnit().getSize();
        double cycleBound = getCycleBound();
        double currentTickValue = Math.ceil(cycleBound / unit) * unit;
        double upperValue = getRange().getUpperBound();
        boolean cycled = false;
        boolean boundMapping = this.boundMappedToLastCycle;
        this.boundMappedToLastCycle = false;
        CycleBoundTick lastTick = null;
        float lastX = 0.0f;
        if (upperValue == cycleBound) {
            currentTickValue = calculateLowestVisibleTickValue();
            cycled = true;
            this.boundMappedToLastCycle = true;
        }
        while (currentTickValue <= upperValue) {
            String tickLabel;
            TextAnchor anchor;
            TextAnchor rotationAnchor;
            double angle;
            boolean cyclenow = false;
            if (currentTickValue + unit > upperValue && !cycled) {
                cyclenow = true;
            }
            double xx = valueToJava2D(currentTickValue, dataArea, edge);
            NumberFormat formatter = getNumberFormatOverride();
            if (formatter != null) {
                tickLabel = formatter.format(currentTickValue);
            } else {
                tickLabel = getTickUnit().valueToString(currentTickValue);
            }
            float x = (float) xx;
            if (isVerticalTickLabels()) {
                double angle2;
                if (edge == RectangleEdge.TOP) {
                    angle2 = 1.5707963267948966d;
                } else {
                    angle2 = -1.5707963267948966d;
                }
                anchor = TextAnchor.CENTER_RIGHT;
                if (!(lastTick == null || lastX != x || currentTickValue == cycleBound)) {
                    anchor = isInverted() ? TextAnchor.TOP_RIGHT : TextAnchor.BOTTOM_RIGHT;
                    result.remove(result.size() - 1);
                    result.add(new CycleBoundTick(this.boundMappedToLastCycle, lastTick.getNumber(), lastTick.getText(), anchor, anchor, lastTick.getAngle()));
                    this.internalMarkerWhenTicksOverlap = true;
                    anchor = isInverted() ? TextAnchor.BOTTOM_RIGHT : TextAnchor.TOP_RIGHT;
                }
                rotationAnchor = anchor;
                angle = angle2;
            } else if (edge == RectangleEdge.TOP) {
                anchor = TextAnchor.BOTTOM_CENTER;
                if (!(lastTick == null || lastX != x || currentTickValue == cycleBound)) {
                    anchor = isInverted() ? TextAnchor.BOTTOM_LEFT : TextAnchor.BOTTOM_RIGHT;
                    result.remove(result.size() - 1);
                    result.add(new CycleBoundTick(this.boundMappedToLastCycle, lastTick.getNumber(), lastTick.getText(), anchor, anchor, lastTick.getAngle()));
                    this.internalMarkerWhenTicksOverlap = true;
                    anchor = isInverted() ? TextAnchor.BOTTOM_RIGHT : TextAnchor.BOTTOM_LEFT;
                }
                rotationAnchor = anchor;
                angle = 0.0d;
            } else {
                anchor = TextAnchor.TOP_CENTER;
                if (!(lastTick == null || lastX != x || currentTickValue == cycleBound)) {
                    anchor = isInverted() ? TextAnchor.TOP_LEFT : TextAnchor.TOP_RIGHT;
                    result.remove(result.size() - 1);
                    result.add(new CycleBoundTick(this.boundMappedToLastCycle, lastTick.getNumber(), lastTick.getText(), anchor, anchor, lastTick.getAngle()));
                    this.internalMarkerWhenTicksOverlap = true;
                    anchor = isInverted() ? TextAnchor.TOP_RIGHT : TextAnchor.TOP_LEFT;
                }
                rotationAnchor = anchor;
                angle = 0.0d;
            }
            CycleBoundTick tick = new CycleBoundTick(this.boundMappedToLastCycle, new Double(currentTickValue), tickLabel, anchor, rotationAnchor, angle);
            if (currentTickValue == cycleBound) {
                this.internalMarkerCycleBoundTick = tick;
            }
            result.add(tick);
            lastTick = tick;
            lastX = x;
            currentTickValue += unit;
            if (cyclenow) {
                currentTickValue = calculateLowestVisibleTickValue();
                upperValue = cycleBound;
                cycled = true;
                this.boundMappedToLastCycle = true;
            }
        }
        this.boundMappedToLastCycle = boundMapping;
        return result;
    }

    protected List refreshVerticalTicks(Graphics2D g2, Rectangle2D dataArea, RectangleEdge edge) {
        List result = new ArrayList();
        result.clear();
        g2.setFont(getTickLabelFont());
        if (isAutoTickUnitSelection()) {
            selectAutoTickUnit(g2, dataArea, edge);
        }
        double unit = getTickUnit().getSize();
        double cycleBound = getCycleBound();
        double currentTickValue = Math.ceil(cycleBound / unit) * unit;
        double upperValue = getRange().getUpperBound();
        boolean cycled = false;
        boolean boundMapping = this.boundMappedToLastCycle;
        this.boundMappedToLastCycle = true;
        NumberTick lastTick = null;
        float lastY = 0.0f;
        if (upperValue == cycleBound) {
            currentTickValue = calculateLowestVisibleTickValue();
            cycled = true;
            this.boundMappedToLastCycle = true;
        }
        while (currentTickValue <= upperValue) {
            String tickLabel;
            TextAnchor anchor;
            TextAnchor rotationAnchor;
            double angle;
            boolean cyclenow = false;
            if (currentTickValue + unit > upperValue && !cycled) {
                cyclenow = true;
            }
            double yy = valueToJava2D(currentTickValue, dataArea, edge);
            NumberFormat formatter = getNumberFormatOverride();
            if (formatter != null) {
                tickLabel = formatter.format(currentTickValue);
            } else {
                tickLabel = getTickUnit().valueToString(currentTickValue);
            }
            float y = (float) yy;
            if (isVerticalTickLabels()) {
                if (edge == RectangleEdge.LEFT) {
                    anchor = TextAnchor.BOTTOM_CENTER;
                    if (!(lastTick == null || lastY != y || currentTickValue == cycleBound)) {
                        anchor = isInverted() ? TextAnchor.BOTTOM_LEFT : TextAnchor.BOTTOM_RIGHT;
                        result.remove(result.size() - 1);
                        result.add(new CycleBoundTick(this.boundMappedToLastCycle, lastTick.getNumber(), lastTick.getText(), anchor, anchor, lastTick.getAngle()));
                        this.internalMarkerWhenTicksOverlap = true;
                        anchor = isInverted() ? TextAnchor.BOTTOM_RIGHT : TextAnchor.BOTTOM_LEFT;
                    }
                    rotationAnchor = anchor;
                    angle = -1.5707963267948966d;
                } else {
                    anchor = TextAnchor.BOTTOM_CENTER;
                    if (!(lastTick == null || lastY != y || currentTickValue == cycleBound)) {
                        anchor = isInverted() ? TextAnchor.BOTTOM_RIGHT : TextAnchor.BOTTOM_LEFT;
                        result.remove(result.size() - 1);
                        result.add(new CycleBoundTick(this.boundMappedToLastCycle, lastTick.getNumber(), lastTick.getText(), anchor, anchor, lastTick.getAngle()));
                        this.internalMarkerWhenTicksOverlap = true;
                        anchor = isInverted() ? TextAnchor.BOTTOM_LEFT : TextAnchor.BOTTOM_RIGHT;
                    }
                    rotationAnchor = anchor;
                    angle = 1.5707963267948966d;
                }
            } else if (edge == RectangleEdge.LEFT) {
                anchor = TextAnchor.CENTER_RIGHT;
                if (!(lastTick == null || lastY != y || currentTickValue == cycleBound)) {
                    anchor = isInverted() ? TextAnchor.BOTTOM_RIGHT : TextAnchor.TOP_RIGHT;
                    result.remove(result.size() - 1);
                    result.add(new CycleBoundTick(this.boundMappedToLastCycle, lastTick.getNumber(), lastTick.getText(), anchor, anchor, lastTick.getAngle()));
                    this.internalMarkerWhenTicksOverlap = true;
                    anchor = isInverted() ? TextAnchor.TOP_RIGHT : TextAnchor.BOTTOM_RIGHT;
                }
                rotationAnchor = anchor;
                angle = 0.0d;
            } else {
                anchor = TextAnchor.CENTER_LEFT;
                if (!(lastTick == null || lastY != y || currentTickValue == cycleBound)) {
                    anchor = isInverted() ? TextAnchor.BOTTOM_LEFT : TextAnchor.TOP_LEFT;
                    result.remove(result.size() - 1);
                    result.add(new CycleBoundTick(this.boundMappedToLastCycle, lastTick.getNumber(), lastTick.getText(), anchor, anchor, lastTick.getAngle()));
                    this.internalMarkerWhenTicksOverlap = true;
                    anchor = isInverted() ? TextAnchor.TOP_LEFT : TextAnchor.BOTTOM_LEFT;
                }
                rotationAnchor = anchor;
                angle = 0.0d;
            }
            NumberTick tick = new CycleBoundTick(this.boundMappedToLastCycle, new Double(currentTickValue), tickLabel, anchor, rotationAnchor, angle);
            if (currentTickValue == cycleBound) {
                this.internalMarkerCycleBoundTick = tick;
            }
            result.add(tick);
            lastTick = tick;
            lastY = y;
            if (currentTickValue == cycleBound) {
                this.internalMarkerCycleBoundTick = tick;
            }
            currentTickValue += unit;
            if (cyclenow) {
                currentTickValue = calculateLowestVisibleTickValue();
                upperValue = cycleBound;
                cycled = true;
                this.boundMappedToLastCycle = false;
            }
        }
        this.boundMappedToLastCycle = boundMapping;
        return result;
    }

    public double java2DToValue(double java2DValue, Rectangle2D dataArea, RectangleEdge edge) {
        double vmax = getRange().getUpperBound();
        double vp = getCycleBound();
        double jmin = 0.0d;
        double jmax = 0.0d;
        if (RectangleEdge.isTopOrBottom(edge)) {
            jmin = dataArea.getMinX();
            jmax = dataArea.getMaxX();
        } else if (RectangleEdge.isLeftOrRight(edge)) {
            jmin = dataArea.getMaxY();
            jmax = dataArea.getMinY();
        }
        if (isInverted()) {
            if (java2DValue >= jmax - (((vmax - vp) * (jmax - jmin)) / this.period)) {
                return (((jmax - java2DValue) * this.period) / (jmax - jmin)) + vp;
            }
            return vp - (((java2DValue - jmin) * this.period) / (jmax - jmin));
        }
        if (java2DValue <= (((vmax - vp) * (jmax - jmin)) / this.period) + jmin) {
            return (((java2DValue - jmin) * this.period) / (jmax - jmin)) + vp;
        }
        return vp - (((jmax - java2DValue) * this.period) / (jmax - jmin));
    }

    public double valueToJava2D(double value, Rectangle2D dataArea, RectangleEdge edge) {
        Range range = getRange();
        double vmin = range.getLowerBound();
        double vmax = range.getUpperBound();
        double vp = getCycleBound();
        if (value < vmin || value > vmax) {
            return Double.NaN;
        }
        double jmin = 0.0d;
        double jmax = 0.0d;
        if (RectangleEdge.isTopOrBottom(edge)) {
            jmin = dataArea.getMinX();
            jmax = dataArea.getMaxX();
        } else if (RectangleEdge.isLeftOrRight(edge)) {
            jmax = dataArea.getMinY();
            jmin = dataArea.getMaxY();
        }
        if (isInverted()) {
            if (value == vp) {
                if (this.boundMappedToLastCycle) {
                    return jmin;
                }
                return jmax;
            } else if (value > vp) {
                return jmax - (((value - vp) * (jmax - jmin)) / this.period);
            } else {
                return jmin + (((vp - value) * (jmax - jmin)) / this.period);
            }
        } else if (value == vp) {
            if (!this.boundMappedToLastCycle) {
                jmax = jmin;
            }
            return jmax;
        } else if (value >= vp) {
            return jmin + (((value - vp) * (jmax - jmin)) / this.period);
        } else {
            return jmax - (((vp - value) * (jmax - jmin)) / this.period);
        }
    }

    public void centerRange(double value) {
        setRange(value - (this.period / DateAxis.DEFAULT_AUTO_RANGE_MINIMUM_SIZE_IN_MILLISECONDS), (this.period / DateAxis.DEFAULT_AUTO_RANGE_MINIMUM_SIZE_IN_MILLISECONDS) + value);
    }

    public void setAutoRangeMinimumSize(double size, boolean notify) {
        if (size > this.period) {
            this.period = size;
        }
        super.setAutoRangeMinimumSize(size, notify);
    }

    public void setFixedAutoRange(double length) {
        this.period = length;
        super.setFixedAutoRange(length);
    }

    public void setRange(Range range, boolean turnOffAutoRange, boolean notify) {
        double size = range.getUpperBound() - range.getLowerBound();
        if (size > this.period) {
            this.period = size;
        }
        super.setRange(range, turnOffAutoRange, notify);
    }

    public double getCycleBound() {
        return (Math.floor((getRange().getUpperBound() - this.offset) / this.period) * this.period) + this.offset;
    }

    public double getOffset() {
        return this.offset;
    }

    public void setOffset(double offset) {
        this.offset = offset;
    }

    public double getPeriod() {
        return this.period;
    }

    public void setPeriod(double period) {
        this.period = period;
    }

    protected AxisState drawTickMarksAndLabels(Graphics2D g2, double cursor, Rectangle2D plotArea, Rectangle2D dataArea, RectangleEdge edge) {
        this.internalMarkerWhenTicksOverlap = false;
        AxisState ret = super.drawTickMarksAndLabels(g2, cursor, plotArea, dataArea, edge);
        if (this.internalMarkerWhenTicksOverlap) {
            double ol;
            FontMetrics fm = g2.getFontMetrics(getTickLabelFont());
            if (isVerticalTickLabels()) {
                ol = (double) fm.getMaxAdvance();
            } else {
                ol = (double) fm.getHeight();
            }
            if (isTickMarksVisible()) {
                float xx = (float) valueToJava2D(getRange().getUpperBound(), dataArea, edge);
                Line2D mark = null;
                g2.setStroke(getTickMarkStroke());
                g2.setPaint(getTickMarkPaint());
                if (edge == RectangleEdge.LEFT) {
                    mark = new Double(cursor - ol, (double) xx, cursor + 0.0d, (double) xx);
                } else if (edge == RectangleEdge.RIGHT) {
                    mark = new Double(cursor + ol, (double) xx, cursor - 0.0d, (double) xx);
                } else if (edge == RectangleEdge.TOP) {
                    mark = new Double((double) xx, cursor - ol, (double) xx, cursor + 0.0d);
                } else if (edge == RectangleEdge.BOTTOM) {
                    mark = new Double((double) xx, cursor + ol, (double) xx, cursor - 0.0d);
                }
                g2.draw(mark);
            }
        }
        return ret;
    }

    public AxisState draw(Graphics2D g2, double cursor, Rectangle2D plotArea, Rectangle2D dataArea, RectangleEdge edge, PlotRenderingInfo plotState) {
        AxisState ret = super.draw(g2, cursor, plotArea, dataArea, edge, plotState);
        if (isAdvanceLineVisible()) {
            double xx = valueToJava2D(getRange().getUpperBound(), dataArea, edge);
            Line2D mark = null;
            g2.setStroke(getAdvanceLineStroke());
            g2.setPaint(getAdvanceLinePaint());
            if (edge == RectangleEdge.LEFT) {
                mark = new Double(cursor, xx, cursor + dataArea.getWidth(), xx);
            } else if (edge == RectangleEdge.RIGHT) {
                mark = new Double(cursor - dataArea.getWidth(), xx, cursor, xx);
            } else if (edge == RectangleEdge.TOP) {
                r5 = new Double(xx, cursor + dataArea.getHeight(), xx, cursor);
            } else if (edge == RectangleEdge.BOTTOM) {
                r5 = new Double(xx, cursor, xx, cursor - dataArea.getHeight());
            }
            g2.draw(mark);
        }
        return ret;
    }

    public AxisSpace reserveSpace(Graphics2D g2, Plot plot, Rectangle2D plotArea, RectangleEdge edge, AxisSpace space) {
        this.internalMarkerCycleBoundTick = null;
        AxisSpace ret = super.reserveSpace(g2, plot, plotArea, edge, space);
        if (this.internalMarkerCycleBoundTick != null) {
            Rectangle2D r = TextUtilities.getTextBounds(this.internalMarkerCycleBoundTick.getText(), g2, g2.getFontMetrics(getTickLabelFont()));
            if (RectangleEdge.isTopOrBottom(edge)) {
                if (isVerticalTickLabels()) {
                    space.add(r.getHeight() / DateAxis.DEFAULT_AUTO_RANGE_MINIMUM_SIZE_IN_MILLISECONDS, RectangleEdge.RIGHT);
                } else {
                    space.add(r.getWidth() / DateAxis.DEFAULT_AUTO_RANGE_MINIMUM_SIZE_IN_MILLISECONDS, RectangleEdge.RIGHT);
                }
            } else if (RectangleEdge.isLeftOrRight(edge)) {
                if (isVerticalTickLabels()) {
                    space.add(r.getWidth() / DateAxis.DEFAULT_AUTO_RANGE_MINIMUM_SIZE_IN_MILLISECONDS, RectangleEdge.TOP);
                } else {
                    space.add(r.getHeight() / DateAxis.DEFAULT_AUTO_RANGE_MINIMUM_SIZE_IN_MILLISECONDS, RectangleEdge.TOP);
                }
            }
        }
        return ret;
    }

    private void writeObject(ObjectOutputStream stream) throws IOException {
        stream.defaultWriteObject();
        SerialUtilities.writePaint(this.advanceLinePaint, stream);
        SerialUtilities.writeStroke(this.advanceLineStroke, stream);
    }

    private void readObject(ObjectInputStream stream) throws IOException, ClassNotFoundException {
        stream.defaultReadObject();
        this.advanceLinePaint = SerialUtilities.readPaint(stream);
        this.advanceLineStroke = SerialUtilities.readStroke(stream);
    }

    public boolean equals(Object obj) {
        if (obj == this) {
            return true;
        }
        if (!(obj instanceof CyclicNumberAxis)) {
            return false;
        }
        if (!super.equals(obj)) {
            return false;
        }
        CyclicNumberAxis that = (CyclicNumberAxis) obj;
        if (this.period != that.period) {
            return false;
        }
        if (this.offset != that.offset) {
            return false;
        }
        if (!PaintUtilities.equal(this.advanceLinePaint, that.advanceLinePaint)) {
            return false;
        }
        if (!ObjectUtilities.equal(this.advanceLineStroke, that.advanceLineStroke)) {
            return false;
        }
        if (this.advanceLineVisible != that.advanceLineVisible) {
            return false;
        }
        if (this.boundMappedToLastCycle != that.boundMappedToLastCycle) {
            return false;
        }
        return true;
    }
}
