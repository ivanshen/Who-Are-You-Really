package org.jfree.chart.axis;

import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics2D;
import java.awt.Polygon;
import java.awt.RenderingHints;
import java.awt.Shape;
import java.awt.geom.AffineTransform;
import java.awt.geom.Line2D;
import java.awt.geom.Line2D.Double;
import java.awt.geom.Rectangle2D;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.List;
import org.jfree.chart.plot.Plot;
import org.jfree.chart.util.AttrStringUtils;
import org.jfree.chart.util.ParamChecks;
import org.jfree.data.Range;
import org.jfree.io.SerialUtilities;
import org.jfree.text.TextUtilities;
import org.jfree.ui.RectangleEdge;
import org.jfree.ui.RectangleInsets;
import org.jfree.util.ObjectUtilities;
import org.jfree.util.PublicCloneable;

public abstract class ValueAxis extends Axis implements Cloneable, PublicCloneable, Serializable {
    public static final boolean DEFAULT_AUTO_RANGE = true;
    public static final double DEFAULT_AUTO_RANGE_MINIMUM_SIZE = 1.0E-8d;
    public static final boolean DEFAULT_AUTO_TICK_UNIT_SELECTION = true;
    public static final boolean DEFAULT_INVERTED = false;
    public static final double DEFAULT_LOWER_BOUND = 0.0d;
    public static final double DEFAULT_LOWER_MARGIN = 0.05d;
    public static final Range DEFAULT_RANGE;
    public static final double DEFAULT_UPPER_BOUND = 1.0d;
    public static final double DEFAULT_UPPER_MARGIN = 0.05d;
    public static final int MAXIMUM_TICK_COUNT = 500;
    private static final long serialVersionUID = 3698345477322391456L;
    private boolean autoRange;
    private double autoRangeMinimumSize;
    private int autoTickIndex;
    private boolean autoTickUnitSelection;
    private Range defaultAutoRange;
    private transient Shape downArrow;
    private double fixedAutoRange;
    private boolean inverted;
    private transient Shape leftArrow;
    private double lowerMargin;
    private int minorTickCount;
    private boolean negativeArrowVisible;
    private boolean positiveArrowVisible;
    private Range range;
    private transient Shape rightArrow;
    private TickUnitSource standardTickUnits;
    private transient Shape upArrow;
    private double upperMargin;
    private boolean verticalTickLabels;

    protected abstract void autoAdjustRange();

    public abstract double java2DToValue(double d, Rectangle2D rectangle2D, RectangleEdge rectangleEdge);

    public abstract double valueToJava2D(double d, Rectangle2D rectangle2D, RectangleEdge rectangleEdge);

    static {
        DEFAULT_RANGE = new Range(DEFAULT_LOWER_BOUND, DEFAULT_UPPER_BOUND);
    }

    protected ValueAxis(String label, TickUnitSource standardTickUnits) {
        super(label);
        this.positiveArrowVisible = DEFAULT_INVERTED;
        this.negativeArrowVisible = DEFAULT_INVERTED;
        this.range = DEFAULT_RANGE;
        this.autoRange = DEFAULT_AUTO_TICK_UNIT_SELECTION;
        this.defaultAutoRange = DEFAULT_RANGE;
        this.inverted = DEFAULT_INVERTED;
        this.autoRangeMinimumSize = DEFAULT_AUTO_RANGE_MINIMUM_SIZE;
        this.lowerMargin = DEFAULT_UPPER_MARGIN;
        this.upperMargin = DEFAULT_UPPER_MARGIN;
        this.fixedAutoRange = DEFAULT_LOWER_BOUND;
        this.autoTickUnitSelection = DEFAULT_AUTO_TICK_UNIT_SELECTION;
        this.standardTickUnits = standardTickUnits;
        Polygon p1 = new Polygon();
        p1.addPoint(0, 0);
        p1.addPoint(-2, 2);
        p1.addPoint(2, 2);
        this.upArrow = p1;
        Polygon p2 = new Polygon();
        p2.addPoint(0, 0);
        p2.addPoint(-2, -2);
        p2.addPoint(2, -2);
        this.downArrow = p2;
        Polygon p3 = new Polygon();
        p3.addPoint(0, 0);
        p3.addPoint(-2, -2);
        p3.addPoint(-2, 2);
        this.rightArrow = p3;
        Polygon p4 = new Polygon();
        p4.addPoint(0, 0);
        p4.addPoint(2, -2);
        p4.addPoint(2, 2);
        this.leftArrow = p4;
        this.verticalTickLabels = DEFAULT_INVERTED;
        this.minorTickCount = 0;
    }

