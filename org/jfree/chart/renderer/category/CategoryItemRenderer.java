package org.jfree.chart.renderer.category;

import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.Paint;
import java.awt.Shape;
import java.awt.Stroke;
import java.awt.geom.Rectangle2D;
import org.jfree.chart.LegendItem;
import org.jfree.chart.LegendItemSource;
import org.jfree.chart.axis.CategoryAxis;
import org.jfree.chart.axis.ValueAxis;
import org.jfree.chart.event.RendererChangeListener;
import org.jfree.chart.labels.CategoryItemLabelGenerator;
import org.jfree.chart.labels.CategoryToolTipGenerator;
import org.jfree.chart.labels.ItemLabelPosition;
import org.jfree.chart.plot.CategoryMarker;
import org.jfree.chart.plot.CategoryPlot;
import org.jfree.chart.plot.Marker;
import org.jfree.chart.plot.PlotRenderingInfo;
import org.jfree.chart.urls.CategoryURLGenerator;
import org.jfree.data.Range;
import org.jfree.data.category.CategoryDataset;
import org.jfree.ui.RectangleEdge;

public interface CategoryItemRenderer extends LegendItemSource {
    void addChangeListener(RendererChangeListener rendererChangeListener);

    void drawBackground(Graphics2D graphics2D, CategoryPlot categoryPlot, Rectangle2D rectangle2D);

    void drawDomainGridline(Graphics2D graphics2D, CategoryPlot categoryPlot, Rectangle2D rectangle2D, double d);

    void drawDomainMarker(Graphics2D graphics2D, CategoryPlot categoryPlot, CategoryAxis categoryAxis, CategoryMarker categoryMarker, Rectangle2D rectangle2D);

    void drawItem(Graphics2D graphics2D, CategoryItemRendererState categoryItemRendererState, Rectangle2D rectangle2D, CategoryPlot categoryPlot, CategoryAxis categoryAxis, ValueAxis valueAxis, CategoryDataset categoryDataset, int i, int i2, int i3);

    void drawOutline(Graphics2D graphics2D, CategoryPlot categoryPlot, Rectangle2D rectangle2D);

    void drawRangeGridline(Graphics2D graphics2D, CategoryPlot categoryPlot, ValueAxis valueAxis, Rectangle2D rectangle2D, double d);

    void drawRangeMarker(Graphics2D graphics2D, CategoryPlot categoryPlot, ValueAxis valueAxis, Marker marker, Rectangle2D rectangle2D);

    Range findRangeBounds(CategoryDataset categoryDataset);

    Font getBaseItemLabelFont();

    CategoryItemLabelGenerator getBaseItemLabelGenerator();

    Paint getBaseItemLabelPaint();

    Boolean getBaseItemLabelsVisible();

    CategoryURLGenerator getBaseItemURLGenerator();

    ItemLabelPosition getBaseNegativeItemLabelPosition();

    Paint getBaseOutlinePaint();

    Stroke getBaseOutlineStroke();

    Paint getBasePaint();

    ItemLabelPosition getBasePositiveItemLabelPosition();

    boolean getBaseSeriesVisible();

    boolean getBaseSeriesVisibleInLegend();

    Shape getBaseShape();

    Stroke getBaseStroke();

    CategoryToolTipGenerator getBaseToolTipGenerator();

    Font getItemLabelFont();

    Font getItemLabelFont(int i, int i2);

    CategoryItemLabelGenerator getItemLabelGenerator(int i, int i2);

    Paint getItemLabelPaint();

    Paint getItemLabelPaint(int i, int i2);

    double getItemMiddle(Comparable comparable, Comparable comparable2, CategoryDataset categoryDataset, CategoryAxis categoryAxis, Rectangle2D rectangle2D, RectangleEdge rectangleEdge);

    Paint getItemOutlinePaint(int i, int i2);

    Stroke getItemOutlineStroke(int i, int i2);

    Paint getItemPaint(int i, int i2);

    Shape getItemShape(int i, int i2);

    Stroke getItemStroke(int i, int i2);

    CategoryURLGenerator getItemURLGenerator(int i, int i2);

    boolean getItemVisible(int i, int i2);

    LegendItem getLegendItem(int i, int i2);

    ItemLabelPosition getNegativeItemLabelPosition();

    ItemLabelPosition getNegativeItemLabelPosition(int i, int i2);

    int getPassCount();

    CategoryPlot getPlot();

    ItemLabelPosition getPositiveItemLabelPosition();

    ItemLabelPosition getPositiveItemLabelPosition(int i, int i2);

    Font getSeriesItemLabelFont(int i);

