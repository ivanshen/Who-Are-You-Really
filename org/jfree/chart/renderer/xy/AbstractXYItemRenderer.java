package org.jfree.chart.renderer.xy;

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
import java.awt.geom.GeneralPath;
import java.awt.geom.Line2D;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.awt.geom.Rectangle2D.Double;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import org.jfree.chart.LegendItem;
import org.jfree.chart.LegendItemCollection;
import org.jfree.chart.annotations.XYAnnotation;
import org.jfree.chart.axis.DateAxis;
import org.jfree.chart.axis.ValueAxis;
import org.jfree.chart.entity.EntityCollection;
import org.jfree.chart.entity.XYItemEntity;
import org.jfree.chart.event.AnnotationChangeEvent;
import org.jfree.chart.event.AnnotationChangeListener;
import org.jfree.chart.labels.ItemLabelPosition;
import org.jfree.chart.labels.StandardXYSeriesLabelGenerator;
import org.jfree.chart.labels.XYItemLabelGenerator;
import org.jfree.chart.labels.XYSeriesLabelGenerator;
import org.jfree.chart.labels.XYToolTipGenerator;
import org.jfree.chart.plot.CrosshairState;
import org.jfree.chart.plot.DrawingSupplier;
import org.jfree.chart.plot.IntervalMarker;
import org.jfree.chart.plot.Marker;
import org.jfree.chart.plot.Plot;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.plot.PlotRenderingInfo;
import org.jfree.chart.plot.ValueMarker;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.renderer.AbstractRenderer;
import org.jfree.chart.urls.XYURLGenerator;
import org.jfree.chart.util.CloneUtils;
import org.jfree.chart.util.ParamChecks;
import org.jfree.data.Range;
import org.jfree.data.general.DatasetUtilities;
import org.jfree.data.xy.XYDataset;
import org.jfree.text.TextUtilities;
import org.jfree.ui.GradientPaintTransformer;
import org.jfree.ui.Layer;
import org.jfree.ui.LengthAdjustmentType;
import org.jfree.ui.RectangleAnchor;
import org.jfree.ui.RectangleInsets;
import org.jfree.util.ObjectUtilities;
import org.jfree.util.PublicCloneable;

public abstract class AbstractXYItemRenderer extends AbstractRenderer implements XYItemRenderer, AnnotationChangeListener, Cloneable, Serializable {
    private static final long serialVersionUID = 8019124836026607990L;
    private List backgroundAnnotations;
    private XYItemLabelGenerator baseItemLabelGenerator;
    private XYToolTipGenerator baseToolTipGenerator;
    private List foregroundAnnotations;
    private XYItemLabelGenerator itemLabelGenerator;
    private Map<Integer, XYItemLabelGenerator> itemLabelGeneratorMap;
    private XYSeriesLabelGenerator legendItemLabelGenerator;
    private XYSeriesLabelGenerator legendItemToolTipGenerator;
    private XYSeriesLabelGenerator legendItemURLGenerator;
    private XYPlot plot;
    private XYToolTipGenerator toolTipGenerator;
    private Map<Integer, XYToolTipGenerator> toolTipGeneratorMap;
    private XYURLGenerator urlGenerator;

    protected AbstractXYItemRenderer() {
        this.itemLabelGenerator = null;
        this.itemLabelGeneratorMap = new HashMap();
        this.toolTipGenerator = null;
        this.toolTipGeneratorMap = new HashMap();
        this.urlGenerator = null;
        this.backgroundAnnotations = new ArrayList();
        this.foregroundAnnotations = new ArrayList();
        this.legendItemLabelGenerator = new StandardXYSeriesLabelGenerator(StandardXYSeriesLabelGenerator.DEFAULT_LABEL_FORMAT);
    }

    public int getPassCount() {
        return 1;
    }

    public XYPlot getPlot() {
        return this.plot;
    }

    public void setPlot(XYPlot plot) {
        this.plot = plot;
    }

    public XYItemRendererState initialise(Graphics2D g2, Rectangle2D dataArea, XYPlot plot, XYDataset data, PlotRenderingInfo info) {
        return new XYItemRendererState(info);
    }

    public XYItemLabelGenerator getItemLabelGenerator(int series, int item) {
        if (this.itemLabelGenerator != null) {
            return this.itemLabelGenerator;
        }
        XYItemLabelGenerator generator = (XYItemLabelGenerator) this.itemLabelGeneratorMap.get(Integer.valueOf(series));
        if (generator == null) {
            return this.baseItemLabelGenerator;
        }
        return generator;
    }

