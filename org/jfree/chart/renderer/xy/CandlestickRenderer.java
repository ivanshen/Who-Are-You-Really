package org.jfree.chart.renderer.xy;

import java.awt.AlphaComposite;
import java.awt.Color;
import java.awt.Composite;
import java.awt.Graphics2D;
import java.awt.Paint;
import java.awt.geom.Line2D;
import java.awt.geom.Rectangle2D;
import java.awt.geom.Rectangle2D.Double;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import org.jfree.chart.axis.DateAxis;
import org.jfree.chart.axis.ValueAxis;
import org.jfree.chart.entity.EntityCollection;
import org.jfree.chart.labels.HighLowItemLabelGenerator;
import org.jfree.chart.labels.XYToolTipGenerator;
import org.jfree.chart.plot.CrosshairState;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.plot.PlotRenderingInfo;
import org.jfree.chart.plot.SpiderWebPlot;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.renderer.category.BarRenderer;
import org.jfree.chart.util.ParamChecks;
import org.jfree.data.Range;
import org.jfree.data.xy.IntervalXYDataset;
import org.jfree.data.xy.NormalizedMatrixSeries;
import org.jfree.data.xy.OHLCDataset;
import org.jfree.data.xy.XYDataset;
import org.jfree.io.SerialUtilities;
import org.jfree.ui.RectangleEdge;
import org.jfree.util.PaintUtilities;
import org.jfree.util.PublicCloneable;

public class CandlestickRenderer extends AbstractXYItemRenderer implements XYItemRenderer, Cloneable, PublicCloneable, Serializable {
    public static final int WIDTHMETHOD_AVERAGE = 0;
    public static final int WIDTHMETHOD_INTERVALDATA = 2;
    public static final int WIDTHMETHOD_SMALLEST = 1;
    private static final long serialVersionUID = 50390395841817121L;
    private double autoWidthFactor;
    private double autoWidthGap;
    private int autoWidthMethod;
    private double candleWidth;
    private transient Paint downPaint;
    private boolean drawVolume;
    private double maxCandleWidth;
    private double maxCandleWidthInMilliseconds;
    private transient double maxVolume;
    private transient Paint upPaint;
    private boolean useOutlinePaint;
    private transient Paint volumePaint;

    public CandlestickRenderer() {
        this(SpiderWebPlot.DEFAULT_MAX_VALUE);
    }

    public CandlestickRenderer(double candleWidth) {
        this(candleWidth, true, new HighLowItemLabelGenerator());
    }

    public CandlestickRenderer(double candleWidth, boolean drawVolume, XYToolTipGenerator toolTipGenerator) {
        this.autoWidthMethod = WIDTHMETHOD_AVERAGE;
        this.autoWidthFactor = 0.6428571428571429d;
        this.autoWidthGap = 0.0d;
        this.maxCandleWidthInMilliseconds = 7.2E7d;
        setBaseToolTipGenerator(toolTipGenerator);
        this.candleWidth = candleWidth;
        this.drawVolume = drawVolume;
        this.volumePaint = Color.gray;
        this.upPaint = Color.green;
        this.downPaint = Color.red;
        this.useOutlinePaint = false;
    }

    public double getCandleWidth() {
        return this.candleWidth;
    }

    public void setCandleWidth(double width) {
        if (width != this.candleWidth) {
            this.candleWidth = width;
            fireChangeEvent();
        }
    }

    public double getMaxCandleWidthInMilliseconds() {
        return this.maxCandleWidthInMilliseconds;
    }

    public void setMaxCandleWidthInMilliseconds(double millis) {
        this.maxCandleWidthInMilliseconds = millis;
        fireChangeEvent();
    }

    public int getAutoWidthMethod() {
        return this.autoWidthMethod;
    }

    public void setAutoWidthMethod(int autoWidthMethod) {
        if (this.autoWidthMethod != autoWidthMethod) {
            this.autoWidthMethod = autoWidthMethod;
            fireChangeEvent();
        }
    }

    public double getAutoWidthFactor() {
        return this.autoWidthFactor;
    }

    public void setAutoWidthFactor(double autoWidthFactor) {
        if (this.autoWidthFactor != autoWidthFactor) {
            this.autoWidthFactor = autoWidthFactor;
            fireChangeEvent();
        }
    }

