package org.jfree.chart.plot;

import java.awt.AlphaComposite;
import java.awt.Composite;
import java.awt.Graphics2D;
import java.awt.Paint;
import java.awt.RenderingHints;
import java.awt.Shape;
import java.awt.Stroke;
import java.awt.geom.Ellipse2D;
import java.awt.geom.GeneralPath;
import java.awt.geom.Line2D;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.awt.geom.Rectangle2D.Double;
import java.awt.geom.RectangularShape;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;
import org.jfree.chart.ClipPath;
import org.jfree.chart.annotations.XYAnnotation;
import org.jfree.chart.annotations.XYPointerAnnotation;
import org.jfree.chart.axis.AxisSpace;
import org.jfree.chart.axis.ColorBar;
import org.jfree.chart.axis.DateAxis;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.axis.ValueAxis;
import org.jfree.chart.entity.ChartEntity;
import org.jfree.chart.entity.ContourEntity;
import org.jfree.chart.entity.EntityCollection;
import org.jfree.chart.event.AxisChangeEvent;
import org.jfree.chart.labels.ContourToolTipGenerator;
import org.jfree.chart.labels.StandardContourToolTipGenerator;
import org.jfree.chart.urls.XYURLGenerator;
import org.jfree.chart.util.ResourceBundleWrapper;
import org.jfree.data.Range;
import org.jfree.data.contour.ContourDataset;
import org.jfree.data.general.DatasetChangeEvent;
import org.jfree.data.general.DatasetUtilities;
import org.jfree.ui.RectangleEdge;
import org.jfree.ui.RectangleInsets;
import org.jfree.util.ObjectUtilities;

public class ContourPlot extends Plot implements ContourValuePlot, ValueAxisPlot, PropertyChangeListener, Serializable, Cloneable {
    protected static final RectangleInsets DEFAULT_INSETS;
    protected static ResourceBundle localizationResources = null;
    private static final long serialVersionUID = 7861072556590502247L;
    private List annotations;
    private transient ClipPath clipPath;
    private ColorBar colorBar;
    private RectangleEdge colorBarLocation;
    private double dataAreaRatio;
    private ContourDataset dataset;
    private ValueAxis domainAxis;
    private boolean domainCrosshairLockedOnData;
    private transient Paint domainCrosshairPaint;
    private transient Stroke domainCrosshairStroke;
    private double domainCrosshairValue;
    private boolean domainCrosshairVisible;
    private List domainMarkers;
    private transient Paint missingPaint;
    private double ptSizePct;
    private ValueAxis rangeAxis;
    private boolean rangeCrosshairLockedOnData;
    private transient Paint rangeCrosshairPaint;
    private transient Stroke rangeCrosshairStroke;
    private double rangeCrosshairValue;
    private boolean rangeCrosshairVisible;
    private List rangeMarkers;
    private boolean renderAsPoints;
    private ContourToolTipGenerator toolTipGenerator;
    private XYURLGenerator urlGenerator;

    static {
        DEFAULT_INSETS = new RectangleInsets(DateAxis.DEFAULT_AUTO_RANGE_MINIMUM_SIZE_IN_MILLISECONDS, DateAxis.DEFAULT_AUTO_RANGE_MINIMUM_SIZE_IN_MILLISECONDS, 100.0d, XYPointerAnnotation.DEFAULT_TIP_RADIUS);
        localizationResources = ResourceBundleWrapper.getBundle("org.jfree.chart.plot.LocalizationBundle");
    }

    public ContourPlot() {
        this(null, null, null, null);
    }

    public ContourPlot(ContourDataset dataset, ValueAxis domainAxis, ValueAxis rangeAxis, ColorBar colorBar) {
        this.colorBar = null;
        this.domainCrosshairLockedOnData = true;
        this.rangeCrosshairLockedOnData = true;
        this.dataAreaRatio = 0.0d;
        this.renderAsPoints = false;
        this.ptSizePct = ValueAxis.DEFAULT_UPPER_MARGIN;
        this.clipPath = null;
        this.missingPaint = null;
        this.dataset = dataset;
        if (dataset != null) {
            dataset.addChangeListener(this);
        }
        this.domainAxis = domainAxis;
        if (domainAxis != null) {
            domainAxis.setPlot(this);
            domainAxis.addChangeListener(this);
        }
        this.rangeAxis = rangeAxis;
        if (rangeAxis != null) {
            rangeAxis.setPlot(this);
            rangeAxis.addChangeListener(this);
        }
        this.colorBar = colorBar;
        if (colorBar != null) {
            colorBar.getAxis().setPlot(this);
            colorBar.getAxis().addChangeListener(this);
            colorBar.configure(this);
        }
        this.colorBarLocation = RectangleEdge.LEFT;
        this.toolTipGenerator = new StandardContourToolTipGenerator();
    }

