package org.jfree.chart.plot;

import java.awt.AlphaComposite;
import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Composite;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.Paint;
import java.awt.Polygon;
import java.awt.Rectangle;
import java.awt.Shape;
import java.awt.Stroke;
import java.awt.font.FontRenderContext;
import java.awt.geom.Arc2D;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Line2D;
import java.awt.geom.Point2D;
import java.awt.geom.Point2D.Double;
import java.awt.geom.Rectangle2D;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.List;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.LegendItem;
import org.jfree.chart.LegendItemCollection;
import org.jfree.chart.axis.DateAxis;
import org.jfree.chart.entity.CategoryItemEntity;
import org.jfree.chart.entity.EntityCollection;
import org.jfree.chart.labels.CategoryItemLabelGenerator;
import org.jfree.chart.labels.CategoryToolTipGenerator;
import org.jfree.chart.labels.StandardCategoryItemLabelGenerator;
import org.jfree.chart.urls.CategoryURLGenerator;
import org.jfree.chart.util.ParamChecks;
import org.jfree.data.category.CategoryDataset;
import org.jfree.data.general.DatasetChangeEvent;
import org.jfree.data.general.DatasetUtilities;
import org.jfree.data.xy.NormalizedMatrixSeries;
import org.jfree.io.SerialUtilities;
import org.jfree.util.ObjectUtilities;
import org.jfree.util.PaintList;
import org.jfree.util.PaintUtilities;
import org.jfree.util.Rotation;
import org.jfree.util.ShapeUtilities;
import org.jfree.util.StrokeList;
import org.jfree.util.TableOrder;

public class SpiderWebPlot extends Plot implements Cloneable, Serializable {
    public static final double DEFAULT_AXIS_LABEL_GAP = 0.1d;
    public static final double DEFAULT_HEAD = 0.01d;
    public static final double DEFAULT_INTERIOR_GAP = 0.25d;
    public static final Paint DEFAULT_LABEL_BACKGROUND_PAINT;
    public static final Font DEFAULT_LABEL_FONT;
    public static final Paint DEFAULT_LABEL_OUTLINE_PAINT;
    public static final Stroke DEFAULT_LABEL_OUTLINE_STROKE;
    public static final Paint DEFAULT_LABEL_PAINT;
    public static final Paint DEFAULT_LABEL_SHADOW_PAINT;
    public static final double DEFAULT_MAX_VALUE = -1.0d;
    public static final double DEFAULT_START_ANGLE = 90.0d;
    public static final double MAX_INTERIOR_GAP = 0.4d;
    private static final long serialVersionUID = -5376340422031599463L;
    private double axisLabelGap;
    private transient Paint axisLinePaint;
    private transient Stroke axisLineStroke;
    private transient Paint baseSeriesOutlinePaint;
    private transient Stroke baseSeriesOutlineStroke;
    private transient Paint baseSeriesPaint;
    private TableOrder dataExtractOrder;
    private CategoryDataset dataset;
    private Rotation direction;
    protected double headPercent;
    private double interiorGap;
    private Font labelFont;
    private CategoryItemLabelGenerator labelGenerator;
    private transient Paint labelPaint;
    private transient Shape legendItemShape;
    private double maxValue;
    private transient Paint seriesOutlinePaint;
    private PaintList seriesOutlinePaintList;
    private transient Stroke seriesOutlineStroke;
    private StrokeList seriesOutlineStrokeList;
    private transient Paint seriesPaint;
    private PaintList seriesPaintList;
    private double startAngle;
    private CategoryToolTipGenerator toolTipGenerator;
    private CategoryURLGenerator urlGenerator;
    private boolean webFilled;

    static {
        DEFAULT_LABEL_FONT = new Font("SansSerif", 0, 10);
        DEFAULT_LABEL_PAINT = Color.black;
        DEFAULT_LABEL_BACKGROUND_PAINT = new Color(255, 255, 192);
        DEFAULT_LABEL_OUTLINE_PAINT = Color.black;
        DEFAULT_LABEL_OUTLINE_STROKE = new BasicStroke(JFreeChart.DEFAULT_BACKGROUND_IMAGE_ALPHA);
        DEFAULT_LABEL_SHADOW_PAINT = Color.lightGray;
    }

    public SpiderWebPlot() {
        this(null);
    }

