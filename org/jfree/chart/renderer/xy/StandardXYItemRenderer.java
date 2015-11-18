package org.jfree.chart.renderer.xy;

import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Paint;
import java.awt.Point;
import java.awt.Shape;
import java.awt.Stroke;
import java.awt.geom.GeneralPath;
import java.awt.geom.Line2D.Double;
import java.awt.geom.Rectangle2D;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import org.jfree.chart.LegendItem;
import org.jfree.chart.axis.ValueAxis;
import org.jfree.chart.entity.EntityCollection;
import org.jfree.chart.labels.XYToolTipGenerator;
import org.jfree.chart.plot.CrosshairState;
import org.jfree.chart.plot.Plot;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.plot.PlotRenderingInfo;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.urls.XYURLGenerator;
import org.jfree.chart.util.ParamChecks;
import org.jfree.data.xy.NormalizedMatrixSeries;
import org.jfree.data.xy.XYDataset;
import org.jfree.io.SerialUtilities;
import org.jfree.ui.RectangleEdge;
import org.jfree.util.BooleanList;
import org.jfree.util.BooleanUtilities;
import org.jfree.util.ObjectUtilities;
import org.jfree.util.PublicCloneable;
import org.jfree.util.ShapeUtilities;
import org.jfree.util.UnitType;

public class StandardXYItemRenderer extends AbstractXYItemRenderer implements XYItemRenderer, Cloneable, PublicCloneable, Serializable {
    public static final int DISCONTINUOUS = 8;
    public static final int DISCONTINUOUS_LINES = 10;
    public static final int IMAGES = 4;
    public static final int LINES = 2;
    public static final int SHAPES = 1;
    public static final int SHAPES_AND_LINES = 3;
    private static final long serialVersionUID = -3271351259436865995L;
    private boolean baseShapesFilled;
    private boolean baseShapesVisible;
    private boolean drawSeriesLineAsPath;
    private double gapThreshold;
    private UnitType gapThresholdType;
    private transient Shape legendLine;
    private boolean plotDiscontinuous;
    private boolean plotImages;
    private boolean plotLines;
    private BooleanList seriesShapesFilled;
    private Boolean shapesFilled;

    public static class State extends XYItemRendererState {
        private boolean lastPointGood;
        private int seriesIndex;
        public GeneralPath seriesPath;

        public State(PlotRenderingInfo info) {
            super(info);
        }

        public boolean isLastPointGood() {
            return this.lastPointGood;
        }

        public void setLastPointGood(boolean good) {
            this.lastPointGood = good;
        }

        public int getSeriesIndex() {
            return this.seriesIndex;
        }

        public void setSeriesIndex(int index) {
            this.seriesIndex = index;
        }
    }

    public StandardXYItemRenderer() {
        this(LINES, null);
    }

    public StandardXYItemRenderer(int type) {
        this(type, null);
    }

    public StandardXYItemRenderer(int type, XYToolTipGenerator toolTipGenerator) {
        this(type, toolTipGenerator, null);
    }

    public StandardXYItemRenderer(int type, XYToolTipGenerator toolTipGenerator, XYURLGenerator urlGenerator) {
        this.gapThresholdType = UnitType.RELATIVE;
        this.gapThreshold = NormalizedMatrixSeries.DEFAULT_SCALE_FACTOR;
        setBaseToolTipGenerator(toolTipGenerator);
        setURLGenerator(urlGenerator);
        if ((type & SHAPES) != 0) {
            this.baseShapesVisible = true;
        }
        if ((type & LINES) != 0) {
            this.plotLines = true;
        }
        if ((type & IMAGES) != 0) {
            this.plotImages = true;
        }
        if ((type & DISCONTINUOUS) != 0) {
            this.plotDiscontinuous = true;
        }
        this.shapesFilled = null;
        this.seriesShapesFilled = new BooleanList();
        this.baseShapesFilled = true;
        this.legendLine = new Double(-7.0d, 0.0d, 7.0d, 0.0d);
        this.drawSeriesLineAsPath = false;
    }

    public boolean getBaseShapesVisible() {
        return this.baseShapesVisible;
    }

