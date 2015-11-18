package org.jfree.chart.plot;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Paint;
import java.awt.Shape;
import java.awt.Stroke;
import java.awt.geom.Arc2D;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.awt.geom.Rectangle2D.Double;
import java.io.Serializable;
import java.util.ResourceBundle;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.LegendItemCollection;
import org.jfree.chart.annotations.XYPointerAnnotation;
import org.jfree.chart.axis.Axis;
import org.jfree.chart.axis.DateAxis;
import org.jfree.chart.event.RendererChangeEvent;
import org.jfree.chart.event.RendererChangeListener;
import org.jfree.chart.renderer.WaferMapRenderer;
import org.jfree.chart.util.ResourceBundleWrapper;
import org.jfree.data.general.DatasetChangeEvent;
import org.jfree.data.general.WaferMapDataset;
import org.jfree.data.xy.NormalizedMatrixSeries;

public class WaferMapPlot extends Plot implements RendererChangeListener, Cloneable, Serializable {
    public static final Paint DEFAULT_CROSSHAIR_PAINT;
    public static final Stroke DEFAULT_CROSSHAIR_STROKE;
    public static final boolean DEFAULT_CROSSHAIR_VISIBLE = false;
    public static final Paint DEFAULT_GRIDLINE_PAINT;
    public static final Stroke DEFAULT_GRIDLINE_STROKE;
    protected static ResourceBundle localizationResources = null;
    private static final long serialVersionUID = 4668320403707308155L;
    private WaferMapDataset dataset;
    private PlotOrientation orientation;
    private WaferMapRenderer renderer;

    static {
        DEFAULT_GRIDLINE_STROKE = new BasicStroke(JFreeChart.DEFAULT_BACKGROUND_IMAGE_ALPHA, 0, 2, 0.0f, new float[]{Axis.DEFAULT_TICK_MARK_OUTSIDE_LENGTH, Axis.DEFAULT_TICK_MARK_OUTSIDE_LENGTH}, 0.0f);
        DEFAULT_GRIDLINE_PAINT = Color.lightGray;
        DEFAULT_CROSSHAIR_STROKE = DEFAULT_GRIDLINE_STROKE;
        DEFAULT_CROSSHAIR_PAINT = Color.blue;
        localizationResources = ResourceBundleWrapper.getBundle("org.jfree.chart.plot.LocalizationBundle");
    }

    public WaferMapPlot() {
        this(null);
    }

    public WaferMapPlot(WaferMapDataset dataset) {
        this(dataset, null);
    }

    public WaferMapPlot(WaferMapDataset dataset, WaferMapRenderer renderer) {
        this.orientation = PlotOrientation.VERTICAL;
        this.dataset = dataset;
        if (dataset != null) {
            dataset.addChangeListener(this);
        }
        this.renderer = renderer;
        if (renderer != null) {
            renderer.setPlot(this);
            renderer.addChangeListener(this);
        }
    }

    public String getPlotType() {
        return "WMAP_Plot";
    }

    public WaferMapDataset getDataset() {
        return this.dataset;
    }

