package org.jfree.chart;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Font;
import java.awt.Paint;
import java.awt.Stroke;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import org.jfree.chart.annotations.XYAnnotation;
import org.jfree.chart.annotations.XYTextAnnotation;
import org.jfree.chart.axis.Axis;
import org.jfree.chart.axis.CategoryAxis;
import org.jfree.chart.axis.PeriodAxis;
import org.jfree.chart.axis.PeriodAxisLabelInfo;
import org.jfree.chart.axis.SubCategoryAxis;
import org.jfree.chart.axis.SymbolAxis;
import org.jfree.chart.axis.ValueAxis;
import org.jfree.chart.block.Block;
import org.jfree.chart.block.BlockContainer;
import org.jfree.chart.block.LabelBlock;
import org.jfree.chart.plot.CategoryPlot;
import org.jfree.chart.plot.CombinedDomainCategoryPlot;
import org.jfree.chart.plot.CombinedDomainXYPlot;
import org.jfree.chart.plot.CombinedRangeCategoryPlot;
import org.jfree.chart.plot.CombinedRangeXYPlot;
import org.jfree.chart.plot.DefaultDrawingSupplier;
import org.jfree.chart.plot.DrawingSupplier;
import org.jfree.chart.plot.FastScatterPlot;
import org.jfree.chart.plot.MeterPlot;
import org.jfree.chart.plot.MultiplePiePlot;
import org.jfree.chart.plot.PieLabelLinkStyle;
import org.jfree.chart.plot.PiePlot;
import org.jfree.chart.plot.Plot;
import org.jfree.chart.plot.PolarPlot;
import org.jfree.chart.plot.SpiderWebPlot;
import org.jfree.chart.plot.ThermometerPlot;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.renderer.AbstractRenderer;
import org.jfree.chart.renderer.category.BarPainter;
import org.jfree.chart.renderer.category.BarRenderer;
import org.jfree.chart.renderer.category.BarRenderer3D;
import org.jfree.chart.renderer.category.CategoryItemRenderer;
import org.jfree.chart.renderer.category.GradientBarPainter;
import org.jfree.chart.renderer.category.LineRenderer3D;
import org.jfree.chart.renderer.category.MinMaxCategoryRenderer;
import org.jfree.chart.renderer.category.StatisticalBarRenderer;
import org.jfree.chart.renderer.xy.GradientXYBarPainter;
import org.jfree.chart.renderer.xy.XYBarPainter;
import org.jfree.chart.renderer.xy.XYBarRenderer;
import org.jfree.chart.renderer.xy.XYItemRenderer;
import org.jfree.chart.title.CompositeTitle;
import org.jfree.chart.title.LegendTitle;
import org.jfree.chart.title.PaintScaleLegend;
import org.jfree.chart.title.TextTitle;
import org.jfree.chart.title.Title;
import org.jfree.chart.util.DefaultShadowGenerator;
import org.jfree.chart.util.ParamChecks;
import org.jfree.chart.util.ShadowGenerator;
import org.jfree.io.SerialUtilities;
import org.jfree.ui.RectangleInsets;
import org.jfree.util.PaintUtilities;
import org.jfree.util.PublicCloneable;

public class StandardChartTheme implements ChartTheme, Cloneable, PublicCloneable, Serializable {
    private transient Paint axisLabelPaint;
    private RectangleInsets axisOffset;
    private BarPainter barPainter;
    private transient Paint baselinePaint;
    private transient Paint chartBackgroundPaint;
    private transient Paint crosshairPaint;
    private transient Paint domainGridlinePaint;
    private DrawingSupplier drawingSupplier;
    private transient Paint errorIndicatorPaint;
    private Font extraLargeFont;
    private transient Paint gridBandAlternatePaint;
    private transient Paint gridBandPaint;
    private transient Paint itemLabelPaint;
    private transient Paint labelLinkPaint;
    private PieLabelLinkStyle labelLinkStyle;
    private Font largeFont;
    private transient Paint legendBackgroundPaint;
    private transient Paint legendItemPaint;
    private String name;
    private transient Paint plotBackgroundPaint;
    private transient Paint plotOutlinePaint;
    private transient Paint rangeGridlinePaint;
    private Font regularFont;
    private ShadowGenerator shadowGenerator;
    private transient Paint shadowPaint;
    private boolean shadowVisible;
    private Font smallFont;
    private transient Paint subtitlePaint;
    private transient Paint thermometerPaint;
    private transient Paint tickLabelPaint;
    private transient Paint titlePaint;
    private transient Paint wallPaint;
    private XYBarPainter xyBarPainter;

    static class 1 extends StandardChartTheme {
        1(String name) {
            super(name);
        }

        public void apply(JFreeChart chart) {
        }
    }

    public static ChartTheme createJFreeTheme() {
        return new StandardChartTheme("JFree");
    }

