package org.jfree.chart.renderer.category;

import java.awt.Graphics2D;
import java.awt.Paint;
import java.awt.geom.Line2D;
import java.awt.geom.Line2D.Double;
import java.awt.geom.Rectangle2D;
import java.io.Serializable;
import org.jfree.chart.LegendItem;
import org.jfree.chart.axis.CategoryAxis;
import org.jfree.chart.axis.DateAxis;
import org.jfree.chart.axis.ValueAxis;
import org.jfree.chart.entity.EntityCollection;
import org.jfree.chart.plot.CategoryPlot;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.plot.PlotRenderingInfo;
import org.jfree.chart.renderer.xy.XYLine3DRenderer;
import org.jfree.data.category.CategoryDataset;
import org.jfree.util.PublicCloneable;

public class CategoryStepRenderer extends AbstractCategoryItemRenderer implements Cloneable, PublicCloneable, Serializable {
    public static final int STAGGER_WIDTH = 5;
    private static final long serialVersionUID = -5121079703118261470L;
    private boolean stagger;

    protected static class State extends CategoryItemRendererState {
        public Line2D line;

        public State(PlotRenderingInfo info) {
            super(info);
            this.line = new Double();
        }
    }

    public CategoryStepRenderer() {
        this(false);
    }

    public CategoryStepRenderer(boolean stagger) {
        this.stagger = false;
        this.stagger = stagger;
        setBaseLegendShape(new Rectangle2D.Double(-4.0d, -3.0d, XYLine3DRenderer.DEFAULT_Y_OFFSET, 6.0d));
    }

    public boolean getStagger() {
        return this.stagger;
    }

    public void setStagger(boolean shouldStagger) {
        this.stagger = shouldStagger;
        fireChangeEvent();
    }

    public LegendItem getLegendItem(int datasetIndex, int series) {
        LegendItem legendItem = null;
        CategoryPlot p = getPlot();
        if (p != null && isSeriesVisible(series) && isSeriesVisibleInLegend(series)) {
            CategoryDataset dataset = p.getDataset(datasetIndex);
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
            legendItem = new LegendItem(label, description, toolTipText, urlText, lookupLegendShape(series), lookupSeriesPaint(series));
            legendItem.setLabelFont(lookupLegendTextFont(series));
            Paint labelPaint = lookupLegendTextPaint(series);
            if (labelPaint != null) {
                legendItem.setLabelPaint(labelPaint);
            }
            legendItem.setSeriesKey(dataset.getRowKey(series));
            legendItem.setSeriesIndex(series);
            legendItem.setDataset(dataset);
            legendItem.setDatasetIndex(datasetIndex);
        }
        return legendItem;
    }

    protected CategoryItemRendererState createState(PlotRenderingInfo info) {
        return new State(info);
    }

    protected void drawLine(Graphics2D g2, State state, PlotOrientation orientation, double x0, double y0, double x1, double y1) {
        if (orientation == PlotOrientation.VERTICAL) {
            state.line.setLine(x0, y0, x1, y1);
            g2.draw(state.line);
        } else if (orientation == PlotOrientation.HORIZONTAL) {
            state.line.setLine(y0, x0, y1, x1);
            g2.draw(state.line);
        }
    }

    public void drawItem(Graphics2D g2, CategoryItemRendererState state, Rectangle2D dataArea, CategoryPlot plot, CategoryAxis domainAxis, ValueAxis rangeAxis, CategoryDataset dataset, int row, int column, int pass) {
        if (getItemVisible(row, column)) {
            Number value = dataset.getValue(row, column);
            if (value != null) {
                PlotOrientation orientation = plot.getOrientation();
                double x1s = domainAxis.getCategoryStart(column, getColumnCount(), dataArea, plot.getDomainAxisEdge());
                double x1 = domainAxis.getCategoryMiddle(column, getColumnCount(), dataArea, plot.getDomainAxisEdge());
                double x1e = (DateAxis.DEFAULT_AUTO_RANGE_MINIMUM_SIZE_IN_MILLISECONDS * x1) - x1s;
                double y1 = rangeAxis.valueToJava2D(value.doubleValue(), dataArea, plot.getRangeAxisEdge());
                g2.setPaint(getItemPaint(row, column));
                g2.setStroke(getItemStroke(row, column));
                if (column != 0) {
                    Number previousValue = dataset.getValue(row, column - 1);
                    if (previousValue != null) {
                        double x0e = (DateAxis.DEFAULT_AUTO_RANGE_MINIMUM_SIZE_IN_MILLISECONDS * domainAxis.getCategoryMiddle(column - 1, getColumnCount(), dataArea, plot.getDomainAxisEdge())) - domainAxis.getCategoryStart(column - 1, getColumnCount(), dataArea, plot.getDomainAxisEdge());
                        double y0 = rangeAxis.valueToJava2D(previousValue.doubleValue(), dataArea, plot.getRangeAxisEdge());
                        if (getStagger()) {
                            int xStagger = row * STAGGER_WIDTH;
                            if (((double) xStagger) > x1s - x0e) {
                                xStagger = (int) (x1s - x0e);
                            }
                            x1s = x0e + ((double) xStagger);
                        }
                        drawLine(g2, (State) state, orientation, x0e, y0, x1s, y0);
                        drawLine(g2, (State) state, orientation, x1s, y0, x1s, y1);
                    }
                }
                drawLine(g2, (State) state, orientation, x1s, y1, x1e, y1);
                if (isItemLabelVisible(row, column)) {
                    drawItemLabel(g2, orientation, dataset, row, column, x1, y1, value.doubleValue() < 0.0d);
                }
                EntityCollection entities = state.getEntityCollection();
                if (entities != null) {
                    Rectangle2D hotspot = new Rectangle2D.Double();
                    if (orientation == PlotOrientation.VERTICAL) {
                        hotspot.setRect(x1s, y1, x1e - x1s, 4.0d);
                    } else {
                        hotspot.setRect(y1 - DateAxis.DEFAULT_AUTO_RANGE_MINIMUM_SIZE_IN_MILLISECONDS, x1s, 4.0d, x1e - x1s);
                    }
                    addItemEntity(entities, dataset, row, column, hotspot);
                }
            }
        }
    }

    public boolean equals(Object obj) {
        if (obj == this) {
            return true;
        }
        if (!(obj instanceof CategoryStepRenderer)) {
            return false;
        }
        if (this.stagger == ((CategoryStepRenderer) obj).stagger) {
            return super.equals(obj);
        }
        return false;
    }
}