    public SpiderWebPlot(CategoryDataset dataset) {
        this(dataset, TableOrder.BY_ROW);
    }

    public SpiderWebPlot(CategoryDataset dataset, TableOrder extract) {
        this.webFilled = true;
        ParamChecks.nullNotPermitted(extract, "extract");
        this.dataset = dataset;
        if (dataset != null) {
            dataset.addChangeListener(this);
        }
        this.dataExtractOrder = extract;
        this.headPercent = DEFAULT_HEAD;
        this.axisLabelGap = DEFAULT_AXIS_LABEL_GAP;
        this.axisLinePaint = Color.black;
        this.axisLineStroke = new BasicStroke(Plot.DEFAULT_FOREGROUND_ALPHA);
        this.interiorGap = DEFAULT_INTERIOR_GAP;
        this.startAngle = DEFAULT_START_ANGLE;
        this.direction = Rotation.CLOCKWISE;
        this.maxValue = DEFAULT_MAX_VALUE;
        this.seriesPaint = null;
        this.seriesPaintList = new PaintList();
        this.baseSeriesPaint = null;
        this.seriesOutlinePaint = null;
        this.seriesOutlinePaintList = new PaintList();
        this.baseSeriesOutlinePaint = DEFAULT_OUTLINE_PAINT;
        this.seriesOutlineStroke = null;
        this.seriesOutlineStrokeList = new StrokeList();
        this.baseSeriesOutlineStroke = DEFAULT_OUTLINE_STROKE;
        this.labelFont = DEFAULT_LABEL_FONT;
        this.labelPaint = DEFAULT_LABEL_PAINT;
        this.labelGenerator = new StandardCategoryItemLabelGenerator();
        this.legendItemShape = DEFAULT_LEGEND_ITEM_CIRCLE;
    }

    public String getPlotType() {
        return "Spider Web Plot";
    }

    public CategoryDataset getDataset() {
        return this.dataset;
    }

    public void setDataset(CategoryDataset dataset) {
        if (this.dataset != null) {
            this.dataset.removeChangeListener(this);
        }
        this.dataset = dataset;
        if (dataset != null) {
            setDatasetGroup(dataset.getGroup());
            dataset.addChangeListener(this);
        }
        datasetChanged(new DatasetChangeEvent(this, dataset));
    }

    public boolean isWebFilled() {
        return this.webFilled;
    }

    public void setWebFilled(boolean flag) {
        this.webFilled = flag;
        fireChangeEvent();
    }

    public TableOrder getDataExtractOrder() {
        return this.dataExtractOrder;
    }

    public void setDataExtractOrder(TableOrder order) {
        ParamChecks.nullNotPermitted(order, "order");
        this.dataExtractOrder = order;
        fireChangeEvent();
    }

    public double getHeadPercent() {
        return this.headPercent;
    }

    public void setHeadPercent(double percent) {
        this.headPercent = percent;
        fireChangeEvent();
    }

    public double getStartAngle() {
        return this.startAngle;
    }

    public void setStartAngle(double angle) {
        this.startAngle = angle;
        fireChangeEvent();
    }

    public double getMaxValue() {
        return this.maxValue;
    }

    public void setMaxValue(double value) {
        this.maxValue = value;
        fireChangeEvent();
    }

    public Rotation getDirection() {
        return this.direction;
    }

    public void setDirection(Rotation direction) {
        ParamChecks.nullNotPermitted(direction, "direction");
        this.direction = direction;
        fireChangeEvent();
    }

    public double getInteriorGap() {
        return this.interiorGap;
    }

    public void setInteriorGap(double percent) {
        if (percent < 0.0d || percent > MAX_INTERIOR_GAP) {
            throw new IllegalArgumentException("Percentage outside valid range.");
        } else if (this.interiorGap != percent) {
            this.interiorGap = percent;
            fireChangeEvent();
        }
    }

    public double getAxisLabelGap() {
        return this.axisLabelGap;
    }

    public void setAxisLabelGap(double gap) {
        this.axisLabelGap = gap;
        fireChangeEvent();
    }

    public Paint getAxisLinePaint() {
        return this.axisLinePaint;
    }

    public void setAxisLinePaint(Paint paint) {
        ParamChecks.nullNotPermitted(paint, "paint");
        this.axisLinePaint = paint;
        fireChangeEvent();
    }

    public Stroke getAxisLineStroke() {
        return this.axisLineStroke;
    }

