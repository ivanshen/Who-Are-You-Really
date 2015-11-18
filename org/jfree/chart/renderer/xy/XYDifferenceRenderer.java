package org.jfree.chart.renderer.xy;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Paint;
import java.awt.Shape;
import java.awt.Stroke;
import java.awt.geom.GeneralPath;
import java.awt.geom.Line2D;
import java.awt.geom.Line2D.Double;
import java.awt.geom.Rectangle2D;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.Collections;
import java.util.LinkedList;
import org.jfree.chart.LegendItem;
import org.jfree.chart.axis.DateAxis;
import org.jfree.chart.axis.ValueAxis;
import org.jfree.chart.entity.EntityCollection;
import org.jfree.chart.entity.XYItemEntity;
import org.jfree.chart.event.RendererChangeEvent;
import org.jfree.chart.labels.XYToolTipGenerator;
import org.jfree.chart.plot.CrosshairState;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.plot.PlotRenderingInfo;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.urls.XYURLGenerator;
import org.jfree.chart.util.ParamChecks;
import org.jfree.data.xy.NormalizedMatrixSeries;
import org.jfree.data.xy.XYDataset;
import org.jfree.io.SerialUtilities;
import org.jfree.ui.RectangleEdge;
import org.jfree.util.PaintUtilities;
import org.jfree.util.PublicCloneable;
import org.jfree.util.ShapeUtilities;

public class XYDifferenceRenderer extends AbstractXYItemRenderer implements XYItemRenderer, PublicCloneable {
    private static final long serialVersionUID = -8447915602375584857L;
    private transient Shape legendLine;
    private transient Paint negativePaint;
    private transient Paint positivePaint;
    private boolean roundXCoordinates;
    private boolean shapesVisible;

    public XYDifferenceRenderer() {
        this(Color.green, Color.red, false);
    }

    public XYDifferenceRenderer(Paint positivePaint, Paint negativePaint, boolean shapes) {
        ParamChecks.nullNotPermitted(positivePaint, "positivePaint");
        ParamChecks.nullNotPermitted(negativePaint, "negativePaint");
        this.positivePaint = positivePaint;
        this.negativePaint = negativePaint;
        this.shapesVisible = shapes;
        this.legendLine = new Double(-7.0d, 0.0d, 7.0d, 0.0d);
        this.roundXCoordinates = false;
    }

    public Paint getPositivePaint() {
        return this.positivePaint;
    }

    public void setPositivePaint(Paint paint) {
        ParamChecks.nullNotPermitted(paint, "paint");
        this.positivePaint = paint;
        fireChangeEvent();
    }

    public Paint getNegativePaint() {
        return this.negativePaint;
    }

    public void setNegativePaint(Paint paint) {
        ParamChecks.nullNotPermitted(paint, "paint");
        this.negativePaint = paint;
        notifyListeners(new RendererChangeEvent(this));
    }

    public boolean getShapesVisible() {
        return this.shapesVisible;
    }

