package org.jfree.chart.renderer.xy;

import java.awt.GradientPaint;
import java.awt.Graphics2D;
import java.awt.Paint;
import java.awt.geom.GeneralPath;
import java.awt.geom.Point2D;
import java.awt.geom.Point2D.Float;
import java.awt.geom.Rectangle2D;
import java.util.ArrayList;
import java.util.List;
import org.jfree.chart.axis.ValueAxis;
import org.jfree.chart.plot.MeterPlot;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.plot.PlotRenderingInfo;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.renderer.xy.XYLineAndShapeRenderer.State;
import org.jfree.chart.util.ParamChecks;
import org.jfree.data.xy.XYDataset;
import org.jfree.ui.GradientPaintTransformer;
import org.jfree.ui.RectangleEdge;
import org.jfree.ui.StandardGradientPaintTransformer;
import org.jfree.util.ObjectUtilities;

public class XYSplineRenderer extends XYLineAndShapeRenderer {
    private FillType fillType;
    private GradientPaintTransformer gradientPaintTransformer;
    private int precision;

    public enum FillType {
        NONE,
        TO_ZERO,
        TO_LOWER_BOUND,
        TO_UPPER_BOUND
    }

    public static class XYSplineState extends State {
        public GeneralPath fillArea;
        public List<Point2D> points;

        public XYSplineState(PlotRenderingInfo info) {
            super(info);
            this.fillArea = new GeneralPath();
            this.points = new ArrayList();
        }
    }

    public XYSplineRenderer() {
        this(5, FillType.NONE);
    }

    public XYSplineRenderer(int precision) {
        this(precision, FillType.NONE);
    }

    public XYSplineRenderer(int precision, FillType fillType) {
        if (precision <= 0) {
            throw new IllegalArgumentException("Requires precision > 0.");
        }
        ParamChecks.nullNotPermitted(fillType, "fillType");
        this.precision = precision;
        this.fillType = fillType;
        this.gradientPaintTransformer = new StandardGradientPaintTransformer();
    }

    public int getPrecision() {
        return this.precision;
    }

    public void setPrecision(int p) {
        if (p <= 0) {
            throw new IllegalArgumentException("Requires p > 0.");
        }
        this.precision = p;
        fireChangeEvent();
    }

    public FillType getFillType() {
        return this.fillType;
    }

    public void setFillType(FillType fillType) {
        this.fillType = fillType;
        fireChangeEvent();
    }

    public GradientPaintTransformer getGradientPaintTransformer() {
        return this.gradientPaintTransformer;
    }

    public void setGradientPaintTransformer(GradientPaintTransformer gpt) {
        this.gradientPaintTransformer = gpt;
        fireChangeEvent();
    }

    public XYItemRendererState initialise(Graphics2D g2, Rectangle2D dataArea, XYPlot plot, XYDataset data, PlotRenderingInfo info) {
        setDrawSeriesLineAsPath(true);
        XYSplineState state = new XYSplineState(info);
        state.setProcessVisibleItemsOnly(false);
        return state;
    }

