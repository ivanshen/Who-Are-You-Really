package org.jfree.chart.renderer.category;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Paint;
import java.awt.Stroke;
import java.awt.geom.Line2D;
import java.awt.geom.Line2D.Double;
import java.awt.geom.Rectangle2D;
import java.awt.geom.Rectangle2D.Float;
import java.io.Serializable;
import org.jfree.chart.HashUtilities;
import org.jfree.chart.axis.Axis;
import org.jfree.chart.axis.CategoryAxis;
import org.jfree.chart.axis.DateAxis;
import org.jfree.chart.axis.ValueAxis;
import org.jfree.chart.entity.EntityCollection;
import org.jfree.chart.plot.CategoryPlot;
import org.jfree.chart.plot.MeterPlot;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.plot.PlotRenderingInfo;
import org.jfree.data.category.CategoryDataset;
import org.jfree.data.xy.NormalizedMatrixSeries;
import org.jfree.ui.RectangleEdge;
import org.jfree.util.PublicCloneable;

public class LevelRenderer extends AbstractCategoryItemRenderer implements Cloneable, PublicCloneable, Serializable {
    public static final double DEFAULT_ITEM_MARGIN = 0.2d;
    private static final long serialVersionUID = -8204856624355025117L;
    private double itemMargin;
    private double maxItemWidth;

    public LevelRenderer() {
        this.itemMargin = DEFAULT_ITEM_MARGIN;
        this.maxItemWidth = NormalizedMatrixSeries.DEFAULT_SCALE_FACTOR;
        setBaseLegendShape(new Float(-5.0f, -1.0f, MeterPlot.DEFAULT_CIRCLE_SIZE, Axis.DEFAULT_TICK_MARK_OUTSIDE_LENGTH));
        setBaseOutlinePaint(new Color(0, 0, 0, 0));
    }

    public double getItemMargin() {
        return this.itemMargin;
    }

    public void setItemMargin(double percent) {
        this.itemMargin = percent;
        fireChangeEvent();
    }

    public double getMaximumItemWidth() {
        return getMaxItemWidth();
    }

    public void setMaximumItemWidth(double percent) {
        setMaxItemWidth(percent);
    }

    public CategoryItemRendererState initialise(Graphics2D g2, Rectangle2D dataArea, CategoryPlot plot, int rendererIndex, PlotRenderingInfo info) {
        CategoryItemRendererState state = super.initialise(g2, dataArea, plot, rendererIndex, info);
        calculateItemWidth(plot, dataArea, rendererIndex, state);
        return state;
    }

    protected void calculateItemWidth(CategoryPlot plot, Rectangle2D dataArea, int rendererIndex, CategoryItemRendererState state) {
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
            double maxWidth = space * getMaximumItemWidth();
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
        int seriesCount = state.getVisibleSeriesCount();
        if (seriesCount < 0) {
            seriesCount = getRowCount();
        }
        int categoryCount = getColumnCount();
        if (seriesCount > 1) {
            double seriesGap = (getItemMargin() * space) / ((double) ((seriesCount - 1) * categoryCount));
            double seriesW = calculateSeriesWidth(space, domainAxis, categoryCount, seriesCount);
            return (((((double) row) * (seriesW + seriesGap)) + barW0) + (seriesW / DateAxis.DEFAULT_AUTO_RANGE_MINIMUM_SIZE_IN_MILLISECONDS)) - (state.getBarWidth() / DateAxis.DEFAULT_AUTO_RANGE_MINIMUM_SIZE_IN_MILLISECONDS);
        }
        return domainAxis.getCategoryMiddle(column, getColumnCount(), dataArea, plot.getDomainAxisEdge()) - (state.getBarWidth() / DateAxis.DEFAULT_AUTO_RANGE_MINIMUM_SIZE_IN_MILLISECONDS);
    }

    public void drawItem(Graphics2D g2, CategoryItemRendererState state, Rectangle2D dataArea, CategoryPlot plot, CategoryAxis domainAxis, ValueAxis rangeAxis, CategoryDataset dataset, int row, int column, int pass) {
        int visibleRow = state.getVisibleSeriesIndex(row);
        if (visibleRow >= 0) {
            Number dataValue = dataset.getValue(row, column);
            if (dataValue != null) {
                double x;
                double y;
                Line2D line;
                double value = dataValue.doubleValue();
                PlotOrientation orientation = plot.getOrientation();
                double barW0 = calculateBarW0(plot, orientation, dataArea, domainAxis, state, visibleRow, column);
                double barL = rangeAxis.valueToJava2D(value, dataArea, plot.getRangeAxisEdge());
                if (orientation == PlotOrientation.HORIZONTAL) {
                    x = barL;
                    y = barW0 + (state.getBarWidth() / DateAxis.DEFAULT_AUTO_RANGE_MINIMUM_SIZE_IN_MILLISECONDS);
                    line = new Double(barL, barW0, barL, barW0 + state.getBarWidth());
                } else {
                    double x2 = barW0 + (state.getBarWidth() / DateAxis.DEFAULT_AUTO_RANGE_MINIMUM_SIZE_IN_MILLISECONDS);
                    double y2 = barL;
                    Double doubleR = new Double(barW0, barL, barW0 + state.getBarWidth(), barL);
                    y = y2;
                    x = x2;
                }
                Stroke itemStroke = getItemStroke(row, column);
                Paint itemPaint = getItemPaint(row, column);
                g2.setStroke(itemStroke);
                g2.setPaint(itemPaint);
                g2.draw(line);
                if (getItemLabelGenerator(row, column) != null && isItemLabelVisible(row, column)) {
                    drawItemLabel(g2, orientation, dataset, row, column, x, y, value < 0.0d);
                }
                updateCrosshairValues(state.getCrosshairState(), dataset.getRowKey(row), dataset.getColumnKey(column), value, plot.indexOf(dataset), barW0, barL, orientation);
                EntityCollection entities = state.getEntityCollection();
                if (entities != null) {
                    addItemEntity(entities, dataset, row, column, line.getBounds());
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

    public double getItemMiddle(Comparable rowKey, Comparable columnKey, CategoryDataset dataset, CategoryAxis axis, Rectangle2D area, RectangleEdge edge) {
        return axis.getCategorySeriesMiddle(columnKey, rowKey, dataset, this.itemMargin, area, edge);
    }

    public boolean equals(Object obj) {
        if (obj == this) {
            return true;
        }
        if (!(obj instanceof LevelRenderer)) {
            return false;
        }
        LevelRenderer that = (LevelRenderer) obj;
        if (this.itemMargin == that.itemMargin && this.maxItemWidth == that.maxItemWidth) {
            return super.equals(obj);
        }
        return false;
    }

    public int hashCode() {
        return HashUtilities.hashCode(HashUtilities.hashCode(super.hashCode(), this.itemMargin), this.maxItemWidth);
    }

    public double getMaxItemWidth() {
        return this.maxItemWidth;
    }

    public void setMaxItemWidth(double percent) {
        this.maxItemWidth = percent;
        fireChangeEvent();
    }
}
