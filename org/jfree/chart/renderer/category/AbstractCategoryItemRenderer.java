package org.jfree.chart.renderer.category;

import java.awt.AlphaComposite;
import java.awt.Composite;
import java.awt.Font;
import java.awt.GradientPaint;
import java.awt.Graphics2D;
import java.awt.Paint;
import java.awt.RenderingHints;
import java.awt.Shape;
import java.awt.Stroke;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Line2D;
import java.awt.geom.Line2D.Double;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.jfree.chart.LegendItem;
import org.jfree.chart.LegendItemCollection;
import org.jfree.chart.axis.CategoryAxis;
import org.jfree.chart.axis.DateAxis;
import org.jfree.chart.axis.ValueAxis;
import org.jfree.chart.entity.CategoryItemEntity;
import org.jfree.chart.entity.EntityCollection;
import org.jfree.chart.labels.CategoryItemLabelGenerator;
import org.jfree.chart.labels.CategorySeriesLabelGenerator;
import org.jfree.chart.labels.CategoryToolTipGenerator;
import org.jfree.chart.labels.ItemLabelPosition;
import org.jfree.chart.labels.StandardCategorySeriesLabelGenerator;
import org.jfree.chart.plot.CategoryCrosshairState;
import org.jfree.chart.plot.CategoryMarker;
import org.jfree.chart.plot.CategoryPlot;
import org.jfree.chart.plot.DrawingSupplier;
import org.jfree.chart.plot.IntervalMarker;
import org.jfree.chart.plot.Marker;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.plot.PlotRenderingInfo;
import org.jfree.chart.plot.ValueMarker;
import org.jfree.chart.renderer.AbstractRenderer;
import org.jfree.chart.urls.CategoryURLGenerator;
import org.jfree.chart.util.CloneUtils;
import org.jfree.chart.util.ParamChecks;
import org.jfree.chart.util.TextUtils;
import org.jfree.data.Range;
import org.jfree.data.category.CategoryDataset;
import org.jfree.data.general.DatasetUtilities;
import org.jfree.text.TextUtilities;
import org.jfree.ui.GradientPaintTransformer;
import org.jfree.ui.LengthAdjustmentType;
import org.jfree.ui.RectangleAnchor;
import org.jfree.ui.RectangleEdge;
import org.jfree.ui.RectangleInsets;
import org.jfree.util.ObjectUtilities;
import org.jfree.util.PublicCloneable;
import org.jfree.util.SortOrder;

public abstract class AbstractCategoryItemRenderer extends AbstractRenderer implements CategoryItemRenderer, Cloneable, PublicCloneable, Serializable {
    private static final long serialVersionUID = 1247553218442497391L;
    private CategoryItemLabelGenerator baseItemLabelGenerator;
    private CategoryURLGenerator baseItemURLGenerator;
    private CategoryToolTipGenerator baseToolTipGenerator;
    private transient int columnCount;
    private CategoryItemLabelGenerator itemLabelGenerator;
    private Map<Integer, CategoryItemLabelGenerator> itemLabelGeneratorMap;
    private CategoryURLGenerator itemURLGenerator;
    private Map<Integer, CategoryURLGenerator> itemURLGeneratorMap;
    private CategorySeriesLabelGenerator legendItemLabelGenerator;
    private CategorySeriesLabelGenerator legendItemToolTipGenerator;
    private CategorySeriesLabelGenerator legendItemURLGenerator;
    private CategoryPlot plot;
    private transient int rowCount;
    private CategoryToolTipGenerator toolTipGenerator;
    private Map<Integer, CategoryToolTipGenerator> toolTipGeneratorMap;

    protected AbstractCategoryItemRenderer() {
        this.itemLabelGenerator = null;
        this.itemLabelGeneratorMap = new HashMap();
        this.toolTipGenerator = null;
        this.toolTipGeneratorMap = new HashMap();
        this.itemURLGenerator = null;
        this.itemURLGeneratorMap = new HashMap();
        this.legendItemLabelGenerator = new StandardCategorySeriesLabelGenerator();
    }

    public int getPassCount() {
        return 1;
    }

    public CategoryPlot getPlot() {
        return this.plot;
    }

    public void setPlot(CategoryPlot plot) {
        ParamChecks.nullNotPermitted(plot, "plot");
        this.plot = plot;
    }

    public CategoryItemLabelGenerator getItemLabelGenerator(int row, int column) {
        return getSeriesItemLabelGenerator(row);
    }

