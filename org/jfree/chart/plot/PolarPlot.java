package org.jfree.chart.plot;

import java.awt.AlphaComposite;
import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Composite;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.Paint;
import java.awt.Point;
import java.awt.Shape;
import java.awt.Stroke;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.awt.geom.Rectangle2D.Double;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.TreeMap;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.LegendItemCollection;
import org.jfree.chart.annotations.XYPointerAnnotation;
import org.jfree.chart.axis.Axis;
import org.jfree.chart.axis.AxisState;
import org.jfree.chart.axis.DateAxis;
import org.jfree.chart.axis.NumberTick;
import org.jfree.chart.axis.NumberTickUnit;
import org.jfree.chart.axis.TickType;
import org.jfree.chart.axis.TickUnit;
import org.jfree.chart.axis.ValueAxis;
import org.jfree.chart.axis.ValueTick;
import org.jfree.chart.event.RendererChangeEvent;
import org.jfree.chart.event.RendererChangeListener;
import org.jfree.chart.renderer.PolarItemRenderer;
import org.jfree.chart.util.ParamChecks;
import org.jfree.chart.util.ResourceBundleWrapper;
import org.jfree.data.Range;
import org.jfree.data.general.Dataset;
import org.jfree.data.general.DatasetChangeEvent;
import org.jfree.data.general.DatasetUtilities;
import org.jfree.data.xy.XYDataset;
import org.jfree.io.SerialUtilities;
import org.jfree.text.TextUtilities;
import org.jfree.ui.RectangleEdge;
import org.jfree.ui.TextAnchor;
import org.jfree.util.ObjectList;
import org.jfree.util.ObjectUtilities;
import org.jfree.util.PaintUtilities;
import org.jfree.util.PublicCloneable;

public class PolarPlot extends Plot implements ValueAxisPlot, Zoomable, RendererChangeListener, Cloneable, Serializable {
    private static final double ANNOTATION_MARGIN = 7.0d;
    public static final double DEFAULT_ANGLE_OFFSET = -90.0d;
    public static final double DEFAULT_ANGLE_TICK_UNIT_SIZE = 45.0d;
    public static final Paint DEFAULT_GRIDLINE_PAINT;
    public static final Stroke DEFAULT_GRIDLINE_STROKE;
    private static final int DEFAULT_MARGIN = 20;
    protected static ResourceBundle localizationResources = null;
    private static final long serialVersionUID = 3794383185924179525L;
    private transient Paint angleGridlinePaint;
    private transient Stroke angleGridlineStroke;
    private boolean angleGridlinesVisible;
    private Font angleLabelFont;
    private transient Paint angleLabelPaint;
    private boolean angleLabelsVisible;
    private double angleOffset;
    private TickUnit angleTickUnit;
    private List angleTicks;
    private ObjectList axes;
    private ObjectList axisLocations;
    private List cornerTextItems;
    private boolean counterClockwise;
    private Map datasetToAxesMap;
    private ObjectList datasets;
    private LegendItemCollection fixedLegendItems;
    private int margin;
    private transient Paint radiusGridlinePaint;
    private transient Stroke radiusGridlineStroke;
    private boolean radiusGridlinesVisible;
    private boolean radiusMinorGridlinesVisible;
    private ObjectList renderers;

    static {
        DEFAULT_GRIDLINE_STROKE = new BasicStroke(JFreeChart.DEFAULT_BACKGROUND_IMAGE_ALPHA, 0, 2, 0.0f, new float[]{Axis.DEFAULT_TICK_MARK_OUTSIDE_LENGTH, Axis.DEFAULT_TICK_MARK_OUTSIDE_LENGTH}, 0.0f);
        DEFAULT_GRIDLINE_PAINT = Color.gray;
        localizationResources = ResourceBundleWrapper.getBundle("org.jfree.chart.plot.LocalizationBundle");
    }

    public PolarPlot() {
        this(null, null, null);
    }