    public void setDataset(WaferMapDataset dataset) {
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

    public void setRenderer(WaferMapRenderer renderer) {
        if (this.renderer != null) {
            this.renderer.removeChangeListener(this);
        }
        this.renderer = renderer;
        if (renderer != null) {
            renderer.setPlot(this);
        }
        fireChangeEvent();
    }

    public void draw(Graphics2D g2, Rectangle2D area, Point2D anchor, PlotState state, PlotRenderingInfo info) {
        boolean b1;
        if (area.getWidth() <= XYPointerAnnotation.DEFAULT_TIP_RADIUS) {
            b1 = true;
        } else {
            b1 = DEFAULT_CROSSHAIR_VISIBLE;
        }
        boolean b2;
        if (area.getHeight() <= XYPointerAnnotation.DEFAULT_TIP_RADIUS) {
            b2 = true;
        } else {
            b2 = DEFAULT_CROSSHAIR_VISIBLE;
        }
        if (!b1 && !b2) {
            if (info != null) {
                info.setPlotArea(area);
            }
            getInsets().trim(area);
            drawChipGrid(g2, area);
            drawWaferEdge(g2, area);
        }
    }

    protected void drawChipGrid(Graphics2D g2, Rectangle2D plotArea) {
        Shape savedClip = g2.getClip();
        g2.setClip(getWaferEdge(plotArea));
        Rectangle2D chip = new Double();
        int xchips = 35;
        int ychips = 20;
        double space = NormalizedMatrixSeries.DEFAULT_SCALE_FACTOR;
        if (this.dataset != null) {
            xchips = this.dataset.getMaxChipX() + 2;
            ychips = this.dataset.getMaxChipY() + 2;
            space = this.dataset.getChipSpace();
        }
        double startX = plotArea.getX();
        double startY = plotArea.getY();
        double chipWidth = NormalizedMatrixSeries.DEFAULT_SCALE_FACTOR;
        double chipHeight = NormalizedMatrixSeries.DEFAULT_SCALE_FACTOR;
        if (plotArea.getWidth() != plotArea.getHeight()) {
            double major;
            double minor;
            if (plotArea.getWidth() > plotArea.getHeight()) {
                major = plotArea.getWidth();
                minor = plotArea.getHeight();
            } else {
                major = plotArea.getHeight();
                minor = plotArea.getWidth();
            }
            double d;
            if (plotArea.getWidth() == minor) {
                startY += (major - minor) / DateAxis.DEFAULT_AUTO_RANGE_MINIMUM_SIZE_IN_MILLISECONDS;
                d = (double) xchips;
                chipWidth = (plotArea.getWidth() - ((((double) xchips) * space) - NormalizedMatrixSeries.DEFAULT_SCALE_FACTOR)) / r0;
                d = (double) ychips;
                chipHeight = (plotArea.getWidth() - ((((double) ychips) * space) - NormalizedMatrixSeries.DEFAULT_SCALE_FACTOR)) / r0;
            } else {
                startX += (major - minor) / DateAxis.DEFAULT_AUTO_RANGE_MINIMUM_SIZE_IN_MILLISECONDS;
                d = (double) xchips;
                chipWidth = (plotArea.getHeight() - ((((double) xchips) * space) - NormalizedMatrixSeries.DEFAULT_SCALE_FACTOR)) / r0;
                d = (double) ychips;
                chipHeight = (plotArea.getHeight() - ((((double) ychips) * space) - NormalizedMatrixSeries.DEFAULT_SCALE_FACTOR)) / r0;
            }
        }
        for (int x = 1; x <= xchips; x++) {
            double upperLeftX = ((startX - chipWidth) + (((double) x) * chipWidth)) + (((double) (x - 1)) * space);
            for (int y = 1; y <= ychips; y++) {
                chip.setFrame(upperLeftX, ((startY - chipHeight) + (((double) y) * chipHeight)) + (((double) (y - 1)) * space), chipWidth, chipHeight);
                g2.setColor(Color.white);
                if (this.dataset.getChipValue(x - 1, (ychips - y) - 1) != null) {
                    g2.setPaint(this.renderer.getChipColor(this.dataset.getChipValue(x - 1, (ychips - y) - 1)));
                }
                g2.fill(chip);
                g2.setColor(Color.lightGray);
                g2.draw(chip);
            }
        }
        g2.setClip(savedClip);
    }

    protected Ellipse2D getWaferEdge(Rectangle2D plotArea) {
        Ellipse2D edge = new Ellipse2D.Double();
        double diameter = plotArea.getWidth();
        double upperLeftX = plotArea.getX();
        double upperLeftY = plotArea.getY();
        if (plotArea.getWidth() != plotArea.getHeight()) {
            double major;
            double minor;
            if (plotArea.getWidth() > plotArea.getHeight()) {
                major = plotArea.getWidth();
                minor = plotArea.getHeight();
            } else {
                major = plotArea.getHeight();
                minor = plotArea.getWidth();
            }
            diameter = minor;
            if (plotArea.getWidth() == minor) {
                upperLeftY = plotArea.getY() + ((major - minor) / DateAxis.DEFAULT_AUTO_RANGE_MINIMUM_SIZE_IN_MILLISECONDS);
            } else {
                upperLeftX = plotArea.getX() + ((major - minor) / DateAxis.DEFAULT_AUTO_RANGE_MINIMUM_SIZE_IN_MILLISECONDS);
            }
        }
        edge.setFrame(upperLeftX, upperLeftY, diameter, diameter);
        return edge;
    }

    protected void drawWaferEdge(Graphics2D g2, Rectangle2D plotArea) {
        Arc2D notch;
        Ellipse2D waferEdge = getWaferEdge(plotArea);
        g2.setColor(Color.black);
        g2.draw(waferEdge);
        Rectangle2D waferFrame = waferEdge.getFrame();
        double notchDiameter = waferFrame.getWidth() * 0.04d;
        if (this.orientation == PlotOrientation.HORIZONTAL) {
            notch = new Arc2D.Double(new Double((waferFrame.getX() + waferFrame.getWidth()) - (notchDiameter / DateAxis.DEFAULT_AUTO_RANGE_MINIMUM_SIZE_IN_MILLISECONDS), (waferFrame.getY() + (waferFrame.getHeight() / DateAxis.DEFAULT_AUTO_RANGE_MINIMUM_SIZE_IN_MILLISECONDS)) - (notchDiameter / DateAxis.DEFAULT_AUTO_RANGE_MINIMUM_SIZE_IN_MILLISECONDS), notchDiameter, notchDiameter), SpiderWebPlot.DEFAULT_START_ANGLE, 180.0d, 0);
        } else {
            notch = new Arc2D.Double(new Double((waferFrame.getX() + (waferFrame.getWidth() / DateAxis.DEFAULT_AUTO_RANGE_MINIMUM_SIZE_IN_MILLISECONDS)) - (notchDiameter / DateAxis.DEFAULT_AUTO_RANGE_MINIMUM_SIZE_IN_MILLISECONDS), (waferFrame.getY() + waferFrame.getHeight()) - (notchDiameter / DateAxis.DEFAULT_AUTO_RANGE_MINIMUM_SIZE_IN_MILLISECONDS), notchDiameter, notchDiameter), 0.0d, 180.0d, 0);
        }
        g2.setColor(Color.white);
        g2.fill(notch);
        g2.setColor(Color.black);
        g2.draw(notch);
    }

    public LegendItemCollection getLegendItems() {
        return this.renderer.getLegendCollection();
    }

    public void rendererChanged(RendererChangeEvent event) {
        fireChangeEvent();
    }
}
