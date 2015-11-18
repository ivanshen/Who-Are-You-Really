package org.jfree.chart.renderer.category;

import java.awt.Graphics2D;
import java.awt.Paint;
import java.awt.Shape;
import java.awt.geom.GeneralPath;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.awt.geom.Rectangle2D.Double;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import org.jfree.chart.HashUtilities;
import org.jfree.chart.axis.CategoryAxis;
import org.jfree.chart.axis.DateAxis;
import org.jfree.chart.axis.ValueAxis;
import org.jfree.chart.entity.EntityCollection;
import org.jfree.chart.event.RendererChangeEvent;
import org.jfree.chart.labels.CategoryItemLabelGenerator;
import org.jfree.chart.plot.CategoryPlot;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.util.PaintAlpha;
import org.jfree.data.DataUtilities;
import org.jfree.data.Range;
import org.jfree.data.category.CategoryDataset;
import org.jfree.data.general.DatasetUtilities;
import org.jfree.data.xy.NormalizedMatrixSeries;
import org.jfree.util.BooleanUtilities;
import org.jfree.util.PublicCloneable;

public class StackedBarRenderer3D extends BarRenderer3D implements Cloneable, PublicCloneable, Serializable {
    private static final long serialVersionUID = -5832945916493247123L;
    private boolean ignoreZeroValues;
    private boolean renderAsPercentages;

    public StackedBarRenderer3D() {
        this(false);
    }

    public StackedBarRenderer3D(double xOffset, double yOffset) {
        super(xOffset, yOffset);
    }

    public StackedBarRenderer3D(boolean renderAsPercentages) {
        this.renderAsPercentages = renderAsPercentages;
    }

    public StackedBarRenderer3D(double xOffset, double yOffset, boolean renderAsPercentages) {
        super(xOffset, yOffset);
        this.renderAsPercentages = renderAsPercentages;
    }

    public boolean getRenderAsPercentages() {
        return this.renderAsPercentages;
    }

    public void setRenderAsPercentages(boolean asPercentages) {
        this.renderAsPercentages = asPercentages;
        fireChangeEvent();
    }

    public boolean getIgnoreZeroValues() {
        return this.ignoreZeroValues;
    }

    public void setIgnoreZeroValues(boolean ignore) {
        this.ignoreZeroValues = ignore;
        notifyListeners(new RendererChangeEvent(this));
    }

    public Range findRangeBounds(CategoryDataset dataset) {
        if (dataset == null) {
            return null;
        }
        if (this.renderAsPercentages) {
            return new Range(0.0d, NormalizedMatrixSeries.DEFAULT_SCALE_FACTOR);
        }
        return DatasetUtilities.findStackedRangeBounds(dataset);
    }

