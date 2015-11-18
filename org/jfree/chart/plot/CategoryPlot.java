package org.jfree.chart.plot;

import java.awt.AlphaComposite;
import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Composite;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.Paint;
import java.awt.Rectangle;
import java.awt.Shape;
import java.awt.Stroke;
import java.awt.geom.Line2D;
import java.awt.geom.Line2D.Double;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.ResourceBundle;
import java.util.TreeMap;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.LegendItemCollection;
import org.jfree.chart.annotations.CategoryAnnotation;
import org.jfree.chart.annotations.XYPointerAnnotation;
import org.jfree.chart.axis.Axis;
import org.jfree.chart.axis.AxisCollection;
import org.jfree.chart.axis.AxisLocation;
import org.jfree.chart.axis.AxisSpace;
import org.jfree.chart.axis.AxisState;
import org.jfree.chart.axis.CategoryAnchor;
import org.jfree.chart.axis.CategoryAxis;
import org.jfree.chart.axis.DateAxis;
import org.jfree.chart.axis.TickType;
import org.jfree.chart.axis.ValueAxis;
import org.jfree.chart.axis.ValueTick;
import org.jfree.chart.event.AnnotationChangeEvent;
import org.jfree.chart.event.AnnotationChangeListener;
import org.jfree.chart.event.ChartChangeEventType;
import org.jfree.chart.event.PlotChangeEvent;
import org.jfree.chart.event.RendererChangeEvent;
import org.jfree.chart.event.RendererChangeListener;
import org.jfree.chart.renderer.category.AbstractCategoryItemRenderer;
import org.jfree.chart.renderer.category.CategoryItemRenderer;
import org.jfree.chart.renderer.category.CategoryItemRendererState;
import org.jfree.chart.util.CloneUtils;
import org.jfree.chart.util.ParamChecks;
import org.jfree.chart.util.ResourceBundleWrapper;
import org.jfree.chart.util.ShadowGenerator;
import org.jfree.data.Range;
import org.jfree.data.category.CategoryDataset;
import org.jfree.data.general.DatasetChangeEvent;
import org.jfree.data.general.DatasetUtilities;
import org.jfree.io.SerialUtilities;
import org.jfree.ui.Layer;
import org.jfree.ui.RectangleEdge;
import org.jfree.ui.RectangleInsets;
import org.jfree.util.ObjectUtilities;
import org.jfree.util.PaintUtilities;
import org.jfree.util.PublicCloneable;
import org.jfree.util.ShapeUtilities;
import org.jfree.util.SortOrder;

public class CategoryPlot extends Plot implements ValueAxisPlot, Pannable, Zoomable, AnnotationChangeListener, RendererChangeListener, Cloneable, PublicCloneable, Serializable {
    public static final Paint DEFAULT_CROSSHAIR_PAINT;
    public static final Stroke DEFAULT_CROSSHAIR_STROKE;
    public static final boolean DEFAULT_CROSSHAIR_VISIBLE = false;
    public static final boolean DEFAULT_DOMAIN_GRIDLINES_VISIBLE = false;
    public static final Paint DEFAULT_GRIDLINE_PAINT;
    public static final Stroke DEFAULT_GRIDLINE_STROKE;
    public static final boolean DEFAULT_RANGE_GRIDLINES_VISIBLE = true;
    public static final Font DEFAULT_VALUE_LABEL_FONT;
    protected static ResourceBundle localizationResources = null;
    private static final long serialVersionUID = -3537691700434728188L;
    private double anchorValue;
    private List annotations;
    private RectangleInsets axisOffset;
    private Map backgroundDomainMarkers;
    private Map backgroundRangeMarkers;
    private SortOrder columnRenderingOrder;
    private int crosshairDatasetIndex;
    private TreeMap datasetToDomainAxesMap;
    private TreeMap datasetToRangeAxesMap;
    private Map<Integer, CategoryDataset> datasets;
    private Map<Integer, CategoryAxis> domainAxes;
    private Map<Integer, AxisLocation> domainAxisLocations;
    private Comparable domainCrosshairColumnKey;
    private transient Paint domainCrosshairPaint;
    private Comparable domainCrosshairRowKey;
    private transient Stroke domainCrosshairStroke;
    private boolean domainCrosshairVisible;
    private transient Paint domainGridlinePaint;
    private CategoryAnchor domainGridlinePosition;
    private transient Stroke domainGridlineStroke;
    private boolean domainGridlinesVisible;
    private boolean drawSharedDomainAxis;
    private AxisSpace fixedDomainAxisSpace;
    private LegendItemCollection fixedLegendItems;
    private AxisSpace fixedRangeAxisSpace;
    private Map foregroundDomainMarkers;
    private Map foregroundRangeMarkers;
    private PlotOrientation orientation;
    private Map<Integer, ValueAxis> rangeAxes;
    private Map<Integer, AxisLocation> rangeAxisLocations;
    private boolean rangeCrosshairLockedOnData;
    private transient Paint rangeCrosshairPaint;
    private transient Stroke rangeCrosshairStroke;
    private double rangeCrosshairValue;
    private boolean rangeCrosshairVisible;
    private transient Paint rangeGridlinePaint;
    private transient Stroke rangeGridlineStroke;
    private boolean rangeGridlinesVisible;
    private transient Paint rangeMinorGridlinePaint;
    private transient Stroke rangeMinorGridlineStroke;
    private boolean rangeMinorGridlinesVisible;
    private boolean rangePannable;
    private transient Paint rangeZeroBaselinePaint;
    private transient Stroke rangeZeroBaselineStroke;
    private boolean rangeZeroBaselineVisible;
    private Map<Integer, CategoryItemRenderer> renderers;
    private DatasetRenderingOrder renderingOrder;
    private SortOrder rowRenderingOrder;
    private ShadowGenerator shadowGenerator;
    private int weight;

    static {
        DEFAULT_GRIDLINE_STROKE = new BasicStroke(JFreeChart.DEFAULT_BACKGROUND_IMAGE_ALPHA, 0, 2, 0.0f, new float[]{Axis.DEFAULT_TICK_MARK_OUTSIDE_LENGTH, Axis.DEFAULT_TICK_MARK_OUTSIDE_LENGTH}, 0.0f);
        DEFAULT_GRIDLINE_PAINT = Color.lightGray;
        DEFAULT_VALUE_LABEL_FONT = new Font("SansSerif", 0, 10);
        DEFAULT_CROSSHAIR_STROKE = DEFAULT_GRIDLINE_STROKE;
        DEFAULT_CROSSHAIR_PAINT = Color.blue;
        localizationResources = ResourceBundleWrapper.getBundle("org.jfree.chart.plot.LocalizationBundle");
    }

    public CategoryPlot() {
        this(null, null, null, null);
    }

    public CategoryPlot(CategoryDataset dataset, CategoryAxis domainAxis, ValueAxis rangeAxis, CategoryItemRenderer renderer) {
        this.renderingOrder = DatasetRenderingOrder.REVERSE;
        this.columnRenderingOrder = SortOrder.ASCENDING;
        this.rowRenderingOrder = SortOrder.ASCENDING;
        this.rangeCrosshairLockedOnData = DEFAULT_RANGE_GRIDLINES_VISIBLE;
        this.orientation = PlotOrientation.VERTICAL;
        this.domainAxes = new HashMap();
        this.domainAxisLocations = new HashMap();
        this.rangeAxes = new HashMap();
        this.rangeAxisLocations = new HashMap();
        this.datasetToDomainAxesMap = new TreeMap();
        this.datasetToRangeAxesMap = new TreeMap();
        this.renderers = new HashMap();
        this.datasets = new HashMap();
        this.datasets.put(Integer.valueOf(0), dataset);
        if (dataset != null) {
            dataset.addChangeListener(this);
        }
        this.axisOffset = RectangleInsets.ZERO_INSETS;
        this.domainAxisLocations.put(Integer.valueOf(0), AxisLocation.BOTTOM_OR_LEFT);
        this.rangeAxisLocations.put(Integer.valueOf(0), AxisLocation.TOP_OR_LEFT);
        this.renderers.put(Integer.valueOf(0), renderer);
        if (renderer != null) {
            renderer.setPlot(this);
            renderer.addChangeListener(this);
        }
        this.domainAxes.put(Integer.valueOf(0), domainAxis);
        mapDatasetToDomainAxis(0, 0);
        if (domainAxis != null) {
            domainAxis.setPlot(this);
            domainAxis.addChangeListener(this);
        }
        this.drawSharedDomainAxis = DEFAULT_DOMAIN_GRIDLINES_VISIBLE;
        this.rangeAxes.put(Integer.valueOf(0), rangeAxis);
        mapDatasetToRangeAxis(0, 0);
        if (rangeAxis != null) {
            rangeAxis.setPlot(this);
            rangeAxis.addChangeListener(this);
        }
        configureDomainAxes();
        configureRangeAxes();
        this.domainGridlinesVisible = DEFAULT_DOMAIN_GRIDLINES_VISIBLE;
        this.domainGridlinePosition = CategoryAnchor.MIDDLE;
        this.domainGridlineStroke = DEFAULT_GRIDLINE_STROKE;
        this.domainGridlinePaint = DEFAULT_GRIDLINE_PAINT;
        this.rangeZeroBaselineVisible = DEFAULT_DOMAIN_GRIDLINES_VISIBLE;
        this.rangeZeroBaselinePaint = Color.black;
        this.rangeZeroBaselineStroke = new BasicStroke(JFreeChart.DEFAULT_BACKGROUND_IMAGE_ALPHA);
        this.rangeGridlinesVisible = DEFAULT_RANGE_GRIDLINES_VISIBLE;
        this.rangeGridlineStroke = DEFAULT_GRIDLINE_STROKE;
        this.rangeGridlinePaint = DEFAULT_GRIDLINE_PAINT;
        this.rangeMinorGridlinesVisible = DEFAULT_DOMAIN_GRIDLINES_VISIBLE;
        this.rangeMinorGridlineStroke = DEFAULT_GRIDLINE_STROKE;
        this.rangeMinorGridlinePaint = Color.white;
        this.foregroundDomainMarkers = new HashMap();
        this.backgroundDomainMarkers = new HashMap();
        this.foregroundRangeMarkers = new HashMap();
        this.backgroundRangeMarkers = new HashMap();
        this.anchorValue = 0.0d;
        this.domainCrosshairVisible = DEFAULT_DOMAIN_GRIDLINES_VISIBLE;
        this.domainCrosshairStroke = DEFAULT_CROSSHAIR_STROKE;
        this.domainCrosshairPaint = DEFAULT_CROSSHAIR_PAINT;
        this.rangeCrosshairVisible = DEFAULT_DOMAIN_GRIDLINES_VISIBLE;
        this.rangeCrosshairValue = 0.0d;
        this.rangeCrosshairStroke = DEFAULT_CROSSHAIR_STROKE;
        this.rangeCrosshairPaint = DEFAULT_CROSSHAIR_PAINT;
        this.annotations = new ArrayList();
        this.rangePannable = DEFAULT_DOMAIN_GRIDLINES_VISIBLE;
        this.shadowGenerator = null;
    }