    public static ChartTheme createDarknessTheme() {
        StandardChartTheme theme = new StandardChartTheme("Darkness");
        theme.titlePaint = Color.white;
        theme.subtitlePaint = Color.white;
        theme.legendBackgroundPaint = Color.black;
        theme.legendItemPaint = Color.white;
        theme.chartBackgroundPaint = Color.black;
        theme.plotBackgroundPaint = Color.black;
        theme.plotOutlinePaint = Color.yellow;
        theme.baselinePaint = Color.white;
        theme.crosshairPaint = Color.red;
        theme.labelLinkPaint = Color.lightGray;
        theme.tickLabelPaint = Color.white;
        theme.axisLabelPaint = Color.white;
        theme.shadowPaint = Color.darkGray;
        theme.itemLabelPaint = Color.white;
        theme.drawingSupplier = new DefaultDrawingSupplier(new Paint[]{Color.decode("0xFFFF00"), Color.decode("0x0036CC"), Color.decode("0xFF0000"), Color.decode("0xFFFF7F"), Color.decode("0x6681CC"), Color.decode("0xFF7F7F"), Color.decode("0xFFFFBF"), Color.decode("0x99A6CC"), Color.decode("0xFFBFBF"), Color.decode("0xA9A938"), Color.decode("0x2D4587")}, new Paint[]{Color.decode("0xFFFF00"), Color.decode("0x0036CC")}, new Stroke[]{new BasicStroke(Axis.DEFAULT_TICK_MARK_OUTSIDE_LENGTH)}, new Stroke[]{new BasicStroke(JFreeChart.DEFAULT_BACKGROUND_IMAGE_ALPHA)}, DefaultDrawingSupplier.DEFAULT_SHAPE_SEQUENCE);
        theme.wallPaint = Color.darkGray;
        theme.errorIndicatorPaint = Color.lightGray;
        theme.gridBandPaint = new Color(255, 255, 255, 20);
        theme.gridBandAlternatePaint = new Color(255, 255, 255, 40);
        theme.shadowGenerator = null;
        return theme;
    }

    public static ChartTheme createLegacyTheme() {
        return new 1("Legacy");
    }

    public StandardChartTheme(String name) {
        this(name, false);
    }

    public StandardChartTheme(String name, boolean shadow) {
        this.gridBandPaint = SymbolAxis.DEFAULT_GRID_BAND_PAINT;
        this.gridBandAlternatePaint = SymbolAxis.DEFAULT_GRID_BAND_ALTERNATE_PAINT;
        ParamChecks.nullNotPermitted(name, "name");
        this.name = name;
        this.extraLargeFont = new Font("Tahoma", 1, 20);
        this.largeFont = new Font("Tahoma", 1, 14);
        this.regularFont = new Font("Tahoma", 0, 12);
        this.smallFont = new Font("Tahoma", 0, 10);
        this.titlePaint = Color.black;
        this.subtitlePaint = Color.black;
        this.legendBackgroundPaint = Color.white;
        this.legendItemPaint = Color.darkGray;
        this.chartBackgroundPaint = Color.white;
        this.drawingSupplier = new DefaultDrawingSupplier();
        this.plotBackgroundPaint = Color.lightGray;
        this.plotOutlinePaint = Color.black;
        this.labelLinkPaint = Color.black;
        this.labelLinkStyle = PieLabelLinkStyle.CUBIC_CURVE;
        this.axisOffset = new RectangleInsets(4.0d, 4.0d, 4.0d, 4.0d);
        this.domainGridlinePaint = Color.white;
        this.rangeGridlinePaint = Color.white;
        this.baselinePaint = Color.black;
        this.crosshairPaint = Color.blue;
        this.axisLabelPaint = Color.darkGray;
        this.tickLabelPaint = Color.darkGray;
        this.barPainter = new GradientBarPainter();
        this.xyBarPainter = new GradientXYBarPainter();
        this.shadowVisible = false;
        this.shadowPaint = Color.gray;
        this.itemLabelPaint = Color.black;
        this.thermometerPaint = Color.white;
        this.wallPaint = BarRenderer3D.DEFAULT_WALL_PAINT;
        this.errorIndicatorPaint = Color.black;
        this.shadowGenerator = shadow ? new DefaultShadowGenerator() : null;
    }

    public Font getExtraLargeFont() {
        return this.extraLargeFont;
    }

    public void setExtraLargeFont(Font font) {
        ParamChecks.nullNotPermitted(font, "font");
        this.extraLargeFont = font;
    }

    public Font getLargeFont() {
        return this.largeFont;
    }

    public void setLargeFont(Font font) {
        ParamChecks.nullNotPermitted(font, "font");
        this.largeFont = font;
    }

    public Font getRegularFont() {
        return this.regularFont;
    }

    public void setRegularFont(Font font) {
        ParamChecks.nullNotPermitted(font, "font");
        this.regularFont = font;
    }