    public XYItemLabelGenerator getSeriesItemLabelGenerator(int series) {
        return (XYItemLabelGenerator) this.itemLabelGeneratorMap.get(Integer.valueOf(series));
    }

    public void setSeriesItemLabelGenerator(int series, XYItemLabelGenerator generator) {
        this.itemLabelGeneratorMap.put(Integer.valueOf(series), generator);
        fireChangeEvent();
    }

    public XYItemLabelGenerator getBaseItemLabelGenerator() {
        return this.baseItemLabelGenerator;
    }

    public void setBaseItemLabelGenerator(XYItemLabelGenerator generator) {
        this.baseItemLabelGenerator = generator;
        fireChangeEvent();
    }

    public XYToolTipGenerator getToolTipGenerator(int series, int item) {
        if (this.toolTipGenerator != null) {
            return this.toolTipGenerator;
        }
        XYToolTipGenerator generator = (XYToolTipGenerator) this.toolTipGeneratorMap.get(Integer.valueOf(series));
        if (generator == null) {
            return this.baseToolTipGenerator;
        }
        return generator;
    }

    public XYToolTipGenerator getSeriesToolTipGenerator(int series) {
        return (XYToolTipGenerator) this.toolTipGeneratorMap.get(Integer.valueOf(series));
    }

    public void setSeriesToolTipGenerator(int series, XYToolTipGenerator generator) {
        this.toolTipGeneratorMap.put(Integer.valueOf(series), generator);
        fireChangeEvent();
    }

    public XYToolTipGenerator getBaseToolTipGenerator() {
        return this.baseToolTipGenerator;
    }

    public void setBaseToolTipGenerator(XYToolTipGenerator generator) {
        this.baseToolTipGenerator = generator;
        fireChangeEvent();
    }

    public XYURLGenerator getURLGenerator() {
        return this.urlGenerator;
    }

    public void setURLGenerator(XYURLGenerator urlGenerator) {
        this.urlGenerator = urlGenerator;
        fireChangeEvent();
    }

    public void addAnnotation(XYAnnotation annotation) {
        addAnnotation(annotation, Layer.FOREGROUND);
    }

    public void addAnnotation(XYAnnotation annotation, Layer layer) {
        ParamChecks.nullNotPermitted(annotation, "annotation");
        if (layer.equals(Layer.FOREGROUND)) {
            this.foregroundAnnotations.add(annotation);
            annotation.addChangeListener(this);
            fireChangeEvent();
        } else if (layer.equals(Layer.BACKGROUND)) {
            this.backgroundAnnotations.add(annotation);
            annotation.addChangeListener(this);
            fireChangeEvent();
        } else {
            throw new RuntimeException("Unknown layer.");
        }
    }

    public boolean removeAnnotation(XYAnnotation annotation) {
        boolean removed = this.foregroundAnnotations.remove(annotation) & this.backgroundAnnotations.remove(annotation);
        annotation.removeChangeListener(this);
        fireChangeEvent();
        return removed;
    }

    public void removeAnnotations() {
        int i;
        for (i = 0; i < this.foregroundAnnotations.size(); i++) {
            ((XYAnnotation) this.foregroundAnnotations.get(i)).removeChangeListener(this);
        }
        for (i = 0; i < this.backgroundAnnotations.size(); i++) {
            ((XYAnnotation) this.backgroundAnnotations.get(i)).removeChangeListener(this);
        }
        this.foregroundAnnotations.clear();
        this.backgroundAnnotations.clear();
        fireChangeEvent();
    }

    public void annotationChanged(AnnotationChangeEvent event) {
        fireChangeEvent();
    }

    public Collection getAnnotations() {
        List result = new ArrayList(this.foregroundAnnotations);
        result.addAll(this.backgroundAnnotations);
        return result;
    }

    public XYSeriesLabelGenerator getLegendItemLabelGenerator() {
        return this.legendItemLabelGenerator;
    }

    public void setLegendItemLabelGenerator(XYSeriesLabelGenerator generator) {
        ParamChecks.nullNotPermitted(generator, "generator");
        this.legendItemLabelGenerator = generator;
        fireChangeEvent();
    }

    public XYSeriesLabelGenerator getLegendItemToolTipGenerator() {
        return this.legendItemToolTipGenerator;
    }

    public void setLegendItemToolTipGenerator(XYSeriesLabelGenerator generator) {
        this.legendItemToolTipGenerator = generator;
        fireChangeEvent();
    }

