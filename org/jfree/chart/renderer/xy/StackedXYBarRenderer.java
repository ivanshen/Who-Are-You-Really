package org.jfree.chart.renderer.xy;

import java.awt.Graphics2D;
import java.awt.geom.Rectangle2D;
import java.awt.geom.Rectangle2D.Double;
import org.jfree.chart.axis.DateAxis;
import org.jfree.chart.axis.ValueAxis;
import org.jfree.chart.entity.EntityCollection;
import org.jfree.chart.labels.ItemLabelAnchor;
import org.jfree.chart.labels.ItemLabelPosition;
import org.jfree.chart.plot.CrosshairState;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.plot.PlotRenderingInfo;
import org.jfree.chart.plot.XYPlot;
import org.jfree.data.Range;
import org.jfree.data.general.DatasetUtilities;
import org.jfree.data.xy.IntervalXYDataset;
import org.jfree.data.xy.NormalizedMatrixSeries;
import org.jfree.data.xy.TableXYDataset;
import org.jfree.data.xy.XYDataset;
import org.jfree.ui.RectangleEdge;
import org.jfree.ui.TextAnchor;

public class StackedXYBarRenderer extends XYBarRenderer {
    private static final long serialVersionUID = -7049101055533436444L;
    private boolean renderAsPercentages;

    public StackedXYBarRenderer() {
        this(0.0d);
    }

    public StackedXYBarRenderer(double margin) {
        super(margin);
        this.renderAsPercentages = false;
        ItemLabelPosition p = new ItemLabelPosition(ItemLabelAnchor.CENTER, TextAnchor.CENTER);
        setBasePositiveItemLabelPosition(p);
        setBaseNegativeItemLabelPosition(p);
        setPositiveItemLabelPositionFallback(null);
        setNegativeItemLabelPositionFallback(null);
    }

    public boolean getRenderAsPercentages() {
        return this.renderAsPercentages;
    }

    public void setRenderAsPercentages(boolean asPercentages) {
        this.renderAsPercentages = asPercentages;
        fireChangeEvent();
    }

    public int getPassCount() {
        return 3;
    }

    public XYItemRendererState initialise(Graphics2D g2, Rectangle2D dataArea, XYPlot plot, XYDataset data, PlotRenderingInfo info) {
        return new XYBarRendererState(info);
    }

    public Range findRangeBounds(XYDataset dataset) {
        if (dataset == null) {
            return null;
        }
        if (this.renderAsPercentages) {
            return new Range(0.0d, NormalizedMatrixSeries.DEFAULT_SCALE_FACTOR);
        }
        return DatasetUtilities.findStackedRangeBounds((TableXYDataset) dataset);
    }