    public double getAutoWidthGap() {
        return this.autoWidthGap;
    }

    public void setAutoWidthGap(double autoWidthGap) {
        if (this.autoWidthGap != autoWidthGap) {
            this.autoWidthGap = autoWidthGap;
            fireChangeEvent();
        }
    }

    public Paint getUpPaint() {
        return this.upPaint;
    }

    public void setUpPaint(Paint paint) {
        this.upPaint = paint;
        fireChangeEvent();
    }

    public Paint getDownPaint() {
        return this.downPaint;
    }

    public void setDownPaint(Paint paint) {
        this.downPaint = paint;
        fireChangeEvent();
    }

    public boolean getDrawVolume() {
        return this.drawVolume;
    }

    public void setDrawVolume(boolean flag) {
        if (this.drawVolume != flag) {
            this.drawVolume = flag;
            fireChangeEvent();
        }
    }

    public Paint getVolumePaint() {
        return this.volumePaint;
    }

    public void setVolumePaint(Paint paint) {
        ParamChecks.nullNotPermitted(paint, "paint");
        this.volumePaint = paint;
        fireChangeEvent();
    }

    public boolean getUseOutlinePaint() {
        return this.useOutlinePaint;
    }

    public void setUseOutlinePaint(boolean use) {
        if (this.useOutlinePaint != use) {
            this.useOutlinePaint = use;
            fireChangeEvent();
        }
    }

    public Range findRangeBounds(XYDataset dataset) {
        return findRangeBounds(dataset, true);
    }

    public XYItemRendererState initialise(Graphics2D g2, Rectangle2D dataArea, XYPlot plot, XYDataset dataset, PlotRenderingInfo info) {
        ValueAxis axis = plot.getDomainAxis();
        double x1 = axis.getLowerBound();
        double x2 = x1 + this.maxCandleWidthInMilliseconds;
        RectangleEdge edge = plot.getDomainAxisEdge();
        this.maxCandleWidth = Math.abs(axis.valueToJava2D(x2, dataArea, edge) - axis.valueToJava2D(x1, dataArea, edge));
        if (this.drawVolume) {
            OHLCDataset highLowDataset = (OHLCDataset) dataset;
            this.maxVolume = 0.0d;
            for (int series = WIDTHMETHOD_AVERAGE; series < highLowDataset.getSeriesCount(); series += WIDTHMETHOD_SMALLEST) {
                for (int item = WIDTHMETHOD_AVERAGE; item < highLowDataset.getItemCount(series); item += WIDTHMETHOD_SMALLEST) {
                    double volume = highLowDataset.getVolumeValue(series, item);
                    if (volume > this.maxVolume) {
                        this.maxVolume = volume;
                    }
                }
            }
        }
        return new XYItemRendererState(info);
    }

