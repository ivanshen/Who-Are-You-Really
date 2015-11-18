package org.jfree.chart.renderer.category;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.GradientPaint;
import java.awt.Graphics2D;
import java.awt.Paint;
import java.awt.Stroke;
import java.awt.geom.Line2D;
import java.awt.geom.Rectangle2D;
import java.awt.geom.Rectangle2D.Double;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import org.jfree.chart.annotations.XYPointerAnnotation;
import org.jfree.chart.axis.CategoryAxis;
import org.jfree.chart.axis.DateAxis;
import org.jfree.chart.axis.ValueAxis;
import org.jfree.chart.entity.EntityCollection;
import org.jfree.chart.labels.CategoryItemLabelGenerator;
import org.jfree.chart.plot.CategoryPlot;
import org.jfree.chart.plot.Plot;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.plot.SpiderWebPlot;
import org.jfree.data.Range;
import org.jfree.data.category.CategoryDataset;
import org.jfree.data.statistics.StatisticalCategoryDataset;
import org.jfree.io.SerialUtilities;
import org.jfree.ui.GradientPaintTransformer;
import org.jfree.ui.RectangleEdge;
import org.jfree.util.ObjectUtilities;
import org.jfree.util.PaintUtilities;
import org.jfree.util.PublicCloneable;

public class StatisticalBarRenderer extends BarRenderer implements CategoryItemRenderer, Cloneable, PublicCloneable, Serializable {
    private static final long serialVersionUID = -4986038395414039117L;
    private transient Paint errorIndicatorPaint;
    private transient Stroke errorIndicatorStroke;

    public StatisticalBarRenderer() {
        this.errorIndicatorPaint = Color.gray;
        this.errorIndicatorStroke = new BasicStroke(Plot.DEFAULT_FOREGROUND_ALPHA);
    }

    public Paint getErrorIndicatorPaint() {
        return this.errorIndicatorPaint;
    }

    public void setErrorIndicatorPaint(Paint paint) {
        this.errorIndicatorPaint = paint;
        fireChangeEvent();
    }

    public Stroke getErrorIndicatorStroke() {
        return this.errorIndicatorStroke;
    }

    public void setErrorIndicatorStroke(Stroke stroke) {
        this.errorIndicatorStroke = stroke;
        fireChangeEvent();
    }

    public Range findRangeBounds(CategoryDataset dataset) {
        return findRangeBounds(dataset, true);
    }

    public void drawItem(Graphics2D g2, CategoryItemRendererState state, Rectangle2D dataArea, CategoryPlot plot, CategoryAxis domainAxis, ValueAxis rangeAxis, CategoryDataset data, int row, int column, int pass) {
        int visibleRow = state.getVisibleSeriesIndex(row);
        if (visibleRow >= 0) {
            if (data instanceof StatisticalCategoryDataset) {
                StatisticalCategoryDataset statData = (StatisticalCategoryDataset) data;
                PlotOrientation orientation = plot.getOrientation();
                if (orientation == PlotOrientation.HORIZONTAL) {
                    drawHorizontalItem(g2, state, dataArea, plot, domainAxis, rangeAxis, statData, visibleRow, row, column);
                    return;
                } else if (orientation == PlotOrientation.VERTICAL) {
                    drawVerticalItem(g2, state, dataArea, plot, domainAxis, rangeAxis, statData, visibleRow, row, column);
                    return;
                } else {
                    return;
                }
            }
            throw new IllegalArgumentException("Requires StatisticalCategoryDataset.");
        }
    }

    protected void drawHorizontalItem(Graphics2D g2, CategoryItemRendererState state, Rectangle2D dataArea, CategoryPlot plot, CategoryAxis domainAxis, ValueAxis rangeAxis, StatisticalCategoryDataset dataset, int visibleRow, int row, int column) {
        double rectY = calculateBarW0(plot, PlotOrientation.HORIZONTAL, dataArea, domainAxis, state, visibleRow, column);
        Number meanValue = dataset.getMeanValue(row, column);
        if (meanValue != null) {
            double value = meanValue.doubleValue();
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
            RectangleEdge yAxisLocation = plot.getRangeAxisEdge();
            double transY1 = rangeAxis.valueToJava2D(base, dataArea, yAxisLocation);
            double transY2 = rangeAxis.valueToJava2D(value, dataArea, yAxisLocation);
            double rectX = Math.min(transY2, transY1);
            double rectHeight = state.getBarWidth();
            Rectangle2D bar = new Double(rectX, rectY, Math.abs(transY2 - transY1), rectHeight);
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
            Number n = dataset.getStdDevValue(row, column);
            if (n != null) {
                double valueDelta = n.doubleValue();
                double highVal = rangeAxis.valueToJava2D(meanValue.doubleValue() + valueDelta, dataArea, yAxisLocation);
                double lowVal = rangeAxis.valueToJava2D(meanValue.doubleValue() - valueDelta, dataArea, yAxisLocation);
                if (this.errorIndicatorPaint != null) {
                    g2.setPaint(this.errorIndicatorPaint);
                } else {
                    g2.setPaint(getItemOutlinePaint(row, column));
                }
                if (this.errorIndicatorStroke != null) {
                    g2.setStroke(this.errorIndicatorStroke);
                } else {
                    g2.setStroke(getItemOutlineStroke(row, column));
                }
                g2.draw(new Line2D.Double(lowVal, (rectHeight / DateAxis.DEFAULT_AUTO_RANGE_MINIMUM_SIZE_IN_MILLISECONDS) + rectY, highVal, rectY + (rectHeight / DateAxis.DEFAULT_AUTO_RANGE_MINIMUM_SIZE_IN_MILLISECONDS)));
                g2.draw(new Line2D.Double(highVal, rectY + (SpiderWebPlot.DEFAULT_INTERIOR_GAP * rectHeight), highVal, rectY + (0.75d * rectHeight)));
                g2.draw(new Line2D.Double(lowVal, rectY + (SpiderWebPlot.DEFAULT_INTERIOR_GAP * rectHeight), lowVal, rectY + (0.75d * rectHeight)));
            }
            CategoryItemLabelGenerator generator = getItemLabelGenerator(row, column);
            if (generator != null && isItemLabelVisible(row, column)) {
                drawItemLabel(g2, dataset, row, column, plot, generator, bar, value < 0.0d);
            }
            EntityCollection entities = state.getEntityCollection();
            if (entities != null) {
                addItemEntity(entities, dataset, row, column, bar);
            }
        }
    }

