package org.jfree.chart.plot;

import java.awt.geom.Point2D;

public interface Pannable {
    PlotOrientation getOrientation();

    boolean isDomainPannable();

    boolean isRangePannable();

    void panDomainAxes(double d, PlotRenderingInfo plotRenderingInfo, Point2D point2D);

    void panRangeAxes(double d, PlotRenderingInfo plotRenderingInfo, Point2D point2D);
}