    public void setAxisLineStroke(Stroke stroke) {
        ParamChecks.nullNotPermitted(stroke, "stroke");
        this.axisLineStroke = stroke;
        fireChangeEvent();
    }

    public Paint getSeriesPaint() {
        return this.seriesPaint;
    }

    public void setSeriesPaint(Paint paint) {
        this.seriesPaint = paint;
        fireChangeEvent();
    }

    public Paint getSeriesPaint(int series) {
        if (this.seriesPaint != null) {
            return this.seriesPaint;
        }
        Paint result = this.seriesPaintList.getPaint(series);
        if (result != null) {
            return result;
        }
        DrawingSupplier supplier = getDrawingSupplier();
        if (supplier == null) {
            return this.baseSeriesPaint;
        }
        Paint p = supplier.getNextPaint();
        this.seriesPaintList.setPaint(series, p);
        return p;
    }

    public void setSeriesPaint(int series, Paint paint) {
        this.seriesPaintList.setPaint(series, paint);
        fireChangeEvent();
    }

    public Paint getBaseSeriesPaint() {
        return this.baseSeriesPaint;
    }

    public void setBaseSeriesPaint(Paint paint) {
        ParamChecks.nullNotPermitted(paint, "paint");
        this.baseSeriesPaint = paint;
        fireChangeEvent();
    }

    public Paint getSeriesOutlinePaint() {
        return this.seriesOutlinePaint;
    }

    public void setSeriesOutlinePaint(Paint paint) {
        this.seriesOutlinePaint = paint;
        fireChangeEvent();
    }

    public Paint getSeriesOutlinePaint(int series) {
        if (this.seriesOutlinePaint != null) {
            return this.seriesOutlinePaint;
        }
        Paint result = this.seriesOutlinePaintList.getPaint(series);
        if (result == null) {
            return this.baseSeriesOutlinePaint;
        }
        return result;
    }

    public void setSeriesOutlinePaint(int series, Paint paint) {
        this.seriesOutlinePaintList.setPaint(series, paint);
        fireChangeEvent();
    }

    public Paint getBaseSeriesOutlinePaint() {
        return this.baseSeriesOutlinePaint;
    }

    public void setBaseSeriesOutlinePaint(Paint paint) {
        ParamChecks.nullNotPermitted(paint, "paint");
        this.baseSeriesOutlinePaint = paint;
        fireChangeEvent();
    }

    public Stroke getSeriesOutlineStroke() {
        return this.seriesOutlineStroke;
    }

    public void setSeriesOutlineStroke(Stroke stroke) {
        this.seriesOutlineStroke = stroke;
        fireChangeEvent();
    }

    public Stroke getSeriesOutlineStroke(int series) {
        if (this.seriesOutlineStroke != null) {
            return this.seriesOutlineStroke;
        }
        Stroke result = this.seriesOutlineStrokeList.getStroke(series);
        if (result == null) {
            return this.baseSeriesOutlineStroke;
        }
        return result;
    }

    public void setSeriesOutlineStroke(int series, Stroke stroke) {
        this.seriesOutlineStrokeList.setStroke(series, stroke);
        fireChangeEvent();
    }

    public Stroke getBaseSeriesOutlineStroke() {
        return this.baseSeriesOutlineStroke;
    }

    public void setBaseSeriesOutlineStroke(Stroke stroke) {
        ParamChecks.nullNotPermitted(stroke, "stroke");
        this.baseSeriesOutlineStroke = stroke;
        fireChangeEvent();
    }

    public Shape getLegendItemShape() {
        return this.legendItemShape;
    }

    public void setLegendItemShape(Shape shape) {
        ParamChecks.nullNotPermitted(shape, "shape");
        this.legendItemShape = shape;
        fireChangeEvent();
    }

    public Font getLabelFont() {
        return this.labelFont;
    }

    public void setLabelFont(Font font) {
        ParamChecks.nullNotPermitted(font, "font");
        this.labelFont = font;
        fireChangeEvent();
    }

    public Paint getLabelPaint() {
        return this.labelPaint;
    }

    public void setLabelPaint(Paint paint) {
        ParamChecks.nullNotPermitted(paint, "paint");
        this.labelPaint = paint;
        fireChangeEvent();
    }

    public CategoryItemLabelGenerator getLabelGenerator() {
        return this.labelGenerator;
    }