    public XYSeriesLabelGenerator getLegendItemURLGenerator() {
        return this.legendItemURLGenerator;
    }

    public void setLegendItemURLGenerator(XYSeriesLabelGenerator generator) {
        this.legendItemURLGenerator = generator;
        fireChangeEvent();
    }

    public Range findDomainBounds(XYDataset dataset) {
        return findDomainBounds(dataset, false);
    }

    protected Range findDomainBounds(XYDataset dataset, boolean includeInterval) {
        if (dataset == null) {
            return null;
        }
        if (!getDataBoundsIncludesVisibleSeriesOnly()) {
            return DatasetUtilities.findDomainBounds(dataset, includeInterval);
        }
        List visibleSeriesKeys = new ArrayList();
        int seriesCount = dataset.getSeriesCount();
        for (int s = 0; s < seriesCount; s++) {
            if (isSeriesVisible(s)) {
                visibleSeriesKeys.add(dataset.getSeriesKey(s));
            }
        }
        return DatasetUtilities.findDomainBounds(dataset, visibleSeriesKeys, includeInterval);
    }

    public Range findRangeBounds(XYDataset dataset) {
        return findRangeBounds(dataset, false);
    }

    protected Range findRangeBounds(XYDataset dataset, boolean includeInterval) {
        if (dataset == null) {
            return null;
        }
        if (!getDataBoundsIncludesVisibleSeriesOnly()) {
            return DatasetUtilities.findRangeBounds(dataset, includeInterval);
        }
        List visibleSeriesKeys = new ArrayList();
        int seriesCount = dataset.getSeriesCount();
        for (int s = 0; s < seriesCount; s++) {
            if (isSeriesVisible(s)) {
                visibleSeriesKeys.add(dataset.getSeriesKey(s));
            }
        }
        Range xRange = null;
        XYPlot p = getPlot();
        if (p != null) {
            ValueAxis xAxis = null;
            int index = p.getIndexOf(this);
            if (index >= 0) {
                xAxis = this.plot.getDomainAxisForDataset(index);
            }
            if (xAxis != null) {
                xRange = xAxis.getRange();
            }
        }
        if (xRange == null) {
            xRange = new Range(Double.NEGATIVE_INFINITY, Double.POSITIVE_INFINITY);
        }
        return DatasetUtilities.findRangeBounds(dataset, visibleSeriesKeys, xRange, includeInterval);
    }

    public LegendItemCollection getLegendItems() {
        if (this.plot == null) {
            return new LegendItemCollection();
        }
        LegendItemCollection result = new LegendItemCollection();
        int index = this.plot.getIndexOf(this);
        XYDataset dataset = this.plot.getDataset(index);
        if (dataset == null) {
            return result;
        }
        int seriesCount = dataset.getSeriesCount();
        for (int i = 0; i < seriesCount; i++) {
            if (isSeriesVisibleInLegend(i)) {
                LegendItem item = getLegendItem(index, i);
                if (item != null) {
                    result.add(item);
                }
            }
        }
        return result;
    }

    public LegendItem getLegendItem(int datasetIndex, int series) {
        LegendItem legendItem = null;
        XYPlot xyplot = getPlot();
        if (xyplot != null) {
            XYDataset dataset = xyplot.getDataset(datasetIndex);
            if (dataset != null) {
                String label = this.legendItemLabelGenerator.generateLabel(dataset, series);
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
                legendItem = new LegendItem(label, paint);
                legendItem.setToolTipText(toolTipText);
                legendItem.setURLText(urlText);
                legendItem.setLabelFont(lookupLegendTextFont(series));
                Paint labelPaint = lookupLegendTextPaint(series);
                if (labelPaint != null) {
                    legendItem.setLabelPaint(labelPaint);
                }
                legendItem.setSeriesKey(dataset.getSeriesKey(series));
                legendItem.setSeriesIndex(series);
                legendItem.setDataset(dataset);
                legendItem.setDatasetIndex(datasetIndex);
                if (getTreatLegendShapeAsLine()) {
                    legendItem.setLineVisible(true);
                    legendItem.setLine(shape);
                    legendItem.setLinePaint(paint);
                    legendItem.setShapeVisible(false);
                } else {
                    Paint outlinePaint = lookupSeriesOutlinePaint(series);
                    Stroke outlineStroke = lookupSeriesOutlineStroke(series);
                    legendItem.setOutlinePaint(outlinePaint);
                    legendItem.setOutlineStroke(outlineStroke);
                }
            }
        }
        return legendItem;
    }

