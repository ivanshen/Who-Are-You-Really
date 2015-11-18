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
import org.jfree.chart.axis.CategoryAxis;
import org.jfree.chart.axis.ValueAxis;
import org.jfree.chart.event.PlotChangeEvent;
import org.jfree.chart.event.PlotChangeListener;
import org.jfree.chart.util.ParamChecks;
import org.jfree.chart.util.ShadowGenerator;
import org.jfree.data.Range;
import org.jfree.ui.RectangleEdge;
import org.jfree.ui.RectangleInsets;
import org.jfree.util.ObjectUtilities;

public class CombinedDomainCategoryPlot extends CategoryPlot implements PlotChangeListener {
    private static final long serialVersionUID = 8207194522653701572L;
    private double gap;
    private transient Rectangle2D[] subplotAreas;
    private List subplots;

    public CombinedDomainCategoryPlot() {
        this(new CategoryAxis());
    }

    public CombinedDomainCategoryPlot(CategoryAxis domainAxis) {
        super(null, domainAxis, null, null);
        this.subplots = new ArrayList();
        this.gap = XYPointerAnnotation.DEFAULT_ARROW_LENGTH;
    }

    public double getGap() {
        return this.gap;
    }

    public void setGap(double gap) {
        this.gap = gap;
        fireChangeEvent();
    }

    public void add(CategoryPlot subplot) {
        add(subplot, 1);
    }

    public void add(CategoryPlot subplot, int weight) {
        ParamChecks.nullNotPermitted(subplot, "subplot");
        if (weight < 1) {
            throw new IllegalArgumentException("Require weight >= 1.");
        }
        subplot.setParent(this);
        subplot.setWeight(weight);
        subplot.setInsets(new RectangleInsets(0.0d, 0.0d, 0.0d, 0.0d));
        subplot.setDomainAxis(null);
        subplot.setOrientation(getOrientation());
        subplot.addChangeListener(this);
        this.subplots.add(subplot);
        CategoryAxis axis = getDomainAxis();
        if (axis != null) {
            axis.configure();
        }
        fireChangeEvent();
    }

