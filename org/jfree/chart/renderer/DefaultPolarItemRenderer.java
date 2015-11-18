package org.jfree.chart.renderer;

import java.awt.AlphaComposite;
import java.awt.Composite;
import java.awt.Graphics2D;
import java.awt.Paint;
import java.awt.Point;
import java.awt.Shape;
import java.awt.Stroke;
import java.awt.geom.Ellipse2D;
import java.awt.geom.GeneralPath;
import java.awt.geom.Line2D.Double;
import java.awt.geom.PathIterator;
import java.awt.geom.Rectangle2D;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.List;
import org.jfree.chart.LegendItem;
import org.jfree.chart.axis.DateAxis;
import org.jfree.chart.axis.NumberTick;
import org.jfree.chart.axis.ValueAxis;
import org.jfree.chart.entity.EntityCollection;
import org.jfree.chart.entity.XYItemEntity;
import org.jfree.chart.labels.XYSeriesLabelGenerator;
import org.jfree.chart.labels.XYToolTipGenerator;
import org.jfree.chart.plot.DrawingSupplier;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.plot.PlotRenderingInfo;
import org.jfree.chart.plot.PolarPlot;
import org.jfree.chart.renderer.xy.AbstractXYItemRenderer;
import org.jfree.chart.urls.XYURLGenerator;
import org.jfree.chart.util.ParamChecks;
import org.jfree.data.xy.XYDataset;
import org.jfree.io.SerialUtilities;
import org.jfree.text.TextUtilities;
import org.jfree.util.BooleanList;
import org.jfree.util.BooleanUtilities;
import org.jfree.util.ObjectList;
import org.jfree.util.ObjectUtilities;
import org.jfree.util.PublicCloneable;
import org.jfree.util.ShapeUtilities;

public class DefaultPolarItemRenderer extends AbstractRenderer implements PolarItemRenderer {
    static final /* synthetic */ boolean $assertionsDisabled;
    private XYToolTipGenerator baseToolTipGenerator;
    private boolean connectFirstAndLastPoint;
    private boolean drawOutlineWhenFilled;
    private transient Composite fillComposite;
    private XYSeriesLabelGenerator legendItemToolTipGenerator;
    private XYSeriesLabelGenerator legendItemURLGenerator;
    private transient Shape legendLine;
    private PolarPlot plot;
    private BooleanList seriesFilled;
    private boolean shapesVisible;
    private ObjectList toolTipGeneratorList;
    private XYURLGenerator urlGenerator;
    private boolean useFillPaint;

    static {
        $assertionsDisabled = !DefaultPolarItemRenderer.class.desiredAssertionStatus();
    }

    public DefaultPolarItemRenderer() {
        this.seriesFilled = new BooleanList();
        this.drawOutlineWhenFilled = true;
        this.fillComposite = AlphaComposite.getInstance(3, 0.3f);
        this.useFillPaint = false;
        this.legendLine = new Double(-7.0d, 0.0d, 7.0d, 0.0d);
        this.shapesVisible = true;
        this.connectFirstAndLastPoint = true;
        this.toolTipGeneratorList = new ObjectList();
        this.urlGenerator = null;
        this.legendItemToolTipGenerator = null;
        this.legendItemURLGenerator = null;
    }

    public void setPlot(PolarPlot plot) {
        this.plot = plot;
    }

    public PolarPlot getPlot() {
        return this.plot;
    }

    public boolean getDrawOutlineWhenFilled() {
        return this.drawOutlineWhenFilled;
    }

    public void setDrawOutlineWhenFilled(boolean drawOutlineWhenFilled) {
        this.drawOutlineWhenFilled = drawOutlineWhenFilled;
        fireChangeEvent();
    }

    public Composite getFillComposite() {
        return this.fillComposite;
    }

    public void setFillComposite(Composite composite) {
        ParamChecks.nullNotPermitted(composite, "composite");
        this.fillComposite = composite;
        fireChangeEvent();
    }

    public boolean getShapesVisible() {
        return this.shapesVisible;
    }

    public void setShapesVisible(boolean visible) {
        this.shapesVisible = visible;
        fireChangeEvent();
    }

    public boolean getConnectFirstAndLastPoint() {
        return this.connectFirstAndLastPoint;
    }

    public void setConnectFirstAndLastPoint(boolean connect) {
        this.connectFirstAndLastPoint = connect;
        fireChangeEvent();
    }

    public DrawingSupplier getDrawingSupplier() {
        PolarPlot p = getPlot();
        if (p != null) {
            return p.getDrawingSupplier();
        }
        return null;
    }