    public CategoryItemLabelGenerator getSeriesItemLabelGenerator(int series) {
        if (this.itemLabelGenerator != null) {
            return this.itemLabelGenerator;
        }
        CategoryItemLabelGenerator generator = (CategoryItemLabelGenerator) this.itemLabelGeneratorMap.get(Integer.valueOf(series));
        if (generator == null) {
            return this.baseItemLabelGenerator;
        }
        return generator;
    }

    public void setSeriesItemLabelGenerator(int series, CategoryItemLabelGenerator generator) {
        this.itemLabelGeneratorMap.put(Integer.valueOf(series), generator);
        fireChangeEvent();
    }

    public CategoryItemLabelGenerator getBaseItemLabelGenerator() {
        return this.baseItemLabelGenerator;
    }

    public void setBaseItemLabelGenerator(CategoryItemLabelGenerator generator) {
        this.baseItemLabelGenerator = generator;
        fireChangeEvent();
    }

    public CategoryToolTipGenerator getToolTipGenerator(int row, int column) {
        if (this.toolTipGenerator != null) {
            return this.toolTipGenerator;
        }
        CategoryToolTipGenerator result = getSeriesToolTipGenerator(row);
        if (result == null) {
            return this.baseToolTipGenerator;
        }
        return result;
    }

    public CategoryToolTipGenerator getSeriesToolTipGenerator(int series) {
        return (CategoryToolTipGenerator) this.toolTipGeneratorMap.get(Integer.valueOf(series));
    }

    public void setSeriesToolTipGenerator(int series, CategoryToolTipGenerator generator) {
        this.toolTipGeneratorMap.put(Integer.valueOf(series), generator);
        fireChangeEvent();
    }

    public CategoryToolTipGenerator getBaseToolTipGenerator() {
        return this.baseToolTipGenerator;
    }

    public void setBaseToolTipGenerator(CategoryToolTipGenerator generator) {
        this.baseToolTipGenerator = generator;
        fireChangeEvent();
    }

    public CategoryURLGenerator getItemURLGenerator(int row, int column) {
        return getSeriesItemURLGenerator(row);
    }

    public CategoryURLGenerator getSeriesItemURLGenerator(int series) {
        if (this.itemURLGenerator != null) {
            return this.itemURLGenerator;
        }
        CategoryURLGenerator generator = (CategoryURLGenerator) this.itemURLGeneratorMap.get(Integer.valueOf(series));
        if (generator == null) {
            return this.baseItemURLGenerator;
        }
        return generator;
    }

    public void setSeriesItemURLGenerator(int series, CategoryURLGenerator generator) {
        this.itemURLGeneratorMap.put(Integer.valueOf(series), generator);
        fireChangeEvent();
    }

    public CategoryURLGenerator getBaseItemURLGenerator() {
        return this.baseItemURLGenerator;
    }

    public void setBaseItemURLGenerator(CategoryURLGenerator generator) {
        this.baseItemURLGenerator = generator;
        fireChangeEvent();
    }

    public int getRowCount() {
        return this.rowCount;
    }

    public int getColumnCount() {
        return this.columnCount;
    }

    protected CategoryItemRendererState createState(PlotRenderingInfo info) {
        return new CategoryItemRendererState(info);
    }

    public CategoryItemRendererState initialise(Graphics2D g2, Rectangle2D dataArea, CategoryPlot plot, int rendererIndex, PlotRenderingInfo info) {
        setPlot(plot);
        CategoryDataset data = plot.getDataset(rendererIndex);
        if (data != null) {
            this.rowCount = data.getRowCount();
            this.columnCount = data.getColumnCount();
        } else {
            this.rowCount = 0;
            this.columnCount = 0;
        }
        CategoryItemRendererState state = createState(info);
        int[] visibleSeriesTemp = new int[this.rowCount];
        int visibleSeriesCount = 0;
        for (int row = 0; row < this.rowCount; row++) {
            if (isSeriesVisible(row)) {
                visibleSeriesTemp[visibleSeriesCount] = row;
                visibleSeriesCount++;
            }
        }
        int[] visibleSeries = new int[visibleSeriesCount];
        System.arraycopy(visibleSeriesTemp, 0, visibleSeries, 0, visibleSeriesCount);
        state.setVisibleSeriesArray(visibleSeries);
        return state;
    }

    public Range findRangeBounds(CategoryDataset dataset) {
        return findRangeBounds(dataset, false);
    }

