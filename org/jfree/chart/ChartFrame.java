package org.jfree.chart;

import javax.swing.JFrame;
import javax.swing.JScrollPane;

public class ChartFrame extends JFrame {
    private ChartPanel chartPanel;

    public ChartFrame(String title, JFreeChart chart) {
        this(title, chart, false);
    }

    public ChartFrame(String title, JFreeChart chart, boolean scrollPane) {
        super(title);
        setDefaultCloseOperation(2);
        this.chartPanel = new ChartPanel(chart);
        if (scrollPane) {
            setContentPane(new JScrollPane(this.chartPanel));
        } else {
            setContentPane(this.chartPanel);
        }
    }

    public ChartPanel getChartPanel() {
        return this.chartPanel;
    }
}