    public RectangleEdge getColorBarLocation() {
        return this.colorBarLocation;
    }

    public void setColorBarLocation(RectangleEdge edge) {
        this.colorBarLocation = edge;
        fireChangeEvent();
    }

    public ContourDataset getDataset() {
        return this.dataset;
    }

    public void setDataset(ContourDataset dataset) {
        ContourDataset existing = this.dataset;
        if (existing != null) {
            existing.removeChangeListener(this);
        }
        this.dataset = dataset;
        if (dataset != null) {
            setDatasetGroup(dataset.getGroup());
            dataset.addChangeListener(this);
        }
        datasetChanged(new DatasetChangeEvent(this, dataset));
    }

    public ValueAxis getDomainAxis() {
        return this.domainAxis;
    }

    public void setDomainAxis(ValueAxis axis) {
        if (isCompatibleDomainAxis(axis)) {
            if (axis != null) {
                axis.setPlot(this);
                axis.addChangeListener(this);
            }
            if (this.domainAxis != null) {
                this.domainAxis.removeChangeListener(this);
            }
            this.domainAxis = axis;
            fireChangeEvent();
        }
    }

    public ValueAxis getRangeAxis() {
        return this.rangeAxis;
    }

    public void setRangeAxis(ValueAxis axis) {
        if (axis != null) {
            axis.setPlot(this);
            axis.addChangeListener(this);
        }
        if (this.rangeAxis != null) {
            this.rangeAxis.removeChangeListener(this);
        }
        this.rangeAxis = axis;
        fireChangeEvent();
    }

    public void setColorBarAxis(ColorBar axis) {
        this.colorBar = axis;
        fireChangeEvent();
    }

    public double getDataAreaRatio() {
        return this.dataAreaRatio;
    }

    public void setDataAreaRatio(double ratio) {
        this.dataAreaRatio = ratio;
    }

    public void addDomainMarker(Marker marker) {
        if (this.domainMarkers == null) {
            this.domainMarkers = new ArrayList();
        }
        this.domainMarkers.add(marker);
        fireChangeEvent();
    }

    public void clearDomainMarkers() {
        if (this.domainMarkers != null) {
            this.domainMarkers.clear();
            fireChangeEvent();
        }
    }

    public void addRangeMarker(Marker marker) {
        if (this.rangeMarkers == null) {
            this.rangeMarkers = new ArrayList();
        }
        this.rangeMarkers.add(marker);
        fireChangeEvent();
    }

    public void clearRangeMarkers() {
        if (this.rangeMarkers != null) {
            this.rangeMarkers.clear();
            fireChangeEvent();
        }
    }

    public void addAnnotation(XYAnnotation annotation) {
        if (this.annotations == null) {
            this.annotations = new ArrayList();
        }
        this.annotations.add(annotation);
        fireChangeEvent();
    }

    public void clearAnnotations() {
        if (this.annotations != null) {
            this.annotations.clear();
            fireChangeEvent();
        }
    }

    public boolean isCompatibleDomainAxis(ValueAxis axis) {
        return true;
    }

