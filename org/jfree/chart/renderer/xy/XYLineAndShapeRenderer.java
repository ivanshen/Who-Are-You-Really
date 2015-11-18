package org.jfree.chart.renderer.xy;

import java.awt.Graphics2D;
import java.awt.Paint;
import java.awt.Shape;
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
import org.jfree.chart.plot.CrosshairState;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.plot.PlotRenderingInfo;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.util.LineUtilities;
import org.jfree.chart.util.ParamChecks;
import org.jfree.data.xy.XYDataset;
import org.jfree.io.SerialUtilities;
import org.jfree.ui.RectangleEdge;
import org.jfree.util.BooleanList;
import org.jfree.util.ObjectUtilities;
import org.jfree.util.PublicCloneable;
import org.jfree.util.ShapeUtilities;

public class XYLineAndShapeRenderer extends AbstractXYItemRenderer implements XYItemRenderer, Cloneable, PublicCloneable, Serializable {
    private static final long serialVersionUID = -7435246895986425885L;
    private boolean baseLinesVisible;
    private boolean baseShapesFilled;
    private boolean baseShapesVisible;
    private boolean drawOutlines;
    private boolean drawSeriesLineAsPath;
    private transient Shape legendLine;
    private Boolean linesVisible;
    private BooleanList seriesLinesVisible;
    private BooleanList seriesShapesFilled;
    private BooleanList seriesShapesVisible;
    private Boolean shapesFilled;
    private Boolean shapesVisible;
    private boolean useFillPaint;
    private boolean useOutlinePaint;

    public static class State extends XYItemRendererState {
        private boolean lastPointGood;
        public GeneralPath seriesPath;

        public State(PlotRenderingInfo info) {
            super(info);
            this.seriesPath = new GeneralPath();
        }

        public boolean isLastPointGood() {
            return this.lastPointGood;
        }

        public void setLastPointGood(boolean good) {
            this.lastPointGood = good;
        }

        public void startSeriesPass(XYDataset dataset, int series, int firstItem, int lastItem, int pass, int passCount) {
            this.seriesPath.reset();
            this.lastPointGood = false;
            super.startSeriesPass(dataset, series, firstItem, lastItem, pass, passCount);
        }
    }

    public XYLineAndShapeRenderer() {
        this(true, true);
    }

    public XYLineAndShapeRenderer(boolean lines, boolean shapes) {
        this.linesVisible = null;
        this.seriesLinesVisible = new BooleanList();
        this.baseLinesVisible = lines;
        this.legendLine = new Double(-7.0d, 0.0d, 7.0d, 0.0d);
        this.shapesVisible = null;
        this.seriesShapesVisible = new BooleanList();
        this.baseShapesVisible = shapes;
        this.shapesFilled = null;
        this.useFillPaint = false;
        this.seriesShapesFilled = new BooleanList();
        this.baseShapesFilled = true;
        this.drawOutlines = true;
        this.useOutlinePaint = false;
        this.drawSeriesLineAsPath = false;
    }

    public boolean getDrawSeriesLineAsPath() {
        return this.drawSeriesLineAsPath;
    }

    public void setDrawSeriesLineAsPath(boolean flag) {
        if (this.drawSeriesLineAsPath != flag) {
            this.drawSeriesLineAsPath = flag;
            fireChangeEvent();
        }
    }

    public int getPassCount() {
        return 2;
    }

    public boolean getItemLineVisible(int series, int item) {
        Boolean flag = this.linesVisible;
        if (flag == null) {
            flag = getSeriesLinesVisible(series);
        }
        if (flag != null) {
            return flag.booleanValue();
        }
        return this.baseLinesVisible;
    }

    public Boolean getLinesVisible() {
        return this.linesVisible;
    }

    public void setLinesVisible(Boolean visible) {
        this.linesVisible = visible;
        fireChangeEvent();
    }

    public void setLinesVisible(boolean visible) {
        setLinesVisible(Boolean.valueOf(visible));
    }

    public Boolean getSeriesLinesVisible(int series) {
        return this.seriesLinesVisible.getBoolean(series);
    }

    public void setSeriesLinesVisible(int series, Boolean flag) {
        this.seriesLinesVisible.setBoolean(series, flag);
        fireChangeEvent();
    }

    public void setSeriesLinesVisible(int series, boolean visible) {
        setSeriesLinesVisible(series, Boolean.valueOf(visible));
    }

    public boolean getBaseLinesVisible() {
        return this.baseLinesVisible;
    }

    public void setBaseLinesVisible(boolean flag) {
        this.baseLinesVisible = flag;
        fireChangeEvent();
    }

    public Shape getLegendLine() {
        return this.legendLine;
    }

