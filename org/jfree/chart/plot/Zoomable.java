package org.jfree.chart.plot;

import java.awt.geom.Point2D;

public interface Zoomable {
    PlotOrientation getOrientation();

    boolean isDomainZoomable();

    boolean isRangeZoomable();

    void zoomDomainAxes(double d, double d2, PlotRenderingInfo plotRenderingInfo, Point2D point2D);

    void zoomDomainAxes(double d, PlotRenderingInfo plotRenderingInfo, Point2D point2D);

    void zoomDomainAxes(double d, PlotRenderingInfo plotRenderingInfo, Point2D point2D, boolean z);

    void zoomRangeAxes(double d, double d2, PlotRenderingInfo plotRenderingInfo, Point2D point2D);

    void zoomRangeAxes(double d, PlotRenderingInfo plotRenderingInfo, Point2D point2D);

    void zoomRangeAxes(double d, PlotRenderingInfo plotRenderingInfo, Point2D point2D, boolean z);
}