    protected void drawVerticalItem(Graphics2D g2, CategoryItemRendererState state, Rectangle2D dataArea, CategoryPlot plot, CategoryAxis domainAxis, ValueAxis rangeAxis, StatisticalCategoryDataset dataset, int visibleRow, int row, int column) {
        double rectX = calculateBarW0(plot, PlotOrientation.VERTICAL, dataArea, domainAxis, state, visibleRow, column);
        Number meanValue = dataset.getMeanValue(row, column);
        if (meanValue != null) {
            double value = meanValue.doubleValue();
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
            RectangleEdge yAxisLocation = plot.getRangeAxisEdge();
            double transY1 = rangeAxis.valueToJava2D(base, dataArea, yAxisLocation);
            double transY2 = rangeAxis.valueToJava2D(value, dataArea, yAxisLocation);
            double rectY = Math.min(transY2, transY1);
            double rectWidth = state.getBarWidth();
            Rectangle2D bar = new Double(rectX, rectY, rectWidth, Math.abs(transY2 - transY1));
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
            Number n = dataset.getStdDevValue(row, column);
            if (n != null) {
                double valueDelta = n.doubleValue();
                double highVal = rangeAxis.valueToJava2D(meanValue.doubleValue() + valueDelta, dataArea, yAxisLocation);
                double lowVal = rangeAxis.valueToJava2D(meanValue.doubleValue() - valueDelta, dataArea, yAxisLocation);
                if (this.errorIndicatorPaint != null) {
                    g2.setPaint(this.errorIndicatorPaint);
                } else {
                    g2.setPaint(getItemOutlinePaint(row, column));
                }
                if (this.errorIndicatorStroke != null) {
                    g2.setStroke(this.errorIndicatorStroke);
                } else {
                    g2.setStroke(getItemOutlineStroke(row, column));
                }
                g2.draw(new Line2D.Double((rectWidth / DateAxis.DEFAULT_AUTO_RANGE_MINIMUM_SIZE_IN_MILLISECONDS) + rectX, lowVal, (rectWidth / DateAxis.DEFAULT_AUTO_RANGE_MINIMUM_SIZE_IN_MILLISECONDS) + rectX, highVal));
                g2.draw(new Line2D.Double(((rectWidth / DateAxis.DEFAULT_AUTO_RANGE_MINIMUM_SIZE_IN_MILLISECONDS) + rectX) - XYPointerAnnotation.DEFAULT_ARROW_LENGTH, highVal, ((rectWidth / DateAxis.DEFAULT_AUTO_RANGE_MINIMUM_SIZE_IN_MILLISECONDS) + rectX) + XYPointerAnnotation.DEFAULT_ARROW_LENGTH, highVal));
                g2.draw(new Line2D.Double(((rectWidth / DateAxis.DEFAULT_AUTO_RANGE_MINIMUM_SIZE_IN_MILLISECONDS) + rectX) - XYPointerAnnotation.DEFAULT_ARROW_LENGTH, lowVal, ((rectWidth / DateAxis.DEFAULT_AUTO_RANGE_MINIMUM_SIZE_IN_MILLISECONDS) + rectX) + XYPointerAnnotation.DEFAULT_ARROW_LENGTH, lowVal));
            }
            CategoryItemLabelGenerator generator = getItemLabelGenerator(row, column);
            if (generator != null && isItemLabelVisible(row, column)) {
                drawItemLabel(g2, dataset, row, column, plot, generator, bar, value < 0.0d);
            }
            EntityCollection entities = state.getEntityCollection();
            if (entities != null) {
                addItemEntity(entities, dataset, row, column, bar);
            }
        }
    }

    public boolean equals(Object obj) {
        if (obj == this) {
            return true;
        }
        if (!(obj instanceof StatisticalBarRenderer)) {
            return false;
        }
        StatisticalBarRenderer that = (StatisticalBarRenderer) obj;
        if (PaintUtilities.equal(this.errorIndicatorPaint, that.errorIndicatorPaint) && ObjectUtilities.equal(this.errorIndicatorStroke, that.errorIndicatorStroke)) {
            return super.equals(obj);
        }
        return false;
    }

    private void writeObject(ObjectOutputStream stream) throws IOException {
        stream.defaultWriteObject();
        SerialUtilities.writePaint(this.errorIndicatorPaint, stream);
        SerialUtilities.writeStroke(this.errorIndicatorStroke, stream);
    }

    private void readObject(ObjectInputStream stream) throws IOException, ClassNotFoundException {
        stream.defaultReadObject();
        this.errorIndicatorPaint = SerialUtilities.readPaint(stream);
        this.errorIndicatorStroke = SerialUtilities.readStroke(stream);
    }
}
