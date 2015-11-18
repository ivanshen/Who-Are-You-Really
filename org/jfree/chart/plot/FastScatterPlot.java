package org.jfree.chart.plot;

import java.awt.AlphaComposite;
import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Composite;
import java.awt.Graphics2D;
import java.awt.Paint;
import java.awt.RenderingHints;
import java.awt.Shape;
import java.awt.Stroke;
import java.awt.geom.Line2D;
import java.awt.geom.Line2D.Double;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.List;
import java.util.ResourceBundle;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.Axis;
import org.jfree.chart.axis.AxisSpace;
import org.jfree.chart.axis.AxisState;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.axis.ValueAxis;
import org.jfree.chart.axis.ValueTick;
import org.jfree.chart.util.ParamChecks;
import org.jfree.chart.util.ResourceBundleWrapper;
import org.jfree.data.Range;
import org.jfree.io.SerialUtilities;
import org.jfree.ui.RectangleEdge;
import org.jfree.util.ArrayUtilities;
import org.jfree.util.ObjectUtilities;
import org.jfree.util.PaintUtilities;

public class FastScatterPlot extends Plot implements ValueAxisPlot, Pannable, Zoomable, Cloneable, Serializable {
    public static final Paint DEFAULT_GRIDLINE_PAINT;
    public static final Stroke DEFAULT_GRIDLINE_STROKE;
    protected static ResourceBundle localizationResources = null;
    private static final long serialVersionUID = 7871545897358563521L;
    private float[][] data;
    private ValueAxis domainAxis;
    private transient Paint domainGridlinePaint;
    private transient Stroke domainGridlineStroke;
    private boolean domainGridlinesVisible;
    private boolean domainPannable;
    private transient Paint paint;
    private ValueAxis rangeAxis;
    private transient Paint rangeGridlinePaint;
    private transient Stroke rangeGridlineStroke;
    private boolean rangeGridlinesVisible;
    private boolean rangePannable;
    private Range xDataRange;
    private Range yDataRange;

    static {
        DEFAULT_GRIDLINE_STROKE = new BasicStroke(JFreeChart.DEFAULT_BACKGROUND_IMAGE_ALPHA, 0, 2, 0.0f, new float[]{Axis.DEFAULT_TICK_MARK_OUTSIDE_LENGTH, Axis.DEFAULT_TICK_MARK_OUTSIDE_LENGTH}, 0.0f);
        DEFAULT_GRIDLINE_PAINT = Color.lightGray;
        localizationResources = ResourceBundleWrapper.getBundle("org.jfree.chart.plot.LocalizationBundle");
    }

    public FastScatterPlot() {
        this((float[][]) null, new NumberAxis("X"), new NumberAxis("Y"));
    }

    public FastScatterPlot(float[][] data, ValueAxis domainAxis, ValueAxis rangeAxis) {
        ParamChecks.nullNotPermitted(domainAxis, "domainAxis");
        ParamChecks.nullNotPermitted(rangeAxis, "rangeAxis");
        this.data = data;
        this.xDataRange = calculateXDataRange(data);
        this.yDataRange = calculateYDataRange(data);
        this.domainAxis = domainAxis;
        this.domainAxis.setPlot(this);
        this.domainAxis.addChangeListener(this);
        this.rangeAxis = rangeAxis;
        this.rangeAxis.setPlot(this);
        this.rangeAxis.addChangeListener(this);
        this.paint = Color.red;
        this.domainGridlinesVisible = true;
        this.domainGridlinePaint = DEFAULT_GRIDLINE_PAINT;
        this.domainGridlineStroke = DEFAULT_GRIDLINE_STROKE;
        this.rangeGridlinesVisible = true;
        this.rangeGridlinePaint = DEFAULT_GRIDLINE_PAINT;
        this.rangeGridlineStroke = DEFAULT_GRIDLINE_STROKE;
    }