    public void setLabelGenerator(CategoryItemLabelGenerator generator) {
        ParamChecks.nullNotPermitted(generator, "generator");
        this.labelGenerator = generator;
    }

    public CategoryToolTipGenerator getToolTipGenerator() {
        return this.toolTipGenerator;
    }

    public void setToolTipGenerator(CategoryToolTipGenerator generator) {
        this.toolTipGenerator = generator;
        fireChangeEvent();
    }

    public CategoryURLGenerator getURLGenerator() {
        return this.urlGenerator;
    }

    public void setURLGenerator(CategoryURLGenerator generator) {
        this.urlGenerator = generator;
        fireChangeEvent();
    }

    public LegendItemCollection getLegendItems() {
        LegendItemCollection result = new LegendItemCollection();
        if (getDataset() != null) {
            List keys = null;
            if (this.dataExtractOrder == TableOrder.BY_ROW) {
                keys = this.dataset.getRowKeys();
            } else if (this.dataExtractOrder == TableOrder.BY_COLUMN) {
                keys = this.dataset.getColumnKeys();
            }
            if (keys != null) {
                int series = 0;
                Shape shape = getLegendItemShape();
                for (Comparable key : keys) {
                    String label = key.toString();
                    String str = null;
                    LegendItem item = new LegendItem(label, label, null, str, shape, getSeriesPaint(series), getSeriesOutlineStroke(series), getSeriesOutlinePaint(series));
                    item.setDataset(getDataset());
                    item.setSeriesKey(key);
                    item.setSeriesIndex(series);
                    result.add(item);
                    series++;
                }
            }
        }
        return result;
    }

    protected Point2D getWebPoint(Rectangle2D bounds, double angle, double length) {
        double angrad = Math.toRadians(angle);
        return new Double((bounds.getX() + (((Math.cos(angrad) * length) * bounds.getWidth()) / DateAxis.DEFAULT_AUTO_RANGE_MINIMUM_SIZE_IN_MILLISECONDS)) + (bounds.getWidth() / DateAxis.DEFAULT_AUTO_RANGE_MINIMUM_SIZE_IN_MILLISECONDS), (bounds.getY() + ((((-Math.sin(angrad)) * length) * bounds.getHeight()) / DateAxis.DEFAULT_AUTO_RANGE_MINIMUM_SIZE_IN_MILLISECONDS)) + (bounds.getHeight() / DateAxis.DEFAULT_AUTO_RANGE_MINIMUM_SIZE_IN_MILLISECONDS));
    }

