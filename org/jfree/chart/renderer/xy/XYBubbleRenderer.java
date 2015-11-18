package org.jfree.chart.renderer.xy;

import java.awt.Graphics2D;
import java.awt.Paint;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Ellipse2D.Double;
import java.awt.geom.Rectangle2D;
import org.jfree.chart.LegendItem;
import org.jfree.chart.axis.DateAxis;
import org.jfree.chart.axis.ValueAxis;
import org.jfree.chart.entity.EntityCollection;
import org.jfree.chart.plot.CrosshairState;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.plot.PlotRenderingInfo;
import org.jfree.chart.plot.XYPlot;
import org.jfree.data.xy.XYDataset;
import org.jfree.data.xy.XYZDataset;
import org.jfree.ui.RectangleEdge;
import org.jfree.util.PublicCloneable;

public class XYBubbleRenderer extends AbstractXYItemRenderer implements XYItemRenderer, PublicCloneable {
    public static final int SCALE_ON_BOTH_AXES = 0;
    public static final int SCALE_ON_DOMAIN_AXIS = 1;
    public static final int SCALE_ON_RANGE_AXIS = 2;
    public static final long serialVersionUID = -5221991598674249125L;
    private int scaleType;

    public XYBubbleRenderer() {
        this(SCALE_ON_BOTH_AXES);
    }

    public XYBubbleRenderer(int scaleType) {
        if (scaleType < 0 || scaleType > SCALE_ON_RANGE_AXIS) {
            throw new IllegalArgumentException("Invalid 'scaleType'.");
        }
        this.scaleType = scaleType;
        setBaseLegendShape(new Double(-4.0d, -4.0d, XYLine3DRenderer.DEFAULT_Y_OFFSET, XYLine3DRenderer.DEFAULT_Y_OFFSET));
    }

    public int getScaleType() {
        return this.scaleType;
    }

    public void drawItem(Graphics2D g2, XYItemRendererState state, Rectangle2D dataArea, PlotRenderingInfo info, XYPlot plot, ValueAxis domainAxis, ValueAxis rangeAxis, XYDataset dataset, int series, int item, CrosshairState crosshairState, int pass) {
        if (getItemVisible(series, item)) {
            PlotOrientation orientation = plot.getOrientation();
            double x = dataset.getXValue(series, item);
            double y = dataset.getYValue(series, item);
            double z = Double.NaN;
            if (dataset instanceof XYZDataset) {
                z = ((XYZDataset) dataset).getZValue(series, item);
            }
            if (!Double.isNaN(z)) {
                double transDomain;
                double transRange;
                Ellipse2D circle;
                RectangleEdge domainAxisLocation = plot.getDomainAxisEdge();
                RectangleEdge rangeAxisLocation = plot.getRangeAxisEdge();
                double transX = domainAxis.valueToJava2D(x, dataArea, domainAxisLocation);
                double transY = rangeAxis.valueToJava2D(y, dataArea, rangeAxisLocation);
                switch (getScaleType()) {
                    case SCALE_ON_DOMAIN_AXIS /*1*/:
                        transDomain = domainAxis.valueToJava2D(z, dataArea, domainAxisLocation) - domainAxis.valueToJava2D(0.0d, dataArea, domainAxisLocation);
                        transRange = transDomain;
                        break;
                    case SCALE_ON_RANGE_AXIS /*2*/:
                        transRange = rangeAxis.valueToJava2D(0.0d, dataArea, rangeAxisLocation) - rangeAxis.valueToJava2D(z, dataArea, rangeAxisLocation);
                        transDomain = transRange;
                        break;
                    default:
                        double zero1 = domainAxis.valueToJava2D(0.0d, dataArea, domainAxisLocation);
                        transDomain = domainAxis.valueToJava2D(z, dataArea, domainAxisLocation) - zero1;
                        transRange = rangeAxis.valueToJava2D(0.0d, dataArea, rangeAxisLocation) - rangeAxis.valueToJava2D(z, dataArea, rangeAxisLocation);
                        break;
                }
                transDomain = Math.abs(transDomain);
                transRange = Math.abs(transRange);
                if (orientation == PlotOrientation.VERTICAL) {
                    circle = new Double(transX - (transDomain / DateAxis.DEFAULT_AUTO_RANGE_MINIMUM_SIZE_IN_MILLISECONDS), transY - (transRange / DateAxis.DEFAULT_AUTO_RANGE_MINIMUM_SIZE_IN_MILLISECONDS), transDomain, transRange);
                } else if (orientation == PlotOrientation.HORIZONTAL) {
                    Double doubleR = new Double(transY - (transRange / DateAxis.DEFAULT_AUTO_RANGE_MINIMUM_SIZE_IN_MILLISECONDS), transX - (transDomain / DateAxis.DEFAULT_AUTO_RANGE_MINIMUM_SIZE_IN_MILLISECONDS), transRange, transDomain);
                } else {
                    throw new IllegalStateException();
                }
                g2.setPaint(getItemPaint(series, item));
                g2.fill(circle);
                g2.setStroke(getItemOutlineStroke(series, item));
                g2.setPaint(getItemOutlinePaint(series, item));
                g2.draw(circle);
                if (isItemLabelVisible(series, item)) {
                    if (orientation == PlotOrientation.VERTICAL) {
                        drawItemLabel(g2, orientation, dataset, series, item, transX, transY, false);
                    } else if (orientation == PlotOrientation.HORIZONTAL) {
                        drawItemLabel(g2, orientation, dataset, series, item, transY, transX, false);
                    }
                }
                if (info != null) {
                    EntityCollection entities = info.getOwner().getEntityCollection();
                    if (entities != null && circle.intersects(dataArea)) {
                        addEntity(entities, circle, dataset, series, item, circle.getCenterX(), circle.getCenterY());
                    }
                }
                updateCrosshairValues(crosshairState, x, y, plot.getDomainAxisIndex(domainAxis), plot.getRangeAxisIndex(rangeAxis), transX, transY, orientation);
            }
        }
    }

    public LegendItem getLegendItem(int datasetIndex, int series) {
        LegendItem result = null;
        XYPlot plot = getPlot();
        if (plot == null) {
            return null;
        }
        XYDataset dataset = plot.getDataset(datasetIndex);
        if (dataset != null && getItemVisible(series, SCALE_ON_BOTH_AXES)) {
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
            result = new LegendItem(label, description, toolTipText, urlText, lookupLegendShape(series), lookupSeriesPaint(series), lookupSeriesOutlineStroke(series), lookupSeriesOutlinePaint(series));
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
        return result;
    }

    public boolean equals(Object obj) {
        if (obj == this) {
            return true;
        }
        if (!(obj instanceof XYBubbleRenderer)) {
            return false;
        }
        if (this.scaleType == ((XYBubbleRenderer) obj).scaleType) {
            return super.equals(obj);
        }
        return false;
    }

    public Object clone() throws CloneNotSupportedException {
        return super.clone();
    }
}