    public void setLegendLine(Shape line) {
        ParamChecks.nullNotPermitted(line, "line");
        this.legendLine = line;
        fireChangeEvent();
    }

    public boolean getItemShapeVisible(int series, int item) {
        Boolean flag = this.shapesVisible;
        if (flag == null) {
            flag = getSeriesShapesVisible(series);
        }
        if (flag != null) {
            return flag.booleanValue();
        }
        return this.baseShapesVisible;
    }

    public Boolean getShapesVisible() {
        return this.shapesVisible;
    }

    public void setShapesVisible(Boolean visible) {
        this.shapesVisible = visible;
        fireChangeEvent();
    }

    public void setShapesVisible(boolean visible) {
        setShapesVisible(Boolean.valueOf(visible));
    }

    public Boolean getSeriesShapesVisible(int series) {
        return this.seriesShapesVisible.getBoolean(series);
    }

    public void setSeriesShapesVisible(int series, boolean visible) {
        setSeriesShapesVisible(series, Boolean.valueOf(visible));
    }

    public void setSeriesShapesVisible(int series, Boolean flag) {
        this.seriesShapesVisible.setBoolean(series, flag);
        fireChangeEvent();
    }

    public boolean getBaseShapesVisible() {
        return this.baseShapesVisible;
    }

    public void setBaseShapesVisible(boolean flag) {
        this.baseShapesVisible = flag;
        fireChangeEvent();
    }

    public boolean getItemShapeFilled(int series, int item) {
        Boolean flag = this.shapesFilled;
        if (flag == null) {
            flag = getSeriesShapesFilled(series);
        }
        if (flag != null) {
            return flag.booleanValue();
        }
        return this.baseShapesFilled;
    }

    public void setShapesFilled(boolean filled) {
        setShapesFilled(Boolean.valueOf(filled));
    }

    public void setShapesFilled(Boolean filled) {
        this.shapesFilled = filled;
        fireChangeEvent();
    }

    public Boolean getSeriesShapesFilled(int series) {
        return this.seriesShapesFilled.getBoolean(series);
    }

    public void setSeriesShapesFilled(int series, boolean flag) {
        setSeriesShapesFilled(series, Boolean.valueOf(flag));
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
        fireChangeEvent();
    }

    public boolean getDrawOutlines() {
        return this.drawOutlines;
    }

    public void setDrawOutlines(boolean flag) {
        this.drawOutlines = flag;
        fireChangeEvent();
    }

    public boolean getUseFillPaint() {
        return this.useFillPaint;
    }

    public void setUseFillPaint(boolean flag) {
        this.useFillPaint = flag;
        fireChangeEvent();
    }

    public boolean getUseOutlinePaint() {
        return this.useOutlinePaint;
    }

    public void setUseOutlinePaint(boolean flag) {
        this.useOutlinePaint = flag;
        fireChangeEvent();
    }

    public XYItemRendererState initialise(Graphics2D g2, Rectangle2D dataArea, XYPlot plot, XYDataset data, PlotRenderingInfo info) {
        return new State(info);
    }

    public void drawItem(Graphics2D g2, XYItemRendererState state, Rectangle2D dataArea, PlotRenderingInfo info, XYPlot plot, ValueAxis domainAxis, ValueAxis rangeAxis, XYDataset dataset, int series, int item, CrosshairState crosshairState, int pass) {
        if (!getItemVisible(series, item)) {
            return;
        }
        if (isLinePass(pass)) {
            if (!getItemLineVisible(series, item)) {
                return;
            }
            if (this.drawSeriesLineAsPath) {
                drawPrimaryLineAsPath(state, g2, plot, dataset, pass, series, item, domainAxis, rangeAxis, dataArea);
            } else {
                drawPrimaryLine(state, g2, plot, dataset, pass, series, item, domainAxis, rangeAxis, dataArea);
            }
        } else if (isItemPass(pass)) {
            EntityCollection entities = null;
            if (!(info == null || info.getOwner() == null)) {
                entities = info.getOwner().getEntityCollection();
            }
            drawSecondaryPass(g2, plot, dataset, pass, series, item, domainAxis, dataArea, rangeAxis, crosshairState, entities);
        }
    }

    protected boolean isLinePass(int pass) {
        return pass == 0;
    }

    protected boolean isItemPass(int pass) {
        return pass == 1;
    }

