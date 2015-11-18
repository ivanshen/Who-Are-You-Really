package org.jfree.chart.renderer.category;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Paint;
import java.awt.Stroke;
import java.awt.geom.Rectangle2D;
import java.awt.geom.Rectangle2D.Double;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import org.jfree.chart.axis.CategoryAxis;
import org.jfree.chart.axis.ValueAxis;
import org.jfree.chart.entity.EntityCollection;
import org.jfree.chart.labels.CategoryItemLabelGenerator;
import org.jfree.chart.plot.CategoryPlot;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.util.ParamChecks;
import org.jfree.data.category.CategoryDataset;
import org.jfree.data.gantt.GanttCategoryDataset;
import org.jfree.data.xy.NormalizedMatrixSeries;
import org.jfree.io.SerialUtilities;
import org.jfree.ui.RectangleEdge;
import org.jfree.util.PaintUtilities;

public class GanttRenderer extends IntervalBarRenderer implements Serializable {
    private static final long serialVersionUID = -4010349116350119512L;
    private transient Paint completePaint;
    private double endPercent;
    private transient Paint incompletePaint;
    private double startPercent;

    public GanttRenderer() {
        setIncludeBaseInRange(false);
        this.completePaint = Color.green;
        this.incompletePaint = Color.red;
        this.startPercent = 0.35d;
        this.endPercent = 0.65d;
    }

    public Paint getCompletePaint() {
        return this.completePaint;
    }

    public void setCompletePaint(Paint paint) {
        ParamChecks.nullNotPermitted(paint, "paint");
        this.completePaint = paint;
        fireChangeEvent();
    }

    public Paint getIncompletePaint() {
        return this.incompletePaint;
    }

    public void setIncompletePaint(Paint paint) {
        ParamChecks.nullNotPermitted(paint, "paint");
        this.incompletePaint = paint;
        fireChangeEvent();
    }

    public double getStartPercent() {
        return this.startPercent;
    }

    public void setStartPercent(double percent) {
        this.startPercent = percent;
        fireChangeEvent();
    }

    public double getEndPercent() {
        return this.endPercent;
    }

    public void setEndPercent(double percent) {
        this.endPercent = percent;
        fireChangeEvent();
    }

    public void drawItem(Graphics2D g2, CategoryItemRendererState state, Rectangle2D dataArea, CategoryPlot plot, CategoryAxis domainAxis, ValueAxis rangeAxis, CategoryDataset dataset, int row, int column, int pass) {
        if (dataset instanceof GanttCategoryDataset) {
            drawTasks(g2, state, dataArea, plot, domainAxis, rangeAxis, (GanttCategoryDataset) dataset, row, column);
            return;
        }
        super.drawItem(g2, state, dataArea, plot, domainAxis, rangeAxis, dataset, row, column, pass);
    }