    public void setBaseShapesVisible(boolean flag) {
        if (this.baseShapesVisible != flag) {
            this.baseShapesVisible = flag;
            fireChangeEvent();
        }
    }

    public boolean getItemShapeFilled(int series, int item) {
        if (this.shapesFilled != null) {
            return this.shapesFilled.booleanValue();
        }
        Boolean flag = this.seriesShapesFilled.getBoolean(series);
        if (flag != null) {
            return flag.booleanValue();
        }
        return this.baseShapesFilled;
    }

    public Boolean getShapesFilled() {
        return this.shapesFilled;
    }

    public void setShapesFilled(boolean filled) {
        setShapesFilled(BooleanUtilities.valueOf(filled));
    }

    public void setShapesFilled(Boolean filled) {
        this.shapesFilled = filled;
        fireChangeEvent();
    }

    public Boolean getSeriesShapesFilled(int series) {
        return this.seriesShapesFilled.getBoolean(series);
    }

    public void setSeriesShapesFilled(int series, Boolean flag) {
        this.seriesShapesFilled.setBoolean(series, flag);
        fireChangeEvent();
    }

    public boolean getBaseShapesFilled() {
        return this.baseShapesFilled;
    }

    public void setBaseShapesFilled(boolean flag) {
        this.baseShapesFilled = flag;
    }

    public boolean getPlotLines() {
        return this.plotLines;
    }

    public void setPlotLines(boolean flag) {
        if (this.plotLines != flag) {
            this.plotLines = flag;
            fireChangeEvent();
        }
    }

    public UnitType getGapThresholdType() {
        return this.gapThresholdType;
    }

    public void setGapThresholdType(UnitType thresholdType) {
        ParamChecks.nullNotPermitted(thresholdType, "thresholdType");
        this.gapThresholdType = thresholdType;
        fireChangeEvent();
    }

    public double getGapThreshold() {
        return this.gapThreshold;
    }

    public void setGapThreshold(double t) {
        this.gapThreshold = t;
        fireChangeEvent();
    }

    public boolean getPlotImages() {
        return this.plotImages;
    }

    public void setPlotImages(boolean flag) {
        if (this.plotImages != flag) {
            this.plotImages = flag;
            fireChangeEvent();
        }
    }

    public boolean getPlotDiscontinuous() {
        return this.plotDiscontinuous;
    }

    public void setPlotDiscontinuous(boolean flag) {
        if (this.plotDiscontinuous != flag) {
            this.plotDiscontinuous = flag;
            fireChangeEvent();
        }
    }

    public boolean getDrawSeriesLineAsPath() {
        return this.drawSeriesLineAsPath;
    }

    public void setDrawSeriesLineAsPath(boolean flag) {
        this.drawSeriesLineAsPath = flag;
    }

    public Shape getLegendLine() {
        return this.legendLine;
    }

    public void setLegendLine(Shape line) {
        ParamChecks.nullNotPermitted(line, "line");
        this.legendLine = line;
        fireChangeEvent();
    }

    public LegendItem getLegendItem(int datasetIndex, int series) {
        XYPlot plot = getPlot();
        if (plot == null) {
            return null;
        }
        XYDataset dataset = plot.getDataset(datasetIndex);
        if (dataset == null || !getItemVisible(series, 0)) {
            return null;
        }
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
        Shape shape = lookupLegendShape(series);
        boolean shapeFilled = getItemShapeFilled(series, 0);
        Paint paint = lookupSeriesPaint(series);
        Paint linePaint = paint;
        Stroke lineStroke = lookupSeriesStroke(series);
        LegendItem result = new LegendItem(label, description, toolTipText, urlText, this.baseShapesVisible, shape, shapeFilled, paint, !shapeFilled, paint, lineStroke, this.plotLines, this.legendLine, lineStroke, linePaint);
        result.setLabelFont(lookupLegendTextFont(series));
        Paint labelPaint = lookupLegendTextPaint(series);
        if (labelPaint != null) {
            result.setLabelPaint(labelPaint);
        }
        result.setDataset(dataset);
        result.setDatasetIndex(datasetIndex);
        result.setSeriesKey(dataset.getSeriesKey(series));
        result.setSeriesIndex(series);
        return result;
    }

