package org.jfree.chart.axis;

import java.util.ArrayList;
import java.util.List;
import org.jfree.ui.RectangleEdge;

public class AxisState {
    private double cursor;
    private double max;
    private List ticks;

    public AxisState() {
        this(0.0d);
    }

    public AxisState(double cursor) {
        this.cursor = cursor;
        this.ticks = new ArrayList();
    }

    public double getCursor() {
        return this.cursor;
    }

    public void setCursor(double cursor) {
        this.cursor = cursor;
    }

    public void moveCursor(double units, RectangleEdge edge) {
        if (edge == RectangleEdge.TOP) {
            cursorUp(units);
        } else if (edge == RectangleEdge.BOTTOM) {
            cursorDown(units);
        } else if (edge == RectangleEdge.LEFT) {
            cursorLeft(units);
        } else if (edge == RectangleEdge.RIGHT) {
            cursorRight(units);
        }
    }

    public void cursorUp(double units) {
        this.cursor -= units;
    }

    public void cursorDown(double units) {
        this.cursor += units;
    }

    public void cursorLeft(double units) {
        this.cursor -= units;
    }

    public void cursorRight(double units) {
        this.cursor += units;
    }

    public List getTicks() {
        return this.ticks;
    }

    public void setTicks(List ticks) {
        this.ticks = ticks;
    }

    public double getMax() {
        return this.max;
    }

    public void setMax(double max) {
        this.max = max;
    }
}
