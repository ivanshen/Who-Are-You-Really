package org.jfree.chart.renderer.xy;

import java.awt.Graphics2D;
import java.awt.Paint;
import java.awt.geom.GeneralPath;
import java.awt.geom.Rectangle2D;
import java.io.Serializable;
import org.jfree.chart.axis.DateAxis;
import org.jfree.chart.axis.ValueAxis;
import org.jfree.chart.entity.EntityCollection;
import org.jfree.chart.labels.XYToolTipGenerator;
import org.jfree.chart.plot.CrosshairState;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.plot.PlotRenderingInfo;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.urls.XYURLGenerator;
import org.jfree.data.Range;
import org.jfree.data.xy.TableXYDataset;
import org.jfree.data.xy.XYDataset;
import org.jfree.ui.RectangleEdge;
import org.jfree.util.PublicCloneable;

public class StackedXYAreaRenderer2 extends XYAreaRenderer2 implements Cloneable, PublicCloneable, Serializable {
    private static final long serialVersionUID = 7752676509764539182L;
    private boolean roundXCoordinates;

    public StackedXYAreaRenderer2() {
        this(null, null);
    }

    public StackedXYAreaRenderer2(XYToolTipGenerator labelGenerator, XYURLGenerator urlGenerator) {
        super(labelGenerator, urlGenerator);
        this.roundXCoordinates = true;
    }

    public boolean getRoundXCoordinates() {
        return this.roundXCoordinates;
    }

    public void setRoundXCoordinates(boolean round) {
        this.roundXCoordinates = round;
        fireChangeEvent();
    }

    public Range findRangeBounds(XYDataset dataset) {
        if (dataset == null) {
            return null;
        }
        double min = Double.POSITIVE_INFINITY;
        double max = Double.NEGATIVE_INFINITY;
        TableXYDataset d = (TableXYDataset) dataset;
        int itemCount = d.getItemCount();
        for (int i = 0; i < itemCount; i++) {
            double[] stackValues = getStackValues((TableXYDataset) dataset, d.getSeriesCount(), i);
            min = Math.min(min, stackValues[0]);
            max = Math.max(max, stackValues[1]);
        }
        if (min == Double.POSITIVE_INFINITY) {
            return null;
        }
        return new Range(min, max);
    }

    public int getPassCount() {
        return 1;
    }

