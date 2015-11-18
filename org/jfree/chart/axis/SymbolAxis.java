package org.jfree.chart.axis;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Paint;
import java.awt.Shape;
import java.awt.Stroke;
import java.awt.geom.Rectangle2D;
import java.awt.geom.Rectangle2D.Double;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import org.jfree.chart.plot.Plot;
import org.jfree.chart.plot.PlotRenderingInfo;
import org.jfree.chart.plot.ValueAxisPlot;
import org.jfree.chart.util.ParamChecks;
import org.jfree.data.Range;
import org.jfree.data.xy.NormalizedMatrixSeries;
import org.jfree.io.SerialUtilities;
import org.jfree.text.TextUtilities;
import org.jfree.ui.RectangleEdge;
import org.jfree.ui.TextAnchor;
import org.jfree.util.PaintUtilities;

public class SymbolAxis extends NumberAxis implements Serializable {
    public static final Paint DEFAULT_GRID_BAND_ALTERNATE_PAINT;
    public static final Paint DEFAULT_GRID_BAND_PAINT;
    private static final long serialVersionUID = 7216330468770619716L;
    private transient Paint gridBandAlternatePaint;
    private transient Paint gridBandPaint;
    private boolean gridBandsVisible;
    private List symbols;

    static {
        DEFAULT_GRID_BAND_PAINT = new Color(232, 234, 232, 128);
        DEFAULT_GRID_BAND_ALTERNATE_PAINT = new Color(0, 0, 0, 0);
    }

    public SymbolAxis(String label, String[] sv) {
        super(label);
        this.symbols = Arrays.asList(sv);
        this.gridBandsVisible = true;
        this.gridBandPaint = DEFAULT_GRID_BAND_PAINT;
        this.gridBandAlternatePaint = DEFAULT_GRID_BAND_ALTERNATE_PAINT;
        setAutoTickUnitSelection(false, false);
        setAutoRangeStickyZero(false);
    }

    public String[] getSymbols() {
        return (String[]) this.symbols.toArray(new String[this.symbols.size()]);
    }

    public boolean isGridBandsVisible() {
        return this.gridBandsVisible;
    }

    public void setGridBandsVisible(boolean flag) {
        this.gridBandsVisible = flag;
        fireChangeEvent();
    }

    public Paint getGridBandPaint() {
        return this.gridBandPaint;
    }

    public void setGridBandPaint(Paint paint) {
        ParamChecks.nullNotPermitted(paint, "paint");
        this.gridBandPaint = paint;
        fireChangeEvent();
    }

    public Paint getGridBandAlternatePaint() {
        return this.gridBandAlternatePaint;
    }

    public void setGridBandAlternatePaint(Paint paint) {
        ParamChecks.nullNotPermitted(paint, "paint");
        this.gridBandAlternatePaint = paint;
        fireChangeEvent();
    }

    protected void selectAutoTickUnit(Graphics2D g2, Rectangle2D dataArea, RectangleEdge edge) {
        throw new UnsupportedOperationException();
    }

    public AxisState draw(Graphics2D g2, double cursor, Rectangle2D plotArea, Rectangle2D dataArea, RectangleEdge edge, PlotRenderingInfo plotState) {
        AxisState info = new AxisState(cursor);
        if (isVisible()) {
            info = super.draw(g2, cursor, plotArea, dataArea, edge, plotState);
        }
        if (this.gridBandsVisible) {
            drawGridBands(g2, plotArea, dataArea, edge, info.getTicks());
        }
        return info;
    }

    protected void drawGridBands(Graphics2D g2, Rectangle2D plotArea, Rectangle2D dataArea, RectangleEdge edge, List ticks) {
        Shape savedClip = g2.getClip();
        g2.clip(dataArea);
        if (RectangleEdge.isTopOrBottom(edge)) {
            drawGridBandsHorizontal(g2, plotArea, dataArea, true, ticks);
        } else if (RectangleEdge.isLeftOrRight(edge)) {
            drawGridBandsVertical(g2, plotArea, dataArea, true, ticks);
        }
        g2.setClip(savedClip);
    }

    protected void drawGridBandsHorizontal(Graphics2D g2, Rectangle2D plotArea, Rectangle2D dataArea, boolean firstGridBandIsDark, List ticks) {
        boolean currentGridBandIsDark = firstGridBandIsDark;
        double yy = dataArea.getY();
        double outlineStrokeWidth = NormalizedMatrixSeries.DEFAULT_SCALE_FACTOR;
        Stroke outlineStroke = getPlot().getOutlineStroke();
        if (outlineStroke != null && (outlineStroke instanceof BasicStroke)) {
            outlineStrokeWidth = (double) ((BasicStroke) outlineStroke).getLineWidth();
        }
        for (ValueTick tick : ticks) {
            double xx1 = valueToJava2D(tick.getValue() - 0.5d, dataArea, RectangleEdge.BOTTOM);
            double xx2 = valueToJava2D(tick.getValue() + 0.5d, dataArea, RectangleEdge.BOTTOM);
            if (currentGridBandIsDark) {
                g2.setPaint(this.gridBandPaint);
            } else {
                g2.setPaint(this.gridBandAlternatePaint);
            }
            g2.fill(new Double(Math.min(xx1, xx2), yy + outlineStrokeWidth, Math.abs(xx2 - xx1), (dataArea.getMaxY() - yy) - outlineStrokeWidth));
            if (currentGridBandIsDark) {
                currentGridBandIsDark = false;
            } else {
                currentGridBandIsDark = true;
            }
        }
    }