    public boolean isVerticalTickLabels() {
        return this.verticalTickLabels;
    }

    public void setVerticalTickLabels(boolean flag) {
        if (this.verticalTickLabels != flag) {
            this.verticalTickLabels = flag;
            fireChangeEvent();
        }
    }

    public boolean isPositiveArrowVisible() {
        return this.positiveArrowVisible;
    }

    public void setPositiveArrowVisible(boolean visible) {
        this.positiveArrowVisible = visible;
        fireChangeEvent();
    }

    public boolean isNegativeArrowVisible() {
        return this.negativeArrowVisible;
    }

    public void setNegativeArrowVisible(boolean visible) {
        this.negativeArrowVisible = visible;
        fireChangeEvent();
    }

    public Shape getUpArrow() {
        return this.upArrow;
    }

    public void setUpArrow(Shape arrow) {
        ParamChecks.nullNotPermitted(arrow, "arrow");
        this.upArrow = arrow;
        fireChangeEvent();
    }

    public Shape getDownArrow() {
        return this.downArrow;
    }

    public void setDownArrow(Shape arrow) {
        ParamChecks.nullNotPermitted(arrow, "arrow");
        this.downArrow = arrow;
        fireChangeEvent();
    }

    public Shape getLeftArrow() {
        return this.leftArrow;
    }

    public void setLeftArrow(Shape arrow) {
        ParamChecks.nullNotPermitted(arrow, "arrow");
        this.leftArrow = arrow;
        fireChangeEvent();
    }

    public Shape getRightArrow() {
        return this.rightArrow;
    }

    public void setRightArrow(Shape arrow) {
        ParamChecks.nullNotPermitted(arrow, "arrow");
        this.rightArrow = arrow;
        fireChangeEvent();
    }

    protected void drawAxisLine(Graphics2D g2, double cursor, Rectangle2D dataArea, RectangleEdge edge) {
        double x;
        double y;
        Shape arrow;
        Line2D axisLine = null;
        double c = cursor;
        if (edge == RectangleEdge.TOP) {
            axisLine = new Double(dataArea.getX(), c, dataArea.getMaxX(), c);
        } else if (edge == RectangleEdge.BOTTOM) {
            axisLine = new Double(dataArea.getX(), c, dataArea.getMaxX(), c);
        } else if (edge == RectangleEdge.LEFT) {
            r9 = new Double(c, dataArea.getY(), c, dataArea.getMaxY());
        } else if (edge == RectangleEdge.RIGHT) {
            r9 = new Double(c, dataArea.getY(), c, dataArea.getMaxY());
        }
        g2.setPaint(getAxisLinePaint());
        g2.setStroke(getAxisLineStroke());
        Object saved = g2.getRenderingHint(RenderingHints.KEY_STROKE_CONTROL);
        g2.setRenderingHint(RenderingHints.KEY_STROKE_CONTROL, RenderingHints.VALUE_STROKE_NORMALIZE);
        g2.draw(axisLine);
        g2.setRenderingHint(RenderingHints.KEY_STROKE_CONTROL, saved);
        boolean drawUpOrRight = DEFAULT_INVERTED;
        boolean drawDownOrLeft = DEFAULT_INVERTED;
        if (this.positiveArrowVisible) {
            if (this.inverted) {
                drawDownOrLeft = DEFAULT_AUTO_TICK_UNIT_SELECTION;
            } else {
                drawUpOrRight = DEFAULT_AUTO_TICK_UNIT_SELECTION;
            }
        }
        if (this.negativeArrowVisible) {
            if (this.inverted) {
                drawUpOrRight = DEFAULT_AUTO_TICK_UNIT_SELECTION;
            } else {
                drawDownOrLeft = DEFAULT_AUTO_TICK_UNIT_SELECTION;
            }
        }
        if (drawUpOrRight) {
            x = DEFAULT_LOWER_BOUND;
            y = DEFAULT_LOWER_BOUND;
            arrow = null;
            if (edge == RectangleEdge.TOP || edge == RectangleEdge.BOTTOM) {
                x = dataArea.getMaxX();
                y = cursor;
                arrow = this.rightArrow;
            } else if (edge == RectangleEdge.LEFT || edge == RectangleEdge.RIGHT) {
                x = cursor;
                y = dataArea.getMinY();
                arrow = this.upArrow;
            }
            AffineTransform transformer = new AffineTransform();
            transformer.setToTranslation(x, y);
            Shape shape = transformer.createTransformedShape(arrow);
            g2.fill(shape);
            g2.draw(shape);
        }
        if (drawDownOrLeft) {
            x = DEFAULT_LOWER_BOUND;
            y = DEFAULT_LOWER_BOUND;
            arrow = null;
            if (edge == RectangleEdge.TOP || edge == RectangleEdge.BOTTOM) {
                x = dataArea.getMinX();
                y = cursor;
                arrow = this.leftArrow;
            } else if (edge == RectangleEdge.LEFT || edge == RectangleEdge.RIGHT) {
                x = cursor;
                y = dataArea.getMaxY();
                arrow = this.downArrow;
            }
            transformer = new AffineTransform();
            transformer.setToTranslation(x, y);
            shape = transformer.createTransformedShape(arrow);
            g2.fill(shape);
            g2.draw(shape);
        }
    }

