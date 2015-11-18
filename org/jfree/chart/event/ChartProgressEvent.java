package org.jfree.chart.event;

import java.util.EventObject;
import org.jfree.chart.JFreeChart;

public class ChartProgressEvent extends EventObject {
    public static final int DRAWING_FINISHED = 2;
    public static final int DRAWING_STARTED = 1;
    private JFreeChart chart;
    private int percent;
    private int type;

    public ChartProgressEvent(Object source, JFreeChart chart, int type, int percent) {
        super(source);
        this.chart = chart;
        this.type = type;
        this.percent = percent;
    }

    public JFreeChart getChart() {
        return this.chart;
    }

    public void setChart(JFreeChart chart) {
        this.chart = chart;
    }

    public int getType() {
        return this.type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public int getPercent() {
        return this.percent;
    }

    public void setPercent(int percent) {
        this.percent = percent;
    }
}
