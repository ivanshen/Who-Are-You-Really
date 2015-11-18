package org.jfree.chart.plot;

import java.awt.AlphaComposite;
import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Composite;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.Paint;
import java.awt.RadialGradientPaint;
import java.awt.Shape;
import java.awt.Stroke;
import java.awt.geom.Arc2D;
import java.awt.geom.CubicCurve2D;
import java.awt.geom.Line2D;
import java.awt.geom.Line2D.Float;
import java.awt.geom.Point2D;
import java.awt.geom.QuadCurve2D;
import java.awt.geom.Rectangle2D;
import java.awt.geom.Rectangle2D.Double;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.TreeMap;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.LegendItem;
import org.jfree.chart.LegendItemCollection;
import org.jfree.chart.PaintMap;
import org.jfree.chart.StrokeMap;
import org.jfree.chart.axis.DateAxis;
import org.jfree.chart.entity.EntityCollection;
import org.jfree.chart.entity.PieSectionEntity;
import org.jfree.chart.labels.PieSectionLabelGenerator;
import org.jfree.chart.labels.PieToolTipGenerator;
import org.jfree.chart.labels.StandardPieSectionLabelGenerator;
import org.jfree.chart.urls.PieURLGenerator;
import org.jfree.chart.util.ParamChecks;
import org.jfree.chart.util.ResourceBundleWrapper;
import org.jfree.chart.util.ShadowGenerator;
import org.jfree.data.DefaultKeyedValues;
import org.jfree.data.KeyedValues;
import org.jfree.data.general.DatasetChangeEvent;
import org.jfree.data.general.DatasetUtilities;
import org.jfree.data.general.PieDataset;
import org.jfree.data.xy.NormalizedMatrixSeries;
import org.jfree.io.SerialUtilities;
import org.jfree.text.G2TextMeasurer;
import org.jfree.text.TextBox;
import org.jfree.text.TextUtilities;
import org.jfree.ui.RectangleAnchor;
import org.jfree.ui.RectangleInsets;
import org.jfree.ui.TextAnchor;
import org.jfree.util.ObjectUtilities;
import org.jfree.util.PaintUtilities;
import org.jfree.util.PublicCloneable;
import org.jfree.util.Rotation;
import org.jfree.util.ShapeUtilities;
import org.jfree.util.UnitType;

public class PiePlot extends Plot implements Cloneable, Serializable {
    static final boolean DEBUG_DRAW_INTERIOR = false;
    static final boolean DEBUG_DRAW_LINK_AREA = false;
    static final boolean DEBUG_DRAW_PIE_AREA = false;
    public static final double DEFAULT_INTERIOR_GAP = 0.08d;
    public static final Paint DEFAULT_LABEL_BACKGROUND_PAINT;
    public static final Font DEFAULT_LABEL_FONT;
    public static final Paint DEFAULT_LABEL_OUTLINE_PAINT;
    public static final Stroke DEFAULT_LABEL_OUTLINE_STROKE;
    public static final Paint DEFAULT_LABEL_PAINT;
    public static final Paint DEFAULT_LABEL_SHADOW_PAINT;
    public static final double DEFAULT_MINIMUM_ARC_ANGLE_TO_DRAW = 1.0E-5d;
    public static final double DEFAULT_START_ANGLE = 90.0d;
    public static final double MAX_INTERIOR_GAP = 0.4d;
    protected static ResourceBundle localizationResources = null;
    private static final long serialVersionUID = -795612466005590431L;
    private boolean autoPopulateSectionOutlinePaint;
    private boolean autoPopulateSectionOutlineStroke;
    private boolean autoPopulateSectionPaint;
    private transient Paint baseSectionOutlinePaint;
    private transient Stroke baseSectionOutlineStroke;
    private transient Paint baseSectionPaint;
    private boolean circular;
    private PieDataset dataset;
    private Rotation direction;
    private Map explodePercentages;
    private boolean ignoreNullValues;
    private boolean ignoreZeroValues;
    private double interiorGap;
    private transient Paint labelBackgroundPaint;
    private AbstractPieLabelDistributor labelDistributor;
    private Font labelFont;
    private double labelGap;
    private PieSectionLabelGenerator labelGenerator;
    private double labelLinkMargin;
    private transient Paint labelLinkPaint;
    private transient Stroke labelLinkStroke;
    private PieLabelLinkStyle labelLinkStyle;
    private boolean labelLinksVisible;
    private transient Paint labelOutlinePaint;
    private transient Stroke labelOutlineStroke;
    private RectangleInsets labelPadding;
    private transient Paint labelPaint;
    private transient Paint labelShadowPaint;
    private transient Shape legendItemShape;
    private PieSectionLabelGenerator legendLabelGenerator;
    private PieSectionLabelGenerator legendLabelToolTipGenerator;
    private PieURLGenerator legendLabelURLGenerator;
    private double maximumLabelWidth;
    private double minimumArcAngleToDraw;
    private int pieIndex;
    private transient Paint sectionOutlinePaint;
    private PaintMap sectionOutlinePaintMap;
    private transient Stroke sectionOutlineStroke;
    private StrokeMap sectionOutlineStrokeMap;
    private boolean sectionOutlinesVisible;
    private transient Paint sectionPaint;
    private PaintMap sectionPaintMap;
    private ShadowGenerator shadowGenerator;
    private transient Paint shadowPaint;
    private double shadowXOffset;
    private double shadowYOffset;
    private RectangleInsets simpleLabelOffset;
    private boolean simpleLabels;
    private double startAngle;
    private PieToolTipGenerator toolTipGenerator;
    private PieURLGenerator urlGenerator;

    static {
        DEFAULT_LABEL_FONT = new Font("SansSerif", 0, 10);
        DEFAULT_LABEL_PAINT = Color.black;
        DEFAULT_LABEL_BACKGROUND_PAINT = new Color(255, 255, 192);
        DEFAULT_LABEL_OUTLINE_PAINT = Color.black;
        DEFAULT_LABEL_OUTLINE_STROKE = new BasicStroke(JFreeChart.DEFAULT_BACKGROUND_IMAGE_ALPHA);
        DEFAULT_LABEL_SHADOW_PAINT = new Color(151, 151, 151, 128);
        localizationResources = ResourceBundleWrapper.getBundle("org.jfree.chart.plot.LocalizationBundle");
    }

    public PiePlot() {
        this(null);
    }

    public PiePlot(PieDataset dataset) {
        this.shadowPaint = Color.gray;
        this.shadowXOffset = 4.0d;
        this.shadowYOffset = 4.0d;
        this.simpleLabels = true;
        this.maximumLabelWidth = 0.14d;
        this.labelGap = 0.025d;
        this.labelLinkStyle = PieLabelLinkStyle.STANDARD;
        this.labelLinkMargin = 0.025d;
        this.labelLinkPaint = Color.black;
        this.labelLinkStroke = new BasicStroke(JFreeChart.DEFAULT_BACKGROUND_IMAGE_ALPHA);
        this.dataset = dataset;
        if (dataset != null) {
            dataset.addChangeListener(this);
        }
        this.pieIndex = 0;
        this.interiorGap = DEFAULT_INTERIOR_GAP;
        this.circular = true;
        this.startAngle = DEFAULT_START_ANGLE;
        this.direction = Rotation.CLOCKWISE;
        this.minimumArcAngleToDraw = DEFAULT_MINIMUM_ARC_ANGLE_TO_DRAW;
        this.sectionPaint = null;
        this.sectionPaintMap = new PaintMap();
        this.baseSectionPaint = Color.gray;
        this.autoPopulateSectionPaint = true;
        this.sectionOutlinesVisible = true;
        this.sectionOutlinePaint = null;
        this.sectionOutlinePaintMap = new PaintMap();
        this.baseSectionOutlinePaint = DEFAULT_OUTLINE_PAINT;
        this.autoPopulateSectionOutlinePaint = DEBUG_DRAW_PIE_AREA;
        this.sectionOutlineStroke = null;
        this.sectionOutlineStrokeMap = new StrokeMap();
        this.baseSectionOutlineStroke = DEFAULT_OUTLINE_STROKE;
        this.autoPopulateSectionOutlineStroke = DEBUG_DRAW_PIE_AREA;
        this.explodePercentages = new TreeMap();
        this.labelGenerator = new StandardPieSectionLabelGenerator();
        this.labelFont = DEFAULT_LABEL_FONT;
        this.labelPaint = DEFAULT_LABEL_PAINT;
        this.labelBackgroundPaint = DEFAULT_LABEL_BACKGROUND_PAINT;
        this.labelOutlinePaint = DEFAULT_LABEL_OUTLINE_PAINT;
        this.labelOutlineStroke = DEFAULT_LABEL_OUTLINE_STROKE;
        this.labelShadowPaint = DEFAULT_LABEL_SHADOW_PAINT;
        this.labelLinksVisible = true;
        this.labelDistributor = new PieLabelDistributor(0);
        this.simpleLabels = DEBUG_DRAW_PIE_AREA;
        this.simpleLabelOffset = new RectangleInsets(UnitType.RELATIVE, 0.18d, 0.18d, 0.18d, 0.18d);
        this.labelPadding = new RectangleInsets(DateAxis.DEFAULT_AUTO_RANGE_MINIMUM_SIZE_IN_MILLISECONDS, DateAxis.DEFAULT_AUTO_RANGE_MINIMUM_SIZE_IN_MILLISECONDS, DateAxis.DEFAULT_AUTO_RANGE_MINIMUM_SIZE_IN_MILLISECONDS, DateAxis.DEFAULT_AUTO_RANGE_MINIMUM_SIZE_IN_MILLISECONDS);
        this.toolTipGenerator = null;
        this.urlGenerator = null;
        this.legendLabelGenerator = new StandardPieSectionLabelGenerator();
        this.legendLabelToolTipGenerator = null;
        this.legendLabelURLGenerator = null;
        this.legendItemShape = Plot.DEFAULT_LEGEND_ITEM_CIRCLE;
        this.ignoreNullValues = DEBUG_DRAW_PIE_AREA;
        this.ignoreZeroValues = DEBUG_DRAW_PIE_AREA;
        this.shadowGenerator = null;
    }

