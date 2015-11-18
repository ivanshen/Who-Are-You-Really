package org.jfree.chart.plot;

import java.awt.Graphics2D;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.awt.geom.Rectangle2D.Double;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import org.jfree.chart.LegendItemCollection;
import org.jfree.chart.annotations.XYPointerAnnotation;
import org.jfree.chart.axis.AxisSpace;
import org.jfree.chart.axis.AxisState;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.axis.ValueAxis;
import org.jfree.chart.event.PlotChangeEvent;
import org.jfree.chart.event.PlotChangeListener;
import org.jfree.chart.renderer.xy.XYItemRenderer;
import org.jfree.chart.util.ParamChecks;
import org.jfree.chart.util.ShadowGenerator;
import org.jfree.data.Range;
import org.jfree.ui.RectangleEdge;
import org.jfree.ui.RectangleInsets;
import org.jfree.util.ObjectUtilities;

public class CombinedRangeXYPlot extends XYPlot implements PlotChangeListener {
    private static final long serialVersionUID = -5177814085082031168L;
    private double gap;
    private transient Rectangle2D[] subplotAreas;
    private List<XYPlot> subplots;

    public CombinedRangeXYPlot() {
        this(new NumberAxis());
    }

    public CombinedRangeXYPlot(ValueAxis rangeAxis) {
        super(null, null, rangeAxis, null);
        this.gap = XYPointerAnnotation.DEFAULT_ARROW_LENGTH;
        this.subplots = new ArrayList();
    }

    public String getPlotType() {
        return localizationResources.getString("Combined_Range_XYPlot");
    }

    public double getGap() {
        return this.gap;
    }

    public void setGap(double gap) {
        this.gap = gap;
    }

    public boolean isDomainPannable() {
        for (XYPlot subplot : this.subplots) {
            if (subplot.isDomainPannable()) {
                return true;
            }
        }
        return false;
    }

    public void setDomainPannable(boolean pannable) {
        for (XYPlot subplot : this.subplots) {
            subplot.setDomainPannable(pannable);
        }
    }

    public void add(XYPlot subplot) {
        add(subplot, 1);
    }

    public void add(XYPlot subplot, int weight) {
        ParamChecks.nullNotPermitted(subplot, "subplot");
        if (weight <= 0) {
            throw new IllegalArgumentException("The 'weight' must be positive.");
        }
        subplot.setParent(this);
        subplot.setWeight(weight);
        subplot.setInsets(new RectangleInsets(0.0d, 0.0d, 0.0d, 0.0d));
        subplot.setRangeAxis(null);
        subplot.addChangeListener(this);
        this.subplots.add(subplot);
        configureRangeAxes();
        fireChangeEvent();
    }

    public void remove(XYPlot subplot) {
        ParamChecks.nullNotPermitted(subplot, "subplot");
        int position = -1;
        int size = this.subplots.size();
        int i = 0;
        while (position == -1 && i < size) {
            if (this.subplots.get(i) == subplot) {
                position = i;
            }
            i++;
        }
        if (position != -1) {
            this.subplots.remove(position);
            subplot.setParent(null);
            subplot.removeChangeListener(this);
            configureRangeAxes();
            fireChangeEvent();
        }
    }

    public List getSubplots() {
        if (this.subplots != null) {
            return Collections.unmodifiableList(this.subplots);
        }
        return Collections.EMPTY_LIST;
    }