    protected Range findRangeBounds(CategoryDataset dataset, boolean includeInterval) {
        if (dataset == null) {
            return null;
        }
        if (!getDataBoundsIncludesVisibleSeriesOnly()) {
            return DatasetUtilities.findRangeBounds(dataset, includeInterval);
        }
        List visibleSeriesKeys = new ArrayList();
        int seriesCount = dataset.getRowCount();
        for (int s = 0; s < seriesCount; s++) {
            if (isSeriesVisible(s)) {
                visibleSeriesKeys.add(dataset.getRowKey(s));
            }
        }
        return DatasetUtilities.findRangeBounds(dataset, visibleSeriesKeys, includeInterval);
    }

    public double getItemMiddle(Comparable rowKey, Comparable columnKey, CategoryDataset dataset, CategoryAxis axis, Rectangle2D area, RectangleEdge edge) {
        return axis.getCategoryMiddle(columnKey, dataset.getColumnKeys(), area, edge);
    }

    public void drawBackground(Graphics2D g2, CategoryPlot plot, Rectangle2D dataArea) {
        plot.drawBackground(g2, dataArea);
    }

    public void drawOutline(Graphics2D g2, CategoryPlot plot, Rectangle2D dataArea) {
        plot.drawOutline(g2, dataArea);
    }

    public void drawDomainGridline(Graphics2D g2, CategoryPlot plot, Rectangle2D dataArea, double value) {
        Line2D line = null;
        PlotOrientation orientation = plot.getOrientation();
        if (orientation == PlotOrientation.HORIZONTAL) {
            line = new Double(dataArea.getMinX(), value, dataArea.getMaxX(), value);
        } else if (orientation == PlotOrientation.VERTICAL) {
            line = new Double(value, dataArea.getMinY(), value, dataArea.getMaxY());
        }
        Paint paint = plot.getDomainGridlinePaint();
        if (paint == null) {
            paint = CategoryPlot.DEFAULT_GRIDLINE_PAINT;
        }
        g2.setPaint(paint);
        Stroke stroke = plot.getDomainGridlineStroke();
        if (stroke == null) {
            stroke = CategoryPlot.DEFAULT_GRIDLINE_STROKE;
        }
        g2.setStroke(stroke);
        g2.draw(line);
    }

    public void drawRangeGridline(Graphics2D g2, CategoryPlot plot, ValueAxis axis, Rectangle2D dataArea, double value) {
        if (axis.getRange().contains(value)) {
            PlotOrientation orientation = plot.getOrientation();
            double v = axis.valueToJava2D(value, dataArea, plot.getRangeAxisEdge());
            Line2D line = null;
            if (orientation == PlotOrientation.HORIZONTAL) {
                line = new Double(v, dataArea.getMinY(), v, dataArea.getMaxY());
            } else if (orientation == PlotOrientation.VERTICAL) {
                Double doubleR = new Double(dataArea.getMinX(), v, dataArea.getMaxX(), v);
            }
            Paint paint = plot.getRangeGridlinePaint();
            if (paint == null) {
                paint = CategoryPlot.DEFAULT_GRIDLINE_PAINT;
            }
            g2.setPaint(paint);
            Stroke stroke = plot.getRangeGridlineStroke();
            if (stroke == null) {
                stroke = CategoryPlot.DEFAULT_GRIDLINE_STROKE;
            }
            g2.setStroke(stroke);
            g2.draw(line);
        }
    }

    public void drawRangeLine(Graphics2D g2, CategoryPlot plot, ValueAxis axis, Rectangle2D dataArea, double value, Paint paint, Stroke stroke) {
        if (axis.getRange().contains(value)) {
            PlotOrientation orientation = plot.getOrientation();
            Line2D line = null;
            double v = axis.valueToJava2D(value, dataArea, plot.getRangeAxisEdge());
            if (orientation == PlotOrientation.HORIZONTAL) {
                line = new Double(v, dataArea.getMinY(), v, dataArea.getMaxY());
            } else if (orientation == PlotOrientation.VERTICAL) {
                Double doubleR = new Double(dataArea.getMinX(), v, dataArea.getMaxX(), v);
            }
            g2.setPaint(paint);
            g2.setStroke(stroke);
            Object saved = g2.getRenderingHint(RenderingHints.KEY_STROKE_CONTROL);
            g2.setRenderingHint(RenderingHints.KEY_STROKE_CONTROL, RenderingHints.VALUE_STROKE_NORMALIZE);
            g2.draw(line);
            g2.setRenderingHint(RenderingHints.KEY_STROKE_CONTROL, saved);
        }
    }