    protected void drawPrimaryLine(XYItemRendererState state, Graphics2D g2, XYPlot plot, XYDataset dataset, int pass, int series, int item, ValueAxis domainAxis, ValueAxis rangeAxis, Rectangle2D dataArea) {
        if (item != 0) {
            double x1 = dataset.getXValue(series, item);
            double y1 = dataset.getYValue(series, item);
            if (!Double.isNaN(y1) && !Double.isNaN(x1)) {
                double x0 = dataset.getXValue(series, item - 1);
                double y0 = dataset.getYValue(series, item - 1);
                if (!Double.isNaN(y0) && !Double.isNaN(x0)) {
                    RectangleEdge xAxisLocation = plot.getDomainAxisEdge();
                    RectangleEdge yAxisLocation = plot.getRangeAxisEdge();
                    double transX0 = domainAxis.valueToJava2D(x0, dataArea, xAxisLocation);
                    double transY0 = rangeAxis.valueToJava2D(y0, dataArea, yAxisLocation);
                    double transX1 = domainAxis.valueToJava2D(x1, dataArea, xAxisLocation);
                    double transY1 = rangeAxis.valueToJava2D(y1, dataArea, yAxisLocation);
                    if (!Double.isNaN(transX0) && !Double.isNaN(transY0) && !Double.isNaN(transX1) && !Double.isNaN(transY1)) {
                        PlotOrientation orientation = plot.getOrientation();
                        if (orientation == PlotOrientation.HORIZONTAL) {
                            state.workingLine.setLine(transY0, transX0, transY1, transX1);
                        } else if (orientation == PlotOrientation.VERTICAL) {
                            state.workingLine.setLine(transX0, transY0, transX1, transY1);
                        }
                        if (LineUtilities.clipLine(state.workingLine, dataArea)) {
                            Graphics2D graphics2D = g2;
                            int i = pass;
                            int i2 = series;
                            int i3 = item;
                            drawFirstPassShape(graphics2D, i, i2, i3, state.workingLine);
                        }
                    }
                }
            }
        }
    }

    protected void drawFirstPassShape(Graphics2D g2, int pass, int series, int item, Shape shape) {
        g2.setStroke(getItemStroke(series, item));
        g2.setPaint(getItemPaint(series, item));
        g2.draw(shape);
    }

