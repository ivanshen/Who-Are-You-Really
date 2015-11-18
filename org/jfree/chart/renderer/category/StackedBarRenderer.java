package org.jfree.chart.renderer.category;

import java.awt.Graphics2D;
import java.awt.geom.Rectangle2D;
import java.awt.geom.Rectangle2D.Double;
import java.io.Serializable;
import org.jfree.chart.axis.CategoryAxis;
import org.jfree.chart.axis.DateAxis;
import org.jfree.chart.axis.ValueAxis;
import org.jfree.chart.entity.EntityCollection;
import org.jfree.chart.labels.CategoryItemLabelGenerator;
import org.jfree.chart.labels.ItemLabelAnchor;
import org.jfree.chart.labels.ItemLabelPosition;
import org.jfree.chart.plot.CategoryPlot;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.data.DataUtilities;
import org.jfree.data.Range;
import org.jfree.data.category.CategoryDataset;
import org.jfree.data.general.DatasetUtilities;
import org.jfree.data.xy.NormalizedMatrixSeries;
import org.jfree.ui.RectangleEdge;
import org.jfree.ui.TextAnchor;
import org.jfree.util.PublicCloneable;

public class StackedBarRenderer extends BarRenderer implements Cloneable, PublicCloneable, Serializable {
    static final long serialVersionUID = 6402943811500067531L;
    private boolean renderAsPercentages;

    public StackedBarRenderer() {
        this(false);
    }

    public StackedBarRenderer(boolean renderAsPercentages) {
        this.renderAsPercentages = renderAsPercentages;
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

    public Range findRangeBounds(CategoryDataset dataset) {
        if (dataset == null) {
            return null;
        }
        if (this.renderAsPercentages) {
            return new Range(0.0d, NormalizedMatrixSeries.DEFAULT_SCALE_FACTOR);
        }
        return DatasetUtilities.findStackedRangeBounds(dataset, getBase());
    }

    protected void calculateBarWidth(CategoryPlot plot, Rectangle2D dataArea, int rendererIndex, CategoryItemRendererState state) {
        CategoryAxis xAxis = plot.getDomainAxisForDataset(rendererIndex);
        CategoryDataset data = plot.getDataset(rendererIndex);
        if (data != null) {
            PlotOrientation orientation = plot.getOrientation();
            double space = 0.0d;
            if (orientation == PlotOrientation.HORIZONTAL) {
                space = dataArea.getHeight();
            } else if (orientation == PlotOrientation.VERTICAL) {
                space = dataArea.getWidth();
            }
            double maxWidth = space * getMaximumBarWidth();
            int columns = data.getColumnCount();
            double categoryMargin = 0.0d;
            if (columns > 1) {
                categoryMargin = xAxis.getCategoryMargin();
            }
            double used = space * (((NormalizedMatrixSeries.DEFAULT_SCALE_FACTOR - xAxis.getLowerMargin()) - xAxis.getUpperMargin()) - categoryMargin);
            if (columns > 0) {
                state.setBarWidth(Math.min(used / ((double) columns), maxWidth));
                return;
            }
            state.setBarWidth(Math.min(used, maxWidth));
        }
    }

    public void drawItem(Graphics2D g2, CategoryItemRendererState state, Rectangle2D dataArea, CategoryPlot plot, CategoryAxis domainAxis, ValueAxis rangeAxis, CategoryDataset dataset, int row, int column, int pass) {
        if (isSeriesVisible(row)) {
            Number dataValue = dataset.getValue(row, column);
            if (dataValue != null) {
                RectangleEdge barBase;
                double translatedBase;
                double translatedValue;
                Rectangle2D bar;
                double value = dataValue.doubleValue();
                double total = 0.0d;
                if (this.renderAsPercentages) {
                    total = DataUtilities.calculateColumnTotal(dataset, column, state.getVisibleSeriesArray());
                    value /= total;
                }
                PlotOrientation orientation = plot.getOrientation();
                double barW0 = domainAxis.getCategoryMiddle(column, getColumnCount(), dataArea, plot.getDomainAxisEdge()) - (state.getBarWidth() / DateAxis.DEFAULT_AUTO_RANGE_MINIMUM_SIZE_IN_MILLISECONDS);
                double positiveBase = getBase();
                double negativeBase = positiveBase;
                int i = 0;
                while (i < row) {
                    Number v = dataset.getValue(i, column);
                    if (v != null && isSeriesVisible(i)) {
                        double d = v.doubleValue();
                        if (this.renderAsPercentages) {
                            d /= total;
                        }
                        if (d > 0.0d) {
                            positiveBase += d;
                        } else {
                            negativeBase += d;
                        }
                    }
                    i++;
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
                RectangleEdge location = plot.getRangeAxisEdge();
                if (positive) {
                    translatedBase = rangeAxis.valueToJava2D(positiveBase, dataArea, location);
                    translatedValue = rangeAxis.valueToJava2D(positiveBase + value, dataArea, location);
                } else {
                    translatedBase = rangeAxis.valueToJava2D(negativeBase, dataArea, location);
                    translatedValue = rangeAxis.valueToJava2D(negativeBase + value, dataArea, location);
                }
                double barL0 = Math.min(translatedBase, translatedValue);
                double barLength = Math.max(Math.abs(translatedValue - translatedBase), getMinimumBarLength());
                if (orientation == PlotOrientation.HORIZONTAL) {
                    bar = new Double(barL0, barW0, barLength, state.getBarWidth());
                } else {
                    Double doubleR = new Double(barW0, barL0, state.getBarWidth(), barLength);
                }
                if (pass == 0) {
                    if (getShadowsVisible()) {
                        boolean pegToBase = (positive && positiveBase == getBase()) || (!positive && negativeBase == getBase());
                        getBarPainter().paintBarShadow(g2, this, row, column, bar, barBase, pegToBase);
                    }
                } else if (pass == 1) {
                    getBarPainter().paintBar(g2, this, row, column, bar, barBase);
                    EntityCollection entities = state.getEntityCollection();
                    if (entities != null) {
                        addItemEntity(entities, dataset, row, column, bar);
                    }
                } else if (pass == 2) {
                    CategoryItemLabelGenerator generator = getItemLabelGenerator(row, column);
                    if (generator != null && isItemLabelVisible(row, column)) {
                        drawItemLabel(g2, dataset, row, column, plot, generator, bar, value < 0.0d);
                    }
                }
            }
        }
    }

    public boolean equals(Object obj) {
        if (obj == this) {
            return true;
        }
        if (!(obj instanceof StackedBarRenderer)) {
            return false;
        }
        if (this.renderAsPercentages == ((StackedBarRenderer) obj).renderAsPercentages) {
            return super.equals(obj);
        }
        return false;
    }
}
