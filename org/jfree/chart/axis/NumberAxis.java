package org.jfree.chart.axis;

import java.awt.FontMetrics;
import java.awt.Graphics2D;
import java.awt.font.FontRenderContext;
import java.awt.geom.Rectangle2D;
import java.io.Serializable;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import org.jfree.chart.annotations.XYPointerAnnotation;
import org.jfree.chart.event.AxisChangeEvent;
import org.jfree.chart.plot.Plot;
import org.jfree.chart.plot.PlotRenderingInfo;
import org.jfree.chart.plot.ValueAxisPlot;
import org.jfree.chart.util.ParamChecks;
import org.jfree.data.Range;
import org.jfree.data.RangeType;
import org.jfree.data.xy.NormalizedMatrixSeries;
import org.jfree.ui.RectangleEdge;
import org.jfree.ui.RectangleInsets;
import org.jfree.ui.TextAnchor;
import org.jfree.util.ObjectUtilities;

public class NumberAxis extends ValueAxis implements Cloneable, Serializable {
    public static final boolean DEFAULT_AUTO_RANGE_INCLUDES_ZERO = true;
    public static final boolean DEFAULT_AUTO_RANGE_STICKY_ZERO = true;
    public static final NumberTickUnit DEFAULT_TICK_UNIT;
    public static final boolean DEFAULT_VERTICAL_TICK_LABELS = false;
    private static final long serialVersionUID = 2805933088476185789L;
    private boolean autoRangeIncludesZero;
    private boolean autoRangeStickyZero;
    private MarkerAxisBand markerBand;
    private NumberFormat numberFormatOverride;
    private RangeType rangeType;
    private NumberTickUnit tickUnit;

    static {
        DEFAULT_TICK_UNIT = new NumberTickUnit(NormalizedMatrixSeries.DEFAULT_SCALE_FACTOR, new DecimalFormat("0"));
    }

    public NumberAxis() {
        this(null);
    }

    public NumberAxis(String label) {
        super(label, createStandardTickUnits());
        this.rangeType = RangeType.FULL;
        this.autoRangeIncludesZero = DEFAULT_AUTO_RANGE_STICKY_ZERO;
        this.autoRangeStickyZero = DEFAULT_AUTO_RANGE_STICKY_ZERO;
        this.tickUnit = DEFAULT_TICK_UNIT;
        this.numberFormatOverride = null;
        this.markerBand = null;
    }

    public RangeType getRangeType() {
        return this.rangeType;
    }

    public void setRangeType(RangeType rangeType) {
        ParamChecks.nullNotPermitted(rangeType, "rangeType");
        this.rangeType = rangeType;
        notifyListeners(new AxisChangeEvent(this));
    }

    public boolean getAutoRangeIncludesZero() {
        return this.autoRangeIncludesZero;
    }

    public void setAutoRangeIncludesZero(boolean flag) {
        if (this.autoRangeIncludesZero != flag) {
            this.autoRangeIncludesZero = flag;
            if (isAutoRange()) {
                autoAdjustRange();
            }
            notifyListeners(new AxisChangeEvent(this));
        }
    }

    public boolean getAutoRangeStickyZero() {
        return this.autoRangeStickyZero;
    }

    public void setAutoRangeStickyZero(boolean flag) {
        if (this.autoRangeStickyZero != flag) {
            this.autoRangeStickyZero = flag;
            if (isAutoRange()) {
                autoAdjustRange();
            }
            notifyListeners(new AxisChangeEvent(this));
        }
    }

    public NumberTickUnit getTickUnit() {
        return this.tickUnit;
    }

    public void setTickUnit(NumberTickUnit unit) {
        setTickUnit(unit, DEFAULT_AUTO_RANGE_STICKY_ZERO, DEFAULT_AUTO_RANGE_STICKY_ZERO);
    }

    public void setTickUnit(NumberTickUnit unit, boolean notify, boolean turnOffAutoSelect) {
        ParamChecks.nullNotPermitted(unit, "unit");
        this.tickUnit = unit;
        if (turnOffAutoSelect) {
            setAutoTickUnitSelection(DEFAULT_VERTICAL_TICK_LABELS, DEFAULT_VERTICAL_TICK_LABELS);
        }
        if (notify) {
            notifyListeners(new AxisChangeEvent(this));
        }
    }

    public NumberFormat getNumberFormatOverride() {
        return this.numberFormatOverride;
    }

    public void setNumberFormatOverride(NumberFormat formatter) {
        this.numberFormatOverride = formatter;
        notifyListeners(new AxisChangeEvent(this));
    }