    public String getPlotType() {
        return localizationResources.getString("Category_Plot");
    }

    public PlotOrientation getOrientation() {
        return this.orientation;
    }

    public void setOrientation(PlotOrientation orientation) {
        ParamChecks.nullNotPermitted(orientation, "orientation");
        this.orientation = orientation;
        fireChangeEvent();
    }

    public RectangleInsets getAxisOffset() {
        return this.axisOffset;
    }

    public void setAxisOffset(RectangleInsets offset) {
        ParamChecks.nullNotPermitted(offset, "offset");
        this.axisOffset = offset;
        fireChangeEvent();
    }

    public CategoryAxis getDomainAxis() {
        return getDomainAxis(0);
    }

    public CategoryAxis getDomainAxis(int index) {
        CategoryAxis result = (CategoryAxis) this.domainAxes.get(Integer.valueOf(index));
        if (result != null) {
            return result;
        }
        Plot parent = getParent();
        if (parent instanceof CategoryPlot) {
            return ((CategoryPlot) parent).getDomainAxis(index);
        }
        return result;
    }

    public void setDomainAxis(CategoryAxis axis) {
        setDomainAxis(0, axis);
    }

    public void setDomainAxis(int index, CategoryAxis axis) {
        setDomainAxis(index, axis, DEFAULT_RANGE_GRIDLINES_VISIBLE);
    }

    public void setDomainAxis(int index, CategoryAxis axis, boolean notify) {
        CategoryAxis existing = (CategoryAxis) this.domainAxes.get(Integer.valueOf(index));
        if (existing != null) {
            existing.removeChangeListener(this);
        }
        if (axis != null) {
            axis.setPlot(this);
        }
        this.domainAxes.put(Integer.valueOf(index), axis);
        if (axis != null) {
            axis.configure();
            axis.addChangeListener(this);
        }
        if (notify) {
            fireChangeEvent();
        }
    }

    public void setDomainAxes(CategoryAxis[] axes) {
        for (int i = 0; i < axes.length; i++) {
            setDomainAxis(i, axes[i], DEFAULT_DOMAIN_GRIDLINES_VISIBLE);
        }
        fireChangeEvent();
    }

    public int getDomainAxisIndex(CategoryAxis axis) {
        ParamChecks.nullNotPermitted(axis, "axis");
        for (Entry<Integer, CategoryAxis> entry : this.domainAxes.entrySet()) {
            if (entry.getValue() == axis) {
                return ((Integer) entry.getKey()).intValue();
            }
        }
        return -1;
    }

    public AxisLocation getDomainAxisLocation() {
        return getDomainAxisLocation(0);
    }

    public AxisLocation getDomainAxisLocation(int index) {
        AxisLocation result = (AxisLocation) this.domainAxisLocations.get(Integer.valueOf(index));
        if (result == null) {
            return AxisLocation.getOpposite(getDomainAxisLocation(0));
        }
        return result;
    }

    public void setDomainAxisLocation(AxisLocation location) {
        setDomainAxisLocation(0, location, DEFAULT_RANGE_GRIDLINES_VISIBLE);
    }

    public void setDomainAxisLocation(AxisLocation location, boolean notify) {
        setDomainAxisLocation(0, location, notify);
    }

    public void setDomainAxisLocation(int index, AxisLocation location) {
        setDomainAxisLocation(index, location, DEFAULT_RANGE_GRIDLINES_VISIBLE);
    }

    public void setDomainAxisLocation(int index, AxisLocation location, boolean notify) {
        if (index == 0 && location == null) {
            throw new IllegalArgumentException("Null 'location' for index 0 not permitted.");
        }
        this.domainAxisLocations.put(Integer.valueOf(index), location);
        if (notify) {
            fireChangeEvent();
        }
    }

    public RectangleEdge getDomainAxisEdge() {
        return getDomainAxisEdge(0);
    }

    public RectangleEdge getDomainAxisEdge(int index) {
        AxisLocation location = getDomainAxisLocation(index);
        if (location != null) {
            return Plot.resolveDomainAxisLocation(location, this.orientation);
        }
        return RectangleEdge.opposite(getDomainAxisEdge(0));
    }

    public int getDomainAxisCount() {
        return this.domainAxes.size();
    }

    public void clearDomainAxes() {
        for (CategoryAxis xAxis : this.domainAxes.values()) {
            if (xAxis != null) {
                xAxis.removeChangeListener(this);
            }
        }
        this.domainAxes.clear();
        fireChangeEvent();
    }

    public void configureDomainAxes() {
        for (CategoryAxis xAxis : this.domainAxes.values()) {
            if (xAxis != null) {
                xAxis.configure();
            }
        }
    }

    public ValueAxis getRangeAxis() {
        return getRangeAxis(0);
    }

    public ValueAxis getRangeAxis(int index) {
        ValueAxis result = (ValueAxis) this.rangeAxes.get(Integer.valueOf(index));
        if (result != null) {
            return result;
        }
        Plot parent = getParent();
        if (parent instanceof CategoryPlot) {
            return ((CategoryPlot) parent).getRangeAxis(index);
        }
        return result;
    }

    public void setRangeAxis(ValueAxis axis) {
        setRangeAxis(0, axis);
    }

    public void setRangeAxis(int index, ValueAxis axis) {
        setRangeAxis(index, axis, DEFAULT_RANGE_GRIDLINES_VISIBLE);
    }

    public void setRangeAxis(int index, ValueAxis axis, boolean notify) {
        ValueAxis existing = (ValueAxis) this.rangeAxes.get(Integer.valueOf(index));
        if (existing != null) {
            existing.removeChangeListener(this);
        }
        if (axis != null) {
            axis.setPlot(this);
        }
        this.rangeAxes.put(Integer.valueOf(index), axis);
        if (axis != null) {
            axis.configure();
            axis.addChangeListener(this);
        }
        if (notify) {
            fireChangeEvent();
        }
    }

    public void setRangeAxes(ValueAxis[] axes) {
        for (int i = 0; i < axes.length; i++) {
            setRangeAxis(i, axes[i], DEFAULT_DOMAIN_GRIDLINES_VISIBLE);
        }
        fireChangeEvent();
    }

    public int getRangeAxisIndex(ValueAxis axis) {
        ParamChecks.nullNotPermitted(axis, "axis");
        int result = findRangeAxisIndex(axis);
        if (result >= 0) {
            return result;
        }
        Plot parent = getParent();
        if (parent instanceof CategoryPlot) {
            return ((CategoryPlot) parent).getRangeAxisIndex(axis);
        }
        return result;
    }

    private int findRangeAxisIndex(ValueAxis axis) {
        for (Entry<Integer, ValueAxis> entry : this.rangeAxes.entrySet()) {
            if (entry.getValue() == axis) {
                return ((Integer) entry.getKey()).intValue();
            }
        }
        return -1;
    }

    public AxisLocation getRangeAxisLocation() {
        return getRangeAxisLocation(0);
    }

    public AxisLocation getRangeAxisLocation(int index) {
        AxisLocation result = (AxisLocation) this.rangeAxisLocations.get(Integer.valueOf(index));
        if (result == null) {
            return AxisLocation.getOpposite(getRangeAxisLocation(0));
        }
        return result;
    }

    public void setRangeAxisLocation(AxisLocation location) {
        setRangeAxisLocation(location, (boolean) DEFAULT_RANGE_GRIDLINES_VISIBLE);
    }

    public void setRangeAxisLocation(AxisLocation location, boolean notify) {
        setRangeAxisLocation(0, location, notify);
    }

    public void setRangeAxisLocation(int index, AxisLocation location) {
        setRangeAxisLocation(index, location, DEFAULT_RANGE_GRIDLINES_VISIBLE);
    }

    public void setRangeAxisLocation(int index, AxisLocation location, boolean notify) {
        if (index == 0 && location == null) {
            throw new IllegalArgumentException("Null 'location' for index 0 not permitted.");
        }
        this.rangeAxisLocations.put(Integer.valueOf(index), location);
        if (notify) {
            fireChangeEvent();
        }
    }

    public RectangleEdge getRangeAxisEdge() {
        return getRangeAxisEdge(0);
    }

    public RectangleEdge getRangeAxisEdge(int index) {
        return Plot.resolveRangeAxisLocation(getRangeAxisLocation(index), this.orientation);
    }

    public int getRangeAxisCount() {
        return this.rangeAxes.size();
    }

    public void clearRangeAxes() {
        for (ValueAxis yAxis : this.rangeAxes.values()) {
            if (yAxis != null) {
                yAxis.removeChangeListener(this);
            }
        }
        this.rangeAxes.clear();
        fireChangeEvent();
    }

    public void configureRangeAxes() {
        for (ValueAxis yAxis : this.rangeAxes.values()) {
            if (yAxis != null) {
                yAxis.configure();
            }
        }
    }

    public CategoryDataset getDataset() {
        return getDataset(0);
    }

    public CategoryDataset getDataset(int index) {
        return (CategoryDataset) this.datasets.get(Integer.valueOf(index));
    }

    public void setDataset(CategoryDataset dataset) {
        setDataset(0, dataset);
    }

    public void setDataset(int index, CategoryDataset dataset) {
        CategoryDataset existing = (CategoryDataset) this.datasets.get(Integer.valueOf(index));
        if (existing != null) {
            existing.removeChangeListener(this);
        }
        this.datasets.put(Integer.valueOf(index), dataset);
        if (dataset != null) {
            dataset.addChangeListener(this);
        }
        datasetChanged(new DatasetChangeEvent(this, dataset));
    }

    public int getDatasetCount() {
        return this.datasets.size();
    }

    public int indexOf(CategoryDataset dataset) {
        for (Entry<Integer, CategoryDataset> entry : this.datasets.entrySet()) {
            if (entry.getValue() == dataset) {
                return ((Integer) entry.getKey()).intValue();
            }
        }
        return -1;
    }

    public void mapDatasetToDomainAxis(int index, int axisIndex) {
        List<Integer> axisIndices = new ArrayList(1);
        axisIndices.add(Integer.valueOf(axisIndex));
        mapDatasetToDomainAxes(index, axisIndices);
    }

    public void mapDatasetToDomainAxes(int index, List axisIndices) {
        ParamChecks.requireNonNegative(index, "index");
        checkAxisIndices(axisIndices);
        this.datasetToDomainAxesMap.put(Integer.valueOf(index), new ArrayList(axisIndices));
        datasetChanged(new DatasetChangeEvent(this, getDataset(index)));
    }

    private void checkAxisIndices(List indices) {
        if (indices != null) {
            int count = indices.size();
            if (count == 0) {
                throw new IllegalArgumentException("Empty list not permitted.");
            }
            HashSet set = new HashSet();
            int i = 0;
            while (i < count) {
                Object item = indices.get(i);
                if (!(item instanceof Integer)) {
                    throw new IllegalArgumentException("Indices must be Integer instances.");
                } else if (set.contains(item)) {
                    throw new IllegalArgumentException("Indices must be unique.");
                } else {
                    set.add(item);
                    i++;
                }
            }
        }
    }