    CategoryItemLabelGenerator getSeriesItemLabelGenerator(int i);

    Paint getSeriesItemLabelPaint(int i);

    CategoryURLGenerator getSeriesItemURLGenerator(int i);

    ItemLabelPosition getSeriesNegativeItemLabelPosition(int i);

    Paint getSeriesOutlinePaint(int i);

    Stroke getSeriesOutlineStroke(int i);

    Paint getSeriesPaint(int i);

    ItemLabelPosition getSeriesPositiveItemLabelPosition(int i);

    Shape getSeriesShape(int i);

    Stroke getSeriesStroke(int i);

    CategoryToolTipGenerator getSeriesToolTipGenerator(int i);

    Boolean getSeriesVisible();

    Boolean getSeriesVisible(int i);

    Boolean getSeriesVisibleInLegend();

    Boolean getSeriesVisibleInLegend(int i);

    CategoryToolTipGenerator getToolTipGenerator();

    CategoryToolTipGenerator getToolTipGenerator(int i, int i2);

    CategoryItemRendererState initialise(Graphics2D graphics2D, Rectangle2D rectangle2D, CategoryPlot categoryPlot, int i, PlotRenderingInfo plotRenderingInfo);

    boolean isItemLabelVisible(int i, int i2);

    boolean isSeriesItemLabelsVisible(int i);

    boolean isSeriesVisible(int i);

    boolean isSeriesVisibleInLegend(int i);

    void removeChangeListener(RendererChangeListener rendererChangeListener);

    void setBaseItemLabelFont(Font font);

    void setBaseItemLabelGenerator(CategoryItemLabelGenerator categoryItemLabelGenerator);

    void setBaseItemLabelPaint(Paint paint);

    void setBaseItemLabelsVisible(Boolean bool);

    void setBaseItemLabelsVisible(Boolean bool, boolean z);

    void setBaseItemLabelsVisible(boolean z);

    void setBaseItemURLGenerator(CategoryURLGenerator categoryURLGenerator);

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

    void setBaseToolTipGenerator(CategoryToolTipGenerator categoryToolTipGenerator);

    void setItemLabelFont(Font font);

    void setItemLabelGenerator(CategoryItemLabelGenerator categoryItemLabelGenerator);

    void setItemLabelPaint(Paint paint);

    void setItemLabelsVisible(Boolean bool);

    void setItemLabelsVisible(Boolean bool, boolean z);

    void setItemLabelsVisible(boolean z);

    void setItemURLGenerator(CategoryURLGenerator categoryURLGenerator);

    void setNegativeItemLabelPosition(ItemLabelPosition itemLabelPosition);

    void setNegativeItemLabelPosition(ItemLabelPosition itemLabelPosition, boolean z);

    void setOutlinePaint(Paint paint);

    void setOutlineStroke(Stroke stroke);

    void setPaint(Paint paint);

    void setPlot(CategoryPlot categoryPlot);

    void setPositiveItemLabelPosition(ItemLabelPosition itemLabelPosition);

    void setPositiveItemLabelPosition(ItemLabelPosition itemLabelPosition, boolean z);

    void setSeriesItemLabelFont(int i, Font font);

    void setSeriesItemLabelGenerator(int i, CategoryItemLabelGenerator categoryItemLabelGenerator);

    void setSeriesItemLabelPaint(int i, Paint paint);

    void setSeriesItemLabelsVisible(int i, Boolean bool);

    void setSeriesItemLabelsVisible(int i, Boolean bool, boolean z);

    void setSeriesItemLabelsVisible(int i, boolean z);

    void setSeriesItemURLGenerator(int i, CategoryURLGenerator categoryURLGenerator);

    void setSeriesNegativeItemLabelPosition(int i, ItemLabelPosition itemLabelPosition);

    void setSeriesNegativeItemLabelPosition(int i, ItemLabelPosition itemLabelPosition, boolean z);

    void setSeriesOutlinePaint(int i, Paint paint);

    void setSeriesOutlineStroke(int i, Stroke stroke);

    void setSeriesPaint(int i, Paint paint);

    void setSeriesPositiveItemLabelPosition(int i, ItemLabelPosition itemLabelPosition);

    void setSeriesPositiveItemLabelPosition(int i, ItemLabelPosition itemLabelPosition, boolean z);

    void setSeriesShape(int i, Shape shape);

    void setSeriesStroke(int i, Stroke stroke);

    void setSeriesToolTipGenerator(int i, CategoryToolTipGenerator categoryToolTipGenerator);

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

    void setToolTipGenerator(CategoryToolTipGenerator categoryToolTipGenerator);
}