    public MarkerAxisBand getMarkerBand() {
        return this.markerBand;
    }

    public void setMarkerBand(MarkerAxisBand band) {
        this.markerBand = band;
        notifyListeners(new AxisChangeEvent(this));
    }

    public void configure() {
        if (isAutoRange()) {
            autoAdjustRange();
        }
    }

    protected void autoAdjustRange() {
        Plot plot = getPlot();
        if (plot != null && (plot instanceof ValueAxisPlot)) {
            Range r = ((ValueAxisPlot) plot).getDataRange(this);
            if (r == null) {
                r = getDefaultAutoRange();
            }
            double upper = r.getUpperBound();
            double lower = r.getLowerBound();
            if (this.rangeType == RangeType.POSITIVE) {
                lower = Math.max(0.0d, lower);
                upper = Math.max(0.0d, upper);
            } else {
                if (this.rangeType == RangeType.NEGATIVE) {
                    lower = Math.min(0.0d, lower);
                    upper = Math.min(0.0d, upper);
                }
            }
            if (getAutoRangeIncludesZero()) {
                lower = Math.min(lower, 0.0d);
                upper = Math.max(upper, 0.0d);
            }
            double range = upper - lower;
            double fixedAutoRange = getFixedAutoRange();
            if (fixedAutoRange > 0.0d) {
                lower = upper - fixedAutoRange;
            } else {
                double minRange = getAutoRangeMinimumSize();
                if (range < minRange) {
                    double expand = (minRange - range) / DateAxis.DEFAULT_AUTO_RANGE_MINIMUM_SIZE_IN_MILLISECONDS;
                    upper += expand;
                    lower -= expand;
                    if (lower == upper) {
                        double adjust = Math.abs(lower) / XYPointerAnnotation.DEFAULT_TIP_RADIUS;
                        lower -= adjust;
                        upper += adjust;
                    }
                    if (this.rangeType != RangeType.POSITIVE) {
                        if (this.rangeType == RangeType.NEGATIVE && upper > 0.0d) {
                            lower -= upper;
                            upper = 0.0d;
                        }
                    } else if (lower < 0.0d) {
                        upper -= lower;
                        lower = 0.0d;
                    }
                }
                if (getAutoRangeStickyZero()) {
                    if (upper <= 0.0d) {
                        upper = Math.min(0.0d, (getUpperMargin() * range) + upper);
                    } else {
                        upper += getUpperMargin() * range;
                    }
                    if (lower >= 0.0d) {
                        lower = Math.max(0.0d, lower - (getLowerMargin() * range));
                    } else {
                        lower -= getLowerMargin() * range;
                    }
                } else {
                    upper += getUpperMargin() * range;
                    lower -= getLowerMargin() * range;
                }
            }
            setRange(new Range(lower, upper), DEFAULT_VERTICAL_TICK_LABELS, DEFAULT_VERTICAL_TICK_LABELS);
        }
    }

    public double valueToJava2D(double value, Rectangle2D area, RectangleEdge edge) {
        Range range = getRange();
        double axisMin = range.getLowerBound();
        double axisMax = range.getUpperBound();
        double min = 0.0d;
        double max = 0.0d;
        if (RectangleEdge.isTopOrBottom(edge)) {
            min = area.getX();
            max = area.getMaxX();
        } else if (RectangleEdge.isLeftOrRight(edge)) {
            max = area.getMinY();
            min = area.getMaxY();
        }
        if (isInverted()) {
            return max - (((value - axisMin) / (axisMax - axisMin)) * (max - min));
        }
        return (((value - axisMin) / (axisMax - axisMin)) * (max - min)) + min;
    }

    public double java2DToValue(double java2DValue, Rectangle2D area, RectangleEdge edge) {
        Range range = getRange();
        double axisMin = range.getLowerBound();
        double axisMax = range.getUpperBound();
        double min = 0.0d;
        double max = 0.0d;
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
        return (((java2DValue - min) / (max - min)) * (axisMax - axisMin)) + axisMin;
    }

    protected double calculateLowestVisibleTickValue() {
        double unit = getTickUnit().getSize();
        return Math.ceil(getRange().getLowerBound() / unit) * unit;
    }

    protected double calculateHighestVisibleTickValue() {
        double unit = getTickUnit().getSize();
        return Math.floor(getRange().getUpperBound() / unit) * unit;
    }