    public XYItemRendererState initialise(Graphics2D g2, Rectangle2D dataArea, XYPlot plot, XYDataset data, PlotRenderingInfo info) {
        State state = new State(info);
        state.seriesPath = new GeneralPath();
        state.seriesIndex = -1;
        return state;
    }

    public void drawItem(Graphics2D g2, XYItemRendererState state, Rectangle2D dataArea, PlotRenderingInfo info, XYPlot plot, ValueAxis domainAxis, ValueAxis rangeAxis, XYDataset dataset, int series, int item, CrosshairState crosshairState, int pass) {
        boolean itemVisible = getItemVisible(series, item);
        EntityCollection entities = null;
        if (info != null) {
            entities = info.getOwner().getEntityCollection();
        }
        PlotOrientation orientation = plot.getOrientation();
        Paint paint = getItemPaint(series, item);
        Stroke seriesStroke = getItemStroke(series, item);
        g2.setPaint(paint);
        g2.setStroke(seriesStroke);
        double x1 = dataset.getXValue(series, item);
        double y1 = dataset.getYValue(series, item);
        if (Double.isNaN(x1) || Double.isNaN(y1)) {
            itemVisible = false;
        }
        RectangleEdge xAxisLocation = plot.getDomainAxisEdge();
        RectangleEdge yAxisLocation = plot.getRangeAxisEdge();
        double transX1 = domainAxis.valueToJava2D(x1, dataArea, xAxisLocation);
        double transY1 = rangeAxis.valueToJava2D(y1, dataArea, yAxisLocation);
        if (getPlotLines()) {
            if (this.drawSeriesLineAsPath) {
                State s = (State) state;
                if (s.getSeriesIndex() != series) {
                    s.seriesPath.reset();
                    s.lastPointGood = false;
                    s.setSeriesIndex(series);
                }
                if (!itemVisible || Double.isNaN(transX1) || Double.isNaN(transY1)) {
                    s.setLastPointGood(false);
                } else {
                    float x = (float) transX1;
                    float y = (float) transY1;
                    if (orientation == PlotOrientation.HORIZONTAL) {
                        x = (float) transY1;
                        y = (float) transX1;
                    }
                    if (s.isLastPointGood()) {
                        s.seriesPath.lineTo(x, y);
                    } else {
                        s.seriesPath.moveTo(x, y);
                    }
                    s.setLastPointGood(true);
                }
                if (item == dataset.getItemCount(series) - 1 && s.seriesIndex == series) {
                    g2.setStroke(lookupSeriesStroke(series));
                    g2.setPaint(lookupSeriesPaint(series));
                    g2.draw(s.seriesPath);
                }
            } else if (item != 0 && itemVisible) {
                double x0 = dataset.getXValue(series, item - 1);
                double y0 = dataset.getYValue(series, item - 1);
                if (!(Double.isNaN(x0) || Double.isNaN(y0))) {
                    boolean drawLine = true;
                    if (getPlotDiscontinuous()) {
                        int numX = dataset.getItemCount(series);
                        double minX = dataset.getXValue(series, 0);
                        double maxX = dataset.getXValue(series, numX - 1);
                        if (this.gapThresholdType == UnitType.ABSOLUTE) {
                            drawLine = Math.abs(x1 - x0) <= this.gapThreshold;
                        } else {
                            drawLine = Math.abs(x1 - x0) <= ((maxX - minX) / ((double) numX)) * getGapThreshold();
                        }
                    }
                    if (drawLine) {
                        double transX0 = domainAxis.valueToJava2D(x0, dataArea, xAxisLocation);
                        double transY0 = rangeAxis.valueToJava2D(y0, dataArea, yAxisLocation);
                        if (Double.isNaN(transX0) || Double.isNaN(transY0) || Double.isNaN(transX1) || Double.isNaN(transY1)) {
                            Shape shape = null;
                            return;
                        }
                        if (orientation == PlotOrientation.HORIZONTAL) {
                            state.workingLine.setLine(transY0, transX0, transY1, transX1);
                        } else if (orientation == PlotOrientation.VERTICAL) {
                            state.workingLine.setLine(transX0, transY0, transX1, transY1);
                        }
                        if (state.workingLine.intersects(dataArea)) {
                            g2.draw(state.workingLine);
                        }
                    }
                }
            }
        }
        if (itemVisible) {
            if (getBaseShapesVisible()) {
                Shape shape2 = getItemShape(series, item);
                if (orientation == PlotOrientation.HORIZONTAL) {
                    shape2 = ShapeUtilities.createTranslatedShape(shape2, transY1, transX1);
                } else if (orientation == PlotOrientation.VERTICAL) {
                    shape2 = ShapeUtilities.createTranslatedShape(shape2, transX1, transY1);
                }
                if (shape2.intersects(dataArea)) {
                    if (getItemShapeFilled(series, item)) {
                        g2.fill(shape2);
                    } else {
                        g2.draw(shape2);
                    }
                }
                shape = shape2;
            } else {
                shape = null;
            }
            if (getPlotImages()) {
                Image image = getImage(plot, series, item, transX1, transY1);
                if (image != null) {
                    Point hotspot = getImageHotspot(plot, series, item, transX1, transY1, image);
                    g2.drawImage(image, (int) (transX1 - hotspot.getX()), (int) (transY1 - hotspot.getY()), null);
                    shape = new Rectangle2D.Double(transX1 - hotspot.getX(), transY1 - hotspot.getY(), (double) image.getWidth(null), (double) image.getHeight(null));
                }
            }
            double xx = transX1;
            double yy = transY1;
            if (orientation == PlotOrientation.HORIZONTAL) {
                xx = transY1;
                yy = transX1;
            }
            if (isItemLabelVisible(series, item)) {
                drawItemLabel(g2, orientation, dataset, series, item, xx, yy, y1 < 0.0d);
            }
            updateCrosshairValues(crosshairState, x1, y1, plot.getDomainAxisIndex(domainAxis), plot.getRangeAxisIndex(rangeAxis), transX1, transY1, orientation);
            if (entities != null && AbstractXYItemRenderer.isPointInRect(dataArea, xx, yy)) {
                addEntity(entities, shape, dataset, series, item, xx, yy);
                return;
            }
            return;
        }
        shape = null;
    }