    protected void drawGridBandsVertical(Graphics2D g2, Rectangle2D plotArea, Rectangle2D dataArea, boolean firstGridBandIsDark, List ticks) {
        boolean currentGridBandIsDark = firstGridBandIsDark;
        double xx = dataArea.getX();
        double outlineStrokeWidth = NormalizedMatrixSeries.DEFAULT_SCALE_FACTOR;
        Stroke outlineStroke = getPlot().getOutlineStroke();
        if (outlineStroke != null && (outlineStroke instanceof BasicStroke)) {
            outlineStrokeWidth = (double) ((BasicStroke) outlineStroke).getLineWidth();
        }
        for (ValueTick tick : ticks) {
            double yy1 = valueToJava2D(tick.getValue() + 0.5d, dataArea, RectangleEdge.LEFT);
            double yy2 = valueToJava2D(tick.getValue() - 0.5d, dataArea, RectangleEdge.LEFT);
            if (currentGridBandIsDark) {
                g2.setPaint(this.gridBandPaint);
            } else {
                g2.setPaint(this.gridBandAlternatePaint);
            }
            g2.fill(new Double(xx + outlineStrokeWidth, Math.min(yy1, yy2), (dataArea.getMaxX() - xx) - outlineStrokeWidth, Math.abs(yy2 - yy1)));
            if (currentGridBandIsDark) {
                currentGridBandIsDark = false;
            } else {
                currentGridBandIsDark = true;
            }
        }
    }

    protected void autoAdjustRange() {
        Plot plot = getPlot();
        if (plot != null && (plot instanceof ValueAxisPlot)) {
            double upper = (double) (this.symbols.size() - 1);
            double lower = 0.0d;
            double range = upper - 0.0d;
            double minRange = getAutoRangeMinimumSize();
            if (range < minRange) {
                upper = ((upper + 0.0d) + minRange) / DateAxis.DEFAULT_AUTO_RANGE_MINIMUM_SIZE_IN_MILLISECONDS;
                lower = ((upper + 0.0d) - minRange) / DateAxis.DEFAULT_AUTO_RANGE_MINIMUM_SIZE_IN_MILLISECONDS;
            }
            if (getAutoRangeIncludesZero()) {
                if (getAutoRangeStickyZero()) {
                    if (upper <= 0.0d) {
                        upper = 0.0d;
                    } else {
                        upper += 0.5d;
                    }
                    if (lower >= 0.0d) {
                        lower = 0.0d;
                    } else {
                        lower -= 0.5d;
                    }
                } else {
                    upper = Math.max(0.0d, upper + 0.5d);
                    lower = Math.min(0.0d, lower - 0.5d);
                }
            } else if (getAutoRangeStickyZero()) {
                if (upper <= 0.0d) {
                    upper = Math.min(0.0d, upper + 0.5d);
                } else {
                    upper += 0.5d * range;
                }
                if (lower >= 0.0d) {
                    lower = Math.max(0.0d, lower - 0.5d);
                } else {
                    lower -= 0.5d;
                }
            } else {
                upper += 0.5d;
                lower -= 0.5d;
            }
            setRange(new Range(lower, upper), false, false);
        }
    }

    public List refreshTicks(Graphics2D g2, AxisState state, Rectangle2D dataArea, RectangleEdge edge) {
        if (RectangleEdge.isTopOrBottom(edge)) {
            return refreshTicksHorizontal(g2, dataArea, edge);
        }
        if (RectangleEdge.isLeftOrRight(edge)) {
            return refreshTicksVertical(g2, dataArea, edge);
        }
        return null;
    }