    public CategoryAxis getDomainAxisForDataset(int index) {
        ParamChecks.requireNonNegative(index, "index");
        List axisIndices = (List) this.datasetToDomainAxesMap.get(new Integer(index));
        if (axisIndices != null) {
            return getDomainAxis(((Integer) axisIndices.get(0)).intValue());
        }
        return getDomainAxis(0);
    }

    public void mapDatasetToRangeAxis(int index, int axisIndex) {
        List axisIndices = new ArrayList(1);
        axisIndices.add(new Integer(axisIndex));
        mapDatasetToRangeAxes(index, axisIndices);
    }

    public void mapDatasetToRangeAxes(int index, List axisIndices) {
        ParamChecks.requireNonNegative(index, "index");
        checkAxisIndices(axisIndices);
        this.datasetToRangeAxesMap.put(Integer.valueOf(index), new ArrayList(axisIndices));
        datasetChanged(new DatasetChangeEvent(this, getDataset(index)));
    }

    public ValueAxis getRangeAxisForDataset(int index) {
        ParamChecks.requireNonNegative(index, "index");
        List axisIndices = (List) this.datasetToRangeAxesMap.get(new Integer(index));
        if (axisIndices != null) {
            return getRangeAxis(((Integer) axisIndices.get(0)).intValue());
        }
        return getRangeAxis(0);
    }

    public int getRendererCount() {
        return this.renderers.size();
    }

    public CategoryItemRenderer getRenderer() {
        return getRenderer(0);
    }

    public CategoryItemRenderer getRenderer(int index) {
        CategoryItemRenderer renderer = (CategoryItemRenderer) this.renderers.get(Integer.valueOf(index));
        if (renderer == null) {
            return (CategoryItemRenderer) this.renderers.get(Integer.valueOf(0));
        }
        return renderer;
    }

    public void setRenderer(CategoryItemRenderer renderer) {
        setRenderer(0, renderer, DEFAULT_RANGE_GRIDLINES_VISIBLE);
    }

    public void setRenderer(CategoryItemRenderer renderer, boolean notify) {
        setRenderer(0, renderer, notify);
    }

    public void setRenderer(int index, CategoryItemRenderer renderer) {
        setRenderer(index, renderer, DEFAULT_RANGE_GRIDLINES_VISIBLE);
    }

    public void setRenderer(int index, CategoryItemRenderer renderer, boolean notify) {
        CategoryItemRenderer existing = (CategoryItemRenderer) this.renderers.get(Integer.valueOf(index));
        if (existing != null) {
            existing.removeChangeListener(this);
        }
        this.renderers.put(Integer.valueOf(index), renderer);
        if (renderer != null) {
            renderer.setPlot(this);
            renderer.addChangeListener(this);
        }
        configureDomainAxes();
        configureRangeAxes();
        if (notify) {
            fireChangeEvent();
        }
    }

    public void setRenderers(CategoryItemRenderer[] renderers) {
        for (int i = 0; i < renderers.length; i++) {
            setRenderer(i, renderers[i], DEFAULT_DOMAIN_GRIDLINES_VISIBLE);
        }
        fireChangeEvent();
    }

    public CategoryItemRenderer getRendererForDataset(CategoryDataset dataset) {
        int datasetIndex = indexOf(dataset);
        if (datasetIndex < 0) {
            return null;
        }
        CategoryItemRenderer renderer = (CategoryItemRenderer) this.renderers.get(Integer.valueOf(datasetIndex));
        if (renderer == null) {
            return getRenderer();
        }
        return renderer;
    }

    public int getIndexOf(CategoryItemRenderer renderer) {
        for (Entry<Integer, CategoryItemRenderer> entry : this.renderers.entrySet()) {
            if (entry.getValue() == renderer) {
                return ((Integer) entry.getKey()).intValue();
            }
        }
        return -1;
    }

    public DatasetRenderingOrder getDatasetRenderingOrder() {
        return this.renderingOrder;
    }

    public void setDatasetRenderingOrder(DatasetRenderingOrder order) {
        ParamChecks.nullNotPermitted(order, "order");
        this.renderingOrder = order;
        fireChangeEvent();
    }

    public SortOrder getColumnRenderingOrder() {
        return this.columnRenderingOrder;
    }

    public void setColumnRenderingOrder(SortOrder order) {
        ParamChecks.nullNotPermitted(order, "order");
        this.columnRenderingOrder = order;
        fireChangeEvent();
    }

    public SortOrder getRowRenderingOrder() {
        return this.rowRenderingOrder;
    }

    public void setRowRenderingOrder(SortOrder order) {
        ParamChecks.nullNotPermitted(order, "order");
        this.rowRenderingOrder = order;
        fireChangeEvent();
    }

    public boolean isDomainGridlinesVisible() {
        return this.domainGridlinesVisible;
    }

    public void setDomainGridlinesVisible(boolean visible) {
        if (this.domainGridlinesVisible != visible) {
            this.domainGridlinesVisible = visible;
            fireChangeEvent();
        }
    }

    public CategoryAnchor getDomainGridlinePosition() {
        return this.domainGridlinePosition;
    }

    public void setDomainGridlinePosition(CategoryAnchor position) {
        ParamChecks.nullNotPermitted(position, "position");
        this.domainGridlinePosition = position;
        fireChangeEvent();
    }

    public Stroke getDomainGridlineStroke() {
        return this.domainGridlineStroke;
    }

    public void setDomainGridlineStroke(Stroke stroke) {
        ParamChecks.nullNotPermitted(stroke, "stroke");
        this.domainGridlineStroke = stroke;
        fireChangeEvent();
    }

    public Paint getDomainGridlinePaint() {
        return this.domainGridlinePaint;
    }

    public void setDomainGridlinePaint(Paint paint) {
        ParamChecks.nullNotPermitted(paint, "paint");
        this.domainGridlinePaint = paint;
        fireChangeEvent();
    }

    public boolean isRangeZeroBaselineVisible() {
        return this.rangeZeroBaselineVisible;
    }

    public void setRangeZeroBaselineVisible(boolean visible) {
        this.rangeZeroBaselineVisible = visible;
        fireChangeEvent();
    }

    public Stroke getRangeZeroBaselineStroke() {
        return this.rangeZeroBaselineStroke;
    }

    public void setRangeZeroBaselineStroke(Stroke stroke) {
        ParamChecks.nullNotPermitted(stroke, "stroke");
        this.rangeZeroBaselineStroke = stroke;
        fireChangeEvent();
    }

    public Paint getRangeZeroBaselinePaint() {
        return this.rangeZeroBaselinePaint;
    }

    public void setRangeZeroBaselinePaint(Paint paint) {
        ParamChecks.nullNotPermitted(paint, "paint");
        this.rangeZeroBaselinePaint = paint;
        fireChangeEvent();
    }

    public boolean isRangeGridlinesVisible() {
        return this.rangeGridlinesVisible;
    }

    public void setRangeGridlinesVisible(boolean visible) {
        if (this.rangeGridlinesVisible != visible) {
            this.rangeGridlinesVisible = visible;
            fireChangeEvent();
        }
    }

    public Stroke getRangeGridlineStroke() {
        return this.rangeGridlineStroke;
    }

    public void setRangeGridlineStroke(Stroke stroke) {
        ParamChecks.nullNotPermitted(stroke, "stroke");
        this.rangeGridlineStroke = stroke;
        fireChangeEvent();
    }

    public Paint getRangeGridlinePaint() {
        return this.rangeGridlinePaint;
    }

    public void setRangeGridlinePaint(Paint paint) {
        ParamChecks.nullNotPermitted(paint, "paint");
        this.rangeGridlinePaint = paint;
        fireChangeEvent();
    }

    public boolean isRangeMinorGridlinesVisible() {
        return this.rangeMinorGridlinesVisible;
    }

    public void setRangeMinorGridlinesVisible(boolean visible) {
        if (this.rangeMinorGridlinesVisible != visible) {
            this.rangeMinorGridlinesVisible = visible;
            fireChangeEvent();
        }
    }

    public Stroke getRangeMinorGridlineStroke() {
        return this.rangeMinorGridlineStroke;
    }

    public void setRangeMinorGridlineStroke(Stroke stroke) {
        ParamChecks.nullNotPermitted(stroke, "stroke");
        this.rangeMinorGridlineStroke = stroke;
        fireChangeEvent();
    }

    public Paint getRangeMinorGridlinePaint() {
        return this.rangeMinorGridlinePaint;
    }

    public void setRangeMinorGridlinePaint(Paint paint) {
        ParamChecks.nullNotPermitted(paint, "paint");
        this.rangeMinorGridlinePaint = paint;
        fireChangeEvent();
    }

    public LegendItemCollection getFixedLegendItems() {
        return this.fixedLegendItems;
    }

    public void setFixedLegendItems(LegendItemCollection items) {
        this.fixedLegendItems = items;
        fireChangeEvent();
    }

    public LegendItemCollection getLegendItems() {
        if (this.fixedLegendItems != null) {
            return this.fixedLegendItems;
        }
        LegendItemCollection result = new LegendItemCollection();
        for (CategoryDataset dataset : this.datasets.values()) {
            if (dataset != null) {
                CategoryItemRenderer renderer = getRenderer(indexOf(dataset));
                if (renderer != null) {
                    result.addAll(renderer.getLegendItems());
                }
            }
        }
        return result;
    }

    public void handleClick(int x, int y, PlotRenderingInfo info) {
        if (info.getDataArea().contains((double) x, (double) y)) {
            double java2D = 0.0d;
            if (this.orientation == PlotOrientation.HORIZONTAL) {
                java2D = (double) x;
            } else if (this.orientation == PlotOrientation.VERTICAL) {
                java2D = (double) y;
            }
            double value = getRangeAxis().java2DToValue(java2D, info.getDataArea(), Plot.resolveRangeAxisLocation(getRangeAxisLocation(), this.orientation));
            setAnchorValue(value);
            setRangeCrosshairValue(value);
        }
    }

    public void zoom(double percent) {
        if (percent > 0.0d) {
            double scaledRange = getRangeAxis().getRange().getLength() * percent;
            getRangeAxis().setRange(this.anchorValue - (scaledRange / DateAxis.DEFAULT_AUTO_RANGE_MINIMUM_SIZE_IN_MILLISECONDS), this.anchorValue + (scaledRange / DateAxis.DEFAULT_AUTO_RANGE_MINIMUM_SIZE_IN_MILLISECONDS));
            return;
        }
        getRangeAxis().setAutoRange(DEFAULT_RANGE_GRIDLINES_VISIBLE);
    }

    public void annotationChanged(AnnotationChangeEvent event) {
        if (getParent() != null) {
            getParent().annotationChanged(event);
        } else {
            notifyListeners(new PlotChangeEvent(this));
        }
    }

