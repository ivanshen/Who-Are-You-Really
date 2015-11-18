package org.jfree.chart.plot;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.Paint;
import java.awt.Shape;
import java.awt.Stroke;
import java.awt.geom.Arc2D.Double;
import java.awt.geom.GeneralPath;
import java.awt.geom.Line2D;
import java.awt.geom.Rectangle2D;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.text.DecimalFormat;
import java.text.Format;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.DateAxis;
import org.jfree.chart.entity.EntityCollection;
import org.jfree.chart.entity.PieSectionEntity;
import org.jfree.chart.labels.PieToolTipGenerator;
import org.jfree.chart.renderer.category.LevelRenderer;
import org.jfree.chart.urls.PieURLGenerator;
import org.jfree.chart.util.LineUtilities;
import org.jfree.chart.util.ParamChecks;
import org.jfree.data.general.PieDataset;
import org.jfree.io.SerialUtilities;
import org.jfree.text.TextUtilities;
import org.jfree.ui.RectangleInsets;
import org.jfree.ui.TextAnchor;
import org.jfree.util.ObjectUtilities;
import org.jfree.util.PaintUtilities;
import org.jfree.util.Rotation;
import org.jfree.util.ShapeUtilities;
import org.jfree.util.UnitType;

public class RingPlot extends PiePlot implements Cloneable, Serializable {
    private static final long serialVersionUID = 1556064784129676620L;
    private String centerText;
    private Color centerTextColor;
    private Font centerTextFont;
    private Format centerTextFormatter;
    private CenterTextMode centerTextMode;
    private double innerSeparatorExtension;
    private double outerSeparatorExtension;
    private double sectionDepth;
    private transient Paint separatorPaint;
    private transient Stroke separatorStroke;
    private boolean separatorsVisible;

    public RingPlot() {
        this(null);
    }

    public RingPlot(PieDataset dataset) {
        super(dataset);
        this.centerTextMode = CenterTextMode.NONE;
        this.centerTextFormatter = new DecimalFormat("0.00");
        this.centerTextMode = CenterTextMode.NONE;
        this.centerText = null;
        this.centerTextFormatter = new DecimalFormat("0.00");
        this.centerTextFont = DEFAULT_LABEL_FONT;
        this.centerTextColor = Color.BLACK;
        this.separatorsVisible = true;
        this.separatorStroke = new BasicStroke(JFreeChart.DEFAULT_BACKGROUND_IMAGE_ALPHA);
        this.separatorPaint = Color.gray;
        this.innerSeparatorExtension = LevelRenderer.DEFAULT_ITEM_MARGIN;
        this.outerSeparatorExtension = LevelRenderer.DEFAULT_ITEM_MARGIN;
        this.sectionDepth = LevelRenderer.DEFAULT_ITEM_MARGIN;
    }

    public CenterTextMode getCenterTextMode() {
        return this.centerTextMode;
    }

    public void setCenterTextMode(CenterTextMode mode) {
        ParamChecks.nullNotPermitted(mode, "mode");
        this.centerTextMode = mode;
        fireChangeEvent();
    }

    public String getCenterText() {
        return this.centerText;
    }

    public void setCenterText(String text) {
        this.centerText = text;
        fireChangeEvent();
    }

    public Format getCenterTextFormatter() {
        return this.centerTextFormatter;
    }

    public void setCenterTextFormatter(Format formatter) {
        ParamChecks.nullNotPermitted(formatter, "formatter");
        this.centerTextFormatter = formatter;
    }

    public Font getCenterTextFont() {
        return this.centerTextFont;
    }

    public void setCenterTextFont(Font font) {
        ParamChecks.nullNotPermitted(font, "font");
        this.centerTextFont = font;
        fireChangeEvent();
    }

    public Color getCenterTextColor() {
        return this.centerTextColor;
    }

    public void setCenterTextColor(Color color) {
        ParamChecks.nullNotPermitted(color, "color");
        this.centerTextColor = color;
        fireChangeEvent();
    }

    public boolean getSeparatorsVisible() {
        return this.separatorsVisible;
    }

