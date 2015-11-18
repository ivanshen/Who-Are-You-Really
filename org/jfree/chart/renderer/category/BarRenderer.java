package org.jfree.chart.renderer.category;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Paint;
import java.awt.Shape;
import java.awt.geom.Line2D.Float;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.awt.geom.Rectangle2D.Double;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import org.jfree.chart.LegendItem;
import org.jfree.chart.axis.CategoryAxis;
import org.jfree.chart.axis.DateAxis;
import org.jfree.chart.axis.ValueAxis;
import org.jfree.chart.entity.EntityCollection;
import org.jfree.chart.labels.CategoryItemLabelGenerator;
import org.jfree.chart.labels.ItemLabelAnchor;
import org.jfree.chart.labels.ItemLabelPosition;
import org.jfree.chart.plot.CategoryPlot;
import org.jfree.chart.plot.Plot;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.plot.PlotRenderingInfo;
import org.jfree.chart.renderer.xy.XYLine3DRenderer;
import org.jfree.chart.util.ParamChecks;
import org.jfree.data.Range;
import org.jfree.data.category.CategoryDataset;
import org.jfree.data.xy.NormalizedMatrixSeries;
import org.jfree.io.SerialUtilities;
import org.jfree.text.TextUtilities;
import org.jfree.ui.GradientPaintTransformer;
import org.jfree.ui.RectangleEdge;
import org.jfree.ui.StandardGradientPaintTransformer;
import org.jfree.util.ObjectUtilities;
import org.jfree.util.PaintUtilities;
import org.jfree.util.PublicCloneable;

public class BarRenderer extends AbstractCategoryItemRenderer implements Cloneable, PublicCloneable, Serializable {
    public static final double BAR_OUTLINE_WIDTH_THRESHOLD = 3.0d;
    public static final double DEFAULT_ITEM_MARGIN = 0.2d;
    private static BarPainter defaultBarPainter = null;
    private static boolean defaultShadowsVisible = false;
    private static final long serialVersionUID = 6000649414965887481L;
    private BarPainter barPainter;
    private double base;
    private boolean drawBarOutline;
    private GradientPaintTransformer gradientPaintTransformer;
    private boolean includeBaseInRange;
    private double itemMargin;
    private double lowerClip;
    private double maximumBarWidth;
    private double minimumBarLength;
    private ItemLabelPosition negativeItemLabelPositionFallback;
    private ItemLabelPosition positiveItemLabelPositionFallback;
    private transient Paint shadowPaint;
    private double shadowXOffset;
    private double shadowYOffset;
    private boolean shadowsVisible;
    private double upperClip;

    static {
        defaultBarPainter = new GradientBarPainter();
        defaultShadowsVisible = true;
    }

    public static BarPainter getDefaultBarPainter() {
        return defaultBarPainter;
    }

    public static void setDefaultBarPainter(BarPainter painter) {
        ParamChecks.nullNotPermitted(painter, "painter");
        defaultBarPainter = painter;
    }

    public static boolean getDefaultShadowsVisible() {
        return defaultShadowsVisible;
    }

    public static void setDefaultShadowsVisible(boolean visible) {
        defaultShadowsVisible = visible;
    }

    public BarRenderer() {
        this.base = 0.0d;
        this.includeBaseInRange = true;
        this.itemMargin = DEFAULT_ITEM_MARGIN;
        this.drawBarOutline = false;
        this.maximumBarWidth = NormalizedMatrixSeries.DEFAULT_SCALE_FACTOR;
        this.positiveItemLabelPositionFallback = null;
        this.negativeItemLabelPositionFallback = null;
        this.gradientPaintTransformer = new StandardGradientPaintTransformer();
        this.minimumBarLength = 0.0d;
        setBaseLegendShape(new Double(-4.0d, -4.0d, XYLine3DRenderer.DEFAULT_Y_OFFSET, XYLine3DRenderer.DEFAULT_Y_OFFSET));
        this.barPainter = getDefaultBarPainter();
        this.shadowsVisible = getDefaultShadowsVisible();
        this.shadowPaint = Color.gray;
        this.shadowXOffset = 4.0d;
        this.shadowYOffset = 4.0d;
    }

    public double getBase() {
        return this.base;
    }