    public PolarPlot(XYDataset dataset, ValueAxis radiusAxis, PolarItemRenderer renderer) {
        this.angleLabelsVisible = true;
        this.angleLabelFont = new Font("SansSerif", 0, 12);
        this.angleLabelPaint = Color.black;
        this.cornerTextItems = new ArrayList();
        this.datasets = new ObjectList();
        this.datasets.set(0, dataset);
        if (dataset != null) {
            dataset.addChangeListener(this);
        }
        this.angleTickUnit = new NumberTickUnit(DEFAULT_ANGLE_TICK_UNIT_SIZE);
        this.axes = new ObjectList();
        this.datasetToAxesMap = new TreeMap();
        this.axes.set(0, radiusAxis);
        if (radiusAxis != null) {
            radiusAxis.setPlot(this);
            radiusAxis.addChangeListener(this);
        }
        this.axisLocations = new ObjectList();
        this.axisLocations.set(0, PolarAxisLocation.EAST_ABOVE);
        this.axisLocations.set(1, PolarAxisLocation.NORTH_LEFT);
        this.axisLocations.set(2, PolarAxisLocation.WEST_BELOW);
        this.axisLocations.set(3, PolarAxisLocation.SOUTH_RIGHT);
        this.axisLocations.set(4, PolarAxisLocation.EAST_BELOW);
        this.axisLocations.set(5, PolarAxisLocation.NORTH_RIGHT);
        this.axisLocations.set(6, PolarAxisLocation.WEST_ABOVE);
        this.axisLocations.set(7, PolarAxisLocation.SOUTH_LEFT);
        this.renderers = new ObjectList();
        this.renderers.set(0, renderer);
        if (renderer != null) {
            renderer.setPlot(this);
            renderer.addChangeListener(this);
        }
        this.angleOffset = DEFAULT_ANGLE_OFFSET;
        this.counterClockwise = false;
        this.angleGridlinesVisible = true;
        this.angleGridlineStroke = DEFAULT_GRIDLINE_STROKE;
        this.angleGridlinePaint = DEFAULT_GRIDLINE_PAINT;
        this.radiusGridlinesVisible = true;
        this.radiusMinorGridlinesVisible = true;
        this.radiusGridlineStroke = DEFAULT_GRIDLINE_STROKE;
        this.radiusGridlinePaint = DEFAULT_GRIDLINE_PAINT;
        this.margin = DEFAULT_MARGIN;
    }

    public String getPlotType() {
        return localizationResources.getString("Polar_Plot");
    }

    public ValueAxis getAxis() {
        return getAxis(0);
    }

    public ValueAxis getAxis(int index) {
        if (index < this.axes.size()) {
            return (ValueAxis) this.axes.get(index);
        }
        return null;
    }

    public void setAxis(ValueAxis axis) {
        setAxis(0, axis);
    }

    public void setAxis(int index, ValueAxis axis) {
        setAxis(index, axis, true);
    }

    public void setAxis(int index, ValueAxis axis, boolean notify) {
        ValueAxis existing = getAxis(index);
        if (existing != null) {
            existing.removeChangeListener(this);
        }
        if (axis != null) {
            axis.setPlot(this);
        }
        this.axes.set(index, axis);
        if (axis != null) {
            axis.configure();
            axis.addChangeListener(this);
        }
        if (notify) {
            fireChangeEvent();
        }
    }

    public PolarAxisLocation getAxisLocation() {
        return getAxisLocation(0);
    }

    public PolarAxisLocation getAxisLocation(int index) {
        if (index < this.axisLocations.size()) {
            return (PolarAxisLocation) this.axisLocations.get(index);
        }
        return null;
    }

    public void setAxisLocation(PolarAxisLocation location) {
        setAxisLocation(0, location, true);
    }

    public void setAxisLocation(PolarAxisLocation location, boolean notify) {
        setAxisLocation(0, location, notify);
    }

    public void setAxisLocation(int index, PolarAxisLocation location) {
        setAxisLocation(index, location, true);
    }

    public void setAxisLocation(int index, PolarAxisLocation location, boolean notify) {
        ParamChecks.nullNotPermitted(location, "location");
        this.axisLocations.set(index, location);
        if (notify) {
            fireChangeEvent();
        }
    }

    public int getAxisCount() {
        return this.axes.size();
    }

    public XYDataset getDataset() {
        return getDataset(0);
    }

    public XYDataset getDataset(int index) {
        if (index < this.datasets.size()) {
            return (XYDataset) this.datasets.get(index);
        }
        return null;
    }

    public void setDataset(XYDataset dataset) {
        setDataset(0, dataset);
    }

    public void setDataset(int index, XYDataset dataset) {
        XYDataset existing = getDataset(index);
        if (existing != null) {
            existing.removeChangeListener(this);
        }
        this.datasets.set(index, dataset);
        if (dataset != null) {
            dataset.addChangeListener(this);
        }
        datasetChanged(new DatasetChangeEvent(this, dataset));
    }

    public int getDatasetCount() {
        return this.datasets.size();
    }

