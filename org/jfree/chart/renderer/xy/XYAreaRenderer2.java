package org.jfree.chart.renderer.xy;

import java.awt.Graphics2D;
import java.awt.Paint;
import java.awt.Shape;
import java.awt.Stroke;
import java.awt.geom.GeneralPath;
import java.awt.geom.Rectangle2D;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import org.jfree.chart.LegendItem;
import org.jfree.chart.axis.DateAxis;
import org.jfree.chart.axis.ValueAxis;
import org.jfree.chart.entity.EntityCollection;
import org.jfree.chart.entity.XYItemEntity;
import org.jfree.chart.labels.XYToolTipGenerator;
import org.jfree.chart.plot.CrosshairState;
import org.jfree.chart.plot.MeterPlot;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.plot.PlotRenderingInfo;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.urls.XYURLGenerator;
import org.jfree.chart.util.ParamChecks;
import org.jfree.data.xy.XYDataset;
import org.jfree.io.SerialUtilities;
import org.jfree.util.PublicCloneable;
import org.jfree.util.ShapeUtilities;

public class XYAreaRenderer2 extends AbstractXYItemRenderer implements XYItemRenderer, PublicCloneable {
    private static final long serialVersionUID = -7378069681579984133L;
    private transient Shape legendArea;
    private boolean showOutline;

    public XYAreaRenderer2() {
        this(null, null);
    }

    public XYAreaRenderer2(XYToolTipGenerator labelGenerator, XYURLGenerator urlGenerator) {
        this.showOutline = false;
        setBaseToolTipGenerator(labelGenerator);
        setURLGenerator(urlGenerator);
        GeneralPath area = new GeneralPath();
        area.moveTo(0.0f, -4.0f);
        area.lineTo(MeterPlot.DEFAULT_BORDER_SIZE, -2.0f);
        area.lineTo(4.0f, 4.0f);
        area.lineTo(-4.0f, 4.0f);
        area.lineTo(-3.0f, -2.0f);
        area.closePath();
        this.legendArea = area;
    }

    public boolean isOutline() {
        return this.showOutline;
    }

    public void setOutline(boolean show) {
        this.showOutline = show;
        fireChangeEvent();
    }

    public boolean getPlotLines() {
        return false;
    }

    public Shape getLegendArea() {
        return this.legendArea;
    }

    public void setLegendArea(Shape area) {
        ParamChecks.nullNotPermitted(area, "area");
        this.legendArea = area;
        fireChangeEvent();
    }

    public LegendItem getLegendItem(int datasetIndex, int series) {
        LegendItem result = null;
        XYPlot xyplot = getPlot();
        if (xyplot != null) {
            XYDataset dataset = xyplot.getDataset(datasetIndex);
            if (dataset != null) {
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
                result = new LegendItem(label, description, toolTipText, urlText, this.legendArea, lookupSeriesPaint(series));
                result.setLabelFont(lookupLegendTextFont(series));
                Paint labelPaint = lookupLegendTextPaint(series);
                if (labelPaint != null) {
                    result.setLabelPaint(labelPaint);
                }
                result.setDataset(dataset);
                result.setDatasetIndex(datasetIndex);
                result.setSeriesKey(dataset.getSeriesKey(series));
                result.setSeriesIndex(series);
            }
        }
        return result;
    }