    public void draw(Graphics2D g2, Rectangle2D area, Point2D anchor, PlotState parentState, PlotRenderingInfo info) {
        boolean b1 = area.getWidth() <= XYPointerAnnotation.DEFAULT_TIP_RADIUS;
        boolean b2 = area.getHeight() <= XYPointerAnnotation.DEFAULT_TIP_RADIUS;
        if (!b1 && !b2) {
            if (info != null) {
                info.setPlotArea(area);
            }
            getInsets().trim(area);
            AxisSpace space = this.rangeAxis.reserveSpace(g2, this, area, RectangleEdge.LEFT, this.domainAxis.reserveSpace(g2, this, area, RectangleEdge.BOTTOM, new AxisSpace()));
            AxisSpace space2 = this.colorBar.reserveSpace(g2, this, area, space.shrink(area, null), this.colorBarLocation, new AxisSpace());
            Rectangle2D adjustedPlotArea = space2.shrink(area, null);
            Rectangle2D dataArea = space.shrink(adjustedPlotArea, null);
            Rectangle2D colorBarArea = space2.reserved(area, this.colorBarLocation);
            if (getDataAreaRatio() != 0.0d) {
                double ratio = getDataAreaRatio();
                Rectangle2D tmpDataArea = (Rectangle2D) dataArea.clone();
                double h = tmpDataArea.getHeight();
                double w = tmpDataArea.getWidth();
                if (ratio <= 0.0d) {
                    ratio = (ratio * SpiderWebPlot.DEFAULT_MAX_VALUE) * (getRangeAxis().getRange().getLength() / getDomainAxis().getRange().getLength());
                    if (w * ratio <= h) {
                        h = ratio * w;
                    } else {
                        w = h / ratio;
                    }
                } else if (w * ratio <= h) {
                    h = ratio * w;
                } else {
                    w = h / ratio;
                }
                dataArea.setRect((tmpDataArea.getX() + (tmpDataArea.getWidth() / DateAxis.DEFAULT_AUTO_RANGE_MINIMUM_SIZE_IN_MILLISECONDS)) - (w / DateAxis.DEFAULT_AUTO_RANGE_MINIMUM_SIZE_IN_MILLISECONDS), tmpDataArea.getY(), w, h);
            }
            if (info != null) {
                info.setDataArea(dataArea);
            }
            CrosshairState crosshairState = new CrosshairState();
            crosshairState.setCrosshairDistance(Double.POSITIVE_INFINITY);
            drawBackground(g2, dataArea);
            double cursor = dataArea.getMaxY();
            if (this.domainAxis != null) {
                this.domainAxis.draw(g2, cursor, adjustedPlotArea, dataArea, RectangleEdge.BOTTOM, info);
            }
            if (this.rangeAxis != null) {
                this.rangeAxis.draw(g2, dataArea.getMinX(), adjustedPlotArea, dataArea, RectangleEdge.LEFT, info);
            }
            if (this.colorBar != null) {
                this.colorBar.draw(g2, 0.0d, adjustedPlotArea, dataArea, colorBarArea, this.colorBarLocation);
            }
            Shape originalClip = g2.getClip();
            Composite originalComposite = g2.getComposite();
            g2.clip(dataArea);
            g2.setComposite(AlphaComposite.getInstance(3, getForegroundAlpha()));
            render(g2, dataArea, info, crosshairState);
            if (this.domainMarkers != null) {
                for (Marker marker : this.domainMarkers) {
                    drawDomainMarker(g2, this, getDomainAxis(), marker, dataArea);
                }
            }
            if (this.rangeMarkers != null) {
                for (Marker marker2 : this.rangeMarkers) {
                    drawRangeMarker(g2, this, getRangeAxis(), marker2, dataArea);
                }
            }
            g2.setClip(originalClip);
            g2.setComposite(originalComposite);
            drawOutline(g2, dataArea);
        }
    }

    public void render(Graphics2D g2, Rectangle2D dataArea, PlotRenderingInfo info, CrosshairState crosshairState) {
        ContourDataset data = getDataset();
        if (data != null) {
            ColorBar zAxis = getColorBar();
            if (this.clipPath != null) {
                GeneralPath clipper = getClipPath().draw(g2, dataArea, this.domainAxis, this.rangeAxis);
                if (this.clipPath.isClip()) {
                    g2.clip(clipper);
                }
            }
            if (this.renderAsPoints) {
                pointRenderer(g2, dataArea, info, this, this.domainAxis, this.rangeAxis, zAxis, data, crosshairState);
            } else {
                contourRenderer(g2, dataArea, info, this, this.domainAxis, this.rangeAxis, zAxis, data, crosshairState);
            }
            setDomainCrosshairValue(crosshairState.getCrosshairX(), false);
            if (isDomainCrosshairVisible()) {
                drawVerticalLine(g2, dataArea, getDomainCrosshairValue(), getDomainCrosshairStroke(), getDomainCrosshairPaint());
            }
            setRangeCrosshairValue(crosshairState.getCrosshairY(), false);
            if (isRangeCrosshairVisible()) {
                drawHorizontalLine(g2, dataArea, getRangeCrosshairValue(), getRangeCrosshairStroke(), getRangeCrosshairPaint());
            }
        } else if (this.clipPath != null) {
            getClipPath().draw(g2, dataArea, this.domainAxis, this.rangeAxis);
        }
    }