    public int indexOf(XYDataset dataset) {
        for (int i = 0; i < this.datasets.size(); i++) {
            if (dataset == this.datasets.get(i)) {
                return i;
            }
        }
        return -1;
    }

    public PolarItemRenderer getRenderer() {
        return getRenderer(0);
    }

    public PolarItemRenderer getRenderer(int index) {
        if (index < this.renderers.size()) {
            return (PolarItemRenderer) this.renderers.get(index);
        }
        return null;
    }

    public void setRenderer(PolarItemRenderer renderer) {
        setRenderer(0, renderer);
    }

    public void setRenderer(int index, PolarItemRenderer renderer) {
        setRenderer(index, renderer, true);
    }

    public void setRenderer(int index, PolarItemRenderer renderer, boolean notify) {
        PolarItemRenderer existing = getRenderer(index);
        if (existing != null) {
            existing.removeChangeListener(this);
        }
        this.renderers.set(index, renderer);
        if (renderer != null) {
            renderer.setPlot(this);
            renderer.addChangeListener(this);
        }
        if (notify) {
            fireChangeEvent();
        }
    }

    public TickUnit getAngleTickUnit() {
        return this.angleTickUnit;
    }

    public void setAngleTickUnit(TickUnit unit) {
        ParamChecks.nullNotPermitted(unit, "unit");
        this.angleTickUnit = unit;
        fireChangeEvent();
    }

    public double getAngleOffset() {
        return this.angleOffset;
    }

    public void setAngleOffset(double offset) {
        this.angleOffset = offset;
        fireChangeEvent();
    }

    public boolean isCounterClockwise() {
        return this.counterClockwise;
    }

    public void setCounterClockwise(boolean counterClockwise) {
        this.counterClockwise = counterClockwise;
    }

    public boolean isAngleLabelsVisible() {
        return this.angleLabelsVisible;
    }

    public void setAngleLabelsVisible(boolean visible) {
        if (this.angleLabelsVisible != visible) {
            this.angleLabelsVisible = visible;
            fireChangeEvent();
        }
    }

    public Font getAngleLabelFont() {
        return this.angleLabelFont;
    }

    public void setAngleLabelFont(Font font) {
        ParamChecks.nullNotPermitted(font, "font");
        this.angleLabelFont = font;
        fireChangeEvent();
    }

    public Paint getAngleLabelPaint() {
        return this.angleLabelPaint;
    }

    public void setAngleLabelPaint(Paint paint) {
        ParamChecks.nullNotPermitted(paint, "paint");
        this.angleLabelPaint = paint;
        fireChangeEvent();
    }

    public boolean isAngleGridlinesVisible() {
        return this.angleGridlinesVisible;
    }

    public void setAngleGridlinesVisible(boolean visible) {
        if (this.angleGridlinesVisible != visible) {
            this.angleGridlinesVisible = visible;
            fireChangeEvent();
        }
    }

    public Stroke getAngleGridlineStroke() {
        return this.angleGridlineStroke;
    }

    public void setAngleGridlineStroke(Stroke stroke) {
        this.angleGridlineStroke = stroke;
        fireChangeEvent();
    }

    public Paint getAngleGridlinePaint() {
        return this.angleGridlinePaint;
    }

    public void setAngleGridlinePaint(Paint paint) {
        this.angleGridlinePaint = paint;
        fireChangeEvent();
    }

    public boolean isRadiusGridlinesVisible() {
        return this.radiusGridlinesVisible;
    }

    public void setRadiusGridlinesVisible(boolean visible) {
        if (this.radiusGridlinesVisible != visible) {
            this.radiusGridlinesVisible = visible;
            fireChangeEvent();
        }
    }

    public Stroke getRadiusGridlineStroke() {
        return this.radiusGridlineStroke;
    }

    public void setRadiusGridlineStroke(Stroke stroke) {
        this.radiusGridlineStroke = stroke;
        fireChangeEvent();
    }

    public Paint getRadiusGridlinePaint() {
        return this.radiusGridlinePaint;
    }

    public void setRadiusGridlinePaint(Paint paint) {
        this.radiusGridlinePaint = paint;
        fireChangeEvent();
    }

    public boolean isRadiusMinorGridlinesVisible() {
        return this.radiusMinorGridlinesVisible;
    }

    public void setRadiusMinorGridlinesVisible(boolean flag) {
        this.radiusMinorGridlinesVisible = flag;
        fireChangeEvent();
    }