    public void drawItem(Graphics2D g2, XYItemRendererState state, Rectangle2D dataArea, PlotRenderingInfo info, XYPlot plot, ValueAxis domainAxis, ValueAxis rangeAxis, XYDataset dataset, int series, int item, CrosshairState crosshairState, int pass) {
        float transY1;
        EntityCollection entities = null;
        if (info != null) {
            entities = info.getOwner().getEntityCollection();
        }
        TableXYDataset tdataset = (TableXYDataset) dataset;
        PlotOrientation orientation = plot.getOrientation();
        double x1 = dataset.getXValue(series, item);
        double y1 = dataset.getYValue(series, item);
        if (Double.isNaN(y1)) {
            y1 = 0.0d;
        }
        double[] stack1 = getStackValues(tdataset, series, item);
        double x0 = dataset.getXValue(series, Math.max(item - 1, 0));
        double y0 = dataset.getYValue(series, Math.max(item - 1, 0));
        if (Double.isNaN(y0)) {
            y0 = 0.0d;
        }
        double[] stack0 = getStackValues(tdataset, series, Math.max(item - 1, 0));
        int itemCount = dataset.getItemCount(series);
        double x2 = dataset.getXValue(series, Math.min(item + 1, itemCount - 1));
        double y2 = dataset.getYValue(series, Math.min(item + 1, itemCount - 1));
        if (Double.isNaN(y2)) {
            y2 = 0.0d;
        }
        double[] stack2 = getStackValues(tdataset, series, Math.min(item + 1, itemCount - 1));
        double xleft = (x0 + x1) / DateAxis.DEFAULT_AUTO_RANGE_MINIMUM_SIZE_IN_MILLISECONDS;
        double xright = (x1 + x2) / DateAxis.DEFAULT_AUTO_RANGE_MINIMUM_SIZE_IN_MILLISECONDS;
        double[] stackLeft = averageStackValues(stack0, stack1);
        double[] stackRight = averageStackValues(stack1, stack2);
        double[] adjStackLeft = adjustedStackValues(stack0, stack1);
        double[] adjStackRight = adjustedStackValues(stack1, stack2);
        RectangleEdge edge0 = plot.getDomainAxisEdge();
        float transX1 = (float) domainAxis.valueToJava2D(x1, dataArea, edge0);
        float transXLeft = (float) domainAxis.valueToJava2D(xleft, dataArea, edge0);
        float transXRight = (float) domainAxis.valueToJava2D(xright, dataArea, edge0);
        if (this.roundXCoordinates) {
            transX1 = (float) Math.round(transX1);
            transXLeft = (float) Math.round(transXLeft);
            transXRight = (float) Math.round(transXRight);
        }
        RectangleEdge edge1 = plot.getRangeAxisEdge();
        GeneralPath left = new GeneralPath();
        GeneralPath right = new GeneralPath();
        float transStack1;
        float transStackLeft;
        float transYLeft;
        float transStackRight;
        float transYRight;
        if (y1 >= 0.0d) {
            transY1 = (float) rangeAxis.valueToJava2D(stack1[1] + y1, dataArea, edge1);
            transStack1 = (float) rangeAxis.valueToJava2D(stack1[1], dataArea, edge1);
            transStackLeft = (float) rangeAxis.valueToJava2D(adjStackLeft[1], dataArea, edge1);
            if (y0 >= 0.0d) {
                transYLeft = (float) rangeAxis.valueToJava2D(((y0 + y1) / DateAxis.DEFAULT_AUTO_RANGE_MINIMUM_SIZE_IN_MILLISECONDS) + stackLeft[1], dataArea, edge1);
                if (orientation == PlotOrientation.VERTICAL) {
                    left.moveTo(transX1, transY1);
                    left.lineTo(transX1, transStack1);
                    left.lineTo(transXLeft, transStackLeft);
                    left.lineTo(transXLeft, transYLeft);
                } else {
                    left.moveTo(transY1, transX1);
                    left.lineTo(transStack1, transX1);
                    left.lineTo(transStackLeft, transXLeft);
                    left.lineTo(transYLeft, transXLeft);
                }
                left.closePath();
            } else {
                if (orientation == PlotOrientation.VERTICAL) {
                    left.moveTo(transX1, transStack1);
                    left.lineTo(transX1, transY1);
                    left.lineTo(transXLeft, transStackLeft);
                } else {
                    left.moveTo(transStack1, transX1);
                    left.lineTo(transY1, transX1);
                    left.lineTo(transStackLeft, transXLeft);
                }
                left.closePath();
            }
            transStackRight = (float) rangeAxis.valueToJava2D(adjStackRight[1], dataArea, edge1);
            if (y2 >= 0.0d) {
                transYRight = (float) rangeAxis.valueToJava2D(((y1 + y2) / DateAxis.DEFAULT_AUTO_RANGE_MINIMUM_SIZE_IN_MILLISECONDS) + stackRight[1], dataArea, edge1);
                if (orientation == PlotOrientation.VERTICAL) {
                    right.moveTo(transX1, transStack1);
                    right.lineTo(transX1, transY1);
                    right.lineTo(transXRight, transYRight);
                    right.lineTo(transXRight, transStackRight);
                } else {
                    right.moveTo(transStack1, transX1);
                    right.lineTo(transY1, transX1);
                    right.lineTo(transYRight, transXRight);
                    right.lineTo(transStackRight, transXRight);
                }
                right.closePath();
            } else {
                if (orientation == PlotOrientation.VERTICAL) {
                    right.moveTo(transX1, transStack1);
                    right.lineTo(transX1, transY1);
                    right.lineTo(transXRight, transStackRight);
                } else {
                    right.moveTo(transStack1, transX1);
                    right.lineTo(transY1, transX1);
                    right.lineTo(transStackRight, transXRight);
                }
                right.closePath();
            }
        } else {
            transY1 = (float) rangeAxis.valueToJava2D(stack1[0] + y1, dataArea, edge1);
            transStack1 = (float) rangeAxis.valueToJava2D(stack1[0], dataArea, edge1);
            transStackLeft = (float) rangeAxis.valueToJava2D(adjStackLeft[0], dataArea, edge1);
            if (y0 >= 0.0d) {
                if (orientation == PlotOrientation.VERTICAL) {
                    left.moveTo(transX1, transStack1);
                    left.lineTo(transX1, transY1);
                    left.lineTo(transXLeft, transStackLeft);
                } else {
                    left.moveTo(transStack1, transX1);
                    left.lineTo(transY1, transX1);
                    left.lineTo(transStackLeft, transXLeft);
                }
                left.clone();
            } else {
                transYLeft = (float) rangeAxis.valueToJava2D(((y0 + y1) / DateAxis.DEFAULT_AUTO_RANGE_MINIMUM_SIZE_IN_MILLISECONDS) + stackLeft[0], dataArea, edge1);
                if (orientation == PlotOrientation.VERTICAL) {
                    left.moveTo(transX1, transY1);
                    left.lineTo(transX1, transStack1);
                    left.lineTo(transXLeft, transStackLeft);
                    left.lineTo(transXLeft, transYLeft);
                } else {
                    left.moveTo(transY1, transX1);
                    left.lineTo(transStack1, transX1);
                    left.lineTo(transStackLeft, transXLeft);
                    left.lineTo(transYLeft, transXLeft);
                }
                left.closePath();
            }
            transStackRight = (float) rangeAxis.valueToJava2D(adjStackRight[0], dataArea, edge1);
            if (y2 >= 0.0d) {
                if (orientation == PlotOrientation.VERTICAL) {
                    right.moveTo(transX1, transStack1);
                    right.lineTo(transX1, transY1);
                    right.lineTo(transXRight, transStackRight);
                } else {
                    right.moveTo(transStack1, transX1);
                    right.lineTo(transY1, transX1);
                    right.lineTo(transStackRight, transXRight);
                }
                right.closePath();
            } else {
                transYRight = (float) rangeAxis.valueToJava2D(((y1 + y2) / DateAxis.DEFAULT_AUTO_RANGE_MINIMUM_SIZE_IN_MILLISECONDS) + stackRight[0], dataArea, edge1);
                if (orientation == PlotOrientation.VERTICAL) {
                    right.moveTo(transX1, transStack1);
                    right.lineTo(transX1, transY1);
                    right.lineTo(transXRight, transYRight);
                    right.lineTo(transXRight, transStackRight);
                } else {
                    right.moveTo(transStack1, transX1);
                    right.lineTo(transY1, transX1);
                    right.lineTo(transYRight, transXRight);
                    right.lineTo(transStackRight, transXRight);
                }
                right.closePath();
            }
        }
        Paint itemPaint = getItemPaint(series, item);
        if (pass == 0) {
            g2.setPaint(itemPaint);
            g2.fill(left);
            g2.fill(right);
        }
        if (entities != null) {
            GeneralPath generalPath = new GeneralPath(left);
            generalPath.append(right, false);
            addEntity(entities, generalPath, dataset, series, item, (double) transX1, (double) transY1);
        }
    }

