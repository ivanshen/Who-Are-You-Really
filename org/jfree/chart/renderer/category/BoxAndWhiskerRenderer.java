package org.jfree.chart.renderer.category;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Paint;
import java.awt.Shape;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Line2D;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.awt.geom.Rectangle2D.Double;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import org.jfree.chart.LegendItem;
import org.jfree.chart.axis.CategoryAxis;
import org.jfree.chart.axis.DateAxis;
import org.jfree.chart.axis.ValueAxis;
import org.jfree.chart.entity.EntityCollection;
import org.jfree.chart.plot.CategoryPlot;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.plot.PlotRenderingInfo;
import org.jfree.chart.renderer.Outlier;
import org.jfree.chart.renderer.OutlierList;
import org.jfree.chart.renderer.OutlierListCollection;
import org.jfree.chart.renderer.xy.XYLine3DRenderer;
import org.jfree.chart.util.ParamChecks;
import org.jfree.data.Range;
import org.jfree.data.category.CategoryDataset;
import org.jfree.data.statistics.BoxAndWhiskerCategoryDataset;
import org.jfree.data.xy.NormalizedMatrixSeries;
import org.jfree.io.SerialUtilities;
import org.jfree.ui.RectangleEdge;
import org.jfree.util.PaintUtilities;
import org.jfree.util.PublicCloneable;

public class BoxAndWhiskerRenderer extends AbstractCategoryItemRenderer implements Cloneable, PublicCloneable, Serializable {
    private static final long serialVersionUID = 632027470694481177L;
    private transient Paint artifactPaint;
    private boolean fillBox;
    private double itemMargin;
    private double maximumBarWidth;
    private boolean meanVisible;
    private boolean medianVisible;
    private boolean useOutlinePaintForWhiskers;
    private double whiskerWidth;

    public BoxAndWhiskerRenderer() {
        this.artifactPaint = Color.black;
        this.fillBox = true;
        this.itemMargin = LevelRenderer.DEFAULT_ITEM_MARGIN;
        this.maximumBarWidth = NormalizedMatrixSeries.DEFAULT_SCALE_FACTOR;
        this.medianVisible = true;
        this.meanVisible = true;
        this.useOutlinePaintForWhiskers = false;
        this.whiskerWidth = NormalizedMatrixSeries.DEFAULT_SCALE_FACTOR;
        setBaseLegendShape(new Double(-4.0d, -4.0d, XYLine3DRenderer.DEFAULT_Y_OFFSET, XYLine3DRenderer.DEFAULT_Y_OFFSET));
    }

    public Paint getArtifactPaint() {
        return this.artifactPaint;
    }

    public void setArtifactPaint(Paint paint) {
        ParamChecks.nullNotPermitted(paint, "paint");
        this.artifactPaint = paint;
        fireChangeEvent();
    }

    public boolean getFillBox() {
        return this.fillBox;
    }

    public void setFillBox(boolean flag) {
        this.fillBox = flag;
        fireChangeEvent();
    }

    public double getItemMargin() {
        return this.itemMargin;
    }

    public void setItemMargin(double margin) {
        this.itemMargin = margin;
        fireChangeEvent();
    }

    public double getMaximumBarWidth() {
        return this.maximumBarWidth;
    }

    public void setMaximumBarWidth(double percent) {
        this.maximumBarWidth = percent;
        fireChangeEvent();
    }

    public boolean isMeanVisible() {
        return this.meanVisible;
    }

    public void setMeanVisible(boolean visible) {
        if (this.meanVisible != visible) {
            this.meanVisible = visible;
            fireChangeEvent();
        }
    }

    public boolean isMedianVisible() {
        return this.medianVisible;
    }

    public void setMedianVisible(boolean visible) {
        if (this.medianVisible != visible) {
            this.medianVisible = visible;
            fireChangeEvent();
        }
    }

    public boolean getUseOutlinePaintForWhiskers() {
        return this.useOutlinePaintForWhiskers;
    }

    public void setUseOutlinePaintForWhiskers(boolean flag) {
        if (this.useOutlinePaintForWhiskers != flag) {
            this.useOutlinePaintForWhiskers = flag;
            fireChangeEvent();
        }
    }

