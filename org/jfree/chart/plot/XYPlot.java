package org.jfree.chart.plot;

import java.awt.AlphaComposite;
import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Composite;
import java.awt.Graphics2D;
import java.awt.Paint;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.awt.Shape;
import java.awt.Stroke;
import java.awt.geom.Line2D;
import java.awt.geom.Point2D;
import java.awt.geom.Point2D.Double;
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
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.ResourceBundle;
import java.util.Set;
import java.util.TreeMap;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.LegendItem;
import org.jfree.chart.LegendItemCollection;
import org.jfree.chart.annotations.XYAnnotation;
import org.jfree.chart.annotations.XYAnnotationBoundsInfo;
import org.jfree.chart.annotations.XYPointerAnnotation;
import org.jfree.chart.axis.Axis;
import org.jfree.chart.axis.AxisCollection;
import org.jfree.chart.axis.AxisLocation;
import org.jfree.chart.axis.AxisSpace;
import org.jfree.chart.axis.AxisState;
import org.jfree.chart.axis.TickType;
import org.jfree.chart.axis.ValueAxis;
import org.jfree.chart.axis.ValueTick;
import org.jfree.chart.event.AnnotationChangeEvent;
import org.jfree.chart.event.ChartChangeEventType;
import org.jfree.chart.event.PlotChangeEvent;
import org.jfree.chart.event.RendererChangeEvent;
import org.jfree.chart.event.RendererChangeListener;
import org.jfree.chart.renderer.RendererUtilities;
import org.jfree.chart.renderer.xy.AbstractXYItemRenderer;
import org.jfree.chart.renderer.xy.XYItemRenderer;
import org.jfree.chart.renderer.xy.XYItemRendererState;
import org.jfree.chart.util.CloneUtils;
import org.jfree.chart.util.ParamChecks;
import org.jfree.chart.util.ResourceBundleWrapper;
import org.jfree.chart.util.ShadowGenerator;
import org.jfree.data.Range;
import org.jfree.data.general.DatasetChangeEvent;
import org.jfree.data.general.DatasetUtilities;
import org.jfree.data.xy.XYDataset;
import org.jfree.io.SerialUtilities;
import org.jfree.ui.Layer;
import org.jfree.ui.RectangleEdge;
import org.jfree.ui.RectangleInsets;
import org.jfree.util.ObjectUtilities;
import org.jfree.util.PaintUtilities;
import org.jfree.util.PublicCloneable;

public class XYPlot extends Plot implements ValueAxisPlot, Pannable, Zoomable, RendererChangeListener, Cloneable, PublicCloneable, Serializable {
    public static final Paint DEFAULT_CROSSHAIR_PAINT;
    public static final Stroke DEFAULT_CROSSHAIR_STROKE;
    public static final boolean DEFAULT_CROSSHAIR_VISIBLE = false;
    public static final Paint DEFAULT_GRIDLINE_PAINT;
    public static final Stroke DEFAULT_GRIDLINE_STROKE;
    protected static ResourceBundle localizationResources = null;
    private static final long serialVersionUID = 7044148245716569264L;
    private List<XYAnnotation> annotations;
    private RectangleInsets axisOffset;
    private Map backgroundDomainMarkers;
    private Map backgroundRangeMarkers;
    private DatasetRenderingOrder datasetRenderingOrder;
    private Map<Integer, List<Integer>> datasetToDomainAxesMap;
    private Map<Integer, List<Integer>> datasetToRangeAxesMap;
    private Map<Integer, XYDataset> datasets;
    private Map<Integer, ValueAxis> domainAxes;
    private Map<Integer, AxisLocation> domainAxisLocations;
    private boolean domainCrosshairLockedOnData;
    private transient Paint domainCrosshairPaint;
    private transient Stroke domainCrosshairStroke;
    private double domainCrosshairValue;
    private boolean domainCrosshairVisible;
    private transient Paint domainGridlinePaint;
    private transient Stroke domainGridlineStroke;
    private boolean domainGridlinesVisible;
    private transient Paint domainMinorGridlinePaint;
    private transient Stroke domainMinorGridlineStroke;
    private boolean domainMinorGridlinesVisible;
    private boolean domainPannable;
    private transient Paint domainTickBandPaint;
    private transient Paint domainZeroBaselinePaint;
    private transient Stroke domainZeroBaselineStroke;
    private boolean domainZeroBaselineVisible;
    private AxisSpace fixedDomainAxisSpace;
    private LegendItemCollection fixedLegendItems;
    private AxisSpace fixedRangeAxisSpace;
    private Map foregroundDomainMarkers;
    private Map foregroundRangeMarkers;
    private PlotOrientation orientation;
    private transient Point2D quadrantOrigin;
    private transient Paint[] quadrantPaint;
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
    private transient Paint rangeTickBandPaint;
    private transient Paint rangeZeroBaselinePaint;
    private transient Stroke rangeZeroBaselineStroke;
    private boolean rangeZeroBaselineVisible;
    private Map<Integer, XYItemRenderer> renderers;
    private SeriesRenderingOrder seriesRenderingOrder;
    private ShadowGenerator shadowGenerator;
    private int weight;

    static {
        DEFAULT_GRIDLINE_STROKE = new BasicStroke(JFreeChart.DEFAULT_BACKGROUND_IMAGE_ALPHA, 0, 2, 0.0f, new float[]{Axis.DEFAULT_TICK_MARK_OUTSIDE_LENGTH, Axis.DEFAULT_TICK_MARK_OUTSIDE_LENGTH}, 0.0f);
        DEFAULT_GRIDLINE_PAINT = Color.lightGray;
        DEFAULT_CROSSHAIR_STROKE = DEFAULT_GRIDLINE_STROKE;
        DEFAULT_CROSSHAIR_PAINT = Color.blue;
        localizationResources = ResourceBundleWrapper.getBundle("org.jfree.chart.plot.LocalizationBundle");
    }

    public XYPlot() {
        this(null, null, null, null);
    }

    public XYPlot(XYDataset dataset, ValueAxis domainAxis, ValueAxis rangeAxis, XYItemRenderer renderer) {
        this.quadrantOrigin = new Double(0.0d, 0.0d);
        this.quadrantPaint = new Paint[]{null, null, null, null};
        this.domainCrosshairLockedOnData = true;
        this.rangeCrosshairLockedOnData = true;
        this.datasetRenderingOrder = DatasetRenderingOrder.REVERSE;
        this.seriesRenderingOrder = SeriesRenderingOrder.REVERSE;
        this.orientation = PlotOrientation.VERTICAL;
        this.weight = 1;
        this.axisOffset = RectangleInsets.ZERO_INSETS;
        this.domainAxes = new HashMap();
        this.domainAxisLocations = new HashMap();
        this.foregroundDomainMarkers = new HashMap();
        this.backgroundDomainMarkers = new HashMap();
        this.rangeAxes = new HashMap();
        this.rangeAxisLocations = new HashMap();
        this.foregroundRangeMarkers = new HashMap();
        this.backgroundRangeMarkers = new HashMap();
        this.datasets = new HashMap();
        this.renderers = new HashMap();
        this.datasetToDomainAxesMap = new TreeMap();
        this.datasetToRangeAxesMap = new TreeMap();
        this.annotations = new ArrayList();
        this.datasets.put(Integer.valueOf(0), dataset);
        if (dataset != null) {
            dataset.addChangeListener(this);
        }
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
        this.domainAxisLocations.put(Integer.valueOf(0), AxisLocation.BOTTOM_OR_LEFT);
        this.rangeAxes.put(Integer.valueOf(0), rangeAxis);
        mapDatasetToRangeAxis(0, 0);
        if (rangeAxis != null) {
            rangeAxis.setPlot(this);
            rangeAxis.addChangeListener(this);
        }
        this.rangeAxisLocations.put(Integer.valueOf(0), AxisLocation.BOTTOM_OR_LEFT);
        configureDomainAxes();
        configureRangeAxes();
        this.domainGridlinesVisible = true;
        this.domainGridlineStroke = DEFAULT_GRIDLINE_STROKE;
        this.domainGridlinePaint = DEFAULT_GRIDLINE_PAINT;
        this.domainMinorGridlinesVisible = DEFAULT_CROSSHAIR_VISIBLE;
        this.domainMinorGridlineStroke = DEFAULT_GRIDLINE_STROKE;
        this.domainMinorGridlinePaint = Color.white;
        this.domainZeroBaselineVisible = DEFAULT_CROSSHAIR_VISIBLE;
        this.domainZeroBaselinePaint = Color.black;
        this.domainZeroBaselineStroke = new BasicStroke(JFreeChart.DEFAULT_BACKGROUND_IMAGE_ALPHA);
        this.rangeGridlinesVisible = true;
        this.rangeGridlineStroke = DEFAULT_GRIDLINE_STROKE;
        this.rangeGridlinePaint = DEFAULT_GRIDLINE_PAINT;
        this.rangeMinorGridlinesVisible = DEFAULT_CROSSHAIR_VISIBLE;
        this.rangeMinorGridlineStroke = DEFAULT_GRIDLINE_STROKE;
        this.rangeMinorGridlinePaint = Color.white;
        this.rangeZeroBaselineVisible = DEFAULT_CROSSHAIR_VISIBLE;
        this.rangeZeroBaselinePaint = Color.black;
        this.rangeZeroBaselineStroke = new BasicStroke(JFreeChart.DEFAULT_BACKGROUND_IMAGE_ALPHA);
        this.domainCrosshairVisible = DEFAULT_CROSSHAIR_VISIBLE;
        this.domainCrosshairValue = 0.0d;
        this.domainCrosshairStroke = DEFAULT_CROSSHAIR_STROKE;
        this.domainCrosshairPaint = DEFAULT_CROSSHAIR_PAINT;
        this.rangeCrosshairVisible = DEFAULT_CROSSHAIR_VISIBLE;
        this.rangeCrosshairValue = 0.0d;
        this.rangeCrosshairStroke = DEFAULT_CROSSHAIR_STROKE;
        this.rangeCrosshairPaint = DEFAULT_CROSSHAIR_PAINT;
        this.shadowGenerator = null;
    }

    public String getPlotType() {
        return localizationResources.getString("XY_Plot");
    }

    public PlotOrientation getOrientation() {
        return this.orientation;
    }

