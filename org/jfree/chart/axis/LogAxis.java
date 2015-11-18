package org.jfree.chart.axis;

import java.awt.Graphics2D;
import java.awt.font.FontRenderContext;
import java.awt.font.TextAttribute;
import java.awt.geom.Rectangle2D;
import java.text.AttributedString;
import java.text.DecimalFormat;
import java.text.Format;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import org.jfree.chart.annotations.XYPointerAnnotation;
import org.jfree.chart.plot.Plot;
import org.jfree.chart.plot.PlotRenderingInfo;
import org.jfree.chart.plot.SpiderWebPlot;
import org.jfree.chart.plot.ValueAxisPlot;
import org.jfree.chart.renderer.category.BarRenderer;
import org.jfree.chart.renderer.category.LevelRenderer;
import org.jfree.chart.renderer.xy.XYLine3DRenderer;
import org.jfree.chart.util.AttrStringUtils;
import org.jfree.chart.util.LogFormat;
import org.jfree.chart.util.ParamChecks;
import org.jfree.data.Range;
import org.jfree.data.xy.NormalizedMatrixSeries;
import org.jfree.ui.RectangleEdge;
import org.jfree.ui.RectangleInsets;
import org.jfree.ui.TextAnchor;
import org.jfree.util.ObjectUtilities;

public class LogAxis extends ValueAxis {
    private double base;
    private Format baseFormatter;
    private double baseLog;
    private String baseSymbol;
    private NumberFormat numberFormatOverride;
    private double smallestValue;
    private NumberTickUnit tickUnit;

    public LogAxis() {
        this(null);
    }

    public LogAxis(String label) {
        super(label, new NumberTickUnitSource());
        this.base = XYPointerAnnotation.DEFAULT_TIP_RADIUS;
        this.baseLog = Math.log(XYPointerAnnotation.DEFAULT_TIP_RADIUS);
        this.baseSymbol = null;
        this.baseFormatter = new DecimalFormat("0");
        this.smallestValue = LogarithmicAxis.SMALL_LOG_VALUE;
        setDefaultAutoRange(new Range(SpiderWebPlot.DEFAULT_HEAD, NormalizedMatrixSeries.DEFAULT_SCALE_FACTOR));
        this.tickUnit = new NumberTickUnit(NormalizedMatrixSeries.DEFAULT_SCALE_FACTOR, new DecimalFormat("0.#"), 10);
    }

    public double getBase() {
        return this.base;
    }

    public void setBase(double base) {
        if (base <= NormalizedMatrixSeries.DEFAULT_SCALE_FACTOR) {
            throw new IllegalArgumentException("Requires 'base' > 1.0.");
        }
        this.base = base;
        this.baseLog = Math.log(base);
        fireChangeEvent();
    }

    public String getBaseSymbol() {
        return this.baseSymbol;
    }

    public void setBaseSymbol(String symbol) {
        this.baseSymbol = symbol;
        fireChangeEvent();
    }

    public Format getBaseFormatter() {
        return this.baseFormatter;
    }

    public void setBaseFormatter(Format formatter) {
        ParamChecks.nullNotPermitted(formatter, "formatter");
        this.baseFormatter = formatter;
        fireChangeEvent();
    }

    public double getSmallestValue() {
        return this.smallestValue;
    }

    public void setSmallestValue(double value) {
        if (value <= 0.0d) {
            throw new IllegalArgumentException("Requires 'value' > 0.0.");
        }
        this.smallestValue = value;
        fireChangeEvent();
    }

    public NumberTickUnit getTickUnit() {
        return this.tickUnit;
    }

    public void setTickUnit(NumberTickUnit unit) {
        setTickUnit(unit, true, true);
    }

    public void setTickUnit(NumberTickUnit unit, boolean notify, boolean turnOffAutoSelect) {
        ParamChecks.nullNotPermitted(unit, "unit");
        this.tickUnit = unit;
        if (turnOffAutoSelect) {
            setAutoTickUnitSelection(false, false);
        }
        if (notify) {
            fireChangeEvent();
        }
    }

    public NumberFormat getNumberFormatOverride() {
        return this.numberFormatOverride;
    }

    public void setNumberFormatOverride(NumberFormat formatter) {
        this.numberFormatOverride = formatter;
        fireChangeEvent();
    }

    public double calculateLog(double value) {
        return Math.log(value) / this.baseLog;
    }

    public double calculateValue(double log) {
        return Math.pow(this.base, log);
    }

    private double calculateValueNoINF(double log) {
        double result = calculateValue(log);
        if (Double.isInfinite(result)) {
            result = Double.MAX_VALUE;
        }
        if (result <= 0.0d) {
            return Double.MIN_VALUE;
        }
        return result;
    }