    public void setBase(double base) {
        this.base = base;
        fireChangeEvent();
    }

    public double getItemMargin() {
        return this.itemMargin;
    }

    public void setItemMargin(double percent) {
        this.itemMargin = percent;
        fireChangeEvent();
    }

    public boolean isDrawBarOutline() {
        return this.drawBarOutline;
    }

    public void setDrawBarOutline(boolean draw) {
        this.drawBarOutline = draw;
        fireChangeEvent();
    }

    public double getMaximumBarWidth() {
        return this.maximumBarWidth;
    }

    public void setMaximumBarWidth(double percent) {
        this.maximumBarWidth = percent;
        fireChangeEvent();
    }

    public double getMinimumBarLength() {
        return this.minimumBarLength;
    }

    public void setMinimumBarLength(double min) {
        if (min < 0.0d) {
            throw new IllegalArgumentException("Requires 'min' >= 0.0");
        }
        this.minimumBarLength = min;
        fireChangeEvent();
    }

    public GradientPaintTransformer getGradientPaintTransformer() {
        return this.gradientPaintTransformer;
    }

    public void setGradientPaintTransformer(GradientPaintTransformer transformer) {
        this.gradientPaintTransformer = transformer;
        fireChangeEvent();
    }

    public ItemLabelPosition getPositiveItemLabelPositionFallback() {
        return this.positiveItemLabelPositionFallback;
    }

    public void setPositiveItemLabelPositionFallback(ItemLabelPosition position) {
        this.positiveItemLabelPositionFallback = position;
        fireChangeEvent();
    }

    public ItemLabelPosition getNegativeItemLabelPositionFallback() {
        return this.negativeItemLabelPositionFallback;
    }

    public void setNegativeItemLabelPositionFallback(ItemLabelPosition position) {
        this.negativeItemLabelPositionFallback = position;
        fireChangeEvent();
    }

    public boolean getIncludeBaseInRange() {
        return this.includeBaseInRange;
    }

    public void setIncludeBaseInRange(boolean include) {
        if (this.includeBaseInRange != include) {
            this.includeBaseInRange = include;
            fireChangeEvent();
        }
    }

    public BarPainter getBarPainter() {
        return this.barPainter;
    }

    public void setBarPainter(BarPainter painter) {
        ParamChecks.nullNotPermitted(painter, "painter");
        this.barPainter = painter;
        fireChangeEvent();
    }

    public boolean getShadowsVisible() {
        return this.shadowsVisible;
    }

    public void setShadowVisible(boolean visible) {
        this.shadowsVisible = visible;
        fireChangeEvent();
    }

    public Paint getShadowPaint() {
        return this.shadowPaint;
    }

    public void setShadowPaint(Paint paint) {
        ParamChecks.nullNotPermitted(paint, "paint");
        this.shadowPaint = paint;
        fireChangeEvent();
    }

    public double getShadowXOffset() {
        return this.shadowXOffset;
    }

    public void setShadowXOffset(double offset) {
        this.shadowXOffset = offset;
        fireChangeEvent();
    }

    public double getShadowYOffset() {
        return this.shadowYOffset;
    }

    public void setShadowYOffset(double offset) {
        this.shadowYOffset = offset;
        fireChangeEvent();
    }

    public double getLowerClip() {
        return this.lowerClip;
    }

    public double getUpperClip() {
        return this.upperClip;
    }

    public CategoryItemRendererState initialise(Graphics2D g2, Rectangle2D dataArea, CategoryPlot plot, int rendererIndex, PlotRenderingInfo info) {
        CategoryItemRendererState state = super.initialise(g2, dataArea, plot, rendererIndex, info);
        ValueAxis rangeAxis = plot.getRangeAxisForDataset(rendererIndex);
        this.lowerClip = rangeAxis.getRange().getLowerBound();
        this.upperClip = rangeAxis.getRange().getUpperBound();
        calculateBarWidth(plot, dataArea, rendererIndex, state);
        return state;
    }