    public double getWhiskerWidth() {
        return this.whiskerWidth;
    }

    public void setWhiskerWidth(double width) {
        if (width < 0.0d || width > NormalizedMatrixSeries.DEFAULT_SCALE_FACTOR) {
            throw new IllegalArgumentException("Value for whisker width out of range");
        } else if (width != this.whiskerWidth) {
            this.whiskerWidth = width;
            fireChangeEvent();
        }
    }

    public LegendItem getLegendItem(int datasetIndex, int series) {
        LegendItem legendItem = null;
        CategoryPlot cp = getPlot();
        if (cp != null && isSeriesVisible(series) && isSeriesVisibleInLegend(series)) {
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
            legendItem = new LegendItem(label, description, toolTipText, urlText, lookupLegendShape(series), lookupSeriesPaint(series), lookupSeriesOutlineStroke(series), lookupSeriesOutlinePaint(series));
            legendItem.setLabelFont(lookupLegendTextFont(series));
            Paint labelPaint = lookupLegendTextPaint(series);
            if (labelPaint != null) {
                legendItem.setLabelPaint(labelPaint);
            }
            legendItem.setDataset(dataset);
            legendItem.setDatasetIndex(datasetIndex);
            legendItem.setSeriesKey(dataset.getRowKey(series));
            legendItem.setSeriesIndex(series);
        }
        return legendItem;
    }

    public Range findRangeBounds(CategoryDataset dataset) {
        return super.findRangeBounds(dataset, true);
    }

