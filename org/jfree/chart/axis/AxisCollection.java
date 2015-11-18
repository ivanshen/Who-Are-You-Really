package org.jfree.chart.axis;

import java.util.ArrayList;
import java.util.List;
import org.jfree.chart.util.ParamChecks;
import org.jfree.ui.RectangleEdge;

public class AxisCollection {
    private List axesAtBottom;
    private List axesAtLeft;
    private List axesAtRight;
    private List axesAtTop;

    public AxisCollection() {
        this.axesAtTop = new ArrayList();
        this.axesAtBottom = new ArrayList();
        this.axesAtLeft = new ArrayList();
        this.axesAtRight = new ArrayList();
    }

    public List getAxesAtTop() {
        return this.axesAtTop;
    }

    public List getAxesAtBottom() {
        return this.axesAtBottom;
    }

    public List getAxesAtLeft() {
        return this.axesAtLeft;
    }

    public List getAxesAtRight() {
        return this.axesAtRight;
    }

    public void add(Axis axis, RectangleEdge edge) {
        ParamChecks.nullNotPermitted(axis, "axis");
        ParamChecks.nullNotPermitted(edge, "edge");
        if (edge == RectangleEdge.TOP) {
            this.axesAtTop.add(axis);
        } else if (edge == RectangleEdge.BOTTOM) {
            this.axesAtBottom.add(axis);
        } else if (edge == RectangleEdge.LEFT) {
            this.axesAtLeft.add(axis);
        } else if (edge == RectangleEdge.RIGHT) {
            this.axesAtRight.add(axis);
        }
    }
}
