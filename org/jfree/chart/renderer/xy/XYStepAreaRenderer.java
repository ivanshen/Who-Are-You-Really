package org.jfree.chart.renderer.xy;

import java.awt.Graphics2D;
import java.awt.Paint;
import java.awt.Polygon;
import java.awt.Shape;
import java.awt.Stroke;
import java.awt.geom.Rectangle2D;
import java.awt.geom.Rectangle2D.Double;
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
import org.jfree.data.xy.NormalizedMatrixSeries;
import org.jfree.data.xy.XYDataset;
import org.jfree.util.PublicCloneable;
import org.jfree.util.ShapeUtilities;

public class XYStepAreaRenderer extends AbstractXYItemRenderer implements XYItemRenderer, Cloneable, PublicCloneable, Serializable {
    public static final int AREA = 2;
    public static final int AREA_AND_SHAPES = 3;
    public static final int SHAPES = 1;
    private static final long serialVersionUID = -7311560779702649635L;
    protected transient Polygon pArea;
    private boolean plotArea;
    private double rangeBase;
    private boolean shapesFilled;
    private boolean shapesVisible;
    private boolean showOutline;
    private double stepPoint;

    public XYStepAreaRenderer() {
        this(AREA);
    }

    public XYStepAreaRenderer(int type) {
        this(type, null, null);
    }

    public XYStepAreaRenderer(int type, XYToolTipGenerator toolTipGenerator, XYURLGenerator urlGenerator) {
        this.pArea = null;
        setBaseToolTipGenerator(toolTipGenerator);
        setURLGenerator(urlGenerator);
        if (type == AREA) {
            this.plotArea = true;
        } else if (type == SHAPES) {
            this.shapesVisible = true;
        } else if (type == AREA_AND_SHAPES) {
            this.plotArea = true;
            this.shapesVisible = true;
        }
        this.showOutline = false;
        this.stepPoint = NormalizedMatrixSeries.DEFAULT_SCALE_FACTOR;
    }

    public boolean isOutline() {
        return this.showOutline;
    }

    public void setOutline(boolean show) {
        this.showOutline = show;
        fireChangeEvent();
    }

    public boolean getShapesVisible() {
        return this.shapesVisible;
    }

    public void setShapesVisible(boolean flag) {
        this.shapesVisible = flag;
        fireChangeEvent();
    }

    public boolean isShapesFilled() {
        return this.shapesFilled;
    }

    public void setShapesFilled(boolean filled) {
        this.shapesFilled = filled;
        fireChangeEvent();
    }

    public boolean getPlotArea() {
        return this.plotArea;
    }

    public void setPlotArea(boolean flag) {
        this.plotArea = flag;
        fireChangeEvent();
    }

    public double getRangeBase() {
        return this.rangeBase;
    }