    public Font getSmallFont() {
        return this.smallFont;
    }

    public void setSmallFont(Font font) {
        ParamChecks.nullNotPermitted(font, "font");
        this.smallFont = font;
    }

    public Paint getTitlePaint() {
        return this.titlePaint;
    }

    public void setTitlePaint(Paint paint) {
        ParamChecks.nullNotPermitted(paint, "paint");
        this.titlePaint = paint;
    }

    public Paint getSubtitlePaint() {
        return this.subtitlePaint;
    }

    public void setSubtitlePaint(Paint paint) {
        ParamChecks.nullNotPermitted(paint, "paint");
        this.subtitlePaint = paint;
    }

    public Paint getChartBackgroundPaint() {
        return this.chartBackgroundPaint;
    }

    public void setChartBackgroundPaint(Paint paint) {
        ParamChecks.nullNotPermitted(paint, "paint");
        this.chartBackgroundPaint = paint;
    }

    public Paint getLegendBackgroundPaint() {
        return this.legendBackgroundPaint;
    }

    public void setLegendBackgroundPaint(Paint paint) {
        ParamChecks.nullNotPermitted(paint, "paint");
        this.legendBackgroundPaint = paint;
    }

    public Paint getLegendItemPaint() {
        return this.legendItemPaint;
    }

    public void setLegendItemPaint(Paint paint) {
        ParamChecks.nullNotPermitted(paint, "paint");
        this.legendItemPaint = paint;
    }

    public Paint getPlotBackgroundPaint() {
        return this.plotBackgroundPaint;
    }

    public void setPlotBackgroundPaint(Paint paint) {
        ParamChecks.nullNotPermitted(paint, "paint");
        this.plotBackgroundPaint = paint;
    }

    public Paint getPlotOutlinePaint() {
        return this.plotOutlinePaint;
    }

    public void setPlotOutlinePaint(Paint paint) {
        ParamChecks.nullNotPermitted(paint, "paint");
        this.plotOutlinePaint = paint;
    }

    public PieLabelLinkStyle getLabelLinkStyle() {
        return this.labelLinkStyle;
    }

    public void setLabelLinkStyle(PieLabelLinkStyle style) {
        ParamChecks.nullNotPermitted(style, "style");
        this.labelLinkStyle = style;
    }

    public Paint getLabelLinkPaint() {
        return this.labelLinkPaint;
    }

    public void setLabelLinkPaint(Paint paint) {
        ParamChecks.nullNotPermitted(paint, "paint");
        this.labelLinkPaint = paint;
    }

    public Paint getDomainGridlinePaint() {
        return this.domainGridlinePaint;
    }

    public void setDomainGridlinePaint(Paint paint) {
        ParamChecks.nullNotPermitted(paint, "paint");
        this.domainGridlinePaint = paint;
    }

    public Paint getRangeGridlinePaint() {
        return this.rangeGridlinePaint;
    }

    public void setRangeGridlinePaint(Paint paint) {
        ParamChecks.nullNotPermitted(paint, "paint");
        this.rangeGridlinePaint = paint;
    }

    public Paint getBaselinePaint() {
        return this.baselinePaint;
    }

    public void setBaselinePaint(Paint paint) {
        ParamChecks.nullNotPermitted(paint, "paint");
        this.baselinePaint = paint;
    }

    public Paint getCrosshairPaint() {
        return this.crosshairPaint;
    }

    public void setCrosshairPaint(Paint paint) {
        ParamChecks.nullNotPermitted(paint, "paint");
        this.crosshairPaint = paint;
    }

    public RectangleInsets getAxisOffset() {
        return this.axisOffset;
    }

    public void setAxisOffset(RectangleInsets offset) {
        ParamChecks.nullNotPermitted(offset, "offset");
        this.axisOffset = offset;
    }

    public Paint getAxisLabelPaint() {
        return this.axisLabelPaint;
    }

    public void setAxisLabelPaint(Paint paint) {
        ParamChecks.nullNotPermitted(paint, "paint");
        this.axisLabelPaint = paint;
    }

    public Paint getTickLabelPaint() {
        return this.tickLabelPaint;
    }

    public void setTickLabelPaint(Paint paint) {
        ParamChecks.nullNotPermitted(paint, "paint");
        this.tickLabelPaint = paint;
    }

    public Paint getItemLabelPaint() {
        return this.itemLabelPaint;
    }

    public void setItemLabelPaint(Paint paint) {
        ParamChecks.nullNotPermitted(paint, "paint");
        this.itemLabelPaint = paint;
    }

    public boolean isShadowVisible() {
        return this.shadowVisible;
    }

    public void setShadowVisible(boolean visible) {
        this.shadowVisible = visible;
    }

    public Paint getShadowPaint() {
        return this.shadowPaint;
    }

