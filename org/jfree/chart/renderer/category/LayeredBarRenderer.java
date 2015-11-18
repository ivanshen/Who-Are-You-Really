package org.jfree.chart.renderer.category;

import java.awt.GradientPaint;
import java.awt.Graphics2D;
import java.awt.Paint;
import java.awt.Stroke;
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
import org.jfree.data.category.CategoryDataset;
import org.jfree.data.xy.NormalizedMatrixSeries;
import org.jfree.ui.GradientPaintTransformer;
import org.jfree.ui.RectangleEdge;
import org.jfree.util.ObjectList;

public class LayeredBarRenderer extends BarRenderer implements Serializable {
    private static final long serialVersionUID = -8716572894780469487L;
    protected ObjectList seriesBarWidthList;

    public LayeredBarRenderer() {
        this.seriesBarWidthList = new ObjectList();
    }

    public double getSeriesBarWidth(int series) {
        Number n = (Number) this.seriesBarWidthList.get(series);
        if (n != null) {
            return n.doubleValue();
        }
        return Double.NaN;
    }

    public void setSeriesBarWidth(int series, double width) {
        this.seriesBarWidthList.set(series, new Double(width));
    }

    protected void calculateBarWidth(CategoryPlot plot, Rectangle2D dataArea, int rendererIndex, CategoryItemRendererState state) {
        CategoryAxis domainAxis = getDomainAxis(plot, rendererIndex);
        CategoryDataset dataset = plot.getDataset(rendererIndex);
        if (dataset != null) {
            int columns = dataset.getColumnCount();
            int rows = dataset.getRowCount();
            double space = 0.0d;
            PlotOrientation orientation = plot.getOrientation();
            if (orientation == PlotOrientation.HORIZONTAL) {
                space = dataArea.getHeight();
            } else if (orientation == PlotOrientation.VERTICAL) {
                space = dataArea.getWidth();
            }
            double maxWidth = space * getMaximumBarWidth();
            double categoryMargin = 0.0d;
            if (columns > 1) {
                categoryMargin = domainAxis.getCategoryMargin();
            }
            double used = space * (((NormalizedMatrixSeries.DEFAULT_SCALE_FACTOR - domainAxis.getLowerMargin()) - domainAxis.getUpperMargin()) - categoryMargin);
            if (rows * columns > 0) {
                state.setBarWidth(Math.min(used / ((double) dataset.getColumnCount()), maxWidth));
                return;
            }
            state.setBarWidth(Math.min(used, maxWidth));
        }
    }

    public void drawItem(Graphics2D g2, CategoryItemRendererState state, Rectangle2D dataArea, CategoryPlot plot, CategoryAxis domainAxis, ValueAxis rangeAxis, CategoryDataset data, int row, int column, int pass) {
        PlotOrientation orientation = plot.getOrientation();
        if (orientation == PlotOrientation.HORIZONTAL) {
            drawHorizontalItem(g2, state, dataArea, plot, domainAxis, rangeAxis, data, row, column);
        } else if (orientation == PlotOrientation.VERTICAL) {
            drawVerticalItem(g2, state, dataArea, plot, domainAxis, rangeAxis, data, row, column);
        }
    }

    protected void drawHorizontalItem(Graphics2D g2, CategoryItemRendererState state, Rectangle2D dataArea, CategoryPlot plot, CategoryAxis domainAxis, ValueAxis rangeAxis, CategoryDataset dataset, int row, int column) {
        Number dataValue = dataset.getValue(row, column);
        if (dataValue != null) {
            double value = dataValue.doubleValue();
            double base = 0.0d;
            double lclip = getLowerClip();
            double uclip = getUpperClip();
            if (uclip <= 0.0d) {
                if (value < uclip) {
                    base = uclip;
                    if (value <= lclip) {
                        value = lclip;
                    }
                } else {
                    return;
                }
            } else if (lclip <= 0.0d) {
                if (value >= uclip) {
                    value = uclip;
                } else if (value <= lclip) {
                    value = lclip;
                }
            } else if (value > lclip) {
                base = lclip;
                if (value >= uclip) {
                    value = uclip;
                }
            } else {
                return;
            }
            RectangleEdge edge = plot.getRangeAxisEdge();
            double transX1 = rangeAxis.valueToJava2D(base, dataArea, edge);
            double transX2 = rangeAxis.valueToJava2D(value, dataArea, edge);
            double rectX = Math.min(transX1, transX2);
            double rectWidth = Math.abs(transX2 - transX1);
            double rectY = domainAxis.getCategoryMiddle(column, getColumnCount(), dataArea, plot.getDomainAxisEdge()) - (state.getBarWidth() / DateAxis.DEFAULT_AUTO_RANGE_MINIMUM_SIZE_IN_MILLISECONDS);
            int seriesCount = getRowCount();
            double shift = 0.0d;
            double widthFactor = NormalizedMatrixSeries.DEFAULT_SCALE_FACTOR;
            double seriesBarWidth = getSeriesBarWidth(row);
            if (!Double.isNaN(seriesBarWidth)) {
                widthFactor = seriesBarWidth;
            }
            double rectHeight = widthFactor * state.getBarWidth();
            rectY += ((NormalizedMatrixSeries.DEFAULT_SCALE_FACTOR - widthFactor) * state.getBarWidth()) / DateAxis.DEFAULT_AUTO_RANGE_MINIMUM_SIZE_IN_MILLISECONDS;
            if (seriesCount > 1) {
                shift = (LevelRenderer.DEFAULT_ITEM_MARGIN * rectHeight) / ((double) (seriesCount - 1));
            }
            Rectangle2D bar = new Double(rectX, (((double) ((seriesCount - 1) - row)) * shift) + rectY, rectWidth, rectHeight - ((((double) ((seriesCount - 1) - row)) * shift) * DateAxis.DEFAULT_AUTO_RANGE_MINIMUM_SIZE_IN_MILLISECONDS));
            Paint itemPaint = getItemPaint(row, column);
            GradientPaintTransformer t = getGradientPaintTransformer();
            if (t != null && (itemPaint instanceof GradientPaint)) {
                itemPaint = t.transform((GradientPaint) itemPaint, bar);
            }
            g2.setPaint(itemPaint);
            g2.fill(bar);
            if (isDrawBarOutline() && state.getBarWidth() > BarRenderer.BAR_OUTLINE_WIDTH_THRESHOLD) {
                Stroke stroke = getItemOutlineStroke(row, column);
                Paint paint = getItemOutlinePaint(row, column);
                if (!(stroke == null || paint == null)) {
                    g2.setStroke(stroke);
                    g2.setPaint(paint);
                    g2.draw(bar);
                }
            }
            CategoryItemLabelGenerator generator = getItemLabelGenerator(row, column);
            if (generator != null && isItemLabelVisible(row, column)) {
                drawItemLabel(g2, dataset, row, column, plot, generator, bar, transX1 > transX2);
            }
            EntityCollection entities = state.getEntityCollection();
            if (entities != null) {
                addItemEntity(entities, dataset, row, column, bar);
            }
        }
    }