    protected int calculateVisibleTickCount() {
        double unit = getTickUnit().getSize();
        Range range = getRange();
        return (int) ((Math.floor(range.getUpperBound() / unit) - Math.ceil(range.getLowerBound() / unit)) + NormalizedMatrixSeries.DEFAULT_SCALE_FACTOR);
    }

    public AxisState draw(Graphics2D g2, double cursor, Rectangle2D plotArea, Rectangle2D dataArea, RectangleEdge edge, PlotRenderingInfo plotState) {
        AxisState state;
        if (isVisible()) {
            state = drawTickMarksAndLabels(g2, cursor, plotArea, dataArea, edge);
            if (getAttributedLabel() != null) {
                state = drawAttributedLabel(getAttributedLabel(), g2, plotArea, dataArea, edge, state);
            } else {
                state = drawLabel(getLabel(), g2, plotArea, dataArea, edge, state);
            }
            createAndAddEntity(cursor, state, dataArea, edge, plotState);
            return state;
        }
        state = new AxisState(cursor);
        state.setTicks(refreshTicks(g2, state, dataArea, edge));
        return state;
    }

    public static TickUnitSource createStandardTickUnits() {
        return new NumberTickUnitSource();
    }

    public static TickUnitSource createIntegerTickUnits() {
        return new NumberTickUnitSource(DEFAULT_AUTO_RANGE_STICKY_ZERO);
    }

    public static TickUnitSource createStandardTickUnits(Locale locale) {
        return new NumberTickUnitSource(DEFAULT_VERTICAL_TICK_LABELS, NumberFormat.getNumberInstance(locale));
    }

    public static TickUnitSource createIntegerTickUnits(Locale locale) {
        return new NumberTickUnitSource(DEFAULT_AUTO_RANGE_STICKY_ZERO, NumberFormat.getNumberInstance(locale));
    }

    protected double estimateMaximumTickLabelHeight(Graphics2D g2) {
        RectangleInsets tickLabelInsets = getTickLabelInsets();
        return (tickLabelInsets.getTop() + tickLabelInsets.getBottom()) + ((double) getTickLabelFont().getLineMetrics("123", g2.getFontRenderContext()).getHeight());
    }

    protected double estimateMaximumTickLabelWidth(Graphics2D g2, TickUnit unit) {
        RectangleInsets tickLabelInsets = getTickLabelInsets();
        double result = tickLabelInsets.getLeft() + tickLabelInsets.getRight();
        if (isVerticalTickLabels()) {
            FontRenderContext frc = g2.getFontRenderContext();
            return result + ((double) getTickLabelFont().getLineMetrics("0", frc).getHeight());
        }
        String lowerStr;
        String upperStr;
        FontMetrics fm = g2.getFontMetrics(getTickLabelFont());
        Range range = getRange();
        double lower = range.getLowerBound();
        double upper = range.getUpperBound();
        NumberFormat formatter = getNumberFormatOverride();
        if (formatter != null) {
            lowerStr = formatter.format(lower);
            upperStr = formatter.format(upper);
        } else {
            lowerStr = unit.valueToString(lower);
            upperStr = unit.valueToString(upper);
        }
        return result + Math.max((double) fm.stringWidth(lowerStr), (double) fm.stringWidth(upperStr));
    }

    protected void selectAutoTickUnit(Graphics2D g2, Rectangle2D dataArea, RectangleEdge edge) {
        if (RectangleEdge.isTopOrBottom(edge)) {
            selectHorizontalAutoTickUnit(g2, dataArea, edge);
        } else if (RectangleEdge.isLeftOrRight(edge)) {
            selectVerticalAutoTickUnit(g2, dataArea, edge);
        }
    }

    protected void selectHorizontalAutoTickUnit(Graphics2D g2, Rectangle2D dataArea, RectangleEdge edge) {
        double tickLabelWidth = estimateMaximumTickLabelWidth(g2, getTickUnit());
        TickUnitSource tickUnits = getStandardTickUnits();
        TickUnit unit1 = tickUnits.getCeilingTickUnit(getTickUnit());
        NumberTickUnit unit2 = (NumberTickUnit) tickUnits.getCeilingTickUnit((tickLabelWidth / lengthToJava2D(unit1.getSize(), dataArea, edge)) * unit1.getSize());
        if (estimateMaximumTickLabelWidth(g2, unit2) > lengthToJava2D(unit2.getSize(), dataArea, edge)) {
            unit2 = (NumberTickUnit) tickUnits.getLargerTickUnit(unit2);
        }
        setTickUnit(unit2, DEFAULT_VERTICAL_TICK_LABELS, DEFAULT_VERTICAL_TICK_LABELS);
    }