    public void draw(Graphics2D g2, Rectangle2D area, Point2D anchor, PlotState parentState, PlotRenderingInfo info) {
        getInsets().trim(area);
        if (info != null) {
            info.setPlotArea(area);
            info.setDataArea(area);
        }
        drawBackground(g2, area);
        drawOutline(g2, area);
        Shape savedClip = g2.getClip();
        g2.clip(area);
        Composite originalComposite = g2.getComposite();
        g2.setComposite(AlphaComposite.getInstance(3, getForegroundAlpha()));
        if (DatasetUtilities.isEmptyOrNull(this.dataset)) {
            drawNoDataMessage(g2, area);
        } else {
            int seriesCount;
            int catCount;
            if (this.dataExtractOrder == TableOrder.BY_ROW) {
                seriesCount = this.dataset.getRowCount();
                catCount = this.dataset.getColumnCount();
            } else {
                seriesCount = this.dataset.getColumnCount();
                catCount = this.dataset.getRowCount();
            }
            if (this.maxValue == DEFAULT_MAX_VALUE) {
                calculateMaxValue(seriesCount, catCount);
            }
            double gapHorizontal = area.getWidth() * getInteriorGap();
            double gapVertical = area.getHeight() * getInteriorGap();
            double X = area.getX() + (gapHorizontal / DateAxis.DEFAULT_AUTO_RANGE_MINIMUM_SIZE_IN_MILLISECONDS);
            double Y = area.getY() + (gapVertical / DateAxis.DEFAULT_AUTO_RANGE_MINIMUM_SIZE_IN_MILLISECONDS);
            double W = area.getWidth() - gapHorizontal;
            double H = area.getHeight() - gapVertical;
            double headW = area.getWidth() * this.headPercent;
            double headH = area.getHeight() * this.headPercent;
            double min = Math.min(W, H) / DateAxis.DEFAULT_AUTO_RANGE_MINIMUM_SIZE_IN_MILLISECONDS;
            X = (((X + X) + W) / DateAxis.DEFAULT_AUTO_RANGE_MINIMUM_SIZE_IN_MILLISECONDS) - min;
            Y = (((Y + Y) + H) / DateAxis.DEFAULT_AUTO_RANGE_MINIMUM_SIZE_IN_MILLISECONDS) - min;
            W = DateAxis.DEFAULT_AUTO_RANGE_MINIMUM_SIZE_IN_MILLISECONDS * min;
            H = DateAxis.DEFAULT_AUTO_RANGE_MINIMUM_SIZE_IN_MILLISECONDS * min;
            Double doubleR = new Double((W / DateAxis.DEFAULT_AUTO_RANGE_MINIMUM_SIZE_IN_MILLISECONDS) + X, (H / DateAxis.DEFAULT_AUTO_RANGE_MINIMUM_SIZE_IN_MILLISECONDS) + Y);
            Rectangle2D radarArea = new Rectangle2D.Double(X, Y, W, H);
            for (int cat = 0; cat < catCount; cat++) {
                double d = (double) catCount;
                double angle = getStartAngle() + (((getDirection().getFactor() * ((double) cat)) * 360.0d) / r0);
                Line2D.Double doubleR2 = new Line2D.Double(doubleR, getWebPoint(radarArea, angle, NormalizedMatrixSeries.DEFAULT_SCALE_FACTOR));
                g2.setPaint(this.axisLinePaint);
                g2.setStroke(this.axisLineStroke);
                g2.draw(doubleR2);
                drawLabel(g2, radarArea, 0.0d, cat, angle, 360.0d / ((double) catCount));
            }
            for (int series = 0; series < seriesCount; series++) {
                drawRadarPoly(g2, radarArea, doubleR, info, series, catCount, headH, headW);
            }
        }
        g2.setClip(savedClip);
        g2.setComposite(originalComposite);
        drawOutline(g2, area);
    }

    private void calculateMaxValue(int seriesCount, int catCount) {
        for (int seriesIndex = 0; seriesIndex < seriesCount; seriesIndex++) {
            for (int catIndex = 0; catIndex < catCount; catIndex++) {
                Number nV = getPlotValue(seriesIndex, catIndex);
                if (nV != null) {
                    double v = nV.doubleValue();
                    if (v > this.maxValue) {
                        this.maxValue = v;
                    }
                }
            }
        }
    }

    protected void drawRadarPoly(Graphics2D g2, Rectangle2D plotArea, Point2D centre, PlotRenderingInfo info, int series, int catCount, double headH, double headW) {
        Polygon polygon = new Polygon();
        EntityCollection entities = null;
        if (info != null) {
            entities = info.getOwner().getEntityCollection();
        }
        for (int cat = 0; cat < catCount; cat++) {
            Number dataValue = getPlotValue(series, cat);
            if (dataValue != null) {
                double value = dataValue.doubleValue();
                if (value >= 0.0d) {
                    double d = (double) catCount;
                    Point2D point = getWebPoint(plotArea, getStartAngle() + (((getDirection().getFactor() * ((double) cat)) * 360.0d) / r0), value / this.maxValue);
                    polygon.addPoint((int) point.getX(), (int) point.getY());
                    Paint paint = getSeriesPaint(series);
                    Paint outlinePaint = getSeriesOutlinePaint(series);
                    Stroke outlineStroke = getSeriesOutlineStroke(series);
                    Ellipse2D head = new Ellipse2D.Double(point.getX() - (headW / DateAxis.DEFAULT_AUTO_RANGE_MINIMUM_SIZE_IN_MILLISECONDS), point.getY() - (headH / DateAxis.DEFAULT_AUTO_RANGE_MINIMUM_SIZE_IN_MILLISECONDS), headW, headH);
                    g2.setPaint(paint);
                    g2.fill(head);
                    g2.setStroke(outlineStroke);
                    g2.setPaint(outlinePaint);
                    g2.draw(head);
                    if (entities != null) {
                        int row;
                        int col;
                        if (this.dataExtractOrder == TableOrder.BY_ROW) {
                            row = series;
                            col = cat;
                        } else {
                            row = cat;
                            col = series;
                        }
                        String tip = null;
                        if (this.toolTipGenerator != null) {
                            tip = this.toolTipGenerator.generateToolTip(this.dataset, row, col);
                        }
                        String url = null;
                        if (this.urlGenerator != null) {
                            url = this.urlGenerator.generateURL(this.dataset, row, col);
                        }
                        entities.add(new CategoryItemEntity(new Rectangle((int) (point.getX() - headW), (int) (point.getY() - headH), (int) (DateAxis.DEFAULT_AUTO_RANGE_MINIMUM_SIZE_IN_MILLISECONDS * headW), (int) (DateAxis.DEFAULT_AUTO_RANGE_MINIMUM_SIZE_IN_MILLISECONDS * headH)), tip, url, this.dataset, this.dataset.getRowKey(row), this.dataset.getColumnKey(col)));
                    }
                }
            }
        }
        g2.setPaint(getSeriesPaint(series));
        g2.setStroke(getSeriesOutlineStroke(series));
        g2.draw(polygon);
        if (this.webFilled) {
            g2.setComposite(AlphaComposite.getInstance(3, 0.1f));
            g2.fill(polygon);
            g2.setComposite(AlphaComposite.getInstance(3, getForegroundAlpha()));
        }
    }

