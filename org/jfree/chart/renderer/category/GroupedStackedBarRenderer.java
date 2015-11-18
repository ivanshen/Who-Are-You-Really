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
import org.jfree.chart.plot.CategoryPlot;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.util.ParamChecks;
import org.jfree.data.KeyToGroupMap;
import org.jfree.data.Range;
import org.jfree.data.category.CategoryDataset;
import org.jfree.data.general.DatasetUtilities;
import org.jfree.data.xy.NormalizedMatrixSeries;
import org.jfree.ui.RectangleEdge;
import org.jfree.util.PublicCloneable;

public class GroupedStackedBarRenderer extends StackedBarRenderer implements Cloneable, PublicCloneable, Serializable {
    private static final long serialVersionUID = -2725921399005922939L;
    private KeyToGroupMap seriesToGroupMap;

    public GroupedStackedBarRenderer() {
        this.seriesToGroupMap = new KeyToGroupMap();
    }

    public void setSeriesToGroupMap(KeyToGroupMap map) {
        ParamChecks.nullNotPermitted(map, "map");
        this.seriesToGroupMap = map;
        fireChangeEvent();
    }

    public Range findRangeBounds(CategoryDataset dataset) {
        if (dataset == null) {
            return null;
        }
        return DatasetUtilities.findStackedRangeBounds(dataset, this.seriesToGroupMap);
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
            int groups = this.seriesToGroupMap.getGroupCount();
            int categories = data.getColumnCount();
            int columns = groups * categories;
            double categoryMargin = 0.0d;
            double itemMargin = 0.0d;
            if (categories > 1) {
                categoryMargin = xAxis.getCategoryMargin();
            }
            if (groups > 1) {
                itemMargin = getItemMargin();
            }
            double used = space * ((((NormalizedMatrixSeries.DEFAULT_SCALE_FACTOR - xAxis.getLowerMargin()) - xAxis.getUpperMargin()) - categoryMargin) - itemMargin);
            if (columns > 0) {
                state.setBarWidth(Math.min(used / ((double) columns), maxWidth));
                return;
            }
            state.setBarWidth(Math.min(used, maxWidth));
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
        int groupCount = this.seriesToGroupMap.getGroupCount();
        int groupIndex = this.seriesToGroupMap.getGroupIndex(this.seriesToGroupMap.getGroup(plot.getDataset(plot.getIndexOf(this)).getRowKey(row)));
        int categoryCount = getColumnCount();
        if (groupCount > 1) {
            double groupGap = (getItemMargin() * space) / ((double) ((groupCount - 1) * categoryCount));
            double groupW = calculateSeriesWidth(space, domainAxis, categoryCount, groupCount);
            return (((((double) groupIndex) * (groupW + groupGap)) + barW0) + (groupW / DateAxis.DEFAULT_AUTO_RANGE_MINIMUM_SIZE_IN_MILLISECONDS)) - (state.getBarWidth() / DateAxis.DEFAULT_AUTO_RANGE_MINIMUM_SIZE_IN_MILLISECONDS);
        }
        return domainAxis.getCategoryMiddle(column, getColumnCount(), dataArea, plot.getDomainAxisEdge()) - (state.getBarWidth() / DateAxis.DEFAULT_AUTO_RANGE_MINIMUM_SIZE_IN_MILLISECONDS);
    }

    public void drawItem(Graphics2D g2, CategoryItemRendererState state, Rectangle2D dataArea, CategoryPlot plot, CategoryAxis domainAxis, ValueAxis rangeAxis, CategoryDataset dataset, int row, int column, int pass) {
        Number dataValue = dataset.getValue(row, column);
        if (dataValue != null) {
            RectangleEdge barBase;
            double translatedBase;
            double translatedValue;
            Rectangle2D bar;
            double value = dataValue.doubleValue();
            Comparable group = this.seriesToGroupMap.getGroup(dataset.getRowKey(row));
            PlotOrientation orientation = plot.getOrientation();
            double barW0 = calculateBarW0(plot, orientation, dataArea, domainAxis, state, row, column);
            double positiveBase = 0.0d;
            double negativeBase = 0.0d;
            for (int i = 0; i < row; i++) {
                if (group.equals(this.seriesToGroupMap.getGroup(dataset.getRowKey(i)))) {
                    Number v = dataset.getValue(i, column);
                    if (v != null) {
                        double d = v.doubleValue();
                        if (d > 0.0d) {
                            positiveBase += d;
                        } else {
                            negativeBase += d;
                        }
                    }
                }
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
            if (value > 0.0d) {
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
            getBarPainter().paintBar(g2, this, row, column, bar, barBase);
            CategoryItemLabelGenerator generator = getItemLabelGenerator(row, column);
            if (generator != null && isItemLabelVisible(row, column)) {
                drawItemLabel(g2, dataset, row, column, plot, generator, bar, value < 0.0d);
            }
            if (state.getInfo() != null) {
                EntityCollection entities = state.getEntityCollection();
                if (entities != null) {
                    addItemEntity(entities, dataset, row, column, bar);
                }
            }
        }
    }

    public boolean equals(Object obj) {
        if (obj == this) {
            return true;
        }
        if (!(obj instanceof GroupedStackedBarRenderer)) {
            return false;
        }
        if (this.seriesToGroupMap.equals(((GroupedStackedBarRenderer) obj).seriesToGroupMap)) {
            return super.equals(obj);
        }
        return false;
    }
}