    public void setShapesVisible(boolean flag) {
        this.shapesVisible = flag;
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

    public boolean getRoundXCoordinates() {
        return this.roundXCoordinates;
    }

    public void setRoundXCoordinates(boolean round) {
        this.roundXCoordinates = round;
        fireChangeEvent();
    }

    public XYItemRendererState initialise(Graphics2D g2, Rectangle2D dataArea, XYPlot plot, XYDataset data, PlotRenderingInfo info) {
        XYItemRendererState state = super.initialise(g2, dataArea, plot, data, info);
        state.setProcessVisibleItemsOnly(false);
        return state;
    }

    public int getPassCount() {
        return 2;
    }

    public void drawItem(Graphics2D g2, XYItemRendererState state, Rectangle2D dataArea, PlotRenderingInfo info, XYPlot plot, ValueAxis domainAxis, ValueAxis rangeAxis, XYDataset dataset, int series, int item, CrosshairState crosshairState, int pass) {
        if (pass == 0) {
            drawItemPass0(g2, dataArea, info, plot, domainAxis, rangeAxis, dataset, series, item, crosshairState);
        } else if (pass == 1) {
            drawItemPass1(g2, dataArea, info, plot, domainAxis, rangeAxis, dataset, series, item, crosshairState);
        }
    }

    protected void drawItemPass0(Graphics2D x_graphics, Rectangle2D x_dataArea, PlotRenderingInfo x_info, XYPlot x_plot, ValueAxis x_domainAxis, ValueAxis x_rangeAxis, XYDataset x_dataset, int x_series, int x_item, CrosshairState x_crosshairState) {
        if (x_series == 0 && x_item == 0) {
            boolean b_impliedZeroSubtrahend = 1 == x_dataset.getSeriesCount();
            if (!isEitherSeriesDegenerate(x_dataset, b_impliedZeroSubtrahend)) {
                if (b_impliedZeroSubtrahend || !areSeriesDisjoint(x_dataset)) {
                    int l_subtrahendItemCount;
                    Double d;
                    double l_slope;
                    boolean b_positive;
                    LinkedList l_minuendXs = new LinkedList();
                    LinkedList l_minuendYs = new LinkedList();
                    LinkedList l_subtrahendXs = new LinkedList();
                    LinkedList l_subtrahendYs = new LinkedList();
                    LinkedList l_polygonXs = new LinkedList();
                    LinkedList l_polygonYs = new LinkedList();
                    int l_minuendItem = 0;
                    int l_minuendItemCount = x_dataset.getItemCount(0);
                    Double l_minuendCurX = null;
                    Double l_minuendNextX = null;
                    Double l_minuendCurY = null;
                    Double l_minuendNextY = null;
                    double l_minuendMaxY = Double.NEGATIVE_INFINITY;
                    double l_minuendMinY = Double.POSITIVE_INFINITY;
                    int l_subtrahendItem = 0;
                    Double d2 = null;
                    Double d3 = null;
                    Double d4 = null;
                    Double d5 = null;
                    double l_subtrahendMaxY = Double.NEGATIVE_INFINITY;
                    double l_subtrahendMinY = Double.POSITIVE_INFINITY;
                    if (b_impliedZeroSubtrahend) {
                        l_subtrahendItem = 0;
                        l_subtrahendItemCount = 2;
                        d = new Double(x_dataset.getXValue(0, 0));
                        d = new Double(x_dataset.getXValue(0, l_minuendItemCount - 1));
                        d = new Double(0.0d);
                        d = new Double(0.0d);
                        l_subtrahendMaxY = 0.0d;
                        l_subtrahendMinY = 0.0d;
                        l_subtrahendXs.add(d);
                        l_subtrahendYs.add(d);
                    } else {
                        l_subtrahendItemCount = x_dataset.getItemCount(1);
                    }
                    boolean b_minuendDone = false;
                    boolean b_minuendAdvanced = true;
                    boolean b_minuendAtIntersect = false;
                    boolean b_minuendFastForward = false;
                    boolean b_subtrahendDone = false;
                    boolean b_subtrahendAdvanced = true;
                    boolean b_subtrahendAtIntersect = false;
                    boolean b_subtrahendFastForward = false;
                    boolean b_colinear = false;
                    double l_x1 = 0.0d;
                    double l_y1 = 0.0d;
                    double l_x2 = 0.0d;
                    double l_y2 = 0.0d;
                    double l_x3 = 0.0d;
                    double l_y3 = 0.0d;
                    double l_x4 = 0.0d;
                    double l_y4 = 0.0d;
                    boolean b_fastForwardDone = false;
                    while (!b_fastForwardDone) {
                        l_x1 = x_dataset.getXValue(0, l_minuendItem);
                        l_y1 = x_dataset.getYValue(0, l_minuendItem);
                        l_x2 = x_dataset.getXValue(0, l_minuendItem + 1);
                        l_y2 = x_dataset.getYValue(0, l_minuendItem + 1);
                        d = new Double(l_x1);
                        d = new Double(l_y1);
                        d = new Double(l_x2);
                        d = new Double(l_y2);
                        if (b_impliedZeroSubtrahend) {
                            l_x3 = d2.doubleValue();
                            l_y3 = d4.doubleValue();
                            l_x4 = d3.doubleValue();
                            l_y4 = d5.doubleValue();
                        } else {
                            l_x3 = x_dataset.getXValue(1, l_subtrahendItem);
                            l_y3 = x_dataset.getYValue(1, l_subtrahendItem);
                            l_x4 = x_dataset.getXValue(1, l_subtrahendItem + 1);
                            l_y4 = x_dataset.getYValue(1, l_subtrahendItem + 1);
                            d = new Double(l_x3);
                            d = new Double(l_y3);
                            d = new Double(l_x4);
                            d = new Double(l_y4);
                        }
                        if (l_x2 <= l_x3) {
                            l_minuendItem++;
                            b_minuendFastForward = true;
                        } else if (l_x4 <= l_x1) {
                            l_subtrahendItem++;
                            b_subtrahendFastForward = true;
                        } else {
                            if (l_x3 < l_x1 && l_x1 < l_x4) {
                                l_slope = (l_y4 - l_y3) / (l_x4 - l_x3);
                                d2 = d;
                                d = new Double((l_slope * l_x1) + (l_y3 - (l_slope * l_x3)));
                                l_subtrahendXs.add(d2);
                                l_subtrahendYs.add(d);
                            }
                            if (l_x1 < l_x3 && l_x3 < l_x2) {
                                l_slope = (l_y2 - l_y1) / (l_x2 - l_x1);
                                l_minuendCurX = d2;
                                d = new Double((l_slope * l_x3) + (l_y1 - (l_slope * l_x1)));
                                l_minuendXs.add(l_minuendCurX);
                                l_minuendYs.add(d);
                            }
                            l_minuendMaxY = l_minuendCurY.doubleValue();
                            l_minuendMinY = l_minuendCurY.doubleValue();
                            l_subtrahendMaxY = d4.doubleValue();
                            l_subtrahendMinY = d4.doubleValue();
                            b_fastForwardDone = true;
                        }
                    }
                    while (!b_minuendDone && !b_subtrahendDone) {
                        if (!(b_minuendDone || b_minuendFastForward || !b_minuendAdvanced)) {
                            l_x1 = x_dataset.getXValue(0, l_minuendItem);
                            l_y1 = x_dataset.getYValue(0, l_minuendItem);
                            d = new Double(l_x1);
                            d = new Double(l_y1);
                            if (!b_minuendAtIntersect) {
                                l_minuendXs.add(d);
                                l_minuendYs.add(d);
                            }
                            l_minuendMaxY = Math.max(l_minuendMaxY, l_y1);
                            l_minuendMinY = Math.min(l_minuendMinY, l_y1);
                            l_x2 = x_dataset.getXValue(0, l_minuendItem + 1);
                            l_y2 = x_dataset.getYValue(0, l_minuendItem + 1);
                            d = new Double(l_x2);
                            d = new Double(l_y2);
                        }
                        if (!(b_impliedZeroSubtrahend || b_subtrahendDone || b_subtrahendFastForward || !b_subtrahendAdvanced)) {
                            l_x3 = x_dataset.getXValue(1, l_subtrahendItem);
                            l_y3 = x_dataset.getYValue(1, l_subtrahendItem);
                            d = new Double(l_x3);
                            d = new Double(l_y3);
                            if (!b_subtrahendAtIntersect) {
                                l_subtrahendXs.add(d);
                                l_subtrahendYs.add(d);
                            }
                            l_subtrahendMaxY = Math.max(l_subtrahendMaxY, l_y3);
                            l_subtrahendMinY = Math.min(l_subtrahendMinY, l_y3);
                            l_x4 = x_dataset.getXValue(1, l_subtrahendItem + 1);
                            l_y4 = x_dataset.getYValue(1, l_subtrahendItem + 1);
                            d = new Double(l_x4);
                            d = new Double(l_y4);
                        }
                        b_minuendFastForward = false;
                        b_subtrahendFastForward = false;
                        Double l_intersectX = null;
                        Double l_intersectY = null;
                        boolean b_intersect = false;
                        b_minuendAtIntersect = false;
                        b_subtrahendAtIntersect = false;
                        if (l_x2 != l_x4 || l_y2 != l_y4) {
                            double l_denominator = ((l_y4 - l_y3) * (l_x2 - l_x1)) - ((l_x4 - l_x3) * (l_y2 - l_y1));
                            double l_deltaY = l_y1 - l_y3;
                            double l_deltaX = l_x1 - l_x3;
                            double l_numeratorA = ((l_x4 - l_x3) * l_deltaY) - ((l_y4 - l_y3) * l_deltaX);
                            double l_numeratorB = ((l_x2 - l_x1) * l_deltaY) - ((l_y2 - l_y1) * l_deltaX);
                            if (0.0d == l_numeratorA && 0.0d == l_numeratorB && 0.0d == l_denominator) {
                                b_colinear = true;
                            } else if (b_colinear) {
                                Object obj;
                                Double d6;
                                l_minuendXs.clear();
                                l_minuendYs.clear();
                                l_subtrahendXs.clear();
                                l_subtrahendYs.clear();
                                l_polygonXs.clear();
                                l_polygonYs.clear();
                                b_colinear = false;
                                boolean b_useMinuend = l_x3 <= l_x1 && l_x1 <= l_x4;
                                if (b_useMinuend) {
                                    obj = l_minuendCurX;
                                } else {
                                    d6 = d2;
                                }
                                l_polygonXs.add(obj);
                                if (b_useMinuend) {
                                    obj = l_minuendCurY;
                                } else {
                                    d6 = d4;
                                }
                                l_polygonYs.add(obj);
                            }
                            double l_slopeA = l_numeratorA / l_denominator;
                            double l_slopeB = l_numeratorB / l_denominator;
                            boolean b_vertical = l_x1 == l_x2 && l_x3 == l_x4 && l_x2 == l_x4;
                            if ((0.0d < l_slopeA && l_slopeA <= NormalizedMatrixSeries.DEFAULT_SCALE_FACTOR && 0.0d < l_slopeB && l_slopeB <= NormalizedMatrixSeries.DEFAULT_SCALE_FACTOR) || b_vertical) {
                                double l_xi;
                                double l_yi;
                                if (b_vertical) {
                                    b_colinear = false;
                                    l_xi = l_x2;
                                    l_yi = l_x4;
                                } else {
                                    l_xi = l_x1 + ((l_x2 - l_x1) * l_slopeA);
                                    l_yi = l_y1 + ((l_y2 - l_y1) * l_slopeA);
                                }
                                d = new Double(l_xi);
                                d = new Double(l_yi);
                                b_intersect = true;
                                b_minuendAtIntersect = l_xi == l_x2 && l_yi == l_y2;
                                b_subtrahendAtIntersect = l_xi == l_x4 && l_yi == l_y4;
                                l_minuendCurX = d;
                                l_minuendCurY = d;
                                d2 = d;
                                d4 = d;
                            }
                        } else if (l_x1 == l_x3 && l_y1 == l_y3) {
                            b_colinear = true;
                        } else {
                            d = new Double(l_x2);
                            d = new Double(l_y2);
                            b_intersect = true;
                            b_minuendAtIntersect = true;
                            b_subtrahendAtIntersect = true;
                        }
                        if (b_intersect) {
                            l_polygonXs.addAll(l_minuendXs);
                            l_polygonYs.addAll(l_minuendYs);
                            l_polygonXs.add(l_intersectX);
                            l_polygonYs.add(l_intersectY);
                            Collections.reverse(l_subtrahendXs);
                            Collections.reverse(l_subtrahendYs);
                            l_polygonXs.addAll(l_subtrahendXs);
                            l_polygonYs.addAll(l_subtrahendYs);
                            b_positive = l_subtrahendMaxY <= l_minuendMaxY && l_subtrahendMinY <= l_minuendMinY;
                            createPolygon(x_graphics, x_dataArea, x_plot, x_domainAxis, x_rangeAxis, b_positive, l_polygonXs, l_polygonYs);
                            l_minuendXs.clear();
                            l_minuendYs.clear();
                            l_subtrahendXs.clear();
                            l_subtrahendYs.clear();
                            l_polygonXs.clear();
                            l_polygonYs.clear();
                            double l_y = l_intersectY.doubleValue();
                            l_minuendMaxY = l_y;
                            l_subtrahendMaxY = l_y;
                            l_minuendMinY = l_y;
                            l_subtrahendMinY = l_y;
                            l_polygonXs.add(l_intersectX);
                            l_polygonYs.add(l_intersectY);
                        }
                        if (l_x2 <= l_x4) {
                            l_minuendItem++;
                            b_minuendAdvanced = true;
                        } else {
                            b_minuendAdvanced = false;
                        }
                        if (l_x4 <= l_x2) {
                            l_subtrahendItem++;
                            b_subtrahendAdvanced = true;
                        } else {
                            b_subtrahendAdvanced = false;
                        }
                        b_minuendDone = l_minuendItem == l_minuendItemCount + -1;
                        b_subtrahendDone = l_subtrahendItem == l_subtrahendItemCount + -1;
                    }
                    if (b_minuendDone && l_x3 < l_x2 && l_x2 < l_x4) {
                        l_slope = (l_y4 - l_y3) / (l_x4 - l_x3);
                        d3 = l_minuendNextX;
                        d = new Double((l_slope * l_x2) + (l_y3 - (l_slope * l_x3)));
                    }
                    if (b_subtrahendDone && l_x1 < l_x4 && l_x4 < l_x2) {
                        l_slope = (l_y2 - l_y1) / (l_x2 - l_x1);
                        l_minuendNextX = d3;
                        d = new Double((l_slope * l_x4) + (l_y1 - (l_slope * l_x1)));
                    }
                    l_minuendMaxY = Math.max(l_minuendMaxY, l_minuendNextY.doubleValue());
                    l_subtrahendMaxY = Math.max(l_subtrahendMaxY, d5.doubleValue());
                    l_minuendMinY = Math.min(l_minuendMinY, l_minuendNextY.doubleValue());
                    l_subtrahendMinY = Math.min(l_subtrahendMinY, d5.doubleValue());
                    l_minuendXs.add(l_minuendNextX);
                    l_minuendYs.add(l_minuendNextY);
                    l_subtrahendXs.add(d3);
                    l_subtrahendYs.add(d5);
                    l_polygonXs.addAll(l_minuendXs);
                    l_polygonYs.addAll(l_minuendYs);
                    Collections.reverse(l_subtrahendXs);
                    Collections.reverse(l_subtrahendYs);
                    l_polygonXs.addAll(l_subtrahendXs);
                    l_polygonYs.addAll(l_subtrahendYs);
                    b_positive = l_subtrahendMaxY <= l_minuendMaxY && l_subtrahendMinY <= l_minuendMinY;
                    createPolygon(x_graphics, x_dataArea, x_plot, x_domainAxis, x_rangeAxis, b_positive, l_polygonXs, l_polygonYs);
                }
            }
        }
    }

    protected void drawItemPass1(Graphics2D x_graphics, Rectangle2D x_dataArea, PlotRenderingInfo x_info, XYPlot x_plot, ValueAxis x_domainAxis, ValueAxis x_rangeAxis, XYDataset x_dataset, int x_series, int x_item, CrosshairState x_crosshairState) {
        Shape l_entityArea = null;
        EntityCollection l_entities = null;
        if (x_info != null) {
            l_entities = x_info.getOwner().getEntityCollection();
        }
        Paint l_seriesPaint = getItemPaint(x_series, x_item);
        Stroke l_seriesStroke = getItemStroke(x_series, x_item);
        x_graphics.setPaint(l_seriesPaint);
        x_graphics.setStroke(l_seriesStroke);
        PlotOrientation l_orientation = x_plot.getOrientation();
        RectangleEdge l_domainAxisLocation = x_plot.getDomainAxisEdge();
        RectangleEdge l_rangeAxisLocation = x_plot.getRangeAxisEdge();
        double l_x0 = x_dataset.getXValue(x_series, x_item);
        double l_y0 = x_dataset.getYValue(x_series, x_item);
        double l_x1 = x_domainAxis.valueToJava2D(l_x0, x_dataArea, l_domainAxisLocation);
        double l_y1 = x_rangeAxis.valueToJava2D(l_y0, x_dataArea, l_rangeAxisLocation);
        if (getShapesVisible()) {
            Shape l_shape = getItemShape(x_series, x_item);
            if (l_orientation == PlotOrientation.HORIZONTAL) {
                l_shape = ShapeUtilities.createTranslatedShape(l_shape, l_y1, l_x1);
            } else {
                l_shape = ShapeUtilities.createTranslatedShape(l_shape, l_x1, l_y1);
            }
            if (l_shape.intersects(x_dataArea)) {
                x_graphics.setPaint(getItemPaint(x_series, x_item));
                x_graphics.fill(l_shape);
            }
            l_entityArea = l_shape;
        }
        if (l_entities != null) {
            if (l_entityArea == null) {
                l_entityArea = new Rectangle2D.Double(l_x1 - DateAxis.DEFAULT_AUTO_RANGE_MINIMUM_SIZE_IN_MILLISECONDS, l_y1 - DateAxis.DEFAULT_AUTO_RANGE_MINIMUM_SIZE_IN_MILLISECONDS, 4.0d, 4.0d);
            }
            String l_tip = null;
            XYToolTipGenerator l_tipGenerator = getToolTipGenerator(x_series, x_item);
            if (l_tipGenerator != null) {
                l_tip = l_tipGenerator.generateToolTip(x_dataset, x_series, x_item);
            }
            String l_url = null;
            XYURLGenerator l_urlGenerator = getURLGenerator();
            if (l_urlGenerator != null) {
                l_url = l_urlGenerator.generateURL(x_dataset, x_series, x_item);
            }
            l_entities.add(new XYItemEntity(l_entityArea, x_dataset, x_series, x_item, l_tip, l_url));
        }
        if (isItemLabelVisible(x_series, x_item)) {
            drawItemLabel(x_graphics, l_orientation, x_dataset, x_series, x_item, l_x1, l_y1, l_y1 < 0.0d);
        }
        updateCrosshairValues(x_crosshairState, l_x0, l_y0, x_plot.getDomainAxisIndex(x_domainAxis), x_plot.getRangeAxisIndex(x_rangeAxis), l_x1, l_y1, l_orientation);
        if (x_item != 0) {
            double l_x2 = x_domainAxis.valueToJava2D(x_dataset.getXValue(x_series, x_item - 1), x_dataArea, l_domainAxisLocation);
            double l_y2 = x_rangeAxis.valueToJava2D(x_dataset.getYValue(x_series, x_item - 1), x_dataArea, l_rangeAxisLocation);
            Line2D l_line = null;
            if (PlotOrientation.HORIZONTAL == l_orientation) {
                l_line = new Double(l_y1, l_x1, l_y2, l_x2);
            } else if (PlotOrientation.VERTICAL == l_orientation) {
                Double doubleR = new Double(l_x1, l_y1, l_x2, l_y2);
            }
            if (l_line != null && l_line.intersects(x_dataArea)) {
                x_graphics.setPaint(getItemPaint(x_series, x_item));
                x_graphics.setStroke(getItemStroke(x_series, x_item));
                x_graphics.draw(l_line);
            }
        }
    }

    private boolean isEitherSeriesDegenerate(XYDataset x_dataset, boolean x_impliedZeroSubtrahend) {
        boolean z = false;
        if (!x_impliedZeroSubtrahend) {
            if (x_dataset.getItemCount(0) < 2 || x_dataset.getItemCount(1) < 2) {
                z = true;
            }
            return z;
        } else if (x_dataset.getItemCount(0) < 2) {
            return true;
        } else {
            return false;
        }
    }

    private boolean areSeriesDisjoint(XYDataset x_dataset) {
        int l_minuendItemCount = x_dataset.getItemCount(0);
        double l_minuendFirst = x_dataset.getXValue(0, 0);
        double l_minuendLast = x_dataset.getXValue(0, l_minuendItemCount - 1);
        int l_subtrahendItemCount = x_dataset.getItemCount(1);
        double l_subtrahendFirst = x_dataset.getXValue(1, 0);
        double l_subtrahendLast = x_dataset.getXValue(1, l_subtrahendItemCount - 1);
        if (l_minuendLast < l_subtrahendFirst || l_subtrahendLast < l_minuendFirst) {
            return true;
        }
        return false;
    }

    private void createPolygon(Graphics2D x_graphics, Rectangle2D x_dataArea, XYPlot x_plot, ValueAxis x_domainAxis, ValueAxis x_rangeAxis, boolean x_positive, LinkedList x_xValues, LinkedList x_yValues) {
        PlotOrientation l_orientation = x_plot.getOrientation();
        RectangleEdge l_domainAxisLocation = x_plot.getDomainAxisEdge();
        RectangleEdge l_rangeAxisLocation = x_plot.getRangeAxisEdge();
        Object[] l_xValues = x_xValues.toArray();
        Object[] l_yValues = x_yValues.toArray();
        GeneralPath l_path = new GeneralPath();
        double l_x;
        int i;
        if (PlotOrientation.VERTICAL == l_orientation) {
            l_x = x_domainAxis.valueToJava2D(((Double) l_xValues[0]).doubleValue(), x_dataArea, l_domainAxisLocation);
            if (this.roundXCoordinates) {
                l_x = Math.rint(l_x);
            }
            l_path.moveTo((float) l_x, (float) x_rangeAxis.valueToJava2D(((Double) l_yValues[0]).doubleValue(), x_dataArea, l_rangeAxisLocation));
            for (i = 1; i < l_xValues.length; i++) {
                l_x = x_domainAxis.valueToJava2D(((Double) l_xValues[i]).doubleValue(), x_dataArea, l_domainAxisLocation);
                if (this.roundXCoordinates) {
                    l_x = Math.rint(l_x);
                }
                l_path.lineTo((float) l_x, (float) x_rangeAxis.valueToJava2D(((Double) l_yValues[i]).doubleValue(), x_dataArea, l_rangeAxisLocation));
            }
            l_path.closePath();
        } else {
            l_x = x_domainAxis.valueToJava2D(((Double) l_xValues[0]).doubleValue(), x_dataArea, l_domainAxisLocation);
            if (this.roundXCoordinates) {
                l_x = Math.rint(l_x);
            }
            l_path.moveTo((float) x_rangeAxis.valueToJava2D(((Double) l_yValues[0]).doubleValue(), x_dataArea, l_rangeAxisLocation), (float) l_x);
            for (i = 1; i < l_xValues.length; i++) {
                l_x = x_domainAxis.valueToJava2D(((Double) l_xValues[i]).doubleValue(), x_dataArea, l_domainAxisLocation);
                if (this.roundXCoordinates) {
                    l_x = Math.rint(l_x);
                }
                l_path.lineTo((float) x_rangeAxis.valueToJava2D(((Double) l_yValues[i]).doubleValue(), x_dataArea, l_rangeAxisLocation), (float) l_x);
            }
            l_path.closePath();
        }
        if (l_path.intersects(x_dataArea)) {
            Paint positivePaint;
            if (x_positive) {
                positivePaint = getPositivePaint();
            } else {
                positivePaint = getNegativePaint();
            }
            x_graphics.setPaint(positivePaint);
            x_graphics.fill(l_path);
        }
    }

    public LegendItem getLegendItem(int datasetIndex, int series) {
        LegendItem result = null;
        XYPlot p = getPlot();
        if (p != null) {
            XYDataset dataset = p.getDataset(datasetIndex);
            if (dataset != null && getItemVisible(series, 0)) {
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
                Paint paint = lookupSeriesPaint(series);
                result = new LegendItem(label, description, toolTipText, urlText, getLegendLine(), lookupSeriesStroke(series), paint);
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

    public boolean equals(Object obj) {
        if (obj == this) {
            return true;
        }
        if (!(obj instanceof XYDifferenceRenderer)) {
            return false;
        }
        if (!super.equals(obj)) {
            return false;
        }
        XYDifferenceRenderer that = (XYDifferenceRenderer) obj;
        if (!PaintUtilities.equal(this.positivePaint, that.positivePaint)) {
            return false;
        }
        if (!PaintUtilities.equal(this.negativePaint, that.negativePaint)) {
            return false;
        }
        if (this.shapesVisible != that.shapesVisible) {
            return false;
        }
        if (!ShapeUtilities.equal(this.legendLine, that.legendLine)) {
            return false;
        }
        if (this.roundXCoordinates != that.roundXCoordinates) {
            return false;
        }
        return true;
    }

    public Object clone() throws CloneNotSupportedException {
        XYDifferenceRenderer clone = (XYDifferenceRenderer) super.clone();
        clone.legendLine = ShapeUtilities.clone(this.legendLine);
        return clone;
    }

    private void writeObject(ObjectOutputStream stream) throws IOException {
        stream.defaultWriteObject();
        SerialUtilities.writePaint(this.positivePaint, stream);
        SerialUtilities.writePaint(this.negativePaint, stream);
        SerialUtilities.writeShape(this.legendLine, stream);
    }

    private void readObject(ObjectInputStream stream) throws IOException, ClassNotFoundException {
        stream.defaultReadObject();
        this.positivePaint = SerialUtilities.readPaint(stream);
        this.negativePaint = SerialUtilities.readPaint(stream);
        this.legendLine = SerialUtilities.readShape(stream);
    }
}