    protected float[] calculateAnchorPoint(ValueTick tick, double cursor, Rectangle2D dataArea, RectangleEdge edge) {
        RectangleInsets insets = getTickLabelInsets();
        float[] result = new float[2];
        if (edge == RectangleEdge.TOP) {
            result[0] = (float) valueToJava2D(tick.getValue(), dataArea, edge);
            result[1] = (float) ((cursor - insets.getBottom()) - DateAxis.DEFAULT_AUTO_RANGE_MINIMUM_SIZE_IN_MILLISECONDS);
        } else if (edge == RectangleEdge.BOTTOM) {
            result[0] = (float) valueToJava2D(tick.getValue(), dataArea, edge);
            result[1] = (float) ((insets.getTop() + cursor) + DateAxis.DEFAULT_AUTO_RANGE_MINIMUM_SIZE_IN_MILLISECONDS);
        } else if (edge == RectangleEdge.LEFT) {
            result[0] = (float) ((cursor - insets.getLeft()) - DateAxis.DEFAULT_AUTO_RANGE_MINIMUM_SIZE_IN_MILLISECONDS);
            result[1] = (float) valueToJava2D(tick.getValue(), dataArea, edge);
        } else if (edge == RectangleEdge.RIGHT) {
            result[0] = (float) ((insets.getRight() + cursor) + DateAxis.DEFAULT_AUTO_RANGE_MINIMUM_SIZE_IN_MILLISECONDS);
            result[1] = (float) valueToJava2D(tick.getValue(), dataArea, edge);
        }
        return result;
    }