    public void datasetChanged(DatasetChangeEvent event) {
        for (ValueAxis yAxis : this.rangeAxes.values()) {
            if (yAxis != null) {
                yAxis.configure();
            }
        }
        if (getParent() != null) {
            getParent().datasetChanged(event);
            return;
        }
        PlotChangeEvent e = new PlotChangeEvent(this);
        e.setType(ChartChangeEventType.DATASET_UPDATED);
        notifyListeners(e);
    }

    public void rendererChanged(RendererChangeEvent event) {
        Plot parent = getParent();
        if (parent == null) {
            configureRangeAxes();
            notifyListeners(new PlotChangeEvent(this));
        } else if (parent instanceof RendererChangeListener) {
            ((RendererChangeListener) parent).rendererChanged(event);
        } else {
            throw new RuntimeException("The renderer has changed and I don't know what to do!");
        }
    }

    public void addDomainMarker(CategoryMarker marker) {
        addDomainMarker(marker, Layer.FOREGROUND);
    }

    public void addDomainMarker(CategoryMarker marker, Layer layer) {
        addDomainMarker(0, marker, layer);
    }

    public void addDomainMarker(int index, CategoryMarker marker, Layer layer) {
        addDomainMarker(index, marker, layer, DEFAULT_RANGE_GRIDLINES_VISIBLE);
    }

    public void addDomainMarker(int index, CategoryMarker marker, Layer layer, boolean notify) {
        ParamChecks.nullNotPermitted(marker, "marker");
        ParamChecks.nullNotPermitted(layer, "layer");
        Collection markers;
        if (layer == Layer.FOREGROUND) {
            markers = (Collection) this.foregroundDomainMarkers.get(new Integer(index));
            if (markers == null) {
                markers = new ArrayList();
                this.foregroundDomainMarkers.put(new Integer(index), markers);
            }
            markers.add(marker);
        } else if (layer == Layer.BACKGROUND) {
            markers = (Collection) this.backgroundDomainMarkers.get(new Integer(index));
            if (markers == null) {
                markers = new ArrayList();
                this.backgroundDomainMarkers.put(new Integer(index), markers);
            }
            markers.add(marker);
        }
        marker.addChangeListener(this);
        if (notify) {
            fireChangeEvent();
        }
    }

    public void clearDomainMarkers() {
        if (this.backgroundDomainMarkers != null) {
            for (Integer key : this.backgroundDomainMarkers.keySet()) {
                clearDomainMarkers(key.intValue());
            }
            this.backgroundDomainMarkers.clear();
        }
        if (this.foregroundDomainMarkers != null) {
            for (Integer key2 : this.foregroundDomainMarkers.keySet()) {
                clearDomainMarkers(key2.intValue());
            }
            this.foregroundDomainMarkers.clear();
        }
        fireChangeEvent();
    }

    public Collection getDomainMarkers(Layer layer) {
        return getDomainMarkers(0, layer);
    }

    public Collection getDomainMarkers(int index, Layer layer) {
        Collection result = null;
        Integer key = new Integer(index);
        if (layer == Layer.FOREGROUND) {
            result = (Collection) this.foregroundDomainMarkers.get(key);
        } else if (layer == Layer.BACKGROUND) {
            result = (Collection) this.backgroundDomainMarkers.get(key);
        }
        if (result != null) {
            return Collections.unmodifiableCollection(result);
        }
        return result;
    }

    public void clearDomainMarkers(int index) {
        Collection<Marker> markers;
        Integer key = new Integer(index);
        if (this.backgroundDomainMarkers != null) {
            markers = (Collection) this.backgroundDomainMarkers.get(key);
            if (markers != null) {
                for (Marker m : markers) {
                    m.removeChangeListener(this);
                }
                markers.clear();
            }
        }
        if (this.foregroundDomainMarkers != null) {
            markers = (Collection) this.foregroundDomainMarkers.get(key);
            if (markers != null) {
                for (Marker m2 : markers) {
                    m2.removeChangeListener(this);
                }
                markers.clear();
            }
        }
        fireChangeEvent();
    }

    public boolean removeDomainMarker(Marker marker) {
        return removeDomainMarker(marker, Layer.FOREGROUND);
    }

    public boolean removeDomainMarker(Marker marker, Layer layer) {
        return removeDomainMarker(0, marker, layer);
    }

    public boolean removeDomainMarker(int index, Marker marker, Layer layer) {
        return removeDomainMarker(index, marker, layer, DEFAULT_RANGE_GRIDLINES_VISIBLE);
    }

    public boolean removeDomainMarker(int index, Marker marker, Layer layer, boolean notify) {
        ArrayList markers;
        if (layer == Layer.FOREGROUND) {
            markers = (ArrayList) this.foregroundDomainMarkers.get(new Integer(index));
        } else {
            markers = (ArrayList) this.backgroundDomainMarkers.get(new Integer(index));
        }
        if (markers == null) {
            return DEFAULT_DOMAIN_GRIDLINES_VISIBLE;
        }
        boolean removed = markers.remove(marker);
        if (!removed || !notify) {
            return removed;
        }
        fireChangeEvent();
        return removed;
    }

    public void addRangeMarker(Marker marker) {
        addRangeMarker(marker, Layer.FOREGROUND);
    }

    public void addRangeMarker(Marker marker, Layer layer) {
        addRangeMarker(0, marker, layer);
    }

    public void addRangeMarker(int index, Marker marker, Layer layer) {
        addRangeMarker(index, marker, layer, DEFAULT_RANGE_GRIDLINES_VISIBLE);
    }

    public void addRangeMarker(int index, Marker marker, Layer layer, boolean notify) {
        Collection markers;
        if (layer == Layer.FOREGROUND) {
            markers = (Collection) this.foregroundRangeMarkers.get(new Integer(index));
            if (markers == null) {
                markers = new ArrayList();
                this.foregroundRangeMarkers.put(new Integer(index), markers);
            }
            markers.add(marker);
        } else if (layer == Layer.BACKGROUND) {
            markers = (Collection) this.backgroundRangeMarkers.get(new Integer(index));
            if (markers == null) {
                markers = new ArrayList();
                this.backgroundRangeMarkers.put(new Integer(index), markers);
            }
            markers.add(marker);
        }
        marker.addChangeListener(this);
        if (notify) {
            fireChangeEvent();
        }
    }

    public void clearRangeMarkers() {
        if (this.backgroundRangeMarkers != null) {
            for (Integer key : this.backgroundRangeMarkers.keySet()) {
                clearRangeMarkers(key.intValue());
            }
            this.backgroundRangeMarkers.clear();
        }
        if (this.foregroundRangeMarkers != null) {
            for (Integer key2 : this.foregroundRangeMarkers.keySet()) {
                clearRangeMarkers(key2.intValue());
            }
            this.foregroundRangeMarkers.clear();
        }
        fireChangeEvent();
    }

    public Collection getRangeMarkers(Layer layer) {
        return getRangeMarkers(0, layer);
    }

    public Collection getRangeMarkers(int index, Layer layer) {
        Collection result = null;
        Integer key = new Integer(index);
        if (layer == Layer.FOREGROUND) {
            result = (Collection) this.foregroundRangeMarkers.get(key);
        } else if (layer == Layer.BACKGROUND) {
            result = (Collection) this.backgroundRangeMarkers.get(key);
        }
        if (result != null) {
            return Collections.unmodifiableCollection(result);
        }
        return result;
    }

    public void clearRangeMarkers(int index) {
        Collection<Marker> markers;
        Integer key = new Integer(index);
        if (this.backgroundRangeMarkers != null) {
            markers = (Collection) this.backgroundRangeMarkers.get(key);
            if (markers != null) {
                for (Marker m : markers) {
                    m.removeChangeListener(this);
                }
                markers.clear();
            }
        }
        if (this.foregroundRangeMarkers != null) {
            markers = (Collection) this.foregroundRangeMarkers.get(key);
            if (markers != null) {
                for (Marker m2 : markers) {
                    m2.removeChangeListener(this);
                }
                markers.clear();
            }
        }
        fireChangeEvent();
    }

    public boolean removeRangeMarker(Marker marker) {
        return removeRangeMarker(marker, Layer.FOREGROUND);
    }

    public boolean removeRangeMarker(Marker marker, Layer layer) {
        return removeRangeMarker(0, marker, layer);
    }

    public boolean removeRangeMarker(int index, Marker marker, Layer layer) {
        return removeRangeMarker(index, marker, layer, DEFAULT_RANGE_GRIDLINES_VISIBLE);
    }

    public boolean removeRangeMarker(int index, Marker marker, Layer layer, boolean notify) {
        ArrayList markers;
        ParamChecks.nullNotPermitted(marker, "marker");
        if (layer == Layer.FOREGROUND) {
            markers = (ArrayList) this.foregroundRangeMarkers.get(new Integer(index));
        } else {
            markers = (ArrayList) this.backgroundRangeMarkers.get(new Integer(index));
        }
        if (markers == null) {
            return DEFAULT_DOMAIN_GRIDLINES_VISIBLE;
        }
        boolean removed = markers.remove(marker);
        if (!removed || !notify) {
            return removed;
        }
        fireChangeEvent();
        return removed;
    }

    public boolean isDomainCrosshairVisible() {
        return this.domainCrosshairVisible;
    }

    public void setDomainCrosshairVisible(boolean flag) {
        if (this.domainCrosshairVisible != flag) {
            this.domainCrosshairVisible = flag;
            fireChangeEvent();
        }
    }

    public Comparable getDomainCrosshairRowKey() {
        return this.domainCrosshairRowKey;
    }

    public void setDomainCrosshairRowKey(Comparable key) {
        setDomainCrosshairRowKey(key, DEFAULT_RANGE_GRIDLINES_VISIBLE);
    }

    public void setDomainCrosshairRowKey(Comparable key, boolean notify) {
        this.domainCrosshairRowKey = key;
        if (notify) {
            fireChangeEvent();
        }
    }

    public Comparable getDomainCrosshairColumnKey() {
        return this.domainCrosshairColumnKey;
    }

    public void setDomainCrosshairColumnKey(Comparable key) {
        setDomainCrosshairColumnKey(key, DEFAULT_RANGE_GRIDLINES_VISIBLE);
    }

    public void setDomainCrosshairColumnKey(Comparable key, boolean notify) {
        this.domainCrosshairColumnKey = key;
        if (notify) {
            fireChangeEvent();
        }
    }

    public int getCrosshairDatasetIndex() {
        return this.crosshairDatasetIndex;
    }

    public void setCrosshairDatasetIndex(int index) {
        setCrosshairDatasetIndex(index, DEFAULT_RANGE_GRIDLINES_VISIBLE);
    }

    public void setCrosshairDatasetIndex(int index, boolean notify) {
        this.crosshairDatasetIndex = index;
        if (notify) {
            fireChangeEvent();
        }
    }

    public Paint getDomainCrosshairPaint() {
        return this.domainCrosshairPaint;
    }

    public void setDomainCrosshairPaint(Paint paint) {
        ParamChecks.nullNotPermitted(paint, "paint");
        this.domainCrosshairPaint = paint;
        fireChangeEvent();
    }

