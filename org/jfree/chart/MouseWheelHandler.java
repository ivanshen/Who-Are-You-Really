package org.jfree.chart;

import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseWheelListener;
import java.awt.geom.Point2D;
import java.io.Serializable;
import org.jfree.chart.plot.PiePlot;
import org.jfree.chart.plot.Plot;
import org.jfree.chart.plot.PlotRenderingInfo;
import org.jfree.chart.plot.SpiderWebPlot;
import org.jfree.chart.plot.Zoomable;
import org.jfree.data.xy.NormalizedMatrixSeries;

class MouseWheelHandler implements MouseWheelListener, Serializable {
    private ChartPanel chartPanel;
    double zoomFactor;

    public MouseWheelHandler(ChartPanel chartPanel) {
        this.chartPanel = chartPanel;
        this.zoomFactor = SpiderWebPlot.DEFAULT_AXIS_LABEL_GAP;
        this.chartPanel.addMouseWheelListener(this);
    }

    public double getZoomFactor() {
        return this.zoomFactor;
    }

    public void setZoomFactor(double zoomFactor) {
        this.zoomFactor = zoomFactor;
    }

    public void mouseWheelMoved(MouseWheelEvent e) {
        JFreeChart chart = this.chartPanel.getChart();
        if (chart != null) {
            Plot plot = chart.getPlot();
            if (plot instanceof Zoomable) {
                handleZoomable((Zoomable) plot, e);
            } else if (plot instanceof PiePlot) {
                ((PiePlot) plot).handleMouseWheelRotation(e.getWheelRotation());
            }
        }
    }

    private void handleZoomable(Zoomable zoomable, MouseWheelEvent e) {
        PlotRenderingInfo pinfo = this.chartPanel.getChartRenderingInfo().getPlotInfo();
        Point2D p = this.chartPanel.translateScreenToJava2D(e.getPoint());
        if (pinfo.getDataArea().contains(p)) {
            Plot plot = (Plot) zoomable;
            boolean notifyState = plot.isNotify();
            plot.setNotify(false);
            double zf = NormalizedMatrixSeries.DEFAULT_SCALE_FACTOR + this.zoomFactor;
            if (e.getWheelRotation() < 0) {
                zf = NormalizedMatrixSeries.DEFAULT_SCALE_FACTOR / zf;
            }
            if (this.chartPanel.isDomainZoomable()) {
                zoomable.zoomDomainAxes(zf, pinfo, p, true);
            }
            if (this.chartPanel.isRangeZoomable()) {
                zoomable.zoomRangeAxes(zf, pinfo, p, true);
            }
            plot.setNotify(notifyState);
        }
    }
}
