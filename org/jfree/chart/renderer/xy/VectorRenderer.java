package org.jfree.chart.renderer.xy;

import java.awt.Graphics2D;
import java.awt.geom.GeneralPath;
import java.awt.geom.Line2D;
import java.awt.geom.Line2D.Double;
import java.awt.geom.Rectangle2D;
import java.io.Serializable;
import org.jfree.chart.axis.DateAxis;
import org.jfree.chart.axis.ValueAxis;
import org.jfree.chart.entity.EntityCollection;
import org.jfree.chart.plot.CrosshairState;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.plot.PlotRenderingInfo;
import org.jfree.chart.plot.SpiderWebPlot;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.util.ParamChecks;
import org.jfree.data.Range;
import org.jfree.data.xy.NormalizedMatrixSeries;
import org.jfree.data.xy.VectorXYDataset;
import org.jfree.data.xy.XYDataset;
import org.jfree.util.PublicCloneable;

public class VectorRenderer extends AbstractXYItemRenderer implements XYItemRenderer, Cloneable, PublicCloneable, Serializable {
    private double baseLength;
    private double headLength;

    public VectorRenderer() {
        this.baseLength = SpiderWebPlot.DEFAULT_AXIS_LABEL_GAP;
        this.headLength = 0.14d;
    }

    public Range findDomainBounds(XYDataset dataset) {
        ParamChecks.nullNotPermitted(dataset, "dataset");
        double minimum = Double.POSITIVE_INFINITY;
        double maximum = Double.NEGATIVE_INFINITY;
        int seriesCount = dataset.getSeriesCount();
        int series;
        int itemCount;
        int item;
        double uvalue;
        double lvalue;
        if (dataset instanceof VectorXYDataset) {
            VectorXYDataset vdataset = (VectorXYDataset) dataset;
            for (series = 0; series < seriesCount; series++) {
                itemCount = dataset.getItemCount(series);
                for (item = 0; item < itemCount; item++) {
                    double delta = vdataset.getVectorXValue(series, item);
                    if (delta < 0.0d) {
                        uvalue = vdataset.getXValue(series, item);
                        lvalue = uvalue + delta;
                    } else {
                        lvalue = vdataset.getXValue(series, item);
                        uvalue = lvalue + delta;
                    }
                    minimum = Math.min(minimum, lvalue);
                    maximum = Math.max(maximum, uvalue);
                }
            }
        } else {
            for (series = 0; series < seriesCount; series++) {
                itemCount = dataset.getItemCount(series);
                for (item = 0; item < itemCount; item++) {
                    lvalue = dataset.getXValue(series, item);
                    uvalue = lvalue;
                    minimum = Math.min(minimum, lvalue);
                    maximum = Math.max(maximum, uvalue);
                }
            }
        }
        if (minimum > maximum) {
            return null;
        }
        return new Range(minimum, maximum);
    }

    public Range findRangeBounds(XYDataset dataset) {
        ParamChecks.nullNotPermitted(dataset, "dataset");
        double minimum = Double.POSITIVE_INFINITY;
        double maximum = Double.NEGATIVE_INFINITY;
        int seriesCount = dataset.getSeriesCount();
        int series;
        int itemCount;
        int item;
        double uvalue;
        double lvalue;
        if (dataset instanceof VectorXYDataset) {
            VectorXYDataset vdataset = (VectorXYDataset) dataset;
            for (series = 0; series < seriesCount; series++) {
                itemCount = dataset.getItemCount(series);
                for (item = 0; item < itemCount; item++) {
                    double delta = vdataset.getVectorYValue(series, item);
                    if (delta < 0.0d) {
                        uvalue = vdataset.getYValue(series, item);
                        lvalue = uvalue + delta;
                    } else {
                        lvalue = vdataset.getYValue(series, item);
                        uvalue = lvalue + delta;
                    }
                    minimum = Math.min(minimum, lvalue);
                    maximum = Math.max(maximum, uvalue);
                }
            }
        } else {
            for (series = 0; series < seriesCount; series++) {
                itemCount = dataset.getItemCount(series);
                for (item = 0; item < itemCount; item++) {
                    lvalue = dataset.getYValue(series, item);
                    uvalue = lvalue;
                    minimum = Math.min(minimum, lvalue);
                    maximum = Math.max(maximum, uvalue);
                }
            }
        }
        if (minimum > maximum) {
            return null;
        }
        return new Range(minimum, maximum);
    }