    public void setShadowPaint(Paint paint) {
        ParamChecks.nullNotPermitted(paint, "paint");
        this.shadowPaint = paint;
    }

    public BarPainter getBarPainter() {
        return this.barPainter;
    }

    public void setBarPainter(BarPainter painter) {
        ParamChecks.nullNotPermitted(painter, "painter");
        this.barPainter = painter;
    }

    public XYBarPainter getXYBarPainter() {
        return this.xyBarPainter;
    }

    public void setXYBarPainter(XYBarPainter painter) {
        ParamChecks.nullNotPermitted(painter, "painter");
        this.xyBarPainter = painter;
    }

    public Paint getThermometerPaint() {
        return this.thermometerPaint;
    }

    public void setThermometerPaint(Paint paint) {
        ParamChecks.nullNotPermitted(paint, "paint");
        this.thermometerPaint = paint;
    }

    public Paint getWallPaint() {
        return this.wallPaint;
    }

    public void setWallPaint(Paint paint) {
        ParamChecks.nullNotPermitted(paint, "paint");
        this.wallPaint = paint;
    }

    public Paint getErrorIndicatorPaint() {
        return this.errorIndicatorPaint;
    }

    public void setErrorIndicatorPaint(Paint paint) {
        ParamChecks.nullNotPermitted(paint, "paint");
        this.errorIndicatorPaint = paint;
    }

    public Paint getGridBandPaint() {
        return this.gridBandPaint;
    }

    public void setGridBandPaint(Paint paint) {
        ParamChecks.nullNotPermitted(paint, "paint");
        this.gridBandPaint = paint;
    }

    public Paint getGridBandAlternatePaint() {
        return this.gridBandAlternatePaint;
    }

    public void setGridBandAlternatePaint(Paint paint) {
        ParamChecks.nullNotPermitted(paint, "paint");
        this.gridBandAlternatePaint = paint;
    }

    public String getName() {
        return this.name;
    }

    public DrawingSupplier getDrawingSupplier() {
        if (!(this.drawingSupplier instanceof PublicCloneable)) {
            return null;
        }
        try {
            return (DrawingSupplier) this.drawingSupplier.clone();
        } catch (CloneNotSupportedException e) {
            throw new RuntimeException(e);
        }
    }

    public void setDrawingSupplier(DrawingSupplier supplier) {
        ParamChecks.nullNotPermitted(supplier, "supplier");
        this.drawingSupplier = supplier;
    }

    public void apply(JFreeChart chart) {
        ParamChecks.nullNotPermitted(chart, "chart");
        TextTitle title = chart.getTitle();
        if (title != null) {
            title.setFont(this.extraLargeFont);
            title.setPaint(this.titlePaint);
        }
        int subtitleCount = chart.getSubtitleCount();
        for (int i = 0; i < subtitleCount; i++) {
            applyToTitle(chart.getSubtitle(i));
        }
        chart.setBackgroundPaint(this.chartBackgroundPaint);
        Plot plot = chart.getPlot();
        if (plot != null) {
            applyToPlot(plot);
        }
    }

    protected void applyToTitle(Title title) {
        if (title instanceof TextTitle) {
            TextTitle tt = (TextTitle) title;
            tt.setFont(this.largeFont);
            tt.setPaint(this.subtitlePaint);
        } else if (title instanceof LegendTitle) {
            LegendTitle lt = (LegendTitle) title;
            if (lt.getBackgroundPaint() != null) {
                lt.setBackgroundPaint(this.legendBackgroundPaint);
            }
            lt.setItemFont(this.regularFont);
            lt.setItemPaint(this.legendItemPaint);
            if (lt.getWrapper() != null) {
                applyToBlockContainer(lt.getWrapper());
            }
        } else if (title instanceof PaintScaleLegend) {
            PaintScaleLegend psl = (PaintScaleLegend) title;
            psl.setBackgroundPaint(this.legendBackgroundPaint);
            ValueAxis axis = psl.getAxis();
            if (axis != null) {
                applyToValueAxis(axis);
            }
        } else if (title instanceof CompositeTitle) {
            for (Block b : ((CompositeTitle) title).getContainer().getBlocks()) {
                if (b instanceof Title) {
                    applyToTitle((Title) b);
                }
            }
        }
    }

    protected void applyToBlockContainer(BlockContainer bc) {
        for (Block b : bc.getBlocks()) {
            applyToBlock(b);
        }
    }

    protected void applyToBlock(Block b) {
        if (b instanceof Title) {
            applyToTitle((Title) b);
        } else if (b instanceof LabelBlock) {
            LabelBlock lb = (LabelBlock) b;
            lb.setFont(this.regularFont);
            lb.setPaint(this.legendItemPaint);
        }
    }