    public void drawDomainMarker(Graphics2D g2, CategoryPlot plot, CategoryAxis axis, CategoryMarker marker, Rectangle2D dataArea) {
        Comparable category = marker.getKey();
        CategoryDataset dataset = plot.getDataset(plot.getIndexOf(this));
        int columnIndex = dataset.getColumnIndex(category);
        if (columnIndex >= 0) {
            Rectangle2D bounds;
            Composite savedComposite = g2.getComposite();
            g2.setComposite(AlphaComposite.getInstance(3, marker.getAlpha()));
            PlotOrientation orientation = plot.getOrientation();
            if (marker.getDrawAsLine()) {
                Line2D line;
                double v = axis.getCategoryMiddle(columnIndex, dataset.getColumnCount(), dataArea, plot.getDomainAxisEdge());
                if (orientation == PlotOrientation.HORIZONTAL) {
                    line = new Double(dataArea.getMinX(), v, dataArea.getMaxX(), v);
                } else if (orientation == PlotOrientation.VERTICAL) {
                    Double doubleR = new Double(v, dataArea.getMinY(), v, dataArea.getMaxY());
                } else {
                    throw new IllegalStateException();
                }
                g2.setPaint(marker.getPaint());
                g2.setStroke(marker.getStroke());
                g2.draw(line);
                bounds = line.getBounds2D();
            } else {
                double v0 = axis.getCategoryStart(columnIndex, dataset.getColumnCount(), dataArea, plot.getDomainAxisEdge());
                double v1 = axis.getCategoryEnd(columnIndex, dataset.getColumnCount(), dataArea, plot.getDomainAxisEdge());
                Rectangle2D area = null;
                if (orientation == PlotOrientation.HORIZONTAL) {
                    area = new Rectangle2D.Double(dataArea.getMinX(), v0, dataArea.getWidth(), v1 - v0);
                } else if (orientation == PlotOrientation.VERTICAL) {
                    Rectangle2D.Double doubleR2 = new Rectangle2D.Double(v0, dataArea.getMinY(), v1 - v0, dataArea.getHeight());
                }
                g2.setPaint(marker.getPaint());
                g2.fill(area);
                bounds = area;
            }
            String label = marker.getLabel();
            RectangleAnchor anchor = marker.getLabelAnchor();
            if (label != null) {
                g2.setFont(marker.getLabelFont());
                g2.setPaint(marker.getLabelPaint());
                Point2D coordinates = calculateDomainMarkerTextAnchorPoint(g2, orientation, dataArea, bounds, marker.getLabelOffset(), marker.getLabelOffsetType(), anchor);
                TextUtilities.drawAlignedString(label, g2, (float) coordinates.getX(), (float) coordinates.getY(), marker.getLabelTextAnchor());
            }
            g2.setComposite(savedComposite);
        }
    }