    protected void drawVerticalItem(Graphics2D g2, CategoryItemRendererState state, Rectangle2D dataArea, CategoryPlot plot, CategoryAxis domainAxis, ValueAxis rangeAxis, CategoryDataset dataset, int row, int column) {
        Number dataValue = dataset.getValue(row, column);
        if (dataValue != null) {
            double rectX = domainAxis.getCategoryMiddle(column, getColumnCount(), dataArea, plot.getDomainAxisEdge()) - (state.getBarWidth() / DateAxis.DEFAULT_AUTO_RANGE_MINIMUM_SIZE_IN_MILLISECONDS);
            int seriesCount = getRowCount();
            double value = dataValue.doubleValue();
            double base = 0.0d;
            double lclip = getLowerClip();
            double uclip = getUpperClip();
            if (uclip <= 0.0d) {
                if (value < uclip) {
                    base = uclip;
                    if (value <= lclip) {
                        value = lclip;
                    }
                } else {
                    return;
                }
            } else if (lclip <= 0.0d) {
                if (value >= uclip) {
                    value = uclip;
                } else if (value <= lclip) {
                    value = lclip;
                }
            } else if (value > lclip) {
                base = getLowerClip();
                if (value >= uclip) {
                    value = uclip;
                }
            } else {
                return;
            }
            RectangleEdge edge = plot.getRangeAxisEdge();
            double transY1 = rangeAxis.valueToJava2D(base, dataArea, edge);
            double transY2 = rangeAxis.valueToJava2D(value, dataArea, edge);
            double rectY = Math.min(transY2, transY1);
            double rectHeight = Math.abs(transY2 - transY1);
            double shift = 0.0d;
            double widthFactor = NormalizedMatrixSeries.DEFAULT_SCALE_FACTOR;
            double seriesBarWidth = getSeriesBarWidth(row);
            if (!Double.isNaN(seriesBarWidth)) {
                widthFactor = seriesBarWidth;
            }
            double rectWidth = widthFactor * state.getBarWidth();
            rectX += ((NormalizedMatrixSeries.DEFAULT_SCALE_FACTOR - widthFactor) * state.getBarWidth()) / DateAxis.DEFAULT_AUTO_RANGE_MINIMUM_SIZE_IN_MILLISECONDS;
            if (seriesCount > 1) {
                shift = (LevelRenderer.DEFAULT_ITEM_MARGIN * rectWidth) / ((double) (seriesCount - 1));
            }
            Rectangle2D bar = new Double((((double) ((seriesCount - 1) - row)) * shift) + rectX, rectY, rectWidth - ((((double) ((seriesCount - 1) - row)) * shift) * DateAxis.DEFAULT_AUTO_RANGE_MINIMUM_SIZE_IN_MILLISECONDS), rectHeight);
            Paint itemPaint = getItemPaint(row, column);
            GradientPaintTransformer t = getGradientPaintTransformer();
            if (t != null && (itemPaint instanceof GradientPaint)) {
                itemPaint = t.transform((GradientPaint) itemPaint, bar);
            }
            g2.setPaint(itemPaint);
            g2.fill(bar);
            if (isDrawBarOutline() && state.getBarWidth() > BarRenderer.BAR_OUTLINE_WIDTH_THRESHOLD) {
                Stroke stroke = getItemOutlineStroke(row, column);
                Paint paint = getItemOutlinePaint(row, column);
                if (!(stroke == null || paint == null)) {
                    g2.setStroke(stroke);
                    g2.setPaint(paint);
                    g2.draw(bar);
                }
            }
            double transX1 = rangeAxis.valueToJava2D(base, dataArea, edge);
            double transX2 = rangeAxis.valueToJava2D(value, dataArea, edge);
            CategoryItemLabelGenerator generator = getItemLabelGenerator(row, column);
            if (generator != null && isItemLabelVisible(row, column)) {
                drawItemLabel(g2, dataset, row, column, plot, generator, bar, transX1 > transX2);
            }
            EntityCollection entities = state.getEntityCollection();
            if (entities != null) {
                addItemEntity(entities, dataset, row, column, bar);
            }
        }
    }
}
