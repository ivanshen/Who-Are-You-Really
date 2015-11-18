package org.jfree.chart.renderer;

import java.awt.Graphics2D;
import java.awt.geom.Rectangle2D;
import java.util.List;
import org.jfree.chart.LegendItem;
import org.jfree.chart.axis.ValueAxis;
import org.jfree.chart.event.RendererChangeListener;
import org.jfree.chart.labels.XYToolTipGenerator;
import org.jfree.chart.plot.PlotRenderingInfo;
import org.jfree.chart.plot.PolarPlot;
import org.jfree.chart.urls.XYURLGenerator;
import org.jfree.data.xy.XYDataset;

public interface PolarItemRenderer {
    void addChangeListener(RendererChangeListener rendererChangeListener);

    void drawAngularGridLines(Graphics2D graphics2D, PolarPlot polarPlot, List list, Rectangle2D rectangle2D);

    void drawRadialGridLines(Graphics2D graphics2D, PolarPlot polarPlot, ValueAxis valueAxis, List list, Rectangle2D rectangle2D);

    void drawSeries(Graphics2D graphics2D, Rectangle2D rectangle2D, PlotRenderingInfo plotRenderingInfo, PolarPlot polarPlot, XYDataset xYDataset, int i);

    XYToolTipGenerator getBaseToolTipGenerator();

    LegendItem getLegendItem(int i);

    PolarPlot getPlot();

    XYToolTipGenerator getSeriesToolTipGenerator(int i);

    XYToolTipGenerator getToolTipGenerator(int i, int i2);

    XYURLGenerator getURLGenerator();

    void removeChangeListener(RendererChangeListener rendererChangeListener);

    void setBaseToolTipGenerator(XYToolTipGenerator xYToolTipGenerator);

    void setPlot(PolarPlot polarPlot);

    void setSeriesToolTipGenerator(int i, XYToolTipGenerator xYToolTipGenerator);

    void setURLGenerator(XYURLGenerator xYURLGenerator);
}
