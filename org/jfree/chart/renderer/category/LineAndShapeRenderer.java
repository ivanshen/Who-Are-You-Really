package org.jfree.chart.renderer.category;

import java.awt.Graphics2D;
import java.awt.Paint;
import java.awt.Shape;
import java.awt.Stroke;
import java.awt.geom.Line2D;
import java.awt.geom.Line2D.Double;
import java.awt.geom.Rectangle2D;
import java.io.Serializable;
import org.jfree.chart.LegendItem;
import org.jfree.chart.axis.CategoryAxis;
import org.jfree.chart.axis.ValueAxis;
import org.jfree.chart.entity.EntityCollection;
import org.jfree.chart.plot.CategoryPlot;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.data.category.CategoryDataset;
import org.jfree.data.xy.NormalizedMatrixSeries;
import org.jfree.util.BooleanList;
import org.jfree.util.BooleanUtilities;
import org.jfree.util.ObjectUtilities;
import org.jfree.util.PublicCloneable;
import org.jfree.util.ShapeUtilities;

public class LineAndShapeRenderer extends AbstractCategoryItemRenderer implements Cloneable, PublicCloneable, Serializable {
    private static final long serialVersionUID = -197749519869226398L;
    private boolean baseLinesVisible;
    private boolean baseShapesFilled;
    private boolean baseShapesVisible;
    private boolean drawOutlines;
    private double itemMargin;
    private Boolean linesVisible;
    private BooleanList seriesLinesVisible;
    private BooleanList seriesShapesFilled;
    private BooleanList seriesShapesVisible;
    private Boolean shapesFilled;
    private Boolean shapesVisible;
    private boolean useFillPaint;
    private boolean useOutlinePaint;
    private boolean useSeriesOffset;

    public LineAndShapeRenderer() {
        this(true, true);
    }

    public LineAndShapeRenderer(boolean lines, boolean shapes) {
        this.linesVisible = null;
        this.seriesLinesVisible = new BooleanList();
        this.baseLinesVisible = lines;
        this.shapesVisible = null;
        this.seriesShapesVisible = new BooleanList();
        this.baseShapesVisible = shapes;
        this.shapesFilled = null;
        this.seriesShapesFilled = new BooleanList();
        this.baseShapesFilled = true;
        this.useFillPaint = false;
        this.drawOutlines = true;
        this.useOutlinePaint = false;
        this.useSeriesOffset = false;
        this.itemMargin = 0.0d;
    }

    public boolean getItemLineVisible(int series, int item) {
        Boolean flag = this.linesVisible;
        if (flag == null) {
            flag = getSeriesLinesVisible(series);
        }
        if (flag != null) {
            return flag.booleanValue();
        }
        return this.baseLinesVisible;
    }

    public Boolean getLinesVisible() {
        return this.linesVisible;
    }

    public void setLinesVisible(Boolean visible) {
        this.linesVisible = visible;
        fireChangeEvent();
    }

    public void setLinesVisible(boolean visible) {
        setLinesVisible(BooleanUtilities.valueOf(visible));
    }

    public Boolean getSeriesLinesVisible(int series) {
        return this.seriesLinesVisible.getBoolean(series);
    }

    public void setSeriesLinesVisible(int series, Boolean flag) {
        this.seriesLinesVisible.setBoolean(series, flag);
        fireChangeEvent();
    }

    public void setSeriesLinesVisible(int series, boolean visible) {
        setSeriesLinesVisible(series, BooleanUtilities.valueOf(visible));
    }

    public boolean getBaseLinesVisible() {
        return this.baseLinesVisible;
    }

    public void setBaseLinesVisible(boolean flag) {
        this.baseLinesVisible = flag;
        fireChangeEvent();
    }

    public boolean getItemShapeVisible(int series, int item) {
        Boolean flag = this.shapesVisible;
        if (flag == null) {
            flag = getSeriesShapesVisible(series);
        }
        if (flag != null) {
            return flag.booleanValue();
        }
        return this.baseShapesVisible;
    }

