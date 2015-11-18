package org.jfree.chart.demo;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GradientPaint;
import java.awt.Point;
import java.awt.RadialGradientPaint;
import java.awt.geom.Point2D.Float;
import javax.swing.JPanel;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.StandardChartTheme;
import org.jfree.chart.axis.Axis;
import org.jfree.chart.axis.DateAxis;
import org.jfree.chart.plot.PiePlot;
import org.jfree.chart.plot.Plot;
import org.jfree.chart.renderer.xy.XYLine3DRenderer;
import org.jfree.chart.title.TextTitle;
import org.jfree.data.general.DefaultPieDataset;
import org.jfree.data.general.PieDataset;
import org.jfree.ui.ApplicationFrame;
import org.jfree.ui.HorizontalAlignment;
import org.jfree.ui.RectangleEdge;
import org.jfree.ui.RectangleInsets;
import org.jfree.ui.RefineryUtilities;

public class PieChartDemo1 extends ApplicationFrame {
    private static final long serialVersionUID = 1;

    static {
        ChartFactory.setChartTheme(new StandardChartTheme("JFree/Shadow", true));
    }

    public PieChartDemo1(String title) {
        super(title);
        setContentPane(createDemoPanel());
    }

    private static PieDataset createDataset() {
        DefaultPieDataset dataset = new DefaultPieDataset();
        dataset.setValue((Comparable) "Samsung", new Double(27.8d));
        dataset.setValue((Comparable) "Others", new Double(55.3d));
        dataset.setValue((Comparable) "Nokia", new Double(16.8d));
        dataset.setValue((Comparable) "Apple", new Double(17.1d));
        return dataset;
    }

    private static JFreeChart createChart(PieDataset dataset) {
        JFreeChart chart = ChartFactory.createPieChart("Smart Phones Manufactured / Q3 2011", dataset, false, true, false);
        chart.setBackgroundPaint(new GradientPaint(new Point(0, 0), new Color(20, 20, 20), new Point(400, ChartPanel.DEFAULT_MINIMUM_DRAW_HEIGHT), Color.DARK_GRAY));
        TextTitle t = chart.getTitle();
        t.setHorizontalAlignment(HorizontalAlignment.LEFT);
        t.setPaint(new Color(240, 240, 240));
        t.setFont(new Font("Arial", 1, 26));
        PiePlot plot = (PiePlot) chart.getPlot();
        plot.setBackgroundPaint(null);
        plot.setInteriorGap(0.04d);
        plot.setOutlineVisible(false);
        plot.setSectionPaint((Comparable) "Others", createGradientPaint(new Color(ChartPanel.DEFAULT_MINIMUM_DRAW_HEIGHT, ChartPanel.DEFAULT_MINIMUM_DRAW_HEIGHT, 255), Color.BLUE));
        plot.setSectionPaint((Comparable) "Samsung", createGradientPaint(new Color(255, ChartPanel.DEFAULT_MINIMUM_DRAW_HEIGHT, ChartPanel.DEFAULT_MINIMUM_DRAW_HEIGHT), Color.RED));
        plot.setSectionPaint((Comparable) "Apple", createGradientPaint(new Color(ChartPanel.DEFAULT_MINIMUM_DRAW_HEIGHT, 255, ChartPanel.DEFAULT_MINIMUM_DRAW_HEIGHT), Color.GREEN));
        plot.setSectionPaint((Comparable) "Nokia", createGradientPaint(new Color(ChartPanel.DEFAULT_MINIMUM_DRAW_HEIGHT, 255, ChartPanel.DEFAULT_MINIMUM_DRAW_HEIGHT), Color.YELLOW));
        plot.setBaseSectionOutlinePaint(Color.WHITE);
        plot.setSectionOutlinesVisible(true);
        plot.setBaseSectionOutlineStroke(new BasicStroke(Axis.DEFAULT_TICK_MARK_OUTSIDE_LENGTH));
        plot.setLabelFont(new Font("Courier New", 1, 20));
        plot.setLabelLinkPaint(Color.WHITE);
        plot.setLabelLinkStroke(new BasicStroke(Axis.DEFAULT_TICK_MARK_OUTSIDE_LENGTH));
        plot.setLabelOutlineStroke(null);
        plot.setLabelPaint(Color.WHITE);
        plot.setLabelBackgroundPaint(null);
        TextTitle source = new TextTitle("Source: http://www.bbc.co.uk/news/business-15489523", new Font("Courier New", 0, 12));
        source.setPaint(Color.WHITE);
        source.setPosition(RectangleEdge.BOTTOM);
        source.setHorizontalAlignment(HorizontalAlignment.RIGHT);
        chart.addSubtitle(source);
        return chart;
    }

    private static RadialGradientPaint createGradientPaint(Color c1, Color c2) {
        return new RadialGradientPaint(new Float(0.0f, 0.0f), 200.0f, new float[]{0.0f, Plot.DEFAULT_FOREGROUND_ALPHA}, new Color[]{c1, c2});
    }

    public static JPanel createDemoPanel() {
        JFreeChart chart = createChart(createDataset());
        chart.setPadding(new RectangleInsets(4.0d, XYLine3DRenderer.DEFAULT_Y_OFFSET, DateAxis.DEFAULT_AUTO_RANGE_MINIMUM_SIZE_IN_MILLISECONDS, DateAxis.DEFAULT_AUTO_RANGE_MINIMUM_SIZE_IN_MILLISECONDS));
        ChartPanel panel = new ChartPanel(chart);
        panel.setMouseWheelEnabled(true);
        panel.setPreferredSize(new Dimension(600, ChartPanel.DEFAULT_MINIMUM_DRAW_WIDTH));
        return panel;
    }

    public static void main(String[] args) {
        PieChartDemo1 demo = new PieChartDemo1("JFreeChart: Pie Chart Demo 1");
        demo.pack();
        RefineryUtilities.centerFrameOnScreen(demo);
        demo.setVisible(true);
    }
}
