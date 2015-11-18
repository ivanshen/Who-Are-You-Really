package org.jfree.chart.renderer.xy;

import java.awt.Graphics2D;
import java.awt.Paint;
import java.awt.Stroke;
import java.awt.geom.Line2D;
import java.awt.geom.Rectangle2D;
import java.io.Serializable;
import org.jfree.chart.HashUtilities;
import org.jfree.chart.axis.ValueAxis;
import org.jfree.chart.entity.EntityCollection;
import org.jfree.chart.labels.XYToolTipGenerator;
import org.jfree.chart.plot.CrosshairState;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.plot.PlotRenderingInfo;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.urls.XYURLGenerator;
import org.jfree.data.xy.NormalizedMatrixSeries;
import org.jfree.data.xy.XYDataset;
import org.jfree.ui.RectangleEdge;
import org.jfree.util.PublicCloneable;

public class XYStepRenderer extends XYLineAndShapeRenderer implements XYItemRenderer, Cloneable, PublicCloneable, Serializable {
    private static final long serialVersionUID = -8918141928884796108L;
    private double stepPoint;

    public XYStepRenderer() {
        this(null, null);
    }

    public XYStepRenderer(XYToolTipGenerator toolTipGenerator, XYURLGenerator urlGenerator) {
        this.stepPoint = NormalizedMatrixSeries.DEFAULT_SCALE_FACTOR;
        setBaseToolTipGenerator(toolTipGenerator);
        setURLGenerator(urlGenerator);
        setBaseShapesVisible(false);
    }

    public double getStepPoint() {
        return this.stepPoint;
    }

    public void setStepPoint(double stepPoint) {
        if (stepPoint < 0.0d || stepPoint > NormalizedMatrixSeries.DEFAULT_SCALE_FACTOR) {
            throw new IllegalArgumentException("Requires stepPoint in [0.0;1.0]");
        }
        this.stepPoint = stepPoint;
        fireChangeEvent();
    }

    public void drawItem(Graphics2D g2, XYItemRendererState state, Rectangle2D dataArea, PlotRenderingInfo info, XYPlot plot, ValueAxis domainAxis, ValueAxis rangeAxis, XYDataset dataset, int series, int item, CrosshairState crosshairState, int pass) {
        if (getItemVisible(series, item)) {
            double transY1;
            PlotOrientation orientation = plot.getOrientation();
            Paint seriesPaint = getItemPaint(series, item);
            Stroke seriesStroke = getItemStroke(series, item);
            g2.setPaint(seriesPaint);
            g2.setStroke(seriesStroke);
            double x1 = dataset.getXValue(series, item);
            double y1 = dataset.getYValue(series, item);
            RectangleEdge xAxisLocation = plot.getDomainAxisEdge();
            RectangleEdge yAxisLocation = plot.getRangeAxisEdge();
            double transX1 = domainAxis.valueToJava2D(x1, dataArea, xAxisLocation);
            if (Double.isNaN(y1)) {
                transY1 = Double.NaN;
            } else {
                transY1 = rangeAxis.valueToJava2D(y1, dataArea, yAxisLocation);
            }
            if (pass == 0 && item > 0) {
                double transY0;
                double x0 = dataset.getXValue(series, item - 1);
                double y0 = dataset.getYValue(series, item - 1);
                double transX0 = domainAxis.valueToJava2D(x0, dataArea, xAxisLocation);
                if (Double.isNaN(y0)) {
                    transY0 = Double.NaN;
                } else {
                    transY0 = rangeAxis.valueToJava2D(y0, dataArea, yAxisLocation);
                }
                double transXs;
                if (orientation == PlotOrientation.HORIZONTAL) {
                    if (transY0 == transY1) {
                        drawLine(g2, state.workingLine, transY0, transX0, transY1, transX1);
                    } else {
                        transXs = transX0 + (getStepPoint() * (transX1 - transX0));
                        Graphics2D graphics2D = g2;
                        drawLine(graphics2D, state.workingLine, transY0, transX0, transY0, transXs);
                        Graphics2D graphics2D2 = g2;
                        drawLine(graphics2D2, state.workingLine, transY0, transXs, transY1, transXs);
                        graphics2D2 = g2;
                        drawLine(graphics2D2, state.workingLine, transY1, transXs, transY1, transX1);
                    }
                } else if (orientation == PlotOrientation.VERTICAL) {
                    if (transY0 == transY1) {
                        Graphics2D graphics2D3 = g2;
                        drawLine(graphics2D3, state.workingLine, transX0, transY0, transX1, transY1);
                    } else {
                        transXs = transX0 + (getStepPoint() * (transX1 - transX0));
                        Graphics2D graphics2D4 = g2;
                        drawLine(graphics2D4, state.workingLine, transX0, transY0, transXs, transY0);
                        Graphics2D graphics2D5 = g2;
                        drawLine(graphics2D5, state.workingLine, transXs, transY0, transXs, transY1);
                        graphics2D5 = g2;
                        drawLine(graphics2D5, state.workingLine, transXs, transY1, transX1, transY1);
                    }
                }
                updateCrosshairValues(crosshairState, x1, y1, plot.getDomainAxisIndex(domainAxis), plot.getRangeAxisIndex(rangeAxis), transX1, transY1, orientation);
                EntityCollection entities = state.getEntityCollection();
                if (entities != null) {
                    addEntity(entities, null, dataset, series, item, transX1, transY1);
                }
            }
            if (pass == 1 && isItemLabelVisible(series, item)) {
                boolean z;
                double xx = transX1;
                double yy = transY1;
                if (orientation == PlotOrientation.HORIZONTAL) {
                    xx = transY1;
                    yy = transX1;
                }
                if (y1 < 0.0d) {
                    z = true;
                } else {
                    z = false;
                }
                drawItemLabel(g2, orientation, dataset, series, item, xx, yy, z);
            }
        }
    }

    private void drawLine(Graphics2D g2, Line2D line, double x0, double y0, double x1, double y1) {
        if (!Double.isNaN(x0) && !Double.isNaN(x1) && !Double.isNaN(y0) && !Double.isNaN(y1)) {
            line.setLine(x0, y0, x1, y1);
            g2.draw(line);
        }
    }

    public boolean equals(Object obj) {
        if (obj == this) {
            return true;
        }
        if (!(obj instanceof XYLineAndShapeRenderer)) {
            return false;
        }
        if (this.stepPoint == ((XYStepRenderer) obj).stepPoint) {
            return super.equals(obj);
        }
        return false;
    }

    public int hashCode() {
        return HashUtilities.hashCode(super.hashCode(), this.stepPoint);
    }

    public Object clone() throws CloneNotSupportedException {
        return super.clone();
    }
}
