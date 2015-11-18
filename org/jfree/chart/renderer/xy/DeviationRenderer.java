package org.jfree.chart.renderer.xy;

import java.awt.AlphaComposite;
import java.awt.Composite;
import java.awt.Graphics2D;
import java.awt.geom.GeneralPath;
import java.awt.geom.Rectangle2D;
import java.util.ArrayList;
import java.util.List;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.ValueAxis;
import org.jfree.chart.entity.EntityCollection;
import org.jfree.chart.plot.CrosshairState;
import org.jfree.chart.plot.Plot;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.plot.PlotRenderingInfo;
import org.jfree.chart.plot.XYPlot;
import org.jfree.data.Range;
import org.jfree.data.xy.IntervalXYDataset;
import org.jfree.data.xy.XYDataset;
import org.jfree.ui.RectangleEdge;

public class DeviationRenderer extends XYLineAndShapeRenderer {
    private float alpha;

    public static class State extends org.jfree.chart.renderer.xy.XYLineAndShapeRenderer.State {
        public List lowerCoordinates;
        public List upperCoordinates;

        public State(PlotRenderingInfo info) {
            super(info);
            this.lowerCoordinates = new ArrayList();
            this.upperCoordinates = new ArrayList();
        }
    }

    public DeviationRenderer() {
        this(true, true);
    }

    public DeviationRenderer(boolean lines, boolean shapes) {
        super(lines, shapes);
        super.setDrawSeriesLineAsPath(true);
        this.alpha = JFreeChart.DEFAULT_BACKGROUND_IMAGE_ALPHA;
    }

    public float getAlpha() {
        return this.alpha;
    }

    public void setAlpha(float alpha) {
        if (alpha < 0.0f || alpha > Plot.DEFAULT_FOREGROUND_ALPHA) {
            throw new IllegalArgumentException("Requires 'alpha' in the range 0.0 to 1.0.");
        }
        this.alpha = alpha;
        fireChangeEvent();
    }

    public void setDrawSeriesLineAsPath(boolean flag) {
    }

    public Range findRangeBounds(XYDataset dataset) {
        return findRangeBounds(dataset, true);
    }

    public XYItemRendererState initialise(Graphics2D g2, Rectangle2D dataArea, XYPlot plot, XYDataset dataset, PlotRenderingInfo info) {
        State state = new State(info);
        state.seriesPath = new GeneralPath();
        state.setProcessVisibleItemsOnly(false);
        return state;
    }

    public int getPassCount() {
        return 3;
    }

    protected boolean isItemPass(int pass) {
        return pass == 2;
    }

    protected boolean isLinePass(int pass) {
        return pass == 1;
    }

    public void drawItem(Graphics2D g2, XYItemRendererState state, Rectangle2D dataArea, PlotRenderingInfo info, XYPlot plot, ValueAxis domainAxis, ValueAxis rangeAxis, XYDataset dataset, int series, int item, CrosshairState crosshairState, int pass) {
        if (getItemVisible(series, item)) {
            if (pass == 0) {
                IntervalXYDataset intervalDataset = (IntervalXYDataset) dataset;
                State drState = (State) state;
                double x = intervalDataset.getXValue(series, item);
                double yLow = intervalDataset.getStartYValue(series, item);
                double yHigh = intervalDataset.getEndYValue(series, item);
                RectangleEdge xAxisLocation = plot.getDomainAxisEdge();
                RectangleEdge yAxisLocation = plot.getRangeAxisEdge();
                double xx = domainAxis.valueToJava2D(x, dataArea, xAxisLocation);
                double yyLow = rangeAxis.valueToJava2D(yLow, dataArea, yAxisLocation);
                double yyHigh = rangeAxis.valueToJava2D(yHigh, dataArea, yAxisLocation);
                PlotOrientation orientation = plot.getOrientation();
                if (orientation == PlotOrientation.HORIZONTAL) {
                    drState.lowerCoordinates.add(new double[]{yyLow, xx});
                    drState.upperCoordinates.add(new double[]{yyHigh, xx});
                } else if (orientation == PlotOrientation.VERTICAL) {
                    drState.lowerCoordinates.add(new double[]{xx, yyLow});
                    drState.upperCoordinates.add(new double[]{xx, yyHigh});
                }
                if (item == dataset.getItemCount(series) - 1) {
                    int i;
                    Composite originalComposite = g2.getComposite();
                    g2.setComposite(AlphaComposite.getInstance(3, this.alpha));
                    g2.setPaint(getItemFillPaint(series, item));
                    GeneralPath generalPath = new GeneralPath(1, drState.lowerCoordinates.size() + drState.upperCoordinates.size());
                    double[] coords = (double[]) drState.lowerCoordinates.get(0);
                    generalPath.moveTo((float) coords[0], (float) coords[1]);
                    for (i = 1; i < drState.lowerCoordinates.size(); i++) {
                        coords = (double[]) drState.lowerCoordinates.get(i);
                        generalPath.lineTo((float) coords[0], (float) coords[1]);
                    }
                    int count = drState.upperCoordinates.size();
                    coords = (double[]) drState.upperCoordinates.get(count - 1);
                    generalPath.lineTo((float) coords[0], (float) coords[1]);
                    for (i = count - 2; i >= 0; i--) {
                        coords = (double[]) drState.upperCoordinates.get(i);
                        generalPath.lineTo((float) coords[0], (float) coords[1]);
                    }
                    generalPath.closePath();
                    g2.fill(generalPath);
                    g2.setComposite(originalComposite);
                    drState.lowerCoordinates.clear();
                    drState.upperCoordinates.clear();
                }
            }
            if (isLinePass(pass)) {
                if (item == 0) {
                    State s = (State) state;
                    s.seriesPath.reset();
                    s.setLastPointGood(false);
                }
                if (getItemLineVisible(series, item)) {
                    drawPrimaryLineAsPath(state, g2, plot, dataset, pass, series, item, domainAxis, rangeAxis, dataArea);
                }
            } else if (isItemPass(pass)) {
                EntityCollection entities = null;
                if (info != null) {
                    entities = info.getOwner().getEntityCollection();
                }
                drawSecondaryPass(g2, plot, dataset, pass, series, item, domainAxis, dataArea, rangeAxis, crosshairState, entities);
            }
        }
    }

    public boolean equals(Object obj) {
        if (obj == this) {
            return true;
        }
        if (!(obj instanceof DeviationRenderer)) {
            return false;
        }
        if (this.alpha == ((DeviationRenderer) obj).alpha) {
            return super.equals(obj);
        }
        return false;
    }
}