    public void drawItem(Graphics2D g2, XYItemRendererState state, Rectangle2D dataArea, PlotRenderingInfo info, XYPlot plot, ValueAxis domainAxis, ValueAxis rangeAxis, XYDataset dataset, int series, int item, CrosshairState crosshairState, int pass) {
        Line2D line;
        double x = dataset.getXValue(series, item);
        double y = dataset.getYValue(series, item);
        double dx = 0.0d;
        double dy = 0.0d;
        if (dataset instanceof VectorXYDataset) {
            dx = ((VectorXYDataset) dataset).getVectorXValue(series, item);
            dy = ((VectorXYDataset) dataset).getVectorYValue(series, item);
        }
        double xx0 = domainAxis.valueToJava2D(x, dataArea, plot.getDomainAxisEdge());
        double yy0 = rangeAxis.valueToJava2D(y, dataArea, plot.getRangeAxisEdge());
        double d = x + dx;
        double xx1 = domainAxis.valueToJava2D(r16, dataArea, plot.getDomainAxisEdge());
        d = y + dy;
        double yy1 = rangeAxis.valueToJava2D(r16, dataArea, plot.getRangeAxisEdge());
        PlotOrientation orientation = plot.getOrientation();
        if (orientation.equals(PlotOrientation.HORIZONTAL)) {
            line = new Double(yy0, xx0, yy1, xx1);
        } else {
            Double doubleR = new Double(xx0, yy0, xx1, yy1);
        }
        g2.setPaint(getItemPaint(series, item));
        g2.setStroke(getItemStroke(series, item));
        g2.draw(line);
        double dxx = xx1 - xx0;
        double dyy = yy1 - yy0;
        double bx = xx0 + ((NormalizedMatrixSeries.DEFAULT_SCALE_FACTOR - this.baseLength) * dxx);
        double by = yy0 + ((NormalizedMatrixSeries.DEFAULT_SCALE_FACTOR - this.baseLength) * dyy);
        double cx = xx0 + ((NormalizedMatrixSeries.DEFAULT_SCALE_FACTOR - this.headLength) * dxx);
        double cy = yy0 + ((NormalizedMatrixSeries.DEFAULT_SCALE_FACTOR - this.headLength) * dyy);
        double angle = 0.0d;
        if (dxx != 0.0d) {
            angle = 1.5707963267948966d - Math.atan(dyy / dxx);
        }
        double deltaX = DateAxis.DEFAULT_AUTO_RANGE_MINIMUM_SIZE_IN_MILLISECONDS * Math.cos(angle);
        double deltaY = DateAxis.DEFAULT_AUTO_RANGE_MINIMUM_SIZE_IN_MILLISECONDS * Math.sin(angle);
        double leftx = cx + deltaX;
        double lefty = cy - deltaY;
        double rightx = cx - deltaX;
        double righty = cy + deltaY;
        GeneralPath p = new GeneralPath();
        GeneralPath generalPath;
        if (orientation == PlotOrientation.VERTICAL) {
            generalPath = p;
            generalPath.moveTo((float) xx1, (float) yy1);
            p.lineTo((float) rightx, (float) righty);
            p.lineTo((float) bx, (float) by);
            p.lineTo((float) leftx, (float) lefty);
        } else {
            generalPath = p;
            generalPath.moveTo((float) yy1, (float) xx1);
            p.lineTo((float) righty, (float) rightx);
            p.lineTo((float) by, (float) bx);
            p.lineTo((float) lefty, (float) leftx);
        }
        p.closePath();
        g2.draw(p);
        if (info != null) {
            EntityCollection entities = info.getOwner().getEntityCollection();
            if (entities != null) {
                addEntity(entities, line.getBounds(), dataset, series, item, 0.0d, 0.0d);
            }
        }
    }

    public boolean equals(Object obj) {
        if (obj == this) {
            return true;
        }
        if (!(obj instanceof VectorRenderer)) {
            return false;
        }
        VectorRenderer that = (VectorRenderer) obj;
        if (this.baseLength == that.baseLength && this.headLength == that.headLength) {
            return super.equals(obj);
        }
        return false;
    }

    public Object clone() throws CloneNotSupportedException {
        return super.clone();
    }
}