    public Boolean getShapesVisible() {
        return this.shapesVisible;
    }

    public void setShapesVisible(Boolean visible) {
        this.shapesVisible = visible;
        fireChangeEvent();
    }

    public void setShapesVisible(boolean visible) {
        setShapesVisible(BooleanUtilities.valueOf(visible));
    }

    public Boolean getSeriesShapesVisible(int series) {
        return this.seriesShapesVisible.getBoolean(series);
    }

    public void setSeriesShapesVisible(int series, boolean visible) {
        setSeriesShapesVisible(series, BooleanUtilities.valueOf(visible));
    }

    public void setSeriesShapesVisible(int series, Boolean flag) {
        this.seriesShapesVisible.setBoolean(series, flag);
        fireChangeEvent();
    }

    public boolean getBaseShapesVisible() {
        return this.baseShapesVisible;
    }

    public void setBaseShapesVisible(boolean flag) {
        this.baseShapesVisible = flag;
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
        if (this.shapesFilled != null) {
            return this.shapesFilled.booleanValue();
        }
        Boolean flag = this.seriesShapesFilled.getBoolean(series);
        if (flag != null) {
            return flag.booleanValue();
        }
        return this.baseShapesFilled;
    }

    public Boolean getShapesFilled() {
        return this.shapesFilled;
    }

    public void setShapesFilled(boolean filled) {
        if (filled) {
            setShapesFilled(Boolean.TRUE);
        } else {
            setShapesFilled(Boolean.FALSE);
        }
    }

    public void setShapesFilled(Boolean filled) {
        this.shapesFilled = filled;
        fireChangeEvent();
    }

    public void setSeriesShapesFilled(int series, Boolean filled) {
        this.seriesShapesFilled.setBoolean(series, filled);
        fireChangeEvent();
    }