    public Stroke getDomainCrosshairStroke() {
        return this.domainCrosshairStroke;
    }

    public void setDomainCrosshairStroke(Stroke stroke) {
        ParamChecks.nullNotPermitted(stroke, "stroke");
        this.domainCrosshairStroke = stroke;
    }

    public boolean isRangeCrosshairVisible() {
        return this.rangeCrosshairVisible;
    }

    public void setRangeCrosshairVisible(boolean flag) {
        if (this.rangeCrosshairVisible != flag) {
            this.rangeCrosshairVisible = flag;
            fireChangeEvent();
        }
    }

    public boolean isRangeCrosshairLockedOnData() {
        return this.rangeCrosshairLockedOnData;
    }

    public void setRangeCrosshairLockedOnData(boolean flag) {
        if (this.rangeCrosshairLockedOnData != flag) {
            this.rangeCrosshairLockedOnData = flag;
            fireChangeEvent();
        }
    }

    public double getRangeCrosshairValue() {
        return this.rangeCrosshairValue;
    }

    public void setRangeCrosshairValue(double value) {
        setRangeCrosshairValue(value, DEFAULT_RANGE_GRIDLINES_VISIBLE);
    }

    public void setRangeCrosshairValue(double value, boolean notify) {
        this.rangeCrosshairValue = value;
        if (isRangeCrosshairVisible() && notify) {
            fireChangeEvent();
        }
    }

    public Stroke getRangeCrosshairStroke() {
        return this.rangeCrosshairStroke;
    }

    public void setRangeCrosshairStroke(Stroke stroke) {
        ParamChecks.nullNotPermitted(stroke, "stroke");
        this.rangeCrosshairStroke = stroke;
        fireChangeEvent();
    }

    public Paint getRangeCrosshairPaint() {
        return this.rangeCrosshairPaint;
    }

    public void setRangeCrosshairPaint(Paint paint) {
        ParamChecks.nullNotPermitted(paint, "paint");
        this.rangeCrosshairPaint = paint;
        fireChangeEvent();
    }

    public List getAnnotations() {
        return this.annotations;
    }

    public void addAnnotation(CategoryAnnotation annotation) {
        addAnnotation(annotation, DEFAULT_RANGE_GRIDLINES_VISIBLE);
    }

    public void addAnnotation(CategoryAnnotation annotation, boolean notify) {
        ParamChecks.nullNotPermitted(annotation, "annotation");
        this.annotations.add(annotation);
        annotation.addChangeListener(this);
        if (notify) {
            fireChangeEvent();
        }
    }

    public boolean removeAnnotation(CategoryAnnotation annotation) {
        return removeAnnotation(annotation, DEFAULT_RANGE_GRIDLINES_VISIBLE);
    }

    public boolean removeAnnotation(CategoryAnnotation annotation, boolean notify) {
        ParamChecks.nullNotPermitted(annotation, "annotation");
        boolean removed = this.annotations.remove(annotation);
        annotation.removeChangeListener(this);
        if (removed && notify) {
            fireChangeEvent();
        }
        return removed;
    }

    public void clearAnnotations() {
        for (int i = 0; i < this.annotations.size(); i++) {
            ((CategoryAnnotation) this.annotations.get(i)).removeChangeListener(this);
        }
        this.annotations.clear();
        fireChangeEvent();
    }

    public ShadowGenerator getShadowGenerator() {
        return this.shadowGenerator;
    }

    public void setShadowGenerator(ShadowGenerator generator) {
        this.shadowGenerator = generator;
        fireChangeEvent();
    }

    protected AxisSpace calculateDomainAxisSpace(Graphics2D g2, Rectangle2D plotArea, AxisSpace space) {
        if (space == null) {
            space = new AxisSpace();
        }
        if (this.fixedDomainAxisSpace == null) {
            RectangleEdge domainEdge = Plot.resolveDomainAxisLocation(getDomainAxisLocation(), this.orientation);
            if (this.drawSharedDomainAxis) {
                space = getDomainAxis().reserveSpace(g2, this, plotArea, domainEdge, space);
            }
            for (CategoryAxis xAxis : this.domainAxes.values()) {
                if (xAxis != null) {
                    space = xAxis.reserveSpace(g2, this, plotArea, getDomainAxisEdge(getDomainAxisIndex(xAxis)), space);
                }
            }
        } else if (this.orientation.isHorizontal()) {
            space.ensureAtLeast(this.fixedDomainAxisSpace.getLeft(), RectangleEdge.LEFT);
            space.ensureAtLeast(this.fixedDomainAxisSpace.getRight(), RectangleEdge.RIGHT);
        } else if (this.orientation.isVertical()) {
            space.ensureAtLeast(this.fixedDomainAxisSpace.getTop(), RectangleEdge.TOP);
            space.ensureAtLeast(this.fixedDomainAxisSpace.getBottom(), RectangleEdge.BOTTOM);
        }
        return space;
    }

    protected AxisSpace calculateRangeAxisSpace(Graphics2D g2, Rectangle2D plotArea, AxisSpace space) {
        if (space == null) {
            space = new AxisSpace();
        }
        if (this.fixedRangeAxisSpace == null) {
            for (ValueAxis yAxis : this.rangeAxes.values()) {
                if (yAxis != null) {
                    space = yAxis.reserveSpace(g2, this, plotArea, getRangeAxisEdge(findRangeAxisIndex(yAxis)), space);
                }
            }
        } else if (this.orientation.isHorizontal()) {
            space.ensureAtLeast(this.fixedRangeAxisSpace.getTop(), RectangleEdge.TOP);
            space.ensureAtLeast(this.fixedRangeAxisSpace.getBottom(), RectangleEdge.BOTTOM);
        } else if (this.orientation == PlotOrientation.VERTICAL) {
            space.ensureAtLeast(this.fixedRangeAxisSpace.getLeft(), RectangleEdge.LEFT);
            space.ensureAtLeast(this.fixedRangeAxisSpace.getRight(), RectangleEdge.RIGHT);
        }
        return space;
    }

    private Rectangle integerise(Rectangle2D rect) {
        int x0 = (int) Math.ceil(rect.getMinX());
        int y0 = (int) Math.ceil(rect.getMinY());
        return new Rectangle(x0, y0, ((int) Math.floor(rect.getMaxX())) - x0, ((int) Math.floor(rect.getMaxY())) - y0);
    }

    protected AxisSpace calculateAxisSpace(Graphics2D g2, Rectangle2D plotArea) {
        return calculateDomainAxisSpace(g2, plotArea, calculateRangeAxisSpace(g2, plotArea, new AxisSpace()));
    }

    public void draw(Graphics2D g2, Rectangle2D area, Point2D anchor, PlotState parentState, PlotRenderingInfo state) {
        boolean b1 = area.getWidth() <= XYPointerAnnotation.DEFAULT_TIP_RADIUS ? DEFAULT_RANGE_GRIDLINES_VISIBLE : DEFAULT_DOMAIN_GRIDLINES_VISIBLE;
        boolean b2 = area.getHeight() <= XYPointerAnnotation.DEFAULT_TIP_RADIUS ? DEFAULT_RANGE_GRIDLINES_VISIBLE : DEFAULT_DOMAIN_GRIDLINES_VISIBLE;
        if (!b1 && !b2) {
            if (state == null) {
                PlotRenderingInfo plotRenderingInfo = new PlotRenderingInfo(null);
            }
            state.setPlotArea(area);
            getInsets().trim(area);
            Rectangle2D dataArea = calculateAxisSpace(g2, area).shrink(area, null);
            this.axisOffset.trim(dataArea);
            dataArea = integerise(dataArea);
            if (!dataArea.isEmpty()) {
                state.setDataArea(dataArea);
                createAndAddEntity((Rectangle2D) dataArea.clone(), state, null, null);
                if (getRenderer() != null) {
                    getRenderer().drawBackground(g2, this, dataArea);
                } else {
                    drawBackground(g2, dataArea);
                }
                Map axisStateMap = drawAxes(g2, area, dataArea, state);
                if (!(anchor == null || dataArea.contains(anchor))) {
                    anchor = ShapeUtilities.getPointInRectangle(anchor.getX(), anchor.getY(), dataArea);
                }
                CategoryCrosshairState crosshairState = new CategoryCrosshairState();
                crosshairState.setCrosshairDistance(Double.POSITIVE_INFINITY);
                crosshairState.setAnchor(anchor);
                crosshairState.setAnchorX(Double.NaN);
                crosshairState.setAnchorY(Double.NaN);
                if (anchor != null) {
                    ValueAxis rangeAxis = getRangeAxis();
                    if (rangeAxis != null) {
                        double y;
                        if (getOrientation() == PlotOrientation.VERTICAL) {
                            y = rangeAxis.java2DToValue(anchor.getY(), dataArea, getRangeAxisEdge());
                        } else {
                            y = rangeAxis.java2DToValue(anchor.getX(), dataArea, getRangeAxisEdge());
                        }
                        crosshairState.setAnchorY(y);
                    }
                }
                crosshairState.setRowKey(getDomainCrosshairRowKey());
                crosshairState.setColumnKey(getDomainCrosshairColumnKey());
                crosshairState.setCrosshairY(getRangeCrosshairValue());
                Shape savedClip = g2.getClip();
                g2.clip(dataArea);
                drawDomainGridlines(g2, dataArea);
                AxisState rangeAxisState = (AxisState) axisStateMap.get(getRangeAxis());
                if (rangeAxisState == null && parentState != null) {
                    rangeAxisState = (AxisState) parentState.getSharedAxisStates().get(getRangeAxis());
                }
                if (rangeAxisState != null) {
                    drawRangeGridlines(g2, dataArea, rangeAxisState.getTicks());
                    drawZeroRangeBaseline(g2, dataArea);
                }
                Graphics2D savedG2 = g2;
                BufferedImage dataImage = null;
                boolean suppressShadow = Boolean.TRUE.equals(g2.getRenderingHint(JFreeChart.KEY_SUPPRESS_SHADOW_GENERATION));
                if (!(this.shadowGenerator == null || suppressShadow)) {
                    BufferedImage bufferedImage = new BufferedImage((int) dataArea.getWidth(), (int) dataArea.getHeight(), 2);
                    g2 = bufferedImage.createGraphics();
                    g2.translate(-dataArea.getX(), -dataArea.getY());
                    g2.setRenderingHints(savedG2.getRenderingHints());
                }
                for (CategoryItemRenderer indexOf : this.renderers.values()) {
                    drawDomainMarkers(g2, dataArea, getIndexOf(indexOf), Layer.BACKGROUND);
                }
                for (CategoryItemRenderer indexOf2 : this.renderers.values()) {
                    drawRangeMarkers(g2, dataArea, getIndexOf(indexOf2), Layer.BACKGROUND);
                }
                boolean foundData = DEFAULT_DOMAIN_GRIDLINES_VISIBLE;
                Composite originalComposite = g2.getComposite();
                g2.setComposite(AlphaComposite.getInstance(3, getForegroundAlpha()));
                DatasetRenderingOrder order = getDatasetRenderingOrder();
                for (Integer intValue : getDatasetIndices(order)) {
                    foundData = (render(g2, dataArea, intValue.intValue(), state, crosshairState) || foundData) ? DEFAULT_RANGE_GRIDLINES_VISIBLE : DEFAULT_DOMAIN_GRIDLINES_VISIBLE;
                }
                List<Integer> rendererIndices = getRendererIndices(order);
                for (Integer intValue2 : rendererIndices) {
                    drawDomainMarkers(g2, dataArea, intValue2.intValue(), Layer.FOREGROUND);
                }
                for (Integer intValue22 : rendererIndices) {
                    drawRangeMarkers(g2, dataArea, intValue22.intValue(), Layer.FOREGROUND);
                }
                drawAnnotations(g2, dataArea);
                if (!(this.shadowGenerator == null || suppressShadow)) {
                    g2 = savedG2;
                    g2.drawImage(this.shadowGenerator.createDropShadow(dataImage), ((int) dataArea.getX()) + this.shadowGenerator.calculateOffsetX(), ((int) dataArea.getY()) + this.shadowGenerator.calculateOffsetY(), null);
                    g2.drawImage(dataImage, (int) dataArea.getX(), (int) dataArea.getY(), null);
                }
                g2.setClip(savedClip);
                g2.setComposite(originalComposite);
                if (!foundData) {
                    drawNoDataMessage(g2, dataArea);
                }
                int datasetIndex = crosshairState.getDatasetIndex();
                setCrosshairDatasetIndex(datasetIndex, DEFAULT_DOMAIN_GRIDLINES_VISIBLE);
                Comparable rowKey = crosshairState.getRowKey();
                Comparable columnKey = crosshairState.getColumnKey();
                setDomainCrosshairRowKey(rowKey, DEFAULT_DOMAIN_GRIDLINES_VISIBLE);
                setDomainCrosshairColumnKey(columnKey, DEFAULT_DOMAIN_GRIDLINES_VISIBLE);
                if (isDomainCrosshairVisible() && columnKey != null) {
                    drawDomainCrosshair(g2, dataArea, this.orientation, datasetIndex, rowKey, columnKey, getDomainCrosshairStroke(), getDomainCrosshairPaint());
                }
                ValueAxis yAxis = getRangeAxisForDataset(datasetIndex);
                RectangleEdge yAxisEdge = getRangeAxisEdge();
                if (!(this.rangeCrosshairLockedOnData || anchor == null)) {
                    double yy;
                    if (getOrientation() == PlotOrientation.VERTICAL) {
                        yy = yAxis.java2DToValue(anchor.getY(), dataArea, yAxisEdge);
                    } else {
                        yy = yAxis.java2DToValue(anchor.getX(), dataArea, yAxisEdge);
                    }
                    crosshairState.setCrosshairY(yy);
                }
                setRangeCrosshairValue(crosshairState.getCrosshairY(), DEFAULT_DOMAIN_GRIDLINES_VISIBLE);
                if (isRangeCrosshairVisible()) {
                    drawRangeCrosshair(g2, dataArea, getOrientation(), getRangeCrosshairValue(), yAxis, getRangeCrosshairStroke(), getRangeCrosshairPaint());
                }
                if (!isOutlineVisible()) {
                    return;
                }
                if (getRenderer() != null) {
                    getRenderer().drawOutline(g2, this, dataArea);
                } else {
                    drawOutline(g2, dataArea);
                }
            }
        }
    }