    protected void applyToPlot(Plot plot) {
        ParamChecks.nullNotPermitted(plot, "plot");
        if (plot.getDrawingSupplier() != null) {
            plot.setDrawingSupplier(getDrawingSupplier());
        }
        if (plot.getBackgroundPaint() != null) {
            plot.setBackgroundPaint(this.plotBackgroundPaint);
        }
        plot.setOutlinePaint(this.plotOutlinePaint);
        if (plot instanceof PiePlot) {
            applyToPiePlot((PiePlot) plot);
        } else if (plot instanceof MultiplePiePlot) {
            applyToMultiplePiePlot((MultiplePiePlot) plot);
        } else if (plot instanceof CategoryPlot) {
            applyToCategoryPlot((CategoryPlot) plot);
        } else if (plot instanceof XYPlot) {
            applyToXYPlot((XYPlot) plot);
        } else if (plot instanceof FastScatterPlot) {
            applyToFastScatterPlot((FastScatterPlot) plot);
        } else if (plot instanceof MeterPlot) {
            applyToMeterPlot((MeterPlot) plot);
        } else if (plot instanceof ThermometerPlot) {
            applyToThermometerPlot((ThermometerPlot) plot);
        } else if (plot instanceof SpiderWebPlot) {
            applyToSpiderWebPlot((SpiderWebPlot) plot);
        } else if (plot instanceof PolarPlot) {
            applyToPolarPlot((PolarPlot) plot);
        }
    }

    protected void applyToPiePlot(PiePlot plot) {
        plot.setLabelLinkPaint(this.labelLinkPaint);
        plot.setLabelLinkStyle(this.labelLinkStyle);
        plot.setLabelFont(this.regularFont);
        plot.setShadowGenerator(this.shadowGenerator);
        if (plot.getAutoPopulateSectionPaint()) {
            plot.clearSectionPaints(false);
        }
        if (plot.getAutoPopulateSectionOutlinePaint()) {
            plot.clearSectionOutlinePaints(false);
        }
        if (plot.getAutoPopulateSectionOutlineStroke()) {
            plot.clearSectionOutlineStrokes(false);
        }
    }

    protected void applyToMultiplePiePlot(MultiplePiePlot plot) {
        apply(plot.getPieChart());
    }

    protected void applyToCategoryPlot(CategoryPlot plot) {
        int i;
        plot.setAxisOffset(this.axisOffset);
        plot.setDomainGridlinePaint(this.domainGridlinePaint);
        plot.setRangeGridlinePaint(this.rangeGridlinePaint);
        plot.setRangeZeroBaselinePaint(this.baselinePaint);
        plot.setShadowGenerator(this.shadowGenerator);
        int domainAxisCount = plot.getDomainAxisCount();
        for (i = 0; i < domainAxisCount; i++) {
            CategoryAxis axis = plot.getDomainAxis(i);
            if (axis != null) {
                applyToCategoryAxis(axis);
            }
        }
        int rangeAxisCount = plot.getRangeAxisCount();
        for (i = 0; i < rangeAxisCount; i++) {
            ValueAxis axis2 = plot.getRangeAxis(i);
            if (axis2 != null) {
                applyToValueAxis(axis2);
            }
        }
        int rendererCount = plot.getRendererCount();
        for (i = 0; i < rendererCount; i++) {
            CategoryItemRenderer r = plot.getRenderer(i);
            if (r != null) {
                applyToCategoryItemRenderer(r);
            }
        }
        if (plot instanceof CombinedDomainCategoryPlot) {
            for (CategoryPlot subplot : ((CombinedDomainCategoryPlot) plot).getSubplots()) {
                if (subplot != null) {
                    applyToPlot(subplot);
                }
            }
        }
        if (plot instanceof CombinedRangeCategoryPlot) {
            for (CategoryPlot subplot2 : ((CombinedRangeCategoryPlot) plot).getSubplots()) {
                if (subplot2 != null) {
                    applyToPlot(subplot2);
                }
            }
        }
    }

    protected void applyToXYPlot(XYPlot plot) {
        int i;
        plot.setAxisOffset(this.axisOffset);
        plot.setDomainZeroBaselinePaint(this.baselinePaint);
        plot.setRangeZeroBaselinePaint(this.baselinePaint);
        plot.setDomainGridlinePaint(this.domainGridlinePaint);
        plot.setRangeGridlinePaint(this.rangeGridlinePaint);
        plot.setDomainCrosshairPaint(this.crosshairPaint);
        plot.setRangeCrosshairPaint(this.crosshairPaint);
        plot.setShadowGenerator(this.shadowGenerator);
        int domainAxisCount = plot.getDomainAxisCount();
        for (i = 0; i < domainAxisCount; i++) {
            ValueAxis axis = plot.getDomainAxis(i);
            if (axis != null) {
                applyToValueAxis(axis);
            }
        }
        int rangeAxisCount = plot.getRangeAxisCount();
        for (i = 0; i < rangeAxisCount; i++) {
            axis = plot.getRangeAxis(i);
            if (axis != null) {
                applyToValueAxis(axis);
            }
        }
        int rendererCount = plot.getRendererCount();
        for (i = 0; i < rendererCount; i++) {
            XYItemRenderer r = plot.getRenderer(i);
            if (r != null) {
                applyToXYItemRenderer(r);
            }
        }
        for (XYAnnotation a : plot.getAnnotations()) {
            applyToXYAnnotation(a);
        }
        if (plot instanceof CombinedDomainXYPlot) {
            for (XYPlot subplot : ((CombinedDomainXYPlot) plot).getSubplots()) {
                if (subplot != null) {
                    applyToPlot(subplot);
                }
            }
        }
        if (plot instanceof CombinedRangeXYPlot) {
            for (XYPlot subplot2 : ((CombinedRangeXYPlot) plot).getSubplots()) {
                if (subplot2 != null) {
                    applyToPlot(subplot2);
                }
            }
        }
    }