    public String getPlotType() {
        return localizationResources.getString("Fast_Scatter_Plot");
    }

    public float[][] getData() {
        return this.data;
    }

    public void setData(float[][] data) {
        this.data = data;
        fireChangeEvent();
    }

    public PlotOrientation getOrientation() {
        return PlotOrientation.VERTICAL;
    }

    public ValueAxis getDomainAxis() {
        return this.domainAxis;
    }

    public void setDomainAxis(ValueAxis axis) {
        ParamChecks.nullNotPermitted(axis, "axis");
        this.domainAxis = axis;
        fireChangeEvent();
    }

    public ValueAxis getRangeAxis() {
        return this.rangeAxis;
    }

    public void setRangeAxis(ValueAxis axis) {
        ParamChecks.nullNotPermitted(axis, "axis");
        this.rangeAxis = axis;
        fireChangeEvent();
    }

    public Paint getPaint() {
        return this.paint;
    }

    public void setPaint(Paint paint) {
        ParamChecks.nullNotPermitted(paint, "paint");
        this.paint = paint;
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

    public void draw(Graphics2D g2, Rectangle2D area, Point2D anchor, PlotState parentState, PlotRenderingInfo info) {
        if (info != null) {
            info.setPlotArea(area);
        }
        getInsets().trim(area);
        Rectangle2D rectangle2D = area;
        AxisSpace space = this.domainAxis.reserveSpace(g2, this, rectangle2D, RectangleEdge.BOTTOM, new AxisSpace());
        Rectangle2D dataArea = this.rangeAxis.reserveSpace(g2, this, area, RectangleEdge.LEFT, space).shrink(area, null);
        if (info != null) {
            info.setDataArea(dataArea);
        }
        drawBackground(g2, dataArea);
        AxisState domainAxisState = this.domainAxis.draw(g2, dataArea.getMaxY(), area, dataArea, RectangleEdge.BOTTOM, info);
        AxisState rangeAxisState = this.rangeAxis.draw(g2, dataArea.getMinX(), area, dataArea, RectangleEdge.LEFT, info);
        drawDomainGridlines(g2, dataArea, domainAxisState.getTicks());
        drawRangeGridlines(g2, dataArea, rangeAxisState.getTicks());
        Shape originalClip = g2.getClip();
        Composite originalComposite = g2.getComposite();
        g2.clip(dataArea);
        g2.setComposite(AlphaComposite.getInstance(3, getForegroundAlpha()));
        render(g2, dataArea, info, null);
        g2.setClip(originalClip);
        g2.setComposite(originalComposite);
        drawOutline(g2, dataArea);
    }

    public void render(Graphics2D g2, Rectangle2D dataArea, PlotRenderingInfo info, CrosshairState crosshairState) {
        g2.setPaint(this.paint);
        if (this.data != null) {
            for (int i = 0; i < this.data[0].length; i++) {
                g2.fillRect((int) this.domainAxis.valueToJava2D((double) this.data[0][i], dataArea, RectangleEdge.BOTTOM), (int) this.rangeAxis.valueToJava2D((double) this.data[1][i], dataArea, RectangleEdge.LEFT), 1, 1);
            }
        }
    }

    protected void drawDomainGridlines(Graphics2D g2, Rectangle2D dataArea, List ticks) {
        if (isDomainGridlinesVisible()) {
            Object saved = g2.getRenderingHint(RenderingHints.KEY_STROKE_CONTROL);
            g2.setRenderingHint(RenderingHints.KEY_STROKE_CONTROL, RenderingHints.VALUE_STROKE_NORMALIZE);
            for (ValueTick tick : ticks) {
                double v = this.domainAxis.valueToJava2D(tick.getValue(), dataArea, RectangleEdge.BOTTOM);
                Line2D line = new Double(v, dataArea.getMinY(), v, dataArea.getMaxY());
                g2.setPaint(getDomainGridlinePaint());
                g2.setStroke(getDomainGridlineStroke());
                g2.draw(line);
            }
            g2.setRenderingHint(RenderingHints.KEY_STROKE_CONTROL, saved);
        }
    }

    protected void drawRangeGridlines(Graphics2D g2, Rectangle2D dataArea, List ticks) {
        if (isRangeGridlinesVisible()) {
            Object saved = g2.getRenderingHint(RenderingHints.KEY_STROKE_CONTROL);
            g2.setRenderingHint(RenderingHints.KEY_STROKE_CONTROL, RenderingHints.VALUE_STROKE_NORMALIZE);
            for (ValueTick tick : ticks) {
                double v = this.rangeAxis.valueToJava2D(tick.getValue(), dataArea, RectangleEdge.LEFT);
                Line2D line = new Double(dataArea.getMinX(), v, dataArea.getMaxX(), v);
                g2.setPaint(getRangeGridlinePaint());
                g2.setStroke(getRangeGridlineStroke());
                g2.draw(line);
            }
            g2.setRenderingHint(RenderingHints.KEY_STROKE_CONTROL, saved);
        }
    }

    public Range getDataRange(ValueAxis axis) {
        if (axis == this.domainAxis) {
            return this.xDataRange;
        }
        if (axis == this.rangeAxis) {
            return this.yDataRange;
        }
        return null;
    }

    private Range calculateXDataRange(float[][] data) {
        if (data == null) {
            return null;
        }
        float lowest = Float.POSITIVE_INFINITY;
        float highest = Float.NEGATIVE_INFINITY;
        for (float v : data[0]) {
            if (v < lowest) {
                lowest = v;
            }
            if (v > highest) {
                highest = v;
            }
        }
        if (lowest <= highest) {
            return new Range((double) lowest, (double) highest);
        }
        return null;
    }

    private Range calculateYDataRange(float[][] data) {
        if (data == null) {
            return null;
        }
        float lowest = Float.POSITIVE_INFINITY;
        float highest = Float.NEGATIVE_INFINITY;
        for (int i = 0; i < data[0].length; i++) {
            float v = data[1][i];
            if (v < lowest) {
                lowest = v;
            }
            if (v > highest) {
                highest = v;
            }
        }
        if (lowest <= highest) {
            return new Range((double) lowest, (double) highest);
        }
        return null;
    }

    public void zoomDomainAxes(double factor, PlotRenderingInfo info, Point2D source) {
        this.domainAxis.resizeRange(factor);
    }

    public void zoomDomainAxes(double factor, PlotRenderingInfo info, Point2D source, boolean useAnchor) {
        if (useAnchor) {
            this.domainAxis.resizeRange2(factor, this.domainAxis.java2DToValue(source.getX(), info.getDataArea(), RectangleEdge.BOTTOM));
            return;
        }
        this.domainAxis.resizeRange(factor);
    }

    public void zoomDomainAxes(double lowerPercent, double upperPercent, PlotRenderingInfo info, Point2D source) {
        this.domainAxis.zoomRange(lowerPercent, upperPercent);
    }

    public void zoomRangeAxes(double factor, PlotRenderingInfo info, Point2D source) {
        this.rangeAxis.resizeRange(factor);
    }

    public void zoomRangeAxes(double factor, PlotRenderingInfo info, Point2D source, boolean useAnchor) {
        if (useAnchor) {
            this.rangeAxis.resizeRange2(factor, this.rangeAxis.java2DToValue(source.getY(), info.getDataArea(), RectangleEdge.LEFT));
            return;
        }
        this.rangeAxis.resizeRange(factor);
    }

    public void zoomRangeAxes(double lowerPercent, double upperPercent, PlotRenderingInfo info, Point2D source) {
        this.rangeAxis.zoomRange(lowerPercent, upperPercent);
    }

    public boolean isDomainZoomable() {
        return true;
    }

    public boolean isRangeZoomable() {
        return true;
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
        if (isDomainPannable() && this.domainAxis != null) {
            double adj = percent * this.domainAxis.getRange().getLength();
            if (this.domainAxis.isInverted()) {
                adj = -adj;
            }
            this.domainAxis.setRange(this.domainAxis.getLowerBound() + adj, this.domainAxis.getUpperBound() + adj);
        }
    }

    public void panRangeAxes(double percent, PlotRenderingInfo info, Point2D source) {
        if (isRangePannable() && this.rangeAxis != null) {
            double adj = percent * this.rangeAxis.getRange().getLength();
            if (this.rangeAxis.isInverted()) {
                adj = -adj;
            }
            this.rangeAxis.setRange(this.rangeAxis.getLowerBound() + adj, this.rangeAxis.getUpperBound() + adj);
        }
    }

    public boolean equals(Object obj) {
        if (obj == this) {
            return true;
        }
        if (!super.equals(obj) || !(obj instanceof FastScatterPlot)) {
            return false;
        }
        FastScatterPlot that = (FastScatterPlot) obj;
        if (this.domainPannable != that.domainPannable || this.rangePannable != that.rangePannable || !ArrayUtilities.equal(this.data, that.data) || !ObjectUtilities.equal(this.domainAxis, that.domainAxis) || !ObjectUtilities.equal(this.rangeAxis, that.rangeAxis) || !PaintUtilities.equal(this.paint, that.paint) || this.domainGridlinesVisible != that.domainGridlinesVisible || !PaintUtilities.equal(this.domainGridlinePaint, that.domainGridlinePaint) || !ObjectUtilities.equal(this.domainGridlineStroke, that.domainGridlineStroke)) {
            return false;
        }
        if ((!this.rangeGridlinesVisible) != that.rangeGridlinesVisible && PaintUtilities.equal(this.rangeGridlinePaint, that.rangeGridlinePaint) && ObjectUtilities.equal(this.rangeGridlineStroke, that.rangeGridlineStroke)) {
            return true;
        }
        return false;
    }

    public Object clone() throws CloneNotSupportedException {
        FastScatterPlot clone = (FastScatterPlot) super.clone();
        if (this.data != null) {
            clone.data = ArrayUtilities.clone(this.data);
        }
        if (this.domainAxis != null) {
            clone.domainAxis = (ValueAxis) this.domainAxis.clone();
            clone.domainAxis.setPlot(clone);
            clone.domainAxis.addChangeListener(clone);
        }
        if (this.rangeAxis != null) {
            clone.rangeAxis = (ValueAxis) this.rangeAxis.clone();
            clone.rangeAxis.setPlot(clone);
            clone.rangeAxis.addChangeListener(clone);
        }
        return clone;
    }

    private void writeObject(ObjectOutputStream stream) throws IOException {
        stream.defaultWriteObject();
        SerialUtilities.writePaint(this.paint, stream);
        SerialUtilities.writeStroke(this.domainGridlineStroke, stream);
        SerialUtilities.writePaint(this.domainGridlinePaint, stream);
        SerialUtilities.writeStroke(this.rangeGridlineStroke, stream);
        SerialUtilities.writePaint(this.rangeGridlinePaint, stream);
    }

    private void readObject(ObjectInputStream stream) throws IOException, ClassNotFoundException {
        stream.defaultReadObject();
        this.paint = SerialUtilities.readPaint(stream);
        this.domainGridlineStroke = SerialUtilities.readStroke(stream);
        this.domainGridlinePaint = SerialUtilities.readPaint(stream);
        this.rangeGridlineStroke = SerialUtilities.readStroke(stream);
        this.rangeGridlinePaint = SerialUtilities.readPaint(stream);
        if (this.domainAxis != null) {
            this.domainAxis.addChangeListener(this);
        }
        if (this.rangeAxis != null) {
            this.rangeAxis.addChangeListener(this);
        }
    }
}