    private List<Integer> getDatasetIndices(DatasetRenderingOrder order) {
        List<Integer> result = new ArrayList();
        for (Entry<Integer, CategoryDataset> entry : this.datasets.entrySet()) {
            if (entry.getValue() != null) {
                result.add(entry.getKey());
            }
        }
        Collections.sort(result);
        if (order == DatasetRenderingOrder.REVERSE) {
            Collections.reverse(result);
        }
        return result;
    }

    private List<Integer> getRendererIndices(DatasetRenderingOrder order) {
        List<Integer> result = new ArrayList();
        for (Entry<Integer, CategoryItemRenderer> entry : this.renderers.entrySet()) {
            if (entry.getValue() != null) {
                result.add(entry.getKey());
            }
        }
        Collections.sort(result);
        if (order == DatasetRenderingOrder.REVERSE) {
            Collections.reverse(result);
        }
        return result;
    }

    public void drawBackground(Graphics2D g2, Rectangle2D area) {
        fillBackground(g2, area, this.orientation);
        drawBackgroundImage(g2, area);
    }

    protected Map drawAxes(Graphics2D g2, Rectangle2D plotArea, Rectangle2D dataArea, PlotRenderingInfo plotState) {
        AxisCollection axisCollection = new AxisCollection();
        for (CategoryAxis xAxis : this.domainAxes.values()) {
            if (xAxis != null) {
                axisCollection.add(xAxis, getDomainAxisEdge(getDomainAxisIndex(xAxis)));
            }
        }
        for (ValueAxis yAxis : this.rangeAxes.values()) {
            if (yAxis != null) {
                axisCollection.add(yAxis, getRangeAxisEdge(findRangeAxisIndex(yAxis)));
            }
        }
        Map axisStateMap = new HashMap();
        double cursor = dataArea.getMinY() - this.axisOffset.calculateTopOutset(dataArea.getHeight());
        for (Axis axis : axisCollection.getAxesAtTop()) {
            if (axis != null) {
                AxisState axisState = axis.draw(g2, cursor, plotArea, dataArea, RectangleEdge.TOP, plotState);
                cursor = axisState.getCursor();
                axisStateMap.put(axis, axisState);
            }
        }
        cursor = dataArea.getMaxY() + this.axisOffset.calculateBottomOutset(dataArea.getHeight());
        for (Axis axis2 : axisCollection.getAxesAtBottom()) {
            if (axis2 != null) {
                axisState = axis2.draw(g2, cursor, plotArea, dataArea, RectangleEdge.BOTTOM, plotState);
                cursor = axisState.getCursor();
                axisStateMap.put(axis2, axisState);
            }
        }
        cursor = dataArea.getMinX() - this.axisOffset.calculateLeftOutset(dataArea.getWidth());
        for (Axis axis22 : axisCollection.getAxesAtLeft()) {
            if (axis22 != null) {
                axisState = axis22.draw(g2, cursor, plotArea, dataArea, RectangleEdge.LEFT, plotState);
                cursor = axisState.getCursor();
                axisStateMap.put(axis22, axisState);
            }
        }
        cursor = dataArea.getMaxX() + this.axisOffset.calculateRightOutset(dataArea.getWidth());
        for (Axis axis222 : axisCollection.getAxesAtRight()) {
            if (axis222 != null) {
                axisState = axis222.draw(g2, cursor, plotArea, dataArea, RectangleEdge.RIGHT, plotState);
                cursor = axisState.getCursor();
                axisStateMap.put(axis222, axisState);
            }
        }
        return axisStateMap;
    }

    public boolean render(Graphics2D g2, Rectangle2D dataArea, int index, PlotRenderingInfo info, CategoryCrosshairState crosshairState) {
        boolean foundData = DEFAULT_DOMAIN_GRIDLINES_VISIBLE;
        CategoryDataset currentDataset = getDataset(index);
        CategoryItemRenderer renderer = getRenderer(index);
        CategoryAxis domainAxis = getDomainAxisForDataset(index);
        ValueAxis rangeAxis = getRangeAxisForDataset(index);
        if ((!DatasetUtilities.isEmptyOrNull(currentDataset) ? DEFAULT_RANGE_GRIDLINES_VISIBLE : DEFAULT_DOMAIN_GRIDLINES_VISIBLE) && renderer != null) {
            foundData = DEFAULT_RANGE_GRIDLINES_VISIBLE;
            CategoryItemRendererState state = renderer.initialise(g2, dataArea, this, index, info);
            state.setCrosshairState(crosshairState);
            int columnCount = currentDataset.getColumnCount();
            int rowCount = currentDataset.getRowCount();
            int passCount = renderer.getPassCount();
            for (int pass = 0; pass < passCount; pass++) {
                int column;
                int row;
                if (this.columnRenderingOrder == SortOrder.ASCENDING) {
                    for (column = 0; column < columnCount; column++) {
                        if (this.rowRenderingOrder == SortOrder.ASCENDING) {
                            for (row = 0; row < rowCount; row++) {
                                renderer.drawItem(g2, state, dataArea, this, domainAxis, rangeAxis, currentDataset, row, column, pass);
                            }
                        } else {
                            for (row = rowCount - 1; row >= 0; row--) {
                                renderer.drawItem(g2, state, dataArea, this, domainAxis, rangeAxis, currentDataset, row, column, pass);
                            }
                        }
                    }
                } else {
                    for (column = columnCount - 1; column >= 0; column--) {
                        if (this.rowRenderingOrder == SortOrder.ASCENDING) {
                            for (row = 0; row < rowCount; row++) {
                                renderer.drawItem(g2, state, dataArea, this, domainAxis, rangeAxis, currentDataset, row, column, pass);
                            }
                        } else {
                            for (row = rowCount - 1; row >= 0; row--) {
                                renderer.drawItem(g2, state, dataArea, this, domainAxis, rangeAxis, currentDataset, row, column, pass);
                            }
                        }
                    }
                }
            }
        }
        return foundData;
    }

    protected void drawDomainGridlines(Graphics2D g2, Rectangle2D dataArea) {
        if (isDomainGridlinesVisible()) {
            CategoryAnchor anchor = getDomainGridlinePosition();
            RectangleEdge domainAxisEdge = getDomainAxisEdge();
            CategoryDataset dataset = getDataset();
            if (dataset != null) {
                CategoryAxis axis = getDomainAxis();
                if (axis != null) {
                    int columnCount = dataset.getColumnCount();
                    for (int c = 0; c < columnCount; c++) {
                        double xx = axis.getCategoryJava2DCoordinate(anchor, c, columnCount, dataArea, domainAxisEdge);
                        CategoryItemRenderer renderer1 = getRenderer();
                        if (renderer1 != null) {
                            renderer1.drawDomainGridline(g2, this, dataArea, xx);
                        }
                    }
                }
            }
        }
    }

    protected void drawRangeGridlines(Graphics2D g2, Rectangle2D dataArea, List ticks) {
        if (isRangeGridlinesVisible() || isRangeMinorGridlinesVisible()) {
            ValueAxis axis = getRangeAxis();
            if (axis != null) {
                CategoryItemRenderer r = getRenderer();
                if (r != null) {
                    Stroke gridStroke = null;
                    Paint gridPaint = null;
                    for (ValueTick tick : ticks) {
                        boolean paintLine = DEFAULT_DOMAIN_GRIDLINES_VISIBLE;
                        if (tick.getTickType() == TickType.MINOR && isRangeMinorGridlinesVisible()) {
                            gridStroke = getRangeMinorGridlineStroke();
                            gridPaint = getRangeMinorGridlinePaint();
                            paintLine = DEFAULT_RANGE_GRIDLINES_VISIBLE;
                        } else if (tick.getTickType() == TickType.MAJOR && isRangeGridlinesVisible()) {
                            gridStroke = getRangeGridlineStroke();
                            gridPaint = getRangeGridlinePaint();
                            paintLine = DEFAULT_RANGE_GRIDLINES_VISIBLE;
                        }
                        if (!(tick.getValue() == 0.0d && isRangeZeroBaselineVisible()) && paintLine) {
                            if (r instanceof AbstractCategoryItemRenderer) {
                                ((AbstractCategoryItemRenderer) r).drawRangeLine(g2, this, axis, dataArea, tick.getValue(), gridPaint, gridStroke);
                            } else {
                                r.drawRangeGridline(g2, this, axis, dataArea, tick.getValue());
                            }
                        }
                    }
                }
            }
        }
    }