    protected void drawTasks(Graphics2D g2, CategoryItemRendererState state, Rectangle2D dataArea, CategoryPlot plot, CategoryAxis domainAxis, ValueAxis rangeAxis, GanttCategoryDataset dataset, int row, int column) {
        int count = dataset.getSubIntervalCount(row, column);
        if (count == 0) {
            drawTask(g2, state, dataArea, plot, domainAxis, rangeAxis, dataset, row, column);
        }
        PlotOrientation orientation = plot.getOrientation();
        int subinterval = 0;
        while (subinterval < count) {
            RectangleEdge rangeAxisLocation = plot.getRangeAxisEdge();
            Number value0 = dataset.getStartValue(row, column, subinterval);
            if (value0 != null) {
                double translatedValue0 = rangeAxis.valueToJava2D(value0.doubleValue(), dataArea, rangeAxisLocation);
                Number value1 = dataset.getEndValue(row, column, subinterval);
                if (value1 != null) {
                    double translatedValue1 = rangeAxis.valueToJava2D(value1.doubleValue(), dataArea, rangeAxisLocation);
                    if (translatedValue1 < translatedValue0) {
                        double temp = translatedValue1;
                        translatedValue1 = translatedValue0;
                        translatedValue0 = temp;
                    }
                    double rectStart = calculateBarW0(plot, plot.getOrientation(), dataArea, domainAxis, state, row, column);
                    double rectLength = Math.abs(translatedValue1 - translatedValue0);
                    double rectBreadth = state.getBarWidth();
                    Rectangle2D bar = null;
                    RectangleEdge barBase = null;
                    if (plot.getOrientation() == PlotOrientation.HORIZONTAL) {
                        bar = new Double(translatedValue0, rectStart, rectLength, rectBreadth);
                        barBase = RectangleEdge.LEFT;
                    } else if (plot.getOrientation() == PlotOrientation.VERTICAL) {
                        Double doubleR = new Double(rectStart, translatedValue0, rectBreadth, rectLength);
                        barBase = RectangleEdge.BOTTOM;
                    }
                    Rectangle2D completeBar = null;
                    Rectangle2D incompleteBar = null;
                    Number percent = dataset.getPercentComplete(row, column, subinterval);
                    double start = getStartPercent();
                    double end = getEndPercent();
                    if (percent != null) {
                        double p = percent.doubleValue();
                        if (orientation == PlotOrientation.HORIZONTAL) {
                            completeBar = new Double(translatedValue0, rectStart + (start * rectBreadth), rectLength * p, rectBreadth * (end - start));
                            incompleteBar = new Double(translatedValue0 + (rectLength * p), rectStart + (start * rectBreadth), rectLength * (NormalizedMatrixSeries.DEFAULT_SCALE_FACTOR - p), rectBreadth * (end - start));
                        } else if (orientation == PlotOrientation.VERTICAL) {
                            Double doubleR2 = new Double(rectStart + (start * rectBreadth), translatedValue0 + ((NormalizedMatrixSeries.DEFAULT_SCALE_FACTOR - p) * rectLength), rectBreadth * (end - start), rectLength * p);
                            incompleteBar = new Double(rectStart + (start * rectBreadth), translatedValue0, rectBreadth * (end - start), rectLength * (NormalizedMatrixSeries.DEFAULT_SCALE_FACTOR - p));
                        }
                    }
                    if (getShadowsVisible()) {
                        getBarPainter().paintBarShadow(g2, this, row, column, bar, barBase, true);
                    }
                    getBarPainter().paintBar(g2, this, row, column, bar, barBase);
                    if (completeBar != null) {
                        g2.setPaint(getCompletePaint());
                        g2.fill(completeBar);
                    }
                    if (incompleteBar != null) {
                        g2.setPaint(getIncompletePaint());
                        g2.fill(incompleteBar);
                    }
                    if (isDrawBarOutline() && state.getBarWidth() > BarRenderer.BAR_OUTLINE_WIDTH_THRESHOLD) {
                        g2.setStroke(getItemStroke(row, column));
                        g2.setPaint(getItemOutlinePaint(row, column));
                        g2.draw(bar);
                    }
                    if (subinterval == count - 1) {
                        updateCrosshairValues(state.getCrosshairState(), dataset.getRowKey(row), dataset.getColumnKey(column), value1.doubleValue(), plot.indexOf(dataset), domainAxis.getCategorySeriesMiddle(dataset.getColumnKey(column), dataset.getRowKey(row), dataset, getItemMargin(), dataArea, plot.getDomainAxisEdge()), translatedValue1, orientation);
                    }
                    if (state.getInfo() != null) {
                        EntityCollection entities = state.getEntityCollection();
                        if (entities != null) {
                            addItemEntity(entities, dataset, row, column, bar);
                        }
                    }
                    subinterval++;
                } else {
                    return;
                }
            }
            return;
        }
    }