    public void drawItem(Graphics2D g2, XYItemRendererState state, Rectangle2D dataArea, PlotRenderingInfo info, XYPlot plot, ValueAxis domainAxis, ValueAxis rangeAxis, XYDataset dataset, int series, int item, CrosshairState crosshairState, int pass) {
        boolean horiz;
        double volumeWidth;
        double stickWidth;
        Rectangle2D body;
        PlotOrientation orientation = plot.getOrientation();
        if (orientation == PlotOrientation.HORIZONTAL) {
            horiz = true;
        } else if (orientation == PlotOrientation.VERTICAL) {
            horiz = false;
        } else {
            return;
        }
        EntityCollection entities = null;
        if (info != null) {
            entities = info.getOwner().getEntityCollection();
        }
        OHLCDataset highLowData = (OHLCDataset) dataset;
        double x = highLowData.getXValue(series, item);
        double yHigh = highLowData.getHighValue(series, item);
        double yLow = highLowData.getLowValue(series, item);
        double yOpen = highLowData.getOpenValue(series, item);
        double yClose = highLowData.getCloseValue(series, item);
        RectangleEdge domainEdge = plot.getDomainAxisEdge();
        double xx = domainAxis.valueToJava2D(x, dataArea, domainEdge);
        RectangleEdge edge = plot.getRangeAxisEdge();
        double yyHigh = rangeAxis.valueToJava2D(yHigh, dataArea, edge);
        double yyLow = rangeAxis.valueToJava2D(yLow, dataArea, edge);
        double yyOpen = rangeAxis.valueToJava2D(yOpen, dataArea, edge);
        double yyClose = rangeAxis.valueToJava2D(yClose, dataArea, edge);
        if (this.candleWidth > 0.0d) {
            volumeWidth = this.candleWidth;
            stickWidth = this.candleWidth;
        } else {
            double xxWidth = 0.0d;
            int itemCount;
            switch (this.autoWidthMethod) {
                case WIDTHMETHOD_AVERAGE /*0*/:
                    itemCount = highLowData.getItemCount(series);
                    if (!horiz) {
                        xxWidth = dataArea.getWidth() / ((double) itemCount);
                        break;
                    }
                    xxWidth = dataArea.getHeight() / ((double) itemCount);
                    break;
                case WIDTHMETHOD_SMALLEST /*1*/:
                    itemCount = highLowData.getItemCount(series);
                    double lastPos = SpiderWebPlot.DEFAULT_MAX_VALUE;
                    xxWidth = dataArea.getWidth();
                    for (int i = WIDTHMETHOD_AVERAGE; i < itemCount; i += WIDTHMETHOD_SMALLEST) {
                        double pos = domainAxis.valueToJava2D(highLowData.getXValue(series, i), dataArea, domainEdge);
                        if (lastPos != SpiderWebPlot.DEFAULT_MAX_VALUE) {
                            xxWidth = Math.min(xxWidth, Math.abs(pos - lastPos));
                        }
                        lastPos = pos;
                    }
                    break;
                case WIDTHMETHOD_INTERVALDATA /*2*/:
                    IntervalXYDataset intervalXYData = (IntervalXYDataset) dataset;
                    xxWidth = Math.abs(domainAxis.valueToJava2D(intervalXYData.getEndXValue(series, item), dataArea, plot.getDomainAxisEdge()) - domainAxis.valueToJava2D(intervalXYData.getStartXValue(series, item), dataArea, plot.getDomainAxisEdge()));
                    break;
            }
            xxWidth = Math.min((xxWidth - (DateAxis.DEFAULT_AUTO_RANGE_MINIMUM_SIZE_IN_MILLISECONDS * this.autoWidthGap)) * this.autoWidthFactor, this.maxCandleWidth);
            volumeWidth = Math.max(Math.min(NormalizedMatrixSeries.DEFAULT_SCALE_FACTOR, this.maxCandleWidth), xxWidth);
            stickWidth = Math.max(Math.min(BarRenderer.BAR_OUTLINE_WIDTH_THRESHOLD, this.maxCandleWidth), xxWidth);
        }
        Paint p = getItemPaint(series, item);
        Paint outlinePaint = null;
        if (this.useOutlinePaint) {
            outlinePaint = getItemOutlinePaint(series, item);
        }
        g2.setStroke(getItemStroke(series, item));
        if (this.drawVolume) {
            double min;
            double max;
            int volume = (int) highLowData.getVolumeValue(series, item);
            double volumeHeight = ((double) volume) / this.maxVolume;
            if (horiz) {
                min = dataArea.getMinX();
                max = dataArea.getMaxX();
            } else {
                min = dataArea.getMinY();
                max = dataArea.getMaxY();
            }
            double zzVolume = volumeHeight * (max - min);
            g2.setPaint(getVolumePaint());
            Composite originalComposite = g2.getComposite();
            g2.setComposite(AlphaComposite.getInstance(3, 0.3f));
            if (horiz) {
                g2.fill(new Double(min, xx - (volumeWidth / DateAxis.DEFAULT_AUTO_RANGE_MINIMUM_SIZE_IN_MILLISECONDS), zzVolume, volumeWidth));
            } else {
                g2.fill(new Double(xx - (volumeWidth / DateAxis.DEFAULT_AUTO_RANGE_MINIMUM_SIZE_IN_MILLISECONDS), max - zzVolume, volumeWidth, zzVolume));
            }
            g2.setComposite(originalComposite);
        }
        if (this.useOutlinePaint) {
            g2.setPaint(outlinePaint);
        } else {
            g2.setPaint(p);
        }
        double yyMaxOpenClose = Math.max(yyOpen, yyClose);
        double yyMinOpenClose = Math.min(yyOpen, yyClose);
        double maxOpenClose = Math.max(yOpen, yClose);
        double minOpenClose = Math.min(yOpen, yClose);
        if (yHigh > maxOpenClose) {
            if (horiz) {
                g2.draw(new Line2D.Double(yyHigh, xx, yyMaxOpenClose, xx));
            } else {
                g2.draw(new Line2D.Double(xx, yyHigh, xx, yyMaxOpenClose));
            }
        }
        if (yLow < minOpenClose) {
            if (horiz) {
                g2.draw(new Line2D.Double(yyLow, xx, yyMinOpenClose, xx));
            } else {
                g2.draw(new Line2D.Double(xx, yyLow, xx, yyMinOpenClose));
            }
        }
        double length = Math.abs(yyHigh - yyLow);
        double base = Math.min(yyHigh, yyLow);
        if (horiz) {
            body = new Double(yyMinOpenClose, xx - (stickWidth / DateAxis.DEFAULT_AUTO_RANGE_MINIMUM_SIZE_IN_MILLISECONDS), yyMaxOpenClose - yyMinOpenClose, stickWidth);
            Rectangle2D hotspot = new Double(base, xx - (stickWidth / DateAxis.DEFAULT_AUTO_RANGE_MINIMUM_SIZE_IN_MILLISECONDS), length, stickWidth);
        } else {
            Double doubleR = new Double(xx - (stickWidth / DateAxis.DEFAULT_AUTO_RANGE_MINIMUM_SIZE_IN_MILLISECONDS), yyMinOpenClose, stickWidth, yyMaxOpenClose - yyMinOpenClose);
            Double doubleR2 = new Double(xx - (stickWidth / DateAxis.DEFAULT_AUTO_RANGE_MINIMUM_SIZE_IN_MILLISECONDS), base, stickWidth, length);
        }
        if (yClose > yOpen) {
            if (this.upPaint != null) {
                g2.setPaint(this.upPaint);
            } else {
                g2.setPaint(p);
            }
            g2.fill(body);
        } else {
            if (this.downPaint != null) {
                g2.setPaint(this.downPaint);
            } else {
                g2.setPaint(p);
            }
            g2.fill(body);
        }
        if (this.useOutlinePaint) {
            g2.setPaint(outlinePaint);
        } else {
            g2.setPaint(p);
        }
        g2.draw(body);
        if (entities != null) {
            addEntity(entities, hotspot, dataset, series, item, 0.0d, 0.0d);
        }
    }