    protected void drawPrimaryLineAsPath(XYItemRendererState state, Graphics2D g2, XYPlot plot, XYDataset dataset, int pass, int series, int item, ValueAxis xAxis, ValueAxis yAxis, Rectangle2D dataArea) {
        Float floatR;
        XYSplineState s = (XYSplineState) state;
        RectangleEdge xAxisLocation = plot.getDomainAxisEdge();
        RectangleEdge yAxisLocation = plot.getRangeAxisEdge();
        double x1 = dataset.getXValue(series, item);
        double y1 = dataset.getYValue(series, item);
        double transX1 = xAxis.valueToJava2D(x1, dataArea, xAxisLocation);
        double transY1 = yAxis.valueToJava2D(y1, dataArea, yAxisLocation);
        if (!(Double.isNaN(transX1) || Double.isNaN(transY1))) {
            if (plot.getOrientation() == PlotOrientation.HORIZONTAL) {
                floatR = new Float((float) transY1, (float) transX1);
            } else {
                floatR = new Float((float) transX1, (float) transY1);
            }
            if (!s.points.contains(p)) {
                s.points.add(p);
            }
        }
        if (item == dataset.getItemCount(series) - 1) {
            if (s.points.size() > 1) {
                if (this.fillType == FillType.TO_ZERO) {
                    float xz = (float) xAxis.valueToJava2D(0.0d, dataArea, yAxisLocation);
                    float yz = (float) yAxis.valueToJava2D(0.0d, dataArea, yAxisLocation);
                    if (plot.getOrientation() == PlotOrientation.HORIZONTAL) {
                        floatR = new Float(yz, xz);
                    } else {
                        floatR = new Float(xz, yz);
                    }
                } else if (this.fillType == FillType.TO_LOWER_BOUND) {
                    float xlb = (float) xAxis.valueToJava2D(xAxis.getLowerBound(), dataArea, xAxisLocation);
                    float ylb = (float) yAxis.valueToJava2D(yAxis.getLowerBound(), dataArea, yAxisLocation);
                    if (plot.getOrientation() == PlotOrientation.HORIZONTAL) {
                        floatR = new Float(ylb, xlb);
                    } else {
                        floatR = new Float(xlb, ylb);
                    }
                } else {
                    float xub = (float) xAxis.valueToJava2D(xAxis.getUpperBound(), dataArea, xAxisLocation);
                    float yub = (float) yAxis.valueToJava2D(yAxis.getUpperBound(), dataArea, yAxisLocation);
                    if (plot.getOrientation() == PlotOrientation.HORIZONTAL) {
                        floatR = new Float(yub, xub);
                    } else {
                        floatR = new Float(xub, yub);
                    }
                }
                Point2D cp0 = (Point2D) s.points.get(0);
                s.seriesPath.moveTo(cp0.getX(), cp0.getY());
                if (this.fillType != FillType.NONE) {
                    if (plot.getOrientation() == PlotOrientation.HORIZONTAL) {
                        s.fillArea.moveTo(origin.getX(), cp0.getY());
                    } else {
                        s.fillArea.moveTo(cp0.getX(), origin.getY());
                    }
                    s.fillArea.lineTo(cp0.getX(), cp0.getY());
                }
                if (s.points.size() == 2) {
                    Point2D cp1 = (Point2D) s.points.get(1);
                    if (this.fillType != FillType.NONE) {
                        s.fillArea.lineTo(cp1.getX(), cp1.getY());
                        s.fillArea.lineTo(cp1.getX(), origin.getY());
                        s.fillArea.closePath();
                    }
                    s.seriesPath.lineTo(cp1.getX(), cp1.getY());
                } else {
                    int i;
                    int np = s.points.size();
                    float[] d = new float[np];
                    float[] x = new float[np];
                    float[] a = new float[np];
                    float[] h = new float[np];
                    for (i = 0; i < np; i++) {
                        Float cpi = (Float) s.points.get(i);
                        x[i] = cpi.x;
                        d[i] = cpi.y;
                    }
                    for (i = 1; i <= np - 1; i++) {
                        h[i] = x[i] - x[i - 1];
                    }
                    float[] sub = new float[(np - 1)];
                    float[] diag = new float[(np - 1)];
                    float[] sup = new float[(np - 1)];
                    for (i = 1; i <= np - 2; i++) {
                        diag[i] = (h[i] + h[i + 1]) / MeterPlot.DEFAULT_BORDER_SIZE;
                        sup[i] = h[i + 1] / 6.0f;
                        sub[i] = h[i] / 6.0f;
                        a[i] = ((d[i + 1] - d[i]) / h[i + 1]) - ((d[i] - d[i - 1]) / h[i]);
                    }
                    solveTridiag(sub, diag, sup, a, np - 2);
                    float oldt = x[0];
                    float oldy = d[0];
                    for (i = 1; i <= np - 1; i++) {
                        for (int j = 1; j <= this.precision; j++) {
                            float t1 = (h[i] * ((float) j)) / ((float) this.precision);
                            float t2 = h[i] - t1;
                            float y = (((((((-a[i - 1]) / 6.0f) * (h[i] + t2)) * t1) + d[i - 1]) * t2) + ((((((-a[i]) / 6.0f) * (h[i] + t1)) * t2) + d[i]) * t1)) / h[i];
                            float t = x[i - 1] + t1;
                            s.seriesPath.lineTo(t, y);
                            if (this.fillType != FillType.NONE) {
                                s.fillArea.lineTo(t, y);
                            }
                        }
                    }
                }
                if (this.fillType != FillType.NONE) {
                    if (plot.getOrientation() == PlotOrientation.HORIZONTAL) {
                        s.fillArea.lineTo(origin.getX(), ((Point2D) s.points.get(s.points.size() - 1)).getY());
                    } else {
                        s.fillArea.lineTo(((Point2D) s.points.get(s.points.size() - 1)).getX(), origin.getY());
                    }
                    s.fillArea.closePath();
                }
                if (this.fillType != FillType.NONE) {
                    Paint fp = getSeriesFillPaint(series);
                    if (this.gradientPaintTransformer == null || !(fp instanceof GradientPaint)) {
                        g2.setPaint(fp);
                    } else {
                        g2.setPaint(this.gradientPaintTransformer.transform((GradientPaint) fp, s.fillArea));
                    }
                    g2.fill(s.fillArea);
                    s.fillArea.reset();
                }
                Graphics2D graphics2D = g2;
                int i2 = pass;
                int i3 = series;
                int i4 = item;
                drawFirstPassShape(graphics2D, i2, i3, i4, s.seriesPath);
            }
            s.points = new ArrayList();
        }
    }

    private void solveTridiag(float[] sub, float[] diag, float[] sup, float[] b, int n) {
        int i;
        for (i = 2; i <= n; i++) {
            sub[i] = sub[i] / diag[i - 1];
            diag[i] = diag[i] - (sub[i] * sup[i - 1]);
            b[i] = b[i] - (sub[i] * b[i - 1]);
        }
        b[n] = b[n] / diag[n];
        for (i = n - 1; i >= 1; i--) {
            b[i] = (b[i] - (sup[i] * b[i + 1])) / diag[i];
        }
    }

    public boolean equals(Object obj) {
        if (obj == this) {
            return true;
        }
        if (!(obj instanceof XYSplineRenderer)) {
            return false;
        }
        XYSplineRenderer that = (XYSplineRenderer) obj;
        if (this.precision == that.precision && this.fillType == that.fillType && ObjectUtilities.equal(this.gradientPaintTransformer, that.gradientPaintTransformer)) {
            return super.equals(obj);
        }
        return false;
    }
}
