package org.jfree.chart.renderer.xy;

import java.awt.Graphics2D;
import java.awt.Paint;
import java.awt.Point;
import java.awt.Polygon;
import java.awt.Shape;
import java.awt.Stroke;
import java.awt.geom.Line2D;
import java.awt.geom.Line2D.Double;
import java.awt.geom.Rectangle2D;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.Stack;
import org.jfree.chart.axis.ValueAxis;
import org.jfree.chart.entity.EntityCollection;
import org.jfree.chart.entity.XYItemEntity;
import org.jfree.chart.labels.XYToolTipGenerator;
import org.jfree.chart.plot.CrosshairState;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.plot.PlotRenderingInfo;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.renderer.category.BarRenderer;
import org.jfree.chart.urls.XYURLGenerator;
import org.jfree.data.Range;
import org.jfree.data.general.DatasetUtilities;
import org.jfree.data.xy.TableXYDataset;
import org.jfree.data.xy.XYDataset;
import org.jfree.io.SerialUtilities;
import org.jfree.util.ObjectUtilities;
import org.jfree.util.PaintUtilities;
import org.jfree.util.PublicCloneable;
import org.jfree.util.ShapeUtilities;

public class StackedXYAreaRenderer extends XYAreaRenderer implements Cloneable, PublicCloneable, Serializable {
    private static final long serialVersionUID = 5217394318178570889L;
    private transient Paint shapePaint;
    private transient Stroke shapeStroke;

    static class StackedXYAreaRendererState extends XYItemRendererState {
        private Stack currentSeriesPoints;
        private Stack lastSeriesPoints;
        private Line2D line;
        private Polygon seriesArea;

        public StackedXYAreaRendererState(PlotRenderingInfo info) {
            super(info);
            this.seriesArea = null;
            this.line = new Double();
            this.lastSeriesPoints = new Stack();
            this.currentSeriesPoints = new Stack();
        }

        public Polygon getSeriesArea() {
            return this.seriesArea;
        }

        public void setSeriesArea(Polygon area) {
            this.seriesArea = area;
        }

        public Line2D getLine() {
            return this.line;
        }

        public Stack getCurrentSeriesPoints() {
            return this.currentSeriesPoints;
        }

        public void setCurrentSeriesPoints(Stack points) {
            this.currentSeriesPoints = points;
        }

        public Stack getLastSeriesPoints() {
            return this.lastSeriesPoints;
        }

        public void setLastSeriesPoints(Stack points) {
            this.lastSeriesPoints = points;
        }
    }

    public StackedXYAreaRenderer() {
        this(4);
    }

    public StackedXYAreaRenderer(int type) {
        this(type, null, null);
    }

    public StackedXYAreaRenderer(int type, XYToolTipGenerator labelGenerator, XYURLGenerator urlGenerator) {
        super(type, labelGenerator, urlGenerator);
        this.shapePaint = null;
        this.shapeStroke = null;
    }

    public Paint getShapePaint() {
        return this.shapePaint;
    }

    public void setShapePaint(Paint shapePaint) {
        this.shapePaint = shapePaint;
        fireChangeEvent();
    }

    public Stroke getShapeStroke() {
        return this.shapeStroke;
    }

    public void setShapeStroke(Stroke shapeStroke) {
        this.shapeStroke = shapeStroke;
        fireChangeEvent();
    }

    public XYItemRendererState initialise(Graphics2D g2, Rectangle2D dataArea, XYPlot plot, XYDataset data, PlotRenderingInfo info) {
        XYItemRendererState state = new StackedXYAreaRendererState(info);
        state.setProcessVisibleItemsOnly(false);
        return state;
    }

    public int getPassCount() {
        return 2;
    }

    public Range findRangeBounds(XYDataset dataset) {
        if (dataset != null) {
            return DatasetUtilities.findStackedRangeBounds((TableXYDataset) dataset);
        }
        return null;
    }