    public int getMargin() {
        return this.margin;
    }

    public void setMargin(int margin) {
        this.margin = margin;
        fireChangeEvent();
    }

    public LegendItemCollection getFixedLegendItems() {
        return this.fixedLegendItems;
    }

    public void setFixedLegendItems(LegendItemCollection items) {
        this.fixedLegendItems = items;
        fireChangeEvent();
    }

    public void addCornerTextItem(String text) {
        ParamChecks.nullNotPermitted(text, "text");
        this.cornerTextItems.add(text);
        fireChangeEvent();
    }

    public void removeCornerTextItem(String text) {
        if (this.cornerTextItems.remove(text)) {
            fireChangeEvent();
        }
    }

    public void clearCornerTextItems() {
        if (this.cornerTextItems.size() > 0) {
            this.cornerTextItems.clear();
            fireChangeEvent();
        }
    }

    protected List refreshAngleTicks() {
        List ticks = new ArrayList();
        double currentTickVal = 0.0d;
        while (currentTickVal < 360.0d) {
            ticks.add(new NumberTick(new Double(currentTickVal), this.angleTickUnit.valueToString(currentTickVal), calculateTextAnchor(currentTickVal), TextAnchor.CENTER, 0.0d));
            currentTickVal += this.angleTickUnit.getSize();
        }
        return ticks;
    }

    protected TextAnchor calculateTextAnchor(double angleDegrees) {
        TextAnchor ta = TextAnchor.CENTER;
        double offset = this.angleOffset;
        while (offset < 0.0d) {
            offset += 360.0d;
        }
        double normalizedAngle = ((((double) (this.counterClockwise ? -1 : 1)) * angleDegrees) + offset) % 360.0d;
        while (this.counterClockwise && normalizedAngle < 0.0d) {
            normalizedAngle += 360.0d;
        }
        if (normalizedAngle == 0.0d) {
            return TextAnchor.CENTER_LEFT;
        }
        if (normalizedAngle > 0.0d && normalizedAngle < SpiderWebPlot.DEFAULT_START_ANGLE) {
            return TextAnchor.TOP_LEFT;
        }
        if (normalizedAngle == SpiderWebPlot.DEFAULT_START_ANGLE) {
            return TextAnchor.TOP_CENTER;
        }
        if (normalizedAngle > SpiderWebPlot.DEFAULT_START_ANGLE && normalizedAngle < 180.0d) {
            return TextAnchor.TOP_RIGHT;
        }
        if (normalizedAngle == 180.0d) {
            return TextAnchor.CENTER_RIGHT;
        }
        if (normalizedAngle > 180.0d && normalizedAngle < 270.0d) {
            return TextAnchor.BOTTOM_RIGHT;
        }
        if (normalizedAngle == 270.0d) {
            return TextAnchor.BOTTOM_CENTER;
        }
        if (normalizedAngle <= 270.0d || normalizedAngle >= 360.0d) {
            return ta;
        }
        return TextAnchor.BOTTOM_LEFT;
    }

    public void mapDatasetToAxis(int index, int axisIndex) {
        List axisIndices = new ArrayList(1);
        axisIndices.add(new Integer(axisIndex));
        mapDatasetToAxes(index, axisIndices);
    }