    protected void calculateBarWidth(CategoryPlot plot, Rectangle2D dataArea, int rendererIndex, CategoryItemRendererState state) {
        CategoryAxis domainAxis = getDomainAxis(plot, rendererIndex);
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
                categoryMargin = domainAxis.getCategoryMargin();
            }
            double used = space * (((NormalizedMatrixSeries.DEFAULT_SCALE_FACTOR - domainAxis.getLowerMargin()) - domainAxis.getUpperMargin()) - categoryMargin);
            if (columns > 0) {
                state.setBarWidth(Math.min(used / ((double) columns), maxWidth));
                return;
            }
            state.setBarWidth(Math.min(used, maxWidth));
        }
    }

    protected List createStackedValueList(CategoryDataset dataset, Comparable category, double base, boolean asPercentages) {
        int[] rows = new int[dataset.getRowCount()];
        for (int i = 0; i < rows.length; i++) {
            rows[i] = i;
        }
        return createStackedValueList(dataset, category, rows, base, asPercentages);
    }

    protected List createStackedValueList(CategoryDataset dataset, Comparable category, int[] includedRows, double base, boolean asPercentages) {
        List result = new ArrayList();
        double posBase = base;
        double negBase = base;
        double total = 0.0d;
        if (asPercentages) {
            total = DataUtilities.calculateColumnTotal(dataset, dataset.getColumnIndex(category), includedRows);
        }
        int baseIndex = -1;
        for (int r : includedRows) {
            Number n = dataset.getValue(dataset.getRowKey(r), category);
            if (n != null) {
                double v = n.doubleValue();
                if (asPercentages) {
                    v /= total;
                }
                if (v > 0.0d || (!this.ignoreZeroValues && v >= 0.0d)) {
                    if (baseIndex < 0) {
                        result.add(new Object[]{null, new Double(base)});
                        baseIndex = 0;
                    }
                    posBase += v;
                    result.add(new Object[]{new Integer(r), new Double(posBase)});
                } else if (v < 0.0d) {
                    if (baseIndex < 0) {
                        result.add(new Object[]{null, new Double(base)});
                        baseIndex = 0;
                    }
                    negBase += v;
                    r19 = new Object[2];
                    r19[0] = new Integer((-r) - 1);
                    r19[1] = new Double(negBase);
                    result.add(0, r19);
                    baseIndex++;
                }
            }
        }
        return result;
    }

    public void drawItem(Graphics2D g2, CategoryItemRendererState state, Rectangle2D dataArea, CategoryPlot plot, CategoryAxis domainAxis, ValueAxis rangeAxis, CategoryDataset dataset, int row, int column, int pass) {
        if (row >= dataset.getRowCount() - 1) {
            Comparable category = dataset.getColumnKey(column);
            List values = createStackedValueList(dataset, dataset.getColumnKey(column), state.getVisibleSeriesArray(), getBase(), this.renderAsPercentages);
            Rectangle2D adjusted = new Double(dataArea.getX(), dataArea.getY() + getYOffset(), dataArea.getWidth() - getXOffset(), dataArea.getHeight() - getYOffset());
            if (plot.getOrientation() == PlotOrientation.HORIZONTAL) {
                drawStackHorizontal(values, category, g2, state, adjusted, plot, domainAxis, rangeAxis, dataset);
            } else {
                drawStackVertical(values, category, g2, state, adjusted, plot, domainAxis, rangeAxis, dataset);
            }
        }
    }

    protected void drawStackHorizontal(List values, Comparable category, Graphics2D g2, CategoryItemRendererState state, Rectangle2D dataArea, CategoryPlot plot, CategoryAxis domainAxis, ValueAxis rangeAxis, CategoryDataset dataset) {
        int column = dataset.getColumnIndex(category);
        double barX0 = domainAxis.getCategoryMiddle(column, dataset.getColumnCount(), dataArea, plot.getDomainAxisEdge()) - (state.getBarWidth() / DateAxis.DEFAULT_AUTO_RANGE_MINIMUM_SIZE_IN_MILLISECONDS);
        double barW = state.getBarWidth();
        List itemLabelList = new ArrayList();
        boolean inverted = rangeAxis.isInverted();
        int blockCount = values.size() - 1;
        for (int k = 0; k < blockCount; k++) {
            int index;
            int series;
            if (inverted) {
                index = (blockCount - k) - 1;
            } else {
                index = k;
            }
            Object[] prev = (Object[]) values.get(index);
            Object[] curr = (Object[]) values.get(index + 1);
            if (curr[0] == null) {
                series = (-((Integer) prev[0]).intValue()) - 1;
            } else {
                series = ((Integer) curr[0]).intValue();
                if (series < 0) {
                    series = (-((Integer) prev[0]).intValue()) - 1;
                }
            }
            double v0 = ((Double) prev[1]).doubleValue();
            Shape[] faces = createHorizontalBlock(barX0, barW, rangeAxis.valueToJava2D(v0, dataArea, plot.getRangeAxisEdge()), rangeAxis.valueToJava2D(((Double) curr[1]).doubleValue(), dataArea, plot.getRangeAxisEdge()), inverted);
            Paint fillPaint = getItemPaint(series, column);
            Paint fillPaintDark = PaintAlpha.darker(fillPaint);
            boolean drawOutlines = isDrawBarOutline();
            Paint outlinePaint = fillPaint;
            if (drawOutlines) {
                outlinePaint = getItemOutlinePaint(series, column);
                g2.setStroke(getItemOutlineStroke(series, column));
            }
            for (int f = 0; f < 6; f++) {
                if (f == 5) {
                    g2.setPaint(fillPaint);
                } else {
                    g2.setPaint(fillPaintDark);
                }
                g2.fill(faces[f]);
                if (drawOutlines) {
                    g2.setPaint(outlinePaint);
                    g2.draw(faces[f]);
                }
            }
            Object obj = new Object[3];
            obj[0] = new Integer(series);
            obj[1] = faces[5].getBounds2D();
            obj[2] = BooleanUtilities.valueOf(v0 < getBase());
            itemLabelList.add(obj);
            EntityCollection entities = state.getEntityCollection();
            if (entities != null) {
                addItemEntity(entities, dataset, series, column, faces[5]);
            }
        }
        for (int i = 0; i < itemLabelList.size(); i++) {
            Object[] record = (Object[]) itemLabelList.get(i);
            series = ((Integer) record[0]).intValue();
            Rectangle2D bar = record[1];
            boolean neg = ((Boolean) record[2]).booleanValue();
            CategoryItemLabelGenerator generator = getItemLabelGenerator(series, column);
            if (generator != null && isItemLabelVisible(series, column)) {
                drawItemLabel(g2, dataset, series, column, plot, generator, bar, neg);
            }
        }
    }

    private Shape[] createHorizontalBlock(double x0, double width, double y0, double y1, boolean inverted) {
        Shape[] result = new Shape[6];
        Point2D p00 = new Point2D.Double(y0, x0);
        Point2D p01 = new Point2D.Double(y0, x0 + width);
        Point2D p02 = new Point2D.Double(p01.getX() + getXOffset(), p01.getY() - getYOffset());
        Point2D p03 = new Point2D.Double(p00.getX() + getXOffset(), p00.getY() - getYOffset());
        Point2D p0 = new Point2D.Double(y1, x0);
        Point2D p1 = new Point2D.Double(y1, x0 + width);
        Point2D p2 = new Point2D.Double(p1.getX() + getXOffset(), p1.getY() - getYOffset());
        Point2D p3 = new Point2D.Double(p0.getX() + getXOffset(), p0.getY() - getYOffset());
        GeneralPath bottom = new GeneralPath();
        bottom.moveTo((float) p1.getX(), (float) p1.getY());
        bottom.lineTo((float) p01.getX(), (float) p01.getY());
        bottom.lineTo((float) p02.getX(), (float) p02.getY());
        bottom.lineTo((float) p2.getX(), (float) p2.getY());
        bottom.closePath();
        GeneralPath top = new GeneralPath();
        top.moveTo((float) p0.getX(), (float) p0.getY());
        top.lineTo((float) p00.getX(), (float) p00.getY());
        top.lineTo((float) p03.getX(), (float) p03.getY());
        top.lineTo((float) p3.getX(), (float) p3.getY());
        top.closePath();
        GeneralPath back = new GeneralPath();
        back.moveTo((float) p2.getX(), (float) p2.getY());
        back.lineTo((float) p02.getX(), (float) p02.getY());
        back.lineTo((float) p03.getX(), (float) p03.getY());
        back.lineTo((float) p3.getX(), (float) p3.getY());
        back.closePath();
        GeneralPath front = new GeneralPath();
        front.moveTo((float) p0.getX(), (float) p0.getY());
        front.lineTo((float) p1.getX(), (float) p1.getY());
        front.lineTo((float) p01.getX(), (float) p01.getY());
        front.lineTo((float) p00.getX(), (float) p00.getY());
        front.closePath();
        GeneralPath left = new GeneralPath();
        left.moveTo((float) p0.getX(), (float) p0.getY());
        left.lineTo((float) p1.getX(), (float) p1.getY());
        left.lineTo((float) p2.getX(), (float) p2.getY());
        left.lineTo((float) p3.getX(), (float) p3.getY());
        left.closePath();
        GeneralPath right = new GeneralPath();
        right.moveTo((float) p00.getX(), (float) p00.getY());
        right.lineTo((float) p01.getX(), (float) p01.getY());
        right.lineTo((float) p02.getX(), (float) p02.getY());
        right.lineTo((float) p03.getX(), (float) p03.getY());
        right.closePath();
        result[0] = bottom;
        result[1] = back;
        if (inverted) {
            result[2] = right;
            result[3] = left;
        } else {
            result[2] = left;
            result[3] = right;
        }
        result[4] = top;
        result[5] = front;
        return result;
    }

    protected void drawStackVertical(List values, Comparable category, Graphics2D g2, CategoryItemRendererState state, Rectangle2D dataArea, CategoryPlot plot, CategoryAxis domainAxis, ValueAxis rangeAxis, CategoryDataset dataset) {
        int column = dataset.getColumnIndex(category);
        double barX0 = domainAxis.getCategoryMiddle(column, dataset.getColumnCount(), dataArea, plot.getDomainAxisEdge()) - (state.getBarWidth() / DateAxis.DEFAULT_AUTO_RANGE_MINIMUM_SIZE_IN_MILLISECONDS);
        double barW = state.getBarWidth();
        List itemLabelList = new ArrayList();
        boolean inverted = rangeAxis.isInverted();
        int blockCount = values.size() - 1;
        for (int k = 0; k < blockCount; k++) {
            int index;
            int series;
            if (inverted) {
                index = (blockCount - k) - 1;
            } else {
                index = k;
            }
            Object[] prev = (Object[]) values.get(index);
            Object[] curr = (Object[]) values.get(index + 1);
            if (curr[0] == null) {
                series = (-((Integer) prev[0]).intValue()) - 1;
            } else {
                series = ((Integer) curr[0]).intValue();
                if (series < 0) {
                    series = (-((Integer) prev[0]).intValue()) - 1;
                }
            }
            double v0 = ((Double) prev[1]).doubleValue();
            Shape[] faces = createVerticalBlock(barX0, barW, rangeAxis.valueToJava2D(v0, dataArea, plot.getRangeAxisEdge()), rangeAxis.valueToJava2D(((Double) curr[1]).doubleValue(), dataArea, plot.getRangeAxisEdge()), inverted);
            Paint fillPaint = getItemPaint(series, column);
            Paint fillPaintDark = PaintAlpha.darker(fillPaint);
            boolean drawOutlines = isDrawBarOutline();
            Paint outlinePaint = fillPaint;
            if (drawOutlines) {
                outlinePaint = getItemOutlinePaint(series, column);
                g2.setStroke(getItemOutlineStroke(series, column));
            }
            for (int f = 0; f < 6; f++) {
                if (f == 5) {
                    g2.setPaint(fillPaint);
                } else {
                    g2.setPaint(fillPaintDark);
                }
                g2.fill(faces[f]);
                if (drawOutlines) {
                    g2.setPaint(outlinePaint);
                    g2.draw(faces[f]);
                }
            }
            Object obj = new Object[3];
            obj[0] = new Integer(series);
            obj[1] = faces[5].getBounds2D();
            obj[2] = BooleanUtilities.valueOf(v0 < getBase());
            itemLabelList.add(obj);
            EntityCollection entities = state.getEntityCollection();
            if (entities != null) {
                addItemEntity(entities, dataset, series, column, faces[5]);
            }
        }
        for (int i = 0; i < itemLabelList.size(); i++) {
            Object[] record = (Object[]) itemLabelList.get(i);
            series = ((Integer) record[0]).intValue();
            Rectangle2D bar = record[1];
            boolean neg = ((Boolean) record[2]).booleanValue();
            CategoryItemLabelGenerator generator = getItemLabelGenerator(series, column);
            if (generator != null && isItemLabelVisible(series, column)) {
                drawItemLabel(g2, dataset, series, column, plot, generator, bar, neg);
            }
        }
    }

    private Shape[] createVerticalBlock(double x0, double width, double y0, double y1, boolean inverted) {
        Shape[] result = new Shape[6];
        Point2D p00 = new Point2D.Double(x0, y0);
        Point2D p01 = new Point2D.Double(x0 + width, y0);
        Point2D p02 = new Point2D.Double(p01.getX() + getXOffset(), p01.getY() - getYOffset());
        Point2D p03 = new Point2D.Double(p00.getX() + getXOffset(), p00.getY() - getYOffset());
        Point2D p0 = new Point2D.Double(x0, y1);
        Point2D p1 = new Point2D.Double(x0 + width, y1);
        Point2D p2 = new Point2D.Double(p1.getX() + getXOffset(), p1.getY() - getYOffset());
        Point2D p3 = new Point2D.Double(p0.getX() + getXOffset(), p0.getY() - getYOffset());
        GeneralPath right = new GeneralPath();
        right.moveTo((float) p1.getX(), (float) p1.getY());
        right.lineTo((float) p01.getX(), (float) p01.getY());
        right.lineTo((float) p02.getX(), (float) p02.getY());
        right.lineTo((float) p2.getX(), (float) p2.getY());
        right.closePath();
        GeneralPath left = new GeneralPath();
        left.moveTo((float) p0.getX(), (float) p0.getY());
        left.lineTo((float) p00.getX(), (float) p00.getY());
        left.lineTo((float) p03.getX(), (float) p03.getY());
        left.lineTo((float) p3.getX(), (float) p3.getY());
        left.closePath();
        GeneralPath back = new GeneralPath();
        back.moveTo((float) p2.getX(), (float) p2.getY());
        back.lineTo((float) p02.getX(), (float) p02.getY());
        back.lineTo((float) p03.getX(), (float) p03.getY());
        back.lineTo((float) p3.getX(), (float) p3.getY());
        back.closePath();
        GeneralPath front = new GeneralPath();
        front.moveTo((float) p0.getX(), (float) p0.getY());
        front.lineTo((float) p1.getX(), (float) p1.getY());
        front.lineTo((float) p01.getX(), (float) p01.getY());
        front.lineTo((float) p00.getX(), (float) p00.getY());
        front.closePath();
        GeneralPath top = new GeneralPath();
        top.moveTo((float) p0.getX(), (float) p0.getY());
        top.lineTo((float) p1.getX(), (float) p1.getY());
        top.lineTo((float) p2.getX(), (float) p2.getY());
        top.lineTo((float) p3.getX(), (float) p3.getY());
        top.closePath();
        GeneralPath bottom = new GeneralPath();
        bottom.moveTo((float) p00.getX(), (float) p00.getY());
        bottom.lineTo((float) p01.getX(), (float) p01.getY());
        bottom.lineTo((float) p02.getX(), (float) p02.getY());
        bottom.lineTo((float) p03.getX(), (float) p03.getY());
        bottom.closePath();
        result[0] = bottom;
        result[1] = back;
        result[2] = left;
        result[3] = right;
        result[4] = top;
        result[5] = front;
        if (inverted) {
            result[0] = top;
            result[4] = bottom;
        }
        return result;
    }

    public boolean equals(Object obj) {
        if (obj == this) {
            return true;
        }
        if (!(obj instanceof StackedBarRenderer3D)) {
            return false;
        }
        StackedBarRenderer3D that = (StackedBarRenderer3D) obj;
        if (this.renderAsPercentages == that.getRenderAsPercentages() && this.ignoreZeroValues == that.ignoreZeroValues) {
            return super.equals(obj);
        }
        return false;
    }

    public int hashCode() {
        return HashUtilities.hashCode(HashUtilities.hashCode(super.hashCode(), this.renderAsPercentages), this.ignoreZeroValues);
    }
}
