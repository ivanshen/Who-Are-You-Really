package org.jfree.chart.axis;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.Paint;
import java.awt.RenderingHints;
import java.awt.Stroke;
import java.awt.font.TextLayout;
import java.awt.geom.AffineTransform;
import java.awt.geom.Line2D;
import java.awt.geom.Rectangle2D;
import java.awt.geom.Rectangle2D.Double;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.text.AttributedString;
import java.util.Arrays;
import java.util.EventListener;
import java.util.List;
import javax.swing.event.EventListenerList;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.entity.AxisEntity;
import org.jfree.chart.entity.EntityCollection;
import org.jfree.chart.event.AxisChangeEvent;
import org.jfree.chart.event.AxisChangeListener;
import org.jfree.chart.plot.Plot;
import org.jfree.chart.plot.PlotRenderingInfo;
import org.jfree.chart.renderer.category.BarRenderer;
import org.jfree.chart.util.AttrStringUtils;
import org.jfree.chart.util.ParamChecks;
import org.jfree.io.SerialUtilities;
import org.jfree.text.TextUtilities;
import org.jfree.ui.RectangleEdge;
import org.jfree.ui.RectangleInsets;
import org.jfree.ui.TextAnchor;
import org.jfree.util.AttributedStringUtilities;
import org.jfree.util.ObjectUtilities;
import org.jfree.util.PaintUtilities;

public abstract class Axis implements Cloneable, Serializable {
    public static final Font DEFAULT_AXIS_LABEL_FONT;
    public static final RectangleInsets DEFAULT_AXIS_LABEL_INSETS;
    public static final Paint DEFAULT_AXIS_LABEL_PAINT;
    public static final Paint DEFAULT_AXIS_LINE_PAINT;
    public static final Stroke DEFAULT_AXIS_LINE_STROKE;
    public static final boolean DEFAULT_AXIS_VISIBLE = true;
    public static final boolean DEFAULT_TICK_LABELS_VISIBLE = true;
    public static final Font DEFAULT_TICK_LABEL_FONT;
    public static final RectangleInsets DEFAULT_TICK_LABEL_INSETS;
    public static final Paint DEFAULT_TICK_LABEL_PAINT;
    public static final boolean DEFAULT_TICK_MARKS_VISIBLE = true;
    public static final float DEFAULT_TICK_MARK_INSIDE_LENGTH = 0.0f;
    public static final float DEFAULT_TICK_MARK_OUTSIDE_LENGTH = 2.0f;
    public static final Paint DEFAULT_TICK_MARK_PAINT;
    public static final Stroke DEFAULT_TICK_MARK_STROKE;
    private static final long serialVersionUID = 7719289504573298271L;
    private transient AttributedString attributedLabel;
    private transient Paint axisLinePaint;
    private transient Stroke axisLineStroke;
    private boolean axisLineVisible;
    private double fixedDimension;
    private String label;
    private double labelAngle;
    private Font labelFont;
    private RectangleInsets labelInsets;
    private AxisLabelLocation labelLocation;
    private transient Paint labelPaint;
    private transient EventListenerList listenerList;
    private float minorTickMarkInsideLength;
    private float minorTickMarkOutsideLength;
    private boolean minorTickMarksVisible;
    private transient Plot plot;
    private Font tickLabelFont;
    private RectangleInsets tickLabelInsets;
    private transient Paint tickLabelPaint;
    private boolean tickLabelsVisible;
    private float tickMarkInsideLength;
    private float tickMarkOutsideLength;
    private transient Paint tickMarkPaint;
    private transient Stroke tickMarkStroke;
    private boolean tickMarksVisible;
    private boolean visible;

    public abstract void configure();

    public abstract AxisState draw(Graphics2D graphics2D, double d, Rectangle2D rectangle2D, Rectangle2D rectangle2D2, RectangleEdge rectangleEdge, PlotRenderingInfo plotRenderingInfo);

    public abstract List refreshTicks(Graphics2D graphics2D, AxisState axisState, Rectangle2D rectangle2D, RectangleEdge rectangleEdge);

    public abstract AxisSpace reserveSpace(Graphics2D graphics2D, Plot plot, Rectangle2D rectangle2D, RectangleEdge rectangleEdge, AxisSpace axisSpace);

