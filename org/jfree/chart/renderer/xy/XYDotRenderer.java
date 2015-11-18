package org.jfree.chart.renderer.xy;

import java.awt.Graphics2D;
import java.awt.Paint;
import java.awt.Shape;
import java.awt.geom.Rectangle2D;
import java.awt.geom.Rectangle2D.Double;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import org.jfree.chart.LegendItem;
import org.jfree.chart.axis.DateAxis;
import org.jfree.chart.axis.ValueAxis;
import org.jfree.chart.plot.CrosshairState;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.plot.PlotRenderingInfo;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.util.ParamChecks;
import org.jfree.data.xy.XYDataset;
import org.jfree.io.SerialUtilities;
import org.jfree.ui.RectangleEdge;
import org.jfree.util.PublicCloneable;
import org.jfree.util.ShapeUtilities;

public class XYDotRenderer extends AbstractXYItemRenderer implements XYItemRenderer, PublicCloneable {
    private static final long serialVersionUID = -2764344339073566425L;
    private int dotHeight;
    private int dotWidth;
    private transient Shape legendShape;

    public XYDotRenderer() {
        this.dotWidth = 1;
        this.dotHeight = 1;
        this.legendShape = new Double(-3.0d, -3.0d, 6.0d, 6.0d);
    }

    public int getDotWidth() {
        return this.dotWidth;
    }

    public void setDotWidth(int w) {
        if (w < 1) {
            throw new IllegalArgumentException("Requires w > 0.");
        }
        this.dotWidth = w;
        fireChangeEvent();
    }

    public int getDotHeight() {
        return this.dotHeight;
    }

    public void setDotHeight(int h) {
        if (h < 1) {
            throw new IllegalArgumentException("Requires h > 0.");
        }
        this.dotHeight = h;
        fireChangeEvent();
    }

    public Shape getLegendShape() {
        return this.legendShape;
    }

    public void setLegendShape(Shape shape) {
        ParamChecks.nullNotPermitted(shape, "shape");
        this.legendShape = shape;
        fireChangeEvent();
    }

    public void drawItem(Graphics2D g2, XYItemRendererState state, Rectangle2D dataArea, PlotRenderingInfo info, XYPlot plot, ValueAxis domainAxis, ValueAxis rangeAxis, XYDataset dataset, int series, int item, CrosshairState crosshairState, int pass) {
        if (getItemVisible(series, item)) {
            double x = dataset.getXValue(series, item);
            double y = dataset.getYValue(series, item);
            double adjx = ((double) (this.dotWidth - 1)) / DateAxis.DEFAULT_AUTO_RANGE_MINIMUM_SIZE_IN_MILLISECONDS;
            double adjy = ((double) (this.dotHeight - 1)) / DateAxis.DEFAULT_AUTO_RANGE_MINIMUM_SIZE_IN_MILLISECONDS;
            if (!Double.isNaN(y)) {
                RectangleEdge xAxisLocation = plot.getDomainAxisEdge();
                double transX = domainAxis.valueToJava2D(x, dataArea, xAxisLocation) - adjx;
                double transY = rangeAxis.valueToJava2D(y, dataArea, plot.getRangeAxisEdge()) - adjy;
                g2.setPaint(getItemPaint(series, item));
                PlotOrientation orientation = plot.getOrientation();
                if (orientation == PlotOrientation.HORIZONTAL) {
                    g2.fillRect((int) transY, (int) transX, this.dotHeight, this.dotWidth);
                } else if (orientation == PlotOrientation.VERTICAL) {
                    g2.fillRect((int) transX, (int) transY, this.dotWidth, this.dotHeight);
                }
                updateCrosshairValues(crosshairState, x, y, plot.getDomainAxisIndex(domainAxis), plot.getRangeAxisIndex(rangeAxis), transX, transY, orientation);
            }
        }
    }

    public LegendItem getLegendItem(int datasetIndex, int series) {
        LegendItem legendItem = null;
        XYPlot plot = getPlot();
        if (plot != null) {
            XYDataset dataset = plot.getDataset(datasetIndex);
            if (dataset != null) {
                legendItem = null;
                if (getItemVisible(series, 0)) {
                    String label = getLegendItemLabelGenerator().generateLabel(dataset, series);
                    String description = label;
                    String toolTipText = null;
                    if (getLegendItemToolTipGenerator() != null) {
                        toolTipText = getLegendItemToolTipGenerator().generateLabel(dataset, series);
                    }
                    String urlText = null;
                    if (getLegendItemURLGenerator() != null) {
                        urlText = getLegendItemURLGenerator().generateLabel(dataset, series);
                    }
                    legendItem = new LegendItem(label, description, toolTipText, urlText, getLegendShape(), lookupSeriesPaint(series));
                    legendItem.setLabelFont(lookupLegendTextFont(series));
                    Paint labelPaint = lookupLegendTextPaint(series);
                    if (labelPaint != null) {
                        legendItem.setLabelPaint(labelPaint);
                    }
                    legendItem.setSeriesKey(dataset.getSeriesKey(series));
                    legendItem.setSeriesIndex(series);
                    legendItem.setDataset(dataset);
                    legendItem.setDatasetIndex(datasetIndex);
                }
            }
        }
        return legendItem;
    }

    public boolean equals(Object obj) {
        if (obj == this) {
            return true;
        }
        if (!(obj instanceof XYDotRenderer)) {
            return false;
        }
        XYDotRenderer that = (XYDotRenderer) obj;
        if (this.dotWidth == that.dotWidth && this.dotHeight == that.dotHeight && ShapeUtilities.equal(this.legendShape, that.legendShape)) {
            return super.equals(obj);
        }
        return false;
    }

    public Object clone() throws CloneNotSupportedException {
        return super.clone();
    }

    private void readObject(ObjectInputStream stream) throws IOException, ClassNotFoundException {
        stream.defaultReadObject();
        this.legendShape = SerialUtilities.readShape(stream);
    }

    private void writeObject(ObjectOutputStream stream) throws IOException {
        stream.defaultWriteObject();
        SerialUtilities.writeShape(this.legendShape, stream);
    }
}