    protected void drawPrimaryLineAsPath(XYItemRendererState state, Graphics2D g2, XYPlot plot, XYDataset dataset, int pass, int series, int item, ValueAxis domainAxis, ValueAxis rangeAxis, Rectangle2D dataArea) {
        RectangleEdge xAxisLocation = plot.getDomainAxisEdge();
        RectangleEdge yAxisLocation = plot.getRangeAxisEdge();
        double x1 = dataset.getXValue(series, item);
        double y1 = dataset.getYValue(series, item);
        double transX1 = domainAxis.valueToJava2D(x1, dataArea, xAxisLocation);
        double transY1 = rangeAxis.valueToJava2D(y1, dataArea, yAxisLocation);
        State s = (State) state;
        if (Double.isNaN(transX1) || Double.isNaN(transY1)) {
            s.setLastPointGood(false);
        } else {
            float x = (float) transX1;
            float y = (float) transY1;
            if (plot.getOrientation() == PlotOrientation.HORIZONTAL) {
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
        if (item == s.getLastItemIndex()) {
            drawFirstPassShape(g2, pass, series, item, s.seriesPath);
        }
    }

    protected void drawSecondaryPass(Graphics2D g2, XYPlot plot, XYDataset dataset, int pass, int series, int item, ValueAxis domainAxis, Rectangle2D dataArea, ValueAxis rangeAxis, CrosshairState crosshairState, EntityCollection entities) {
        Shape entityArea = null;
        double x1 = dataset.getXValue(series, item);
        double y1 = dataset.getYValue(series, item);
        if (!Double.isNaN(y1) && !Double.isNaN(x1)) {
            PlotOrientation orientation = plot.getOrientation();
            RectangleEdge xAxisLocation = plot.getDomainAxisEdge();
            RectangleEdge yAxisLocation = plot.getRangeAxisEdge();
            double transX1 = domainAxis.valueToJava2D(x1, dataArea, xAxisLocation);
            double transY1 = rangeAxis.valueToJava2D(y1, dataArea, yAxisLocation);
            if (getItemShapeVisible(series, item)) {
                Shape shape = getItemShape(series, item);
                if (orientation == PlotOrientation.HORIZONTAL) {
                    shape = ShapeUtilities.createTranslatedShape(shape, transY1, transX1);
                } else if (orientation == PlotOrientation.VERTICAL) {
                    shape = ShapeUtilities.createTranslatedShape(shape, transX1, transY1);
                }
                entityArea = shape;
                if (shape.intersects(dataArea)) {
                    if (getItemShapeFilled(series, item)) {
                        if (this.useFillPaint) {
                            g2.setPaint(getItemFillPaint(series, item));
                        } else {
                            g2.setPaint(getItemPaint(series, item));
                        }
                        g2.fill(shape);
                    }
                    if (this.drawOutlines) {
                        if (getUseOutlinePaint()) {
                            g2.setPaint(getItemOutlinePaint(series, item));
                        } else {
                            g2.setPaint(getItemPaint(series, item));
                        }
                        g2.setStroke(getItemOutlineStroke(series, item));
                        g2.draw(shape);
                    }
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
                addEntity(entities, entityArea, dataset, series, item, xx, yy);
            }
        }
    }

    public LegendItem getLegendItem(int datasetIndex, int series) {
        XYPlot plot = getPlot();
        if (plot == null) {
            return null;
        }
        XYDataset dataset = plot.getDataset(datasetIndex);
        if (dataset == null) {
            return null;
        }
        if (!getItemVisible(series, 0)) {
            return null;
        }
        Paint fillPaint;
        Paint outlinePaint;
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
        boolean shapeIsVisible = getItemShapeVisible(series, 0);
        Shape shape = lookupLegendShape(series);
        boolean shapeIsFilled = getItemShapeFilled(series, 0);
        if (this.useFillPaint) {
            fillPaint = lookupSeriesFillPaint(series);
        } else {
            fillPaint = lookupSeriesPaint(series);
        }
        boolean shapeOutlineVisible = this.drawOutlines;
        if (this.useOutlinePaint) {
            outlinePaint = lookupSeriesOutlinePaint(series);
        } else {
            outlinePaint = lookupSeriesPaint(series);
        }
        LegendItem result = new LegendItem(label, description, toolTipText, urlText, shapeIsVisible, shape, shapeIsFilled, fillPaint, shapeOutlineVisible, outlinePaint, lookupSeriesOutlineStroke(series), getItemLineVisible(series, 0), this.legendLine, lookupSeriesStroke(series), lookupSeriesPaint(series));
        result.setLabelFont(lookupLegendTextFont(series));
        Paint labelPaint = lookupLegendTextPaint(series);
        if (labelPaint != null) {
            result.setLabelPaint(labelPaint);
        }
        result.setSeriesKey(dataset.getSeriesKey(series));
        result.setSeriesIndex(series);
        result.setDataset(dataset);
        result.setDatasetIndex(datasetIndex);
        return result;
    }

    public Object clone() throws CloneNotSupportedException {
        XYLineAndShapeRenderer clone = (XYLineAndShapeRenderer) super.clone();
        clone.seriesLinesVisible = (BooleanList) this.seriesLinesVisible.clone();
        if (this.legendLine != null) {
            clone.legendLine = ShapeUtilities.clone(this.legendLine);
        }
        clone.seriesShapesVisible = (BooleanList) this.seriesShapesVisible.clone();
        clone.seriesShapesFilled = (BooleanList) this.seriesShapesFilled.clone();
        return clone;
    }

    public boolean equals(Object obj) {
        if (obj == this) {
            return true;
        }
        if (!(obj instanceof XYLineAndShapeRenderer)) {
            return false;
        }
        if (!super.equals(obj)) {
            return false;
        }
        XYLineAndShapeRenderer that = (XYLineAndShapeRenderer) obj;
        if (!ObjectUtilities.equal(this.linesVisible, that.linesVisible)) {
            return false;
        }
        if (!ObjectUtilities.equal(this.seriesLinesVisible, that.seriesLinesVisible)) {
            return false;
        }
        if (this.baseLinesVisible != that.baseLinesVisible) {
            return false;
        }
        if (!ShapeUtilities.equal(this.legendLine, that.legendLine)) {
            return false;
        }
        if (!ObjectUtilities.equal(this.shapesVisible, that.shapesVisible)) {
            return false;
        }
        if (!ObjectUtilities.equal(this.seriesShapesVisible, that.seriesShapesVisible)) {
            return false;
        }
        if (this.baseShapesVisible != that.baseShapesVisible) {
            return false;
        }
        if (!ObjectUtilities.equal(this.shapesFilled, that.shapesFilled)) {
            return false;
        }
        if (!ObjectUtilities.equal(this.seriesShapesFilled, that.seriesShapesFilled)) {
            return false;
        }
        if (this.baseShapesFilled != that.baseShapesFilled) {
            return false;
        }
        if (this.drawOutlines != that.drawOutlines) {
            return false;
        }
        if (this.useOutlinePaint != that.useOutlinePaint) {
            return false;
        }
        if (this.useFillPaint != that.useFillPaint) {
            return false;
        }
        if (this.drawSeriesLineAsPath != that.drawSeriesLineAsPath) {
            return false;
        }
        return true;
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
