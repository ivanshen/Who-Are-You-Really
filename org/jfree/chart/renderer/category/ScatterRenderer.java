package org.jfree.chart.renderer.category;

import java.awt.Graphics2D;
import java.awt.Paint;
import java.awt.Shape;
import java.awt.Stroke;
import java.awt.geom.Line2D.Double;
import java.awt.geom.Rectangle2D;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.List;
import org.jfree.chart.LegendItem;
import org.jfree.chart.axis.CategoryAxis;
import org.jfree.chart.axis.ValueAxis;
import org.jfree.chart.plot.CategoryPlot;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.data.Range;
import org.jfree.data.category.CategoryDataset;
import org.jfree.data.statistics.MultiValueCategoryDataset;
import org.jfree.data.xy.NormalizedMatrixSeries;
import org.jfree.util.BooleanList;
import org.jfree.util.BooleanUtilities;
import org.jfree.util.ObjectUtilities;
import org.jfree.util.PublicCloneable;
import org.jfree.util.ShapeUtilities;

public class ScatterRenderer extends AbstractCategoryItemRenderer implements Cloneable, PublicCloneable, Serializable {
    private boolean baseShapesFilled;
    private boolean drawOutlines;
    private double itemMargin;
    private BooleanList seriesShapesFilled;
    private boolean useFillPaint;
    private boolean useOutlinePaint;
    private boolean useSeriesOffset;

    public ScatterRenderer() {
        this.seriesShapesFilled = new BooleanList();
        this.baseShapesFilled = true;
        this.useFillPaint = false;
        this.drawOutlines = false;
        this.useOutlinePaint = false;
        this.useSeriesOffset = true;
        this.itemMargin = LevelRenderer.DEFAULT_ITEM_MARGIN;
    }

    public boolean getUseSeriesOffset() {
        return this.useSeriesOffset;
    }

    public void setUseSeriesOffset(boolean offset) {
        this.useSeriesOffset = offset;
        fireChangeEvent();
    }

    public double getItemMargin() {
        return this.itemMargin;
    }

    public void setItemMargin(double margin) {
        if (margin < 0.0d || margin >= NormalizedMatrixSeries.DEFAULT_SCALE_FACTOR) {
            throw new IllegalArgumentException("Requires 0.0 <= margin < 1.0.");
        }
        this.itemMargin = margin;
        fireChangeEvent();
    }

    public boolean getDrawOutlines() {
        return this.drawOutlines;
    }

    public void setDrawOutlines(boolean flag) {
        this.drawOutlines = flag;
        fireChangeEvent();
    }

    public boolean getUseOutlinePaint() {
        return this.useOutlinePaint;
    }

    public void setUseOutlinePaint(boolean use) {
        this.useOutlinePaint = use;
        fireChangeEvent();
    }

    public boolean getItemShapeFilled(int series, int item) {
        return getSeriesShapesFilled(series);
    }

    public boolean getSeriesShapesFilled(int series) {
        Boolean flag = this.seriesShapesFilled.getBoolean(series);
        if (flag != null) {
            return flag.booleanValue();
        }
        return this.baseShapesFilled;
    }

    public void setSeriesShapesFilled(int series, Boolean filled) {
        this.seriesShapesFilled.setBoolean(series, filled);
        fireChangeEvent();
    }

    public void setSeriesShapesFilled(int series, boolean filled) {
        this.seriesShapesFilled.setBoolean(series, BooleanUtilities.valueOf(filled));
        fireChangeEvent();
    }

    public boolean getBaseShapesFilled() {
        return this.baseShapesFilled;
    }

    public void setBaseShapesFilled(boolean flag) {
        this.baseShapesFilled = flag;
        fireChangeEvent();
    }

    public boolean getUseFillPaint() {
        return this.useFillPaint;
    }

    public void setUseFillPaint(boolean flag) {
        this.useFillPaint = flag;
        fireChangeEvent();
    }

    public Range findRangeBounds(CategoryDataset dataset) {
        return findRangeBounds(dataset, true);
    }