    public PieDataset getDataset() {
        return this.dataset;
    }

    public void setDataset(PieDataset dataset) {
        PieDataset existing = this.dataset;
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

    public int getPieIndex() {
        return this.pieIndex;
    }

    public void setPieIndex(int index) {
        this.pieIndex = index;
    }

    public double getStartAngle() {
        return this.startAngle;
    }

    public void setStartAngle(double angle) {
        this.startAngle = angle;
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
            throw new IllegalArgumentException("Invalid 'percent' (" + percent + ") argument.");
        } else if (this.interiorGap != percent) {
            this.interiorGap = percent;
            fireChangeEvent();
        }
    }

    public boolean isCircular() {
        return this.circular;
    }

    public void setCircular(boolean flag) {
        setCircular(flag, true);
    }

    public void setCircular(boolean circular, boolean notify) {
        this.circular = circular;
        if (notify) {
            fireChangeEvent();
        }
    }

    public boolean getIgnoreNullValues() {
        return this.ignoreNullValues;
    }

    public void setIgnoreNullValues(boolean flag) {
        this.ignoreNullValues = flag;
        fireChangeEvent();
    }

    public boolean getIgnoreZeroValues() {
        return this.ignoreZeroValues;
    }

    public void setIgnoreZeroValues(boolean flag) {
        this.ignoreZeroValues = flag;
        fireChangeEvent();
    }

    protected Paint lookupSectionPaint(Comparable key) {
        return lookupSectionPaint(key, getAutoPopulateSectionPaint());
    }

    protected Paint lookupSectionPaint(Comparable key, boolean autoPopulate) {
        Paint result = getSectionPaint();
        if (result != null) {
            return result;
        }
        result = this.sectionPaintMap.getPaint(key);
        if (result != null) {
            return result;
        }
        if (autoPopulate) {
            DrawingSupplier ds = getDrawingSupplier();
            if (ds != null) {
                result = ds.getNextPaint();
                this.sectionPaintMap.put(key, result);
            } else {
                result = this.baseSectionPaint;
            }
        } else {
            result = this.baseSectionPaint;
        }
        return result;
    }

    public Paint getSectionPaint() {
        return this.sectionPaint;
    }

    public void setSectionPaint(Paint paint) {
        this.sectionPaint = paint;
        fireChangeEvent();
    }

    protected Comparable getSectionKey(int section) {
        Comparable key = null;
        if (this.dataset != null && section >= 0 && section < this.dataset.getItemCount()) {
            key = this.dataset.getKey(section);
        }
        if (key == null) {
            return new Integer(section);
        }
        return key;
    }

    public Paint getSectionPaint(Comparable key) {
        return this.sectionPaintMap.getPaint(key);
    }

    public void setSectionPaint(Comparable key, Paint paint) {
        this.sectionPaintMap.put(key, paint);
        fireChangeEvent();
    }

    public void clearSectionPaints(boolean notify) {
        this.sectionPaintMap.clear();
        if (notify) {
            fireChangeEvent();
        }
    }

    public Paint getBaseSectionPaint() {
        return this.baseSectionPaint;
    }

    public void setBaseSectionPaint(Paint paint) {
        ParamChecks.nullNotPermitted(paint, "paint");
        this.baseSectionPaint = paint;
        fireChangeEvent();
    }

    public boolean getAutoPopulateSectionPaint() {
        return this.autoPopulateSectionPaint;
    }

    public void setAutoPopulateSectionPaint(boolean auto) {
        this.autoPopulateSectionPaint = auto;
        fireChangeEvent();
    }

    public boolean getSectionOutlinesVisible() {
        return this.sectionOutlinesVisible;
    }

    public void setSectionOutlinesVisible(boolean visible) {
        this.sectionOutlinesVisible = visible;
        fireChangeEvent();
    }

    protected Paint lookupSectionOutlinePaint(Comparable key) {
        return lookupSectionOutlinePaint(key, getAutoPopulateSectionOutlinePaint());
    }

    protected Paint lookupSectionOutlinePaint(Comparable key, boolean autoPopulate) {
        Paint result = getSectionOutlinePaint();
        if (result != null) {
            return result;
        }
        result = this.sectionOutlinePaintMap.getPaint(key);
        if (result != null) {
            return result;
        }
        if (autoPopulate) {
            DrawingSupplier ds = getDrawingSupplier();
            if (ds != null) {
                result = ds.getNextOutlinePaint();
                this.sectionOutlinePaintMap.put(key, result);
            } else {
                result = this.baseSectionOutlinePaint;
            }
        } else {
            result = this.baseSectionOutlinePaint;
        }
        return result;
    }

    public Paint getSectionOutlinePaint(Comparable key) {
        return this.sectionOutlinePaintMap.getPaint(key);
    }

    public void setSectionOutlinePaint(Comparable key, Paint paint) {
        this.sectionOutlinePaintMap.put(key, paint);
        fireChangeEvent();
    }

    public void clearSectionOutlinePaints(boolean notify) {
        this.sectionOutlinePaintMap.clear();
        if (notify) {
            fireChangeEvent();
        }
    }

    public Paint getBaseSectionOutlinePaint() {
        return this.baseSectionOutlinePaint;
    }

    public void setBaseSectionOutlinePaint(Paint paint) {
        ParamChecks.nullNotPermitted(paint, "paint");
        this.baseSectionOutlinePaint = paint;
        fireChangeEvent();
    }

    public boolean getAutoPopulateSectionOutlinePaint() {
        return this.autoPopulateSectionOutlinePaint;
    }

    public void setAutoPopulateSectionOutlinePaint(boolean auto) {
        this.autoPopulateSectionOutlinePaint = auto;
        fireChangeEvent();
    }

    protected Stroke lookupSectionOutlineStroke(Comparable key) {
        return lookupSectionOutlineStroke(key, getAutoPopulateSectionOutlineStroke());
    }

    protected Stroke lookupSectionOutlineStroke(Comparable key, boolean autoPopulate) {
        Stroke result = getSectionOutlineStroke();
        if (result != null) {
            return result;
        }
        result = this.sectionOutlineStrokeMap.getStroke(key);
        if (result != null) {
            return result;
        }
        if (autoPopulate) {
            DrawingSupplier ds = getDrawingSupplier();
            if (ds != null) {
                result = ds.getNextOutlineStroke();
                this.sectionOutlineStrokeMap.put(key, result);
            } else {
                result = this.baseSectionOutlineStroke;
            }
        } else {
            result = this.baseSectionOutlineStroke;
        }
        return result;
    }

    public Stroke getSectionOutlineStroke(Comparable key) {
        return this.sectionOutlineStrokeMap.getStroke(key);
    }

    public void setSectionOutlineStroke(Comparable key, Stroke stroke) {
        this.sectionOutlineStrokeMap.put(key, stroke);
        fireChangeEvent();
    }

    public void clearSectionOutlineStrokes(boolean notify) {
        this.sectionOutlineStrokeMap.clear();
        if (notify) {
            fireChangeEvent();
        }
    }