    public void setOrientation(PlotOrientation orientation) {
        ParamChecks.nullNotPermitted(orientation, "orientation");
        if (orientation != this.orientation) {
            this.orientation = orientation;
            fireChangeEvent();
        }
    }

    public RectangleInsets getAxisOffset() {
        return this.axisOffset;
    }

    public void setAxisOffset(RectangleInsets offset) {
        ParamChecks.nullNotPermitted(offset, "offset");
        this.axisOffset = offset;
        fireChangeEvent();
    }

    public ValueAxis getDomainAxis() {
        return getDomainAxis(0);
    }

    public ValueAxis getDomainAxis(int index) {
        ValueAxis result = (ValueAxis) this.domainAxes.get(Integer.valueOf(index));
        if (result != null) {
            return result;
        }
        Plot parent = getParent();
        if (parent instanceof XYPlot) {
            return ((XYPlot) parent).getDomainAxis(index);
        }
        return result;
    }

    public void setDomainAxis(ValueAxis axis) {
        setDomainAxis(0, axis);
    }

    public void setDomainAxis(int index, ValueAxis axis) {
        setDomainAxis(index, axis, true);
    }

    public void setDomainAxis(int index, ValueAxis axis, boolean notify) {
        ValueAxis existing = getDomainAxis(index);
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

    public void setDomainAxes(ValueAxis[] axes) {
        for (int i = 0; i < axes.length; i++) {
            setDomainAxis(i, axes[i], DEFAULT_CROSSHAIR_VISIBLE);
        }
        fireChangeEvent();
    }

    public AxisLocation getDomainAxisLocation() {
        return (AxisLocation) this.domainAxisLocations.get(Integer.valueOf(0));
    }

    public void setDomainAxisLocation(AxisLocation location) {
        setDomainAxisLocation(0, location, true);
    }

    public void setDomainAxisLocation(AxisLocation location, boolean notify) {
        setDomainAxisLocation(0, location, notify);
    }

    public RectangleEdge getDomainAxisEdge() {
        return Plot.resolveDomainAxisLocation(getDomainAxisLocation(), this.orientation);
    }

    public int getDomainAxisCount() {
        return this.domainAxes.size();
    }

    public void clearDomainAxes() {
        for (ValueAxis axis : this.domainAxes.values()) {
            if (axis != null) {
                axis.removeChangeListener(this);
            }
        }
        this.domainAxes.clear();
        fireChangeEvent();
    }

    public void configureDomainAxes() {
        for (ValueAxis axis : this.domainAxes.values()) {
            if (axis != null) {
                axis.configure();
            }
        }
    }

    public AxisLocation getDomainAxisLocation(int index) {
        AxisLocation result = (AxisLocation) this.domainAxisLocations.get(Integer.valueOf(index));
        if (result == null) {
            return AxisLocation.getOpposite(getDomainAxisLocation());
        }
        return result;
    }

    public void setDomainAxisLocation(int index, AxisLocation location) {
        setDomainAxisLocation(index, location, true);
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

    public RectangleEdge getDomainAxisEdge(int index) {
        return Plot.resolveDomainAxisLocation(getDomainAxisLocation(index), this.orientation);
    }

    public ValueAxis getRangeAxis() {
        return getRangeAxis(0);
    }

    public void setRangeAxis(ValueAxis axis) {
        if (axis != null) {
            axis.setPlot(this);
        }
        ValueAxis existing = getRangeAxis();
        if (existing != null) {
            existing.removeChangeListener(this);
        }
        this.rangeAxes.put(Integer.valueOf(0), axis);
        if (axis != null) {
            axis.configure();
            axis.addChangeListener(this);
        }
        fireChangeEvent();
    }

    public AxisLocation getRangeAxisLocation() {
        return (AxisLocation) this.rangeAxisLocations.get(Integer.valueOf(0));
    }

    public void setRangeAxisLocation(AxisLocation location) {
        setRangeAxisLocation(0, location, true);
    }

    public void setRangeAxisLocation(AxisLocation location, boolean notify) {
        setRangeAxisLocation(0, location, notify);
    }

    public RectangleEdge getRangeAxisEdge() {
        return Plot.resolveRangeAxisLocation(getRangeAxisLocation(), this.orientation);
    }

    public ValueAxis getRangeAxis(int index) {
        ValueAxis result = (ValueAxis) this.rangeAxes.get(Integer.valueOf(index));
        if (result != null) {
            return result;
        }
        Plot parent = getParent();
        if (parent instanceof XYPlot) {
            return ((XYPlot) parent).getRangeAxis(index);
        }
        return result;
    }

    public void setRangeAxis(int index, ValueAxis axis) {
        setRangeAxis(index, axis, true);
    }

    public void setRangeAxis(int index, ValueAxis axis, boolean notify) {
        ValueAxis existing = getRangeAxis(index);
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
            setRangeAxis(i, axes[i], DEFAULT_CROSSHAIR_VISIBLE);
        }
        fireChangeEvent();
    }

    public int getRangeAxisCount() {
        return this.rangeAxes.size();
    }

    public void clearRangeAxes() {
        for (ValueAxis axis : this.rangeAxes.values()) {
            if (axis != null) {
                axis.removeChangeListener(this);
            }
        }
        this.rangeAxes.clear();
        fireChangeEvent();
    }

    public void configureRangeAxes() {
        for (ValueAxis axis : this.rangeAxes.values()) {
            if (axis != null) {
                axis.configure();
            }
        }
    }

    public AxisLocation getRangeAxisLocation(int index) {
        AxisLocation result = (AxisLocation) this.rangeAxisLocations.get(Integer.valueOf(index));
        if (result == null) {
            return AxisLocation.getOpposite(getRangeAxisLocation());
        }
        return result;
    }

    public void setRangeAxisLocation(int index, AxisLocation location) {
        setRangeAxisLocation(index, location, true);
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

    public RectangleEdge getRangeAxisEdge(int index) {
        return Plot.resolveRangeAxisLocation(getRangeAxisLocation(index), this.orientation);
    }

    public XYDataset getDataset() {
        return getDataset(0);
    }

    public XYDataset getDataset(int index) {
        return (XYDataset) this.datasets.get(Integer.valueOf(index));
    }

    public void setDataset(XYDataset dataset) {
        setDataset(0, dataset);
    }

    public void setDataset(int index, XYDataset dataset) {
        XYDataset existing = getDataset(index);
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

    public int indexOf(XYDataset dataset) {
        for (Entry<Integer, XYDataset> entry : this.datasets.entrySet()) {
            if (dataset == entry.getValue()) {
                return ((Integer) entry.getKey()).intValue();
            }
        }
        return -1;
    }

    public void mapDatasetToDomainAxis(int index, int axisIndex) {
        List axisIndices = new ArrayList(1);
        axisIndices.add(new Integer(axisIndex));
        mapDatasetToDomainAxes(index, axisIndices);
    }

    public void mapDatasetToDomainAxes(int index, List axisIndices) {
        ParamChecks.requireNonNegative(index, "index");
        checkAxisIndices(axisIndices);
        this.datasetToDomainAxesMap.put(new Integer(index), new ArrayList(axisIndices));
        datasetChanged(new DatasetChangeEvent(this, getDataset(index)));
    }

    public void mapDatasetToRangeAxis(int index, int axisIndex) {
        List axisIndices = new ArrayList(1);
        axisIndices.add(new Integer(axisIndex));
        mapDatasetToRangeAxes(index, axisIndices);
    }

    public void mapDatasetToRangeAxes(int index, List axisIndices) {
        ParamChecks.requireNonNegative(index, "index");
        checkAxisIndices(axisIndices);
        this.datasetToRangeAxesMap.put(new Integer(index), new ArrayList(axisIndices));
        datasetChanged(new DatasetChangeEvent(this, getDataset(index)));
    }

    private void checkAxisIndices(List<Integer> indices) {
        if (indices != null) {
            if (indices.size() == 0) {
                throw new IllegalArgumentException("Empty list not permitted.");
            }
            Set<Integer> set = new HashSet();
            for (Integer item : indices) {
                if (set.contains(item)) {
                    throw new IllegalArgumentException("Indices must be unique.");
                }
                set.add(item);
            }
        }
    }

    public int getRendererCount() {
        return this.renderers.size();
    }

    public XYItemRenderer getRenderer() {
        return getRenderer(0);
    }

    public XYItemRenderer getRenderer(int index) {
        return (XYItemRenderer) this.renderers.get(Integer.valueOf(index));
    }

    public void setRenderer(XYItemRenderer renderer) {
        setRenderer(0, renderer);
    }

    public void setRenderer(int index, XYItemRenderer renderer) {
        setRenderer(index, renderer, true);
    }

    public void setRenderer(int index, XYItemRenderer renderer, boolean notify) {
        XYItemRenderer existing = getRenderer(index);
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

    public void setRenderers(XYItemRenderer[] renderers) {
        for (int i = 0; i < renderers.length; i++) {
            setRenderer(i, renderers[i], DEFAULT_CROSSHAIR_VISIBLE);
        }
        fireChangeEvent();
    }

    public DatasetRenderingOrder getDatasetRenderingOrder() {
        return this.datasetRenderingOrder;
    }

    public void setDatasetRenderingOrder(DatasetRenderingOrder order) {
        ParamChecks.nullNotPermitted(order, "order");
        this.datasetRenderingOrder = order;
        fireChangeEvent();
    }

    public SeriesRenderingOrder getSeriesRenderingOrder() {
        return this.seriesRenderingOrder;
    }

    public void setSeriesRenderingOrder(SeriesRenderingOrder order) {
        ParamChecks.nullNotPermitted(order, "order");
        this.seriesRenderingOrder = order;
        fireChangeEvent();
    }

    public int getIndexOf(XYItemRenderer renderer) {
        for (Entry<Integer, XYItemRenderer> entry : this.renderers.entrySet()) {
            if (entry.getValue() == renderer) {
                return ((Integer) entry.getKey()).intValue();
            }
        }
        return -1;
    }

    public XYItemRenderer getRendererForDataset(XYDataset dataset) {
        int datasetIndex = indexOf(dataset);
        if (datasetIndex < 0) {
            return null;
        }
        XYItemRenderer result = (XYItemRenderer) this.renderers.get(Integer.valueOf(datasetIndex));
        if (result == null) {
            return getRenderer();
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

    public boolean isDomainGridlinesVisible() {
        return this.domainGridlinesVisible;
    }

    public void setDomainGridlinesVisible(boolean visible) {
        if (this.domainGridlinesVisible != visible) {
            this.domainGridlinesVisible = visible;
            fireChangeEvent();
        }
    }

    public boolean isDomainMinorGridlinesVisible() {
        return this.domainMinorGridlinesVisible;
    }

    public void setDomainMinorGridlinesVisible(boolean visible) {
        if (this.domainMinorGridlinesVisible != visible) {
            this.domainMinorGridlinesVisible = visible;
            fireChangeEvent();
        }
    }

    public Stroke getDomainGridlineStroke() {
        return this.domainGridlineStroke;
    }

    public void setDomainGridlineStroke(Stroke stroke) {
        ParamChecks.nullNotPermitted(stroke, "stroke");
        this.domainGridlineStroke = stroke;
        fireChangeEvent();
    }

    public Stroke getDomainMinorGridlineStroke() {
        return this.domainMinorGridlineStroke;
    }

    public void setDomainMinorGridlineStroke(Stroke stroke) {
        ParamChecks.nullNotPermitted(stroke, "stroke");
        this.domainMinorGridlineStroke = stroke;
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

    public Paint getDomainMinorGridlinePaint() {
        return this.domainMinorGridlinePaint;
    }

    public void setDomainMinorGridlinePaint(Paint paint) {
        ParamChecks.nullNotPermitted(paint, "paint");
        this.domainMinorGridlinePaint = paint;
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

    public boolean isDomainZeroBaselineVisible() {
        return this.domainZeroBaselineVisible;
    }

    public void setDomainZeroBaselineVisible(boolean visible) {
        this.domainZeroBaselineVisible = visible;
        fireChangeEvent();
    }

    public Stroke getDomainZeroBaselineStroke() {
        return this.domainZeroBaselineStroke;
    }

    public void setDomainZeroBaselineStroke(Stroke stroke) {
        ParamChecks.nullNotPermitted(stroke, "stroke");
        this.domainZeroBaselineStroke = stroke;
        fireChangeEvent();
    }

    public Paint getDomainZeroBaselinePaint() {
        return this.domainZeroBaselinePaint;
    }

    public void setDomainZeroBaselinePaint(Paint paint) {
        ParamChecks.nullNotPermitted(paint, "paint");
        this.domainZeroBaselinePaint = paint;
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

    public Paint getDomainTickBandPaint() {
        return this.domainTickBandPaint;
    }

    public void setDomainTickBandPaint(Paint paint) {
        this.domainTickBandPaint = paint;
        fireChangeEvent();
    }

    public Paint getRangeTickBandPaint() {
        return this.rangeTickBandPaint;
    }

    public void setRangeTickBandPaint(Paint paint) {
        this.rangeTickBandPaint = paint;
        fireChangeEvent();
    }

    public Point2D getQuadrantOrigin() {
        return this.quadrantOrigin;
    }

    public void setQuadrantOrigin(Point2D origin) {
        ParamChecks.nullNotPermitted(origin, "origin");
        this.quadrantOrigin = origin;
        fireChangeEvent();
    }

    public Paint getQuadrantPaint(int index) {
        if (index >= 0 && index <= 3) {
            return this.quadrantPaint[index];
        }
        throw new IllegalArgumentException("The index value (" + index + ") should be in the range 0 to 3.");
    }

    public void setQuadrantPaint(int index, Paint paint) {
        if (index < 0 || index > 3) {
            throw new IllegalArgumentException("The index value (" + index + ") should be in the range 0 to 3.");
        }
        this.quadrantPaint[index] = paint;
        fireChangeEvent();
    }

    public void addDomainMarker(Marker marker) {
        addDomainMarker(marker, Layer.FOREGROUND);
    }

    public void addDomainMarker(Marker marker, Layer layer) {
        addDomainMarker(0, marker, layer);
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
        if (this.foregroundRangeMarkers != null) {
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

    public void addDomainMarker(int index, Marker marker, Layer layer) {
        addDomainMarker(index, marker, layer, true);
    }

    public void addDomainMarker(int index, Marker marker, Layer layer, boolean notify) {
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

    public boolean removeDomainMarker(Marker marker) {
        return removeDomainMarker(marker, Layer.FOREGROUND);
    }

    public boolean removeDomainMarker(Marker marker, Layer layer) {
        return removeDomainMarker(0, marker, layer);
    }

    public boolean removeDomainMarker(int index, Marker marker, Layer layer) {
        return removeDomainMarker(index, marker, layer, true);
    }

    public boolean removeDomainMarker(int index, Marker marker, Layer layer, boolean notify) {
        ArrayList markers;
        if (layer == Layer.FOREGROUND) {
            markers = (ArrayList) this.foregroundDomainMarkers.get(new Integer(index));
        } else {
            markers = (ArrayList) this.backgroundDomainMarkers.get(new Integer(index));
        }
        if (markers == null) {
            return DEFAULT_CROSSHAIR_VISIBLE;
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

    public void addRangeMarker(int index, Marker marker, Layer layer) {
        addRangeMarker(index, marker, layer, true);
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
        return removeRangeMarker(index, marker, layer, true);
    }

    public boolean removeRangeMarker(int index, Marker marker, Layer layer, boolean notify) {
        List markers;
        ParamChecks.nullNotPermitted(marker, "marker");
        ParamChecks.nullNotPermitted(layer, "layer");
        if (layer == Layer.FOREGROUND) {
            markers = (List) this.foregroundRangeMarkers.get(new Integer(index));
        } else {
            markers = (List) this.backgroundRangeMarkers.get(new Integer(index));
        }
        if (markers == null) {
            return DEFAULT_CROSSHAIR_VISIBLE;
        }
        boolean removed = markers.remove(marker);
        if (!removed || !notify) {
            return removed;
        }
        fireChangeEvent();
        return removed;
    }

    public void addAnnotation(XYAnnotation annotation) {
        addAnnotation(annotation, true);
    }

    public void addAnnotation(XYAnnotation annotation, boolean notify) {
        ParamChecks.nullNotPermitted(annotation, "annotation");
        this.annotations.add(annotation);
        annotation.addChangeListener(this);
        if (notify) {
            fireChangeEvent();
        }
    }

    public boolean removeAnnotation(XYAnnotation annotation) {
        return removeAnnotation(annotation, true);
    }

    public boolean removeAnnotation(XYAnnotation annotation, boolean notify) {
        ParamChecks.nullNotPermitted(annotation, "annotation");
        boolean removed = this.annotations.remove(annotation);
        annotation.removeChangeListener(this);
        if (removed && notify) {
            fireChangeEvent();
        }
        return removed;
    }

    public List getAnnotations() {
        return new ArrayList(this.annotations);
    }

    public void clearAnnotations() {
        for (XYAnnotation annotation : this.annotations) {
            annotation.removeChangeListener(this);
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

    protected AxisSpace calculateAxisSpace(Graphics2D g2, Rectangle2D plotArea) {
        AxisSpace space = calculateRangeAxisSpace(g2, plotArea, new AxisSpace());
        return calculateDomainAxisSpace(g2, space.shrink(plotArea, null), space);
    }

    protected AxisSpace calculateDomainAxisSpace(Graphics2D g2, Rectangle2D plotArea, AxisSpace space) {
        if (space == null) {
            space = new AxisSpace();
        }
        if (this.fixedDomainAxisSpace == null) {
            for (ValueAxis axis : this.domainAxes.values()) {
                if (axis != null) {
                    space = axis.reserveSpace(g2, this, plotArea, getDomainAxisEdge(findDomainAxisIndex(axis)), space);
                }
            }
        } else if (this.orientation == PlotOrientation.HORIZONTAL) {
            space.ensureAtLeast(this.fixedDomainAxisSpace.getLeft(), RectangleEdge.LEFT);
            space.ensureAtLeast(this.fixedDomainAxisSpace.getRight(), RectangleEdge.RIGHT);
        } else if (this.orientation == PlotOrientation.VERTICAL) {
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
            for (ValueAxis axis : this.rangeAxes.values()) {
                if (axis != null) {
                    space = axis.reserveSpace(g2, this, plotArea, getRangeAxisEdge(findRangeAxisIndex(axis)), space);
                }
            }
        } else if (this.orientation == PlotOrientation.HORIZONTAL) {
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

    public void draw(Graphics2D g2, Rectangle2D area, Point2D anchor, PlotState parentState, PlotRenderingInfo info) {
        boolean b1 = area.getWidth() <= XYPointerAnnotation.DEFAULT_TIP_RADIUS ? true : DEFAULT_CROSSHAIR_VISIBLE;
        boolean b2 = area.getHeight() <= XYPointerAnnotation.DEFAULT_TIP_RADIUS ? true : DEFAULT_CROSSHAIR_VISIBLE;
        if (!b1 && !b2) {
            if (info != null) {
                info.setPlotArea(area);
            }
            getInsets().trim(area);
            Rectangle2D dataArea = calculateAxisSpace(g2, area).shrink(area, null);
            this.axisOffset.trim(dataArea);
            dataArea = integerise(dataArea);
            if (!dataArea.isEmpty()) {
                int i;
                XYItemRenderer renderer;
                int datasetIndex;
                createAndAddEntity((Rectangle2D) dataArea.clone(), info, null, null);
                if (info != null) {
                    info.setDataArea(dataArea);
                }
                drawBackground(g2, dataArea);
                Map axisStateMap = drawAxes(g2, area, dataArea, info);
                PlotOrientation orient = getOrientation();
                if (!(anchor == null || dataArea.contains(anchor))) {
                    anchor = null;
                }
                CrosshairState crosshairState = new CrosshairState();
                crosshairState.setCrosshairDistance(Double.POSITIVE_INFINITY);
                crosshairState.setAnchor(anchor);
                crosshairState.setAnchorX(Double.NaN);
                crosshairState.setAnchorY(Double.NaN);
                if (anchor != null) {
                    ValueAxis domainAxis = getDomainAxis();
                    if (domainAxis != null) {
                        double x;
                        if (orient == PlotOrientation.VERTICAL) {
                            x = domainAxis.java2DToValue(anchor.getX(), dataArea, getDomainAxisEdge());
                        } else {
                            x = domainAxis.java2DToValue(anchor.getY(), dataArea, getDomainAxisEdge());
                        }
                        crosshairState.setAnchorX(x);
                    }
                    ValueAxis rangeAxis = getRangeAxis();
                    if (rangeAxis != null) {
                        double y;
                        if (orient == PlotOrientation.VERTICAL) {
                            y = rangeAxis.java2DToValue(anchor.getY(), dataArea, getRangeAxisEdge());
                        } else {
                            y = rangeAxis.java2DToValue(anchor.getX(), dataArea, getRangeAxisEdge());
                        }
                        crosshairState.setAnchorY(y);
                    }
                }
                crosshairState.setCrosshairX(getDomainCrosshairValue());
                crosshairState.setCrosshairY(getRangeCrosshairValue());
                Shape originalClip = g2.getClip();
                Composite originalComposite = g2.getComposite();
                g2.clip(dataArea);
                g2.setComposite(AlphaComposite.getInstance(3, getForegroundAlpha()));
                AxisState domainAxisState = (AxisState) axisStateMap.get(getDomainAxis());
                if (domainAxisState == null && parentState != null) {
                    domainAxisState = (AxisState) parentState.getSharedAxisStates().get(getDomainAxis());
                }
                AxisState rangeAxisState = (AxisState) axisStateMap.get(getRangeAxis());
                if (rangeAxisState == null && parentState != null) {
                    rangeAxisState = (AxisState) parentState.getSharedAxisStates().get(getRangeAxis());
                }
                if (domainAxisState != null) {
                    drawDomainTickBands(g2, dataArea, domainAxisState.getTicks());
                }
                if (rangeAxisState != null) {
                    drawRangeTickBands(g2, dataArea, rangeAxisState.getTicks());
                }
                if (domainAxisState != null) {
                    drawDomainGridlines(g2, dataArea, domainAxisState.getTicks());
                    drawZeroDomainBaseline(g2, dataArea);
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
                for (XYDataset indexOf : this.datasets.values()) {
                    drawDomainMarkers(g2, dataArea, indexOf(indexOf), Layer.BACKGROUND);
                }
                for (XYDataset indexOf2 : this.datasets.values()) {
                    drawRangeMarkers(g2, dataArea, indexOf(indexOf2), Layer.BACKGROUND);
                }
                boolean foundData = DEFAULT_CROSSHAIR_VISIBLE;
                DatasetRenderingOrder order = getDatasetRenderingOrder();
                List<Integer> rendererIndices = getRendererIndices(order);
                List<Integer> datasetIndices = getDatasetIndices(order);
                for (Integer intValue : rendererIndices) {
                    i = intValue.intValue();
                    renderer = getRenderer(i);
                    if (renderer != null) {
                        renderer.drawAnnotations(g2, dataArea, getDomainAxisForDataset(i), getRangeAxisForDataset(i), Layer.BACKGROUND, info);
                    }
                }
                for (Integer intValue2 : datasetIndices) {
                    datasetIndex = intValue2.intValue();
                    XYDataset dataset = getDataset(datasetIndex);
                    foundData = (render(g2, dataArea, datasetIndex, info, crosshairState) || foundData) ? true : DEFAULT_CROSSHAIR_VISIBLE;
                }
                for (Integer intValue22 : rendererIndices) {
                    i = intValue22.intValue();
                    renderer = getRenderer(i);
                    if (renderer != null) {
                        renderer.drawAnnotations(g2, dataArea, getDomainAxisForDataset(i), getRangeAxisForDataset(i), Layer.FOREGROUND, info);
                    }
                }
                datasetIndex = crosshairState.getDatasetIndex();
                ValueAxis xAxis = getDomainAxisForDataset(datasetIndex);
                RectangleEdge xAxisEdge = getDomainAxisEdge(getDomainAxisIndex(xAxis));
                if (!(this.domainCrosshairLockedOnData || anchor == null)) {
                    double xx;
                    if (orient == PlotOrientation.VERTICAL) {
                        xx = xAxis.java2DToValue(anchor.getX(), dataArea, xAxisEdge);
                    } else {
                        xx = xAxis.java2DToValue(anchor.getY(), dataArea, xAxisEdge);
                    }
                    crosshairState.setCrosshairX(xx);
                }
                setDomainCrosshairValue(crosshairState.getCrosshairX(), DEFAULT_CROSSHAIR_VISIBLE);
                if (isDomainCrosshairVisible()) {
                    drawDomainCrosshair(g2, dataArea, orient, getDomainCrosshairValue(), xAxis, getDomainCrosshairStroke(), getDomainCrosshairPaint());
                }
                ValueAxis yAxis = getRangeAxisForDataset(datasetIndex);
                RectangleEdge yAxisEdge = getRangeAxisEdge(getRangeAxisIndex(yAxis));
                if (!(this.rangeCrosshairLockedOnData || anchor == null)) {
                    double yy;
                    if (orient == PlotOrientation.VERTICAL) {
                        yy = yAxis.java2DToValue(anchor.getY(), dataArea, yAxisEdge);
                    } else {
                        yy = yAxis.java2DToValue(anchor.getX(), dataArea, yAxisEdge);
                    }
                    crosshairState.setCrosshairY(yy);
                }
                setRangeCrosshairValue(crosshairState.getCrosshairY(), DEFAULT_CROSSHAIR_VISIBLE);
                if (isRangeCrosshairVisible()) {
                    drawRangeCrosshair(g2, dataArea, orient, getRangeCrosshairValue(), yAxis, getRangeCrosshairStroke(), getRangeCrosshairPaint());
                }
                if (!foundData) {
                    drawNoDataMessage(g2, dataArea);
                }
                for (Integer intValue222 : rendererIndices) {
                    drawDomainMarkers(g2, dataArea, intValue222.intValue(), Layer.FOREGROUND);
                }
                for (Integer intValue2222 : rendererIndices) {
                    drawRangeMarkers(g2, dataArea, intValue2222.intValue(), Layer.FOREGROUND);
                }
                drawAnnotations(g2, dataArea, info);
                if (!(this.shadowGenerator == null || suppressShadow)) {
                    g2 = savedG2;
                    g2.drawImage(this.shadowGenerator.createDropShadow(dataImage), ((int) dataArea.getX()) + this.shadowGenerator.calculateOffsetX(), ((int) dataArea.getY()) + this.shadowGenerator.calculateOffsetY(), null);
                    g2.drawImage(dataImage, (int) dataArea.getX(), (int) dataArea.getY(), null);
                }
                g2.setClip(originalClip);
                g2.setComposite(originalComposite);
                drawOutline(g2, dataArea);
            }
        }
    }

    private List<Integer> getDatasetIndices(DatasetRenderingOrder order) {
        List<Integer> result = new ArrayList();
        for (Entry<Integer, XYDataset> entry : this.datasets.entrySet()) {
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
        for (Entry<Integer, XYItemRenderer> entry : this.renderers.entrySet()) {
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
        drawQuadrants(g2, area);
        drawBackgroundImage(g2, area);
    }

    protected void drawQuadrants(Graphics2D g2, Rectangle2D area) {
        boolean somethingToDraw = DEFAULT_CROSSHAIR_VISIBLE;
        ValueAxis xAxis = getDomainAxis();
        if (xAxis != null) {
            double x = xAxis.getRange().constrain(this.quadrantOrigin.getX());
            double xx = xAxis.valueToJava2D(x, area, getDomainAxisEdge());
            ValueAxis yAxis = getRangeAxis();
            if (yAxis != null) {
                double y = yAxis.getRange().constrain(this.quadrantOrigin.getY());
                double yy = yAxis.valueToJava2D(y, area, getRangeAxisEdge());
                double xmin = xAxis.getLowerBound();
                double xxmin = xAxis.valueToJava2D(xmin, area, getDomainAxisEdge());
                double xmax = xAxis.getUpperBound();
                double xxmax = xAxis.valueToJava2D(xmax, area, getDomainAxisEdge());
                double ymin = yAxis.getLowerBound();
                double yymin = yAxis.valueToJava2D(ymin, area, getRangeAxisEdge());
                double ymax = yAxis.getUpperBound();
                double yymax = yAxis.valueToJava2D(ymax, area, getRangeAxisEdge());
                Rectangle2D[] r = new Rectangle2D[]{null, null, null, null};
                if (this.quadrantPaint[0] != null && x > xmin && y < ymax) {
                    if (this.orientation == PlotOrientation.HORIZONTAL) {
                        r[0] = new Rectangle2D.Double(Math.min(yymax, yy), Math.min(xxmin, xx), Math.abs(yy - yymax), Math.abs(xx - xxmin));
                    } else {
                        r[0] = new Rectangle2D.Double(Math.min(xxmin, xx), Math.min(yymax, yy), Math.abs(xx - xxmin), Math.abs(yy - yymax));
                    }
                    somethingToDraw = true;
                }
                if (this.quadrantPaint[1] != null && x < xmax && y < ymax) {
                    if (this.orientation == PlotOrientation.HORIZONTAL) {
                        r[1] = new Rectangle2D.Double(Math.min(yymax, yy), Math.min(xxmax, xx), Math.abs(yy - yymax), Math.abs(xx - xxmax));
                    } else {
                        r[1] = new Rectangle2D.Double(Math.min(xx, xxmax), Math.min(yymax, yy), Math.abs(xx - xxmax), Math.abs(yy - yymax));
                    }
                    somethingToDraw = true;
                }
                if (this.quadrantPaint[2] != null && x > xmin && y > ymin) {
                    if (this.orientation == PlotOrientation.HORIZONTAL) {
                        r[2] = new Rectangle2D.Double(Math.min(yymin, yy), Math.min(xxmin, xx), Math.abs(yy - yymin), Math.abs(xx - xxmin));
                    } else {
                        r[2] = new Rectangle2D.Double(Math.min(xxmin, xx), Math.min(yymin, yy), Math.abs(xx - xxmin), Math.abs(yy - yymin));
                    }
                    somethingToDraw = true;
                }
                if (this.quadrantPaint[3] != null && x < xmax && y > ymin) {
                    if (this.orientation == PlotOrientation.HORIZONTAL) {
                        r[3] = new Rectangle2D.Double(Math.min(yymin, yy), Math.min(xxmax, xx), Math.abs(yy - yymin), Math.abs(xx - xxmax));
                    } else {
                        r[3] = new Rectangle2D.Double(Math.min(xx, xxmax), Math.min(yymin, yy), Math.abs(xx - xxmax), Math.abs(yy - yymin));
                    }
                    somethingToDraw = true;
                }
                if (somethingToDraw) {
                    Composite originalComposite = g2.getComposite();
                    g2.setComposite(AlphaComposite.getInstance(3, getBackgroundAlpha()));
                    int i = 0;
                    while (i < 4) {
                        if (!(this.quadrantPaint[i] == null || r[i] == null)) {
                            g2.setPaint(this.quadrantPaint[i]);
                            g2.fill(r[i]);
                        }
                        i++;
                    }
                    g2.setComposite(originalComposite);
                }
            }
        }
    }

    public void drawDomainTickBands(Graphics2D g2, Rectangle2D dataArea, List ticks) {
        if (getDomainTickBandPaint() != null) {
            boolean fillBand = DEFAULT_CROSSHAIR_VISIBLE;
            ValueAxis xAxis = getDomainAxis();
            double previous = xAxis.getLowerBound();
            for (ValueTick tick : ticks) {
                double current = tick.getValue();
                if (fillBand) {
                    getRenderer().fillDomainGridBand(g2, this, xAxis, dataArea, previous, current);
                }
                previous = current;
                fillBand = !fillBand ? true : DEFAULT_CROSSHAIR_VISIBLE;
            }
            double end = xAxis.getUpperBound();
            if (fillBand) {
                getRenderer().fillDomainGridBand(g2, this, xAxis, dataArea, previous, end);
            }
        }
    }

    public void drawRangeTickBands(Graphics2D g2, Rectangle2D dataArea, List ticks) {
        if (getRangeTickBandPaint() != null) {
            boolean fillBand = DEFAULT_CROSSHAIR_VISIBLE;
            ValueAxis axis = getRangeAxis();
            double previous = axis.getLowerBound();
            for (ValueTick tick : ticks) {
                double current = tick.getValue();
                if (fillBand) {
                    getRenderer().fillRangeGridBand(g2, this, axis, dataArea, previous, current);
                }
                previous = current;
                fillBand = !fillBand ? true : DEFAULT_CROSSHAIR_VISIBLE;
            }
            double end = axis.getUpperBound();
            if (fillBand) {
                getRenderer().fillRangeGridBand(g2, this, axis, dataArea, previous, end);
            }
        }
    }

    protected Map<Axis, AxisState> drawAxes(Graphics2D g2, Rectangle2D plotArea, Rectangle2D dataArea, PlotRenderingInfo plotState) {
        AxisCollection axisCollection = new AxisCollection();
        for (ValueAxis axis : this.domainAxes.values()) {
            if (axis != null) {
                axisCollection.add(axis, getDomainAxisEdge(findDomainAxisIndex(axis)));
            }
        }
        for (ValueAxis axis2 : this.rangeAxes.values()) {
            if (axis2 != null) {
                axisCollection.add(axis2, getRangeAxisEdge(findRangeAxisIndex(axis2)));
            }
        }
        Map axisStateMap = new HashMap();
        double cursor = dataArea.getMinY() - this.axisOffset.calculateTopOutset(dataArea.getHeight());
        for (ValueAxis axis22 : axisCollection.getAxesAtTop()) {
            AxisState info = axis22.draw(g2, cursor, plotArea, dataArea, RectangleEdge.TOP, plotState);
            cursor = info.getCursor();
            axisStateMap.put(axis22, info);
        }
        cursor = dataArea.getMaxY() + this.axisOffset.calculateBottomOutset(dataArea.getHeight());
        for (ValueAxis axis222 : axisCollection.getAxesAtBottom()) {
            info = axis222.draw(g2, cursor, plotArea, dataArea, RectangleEdge.BOTTOM, plotState);
            cursor = info.getCursor();
            axisStateMap.put(axis222, info);
        }
        cursor = dataArea.getMinX() - this.axisOffset.calculateLeftOutset(dataArea.getWidth());
        for (ValueAxis axis2222 : axisCollection.getAxesAtLeft()) {
            info = axis2222.draw(g2, cursor, plotArea, dataArea, RectangleEdge.LEFT, plotState);
            cursor = info.getCursor();
            axisStateMap.put(axis2222, info);
        }
        cursor = dataArea.getMaxX() + this.axisOffset.calculateRightOutset(dataArea.getWidth());
        for (ValueAxis axis22222 : axisCollection.getAxesAtRight()) {
            info = axis22222.draw(g2, cursor, plotArea, dataArea, RectangleEdge.RIGHT, plotState);
            cursor = info.getCursor();
            axisStateMap.put(axis22222, info);
        }
        return axisStateMap;
    }

    public boolean render(Graphics2D g2, Rectangle2D dataArea, int index, PlotRenderingInfo info, CrosshairState crosshairState) {
        boolean foundData = DEFAULT_CROSSHAIR_VISIBLE;
        XYDataset dataset = getDataset(index);
        if (!DatasetUtilities.isEmptyOrNull(dataset)) {
            foundData = true;
            ValueAxis xAxis = getDomainAxisForDataset(index);
            ValueAxis yAxis = getRangeAxisForDataset(index);
            if (xAxis == null || yAxis == null) {
                return 1;
            }
            XYItemRenderer renderer = getRenderer(index);
            if (renderer == null) {
                renderer = getRenderer();
                if (renderer == null) {
                    return 1;
                }
            }
            XYItemRendererState state = renderer.initialise(g2, dataArea, this, dataset, info);
            int passCount = renderer.getPassCount();
            int pass;
            int series;
            int firstItem;
            int[] itemBounds;
            int lastItem;
            int item;
            if (getSeriesRenderingOrder() == SeriesRenderingOrder.REVERSE) {
                for (pass = 0; pass < passCount; pass++) {
                    for (series = dataset.getSeriesCount() - 1; series >= 0; series--) {
                        firstItem = 0;
                        int lastItem2 = dataset.getItemCount(series) - 1;
                        if (lastItem2 == -1) {
                        } else {
                            if (state.getProcessVisibleItemsOnly()) {
                                itemBounds = RendererUtilities.findLiveItems(dataset, series, xAxis.getLowerBound(), xAxis.getUpperBound());
                                firstItem = Math.max(itemBounds[0] - 1, 0);
                                lastItem = Math.min(itemBounds[1] + 1, lastItem2);
                            } else {
                                lastItem = lastItem2;
                            }
                            state.startSeriesPass(dataset, series, firstItem, lastItem, pass, passCount);
                            for (item = firstItem; item <= lastItem; item++) {
                                renderer.drawItem(g2, state, dataArea, info, this, xAxis, yAxis, dataset, series, item, crosshairState, pass);
                            }
                            state.endSeriesPass(dataset, series, firstItem, lastItem, pass, passCount);
                        }
                    }
                }
            } else {
                for (pass = 0; pass < passCount; pass++) {
                    int seriesCount = dataset.getSeriesCount();
                    for (series = 0; series < seriesCount; series++) {
                        firstItem = 0;
                        lastItem = dataset.getItemCount(series) - 1;
                        if (state.getProcessVisibleItemsOnly()) {
                            itemBounds = RendererUtilities.findLiveItems(dataset, series, xAxis.getLowerBound(), xAxis.getUpperBound());
                            firstItem = Math.max(itemBounds[0] - 1, 0);
                            lastItem = Math.min(itemBounds[1] + 1, lastItem);
                        }
                        state.startSeriesPass(dataset, series, firstItem, lastItem, pass, passCount);
                        for (item = firstItem; item <= lastItem; item++) {
                            renderer.drawItem(g2, state, dataArea, info, this, xAxis, yAxis, dataset, series, item, crosshairState, pass);
                        }
                        state.endSeriesPass(dataset, series, firstItem, lastItem, pass, passCount);
                    }
                }
            }
        }
        return foundData;
    }

    public ValueAxis getDomainAxisForDataset(int index) {
        ParamChecks.requireNonNegative(index, "index");
        List axisIndices = (List) this.datasetToDomainAxesMap.get(new Integer(index));
        if (axisIndices != null) {
            return getDomainAxis(((Integer) axisIndices.get(0)).intValue());
        }
        return getDomainAxis(0);
    }

    public ValueAxis getRangeAxisForDataset(int index) {
        ParamChecks.requireNonNegative(index, "index");
        List axisIndices = (List) this.datasetToRangeAxesMap.get(new Integer(index));
        if (axisIndices != null) {
            return getRangeAxis(((Integer) axisIndices.get(0)).intValue());
        }
        return getRangeAxis(0);
    }

    protected void drawDomainGridlines(Graphics2D g2, Rectangle2D dataArea, List ticks) {
        if (getRenderer() != null) {
            if (isDomainGridlinesVisible() || isDomainMinorGridlinesVisible()) {
                Stroke gridStroke = null;
                Paint gridPaint = null;
                for (ValueTick tick : ticks) {
                    boolean paintLine = DEFAULT_CROSSHAIR_VISIBLE;
                    if (tick.getTickType() == TickType.MINOR && isDomainMinorGridlinesVisible()) {
                        gridStroke = getDomainMinorGridlineStroke();
                        gridPaint = getDomainMinorGridlinePaint();
                        paintLine = true;
                    } else if (tick.getTickType() == TickType.MAJOR && isDomainGridlinesVisible()) {
                        gridStroke = getDomainGridlineStroke();
                        gridPaint = getDomainGridlinePaint();
                        paintLine = true;
                    }
                    XYItemRenderer r = getRenderer();
                    if ((r instanceof AbstractXYItemRenderer) && paintLine) {
                        ((AbstractXYItemRenderer) r).drawDomainLine(g2, this, getDomainAxis(), dataArea, tick.getValue(), gridPaint, gridStroke);
                    }
                }
            }
        }
    }

    protected void drawRangeGridlines(Graphics2D g2, Rectangle2D area, List ticks) {
        if (getRenderer() != null) {
            if (isRangeGridlinesVisible() || isRangeMinorGridlinesVisible()) {
                Stroke gridStroke = null;
                Paint gridPaint = null;
                if (getRangeAxis() != null) {
                    for (ValueTick tick : ticks) {
                        boolean paintLine = DEFAULT_CROSSHAIR_VISIBLE;
                        if (tick.getTickType() == TickType.MINOR && isRangeMinorGridlinesVisible()) {
                            gridStroke = getRangeMinorGridlineStroke();
                            gridPaint = getRangeMinorGridlinePaint();
                            paintLine = true;
                        } else if (tick.getTickType() == TickType.MAJOR && isRangeGridlinesVisible()) {
                            gridStroke = getRangeGridlineStroke();
                            gridPaint = getRangeGridlinePaint();
                            paintLine = true;
                        }
                        if (!(tick.getValue() == 0.0d && isRangeZeroBaselineVisible()) && paintLine) {
                            getRenderer().drawRangeLine(g2, this, getRangeAxis(), area, tick.getValue(), gridPaint, gridStroke);
                        }
                    }
                }
            }
        }
    }

    protected void drawZeroDomainBaseline(Graphics2D g2, Rectangle2D area) {
        if (isDomainZeroBaselineVisible()) {
            XYItemRenderer r = getRenderer();
            if (r instanceof AbstractXYItemRenderer) {
                ((AbstractXYItemRenderer) r).drawDomainLine(g2, this, getDomainAxis(), area, 0.0d, this.domainZeroBaselinePaint, this.domainZeroBaselineStroke);
            }
        }
    }

    protected void drawZeroRangeBaseline(Graphics2D g2, Rectangle2D area) {
        if (isRangeZeroBaselineVisible()) {
            getRenderer().drawRangeLine(g2, this, getRangeAxis(), area, 0.0d, this.rangeZeroBaselinePaint, this.rangeZeroBaselineStroke);
        }
    }

    public void drawAnnotations(Graphics2D g2, Rectangle2D dataArea, PlotRenderingInfo info) {
        for (XYAnnotation annotation : this.annotations) {
            annotation.draw(g2, this, dataArea, getDomainAxis(), getRangeAxis(), 0, info);
        }
    }

    protected void drawDomainMarkers(Graphics2D g2, Rectangle2D dataArea, int index, Layer layer) {
        XYItemRenderer r = getRenderer(index);
        if (r != null && index < getDatasetCount()) {
            Collection<Marker> markers = getDomainMarkers(index, layer);
            ValueAxis axis = getDomainAxisForDataset(index);
            if (markers != null && axis != null) {
                for (Marker marker : markers) {
                    r.drawDomainMarker(g2, this, axis, marker, dataArea);
                }
            }
        }
    }

    protected void drawRangeMarkers(Graphics2D g2, Rectangle2D dataArea, int index, Layer layer) {
        XYItemRenderer r = getRenderer(index);
        if (r != null && index < getDatasetCount()) {
            Collection<Marker> markers = getRangeMarkers(index, layer);
            ValueAxis axis = getRangeAxisForDataset(index);
            if (markers != null && axis != null) {
                for (Marker marker : markers) {
                    r.drawRangeMarker(g2, this, axis, marker, dataArea);
                }
            }
        }
    }

    public Collection getDomainMarkers(Layer layer) {
        return getDomainMarkers(0, layer);
    }

    public Collection getRangeMarkers(Layer layer) {
        return getRangeMarkers(0, layer);
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

    protected void drawHorizontalLine(Graphics2D g2, Rectangle2D dataArea, double value, Stroke stroke, Paint paint) {
        ValueAxis axis = getRangeAxis();
        if (getOrientation() == PlotOrientation.HORIZONTAL) {
            axis = getDomainAxis();
        }
        if (axis.getRange().contains(value)) {
            double yy = axis.valueToJava2D(value, dataArea, RectangleEdge.LEFT);
            Line2D line = new Line2D.Double(dataArea.getMinX(), yy, dataArea.getMaxX(), yy);
            g2.setStroke(stroke);
            g2.setPaint(paint);
            g2.draw(line);
        }
    }

    protected void drawDomainCrosshair(Graphics2D g2, Rectangle2D dataArea, PlotOrientation orientation, double value, ValueAxis axis, Stroke stroke, Paint paint) {
        if (axis.getRange().contains(value)) {
            Line2D line;
            if (orientation == PlotOrientation.VERTICAL) {
                double xx = axis.valueToJava2D(value, dataArea, RectangleEdge.BOTTOM);
                line = new Line2D.Double(xx, dataArea.getMinY(), xx, dataArea.getMaxY());
            } else {
                double yy = axis.valueToJava2D(value, dataArea, RectangleEdge.LEFT);
                Line2D.Double doubleR = new Line2D.Double(dataArea.getMinX(), yy, dataArea.getMaxX(), yy);
            }
            Object saved = g2.getRenderingHint(RenderingHints.KEY_STROKE_CONTROL);
            g2.setRenderingHint(RenderingHints.KEY_STROKE_CONTROL, RenderingHints.VALUE_STROKE_NORMALIZE);
            g2.setStroke(stroke);
            g2.setPaint(paint);
            g2.draw(line);
            g2.setRenderingHint(RenderingHints.KEY_STROKE_CONTROL, saved);
        }
    }

    protected void drawVerticalLine(Graphics2D g2, Rectangle2D dataArea, double value, Stroke stroke, Paint paint) {
        ValueAxis axis = getDomainAxis();
        if (getOrientation() == PlotOrientation.HORIZONTAL) {
            axis = getRangeAxis();
        }
        if (axis.getRange().contains(value)) {
            double xx = axis.valueToJava2D(value, dataArea, RectangleEdge.BOTTOM);
            Line2D line = new Line2D.Double(xx, dataArea.getMinY(), xx, dataArea.getMaxY());
            g2.setStroke(stroke);
            g2.setPaint(paint);
            g2.draw(line);
        }
    }

    protected void drawRangeCrosshair(Graphics2D g2, Rectangle2D dataArea, PlotOrientation orientation, double value, ValueAxis axis, Stroke stroke, Paint paint) {
        if (axis.getRange().contains(value)) {
            Line2D line;
            Object saved = g2.getRenderingHint(RenderingHints.KEY_STROKE_CONTROL);
            g2.setRenderingHint(RenderingHints.KEY_STROKE_CONTROL, RenderingHints.VALUE_STROKE_NORMALIZE);
            if (orientation == PlotOrientation.HORIZONTAL) {
                double xx = axis.valueToJava2D(value, dataArea, RectangleEdge.BOTTOM);
                line = new Line2D.Double(xx, dataArea.getMinY(), xx, dataArea.getMaxY());
            } else {
                double yy = axis.valueToJava2D(value, dataArea, RectangleEdge.LEFT);
                Line2D.Double doubleR = new Line2D.Double(dataArea.getMinX(), yy, dataArea.getMaxX(), yy);
            }
            g2.setStroke(stroke);
            g2.setPaint(paint);
            g2.draw(line);
            g2.setRenderingHint(RenderingHints.KEY_STROKE_CONTROL, saved);
        }
    }

    public void handleClick(int x, int y, PlotRenderingInfo info) {
        if (info.getDataArea().contains((double) x, (double) y)) {
            ValueAxis xaxis = getDomainAxis();
            if (xaxis != null) {
                setDomainCrosshairValue(xaxis.java2DToValue((double) x, info.getDataArea(), getDomainAxisEdge()));
            }
            ValueAxis yaxis = getRangeAxis();
            if (yaxis != null) {
                setRangeCrosshairValue(yaxis.java2DToValue((double) y, info.getDataArea(), getRangeAxisEdge()));
            }
        }
    }

    private List<XYDataset> getDatasetsMappedToDomainAxis(Integer axisIndex) {
        ParamChecks.nullNotPermitted(axisIndex, "axisIndex");
        List<XYDataset> result = new ArrayList();
        for (Entry<Integer, XYDataset> entry : this.datasets.entrySet()) {
            List<Integer> mappedAxes = (List) this.datasetToDomainAxesMap.get(Integer.valueOf(((Integer) entry.getKey()).intValue()));
            if (mappedAxes == null) {
                if (axisIndex.equals(ZERO)) {
                    result.add(entry.getValue());
                }
            } else if (mappedAxes.contains(axisIndex)) {
                result.add(entry.getValue());
            }
        }
        return result;
    }

    private List<XYDataset> getDatasetsMappedToRangeAxis(Integer axisIndex) {
        ParamChecks.nullNotPermitted(axisIndex, "axisIndex");
        List<XYDataset> result = new ArrayList();
        for (Entry<Integer, XYDataset> entry : this.datasets.entrySet()) {
            List<Integer> mappedAxes = (List) this.datasetToRangeAxesMap.get(Integer.valueOf(((Integer) entry.getKey()).intValue()));
            if (mappedAxes == null) {
                if (axisIndex.equals(ZERO)) {
                    result.add(entry.getValue());
                }
            } else if (mappedAxes.contains(axisIndex)) {
                result.add(entry.getValue());
            }
        }
        return result;
    }

    public int getDomainAxisIndex(ValueAxis axis) {
        int result = findDomainAxisIndex(axis);
        if (result >= 0) {
            return result;
        }
        Plot parent = getParent();
        if (parent instanceof XYPlot) {
            return ((XYPlot) parent).getDomainAxisIndex(axis);
        }
        return result;
    }

    private int findDomainAxisIndex(ValueAxis axis) {
        for (Entry<Integer, ValueAxis> entry : this.domainAxes.entrySet()) {
            if (entry.getValue() == axis) {
                return ((Integer) entry.getKey()).intValue();
            }
        }
        return -1;
    }

    public int getRangeAxisIndex(ValueAxis axis) {
        int result = findRangeAxisIndex(axis);
        if (result >= 0) {
            return result;
        }
        Plot parent = getParent();
        if (parent instanceof XYPlot) {
            return ((XYPlot) parent).getRangeAxisIndex(axis);
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

    public Range getDataRange(ValueAxis axis) {
        Range result = null;
        List<XYDataset> mappedDatasets = new ArrayList();
        List<XYAnnotation> includedAnnotations = new ArrayList();
        boolean isDomainAxis = true;
        int domainIndex = getDomainAxisIndex(axis);
        if (domainIndex >= 0) {
            isDomainAxis = true;
            mappedDatasets.addAll(getDatasetsMappedToDomainAxis(Integer.valueOf(domainIndex)));
            if (domainIndex == 0) {
                for (XYAnnotation annotation : this.annotations) {
                    if (annotation instanceof XYAnnotationBoundsInfo) {
                        includedAnnotations.add(annotation);
                    }
                }
            }
        }
        int rangeIndex = getRangeAxisIndex(axis);
        if (rangeIndex >= 0) {
            isDomainAxis = DEFAULT_CROSSHAIR_VISIBLE;
            mappedDatasets.addAll(getDatasetsMappedToRangeAxis(Integer.valueOf(rangeIndex)));
            if (rangeIndex == 0) {
                for (XYAnnotation annotation2 : this.annotations) {
                    if (annotation2 instanceof XYAnnotationBoundsInfo) {
                        includedAnnotations.add(annotation2);
                    }
                }
            }
        }
        for (XYDataset d : mappedDatasets) {
            if (d != null) {
                XYItemRenderer r = getRendererForDataset(d);
                if (isDomainAxis) {
                    if (r != null) {
                        result = Range.combine(result, r.findDomainBounds(d));
                    } else {
                        result = Range.combine(result, DatasetUtilities.findDomainBounds(d));
                    }
                } else if (r != null) {
                    result = Range.combine(result, r.findRangeBounds(d));
                } else {
                    result = Range.combine(result, DatasetUtilities.findRangeBounds(d));
                }
                if (r instanceof AbstractXYItemRenderer) {
                    for (XYAnnotation a : ((AbstractXYItemRenderer) r).getAnnotations()) {
                        if (a instanceof XYAnnotationBoundsInfo) {
                            includedAnnotations.add(a);
                        }
                    }
                }
            }
        }
        Iterator it = includedAnnotations.iterator();
        while (it.hasNext()) {
            XYAnnotationBoundsInfo xyabi = (XYAnnotationBoundsInfo) it.next();
            if (xyabi.getIncludeInDataBounds()) {
                if (isDomainAxis) {
                    result = Range.combine(result, xyabi.getXRange());
                } else {
                    result = Range.combine(result, xyabi.getYRange());
                }
            }
        }
        return result;
    }

    public void annotationChanged(AnnotationChangeEvent event) {
        if (getParent() != null) {
            getParent().annotationChanged(event);
        } else {
            notifyListeners(new PlotChangeEvent(this));
        }
    }

    public void datasetChanged(DatasetChangeEvent event) {
        configureDomainAxes();
        configureRangeAxes();
        if (getParent() != null) {
            getParent().datasetChanged(event);
            return;
        }
        PlotChangeEvent e = new PlotChangeEvent(this);
        e.setType(ChartChangeEventType.DATASET_UPDATED);
        notifyListeners(e);
    }

    public void rendererChanged(RendererChangeEvent event) {
        if (event.getSeriesVisibilityChanged()) {
            configureDomainAxes();
            configureRangeAxes();
        }
        fireChangeEvent();
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

    public boolean isDomainCrosshairLockedOnData() {
        return this.domainCrosshairLockedOnData;
    }

    public void setDomainCrosshairLockedOnData(boolean flag) {
        if (this.domainCrosshairLockedOnData != flag) {
            this.domainCrosshairLockedOnData = flag;
            fireChangeEvent();
        }
    }

    public double getDomainCrosshairValue() {
        return this.domainCrosshairValue;
    }

    public void setDomainCrosshairValue(double value) {
        setDomainCrosshairValue(value, true);
    }

    public void setDomainCrosshairValue(double value, boolean notify) {
        this.domainCrosshairValue = value;
        if (isDomainCrosshairVisible() && notify) {
            fireChangeEvent();
        }
    }

    public Stroke getDomainCrosshairStroke() {
        return this.domainCrosshairStroke;
    }

    public void setDomainCrosshairStroke(Stroke stroke) {
        ParamChecks.nullNotPermitted(stroke, "stroke");
        this.domainCrosshairStroke = stroke;
        fireChangeEvent();
    }

    public Paint getDomainCrosshairPaint() {
        return this.domainCrosshairPaint;
    }

    public void setDomainCrosshairPaint(Paint paint) {
        ParamChecks.nullNotPermitted(paint, "paint");
        this.domainCrosshairPaint = paint;
        fireChangeEvent();
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
        setRangeCrosshairValue(value, true);
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

    public AxisSpace getFixedDomainAxisSpace() {
        return this.fixedDomainAxisSpace;
    }

    public void setFixedDomainAxisSpace(AxisSpace space) {
        setFixedDomainAxisSpace(space, true);
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
        setFixedRangeAxisSpace(space, true);
    }

    public void setFixedRangeAxisSpace(AxisSpace space, boolean notify) {
        this.fixedRangeAxisSpace = space;
        if (notify) {
            fireChangeEvent();
        }
    }

    public boolean isDomainPannable() {
        return this.domainPannable;
    }

    public void setDomainPannable(boolean pannable) {
        this.domainPannable = pannable;
    }

    public boolean isRangePannable() {
        return this.rangePannable;
    }

    public void setRangePannable(boolean pannable) {
        this.rangePannable = pannable;
    }

    public void panDomainAxes(double percent, PlotRenderingInfo info, Point2D source) {
        if (isDomainPannable()) {
            int domainAxisCount = getDomainAxisCount();
            for (int i = 0; i < domainAxisCount; i++) {
                ValueAxis axis = getDomainAxis(i);
                if (axis != null) {
                    if (axis.isInverted()) {
                        percent = -percent;
                    }
                    axis.pan(percent);
                }
            }
        }
    }

    public void panRangeAxes(double percent, PlotRenderingInfo info, Point2D source) {
        if (isRangePannable()) {
            int rangeAxisCount = getRangeAxisCount();
            for (int i = 0; i < rangeAxisCount; i++) {
                ValueAxis axis = getRangeAxis(i);
                if (axis != null) {
                    if (axis.isInverted()) {
                        percent = -percent;
                    }
                    axis.pan(percent);
                }
            }
        }
    }

    public void zoomDomainAxes(double factor, PlotRenderingInfo info, Point2D source) {
        zoomDomainAxes(factor, info, source, (boolean) DEFAULT_CROSSHAIR_VISIBLE);
    }

    public void zoomDomainAxes(double factor, PlotRenderingInfo info, Point2D source, boolean useAnchor) {
        for (ValueAxis xAxis : this.domainAxes.values()) {
            if (xAxis != null) {
                if (useAnchor) {
                    double sourceX = source.getX();
                    if (this.orientation == PlotOrientation.HORIZONTAL) {
                        sourceX = source.getY();
                    }
                    xAxis.resizeRange2(factor, xAxis.java2DToValue(sourceX, info.getDataArea(), getDomainAxisEdge()));
                } else {
                    xAxis.resizeRange(factor);
                }
            }
        }
    }

    public void zoomDomainAxes(double lowerPercent, double upperPercent, PlotRenderingInfo info, Point2D source) {
        for (ValueAxis xAxis : this.domainAxes.values()) {
            if (xAxis != null) {
                xAxis.zoomRange(lowerPercent, upperPercent);
            }
        }
    }

    public void zoomRangeAxes(double factor, PlotRenderingInfo info, Point2D source) {
        zoomRangeAxes(factor, info, source, (boolean) DEFAULT_CROSSHAIR_VISIBLE);
    }

    public void zoomRangeAxes(double factor, PlotRenderingInfo info, Point2D source, boolean useAnchor) {
        for (ValueAxis yAxis : this.rangeAxes.values()) {
            if (yAxis != null) {
                if (useAnchor) {
                    double sourceY = source.getY();
                    if (this.orientation == PlotOrientation.HORIZONTAL) {
                        sourceY = source.getX();
                    }
                    yAxis.resizeRange2(factor, yAxis.java2DToValue(sourceY, info.getDataArea(), getRangeAxisEdge()));
                } else {
                    yAxis.resizeRange(factor);
                }
            }
        }
    }

    public void zoomRangeAxes(double lowerPercent, double upperPercent, PlotRenderingInfo info, Point2D source) {
        for (ValueAxis yAxis : this.rangeAxes.values()) {
            if (yAxis != null) {
                yAxis.zoomRange(lowerPercent, upperPercent);
            }
        }
    }

    public boolean isDomainZoomable() {
        return true;
    }

    public boolean isRangeZoomable() {
        return true;
    }

    public int getSeriesCount() {
        XYDataset dataset = getDataset();
        if (dataset != null) {
            return dataset.getSeriesCount();
        }
        return 0;
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
        for (XYDataset dataset : this.datasets.values()) {
            if (dataset != null) {
                int datasetIndex = indexOf(dataset);
                XYItemRenderer renderer = getRenderer(datasetIndex);
                if (renderer == null) {
                    renderer = getRenderer(0);
                }
                if (renderer != null) {
                    int seriesCount = dataset.getSeriesCount();
                    int i = 0;
                    while (i < seriesCount) {
                        if (renderer.isSeriesVisible(i) && renderer.isSeriesVisibleInLegend(i)) {
                            LegendItem item = renderer.getLegendItem(datasetIndex, i);
                            if (item != null) {
                                result.add(item);
                            }
                        }
                        i++;
                    }
                }
            }
        }
        return result;
    }

    public boolean equals(Object obj) {
        if (obj == this) {
            return true;
        }
        if (!(obj instanceof XYPlot)) {
            return DEFAULT_CROSSHAIR_VISIBLE;
        }
        XYPlot that = (XYPlot) obj;
        if (this.weight != that.weight || this.orientation != that.orientation || !this.domainAxes.equals(that.domainAxes) || !this.domainAxisLocations.equals(that.domainAxisLocations) || this.rangeCrosshairLockedOnData != that.rangeCrosshairLockedOnData || this.domainGridlinesVisible != that.domainGridlinesVisible || this.rangeGridlinesVisible != that.rangeGridlinesVisible || this.domainMinorGridlinesVisible != that.domainMinorGridlinesVisible || this.rangeMinorGridlinesVisible != that.rangeMinorGridlinesVisible || this.domainZeroBaselineVisible != that.domainZeroBaselineVisible || this.rangeZeroBaselineVisible != that.rangeZeroBaselineVisible || this.domainCrosshairVisible != that.domainCrosshairVisible || this.domainCrosshairValue != that.domainCrosshairValue || this.domainCrosshairLockedOnData != that.domainCrosshairLockedOnData || this.rangeCrosshairVisible != that.rangeCrosshairVisible || this.rangeCrosshairValue != that.rangeCrosshairValue || !ObjectUtilities.equal(this.axisOffset, that.axisOffset) || !ObjectUtilities.equal(this.renderers, that.renderers) || !ObjectUtilities.equal(this.rangeAxes, that.rangeAxes) || !this.rangeAxisLocations.equals(that.rangeAxisLocations) || !ObjectUtilities.equal(this.datasetToDomainAxesMap, that.datasetToDomainAxesMap) || !ObjectUtilities.equal(this.datasetToRangeAxesMap, that.datasetToRangeAxesMap) || !ObjectUtilities.equal(this.domainGridlineStroke, that.domainGridlineStroke) || !PaintUtilities.equal(this.domainGridlinePaint, that.domainGridlinePaint) || !ObjectUtilities.equal(this.rangeGridlineStroke, that.rangeGridlineStroke) || !PaintUtilities.equal(this.rangeGridlinePaint, that.rangeGridlinePaint) || !ObjectUtilities.equal(this.domainMinorGridlineStroke, that.domainMinorGridlineStroke) || !PaintUtilities.equal(this.domainMinorGridlinePaint, that.domainMinorGridlinePaint) || !ObjectUtilities.equal(this.rangeMinorGridlineStroke, that.rangeMinorGridlineStroke) || !PaintUtilities.equal(this.rangeMinorGridlinePaint, that.rangeMinorGridlinePaint) || !PaintUtilities.equal(this.domainZeroBaselinePaint, that.domainZeroBaselinePaint) || !ObjectUtilities.equal(this.domainZeroBaselineStroke, that.domainZeroBaselineStroke) || !PaintUtilities.equal(this.rangeZeroBaselinePaint, that.rangeZeroBaselinePaint) || !ObjectUtilities.equal(this.rangeZeroBaselineStroke, that.rangeZeroBaselineStroke) || !ObjectUtilities.equal(this.domainCrosshairStroke, that.domainCrosshairStroke) || !PaintUtilities.equal(this.domainCrosshairPaint, that.domainCrosshairPaint) || !ObjectUtilities.equal(this.rangeCrosshairStroke, that.rangeCrosshairStroke) || !PaintUtilities.equal(this.rangeCrosshairPaint, that.rangeCrosshairPaint) || !ObjectUtilities.equal(this.foregroundDomainMarkers, that.foregroundDomainMarkers) || !ObjectUtilities.equal(this.backgroundDomainMarkers, that.backgroundDomainMarkers) || !ObjectUtilities.equal(this.foregroundRangeMarkers, that.foregroundRangeMarkers) || !ObjectUtilities.equal(this.backgroundRangeMarkers, that.backgroundRangeMarkers) || !ObjectUtilities.equal(this.foregroundDomainMarkers, that.foregroundDomainMarkers) || !ObjectUtilities.equal(this.backgroundDomainMarkers, that.backgroundDomainMarkers) || !ObjectUtilities.equal(this.foregroundRangeMarkers, that.foregroundRangeMarkers) || !ObjectUtilities.equal(this.backgroundRangeMarkers, that.backgroundRangeMarkers) || !ObjectUtilities.equal(this.annotations, that.annotations) || !ObjectUtilities.equal(this.fixedLegendItems, that.fixedLegendItems) || !PaintUtilities.equal(this.domainTickBandPaint, that.domainTickBandPaint) || !PaintUtilities.equal(this.rangeTickBandPaint, that.rangeTickBandPaint) || !this.quadrantOrigin.equals(that.quadrantOrigin)) {
            return DEFAULT_CROSSHAIR_VISIBLE;
        }
        for (int i = 0; i < 4; i++) {
            if (!PaintUtilities.equal(this.quadrantPaint[i], that.quadrantPaint[i])) {
                return DEFAULT_CROSSHAIR_VISIBLE;
            }
        }
        if (ObjectUtilities.equal(this.shadowGenerator, that.shadowGenerator)) {
            return super.equals(obj);
        }
        return DEFAULT_CROSSHAIR_VISIBLE;
    }

    public Object clone() throws CloneNotSupportedException {
        XYPlot clone = (XYPlot) super.clone();
        clone.domainAxes = CloneUtils.cloneMapValues(this.domainAxes);
        for (ValueAxis axis : clone.domainAxes.values()) {
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
        for (XYDataset dataset : clone.datasets.values()) {
            if (dataset != null) {
                dataset.addChangeListener(clone);
            }
        }
        clone.datasetToDomainAxesMap = new TreeMap();
        clone.datasetToDomainAxesMap.putAll(this.datasetToDomainAxesMap);
        clone.datasetToRangeAxesMap = new TreeMap();
        clone.datasetToRangeAxesMap.putAll(this.datasetToRangeAxesMap);
        clone.renderers = CloneUtils.cloneMapValues(this.renderers);
        for (XYItemRenderer renderer : clone.renderers.values()) {
            if (renderer != null) {
                renderer.setPlot(clone);
                renderer.addChangeListener(clone);
            }
        }
        clone.foregroundDomainMarkers = (Map) ObjectUtilities.clone(this.foregroundDomainMarkers);
        clone.backgroundDomainMarkers = (Map) ObjectUtilities.clone(this.backgroundDomainMarkers);
        clone.foregroundRangeMarkers = (Map) ObjectUtilities.clone(this.foregroundRangeMarkers);
        clone.backgroundRangeMarkers = (Map) ObjectUtilities.clone(this.backgroundRangeMarkers);
        clone.annotations = (List) ObjectUtilities.deepClone(this.annotations);
        if (this.fixedDomainAxisSpace != null) {
            clone.fixedDomainAxisSpace = (AxisSpace) ObjectUtilities.clone(this.fixedDomainAxisSpace);
        }
        if (this.fixedRangeAxisSpace != null) {
            clone.fixedRangeAxisSpace = (AxisSpace) ObjectUtilities.clone(this.fixedRangeAxisSpace);
        }
        if (this.fixedLegendItems != null) {
            clone.fixedLegendItems = (LegendItemCollection) this.fixedLegendItems.clone();
        }
        clone.quadrantOrigin = (Point2D) ObjectUtilities.clone(this.quadrantOrigin);
        clone.quadrantPaint = (Paint[]) this.quadrantPaint.clone();
        return clone;
    }

    private void writeObject(ObjectOutputStream stream) throws IOException {
        stream.defaultWriteObject();
        SerialUtilities.writeStroke(this.domainGridlineStroke, stream);
        SerialUtilities.writePaint(this.domainGridlinePaint, stream);
        SerialUtilities.writeStroke(this.rangeGridlineStroke, stream);
        SerialUtilities.writePaint(this.rangeGridlinePaint, stream);
        SerialUtilities.writeStroke(this.domainMinorGridlineStroke, stream);
        SerialUtilities.writePaint(this.domainMinorGridlinePaint, stream);
        SerialUtilities.writeStroke(this.rangeMinorGridlineStroke, stream);
        SerialUtilities.writePaint(this.rangeMinorGridlinePaint, stream);
        SerialUtilities.writeStroke(this.rangeZeroBaselineStroke, stream);
        SerialUtilities.writePaint(this.rangeZeroBaselinePaint, stream);
        SerialUtilities.writeStroke(this.domainCrosshairStroke, stream);
        SerialUtilities.writePaint(this.domainCrosshairPaint, stream);
        SerialUtilities.writeStroke(this.rangeCrosshairStroke, stream);
        SerialUtilities.writePaint(this.rangeCrosshairPaint, stream);
        SerialUtilities.writePaint(this.domainTickBandPaint, stream);
        SerialUtilities.writePaint(this.rangeTickBandPaint, stream);
        SerialUtilities.writePoint2D(this.quadrantOrigin, stream);
        for (int i = 0; i < 4; i++) {
            SerialUtilities.writePaint(this.quadrantPaint[i], stream);
        }
        SerialUtilities.writeStroke(this.domainZeroBaselineStroke, stream);
        SerialUtilities.writePaint(this.domainZeroBaselinePaint, stream);
    }

    private void readObject(ObjectInputStream stream) throws IOException, ClassNotFoundException {
        stream.defaultReadObject();
        this.domainGridlineStroke = SerialUtilities.readStroke(stream);
        this.domainGridlinePaint = SerialUtilities.readPaint(stream);
        this.rangeGridlineStroke = SerialUtilities.readStroke(stream);
        this.rangeGridlinePaint = SerialUtilities.readPaint(stream);
        this.domainMinorGridlineStroke = SerialUtilities.readStroke(stream);
        this.domainMinorGridlinePaint = SerialUtilities.readPaint(stream);
        this.rangeMinorGridlineStroke = SerialUtilities.readStroke(stream);
        this.rangeMinorGridlinePaint = SerialUtilities.readPaint(stream);
        this.rangeZeroBaselineStroke = SerialUtilities.readStroke(stream);
        this.rangeZeroBaselinePaint = SerialUtilities.readPaint(stream);
        this.domainCrosshairStroke = SerialUtilities.readStroke(stream);
        this.domainCrosshairPaint = SerialUtilities.readPaint(stream);
        this.rangeCrosshairStroke = SerialUtilities.readStroke(stream);
        this.rangeCrosshairPaint = SerialUtilities.readPaint(stream);
        this.domainTickBandPaint = SerialUtilities.readPaint(stream);
        this.rangeTickBandPaint = SerialUtilities.readPaint(stream);
        this.quadrantOrigin = SerialUtilities.readPoint2D(stream);
        this.quadrantPaint = new Paint[4];
        for (int i = 0; i < 4; i++) {
            this.quadrantPaint[i] = SerialUtilities.readPaint(stream);
        }
        this.domainZeroBaselineStroke = SerialUtilities.readStroke(stream);
        this.domainZeroBaselinePaint = SerialUtilities.readPaint(stream);
        for (ValueAxis axis : this.domainAxes.values()) {
            if (axis != null) {
                axis.setPlot(this);
                axis.addChangeListener(this);
            }
        }
        for (ValueAxis axis2 : this.rangeAxes.values()) {
            if (axis2 != null) {
                axis2.setPlot(this);
                axis2.addChangeListener(this);
            }
        }
        for (XYDataset dataset : this.datasets.values()) {
            if (dataset != null) {
                dataset.addChangeListener(this);
            }
        }
        for (XYItemRenderer renderer : this.renderers.values()) {
            if (renderer != null) {
                renderer.addChangeListener(this);
            }
        }
    }
}
