package org.jfree.chart.demo;

import java.awt.Color;
import java.awt.Dimension;
import java.text.SimpleDateFormat;
import javax.swing.JPanel;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.StandardChartTheme;
import org.jfree.chart.annotations.XYPointerAnnotation;
import org.jfree.chart.axis.DateAxis;
import org.jfree.chart.axis.ValueAxis;
import org.jfree.chart.plot.MeterPlot;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.renderer.xy.XYItemRenderer;
import org.jfree.chart.renderer.xy.XYLineAndShapeRenderer;
import org.jfree.data.time.Month;
import org.jfree.data.time.TimeSeries;
import org.jfree.data.time.TimeSeriesCollection;
import org.jfree.data.xy.XYDataset;
import org.jfree.ui.ApplicationFrame;
import org.jfree.ui.RectangleInsets;
import org.jfree.ui.RefineryUtilities;

public class TimeSeriesChartDemo1 extends ApplicationFrame {
    private static final long serialVersionUID = 1;

    static {
        ChartFactory.setChartTheme(new StandardChartTheme("JFree/Shadow", true));
    }

    public TimeSeriesChartDemo1(String title) {
        super(title);
        ChartPanel chartPanel = (ChartPanel) createDemoPanel();
        chartPanel.setPreferredSize(new Dimension(ValueAxis.MAXIMUM_TICK_COUNT, MeterPlot.DEFAULT_METER_ANGLE));
        setContentPane(chartPanel);
    }

    private static JFreeChart createChart(XYDataset dataset) {
        JFreeChart chart = ChartFactory.createTimeSeriesChart("Legal & General Unit Trust Prices", "Date", "Price Per Unit", dataset, true, true, false);
        chart.setBackgroundPaint(Color.white);
        XYPlot plot = (XYPlot) chart.getPlot();
        plot.setBackgroundPaint(Color.lightGray);
        plot.setDomainGridlinePaint(Color.white);
        plot.setRangeGridlinePaint(Color.white);
        plot.setAxisOffset(new RectangleInsets(XYPointerAnnotation.DEFAULT_ARROW_LENGTH, XYPointerAnnotation.DEFAULT_ARROW_LENGTH, XYPointerAnnotation.DEFAULT_ARROW_LENGTH, XYPointerAnnotation.DEFAULT_ARROW_LENGTH));
        plot.setDomainCrosshairVisible(true);
        plot.setRangeCrosshairVisible(true);
        XYItemRenderer r = plot.getRenderer();
        if (r instanceof XYLineAndShapeRenderer) {
            XYLineAndShapeRenderer renderer = (XYLineAndShapeRenderer) r;
            renderer.setBaseShapesVisible(true);
            renderer.setBaseShapesFilled(true);
            renderer.setDrawSeriesLineAsPath(true);
        }
        ((DateAxis) plot.getDomainAxis()).setDateFormatOverride(new SimpleDateFormat("MMM-yyyy"));
        return chart;
    }

    private static XYDataset createDataset() {
        TimeSeries s1 = new TimeSeries("L&G European Index Trust");
        s1.add(new Month(2, 2001), 181.8d);
        s1.add(new Month(3, 2001), 167.3d);
        s1.add(new Month(4, 2001), 153.8d);
        s1.add(new Month(5, 2001), 167.6d);
        s1.add(new Month(6, 2001), 158.8d);
        s1.add(new Month(7, 2001), 148.3d);
        s1.add(new Month(8, 2001), 153.9d);
        s1.add(new Month(9, 2001), 142.7d);
        s1.add(new Month(10, 2001), 123.2d);
        s1.add(new Month(11, 2001), 131.8d);
        s1.add(new Month(12, 2001), 139.6d);
        s1.add(new Month(1, 2002), 142.9d);
        s1.add(new Month(2, 2002), 138.7d);
        s1.add(new Month(3, 2002), 137.3d);
        s1.add(new Month(4, 2002), 143.9d);
        s1.add(new Month(5, 2002), 139.8d);
        s1.add(new Month(6, 2002), 137.0d);
        s1.add(new Month(7, 2002), 132.8d);
        TimeSeries s2 = new TimeSeries("L&G UK Index Trust");
        s2.add(new Month(2, 2001), 129.6d);
        s2.add(new Month(3, 2001), 123.2d);
        s2.add(new Month(4, 2001), 117.2d);
        s2.add(new Month(5, 2001), 124.1d);
        s2.add(new Month(6, 2001), 122.6d);
        s2.add(new Month(7, 2001), 119.2d);
        s2.add(new Month(8, 2001), 116.5d);
        s2.add(new Month(9, 2001), 112.7d);
        s2.add(new Month(10, 2001), 101.5d);
        s2.add(new Month(11, 2001), 106.1d);
        s2.add(new Month(12, 2001), 110.3d);
        s2.add(new Month(1, 2002), 111.7d);
        s2.add(new Month(2, 2002), 111.0d);
        s2.add(new Month(3, 2002), 109.6d);
        s2.add(new Month(4, 2002), 113.2d);
        s2.add(new Month(5, 2002), 111.6d);
        s2.add(new Month(6, 2002), 108.8d);
        s2.add(new Month(7, 2002), 101.6d);
        TimeSeriesCollection dataset = new TimeSeriesCollection();
        dataset.addSeries(s1);
        dataset.addSeries(s2);
        return dataset;
    }

    public static JPanel createDemoPanel() {
        ChartPanel panel = new ChartPanel(createChart(createDataset()));
        panel.setFillZoomRectangle(true);
        panel.setMouseWheelEnabled(true);
        return panel;
    }

    public static void main(String[] args) {
        TimeSeriesChartDemo1 demo = new TimeSeriesChartDemo1("Time Series Chart Demo 1");
        demo.pack();
        RefineryUtilities.centerFrameOnScreen(demo);
        demo.setVisible(true);
    }
}