    public Stroke getBaseSectionOutlineStroke() {
        return this.baseSectionOutlineStroke;
    }

    public void setBaseSectionOutlineStroke(Stroke stroke) {
        ParamChecks.nullNotPermitted(stroke, "stroke");
        this.baseSectionOutlineStroke = stroke;
        fireChangeEvent();
    }

    public boolean getAutoPopulateSectionOutlineStroke() {
        return this.autoPopulateSectionOutlineStroke;
    }

    public void setAutoPopulateSectionOutlineStroke(boolean auto) {
        this.autoPopulateSectionOutlineStroke = auto;
        fireChangeEvent();
    }

    public Paint getShadowPaint() {
        return this.shadowPaint;
    }

    public void setShadowPaint(Paint paint) {
        this.shadowPaint = paint;
        fireChangeEvent();
    }

    public double getShadowXOffset() {
        return this.shadowXOffset;
    }

    public void setShadowXOffset(double offset) {
        this.shadowXOffset = offset;
        fireChangeEvent();
    }

    public double getShadowYOffset() {
        return this.shadowYOffset;
    }

    public void setShadowYOffset(double offset) {
        this.shadowYOffset = offset;
        fireChangeEvent();
    }

    public double getExplodePercent(Comparable key) {
        if (this.explodePercentages == null) {
            return 0.0d;
        }
        Number percent = (Number) this.explodePercentages.get(key);
        if (percent != null) {
            return percent.doubleValue();
        }
        return 0.0d;
    }

    public void setExplodePercent(Comparable key, double percent) {
        ParamChecks.nullNotPermitted(key, "key");
        if (this.explodePercentages == null) {
            this.explodePercentages = new TreeMap();
        }
        this.explodePercentages.put(key, new Double(percent));
        fireChangeEvent();
    }

    public double getMaximumExplodePercent() {
        if (this.dataset == null) {
            return 0.0d;
        }
        double result = 0.0d;
        for (Comparable key : this.dataset.getKeys()) {
            Number explode = (Number) this.explodePercentages.get(key);
            if (explode != null) {
                result = Math.max(result, explode.doubleValue());
            }
        }
        return result;
    }

    public PieSectionLabelGenerator getLabelGenerator() {
        return this.labelGenerator;
    }

    public void setLabelGenerator(PieSectionLabelGenerator generator) {
        this.labelGenerator = generator;
        fireChangeEvent();
    }

    public double getLabelGap() {
        return this.labelGap;
    }

    public void setLabelGap(double gap) {
        this.labelGap = gap;
        fireChangeEvent();
    }

    public double getMaximumLabelWidth() {
        return this.maximumLabelWidth;
    }

    public void setMaximumLabelWidth(double width) {
        this.maximumLabelWidth = width;
        fireChangeEvent();
    }

    public boolean getLabelLinksVisible() {
        return this.labelLinksVisible;
    }

    public void setLabelLinksVisible(boolean visible) {
        this.labelLinksVisible = visible;
        fireChangeEvent();
    }

    public PieLabelLinkStyle getLabelLinkStyle() {
        return this.labelLinkStyle;
    }

    public void setLabelLinkStyle(PieLabelLinkStyle style) {
        ParamChecks.nullNotPermitted(style, "style");
        this.labelLinkStyle = style;
        fireChangeEvent();
    }

    public double getLabelLinkMargin() {
        return this.labelLinkMargin;
    }

    public void setLabelLinkMargin(double margin) {
        this.labelLinkMargin = margin;
        fireChangeEvent();
    }

    public Paint getLabelLinkPaint() {
        return this.labelLinkPaint;
    }

    public void setLabelLinkPaint(Paint paint) {
        ParamChecks.nullNotPermitted(paint, "paint");
        this.labelLinkPaint = paint;
        fireChangeEvent();
    }

    public Stroke getLabelLinkStroke() {
        return this.labelLinkStroke;
    }

    public void setLabelLinkStroke(Stroke stroke) {
        ParamChecks.nullNotPermitted(stroke, "stroke");
        this.labelLinkStroke = stroke;
        fireChangeEvent();
    }

    protected double getLabelLinkDepth() {
        return SpiderWebPlot.DEFAULT_AXIS_LABEL_GAP;
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

    public Paint getLabelBackgroundPaint() {
        return this.labelBackgroundPaint;
    }

    public void setLabelBackgroundPaint(Paint paint) {
        this.labelBackgroundPaint = paint;
        fireChangeEvent();
    }

    public Paint getLabelOutlinePaint() {
        return this.labelOutlinePaint;
    }

    public void setLabelOutlinePaint(Paint paint) {
        this.labelOutlinePaint = paint;
        fireChangeEvent();
    }

    public Stroke getLabelOutlineStroke() {
        return this.labelOutlineStroke;
    }

    public void setLabelOutlineStroke(Stroke stroke) {
        this.labelOutlineStroke = stroke;
        fireChangeEvent();
    }

    public Paint getLabelShadowPaint() {
        return this.labelShadowPaint;
    }

    public void setLabelShadowPaint(Paint paint) {
        this.labelShadowPaint = paint;
        fireChangeEvent();
    }

    public RectangleInsets getLabelPadding() {
        return this.labelPadding;
    }

    public void setLabelPadding(RectangleInsets padding) {
        ParamChecks.nullNotPermitted(padding, "padding");
        this.labelPadding = padding;
        fireChangeEvent();
    }

    public boolean getSimpleLabels() {
        return this.simpleLabels;
    }

    public void setSimpleLabels(boolean simple) {
        this.simpleLabels = simple;
        fireChangeEvent();
    }

    public RectangleInsets getSimpleLabelOffset() {
        return this.simpleLabelOffset;
    }

    public void setSimpleLabelOffset(RectangleInsets offset) {
        ParamChecks.nullNotPermitted(offset, "offset");
        this.simpleLabelOffset = offset;
        fireChangeEvent();
    }

    public AbstractPieLabelDistributor getLabelDistributor() {
        return this.labelDistributor;
    }

    public void setLabelDistributor(AbstractPieLabelDistributor distributor) {
        ParamChecks.nullNotPermitted(distributor, "distributor");
        this.labelDistributor = distributor;
        fireChangeEvent();
    }

    public PieToolTipGenerator getToolTipGenerator() {
        return this.toolTipGenerator;
    }

    public void setToolTipGenerator(PieToolTipGenerator generator) {
        this.toolTipGenerator = generator;
        fireChangeEvent();
    }

    public PieURLGenerator getURLGenerator() {
        return this.urlGenerator;
    }

    public void setURLGenerator(PieURLGenerator generator) {
        this.urlGenerator = generator;
        fireChangeEvent();
    }

    public double getMinimumArcAngleToDraw() {
        return this.minimumArcAngleToDraw;
    }

    public void setMinimumArcAngleToDraw(double angle) {
        this.minimumArcAngleToDraw = angle;
    }

    public Shape getLegendItemShape() {
        return this.legendItemShape;
    }

    public void setLegendItemShape(Shape shape) {
        ParamChecks.nullNotPermitted(shape, "shape");
        this.legendItemShape = shape;
        fireChangeEvent();
    }

    public PieSectionLabelGenerator getLegendLabelGenerator() {
        return this.legendLabelGenerator;
    }

    public void setLegendLabelGenerator(PieSectionLabelGenerator generator) {
        ParamChecks.nullNotPermitted(generator, "generator");
        this.legendLabelGenerator = generator;
        fireChangeEvent();
    }

    public PieSectionLabelGenerator getLegendLabelToolTipGenerator() {
        return this.legendLabelToolTipGenerator;
    }

    public void setLegendLabelToolTipGenerator(PieSectionLabelGenerator generator) {
        this.legendLabelToolTipGenerator = generator;
        fireChangeEvent();
    }

    public PieURLGenerator getLegendLabelURLGenerator() {
        return this.legendLabelURLGenerator;
    }

    public void setLegendLabelURLGenerator(PieURLGenerator generator) {
        this.legendLabelURLGenerator = generator;
        fireChangeEvent();
    }

    public ShadowGenerator getShadowGenerator() {
        return this.shadowGenerator;
    }

    public void setShadowGenerator(ShadowGenerator generator) {
        this.shadowGenerator = generator;
        fireChangeEvent();
    }

    public void handleMouseWheelRotation(int rotateClicks) {
        setStartAngle(this.startAngle + (((double) rotateClicks) * 4.0d));
    }

    public PiePlotState initialise(Graphics2D g2, Rectangle2D plotArea, PiePlot plot, Integer index, PlotRenderingInfo info) {
        PiePlotState state = new PiePlotState(info);
        state.setPassesRequired(2);
        if (this.dataset != null) {
            state.setTotal(DatasetUtilities.calculatePieDatasetTotal(plot.getDataset()));
        }
        state.setLatestAngle(plot.getStartAngle());
        return state;
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
            Graphics2D savedG2 = g2;
            boolean suppressShadow = Boolean.TRUE.equals(g2.getRenderingHint(JFreeChart.KEY_SUPPRESS_SHADOW_GENERATION));
            BufferedImage dataImage = null;
            if (!(this.shadowGenerator == null || suppressShadow)) {
                dataImage = new BufferedImage((int) area.getWidth(), (int) area.getHeight(), 2);
                g2 = dataImage.createGraphics();
                g2.translate(-area.getX(), -area.getY());
                g2.setRenderingHints(savedG2.getRenderingHints());
            }
            drawPie(g2, area, info);
            if (!(this.shadowGenerator == null || suppressShadow)) {
                g2 = savedG2;
                g2.drawImage(this.shadowGenerator.createDropShadow(dataImage), ((int) area.getX()) + this.shadowGenerator.calculateOffsetX(), ((int) area.getY()) + this.shadowGenerator.calculateOffsetY(), null);
                g2.drawImage(dataImage, (int) area.getX(), (int) area.getY(), null);
            }
        }
        g2.setClip(savedClip);
        g2.setComposite(originalComposite);
        drawOutline(g2, area);
    }