    protected AxisState drawTickMarksAndLabels(Graphics2D g2, double cursor, Rectangle2D plotArea, Rectangle2D dataArea, RectangleEdge edge) {
        AxisState axisState = new AxisState(cursor);
        if (isAxisLineVisible()) {
            drawAxisLine(g2, cursor, dataArea, edge);
        }
        List<ValueTick> ticks = refreshTicks(g2, axisState, dataArea, edge);
        axisState.setTicks(ticks);
        g2.setFont(getTickLabelFont());
        Object saved = g2.getRenderingHint(RenderingHints.KEY_STROKE_CONTROL);
        g2.setRenderingHint(RenderingHints.KEY_STROKE_CONTROL, RenderingHints.VALUE_STROKE_NORMALIZE);
        for (ValueTick tick : ticks) {
            if (isTickLabelsVisible()) {
                g2.setPaint(getTickLabelPaint());
                float[] anchorPoint = calculateAnchorPoint(tick, cursor, dataArea, edge);
                if (tick instanceof LogTick) {
                    LogTick lt = (LogTick) tick;
                    if (lt.getAttributedLabel() != null) {
                        AttrStringUtils.drawRotatedString(lt.getAttributedLabel(), g2, anchorPoint[0], anchorPoint[1], tick.getTextAnchor(), tick.getAngle(), tick.getRotationAnchor());
                    }
                } else if (tick.getText() != null) {
                    TextUtilities.drawRotatedString(tick.getText(), g2, anchorPoint[0], anchorPoint[1], tick.getTextAnchor(), tick.getAngle(), tick.getRotationAnchor());
                }
            }
            if ((isTickMarksVisible() && tick.getTickType().equals(TickType.MAJOR)) || (isMinorTickMarksVisible() && tick.getTickType().equals(TickType.MINOR))) {
                double ol;
                double il;
                if (tick.getTickType().equals(TickType.MINOR)) {
                    ol = (double) getMinorTickMarkOutsideLength();
                } else {
                    ol = (double) getTickMarkOutsideLength();
                }
                if (tick.getTickType().equals(TickType.MINOR)) {
                    il = (double) getMinorTickMarkInsideLength();
                } else {
                    il = (double) getTickMarkInsideLength();
                }
                float xx = (float) valueToJava2D(tick.getValue(), dataArea, edge);
                Line2D mark = null;
                g2.setStroke(getTickMarkStroke());
                g2.setPaint(getTickMarkPaint());
                if (edge == RectangleEdge.LEFT) {
                    mark = new Double(cursor - ol, (double) xx, cursor + il, (double) xx);
                } else if (edge == RectangleEdge.RIGHT) {
                    mark = new Double(cursor + ol, (double) xx, cursor - il, (double) xx);
                } else if (edge == RectangleEdge.TOP) {
                    mark = new Double((double) xx, cursor - ol, (double) xx, cursor + il);
                } else if (edge == RectangleEdge.BOTTOM) {
                    mark = new Double((double) xx, cursor + ol, (double) xx, cursor - il);
                }
                g2.draw(mark);
            }
        }
        g2.setRenderingHint(RenderingHints.KEY_STROKE_CONTROL, saved);
        if (isTickLabelsVisible()) {
            if (edge == RectangleEdge.LEFT) {
                axisState.cursorLeft(DEFAULT_LOWER_BOUND + findMaximumTickLabelWidth(ticks, g2, plotArea, isVerticalTickLabels()));
            } else if (edge == RectangleEdge.RIGHT) {
                axisState.cursorRight(findMaximumTickLabelWidth(ticks, g2, plotArea, isVerticalTickLabels()));
            } else if (edge == RectangleEdge.TOP) {
                axisState.cursorUp(findMaximumTickLabelHeight(ticks, g2, plotArea, isVerticalTickLabels()));
            } else if (edge == RectangleEdge.BOTTOM) {
                axisState.cursorDown(findMaximumTickLabelHeight(ticks, g2, plotArea, isVerticalTickLabels()));
            }
        }
        return axisState;
    }

    public AxisSpace reserveSpace(Graphics2D g2, Plot plot, Rectangle2D plotArea, RectangleEdge edge, AxisSpace space) {
        if (space == null) {
            space = new AxisSpace();
        }
        if (isVisible()) {
            double dimension = getFixedDimension();
            if (dimension > DEFAULT_LOWER_BOUND) {
                space.add(dimension, edge);
            } else {
                double tickLabelHeight = DEFAULT_LOWER_BOUND;
                double tickLabelWidth = DEFAULT_LOWER_BOUND;
                if (isTickLabelsVisible()) {
                    g2.setFont(getTickLabelFont());
                    List ticks = refreshTicks(g2, new AxisState(), plotArea, edge);
                    if (RectangleEdge.isTopOrBottom(edge)) {
                        tickLabelHeight = findMaximumTickLabelHeight(ticks, g2, plotArea, isVerticalTickLabels());
                    } else if (RectangleEdge.isLeftOrRight(edge)) {
                        tickLabelWidth = findMaximumTickLabelWidth(ticks, g2, plotArea, isVerticalTickLabels());
                    }
                }
                Rectangle2D labelEnclosure = getLabelEnclosure(g2, edge);
                if (RectangleEdge.isTopOrBottom(edge)) {
                    space.add(labelEnclosure.getHeight() + tickLabelHeight, edge);
                } else if (RectangleEdge.isLeftOrRight(edge)) {
                    space.add(labelEnclosure.getWidth() + tickLabelWidth, edge);
                }
            }
        }
        return space;
    }