    protected void applyToFastScatterPlot(FastScatterPlot plot) {
        plot.setDomainGridlinePaint(this.domainGridlinePaint);
        plot.setRangeGridlinePaint(this.rangeGridlinePaint);
        ValueAxis xAxis = plot.getDomainAxis();
        if (xAxis != null) {
            applyToValueAxis(xAxis);
        }
        ValueAxis yAxis = plot.getRangeAxis();
        if (yAxis != null) {
            applyToValueAxis(yAxis);
        }
    }

    protected void applyToPolarPlot(PolarPlot plot) {
        plot.setAngleLabelFont(this.regularFont);
        plot.setAngleLabelPaint(this.tickLabelPaint);
        plot.setAngleGridlinePaint(this.domainGridlinePaint);
        plot.setRadiusGridlinePaint(this.rangeGridlinePaint);
        ValueAxis axis = plot.getAxis();
        if (axis != null) {
            applyToValueAxis(axis);
        }
    }

    protected void applyToSpiderWebPlot(SpiderWebPlot plot) {
        plot.setLabelFont(this.regularFont);
        plot.setLabelPaint(this.axisLabelPaint);
        plot.setAxisLinePaint(this.axisLabelPaint);
    }

    protected void applyToMeterPlot(MeterPlot plot) {
        plot.setDialBackgroundPaint(this.plotBackgroundPaint);
        plot.setValueFont(this.largeFont);
        plot.setValuePaint(this.axisLabelPaint);
        plot.setDialOutlinePaint(this.plotOutlinePaint);
        plot.setNeedlePaint(this.thermometerPaint);
        plot.setTickLabelFont(this.regularFont);
        plot.setTickLabelPaint(this.tickLabelPaint);
    }

    protected void applyToThermometerPlot(ThermometerPlot plot) {
        plot.setValueFont(this.largeFont);
        plot.setThermometerPaint(this.thermometerPaint);
        ValueAxis axis = plot.getRangeAxis();
        if (axis != null) {
            applyToValueAxis(axis);
        }
    }

    protected void applyToCategoryAxis(CategoryAxis axis) {
        axis.setLabelFont(this.largeFont);
        axis.setLabelPaint(this.axisLabelPaint);
        axis.setTickLabelFont(this.regularFont);
        axis.setTickLabelPaint(this.tickLabelPaint);
        if (axis instanceof SubCategoryAxis) {
            SubCategoryAxis sca = (SubCategoryAxis) axis;
            sca.setSubLabelFont(this.regularFont);
            sca.setSubLabelPaint(this.tickLabelPaint);
        }
    }

    protected void applyToValueAxis(ValueAxis axis) {
        axis.setLabelFont(this.largeFont);
        axis.setLabelPaint(this.axisLabelPaint);
        axis.setTickLabelFont(this.regularFont);
        axis.setTickLabelPaint(this.tickLabelPaint);
        if (axis instanceof SymbolAxis) {
            applyToSymbolAxis((SymbolAxis) axis);
        }
        if (axis instanceof PeriodAxis) {
            applyToPeriodAxis((PeriodAxis) axis);
        }
    }

    protected void applyToSymbolAxis(SymbolAxis axis) {
        axis.setGridBandPaint(this.gridBandPaint);
        axis.setGridBandAlternatePaint(this.gridBandAlternatePaint);
    }

    protected void applyToPeriodAxis(PeriodAxis axis) {
        PeriodAxisLabelInfo[] info = axis.getLabelInfo();
        for (int i = 0; i < info.length; i++) {
            PeriodAxisLabelInfo e = info[i];
            info[i] = new PeriodAxisLabelInfo(e.getPeriodClass(), e.getDateFormat(), e.getPadding(), this.regularFont, this.tickLabelPaint, e.getDrawDividers(), e.getDividerStroke(), e.getDividerPaint());
        }
        axis.setLabelInfo(info);
    }