    protected void drawPie(Graphics2D g2, Rectangle2D plotArea, PlotRenderingInfo info) {
        PiePlotState state = initialise(g2, plotArea, this, null, info);
        double labelReserve = 0.0d;
        if (!(this.labelGenerator == null || this.simpleLabels)) {
            labelReserve = this.labelGap + this.maximumLabelWidth;
        }
        double gapHorizontal = (plotArea.getWidth() * labelReserve) * DateAxis.DEFAULT_AUTO_RANGE_MINIMUM_SIZE_IN_MILLISECONDS;
        double gapVertical = (plotArea.getHeight() * this.interiorGap) * DateAxis.DEFAULT_AUTO_RANGE_MINIMUM_SIZE_IN_MILLISECONDS;
        double linkX = plotArea.getX() + (gapHorizontal / DateAxis.DEFAULT_AUTO_RANGE_MINIMUM_SIZE_IN_MILLISECONDS);
        double linkY = plotArea.getY() + (gapVertical / DateAxis.DEFAULT_AUTO_RANGE_MINIMUM_SIZE_IN_MILLISECONDS);
        double linkW = plotArea.getWidth() - gapHorizontal;
        double linkH = plotArea.getHeight() - gapVertical;
        if (this.circular) {
            double min = Math.min(linkW, linkH) / DateAxis.DEFAULT_AUTO_RANGE_MINIMUM_SIZE_IN_MILLISECONDS;
            linkX = (((linkX + linkX) + linkW) / DateAxis.DEFAULT_AUTO_RANGE_MINIMUM_SIZE_IN_MILLISECONDS) - min;
            linkY = (((linkY + linkY) + linkH) / DateAxis.DEFAULT_AUTO_RANGE_MINIMUM_SIZE_IN_MILLISECONDS) - min;
            linkW = DateAxis.DEFAULT_AUTO_RANGE_MINIMUM_SIZE_IN_MILLISECONDS * min;
            linkH = DateAxis.DEFAULT_AUTO_RANGE_MINIMUM_SIZE_IN_MILLISECONDS * min;
        }
        Rectangle2D linkArea = new Double(linkX, linkY, linkW, linkH);
        state.setLinkArea(linkArea);
        double lm = 0.0d;
        if (!this.simpleLabels) {
            lm = this.labelLinkMargin;
        }
        double hh = (linkArea.getWidth() * lm) * DateAxis.DEFAULT_AUTO_RANGE_MINIMUM_SIZE_IN_MILLISECONDS;
        double vv = (linkArea.getHeight() * lm) * DateAxis.DEFAULT_AUTO_RANGE_MINIMUM_SIZE_IN_MILLISECONDS;
        Rectangle2D explodeArea = new Double(linkX + (hh / DateAxis.DEFAULT_AUTO_RANGE_MINIMUM_SIZE_IN_MILLISECONDS), (vv / DateAxis.DEFAULT_AUTO_RANGE_MINIMUM_SIZE_IN_MILLISECONDS) + linkY, linkW - hh, linkH - vv);
        state.setExplodedPieArea(explodeArea);
        double maximumExplodePercent = getMaximumExplodePercent();
        double percent = maximumExplodePercent / (NormalizedMatrixSeries.DEFAULT_SCALE_FACTOR + maximumExplodePercent);
        double h1 = explodeArea.getWidth() * percent;
        double v1 = explodeArea.getHeight() * percent;
        Rectangle2D pieArea = new Double(explodeArea.getX() + (h1 / DateAxis.DEFAULT_AUTO_RANGE_MINIMUM_SIZE_IN_MILLISECONDS), explodeArea.getY() + (v1 / DateAxis.DEFAULT_AUTO_RANGE_MINIMUM_SIZE_IN_MILLISECONDS), explodeArea.getWidth() - h1, explodeArea.getHeight() - v1);
        state.setPieArea(pieArea);
        state.setPieCenterX(pieArea.getCenterX());
        state.setPieCenterY(pieArea.getCenterY());
        state.setPieWRadius(pieArea.getWidth() / DateAxis.DEFAULT_AUTO_RANGE_MINIMUM_SIZE_IN_MILLISECONDS);
        state.setPieHRadius(pieArea.getHeight() / DateAxis.DEFAULT_AUTO_RANGE_MINIMUM_SIZE_IN_MILLISECONDS);
        if (this.dataset == null || this.dataset.getKeys().size() <= 0) {
            drawNoDataMessage(g2, plotArea);
            return;
        }
        List keys = this.dataset.getKeys();
        double totalValue = DatasetUtilities.calculatePieDatasetTotal(this.dataset);
        int passesRequired = state.getPassesRequired();
        for (int pass = 0; pass < passesRequired; pass++) {
            double runningTotal = 0.0d;
            for (int section = 0; section < keys.size(); section++) {
                Number n = this.dataset.getValue(section);
                if (n != null) {
                    double value = n.doubleValue();
                    if (value > 0.0d) {
                        runningTotal += value;
                        drawItem(g2, section, explodeArea, state, pass);
                    }
                }
            }
        }
        if (this.simpleLabels) {
            drawSimpleLabels(g2, keys, totalValue, plotArea, linkArea, state);
        } else {
            drawLabels(g2, keys, totalValue, plotArea, linkArea, state);
        }
    }

    protected void drawItem(Graphics2D g2, int section, Rectangle2D dataArea, PiePlotState state, int currentPass) {
        Number n = this.dataset.getValue(section);
        if (n != null) {
            double angle1;
            double angle2;
            double value = n.doubleValue();
            if (this.direction == Rotation.CLOCKWISE) {
                angle1 = state.getLatestAngle();
                angle2 = angle1 - ((value / state.getTotal()) * 360.0d);
            } else if (this.direction == Rotation.ANTICLOCKWISE) {
                angle1 = state.getLatestAngle();
                angle2 = angle1 + ((value / state.getTotal()) * 360.0d);
            } else {
                throw new IllegalStateException("Rotation type not recognised.");
            }
            double angle = angle2 - angle1;
            if (Math.abs(angle) > getMinimumArcAngleToDraw()) {
                double ep = 0.0d;
                double mep = getMaximumExplodePercent();
                if (mep > 0.0d) {
                    ep = getExplodePercent(section) / mep;
                }
                Arc2D.Double arc = new Arc2D.Double(getArcBounds(state.getPieArea(), state.getExplodedPieArea(), angle1, angle, ep), angle1, angle, 2);
                if (currentPass == 0) {
                    if (this.shadowPaint != null && this.shadowGenerator == null) {
                        Shape shadowArc = ShapeUtilities.createTranslatedShape(arc, (double) ((float) this.shadowXOffset), (double) ((float) this.shadowYOffset));
                        g2.setPaint(this.shadowPaint);
                        g2.fill(shadowArc);
                    }
                } else if (currentPass == 1) {
                    Comparable key = getSectionKey(section);
                    g2.setPaint(lookupSectionPaint(key, state));
                    g2.fill(arc);
                    Paint outlinePaint = lookupSectionOutlinePaint(key);
                    Stroke outlineStroke = lookupSectionOutlineStroke(key);
                    if (this.sectionOutlinesVisible) {
                        g2.setPaint(outlinePaint);
                        g2.setStroke(outlineStroke);
                        g2.draw(arc);
                    }
                    if (state.getInfo() != null) {
                        EntityCollection entities = state.getEntityCollection();
                        if (entities != null) {
                            String tip = null;
                            if (this.toolTipGenerator != null) {
                                tip = this.toolTipGenerator.generateToolTip(this.dataset, key);
                            }
                            String url = null;
                            if (this.urlGenerator != null) {
                                url = this.urlGenerator.generateURL(this.dataset, key, this.pieIndex);
                            }
                            Shape shape = arc;
                            entities.add(new PieSectionEntity(shape, this.dataset, this.pieIndex, section, key, tip, url));
                        }
                    }
                }
            }
            state.setLatestAngle(angle2);
        }
    }