    protected void selectVerticalAutoTickUnit(Graphics2D g2, Rectangle2D dataArea, RectangleEdge edge) {
        double tickLabelHeight = estimateMaximumTickLabelHeight(g2);
        TickUnitSource tickUnits = getStandardTickUnits();
        TickUnit unit1 = tickUnits.getCeilingTickUnit(getTickUnit());
        double unitHeight = lengthToJava2D(unit1.getSize(), dataArea, edge);
        double guess = unit1.getSize();
        if (unitHeight > 0.0d) {
            guess = (tickLabelHeight / unitHeight) * unit1.getSize();
        }
        NumberTickUnit unit2 = (NumberTickUnit) tickUnits.getCeilingTickUnit(guess);
        if (estimateMaximumTickLabelHeight(g2) > lengthToJava2D(unit2.getSize(), dataArea, edge)) {
            unit2 = (NumberTickUnit) tickUnits.getLargerTickUnit(unit2);
        }
        setTickUnit(unit2, DEFAULT_VERTICAL_TICK_LABELS, DEFAULT_VERTICAL_TICK_LABELS);
    }

    public List refreshTicks(Graphics2D g2, AxisState state, Rectangle2D dataArea, RectangleEdge edge) {
        List result = new ArrayList();
        if (RectangleEdge.isTopOrBottom(edge)) {
            return refreshTicksHorizontal(g2, dataArea, edge);
        }
        if (RectangleEdge.isLeftOrRight(edge)) {
            return refreshTicksVertical(g2, dataArea, edge);
        }
        return result;
    }

    protected List refreshTicksHorizontal(Graphics2D g2, Rectangle2D dataArea, RectangleEdge edge) {
        List result = new ArrayList();
        g2.setFont(getTickLabelFont());
        if (isAutoTickUnitSelection()) {
            selectAutoTickUnit(g2, dataArea, edge);
        }
        TickUnit tu = getTickUnit();
        double size = tu.getSize();
        int count = calculateVisibleTickCount();
        double lowestTickValue = calculateLowestVisibleTickValue();
        if (count <= 500) {
            int minorTick;
            double minorTickValue;
            int minorTickSpaces = getMinorTickCount();
            if (minorTickSpaces <= 0) {
                minorTickSpaces = tu.getMinorTickCount();
            }
            for (minorTick = 1; minorTick < minorTickSpaces; minorTick++) {
                minorTickValue = lowestTickValue - ((((double) minorTick) * size) / ((double) minorTickSpaces));
                if (getRange().contains(minorTickValue)) {
                    result.add(new NumberTick(TickType.MINOR, minorTickValue, "", TextAnchor.TOP_CENTER, TextAnchor.CENTER, 0.0d));
                }
            }
            for (int i = 0; i < count; i++) {
                String tickLabel;
                TextAnchor anchor;
                TextAnchor rotationAnchor;
                double currentTickValue = lowestTickValue + (((double) i) * size);
                NumberFormat formatter = getNumberFormatOverride();
                if (formatter != null) {
                    tickLabel = formatter.format(currentTickValue);
                } else {
                    tickLabel = getTickUnit().valueToString(currentTickValue);
                }
                double angle = 0.0d;
                if (isVerticalTickLabels()) {
                    anchor = TextAnchor.CENTER_RIGHT;
                    rotationAnchor = TextAnchor.CENTER_RIGHT;
                    angle = edge == RectangleEdge.TOP ? 1.5707963267948966d : -1.5707963267948966d;
                } else if (edge == RectangleEdge.TOP) {
                    anchor = TextAnchor.BOTTOM_CENTER;
                    rotationAnchor = TextAnchor.BOTTOM_CENTER;
                } else {
                    anchor = TextAnchor.TOP_CENTER;
                    rotationAnchor = TextAnchor.TOP_CENTER;
                }
                result.add(new NumberTick(new Double(currentTickValue), tickLabel, anchor, rotationAnchor, angle));
                double nextTickValue = lowestTickValue + (((double) (i + 1)) * size);
                for (minorTick = 1; minorTick < minorTickSpaces; minorTick++) {
                    minorTickValue = currentTickValue + (((nextTickValue - currentTickValue) * ((double) minorTick)) / ((double) minorTickSpaces));
                    if (getRange().contains(minorTickValue)) {
                        result.add(new NumberTick(TickType.MINOR, minorTickValue, "", TextAnchor.TOP_CENTER, TextAnchor.CENTER, 0.0d));
                    }
                }
            }
        }
        return result;
    }