    protected void calculateBarWidth(CategoryPlot plot, Rectangle2D dataArea, int rendererIndex, CategoryItemRendererState state) {
        CategoryAxis domainAxis = getDomainAxis(plot, rendererIndex);
        CategoryDataset dataset = plot.getDataset(rendererIndex);
        if (dataset != null) {
            int columns = dataset.getColumnCount();
            int rows = state.getVisibleSeriesCount() >= 0 ? state.getVisibleSeriesCount() : dataset.getRowCount();
            double space = 0.0d;
            PlotOrientation orientation = plot.getOrientation();
            if (orientation == PlotOrientation.HORIZONTAL) {
                space = dataArea.getHeight();
            } else if (orientation == PlotOrientation.VERTICAL) {
                space = dataArea.getWidth();
            }
            double maxWidth = space * getMaximumBarWidth();
            double categoryMargin = 0.0d;
            double currentItemMargin = 0.0d;
            if (columns > 1) {
                categoryMargin = domainAxis.getCategoryMargin();
            }
            if (rows > 1) {
                currentItemMargin = getItemMargin();
            }
            double used = space * ((((NormalizedMatrixSeries.DEFAULT_SCALE_FACTOR - domainAxis.getLowerMargin()) - domainAxis.getUpperMargin()) - categoryMargin) - currentItemMargin);
            if (rows * columns > 0) {
                state.setBarWidth(Math.min(used / ((double) (rows * columns)), maxWidth));
            } else {
                state.setBarWidth(Math.min(used, maxWidth));
            }
        }
    }

    protected double calculateBarW0(CategoryPlot plot, PlotOrientation orientation, Rectangle2D dataArea, CategoryAxis domainAxis, CategoryItemRendererState state, int row, int column) {
        double space;
        if (orientation == PlotOrientation.HORIZONTAL) {
            space = dataArea.getHeight();
        } else {
            space = dataArea.getWidth();
        }
        double barW0 = domainAxis.getCategoryStart(column, getColumnCount(), dataArea, plot.getDomainAxisEdge());
        int seriesCount = state.getVisibleSeriesCount() >= 0 ? state.getVisibleSeriesCount() : getRowCount();
        int categoryCount = getColumnCount();
        if (seriesCount > 1) {
            double seriesGap = (getItemMargin() * space) / ((double) ((seriesCount - 1) * categoryCount));
            double seriesW = calculateSeriesWidth(space, domainAxis, categoryCount, seriesCount);
            return (((((double) row) * (seriesW + seriesGap)) + barW0) + (seriesW / DateAxis.DEFAULT_AUTO_RANGE_MINIMUM_SIZE_IN_MILLISECONDS)) - (state.getBarWidth() / DateAxis.DEFAULT_AUTO_RANGE_MINIMUM_SIZE_IN_MILLISECONDS);
        }
        return domainAxis.getCategoryMiddle(column, getColumnCount(), dataArea, plot.getDomainAxisEdge()) - (state.getBarWidth() / DateAxis.DEFAULT_AUTO_RANGE_MINIMUM_SIZE_IN_MILLISECONDS);
    }

    protected double[] calculateBarL0L1(double value) {
        double lclip = getLowerClip();
        double uclip = getUpperClip();
        double barLow = Math.min(this.base, value);
        double barHigh = Math.max(this.base, value);
        if (barHigh < lclip || barLow > uclip) {
            return null;
        }
        barLow = Math.max(barLow, lclip);
        barHigh = Math.min(barHigh, uclip);
        return new double[]{barLow, barHigh};
    }

    public Range findRangeBounds(CategoryDataset dataset, boolean includeInterval) {
        if (dataset == null) {
            return null;
        }
        Range result = super.findRangeBounds(dataset, includeInterval);
        if (result == null || !this.includeBaseInRange) {
            return result;
        }
        return Range.expandToInclude(result, this.base);
    }