    public boolean equals(Object obj) {
        if (obj == this) {
            return true;
        }
        if (!(obj instanceof StandardXYItemRenderer)) {
            return false;
        }
        StandardXYItemRenderer that = (StandardXYItemRenderer) obj;
        if (this.baseShapesVisible == that.baseShapesVisible && this.plotLines == that.plotLines && this.plotImages == that.plotImages && this.plotDiscontinuous == that.plotDiscontinuous && this.gapThresholdType == that.gapThresholdType && this.gapThreshold == that.gapThreshold && ObjectUtilities.equal(this.shapesFilled, that.shapesFilled) && this.seriesShapesFilled.equals(that.seriesShapesFilled) && this.baseShapesFilled == that.baseShapesFilled && this.drawSeriesLineAsPath == that.drawSeriesLineAsPath && ShapeUtilities.equal(this.legendLine, that.legendLine)) {
            return super.equals(obj);
        }
        return false;
    }

    public Object clone() throws CloneNotSupportedException {
        StandardXYItemRenderer clone = (StandardXYItemRenderer) super.clone();
        clone.seriesShapesFilled = (BooleanList) this.seriesShapesFilled.clone();
        clone.legendLine = ShapeUtilities.clone(this.legendLine);
        return clone;
    }

    protected Image getImage(Plot plot, int series, int item, double x, double y) {
        return null;
    }

    protected Point getImageHotspot(Plot plot, int series, int item, double x, double y, Image image) {
        return new Point(image.getWidth(null) / LINES, image.getHeight(null) / LINES);
    }

    private void readObject(ObjectInputStream stream) throws IOException, ClassNotFoundException {
        stream.defaultReadObject();
        this.legendLine = SerialUtilities.readShape(stream);
    }

    private void writeObject(ObjectOutputStream stream) throws IOException {
        stream.defaultWriteObject();
        SerialUtilities.writeShape(this.legendLine, stream);
    }
}