    protected Number getPlotValue(int series, int cat) {
        if (this.dataExtractOrder == TableOrder.BY_ROW) {
            return this.dataset.getValue(series, cat);
        }
        if (this.dataExtractOrder == TableOrder.BY_COLUMN) {
            return this.dataset.getValue(cat, series);
        }
        return null;
    }

    protected void drawLabel(Graphics2D g2, Rectangle2D plotArea, double value, int cat, double startAngle, double extent) {
        String label;
        FontRenderContext frc = g2.getFontRenderContext();
        if (this.dataExtractOrder == TableOrder.BY_ROW) {
            label = this.labelGenerator.generateColumnLabel(this.dataset, cat);
        } else {
            label = this.labelGenerator.generateRowLabel(this.dataset, cat);
        }
        Point2D labelLocation = calculateLabelLocation(getLabelFont().getStringBounds(label, frc), (double) getLabelFont().getLineMetrics(label, frc).getAscent(), plotArea, startAngle);
        Composite saveComposite = g2.getComposite();
        g2.setComposite(AlphaComposite.getInstance(3, Plot.DEFAULT_FOREGROUND_ALPHA));
        g2.setPaint(getLabelPaint());
        g2.setFont(getLabelFont());
        g2.drawString(label, (float) labelLocation.getX(), (float) labelLocation.getY());
        g2.setComposite(saveComposite);
    }

    protected Point2D calculateLabelLocation(Rectangle2D labelBounds, double ascent, Rectangle2D plotArea, double startAngle) {
        Point2D point1 = new Arc2D.Double(plotArea, startAngle, 0.0d, 0).getEndPoint();
        double labelX = point1.getX() - ((-(point1.getX() - plotArea.getCenterX())) * this.axisLabelGap);
        double labelY = point1.getY() - ((-(point1.getY() - plotArea.getCenterY())) * this.axisLabelGap);
        if (labelX < plotArea.getCenterX()) {
            labelX -= labelBounds.getWidth();
        }
        if (labelX == plotArea.getCenterX()) {
            labelX -= labelBounds.getWidth() / DateAxis.DEFAULT_AUTO_RANGE_MINIMUM_SIZE_IN_MILLISECONDS;
        }
        if (labelY > plotArea.getCenterY()) {
            labelY += ascent;
        }
        return new Double(labelX, labelY);
    }