    public void drawRangeMarker(Graphics2D g2, CategoryPlot plot, ValueAxis axis, Marker marker, Rectangle2D dataArea) {
        Composite savedComposite;
        Line2D line;
        String label;
        RectangleAnchor anchor;
        Point2D coordinates;
        Rectangle2D rect;
        if (marker instanceof ValueMarker) {
            double value = ((ValueMarker) marker).getValue();
            if (axis.getRange().contains(value)) {
                savedComposite = g2.getComposite();
                g2.setComposite(AlphaComposite.getInstance(3, marker.getAlpha()));
                PlotOrientation orientation = plot.getOrientation();
                double v = axis.valueToJava2D(value, dataArea, plot.getRangeAxisEdge());
                if (orientation == PlotOrientation.HORIZONTAL) {
                    line = new Double(v, dataArea.getMinY(), v, dataArea.getMaxY());
                } else if (orientation == PlotOrientation.VERTICAL) {
                    Double doubleR = new Double(dataArea.getMinX(), v, dataArea.getMaxX(), v);
                } else {
                    throw new IllegalStateException();
                }
                g2.setPaint(marker.getPaint());
                g2.setStroke(marker.getStroke());
                g2.draw(line);
                label = marker.getLabel();
                anchor = marker.getLabelAnchor();
                if (label != null) {
                    g2.setFont(marker.getLabelFont());
                    coordinates = calculateRangeMarkerTextAnchorPoint(g2, orientation, dataArea, line.getBounds2D(), marker.getLabelOffset(), LengthAdjustmentType.EXPAND, anchor);
                    rect = TextUtils.calcAlignedStringBounds(label, g2, (float) coordinates.getX(), (float) coordinates.getY(), marker.getLabelTextAnchor());
                    g2.setPaint(marker.getLabelBackgroundColor());
                    g2.fill(rect);
                    g2.setPaint(marker.getLabelPaint());
                    TextUtils.drawAlignedString(label, g2, (float) coordinates.getX(), (float) coordinates.getY(), marker.getLabelTextAnchor());
                }
                g2.setComposite(savedComposite);
            }
        } else if (marker instanceof IntervalMarker) {
            IntervalMarker im = (IntervalMarker) marker;
            double start = im.getStartValue();
            double end = im.getEndValue();
            Range range = axis.getRange();
            if (range.intersects(start, end)) {
                savedComposite = g2.getComposite();
                g2.setComposite(AlphaComposite.getInstance(3, marker.getAlpha()));
                double start2d = axis.valueToJava2D(start, dataArea, plot.getRangeAxisEdge());
                double end2d = axis.valueToJava2D(end, dataArea, plot.getRangeAxisEdge());
                double low = Math.min(start2d, end2d);
                double high = Math.max(start2d, end2d);
                PlotOrientation orientation2 = plot.getOrientation();
                rect = null;
                if (orientation2 == PlotOrientation.HORIZONTAL) {
                    low = Math.max(low, dataArea.getMinX());
                    rect = new Rectangle2D.Double(low, dataArea.getMinY(), Math.min(high, dataArea.getMaxX()) - low, dataArea.getHeight());
                } else if (orientation2 == PlotOrientation.VERTICAL) {
                    low = Math.max(low, dataArea.getMinY());
                    double d = low;
                    Rectangle2D.Double doubleR2 = new Rectangle2D.Double(dataArea.getMinX(), d, dataArea.getWidth(), Math.min(high, dataArea.getMaxY()) - low);
                }
                Paint p = marker.getPaint();
                if (p instanceof GradientPaint) {
                    GradientPaint gp = (GradientPaint) p;
                    GradientPaintTransformer t = im.getGradientPaintTransformer();
                    if (t != null) {
                        gp = t.transform(gp, rect);
                    }
                    g2.setPaint(gp);
                } else {
                    g2.setPaint(p);
                }
                g2.fill(rect);
                if (!(im.getOutlinePaint() == null || im.getOutlineStroke() == null)) {
                    if (orientation2 == PlotOrientation.VERTICAL) {
                        line = new Double();
                        double x0 = dataArea.getMinX();
                        double x1 = dataArea.getMaxX();
                        g2.setPaint(im.getOutlinePaint());
                        g2.setStroke(im.getOutlineStroke());
                        if (range.contains(start)) {
                            line.setLine(x0, start2d, x1, start2d);
                            g2.draw(line);
                        }
                        if (range.contains(end)) {
                            line.setLine(x0, end2d, x1, end2d);
                            g2.draw(line);
                        }
                    } else {
                        line = new Double();
                        double y0 = dataArea.getMinY();
                        double y1 = dataArea.getMaxY();
                        g2.setPaint(im.getOutlinePaint());
                        g2.setStroke(im.getOutlineStroke());
                        if (range.contains(start)) {
                            line.setLine(start2d, y0, start2d, y1);
                            g2.draw(line);
                        }
                        if (range.contains(end)) {
                            line.setLine(end2d, y0, end2d, y1);
                            g2.draw(line);
                        }
                    }
                }
                label = marker.getLabel();
                anchor = marker.getLabelAnchor();
                if (label != null) {
                    g2.setFont(marker.getLabelFont());
                    g2.setPaint(marker.getLabelPaint());
                    coordinates = calculateRangeMarkerTextAnchorPoint(g2, orientation2, dataArea, rect, marker.getLabelOffset(), marker.getLabelOffsetType(), anchor);
                    TextUtilities.drawAlignedString(label, g2, (float) coordinates.getX(), (float) coordinates.getY(), marker.getLabelTextAnchor());
                }
                g2.setComposite(savedComposite);
            }
        }
    }

    protected Point2D calculateDomainMarkerTextAnchorPoint(Graphics2D g2, PlotOrientation orientation, Rectangle2D dataArea, Rectangle2D markerArea, RectangleInsets markerOffset, LengthAdjustmentType labelOffsetType, RectangleAnchor anchor) {
        Rectangle2D anchorRect = null;
        if (orientation == PlotOrientation.HORIZONTAL) {
            anchorRect = markerOffset.createAdjustedRectangle(markerArea, LengthAdjustmentType.CONTRACT, labelOffsetType);
        } else if (orientation == PlotOrientation.VERTICAL) {
            anchorRect = markerOffset.createAdjustedRectangle(markerArea, labelOffsetType, LengthAdjustmentType.CONTRACT);
        }
        return RectangleAnchor.coordinates(anchorRect, anchor);
    }