    public void drawItem(Graphics2D g2, CategoryItemRendererState state, Rectangle2D dataArea, CategoryPlot plot, CategoryAxis domainAxis, ValueAxis rangeAxis, CategoryDataset dataset, int row, int column, int pass) {
        if (getItemVisible(row, column)) {
            int visibleRow = state.getVisibleSeriesIndex(row);
            if (visibleRow >= 0) {
                int visibleRowCount = state.getVisibleSeriesCount();
                PlotOrientation orientation = plot.getOrientation();
                List values = ((MultiValueCategoryDataset) dataset).getValues(row, column);
                if (values != null) {
                    int valueCount = values.size();
                    for (int i = 0; i < valueCount; i++) {
                        double x1;
                        if (this.useSeriesOffset) {
                            x1 = domainAxis.getCategorySeriesMiddle(column, dataset.getColumnCount(), visibleRow, visibleRowCount, this.itemMargin, dataArea, plot.getDomainAxisEdge());
                        } else {
                            x1 = domainAxis.getCategoryMiddle(column, getColumnCount(), dataArea, plot.getDomainAxisEdge());
                        }
                        double doubleValue = ((Number) values.get(i)).doubleValue();
                        double y1 = rangeAxis.valueToJava2D(value, dataArea, plot.getRangeAxisEdge());
                        Shape shape = getItemShape(row, column);
                        if (orientation == PlotOrientation.HORIZONTAL) {
                            shape = ShapeUtilities.createTranslatedShape(shape, y1, x1);
                        } else if (orientation == PlotOrientation.VERTICAL) {
                            shape = ShapeUtilities.createTranslatedShape(shape, x1, y1);
                        }
                        if (getItemShapeFilled(row, column)) {
                            if (this.useFillPaint) {
                                g2.setPaint(getItemFillPaint(row, column));
                            } else {
                                g2.setPaint(getItemPaint(row, column));
                            }
                            g2.fill(shape);
                        }
                        if (this.drawOutlines) {
                            if (this.useOutlinePaint) {
                                g2.setPaint(getItemOutlinePaint(row, column));
                            } else {
                                g2.setPaint(getItemPaint(row, column));
                            }
                            g2.setStroke(getItemOutlineStroke(row, column));
                            g2.draw(shape);
                        }
                    }
                }
            }
        }
    }

    public LegendItem getLegendItem(int datasetIndex, int series) {
        CategoryPlot cp = getPlot();
        if (cp == null) {
            return null;
        }
        if (!isSeriesVisible(series) || !isSeriesVisibleInLegend(series)) {
            return null;
        }
        Paint fillPaint;
        Paint outlinePaint;
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
        Shape shape = lookupLegendShape(series);
        Paint paint = lookupSeriesPaint(series);
        if (this.useFillPaint) {
            fillPaint = getItemFillPaint(series, 0);
        } else {
            fillPaint = paint;
        }
        boolean shapeOutlineVisible = this.drawOutlines;
        if (this.useOutlinePaint) {
            outlinePaint = getItemOutlinePaint(series, 0);
        } else {
            outlinePaint = paint;
        }
        Stroke outlineStroke = lookupSeriesOutlineStroke(series);
        boolean itemShapeFilled = getItemShapeFilled(series, 0);
        Shape shape2 = new Double(-7.0d, 0.0d, 7.0d, 0.0d);
        LegendItem legendItem = new LegendItem(label, description, toolTipText, urlText, true, shape, itemShapeFilled, fillPaint, shapeOutlineVisible, outlinePaint, outlineStroke, false, (Shape) r3, getItemStroke(series, 0), getItemPaint(series, 0));
        legendItem.setLabelFont(lookupLegendTextFont(series));
        Paint labelPaint = lookupLegendTextPaint(series);
        if (labelPaint != null) {
            legendItem.setLabelPaint(labelPaint);
        }
        legendItem.setDataset(dataset);
        legendItem.setDatasetIndex(datasetIndex);
        legendItem.setSeriesKey(dataset.getRowKey(series));
        legendItem.setSeriesIndex(series);
        return legendItem;
    }

    public boolean equals(Object obj) {
        if (obj == this) {
            return true;
        }
        if (!(obj instanceof ScatterRenderer)) {
            return false;
        }
        ScatterRenderer that = (ScatterRenderer) obj;
        if (ObjectUtilities.equal(this.seriesShapesFilled, that.seriesShapesFilled) && this.baseShapesFilled == that.baseShapesFilled && this.useFillPaint == that.useFillPaint && this.drawOutlines == that.drawOutlines && this.useOutlinePaint == that.useOutlinePaint && this.useSeriesOffset == that.useSeriesOffset && this.itemMargin == that.itemMargin) {
            return super.equals(obj);
        }
        return false;
    }

    public Object clone() throws CloneNotSupportedException {
        ScatterRenderer clone = (ScatterRenderer) super.clone();
        clone.seriesShapesFilled = (BooleanList) this.seriesShapesFilled.clone();
        return clone;
    }

    private void writeObject(ObjectOutputStream stream) throws IOException {
        stream.defaultWriteObject();
    }

    private void readObject(ObjectInputStream stream) throws IOException, ClassNotFoundException {
        stream.defaultReadObject();
    }
}