    public void drawItem(Graphics2D g2, XYItemRendererState state, Rectangle2D dataArea, PlotRenderingInfo info, XYPlot plot, ValueAxis domainAxis, ValueAxis rangeAxis, XYDataset dataset, int series, int item, CrosshairState crosshairState, int pass) {
        if (getItemVisible(series, item)) {
            double x1 = dataset.getXValue(series, item);
            double y1 = dataset.getYValue(series, item);
            if (Double.isNaN(y1)) {
                y1 = 0.0d;
            }
            double transX1 = domainAxis.valueToJava2D(x1, dataArea, plot.getDomainAxisEdge());
            double transY1 = rangeAxis.valueToJava2D(y1, dataArea, plot.getRangeAxisEdge());
            double x0 = dataset.getXValue(series, Math.max(item - 1, 0));
            double y0 = dataset.getYValue(series, Math.max(item - 1, 0));
            if (Double.isNaN(y0)) {
                y0 = 0.0d;
            }
            double transX0 = domainAxis.valueToJava2D(x0, dataArea, plot.getDomainAxisEdge());
            double transY0 = rangeAxis.valueToJava2D(y0, dataArea, plot.getRangeAxisEdge());
            int itemCount = dataset.getItemCount(series);
            double x2 = dataset.getXValue(series, Math.min(item + 1, itemCount - 1));
            double y2 = dataset.getYValue(series, Math.min(item + 1, itemCount - 1));
            if (Double.isNaN(y2)) {
                y2 = 0.0d;
            }
            double transX2 = domainAxis.valueToJava2D(x2, dataArea, plot.getDomainAxisEdge());
            double transY2 = rangeAxis.valueToJava2D(y2, dataArea, plot.getRangeAxisEdge());
            double transZero = rangeAxis.valueToJava2D(0.0d, dataArea, plot.getRangeAxisEdge());
            GeneralPath hotspot = new GeneralPath();
            if (plot.getOrientation() == PlotOrientation.HORIZONTAL) {
                AbstractXYItemRenderer.moveTo(hotspot, transZero, (transX0 + transX1) / DateAxis.DEFAULT_AUTO_RANGE_MINIMUM_SIZE_IN_MILLISECONDS);
                AbstractXYItemRenderer.lineTo(hotspot, (transY0 + transY1) / DateAxis.DEFAULT_AUTO_RANGE_MINIMUM_SIZE_IN_MILLISECONDS, (transX0 + transX1) / DateAxis.DEFAULT_AUTO_RANGE_MINIMUM_SIZE_IN_MILLISECONDS);
                AbstractXYItemRenderer.lineTo(hotspot, transY1, transX1);
                AbstractXYItemRenderer.lineTo(hotspot, (transY1 + transY2) / DateAxis.DEFAULT_AUTO_RANGE_MINIMUM_SIZE_IN_MILLISECONDS, (transX1 + transX2) / DateAxis.DEFAULT_AUTO_RANGE_MINIMUM_SIZE_IN_MILLISECONDS);
                AbstractXYItemRenderer.lineTo(hotspot, transZero, (transX1 + transX2) / DateAxis.DEFAULT_AUTO_RANGE_MINIMUM_SIZE_IN_MILLISECONDS);
            } else {
                AbstractXYItemRenderer.moveTo(hotspot, (transX0 + transX1) / DateAxis.DEFAULT_AUTO_RANGE_MINIMUM_SIZE_IN_MILLISECONDS, transZero);
                AbstractXYItemRenderer.lineTo(hotspot, (transX0 + transX1) / DateAxis.DEFAULT_AUTO_RANGE_MINIMUM_SIZE_IN_MILLISECONDS, (transY0 + transY1) / DateAxis.DEFAULT_AUTO_RANGE_MINIMUM_SIZE_IN_MILLISECONDS);
                AbstractXYItemRenderer.lineTo(hotspot, transX1, transY1);
                AbstractXYItemRenderer.lineTo(hotspot, (transX1 + transX2) / DateAxis.DEFAULT_AUTO_RANGE_MINIMUM_SIZE_IN_MILLISECONDS, (transY1 + transY2) / DateAxis.DEFAULT_AUTO_RANGE_MINIMUM_SIZE_IN_MILLISECONDS);
                AbstractXYItemRenderer.lineTo(hotspot, (transX1 + transX2) / DateAxis.DEFAULT_AUTO_RANGE_MINIMUM_SIZE_IN_MILLISECONDS, transZero);
            }
            hotspot.closePath();
            PlotOrientation orientation = plot.getOrientation();
            Paint paint = getItemPaint(series, item);
            Stroke stroke = getItemStroke(series, item);
            g2.setPaint(paint);
            g2.setStroke(stroke);
            g2.fill(hotspot);
            if (isOutline()) {
                g2.setStroke(lookupSeriesOutlineStroke(series));
                g2.setPaint(lookupSeriesOutlinePaint(series));
                g2.draw(hotspot);
            }
            updateCrosshairValues(crosshairState, x1, y1, plot.getDomainAxisIndex(domainAxis), plot.getRangeAxisIndex(rangeAxis), transX1, transY1, orientation);
            if (state.getInfo() != null) {
                EntityCollection entities = state.getEntityCollection();
                if (entities != null) {
                    String tip = null;
                    XYToolTipGenerator generator = getToolTipGenerator(series, item);
                    if (generator != null) {
                        tip = generator.generateToolTip(dataset, series, item);
                    }
                    String url = null;
                    if (getURLGenerator() != null) {
                        url = getURLGenerator().generateURL(dataset, series, item);
                    }
                    entities.add(new XYItemEntity(hotspot, dataset, series, item, tip, url));
                }
            }
        }
    }

    public boolean equals(Object obj) {
        if (obj == this) {
            return true;
        }
        if (!(obj instanceof XYAreaRenderer2)) {
            return false;
        }
        XYAreaRenderer2 that = (XYAreaRenderer2) obj;
        if (this.showOutline == that.showOutline && ShapeUtilities.equal(this.legendArea, that.legendArea)) {
            return super.equals(obj);
        }
        return false;
    }

    public Object clone() throws CloneNotSupportedException {
        XYAreaRenderer2 clone = (XYAreaRenderer2) super.clone();
        clone.legendArea = ShapeUtilities.clone(this.legendArea);
        return clone;
    }

    private void readObject(ObjectInputStream stream) throws IOException, ClassNotFoundException {
        stream.defaultReadObject();
        this.legendArea = SerialUtilities.readShape(stream);
    }

    private void writeObject(ObjectOutputStream stream) throws IOException {
        stream.defaultWriteObject();
        SerialUtilities.writeShape(this.legendArea, stream);
    }
}