    protected void drawSimpleLabels(Graphics2D g2, List keys, double totalValue, Rectangle2D plotArea, Rectangle2D pieArea, PiePlotState state) {
        Composite originalComposite = g2.getComposite();
        g2.setComposite(AlphaComposite.getInstance(3, Plot.DEFAULT_FOREGROUND_ALPHA));
        Rectangle2D labelsArea = this.simpleLabelOffset.createInsetRectangle(pieArea);
        double runningTotal = 0.0d;
        for (Comparable key : keys) {
            boolean include;
            double v = 0.0d;
            Number n = getDataset().getValue(key);
            if (n == null) {
                include = !getIgnoreNullValues() ? true : DEBUG_DRAW_PIE_AREA;
            } else {
                v = n.doubleValue();
                include = getIgnoreZeroValues() ? v > 0.0d ? true : DEBUG_DRAW_PIE_AREA : v >= 0.0d ? true : DEBUG_DRAW_PIE_AREA;
            }
            if (include) {
                runningTotal += v;
                Arc2D arc = new Arc2D.Double(labelsArea, getStartAngle(), (getStartAngle() + ((getDirection().getFactor() * ((runningTotal - (v / DateAxis.DEFAULT_AUTO_RANGE_MINIMUM_SIZE_IN_MILLISECONDS)) * 360.0d)) / totalValue)) - getStartAngle(), 0);
                int x = (int) arc.getEndPoint().getX();
                int y = (int) arc.getEndPoint().getY();
                PieSectionLabelGenerator myLabelGenerator = getLabelGenerator();
                if (myLabelGenerator != null) {
                    String label = myLabelGenerator.generateSectionLabel(this.dataset, key);
                    if (label != null) {
                        g2.setFont(this.labelFont);
                        Rectangle2D bounds = TextUtilities.getTextBounds(label, g2, g2.getFontMetrics());
                        Shape bg = ShapeUtilities.createTranslatedShape(this.labelPadding.createOutsetRectangle(bounds), ((double) x) - bounds.getCenterX(), ((double) y) - bounds.getCenterY());
                        if (this.labelShadowPaint != null && this.shadowGenerator == null) {
                            Shape shadow = ShapeUtilities.createTranslatedShape(bg, this.shadowXOffset, this.shadowYOffset);
                            g2.setPaint(this.labelShadowPaint);
                            g2.fill(shadow);
                        }
                        if (this.labelBackgroundPaint != null) {
                            g2.setPaint(this.labelBackgroundPaint);
                            g2.fill(bg);
                        }
                        if (!(this.labelOutlinePaint == null || this.labelOutlineStroke == null)) {
                            g2.setPaint(this.labelOutlinePaint);
                            g2.setStroke(this.labelOutlineStroke);
                            g2.draw(bg);
                        }
                        g2.setPaint(this.labelPaint);
                        g2.setFont(this.labelFont);
                        TextUtilities.drawAlignedString(label, g2, (float) x, (float) y, TextAnchor.CENTER);
                    }
                }
            }
        }
        g2.setComposite(originalComposite);
    }

    protected void drawLabels(Graphics2D g2, List keys, double totalValue, Rectangle2D plotArea, Rectangle2D linkArea, PiePlotState state) {
        Composite originalComposite = g2.getComposite();
        g2.setComposite(AlphaComposite.getInstance(3, Plot.DEFAULT_FOREGROUND_ALPHA));
        DefaultKeyedValues leftKeys = new DefaultKeyedValues();
        DefaultKeyedValues rightKeys = new DefaultKeyedValues();
        double runningTotal = 0.0d;
        for (Comparable key : keys) {
            boolean include;
            double v = 0.0d;
            Number n = this.dataset.getValue(key);
            if (n == null) {
                include = !this.ignoreNullValues ? true : DEBUG_DRAW_PIE_AREA;
            } else {
                v = n.doubleValue();
                include = this.ignoreZeroValues ? v > 0.0d ? true : DEBUG_DRAW_PIE_AREA : v >= 0.0d ? true : DEBUG_DRAW_PIE_AREA;
            }
            if (include) {
                runningTotal += v;
                double mid = this.startAngle + ((this.direction.getFactor() * ((runningTotal - (v / DateAxis.DEFAULT_AUTO_RANGE_MINIMUM_SIZE_IN_MILLISECONDS)) * 360.0d)) / totalValue);
                if (Math.cos(Math.toRadians(mid)) < 0.0d) {
                    leftKeys.addValue(key, new Double(mid));
                } else {
                    rightKeys.addValue(key, new Double(mid));
                }
            }
        }
        g2.setFont(getLabelFont());
        float labelWidth = (float) this.labelPadding.trimWidth((linkArea.getX() - (plotArea.getWidth() * this.labelGap)) - plotArea.getX());
        if (this.labelGenerator != null) {
            drawLeftLabels(leftKeys, g2, plotArea, linkArea, labelWidth, state);
            drawRightLabels(rightKeys, g2, plotArea, linkArea, labelWidth, state);
        }
        g2.setComposite(originalComposite);
    }

    protected void drawLeftLabels(KeyedValues leftKeys, Graphics2D g2, Rectangle2D plotArea, Rectangle2D linkArea, float maxLabelWidth, PiePlotState state) {
        int i;
        double hh;
        this.labelDistributor.clear();
        double lGap = plotArea.getWidth() * this.labelGap;
        double verticalLinkRadius = state.getLinkArea().getHeight() / DateAxis.DEFAULT_AUTO_RANGE_MINIMUM_SIZE_IN_MILLISECONDS;
        for (i = 0; i < leftKeys.getItemCount(); i++) {
            String label = this.labelGenerator.generateSectionLabel(this.dataset, leftKeys.getKey(i));
            if (label != null) {
                TextBox labelBox = new TextBox(TextUtilities.createTextBlock(label, this.labelFont, this.labelPaint, maxLabelWidth, new G2TextMeasurer(g2)));
                labelBox.setBackgroundPaint(this.labelBackgroundPaint);
                labelBox.setOutlinePaint(this.labelOutlinePaint);
                labelBox.setOutlineStroke(this.labelOutlineStroke);
                if (this.shadowGenerator == null) {
                    labelBox.setShadowPaint(this.labelShadowPaint);
                } else {
                    labelBox.setShadowPaint(null);
                }
                labelBox.setInteriorGap(this.labelPadding);
                double theta = Math.toRadians(leftKeys.getValue(i).doubleValue());
                double baseY = state.getPieCenterY() - (Math.sin(theta) * verticalLinkRadius);
                hh = labelBox.getHeight(g2);
                AbstractPieLabelDistributor abstractPieLabelDistributor = this.labelDistributor;
                AbstractPieLabelDistributor abstractPieLabelDistributor2 = abstractPieLabelDistributor;
                abstractPieLabelDistributor2.addPieLabelRecord(new PieLabelRecord(leftKeys.getKey(i), theta, baseY, labelBox, hh, (lGap / DateAxis.DEFAULT_AUTO_RANGE_MINIMUM_SIZE_IN_MILLISECONDS) + ((lGap / DateAxis.DEFAULT_AUTO_RANGE_MINIMUM_SIZE_IN_MILLISECONDS) * (-Math.cos(theta))), (NormalizedMatrixSeries.DEFAULT_SCALE_FACTOR - getLabelLinkDepth()) + getExplodePercent(leftKeys.getKey(i))));
            }
        }
        hh = plotArea.getHeight();
        double gap = hh * getInteriorGap();
        this.labelDistributor.distributeLabels(plotArea.getMinY() + gap, hh - (DateAxis.DEFAULT_AUTO_RANGE_MINIMUM_SIZE_IN_MILLISECONDS * gap));
        for (i = 0; i < this.labelDistributor.getItemCount(); i++) {
            drawLeftLabel(g2, state, this.labelDistributor.getPieLabelRecord(i));
        }
    }