    public double java2DToValue(double java2DValue, Rectangle2D area, RectangleEdge edge) {
        double log;
        Range range = getRange();
        double axisMin = calculateLog(Math.max(this.smallestValue, range.getLowerBound()));
        double axisMax = calculateLog(range.getUpperBound());
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
            log = axisMax - (((java2DValue - min) / (max - min)) * (axisMax - axisMin));
        } else {
            log = axisMin + (((java2DValue - min) / (max - min)) * (axisMax - axisMin));
        }
        return calculateValue(log);
    }

    public double valueToJava2D(double value, Rectangle2D area, RectangleEdge edge) {
        Range range = getRange();
        double axisMin = calculateLog(range.getLowerBound());
        double axisMax = calculateLog(range.getUpperBound());
        value = calculateLog(value);
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
            double lower = Math.max(r.getLowerBound(), this.smallestValue);
            double range = upper - lower;
            double fixedAutoRange = getFixedAutoRange();
            if (fixedAutoRange > 0.0d) {
                lower = Math.max(upper - fixedAutoRange, this.smallestValue);
            } else {
                double minRange = getAutoRangeMinimumSize();
                if (range < minRange) {
                    double expand = (minRange - range) / DateAxis.DEFAULT_AUTO_RANGE_MINIMUM_SIZE_IN_MILLISECONDS;
                    upper += expand;
                    lower -= expand;
                }
                double logUpper = calculateLog(upper);
                double logLower = calculateLog(lower);
                double logRange = logUpper - logLower;
                logLower -= getLowerMargin() * logRange;
                upper = calculateValueNoINF(logUpper + (getUpperMargin() * logRange));
                lower = calculateValueNoINF(logLower);
            }
            setRange(new Range(lower, upper), false, false);
        }
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
        TextAnchor textAnchor;
        Range range = getRange();
        List ticks = new ArrayList();
        g2.setFont(getTickLabelFont());
        if (edge == RectangleEdge.TOP) {
            textAnchor = TextAnchor.BOTTOM_CENTER;
        } else {
            textAnchor = TextAnchor.TOP_CENTER;
        }
        if (isAutoTickUnitSelection()) {
            selectAutoTickUnit(g2, dataArea, edge);
        }
        int minorTickCount = this.tickUnit.getMinorTickCount();
        double unit = getTickUnit().getSize();
        double start = Math.ceil(calculateLog(getRange().getLowerBound()) / unit) * unit;
        double end = calculateLog(getUpperBound());
        double current = start;
        boolean hasTicks = this.tickUnit.getSize() > 0.0d && !Double.isInfinite(start);
        while (hasTicks && current <= end) {
            double v = calculateValueNoINF(current);
            if (range.contains(v)) {
                ticks.add(new LogTick(TickType.MAJOR, v, createTickLabel(v), textAnchor));
            }
            double next = Math.pow(this.base, this.tickUnit.getSize() + current);
            for (int i = 1; i < minorTickCount; i++) {
                double minorV = v + (((double) i) * ((next - v) / ((double) minorTickCount)));
                if (range.contains(minorV)) {
                    ticks.add(new LogTick(TickType.MINOR, minorV, null, textAnchor));
                }
            }
            current += this.tickUnit.getSize();
        }
        return ticks;
    }

    protected List refreshTicksVertical(Graphics2D g2, Rectangle2D dataArea, RectangleEdge edge) {
        TextAnchor textAnchor;
        Range range = getRange();
        List ticks = new ArrayList();
        g2.setFont(getTickLabelFont());
        if (edge == RectangleEdge.RIGHT) {
            textAnchor = TextAnchor.CENTER_LEFT;
        } else {
            textAnchor = TextAnchor.CENTER_RIGHT;
        }
        if (isAutoTickUnitSelection()) {
            selectAutoTickUnit(g2, dataArea, edge);
        }
        int minorTickCount = this.tickUnit.getMinorTickCount();
        double unit = getTickUnit().getSize();
        double start = Math.ceil(calculateLog(getRange().getLowerBound()) / unit) * unit;
        double end = calculateLog(getUpperBound());
        double current = start;
        boolean hasTicks = this.tickUnit.getSize() > 0.0d && !Double.isInfinite(start);
        while (hasTicks && current <= end) {
            double v = calculateValueNoINF(current);
            if (range.contains(v)) {
                ticks.add(new LogTick(TickType.MAJOR, v, createTickLabel(v), textAnchor));
            }
            double next = Math.pow(this.base, this.tickUnit.getSize() + current);
            for (int i = 1; i < minorTickCount; i++) {
                double minorV = v + (((double) i) * ((next - v) / ((double) minorTickCount)));
                if (range.contains(minorV)) {
                    ticks.add(new LogTick(TickType.MINOR, minorV, null, textAnchor));
                }
            }
            current += this.tickUnit.getSize();
        }
        return ticks;
    }

    protected void selectAutoTickUnit(Graphics2D g2, Rectangle2D dataArea, RectangleEdge edge) {
        if (RectangleEdge.isTopOrBottom(edge)) {
            selectHorizontalAutoTickUnit(g2, dataArea, edge);
        } else if (RectangleEdge.isLeftOrRight(edge)) {
            selectVerticalAutoTickUnit(g2, dataArea, edge);
        }
    }

    protected void selectHorizontalAutoTickUnit(Graphics2D g2, Rectangle2D dataArea, RectangleEdge edge) {
        Range range = getRange();
        double size = (calculateLog(range.getUpperBound()) - calculateLog(Math.max(this.smallestValue, range.getLowerBound()))) / 50.0d;
        TickUnitSource tickUnits = getStandardTickUnits();
        TickUnit candidate = tickUnits.getCeilingTickUnit(size);
        TickUnit prevCandidate = candidate;
        boolean found = false;
        while (!found) {
            this.tickUnit = (NumberTickUnit) candidate;
            double tickLabelWidth = estimateMaximumTickLabelWidth(g2, candidate);
            double candidateWidth = exponentLengthToJava2D(candidate.getSize(), dataArea, edge);
            if (tickLabelWidth < candidateWidth) {
                found = true;
            } else if (Double.isNaN(candidateWidth)) {
                candidate = prevCandidate;
                found = true;
            } else {
                prevCandidate = candidate;
                candidate = tickUnits.getLargerTickUnit(prevCandidate);
                if (candidate.equals(prevCandidate)) {
                    found = true;
                }
            }
        }
        setTickUnit((NumberTickUnit) candidate, false, false);
    }

    public double exponentLengthToJava2D(double length, Rectangle2D area, RectangleEdge edge) {
        return Math.abs(valueToJava2D(calculateValueNoINF(length + NormalizedMatrixSeries.DEFAULT_SCALE_FACTOR), area, edge) - valueToJava2D(calculateValueNoINF(NormalizedMatrixSeries.DEFAULT_SCALE_FACTOR), area, edge));
    }

    protected void selectVerticalAutoTickUnit(Graphics2D g2, Rectangle2D dataArea, RectangleEdge edge) {
        Range range = getRange();
        double size = (calculateLog(range.getUpperBound()) - calculateLog(Math.max(this.smallestValue, range.getLowerBound()))) / 50.0d;
        TickUnitSource tickUnits = getStandardTickUnits();
        TickUnit candidate = tickUnits.getCeilingTickUnit(size);
        TickUnit prevCandidate = candidate;
        boolean found = false;
        while (!found) {
            this.tickUnit = (NumberTickUnit) candidate;
            double tickLabelHeight = estimateMaximumTickLabelHeight(g2);
            double candidateHeight = exponentLengthToJava2D(candidate.getSize(), dataArea, edge);
            if (tickLabelHeight < candidateHeight) {
                found = true;
            } else if (Double.isNaN(candidateHeight)) {
                candidate = prevCandidate;
                found = true;
            } else {
                prevCandidate = candidate;
                candidate = tickUnits.getLargerTickUnit(prevCandidate);
                if (candidate.equals(prevCandidate)) {
                    found = true;
                }
            }
        }
        setTickUnit((NumberTickUnit) candidate, false, false);
    }

    protected AttributedString createTickLabel(double value) {
        if (this.numberFormatOverride != null) {
            return new AttributedString(this.numberFormatOverride.format(value));
        }
        String baseStr = this.baseSymbol;
        if (baseStr == null) {
            baseStr = this.baseFormatter.format(Double.valueOf(this.base));
        }
        String exponentStr = getTickUnit().valueToString(calculateLog(value));
        AttributedString as = new AttributedString(baseStr + exponentStr);
        as.addAttributes(getTickLabelFont().getAttributes(), 0, (baseStr + exponentStr).length());
        as.addAttribute(TextAttribute.SUPERSCRIPT, TextAttribute.SUPERSCRIPT_SUPER, baseStr.length(), baseStr.length() + exponentStr.length());
        return as;
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
        Range range = getRange();
        double lower = range.getLowerBound();
        double upper = range.getUpperBound();
        return result + Math.max(AttrStringUtils.getTextBounds(createTickLabel(lower), g2).getWidth(), AttrStringUtils.getTextBounds(createTickLabel(upper), g2).getWidth());
    }

    public void zoomRange(double lowerPercent, double upperPercent) {
        Range adjusted;
        Range range = getRange();
        double start = range.getLowerBound();
        double end = range.getUpperBound();
        double log1 = calculateLog(start);
        double length = calculateLog(end) - log1;
        double logB;
        if (isInverted()) {
            logB = log1 + ((NormalizedMatrixSeries.DEFAULT_SCALE_FACTOR - lowerPercent) * length);
            adjusted = new Range(calculateValueNoINF(log1 + ((NormalizedMatrixSeries.DEFAULT_SCALE_FACTOR - upperPercent) * length)), calculateValueNoINF(logB));
        } else {
            logB = log1 + (length * upperPercent);
            adjusted = new Range(calculateValueNoINF(log1 + (length * lowerPercent)), calculateValueNoINF(logB));
        }
        setRange(adjusted);
    }

    public void pan(double percent) {
        Range range = getRange();
        double lower = range.getLowerBound();
        double upper = range.getUpperBound();
        double log1 = calculateLog(lower);
        double log2 = calculateLog(upper);
        double adj = (log2 - log1) * percent;
        log2 += adj;
        setRange(calculateValueNoINF(log1 + adj), calculateValueNoINF(log2));
    }

    public void resizeRange(double percent) {
        Range range = getRange();
        resizeRange(percent, calculateValueNoINF((calculateLog(range.getLowerBound()) + calculateLog(range.getUpperBound())) / DateAxis.DEFAULT_AUTO_RANGE_MINIMUM_SIZE_IN_MILLISECONDS));
    }

    public void resizeRange(double percent, double anchorValue) {
        resizeRange2(percent, anchorValue);
    }

    public void resizeRange2(double percent, double anchorValue) {
        if (percent > 0.0d) {
            double logAnchorValue = calculateLog(anchorValue);
            Range range = getRange();
            double logAxisMin = calculateLog(range.getLowerBound());
            double left = percent * (logAnchorValue - logAxisMin);
            double upperBound = calculateValueNoINF(logAnchorValue + (percent * (calculateLog(range.getUpperBound()) - logAnchorValue)));
            setRange(new Range(calculateValueNoINF(logAnchorValue - left), upperBound));
            return;
        }
        setAutoRange(true);
    }

    public boolean equals(Object obj) {
        if (obj == this) {
            return true;
        }
        if (!(obj instanceof LogAxis)) {
            return false;
        }
        LogAxis that = (LogAxis) obj;
        if (this.base == that.base && ObjectUtilities.equal(this.baseSymbol, that.baseSymbol) && this.baseFormatter.equals(that.baseFormatter) && this.smallestValue == that.smallestValue && ObjectUtilities.equal(this.numberFormatOverride, that.numberFormatOverride)) {
            return super.equals(obj);
        }
        return false;
    }

    public int hashCode() {
        long temp = Double.doubleToLongBits(this.base);
        int result = ((int) ((temp >>> 32) ^ temp)) + 7141;
        temp = Double.doubleToLongBits(this.smallestValue);
        result = (result * 37) + ((int) ((temp >>> 32) ^ temp));
        if (this.numberFormatOverride != null) {
            result = (result * 37) + this.numberFormatOverride.hashCode();
        }
        return (result * 37) + this.tickUnit.hashCode();
    }

    public static TickUnitSource createLogTickUnits(Locale locale) {
        TickUnits units = new TickUnits();
        NumberFormat numberFormat = new LogFormat();
        units.add(new NumberTickUnit(ValueAxis.DEFAULT_UPPER_MARGIN, numberFormat, 2));
        units.add(new NumberTickUnit(SpiderWebPlot.DEFAULT_AXIS_LABEL_GAP, numberFormat, 10));
        units.add(new NumberTickUnit(LevelRenderer.DEFAULT_ITEM_MARGIN, numberFormat, 2));
        units.add(new NumberTickUnit(0.5d, numberFormat, 5));
        units.add(new NumberTickUnit(NormalizedMatrixSeries.DEFAULT_SCALE_FACTOR, numberFormat, 10));
        units.add(new NumberTickUnit(DateAxis.DEFAULT_AUTO_RANGE_MINIMUM_SIZE_IN_MILLISECONDS, numberFormat, 10));
        units.add(new NumberTickUnit(BarRenderer.BAR_OUTLINE_WIDTH_THRESHOLD, numberFormat, 15));
        units.add(new NumberTickUnit(4.0d, numberFormat, 20));
        units.add(new NumberTickUnit(XYPointerAnnotation.DEFAULT_ARROW_LENGTH, numberFormat, 25));
        units.add(new NumberTickUnit(6.0d, numberFormat));
        units.add(new NumberTickUnit(7.0d, numberFormat));
        units.add(new NumberTickUnit(XYLine3DRenderer.DEFAULT_Y_OFFSET, numberFormat));
        units.add(new NumberTickUnit(9.0d, numberFormat));
        units.add(new NumberTickUnit(XYPointerAnnotation.DEFAULT_TIP_RADIUS, numberFormat));
        return units;
    }
}