    static {
        DEFAULT_AXIS_LABEL_FONT = new Font("SansSerif", 0, 12);
        DEFAULT_AXIS_LABEL_PAINT = Color.black;
        DEFAULT_AXIS_LABEL_INSETS = new RectangleInsets(BarRenderer.BAR_OUTLINE_WIDTH_THRESHOLD, BarRenderer.BAR_OUTLINE_WIDTH_THRESHOLD, BarRenderer.BAR_OUTLINE_WIDTH_THRESHOLD, BarRenderer.BAR_OUTLINE_WIDTH_THRESHOLD);
        DEFAULT_AXIS_LINE_PAINT = Color.gray;
        DEFAULT_AXIS_LINE_STROKE = new BasicStroke(JFreeChart.DEFAULT_BACKGROUND_IMAGE_ALPHA);
        DEFAULT_TICK_LABEL_FONT = new Font("SansSerif", 0, 10);
        DEFAULT_TICK_LABEL_PAINT = Color.black;
        DEFAULT_TICK_LABEL_INSETS = new RectangleInsets(DateAxis.DEFAULT_AUTO_RANGE_MINIMUM_SIZE_IN_MILLISECONDS, 4.0d, DateAxis.DEFAULT_AUTO_RANGE_MINIMUM_SIZE_IN_MILLISECONDS, 4.0d);
        DEFAULT_TICK_MARK_STROKE = new BasicStroke(JFreeChart.DEFAULT_BACKGROUND_IMAGE_ALPHA);
        DEFAULT_TICK_MARK_PAINT = Color.gray;
    }

    protected Axis(String label) {
        this.label = label;
        this.visible = DEFAULT_TICK_MARKS_VISIBLE;
        this.labelFont = DEFAULT_AXIS_LABEL_FONT;
        this.labelPaint = DEFAULT_AXIS_LABEL_PAINT;
        this.labelInsets = DEFAULT_AXIS_LABEL_INSETS;
        this.labelAngle = 0.0d;
        this.labelLocation = AxisLabelLocation.MIDDLE;
        this.axisLineVisible = DEFAULT_TICK_MARKS_VISIBLE;
        this.axisLinePaint = DEFAULT_AXIS_LINE_PAINT;
        this.axisLineStroke = DEFAULT_AXIS_LINE_STROKE;
        this.tickLabelsVisible = DEFAULT_TICK_MARKS_VISIBLE;
        this.tickLabelFont = DEFAULT_TICK_LABEL_FONT;
        this.tickLabelPaint = DEFAULT_TICK_LABEL_PAINT;
        this.tickLabelInsets = DEFAULT_TICK_LABEL_INSETS;
        this.tickMarksVisible = DEFAULT_TICK_MARKS_VISIBLE;
        this.tickMarkStroke = DEFAULT_TICK_MARK_STROKE;
        this.tickMarkPaint = DEFAULT_TICK_MARK_PAINT;
        this.tickMarkInsideLength = DEFAULT_TICK_MARK_INSIDE_LENGTH;
        this.tickMarkOutsideLength = DEFAULT_TICK_MARK_OUTSIDE_LENGTH;
        this.minorTickMarksVisible = false;
        this.minorTickMarkInsideLength = DEFAULT_TICK_MARK_INSIDE_LENGTH;
        this.minorTickMarkOutsideLength = DEFAULT_TICK_MARK_OUTSIDE_LENGTH;
        this.plot = null;
        this.listenerList = new EventListenerList();
    }

    public boolean isVisible() {
        return this.visible;
    }

    public void setVisible(boolean flag) {
        if (flag != this.visible) {
            this.visible = flag;
            fireChangeEvent();
        }
    }

    public String getLabel() {
        return this.label;
    }

    public void setLabel(String label) {
        this.label = label;
        fireChangeEvent();
    }

    public AttributedString getAttributedLabel() {
        if (this.attributedLabel != null) {
            return new AttributedString(this.attributedLabel.getIterator());
        }
        return null;
    }

    public void setAttributedLabel(String label) {
        setAttributedLabel(createAttributedLabel(label));
    }