    public void setSeparatorsVisible(boolean visible) {
        this.separatorsVisible = visible;
        fireChangeEvent();
    }

    public Stroke getSeparatorStroke() {
        return this.separatorStroke;
    }

    public void setSeparatorStroke(Stroke stroke) {
        ParamChecks.nullNotPermitted(stroke, "stroke");
        this.separatorStroke = stroke;
        fireChangeEvent();
    }

    public Paint getSeparatorPaint() {
        return this.separatorPaint;
    }

    public void setSeparatorPaint(Paint paint) {
        ParamChecks.nullNotPermitted(paint, "paint");
        this.separatorPaint = paint;
        fireChangeEvent();
    }

    public double getInnerSeparatorExtension() {
        return this.innerSeparatorExtension;
    }

    public void setInnerSeparatorExtension(double percent) {
        this.innerSeparatorExtension = percent;
        fireChangeEvent();
    }

    public double getOuterSeparatorExtension() {
        return this.outerSeparatorExtension;
    }

    public void setOuterSeparatorExtension(double percent) {
        this.outerSeparatorExtension = percent;
        fireChangeEvent();
    }

    public double getSectionDepth() {
        return this.sectionDepth;
    }

    public void setSectionDepth(double sectionDepth) {
        this.sectionDepth = sectionDepth;
        fireChangeEvent();
    }

    public PiePlotState initialise(Graphics2D g2, Rectangle2D plotArea, PiePlot plot, Integer index, PlotRenderingInfo info) {
        PiePlotState state = super.initialise(g2, plotArea, plot, index, info);
        state.setPassesRequired(3);
        return state;
    }

    protected void drawItem(Graphics2D g2, int section, Rectangle2D dataArea, PiePlotState state, int currentPass) {
        PieDataset dataset = getDataset();
        Number n = dataset.getValue(section);
        if (n != null) {
            double angle1;
            double angle2;
            double value = n.doubleValue();
            Rotation direction = getDirection();
            if (direction == Rotation.CLOCKWISE) {
                angle1 = state.getLatestAngle();
                angle2 = angle1 - ((value / state.getTotal()) * 360.0d);
            } else if (direction == Rotation.ANTICLOCKWISE) {
                angle1 = state.getLatestAngle();
                angle2 = angle1 + ((value / state.getTotal()) * 360.0d);
            } else {
                throw new IllegalStateException("Rotation type not recognised.");
            }
            double angle = angle2 - angle1;
            if (Math.abs(angle) > getMinimumArcAngleToDraw()) {
                Comparable key = getSectionKey(section);
                double ep = 0.0d;
                double mep = getMaximumExplodePercent();
                if (mep > 0.0d) {
                    ep = getExplodePercent(key) / mep;
                }
                Rectangle2D arcBounds = getArcBounds(state.getPieArea(), state.getExplodedPieArea(), angle1, angle, ep);
                Double arc = new Double(arcBounds, angle1, angle, 0);
                double depth = this.sectionDepth / DateAxis.DEFAULT_AUTO_RANGE_MINIMUM_SIZE_IN_MILLISECONDS;
                RectangleInsets s = new RectangleInsets(UnitType.RELATIVE, depth, depth, depth, depth);
                Rectangle2D innerArcBounds = new Rectangle2D.Double();
                innerArcBounds.setRect(arcBounds);
                s.trim(innerArcBounds);
                Double arc2 = new Double(innerArcBounds, angle1 + angle, -angle, 0);
                GeneralPath path = new GeneralPath();
                path.moveTo((float) arc.getStartPoint().getX(), (float) arc.getStartPoint().getY());
                path.append(arc.getPathIterator(null), false);
                path.append(arc2.getPathIterator(null), true);
                path.closePath();
                Line2D.Double doubleR = new Line2D.Double(arc2.getEndPoint(), arc.getStartPoint());
                if (currentPass == 0) {
                    Paint shadowPaint = getShadowPaint();
                    double shadowXOffset = getShadowXOffset();
                    double shadowYOffset = getShadowYOffset();
                    if (shadowPaint != null && getShadowGenerator() == null) {
                        Shape shadowArc = ShapeUtilities.createTranslatedShape(path, (double) ((float) shadowXOffset), (double) ((float) shadowYOffset));
                        g2.setPaint(shadowPaint);
                        g2.fill(shadowArc);
                    }
                } else if (currentPass == 1) {
                    g2.setPaint(lookupSectionPaint(key));
                    g2.fill(path);
                    Paint outlinePaint = lookupSectionOutlinePaint(key);
                    Stroke outlineStroke = lookupSectionOutlineStroke(key);
                    if (!(!getSectionOutlinesVisible() || outlinePaint == null || outlineStroke == null)) {
                        g2.setPaint(outlinePaint);
                        g2.setStroke(outlineStroke);
                        g2.draw(path);
                    }
                    if (section == 0) {
                        String nstr = null;
                        if (this.centerTextMode.equals(CenterTextMode.VALUE)) {
                            nstr = this.centerTextFormatter.format(n);
                        } else if (this.centerTextMode.equals(CenterTextMode.FIXED)) {
                            nstr = this.centerText;
                        }
                        if (nstr != null) {
                            g2.setFont(this.centerTextFont);
                            g2.setPaint(this.centerTextColor);
                            TextUtilities.drawAlignedString(nstr, g2, (float) dataArea.getCenterX(), (float) dataArea.getCenterY(), TextAnchor.CENTER);
                        }
                    }
                    if (state.getInfo() != null) {
                        EntityCollection entities = state.getEntityCollection();
                        if (entities != null) {
                            String tip = null;
                            PieToolTipGenerator toolTipGenerator = getToolTipGenerator();
                            if (toolTipGenerator != null) {
                                tip = toolTipGenerator.generateToolTip(dataset, key);
                            }
                            String url = null;
                            PieURLGenerator urlGenerator = getURLGenerator();
                            if (urlGenerator != null) {
                                url = urlGenerator.generateURL(dataset, key, getPieIndex());
                            }
                            entities.add(new PieSectionEntity(path, dataset, getPieIndex(), section, key, tip, url));
                        }
                    }
                } else if (currentPass == 2 && this.separatorsVisible) {
                    Line2D extendedSeparator = LineUtilities.extendLine(doubleR, this.innerSeparatorExtension, this.outerSeparatorExtension);
                    g2.setStroke(this.separatorStroke);
                    g2.setPaint(this.separatorPaint);
                    g2.draw(extendedSeparator);
                }
            }
            state.setLatestAngle(angle2);
        }
    }