    public LegendItem getLegendItem(int datasetIndex, int series) {
        CategoryPlot cp = getPlot();
        if (cp == null) {
            return null;
        }
        if (!isSeriesVisible(series) || !isSeriesVisibleInLegend(series)) {
            return null;
        }
        CategoryDataset dataset = cp.getDataset(datasetIndex);
        String label = getLegendItemLabelGenerator().generateLabel(dataset, series);
        String description = label;
        String toolTipText = null;
        if (getLegendItemToolTipGenerator() != null) {
            toolTipText = getLegendItemToolTipGenerator().generateLabel(dataset, series);
        }
        String urlText = null;
        if (getLegendItemURLGenerator() != null) {
            urlText = getLegendItemURLGenerator().generateLabel(dataset, series);
        }
        LegendItem result = new LegendItem(label, description, toolTipText, urlText, true, lookupLegendShape(series), true, lookupSeriesPaint(series), isDrawBarOutline(), lookupSeriesOutlinePaint(series), lookupSeriesOutlineStroke(series), false, new Float(), new BasicStroke(Plot.DEFAULT_FOREGROUND_ALPHA), Color.black);
        result.setLabelFont(lookupLegendTextFont(series));
        Paint labelPaint = lookupLegendTextPaint(series);
        if (labelPaint != null) {
            result.setLabelPaint(labelPaint);
        }
        result.setDataset(dataset);
        result.setDatasetIndex(datasetIndex);
        result.setSeriesKey(dataset.getRowKey(series));
        result.setSeriesIndex(series);
        if (this.gradientPaintTransformer == null) {
            return result;
        }
        result.setFillPaintTransformer(this.gradientPaintTransformer);
        return result;
    }

    public void drawItem(Graphics2D g2, CategoryItemRendererState state, Rectangle2D dataArea, CategoryPlot plot, CategoryAxis domainAxis, ValueAxis rangeAxis, CategoryDataset dataset, int row, int column, int pass) {
        int visibleRow = state.getVisibleSeriesIndex(row);
        if (visibleRow >= 0) {
            Number dataValue = dataset.getValue(row, column);
            if (dataValue != null) {
                double value = dataValue.doubleValue();
                PlotOrientation orientation = plot.getOrientation();
                double barW0 = calculateBarW0(plot, orientation, dataArea, domainAxis, state, visibleRow, column);
                double[] barL0L1 = calculateBarL0L1(value);
                if (barL0L1 != null) {
                    RectangleEdge barBase;
                    Rectangle2D bar;
                    RectangleEdge edge = plot.getRangeAxisEdge();
                    double transL0 = rangeAxis.valueToJava2D(barL0L1[0], dataArea, edge);
                    double transL1 = rangeAxis.valueToJava2D(barL0L1[1], dataArea, edge);
                    boolean positive = value >= this.base;
                    boolean inverted = rangeAxis.isInverted();
                    double barL0 = Math.min(transL0, transL1);
                    double barLength = Math.abs(transL1 - transL0);
                    double barLengthAdj = 0.0d;
                    if (barLength > 0.0d && barLength < getMinimumBarLength()) {
                        barLengthAdj = getMinimumBarLength() - barLength;
                    }
                    double barL0Adj = 0.0d;
                    if (orientation == PlotOrientation.HORIZONTAL) {
                        if (!(positive && inverted) && (positive || inverted)) {
                            barBase = RectangleEdge.LEFT;
                        } else {
                            barL0Adj = barLengthAdj;
                            barBase = RectangleEdge.RIGHT;
                        }
                    } else if ((!positive || inverted) && (positive || !inverted)) {
                        barBase = RectangleEdge.TOP;
                    } else {
                        barL0Adj = barLengthAdj;
                        barBase = RectangleEdge.BOTTOM;
                    }
                    if (orientation == PlotOrientation.HORIZONTAL) {
                        bar = new Double(barL0 - barL0Adj, barW0, barLength + barLengthAdj, state.getBarWidth());
                    } else {
                        Double doubleR = new Double(barW0, barL0 - barL0Adj, state.getBarWidth(), barLength + barLengthAdj);
                    }
                    if (getShadowsVisible()) {
                        this.barPainter.paintBarShadow(g2, this, row, column, bar, barBase, true);
                    }
                    this.barPainter.paintBar(g2, this, row, column, bar, barBase);
                    CategoryItemLabelGenerator generator = getItemLabelGenerator(row, column);
                    if (generator != null && isItemLabelVisible(row, column)) {
                        drawItemLabel(g2, dataset, row, column, plot, generator, bar, value < 0.0d);
                    }
                    updateCrosshairValues(state.getCrosshairState(), dataset.getRowKey(row), dataset.getColumnKey(column), value, plot.indexOf(dataset), barW0, barL0, orientation);
                    EntityCollection entities = state.getEntityCollection();
                    if (entities != null) {
                        addItemEntity(entities, dataset, row, column, bar);
                    }
                }
            }
        }
    }