    private double[] getStackValues(TableXYDataset dataset, int series, int index) {
        double[] result = new double[2];
        for (int i = 0; i < series; i++) {
            double v = dataset.getYValue(i, index);
            if (!Double.isNaN(v)) {
                if (v >= 0.0d) {
                    result[1] = result[1] + v;
                } else {
                    result[0] = result[0] + v;
                }
            }
        }
        return result;
    }

    private double[] averageStackValues(double[] stack1, double[] stack2) {
        return new double[]{(stack1[0] + stack2[0]) / DateAxis.DEFAULT_AUTO_RANGE_MINIMUM_SIZE_IN_MILLISECONDS, (stack1[1] + stack2[1]) / DateAxis.DEFAULT_AUTO_RANGE_MINIMUM_SIZE_IN_MILLISECONDS};
    }

    private double[] adjustedStackValues(double[] stack1, double[] stack2) {
        double[] result = new double[2];
        if (stack1[0] == 0.0d || stack2[0] == 0.0d) {
            result[0] = 0.0d;
        } else {
            result[0] = (stack1[0] + stack2[0]) / DateAxis.DEFAULT_AUTO_RANGE_MINIMUM_SIZE_IN_MILLISECONDS;
        }
        if (stack1[1] == 0.0d || stack2[1] == 0.0d) {
            result[1] = 0.0d;
        } else {
            result[1] = (stack1[1] + stack2[1]) / DateAxis.DEFAULT_AUTO_RANGE_MINIMUM_SIZE_IN_MILLISECONDS;
        }
        return result;
    }

    public boolean equals(Object obj) {
        if (obj == this) {
            return true;
        }
        if (!(obj instanceof StackedXYAreaRenderer2)) {
            return false;
        }
        if (this.roundXCoordinates == ((StackedXYAreaRenderer2) obj).roundXCoordinates) {
            return super.equals(obj);
        }
        return false;
    }

    public Object clone() throws CloneNotSupportedException {
        return super.clone();
    }
}