    protected double getLabelLinkDepth() {
        return Math.min(super.getLabelLinkDepth(), getSectionDepth() / DateAxis.DEFAULT_AUTO_RANGE_MINIMUM_SIZE_IN_MILLISECONDS);
    }

    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (!(obj instanceof RingPlot)) {
            return false;
        }
        RingPlot that = (RingPlot) obj;
        if (this.centerTextMode.equals(that.centerTextMode) && ObjectUtilities.equal(this.centerText, that.centerText) && this.centerTextFormatter.equals(that.centerTextFormatter) && this.centerTextFont.equals(that.centerTextFont) && this.centerTextColor.equals(that.centerTextColor) && this.separatorsVisible == that.separatorsVisible && ObjectUtilities.equal(this.separatorStroke, that.separatorStroke) && PaintUtilities.equal(this.separatorPaint, that.separatorPaint) && this.innerSeparatorExtension == that.innerSeparatorExtension && this.outerSeparatorExtension == that.outerSeparatorExtension && this.sectionDepth == that.sectionDepth) {
            return super.equals(obj);
        }
        return false;
    }

    private void writeObject(ObjectOutputStream stream) throws IOException {
        stream.defaultWriteObject();
        SerialUtilities.writeStroke(this.separatorStroke, stream);
        SerialUtilities.writePaint(this.separatorPaint, stream);
    }

    private void readObject(ObjectInputStream stream) throws IOException, ClassNotFoundException {
        stream.defaultReadObject();
        this.separatorStroke = SerialUtilities.readStroke(stream);
        this.separatorPaint = SerialUtilities.readPaint(stream);
    }
}
