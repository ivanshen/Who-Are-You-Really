package org.jfree.chart.renderer.xy;

import java.awt.Graphics2D;
import java.awt.geom.Rectangle2D;
import java.awt.geom.Rectangle2D.Double;
import java.io.Serializable;
import org.jfree.chart.axis.DateAxis;
import org.jfree.chart.axis.ValueAxis;
import org.jfree.chart.entity.EntityCollection;
import org.jfree.chart.plot.CrosshairState;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.plot.PlotRenderingInfo;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.util.ParamChecks;
import org.jfree.data.Range;
import org.jfree.data.xy.IntervalXYDataset;
import org.jfree.data.xy.XYDataset;
import org.jfree.ui.RectangleEdge;
import org.jfree.util.PublicCloneable;

public class ClusteredXYBarRenderer extends XYBarRenderer implements Cloneable, PublicCloneable, Serializable {
    private static final long serialVersionUID = 5864462149177133147L;
    private boolean centerBarAtStartValue;

    public ClusteredXYBarRenderer() {
        this(0.0d, false);
    }

    public ClusteredXYBarRenderer(double margin, boolean centerBarAtStartValue) {
        super(margin);
        this.centerBarAtStartValue = centerBarAtStartValue;
    }

    public int getPassCount() {
        return 2;
    }

    public Range findDomainBounds(XYDataset dataset) {
        if (dataset == null) {
            return null;
        }
        if (this.centerBarAtStartValue) {
            return findDomainBoundsWithOffset((IntervalXYDataset) dataset);
        }
        return super.findDomainBounds(dataset);
    }

    protected Range findDomainBoundsWithOffset(IntervalXYDataset dataset) {
        ParamChecks.nullNotPermitted(dataset, "dataset");
        double minimum = Double.POSITIVE_INFINITY;
        double maximum = Double.NEGATIVE_INFINITY;
        int seriesCount = dataset.getSeriesCount();
        for (int series = 0; series < seriesCount; series++) {
            int itemCount = dataset.getItemCount(series);
            for (int item = 0; item < itemCount; item++) {
                double lvalue = dataset.getStartXValue(series, item);
                double uvalue = dataset.getEndXValue(series, item);
                double offset = (uvalue - lvalue) / DateAxis.DEFAULT_AUTO_RANGE_MINIMUM_SIZE_IN_MILLISECONDS;
                uvalue -= offset;
                minimum = Math.min(minimum, lvalue - offset);
                maximum = Math.max(maximum, uvalue);
            }
        }
        if (minimum > maximum) {
            return null;
        }
        return new Range(minimum, maximum);
    }

    public void drawItem(Graphics2D g2, XYItemRendererState state, Rectangle2D dataArea, PlotRenderingInfo info, XYPlot plot, ValueAxis domainAxis, ValueAxis rangeAxis, XYDataset dataset, int series, int item, CrosshairState crosshairState, int pass) {
        double y0;
        double y1;
        IntervalXYDataset intervalDataset = (IntervalXYDataset) dataset;
        if (getUseYInterval()) {
            y0 = intervalDataset.getStartYValue(series, item);
            y1 = intervalDataset.getEndYValue(series, item);
        } else {
            y0 = getBase();
            y1 = intervalDataset.getYValue(series, item);
        }
        if (!Double.isNaN(y0) && !Double.isNaN(y1)) {
            Rectangle2D bar;
            RectangleEdge barBase;
            double yy0 = rangeAxis.valueToJava2D(y0, dataArea, plot.getRangeAxisEdge());
            double yy1 = rangeAxis.valueToJava2D(y1, dataArea, plot.getRangeAxisEdge());
            RectangleEdge xAxisLocation = plot.getDomainAxisEdge();
            double xx0 = domainAxis.valueToJava2D(intervalDataset.getStartXValue(series, item), dataArea, xAxisLocation);
            double intervalW = domainAxis.valueToJava2D(intervalDataset.getEndXValue(series, item), dataArea, xAxisLocation) - xx0;
            double baseX = xx0;
            if (this.centerBarAtStartValue) {
                baseX -= intervalW / DateAxis.DEFAULT_AUTO_RANGE_MINIMUM_SIZE_IN_MILLISECONDS;
            }
            if (getMargin() > 0.0d) {
                double cut = intervalW * getMargin();
                intervalW -= cut;
                baseX += cut / DateAxis.DEFAULT_AUTO_RANGE_MINIMUM_SIZE_IN_MILLISECONDS;
            }
            double intervalH = Math.abs(yy0 - yy1);
            PlotOrientation orientation = plot.getOrientation();
            double seriesBarWidth = intervalW / ((double) dataset.getSeriesCount());
            if (orientation == PlotOrientation.HORIZONTAL) {
                double barY0 = baseX + (((double) series) * seriesBarWidth);
                double barY1 = barY0 + seriesBarWidth;
                bar = new Double(Math.min(yy0, yy1), Math.min(barY0, barY1), intervalH, Math.abs(barY1 - barY0));
            } else if (orientation == PlotOrientation.VERTICAL) {
                double barX0 = baseX + (((double) series) * seriesBarWidth);
                double barX1 = barX0 + seriesBarWidth;
                bar = new Double(Math.min(barX0, barX1), Math.min(yy0, yy1), Math.abs(barX1 - barX0), intervalH);
            } else {
                throw new IllegalStateException();
            }
            boolean positive = y1 > 0.0d;
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
            if (pass == 0 && getShadowsVisible()) {
                getBarPainter().paintBarShadow(g2, this, series, item, bar, barBase, !getUseYInterval());
            }
            if (pass == 1) {
                getBarPainter().paintBar(g2, this, series, item, bar, barBase);
                if (isItemLabelVisible(series, item)) {
                    drawItemLabel(g2, dataset, series, item, plot, getItemLabelGenerator(series, item), bar, y1 < 0.0d);
                }
                if (info != null) {
                    EntityCollection entities = info.getOwner().getEntityCollection();
                    if (entities != null) {
                        addEntity(entities, bar, dataset, series, item, bar.getCenterX(), bar.getCenterY());
                    }
                }
            }
        }
    }

    public boolean equals(Object obj) {
        if (obj == this) {
            return true;
        }
        if (!(obj instanceof ClusteredXYBarRenderer)) {
            return false;
        }
        if (this.centerBarAtStartValue == ((ClusteredXYBarRenderer) obj).centerBarAtStartValue) {
            return super.equals(obj);
        }
        return false;
    }

    public Object clone() throws CloneNotSupportedException {
        return super.clone();
    }
}
