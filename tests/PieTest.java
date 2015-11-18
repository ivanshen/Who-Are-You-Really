package tests;

import gui.PieChart;
import java.awt.Dimension;
import javax.swing.JFrame;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.annotations.XYPointerAnnotation;
import org.jfree.chart.axis.ValueAxis;
import org.jfree.chart.plot.MeterPlot;

public class PieTest extends JFrame {
    public static String[] x;
    public static double[] y;

    static {
        x = new String[]{"a", "b", "c", "d", "e", "f", "g", "h", "i", "j"};
        y = new double[10];
    }

    public PieTest() {
        ChartPanel p = PieChart.createPieChart("a", "b", x, y);
        p.setPreferredSize(new Dimension(ValueAxis.MAXIMUM_TICK_COUNT, MeterPlot.DEFAULT_METER_ANGLE));
        p.setVisible(true);
        setContentPane(p);
    }

    public static void main(String[] args) {
        for (int i = 0; i < 10; i++) {
            y[i] = (double) ((int) (Math.random() * XYPointerAnnotation.DEFAULT_TIP_RADIUS));
        }
        PieTest a = new PieTest();
        a.setVisible(true);
        a.pack();
        a.setDefaultCloseOperation(3);
    }
}