    public void mapDatasetToAxes(int index, List axisIndices) {
        if (index < 0) {
            throw new IllegalArgumentException("Requires 'index' >= 0.");
        }
        checkAxisIndices(axisIndices);
        this.datasetToAxesMap.put(new Integer(index), new ArrayList(axisIndices));
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

    public ValueAxis getAxisForDataset(int index) {
        List axisIndices = (List) this.datasetToAxesMap.get(new Integer(index));
        if (axisIndices != null) {
            return getAxis(((Integer) axisIndices.get(0)).intValue());
        }
        return getAxis(0);
    }

    public int getAxisIndex(ValueAxis axis) {
        int result = this.axes.indexOf(axis);
        if (result >= 0) {
            return result;
        }
        Plot parent = getParent();
        if (parent instanceof PolarPlot) {
            return ((PolarPlot) parent).getAxisIndex(axis);
        }
        return result;
    }

    public int getIndexOf(PolarItemRenderer renderer) {
        return this.renderers.indexOf(renderer);
    }

    public void draw(Graphics2D g2, Rectangle2D area, Point2D anchor, PlotState parentState, PlotRenderingInfo info) {
        boolean b1 = area.getWidth() <= XYPointerAnnotation.DEFAULT_TIP_RADIUS;
        boolean b2 = area.getHeight() <= XYPointerAnnotation.DEFAULT_TIP_RADIUS;
        if (!b1 && !b2) {
            if (info != null) {
                info.setPlotArea(area);
            }
            getInsets().trim(area);
            Rectangle2D dataArea = area;
            if (info != null) {
                info.setDataArea(dataArea);
            }
            drawBackground(g2, dataArea);
            int axisCount = this.axes.size();
            AxisState state = null;
            for (int i = 0; i < axisCount; i++) {
                ValueAxis axis = getAxis(i);
                if (axis != null) {
                    AxisState s = drawAxis(axis, (PolarAxisLocation) this.axisLocations.get(i), g2, dataArea);
                    if (i == 0) {
                        state = s;
                    }
                }
            }
            Shape originalClip = g2.getClip();
            Composite originalComposite = g2.getComposite();
            g2.clip(dataArea);
            g2.setComposite(AlphaComposite.getInstance(3, getForegroundAlpha()));
            this.angleTicks = refreshAngleTicks();
            drawGridlines(g2, dataArea, this.angleTicks, state.getTicks());
            render(g2, dataArea, info);
            g2.setClip(originalClip);
            g2.setComposite(originalComposite);
            drawOutline(g2, dataArea);
            drawCornerTextItems(g2, dataArea);
        }
    }

    protected void drawCornerTextItems(Graphics2D g2, Rectangle2D area) {
        if (!this.cornerTextItems.isEmpty()) {
            g2.setColor(Color.black);
            double width = 0.0d;
            double height = 0.0d;
            for (String msg : this.cornerTextItems) {
                Rectangle2D bounds = TextUtilities.getTextBounds(msg, g2, g2.getFontMetrics());
                width = Math.max(width, bounds.getWidth());
                height += bounds.getHeight();
            }
            width += 14.0d;
            height += ANNOTATION_MARGIN;
            double x = area.getMaxX() - width;
            double y = area.getMaxY() - height;
            g2.drawRect((int) x, (int) y, (int) width, (int) height);
            x += ANNOTATION_MARGIN;
            for (String msg2 : this.cornerTextItems) {
                y += TextUtilities.getTextBounds(msg2, g2, g2.getFontMetrics()).getHeight();
                g2.drawString(msg2, (int) x, (int) y);
            }
        }
    }

    protected AxisState drawAxis(ValueAxis axis, PolarAxisLocation location, Graphics2D g2, Rectangle2D plotArea) {
        double centerX = plotArea.getCenterX();
        double centerY = plotArea.getCenterY();
        double height = plotArea.getHeight() / DateAxis.DEFAULT_AUTO_RANGE_MINIMUM_SIZE_IN_MILLISECONDS;
        double r = Math.min(plotArea.getWidth() / DateAxis.DEFAULT_AUTO_RANGE_MINIMUM_SIZE_IN_MILLISECONDS, r16) - ((double) this.margin);
        double x = centerX - r;
        double y = centerY - r;
        if (location == PolarAxisLocation.NORTH_RIGHT) {
            return axis.draw(g2, centerX, plotArea, new Double(x, y, r, r), RectangleEdge.RIGHT, null);
        } else if (location == PolarAxisLocation.NORTH_LEFT) {
            Double doubleR = new Double(centerX, y, r, r);
            return axis.draw(g2, centerX, plotArea, doubleR, RectangleEdge.LEFT, null);
        } else if (location == PolarAxisLocation.SOUTH_LEFT) {
            return axis.draw(g2, centerX, plotArea, new Double(centerX, centerY, r, r), RectangleEdge.LEFT, null);
        } else if (location == PolarAxisLocation.SOUTH_RIGHT) {
            r17 = new Double(x, centerY, r, r);
            return axis.draw(g2, centerX, plotArea, r17, RectangleEdge.RIGHT, null);
        } else if (location == PolarAxisLocation.EAST_ABOVE) {
            return axis.draw(g2, centerY, plotArea, new Double(centerX, centerY, r, r), RectangleEdge.TOP, null);
        } else if (location == PolarAxisLocation.EAST_BELOW) {
            r17 = new Double(centerX, y, r, r);
            return axis.draw(g2, centerY, plotArea, r17, RectangleEdge.BOTTOM, null);
        } else if (location == PolarAxisLocation.WEST_ABOVE) {
            r17 = new Double(x, centerY, r, r);
            return axis.draw(g2, centerY, plotArea, r17, RectangleEdge.TOP, null);
        } else if (location != PolarAxisLocation.WEST_BELOW) {
            return null;
        } else {
            return axis.draw(g2, centerY, plotArea, new Double(x, y, r, r), RectangleEdge.BOTTOM, null);
        }
    }

    protected void render(Graphics2D g2, Rectangle2D dataArea, PlotRenderingInfo info) {
        boolean hasData = false;
        for (int i = this.datasets.size() - 1; i >= 0; i--) {
            XYDataset dataset = getDataset(i);
            if (dataset != null) {
                PolarItemRenderer renderer = getRenderer(i);
                if (!(renderer == null || DatasetUtilities.isEmptyOrNull(dataset))) {
                    hasData = true;
                    int seriesCount = dataset.getSeriesCount();
                    for (int series = 0; series < seriesCount; series++) {
                        renderer.drawSeries(g2, dataArea, info, this, dataset, series);
                    }
                }
            }
        }
        if (!hasData) {
            drawNoDataMessage(g2, dataArea);
        }
    }

    protected void drawGridlines(Graphics2D g2, Rectangle2D dataArea, List angularTicks, List radialTicks) {
        PolarItemRenderer renderer = getRenderer();
        if (renderer != null) {
            Stroke gridStroke;
            Paint gridPaint;
            if (isAngleGridlinesVisible()) {
                gridStroke = getAngleGridlineStroke();
                gridPaint = getAngleGridlinePaint();
                if (!(gridStroke == null || gridPaint == null)) {
                    renderer.drawAngularGridLines(g2, this, angularTicks, dataArea);
                }
            }
            if (isRadiusGridlinesVisible()) {
                gridStroke = getRadiusGridlineStroke();
                gridPaint = getRadiusGridlinePaint();
                if (gridStroke != null && gridPaint != null) {
                    renderer.drawRadialGridLines(g2, this, getAxis(), buildRadialTicks(radialTicks), dataArea);
                }
            }
        }
    }

    protected List buildRadialTicks(List allTicks) {
        List ticks = new ArrayList();
        for (ValueTick tick : allTicks) {
            if (isRadiusMinorGridlinesVisible() || TickType.MAJOR.equals(tick.getTickType())) {
                ticks.add(tick);
            }
        }
        return ticks;
    }

    public void zoom(double percent) {
        for (int axisIdx = 0; axisIdx < getAxisCount(); axisIdx++) {
            ValueAxis axis = getAxis(axisIdx);
            if (axis != null) {
                if (percent > 0.0d) {
                    axis.setUpperBound(axis.getUpperBound() * percent);
                    axis.setAutoRange(false);
                } else {
                    axis.setAutoRange(true);
                }
            }
        }
    }

    private List getDatasetsMappedToAxis(Integer axisIndex) {
        ParamChecks.nullNotPermitted(axisIndex, "axisIndex");
        List result = new ArrayList();
        for (int i = 0; i < this.datasets.size(); i++) {
            List mappedAxes = (List) this.datasetToAxesMap.get(new Integer(i));
            if (mappedAxes == null) {
                if (axisIndex.equals(ZERO)) {
                    result.add(this.datasets.get(i));
                }
            } else if (mappedAxes.contains(axisIndex)) {
                result.add(this.datasets.get(i));
            }
        }
        return result;
    }

    public Range getDataRange(ValueAxis axis) {
        Range result = null;
        int axisIdx = getAxisIndex(axis);
        List mappedDatasets = new ArrayList();
        if (axisIdx >= 0) {
            mappedDatasets = getDatasetsMappedToAxis(new Integer(axisIdx));
        }
        int datasetIdx = -1;
        for (XYDataset d : mappedDatasets) {
            datasetIdx++;
            if (d != null) {
                result = Range.combine(result, DatasetUtilities.findRangeBounds(d));
            }
        }
        return result;
    }

    public void datasetChanged(DatasetChangeEvent event) {
        for (int i = 0; i < this.axes.size(); i++) {
            ValueAxis axis = (ValueAxis) this.axes.get(i);
            if (axis != null) {
                axis.configure();
            }
        }
        if (getParent() != null) {
            getParent().datasetChanged(event);
        } else {
            super.datasetChanged(event);
        }
    }

    public void rendererChanged(RendererChangeEvent event) {
        fireChangeEvent();
    }

    public LegendItemCollection getLegendItems() {
        if (this.fixedLegendItems != null) {
            return this.fixedLegendItems;
        }
        LegendItemCollection result = new LegendItemCollection();
        int count = this.datasets.size();
        for (int datasetIndex = 0; datasetIndex < count; datasetIndex++) {
            XYDataset dataset = getDataset(datasetIndex);
            PolarItemRenderer renderer = getRenderer(datasetIndex);
            if (!(dataset == null || renderer == null)) {
                int seriesCount = dataset.getSeriesCount();
                for (int i = 0; i < seriesCount; i++) {
                    result.add(renderer.getLegendItem(i));
                }
            }
        }
        return result;
    }

    public boolean equals(Object obj) {
        if (obj == this) {
            return true;
        }
        if (!(obj instanceof PolarPlot)) {
            return false;
        }
        PolarPlot that = (PolarPlot) obj;
        if (this.axes.equals(that.axes) && this.axisLocations.equals(that.axisLocations) && this.renderers.equals(that.renderers) && this.angleTickUnit.equals(that.angleTickUnit) && this.angleGridlinesVisible == that.angleGridlinesVisible && this.angleOffset == that.angleOffset && this.counterClockwise == that.counterClockwise && this.angleLabelsVisible == that.angleLabelsVisible && this.angleLabelFont.equals(that.angleLabelFont) && PaintUtilities.equal(this.angleLabelPaint, that.angleLabelPaint) && ObjectUtilities.equal(this.angleGridlineStroke, that.angleGridlineStroke) && PaintUtilities.equal(this.angleGridlinePaint, that.angleGridlinePaint) && this.radiusGridlinesVisible == that.radiusGridlinesVisible && ObjectUtilities.equal(this.radiusGridlineStroke, that.radiusGridlineStroke) && PaintUtilities.equal(this.radiusGridlinePaint, that.radiusGridlinePaint) && this.radiusMinorGridlinesVisible == that.radiusMinorGridlinesVisible && this.cornerTextItems.equals(that.cornerTextItems) && this.margin == that.margin && ObjectUtilities.equal(this.fixedLegendItems, that.fixedLegendItems)) {
            return super.equals(obj);
        }
        return false;
    }

    public Object clone() throws CloneNotSupportedException {
        int i;
        PolarPlot clone = (PolarPlot) super.clone();
        clone.axes = (ObjectList) ObjectUtilities.clone(this.axes);
        for (i = 0; i < this.axes.size(); i++) {
            ValueAxis axis = (ValueAxis) this.axes.get(i);
            if (axis != null) {
                ValueAxis clonedAxis = (ValueAxis) axis.clone();
                clone.axes.set(i, clonedAxis);
                clonedAxis.setPlot(clone);
                clonedAxis.addChangeListener(clone);
            }
        }
        clone.datasets = (ObjectList) ObjectUtilities.clone(this.datasets);
        for (i = 0; i < clone.datasets.size(); i++) {
            XYDataset d = getDataset(i);
            if (d != null) {
                d.addChangeListener(clone);
            }
        }
        clone.renderers = (ObjectList) ObjectUtilities.clone(this.renderers);
        for (i = 0; i < this.renderers.size(); i++) {
            PolarItemRenderer renderer2 = (PolarItemRenderer) this.renderers.get(i);
            if (renderer2 instanceof PublicCloneable) {
                PolarItemRenderer rc = (PolarItemRenderer) ((PublicCloneable) renderer2).clone();
                clone.renderers.set(i, rc);
                rc.setPlot(clone);
                rc.addChangeListener(clone);
            }
        }
        clone.cornerTextItems = new ArrayList(this.cornerTextItems);
        return clone;
    }

    private void writeObject(ObjectOutputStream stream) throws IOException {
        stream.defaultWriteObject();
        SerialUtilities.writeStroke(this.angleGridlineStroke, stream);
        SerialUtilities.writePaint(this.angleGridlinePaint, stream);
        SerialUtilities.writeStroke(this.radiusGridlineStroke, stream);
        SerialUtilities.writePaint(this.radiusGridlinePaint, stream);
        SerialUtilities.writePaint(this.angleLabelPaint, stream);
    }

    private void readObject(ObjectInputStream stream) throws IOException, ClassNotFoundException {
        int i;
        stream.defaultReadObject();
        this.angleGridlineStroke = SerialUtilities.readStroke(stream);
        this.angleGridlinePaint = SerialUtilities.readPaint(stream);
        this.radiusGridlineStroke = SerialUtilities.readStroke(stream);
        this.radiusGridlinePaint = SerialUtilities.readPaint(stream);
        this.angleLabelPaint = SerialUtilities.readPaint(stream);
        int rangeAxisCount = this.axes.size();
        for (i = 0; i < rangeAxisCount; i++) {
            Axis axis = (Axis) this.axes.get(i);
            if (axis != null) {
                axis.setPlot(this);
                axis.addChangeListener(this);
            }
        }
        int datasetCount = this.datasets.size();
        for (i = 0; i < datasetCount; i++) {
            Dataset dataset = (Dataset) this.datasets.get(i);
            if (dataset != null) {
                dataset.addChangeListener(this);
            }
        }
        int rendererCount = this.renderers.size();
        for (i = 0; i < rendererCount; i++) {
            PolarItemRenderer renderer = (PolarItemRenderer) this.renderers.get(i);
            if (renderer != null) {
                renderer.addChangeListener(this);
            }
        }
    }

    public void zoomDomainAxes(double factor, PlotRenderingInfo state, Point2D source) {
    }

    public void zoomDomainAxes(double factor, PlotRenderingInfo state, Point2D source, boolean useAnchor) {
    }

    public void zoomDomainAxes(double lowerPercent, double upperPercent, PlotRenderingInfo state, Point2D source) {
    }

    public void zoomRangeAxes(double factor, PlotRenderingInfo state, Point2D source) {
        zoom(factor);
    }

    public void zoomRangeAxes(double factor, PlotRenderingInfo info, Point2D source, boolean useAnchor) {
        double sourceX = source.getX();
        for (int axisIdx = 0; axisIdx < getAxisCount(); axisIdx++) {
            ValueAxis axis = getAxis(axisIdx);
            if (axis != null) {
                if (useAnchor) {
                    axis.resizeRange(factor, axis.java2DToValue(sourceX, info.getDataArea(), RectangleEdge.BOTTOM));
                } else {
                    axis.resizeRange(factor);
                }
            }
        }
    }

    public void zoomRangeAxes(double lowerPercent, double upperPercent, PlotRenderingInfo state, Point2D source) {
        zoom((upperPercent + lowerPercent) / DateAxis.DEFAULT_AUTO_RANGE_MINIMUM_SIZE_IN_MILLISECONDS);
    }

    public boolean isDomainZoomable() {
        return false;
    }

    public boolean isRangeZoomable() {
        return true;
    }

    public PlotOrientation getOrientation() {
        return PlotOrientation.HORIZONTAL;
    }

    public Point translateToJava2D(double angleDegrees, double radius, ValueAxis axis, Rectangle2D dataArea) {
        if (this.counterClockwise) {
            angleDegrees = -angleDegrees;
        }
        double radians = Math.toRadians(this.angleOffset + angleDegrees);
        double minx = dataArea.getMinX() + ((double) this.margin);
        double maxx = dataArea.getMaxX() - ((double) this.margin);
        double miny = dataArea.getMinY() + ((double) this.margin);
        double halfWidth = (maxx - minx) / DateAxis.DEFAULT_AUTO_RANGE_MINIMUM_SIZE_IN_MILLISECONDS;
        double halfHeight = ((dataArea.getMaxY() - ((double) this.margin)) - miny) / DateAxis.DEFAULT_AUTO_RANGE_MINIMUM_SIZE_IN_MILLISECONDS;
        double midX = minx + halfWidth;
        double midY = miny + halfHeight;
        double l = Math.min(halfWidth, halfHeight);
        ValueAxis valueAxis = axis;
        double length = valueAxis.valueToJava2D(Math.max(radius, axis.getLowerBound()), new Double(midX, midY, l, l), RectangleEdge.BOTTOM) - midX;
        float x = (float) ((Math.cos(radians) * length) + midX);
        return new Point(Math.round(x), Math.round((float) ((Math.sin(radians) * length) + midY)));
    }

    public Point translateValueThetaRadiusToJava2D(double angleDegrees, double radius, Rectangle2D dataArea) {
        return translateToJava2D(angleDegrees, radius, getAxis(), dataArea);
    }

    public double getMaxRadius() {
        return getAxis().getUpperBound();
    }

    public int getSeriesCount() {
        XYDataset dataset = getDataset(0);
        if (dataset != null) {
            return dataset.getSeriesCount();
        }
        return 0;
    }

    protected AxisState drawAxis(Graphics2D g2, Rectangle2D plotArea, Rectangle2D dataArea) {
        return getAxis().draw(g2, dataArea.getMinY(), plotArea, dataArea, RectangleEdge.TOP, null);
    }
}