    public CategoryItemRendererState initialise(Graphics2D g2, Rectangle2D dataArea, CategoryPlot plot, int rendererIndex, PlotRenderingInfo info) {
        CategoryItemRendererState state = super.initialise(g2, dataArea, plot, rendererIndex, info);
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
            double currentItemMargin = 0.0d;
            if (columns > 1) {
                categoryMargin = domainAxis.getCategoryMargin();
            }
            if (rows > 1) {
                currentItemMargin = getItemMargin();
            }
            double used = space * ((((NormalizedMatrixSeries.DEFAULT_SCALE_FACTOR - domainAxis.getLowerMargin()) - domainAxis.getUpperMargin()) - categoryMargin) - currentItemMargin);
            if (rows * columns > 0) {
                state.setBarWidth(Math.min(used / ((double) (dataset.getColumnCount() * dataset.getRowCount())), maxWidth));
            } else {
                state.setBarWidth(Math.min(used, maxWidth));
            }
        }
        return state;
    }

    public void drawItem(Graphics2D g2, CategoryItemRendererState state, Rectangle2D dataArea, CategoryPlot plot, CategoryAxis domainAxis, ValueAxis rangeAxis, CategoryDataset dataset, int row, int column, int pass) {
        if (!getItemVisible(row, column)) {
            return;
        }
        if (dataset instanceof BoxAndWhiskerCategoryDataset) {
            PlotOrientation orientation = plot.getOrientation();
            if (orientation == PlotOrientation.HORIZONTAL) {
                drawHorizontalItem(g2, state, dataArea, plot, domainAxis, rangeAxis, dataset, row, column);
                return;
            } else if (orientation == PlotOrientation.VERTICAL) {
                drawVerticalItem(g2, state, dataArea, plot, domainAxis, rangeAxis, dataset, row, column);
                return;
            } else {
                return;
            }
        }
        throw new IllegalArgumentException("BoxAndWhiskerRenderer.drawItem() : the data should be of type BoxAndWhiskerCategoryDataset only.");
    }

    public void drawHorizontalItem(Graphics2D g2, CategoryItemRendererState state, Rectangle2D dataArea, CategoryPlot plot, CategoryAxis domainAxis, ValueAxis rangeAxis, CategoryDataset dataset, int row, int column) {
        BoxAndWhiskerCategoryDataset bawDataset = (BoxAndWhiskerCategoryDataset) dataset;
        double categoryEnd = domainAxis.getCategoryEnd(column, getColumnCount(), dataArea, plot.getDomainAxisEdge());
        double categoryStart = domainAxis.getCategoryStart(column, getColumnCount(), dataArea, plot.getDomainAxisEdge());
        double categoryWidth = Math.abs(categoryEnd - categoryStart);
        double yy = categoryStart;
        int seriesCount = getRowCount();
        int categoryCount = getColumnCount();
        if (seriesCount > 1) {
            double seriesGap = (dataArea.getHeight() * getItemMargin()) / ((double) ((seriesCount - 1) * categoryCount));
            yy = (yy + ((categoryWidth - ((state.getBarWidth() * ((double) seriesCount)) + (((double) (seriesCount - 1)) * seriesGap))) / DateAxis.DEFAULT_AUTO_RANGE_MINIMUM_SIZE_IN_MILLISECONDS)) + (((double) row) * (state.getBarWidth() + seriesGap));
        } else {
            yy += (categoryWidth - state.getBarWidth()) / DateAxis.DEFAULT_AUTO_RANGE_MINIMUM_SIZE_IN_MILLISECONDS;
        }
        g2.setPaint(getItemPaint(row, column));
        g2.setStroke(getItemStroke(row, column));
        RectangleEdge location = plot.getRangeAxisEdge();
        Number xQ1 = bawDataset.getQ1Value(row, column);
        Number xQ3 = bawDataset.getQ3Value(row, column);
        Number xMax = bawDataset.getMaxRegularValue(row, column);
        Number xMin = bawDataset.getMinRegularValue(row, column);
        Shape box = null;
        if (!(xQ1 == null || xQ3 == null || xMax == null || xMin == null)) {
            double xxQ1 = rangeAxis.valueToJava2D(xQ1.doubleValue(), dataArea, location);
            double xxQ3 = rangeAxis.valueToJava2D(xQ3.doubleValue(), dataArea, location);
            double xxMax = rangeAxis.valueToJava2D(xMax.doubleValue(), dataArea, location);
            double xxMin = rangeAxis.valueToJava2D(xMin.doubleValue(), dataArea, location);
            double yymid = yy + (state.getBarWidth() / DateAxis.DEFAULT_AUTO_RANGE_MINIMUM_SIZE_IN_MILLISECONDS);
            double halfW = (state.getBarWidth() / DateAxis.DEFAULT_AUTO_RANGE_MINIMUM_SIZE_IN_MILLISECONDS) * this.whiskerWidth;
            box = new Double(Math.min(xxQ1, xxQ3), yy, Math.abs(xxQ1 - xxQ3), state.getBarWidth());
            if (this.fillBox) {
                g2.fill(box);
            }
            Paint outlinePaint = getItemOutlinePaint(row, column);
            if (this.useOutlinePaintForWhiskers) {
                g2.setPaint(outlinePaint);
            }
            g2.draw(new Line2D.Double(xxMax, yymid, xxQ3, yymid));
            g2.draw(new Line2D.Double(xxMax, yymid - halfW, xxMax, yymid + halfW));
            g2.draw(new Line2D.Double(xxMin, yymid, xxQ1, yymid));
            g2.draw(new Line2D.Double(xxMin, yymid - halfW, xxMin, yy + halfW));
            g2.setStroke(getItemOutlineStroke(row, column));
            g2.setPaint(outlinePaint);
            g2.draw(box);
        }
        g2.setPaint(this.artifactPaint);
        if (this.meanVisible) {
            Number xMean = bawDataset.getMeanValue(row, column);
            if (xMean != null) {
                double xxMean = rangeAxis.valueToJava2D(xMean.doubleValue(), dataArea, location);
                double aRadius = state.getBarWidth() / 4.0d;
                if (xxMean > dataArea.getMinX() - aRadius && xxMean < dataArea.getMaxX() + aRadius) {
                    Ellipse2D.Double avgEllipse = new Ellipse2D.Double(xxMean - aRadius, yy + aRadius, aRadius * DateAxis.DEFAULT_AUTO_RANGE_MINIMUM_SIZE_IN_MILLISECONDS, aRadius * DateAxis.DEFAULT_AUTO_RANGE_MINIMUM_SIZE_IN_MILLISECONDS);
                    g2.fill(avgEllipse);
                    g2.draw(avgEllipse);
                }
            }
        }
        if (this.medianVisible) {
            Number xMedian = bawDataset.getMedianValue(row, column);
            if (xMedian != null) {
                double xxMedian = rangeAxis.valueToJava2D(xMedian.doubleValue(), dataArea, location);
                g2.draw(new Line2D.Double(xxMedian, yy, xxMedian, yy + state.getBarWidth()));
            }
        }
        if (state.getInfo() != null && box != null) {
            EntityCollection entities = state.getEntityCollection();
            if (entities != null) {
                addItemEntity(entities, dataset, row, column, box);
            }
        }
    }

    public void drawVerticalItem(Graphics2D g2, CategoryItemRendererState state, Rectangle2D dataArea, CategoryPlot plot, CategoryAxis domainAxis, ValueAxis rangeAxis, CategoryDataset dataset, int row, int column) {
        BoxAndWhiskerCategoryDataset bawDataset = (BoxAndWhiskerCategoryDataset) dataset;
        double categoryEnd = domainAxis.getCategoryEnd(column, getColumnCount(), dataArea, plot.getDomainAxisEdge());
        double categoryStart = domainAxis.getCategoryStart(column, getColumnCount(), dataArea, plot.getDomainAxisEdge());
        double categoryWidth = categoryEnd - categoryStart;
        double xx = categoryStart;
        int seriesCount = getRowCount();
        int categoryCount = getColumnCount();
        if (seriesCount > 1) {
            double seriesGap = (dataArea.getWidth() * getItemMargin()) / ((double) ((seriesCount - 1) * categoryCount));
            xx = (xx + ((categoryWidth - ((state.getBarWidth() * ((double) seriesCount)) + (((double) (seriesCount - 1)) * seriesGap))) / DateAxis.DEFAULT_AUTO_RANGE_MINIMUM_SIZE_IN_MILLISECONDS)) + (((double) row) * (state.getBarWidth() + seriesGap));
        } else {
            xx += (categoryWidth - state.getBarWidth()) / DateAxis.DEFAULT_AUTO_RANGE_MINIMUM_SIZE_IN_MILLISECONDS;
        }
        Paint itemPaint = getItemPaint(row, column);
        g2.setPaint(itemPaint);
        g2.setStroke(getItemStroke(row, column));
        double aRadius = 0.0d;
        RectangleEdge location = plot.getRangeAxisEdge();
        Number yQ1 = bawDataset.getQ1Value(row, column);
        Number yQ3 = bawDataset.getQ3Value(row, column);
        Number yMax = bawDataset.getMaxRegularValue(row, column);
        Number yMin = bawDataset.getMinRegularValue(row, column);
        Shape box = null;
        if (!(yQ1 == null || yQ3 == null || yMax == null || yMin == null)) {
            double yyQ1 = rangeAxis.valueToJava2D(yQ1.doubleValue(), dataArea, location);
            double yyQ3 = rangeAxis.valueToJava2D(yQ3.doubleValue(), dataArea, location);
            double yyMax = rangeAxis.valueToJava2D(yMax.doubleValue(), dataArea, location);
            double yyMin = rangeAxis.valueToJava2D(yMin.doubleValue(), dataArea, location);
            double xxmid = xx + (state.getBarWidth() / DateAxis.DEFAULT_AUTO_RANGE_MINIMUM_SIZE_IN_MILLISECONDS);
            double halfW = (state.getBarWidth() / DateAxis.DEFAULT_AUTO_RANGE_MINIMUM_SIZE_IN_MILLISECONDS) * this.whiskerWidth;
            box = new Double(xx, Math.min(yyQ1, yyQ3), state.getBarWidth(), Math.abs(yyQ1 - yyQ3));
            if (this.fillBox) {
                g2.fill(box);
            }
            Paint outlinePaint = getItemOutlinePaint(row, column);
            if (this.useOutlinePaintForWhiskers) {
                g2.setPaint(outlinePaint);
            }
            g2.draw(new Line2D.Double(xxmid, yyMax, xxmid, yyQ3));
            g2.draw(new Line2D.Double(xxmid - halfW, yyMax, xxmid + halfW, yyMax));
            g2.draw(new Line2D.Double(xxmid, yyMin, xxmid, yyQ1));
            g2.draw(new Line2D.Double(xxmid - halfW, yyMin, xxmid + halfW, yyMin));
            g2.setStroke(getItemOutlineStroke(row, column));
            g2.setPaint(outlinePaint);
            g2.draw(box);
        }
        g2.setPaint(this.artifactPaint);
        if (this.meanVisible) {
            Number yMean = bawDataset.getMeanValue(row, column);
            if (yMean != null) {
                double yyAverage = rangeAxis.valueToJava2D(yMean.doubleValue(), dataArea, location);
                aRadius = state.getBarWidth() / 4.0d;
                if (yyAverage > dataArea.getMinY() - aRadius && yyAverage < dataArea.getMaxY() + aRadius) {
                    Ellipse2D.Double avgEllipse = new Ellipse2D.Double(xx + aRadius, yyAverage - aRadius, aRadius * DateAxis.DEFAULT_AUTO_RANGE_MINIMUM_SIZE_IN_MILLISECONDS, aRadius * DateAxis.DEFAULT_AUTO_RANGE_MINIMUM_SIZE_IN_MILLISECONDS);
                    g2.fill(avgEllipse);
                    g2.draw(avgEllipse);
                }
            }
        }
        if (this.medianVisible) {
            Number yMedian = bawDataset.getMedianValue(row, column);
            if (yMedian != null) {
                double yyMedian = rangeAxis.valueToJava2D(yMedian.doubleValue(), dataArea, location);
                g2.draw(new Line2D.Double(xx, yyMedian, xx + state.getBarWidth(), yyMedian));
            }
        }
        double maxAxisValue = rangeAxis.valueToJava2D(rangeAxis.getUpperBound(), dataArea, location) + aRadius;
        double minAxisValue = rangeAxis.valueToJava2D(rangeAxis.getLowerBound(), dataArea, location) - aRadius;
        g2.setPaint(itemPaint);
        double oRadius = state.getBarWidth() / BarRenderer.BAR_OUTLINE_WIDTH_THRESHOLD;
        List<Outlier> outliers = new ArrayList();
        OutlierListCollection outlierListCollection = new OutlierListCollection();
        List yOutliers = bawDataset.getOutliers(row, column);
        if (yOutliers != null) {
            for (int i = 0; i < yOutliers.size(); i++) {
                double outlier = ((Number) yOutliers.get(i)).doubleValue();
                Number minOutlier = bawDataset.getMinOutlier(row, column);
                Number maxOutlier = bawDataset.getMaxOutlier(row, column);
                Number minRegular = bawDataset.getMinRegularValue(row, column);
                Number maxRegular = bawDataset.getMaxRegularValue(row, column);
                if (outlier > maxOutlier.doubleValue()) {
                    outlierListCollection.setHighFarOut(true);
                } else if (outlier < minOutlier.doubleValue()) {
                    outlierListCollection.setLowFarOut(true);
                } else if (outlier > maxRegular.doubleValue()) {
                    r0 = outliers;
                    r0.add(new Outlier((state.getBarWidth() / DateAxis.DEFAULT_AUTO_RANGE_MINIMUM_SIZE_IN_MILLISECONDS) + xx, rangeAxis.valueToJava2D(outlier, dataArea, location), oRadius));
                } else if (outlier < minRegular.doubleValue()) {
                    r0 = outliers;
                    r0.add(new Outlier((state.getBarWidth() / DateAxis.DEFAULT_AUTO_RANGE_MINIMUM_SIZE_IN_MILLISECONDS) + xx, rangeAxis.valueToJava2D(outlier, dataArea, location), oRadius));
                }
                Collections.sort(outliers);
            }
            for (Outlier add : outliers) {
                outlierListCollection.add(add);
            }
            Iterator iterator = outlierListCollection.iterator();
            while (iterator.hasNext()) {
                OutlierList list = (OutlierList) iterator.next();
                Point2D point = list.getAveragedOutlier().getPoint();
                if (list.isMultiple()) {
                    drawMultipleEllipse(point, state.getBarWidth(), oRadius, g2);
                } else {
                    drawEllipse(point, oRadius, g2);
                }
            }
            if (outlierListCollection.isHighFarOut()) {
                drawHighFarOut(aRadius / DateAxis.DEFAULT_AUTO_RANGE_MINIMUM_SIZE_IN_MILLISECONDS, g2, xx + (state.getBarWidth() / DateAxis.DEFAULT_AUTO_RANGE_MINIMUM_SIZE_IN_MILLISECONDS), maxAxisValue);
            }
            if (outlierListCollection.isLowFarOut()) {
                drawLowFarOut(aRadius / DateAxis.DEFAULT_AUTO_RANGE_MINIMUM_SIZE_IN_MILLISECONDS, g2, xx + (state.getBarWidth() / DateAxis.DEFAULT_AUTO_RANGE_MINIMUM_SIZE_IN_MILLISECONDS), minAxisValue);
            }
        }
        if (state.getInfo() != null && box != null) {
            EntityCollection entities = state.getEntityCollection();
            if (entities != null) {
                addItemEntity(entities, dataset, row, column, box);
            }
        }
    }

    private void drawEllipse(Point2D point, double oRadius, Graphics2D g2) {
        g2.draw(new Ellipse2D.Double(point.getX() + (oRadius / DateAxis.DEFAULT_AUTO_RANGE_MINIMUM_SIZE_IN_MILLISECONDS), point.getY(), oRadius, oRadius));
    }

    private void drawMultipleEllipse(Point2D point, double boxWidth, double oRadius, Graphics2D g2) {
        Ellipse2D dot1 = new Ellipse2D.Double((point.getX() - (boxWidth / DateAxis.DEFAULT_AUTO_RANGE_MINIMUM_SIZE_IN_MILLISECONDS)) + oRadius, point.getY(), oRadius, oRadius);
        Ellipse2D dot2 = new Ellipse2D.Double(point.getX() + (boxWidth / DateAxis.DEFAULT_AUTO_RANGE_MINIMUM_SIZE_IN_MILLISECONDS), point.getY(), oRadius, oRadius);
        g2.draw(dot1);
        g2.draw(dot2);
    }

    private void drawHighFarOut(double aRadius, Graphics2D g2, double xx, double m) {
        double side = aRadius * DateAxis.DEFAULT_AUTO_RANGE_MINIMUM_SIZE_IN_MILLISECONDS;
        g2.draw(new Line2D.Double(xx - side, m + side, xx + side, m + side));
        g2.draw(new Line2D.Double(xx - side, m + side, xx, m));
        g2.draw(new Line2D.Double(xx + side, m + side, xx, m));
    }

    private void drawLowFarOut(double aRadius, Graphics2D g2, double xx, double m) {
        double side = aRadius * DateAxis.DEFAULT_AUTO_RANGE_MINIMUM_SIZE_IN_MILLISECONDS;
        g2.draw(new Line2D.Double(xx - side, m - side, xx + side, m - side));
        g2.draw(new Line2D.Double(xx - side, m - side, xx, m));
        g2.draw(new Line2D.Double(xx + side, m - side, xx, m));
    }

    public boolean equals(Object obj) {
        if (obj == this) {
            return true;
        }
        if (!(obj instanceof BoxAndWhiskerRenderer)) {
            return false;
        }
        BoxAndWhiskerRenderer that = (BoxAndWhiskerRenderer) obj;
        if (this.fillBox == that.fillBox && this.itemMargin == that.itemMargin && this.maximumBarWidth == that.maximumBarWidth && this.meanVisible == that.meanVisible && this.medianVisible == that.medianVisible && this.useOutlinePaintForWhiskers == that.useOutlinePaintForWhiskers && this.whiskerWidth == that.whiskerWidth && PaintUtilities.equal(this.artifactPaint, that.artifactPaint)) {
            return super.equals(obj);
        }
        return false;
    }

    private void writeObject(ObjectOutputStream stream) throws IOException {
        stream.defaultWriteObject();
        SerialUtilities.writePaint(this.artifactPaint, stream);
    }

    private void readObject(ObjectInputStream stream) throws IOException, ClassNotFoundException {
        stream.defaultReadObject();
        this.artifactPaint = SerialUtilities.readPaint(stream);
    }
}