    public void contourRenderer(Graphics2D g2, Rectangle2D dataArea, PlotRenderingInfo info, ContourPlot plot, ValueAxis horizontalAxis, ValueAxis verticalAxis, ColorBar colorBar, ContourDataset data, CrosshairState crosshairState) {
        int i;
        EntityCollection entities = null;
        if (info != null) {
            entities = info.getOwner().getEntityCollection();
        }
        Double rect = new Double();
        Object antiAlias = g2.getRenderingHint(RenderingHints.KEY_ANTIALIASING);
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_OFF);
        Number[] xNumber = data.getXValues();
        Number[] yNumber = data.getYValues();
        Number[] zNumber = data.getZValues();
        double[] x = new double[xNumber.length];
        double[] y = new double[yNumber.length];
        for (i = 0; i < x.length; i++) {
            x[i] = xNumber[i].doubleValue();
            y[i] = yNumber[i].doubleValue();
        }
        int[] xIndex = data.indexX();
        int[] indexX = data.getXIndices();
        boolean vertInverted = ((NumberAxis) verticalAxis).isInverted();
        boolean horizInverted = false;
        if (horizontalAxis instanceof NumberAxis) {
            horizInverted = ((NumberAxis) horizontalAxis).isInverted();
        }
        double transX = 0.0d;
        double transDXp1 = 0.0d;
        double transDX = 0.0d;
        double transDYp1 = 0.0d;
        int iMax = xIndex[xIndex.length - 1];
        int k = 0;
        while (k < x.length) {
            i = xIndex[k];
            double d;
            double transY;
            double transYp1;
            double transDYm1;
            Double entityArea;
            String tip;
            ChartEntity contourEntity;
            if (indexX[i] == k) {
                double transDXm1;
                double transXp1;
                if (i == 0) {
                    d = x[k];
                    transX = horizontalAxis.valueToJava2D(r16, dataArea, RectangleEdge.BOTTOM);
                    double transXm1 = transX;
                    d = x[indexX[i + 1]];
                    transXp1 = horizontalAxis.valueToJava2D(r16, dataArea, RectangleEdge.BOTTOM);
                    transDXm1 = Math.abs(0.5d * (transX - transXm1));
                    transDXp1 = Math.abs(0.5d * (transX - transXp1));
                } else if (i == iMax) {
                    d = x[k];
                    transX = horizontalAxis.valueToJava2D(r16, dataArea, RectangleEdge.BOTTOM);
                    d = x[indexX[i - 1]];
                    transXp1 = transX;
                    transDXm1 = Math.abs(0.5d * (transX - horizontalAxis.valueToJava2D(r16, dataArea, RectangleEdge.BOTTOM)));
                    transDXp1 = Math.abs(0.5d * (transX - transXp1));
                } else {
                    d = x[k];
                    transX = horizontalAxis.valueToJava2D(r16, dataArea, RectangleEdge.BOTTOM);
                    d = x[indexX[i + 1]];
                    transDXm1 = transDXp1;
                    transDXp1 = Math.abs(0.5d * (transX - horizontalAxis.valueToJava2D(r16, dataArea, RectangleEdge.BOTTOM)));
                }
                if (horizInverted) {
                    transX -= transDXp1;
                } else {
                    transX -= transDXm1;
                }
                transDX = transDXm1 + transDXp1;
                d = y[k];
                transY = verticalAxis.valueToJava2D(r16, dataArea, RectangleEdge.LEFT);
                double transYm1 = transY;
                int i2 = k + 1;
                int length = y.length;
                if (i2 != r0) {
                    d = y[k + 1];
                    transYp1 = verticalAxis.valueToJava2D(r16, dataArea, RectangleEdge.LEFT);
                    transDYm1 = Math.abs(0.5d * (transY - transYm1));
                    transDYp1 = Math.abs(0.5d * (transY - transYp1));
                    if (vertInverted) {
                        transY -= transDYp1;
                    } else {
                        transY -= transDYm1;
                    }
                    rect.setRect(transX, transY, transDX, transDYm1 + transDYp1);
                    if (zNumber[k] != null) {
                        g2.setPaint(colorBar.getPaint(zNumber[k].doubleValue()));
                        g2.fill(rect);
                    } else if (this.missingPaint != null) {
                        g2.setPaint(this.missingPaint);
                        g2.fill(rect);
                    }
                    entityArea = rect;
                    if (entities != null) {
                        tip = "";
                        if (getToolTipGenerator() != null) {
                            tip = this.toolTipGenerator.generateToolTip(data, k);
                        }
                        contourEntity = new ContourEntity((Double) entityArea.clone(), tip, null);
                        contourEntity.setIndex(k);
                        entities.add(contourEntity);
                    }
                    if (plot.isDomainCrosshairLockedOnData()) {
                        if (plot.isRangeCrosshairLockedOnData()) {
                            crosshairState.updateCrosshairY(transY);
                        }
                    } else if (plot.isRangeCrosshairLockedOnData()) {
                        crosshairState.updateCrosshairX(transX);
                    } else {
                        crosshairState.updateCrosshairPoint(x[k], y[k], transX, transY, PlotOrientation.VERTICAL);
                    }
                }
            } else {
                if ((i >= indexX.length - 1 || indexX[i + 1] - 1 != k) && k != x.length - 1) {
                    d = y[k];
                    transY = verticalAxis.valueToJava2D(r16, dataArea, RectangleEdge.LEFT);
                    d = y[k + 1];
                    transDYm1 = transDYp1;
                    transDYp1 = Math.abs(0.5d * (transY - verticalAxis.valueToJava2D(r16, dataArea, RectangleEdge.LEFT)));
                } else {
                    d = y[k];
                    transY = verticalAxis.valueToJava2D(r16, dataArea, RectangleEdge.LEFT);
                    d = y[k - 1];
                    transYp1 = transY;
                    transDYm1 = Math.abs(0.5d * (transY - verticalAxis.valueToJava2D(r16, dataArea, RectangleEdge.LEFT)));
                    transDYp1 = Math.abs(0.5d * (transY - transYp1));
                }
                if (vertInverted) {
                    transY -= transDYp1;
                } else {
                    transY -= transDYm1;
                }
                rect.setRect(transX, transY, transDX, transDYm1 + transDYp1);
                if (zNumber[k] != null) {
                    g2.setPaint(colorBar.getPaint(zNumber[k].doubleValue()));
                    g2.fill(rect);
                } else if (this.missingPaint != null) {
                    g2.setPaint(this.missingPaint);
                    g2.fill(rect);
                }
                entityArea = rect;
                if (entities != null) {
                    tip = "";
                    if (getToolTipGenerator() != null) {
                        tip = this.toolTipGenerator.generateToolTip(data, k);
                    }
                    contourEntity = new ContourEntity((Double) entityArea.clone(), tip, null);
                    contourEntity.setIndex(k);
                    entities.add(contourEntity);
                }
                if (plot.isDomainCrosshairLockedOnData()) {
                    if (plot.isRangeCrosshairLockedOnData()) {
                        crosshairState.updateCrosshairY(transY);
                    }
                } else if (plot.isRangeCrosshairLockedOnData()) {
                    crosshairState.updateCrosshairX(transX);
                } else {
                    crosshairState.updateCrosshairPoint(x[k], y[k], transX, transY, PlotOrientation.VERTICAL);
                }
            }
            k++;
        }
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, antiAlias);
    }

    public void pointRenderer(Graphics2D g2, Rectangle2D dataArea, PlotRenderingInfo info, ContourPlot plot, ValueAxis domainAxis, ValueAxis rangeAxis, ColorBar colorBar, ContourDataset data, CrosshairState crosshairState) {
        EntityCollection entities = null;
        if (info != null) {
            entities = info.getOwner().getEntityCollection();
        }
        RectangularShape rect = new Ellipse2D.Double();
        Object antiAlias = g2.getRenderingHint(RenderingHints.KEY_ANTIALIASING);
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_OFF);
        Number[] xNumber = data.getXValues();
        Number[] yNumber = data.getYValues();
        Number[] zNumber = data.getZValues();
        double[] x = new double[xNumber.length];
        double[] y = new double[yNumber.length];
        for (int i = 0; i < x.length; i++) {
            x[i] = xNumber[i].doubleValue();
            y[i] = yNumber[i].doubleValue();
        }
        double size = dataArea.getWidth() * this.ptSizePct;
        for (int k = 0; k < x.length; k++) {
            double d = x[k];
            double transX = domainAxis.valueToJava2D(r16, dataArea, RectangleEdge.BOTTOM) - (0.5d * size);
            d = y[k];
            double transY = rangeAxis.valueToJava2D(r16, dataArea, RectangleEdge.LEFT) - (0.5d * size);
            rect.setFrame(transX, transY, size, size);
            if (zNumber[k] != null) {
                g2.setPaint(colorBar.getPaint(zNumber[k].doubleValue()));
                g2.fill(rect);
            } else if (this.missingPaint != null) {
                g2.setPaint(this.missingPaint);
                g2.fill(rect);
            }
            RectangularShape entityArea = rect;
            if (entities != null) {
                String tip = null;
                if (getToolTipGenerator() != null) {
                    tip = this.toolTipGenerator.generateToolTip(data, k);
                }
                ChartEntity contourEntity = new ContourEntity((RectangularShape) entityArea.clone(), tip, null);
                contourEntity.setIndex(k);
                entities.add(contourEntity);
            }
            if (plot.isDomainCrosshairLockedOnData()) {
                if (plot.isRangeCrosshairLockedOnData()) {
                    crosshairState.updateCrosshairPoint(x[k], y[k], transX, transY, PlotOrientation.VERTICAL);
                } else {
                    crosshairState.updateCrosshairX(transX);
                }
            } else if (plot.isRangeCrosshairLockedOnData()) {
                crosshairState.updateCrosshairY(transY);
            }
        }
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, antiAlias);
    }

    protected void drawVerticalLine(Graphics2D g2, Rectangle2D dataArea, double value, Stroke stroke, Paint paint) {
        double xx = getDomainAxis().valueToJava2D(value, dataArea, RectangleEdge.BOTTOM);
        Line2D line = new Line2D.Double(xx, dataArea.getMinY(), xx, dataArea.getMaxY());
        g2.setStroke(stroke);
        g2.setPaint(paint);
        g2.draw(line);
    }

    protected void drawHorizontalLine(Graphics2D g2, Rectangle2D dataArea, double value, Stroke stroke, Paint paint) {
        double yy = getRangeAxis().valueToJava2D(value, dataArea, RectangleEdge.LEFT);
        Line2D line = new Line2D.Double(dataArea.getMinX(), yy, dataArea.getMaxX(), yy);
        g2.setStroke(stroke);
        g2.setPaint(paint);
        g2.draw(line);
    }

    public void handleClick(int x, int y, PlotRenderingInfo info) {
    }

    public void zoom(double percent) {
        if (percent <= 0.0d) {
            getRangeAxis().setAutoRange(true);
            getDomainAxis().setAutoRange(true);
        }
    }

    public String getPlotType() {
        return localizationResources.getString("Contour_Plot");
    }

    public Range getDataRange(ValueAxis axis) {
        if (this.dataset == null) {
            return null;
        }
        if (axis == getDomainAxis()) {
            return DatasetUtilities.findDomainBounds(this.dataset);
        }
        if (axis == getRangeAxis()) {
            return DatasetUtilities.findRangeBounds(this.dataset);
        }
        return null;
    }

    public Range getContourDataRange() {
        ContourDataset data = getDataset();
        if (data != null) {
            return visibleRange(data, getDomainAxis().getRange(), getRangeAxis().getRange());
        }
        return null;
    }

    public void propertyChange(PropertyChangeEvent event) {
        fireChangeEvent();
    }

    public void datasetChanged(DatasetChangeEvent event) {
        if (this.domainAxis != null) {
            this.domainAxis.configure();
        }
        if (this.rangeAxis != null) {
            this.rangeAxis.configure();
        }
        if (this.colorBar != null) {
            this.colorBar.configure(this);
        }
        super.datasetChanged(event);
    }

    public ColorBar getColorBar() {
        return this.colorBar;
    }

    public boolean isDomainCrosshairVisible() {
        return this.domainCrosshairVisible;
    }

    public void setDomainCrosshairVisible(boolean flag) {
        if (this.domainCrosshairVisible != flag) {
            this.domainCrosshairVisible = flag;
            fireChangeEvent();
        }
    }

    public boolean isDomainCrosshairLockedOnData() {
        return this.domainCrosshairLockedOnData;
    }

    public void setDomainCrosshairLockedOnData(boolean flag) {
        if (this.domainCrosshairLockedOnData != flag) {
            this.domainCrosshairLockedOnData = flag;
            fireChangeEvent();
        }
    }

    public double getDomainCrosshairValue() {
        return this.domainCrosshairValue;
    }

    public void setDomainCrosshairValue(double value) {
        setDomainCrosshairValue(value, true);
    }

    public void setDomainCrosshairValue(double value, boolean notify) {
        this.domainCrosshairValue = value;
        if (isDomainCrosshairVisible() && notify) {
            fireChangeEvent();
        }
    }

    public Stroke getDomainCrosshairStroke() {
        return this.domainCrosshairStroke;
    }

    public void setDomainCrosshairStroke(Stroke stroke) {
        this.domainCrosshairStroke = stroke;
        fireChangeEvent();
    }

    public Paint getDomainCrosshairPaint() {
        return this.domainCrosshairPaint;
    }

    public void setDomainCrosshairPaint(Paint paint) {
        this.domainCrosshairPaint = paint;
        fireChangeEvent();
    }

    public boolean isRangeCrosshairVisible() {
        return this.rangeCrosshairVisible;
    }

    public void setRangeCrosshairVisible(boolean flag) {
        if (this.rangeCrosshairVisible != flag) {
            this.rangeCrosshairVisible = flag;
            fireChangeEvent();
        }
    }

    public boolean isRangeCrosshairLockedOnData() {
        return this.rangeCrosshairLockedOnData;
    }

    public void setRangeCrosshairLockedOnData(boolean flag) {
        if (this.rangeCrosshairLockedOnData != flag) {
            this.rangeCrosshairLockedOnData = flag;
            fireChangeEvent();
        }
    }

    public double getRangeCrosshairValue() {
        return this.rangeCrosshairValue;
    }

    public void setRangeCrosshairValue(double value) {
        setRangeCrosshairValue(value, true);
    }

    public void setRangeCrosshairValue(double value, boolean notify) {
        this.rangeCrosshairValue = value;
        if (isRangeCrosshairVisible() && notify) {
            fireChangeEvent();
        }
    }

    public Stroke getRangeCrosshairStroke() {
        return this.rangeCrosshairStroke;
    }

    public void setRangeCrosshairStroke(Stroke stroke) {
        this.rangeCrosshairStroke = stroke;
        fireChangeEvent();
    }

    public Paint getRangeCrosshairPaint() {
        return this.rangeCrosshairPaint;
    }

    public void setRangeCrosshairPaint(Paint paint) {
        this.rangeCrosshairPaint = paint;
        fireChangeEvent();
    }

    public ContourToolTipGenerator getToolTipGenerator() {
        return this.toolTipGenerator;
    }

    public void setToolTipGenerator(ContourToolTipGenerator generator) {
        this.toolTipGenerator = generator;
    }

    public XYURLGenerator getURLGenerator() {
        return this.urlGenerator;
    }

    public void setURLGenerator(XYURLGenerator urlGenerator) {
        this.urlGenerator = urlGenerator;
    }

    public void drawDomainMarker(Graphics2D g2, ContourPlot plot, ValueAxis domainAxis, Marker marker, Rectangle2D dataArea) {
        if (marker instanceof ValueMarker) {
            double value = ((ValueMarker) marker).getValue();
            if (domainAxis.getRange().contains(value)) {
                double x = domainAxis.valueToJava2D(value, dataArea, RectangleEdge.BOTTOM);
                Line2D line = new Line2D.Double(x, dataArea.getMinY(), x, dataArea.getMaxY());
                Paint paint = marker.getOutlinePaint();
                Stroke stroke = marker.getOutlineStroke();
                if (paint == null) {
                    paint = Plot.DEFAULT_OUTLINE_PAINT;
                }
                g2.setPaint(paint);
                if (stroke == null) {
                    stroke = Plot.DEFAULT_OUTLINE_STROKE;
                }
                g2.setStroke(stroke);
                g2.draw(line);
            }
        }
    }

    public void drawRangeMarker(Graphics2D g2, ContourPlot plot, ValueAxis rangeAxis, Marker marker, Rectangle2D dataArea) {
        if (marker instanceof ValueMarker) {
            double value = ((ValueMarker) marker).getValue();
            if (rangeAxis.getRange().contains(value)) {
                double y = rangeAxis.valueToJava2D(value, dataArea, RectangleEdge.LEFT);
                Line2D line = new Line2D.Double(dataArea.getMinX(), y, dataArea.getMaxX(), y);
                Paint paint = marker.getOutlinePaint();
                Stroke stroke = marker.getOutlineStroke();
                if (paint == null) {
                    paint = Plot.DEFAULT_OUTLINE_PAINT;
                }
                g2.setPaint(paint);
                if (stroke == null) {
                    stroke = Plot.DEFAULT_OUTLINE_STROKE;
                }
                g2.setStroke(stroke);
                g2.draw(line);
            }
        }
    }

    public ClipPath getClipPath() {
        return this.clipPath;
    }

    public void setClipPath(ClipPath clipPath) {
        this.clipPath = clipPath;
    }

    public double getPtSizePct() {
        return this.ptSizePct;
    }

    public boolean isRenderAsPoints() {
        return this.renderAsPoints;
    }

    public void setPtSizePct(double ptSizePct) {
        this.ptSizePct = ptSizePct;
    }

    public void setRenderAsPoints(boolean renderAsPoints) {
        this.renderAsPoints = renderAsPoints;
    }

    public void axisChanged(AxisChangeEvent event) {
        Object source = event.getSource();
        if (source.equals(this.rangeAxis) || source.equals(this.domainAxis)) {
            ColorBar cba = this.colorBar;
            if (this.colorBar.getAxis().isAutoRange()) {
                cba.getAxis().configure();
            }
        }
        super.axisChanged(event);
    }

    public Range visibleRange(ContourDataset data, Range x, Range y) {
        return data.getZValueRange(x, y);
    }

    public Paint getMissingPaint() {
        return this.missingPaint;
    }

    public void setMissingPaint(Paint paint) {
        this.missingPaint = paint;
    }

    public void zoomDomainAxes(double x, double y, double factor) {
    }

    public void zoomDomainAxes(double x, double y, double lowerPercent, double upperPercent) {
    }

    public void zoomRangeAxes(double x, double y, double factor) {
    }

    public void zoomRangeAxes(double x, double y, double lowerPercent, double upperPercent) {
    }

    public boolean isDomainZoomable() {
        return false;
    }

    public boolean isRangeZoomable() {
        return false;
    }

    public Object clone() throws CloneNotSupportedException {
        ContourPlot clone = (ContourPlot) super.clone();
        if (this.domainAxis != null) {
            clone.domainAxis = (ValueAxis) this.domainAxis.clone();
            clone.domainAxis.setPlot(clone);
            clone.domainAxis.addChangeListener(clone);
        }
        if (this.rangeAxis != null) {
            clone.rangeAxis = (ValueAxis) this.rangeAxis.clone();
            clone.rangeAxis.setPlot(clone);
            clone.rangeAxis.addChangeListener(clone);
        }
        if (clone.dataset != null) {
            clone.dataset.addChangeListener(clone);
        }
        if (this.colorBar != null) {
            clone.colorBar = (ColorBar) this.colorBar.clone();
        }
        clone.domainMarkers = (List) ObjectUtilities.deepClone(this.domainMarkers);
        clone.rangeMarkers = (List) ObjectUtilities.deepClone(this.rangeMarkers);
        clone.annotations = (List) ObjectUtilities.deepClone(this.annotations);
        if (this.clipPath != null) {
            clone.clipPath = (ClipPath) this.clipPath.clone();
        }
        return clone;
    }
}
