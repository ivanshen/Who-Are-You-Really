package org.jfree.chart.renderer.xy;

import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.Paint;
import java.awt.Shape;
import java.awt.Stroke;
import java.awt.geom.Rectangle2D;
import org.jfree.chart.LegendItem;
import org.jfree.chart.LegendItemSource;
import org.jfree.chart.annotations.XYAnnotation;
import org.jfree.chart.axis.ValueAxis;
import org.jfree.chart.event.RendererChangeListener;
import org.jfree.chart.labels.ItemLabelPosition;
import org.jfree.chart.labels.XYItemLabelGenerator;
import org.jfree.chart.labels.XYSeriesLabelGenerator;
import org.jfree.chart.labels.XYToolTipGenerator;
import org.jfree.chart.plot.CrosshairState;
import org.jfree.chart.plot.Marker;
import org.jfree.chart.plot.PlotRenderingInfo;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.urls.XYURLGenerator;
import org.jfree.data.Range;
import org.jfree.data.xy.XYDataset;
import org.jfree.ui.Layer;

public interface XYItemRenderer extends LegendItemSource {
    void addAnnotation(XYAnnotation xYAnnotation);

    void addAnnotation(XYAnnotation xYAnnotation, Layer layer);

    void addChangeListener(RendererChangeListener rendererChangeListener);

    void drawAnnotations(Graphics2D graphics2D, Rectangle2D rectangle2D, ValueAxis valueAxis, ValueAxis valueAxis2, Layer layer, PlotRenderingInfo plotRenderingInfo);

    void drawDomainGridLine(Graphics2D graphics2D, XYPlot xYPlot, ValueAxis valueAxis, Rectangle2D rectangle2D, double d);

    void drawDomainMarker(Graphics2D graphics2D, XYPlot xYPlot, ValueAxis valueAxis, Marker marker, Rectangle2D rectangle2D);

    void drawItem(Graphics2D graphics2D, XYItemRendererState xYItemRendererState, Rectangle2D rectangle2D, PlotRenderingInfo plotRenderingInfo, XYPlot xYPlot, ValueAxis valueAxis, ValueAxis valueAxis2, XYDataset xYDataset, int i, int i2, CrosshairState crosshairState, int i3);

    void drawRangeLine(Graphics2D graphics2D, XYPlot xYPlot, ValueAxis valueAxis, Rectangle2D rectangle2D, double d, Paint paint, Stroke stroke);

    void drawRangeMarker(Graphics2D graphics2D, XYPlot xYPlot, ValueAxis valueAxis, Marker marker, Rectangle2D rectangle2D);

    void fillDomainGridBand(Graphics2D graphics2D, XYPlot xYPlot, ValueAxis valueAxis, Rectangle2D rectangle2D, double d, double d2);

    void fillRangeGridBand(Graphics2D graphics2D, XYPlot xYPlot, ValueAxis valueAxis, Rectangle2D rectangle2D, double d, double d2);

    Range findDomainBounds(XYDataset xYDataset);

    Range findRangeBounds(XYDataset xYDataset);

    Font getBaseItemLabelFont();

    XYItemLabelGenerator getBaseItemLabelGenerator();

    Paint getBaseItemLabelPaint();

    Boolean getBaseItemLabelsVisible();

    ItemLabelPosition getBaseNegativeItemLabelPosition();

    Paint getBaseOutlinePaint();

    Stroke getBaseOutlineStroke();

    Paint getBasePaint();

    ItemLabelPosition getBasePositiveItemLabelPosition();

    boolean getBaseSeriesVisible();

    boolean getBaseSeriesVisibleInLegend();

    Shape getBaseShape();

    Stroke getBaseStroke();

    XYToolTipGenerator getBaseToolTipGenerator();

    Font getItemLabelFont();

    Font getItemLabelFont(int i, int i2);

    XYItemLabelGenerator getItemLabelGenerator(int i, int i2);

    Paint getItemLabelPaint();

    Paint getItemLabelPaint(int i, int i2);

    Paint getItemOutlinePaint(int i, int i2);

    Stroke getItemOutlineStroke(int i, int i2);

    Paint getItemPaint(int i, int i2);

    Shape getItemShape(int i, int i2);

    Stroke getItemStroke(int i, int i2);

    boolean getItemVisible(int i, int i2);

    LegendItem getLegendItem(int i, int i2);

    XYSeriesLabelGenerator getLegendItemLabelGenerator();

    ItemLabelPosition getNegativeItemLabelPosition();

    ItemLabelPosition getNegativeItemLabelPosition(int i, int i2);

    int getPassCount();

    XYPlot getPlot();

    ItemLabelPosition getPositiveItemLabelPosition();

    ItemLabelPosition getPositiveItemLabelPosition(int i, int i2);

    Font getSeriesItemLabelFont(int i);

    XYItemLabelGenerator getSeriesItemLabelGenerator(int i);

    Paint getSeriesItemLabelPaint(int i);

    ItemLabelPosition getSeriesNegativeItemLabelPosition(int i);

    Paint getSeriesOutlinePaint(int i);

    Stroke getSeriesOutlineStroke(int i);

    Paint getSeriesPaint(int i);

    ItemLabelPosition getSeriesPositiveItemLabelPosition(int i);

    Shape getSeriesShape(int i);

    Stroke getSeriesStroke(int i);

