package org.jfree.chart.renderer.xy;

import java.awt.Graphics2D;
import java.awt.Shape;
import java.awt.geom.GeneralPath;
import java.awt.geom.Line2D.Double;
import java.awt.geom.PathIterator;
import java.awt.geom.Rectangle2D;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import org.jfree.chart.axis.ValueAxis;
import org.jfree.chart.plot.CrosshairState;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.plot.PlotRenderingInfo;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.util.ParamChecks;
import org.jfree.data.xy.NormalizedMatrixSeries;
import org.jfree.data.xy.XYDataset;
import org.jfree.io.SerialUtilities;
import org.jfree.ui.RectangleEdge;
import org.jfree.util.PublicCloneable;
import org.jfree.util.ShapeUtilities;

public class SamplingXYLineRenderer extends AbstractXYItemRenderer implements XYItemRenderer, Cloneable, PublicCloneable, Serializable {
    private transient Shape legendLine;

    public static class State extends XYItemRendererState {
        double closeY;
        double dX;
        double highY;
        GeneralPath intervalPath;
        boolean lastPointGood;
        double lastX;
        double lowY;
        double openY;
        GeneralPath seriesPath;

        public State(PlotRenderingInfo info) {
            super(info);
            this.dX = NormalizedMatrixSeries.DEFAULT_SCALE_FACTOR;
            this.openY = 0.0d;
            this.highY = 0.0d;
            this.lowY = 0.0d;
            this.closeY = 0.0d;
        }

        public void startSeriesPass(XYDataset dataset, int series, int firstItem, int lastItem, int pass, int passCount) {
            this.seriesPath.reset();
            this.intervalPath.reset();
            this.lastPointGood = false;
            super.startSeriesPass(dataset, series, firstItem, lastItem, pass, passCount);
        }
    }

    public SamplingXYLineRenderer() {
        this.legendLine = new Double(-7.0d, 0.0d, 7.0d, 0.0d);
        setBaseLegendShape(this.legendLine);
        setTreatLegendShapeAsLine(true);
    }

    public Shape getLegendLine() {
        return this.legendLine;
    }

    public void setLegendLine(Shape line) {
        ParamChecks.nullNotPermitted(line, "line");
        this.legendLine = line;
        fireChangeEvent();
    }

    public int getPassCount() {
        return 1;
    }

    public XYItemRendererState initialise(Graphics2D g2, Rectangle2D dataArea, XYPlot plot, XYDataset data, PlotRenderingInfo info) {
        State state = new State(info);
        state.seriesPath = new GeneralPath();
        state.intervalPath = new GeneralPath();
        state.dX = 72.0d / 72.0d;
        return state;
    }

    public void drawItem(Graphics2D g2, XYItemRendererState state, Rectangle2D dataArea, PlotRenderingInfo info, XYPlot plot, ValueAxis domainAxis, ValueAxis rangeAxis, XYDataset dataset, int series, int item, CrosshairState crosshairState, int pass) {
        if (getItemVisible(series, item)) {
            RectangleEdge xAxisLocation = plot.getDomainAxisEdge();
            RectangleEdge yAxisLocation = plot.getRangeAxisEdge();
            double x1 = dataset.getXValue(series, item);
            double y1 = dataset.getYValue(series, item);
            double transX1 = domainAxis.valueToJava2D(x1, dataArea, xAxisLocation);
            double transY1 = rangeAxis.valueToJava2D(y1, dataArea, yAxisLocation);
            State s = (State) state;
            if (Double.isNaN(transX1) || Double.isNaN(transY1)) {
                s.lastPointGood = false;
            } else {
                float x = (float) transX1;
                float y = (float) transY1;
                if (plot.getOrientation() == PlotOrientation.HORIZONTAL) {
                    x = (float) transY1;
                    y = (float) transX1;
                }
                if (s.lastPointGood) {
                    double d = (double) x;
                    double d2 = s.lastX;
                    if (Math.abs(r0 - r0) > s.dX) {
                        s.seriesPath.lineTo(x, y);
                        if (s.lowY < s.highY) {
                            s.intervalPath.moveTo((float) s.lastX, (float) s.lowY);
                            s.intervalPath.lineTo((float) s.lastX, (float) s.highY);
                        }
                        s.lastX = (double) x;
                        s.openY = (double) y;
                        s.highY = (double) y;
                        s.lowY = (double) y;
                        s.closeY = (double) y;
                    } else {
                        s.highY = Math.max(s.highY, (double) y);
                        s.lowY = Math.min(s.lowY, (double) y);
                        s.closeY = (double) y;
                    }
                } else {
                    s.seriesPath.moveTo(x, y);
                    s.lastX = (double) x;
                    s.openY = (double) y;
                    s.highY = (double) y;
                    s.lowY = (double) y;
                    s.closeY = (double) y;
                }
                s.lastPointGood = true;
            }
            if (item == s.getLastItemIndex()) {
                PathIterator pi = s.seriesPath.getPathIterator(null);
                int count = 0;
                while (!pi.isDone()) {
                    count++;
                    pi.next();
                }
                g2.setStroke(getItemStroke(series, item));
                g2.setPaint(getItemPaint(series, item));
                g2.draw(s.seriesPath);
                g2.draw(s.intervalPath);
            }
        }
    }

    public Object clone() throws CloneNotSupportedException {
        SamplingXYLineRenderer clone = (SamplingXYLineRenderer) super.clone();
        if (this.legendLine != null) {
            clone.legendLine = ShapeUtilities.clone(this.legendLine);
        }
        return clone;
    }

    public boolean equals(Object obj) {
        if (obj == this) {
            return true;
        }
        if (!(obj instanceof SamplingXYLineRenderer)) {
            return false;
        }
        if (!super.equals(obj)) {
            return false;
        }
        if (ShapeUtilities.equal(this.legendLine, ((SamplingXYLineRenderer) obj).legendLine)) {
            return true;
        }
        return false;
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