    protected Point2D calculateRangeMarkerTextAnchorPoint(Graphics2D g2, PlotOrientation orientation, Rectangle2D dataArea, Rectangle2D markerArea, RectangleInsets markerOffset, LengthAdjustmentType labelOffsetType, RectangleAnchor anchor) {
        Rectangle2D anchorRect = null;
        if (orientation == PlotOrientation.HORIZONTAL) {
            anchorRect = markerOffset.createAdjustedRectangle(markerArea, labelOffsetType, LengthAdjustmentType.CONTRACT);
        } else if (orientation == PlotOrientation.VERTICAL) {
            anchorRect = markerOffset.createAdjustedRectangle(markerArea, LengthAdjustmentType.CONTRACT, labelOffsetType);
        }
        return RectangleAnchor.coordinates(anchorRect, anchor);
    }

    public LegendItem getLegendItem(int datasetIndex, int series) {
        LegendItem legendItem = null;
        CategoryPlot p = getPlot();
        if (p != null && isSeriesVisible(series) && isSeriesVisibleInLegend(series)) {
            CategoryDataset dataset = p.getDataset(datasetIndex);
            String label = this.legendItemLabelGenerator.generateLabel(dataset, series);
            String description = label;
            String toolTipText = null;
            if (this.legendItemToolTipGenerator != null) {
                toolTipText = this.legendItemToolTipGenerator.generateLabel(dataset, series);
            }
            String urlText = null;
            if (this.legendItemURLGenerator != null) {
                urlText = this.legendItemURLGenerator.generateLabel(dataset, series);
            }
            legendItem = new LegendItem(label, description, toolTipText, urlText, lookupLegendShape(series), lookupSeriesPaint(series), lookupSeriesOutlineStroke(series), lookupSeriesOutlinePaint(series));
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

    public boolean equals(Object obj) {
        if (obj == this) {
            return true;
        }
        if (!(obj instanceof AbstractCategoryItemRenderer)) {
            return false;
        }
        AbstractCategoryItemRenderer that = (AbstractCategoryItemRenderer) obj;
        if (ObjectUtilities.equal(this.itemLabelGenerator, that.itemLabelGenerator) && ObjectUtilities.equal(this.itemLabelGeneratorMap, that.itemLabelGeneratorMap) && ObjectUtilities.equal(this.baseItemLabelGenerator, that.baseItemLabelGenerator) && ObjectUtilities.equal(this.toolTipGenerator, that.toolTipGenerator) && ObjectUtilities.equal(this.toolTipGeneratorMap, that.toolTipGeneratorMap) && ObjectUtilities.equal(this.baseToolTipGenerator, that.baseToolTipGenerator) && ObjectUtilities.equal(this.itemURLGenerator, that.itemURLGenerator) && ObjectUtilities.equal(this.itemURLGeneratorMap, that.itemURLGeneratorMap) && ObjectUtilities.equal(this.baseItemURLGenerator, that.baseItemURLGenerator) && ObjectUtilities.equal(this.legendItemLabelGenerator, that.legendItemLabelGenerator) && ObjectUtilities.equal(this.legendItemToolTipGenerator, that.legendItemToolTipGenerator) && ObjectUtilities.equal(this.legendItemURLGenerator, that.legendItemURLGenerator)) {
            return super.equals(obj);
        }
        return false;
    }

    public int hashCode() {
        return super.hashCode();
    }

    public DrawingSupplier getDrawingSupplier() {
        CategoryPlot cp = getPlot();
        if (cp != null) {
            return cp.getDrawingSupplier();
        }
        return null;
    }

    protected void updateCrosshairValues(CategoryCrosshairState crosshairState, Comparable rowKey, Comparable columnKey, double value, int datasetIndex, double transX, double transY, PlotOrientation orientation) {
        ParamChecks.nullNotPermitted(orientation, "orientation");
        if (crosshairState == null) {
            return;
        }
        if (this.plot.isRangeCrosshairLockedOnData()) {
            crosshairState.updateCrosshairPoint(rowKey, columnKey, value, datasetIndex, transX, transY, orientation);
        } else {
            crosshairState.updateCrosshairX(rowKey, columnKey, datasetIndex, transX, orientation);
        }
    }

    protected void drawItemLabel(Graphics2D g2, PlotOrientation orientation, CategoryDataset dataset, int row, int column, double x, double y, boolean negative) {
        CategoryItemLabelGenerator generator = getItemLabelGenerator(row, column);
        if (generator != null) {
            ItemLabelPosition position;
            Font labelFont = getItemLabelFont(row, column);
            Paint paint = getItemLabelPaint(row, column);
            g2.setFont(labelFont);
            g2.setPaint(paint);
            String label = generator.generateLabel(dataset, row, column);
            if (negative) {
                position = getNegativeItemLabelPosition(row, column);
            } else {
                position = getPositiveItemLabelPosition(row, column);
            }
            Point2D anchorPoint = calculateLabelAnchorPoint(position.getItemLabelAnchor(), x, y, orientation);
            TextUtilities.drawRotatedString(label, g2, (float) anchorPoint.getX(), (float) anchorPoint.getY(), position.getTextAnchor(), position.getAngle(), position.getRotationAnchor());
        }
    }

    public Object clone() throws CloneNotSupportedException {
        AbstractCategoryItemRenderer clone = (AbstractCategoryItemRenderer) super.clone();
        if (this.itemLabelGenerator != null) {
            if (this.itemLabelGenerator instanceof PublicCloneable) {
                clone.itemLabelGenerator = (CategoryItemLabelGenerator) this.itemLabelGenerator.clone();
            } else {
                throw new CloneNotSupportedException("ItemLabelGenerator not cloneable.");
            }
        }
        if (this.itemLabelGeneratorMap != null) {
            clone.itemLabelGeneratorMap = CloneUtils.cloneMapValues(this.itemLabelGeneratorMap);
        }
        if (this.baseItemLabelGenerator != null) {
            if (this.baseItemLabelGenerator instanceof PublicCloneable) {
                clone.baseItemLabelGenerator = (CategoryItemLabelGenerator) ((PublicCloneable) this.baseItemLabelGenerator).clone();
            } else {
                throw new CloneNotSupportedException("ItemLabelGenerator not cloneable.");
            }
        }
        if (this.toolTipGenerator != null) {
            if (this.toolTipGenerator instanceof PublicCloneable) {
                clone.toolTipGenerator = (CategoryToolTipGenerator) ((PublicCloneable) this.toolTipGenerator).clone();
            } else {
                throw new CloneNotSupportedException("Tool tip generator not cloneable.");
            }
        }
        if (this.toolTipGeneratorMap != null) {
            clone.toolTipGeneratorMap = CloneUtils.cloneMapValues(this.toolTipGeneratorMap);
        }
        if (this.baseToolTipGenerator != null) {
            if (this.baseToolTipGenerator instanceof PublicCloneable) {
                clone.baseToolTipGenerator = (CategoryToolTipGenerator) ((PublicCloneable) this.baseToolTipGenerator).clone();
            } else {
                throw new CloneNotSupportedException("Base tool tip generator not cloneable.");
            }
        }
        if (this.itemURLGenerator != null) {
            if (this.itemURLGenerator instanceof PublicCloneable) {
                clone.itemURLGenerator = (CategoryURLGenerator) ((PublicCloneable) this.itemURLGenerator).clone();
            } else {
                throw new CloneNotSupportedException("Item URL generator not cloneable.");
            }
        }
        if (this.itemURLGeneratorMap != null) {
            clone.itemURLGeneratorMap = CloneUtils.cloneMapValues(this.itemURLGeneratorMap);
        }
        if (this.baseItemURLGenerator != null) {
            if (this.baseItemURLGenerator instanceof PublicCloneable) {
                clone.baseItemURLGenerator = (CategoryURLGenerator) ((PublicCloneable) this.baseItemURLGenerator).clone();
            } else {
                throw new CloneNotSupportedException("Base item URL generator not cloneable.");
            }
        }
        if (this.legendItemLabelGenerator instanceof PublicCloneable) {
            clone.legendItemLabelGenerator = (CategorySeriesLabelGenerator) ObjectUtilities.clone(this.legendItemLabelGenerator);
        }
        if (this.legendItemToolTipGenerator instanceof PublicCloneable) {
            clone.legendItemToolTipGenerator = (CategorySeriesLabelGenerator) ObjectUtilities.clone(this.legendItemToolTipGenerator);
        }
        if (this.legendItemURLGenerator instanceof PublicCloneable) {
            clone.legendItemURLGenerator = (CategorySeriesLabelGenerator) ObjectUtilities.clone(this.legendItemURLGenerator);
        }
        return clone;
    }

    protected CategoryAxis getDomainAxis(CategoryPlot plot, int index) {
        CategoryAxis result = plot.getDomainAxis(index);
        if (result == null) {
            return plot.getDomainAxis();
        }
        return result;
    }

    protected ValueAxis getRangeAxis(CategoryPlot plot, int index) {
        ValueAxis result = plot.getRangeAxis(index);
        if (result == null) {
            return plot.getRangeAxis();
        }
        return result;
    }

    public LegendItemCollection getLegendItems() {
        LegendItemCollection result = new LegendItemCollection();
        if (this.plot != null) {
            int index = this.plot.getIndexOf(this);
            CategoryDataset dataset = this.plot.getDataset(index);
            if (dataset != null) {
                int seriesCount = dataset.getRowCount();
                int i;
                LegendItem item;
                if (this.plot.getRowRenderingOrder().equals(SortOrder.ASCENDING)) {
                    for (i = 0; i < seriesCount; i++) {
                        if (isSeriesVisibleInLegend(i)) {
                            item = getLegendItem(index, i);
                            if (item != null) {
                                result.add(item);
                            }
                        }
                    }
                } else {
                    for (i = seriesCount - 1; i >= 0; i--) {
                        if (isSeriesVisibleInLegend(i)) {
                            item = getLegendItem(index, i);
                            if (item != null) {
                                result.add(item);
                            }
                        }
                    }
                }
            }
        }
        return result;
    }

    public CategorySeriesLabelGenerator getLegendItemLabelGenerator() {
        return this.legendItemLabelGenerator;
    }

    public void setLegendItemLabelGenerator(CategorySeriesLabelGenerator generator) {
        ParamChecks.nullNotPermitted(generator, "generator");
        this.legendItemLabelGenerator = generator;
        fireChangeEvent();
    }

    public CategorySeriesLabelGenerator getLegendItemToolTipGenerator() {
        return this.legendItemToolTipGenerator;
    }

    public void setLegendItemToolTipGenerator(CategorySeriesLabelGenerator generator) {
        this.legendItemToolTipGenerator = generator;
        fireChangeEvent();
    }

    public CategorySeriesLabelGenerator getLegendItemURLGenerator() {
        return this.legendItemURLGenerator;
    }

    public void setLegendItemURLGenerator(CategorySeriesLabelGenerator generator) {
        this.legendItemURLGenerator = generator;
        fireChangeEvent();
    }

    protected void addItemEntity(EntityCollection entities, CategoryDataset dataset, int row, int column, Shape hotspot) {
        ParamChecks.nullNotPermitted(hotspot, "hotspot");
        if (getItemCreateEntity(row, column)) {
            String tip = null;
            CategoryToolTipGenerator tipster = getToolTipGenerator(row, column);
            if (tipster != null) {
                tip = tipster.generateToolTip(dataset, row, column);
            }
            String url = null;
            CategoryURLGenerator urlster = getItemURLGenerator(row, column);
            if (urlster != null) {
                url = urlster.generateURL(dataset, row, column);
            }
            entities.add(new CategoryItemEntity(hotspot, tip, url, dataset, dataset.getRowKey(row), dataset.getColumnKey(column)));
        }
    }

    protected void addEntity(EntityCollection entities, Shape hotspot, CategoryDataset dataset, int row, int column, double entityX, double entityY) {
        if (getItemCreateEntity(row, column)) {
            Shape s = hotspot;
            if (hotspot == null) {
                double r = (double) getDefaultEntityRadius();
                double w = r * DateAxis.DEFAULT_AUTO_RANGE_MINIMUM_SIZE_IN_MILLISECONDS;
                if (getPlot().getOrientation() == PlotOrientation.VERTICAL) {
                    s = new Ellipse2D.Double(entityX - r, entityY - r, w, w);
                } else {
                    s = new Ellipse2D.Double(entityY - r, entityX - r, w, w);
                }
            }
            String tip = null;
            CategoryToolTipGenerator generator = getToolTipGenerator(row, column);
            if (generator != null) {
                tip = generator.generateToolTip(dataset, row, column);
            }
            String url = null;
            CategoryURLGenerator urlster = getItemURLGenerator(row, column);
            if (urlster != null) {
                url = urlster.generateURL(dataset, row, column);
            }
            EntityCollection entityCollection = entities;
            entityCollection.add(new CategoryItemEntity(s, tip, url, dataset, dataset.getRowKey(row), dataset.getColumnKey(column)));
        }
    }

    public void setItemLabelGenerator(CategoryItemLabelGenerator generator) {
        this.itemLabelGenerator = generator;
        fireChangeEvent();
    }

    public CategoryToolTipGenerator getToolTipGenerator() {
        return this.toolTipGenerator;
    }

    public void setToolTipGenerator(CategoryToolTipGenerator generator) {
        this.toolTipGenerator = generator;
        fireChangeEvent();
    }

    public void setItemURLGenerator(CategoryURLGenerator generator) {
        this.itemURLGenerator = generator;
        fireChangeEvent();
    }
}
