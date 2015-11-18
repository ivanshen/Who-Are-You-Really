package tests;

import gui.BarChart;
import java.awt.Dimension;
import javax.swing.JFrame;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.annotations.XYPointerAnnotation;
import org.jfree.chart.axis.ValueAxis;
import org.jfree.chart.plot.MeterPlot;
import org.jfree.chart.plot.SpiderWebPlot;

public class BarTest extends JFrame {
    public static String[] x;
    public static double[] y;

    static {
        x = new String[]{"a", "b", "c", "d", "e", "f", "g", "h", "i", "j"};
        y = new double[10];
    }

    public BarTest() {
        ChartPanel p = BarChart.createBarChart(y, x, "a", "b", "c", "asdfasdfaaf");
        p.setPreferredSize(new Dimension(ValueAxis.MAXIMUM_TICK_COUNT, MeterPlot.DEFAULT_METER_ANGLE));
        p.setVisible(true);
        setContentPane(p);
    }

    public static void main(String[] args) {
        for (int i = 0; i < 10; i++) {
            y[i] = (Math.random() * XYPointerAnnotation.DEFAULT_TIP_RADIUS) * Math.pow(SpiderWebPlot.DEFAULT_MAX_VALUE, (double) (((int) Math.random()) * 2));
        }
        BarTest a = new BarTest();
        a.setVisible(true);
        a.pack();
        a.setDefaultCloseOperation(3);
    }
}