    protected double findMaximumTickLabelHeight(List ticks, Graphics2D g2, Rectangle2D drawArea, boolean vertical) {
        RectangleInsets insets = getTickLabelInsets();
        Font font = getTickLabelFont();
        g2.setFont(font);
        double maxHeight = DEFAULT_LOWER_BOUND;
        if (!vertical) {
            return (((double) font.getLineMetrics("ABCxyz", g2.getFontRenderContext()).getHeight()) + insets.getTop()) + insets.getBottom();
        }
        FontMetrics fm = g2.getFontMetrics(font);
        for (Tick tick : ticks) {
            Rectangle2D labelBounds = null;
            if (tick instanceof LogTick) {
                LogTick lt = (LogTick) tick;
                if (lt.getAttributedLabel() != null) {
                    labelBounds = AttrStringUtils.getTextBounds(lt.getAttributedLabel(), g2);
                }
            } else if (tick.getText() != null) {
                labelBounds = TextUtilities.getTextBounds(tick.getText(), g2, fm);
            }
            if (labelBounds != null && (labelBounds.getWidth() + insets.getTop()) + insets.getBottom() > maxHeight) {
                maxHeight = (labelBounds.getWidth() + insets.getTop()) + insets.getBottom();
            }
        }
        return maxHeight;
    }

    protected double findMaximumTickLabelWidth(List ticks, Graphics2D g2, Rectangle2D drawArea, boolean vertical) {
        RectangleInsets insets = getTickLabelInsets();
        Font font = getTickLabelFont();
        double maxWidth = DEFAULT_LOWER_BOUND;
        if (vertical) {
            return (((double) font.getLineMetrics("ABCxyz", g2.getFontRenderContext()).getHeight()) + insets.getTop()) + insets.getBottom();
        }
        FontMetrics fm = g2.getFontMetrics(font);
        for (Tick tick : ticks) {
            Rectangle2D labelBounds = null;
            if (tick instanceof LogTick) {
                LogTick lt = (LogTick) tick;
                if (lt.getAttributedLabel() != null) {
                    labelBounds = AttrStringUtils.getTextBounds(lt.getAttributedLabel(), g2);
                }
            } else if (tick.getText() != null) {
                labelBounds = TextUtilities.getTextBounds(tick.getText(), g2, fm);
            }
            if (labelBounds != null && (labelBounds.getWidth() + insets.getLeft()) + insets.getRight() > maxWidth) {
                maxWidth = (labelBounds.getWidth() + insets.getLeft()) + insets.getRight();
            }
        }
        return maxWidth;
    }

    public boolean isInverted() {
        return this.inverted;
    }

    public void setInverted(boolean flag) {
        if (this.inverted != flag) {
            this.inverted = flag;
            fireChangeEvent();
        }
    }

    public boolean isAutoRange() {
        return this.autoRange;
    }

    public void setAutoRange(boolean auto) {
        setAutoRange(auto, DEFAULT_AUTO_TICK_UNIT_SELECTION);
    }

    protected void setAutoRange(boolean auto, boolean notify) {
        if (this.autoRange != auto) {
            this.autoRange = auto;
            if (this.autoRange) {
                autoAdjustRange();
            }
            if (notify) {
                fireChangeEvent();
            }
        }
    }

    public double getAutoRangeMinimumSize() {
        return this.autoRangeMinimumSize;
    }

    public void setAutoRangeMinimumSize(double size) {
        setAutoRangeMinimumSize(size, DEFAULT_AUTO_TICK_UNIT_SELECTION);
    }

    public void setAutoRangeMinimumSize(double size, boolean notify) {
        if (size <= DEFAULT_LOWER_BOUND) {
            throw new IllegalArgumentException("NumberAxis.setAutoRangeMinimumSize(double): must be > 0.0.");
        } else if (this.autoRangeMinimumSize != size) {
            this.autoRangeMinimumSize = size;
            if (this.autoRange) {
                autoAdjustRange();
            }
            if (notify) {
                fireChangeEvent();
            }
        }
    }

    public Range getDefaultAutoRange() {
        return this.defaultAutoRange;
    }

    public void setDefaultAutoRange(Range range) {
        ParamChecks.nullNotPermitted(range, "range");
        this.defaultAutoRange = range;
        fireChangeEvent();
    }

    public double getLowerMargin() {
        return this.lowerMargin;
    }