    public void drawItem(Graphics2D g2, XYItemRendererState state, Rectangle2D dataArea, PlotRenderingInfo info, XYPlot plot, ValueAxis domainAxis, ValueAxis rangeAxis, XYDataset dataset, int series, int item, CrosshairState crosshairState, int pass) {
        PlotOrientation orientation = plot.getOrientation();
        StackedXYAreaRendererState areaState = (StackedXYAreaRendererState) state;
        TableXYDataset tdataset = (TableXYDataset) dataset;
        int itemCount = tdataset.getItemCount();
        double x1 = dataset.getXValue(series, item);
        double y1 = dataset.getYValue(series, item);
        boolean nullPoint = false;
        if (Double.isNaN(y1)) {
            y1 = 0.0d;
            nullPoint = true;
        }
        double ph1 = getPreviousHeight(tdataset, series, item);
        double transX1 = domainAxis.valueToJava2D(x1, dataArea, plot.getDomainAxisEdge());
        double transY1 = rangeAxis.valueToJava2D(y1 + ph1, dataArea, plot.getRangeAxisEdge());
        Paint seriesPaint = getItemPaint(series, item);
        Paint seriesFillPaint = seriesPaint;
        if (getUseFillPaint()) {
            seriesFillPaint = getItemFillPaint(series, item);
        }
        Stroke seriesStroke = getItemStroke(series, item);
        if (pass == 0) {
            double transY2;
            if (item == 0) {
                areaState.setSeriesArea(new Polygon());
                areaState.setLastSeriesPoints(areaState.getCurrentSeriesPoints());
                areaState.setCurrentSeriesPoints(new Stack());
                transY2 = rangeAxis.valueToJava2D(ph1, dataArea, plot.getRangeAxisEdge());
                if (orientation == PlotOrientation.VERTICAL) {
                    areaState.getSeriesArea().addPoint((int) transX1, (int) transY2);
                } else if (orientation == PlotOrientation.HORIZONTAL) {
                    areaState.getSeriesArea().addPoint((int) transY2, (int) transX1);
                }
            }
            if (orientation == PlotOrientation.VERTICAL) {
                Point point = new Point((int) transX1, (int) transY1);
                areaState.getSeriesArea().addPoint((int) point.getX(), (int) point.getY());
                areaState.getCurrentSeriesPoints().push(point);
            } else if (orientation == PlotOrientation.HORIZONTAL) {
                areaState.getSeriesArea().addPoint((int) transY1, (int) transX1);
            }
            if (getPlotLines() && item > 0) {
                double x0 = dataset.getXValue(series, item - 1);
                double y0 = dataset.getYValue(series, item - 1);
                double ph0 = getPreviousHeight(tdataset, series, item - 1);
                double transX0 = domainAxis.valueToJava2D(x0, dataArea, plot.getDomainAxisEdge());
                double transY0 = rangeAxis.valueToJava2D(y0 + ph0, dataArea, plot.getRangeAxisEdge());
                if (orientation == PlotOrientation.VERTICAL) {
                    areaState.getLine().setLine(transX0, transY0, transX1, transY1);
                } else if (orientation == PlotOrientation.HORIZONTAL) {
                    areaState.getLine().setLine(transY0, transX0, transY1, transX1);
                }
                g2.setPaint(seriesPaint);
                g2.setStroke(seriesStroke);
                g2.draw(areaState.getLine());
            }
            if (getPlotArea() && item > 0 && item == itemCount - 1) {
                transY2 = rangeAxis.valueToJava2D(ph1, dataArea, plot.getRangeAxisEdge());
                if (orientation == PlotOrientation.VERTICAL) {
                    areaState.getSeriesArea().addPoint((int) transX1, (int) transY2);
                } else if (orientation == PlotOrientation.HORIZONTAL) {
                    areaState.getSeriesArea().addPoint((int) transY2, (int) transX1);
                }
                if (series != 0) {
                    Stack points = areaState.getLastSeriesPoints();
                    while (!points.empty()) {
                        Point point2 = (Point) points.pop();
                        areaState.getSeriesArea().addPoint((int) point2.getX(), (int) point2.getY());
                    }
                }
                g2.setPaint(seriesFillPaint);
                g2.setStroke(seriesStroke);
                g2.fill(areaState.getSeriesArea());
                if (isOutline()) {
                    g2.setStroke(lookupSeriesOutlineStroke(series));
                    g2.setPaint(lookupSeriesOutlinePaint(series));
                    g2.draw(areaState.getSeriesArea());
                }
            }
            CrosshairState crosshairState2 = crosshairState;
            double d = x1;
            updateCrosshairValues(crosshairState2, d, ph1 + y1, plot.getDomainAxisIndex(domainAxis), plot.getRangeAxisIndex(rangeAxis), transX1, transY1, orientation);
        } else if (pass == 1) {
            Shape shape = null;
            if (getPlotShapes()) {
                shape = getItemShape(series, item);
                if (plot.getOrientation() == PlotOrientation.VERTICAL) {
                    shape = ShapeUtilities.createTranslatedShape(shape, transX1, transY1);
                } else if (plot.getOrientation() == PlotOrientation.HORIZONTAL) {
                    shape = ShapeUtilities.createTranslatedShape(shape, transY1, transX1);
                }
                if (!nullPoint) {
                    if (getShapePaint() != null) {
                        g2.setPaint(getShapePaint());
                    } else {
                        g2.setPaint(seriesPaint);
                    }
                    if (getShapeStroke() != null) {
                        g2.setStroke(getShapeStroke());
                    } else {
                        g2.setStroke(seriesStroke);
                    }
                    g2.draw(shape);
                }
            } else if (plot.getOrientation() == PlotOrientation.VERTICAL) {
                shape = new Rectangle2D.Double(transX1 - BarRenderer.BAR_OUTLINE_WIDTH_THRESHOLD, transY1 - BarRenderer.BAR_OUTLINE_WIDTH_THRESHOLD, 6.0d, 6.0d);
            } else if (plot.getOrientation() == PlotOrientation.HORIZONTAL) {
                shape = new Rectangle2D.Double(transY1 - BarRenderer.BAR_OUTLINE_WIDTH_THRESHOLD, transX1 - BarRenderer.BAR_OUTLINE_WIDTH_THRESHOLD, 6.0d, 6.0d);
            }
            if (state.getInfo() != null) {
                EntityCollection entities = state.getEntityCollection();
                if (entities != null && shape != null && !nullPoint) {
                    String tip = null;
                    XYToolTipGenerator generator = getToolTipGenerator(series, item);
                    if (generator != null) {
                        tip = generator.generateToolTip(dataset, series, item);
                    }
                    String url = null;
                    if (getURLGenerator() != null) {
                        url = getURLGenerator().generateURL(dataset, series, item);
                    }
                    entities.add(new XYItemEntity(shape, dataset, series, item, tip, url));
                }
            }
        }
    }

