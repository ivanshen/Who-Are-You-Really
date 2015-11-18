package gui;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.data.category.CategoryDataset;
import org.jfree.data.category.DefaultCategoryDataset;

public class BarChart {
    public static ChartPanel createBarChart(double[] number, String[] name, String title, String domainLabel, String rangeLabel, String label) {
        ChartPanel panel = new ChartPanel(createChart(createDataset(name, number, label), title, domainLabel, rangeLabel));
        panel.setMouseZoomable(true);
        return panel;
    }

    public static CategoryDataset createDataset(String[] name, double[] number, String label) {
        DefaultCategoryDataset dataset = new DefaultCategoryDataset();
        for (int i = 0; i < name.length; i++) {
            dataset.addValue(number[i], name[i], (Comparable) "");
        }
        return dataset;
    }

    public static JFreeChart createChart(CategoryDataset dataset, String title, String dl, String rl) {
        return ChartFactory.createBarChart3D(title, dl, rl, dataset);
    }
}