    public void fillDomainGridBand(Graphics2D g2, XYPlot plot, ValueAxis axis, Rectangle2D dataArea, double start, double end) {
        Rectangle2D band;
        double x1 = axis.valueToJava2D(start, dataArea, plot.getDomainAxisEdge());
        double x2 = axis.valueToJava2D(end, dataArea, plot.getDomainAxisEdge());
        if (plot.getOrientation() == PlotOrientation.VERTICAL) {
            band = new Double(Math.min(x1, x2), dataArea.getMinY(), Math.abs(x2 - x1), dataArea.getHeight());
        } else {
            band = new Double(dataArea.getMinX(), Math.min(x1, x2), dataArea.getWidth(), Math.abs(x2 - x1));
        }
        Paint paint = plot.getDomainTickBandPaint();
        if (paint != null) {
            g2.setPaint(paint);
            g2.fill(band);
        }
    }

    public void fillRangeGridBand(Graphics2D g2, XYPlot plot, ValueAxis axis, Rectangle2D dataArea, double start, double end) {
        Rectangle2D band;
        double y1 = axis.valueToJava2D(start, dataArea, plot.getRangeAxisEdge());
        double y2 = axis.valueToJava2D(end, dataArea, plot.getRangeAxisEdge());
        if (plot.getOrientation() == PlotOrientation.VERTICAL) {
            band = new Double(dataArea.getMinX(), Math.min(y1, y2), dataArea.getWidth(), Math.abs(y2 - y1));
        } else {
            band = new Double(Math.min(y1, y2), dataArea.getMinY(), Math.abs(y2 - y1), dataArea.getHeight());
        }
        Paint paint = plot.getRangeTickBandPaint();
        if (paint != null) {
            g2.setPaint(paint);
            g2.fill(band);
        }
    }

    public void drawDomainGridLine(Graphics2D g2, XYPlot plot, ValueAxis axis, Rectangle2D dataArea, double value) {
        if (axis.getRange().contains(value)) {
            PlotOrientation orientation = plot.getOrientation();
            double v = axis.valueToJava2D(value, dataArea, plot.getDomainAxisEdge());
            Line2D line = null;
            if (orientation == PlotOrientation.HORIZONTAL) {
                line = new Line2D.Double(dataArea.getMinX(), v, dataArea.getMaxX(), v);
            } else if (orientation == PlotOrientation.VERTICAL) {
                Line2D.Double doubleR = new Line2D.Double(v, dataArea.getMinY(), v, dataArea.getMaxY());
            }
            Paint paint = plot.getDomainGridlinePaint();
            Stroke stroke = plot.getDomainGridlineStroke();
            if (paint == null) {
                paint = Plot.DEFAULT_OUTLINE_PAINT;
            }
            g2.setPaint(paint);
            if (stroke == null) {
                stroke = Plot.DEFAULT_OUTLINE_STROKE;
            }
            g2.setStroke(stroke);
            Object saved = g2.getRenderingHint(RenderingHints.KEY_STROKE_CONTROL);
            g2.setRenderingHint(RenderingHints.KEY_STROKE_CONTROL, RenderingHints.VALUE_STROKE_NORMALIZE);
            g2.draw(line);
            g2.setRenderingHint(RenderingHints.KEY_STROKE_CONTROL, saved);
        }
    }

    public void drawDomainLine(Graphics2D g2, XYPlot plot, ValueAxis axis, Rectangle2D dataArea, double value, Paint paint, Stroke stroke) {
        if (axis.getRange().contains(value)) {
            PlotOrientation orientation = plot.getOrientation();
            Line2D line = null;
            double v = axis.valueToJava2D(value, dataArea, plot.getDomainAxisEdge());
            if (orientation.isHorizontal()) {
                line = new Line2D.Double(dataArea.getMinX(), v, dataArea.getMaxX(), v);
            } else if (orientation.isVertical()) {
                Line2D.Double doubleR = new Line2D.Double(v, dataArea.getMinY(), v, dataArea.getMaxY());
            }
            g2.setPaint(paint);
            g2.setStroke(stroke);
            Object saved = g2.getRenderingHint(RenderingHints.KEY_STROKE_CONTROL);
            g2.setRenderingHint(RenderingHints.KEY_STROKE_CONTROL, RenderingHints.VALUE_STROKE_NORMALIZE);
            g2.draw(line);
            g2.setRenderingHint(RenderingHints.KEY_STROKE_CONTROL, saved);
        }
    }