    public void setAttributedLabel(AttributedString label) {
        if (label != null) {
            this.attributedLabel = new AttributedString(label.getIterator());
        } else {
            this.attributedLabel = null;
        }
        fireChangeEvent();
    }

    public AttributedString createAttributedLabel(String label) {
        if (label == null) {
            return null;
        }
        AttributedString s = new AttributedString(label);
        s.addAttributes(this.labelFont.getAttributes(), 0, label.length());
        return s;
    }

    public Font getLabelFont() {
        return this.labelFont;
    }

    public void setLabelFont(Font font) {
        ParamChecks.nullNotPermitted(font, "font");
        if (!this.labelFont.equals(font)) {
            this.labelFont = font;
            fireChangeEvent();
        }
    }

    public Paint getLabelPaint() {
        return this.labelPaint;
    }

    public void setLabelPaint(Paint paint) {
        ParamChecks.nullNotPermitted(paint, "paint");
        this.labelPaint = paint;
        fireChangeEvent();
    }

    public RectangleInsets getLabelInsets() {
        return this.labelInsets;
    }

    public void setLabelInsets(RectangleInsets insets) {
        setLabelInsets(insets, DEFAULT_TICK_MARKS_VISIBLE);
    }

    public void setLabelInsets(RectangleInsets insets, boolean notify) {
        ParamChecks.nullNotPermitted(insets, "insets");
        if (!insets.equals(this.labelInsets)) {
            this.labelInsets = insets;
            if (notify) {
                fireChangeEvent();
            }
        }
    }

    public double getLabelAngle() {
        return this.labelAngle;
    }

    public void setLabelAngle(double angle) {
        this.labelAngle = angle;
        fireChangeEvent();
    }

    public AxisLabelLocation getLabelLocation() {
        return this.labelLocation;
    }

    public void setLabelLocation(AxisLabelLocation location) {
        ParamChecks.nullNotPermitted(location, "location");
        this.labelLocation = location;
        fireChangeEvent();
    }

    public boolean isAxisLineVisible() {
        return this.axisLineVisible;
    }