    public boolean isSeriesFilled(int series) {
        Boolean b = this.seriesFilled.getBoolean(series);
        if (b != null) {
            return b.booleanValue();
        }
        return false;
    }

    public void setSeriesFilled(int series, boolean filled) {
        this.seriesFilled.setBoolean(series, BooleanUtilities.valueOf(filled));
    }

    public boolean getUseFillPaint() {
        return this.useFillPaint;
    }

    public void setUseFillPaint(boolean flag) {
        this.useFillPaint = flag;
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

    protected void addEntity(EntityCollection entities, Shape area, XYDataset dataset, int series, int item, double entityX, double entityY) {
        if (getItemCreateEntity(series, item)) {
            Shape hotspot = area;
            if (hotspot == null) {
                double r = (double) getDefaultEntityRadius();
                double w = r * DateAxis.DEFAULT_AUTO_RANGE_MINIMUM_SIZE_IN_MILLISECONDS;
                if (getPlot().getOrientation() == PlotOrientation.VERTICAL) {
                    hotspot = new Ellipse2D.Double(entityX - r, entityY - r, w, w);
                } else {
                    hotspot = new Ellipse2D.Double(entityY - r, entityX - r, w, w);
                }
            }
            String tip = null;
            XYToolTipGenerator generator = getToolTipGenerator(series, item);
            if (generator != null) {
                tip = generator.generateToolTip(dataset, series, item);
            }
            String url = null;
            if (getURLGenerator() != null) {
                url = getURLGenerator().generateURL(dataset, series, item);
            }
            entities.add(new XYItemEntity(hotspot, dataset, series, item, tip, url));
        }
    }

    public void drawSeries(Graphics2D g2, Rectangle2D dataArea, PlotRenderingInfo info, PolarPlot plot, XYDataset dataset, int seriesIndex) {
        int numPoints = dataset.getItemCount(seriesIndex);
        if (numPoints != 0) {
            int i;
            GeneralPath poly = null;
            ValueAxis axis = plot.getAxisForDataset(plot.indexOf(dataset));
            for (i = 0; i < numPoints; i++) {
                Point p = plot.translateToJava2D(dataset.getXValue(seriesIndex, i), dataset.getYValue(seriesIndex, i), axis, dataArea);
                if (poly == null) {
                    poly = new GeneralPath();
                    poly.moveTo((float) p.x, (float) p.y);
                } else {
                    poly.lineTo((float) p.x, (float) p.y);
                }
            }
            if ($assertionsDisabled || poly != null) {
                if (getConnectFirstAndLastPoint()) {
                    poly.closePath();
                }
                g2.setPaint(lookupSeriesPaint(seriesIndex));
                g2.setStroke(lookupSeriesStroke(seriesIndex));
                if (isSeriesFilled(seriesIndex)) {
                    Composite savedComposite = g2.getComposite();
                    g2.setComposite(this.fillComposite);
                    g2.fill(poly);
                    g2.setComposite(savedComposite);
                    if (this.drawOutlineWhenFilled) {
                        g2.setPaint(lookupSeriesOutlinePaint(seriesIndex));
                        g2.draw(poly);
                    }
                } else {
                    g2.draw(poly);
                }
                if (this.shapesVisible) {
                    EntityCollection entities = null;
                    if (info != null) {
                        entities = info.getOwner().getEntityCollection();
                    }
                    PathIterator pi = poly.getPathIterator(null);
                    i = 0;
                    while (!pi.isDone()) {
                        float[] coords = new float[6];
                        int segType = pi.currentSegment(coords);
                        pi.next();
                        if (segType == 1 || segType == 0) {
                            Paint paint;
                            int x = Math.round(coords[0]);
                            int y = Math.round(coords[1]);
                            int i2 = i + 1;
                            Shape shape = ShapeUtilities.createTranslatedShape(getItemShape(seriesIndex, i), (double) x, (double) y);
                            if (this.useFillPaint) {
                                paint = lookupSeriesFillPaint(seriesIndex);
                            } else {
                                paint = lookupSeriesPaint(seriesIndex);
                            }
                            g2.setPaint(paint);
                            g2.fill(shape);
                            if (isSeriesFilled(seriesIndex) && this.drawOutlineWhenFilled) {
                                g2.setPaint(lookupSeriesOutlinePaint(seriesIndex));
                                g2.setStroke(lookupSeriesOutlineStroke(seriesIndex));
                                g2.draw(shape);
                            }
                            if (entities != null) {
                                if (AbstractXYItemRenderer.isPointInRect(dataArea, (double) x, (double) y)) {
                                    XYDataset xYDataset = dataset;
                                    int i3 = seriesIndex;
                                    addEntity(entities, shape, xYDataset, i3, i2 - 1, (double) x, (double) y);
                                }
                            }
                            i = i2;
                        }
                    }
                    return;
                }
                return;
            }
            throw new AssertionError();
        }
    }

    public void drawAngularGridLines(Graphics2D g2, PolarPlot plot, List ticks, Rectangle2D dataArea) {
        double outerValue;
        double centerValue;
        g2.setFont(plot.getAngleLabelFont());
        g2.setStroke(plot.getAngleGridlineStroke());
        g2.setPaint(plot.getAngleGridlinePaint());
        ValueAxis axis = plot.getAxis();
        if (axis.isInverted()) {
            outerValue = axis.getLowerBound();
            centerValue = axis.getUpperBound();
        } else {
            outerValue = axis.getUpperBound();
            centerValue = axis.getLowerBound();
        }
        Point center = plot.translateToJava2D(0.0d, centerValue, axis, dataArea);
        for (NumberTick tick : ticks) {
            Point p = plot.translateToJava2D(tick.getNumber().doubleValue(), outerValue, axis, dataArea);
            g2.setPaint(plot.getAngleGridlinePaint());
            g2.drawLine(center.x, center.y, p.x, p.y);
            if (plot.isAngleLabelsVisible()) {
                int x = p.x;
                int y = p.y;
                g2.setPaint(plot.getAngleLabelPaint());
                TextUtilities.drawAlignedString(tick.getText(), g2, (float) x, (float) y, tick.getTextAnchor());
            }
        }
    }

    public void drawRadialGridLines(Graphics2D g2, PolarPlot plot, ValueAxis radialAxis, List ticks, Rectangle2D dataArea) {
        double centerValue;
        ParamChecks.nullNotPermitted(radialAxis, "radialAxis");
        g2.setFont(radialAxis.getTickLabelFont());
        g2.setPaint(plot.getRadiusGridlinePaint());
        g2.setStroke(plot.getRadiusGridlineStroke());
        if (radialAxis.isInverted()) {
            centerValue = radialAxis.getUpperBound();
        } else {
            centerValue = radialAxis.getLowerBound();
        }
        Point center = plot.translateToJava2D(0.0d, centerValue, radialAxis, dataArea);
        for (NumberTick tick : ticks) {
            double angleDegrees;
            if (plot.isCounterClockwise()) {
                angleDegrees = plot.getAngleOffset();
            } else {
                angleDegrees = -plot.getAngleOffset();
            }
            int r = plot.translateToJava2D(angleDegrees, tick.getNumber().doubleValue(), radialAxis, dataArea).x - center.x;
            int d = r * 2;
            double d2 = (double) (center.y - r);
            double d3 = (double) d;
            double d4 = (double) d;
            Ellipse2D ring = new Ellipse2D.Double((double) (center.x - r), r0, r0, r0);
            g2.setPaint(plot.getRadiusGridlinePaint());
            g2.draw(ring);
        }
    }

    public LegendItem getLegendItem(int series) {
        PolarPlot plot = getPlot();
        if (plot == null) {
            return null;
        }
        XYDataset dataset = plot.getDataset(plot.getIndexOf(this));
        if (dataset == null) {
            return null;
        }
        Paint paint;
        String toolTipText = null;
        if (getLegendItemToolTipGenerator() != null) {
            toolTipText = getLegendItemToolTipGenerator().generateLabel(dataset, series);
        }
        String urlText = null;
        if (getLegendItemURLGenerator() != null) {
            urlText = getLegendItemURLGenerator().generateLabel(dataset, series);
        }
        Comparable seriesKey = dataset.getSeriesKey(series);
        String label = seriesKey.toString();
        String description = label;
        Shape shape = lookupSeriesShape(series);
        if (this.useFillPaint) {
            paint = lookupSeriesFillPaint(series);
        } else {
            paint = lookupSeriesPaint(series);
        }
        Stroke stroke = lookupSeriesStroke(series);
        Paint outlinePaint = lookupSeriesOutlinePaint(series);
        Stroke outlineStroke = lookupSeriesOutlineStroke(series);
        boolean shapeOutlined = isSeriesFilled(series) && this.drawOutlineWhenFilled;
        LegendItem result = new LegendItem(label, description, toolTipText, urlText, getShapesVisible(), shape, true, paint, shapeOutlined, outlinePaint, outlineStroke, true, this.legendLine, stroke, paint);
        result.setToolTipText(toolTipText);
        result.setURLText(urlText);
        result.setDataset(dataset);
        result.setSeriesKey(seriesKey);
        result.setSeriesIndex(series);
        return result;
    }

    public XYToolTipGenerator getToolTipGenerator(int series, int item) {
        XYToolTipGenerator generator = (XYToolTipGenerator) this.toolTipGeneratorList.get(series);
        if (generator == null) {
            return this.baseToolTipGenerator;
        }
        return generator;
    }

    public XYToolTipGenerator getSeriesToolTipGenerator(int series) {
        return (XYToolTipGenerator) this.toolTipGeneratorList.get(series);
    }

    public void setSeriesToolTipGenerator(int series, XYToolTipGenerator generator) {
        this.toolTipGeneratorList.set(series, generator);
        fireChangeEvent();
    }

    public XYToolTipGenerator getBaseToolTipGenerator() {
        return this.baseToolTipGenerator;
    }

    public void setBaseToolTipGenerator(XYToolTipGenerator generator) {
        this.baseToolTipGenerator = generator;
        fireChangeEvent();
    }

    public XYURLGenerator getURLGenerator() {
        return this.urlGenerator;
    }

    public void setURLGenerator(XYURLGenerator urlGenerator) {
        this.urlGenerator = urlGenerator;
        fireChangeEvent();
    }

    public XYSeriesLabelGenerator getLegendItemToolTipGenerator() {
        return this.legendItemToolTipGenerator;
    }

    public void setLegendItemToolTipGenerator(XYSeriesLabelGenerator generator) {
        this.legendItemToolTipGenerator = generator;
        fireChangeEvent();
    }

    public XYSeriesLabelGenerator getLegendItemURLGenerator() {
        return this.legendItemURLGenerator;
    }

    public void setLegendItemURLGenerator(XYSeriesLabelGenerator generator) {
        this.legendItemURLGenerator = generator;
        fireChangeEvent();
    }

    public boolean equals(Object obj) {
        if (obj == null || !(obj instanceof DefaultPolarItemRenderer)) {
            return false;
        }
        DefaultPolarItemRenderer that = (DefaultPolarItemRenderer) obj;
        if (this.seriesFilled.equals(that.seriesFilled) && this.drawOutlineWhenFilled == that.drawOutlineWhenFilled && ObjectUtilities.equal(this.fillComposite, that.fillComposite) && this.useFillPaint == that.useFillPaint && ShapeUtilities.equal(this.legendLine, that.legendLine) && this.shapesVisible == that.shapesVisible && this.connectFirstAndLastPoint == that.connectFirstAndLastPoint && this.toolTipGeneratorList.equals(that.toolTipGeneratorList) && ObjectUtilities.equal(this.baseToolTipGenerator, that.baseToolTipGenerator) && ObjectUtilities.equal(this.urlGenerator, that.urlGenerator) && ObjectUtilities.equal(this.legendItemToolTipGenerator, that.legendItemToolTipGenerator) && ObjectUtilities.equal(this.legendItemURLGenerator, that.legendItemURLGenerator)) {
            return super.equals(obj);
        }
        return false;
    }

    public Object clone() throws CloneNotSupportedException {
        DefaultPolarItemRenderer clone = (DefaultPolarItemRenderer) super.clone();
        if (this.legendLine != null) {
            clone.legendLine = ShapeUtilities.clone(this.legendLine);
        }
        clone.seriesFilled = (BooleanList) this.seriesFilled.clone();
        clone.toolTipGeneratorList = (ObjectList) this.toolTipGeneratorList.clone();
        if (clone.baseToolTipGenerator instanceof PublicCloneable) {
            clone.baseToolTipGenerator = (XYToolTipGenerator) ObjectUtilities.clone(this.baseToolTipGenerator);
        }
        if (clone.urlGenerator instanceof PublicCloneable) {
            clone.urlGenerator = (XYURLGenerator) ObjectUtilities.clone(this.urlGenerator);
        }
        if (clone.legendItemToolTipGenerator instanceof PublicCloneable) {
            clone.legendItemToolTipGenerator = (XYSeriesLabelGenerator) ObjectUtilities.clone(this.legendItemToolTipGenerator);
        }
        if (clone.legendItemURLGenerator instanceof PublicCloneable) {
            clone.legendItemURLGenerator = (XYSeriesLabelGenerator) ObjectUtilities.clone(this.legendItemURLGenerator);
        }
        return clone;
    }

    private void readObject(ObjectInputStream stream) throws IOException, ClassNotFoundException {
        stream.defaultReadObject();
        this.legendLine = SerialUtilities.readShape(stream);
        this.fillComposite = SerialUtilities.readComposite(stream);
    }

    private void writeObject(ObjectOutputStream stream) throws IOException {
        stream.defaultWriteObject();
        SerialUtilities.writeShape(this.legendLine, stream);
        SerialUtilities.writeComposite(this.fillComposite, stream);
    }
}
