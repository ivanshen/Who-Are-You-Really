package gui;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.PiePlot3D;
import org.jfree.data.general.DefaultPieDataset;
import org.jfree.data.general.PieDataset;
import org.jfree.util.Rotation;

public class PieChart {
    public static ChartPanel createPieChart(String applicationTitle, String chartTitle, String[] name, double[] number) {
        ChartPanel panel = new ChartPanel(createChart(createDataset(name, number), chartTitle));
        panel.setMouseWheelEnabled(true);
        return panel;
    }

    private static PieDataset createDataset(String[] name, double[] number) {
        DefaultPieDataset result = new DefaultPieDataset();
        for (int i = 0; i < name.length; i++) {
            result.setValue(name[i], number[i]);
        }
        return result;
    }

    private static JFreeChart createChart(PieDataset dataset, String title) {
        JFreeChart chart = ChartFactory.createPieChart3D(title, dataset, true, true, false);
        PiePlot3D plot = (PiePlot3D) chart.getPlot();
        plot.setStartAngle(290.0d);
        plot.setDirection(Rotation.CLOCKWISE);
        plot.setForegroundAlpha(JFreeChart.DEFAULT_BACKGROUND_IMAGE_ALPHA);
        return chart;
    }
}