    protected AxisSpace calculateAxisSpace(Graphics2D g2, Rectangle2D plotArea) {
        int i;
        AxisSpace space = new AxisSpace();
        PlotOrientation orientation = getOrientation();
        AxisSpace fixed = getFixedRangeAxisSpace();
        if (fixed == null) {
            ValueAxis valueAxis = getRangeAxis();
            RectangleEdge valueEdge = Plot.resolveRangeAxisLocation(getRangeAxisLocation(), orientation);
            if (valueAxis != null) {
                space = valueAxis.reserveSpace(g2, this, plotArea, valueEdge, space);
            }
        } else if (orientation == PlotOrientation.VERTICAL) {
            space.setLeft(fixed.getLeft());
            space.setRight(fixed.getRight());
        } else if (orientation == PlotOrientation.HORIZONTAL) {
            space.setTop(fixed.getTop());
            space.setBottom(fixed.getBottom());
        }
        Rectangle2D adjustedPlotArea = space.shrink(plotArea, null);
        int n = this.subplots.size();
        int totalWeight = 0;
        for (i = 0; i < n; i++) {
            totalWeight += ((XYPlot) this.subplots.get(i)).getWeight();
        }
        this.subplotAreas = new Rectangle2D[n];
        double x = adjustedPlotArea.getX();
        double y = adjustedPlotArea.getY();
        double usableSize = 0.0d;
        if (orientation == PlotOrientation.VERTICAL) {
            usableSize = adjustedPlotArea.getWidth() - (this.gap * ((double) (n - 1)));
        } else if (orientation == PlotOrientation.HORIZONTAL) {
            usableSize = adjustedPlotArea.getHeight() - (this.gap * ((double) (n - 1)));
        }
        for (i = 0; i < n; i++) {
            XYPlot plot = (XYPlot) this.subplots.get(i);
            if (orientation == PlotOrientation.VERTICAL) {
                double w = (((double) plot.getWeight()) * usableSize) / ((double) totalWeight);
                this.subplotAreas[i] = new Double(x, y, w, adjustedPlotArea.getHeight());
                x = (x + w) + this.gap;
            } else if (orientation == PlotOrientation.HORIZONTAL) {
                double h = (((double) plot.getWeight()) * usableSize) / ((double) totalWeight);
                this.subplotAreas[i] = new Double(x, y, adjustedPlotArea.getWidth(), h);
                y = (y + h) + this.gap;
            }
            space.ensureAtLeast(plot.calculateDomainAxisSpace(g2, this.subplotAreas[i], null));
        }
        return space;
    }

    public void draw(Graphics2D g2, Rectangle2D area, Point2D anchor, PlotState parentState, PlotRenderingInfo info) {
        if (info != null) {
            info.setPlotArea(area);
        }
        getInsets().trim(area);
        AxisSpace space = calculateAxisSpace(g2, area);
        Rectangle2D dataArea = space.shrink(area, null);
        setFixedDomainAxisSpaceForSubplots(space);
        ValueAxis axis = getRangeAxis();
        RectangleEdge edge = getRangeAxisEdge();
        AxisState axisState = axis.draw(g2, RectangleEdge.coordinate(dataArea, edge), area, dataArea, edge, info);
        if (parentState == null) {
            parentState = new PlotState();
        }
        parentState.getSharedAxisStates().put(axis, axisState);
        for (int i = 0; i < this.subplots.size(); i++) {
            XYPlot plot = (XYPlot) this.subplots.get(i);
            PlotRenderingInfo subplotInfo = null;
            if (info != null) {
                subplotInfo = new PlotRenderingInfo(info.getOwner());
                info.addSubplotInfo(subplotInfo);
            }
            plot.draw(g2, this.subplotAreas[i], anchor, parentState, subplotInfo);
        }
        if (info != null) {
            info.setDataArea(dataArea);
        }
    }

    public LegendItemCollection getLegendItems() {
        LegendItemCollection result = getFixedLegendItems();
        if (result == null) {
            result = new LegendItemCollection();
            if (this.subplots != null) {
                for (XYPlot plot : this.subplots) {
                    result.addAll(plot.getLegendItems());
                }
            }
        }
        return result;
    }

    public void zoomDomainAxes(double factor, PlotRenderingInfo info, Point2D source) {
        zoomDomainAxes(factor, info, source, false);
    }

    public void zoomDomainAxes(double factor, PlotRenderingInfo info, Point2D source, boolean useAnchor) {
        XYPlot subplot = findSubplot(info, source);
        if (subplot != null) {
            subplot.zoomDomainAxes(factor, info, source, useAnchor);
            return;
        }
        for (XYPlot subplot2 : getSubplots()) {
            subplot2.zoomDomainAxes(factor, info, source, useAnchor);
        }
    }