    protected double getPreviousHeight(TableXYDataset dataset, int series, int index) {
        double result = 0.0d;
        for (int i = 0; i < series; i++) {
            double value = dataset.getYValue(i, index);
            if (!Double.isNaN(value)) {
                result += value;
            }
        }
        return result;
    }

    public boolean equals(Object obj) {
        if (obj == this) {
            return true;
        }
        if (!(obj instanceof StackedXYAreaRenderer) || !super.equals(obj)) {
            return false;
        }
        StackedXYAreaRenderer that = (StackedXYAreaRenderer) obj;
        if (!PaintUtilities.equal(this.shapePaint, that.shapePaint)) {
            return false;
        }
        if (ObjectUtilities.equal(this.shapeStroke, that.shapeStroke)) {
            return true;
        }
        return false;
    }

    public Object clone() throws CloneNotSupportedException {
        return super.clone();
    }

    private void readObject(ObjectInputStream stream) throws IOException, ClassNotFoundException {
        stream.defaultReadObject();
        this.shapePaint = SerialUtilities.readPaint(stream);
        this.shapeStroke = SerialUtilities.readStroke(stream);
    }

    private void writeObject(ObjectOutputStream stream) throws IOException {
        stream.defaultWriteObject();
        SerialUtilities.writePaint(this.shapePaint, stream);
        SerialUtilities.writeStroke(this.shapeStroke, stream);
    }
}