    protected void applyToAbstractRenderer(AbstractRenderer renderer) {
        if (renderer.getAutoPopulateSeriesPaint()) {
            renderer.clearSeriesPaints(false);
        }
        if (renderer.getAutoPopulateSeriesStroke()) {
            renderer.clearSeriesStrokes(false);
        }
    }

    protected void applyToCategoryItemRenderer(CategoryItemRenderer renderer) {
        ParamChecks.nullNotPermitted(renderer, "renderer");
        if (renderer instanceof AbstractRenderer) {
            applyToAbstractRenderer((AbstractRenderer) renderer);
        }
        renderer.setBaseItemLabelFont(this.regularFont);
        renderer.setBaseItemLabelPaint(this.itemLabelPaint);
        if (renderer instanceof BarRenderer) {
            BarRenderer br = (BarRenderer) renderer;
            br.setBarPainter(this.barPainter);
            br.setShadowVisible(this.shadowVisible);
            br.setShadowPaint(this.shadowPaint);
        }
        if (renderer instanceof BarRenderer3D) {
            ((BarRenderer3D) renderer).setWallPaint(this.wallPaint);
        }
        if (renderer instanceof LineRenderer3D) {
            ((LineRenderer3D) renderer).setWallPaint(this.wallPaint);
        }
        if (renderer instanceof StatisticalBarRenderer) {
            ((StatisticalBarRenderer) renderer).setErrorIndicatorPaint(this.errorIndicatorPaint);
        }
        if (renderer instanceof MinMaxCategoryRenderer) {
            ((MinMaxCategoryRenderer) renderer).setGroupPaint(this.errorIndicatorPaint);
        }
    }

    protected void applyToXYItemRenderer(XYItemRenderer renderer) {
        ParamChecks.nullNotPermitted(renderer, "renderer");
        if (renderer instanceof AbstractRenderer) {
            applyToAbstractRenderer((AbstractRenderer) renderer);
        }
        renderer.setBaseItemLabelFont(this.regularFont);
        renderer.setBaseItemLabelPaint(this.itemLabelPaint);
        if (renderer instanceof XYBarRenderer) {
            XYBarRenderer br = (XYBarRenderer) renderer;
            br.setBarPainter(this.xyBarPainter);
            br.setShadowVisible(this.shadowVisible);
        }
    }

    protected void applyToXYAnnotation(XYAnnotation annotation) {
        ParamChecks.nullNotPermitted(annotation, "annotation");
        if (annotation instanceof XYTextAnnotation) {
            XYTextAnnotation xyta = (XYTextAnnotation) annotation;
            xyta.setFont(this.smallFont);
            xyta.setPaint(this.itemLabelPaint);
        }
    }

    public boolean equals(Object obj) {
        if (obj == this) {
            return true;
        }
        if (!(obj instanceof StandardChartTheme)) {
            return false;
        }
        StandardChartTheme that = (StandardChartTheme) obj;
        if (!this.name.equals(that.name)) {
            return false;
        }
        if (!this.extraLargeFont.equals(that.extraLargeFont)) {
            return false;
        }
        if (!this.largeFont.equals(that.largeFont)) {
            return false;
        }
        if (!this.regularFont.equals(that.regularFont)) {
            return false;
        }
        if (!this.smallFont.equals(that.smallFont)) {
            return false;
        }
        if (!PaintUtilities.equal(this.titlePaint, that.titlePaint)) {
            return false;
        }
        if (!PaintUtilities.equal(this.subtitlePaint, that.subtitlePaint)) {
            return false;
        }
        if (!PaintUtilities.equal(this.chartBackgroundPaint, that.chartBackgroundPaint)) {
            return false;
        }
        if (!PaintUtilities.equal(this.legendBackgroundPaint, that.legendBackgroundPaint)) {
            return false;
        }
        if (!PaintUtilities.equal(this.legendItemPaint, that.legendItemPaint)) {
            return false;
        }
        if (!this.drawingSupplier.equals(that.drawingSupplier)) {
            return false;
        }
        if (!PaintUtilities.equal(this.plotBackgroundPaint, that.plotBackgroundPaint)) {
            return false;
        }
        if (!PaintUtilities.equal(this.plotOutlinePaint, that.plotOutlinePaint)) {
            return false;
        }
        if (!this.labelLinkStyle.equals(that.labelLinkStyle)) {
            return false;
        }
        if (!PaintUtilities.equal(this.labelLinkPaint, that.labelLinkPaint)) {
            return false;
        }
        if (!PaintUtilities.equal(this.domainGridlinePaint, that.domainGridlinePaint)) {
            return false;
        }
        if (!PaintUtilities.equal(this.rangeGridlinePaint, that.rangeGridlinePaint)) {
            return false;
        }
        if (!PaintUtilities.equal(this.crosshairPaint, that.crosshairPaint)) {
            return false;
        }
        if (!this.axisOffset.equals(that.axisOffset)) {
            return false;
        }
        if (!PaintUtilities.equal(this.axisLabelPaint, that.axisLabelPaint)) {
            return false;
        }
        if (!PaintUtilities.equal(this.tickLabelPaint, that.tickLabelPaint)) {
            return false;
        }
        if (!PaintUtilities.equal(this.itemLabelPaint, that.itemLabelPaint)) {
            return false;
        }
        if (this.shadowVisible != that.shadowVisible) {
            return false;
        }
        if (!PaintUtilities.equal(this.shadowPaint, that.shadowPaint)) {
            return false;
        }
        if (!this.barPainter.equals(that.barPainter)) {
            return false;
        }
        if (!this.xyBarPainter.equals(that.xyBarPainter)) {
            return false;
        }
        if (!PaintUtilities.equal(this.thermometerPaint, that.thermometerPaint)) {
            return false;
        }
        if (!PaintUtilities.equal(this.wallPaint, that.wallPaint)) {
            return false;
        }
        if (!PaintUtilities.equal(this.errorIndicatorPaint, that.errorIndicatorPaint)) {
            return false;
        }
        if (!PaintUtilities.equal(this.gridBandPaint, that.gridBandPaint)) {
            return false;
        }
        if (PaintUtilities.equal(this.gridBandAlternatePaint, that.gridBandAlternatePaint)) {
            return true;
        }
        return false;
    }

