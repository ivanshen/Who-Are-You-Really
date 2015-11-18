package org.jfree.chart.plot.dial;

import java.awt.Graphics2D;
import java.awt.Shape;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.awt.geom.Rectangle2D.Double;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.List;
import org.jfree.chart.axis.DateAxis;
import org.jfree.chart.plot.Plot;
import org.jfree.chart.plot.PlotRenderingInfo;
import org.jfree.chart.plot.PlotState;
import org.jfree.chart.util.ParamChecks;
import org.jfree.data.general.DatasetChangeEvent;
import org.jfree.data.general.ValueDataset;
import org.jfree.data.xy.NormalizedMatrixSeries;
import org.jfree.util.ObjectList;
import org.jfree.util.ObjectUtilities;

public class DialPlot extends Plot implements DialLayerChangeListener {
    private DialLayer background;
    private DialLayer cap;
    private ObjectList datasetToScaleMap;
    private ObjectList datasets;
    private DialFrame dialFrame;
    private List layers;
    private List pointers;
    private ObjectList scales;
    private double viewH;
    private double viewW;
    private double viewX;
    private double viewY;

    public DialPlot() {
        this(null);
    }

    public DialPlot(ValueDataset dataset) {
        this.background = null;
        this.cap = null;
        this.dialFrame = new ArcDialFrame();
        this.datasets = new ObjectList();
        if (dataset != null) {
            setDataset(dataset);
        }
        this.scales = new ObjectList();
        this.datasetToScaleMap = new ObjectList();
        this.layers = new ArrayList();
        this.pointers = new ArrayList();
        this.viewX = 0.0d;
        this.viewY = 0.0d;
        this.viewW = NormalizedMatrixSeries.DEFAULT_SCALE_FACTOR;
        this.viewH = NormalizedMatrixSeries.DEFAULT_SCALE_FACTOR;
    }

    public DialLayer getBackground() {
        return this.background;
    }

    public void setBackground(DialLayer background) {
        if (this.background != null) {
            this.background.removeChangeListener(this);
        }
        this.background = background;
        if (background != null) {
            background.addChangeListener(this);
        }
        fireChangeEvent();
    }

    public DialLayer getCap() {
        return this.cap;
    }

    public void setCap(DialLayer cap) {
        if (this.cap != null) {
            this.cap.removeChangeListener(this);
        }
        this.cap = cap;
        if (cap != null) {
            cap.addChangeListener(this);
        }
        fireChangeEvent();
    }

    public DialFrame getDialFrame() {
        return this.dialFrame;
    }

    public void setDialFrame(DialFrame frame) {
        ParamChecks.nullNotPermitted(frame, "frame");
        this.dialFrame.removeChangeListener(this);
        this.dialFrame = frame;
        frame.addChangeListener(this);
        fireChangeEvent();
    }

    public double getViewX() {
        return this.viewX;
    }

    public double getViewY() {
        return this.viewY;
    }

    public double getViewWidth() {
        return this.viewW;
    }

    public double getViewHeight() {
        return this.viewH;
    }

    public void setView(double x, double y, double w, double h) {
        this.viewX = x;
        this.viewY = y;
        this.viewW = w;
        this.viewH = h;
        fireChangeEvent();
    }

    public void addLayer(DialLayer layer) {
        ParamChecks.nullNotPermitted(layer, "layer");
        this.layers.add(layer);
        layer.addChangeListener(this);
        fireChangeEvent();
    }

    public int getLayerIndex(DialLayer layer) {
        ParamChecks.nullNotPermitted(layer, "layer");
        return this.layers.indexOf(layer);
    }

    public void removeLayer(int index) {
        DialLayer layer = (DialLayer) this.layers.get(index);
        if (layer != null) {
            layer.removeChangeListener(this);
        }
        this.layers.remove(index);
        fireChangeEvent();
    }

    public void removeLayer(DialLayer layer) {
        removeLayer(getLayerIndex(layer));
    }

    public void addPointer(DialPointer pointer) {
        ParamChecks.nullNotPermitted(pointer, "pointer");
        this.pointers.add(pointer);
        pointer.addChangeListener(this);
        fireChangeEvent();
    }

    public int getPointerIndex(DialPointer pointer) {
        ParamChecks.nullNotPermitted(pointer, "pointer");
        return this.pointers.indexOf(pointer);
    }

    public void removePointer(int index) {
        DialPointer pointer = (DialPointer) this.pointers.get(index);
        if (pointer != null) {
            pointer.removeChangeListener(this);
        }
        this.pointers.remove(index);
        fireChangeEvent();
    }

    public void removePointer(DialPointer pointer) {
        removeLayer(getPointerIndex(pointer));
    }

    public DialPointer getPointerForDataset(int datasetIndex) {
        for (DialPointer p : this.pointers) {
            if (p.getDatasetIndex() == datasetIndex) {
                return p;
            }
        }
        return null;
    }

    public ValueDataset getDataset() {
        return getDataset(0);
    }

    public ValueDataset getDataset(int index) {
        if (this.datasets.size() > index) {
            return (ValueDataset) this.datasets.get(index);
        }
        return null;
    }

    public void setDataset(ValueDataset dataset) {
        setDataset(0, dataset);
    }