    protected void drawRightLabels(KeyedValues keys, Graphics2D g2, Rectangle2D plotArea, Rectangle2D linkArea, float maxLabelWidth, PiePlotState state) {
        int i;
        this.labelDistributor.clear();
        double lGap = plotArea.getWidth() * this.labelGap;
        double verticalLinkRadius = state.getLinkArea().getHeight() / DateAxis.DEFAULT_AUTO_RANGE_MINIMUM_SIZE_IN_MILLISECONDS;
        for (i = 0; i < keys.getItemCount(); i++) {
            String label = this.labelGenerator.generateSectionLabel(this.dataset, keys.getKey(i));
            if (label != null) {
                TextBox labelBox = new TextBox(TextUtilities.createTextBlock(label, this.labelFont, this.labelPaint, maxLabelWidth, new G2TextMeasurer(g2)));
                labelBox.setBackgroundPaint(this.labelBackgroundPaint);
                labelBox.setOutlinePaint(this.labelOutlinePaint);
                labelBox.setOutlineStroke(this.labelOutlineStroke);
                if (this.shadowGenerator == null) {
                    labelBox.setShadowPaint(this.labelShadowPaint);
                } else {
                    labelBox.setShadowPaint(null);
                }
                labelBox.setInteriorGap(this.labelPadding);
                double theta = Math.toRadians(keys.getValue(i).doubleValue());
                double baseY = state.getPieCenterY() - (Math.sin(theta) * verticalLinkRadius);
                double hh = labelBox.getHeight(g2);
                AbstractPieLabelDistributor abstractPieLabelDistributor = this.labelDistributor;
                AbstractPieLabelDistributor abstractPieLabelDistributor2 = abstractPieLabelDistributor;
                abstractPieLabelDistributor2.addPieLabelRecord(new PieLabelRecord(keys.getKey(i), theta, baseY, labelBox, hh, (lGap / DateAxis.DEFAULT_AUTO_RANGE_MINIMUM_SIZE_IN_MILLISECONDS) + ((lGap / DateAxis.DEFAULT_AUTO_RANGE_MINIMUM_SIZE_IN_MILLISECONDS) * Math.cos(theta)), (NormalizedMatrixSeries.DEFAULT_SCALE_FACTOR - getLabelLinkDepth()) + getExplodePercent(keys.getKey(i))));
            }
        }
        this.labelDistributor.distributeLabels(plotArea.getMinY() + 0.0d, plotArea.getHeight() - (DateAxis.DEFAULT_AUTO_RANGE_MINIMUM_SIZE_IN_MILLISECONDS * 0.0d));
        for (i = 0; i < this.labelDistributor.getItemCount(); i++) {
            drawRightLabel(g2, state, this.labelDistributor.getPieLabelRecord(i));
        }
    }

    public LegendItemCollection getLegendItems() {
        LegendItemCollection result = new LegendItemCollection();
        if (this.dataset != null) {
            List<Comparable> keys = this.dataset.getKeys();
            int section = 0;
            Shape shape = getLegendItemShape();
            for (Comparable key : keys) {
                boolean include;
                Number n = this.dataset.getValue(key);
                if (n != null) {
                    double v = n.doubleValue();
                    if (v == 0.0d) {
                        include = !this.ignoreZeroValues ? true : DEBUG_DRAW_PIE_AREA;
                    } else {
                        include = v > 0.0d ? true : DEBUG_DRAW_PIE_AREA;
                    }
                } else if (this.ignoreNullValues) {
                    include = DEBUG_DRAW_PIE_AREA;
                } else {
                    include = true;
                }
                if (include) {
                    String label = this.legendLabelGenerator.generateSectionLabel(this.dataset, key);
                    if (label != null) {
                        String description = label;
                        String toolTipText = null;
                        if (this.legendLabelToolTipGenerator != null) {
                            toolTipText = this.legendLabelToolTipGenerator.generateSectionLabel(this.dataset, key);
                        }
                        String urlText = null;
                        if (this.legendLabelURLGenerator != null) {
                            urlText = this.legendLabelURLGenerator.generateURL(this.dataset, key, this.pieIndex);
                        }
                        LegendItem item = new LegendItem(label, description, toolTipText, urlText, true, shape, true, lookupSectionPaint(key), true, lookupSectionOutlinePaint(key), lookupSectionOutlineStroke(key), (boolean) DEBUG_DRAW_PIE_AREA, new Float(), new BasicStroke(), Color.black);
                        item.setDataset(getDataset());
                        item.setSeriesIndex(this.dataset.getIndex(key));
                        item.setSeriesKey(key);
                        result.add(item);
                    }
                    section++;
                } else {
                    section++;
                }
            }
        }
        return result;
    }

    public String getPlotType() {
        return localizationResources.getString("Pie_Plot");
    }

    protected Rectangle2D getArcBounds(Rectangle2D unexploded, Rectangle2D exploded, double angle, double extent, double explodePercent) {
        if (explodePercent == 0.0d) {
            return unexploded;
        }
        Point2D point1 = new Arc2D.Double(unexploded, angle, extent / DateAxis.DEFAULT_AUTO_RANGE_MINIMUM_SIZE_IN_MILLISECONDS, 0).getEndPoint();
        Point2D point2 = new Arc2D.Double(exploded, angle, extent / DateAxis.DEFAULT_AUTO_RANGE_MINIMUM_SIZE_IN_MILLISECONDS, 0).getEndPoint();
        return new Double(unexploded.getX() - ((point1.getX() - point2.getX()) * explodePercent), unexploded.getY() - ((point1.getY() - point2.getY()) * explodePercent), unexploded.getWidth(), unexploded.getHeight());
    }

    protected void drawLeftLabel(Graphics2D g2, PiePlotState state, PieLabelRecord record) {
        double anchorX = state.getLinkArea().getMinX();
        double targetX = anchorX - record.getGap();
        double targetY = record.getAllocatedY();
        if (this.labelLinksVisible) {
            double theta = record.getAngle();
            double linkX = state.getPieCenterX() + ((Math.cos(theta) * state.getPieWRadius()) * record.getLinkPercent());
            double linkY = state.getPieCenterY() - ((Math.sin(theta) * state.getPieHRadius()) * record.getLinkPercent());
            double elbowX = state.getPieCenterX() + ((Math.cos(theta) * state.getLinkArea().getWidth()) / DateAxis.DEFAULT_AUTO_RANGE_MINIMUM_SIZE_IN_MILLISECONDS);
            double elbowY = state.getPieCenterY() - ((Math.sin(theta) * state.getLinkArea().getHeight()) / DateAxis.DEFAULT_AUTO_RANGE_MINIMUM_SIZE_IN_MILLISECONDS);
            double anchorY = elbowY;
            g2.setPaint(this.labelLinkPaint);
            g2.setStroke(this.labelLinkStroke);
            PieLabelLinkStyle style = getLabelLinkStyle();
            if (style.equals(PieLabelLinkStyle.STANDARD)) {
                g2.draw(new Line2D.Double(linkX, linkY, elbowX, elbowY));
                g2.draw(new Line2D.Double(anchorX, anchorY, elbowX, elbowY));
                g2.draw(new Line2D.Double(anchorX, anchorY, targetX, targetY));
            } else if (style.equals(PieLabelLinkStyle.QUAD_CURVE)) {
                QuadCurve2D q = new QuadCurve2D.Float();
                q.setCurve(targetX, targetY, anchorX, anchorY, elbowX, elbowY);
                g2.draw(q);
                g2.draw(new Line2D.Double(elbowX, elbowY, linkX, linkY));
            } else if (style.equals(PieLabelLinkStyle.CUBIC_CURVE)) {
                CubicCurve2D c = new CubicCurve2D.Float();
                c.setCurve(targetX, targetY, anchorX, anchorY, elbowX, elbowY, linkX, linkY);
                g2.draw(c);
            }
        }
        record.getLabel().draw(g2, (float) targetX, (float) targetY, RectangleAnchor.RIGHT);
    }