    public void setRangeBase(double val) {
        this.rangeBase = val;
        fireChangeEvent();
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

    public XYItemRendererState initialise(Graphics2D g2, Rectangle2D dataArea, XYPlot plot, XYDataset data, PlotRenderingInfo info) {
        XYItemRendererState state = super.initialise(g2, dataArea, plot, data, info);
        state.setProcessVisibleItemsOnly(false);
        return state;
    }

    public void drawItem(Graphics2D g2, XYItemRendererState state, Rectangle2D dataArea, PlotRenderingInfo info, XYPlot plot, ValueAxis domainAxis, ValueAxis rangeAxis, XYDataset dataset, int series, int item, CrosshairState crosshairState, int pass) {
        double y;
        double transY2;
        PlotOrientation orientation = plot.getOrientation();
        int itemCount = dataset.getItemCount(series);
        Paint paint = getItemPaint(series, item);
        Stroke seriesStroke = getItemStroke(series, item);
        g2.setPaint(paint);
        g2.setStroke(seriesStroke);
        double x1 = dataset.getXValue(series, item);
        double y1 = dataset.getYValue(series, item);
        double x = x1;
        if (Double.isNaN(y1)) {
            y = getRangeBase();
        } else {
            y = y1;
        }
        double transX1 = domainAxis.valueToJava2D(x, dataArea, plot.getDomainAxisEdge());
        double transY1 = restrictValueToDataArea(rangeAxis.valueToJava2D(y, dataArea, plot.getRangeAxisEdge()), plot, dataArea);
        if (this.pArea == null && !Double.isNaN(y1)) {
            this.pArea = new Polygon();
            transY2 = restrictValueToDataArea(rangeAxis.valueToJava2D(getRangeBase(), dataArea, plot.getRangeAxisEdge()), plot, dataArea);
            if (orientation == PlotOrientation.VERTICAL) {
                this.pArea.addPoint((int) transX1, (int) transY2);
            } else if (orientation == PlotOrientation.HORIZONTAL) {
                this.pArea.addPoint((int) transY2, (int) transX1);
            }
        }
        if (item > 0) {
            double y0;
            double x0 = dataset.getXValue(series, item - 1);
            if (Double.isNaN(y1)) {
                y0 = y1;
            } else {
                y0 = dataset.getYValue(series, item - 1);
            }
            x = x0;
            if (Double.isNaN(y0)) {
                y = getRangeBase();
            } else {
                y = y0;
            }
            double transX0 = domainAxis.valueToJava2D(x, dataArea, plot.getDomainAxisEdge());
            double transY0 = restrictValueToDataArea(rangeAxis.valueToJava2D(y, dataArea, plot.getRangeAxisEdge()), plot, dataArea);
            if (Double.isNaN(y1)) {
                transX1 = transX0;
                transY0 = transY1;
            }
            if (transY0 != transY1) {
                double transXs = transX0 + (getStepPoint() * (transX1 - transX0));
                if (orientation == PlotOrientation.VERTICAL) {
                    this.pArea.addPoint((int) transXs, (int) transY0);
                    this.pArea.addPoint((int) transXs, (int) transY1);
                } else if (orientation == PlotOrientation.HORIZONTAL) {
                    this.pArea.addPoint((int) transY0, (int) transXs);
                    this.pArea.addPoint((int) transY1, (int) transXs);
                }
            }
        }
        Shape shape = null;
        if (!Double.isNaN(y1)) {
            if (orientation == PlotOrientation.VERTICAL) {
                this.pArea.addPoint((int) transX1, (int) transY1);
            } else if (orientation == PlotOrientation.HORIZONTAL) {
                this.pArea.addPoint((int) transY1, (int) transX1);
            }
            if (getShapesVisible()) {
                shape = getItemShape(series, item);
                if (orientation == PlotOrientation.VERTICAL) {
                    shape = ShapeUtilities.createTranslatedShape(shape, transX1, transY1);
                } else if (orientation == PlotOrientation.HORIZONTAL) {
                    shape = ShapeUtilities.createTranslatedShape(shape, transY1, transX1);
                }
                if (isShapesFilled()) {
                    g2.fill(shape);
                } else {
                    g2.draw(shape);
                }
            } else if (orientation == PlotOrientation.VERTICAL) {
                shape = new Double(transX1 - DateAxis.DEFAULT_AUTO_RANGE_MINIMUM_SIZE_IN_MILLISECONDS, transY1 - DateAxis.DEFAULT_AUTO_RANGE_MINIMUM_SIZE_IN_MILLISECONDS, 4.0d, 4.0d);
            } else if (orientation == PlotOrientation.HORIZONTAL) {
                shape = new Double(transY1 - DateAxis.DEFAULT_AUTO_RANGE_MINIMUM_SIZE_IN_MILLISECONDS, transX1 - DateAxis.DEFAULT_AUTO_RANGE_MINIMUM_SIZE_IN_MILLISECONDS, 4.0d, 4.0d);
            }
        }
        if (getPlotArea() && item > 0 && this.pArea != null && (item == itemCount - 1 || Double.isNaN(y1))) {
            transY2 = restrictValueToDataArea(rangeAxis.valueToJava2D(getRangeBase(), dataArea, plot.getRangeAxisEdge()), plot, dataArea);
            if (orientation == PlotOrientation.VERTICAL) {
                this.pArea.addPoint((int) transX1, (int) transY2);
            } else if (orientation == PlotOrientation.HORIZONTAL) {
                this.pArea.addPoint((int) transY2, (int) transX1);
            }
            g2.fill(this.pArea);
            if (isOutline()) {
                g2.setStroke(plot.getOutlineStroke());
                g2.setPaint(plot.getOutlinePaint());
                g2.draw(this.pArea);
            }
            this.pArea = null;
        }
        if (!Double.isNaN(y1)) {
            updateCrosshairValues(crosshairState, x1, y1, plot.getDomainAxisIndex(domainAxis), plot.getRangeAxisIndex(rangeAxis), transX1, transY1, orientation);
        }
        EntityCollection entities = state.getEntityCollection();
        if (entities != null) {
            addEntity(entities, shape, dataset, series, item, transX1, transY1);
        }
    }

    public boolean equals(Object obj) {
        if (obj == this) {
            return true;
        }
        if (!(obj instanceof XYStepAreaRenderer)) {
            return false;
        }
        XYStepAreaRenderer that = (XYStepAreaRenderer) obj;
        if (this.showOutline == that.showOutline && this.shapesVisible == that.shapesVisible && this.shapesFilled == that.shapesFilled && this.plotArea == that.plotArea && this.rangeBase == that.rangeBase && this.stepPoint == that.stepPoint) {
            return super.equals(obj);
        }
        return false;
    }

    public Object clone() throws CloneNotSupportedException {
        return super.clone();
    }

    protected static double restrictValueToDataArea(double value, XYPlot plot, Rectangle2D dataArea) {
        double min = 0.0d;
        double max = 0.0d;
        if (plot.getOrientation() == PlotOrientation.VERTICAL) {
            min = dataArea.getMinY();
            max = dataArea.getMaxY();
        } else if (plot.getOrientation() == PlotOrientation.HORIZONTAL) {
            min = dataArea.getMinX();
            max = dataArea.getMaxX();
        }
        if (value < min) {
            return min;
        }
        if (value > max) {
            return max;
        }
        return value;
    }
}