    public void drawItem(Graphics2D g2, XYItemRendererState state, Rectangle2D dataArea, PlotRenderingInfo info, XYPlot plot, ValueAxis domainAxis, ValueAxis rangeAxis, XYDataset dataset, int series, int item, CrosshairState crosshairState, int pass) {
        if (!getItemVisible(series, item)) {
            return;
        }
        if ((dataset instanceof IntervalXYDataset) && (dataset instanceof TableXYDataset)) {
            IntervalXYDataset intervalDataset = (IntervalXYDataset) dataset;
            double value = intervalDataset.getYValue(series, item);
            if (!Double.isNaN(value)) {
                double translatedBase;
                double translatedValue;
                double total = 0.0d;
                if (this.renderAsPercentages) {
                    total = DatasetUtilities.calculateStackTotal((TableXYDataset) dataset, item);
                    value /= total;
                }
                double positiveBase = 0.0d;
                double negativeBase = 0.0d;
                int i = 0;
                while (i < series) {
                    double v = dataset.getYValue(i, item);
                    if (!Double.isNaN(v) && isSeriesVisible(i)) {
                        if (this.renderAsPercentages) {
                            v /= total;
                        }
                        if (v > 0.0d) {
                            positiveBase += v;
                        } else {
                            negativeBase += v;
                        }
                    }
                    i++;
                }
                RectangleEdge edgeR = plot.getRangeAxisEdge();
                if (value > 0.0d) {
                    translatedBase = rangeAxis.valueToJava2D(positiveBase, dataArea, edgeR);
                    translatedValue = rangeAxis.valueToJava2D(positiveBase + value, dataArea, edgeR);
                } else {
                    translatedBase = rangeAxis.valueToJava2D(negativeBase, dataArea, edgeR);
                    translatedValue = rangeAxis.valueToJava2D(negativeBase + value, dataArea, edgeR);
                }
                RectangleEdge edgeD = plot.getDomainAxisEdge();
                double startX = intervalDataset.getStartXValue(series, item);
                if (!Double.isNaN(startX)) {
                    double translatedStartX = domainAxis.valueToJava2D(startX, dataArea, edgeD);
                    double endX = intervalDataset.getEndXValue(series, item);
                    if (!Double.isNaN(endX)) {
                        Rectangle2D bar;
                        RectangleEdge barBase;
                        double translatedEndX = domainAxis.valueToJava2D(endX, dataArea, edgeD);
                        double translatedWidth = Math.max(NormalizedMatrixSeries.DEFAULT_SCALE_FACTOR, Math.abs(translatedEndX - translatedStartX));
                        double translatedHeight = Math.abs(translatedValue - translatedBase);
                        if (getMargin() > 0.0d) {
                            double cut = translatedWidth * getMargin();
                            translatedWidth -= cut;
                            translatedStartX += cut / DateAxis.DEFAULT_AUTO_RANGE_MINIMUM_SIZE_IN_MILLISECONDS;
                        }
                        PlotOrientation orientation = plot.getOrientation();
                        if (orientation == PlotOrientation.HORIZONTAL) {
                            bar = new Double(Math.min(translatedBase, translatedValue), Math.min(translatedEndX, translatedStartX), translatedHeight, translatedWidth);
                        } else if (orientation == PlotOrientation.VERTICAL) {
                            Double doubleR = new Double(Math.min(translatedStartX, translatedEndX), Math.min(translatedBase, translatedValue), translatedWidth, translatedHeight);
                        } else {
                            throw new IllegalStateException();
                        }
                        boolean positive = value > 0.0d;
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
                        if (pass == 0) {
                            if (getShadowsVisible()) {
                                getBarPainter().paintBarShadow(g2, this, series, item, bar, barBase, false);
                                return;
                            }
                            return;
                        } else if (pass == 1) {
                            getBarPainter().paintBar(g2, this, series, item, bar, barBase);
                            if (info != null) {
                                EntityCollection entities = info.getOwner().getEntityCollection();
                                if (entities != null) {
                                    addEntity(entities, bar, dataset, series, item, bar.getCenterX(), bar.getCenterY());
                                    return;
                                }
                                return;
                            }
                            return;
                        } else if (pass == 2 && isItemLabelVisible(series, item)) {
                            drawItemLabel(g2, dataset, series, item, plot, getItemLabelGenerator(series, item), bar, value < 0.0d);
                            return;
                        } else {
                            return;
                        }
                    }
                    return;
                }
                return;
            }
            return;
        }
        String message = "dataset (type " + dataset.getClass().getName() + ") has wrong type:";
        boolean and = false;
        if (!IntervalXYDataset.class.isAssignableFrom(dataset.getClass())) {
            message = message + " it is no IntervalXYDataset";
            and = true;
        }
        if (!TableXYDataset.class.isAssignableFrom(dataset.getClass())) {
            if (and) {
                message = message + " and";
            }
            message = message + " it is no TableXYDataset";
        }
        throw new IllegalArgumentException(message);
    }

    public boolean equals(Object obj) {
        if (obj == this) {
            return true;
        }
        if (!(obj instanceof StackedXYBarRenderer)) {
            return false;
        }
        if (this.renderAsPercentages == ((StackedXYBarRenderer) obj).renderAsPercentages) {
            return super.equals(obj);
        }
        return false;
    }

    public int hashCode() {
        return (super.hashCode() * 37) + (this.renderAsPercentages ? 1 : 0);
    }
}