    XYToolTipGenerator getSeriesToolTipGenerator(int i);

    Boolean getSeriesVisible();

    Boolean getSeriesVisible(int i);

    Boolean getSeriesVisibleInLegend();

    Boolean getSeriesVisibleInLegend(int i);

    XYToolTipGenerator getToolTipGenerator(int i, int i2);

    XYURLGenerator getURLGenerator();

    XYItemRendererState initialise(Graphics2D graphics2D, Rectangle2D rectangle2D, XYPlot xYPlot, XYDataset xYDataset, PlotRenderingInfo plotRenderingInfo);

    boolean isItemLabelVisible(int i, int i2);

    boolean isSeriesItemLabelsVisible(int i);

    boolean isSeriesVisible(int i);

    boolean isSeriesVisibleInLegend(int i);

    boolean removeAnnotation(XYAnnotation xYAnnotation);

    void removeAnnotations();

    void removeChangeListener(RendererChangeListener rendererChangeListener);

    void setBaseItemLabelFont(Font font);

    void setBaseItemLabelGenerator(XYItemLabelGenerator xYItemLabelGenerator);

    void setBaseItemLabelPaint(Paint paint);

    void setBaseItemLabelsVisible(Boolean bool);

    void setBaseItemLabelsVisible(Boolean bool, boolean z);

    void setBaseItemLabelsVisible(boolean z);

    void setBaseNegativeItemLabelPosition(ItemLabelPosition itemLabelPosition);

    void setBaseNegativeItemLabelPosition(ItemLabelPosition itemLabelPosition, boolean z);

    void setBaseOutlinePaint(Paint paint);

    void setBaseOutlineStroke(Stroke stroke);

    void setBasePaint(Paint paint);

    void setBasePositiveItemLabelPosition(ItemLabelPosition itemLabelPosition);

    void setBasePositiveItemLabelPosition(ItemLabelPosition itemLabelPosition, boolean z);

    void setBaseSeriesVisible(boolean z);

    void setBaseSeriesVisible(boolean z, boolean z2);

    void setBaseSeriesVisibleInLegend(boolean z);

    void setBaseSeriesVisibleInLegend(boolean z, boolean z2);

    void setBaseShape(Shape shape);

    void setBaseStroke(Stroke stroke);

    void setBaseToolTipGenerator(XYToolTipGenerator xYToolTipGenerator);

    void setItemLabelFont(Font font);

    void setItemLabelGenerator(XYItemLabelGenerator xYItemLabelGenerator);

    void setItemLabelPaint(Paint paint);

    void setItemLabelsVisible(Boolean bool);

    void setItemLabelsVisible(Boolean bool, boolean z);

    void setItemLabelsVisible(boolean z);

    void setLegendItemLabelGenerator(XYSeriesLabelGenerator xYSeriesLabelGenerator);

    void setNegativeItemLabelPosition(ItemLabelPosition itemLabelPosition);

    void setNegativeItemLabelPosition(ItemLabelPosition itemLabelPosition, boolean z);

    void setOutlinePaint(Paint paint);

    void setOutlineStroke(Stroke stroke);

    void setPaint(Paint paint);

    void setPlot(XYPlot xYPlot);

    void setPositiveItemLabelPosition(ItemLabelPosition itemLabelPosition);

    void setPositiveItemLabelPosition(ItemLabelPosition itemLabelPosition, boolean z);

    void setSeriesItemLabelFont(int i, Font font);

    void setSeriesItemLabelGenerator(int i, XYItemLabelGenerator xYItemLabelGenerator);

    void setSeriesItemLabelPaint(int i, Paint paint);

    void setSeriesItemLabelsVisible(int i, Boolean bool);

    void setSeriesItemLabelsVisible(int i, Boolean bool, boolean z);

    void setSeriesItemLabelsVisible(int i, boolean z);

    void setSeriesNegativeItemLabelPosition(int i, ItemLabelPosition itemLabelPosition);

    void setSeriesNegativeItemLabelPosition(int i, ItemLabelPosition itemLabelPosition, boolean z);

    void setSeriesOutlinePaint(int i, Paint paint);

    void setSeriesOutlineStroke(int i, Stroke stroke);

    void setSeriesPaint(int i, Paint paint);

    void setSeriesPositiveItemLabelPosition(int i, ItemLabelPosition itemLabelPosition);

    void setSeriesPositiveItemLabelPosition(int i, ItemLabelPosition itemLabelPosition, boolean z);

    void setSeriesShape(int i, Shape shape);

    void setSeriesStroke(int i, Stroke stroke);

    void setSeriesToolTipGenerator(int i, XYToolTipGenerator xYToolTipGenerator);

    void setSeriesVisible(int i, Boolean bool);

    void setSeriesVisible(int i, Boolean bool, boolean z);

    void setSeriesVisible(Boolean bool);

    void setSeriesVisible(Boolean bool, boolean z);

    void setSeriesVisibleInLegend(int i, Boolean bool);

    void setSeriesVisibleInLegend(int i, Boolean bool, boolean z);

    void setSeriesVisibleInLegend(Boolean bool);

    void setSeriesVisibleInLegend(Boolean bool, boolean z);

    void setShape(Shape shape);

    void setStroke(Stroke stroke);

    void setToolTipGenerator(XYToolTipGenerator xYToolTipGenerator);

    void setURLGenerator(XYURLGenerator xYURLGenerator);
}