    protected void drawZeroRangeBaseline(Graphics2D g2, Rectangle2D area) {
        if (isRangeZeroBaselineVisible()) {
            CategoryItemRenderer r = getRenderer();
            if (r instanceof AbstractCategoryItemRenderer) {
                ((AbstractCategoryItemRenderer) r).drawRangeLine(g2, this, getRangeAxis(), area, 0.0d, this.rangeZeroBaselinePaint, this.rangeZeroBaselineStroke);
                return;
            }
            r.drawRangeGridline(g2, this, getRangeAxis(), area, 0.0d);
        }
    }

    protected void drawAnnotations(Graphics2D g2, Rectangle2D dataArea) {
        if (getAnnotations() != null) {
            for (CategoryAnnotation annotation : getAnnotations()) {
                annotation.draw(g2, this, dataArea, getDomainAxis(), getRangeAxis());
            }
        }
    }

    protected void drawDomainMarkers(Graphics2D g2, Rectangle2D dataArea, int index, Layer layer) {
        CategoryItemRenderer r = getRenderer(index);
        if (r != null) {
            Collection<CategoryMarker> markers = getDomainMarkers(index, layer);
            CategoryAxis axis = getDomainAxisForDataset(index);
            if (markers != null && axis != null) {
                for (CategoryMarker marker : markers) {
                    r.drawDomainMarker(g2, this, axis, marker, dataArea);
                }
            }
        }
    }

    protected void drawRangeMarkers(Graphics2D g2, Rectangle2D dataArea, int index, Layer layer) {
        CategoryItemRenderer r = getRenderer(index);
        if (r != null) {
            Collection<Marker> markers = getRangeMarkers(index, layer);
            ValueAxis axis = getRangeAxisForDataset(index);
            if (markers != null && axis != null) {
                for (Marker marker : markers) {
                    r.drawRangeMarker(g2, this, axis, marker, dataArea);
                }
            }
        }
    }

    protected void drawRangeLine(Graphics2D g2, Rectangle2D dataArea, double value, Stroke stroke, Paint paint) {
        double java2D = getRangeAxis().valueToJava2D(value, dataArea, getRangeAxisEdge());
        Line2D line = null;
        if (this.orientation == PlotOrientation.HORIZONTAL) {
            line = new Double(java2D, dataArea.getMinY(), java2D, dataArea.getMaxY());
        } else if (this.orientation == PlotOrientation.VERTICAL) {
            Double doubleR = new Double(dataArea.getMinX(), java2D, dataArea.getMaxX(), java2D);
        }
        g2.setStroke(stroke);
        g2.setPaint(paint);
        g2.draw(line);
    }

    protected void drawDomainCrosshair(Graphics2D g2, Rectangle2D dataArea, PlotOrientation orientation, int datasetIndex, Comparable rowKey, Comparable columnKey, Stroke stroke, Paint paint) {
        Line2D line;
        CategoryDataset dataset = getDataset(datasetIndex);
        CategoryAxis axis = getDomainAxisForDataset(datasetIndex);
        CategoryItemRenderer renderer = getRenderer(datasetIndex);
        if (orientation == PlotOrientation.VERTICAL) {
            double xx = renderer.getItemMiddle(rowKey, columnKey, dataset, axis, dataArea, RectangleEdge.BOTTOM);
            line = new Double(xx, dataArea.getMinY(), xx, dataArea.getMaxY());
        } else {
            double yy = renderer.getItemMiddle(rowKey, columnKey, dataset, axis, dataArea, RectangleEdge.LEFT);
            Double doubleR = new Double(dataArea.getMinX(), yy, dataArea.getMaxX(), yy);
        }
        g2.setStroke(stroke);
        g2.setPaint(paint);
        g2.draw(line);
    }

    protected void drawRangeCrosshair(Graphics2D g2, Rectangle2D dataArea, PlotOrientation orientation, double value, ValueAxis axis, Stroke stroke, Paint paint) {
        if (axis.getRange().contains(value)) {
            Line2D line;
            if (orientation == PlotOrientation.HORIZONTAL) {
                double xx = axis.valueToJava2D(value, dataArea, RectangleEdge.BOTTOM);
                line = new Double(xx, dataArea.getMinY(), xx, dataArea.getMaxY());
            } else {
                double yy = axis.valueToJava2D(value, dataArea, RectangleEdge.LEFT);
                Double doubleR = new Double(dataArea.getMinX(), yy, dataArea.getMaxX(), yy);
            }
            g2.setStroke(stroke);
            g2.setPaint(paint);
            g2.draw(line);
        }
    }

    public Range getDataRange(ValueAxis axis) {
        Range result = null;
        List<CategoryDataset> mappedDatasets = new ArrayList();
        int rangeIndex = findRangeAxisIndex(axis);
        if (rangeIndex >= 0) {
            mappedDatasets.addAll(datasetsMappedToRangeAxis(rangeIndex));
        } else if (axis == getRangeAxis()) {
            mappedDatasets.addAll(datasetsMappedToRangeAxis(0));
        }
        for (CategoryDataset d : mappedDatasets) {
            CategoryItemRenderer r = getRendererForDataset(d);
            if (r != null) {
                result = Range.combine(result, r.findRangeBounds(d));
            }
        }
        return result;
    }

    private List<CategoryDataset> datasetsMappedToDomainAxis(int axisIndex) {
        Integer key = new Integer(axisIndex);
        List<CategoryDataset> result = new ArrayList();
        for (CategoryDataset dataset : this.datasets.values()) {
            if (dataset != null) {
                List mappedAxes = (List) this.datasetToDomainAxesMap.get(new Integer(indexOf(dataset)));
                if (mappedAxes == null) {
                    if (key.equals(ZERO)) {
                        result.add(dataset);
                    }
                } else if (mappedAxes.contains(key)) {
                    result.add(dataset);
                }
            }
        }
        return result;
    }

    private List datasetsMappedToRangeAxis(int index) {
        Integer key = new Integer(index);
        List result = new ArrayList();
        for (CategoryDataset dataset : this.datasets.values()) {
            int i = indexOf(dataset);
            List mappedAxes = (List) this.datasetToRangeAxesMap.get(new Integer(i));
            if (mappedAxes == null) {
                if (key.equals(ZERO)) {
                    result.add(this.datasets.get(Integer.valueOf(i)));
                }
            } else if (mappedAxes.contains(key)) {
                result.add(this.datasets.get(Integer.valueOf(i)));
            }
        }
        return result;
    }

    public int getWeight() {
        return this.weight;
    }

    public void setWeight(int weight) {
        this.weight = weight;
        fireChangeEvent();
    }

    public AxisSpace getFixedDomainAxisSpace() {
        return this.fixedDomainAxisSpace;
    }

    public void setFixedDomainAxisSpace(AxisSpace space) {
        setFixedDomainAxisSpace(space, DEFAULT_RANGE_GRIDLINES_VISIBLE);
    }

    public void setFixedDomainAxisSpace(AxisSpace space, boolean notify) {
        this.fixedDomainAxisSpace = space;
        if (notify) {
            fireChangeEvent();
        }
    }

    public AxisSpace getFixedRangeAxisSpace() {
        return this.fixedRangeAxisSpace;
    }

    public void setFixedRangeAxisSpace(AxisSpace space) {
        setFixedRangeAxisSpace(space, DEFAULT_RANGE_GRIDLINES_VISIBLE);
    }

    public void setFixedRangeAxisSpace(AxisSpace space, boolean notify) {
        this.fixedRangeAxisSpace = space;
        if (notify) {
            fireChangeEvent();
        }
    }

    public List getCategories() {
        if (getDataset() != null) {
            return Collections.unmodifiableList(getDataset().getColumnKeys());
        }
        return null;
    }

    public List getCategoriesForAxis(CategoryAxis axis) {
        List result = new ArrayList();
        for (CategoryDataset dataset : datasetsMappedToDomainAxis(getDomainAxisIndex(axis))) {
            for (int i = 0; i < dataset.getColumnCount(); i++) {
                Comparable category = dataset.getColumnKey(i);
                if (!result.contains(category)) {
                    result.add(category);
                }
            }
        }
        return result;
    }

    public boolean getDrawSharedDomainAxis() {
        return this.drawSharedDomainAxis;
    }

    public void setDrawSharedDomainAxis(boolean draw) {
        this.drawSharedDomainAxis = draw;
        fireChangeEvent();
    }

    public boolean isDomainPannable() {
        return DEFAULT_DOMAIN_GRIDLINES_VISIBLE;
    }

    public boolean isRangePannable() {
        return this.rangePannable;
    }

    public void setRangePannable(boolean pannable) {
        this.rangePannable = pannable;
    }

    public void panDomainAxes(double percent, PlotRenderingInfo info, Point2D source) {
    }

    public void panRangeAxes(double percent, PlotRenderingInfo info, Point2D source) {
        if (isRangePannable()) {
            for (ValueAxis axis : this.rangeAxes.values()) {
                if (axis != null) {
                    double adj = percent * axis.getRange().getLength();
                    if (axis.isInverted()) {
                        adj = -adj;
                    }
                    axis.setRange(axis.getLowerBound() + adj, axis.getUpperBound() + adj);
                }
            }
        }
    }

    public boolean isDomainZoomable() {
        return DEFAULT_DOMAIN_GRIDLINES_VISIBLE;
    }

    public boolean isRangeZoomable() {
        return DEFAULT_RANGE_GRIDLINES_VISIBLE;
    }

    public void zoomDomainAxes(double factor, PlotRenderingInfo state, Point2D source) {
    }

    public void zoomDomainAxes(double lowerPercent, double upperPercent, PlotRenderingInfo state, Point2D source) {
    }

    public void zoomDomainAxes(double factor, PlotRenderingInfo info, Point2D source, boolean useAnchor) {
    }

    public void zoomRangeAxes(double factor, PlotRenderingInfo state, Point2D source) {
        zoomRangeAxes(factor, state, source, (boolean) DEFAULT_DOMAIN_GRIDLINES_VISIBLE);
    }

