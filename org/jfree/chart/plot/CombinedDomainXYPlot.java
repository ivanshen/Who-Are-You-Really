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

public class CombinedDomainXYPlot extends XYPlot implements PlotChangeListener {
    private static final long serialVersionUID = -7765545541261907383L;
    private double gap;
    private transient Rectangle2D[] subplotAreas;
    private List<XYPlot> subplots;

    public CombinedDomainXYPlot() {
        this(new NumberAxis());
    }

    public CombinedDomainXYPlot(ValueAxis domainAxis) {
        super(null, domainAxis, null, null);
        this.gap = XYPointerAnnotation.DEFAULT_ARROW_LENGTH;
        this.subplots = new ArrayList();
    }

    public String getPlotType() {
        return "Combined_Domain_XYPlot";
    }

    public double getGap() {
        return this.gap;
    }

    public void setGap(double gap) {
        this.gap = gap;
        fireChangeEvent();
    }

    public boolean isRangePannable() {
        for (XYPlot subplot : this.subplots) {
            if (subplot.isRangePannable()) {
                return true;
            }
        }
        return false;
    }

    public void setRangePannable(boolean pannable) {
        for (XYPlot subplot : this.subplots) {
            subplot.setRangePannable(pannable);
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

    public void add(XYPlot subplot) {
        add(subplot, 1);
    }

    public void add(XYPlot subplot, int weight) {
        ParamChecks.nullNotPermitted(subplot, "subplot");
        if (weight <= 0) {
            throw new IllegalArgumentException("Require weight >= 1.");
        }
        subplot.setParent(this);
        subplot.setWeight(weight);
        subplot.setInsets(RectangleInsets.ZERO_INSETS, false);
        subplot.setDomainAxis(null);
        subplot.addChangeListener(this);
        this.subplots.add(subplot);
        ValueAxis axis = getDomainAxis();
        if (axis != null) {
            axis.configure();
        }
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
            ValueAxis domain = getDomainAxis();
            if (domain != null) {
                domain.configure();
            }
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
        AxisSpace fixed = getFixedDomainAxisSpace();
        if (fixed == null) {
            ValueAxis xAxis = getDomainAxis();
            RectangleEdge xEdge = Plot.resolveDomainAxisLocation(getDomainAxisLocation(), orientation);
            if (xAxis != null) {
                space = xAxis.reserveSpace(g2, this, plotArea, xEdge, space);
            }
        } else if (orientation == PlotOrientation.HORIZONTAL) {
            space.setLeft(fixed.getLeft());
            space.setRight(fixed.getRight());
        } else if (orientation == PlotOrientation.VERTICAL) {
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
        if (orientation == PlotOrientation.HORIZONTAL) {
            usableSize = adjustedPlotArea.getWidth() - (this.gap * ((double) (n - 1)));
        } else if (orientation == PlotOrientation.VERTICAL) {
            usableSize = adjustedPlotArea.getHeight() - (this.gap * ((double) (n - 1)));
        }
        for (i = 0; i < n; i++) {
            XYPlot plot = (XYPlot) this.subplots.get(i);
            if (orientation == PlotOrientation.HORIZONTAL) {
                double w = (((double) plot.getWeight()) * usableSize) / ((double) totalWeight);
                this.subplotAreas[i] = new Double(x, y, w, adjustedPlotArea.getHeight());
                x = (x + w) + this.gap;
            } else if (orientation == PlotOrientation.VERTICAL) {
                double h = (((double) plot.getWeight()) * usableSize) / ((double) totalWeight);
                this.subplotAreas[i] = new Double(x, y, adjustedPlotArea.getWidth(), h);
                y = (y + h) + this.gap;
            }
            space.ensureAtLeast(plot.calculateRangeAxisSpace(g2, this.subplotAreas[i], null));
        }
        return space;
    }

    public void draw(Graphics2D g2, Rectangle2D area, Point2D anchor, PlotState parentState, PlotRenderingInfo info) {
        if (info != null) {
            info.setPlotArea(area);
        }
        getInsets().trim(area);
        setFixedRangeAxisSpaceForSubplots(null);
        AxisSpace space = calculateAxisSpace(g2, area);
        Rectangle2D dataArea = space.shrink(area, null);
        setFixedRangeAxisSpaceForSubplots(space);
        ValueAxis axis = getDomainAxis();
        RectangleEdge edge = getDomainAxisEdge();
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

    public void zoomRangeAxes(double factor, PlotRenderingInfo info, Point2D source) {
        zoomRangeAxes(factor, info, source, false);
    }

    public void zoomRangeAxes(double factor, PlotRenderingInfo state, Point2D source, boolean useAnchor) {
        XYPlot subplot = findSubplot(state, source);
        if (subplot != null) {
            subplot.zoomRangeAxes(factor, state, source, useAnchor);
            return;
        }
        for (XYPlot subplot2 : getSubplots()) {
            subplot2.zoomRangeAxes(factor, state, source, useAnchor);
        }
    }

    public void zoomRangeAxes(double lowerPercent, double upperPercent, PlotRenderingInfo info, Point2D source) {
        XYPlot subplot = findSubplot(info, source);
        if (subplot != null) {
            subplot.zoomRangeAxes(lowerPercent, upperPercent, info, source);
            return;
        }
        for (XYPlot subplot2 : getSubplots()) {
            subplot2.zoomRangeAxes(lowerPercent, upperPercent, info, source);
        }
    }

    public void panRangeAxes(double panRange, PlotRenderingInfo info, Point2D source) {
        XYPlot subplot = findSubplot(info, source);
        if (subplot != null && subplot.isRangePannable() && info.getSubplotInfo(info.getSubplotIndex(source)) != null) {
            for (int i = 0; i < subplot.getRangeAxisCount(); i++) {
                ValueAxis rangeAxis = subplot.getRangeAxis(i);
                if (rangeAxis != null) {
                    rangeAxis.pan(panRange);
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

    public void setFixedRangeAxisSpace(AxisSpace space) {
        super.setFixedRangeAxisSpace(space);
        setFixedRangeAxisSpaceForSubplots(space);
        fireChangeEvent();
    }

    protected void setFixedRangeAxisSpaceForSubplots(AxisSpace space) {
        for (XYPlot plot : this.subplots) {
            plot.setFixedRangeAxisSpace(space, false);
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
        if (!(obj instanceof CombinedDomainXYPlot)) {
            return false;
        }
        CombinedDomainXYPlot that = (CombinedDomainXYPlot) obj;
        if (this.gap == that.gap && ObjectUtilities.equal(this.subplots, that.subplots)) {
            return super.equals(obj);
        }
        return false;
    }

    public Object clone() throws CloneNotSupportedException {
        CombinedDomainXYPlot result = (CombinedDomainXYPlot) super.clone();
        result.subplots = (List) ObjectUtilities.deepClone(this.subplots);
        for (Plot child : result.subplots) {
            child.setParent(result);
        }
        ValueAxis domainAxis = result.getDomainAxis();
        if (domainAxis != null) {
            domainAxis.configure();
        }
        return result;
    }
}