    public void setAxisLineVisible(boolean visible) {
        this.axisLineVisible = visible;
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

    public boolean isTickLabelsVisible() {
        return this.tickLabelsVisible;
    }

    public void setTickLabelsVisible(boolean flag) {
        if (flag != this.tickLabelsVisible) {
            this.tickLabelsVisible = flag;
            fireChangeEvent();
        }
    }

    public boolean isMinorTickMarksVisible() {
        return this.minorTickMarksVisible;
    }

    public void setMinorTickMarksVisible(boolean flag) {
        if (flag != this.minorTickMarksVisible) {
            this.minorTickMarksVisible = flag;
            fireChangeEvent();
        }
    }

    public Font getTickLabelFont() {
        return this.tickLabelFont;
    }

    public void setTickLabelFont(Font font) {
        ParamChecks.nullNotPermitted(font, "font");
        if (!this.tickLabelFont.equals(font)) {
            this.tickLabelFont = font;
            fireChangeEvent();
        }
    }

    public Paint getTickLabelPaint() {
        return this.tickLabelPaint;
    }

    public void setTickLabelPaint(Paint paint) {
        ParamChecks.nullNotPermitted(paint, "paint");
        this.tickLabelPaint = paint;
        fireChangeEvent();
    }

    public RectangleInsets getTickLabelInsets() {
        return this.tickLabelInsets;
    }

    public void setTickLabelInsets(RectangleInsets insets) {
        ParamChecks.nullNotPermitted(insets, "insets");
        if (!this.tickLabelInsets.equals(insets)) {
            this.tickLabelInsets = insets;
            fireChangeEvent();
        }
    }

    public boolean isTickMarksVisible() {
        return this.tickMarksVisible;
    }

    public void setTickMarksVisible(boolean flag) {
        if (flag != this.tickMarksVisible) {
            this.tickMarksVisible = flag;
            fireChangeEvent();
        }
    }

    public float getTickMarkInsideLength() {
        return this.tickMarkInsideLength;
    }

    public void setTickMarkInsideLength(float length) {
        this.tickMarkInsideLength = length;
        fireChangeEvent();
    }

    public float getTickMarkOutsideLength() {
        return this.tickMarkOutsideLength;
    }

    public void setTickMarkOutsideLength(float length) {
        this.tickMarkOutsideLength = length;
        fireChangeEvent();
    }

    public Stroke getTickMarkStroke() {
        return this.tickMarkStroke;
    }

    public void setTickMarkStroke(Stroke stroke) {
        ParamChecks.nullNotPermitted(stroke, "stroke");
        if (!this.tickMarkStroke.equals(stroke)) {
            this.tickMarkStroke = stroke;
            fireChangeEvent();
        }
    }

    public Paint getTickMarkPaint() {
        return this.tickMarkPaint;
    }

    public void setTickMarkPaint(Paint paint) {
        ParamChecks.nullNotPermitted(paint, "paint");
        this.tickMarkPaint = paint;
        fireChangeEvent();
    }

    public float getMinorTickMarkInsideLength() {
        return this.minorTickMarkInsideLength;
    }

    public void setMinorTickMarkInsideLength(float length) {
        this.minorTickMarkInsideLength = length;
        fireChangeEvent();
    }

    public float getMinorTickMarkOutsideLength() {
        return this.minorTickMarkOutsideLength;
    }

    public void setMinorTickMarkOutsideLength(float length) {
        this.minorTickMarkOutsideLength = length;
        fireChangeEvent();
    }

    public Plot getPlot() {
        return this.plot;
    }

    public void setPlot(Plot plot) {
        this.plot = plot;
        configure();
    }

    public double getFixedDimension() {
        return this.fixedDimension;
    }

    public void setFixedDimension(double dimension) {
        this.fixedDimension = dimension;
    }

    protected void createAndAddEntity(double cursor, AxisState state, Rectangle2D dataArea, RectangleEdge edge, PlotRenderingInfo plotState) {
        if (plotState != null && plotState.getOwner() != null) {
            Rectangle2D hotspot = null;
            if (edge.equals(RectangleEdge.TOP)) {
                hotspot = new Double(dataArea.getX(), state.getCursor(), dataArea.getWidth(), cursor - state.getCursor());
            } else {
                if (edge.equals(RectangleEdge.BOTTOM)) {
                    hotspot = new Double(dataArea.getX(), cursor, dataArea.getWidth(), state.getCursor() - cursor);
                } else {
                    if (edge.equals(RectangleEdge.LEFT)) {
                        hotspot = new Double(state.getCursor(), dataArea.getY(), cursor - state.getCursor(), dataArea.getHeight());
                    } else {
                        if (edge.equals(RectangleEdge.RIGHT)) {
                            hotspot = new Double(cursor, dataArea.getY(), state.getCursor() - cursor, dataArea.getHeight());
                        }
                    }
                }
            }
            EntityCollection e = plotState.getOwner().getEntityCollection();
            if (e != null) {
                e.add(new AxisEntity(hotspot, this));
            }
        }
    }

    public void addChangeListener(AxisChangeListener listener) {
        this.listenerList.add(AxisChangeListener.class, listener);
    }

    public void removeChangeListener(AxisChangeListener listener) {
        this.listenerList.remove(AxisChangeListener.class, listener);
    }

    public boolean hasListener(EventListener listener) {
        return Arrays.asList(this.listenerList.getListenerList()).contains(listener);
    }

    protected void notifyListeners(AxisChangeEvent event) {
        Object[] listeners = this.listenerList.getListenerList();
        for (int i = listeners.length - 2; i >= 0; i -= 2) {
            if (listeners[i] == AxisChangeListener.class) {
                ((AxisChangeListener) listeners[i + 1]).axisChanged(event);
            }
        }
    }

    protected void fireChangeEvent() {
        notifyListeners(new AxisChangeEvent(this));
    }

    protected Rectangle2D getLabelEnclosure(Graphics2D g2, RectangleEdge edge) {
        Rectangle2D result = new Double();
        Rectangle2D bounds = null;
        if (this.attributedLabel != null) {
            bounds = new TextLayout(this.attributedLabel.getIterator(), g2.getFontRenderContext()).getBounds();
        } else {
            String axisLabel = getLabel();
            if (axisLabel != null) {
                if (!axisLabel.equals("")) {
                    bounds = TextUtilities.getTextBounds(axisLabel, g2, g2.getFontMetrics(getLabelFont()));
                }
            }
        }
        if (bounds == null) {
            return result;
        }
        bounds = getLabelInsets().createOutsetRectangle(bounds);
        double angle = getLabelAngle();
        if (edge == RectangleEdge.LEFT || edge == RectangleEdge.RIGHT) {
            angle -= 1.5707963267948966d;
        }
        return AffineTransform.getRotateInstance(angle, bounds.getCenterX(), bounds.getCenterY()).createTransformedShape(bounds).getBounds2D();
    }

    protected double labelLocationX(AxisLabelLocation location, Rectangle2D dataArea) {
        if (location.equals(AxisLabelLocation.HIGH_END)) {
            return dataArea.getMaxX();
        }
        if (location.equals(AxisLabelLocation.MIDDLE)) {
            return dataArea.getCenterX();
        }
        if (location.equals(AxisLabelLocation.LOW_END)) {
            return dataArea.getMinX();
        }
        throw new RuntimeException("Unexpected AxisLabelLocation: " + location);
    }

    protected double labelLocationY(AxisLabelLocation location, Rectangle2D dataArea) {
        if (location.equals(AxisLabelLocation.HIGH_END)) {
            return dataArea.getMinY();
        }
        if (location.equals(AxisLabelLocation.MIDDLE)) {
            return dataArea.getCenterY();
        }
        if (location.equals(AxisLabelLocation.LOW_END)) {
            return dataArea.getMaxY();
        }
        throw new RuntimeException("Unexpected AxisLabelLocation: " + location);
    }

    protected TextAnchor labelAnchorH(AxisLabelLocation location) {
        if (location.equals(AxisLabelLocation.HIGH_END)) {
            return TextAnchor.CENTER_RIGHT;
        }
        if (location.equals(AxisLabelLocation.MIDDLE)) {
            return TextAnchor.CENTER;
        }
        if (location.equals(AxisLabelLocation.LOW_END)) {
            return TextAnchor.CENTER_LEFT;
        }
        throw new RuntimeException("Unexpected AxisLabelLocation: " + location);
    }

    protected TextAnchor labelAnchorV(AxisLabelLocation location) {
        if (location.equals(AxisLabelLocation.HIGH_END)) {
            return TextAnchor.CENTER_RIGHT;
        }
        if (location.equals(AxisLabelLocation.MIDDLE)) {
            return TextAnchor.CENTER;
        }
        if (location.equals(AxisLabelLocation.LOW_END)) {
            return TextAnchor.CENTER_LEFT;
        }
        throw new RuntimeException("Unexpected AxisLabelLocation: " + location);
    }

    protected AxisState drawLabel(String label, Graphics2D g2, Rectangle2D plotArea, Rectangle2D dataArea, RectangleEdge edge, AxisState state) {
        ParamChecks.nullNotPermitted(state, "state");
        if (label != null) {
            if (!label.equals("")) {
                Font font = getLabelFont();
                RectangleInsets insets = getLabelInsets();
                g2.setFont(font);
                g2.setPaint(getLabelPaint());
                Rectangle2D labelBounds = TextUtilities.getTextBounds(label, g2, g2.getFontMetrics());
                double labelx;
                double labely;
                String str;
                Graphics2D graphics2D;
                if (edge == RectangleEdge.TOP) {
                    labelBounds = AffineTransform.getRotateInstance(getLabelAngle(), labelBounds.getCenterX(), labelBounds.getCenterY()).createTransformedShape(labelBounds).getBounds2D();
                    labelx = labelLocationX(this.labelLocation, dataArea);
                    labely = (state.getCursor() - insets.getBottom()) - (labelBounds.getHeight() / DateAxis.DEFAULT_AUTO_RANGE_MINIMUM_SIZE_IN_MILLISECONDS);
                    str = label;
                    graphics2D = g2;
                    TextUtilities.drawRotatedString(str, graphics2D, (float) labelx, (float) labely, labelAnchorH(this.labelLocation), getLabelAngle(), TextAnchor.CENTER);
                    state.cursorUp((insets.getTop() + labelBounds.getHeight()) + insets.getBottom());
                } else if (edge == RectangleEdge.BOTTOM) {
                    labelBounds = AffineTransform.getRotateInstance(getLabelAngle(), labelBounds.getCenterX(), labelBounds.getCenterY()).createTransformedShape(labelBounds).getBounds2D();
                    labelx = labelLocationX(this.labelLocation, dataArea);
                    labely = (state.getCursor() + insets.getTop()) + (labelBounds.getHeight() / DateAxis.DEFAULT_AUTO_RANGE_MINIMUM_SIZE_IN_MILLISECONDS);
                    str = label;
                    graphics2D = g2;
                    TextUtilities.drawRotatedString(str, graphics2D, (float) labelx, (float) labely, labelAnchorH(this.labelLocation), getLabelAngle(), TextAnchor.CENTER);
                    state.cursorDown((insets.getTop() + labelBounds.getHeight()) + insets.getBottom());
                } else if (edge == RectangleEdge.LEFT) {
                    labelBounds = AffineTransform.getRotateInstance(getLabelAngle() - 1.5707963267948966d, labelBounds.getCenterX(), labelBounds.getCenterY()).createTransformedShape(labelBounds).getBounds2D();
                    labelx = (state.getCursor() - insets.getRight()) - (labelBounds.getWidth() / DateAxis.DEFAULT_AUTO_RANGE_MINIMUM_SIZE_IN_MILLISECONDS);
                    labely = labelLocationY(this.labelLocation, dataArea);
                    anchor = labelAnchorV(this.labelLocation);
                    TextUtilities.drawRotatedString(label, g2, (float) labelx, (float) labely, anchor, getLabelAngle() - 1.5707963267948966d, anchor);
                    state.cursorLeft((insets.getLeft() + labelBounds.getWidth()) + insets.getRight());
                } else if (edge == RectangleEdge.RIGHT) {
                    labelBounds = AffineTransform.getRotateInstance(getLabelAngle() + 1.5707963267948966d, labelBounds.getCenterX(), labelBounds.getCenterY()).createTransformedShape(labelBounds).getBounds2D();
                    labelx = (state.getCursor() + insets.getLeft()) + (labelBounds.getWidth() / DateAxis.DEFAULT_AUTO_RANGE_MINIMUM_SIZE_IN_MILLISECONDS);
                    labely = labelLocationY(this.labelLocation, dataArea);
                    anchor = labelAnchorV(this.labelLocation);
                    TextUtilities.drawRotatedString(label, g2, (float) labelx, (float) labely, anchor, 1.5707963267948966d + getLabelAngle(), anchor);
                    state.cursorRight((insets.getLeft() + labelBounds.getWidth()) + insets.getRight());
                }
            }
        }
        return state;
    }

    protected AxisState drawAttributedLabel(AttributedString label, Graphics2D g2, Rectangle2D plotArea, Rectangle2D dataArea, RectangleEdge edge, AxisState state) {
        ParamChecks.nullNotPermitted(state, "state");
        if (label != null) {
            RectangleInsets insets = getLabelInsets();
            g2.setFont(getLabelFont());
            g2.setPaint(getLabelPaint());
            Rectangle2D labelBounds = new TextLayout(this.attributedLabel.getIterator(), g2.getFontRenderContext()).getBounds();
            double labelx;
            double labely;
            AttributedString attributedString;
            Graphics2D graphics2D;
            if (edge == RectangleEdge.TOP) {
                labelBounds = AffineTransform.getRotateInstance(getLabelAngle(), labelBounds.getCenterX(), labelBounds.getCenterY()).createTransformedShape(labelBounds).getBounds2D();
                labelx = labelLocationX(this.labelLocation, dataArea);
                labely = (state.getCursor() - insets.getBottom()) - (labelBounds.getHeight() / DateAxis.DEFAULT_AUTO_RANGE_MINIMUM_SIZE_IN_MILLISECONDS);
                attributedString = label;
                graphics2D = g2;
                AttrStringUtils.drawRotatedString(attributedString, graphics2D, (float) labelx, (float) labely, labelAnchorH(this.labelLocation), getLabelAngle(), TextAnchor.CENTER);
                state.cursorUp((insets.getTop() + labelBounds.getHeight()) + insets.getBottom());
            } else if (edge == RectangleEdge.BOTTOM) {
                labelBounds = AffineTransform.getRotateInstance(getLabelAngle(), labelBounds.getCenterX(), labelBounds.getCenterY()).createTransformedShape(labelBounds).getBounds2D();
                labelx = labelLocationX(this.labelLocation, dataArea);
                labely = (state.getCursor() + insets.getTop()) + (labelBounds.getHeight() / DateAxis.DEFAULT_AUTO_RANGE_MINIMUM_SIZE_IN_MILLISECONDS);
                attributedString = label;
                graphics2D = g2;
                AttrStringUtils.drawRotatedString(attributedString, graphics2D, (float) labelx, (float) labely, labelAnchorH(this.labelLocation), getLabelAngle(), TextAnchor.CENTER);
                state.cursorDown((insets.getTop() + labelBounds.getHeight()) + insets.getBottom());
            } else if (edge == RectangleEdge.LEFT) {
                labelBounds = AffineTransform.getRotateInstance(getLabelAngle() - 1.5707963267948966d, labelBounds.getCenterX(), labelBounds.getCenterY()).createTransformedShape(labelBounds).getBounds2D();
                labelx = (state.getCursor() - insets.getRight()) - (labelBounds.getWidth() / DateAxis.DEFAULT_AUTO_RANGE_MINIMUM_SIZE_IN_MILLISECONDS);
                labely = labelLocationY(this.labelLocation, dataArea);
                anchor = labelAnchorV(this.labelLocation);
                AttrStringUtils.drawRotatedString(label, g2, (float) labelx, (float) labely, anchor, getLabelAngle() - 1.5707963267948966d, anchor);
                state.cursorLeft((insets.getLeft() + labelBounds.getWidth()) + insets.getRight());
            } else if (edge == RectangleEdge.RIGHT) {
                labelBounds = AffineTransform.getRotateInstance(getLabelAngle() + 1.5707963267948966d, labelBounds.getCenterX(), labelBounds.getCenterY()).createTransformedShape(labelBounds).getBounds2D();
                labelx = (state.getCursor() + insets.getLeft()) + (labelBounds.getWidth() / DateAxis.DEFAULT_AUTO_RANGE_MINIMUM_SIZE_IN_MILLISECONDS);
                labely = labelLocationY(this.labelLocation, dataArea);
                anchor = labelAnchorV(this.labelLocation);
                AttrStringUtils.drawRotatedString(label, g2, (float) labelx, (float) labely, anchor, 1.5707963267948966d + getLabelAngle(), anchor);
                state.cursorRight((insets.getLeft() + labelBounds.getWidth()) + insets.getRight());
            }
        }
        return state;
    }

    protected void drawAxisLine(Graphics2D g2, double cursor, Rectangle2D dataArea, RectangleEdge edge) {
        Line2D axisLine = null;
        double x = dataArea.getX();
        double y = dataArea.getY();
        if (edge == RectangleEdge.TOP) {
            axisLine = new Line2D.Double(x, cursor, dataArea.getMaxX(), cursor);
        } else if (edge == RectangleEdge.BOTTOM) {
            axisLine = new Line2D.Double(x, cursor, dataArea.getMaxX(), cursor);
        } else if (edge == RectangleEdge.LEFT) {
            r7 = new Line2D.Double(cursor, y, cursor, dataArea.getMaxY());
        } else if (edge == RectangleEdge.RIGHT) {
            r7 = new Line2D.Double(cursor, y, cursor, dataArea.getMaxY());
        }
        g2.setPaint(this.axisLinePaint);
        g2.setStroke(this.axisLineStroke);
        Object saved = g2.getRenderingHint(RenderingHints.KEY_STROKE_CONTROL);
        g2.setRenderingHint(RenderingHints.KEY_STROKE_CONTROL, RenderingHints.VALUE_STROKE_NORMALIZE);
        g2.draw(axisLine);
        g2.setRenderingHint(RenderingHints.KEY_STROKE_CONTROL, saved);
    }

    public Object clone() throws CloneNotSupportedException {
        Axis clone = (Axis) super.clone();
        clone.plot = null;
        clone.listenerList = new EventListenerList();
        return clone;
    }

    public boolean equals(Object obj) {
        if (obj == this) {
            return DEFAULT_TICK_MARKS_VISIBLE;
        }
        if (!(obj instanceof Axis)) {
            return false;
        }
        Axis that = (Axis) obj;
        if (this.visible != that.visible) {
            return false;
        }
        if (!ObjectUtilities.equal(this.label, that.label)) {
            return false;
        }
        if (!AttributedStringUtilities.equal(this.attributedLabel, that.attributedLabel)) {
            return false;
        }
        if (!ObjectUtilities.equal(this.labelFont, that.labelFont)) {
            return false;
        }
        if (!PaintUtilities.equal(this.labelPaint, that.labelPaint)) {
            return false;
        }
        if (!ObjectUtilities.equal(this.labelInsets, that.labelInsets)) {
            return false;
        }
        if (this.labelAngle != that.labelAngle) {
            return false;
        }
        if (!this.labelLocation.equals(that.labelLocation)) {
            return false;
        }
        if (this.axisLineVisible != that.axisLineVisible) {
            return false;
        }
        if (!ObjectUtilities.equal(this.axisLineStroke, that.axisLineStroke)) {
            return false;
        }
        if (!PaintUtilities.equal(this.axisLinePaint, that.axisLinePaint)) {
            return false;
        }
        if (this.tickLabelsVisible != that.tickLabelsVisible) {
            return false;
        }
        if (!ObjectUtilities.equal(this.tickLabelFont, that.tickLabelFont)) {
            return false;
        }
        if (!PaintUtilities.equal(this.tickLabelPaint, that.tickLabelPaint)) {
            return false;
        }
        if (!ObjectUtilities.equal(this.tickLabelInsets, that.tickLabelInsets)) {
            return false;
        }
        if (this.tickMarksVisible != that.tickMarksVisible) {
            return false;
        }
        if (this.tickMarkInsideLength != that.tickMarkInsideLength) {
            return false;
        }
        if (this.tickMarkOutsideLength != that.tickMarkOutsideLength) {
            return false;
        }
        if (!PaintUtilities.equal(this.tickMarkPaint, that.tickMarkPaint)) {
            return false;
        }
        if (!ObjectUtilities.equal(this.tickMarkStroke, that.tickMarkStroke)) {
            return false;
        }
        if (this.minorTickMarksVisible != that.minorTickMarksVisible) {
            return false;
        }
        if (this.minorTickMarkInsideLength != that.minorTickMarkInsideLength) {
            return false;
        }
        if (this.minorTickMarkOutsideLength != that.minorTickMarkOutsideLength) {
            return false;
        }
        if (this.fixedDimension != that.fixedDimension) {
            return false;
        }
        return DEFAULT_TICK_MARKS_VISIBLE;
    }

    public int hashCode() {
        if (this.label != null) {
            return this.label.hashCode() + 249;
        }
        return 3;
    }

    private void writeObject(ObjectOutputStream stream) throws IOException {
        stream.defaultWriteObject();
        SerialUtilities.writeAttributedString(this.attributedLabel, stream);
        SerialUtilities.writePaint(this.labelPaint, stream);
        SerialUtilities.writePaint(this.tickLabelPaint, stream);
        SerialUtilities.writeStroke(this.axisLineStroke, stream);
        SerialUtilities.writePaint(this.axisLinePaint, stream);
        SerialUtilities.writeStroke(this.tickMarkStroke, stream);
        SerialUtilities.writePaint(this.tickMarkPaint, stream);
    }

    private void readObject(ObjectInputStream stream) throws IOException, ClassNotFoundException {
        stream.defaultReadObject();
        this.attributedLabel = SerialUtilities.readAttributedString(stream);
        this.labelPaint = SerialUtilities.readPaint(stream);
        this.tickLabelPaint = SerialUtilities.readPaint(stream);
        this.axisLineStroke = SerialUtilities.readStroke(stream);
        this.axisLinePaint = SerialUtilities.readPaint(stream);
        this.tickMarkStroke = SerialUtilities.readStroke(stream);
        this.tickMarkPaint = SerialUtilities.readPaint(stream);
        this.listenerList = new EventListenerList();
    }
}
