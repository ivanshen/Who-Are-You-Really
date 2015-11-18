package org.jfree.chart.renderer.category;

import java.awt.Color;
import java.awt.GradientPaint;
import java.awt.Graphics2D;
import java.awt.Paint;
import java.awt.Stroke;
import java.awt.geom.Rectangle2D;
import java.awt.geom.Rectangle2D.Double;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import net.miginfocom.layout.UnitValue;
import org.jfree.chart.axis.CategoryAxis;
import org.jfree.chart.axis.DateAxis;
import org.jfree.chart.axis.ValueAxis;
import org.jfree.chart.entity.EntityCollection;
import org.jfree.chart.labels.CategoryItemLabelGenerator;
import org.jfree.chart.plot.CategoryPlot;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.util.ParamChecks;
import org.jfree.data.Range;
import org.jfree.data.category.CategoryDataset;
import org.jfree.data.xy.NormalizedMatrixSeries;
import org.jfree.io.SerialUtilities;
import org.jfree.ui.GradientPaintTransformType;
import org.jfree.ui.RectangleEdge;
import org.jfree.ui.StandardGradientPaintTransformer;
import org.jfree.util.PaintUtilities;

public class WaterfallBarRenderer extends BarRenderer {
    private static final long serialVersionUID = -2482910643727230911L;
    private transient Paint firstBarPaint;
    private transient Paint lastBarPaint;
    private transient Paint negativeBarPaint;
    private transient Paint positiveBarPaint;

    public WaterfallBarRenderer() {
        this(new GradientPaint(0.0f, 0.0f, new Color(34, 34, 255), 0.0f, 0.0f, new Color(UnitValue.SUB, UnitValue.SUB, 255)), new GradientPaint(0.0f, 0.0f, new Color(34, 255, 34), 0.0f, 0.0f, new Color(UnitValue.SUB, 255, UnitValue.SUB)), new GradientPaint(0.0f, 0.0f, new Color(255, 34, 34), 0.0f, 0.0f, new Color(255, UnitValue.SUB, UnitValue.SUB)), new GradientPaint(0.0f, 0.0f, new Color(255, 255, 34), 0.0f, 0.0f, new Color(255, 255, UnitValue.SUB)));
    }

    public WaterfallBarRenderer(Paint firstBarPaint, Paint positiveBarPaint, Paint negativeBarPaint, Paint lastBarPaint) {
        ParamChecks.nullNotPermitted(firstBarPaint, "firstBarPaint");
        ParamChecks.nullNotPermitted(positiveBarPaint, "positiveBarPaint");
        ParamChecks.nullNotPermitted(negativeBarPaint, "negativeBarPaint");
        ParamChecks.nullNotPermitted(lastBarPaint, "lastBarPaint");
        this.firstBarPaint = firstBarPaint;
        this.lastBarPaint = lastBarPaint;
        this.positiveBarPaint = positiveBarPaint;
        this.negativeBarPaint = negativeBarPaint;
        setGradientPaintTransformer(new StandardGradientPaintTransformer(GradientPaintTransformType.CENTER_VERTICAL));
        setMinimumBarLength(NormalizedMatrixSeries.DEFAULT_SCALE_FACTOR);
    }

    public Paint getFirstBarPaint() {
        return this.firstBarPaint;
    }

    public void setFirstBarPaint(Paint paint) {
        ParamChecks.nullNotPermitted(paint, "paint");
        this.firstBarPaint = paint;
        fireChangeEvent();
    }

    public Paint getLastBarPaint() {
        return this.lastBarPaint;
    }

    public void setLastBarPaint(Paint paint) {
        ParamChecks.nullNotPermitted(paint, "paint");
        this.lastBarPaint = paint;
        fireChangeEvent();
    }

    public Paint getPositiveBarPaint() {
        return this.positiveBarPaint;
    }

    public void setPositiveBarPaint(Paint paint) {
        ParamChecks.nullNotPermitted(paint, "paint");
        this.positiveBarPaint = paint;
        fireChangeEvent();
    }

    public Paint getNegativeBarPaint() {
        return this.negativeBarPaint;
    }

    public void setNegativeBarPaint(Paint paint) {
        ParamChecks.nullNotPermitted(paint, "paint");
        this.negativeBarPaint = paint;
        fireChangeEvent();
    }

    public Range findRangeBounds(CategoryDataset dataset) {
        if (dataset == null) {
            return null;
        }
        boolean allItemsNull = true;
        double minimum = 0.0d;
        double maximum = 0.0d;
        int columnCount = dataset.getColumnCount();
        for (int row = 0; row < dataset.getRowCount(); row++) {
            double runningTotal = 0.0d;
            for (int column = 0; column <= columnCount - 1; column++) {
                Number n = dataset.getValue(row, column);
                if (n != null) {
                    allItemsNull = false;
                    double value = n.doubleValue();
                    if (column == columnCount - 1) {
                        runningTotal = value;
                    } else {
                        runningTotal += value;
                    }
                    minimum = Math.min(minimum, runningTotal);
                    maximum = Math.max(maximum, runningTotal);
                }
            }
        }
        if (allItemsNull) {
            return null;
        }
        return new Range(minimum, maximum);
    }