    public void setLowerMargin(double margin) {
        this.lowerMargin = margin;
        if (isAutoRange()) {
            autoAdjustRange();
        }
        fireChangeEvent();
    }

    public double getUpperMargin() {
        return this.upperMargin;
    }

    public void setUpperMargin(double margin) {
        this.upperMargin = margin;
        if (isAutoRange()) {
            autoAdjustRange();
        }
        fireChangeEvent();
    }

    public double getFixedAutoRange() {
        return this.fixedAutoRange;
    }

    public void setFixedAutoRange(double length) {
        this.fixedAutoRange = length;
        if (isAutoRange()) {
            autoAdjustRange();
        }
        fireChangeEvent();
    }

    public double getLowerBound() {
        return this.range.getLowerBound();
    }

    public void setLowerBound(double min) {
        if (this.range.getUpperBound() > min) {
            setRange(new Range(min, this.range.getUpperBound()));
        } else {
            setRange(new Range(min, DEFAULT_UPPER_BOUND + min));
        }
    }

    public double getUpperBound() {
        return this.range.getUpperBound();
    }

    public void setUpperBound(double max) {
        if (this.range.getLowerBound() < max) {
            setRange(new Range(this.range.getLowerBound(), max));
        } else {
            setRange(max - DEFAULT_UPPER_BOUND, max);
        }
    }

    public Range getRange() {
        return this.range;
    }

    public void setRange(Range range) {
        setRange(range, DEFAULT_AUTO_TICK_UNIT_SELECTION, DEFAULT_AUTO_TICK_UNIT_SELECTION);
    }

    public void setRange(Range range, boolean turnOffAutoRange, boolean notify) {
        ParamChecks.nullNotPermitted(range, "range");
        if (range.getLength() <= DEFAULT_LOWER_BOUND) {
            throw new IllegalArgumentException("A positive range length is required: " + range);
        }
        if (turnOffAutoRange) {
            this.autoRange = DEFAULT_INVERTED;
        }
        this.range = range;
        if (notify) {
            fireChangeEvent();
        }
    }

    public void setRange(double lower, double upper) {
        setRange(new Range(lower, upper));
    }

    public void setRangeWithMargins(Range range) {
        setRangeWithMargins(range, DEFAULT_AUTO_TICK_UNIT_SELECTION, DEFAULT_AUTO_TICK_UNIT_SELECTION);
    }

    public void setRangeWithMargins(Range range, boolean turnOffAutoRange, boolean notify) {
        ParamChecks.nullNotPermitted(range, "range");
        setRange(Range.expand(range, getLowerMargin(), getUpperMargin()), turnOffAutoRange, notify);
    }

    public void setRangeWithMargins(double lower, double upper) {
        setRangeWithMargins(new Range(lower, upper));
    }

    public void setRangeAboutValue(double value, double length) {
        setRange(new Range(value - (length / DateAxis.DEFAULT_AUTO_RANGE_MINIMUM_SIZE_IN_MILLISECONDS), (length / DateAxis.DEFAULT_AUTO_RANGE_MINIMUM_SIZE_IN_MILLISECONDS) + value));
    }

    public boolean isAutoTickUnitSelection() {
        return this.autoTickUnitSelection;
    }

    public void setAutoTickUnitSelection(boolean flag) {
        setAutoTickUnitSelection(flag, DEFAULT_AUTO_TICK_UNIT_SELECTION);
    }

    public void setAutoTickUnitSelection(boolean flag, boolean notify) {
        if (this.autoTickUnitSelection != flag) {
            this.autoTickUnitSelection = flag;
            if (notify) {
                fireChangeEvent();
            }
        }
    }

    public TickUnitSource getStandardTickUnits() {
        return this.standardTickUnits;
    }

    public void setStandardTickUnits(TickUnitSource source) {
        this.standardTickUnits = source;
        fireChangeEvent();
    }

    public int getMinorTickCount() {
        return this.minorTickCount;
    }

    public void setMinorTickCount(int count) {
        this.minorTickCount = count;
        fireChangeEvent();
    }

    public double lengthToJava2D(double length, Rectangle2D area, RectangleEdge edge) {
        return Math.abs(valueToJava2D(length, area, edge) - valueToJava2D(DEFAULT_LOWER_BOUND, area, edge));
    }

