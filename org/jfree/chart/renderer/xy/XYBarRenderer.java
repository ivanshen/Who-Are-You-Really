package org.jfree.chart.renderer.xy;

import java.awt.Graphics2D;
import java.awt.Paint;
import java.awt.Shape;
import java.awt.Stroke;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.awt.geom.Rectangle2D.Double;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import org.jfree.chart.LegendItem;
import org.jfree.chart.annotations.XYPointerAnnotation;
import org.jfree.chart.axis.DateAxis;
import org.jfree.chart.axis.ValueAxis;
import org.jfree.chart.entity.EntityCollection;
import org.jfree.chart.labels.ItemLabelAnchor;
import org.jfree.chart.labels.ItemLabelPosition;
import org.jfree.chart.labels.XYItemLabelGenerator;
import org.jfree.chart.plot.CrosshairState;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.plot.PlotRenderingInfo;
import org.jfree.chart.plot.SpiderWebPlot;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.util.ParamChecks;
import org.jfree.data.Range;
import org.jfree.data.xy.IntervalXYDataset;
import org.jfree.data.xy.NormalizedMatrixSeries;
import org.jfree.data.xy.XYDataset;
import org.jfree.io.SerialUtilities;
import org.jfree.text.TextUtilities;
import org.jfree.ui.GradientPaintTransformer;
import org.jfree.ui.RectangleEdge;
import org.jfree.ui.StandardGradientPaintTransformer;
import org.jfree.util.ObjectUtilities;
import org.jfree.util.PublicCloneable;
import org.jfree.util.ShapeUtilities;

public class XYBarRenderer extends AbstractXYItemRenderer implements XYItemRenderer, Cloneable, PublicCloneable, Serializable {
    private static XYBarPainter defaultBarPainter = null;
    private static boolean defaultShadowsVisible = false;
    private static final long serialVersionUID = 770559577251370036L;
    private double barAlignmentFactor;
    private XYBarPainter barPainter;
    private double base;
    private boolean drawBarOutline;
    private GradientPaintTransformer gradientPaintTransformer;
    private transient Shape legendBar;
    private double margin;
    private ItemLabelPosition negativeItemLabelPositionFallback;
    private ItemLabelPosition positiveItemLabelPositionFallback;
    private double shadowXOffset;
    private double shadowYOffset;
    private boolean shadowsVisible;
    private boolean useYInterval;

    protected class XYBarRendererState extends XYItemRendererState {
        private double g2Base;

        public XYBarRendererState(PlotRenderingInfo info) {
            super(info);
        }

        public double getG2Base() {
            return this.g2Base;
        }

        public void setG2Base(double value) {
            this.g2Base = value;
        }
    }

    static {
        defaultBarPainter = new GradientXYBarPainter();
        defaultShadowsVisible = true;
    }

    public static XYBarPainter getDefaultBarPainter() {
        return defaultBarPainter;
    }

    public static void setDefaultBarPainter(XYBarPainter painter) {
        ParamChecks.nullNotPermitted(painter, "painter");
        defaultBarPainter = painter;
    }

    public static boolean getDefaultShadowsVisible() {
        return defaultShadowsVisible;
    }

    public static void setDefaultShadowsVisible(boolean visible) {
        defaultShadowsVisible = visible;
    }

    public XYBarRenderer() {
        this(0.0d);
    }

    public XYBarRenderer(double margin) {
        this.margin = margin;
        this.base = 0.0d;
        this.useYInterval = false;
        this.gradientPaintTransformer = new StandardGradientPaintTransformer();
        this.drawBarOutline = false;
        this.legendBar = new Double(-3.0d, -5.0d, 6.0d, XYPointerAnnotation.DEFAULT_TIP_RADIUS);
        this.barPainter = getDefaultBarPainter();
        this.shadowsVisible = getDefaultShadowsVisible();
        this.shadowXOffset = 4.0d;
        this.shadowYOffset = 4.0d;
        this.barAlignmentFactor = SpiderWebPlot.DEFAULT_MAX_VALUE;
    }

    public double getBase() {
        return this.base;
    }

    public void setBase(double base) {
        this.base = base;
        fireChangeEvent();
    }

    public boolean getUseYInterval() {
        return this.useYInterval;
    }

    public void setUseYInterval(boolean use) {
        if (this.useYInterval != use) {
            this.useYInterval = use;
            fireChangeEvent();
        }
    }

    public double getMargin() {
        return this.margin;
    }

    public void setMargin(double margin) {
        this.margin = margin;
        fireChangeEvent();
    }

    public boolean isDrawBarOutline() {
        return this.drawBarOutline;
    }

    public void setDrawBarOutline(boolean draw) {
        this.drawBarOutline = draw;
        fireChangeEvent();
    }

    public GradientPaintTransformer getGradientPaintTransformer() {
        return this.gradientPaintTransformer;
    }

    public void setGradientPaintTransformer(GradientPaintTransformer transformer) {
        this.gradientPaintTransformer = transformer;
        fireChangeEvent();
    }

    public Shape getLegendBar() {
        return this.legendBar;
    }