    protected List refreshTicksVertical(Graphics2D g2, Rectangle2D dataArea, RectangleEdge edge) {
        List result = new ArrayList();
        result.clear();
        g2.setFont(getTickLabelFont());
        if (isAutoTickUnitSelection()) {
            selectAutoTickUnit(g2, dataArea, edge);
        }
        TickUnit tu = getTickUnit();
        double size = tu.getSize();
        int count = calculateVisibleTickCount();
        double lowestTickValue = calculateLowestVisibleTickValue();
        if (count <= 500) {
            int minorTick;
            double minorTickValue;
            int minorTickSpaces = getMinorTickCount();
            if (minorTickSpaces <= 0) {
                minorTickSpaces = tu.getMinorTickCount();
            }
            for (minorTick = 1; minorTick < minorTickSpaces; minorTick++) {
                minorTickValue = lowestTickValue - ((((double) minorTick) * size) / ((double) minorTickSpaces));
                if (getRange().contains(minorTickValue)) {
                    result.add(new NumberTick(TickType.MINOR, minorTickValue, "", TextAnchor.TOP_CENTER, TextAnchor.CENTER, 0.0d));
                }
            }
            for (int i = 0; i < count; i++) {
                String tickLabel;
                TextAnchor anchor;
                TextAnchor rotationAnchor;
                double currentTickValue = lowestTickValue + (((double) i) * size);
                NumberFormat formatter = getNumberFormatOverride();
                if (formatter != null) {
                    tickLabel = formatter.format(currentTickValue);
                } else {
                    tickLabel = getTickUnit().valueToString(currentTickValue);
                }
                double angle = 0.0d;
                if (isVerticalTickLabels()) {
                    if (edge == RectangleEdge.LEFT) {
                        anchor = TextAnchor.BOTTOM_CENTER;
                        rotationAnchor = TextAnchor.BOTTOM_CENTER;
                        angle = -1.5707963267948966d;
                    } else {
                        anchor = TextAnchor.BOTTOM_CENTER;
                        rotationAnchor = TextAnchor.BOTTOM_CENTER;
                        angle = 1.5707963267948966d;
                    }
                } else if (edge == RectangleEdge.LEFT) {
                    anchor = TextAnchor.CENTER_RIGHT;
                    rotationAnchor = TextAnchor.CENTER_RIGHT;
                } else {
                    anchor = TextAnchor.CENTER_LEFT;
                    rotationAnchor = TextAnchor.CENTER_LEFT;
                }
                result.add(new NumberTick(new Double(currentTickValue), tickLabel, anchor, rotationAnchor, angle));
                double nextTickValue = lowestTickValue + (((double) (i + 1)) * size);
                for (minorTick = 1; minorTick < minorTickSpaces; minorTick++) {
                    double d = (double) minorTickSpaces;
                    minorTickValue = currentTickValue + (((nextTickValue - currentTickValue) * ((double) minorTick)) / r0);
                    if (getRange().contains(minorTickValue)) {
                        result.add(new NumberTick(TickType.MINOR, minorTickValue, "", TextAnchor.TOP_CENTER, TextAnchor.CENTER, 0.0d));
                    }
                }
            }
        }
        return result;
    }

    public Object clone() throws CloneNotSupportedException {
        NumberAxis clone = (NumberAxis) super.clone();
        if (this.numberFormatOverride != null) {
            clone.numberFormatOverride = (NumberFormat) this.numberFormatOverride.clone();
        }
        return clone;
    }

    public boolean equals(Object obj) {
        if (obj == this) {
            return DEFAULT_AUTO_RANGE_STICKY_ZERO;
        }
        if (!(obj instanceof NumberAxis)) {
            return DEFAULT_VERTICAL_TICK_LABELS;
        }
        NumberAxis that = (NumberAxis) obj;
        if (this.autoRangeIncludesZero == that.autoRangeIncludesZero && this.autoRangeStickyZero == that.autoRangeStickyZero && ObjectUtilities.equal(this.tickUnit, that.tickUnit) && ObjectUtilities.equal(this.numberFormatOverride, that.numberFormatOverride) && this.rangeType.equals(that.rangeType)) {
            return super.equals(obj);
        }
        return DEFAULT_VERTICAL_TICK_LABELS;
    }

    public int hashCode() {
        return super.hashCode();
    }
}