    protected void drawRightLabel(Graphics2D g2, PiePlotState state, PieLabelRecord record) {
        double anchorX = state.getLinkArea().getMaxX();
        double targetX = anchorX + record.getGap();
        double targetY = record.getAllocatedY();
        if (this.labelLinksVisible) {
            double theta = record.getAngle();
            double linkX = state.getPieCenterX() + ((Math.cos(theta) * state.getPieWRadius()) * record.getLinkPercent());
            double linkY = state.getPieCenterY() - ((Math.sin(theta) * state.getPieHRadius()) * record.getLinkPercent());
            double elbowX = state.getPieCenterX() + ((Math.cos(theta) * state.getLinkArea().getWidth()) / DateAxis.DEFAULT_AUTO_RANGE_MINIMUM_SIZE_IN_MILLISECONDS);
            double elbowY = state.getPieCenterY() - ((Math.sin(theta) * state.getLinkArea().getHeight()) / DateAxis.DEFAULT_AUTO_RANGE_MINIMUM_SIZE_IN_MILLISECONDS);
            double anchorY = elbowY;
            g2.setPaint(this.labelLinkPaint);
            g2.setStroke(this.labelLinkStroke);
            PieLabelLinkStyle style = getLabelLinkStyle();
            if (style.equals(PieLabelLinkStyle.STANDARD)) {
                g2.draw(new Line2D.Double(linkX, linkY, elbowX, elbowY));
                g2.draw(new Line2D.Double(anchorX, anchorY, elbowX, elbowY));
                g2.draw(new Line2D.Double(anchorX, anchorY, targetX, targetY));
            } else if (style.equals(PieLabelLinkStyle.QUAD_CURVE)) {
                QuadCurve2D q = new QuadCurve2D.Float();
                q.setCurve(targetX, targetY, anchorX, anchorY, elbowX, elbowY);
                g2.draw(q);
                g2.draw(new Line2D.Double(elbowX, elbowY, linkX, linkY));
            } else if (style.equals(PieLabelLinkStyle.CUBIC_CURVE)) {
                CubicCurve2D c = new CubicCurve2D.Float();
                c.setCurve(targetX, targetY, anchorX, anchorY, elbowX, elbowY, linkX, linkY);
                g2.draw(c);
            }
        }
        record.getLabel().draw(g2, (float) targetX, (float) targetY, RectangleAnchor.LEFT);
    }

    protected Point2D getArcCenter(PiePlotState state, Comparable key) {
        Point2D center = new Point2D.Double(state.getPieCenterX(), state.getPieCenterY());
        double ep = getExplodePercent(key);
        double mep = getMaximumExplodePercent();
        if (mep > 0.0d) {
            ep /= mep;
        }
        if (ep == 0.0d) {
            return center;
        }
        double angle1;
        double angle2;
        Rectangle2D pieArea = state.getPieArea();
        Rectangle2D expPieArea = state.getExplodedPieArea();
        double value = this.dataset.getValue(key).doubleValue();
        if (this.direction == Rotation.CLOCKWISE) {
            angle1 = state.getLatestAngle();
            angle2 = angle1 - ((value / state.getTotal()) * 360.0d);
        } else if (this.direction == Rotation.ANTICLOCKWISE) {
            angle1 = state.getLatestAngle();
            angle2 = angle1 + ((value / state.getTotal()) * 360.0d);
        } else {
            throw new IllegalStateException("Rotation type not recognised.");
        }
        double angle = angle2 - angle1;
        Point2D point1 = new Arc2D.Double(pieArea, angle1, angle / DateAxis.DEFAULT_AUTO_RANGE_MINIMUM_SIZE_IN_MILLISECONDS, 0).getEndPoint();
        Point2D point2 = new Arc2D.Double(expPieArea, angle1, angle / DateAxis.DEFAULT_AUTO_RANGE_MINIMUM_SIZE_IN_MILLISECONDS, 0).getEndPoint();
        return new Point2D.Double(state.getPieCenterX() - ((point1.getX() - point2.getX()) * ep), state.getPieCenterY() - ((point1.getY() - point2.getY()) * ep));
    }

    protected Paint lookupSectionPaint(Comparable key, PiePlotState state) {
        Paint paint = lookupSectionPaint(key, getAutoPopulateSectionPaint());
        if (!(paint instanceof RadialGradientPaint)) {
            return paint;
        }
        RadialGradientPaint rgp = (RadialGradientPaint) paint;
        return new RadialGradientPaint(getArcCenter(state, key), (float) Math.max(state.getPieHRadius(), state.getPieWRadius()), rgp.getFractions(), rgp.getColors());
    }

    public boolean equals(Object obj) {
        if (obj == this) {
            return true;
        }
        if (!(obj instanceof PiePlot)) {
            return DEBUG_DRAW_PIE_AREA;
        }
        if (!super.equals(obj)) {
            return DEBUG_DRAW_PIE_AREA;
        }
        PiePlot that = (PiePlot) obj;
        if (this.pieIndex != that.pieIndex) {
            return DEBUG_DRAW_PIE_AREA;
        }
        if (this.interiorGap != that.interiorGap) {
            return DEBUG_DRAW_PIE_AREA;
        }
        if (this.circular != that.circular) {
            return DEBUG_DRAW_PIE_AREA;
        }
        if (this.startAngle != that.startAngle) {
            return DEBUG_DRAW_PIE_AREA;
        }
        if (this.direction != that.direction) {
            return DEBUG_DRAW_PIE_AREA;
        }
        if (this.ignoreZeroValues != that.ignoreZeroValues) {
            return DEBUG_DRAW_PIE_AREA;
        }
        if (this.ignoreNullValues != that.ignoreNullValues) {
            return DEBUG_DRAW_PIE_AREA;
        }
        if (!PaintUtilities.equal(this.sectionPaint, that.sectionPaint)) {
            return DEBUG_DRAW_PIE_AREA;
        }
        if (!ObjectUtilities.equal(this.sectionPaintMap, that.sectionPaintMap)) {
            return DEBUG_DRAW_PIE_AREA;
        }
        if (!PaintUtilities.equal(this.baseSectionPaint, that.baseSectionPaint)) {
            return DEBUG_DRAW_PIE_AREA;
        }
        if (this.sectionOutlinesVisible != that.sectionOutlinesVisible) {
            return DEBUG_DRAW_PIE_AREA;
        }
        if (!PaintUtilities.equal(this.sectionOutlinePaint, that.sectionOutlinePaint)) {
            return DEBUG_DRAW_PIE_AREA;
        }
        if (!ObjectUtilities.equal(this.sectionOutlinePaintMap, that.sectionOutlinePaintMap)) {
            return DEBUG_DRAW_PIE_AREA;
        }
        if (!PaintUtilities.equal(this.baseSectionOutlinePaint, that.baseSectionOutlinePaint)) {
            return DEBUG_DRAW_PIE_AREA;
        }
        if (!ObjectUtilities.equal(this.sectionOutlineStroke, that.sectionOutlineStroke)) {
            return DEBUG_DRAW_PIE_AREA;
        }
        if (!ObjectUtilities.equal(this.sectionOutlineStrokeMap, that.sectionOutlineStrokeMap)) {
            return DEBUG_DRAW_PIE_AREA;
        }
        if (!ObjectUtilities.equal(this.baseSectionOutlineStroke, that.baseSectionOutlineStroke)) {
            return DEBUG_DRAW_PIE_AREA;
        }
        if (!PaintUtilities.equal(this.shadowPaint, that.shadowPaint)) {
            return DEBUG_DRAW_PIE_AREA;
        }
        if (this.shadowXOffset != that.shadowXOffset) {
            return DEBUG_DRAW_PIE_AREA;
        }
        if (this.shadowYOffset != that.shadowYOffset) {
            return DEBUG_DRAW_PIE_AREA;
        }
        if (!ObjectUtilities.equal(this.explodePercentages, that.explodePercentages)) {
            return DEBUG_DRAW_PIE_AREA;
        }
        if (!ObjectUtilities.equal(this.labelGenerator, that.labelGenerator)) {
            return DEBUG_DRAW_PIE_AREA;
        }
        if (!ObjectUtilities.equal(this.labelFont, that.labelFont)) {
            return DEBUG_DRAW_PIE_AREA;
        }
        if (!PaintUtilities.equal(this.labelPaint, that.labelPaint)) {
            return DEBUG_DRAW_PIE_AREA;
        }
        if (!PaintUtilities.equal(this.labelBackgroundPaint, that.labelBackgroundPaint)) {
            return DEBUG_DRAW_PIE_AREA;
        }
        if (!PaintUtilities.equal(this.labelOutlinePaint, that.labelOutlinePaint)) {
            return DEBUG_DRAW_PIE_AREA;
        }
        if (!ObjectUtilities.equal(this.labelOutlineStroke, that.labelOutlineStroke)) {
            return DEBUG_DRAW_PIE_AREA;
        }
        if (!PaintUtilities.equal(this.labelShadowPaint, that.labelShadowPaint)) {
            return DEBUG_DRAW_PIE_AREA;
        }
        if (this.simpleLabels != that.simpleLabels) {
            return DEBUG_DRAW_PIE_AREA;
        }
        if (!this.simpleLabelOffset.equals(that.simpleLabelOffset)) {
            return DEBUG_DRAW_PIE_AREA;
        }
        if (!this.labelPadding.equals(that.labelPadding)) {
            return DEBUG_DRAW_PIE_AREA;
        }
        if (this.maximumLabelWidth != that.maximumLabelWidth) {
            return DEBUG_DRAW_PIE_AREA;
        }
        if (this.labelGap != that.labelGap) {
            return DEBUG_DRAW_PIE_AREA;
        }
        if (this.labelLinkMargin != that.labelLinkMargin) {
            return DEBUG_DRAW_PIE_AREA;
        }
        if (this.labelLinksVisible != that.labelLinksVisible) {
            return DEBUG_DRAW_PIE_AREA;
        }
        if (!this.labelLinkStyle.equals(that.labelLinkStyle)) {
            return DEBUG_DRAW_PIE_AREA;
        }
        if (!PaintUtilities.equal(this.labelLinkPaint, that.labelLinkPaint)) {
            return DEBUG_DRAW_PIE_AREA;
        }
        if (!ObjectUtilities.equal(this.labelLinkStroke, that.labelLinkStroke)) {
            return DEBUG_DRAW_PIE_AREA;
        }
        if (!ObjectUtilities.equal(this.toolTipGenerator, that.toolTipGenerator)) {
            return DEBUG_DRAW_PIE_AREA;
        }
        if (!ObjectUtilities.equal(this.urlGenerator, that.urlGenerator)) {
            return DEBUG_DRAW_PIE_AREA;
        }
        if (this.minimumArcAngleToDraw != that.minimumArcAngleToDraw) {
            return DEBUG_DRAW_PIE_AREA;
        }
        if (!ShapeUtilities.equal(this.legendItemShape, that.legendItemShape)) {
            return DEBUG_DRAW_PIE_AREA;
        }
        if (!ObjectUtilities.equal(this.legendLabelGenerator, that.legendLabelGenerator)) {
            return DEBUG_DRAW_PIE_AREA;
        }
        if (!ObjectUtilities.equal(this.legendLabelToolTipGenerator, that.legendLabelToolTipGenerator)) {
            return DEBUG_DRAW_PIE_AREA;
        }
        if (!ObjectUtilities.equal(this.legendLabelURLGenerator, that.legendLabelURLGenerator)) {
            return DEBUG_DRAW_PIE_AREA;
        }
        if (this.autoPopulateSectionPaint != that.autoPopulateSectionPaint) {
            return DEBUG_DRAW_PIE_AREA;
        }
        if (this.autoPopulateSectionOutlinePaint != that.autoPopulateSectionOutlinePaint) {
            return DEBUG_DRAW_PIE_AREA;
        }
        if (this.autoPopulateSectionOutlineStroke != that.autoPopulateSectionOutlineStroke) {
            return DEBUG_DRAW_PIE_AREA;
        }
        if (ObjectUtilities.equal(this.shadowGenerator, that.shadowGenerator)) {
            return true;
        }
        return DEBUG_DRAW_PIE_AREA;
    }