    public void setDataset(int index, ValueDataset dataset) {
        ValueDataset existing = (ValueDataset) this.datasets.get(index);
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

    public void draw(Graphics2D g2, Rectangle2D area, Point2D anchor, PlotState parentState, PlotRenderingInfo info) {
        Shape origClip = g2.getClip();
        g2.setClip(area);
        Rectangle2D frame = viewToFrame(area);
        if (this.background != null && this.background.isVisible()) {
            if (this.background.isClippedToWindow()) {
                Shape savedClip = g2.getClip();
                g2.clip(this.dialFrame.getWindow(frame));
                this.background.draw(g2, this, frame, area);
                g2.setClip(savedClip);
            } else {
                this.background.draw(g2, this, frame, area);
            }
        }
        for (DialLayer current : this.layers) {
            if (current.isVisible()) {
                if (current.isClippedToWindow()) {
                    savedClip = g2.getClip();
                    g2.clip(this.dialFrame.getWindow(frame));
                    current.draw(g2, this, frame, area);
                    g2.setClip(savedClip);
                } else {
                    current.draw(g2, this, frame, area);
                }
            }
        }
        for (DialPointer current2 : this.pointers) {
            if (current2.isVisible()) {
                if (current2.isClippedToWindow()) {
                    savedClip = g2.getClip();
                    g2.clip(this.dialFrame.getWindow(frame));
                    current2.draw(g2, this, frame, area);
                    g2.setClip(savedClip);
                } else {
                    current2.draw(g2, this, frame, area);
                }
            }
        }
        if (this.cap != null && this.cap.isVisible()) {
            if (this.cap.isClippedToWindow()) {
                savedClip = g2.getClip();
                g2.clip(this.dialFrame.getWindow(frame));
                this.cap.draw(g2, this, frame, area);
                g2.setClip(savedClip);
            } else {
                this.cap.draw(g2, this, frame, area);
            }
        }
        if (this.dialFrame.isVisible()) {
            this.dialFrame.draw(g2, this, frame, area);
        }
        g2.setClip(origClip);
    }

    private Rectangle2D viewToFrame(Rectangle2D view) {
        double width = view.getWidth() / this.viewW;
        double height = view.getHeight() / this.viewH;
        return new Double(view.getX() - (this.viewX * width), view.getY() - (this.viewY * height), width, height);
    }

    public double getValue(int datasetIndex) {
        ValueDataset dataset = getDataset(datasetIndex);
        if (dataset == null) {
            return Double.NaN;
        }
        Number n = dataset.getValue();
        if (n != null) {
            return n.doubleValue();
        }
        return Double.NaN;
    }

    public void addScale(int index, DialScale scale) {
        ParamChecks.nullNotPermitted(scale, "scale");
        DialLayer existing = (DialScale) this.scales.get(index);
        if (existing != null) {
            removeLayer(existing);
        }
        this.layers.add(scale);
        this.scales.set(index, scale);
        scale.addChangeListener(this);
        fireChangeEvent();
    }

    public DialScale getScale(int index) {
        if (this.scales.size() > index) {
            return (DialScale) this.scales.get(index);
        }
        return null;
    }

    public void mapDatasetToScale(int index, int scaleIndex) {
        this.datasetToScaleMap.set(index, new Integer(scaleIndex));
        fireChangeEvent();
    }

    public DialScale getScaleForDataset(int datasetIndex) {
        DialScale result = (DialScale) this.scales.get(0);
        Integer scaleIndex = (Integer) this.datasetToScaleMap.get(datasetIndex);
        if (scaleIndex != null) {
            return getScale(scaleIndex.intValue());
        }
        return result;
    }

    public static Rectangle2D rectangleByRadius(Rectangle2D rect, double radiusW, double radiusH) {
        ParamChecks.nullNotPermitted(rect, "rect");
        double w = rect.getWidth() * radiusW;
        double h = rect.getHeight() * radiusH;
        return new Double(rect.getCenterX() - (w / DateAxis.DEFAULT_AUTO_RANGE_MINIMUM_SIZE_IN_MILLISECONDS), rect.getCenterY() - (h / DateAxis.DEFAULT_AUTO_RANGE_MINIMUM_SIZE_IN_MILLISECONDS), w, h);
    }

    public void dialLayerChanged(DialLayerChangeEvent event) {
        fireChangeEvent();
    }

    public boolean equals(Object obj) {
        if (obj == this) {
            return true;
        }
        if (!(obj instanceof DialPlot)) {
            return false;
        }
        DialPlot that = (DialPlot) obj;
        if (ObjectUtilities.equal(this.background, that.background) && ObjectUtilities.equal(this.cap, that.cap) && this.dialFrame.equals(that.dialFrame) && this.viewX == that.viewX && this.viewY == that.viewY && this.viewW == that.viewW && this.viewH == that.viewH && this.layers.equals(that.layers) && this.pointers.equals(that.pointers)) {
            return super.equals(obj);
        }
        return false;
    }

    public int hashCode() {
        int result = ((((ObjectUtilities.hashCode(this.background) + 7141) * 37) + ObjectUtilities.hashCode(this.cap)) * 37) + this.dialFrame.hashCode();
        long temp = Double.doubleToLongBits(this.viewX);
        result = (result * 37) + ((int) ((temp >>> 32) ^ temp));
        temp = Double.doubleToLongBits(this.viewY);
        result = (result * 37) + ((int) ((temp >>> 32) ^ temp));
        temp = Double.doubleToLongBits(this.viewW);
        result = (result * 37) + ((int) ((temp >>> 32) ^ temp));
        temp = Double.doubleToLongBits(this.viewH);
        return (result * 37) + ((int) ((temp >>> 32) ^ temp));
    }

    public String getPlotType() {
        return "DialPlot";
    }

    private void writeObject(ObjectOutputStream stream) throws IOException {
        stream.defaultWriteObject();
    }

    private void readObject(ObjectInputStream stream) throws IOException, ClassNotFoundException {
        stream.defaultReadObject();
    }
}