    protected void drawTask(Graphics2D g2, CategoryItemRendererState state, Rectangle2D dataArea, CategoryPlot plot, CategoryAxis domainAxis, ValueAxis rangeAxis, GanttCategoryDataset dataset, int row, int column) {
        PlotOrientation orientation = plot.getOrientation();
        RectangleEdge rangeAxisLocation = plot.getRangeAxisEdge();
        Number value0 = dataset.getEndValue(row, column);
        if (value0 != null) {
            double java2dValue0 = rangeAxis.valueToJava2D(value0.doubleValue(), dataArea, rangeAxisLocation);
            Number value1 = dataset.getStartValue(row, column);
            if (value1 != null) {
                double java2dValue1 = rangeAxis.valueToJava2D(value1.doubleValue(), dataArea, rangeAxisLocation);
                if (java2dValue1 < java2dValue0) {
                    double temp = java2dValue1;
                    java2dValue1 = java2dValue0;
                    java2dValue0 = temp;
                    value1 = value0;
                }
                double rectStart = calculateBarW0(plot, orientation, dataArea, domainAxis, state, row, column);
                double rectBreadth = state.getBarWidth();
                double rectLength = Math.abs(java2dValue1 - java2dValue0);
                Rectangle2D bar = null;
                RectangleEdge barBase = null;
                if (orientation == PlotOrientation.HORIZONTAL) {
                    bar = new Double(java2dValue0, rectStart, rectLength, rectBreadth);
                    barBase = RectangleEdge.LEFT;
                } else if (orientation == PlotOrientation.VERTICAL) {
                    Double doubleR = new Double(rectStart, java2dValue1, rectBreadth, rectLength);
                    barBase = RectangleEdge.BOTTOM;
                }
                Rectangle2D completeBar = null;
                Rectangle2D incompleteBar = null;
                Number percent = dataset.getPercentComplete(row, column);
                double start = getStartPercent();
                double end = getEndPercent();
                if (percent != null) {
                    double p = percent.doubleValue();
                    if (plot.getOrientation() == PlotOrientation.HORIZONTAL) {
                        completeBar = new Double(java2dValue0, rectStart + (start * rectBreadth), rectLength * p, rectBreadth * (end - start));
                        incompleteBar = new Double(java2dValue0 + (rectLength * p), rectStart + (start * rectBreadth), rectLength * (NormalizedMatrixSeries.DEFAULT_SCALE_FACTOR - p), rectBreadth * (end - start));
                    } else if (plot.getOrientation() == PlotOrientation.VERTICAL) {
                        Double doubleR2 = new Double(rectStart + (start * rectBreadth), java2dValue1 + ((NormalizedMatrixSeries.DEFAULT_SCALE_FACTOR - p) * rectLength), rectBreadth * (end - start), rectLength * p);
                        incompleteBar = new Double(rectStart + (start * rectBreadth), java2dValue1, rectBreadth * (end - start), rectLength * (NormalizedMatrixSeries.DEFAULT_SCALE_FACTOR - p));
                    }
                }
                if (getShadowsVisible()) {
                    getBarPainter().paintBarShadow(g2, this, row, column, bar, barBase, true);
                }
                getBarPainter().paintBar(g2, this, row, column, bar, barBase);
                if (completeBar != null) {
                    g2.setPaint(getCompletePaint());
                    g2.fill(completeBar);
                }
                if (incompleteBar != null) {
                    g2.setPaint(getIncompletePaint());
                    g2.fill(incompleteBar);
                }
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
                    drawItemLabel(g2, dataset, row, column, plot, generator, bar, false);
                }
                updateCrosshairValues(state.getCrosshairState(), dataset.getRowKey(row), dataset.getColumnKey(column), value1.doubleValue(), plot.indexOf(dataset), domainAxis.getCategorySeriesMiddle(dataset.getColumnKey(column), dataset.getRowKey(row), dataset, getItemMargin(), dataArea, plot.getDomainAxisEdge()), java2dValue1, orientation);
                EntityCollection entities = state.getEntityCollection();
                if (entities != null) {
                    addItemEntity(entities, dataset, row, column, bar);
                }
            }
        }
    }

    public double getItemMiddle(Comparable rowKey, Comparable columnKey, CategoryDataset dataset, CategoryAxis axis, Rectangle2D area, RectangleEdge edge) {
        return axis.getCategorySeriesMiddle(columnKey, rowKey, dataset, getItemMargin(), area, edge);
    }

    public boolean equals(Object obj) {
        if (obj == this) {
            return true;
        }
        if (!(obj instanceof GanttRenderer)) {
            return false;
        }
        GanttRenderer that = (GanttRenderer) obj;
        if (PaintUtilities.equal(this.completePaint, that.completePaint) && PaintUtilities.equal(this.incompletePaint, that.incompletePaint) && this.startPercent == that.startPercent && this.endPercent == that.endPercent) {
            return super.equals(obj);
        }
        return false;
    }

    private void writeObject(ObjectOutputStream stream) throws IOException {
        stream.defaultWriteObject();
        SerialUtilities.writePaint(this.completePaint, stream);
        SerialUtilities.writePaint(this.incompletePaint, stream);
    }

    private void readObject(ObjectInputStream stream) throws IOException, ClassNotFoundException {
        stream.defaultReadObject();
        this.completePaint = SerialUtilities.readPaint(stream);
        this.incompletePaint = SerialUtilities.readPaint(stream);
    }
}