    public void remove(CategoryPlot subplot) {
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
            CategoryAxis domain = getDomainAxis();
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

    public CategoryPlot findSubplot(PlotRenderingInfo info, Point2D source) {
        ParamChecks.nullNotPermitted(info, "info");
        ParamChecks.nullNotPermitted(source, "source");
        int subplotIndex = info.getSubplotIndex(source);
        if (subplotIndex >= 0) {
            return (CategoryPlot) this.subplots.get(subplotIndex);
        }
        return null;
    }

    public void zoomRangeAxes(double factor, PlotRenderingInfo info, Point2D source) {
        zoomRangeAxes(factor, info, source, false);
    }

    public void zoomRangeAxes(double factor, PlotRenderingInfo info, Point2D source, boolean useAnchor) {
        CategoryPlot subplot = findSubplot(info, source);
        if (subplot != null) {
            subplot.zoomRangeAxes(factor, info, source, useAnchor);
            return;
        }
        for (CategoryPlot subplot2 : getSubplots()) {
            subplot2.zoomRangeAxes(factor, info, source, useAnchor);
        }
    }

    public void zoomRangeAxes(double lowerPercent, double upperPercent, PlotRenderingInfo info, Point2D source) {
        CategoryPlot subplot = findSubplot(info, source);
        if (subplot != null) {
            subplot.zoomRangeAxes(lowerPercent, upperPercent, info, source);
            return;
        }
        for (CategoryPlot subplot2 : getSubplots()) {
            subplot2.zoomRangeAxes(lowerPercent, upperPercent, info, source);
        }
    }

    protected AxisSpace calculateAxisSpace(Graphics2D g2, Rectangle2D plotArea) {
        int i;
        AxisSpace space = new AxisSpace();
        PlotOrientation orientation = getOrientation();
        AxisSpace fixed = getFixedDomainAxisSpace();
        if (fixed == null) {
            CategoryAxis categoryAxis = getDomainAxis();
            RectangleEdge categoryEdge = Plot.resolveDomainAxisLocation(getDomainAxisLocation(), orientation);
            if (categoryAxis != null) {
                space = categoryAxis.reserveSpace(g2, this, plotArea, categoryEdge, space);
            } else if (getDrawSharedDomainAxis()) {
                space = getDomainAxis().reserveSpace(g2, this, plotArea, categoryEdge, space);
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
            totalWeight += ((CategoryPlot) this.subplots.get(i)).getWeight();
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
            CategoryPlot plot = (CategoryPlot) this.subplots.get(i);
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
        RectangleInsets insets = getInsets();
        area.setRect(area.getX() + insets.getLeft(), area.getY() + insets.getTop(), (area.getWidth() - insets.getLeft()) - insets.getRight(), (area.getHeight() - insets.getTop()) - insets.getBottom());
        setFixedRangeAxisSpaceForSubplots(null);
        AxisSpace space = calculateAxisSpace(g2, area);
        Rectangle2D dataArea = space.shrink(area, null);
        setFixedRangeAxisSpaceForSubplots(space);
        CategoryAxis axis = getDomainAxis();
        RectangleEdge domainEdge = getDomainAxisEdge();
        AxisState axisState = axis.draw(g2, RectangleEdge.coordinate(dataArea, domainEdge), area, dataArea, domainEdge, info);
        if (parentState == null) {
            parentState = new PlotState();
        }
        parentState.getSharedAxisStates().put(axis, axisState);
        int i = 0;
        while (i < this.subplots.size()) {
            CategoryPlot plot = (CategoryPlot) this.subplots.get(i);
            PlotRenderingInfo subplotInfo = null;
            if (info != null) {
                subplotInfo = new PlotRenderingInfo(info.getOwner());
                info.addSubplotInfo(subplotInfo);
            }
            Point2D subAnchor = null;
            if (anchor != null && this.subplotAreas[i].contains(anchor)) {
                subAnchor = anchor;
            }
            plot.draw(g2, this.subplotAreas[i], subAnchor, parentState, subplotInfo);
            i++;
        }
        if (info != null) {
            info.setDataArea(dataArea);
        }
    }

    protected void setFixedRangeAxisSpaceForSubplots(AxisSpace space) {
        for (CategoryPlot plot : this.subplots) {
            plot.setFixedRangeAxisSpace(space, false);
        }
    }

    public void setOrientation(PlotOrientation orientation) {
        super.setOrientation(orientation);
        for (CategoryPlot plot : this.subplots) {
            plot.setOrientation(orientation);
        }
    }

    public void setShadowGenerator(ShadowGenerator generator) {
        setNotify(false);
        super.setShadowGenerator(generator);
        for (CategoryPlot plot : this.subplots) {
            plot.setShadowGenerator(generator);
        }
        setNotify(true);
    }

    public Range getDataRange(ValueAxis axis) {
        return super.getDataRange(axis);
    }

    public LegendItemCollection getLegendItems() {
        LegendItemCollection result = getFixedLegendItems();
        if (result == null) {
            result = new LegendItemCollection();
            if (this.subplots != null) {
                for (CategoryPlot plot : this.subplots) {
                    result.addAll(plot.getLegendItems());
                }
            }
        }
        return result;
    }

    public List getCategories() {
        List result = new ArrayList();
        if (this.subplots != null) {
            for (CategoryPlot plot : this.subplots) {
                for (Comparable category : plot.getCategories()) {
                    if (!result.contains(category)) {
                        result.add(category);
                    }
                }
            }
        }
        return Collections.unmodifiableList(result);
    }

    public List getCategoriesForAxis(CategoryAxis axis) {
        return getCategories();
    }

    public void handleClick(int x, int y, PlotRenderingInfo info) {
        if (info.getDataArea().contains((double) x, (double) y)) {
            for (int i = 0; i < this.subplots.size(); i++) {
                ((CategoryPlot) this.subplots.get(i)).handleClick(x, y, info.getSubplotInfo(i));
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
        if (!(obj instanceof CombinedDomainCategoryPlot)) {
            return false;
        }
        CombinedDomainCategoryPlot that = (CombinedDomainCategoryPlot) obj;
        if (this.gap == that.gap && ObjectUtilities.equal(this.subplots, that.subplots)) {
            return super.equals(obj);
        }
        return false;
    }

    public Object clone() throws CloneNotSupportedException {
        CombinedDomainCategoryPlot result = (CombinedDomainCategoryPlot) super.clone();
        result.subplots = (List) ObjectUtilities.deepClone(this.subplots);
        for (Plot child : result.subplots) {
            child.setParent(result);
        }
        return result;
    }
}
