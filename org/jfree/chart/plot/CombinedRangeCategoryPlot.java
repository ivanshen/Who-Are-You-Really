package org.jfree.chart.plot;

import java.awt.Graphics2D;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.awt.geom.Rectangle2D.Double;
import java.io.IOException;
import java.io.ObjectInputStream;
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
import org.jfree.chart.util.ParamChecks;
import org.jfree.chart.util.ShadowGenerator;
import org.jfree.data.Range;
import org.jfree.ui.RectangleEdge;
import org.jfree.ui.RectangleInsets;
import org.jfree.util.ObjectUtilities;

public class CombinedRangeCategoryPlot extends CategoryPlot implements PlotChangeListener {
    private static final long serialVersionUID = 7260210007554504515L;
    private double gap;
    private transient Rectangle2D[] subplotArea;
    private List subplots;

    public CombinedRangeCategoryPlot() {
        this(new NumberAxis());
    }

    public CombinedRangeCategoryPlot(ValueAxis rangeAxis) {
        super(null, null, rangeAxis, null);
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
        if (weight <= 0) {
            throw new IllegalArgumentException("Require weight >= 1.");
        }
        subplot.setParent(this);
        subplot.setWeight(weight);
        subplot.setInsets(new RectangleInsets(0.0d, 0.0d, 0.0d, 0.0d));
        subplot.setRangeAxis(null);
        subplot.setOrientation(getOrientation());
        subplot.addChangeListener(this);
        this.subplots.add(subplot);
        ValueAxis axis = getRangeAxis();
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
            ValueAxis range = getRangeAxis();
            if (range != null) {
                range.configure();
            }
            ValueAxis range2 = getRangeAxis(1);
            if (range2 != null) {
                range2.configure();
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
            totalWeight += ((CategoryPlot) this.subplots.get(i)).getWeight();
        }
        this.subplotArea = new Rectangle2D[n];
        double x = adjustedPlotArea.getX();
        double y = adjustedPlotArea.getY();
        double usableSize = 0.0d;
        if (orientation == PlotOrientation.VERTICAL) {
            usableSize = adjustedPlotArea.getWidth() - (this.gap * ((double) (n - 1)));
        } else if (orientation == PlotOrientation.HORIZONTAL) {
            usableSize = adjustedPlotArea.getHeight() - (this.gap * ((double) (n - 1)));
        }
        for (i = 0; i < n; i++) {
            CategoryPlot plot = (CategoryPlot) this.subplots.get(i);
            if (orientation == PlotOrientation.VERTICAL) {
                double w = (((double) plot.getWeight()) * usableSize) / ((double) totalWeight);
                this.subplotArea[i] = new Double(x, y, w, adjustedPlotArea.getHeight());
                x = (x + w) + this.gap;
            } else if (orientation == PlotOrientation.HORIZONTAL) {
                double h = (((double) plot.getWeight()) * usableSize) / ((double) totalWeight);
                this.subplotArea[i] = new Double(x, y, adjustedPlotArea.getWidth(), h);
                y = (y + h) + this.gap;
            }
            space.ensureAtLeast(plot.calculateDomainAxisSpace(g2, this.subplotArea[i], null));
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
        RectangleEdge rangeEdge = getRangeAxisEdge();
        AxisState state = axis.draw(g2, RectangleEdge.coordinate(dataArea, rangeEdge), area, dataArea, rangeEdge, info);
        if (parentState == null) {
            parentState = new PlotState();
        }
        parentState.getSharedAxisStates().put(axis, state);
        int i = 0;
        while (i < this.subplots.size()) {
            CategoryPlot plot = (CategoryPlot) this.subplots.get(i);
            PlotRenderingInfo subplotInfo = null;
            if (info != null) {
                subplotInfo = new PlotRenderingInfo(info.getOwner());
                info.addSubplotInfo(subplotInfo);
            }
            Point2D subAnchor = null;
            if (anchor != null && this.subplotArea[i].contains(anchor)) {
                subAnchor = anchor;
            }
            plot.draw(g2, this.subplotArea[i], subAnchor, parentState, subplotInfo);
            i++;
        }
        if (info != null) {
            info.setDataArea(dataArea);
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
        Range result = null;
        if (this.subplots != null) {
            for (CategoryPlot subplot : this.subplots) {
                result = Range.combine(result, subplot.getDataRange(axis));
            }
        }
        return result;
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

    protected void setFixedDomainAxisSpaceForSubplots(AxisSpace space) {
        for (CategoryPlot plot : this.subplots) {
            plot.setFixedDomainAxisSpace(space, false);
        }
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
        if (!(obj instanceof CombinedRangeCategoryPlot)) {
            return false;
        }
        CombinedRangeCategoryPlot that = (CombinedRangeCategoryPlot) obj;
        if (this.gap == that.gap && ObjectUtilities.equal(this.subplots, that.subplots)) {
            return super.equals(obj);
        }
        return false;
    }

    public Object clone() throws CloneNotSupportedException {
        CombinedRangeCategoryPlot result = (CombinedRangeCategoryPlot) super.clone();
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

    private void readObject(ObjectInputStream stream) throws IOException, ClassNotFoundException {
        stream.defaultReadObject();
        ValueAxis rangeAxis = getRangeAxis();
        if (rangeAxis != null) {
            rangeAxis.configure();
        }
    }
}