    protected double calculateSeriesWidth(double space, CategoryAxis axis, int categories, int series) {
        double factor = ((NormalizedMatrixSeries.DEFAULT_SCALE_FACTOR - getItemMargin()) - axis.getLowerMargin()) - axis.getUpperMargin();
        if (categories > 1) {
            factor -= axis.getCategoryMargin();
        }
        return (space * factor) / ((double) (categories * series));
    }

    protected void drawItemLabel(Graphics2D g2, CategoryDataset data, int row, int column, CategoryPlot plot, CategoryItemLabelGenerator generator, Rectangle2D bar, boolean negative) {
        String label = generator.generateLabel(data, row, column);
        if (label != null) {
            ItemLabelPosition position;
            g2.setFont(getItemLabelFont(row, column));
            g2.setPaint(getItemLabelPaint(row, column));
            if (negative) {
                position = getNegativeItemLabelPosition(row, column);
            } else {
                position = getPositiveItemLabelPosition(row, column);
            }
            Point2D anchorPoint = calculateLabelAnchorPoint(position.getItemLabelAnchor(), bar, plot.getOrientation());
            if (isInternalAnchor(position.getItemLabelAnchor())) {
                Shape bounds = TextUtilities.calculateRotatedStringBounds(label, g2, (float) anchorPoint.getX(), (float) anchorPoint.getY(), position.getTextAnchor(), position.getAngle(), position.getRotationAnchor());
                if (bounds != null) {
                    if (!bar.contains(bounds.getBounds2D())) {
                        if (negative) {
                            position = getNegativeItemLabelPositionFallback();
                        } else {
                            position = getPositiveItemLabelPositionFallback();
                        }
                        if (position != null) {
                            anchorPoint = calculateLabelAnchorPoint(position.getItemLabelAnchor(), bar, plot.getOrientation());
                        }
                    }
                }
            }
            if (position != null) {
                TextUtilities.drawRotatedString(label, g2, (float) anchorPoint.getX(), (float) anchorPoint.getY(), position.getTextAnchor(), position.getAngle(), position.getRotationAnchor());
            }
        }
    }

    private Point2D calculateLabelAnchorPoint(ItemLabelAnchor anchor, Rectangle2D bar, PlotOrientation orientation) {
        double offset = getItemLabelAnchorOffset();
        double x0 = bar.getX() - offset;
        double x1 = bar.getX();
        double x2 = bar.getX() + offset;
        double x3 = bar.getCenterX();
        double x4 = bar.getMaxX() - offset;
        double x5 = bar.getMaxX();
        double x6 = bar.getMaxX() + offset;
        double y0 = bar.getMaxY() + offset;
        double y1 = bar.getMaxY();
        double y2 = bar.getMaxY() - offset;
        double y3 = bar.getCenterY();
        double y4 = bar.getMinY() + offset;
        double y5 = bar.getMinY();
        double y6 = bar.getMinY() - offset;
        if (anchor == ItemLabelAnchor.CENTER) {
            return new Point2D.Double(x3, y3);
        }
        if (anchor == ItemLabelAnchor.INSIDE1) {
            return new Point2D.Double(x4, y4);
        }
        if (anchor == ItemLabelAnchor.INSIDE2) {
            return new Point2D.Double(x4, y4);
        }
        if (anchor == ItemLabelAnchor.INSIDE3) {
            return new Point2D.Double(x4, y3);
        }
        if (anchor == ItemLabelAnchor.INSIDE4) {
            return new Point2D.Double(x4, y2);
        }
        if (anchor == ItemLabelAnchor.INSIDE5) {
            return new Point2D.Double(x4, y2);
        }
        if (anchor == ItemLabelAnchor.INSIDE6) {
            return new Point2D.Double(x3, y2);
        }
        if (anchor == ItemLabelAnchor.INSIDE7) {
            return new Point2D.Double(x2, y2);
        }
        if (anchor == ItemLabelAnchor.INSIDE8) {
            return new Point2D.Double(x2, y2);
        }
        if (anchor == ItemLabelAnchor.INSIDE9) {
            return new Point2D.Double(x2, y3);
        }
        if (anchor == ItemLabelAnchor.INSIDE10) {
            return new Point2D.Double(x2, y4);
        }
        if (anchor == ItemLabelAnchor.INSIDE11) {
            return new Point2D.Double(x2, y4);
        }
        if (anchor == ItemLabelAnchor.INSIDE12) {
            return new Point2D.Double(x3, y4);
        }
        if (anchor == ItemLabelAnchor.OUTSIDE1) {
            return new Point2D.Double(x5, y6);
        }
        if (anchor == ItemLabelAnchor.OUTSIDE2) {
            return new Point2D.Double(x6, y5);
        }
        if (anchor == ItemLabelAnchor.OUTSIDE3) {
            return new Point2D.Double(x6, y3);
        }
        if (anchor == ItemLabelAnchor.OUTSIDE4) {
            return new Point2D.Double(x6, y1);
        }
        if (anchor == ItemLabelAnchor.OUTSIDE5) {
            return new Point2D.Double(x5, y0);
        }
        if (anchor == ItemLabelAnchor.OUTSIDE6) {
            return new Point2D.Double(x3, y0);
        }
        if (anchor == ItemLabelAnchor.OUTSIDE7) {
            return new Point2D.Double(x1, y0);
        }
        if (anchor == ItemLabelAnchor.OUTSIDE8) {
            return new Point2D.Double(x0, y1);
        }
        if (anchor == ItemLabelAnchor.OUTSIDE9) {
            return new Point2D.Double(x0, y3);
        }
        if (anchor == ItemLabelAnchor.OUTSIDE10) {
            return new Point2D.Double(x0, y5);
        }
        if (anchor == ItemLabelAnchor.OUTSIDE11) {
            return new Point2D.Double(x1, y6);
        }
        if (anchor == ItemLabelAnchor.OUTSIDE12) {
            return new Point2D.Double(x3, y6);
        }
        return null;
    }