    public void drawRangeLine(Graphics2D g2, XYPlot plot, ValueAxis axis, Rectangle2D dataArea, double value, Paint paint, Stroke stroke) {
        if (axis.getRange().contains(value)) {
            PlotOrientation orientation = plot.getOrientation();
            Line2D line = null;
            double v = axis.valueToJava2D(value, dataArea, plot.getRangeAxisEdge());
            if (orientation == PlotOrientation.HORIZONTAL) {
                line = new Line2D.Double(v, dataArea.getMinY(), v, dataArea.getMaxY());
            } else if (orientation == PlotOrientation.VERTICAL) {
                Line2D.Double doubleR = new Line2D.Double(dataArea.getMinX(), v, dataArea.getMaxX(), v);
            }
            g2.setPaint(paint);
            g2.setStroke(stroke);
            Object saved = g2.getRenderingHint(RenderingHints.KEY_STROKE_CONTROL);
            g2.setRenderingHint(RenderingHints.KEY_STROKE_CONTROL, RenderingHints.VALUE_STROKE_NORMALIZE);
            g2.draw(line);
            g2.setRenderingHint(RenderingHints.KEY_STROKE_CONTROL, saved);
        }
    }

    public void drawDomainMarker(Graphics2D g2, XYPlot plot, ValueAxis domainAxis, Marker marker, Rectangle2D dataArea) {
        Line2D line;
        Composite originalComposite;
        String label;
        RectangleAnchor anchor;
        Point2D coordinates;
        if (marker instanceof ValueMarker) {
            double value = ((ValueMarker) marker).getValue();
            if (domainAxis.getRange().contains(value)) {
                double v = domainAxis.valueToJava2D(value, dataArea, plot.getDomainAxisEdge());
                PlotOrientation orientation = plot.getOrientation();
                if (orientation == PlotOrientation.HORIZONTAL) {
                    line = new Line2D.Double(dataArea.getMinX(), v, dataArea.getMaxX(), v);
                } else if (orientation == PlotOrientation.VERTICAL) {
                    Line2D.Double doubleR = new Line2D.Double(v, dataArea.getMinY(), v, dataArea.getMaxY());
                } else {
                    throw new IllegalStateException();
                }
                originalComposite = g2.getComposite();
                g2.setComposite(AlphaComposite.getInstance(3, marker.getAlpha()));
                g2.setPaint(marker.getPaint());
                g2.setStroke(marker.getStroke());
                g2.draw(line);
                label = marker.getLabel();
                anchor = marker.getLabelAnchor();
                if (label != null) {
                    g2.setFont(marker.getLabelFont());
                    g2.setPaint(marker.getLabelPaint());
                    coordinates = calculateDomainMarkerTextAnchorPoint(g2, orientation, dataArea, line.getBounds2D(), marker.getLabelOffset(), LengthAdjustmentType.EXPAND, anchor);
                    TextUtilities.drawAlignedString(label, g2, (float) coordinates.getX(), (float) coordinates.getY(), marker.getLabelTextAnchor());
                }
                g2.setComposite(originalComposite);
            }
        } else if (marker instanceof IntervalMarker) {
            IntervalMarker im = (IntervalMarker) marker;
            double start = im.getStartValue();
            double end = im.getEndValue();
            Range range = domainAxis.getRange();
            if (range.intersects(start, end)) {
                double start2d = domainAxis.valueToJava2D(start, dataArea, plot.getDomainAxisEdge());
                double end2d = domainAxis.valueToJava2D(end, dataArea, plot.getDomainAxisEdge());
                double low = Math.min(start2d, end2d);
                double high = Math.max(start2d, end2d);
                PlotOrientation orientation2 = plot.getOrientation();
                Rectangle2D rect = null;
                if (orientation2 == PlotOrientation.HORIZONTAL) {
                    low = Math.max(low, dataArea.getMinY());
                    rect = new Double(dataArea.getMinX(), low, dataArea.getWidth(), Math.min(high, dataArea.getMaxY()) - low);
                } else if (orientation2 == PlotOrientation.VERTICAL) {
                    low = Math.max(low, dataArea.getMinX());
                    Double doubleR2 = new Double(low, dataArea.getMinY(), Math.min(high, dataArea.getMaxX()) - low, dataArea.getHeight());
                }
                originalComposite = g2.getComposite();
                g2.setComposite(AlphaComposite.getInstance(3, marker.getAlpha()));
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
                        line = new Line2D.Double();
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
                    } else {
                        line = new Line2D.Double();
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
                    }
                }
                label = marker.getLabel();
                anchor = marker.getLabelAnchor();
                if (label != null) {
                    g2.setFont(marker.getLabelFont());
                    g2.setPaint(marker.getLabelPaint());
                    coordinates = calculateDomainMarkerTextAnchorPoint(g2, orientation2, dataArea, rect, marker.getLabelOffset(), marker.getLabelOffsetType(), anchor);
                    TextUtilities.drawAlignedString(label, g2, (float) coordinates.getX(), (float) coordinates.getY(), marker.getLabelTextAnchor());
                }
                g2.setComposite(originalComposite);
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

    public void drawRangeMarker(Graphics2D g2, XYPlot plot, ValueAxis rangeAxis, Marker marker, Rectangle2D dataArea) {
        Line2D line;
        Composite originalComposite;
        String label;
        RectangleAnchor anchor;
        Point2D coordinates;
        if (marker instanceof ValueMarker) {
            double value = ((ValueMarker) marker).getValue();
            if (rangeAxis.getRange().contains(value)) {
                double v = rangeAxis.valueToJava2D(value, dataArea, plot.getRangeAxisEdge());
                PlotOrientation orientation = plot.getOrientation();
                if (orientation == PlotOrientation.HORIZONTAL) {
                    line = new Line2D.Double(v, dataArea.getMinY(), v, dataArea.getMaxY());
                } else if (orientation == PlotOrientation.VERTICAL) {
                    Line2D.Double doubleR = new Line2D.Double(dataArea.getMinX(), v, dataArea.getMaxX(), v);
                } else {
                    throw new IllegalStateException("Unknown orientation.");
                }
                originalComposite = g2.getComposite();
                g2.setComposite(AlphaComposite.getInstance(3, marker.getAlpha()));
                g2.setPaint(marker.getPaint());
                g2.setStroke(marker.getStroke());
                g2.draw(line);
                label = marker.getLabel();
                anchor = marker.getLabelAnchor();
                if (label != null) {
                    g2.setFont(marker.getLabelFont());
                    g2.setPaint(marker.getLabelPaint());
                    coordinates = calculateRangeMarkerTextAnchorPoint(g2, orientation, dataArea, line.getBounds2D(), marker.getLabelOffset(), LengthAdjustmentType.EXPAND, anchor);
                    TextUtilities.drawAlignedString(label, g2, (float) coordinates.getX(), (float) coordinates.getY(), marker.getLabelTextAnchor());
                }
                g2.setComposite(originalComposite);
            }
        } else if (marker instanceof IntervalMarker) {
            IntervalMarker im = (IntervalMarker) marker;
            double start = im.getStartValue();
            double end = im.getEndValue();
            Range range = rangeAxis.getRange();
            if (range.intersects(start, end)) {
                double start2d = rangeAxis.valueToJava2D(start, dataArea, plot.getRangeAxisEdge());
                double end2d = rangeAxis.valueToJava2D(end, dataArea, plot.getRangeAxisEdge());
                double low = Math.min(start2d, end2d);
                double high = Math.max(start2d, end2d);
                PlotOrientation orientation2 = plot.getOrientation();
                Rectangle2D rect = null;
                if (orientation2 == PlotOrientation.HORIZONTAL) {
                    low = Math.max(low, dataArea.getMinX());
                    rect = new Double(low, dataArea.getMinY(), Math.min(high, dataArea.getMaxX()) - low, dataArea.getHeight());
                } else if (orientation2 == PlotOrientation.VERTICAL) {
                    low = Math.max(low, dataArea.getMinY());
                    double d = low;
                    Double doubleR2 = new Double(dataArea.getMinX(), d, dataArea.getWidth(), Math.min(high, dataArea.getMaxY()) - low);
                }
                originalComposite = g2.getComposite();
                g2.setComposite(AlphaComposite.getInstance(3, marker.getAlpha()));
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
                        line = new Line2D.Double();
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
                        line = new Line2D.Double();
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
                g2.setComposite(originalComposite);
            }
        }
    }

    private Point2D calculateRangeMarkerTextAnchorPoint(Graphics2D g2, PlotOrientation orientation, Rectangle2D dataArea, Rectangle2D markerArea, RectangleInsets markerOffset, LengthAdjustmentType labelOffsetForRange, RectangleAnchor anchor) {
        Rectangle2D anchorRect = null;
        if (orientation == PlotOrientation.HORIZONTAL) {
            anchorRect = markerOffset.createAdjustedRectangle(markerArea, labelOffsetForRange, LengthAdjustmentType.CONTRACT);
        } else if (orientation == PlotOrientation.VERTICAL) {
            anchorRect = markerOffset.createAdjustedRectangle(markerArea, LengthAdjustmentType.CONTRACT, labelOffsetForRange);
        }
        return RectangleAnchor.coordinates(anchorRect, anchor);
    }

    protected Object clone() throws CloneNotSupportedException {
        AbstractXYItemRenderer clone = (AbstractXYItemRenderer) super.clone();
        if (this.itemLabelGenerator != null && (this.itemLabelGenerator instanceof PublicCloneable)) {
            clone.itemLabelGenerator = (XYItemLabelGenerator) this.itemLabelGenerator.clone();
        }
        clone.itemLabelGeneratorMap = CloneUtils.cloneMapValues(this.itemLabelGeneratorMap);
        if (this.baseItemLabelGenerator != null && (this.baseItemLabelGenerator instanceof PublicCloneable)) {
            clone.baseItemLabelGenerator = (XYItemLabelGenerator) ((PublicCloneable) this.baseItemLabelGenerator).clone();
        }
        if (this.toolTipGenerator != null && (this.toolTipGenerator instanceof PublicCloneable)) {
            clone.toolTipGenerator = (XYToolTipGenerator) ((PublicCloneable) this.toolTipGenerator).clone();
        }
        clone.toolTipGeneratorMap = CloneUtils.cloneMapValues(this.toolTipGeneratorMap);
        if (this.baseToolTipGenerator != null && (this.baseToolTipGenerator instanceof PublicCloneable)) {
            clone.baseToolTipGenerator = (XYToolTipGenerator) ((PublicCloneable) this.baseToolTipGenerator).clone();
        }
        if (this.legendItemLabelGenerator instanceof PublicCloneable) {
            clone.legendItemLabelGenerator = (XYSeriesLabelGenerator) ObjectUtilities.clone(this.legendItemLabelGenerator);
        }
        if (this.legendItemToolTipGenerator instanceof PublicCloneable) {
            clone.legendItemToolTipGenerator = (XYSeriesLabelGenerator) ObjectUtilities.clone(this.legendItemToolTipGenerator);
        }
        if (this.legendItemURLGenerator instanceof PublicCloneable) {
            clone.legendItemURLGenerator = (XYSeriesLabelGenerator) ObjectUtilities.clone(this.legendItemURLGenerator);
        }
        clone.foregroundAnnotations = (List) ObjectUtilities.deepClone(this.foregroundAnnotations);
        clone.backgroundAnnotations = (List) ObjectUtilities.deepClone(this.backgroundAnnotations);
        return clone;
    }

    public boolean equals(Object obj) {
        if (obj == this) {
            return true;
        }
        if (!(obj instanceof AbstractXYItemRenderer)) {
            return false;
        }
        AbstractXYItemRenderer that = (AbstractXYItemRenderer) obj;
        if (ObjectUtilities.equal(this.itemLabelGenerator, that.itemLabelGenerator) && this.itemLabelGeneratorMap.equals(that.itemLabelGeneratorMap) && ObjectUtilities.equal(this.baseItemLabelGenerator, that.baseItemLabelGenerator) && ObjectUtilities.equal(this.toolTipGenerator, that.toolTipGenerator) && this.toolTipGeneratorMap.equals(that.toolTipGeneratorMap) && ObjectUtilities.equal(this.baseToolTipGenerator, that.baseToolTipGenerator) && ObjectUtilities.equal(this.urlGenerator, that.urlGenerator) && this.foregroundAnnotations.equals(that.foregroundAnnotations) && this.backgroundAnnotations.equals(that.backgroundAnnotations) && ObjectUtilities.equal(this.legendItemLabelGenerator, that.legendItemLabelGenerator) && ObjectUtilities.equal(this.legendItemToolTipGenerator, that.legendItemToolTipGenerator) && ObjectUtilities.equal(this.legendItemURLGenerator, that.legendItemURLGenerator)) {
            return super.equals(obj);
        }
        return false;
    }

    public DrawingSupplier getDrawingSupplier() {
        XYPlot p = getPlot();
        if (p != null) {
            return p.getDrawingSupplier();
        }
        return null;
    }

    protected void updateCrosshairValues(CrosshairState crosshairState, double x, double y, int domainAxisIndex, int rangeAxisIndex, double transX, double transY, PlotOrientation orientation) {
        ParamChecks.nullNotPermitted(orientation, "orientation");
        if (crosshairState == null) {
            return;
        }
        if (this.plot.isDomainCrosshairLockedOnData()) {
            if (this.plot.isRangeCrosshairLockedOnData()) {
                crosshairState.updateCrosshairPoint(x, y, domainAxisIndex, rangeAxisIndex, transX, transY, orientation);
            } else {
                crosshairState.updateCrosshairX(x, domainAxisIndex);
            }
        } else if (this.plot.isRangeCrosshairLockedOnData()) {
            crosshairState.updateCrosshairY(y, rangeAxisIndex);
        }
    }

    protected void drawItemLabel(Graphics2D g2, PlotOrientation orientation, XYDataset dataset, int series, int item, double x, double y, boolean negative) {
        XYItemLabelGenerator generator = getItemLabelGenerator(series, item);
        if (generator != null) {
            ItemLabelPosition position;
            Font labelFont = getItemLabelFont(series, item);
            Paint paint = getItemLabelPaint(series, item);
            g2.setFont(labelFont);
            g2.setPaint(paint);
            String label = generator.generateLabel(dataset, series, item);
            if (negative) {
                position = getNegativeItemLabelPosition(series, item);
            } else {
                position = getPositiveItemLabelPosition(series, item);
            }
            Point2D anchorPoint = calculateLabelAnchorPoint(position.getItemLabelAnchor(), x, y, orientation);
            TextUtilities.drawRotatedString(label, g2, (float) anchorPoint.getX(), (float) anchorPoint.getY(), position.getTextAnchor(), position.getAngle(), position.getRotationAnchor());
        }
    }

    public void drawAnnotations(Graphics2D g2, Rectangle2D dataArea, ValueAxis domainAxis, ValueAxis rangeAxis, Layer layer, PlotRenderingInfo info) {
        Iterator iterator;
        if (layer.equals(Layer.FOREGROUND)) {
            iterator = this.foregroundAnnotations.iterator();
        } else if (layer.equals(Layer.BACKGROUND)) {
            iterator = this.backgroundAnnotations.iterator();
        } else {
            throw new RuntimeException("Unknown layer.");
        }
        while (iterator.hasNext()) {
            Graphics2D graphics2D = g2;
            ((XYAnnotation) iterator.next()).draw(graphics2D, this.plot, dataArea, domainAxis, rangeAxis, this.plot.getIndexOf(this), info);
        }
    }

    protected void addEntity(EntityCollection entities, Shape area, XYDataset dataset, int series, int item, double entityX, double entityY) {
        if (getItemCreateEntity(series, item)) {
            Shape hotspot = area;
            if (hotspot == null) {
                double r = (double) getDefaultEntityRadius();
                double w = r * DateAxis.DEFAULT_AUTO_RANGE_MINIMUM_SIZE_IN_MILLISECONDS;
                if (getPlot().getOrientation() == PlotOrientation.VERTICAL) {
                    hotspot = new Ellipse2D.Double(entityX - r, entityY - r, w, w);
                } else {
                    hotspot = new Ellipse2D.Double(entityY - r, entityX - r, w, w);
                }
            }
            String tip = null;
            XYToolTipGenerator generator = getToolTipGenerator(series, item);
            if (generator != null) {
                tip = generator.generateToolTip(dataset, series, item);
            }
            String url = null;
            if (getURLGenerator() != null) {
                url = getURLGenerator().generateURL(dataset, series, item);
            }
            entities.add(new XYItemEntity(hotspot, dataset, series, item, tip, url));
        }
    }

    public static boolean isPointInRect(Rectangle2D rect, double x, double y) {
        return x >= rect.getMinX() && x <= rect.getMaxX() && y >= rect.getMinY() && y <= rect.getMaxY();
    }

    protected static void moveTo(GeneralPath hotspot, double x, double y) {
        hotspot.moveTo((float) x, (float) y);
    }

    protected static void lineTo(GeneralPath hotspot, double x, double y) {
        hotspot.lineTo((float) x, (float) y);
    }

    public XYItemLabelGenerator getItemLabelGenerator() {
        return this.itemLabelGenerator;
    }

    public void setItemLabelGenerator(XYItemLabelGenerator generator) {
        this.itemLabelGenerator = generator;
        fireChangeEvent();
    }

    public XYToolTipGenerator getToolTipGenerator() {
        return this.toolTipGenerator;
    }

    public void setToolTipGenerator(XYToolTipGenerator generator) {
        this.toolTipGenerator = generator;
        fireChangeEvent();
    }

    protected void updateCrosshairValues(CrosshairState crosshairState, double x, double y, double transX, double transY, PlotOrientation orientation) {
        updateCrosshairValues(crosshairState, x, y, 0, 0, transX, transY, orientation);
    }
}