    public void setLegendBar(Shape bar) {
        ParamChecks.nullNotPermitted(bar, "bar");
        this.legendBar = bar;
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

    public XYBarPainter getBarPainter() {
        return this.barPainter;
    }

    public void setBarPainter(XYBarPainter painter) {
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

    public double getBarAlignmentFactor() {
        return this.barAlignmentFactor;
    }

    public void setBarAlignmentFactor(double factor) {
        this.barAlignmentFactor = factor;
        fireChangeEvent();
    }

    public XYItemRendererState initialise(Graphics2D g2, Rectangle2D dataArea, XYPlot plot, XYDataset dataset, PlotRenderingInfo info) {
        XYBarRendererState state = new XYBarRendererState(info);
        state.setG2Base(plot.getRangeAxisForDataset(plot.indexOf(dataset)).valueToJava2D(this.base, dataArea, plot.getRangeAxisEdge()));
        return state;
    }

    public LegendItem getLegendItem(int datasetIndex, int series) {
        XYPlot xyplot = getPlot();
        if (xyplot == null) {
            return null;
        }
        XYDataset dataset = xyplot.getDataset(datasetIndex);
        if (dataset == null) {
            return null;
        }
        LegendItem result;
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
        Shape shape = this.legendBar;
        Paint paint = lookupSeriesPaint(series);
        Paint outlinePaint = lookupSeriesOutlinePaint(series);
        Stroke outlineStroke = lookupSeriesOutlineStroke(series);
        if (this.drawBarOutline) {
            result = new LegendItem(label, description, toolTipText, urlText, shape, paint, outlineStroke, outlinePaint);
        } else {
            result = new LegendItem(label, description, toolTipText, urlText, shape, paint);
        }
        result.setLabelFont(lookupLegendTextFont(series));
        Paint labelPaint = lookupLegendTextPaint(series);
        if (labelPaint != null) {
            result.setLabelPaint(labelPaint);
        }
        result.setDataset(dataset);
        result.setDatasetIndex(datasetIndex);
        result.setSeriesKey(dataset.getSeriesKey(series));
        result.setSeriesIndex(series);
        if (getGradientPaintTransformer() == null) {
            return result;
        }
        result.setFillPaintTransformer(getGradientPaintTransformer());
        return result;
    }

    public void drawItem(Graphics2D g2, XYItemRendererState state, Rectangle2D dataArea, PlotRenderingInfo info, XYPlot plot, ValueAxis domainAxis, ValueAxis rangeAxis, XYDataset dataset, int series, int item, CrosshairState crosshairState, int pass) {
        if (getItemVisible(series, item)) {
            double value0;
            double value1;
            IntervalXYDataset intervalDataset = (IntervalXYDataset) dataset;
            if (this.useYInterval) {
                value0 = intervalDataset.getStartYValue(series, item);
                value1 = intervalDataset.getEndYValue(series, item);
            } else {
                value0 = this.base;
                value1 = intervalDataset.getYValue(series, item);
            }
            if (!Double.isNaN(value0) && !Double.isNaN(value1)) {
                if (value0 <= value1) {
                    if (!rangeAxis.getRange().intersects(value0, value1)) {
                        return;
                    }
                } else if (!rangeAxis.getRange().intersects(value1, value0)) {
                    return;
                }
                double translatedValue0 = rangeAxis.valueToJava2D(value0, dataArea, plot.getRangeAxisEdge());
                double translatedValue1 = rangeAxis.valueToJava2D(value1, dataArea, plot.getRangeAxisEdge());
                double bottom = Math.min(translatedValue0, translatedValue1);
                double top = Math.max(translatedValue0, translatedValue1);
                double startX = intervalDataset.getStartXValue(series, item);
                if (!Double.isNaN(startX)) {
                    double endX = intervalDataset.getEndXValue(series, item);
                    if (!Double.isNaN(endX)) {
                        RectangleEdge barBase;
                        if (startX <= endX) {
                            if (!domainAxis.getRange().intersects(startX, endX)) {
                                return;
                            }
                        } else if (!domainAxis.getRange().intersects(endX, startX)) {
                            return;
                        }
                        if (this.barAlignmentFactor >= 0.0d && this.barAlignmentFactor <= NormalizedMatrixSeries.DEFAULT_SCALE_FACTOR) {
                            double interval = endX - startX;
                            startX = intervalDataset.getXValue(series, item) - (this.barAlignmentFactor * interval);
                            endX = startX + interval;
                        }
                        RectangleEdge location = plot.getDomainAxisEdge();
                        double translatedStartX = domainAxis.valueToJava2D(startX, dataArea, location);
                        double translatedEndX = domainAxis.valueToJava2D(endX, dataArea, location);
                        double translatedWidth = Math.max(NormalizedMatrixSeries.DEFAULT_SCALE_FACTOR, Math.abs(translatedEndX - translatedStartX));
                        double left = Math.min(translatedStartX, translatedEndX);
                        if (getMargin() > 0.0d) {
                            double cut = translatedWidth * getMargin();
                            translatedWidth -= cut;
                            left += cut / DateAxis.DEFAULT_AUTO_RANGE_MINIMUM_SIZE_IN_MILLISECONDS;
                        }
                        Rectangle2D bar = null;
                        PlotOrientation orientation = plot.getOrientation();
                        if (orientation == PlotOrientation.HORIZONTAL) {
                            bottom = Math.max(bottom, dataArea.getMinX());
                            bar = new Double(bottom, left, Math.min(top, dataArea.getMaxX()) - bottom, translatedWidth);
                        } else if (orientation == PlotOrientation.VERTICAL) {
                            bottom = Math.max(bottom, dataArea.getMinY());
                            Double doubleR = new Double(left, bottom, translatedWidth, Math.min(top, dataArea.getMaxY()) - bottom);
                        }
                        boolean positive = value1 > 0.0d;
                        boolean inverted = rangeAxis.isInverted();
                        if (orientation == PlotOrientation.HORIZONTAL) {
                            if (!(positive && inverted) && (positive || inverted)) {
                                barBase = RectangleEdge.LEFT;
                            } else {
                                barBase = RectangleEdge.RIGHT;
                            }
                        } else if ((!positive || inverted) && (positive || !inverted)) {
                            barBase = RectangleEdge.TOP;
                        } else {
                            barBase = RectangleEdge.BOTTOM;
                        }
                        if (getShadowsVisible()) {
                            this.barPainter.paintBarShadow(g2, this, series, item, bar, barBase, !this.useYInterval);
                        }
                        this.barPainter.paintBar(g2, this, series, item, bar, barBase);
                        if (isItemLabelVisible(series, item)) {
                            drawItemLabel(g2, dataset, series, item, plot, getItemLabelGenerator(series, item), bar, value1 < 0.0d);
                        }
                        double x1 = (startX + endX) / DateAxis.DEFAULT_AUTO_RANGE_MINIMUM_SIZE_IN_MILLISECONDS;
                        double y1 = dataset.getYValue(series, item);
                        updateCrosshairValues(crosshairState, x1, y1, plot.getDomainAxisIndex(domainAxis), plot.getRangeAxisIndex(rangeAxis), domainAxis.valueToJava2D(x1, dataArea, location), rangeAxis.valueToJava2D(y1, dataArea, plot.getRangeAxisEdge()), plot.getOrientation());
                        EntityCollection entities = state.getEntityCollection();
                        if (entities != null) {
                            addEntity(entities, bar, dataset, series, item, 0.0d, 0.0d);
                        }
                    }
                }
            }
        }
    }

    protected void drawItemLabel(Graphics2D g2, XYDataset dataset, int series, int item, XYPlot plot, XYItemLabelGenerator generator, Rectangle2D bar, boolean negative) {
        if (generator != null) {
            String label = generator.generateLabel(dataset, series, item);
            if (label != null) {
                ItemLabelPosition position;
                g2.setFont(getItemLabelFont(series, item));
                g2.setPaint(getItemLabelPaint(series, item));
                if (negative) {
                    position = getNegativeItemLabelPosition(series, item);
                } else {
                    position = getPositiveItemLabelPosition(series, item);
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

    public Range findDomainBounds(XYDataset dataset) {
        return findDomainBounds(dataset, true);
    }

    public Range findRangeBounds(XYDataset dataset) {
        return findRangeBounds(dataset, this.useYInterval);
    }

    public Object clone() throws CloneNotSupportedException {
        XYBarRenderer result = (XYBarRenderer) super.clone();
        if (this.gradientPaintTransformer != null) {
            result.gradientPaintTransformer = (GradientPaintTransformer) ObjectUtilities.clone(this.gradientPaintTransformer);
        }
        result.legendBar = ShapeUtilities.clone(this.legendBar);
        return result;
    }

    public boolean equals(Object obj) {
        if (obj == this) {
            return true;
        }
        if (!(obj instanceof XYBarRenderer)) {
            return false;
        }
        XYBarRenderer that = (XYBarRenderer) obj;
        if (this.base == that.base && this.drawBarOutline == that.drawBarOutline && this.margin == that.margin && this.useYInterval == that.useYInterval && ObjectUtilities.equal(this.gradientPaintTransformer, that.gradientPaintTransformer) && ShapeUtilities.equal(this.legendBar, that.legendBar) && ObjectUtilities.equal(this.positiveItemLabelPositionFallback, that.positiveItemLabelPositionFallback) && ObjectUtilities.equal(this.negativeItemLabelPositionFallback, that.negativeItemLabelPositionFallback) && this.barPainter.equals(that.barPainter) && this.shadowsVisible == that.shadowsVisible && this.shadowXOffset == that.shadowXOffset && this.shadowYOffset == that.shadowYOffset && this.barAlignmentFactor == that.barAlignmentFactor) {
            return super.equals(obj);
        }
        return false;
    }

    private void readObject(ObjectInputStream stream) throws IOException, ClassNotFoundException {
        stream.defaultReadObject();
        this.legendBar = SerialUtilities.readShape(stream);
    }

    private void writeObject(ObjectOutputStream stream) throws IOException {
        stream.defaultWriteObject();
        SerialUtilities.writeShape(this.legendBar, stream);
    }
}