    public void zoomDomainAxes(double lowerPercent, double upperPercent, PlotRenderingInfo info, Point2D source) {
        XYPlot subplot = findSubplot(info, source);
        if (subplot != null) {
            subplot.zoomDomainAxes(lowerPercent, upperPercent, info, source);
            return;
        }
        for (XYPlot subplot2 : getSubplots()) {
            subplot2.zoomDomainAxes(lowerPercent, upperPercent, info, source);
        }
    }

    public void panDomainAxes(double panRange, PlotRenderingInfo info, Point2D source) {
        XYPlot subplot = findSubplot(info, source);
        if (subplot != null && subplot.isDomainPannable() && info.getSubplotInfo(info.getSubplotIndex(source)) != null) {
            for (int i = 0; i < subplot.getDomainAxisCount(); i++) {
                ValueAxis domainAxis = subplot.getDomainAxis(i);
                if (domainAxis != null) {
                    domainAxis.pan(panRange);
                }
            }
        }
    }

    public XYPlot findSubplot(PlotRenderingInfo info, Point2D source) {
        ParamChecks.nullNotPermitted(info, "info");
        ParamChecks.nullNotPermitted(source, "source");
        int subplotIndex = info.getSubplotIndex(source);
        if (subplotIndex >= 0) {
            return (XYPlot) this.subplots.get(subplotIndex);
        }
        return null;
    }

    public void setRenderer(XYItemRenderer renderer) {
        super.setRenderer(renderer);
        for (XYPlot plot : this.subplots) {
            plot.setRenderer(renderer);
        }
    }

    public void setOrientation(PlotOrientation orientation) {
        super.setOrientation(orientation);
        for (XYPlot plot : this.subplots) {
            plot.setOrientation(orientation);
        }
    }

    public void setShadowGenerator(ShadowGenerator generator) {
        setNotify(false);
        super.setShadowGenerator(generator);
        for (XYPlot plot : this.subplots) {
            plot.setShadowGenerator(generator);
        }
        setNotify(true);
    }

    public Range getDataRange(ValueAxis axis) {
        Range result = null;
        if (this.subplots != null) {
            for (XYPlot subplot : this.subplots) {
                result = Range.combine(result, subplot.getDataRange(axis));
            }
        }
        return result;
    }

    protected void setFixedDomainAxisSpaceForSubplots(AxisSpace space) {
        for (XYPlot plot : this.subplots) {
            plot.setFixedDomainAxisSpace(space, false);
        }
    }

    public void handleClick(int x, int y, PlotRenderingInfo info) {
        if (info.getDataArea().contains((double) x, (double) y)) {
            for (int i = 0; i < this.subplots.size(); i++) {
                ((XYPlot) this.subplots.get(i)).handleClick(x, y, info.getSubplotInfo(i));
            }
        }
    }

    public void plotChanged(PlotChangeEvent event) {
        notifyListeners(event);
    }

    public boolean equals(Object obj) {
        if (obj == this) {
            return true;
        }
        if (!(obj instanceof CombinedRangeXYPlot)) {
            return false;
        }
        CombinedRangeXYPlot that = (CombinedRangeXYPlot) obj;
        if (this.gap == that.gap && ObjectUtilities.equal(this.subplots, that.subplots)) {
            return super.equals(obj);
        }
        return false;
    }

    public Object clone() throws CloneNotSupportedException {
        CombinedRangeXYPlot result = (CombinedRangeXYPlot) super.clone();
        result.subplots = (List) ObjectUtilities.deepClone(this.subplots);
        for (Plot child : result.subplots) {
            child.setParent(result);
        }
        ValueAxis rangeAxis = result.getRangeAxis();
        if (rangeAxis != null) {
            rangeAxis.configure();
        }
        return result;
    }
}