    protected List refreshTicksHorizontal(Graphics2D g2, Rectangle2D dataArea, RectangleEdge edge) {
        List ticks = new ArrayList();
        g2.setFont(getTickLabelFont());
        double size = getTickUnit().getSize();
        int count = calculateVisibleTickCount();
        double lowestTickValue = calculateLowestVisibleTickValue();
        double previousDrawnTickLabelPos = 0.0d;
        double previousDrawnTickLabelLength = 0.0d;
        if (count <= 500) {
            for (int i = 0; i < count; i++) {
                String tickLabel;
                TextAnchor anchor;
                TextAnchor rotationAnchor;
                double currentTickValue = lowestTickValue + (((double) i) * size);
                double xx = valueToJava2D(currentTickValue, dataArea, edge);
                NumberFormat formatter = getNumberFormatOverride();
                if (formatter != null) {
                    tickLabel = formatter.format(currentTickValue);
                } else {
                    tickLabel = valueToString(currentTickValue);
                }
                Rectangle2D bounds = TextUtilities.getTextBounds(tickLabel, g2, g2.getFontMetrics());
                double tickLabelLength = isVerticalTickLabels() ? bounds.getHeight() : bounds.getWidth();
                boolean tickLabelsOverlapping = false;
                if (i > 0) {
                    if (Math.abs(xx - previousDrawnTickLabelPos) < (previousDrawnTickLabelLength + tickLabelLength) / DateAxis.DEFAULT_AUTO_RANGE_MINIMUM_SIZE_IN_MILLISECONDS) {
                        tickLabelsOverlapping = true;
                    }
                }
                if (tickLabelsOverlapping) {
                    tickLabel = "";
                } else {
                    previousDrawnTickLabelPos = xx;
                    previousDrawnTickLabelLength = tickLabelLength;
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
                ticks.add(new NumberTick(new Double(currentTickValue), tickLabel, anchor, rotationAnchor, angle));
            }
        }
        return ticks;
    }

    protected List refreshTicksVertical(Graphics2D g2, Rectangle2D dataArea, RectangleEdge edge) {
        List ticks = new ArrayList();
        g2.setFont(getTickLabelFont());
        double size = getTickUnit().getSize();
        int count = calculateVisibleTickCount();
        double lowestTickValue = calculateLowestVisibleTickValue();
        double previousDrawnTickLabelPos = 0.0d;
        double previousDrawnTickLabelLength = 0.0d;
        if (count <= 500) {
            for (int i = 0; i < count; i++) {
                String tickLabel;
                TextAnchor anchor;
                TextAnchor rotationAnchor;
                double currentTickValue = lowestTickValue + (((double) i) * size);
                double yy = valueToJava2D(currentTickValue, dataArea, edge);
                NumberFormat formatter = getNumberFormatOverride();
                if (formatter != null) {
                    tickLabel = formatter.format(currentTickValue);
                } else {
                    tickLabel = valueToString(currentTickValue);
                }
                Rectangle2D bounds = TextUtilities.getTextBounds(tickLabel, g2, g2.getFontMetrics());
                double tickLabelLength = isVerticalTickLabels() ? bounds.getWidth() : bounds.getHeight();
                boolean tickLabelsOverlapping = false;
                if (i > 0) {
                    if (Math.abs(yy - previousDrawnTickLabelPos) < (previousDrawnTickLabelLength + tickLabelLength) / DateAxis.DEFAULT_AUTO_RANGE_MINIMUM_SIZE_IN_MILLISECONDS) {
                        tickLabelsOverlapping = true;
                    }
                }
                if (tickLabelsOverlapping) {
                    tickLabel = "";
                } else {
                    previousDrawnTickLabelPos = yy;
                    previousDrawnTickLabelLength = tickLabelLength;
                }
                double angle = 0.0d;
                if (isVerticalTickLabels()) {
                    anchor = TextAnchor.BOTTOM_CENTER;
                    rotationAnchor = TextAnchor.BOTTOM_CENTER;
                    angle = edge == RectangleEdge.LEFT ? -1.5707963267948966d : 1.5707963267948966d;
                } else if (edge == RectangleEdge.LEFT) {
                    anchor = TextAnchor.CENTER_RIGHT;
                    rotationAnchor = TextAnchor.CENTER_RIGHT;
                } else {
                    anchor = TextAnchor.CENTER_LEFT;
                    rotationAnchor = TextAnchor.CENTER_LEFT;
                }
                ticks.add(new NumberTick(new Double(currentTickValue), tickLabel, anchor, rotationAnchor, angle));
            }
        }
        return ticks;
    }

    public String valueToString(double value) {
        try {
            return (String) this.symbols.get((int) value);
        } catch (IndexOutOfBoundsException e) {
            return "";
        }
    }

    public boolean equals(Object obj) {
        if (obj == this) {
            return true;
        }
        if (!(obj instanceof SymbolAxis)) {
            return false;
        }
        SymbolAxis that = (SymbolAxis) obj;
        if (this.symbols.equals(that.symbols) && this.gridBandsVisible == that.gridBandsVisible && PaintUtilities.equal(this.gridBandPaint, that.gridBandPaint) && PaintUtilities.equal(this.gridBandAlternatePaint, that.gridBandAlternatePaint)) {
            return super.equals(obj);
        }
        return false;
    }

    private void writeObject(ObjectOutputStream stream) throws IOException {
        stream.defaultWriteObject();
        SerialUtilities.writePaint(this.gridBandPaint, stream);
        SerialUtilities.writePaint(this.gridBandAlternatePaint, stream);
    }

    private void readObject(ObjectInputStream stream) throws IOException, ClassNotFoundException {
        stream.defaultReadObject();
        this.gridBandPaint = SerialUtilities.readPaint(stream);
        this.gridBandAlternatePaint = SerialUtilities.readPaint(stream);
    }
}