    public boolean equals(Object obj) {
        if (obj == this) {
            return true;
        }
        if (!(obj instanceof CandlestickRenderer)) {
            return false;
        }
        CandlestickRenderer that = (CandlestickRenderer) obj;
        if (this.candleWidth == that.candleWidth && PaintUtilities.equal(this.upPaint, that.upPaint) && PaintUtilities.equal(this.downPaint, that.downPaint) && this.drawVolume == that.drawVolume && this.maxCandleWidthInMilliseconds == that.maxCandleWidthInMilliseconds && this.autoWidthMethod == that.autoWidthMethod && this.autoWidthFactor == that.autoWidthFactor && this.autoWidthGap == that.autoWidthGap && this.useOutlinePaint == that.useOutlinePaint && PaintUtilities.equal(this.volumePaint, that.volumePaint)) {
            return super.equals(obj);
        }
        return false;
    }

    public Object clone() throws CloneNotSupportedException {
        return super.clone();
    }

    private void writeObject(ObjectOutputStream stream) throws IOException {
        stream.defaultWriteObject();
        SerialUtilities.writePaint(this.upPaint, stream);
        SerialUtilities.writePaint(this.downPaint, stream);
        SerialUtilities.writePaint(this.volumePaint, stream);
    }

    private void readObject(ObjectInputStream stream) throws IOException, ClassNotFoundException {
        stream.defaultReadObject();
        this.upPaint = SerialUtilities.readPaint(stream);
        this.downPaint = SerialUtilities.readPaint(stream);
        this.volumePaint = SerialUtilities.readPaint(stream);
    }

    public boolean drawVolume() {
        return this.drawVolume;
    }
}