    public Object clone() throws CloneNotSupportedException {
        return super.clone();
    }

    private void writeObject(ObjectOutputStream stream) throws IOException {
        stream.defaultWriteObject();
        SerialUtilities.writePaint(this.titlePaint, stream);
        SerialUtilities.writePaint(this.subtitlePaint, stream);
        SerialUtilities.writePaint(this.chartBackgroundPaint, stream);
        SerialUtilities.writePaint(this.legendBackgroundPaint, stream);
        SerialUtilities.writePaint(this.legendItemPaint, stream);
        SerialUtilities.writePaint(this.plotBackgroundPaint, stream);
        SerialUtilities.writePaint(this.plotOutlinePaint, stream);
        SerialUtilities.writePaint(this.labelLinkPaint, stream);
        SerialUtilities.writePaint(this.baselinePaint, stream);
        SerialUtilities.writePaint(this.domainGridlinePaint, stream);
        SerialUtilities.writePaint(this.rangeGridlinePaint, stream);
        SerialUtilities.writePaint(this.crosshairPaint, stream);
        SerialUtilities.writePaint(this.axisLabelPaint, stream);
        SerialUtilities.writePaint(this.tickLabelPaint, stream);
        SerialUtilities.writePaint(this.itemLabelPaint, stream);
        SerialUtilities.writePaint(this.shadowPaint, stream);
        SerialUtilities.writePaint(this.thermometerPaint, stream);
        SerialUtilities.writePaint(this.wallPaint, stream);
        SerialUtilities.writePaint(this.errorIndicatorPaint, stream);
        SerialUtilities.writePaint(this.gridBandPaint, stream);
        SerialUtilities.writePaint(this.gridBandAlternatePaint, stream);
    }

    private void readObject(ObjectInputStream stream) throws IOException, ClassNotFoundException {
        stream.defaultReadObject();
        this.titlePaint = SerialUtilities.readPaint(stream);
        this.subtitlePaint = SerialUtilities.readPaint(stream);
        this.chartBackgroundPaint = SerialUtilities.readPaint(stream);
        this.legendBackgroundPaint = SerialUtilities.readPaint(stream);
        this.legendItemPaint = SerialUtilities.readPaint(stream);
        this.plotBackgroundPaint = SerialUtilities.readPaint(stream);
        this.plotOutlinePaint = SerialUtilities.readPaint(stream);
        this.labelLinkPaint = SerialUtilities.readPaint(stream);
        this.baselinePaint = SerialUtilities.readPaint(stream);
        this.domainGridlinePaint = SerialUtilities.readPaint(stream);
        this.rangeGridlinePaint = SerialUtilities.readPaint(stream);
        this.crosshairPaint = SerialUtilities.readPaint(stream);
        this.axisLabelPaint = SerialUtilities.readPaint(stream);
        this.tickLabelPaint = SerialUtilities.readPaint(stream);
        this.itemLabelPaint = SerialUtilities.readPaint(stream);
        this.shadowPaint = SerialUtilities.readPaint(stream);
        this.thermometerPaint = SerialUtilities.readPaint(stream);
        this.wallPaint = SerialUtilities.readPaint(stream);
        this.errorIndicatorPaint = SerialUtilities.readPaint(stream);
        this.gridBandPaint = SerialUtilities.readPaint(stream);
        this.gridBandAlternatePaint = SerialUtilities.readPaint(stream);
    }
}