    public boolean equals(Object obj) {
        if (obj == this) {
            return true;
        }
        if (!(obj instanceof SpiderWebPlot)) {
            return false;
        }
        if (!super.equals(obj)) {
            return false;
        }
        SpiderWebPlot that = (SpiderWebPlot) obj;
        if (!this.dataExtractOrder.equals(that.dataExtractOrder)) {
            return false;
        }
        if (this.headPercent != that.headPercent) {
            return false;
        }
        if (this.interiorGap != that.interiorGap) {
            return false;
        }
        if (this.startAngle != that.startAngle) {
            return false;
        }
        if (!this.direction.equals(that.direction)) {
            return false;
        }
        if (this.maxValue != that.maxValue) {
            return false;
        }
        if (this.webFilled != that.webFilled) {
            return false;
        }
        if (this.axisLabelGap != that.axisLabelGap) {
            return false;
        }
        if (!PaintUtilities.equal(this.axisLinePaint, that.axisLinePaint)) {
            return false;
        }
        if (!this.axisLineStroke.equals(that.axisLineStroke)) {
            return false;
        }
        if (!ShapeUtilities.equal(this.legendItemShape, that.legendItemShape)) {
            return false;
        }
        if (!PaintUtilities.equal(this.seriesPaint, that.seriesPaint)) {
            return false;
        }
        if (!this.seriesPaintList.equals(that.seriesPaintList)) {
            return false;
        }
        if (!PaintUtilities.equal(this.baseSeriesPaint, that.baseSeriesPaint)) {
            return false;
        }
        if (!PaintUtilities.equal(this.seriesOutlinePaint, that.seriesOutlinePaint)) {
            return false;
        }
        if (!this.seriesOutlinePaintList.equals(that.seriesOutlinePaintList)) {
            return false;
        }
        if (!PaintUtilities.equal(this.baseSeriesOutlinePaint, that.baseSeriesOutlinePaint)) {
            return false;
        }
        if (!ObjectUtilities.equal(this.seriesOutlineStroke, that.seriesOutlineStroke)) {
            return false;
        }
        if (!this.seriesOutlineStrokeList.equals(that.seriesOutlineStrokeList)) {
            return false;
        }
        if (!this.baseSeriesOutlineStroke.equals(that.baseSeriesOutlineStroke)) {
            return false;
        }
        if (!this.labelFont.equals(that.labelFont)) {
            return false;
        }
        if (!PaintUtilities.equal(this.labelPaint, that.labelPaint)) {
            return false;
        }
        if (!this.labelGenerator.equals(that.labelGenerator)) {
            return false;
        }
        if (!ObjectUtilities.equal(this.toolTipGenerator, that.toolTipGenerator)) {
            return false;
        }
        if (ObjectUtilities.equal(this.urlGenerator, that.urlGenerator)) {
            return true;
        }
        return false;
    }

    public Object clone() throws CloneNotSupportedException {
        SpiderWebPlot clone = (SpiderWebPlot) super.clone();
        clone.legendItemShape = ShapeUtilities.clone(this.legendItemShape);
        clone.seriesPaintList = (PaintList) this.seriesPaintList.clone();
        clone.seriesOutlinePaintList = (PaintList) this.seriesOutlinePaintList.clone();
        clone.seriesOutlineStrokeList = (StrokeList) this.seriesOutlineStrokeList.clone();
        return clone;
    }

    private void writeObject(ObjectOutputStream stream) throws IOException {
        stream.defaultWriteObject();
        SerialUtilities.writeShape(this.legendItemShape, stream);
        SerialUtilities.writePaint(this.seriesPaint, stream);
        SerialUtilities.writePaint(this.baseSeriesPaint, stream);
        SerialUtilities.writePaint(this.seriesOutlinePaint, stream);
        SerialUtilities.writePaint(this.baseSeriesOutlinePaint, stream);
        SerialUtilities.writeStroke(this.seriesOutlineStroke, stream);
        SerialUtilities.writeStroke(this.baseSeriesOutlineStroke, stream);
        SerialUtilities.writePaint(this.labelPaint, stream);
        SerialUtilities.writePaint(this.axisLinePaint, stream);
        SerialUtilities.writeStroke(this.axisLineStroke, stream);
    }

    private void readObject(ObjectInputStream stream) throws IOException, ClassNotFoundException {
        stream.defaultReadObject();
        this.legendItemShape = SerialUtilities.readShape(stream);
        this.seriesPaint = SerialUtilities.readPaint(stream);
        this.baseSeriesPaint = SerialUtilities.readPaint(stream);
        this.seriesOutlinePaint = SerialUtilities.readPaint(stream);
        this.baseSeriesOutlinePaint = SerialUtilities.readPaint(stream);
        this.seriesOutlineStroke = SerialUtilities.readStroke(stream);
        this.baseSeriesOutlineStroke = SerialUtilities.readStroke(stream);
        this.labelPaint = SerialUtilities.readPaint(stream);
        this.axisLinePaint = SerialUtilities.readPaint(stream);
        this.axisLineStroke = SerialUtilities.readStroke(stream);
        if (this.dataset != null) {
            this.dataset.addChangeListener(this);
        }
    }
}