    public void drawItem(Graphics2D g2, CategoryItemRendererState state, Rectangle2D dataArea, CategoryPlot plot, CategoryAxis domainAxis, ValueAxis rangeAxis, CategoryDataset dataset, int row, int column, int pass) {
        Paint seriesPaint;
        double previous = state.getSeriesRunningTotal();
        if (column == dataset.getColumnCount() - 1) {
            previous = 0.0d;
        }
        double current = 0.0d;
        Number n = dataset.getValue(row, column);
        if (n != null) {
            current = previous + n.doubleValue();
        }
        state.setSeriesRunningTotal(current);
        int categoryCount = getColumnCount();
        PlotOrientation orientation = plot.getOrientation();
        double rectX = 0.0d;
        double rectY = 0.0d;
        RectangleEdge rangeAxisLocation = plot.getRangeAxisEdge();
        double j2dy0 = rangeAxis.valueToJava2D(previous, dataArea, rangeAxisLocation);
        double j2dy1 = rangeAxis.valueToJava2D(current, dataArea, rangeAxisLocation);
        double valDiff = current - previous;
        if (j2dy1 < j2dy0) {
            double temp = j2dy1;
            j2dy1 = j2dy0;
            j2dy0 = temp;
        }
        double rectWidth = state.getBarWidth();
        double rectHeight = Math.max(getMinimumBarLength(), Math.abs(j2dy1 - j2dy0));
        Comparable seriesKey = dataset.getRowKey(row);
        Comparable categoryKey = dataset.getColumnKey(column);
        if (orientation == PlotOrientation.HORIZONTAL) {
            rectY = domainAxis.getCategorySeriesMiddle(categoryKey, seriesKey, dataset, getItemMargin(), dataArea, RectangleEdge.LEFT);
            rectX = j2dy0;
            rectHeight = state.getBarWidth();
            rectY -= rectHeight / DateAxis.DEFAULT_AUTO_RANGE_MINIMUM_SIZE_IN_MILLISECONDS;
            rectWidth = Math.max(getMinimumBarLength(), Math.abs(j2dy1 - j2dy0));
        } else if (orientation == PlotOrientation.VERTICAL) {
            rectX = domainAxis.getCategorySeriesMiddle(categoryKey, seriesKey, dataset, getItemMargin(), dataArea, RectangleEdge.TOP) - (rectWidth / DateAxis.DEFAULT_AUTO_RANGE_MINIMUM_SIZE_IN_MILLISECONDS);
            rectY = j2dy0;
        }
        Rectangle2D bar = new Double(rectX, rectY, rectWidth, rectHeight);
        if (column == 0) {
            seriesPaint = getFirstBarPaint();
        } else if (column == categoryCount - 1) {
            seriesPaint = getLastBarPaint();
        } else if (valDiff >= 0.0d) {
            seriesPaint = getPositiveBarPaint();
        } else {
            seriesPaint = getNegativeBarPaint();
        }
        if (getGradientPaintTransformer() != null && (seriesPaint instanceof GradientPaint)) {
            seriesPaint = getGradientPaintTransformer().transform((GradientPaint) seriesPaint, bar);
        }
        g2.setPaint(seriesPaint);
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
            drawItemLabel(g2, dataset, row, column, plot, generator, bar, valDiff < 0.0d);
        }
        EntityCollection entities = state.getEntityCollection();
        if (entities != null) {
            addItemEntity(entities, dataset, row, column, bar);
        }
    }

    public boolean equals(Object obj) {
        if (obj == this) {
            return true;
        }
        if (!super.equals(obj)) {
            return false;
        }
        if (!(obj instanceof WaterfallBarRenderer)) {
            return false;
        }
        WaterfallBarRenderer that = (WaterfallBarRenderer) obj;
        if (!PaintUtilities.equal(this.firstBarPaint, that.firstBarPaint)) {
            return false;
        }
        if (!PaintUtilities.equal(this.lastBarPaint, that.lastBarPaint)) {
            return false;
        }
        if (!PaintUtilities.equal(this.positiveBarPaint, that.positiveBarPaint)) {
            return false;
        }
        if (PaintUtilities.equal(this.negativeBarPaint, that.negativeBarPaint)) {
            return true;
        }
        return false;
    }

    private void writeObject(ObjectOutputStream stream) throws IOException {
        stream.defaultWriteObject();
        SerialUtilities.writePaint(this.firstBarPaint, stream);
        SerialUtilities.writePaint(this.lastBarPaint, stream);
        SerialUtilities.writePaint(this.positiveBarPaint, stream);
        SerialUtilities.writePaint(this.negativeBarPaint, stream);
    }

    private void readObject(ObjectInputStream stream) throws IOException, ClassNotFoundException {
        stream.defaultReadObject();
        this.firstBarPaint = SerialUtilities.readPaint(stream);
        this.lastBarPaint = SerialUtilities.readPaint(stream);
        this.positiveBarPaint = SerialUtilities.readPaint(stream);
        this.negativeBarPaint = SerialUtilities.readPaint(stream);
    }
}