    public void centerRange(double value) {
        double central = this.range.getCentralValue();
        setRange(new Range((this.range.getLowerBound() + value) - central, (this.range.getUpperBound() + value) - central));
    }

    public void resizeRange(double percent) {
        resizeRange(percent, this.range.getCentralValue());
    }

    public void resizeRange(double percent, double anchorValue) {
        if (percent > DEFAULT_LOWER_BOUND) {
            double halfLength = (this.range.getLength() * percent) / DateAxis.DEFAULT_AUTO_RANGE_MINIMUM_SIZE_IN_MILLISECONDS;
            setRange(new Range(anchorValue - halfLength, anchorValue + halfLength));
            return;
        }
        setAutoRange(DEFAULT_AUTO_TICK_UNIT_SELECTION);
    }

    public void resizeRange2(double percent, double anchorValue) {
        if (percent > DEFAULT_LOWER_BOUND) {
            setRange(new Range(anchorValue - ((anchorValue - getLowerBound()) * percent), ((getUpperBound() - anchorValue) * percent) + anchorValue));
            return;
        }
        setAutoRange(DEFAULT_AUTO_TICK_UNIT_SELECTION);
    }

    public void zoomRange(double lowerPercent, double upperPercent) {
        double r0;
        double r1;
        double start = this.range.getLowerBound();
        double length = this.range.getLength();
        if (isInverted()) {
            r0 = start + ((DEFAULT_UPPER_BOUND - upperPercent) * length);
            r1 = start + ((DEFAULT_UPPER_BOUND - lowerPercent) * length);
        } else {
            r0 = start + (length * lowerPercent);
            r1 = start + (length * upperPercent);
        }
        if (r1 > r0 && !Double.isInfinite(r1 - r0)) {
            setRange(new Range(r0, r1));
        }
    }

    public void pan(double percent) {
        Range r = getRange();
        double adj = this.range.getLength() * percent;
        setRange(r.getLowerBound() + adj, r.getUpperBound() + adj);
    }

    protected int getAutoTickIndex() {
        return this.autoTickIndex;
    }

    protected void setAutoTickIndex(int index) {
        this.autoTickIndex = index;
    }

    public boolean equals(Object obj) {
        if (obj == this) {
            return DEFAULT_AUTO_TICK_UNIT_SELECTION;
        }
        if (!(obj instanceof ValueAxis)) {
            return DEFAULT_INVERTED;
        }
        ValueAxis that = (ValueAxis) obj;
        if (this.positiveArrowVisible != that.positiveArrowVisible || this.negativeArrowVisible != that.negativeArrowVisible || this.inverted != that.inverted) {
            return DEFAULT_INVERTED;
        }
        if ((this.autoRange || ObjectUtilities.equal(this.range, that.range)) && this.autoRange == that.autoRange && this.autoRangeMinimumSize == that.autoRangeMinimumSize && this.defaultAutoRange.equals(that.defaultAutoRange) && this.upperMargin == that.upperMargin && this.lowerMargin == that.lowerMargin && this.fixedAutoRange == that.fixedAutoRange && this.autoTickUnitSelection == that.autoTickUnitSelection && ObjectUtilities.equal(this.standardTickUnits, that.standardTickUnits) && this.verticalTickLabels == that.verticalTickLabels && this.minorTickCount == that.minorTickCount) {
            return super.equals(obj);
        }
        return DEFAULT_INVERTED;
    }

    public Object clone() throws CloneNotSupportedException {
        return (ValueAxis) super.clone();
    }

    private void writeObject(ObjectOutputStream stream) throws IOException {
        stream.defaultWriteObject();
        SerialUtilities.writeShape(this.upArrow, stream);
        SerialUtilities.writeShape(this.downArrow, stream);
        SerialUtilities.writeShape(this.leftArrow, stream);
        SerialUtilities.writeShape(this.rightArrow, stream);
    }

    private void readObject(ObjectInputStream stream) throws IOException, ClassNotFoundException {
        stream.defaultReadObject();
        this.upArrow = SerialUtilities.readShape(stream);
        this.downArrow = SerialUtilities.readShape(stream);
        this.leftArrow = SerialUtilities.readShape(stream);
        this.rightArrow = SerialUtilities.readShape(stream);
    }
}