    public Object clone() throws CloneNotSupportedException {
        PiePlot clone = (PiePlot) super.clone();
        clone.sectionPaintMap = (PaintMap) this.sectionPaintMap.clone();
        clone.sectionOutlinePaintMap = (PaintMap) this.sectionOutlinePaintMap.clone();
        clone.sectionOutlineStrokeMap = (StrokeMap) this.sectionOutlineStrokeMap.clone();
        clone.explodePercentages = new TreeMap(this.explodePercentages);
        if (this.labelGenerator != null) {
            clone.labelGenerator = (PieSectionLabelGenerator) ObjectUtilities.clone(this.labelGenerator);
        }
        if (clone.dataset != null) {
            clone.dataset.addChangeListener(clone);
        }
        if (this.urlGenerator instanceof PublicCloneable) {
            clone.urlGenerator = (PieURLGenerator) ObjectUtilities.clone(this.urlGenerator);
        }
        clone.legendItemShape = ShapeUtilities.clone(this.legendItemShape);
        if (this.legendLabelGenerator != null) {
            clone.legendLabelGenerator = (PieSectionLabelGenerator) ObjectUtilities.clone(this.legendLabelGenerator);
        }
        if (this.legendLabelToolTipGenerator != null) {
            clone.legendLabelToolTipGenerator = (PieSectionLabelGenerator) ObjectUtilities.clone(this.legendLabelToolTipGenerator);
        }
        if (this.legendLabelURLGenerator instanceof PublicCloneable) {
            clone.legendLabelURLGenerator = (PieURLGenerator) ObjectUtilities.clone(this.legendLabelURLGenerator);
        }
        return clone;
    }

    private void writeObject(ObjectOutputStream stream) throws IOException {
        stream.defaultWriteObject();
        SerialUtilities.writePaint(this.sectionPaint, stream);
        SerialUtilities.writePaint(this.baseSectionPaint, stream);
        SerialUtilities.writePaint(this.sectionOutlinePaint, stream);
        SerialUtilities.writePaint(this.baseSectionOutlinePaint, stream);
        SerialUtilities.writeStroke(this.sectionOutlineStroke, stream);
        SerialUtilities.writeStroke(this.baseSectionOutlineStroke, stream);
        SerialUtilities.writePaint(this.shadowPaint, stream);
        SerialUtilities.writePaint(this.labelPaint, stream);
        SerialUtilities.writePaint(this.labelBackgroundPaint, stream);
        SerialUtilities.writePaint(this.labelOutlinePaint, stream);
        SerialUtilities.writeStroke(this.labelOutlineStroke, stream);
        SerialUtilities.writePaint(this.labelShadowPaint, stream);
        SerialUtilities.writePaint(this.labelLinkPaint, stream);
        SerialUtilities.writeStroke(this.labelLinkStroke, stream);
        SerialUtilities.writeShape(this.legendItemShape, stream);
    }

    private void readObject(ObjectInputStream stream) throws IOException, ClassNotFoundException {
        stream.defaultReadObject();
        this.sectionPaint = SerialUtilities.readPaint(stream);
        this.baseSectionPaint = SerialUtilities.readPaint(stream);
        this.sectionOutlinePaint = SerialUtilities.readPaint(stream);
        this.baseSectionOutlinePaint = SerialUtilities.readPaint(stream);
        this.sectionOutlineStroke = SerialUtilities.readStroke(stream);
        this.baseSectionOutlineStroke = SerialUtilities.readStroke(stream);
        this.shadowPaint = SerialUtilities.readPaint(stream);
        this.labelPaint = SerialUtilities.readPaint(stream);
        this.labelBackgroundPaint = SerialUtilities.readPaint(stream);
        this.labelOutlinePaint = SerialUtilities.readPaint(stream);
        this.labelOutlineStroke = SerialUtilities.readStroke(stream);
        this.labelShadowPaint = SerialUtilities.readPaint(stream);
        this.labelLinkPaint = SerialUtilities.readPaint(stream);
        this.labelLinkStroke = SerialUtilities.readStroke(stream);
        this.legendItemShape = SerialUtilities.readShape(stream);
    }

    public Paint getSectionPaint(int section) {
        return getSectionPaint(getSectionKey(section));
    }

    public void setSectionPaint(int section, Paint paint) {
        setSectionPaint(getSectionKey(section), paint);
    }

    public Paint getSectionOutlinePaint() {
        return this.sectionOutlinePaint;
    }

    public void setSectionOutlinePaint(Paint paint) {
        this.sectionOutlinePaint = paint;
        fireChangeEvent();
    }

    public Paint getSectionOutlinePaint(int section) {
        return getSectionOutlinePaint(getSectionKey(section));
    }

    public void setSectionOutlinePaint(int section, Paint paint) {
        setSectionOutlinePaint(getSectionKey(section), paint);
    }

    public Stroke getSectionOutlineStroke() {
        return this.sectionOutlineStroke;
    }

    public void setSectionOutlineStroke(Stroke stroke) {
        this.sectionOutlineStroke = stroke;
        fireChangeEvent();
    }

    public Stroke getSectionOutlineStroke(int section) {
        return getSectionOutlineStroke(getSectionKey(section));
    }

    public void setSectionOutlineStroke(int section, Stroke stroke) {
        setSectionOutlineStroke(getSectionKey(section), stroke);
    }

    public double getExplodePercent(int section) {
        return getExplodePercent(getSectionKey(section));
    }

    public void setExplodePercent(int section, double percent) {
        setExplodePercent(getSectionKey(section), percent);
    }
}