    private boolean isInternalAnchor(ItemLabelAnchor anchor) {
        return anchor == ItemLabelAnchor.CENTER || anchor == ItemLabelAnchor.INSIDE1 || anchor == ItemLabelAnchor.INSIDE2 || anchor == ItemLabelAnchor.INSIDE3 || anchor == ItemLabelAnchor.INSIDE4 || anchor == ItemLabelAnchor.INSIDE5 || anchor == ItemLabelAnchor.INSIDE6 || anchor == ItemLabelAnchor.INSIDE7 || anchor == ItemLabelAnchor.INSIDE8 || anchor == ItemLabelAnchor.INSIDE9 || anchor == ItemLabelAnchor.INSIDE10 || anchor == ItemLabelAnchor.INSIDE11 || anchor == ItemLabelAnchor.INSIDE12;
    }

    public boolean equals(Object obj) {
        if (obj == this) {
            return true;
        }
        if (!(obj instanceof BarRenderer)) {
            return false;
        }
        BarRenderer that = (BarRenderer) obj;
        if (this.base == that.base && this.itemMargin == that.itemMargin && this.drawBarOutline == that.drawBarOutline && this.maximumBarWidth == that.maximumBarWidth && this.minimumBarLength == that.minimumBarLength && ObjectUtilities.equal(this.gradientPaintTransformer, that.gradientPaintTransformer) && ObjectUtilities.equal(this.positiveItemLabelPositionFallback, that.positiveItemLabelPositionFallback) && ObjectUtilities.equal(this.negativeItemLabelPositionFallback, that.negativeItemLabelPositionFallback) && this.barPainter.equals(that.barPainter) && this.shadowsVisible == that.shadowsVisible && PaintUtilities.equal(this.shadowPaint, that.shadowPaint) && this.shadowXOffset == that.shadowXOffset && this.shadowYOffset == that.shadowYOffset) {
            return super.equals(obj);
        }
        return false;
    }

    private void writeObject(ObjectOutputStream stream) throws IOException {
        stream.defaultWriteObject();
        SerialUtilities.writePaint(this.shadowPaint, stream);
    }

    private void readObject(ObjectInputStream stream) throws IOException, ClassNotFoundException {
        stream.defaultReadObject();
        this.shadowPaint = SerialUtilities.readPaint(stream);
    }
}