    public void setSeriesShapesFilled(int series, boolean filled) {
        setSeriesShapesFilled(series, BooleanUtilities.valueOf(filled));
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
        boolean lineVisible = getItemLineVisible(series, 0);
        boolean shapeVisible = getItemShapeVisible(series, 0);
        boolean itemShapeFilled = getItemShapeFilled(series, 0);
        Shape shape2 = new Double(-7.0d, 0.0d, 7.0d, 0.0d);
        LegendItem legendItem = new LegendItem(label, description, toolTipText, urlText, shapeVisible, shape, itemShapeFilled, fillPaint, shapeOutlineVisible, outlinePaint, outlineStroke, lineVisible, (Shape) r3, getItemStroke(series, 0), getItemPaint(series, 0));
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

    public int getPassCount() {
        return 2;
    }

    public void drawItem(Graphics2D g2, CategoryItemRendererState state, Rectangle2D dataArea, CategoryPlot plot, CategoryAxis domainAxis, ValueAxis rangeAxis, CategoryDataset dataset, int row, int column, int pass) {
        if (!getItemVisible(row, column)) {
            return;
        }
        if (getItemLineVisible(row, column) || getItemShapeVisible(row, column)) {
            Number v = dataset.getValue(row, column);
            if (v != null) {
                int visibleRow = state.getVisibleSeriesIndex(row);
                if (visibleRow >= 0) {
                    double x1;
                    int visibleRowCount = state.getVisibleSeriesCount();
                    PlotOrientation orientation = plot.getOrientation();
                    if (this.useSeriesOffset) {
                        x1 = domainAxis.getCategorySeriesMiddle(column, dataset.getColumnCount(), visibleRow, visibleRowCount, this.itemMargin, dataArea, plot.getDomainAxisEdge());
                    } else {
                        x1 = domainAxis.getCategoryMiddle(column, getColumnCount(), dataArea, plot.getDomainAxisEdge());
                    }
                    double value = v.doubleValue();
                    double y1 = rangeAxis.valueToJava2D(value, dataArea, plot.getRangeAxisEdge());
                    if (pass == 0 && getItemLineVisible(row, column) && column != 0) {
                        Number previousValue = dataset.getValue(row, column - 1);
                        if (previousValue != null) {
                            double x0;
                            double previous = previousValue.doubleValue();
                            if (this.useSeriesOffset) {
                                x0 = domainAxis.getCategorySeriesMiddle(column - 1, dataset.getColumnCount(), visibleRow, visibleRowCount, this.itemMargin, dataArea, plot.getDomainAxisEdge());
                            } else {
                                x0 = domainAxis.getCategoryMiddle(column - 1, getColumnCount(), dataArea, plot.getDomainAxisEdge());
                            }
                            double y0 = rangeAxis.valueToJava2D(previous, dataArea, plot.getRangeAxisEdge());
                            Line2D line = null;
                            if (orientation == PlotOrientation.HORIZONTAL) {
                                line = new Double(y0, x0, y1, x1);
                            } else if (orientation == PlotOrientation.VERTICAL) {
                                Double doubleR = new Double(x0, y0, x1, y1);
                            }
                            g2.setPaint(getItemPaint(row, column));
                            g2.setStroke(getItemStroke(row, column));
                            g2.draw(line);
                        }
                    }
                    if (pass == 1) {
                        Shape shape = getItemShape(row, column);
                        if (orientation == PlotOrientation.HORIZONTAL) {
                            shape = ShapeUtilities.createTranslatedShape(shape, y1, x1);
                        } else if (orientation == PlotOrientation.VERTICAL) {
                            shape = ShapeUtilities.createTranslatedShape(shape, x1, y1);
                        }
                        if (getItemShapeVisible(row, column)) {
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
                        if (isItemLabelVisible(row, column)) {
                            if (orientation == PlotOrientation.HORIZONTAL) {
                                drawItemLabel(g2, orientation, dataset, row, column, y1, x1, value < 0.0d);
                            } else if (orientation == PlotOrientation.VERTICAL) {
                                drawItemLabel(g2, orientation, dataset, row, column, x1, y1, value < 0.0d);
                            }
                        }
                        updateCrosshairValues(state.getCrosshairState(), dataset.getRowKey(row), dataset.getColumnKey(column), value, plot.indexOf(dataset), x1, y1, orientation);
                        EntityCollection entities = state.getEntityCollection();
                        if (entities != null) {
                            addItemEntity(entities, dataset, row, column, shape);
                        }
                    }
                }
            }
        }
    }

    public boolean equals(Object obj) {
        if (obj == this) {
            return true;
        }
        if (!(obj instanceof LineAndShapeRenderer)) {
            return false;
        }
        LineAndShapeRenderer that = (LineAndShapeRenderer) obj;
        if (this.baseLinesVisible == that.baseLinesVisible && ObjectUtilities.equal(this.seriesLinesVisible, that.seriesLinesVisible) && ObjectUtilities.equal(this.linesVisible, that.linesVisible) && this.baseShapesVisible == that.baseShapesVisible && ObjectUtilities.equal(this.seriesShapesVisible, that.seriesShapesVisible) && ObjectUtilities.equal(this.shapesVisible, that.shapesVisible) && ObjectUtilities.equal(this.shapesFilled, that.shapesFilled) && ObjectUtilities.equal(this.seriesShapesFilled, that.seriesShapesFilled) && this.baseShapesFilled == that.baseShapesFilled && this.useOutlinePaint == that.useOutlinePaint && this.useSeriesOffset == that.useSeriesOffset && this.itemMargin == that.itemMargin) {
            return super.equals(obj);
        }
        return false;
    }

    public Object clone() throws CloneNotSupportedException {
        LineAndShapeRenderer clone = (LineAndShapeRenderer) super.clone();
        clone.seriesLinesVisible = (BooleanList) this.seriesLinesVisible.clone();
        clone.seriesShapesVisible = (BooleanList) this.seriesShapesVisible.clone();
        clone.seriesShapesFilled = (BooleanList) this.seriesShapesFilled.clone();
        return clone;
    }
}