    public void zoomRangeAxes(double factor, PlotRenderingInfo info, Point2D source, boolean useAnchor) {
        for (ValueAxis rangeAxis : this.rangeAxes.values()) {
            if (rangeAxis != null) {
                if (useAnchor) {
                    double sourceY = source.getY();
                    if (this.orientation.isHorizontal()) {
                        sourceY = source.getX();
                    }
                    rangeAxis.resizeRange2(factor, rangeAxis.java2DToValue(sourceY, info.getDataArea(), getRangeAxisEdge()));
                } else {
                    rangeAxis.resizeRange(factor);
                }
            }
        }
    }

    public void zoomRangeAxes(double lowerPercent, double upperPercent, PlotRenderingInfo state, Point2D source) {
        for (ValueAxis yAxis : this.rangeAxes.values()) {
            if (yAxis != null) {
                yAxis.zoomRange(lowerPercent, upperPercent);
            }
        }
    }

    public double getAnchorValue() {
        return this.anchorValue;
    }

    public void setAnchorValue(double value) {
        setAnchorValue(value, DEFAULT_RANGE_GRIDLINES_VISIBLE);
    }

    public void setAnchorValue(double value, boolean notify) {
        this.anchorValue = value;
        if (notify) {
            fireChangeEvent();
        }
    }

    public boolean equals(Object obj) {
        if (obj == this) {
            return DEFAULT_RANGE_GRIDLINES_VISIBLE;
        }
        if (!(obj instanceof CategoryPlot)) {
            return DEFAULT_DOMAIN_GRIDLINES_VISIBLE;
        }
        CategoryPlot that = (CategoryPlot) obj;
        if (this.orientation == that.orientation && ObjectUtilities.equal(this.axisOffset, that.axisOffset) && this.domainAxes.equals(that.domainAxes) && this.domainAxisLocations.equals(that.domainAxisLocations) && this.drawSharedDomainAxis == that.drawSharedDomainAxis && this.rangeAxes.equals(that.rangeAxes) && this.rangeAxisLocations.equals(that.rangeAxisLocations) && ObjectUtilities.equal(this.datasetToDomainAxesMap, that.datasetToDomainAxesMap) && ObjectUtilities.equal(this.datasetToRangeAxesMap, that.datasetToRangeAxesMap) && ObjectUtilities.equal(this.renderers, that.renderers) && this.renderingOrder == that.renderingOrder && this.columnRenderingOrder == that.columnRenderingOrder && this.rowRenderingOrder == that.rowRenderingOrder && this.domainGridlinesVisible == that.domainGridlinesVisible && this.domainGridlinePosition == that.domainGridlinePosition && ObjectUtilities.equal(this.domainGridlineStroke, that.domainGridlineStroke) && PaintUtilities.equal(this.domainGridlinePaint, that.domainGridlinePaint) && this.rangeGridlinesVisible == that.rangeGridlinesVisible && ObjectUtilities.equal(this.rangeGridlineStroke, that.rangeGridlineStroke) && PaintUtilities.equal(this.rangeGridlinePaint, that.rangeGridlinePaint) && this.anchorValue == that.anchorValue && this.rangeCrosshairVisible == that.rangeCrosshairVisible && this.rangeCrosshairValue == that.rangeCrosshairValue && ObjectUtilities.equal(this.rangeCrosshairStroke, that.rangeCrosshairStroke) && PaintUtilities.equal(this.rangeCrosshairPaint, that.rangeCrosshairPaint) && this.rangeCrosshairLockedOnData == that.rangeCrosshairLockedOnData && ObjectUtilities.equal(this.foregroundDomainMarkers, that.foregroundDomainMarkers) && ObjectUtilities.equal(this.backgroundDomainMarkers, that.backgroundDomainMarkers) && ObjectUtilities.equal(this.foregroundRangeMarkers, that.foregroundRangeMarkers) && ObjectUtilities.equal(this.backgroundRangeMarkers, that.backgroundRangeMarkers) && ObjectUtilities.equal(this.annotations, that.annotations) && this.weight == that.weight && ObjectUtilities.equal(this.fixedDomainAxisSpace, that.fixedDomainAxisSpace) && ObjectUtilities.equal(this.fixedRangeAxisSpace, that.fixedRangeAxisSpace) && ObjectUtilities.equal(this.fixedLegendItems, that.fixedLegendItems) && this.domainCrosshairVisible == that.domainCrosshairVisible && this.crosshairDatasetIndex == that.crosshairDatasetIndex && ObjectUtilities.equal(this.domainCrosshairColumnKey, that.domainCrosshairColumnKey) && ObjectUtilities.equal(this.domainCrosshairRowKey, that.domainCrosshairRowKey) && PaintUtilities.equal(this.domainCrosshairPaint, that.domainCrosshairPaint) && ObjectUtilities.equal(this.domainCrosshairStroke, that.domainCrosshairStroke) && this.rangeMinorGridlinesVisible == that.rangeMinorGridlinesVisible && PaintUtilities.equal(this.rangeMinorGridlinePaint, that.rangeMinorGridlinePaint) && ObjectUtilities.equal(this.rangeMinorGridlineStroke, that.rangeMinorGridlineStroke) && this.rangeZeroBaselineVisible == that.rangeZeroBaselineVisible && PaintUtilities.equal(this.rangeZeroBaselinePaint, that.rangeZeroBaselinePaint) && ObjectUtilities.equal(this.rangeZeroBaselineStroke, that.rangeZeroBaselineStroke) && ObjectUtilities.equal(this.shadowGenerator, that.shadowGenerator)) {
            return super.equals(obj);
        }
        return DEFAULT_DOMAIN_GRIDLINES_VISIBLE;
    }

    public Object clone() throws CloneNotSupportedException {
        CategoryPlot clone = (CategoryPlot) super.clone();
        clone.domainAxes = CloneUtils.cloneMapValues(this.domainAxes);
        for (CategoryAxis axis : clone.domainAxes.values()) {
            if (axis != null) {
                axis.setPlot(clone);
                axis.addChangeListener(clone);
            }
        }
        clone.rangeAxes = CloneUtils.cloneMapValues(this.rangeAxes);
        for (ValueAxis axis2 : clone.rangeAxes.values()) {
            if (axis2 != null) {
                axis2.setPlot(clone);
                axis2.addChangeListener(clone);
            }
        }
        clone.domainAxisLocations = new HashMap(this.domainAxisLocations);
        clone.rangeAxisLocations = new HashMap(this.rangeAxisLocations);
        clone.datasets = new HashMap(this.datasets);
        for (CategoryDataset dataset : clone.datasets.values()) {
            if (dataset != null) {
                dataset.addChangeListener(clone);
            }
        }
        clone.datasetToDomainAxesMap = new TreeMap();
        clone.datasetToDomainAxesMap.putAll(this.datasetToDomainAxesMap);
        clone.datasetToRangeAxesMap = new TreeMap();
        clone.datasetToRangeAxesMap.putAll(this.datasetToRangeAxesMap);
        clone.renderers = CloneUtils.cloneMapValues(this.renderers);
        for (CategoryItemRenderer renderer : clone.renderers.values()) {
            if (renderer != null) {
                renderer.setPlot(clone);
                renderer.addChangeListener(clone);
            }
        }
        if (this.fixedDomainAxisSpace != null) {
            clone.fixedDomainAxisSpace = (AxisSpace) ObjectUtilities.clone(this.fixedDomainAxisSpace);
        }
        if (this.fixedRangeAxisSpace != null) {
            clone.fixedRangeAxisSpace = (AxisSpace) ObjectUtilities.clone(this.fixedRangeAxisSpace);
        }
        clone.annotations = (List) ObjectUtilities.deepClone(this.annotations);
        clone.foregroundDomainMarkers = cloneMarkerMap(this.foregroundDomainMarkers);
        clone.backgroundDomainMarkers = cloneMarkerMap(this.backgroundDomainMarkers);
        clone.foregroundRangeMarkers = cloneMarkerMap(this.foregroundRangeMarkers);
        clone.backgroundRangeMarkers = cloneMarkerMap(this.backgroundRangeMarkers);
        if (this.fixedLegendItems != null) {
            clone.fixedLegendItems = (LegendItemCollection) this.fixedLegendItems.clone();
        }
        return clone;
    }

    private Map cloneMarkerMap(Map map) throws CloneNotSupportedException {
        Map clone = new HashMap();
        for (Object key : map.keySet()) {
            clone.put(key, ObjectUtilities.deepClone((List) map.get(key)));
        }
        return clone;
    }

    private void writeObject(ObjectOutputStream stream) throws IOException {
        stream.defaultWriteObject();
        SerialUtilities.writeStroke(this.domainGridlineStroke, stream);
        SerialUtilities.writePaint(this.domainGridlinePaint, stream);
        SerialUtilities.writeStroke(this.rangeGridlineStroke, stream);
        SerialUtilities.writePaint(this.rangeGridlinePaint, stream);
        SerialUtilities.writeStroke(this.rangeCrosshairStroke, stream);
        SerialUtilities.writePaint(this.rangeCrosshairPaint, stream);
        SerialUtilities.writeStroke(this.domainCrosshairStroke, stream);
        SerialUtilities.writePaint(this.domainCrosshairPaint, stream);
        SerialUtilities.writeStroke(this.rangeMinorGridlineStroke, stream);
        SerialUtilities.writePaint(this.rangeMinorGridlinePaint, stream);
        SerialUtilities.writeStroke(this.rangeZeroBaselineStroke, stream);
        SerialUtilities.writePaint(this.rangeZeroBaselinePaint, stream);
    }

    private void readObject(ObjectInputStream stream) throws IOException, ClassNotFoundException {
        stream.defaultReadObject();
        this.domainGridlineStroke = SerialUtilities.readStroke(stream);
        this.domainGridlinePaint = SerialUtilities.readPaint(stream);
        this.rangeGridlineStroke = SerialUtilities.readStroke(stream);
        this.rangeGridlinePaint = SerialUtilities.readPaint(stream);
        this.rangeCrosshairStroke = SerialUtilities.readStroke(stream);
        this.rangeCrosshairPaint = SerialUtilities.readPaint(stream);
        this.domainCrosshairStroke = SerialUtilities.readStroke(stream);
        this.domainCrosshairPaint = SerialUtilities.readPaint(stream);
        this.rangeMinorGridlineStroke = SerialUtilities.readStroke(stream);
        this.rangeMinorGridlinePaint = SerialUtilities.readPaint(stream);
        this.rangeZeroBaselineStroke = SerialUtilities.readStroke(stream);
        this.rangeZeroBaselinePaint = SerialUtilities.readPaint(stream);
        for (CategoryAxis xAxis : this.domainAxes.values()) {
            if (xAxis != null) {
                xAxis.setPlot(this);
                xAxis.addChangeListener(this);
            }
        }
        for (ValueAxis yAxis : this.rangeAxes.values()) {
            if (yAxis != null) {
                yAxis.setPlot(this);
                yAxis.addChangeListener(this);
            }
        }
        for (CategoryDataset dataset : this.datasets.values()) {
            if (dataset != null) {
                dataset.addChangeListener(this);
            }
        }
        for (CategoryItemRenderer renderer : this.renderers.values()) {
            if (renderer != null) {
                renderer.addChangeListener(this);
            }
        }
    }
}
