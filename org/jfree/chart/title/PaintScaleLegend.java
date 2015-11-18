package org.jfree.chart.title;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Paint;
import java.awt.Stroke;
import java.awt.geom.Rectangle2D;
import java.awt.geom.Rectangle2D.Double;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.AxisLocation;
import org.jfree.chart.axis.AxisSpace;
import org.jfree.chart.axis.ValueAxis;
import org.jfree.chart.block.LengthConstraintType;
import org.jfree.chart.block.RectangleConstraint;
import org.jfree.chart.event.AxisChangeEvent;
import org.jfree.chart.event.AxisChangeListener;
import org.jfree.chart.event.TitleChangeEvent;
import org.jfree.chart.plot.Plot;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.renderer.PaintScale;
import org.jfree.chart.util.ParamChecks;
import org.jfree.data.Range;
import org.jfree.data.xy.NormalizedMatrixSeries;
import org.jfree.io.SerialUtilities;
import org.jfree.ui.RectangleEdge;
import org.jfree.ui.Size2D;
import org.jfree.util.PaintUtilities;
import org.jfree.util.PublicCloneable;

public class PaintScaleLegend extends Title implements AxisChangeListener, PublicCloneable {
    static final /* synthetic */ boolean $assertionsDisabled;
    static final long serialVersionUID = -1365146490993227503L;
    private ValueAxis axis;
    private AxisLocation axisLocation;
    private double axisOffset;
    private transient Paint backgroundPaint;
    private PaintScale scale;
    private transient Paint stripOutlinePaint;
    private transient Stroke stripOutlineStroke;
    private boolean stripOutlineVisible;
    private double stripWidth;
    private int subdivisions;

    static {
        $assertionsDisabled = !PaintScaleLegend.class.desiredAssertionStatus() ? true : $assertionsDisabled;
    }

    public PaintScaleLegend(PaintScale scale, ValueAxis axis) {
        ParamChecks.nullNotPermitted(axis, "axis");
        this.scale = scale;
        this.axis = axis;
        this.axis.addChangeListener(this);
        this.axisLocation = AxisLocation.BOTTOM_OR_LEFT;
        this.axisOffset = 0.0d;
        this.axis.setRange(scale.getLowerBound(), scale.getUpperBound());
        this.stripWidth = 15.0d;
        this.stripOutlineVisible = true;
        this.stripOutlinePaint = Color.gray;
        this.stripOutlineStroke = new BasicStroke(JFreeChart.DEFAULT_BACKGROUND_IMAGE_ALPHA);
        this.backgroundPaint = Color.white;
        this.subdivisions = 100;
    }

    public PaintScale getScale() {
        return this.scale;
    }

    public void setScale(PaintScale scale) {
        ParamChecks.nullNotPermitted(scale, "scale");
        this.scale = scale;
        notifyListeners(new TitleChangeEvent(this));
    }

    public ValueAxis getAxis() {
        return this.axis;
    }

    public void setAxis(ValueAxis axis) {
        ParamChecks.nullNotPermitted(axis, "axis");
        this.axis.removeChangeListener(this);
        this.axis = axis;
        this.axis.addChangeListener(this);
        notifyListeners(new TitleChangeEvent(this));
    }

    public AxisLocation getAxisLocation() {
        return this.axisLocation;
    }

    public void setAxisLocation(AxisLocation location) {
        ParamChecks.nullNotPermitted(location, "location");
        this.axisLocation = location;
        notifyListeners(new TitleChangeEvent(this));
    }

    public double getAxisOffset() {
        return this.axisOffset;
    }

    public void setAxisOffset(double offset) {
        this.axisOffset = offset;
        notifyListeners(new TitleChangeEvent(this));
    }

    public double getStripWidth() {
        return this.stripWidth;
    }

    public void setStripWidth(double width) {
        this.stripWidth = width;
        notifyListeners(new TitleChangeEvent(this));
    }

    public boolean isStripOutlineVisible() {
        return this.stripOutlineVisible;
    }

    public void setStripOutlineVisible(boolean visible) {
        this.stripOutlineVisible = visible;
        notifyListeners(new TitleChangeEvent(this));
    }

    public Paint getStripOutlinePaint() {
        return this.stripOutlinePaint;
    }

    public void setStripOutlinePaint(Paint paint) {
        ParamChecks.nullNotPermitted(paint, "paint");
        this.stripOutlinePaint = paint;
        notifyListeners(new TitleChangeEvent(this));
    }

    public Stroke getStripOutlineStroke() {
        return this.stripOutlineStroke;
    }

    public void setStripOutlineStroke(Stroke stroke) {
        ParamChecks.nullNotPermitted(stroke, "stroke");
        this.stripOutlineStroke = stroke;
        notifyListeners(new TitleChangeEvent(this));
    }

    public Paint getBackgroundPaint() {
        return this.backgroundPaint;
    }

    public void setBackgroundPaint(Paint paint) {
        this.backgroundPaint = paint;
        notifyListeners(new TitleChangeEvent(this));
    }

    public int getSubdivisionCount() {
        return this.subdivisions;
    }

    public void setSubdivisionCount(int count) {
        if (count <= 0) {
            throw new IllegalArgumentException("Requires 'count' > 0.");
        }
        this.subdivisions = count;
        notifyListeners(new TitleChangeEvent(this));
    }

    public void axisChanged(AxisChangeEvent event) {
        if (this.axis == event.getAxis()) {
            notifyListeners(new TitleChangeEvent(this));
        }
    }

    public Size2D arrange(Graphics2D g2, RectangleConstraint constraint) {
        RectangleConstraint cc = toContentConstraint(constraint);
        LengthConstraintType w = cc.getWidthConstraintType();
        LengthConstraintType h = cc.getHeightConstraintType();
        Size2D contentSize = null;
        if (w == LengthConstraintType.NONE) {
            if (h == LengthConstraintType.NONE) {
                contentSize = new Size2D(getWidth(), getHeight());
            } else if (h == LengthConstraintType.RANGE) {
                throw new RuntimeException("Not yet implemented.");
            } else if (h == LengthConstraintType.FIXED) {
                throw new RuntimeException("Not yet implemented.");
            }
        } else if (w == LengthConstraintType.RANGE) {
            if (h == LengthConstraintType.NONE) {
                throw new RuntimeException("Not yet implemented.");
            } else if (h == LengthConstraintType.RANGE) {
                contentSize = arrangeRR(g2, cc.getWidthRange(), cc.getHeightRange());
            } else if (h == LengthConstraintType.FIXED) {
                throw new RuntimeException("Not yet implemented.");
            }
        } else if (w == LengthConstraintType.FIXED) {
            if (h == LengthConstraintType.NONE) {
                throw new RuntimeException("Not yet implemented.");
            } else if (h == LengthConstraintType.RANGE) {
                throw new RuntimeException("Not yet implemented.");
            } else if (h == LengthConstraintType.FIXED) {
                throw new RuntimeException("Not yet implemented.");
            }
        }
        if ($assertionsDisabled || contentSize != null) {
            return new Size2D(calculateTotalWidth(contentSize.getWidth()), calculateTotalHeight(contentSize.getHeight()));
        }
        throw new AssertionError();
    }

    protected Size2D arrangeRR(Graphics2D g2, Range widthRange, Range heightRange) {
        RectangleEdge position = getPosition();
        ValueAxis valueAxis;
        Rectangle2D rectangle2D;
        AxisSpace space;
        if (position == RectangleEdge.TOP || position == RectangleEdge.BOTTOM) {
            float maxWidth = (float) widthRange.getUpperBound();
            valueAxis = this.axis;
            rectangle2D = new Double(0.0d, 0.0d, (double) maxWidth, 100.0d);
            space = valueAxis.reserveSpace(g2, null, r1, RectangleEdge.BOTTOM, null);
            return new Size2D((double) maxWidth, ((this.stripWidth + this.axisOffset) + space.getTop()) + space.getBottom());
        } else if (position == RectangleEdge.LEFT || position == RectangleEdge.RIGHT) {
            float maxHeight = (float) heightRange.getUpperBound();
            valueAxis = this.axis;
            rectangle2D = new Double(0.0d, 0.0d, 100.0d, (double) maxHeight);
            space = valueAxis.reserveSpace(g2, null, r1, RectangleEdge.RIGHT, null);
            return new Size2D(((this.stripWidth + this.axisOffset) + space.getLeft()) + space.getRight(), (double) maxHeight);
        } else {
            throw new RuntimeException("Unrecognised position.");
        }
    }

    public void draw(Graphics2D g2, Rectangle2D area) {
        draw(g2, area, null);
    }

    public Object draw(Graphics2D g2, Rectangle2D area, Object params) {
        Rectangle2D target = trimMargin((Rectangle2D) area.clone());
        if (this.backgroundPaint != null) {
            g2.setPaint(this.backgroundPaint);
            g2.fill(target);
        }
        getFrame().draw(g2, target);
        getFrame().getInsets().trim(target);
        target = trimPadding(target);
        double base = this.axis.getLowerBound();
        double increment = this.axis.getRange().getLength() / ((double) this.subdivisions);
        Rectangle2D r = new Double();
        RectangleEdge axisEdge;
        int i;
        double v;
        Paint p;
        double vv0;
        double vv1;
        if (RectangleEdge.isTopOrBottom(getPosition())) {
            axisEdge = Plot.resolveRangeAxisLocation(this.axisLocation, PlotOrientation.HORIZONTAL);
            Graphics2D graphics2D;
            if (axisEdge == RectangleEdge.TOP) {
                for (i = 0; i < this.subdivisions; i++) {
                    v = base + (((double) i) * increment);
                    p = this.scale.getPaint(v);
                    vv0 = this.axis.valueToJava2D(v, target, RectangleEdge.TOP);
                    vv1 = this.axis.valueToJava2D(v + increment, target, RectangleEdge.TOP);
                    r.setRect(Math.min(vv0, vv1), target.getMaxY() - this.stripWidth, Math.abs(vv1 - vv0) + NormalizedMatrixSeries.DEFAULT_SCALE_FACTOR, this.stripWidth);
                    g2.setPaint(p);
                    g2.fill(r);
                }
                if (isStripOutlineVisible()) {
                    g2.setPaint(this.stripOutlinePaint);
                    g2.setStroke(this.stripOutlineStroke);
                    graphics2D = g2;
                    graphics2D.draw(new Double(target.getMinX(), target.getMaxY() - this.stripWidth, target.getWidth(), this.stripWidth));
                }
                this.axis.draw(g2, (target.getMaxY() - this.stripWidth) - this.axisOffset, target, target, RectangleEdge.TOP, null);
            } else if (axisEdge == RectangleEdge.BOTTOM) {
                for (i = 0; i < this.subdivisions; i++) {
                    v = base + (((double) i) * increment);
                    p = this.scale.getPaint(v);
                    vv0 = this.axis.valueToJava2D(v, target, RectangleEdge.BOTTOM);
                    vv1 = this.axis.valueToJava2D(v + increment, target, RectangleEdge.BOTTOM);
                    r.setRect(Math.min(vv0, vv1), target.getMinY(), Math.abs(vv1 - vv0) + NormalizedMatrixSeries.DEFAULT_SCALE_FACTOR, this.stripWidth);
                    g2.setPaint(p);
                    g2.fill(r);
                }
                if (isStripOutlineVisible()) {
                    g2.setPaint(this.stripOutlinePaint);
                    g2.setStroke(this.stripOutlineStroke);
                    graphics2D = g2;
                    graphics2D.draw(new Double(target.getMinX(), target.getMinY(), target.getWidth(), this.stripWidth));
                }
                this.axis.draw(g2, (target.getMinY() + this.stripWidth) + this.axisOffset, target, target, RectangleEdge.BOTTOM, null);
            }
        } else {
            axisEdge = Plot.resolveRangeAxisLocation(this.axisLocation, PlotOrientation.VERTICAL);
            double hh;
            Rectangle2D rectangle2D;
            if (axisEdge == RectangleEdge.LEFT) {
                for (i = 0; i < this.subdivisions; i++) {
                    v = base + (((double) i) * increment);
                    p = this.scale.getPaint(v);
                    vv0 = this.axis.valueToJava2D(v, target, RectangleEdge.LEFT);
                    vv1 = this.axis.valueToJava2D(v + increment, target, RectangleEdge.LEFT);
                    hh = Math.abs(vv1 - vv0) + NormalizedMatrixSeries.DEFAULT_SCALE_FACTOR;
                    rectangle2D = r;
                    rectangle2D.setRect(target.getMaxX() - this.stripWidth, Math.min(vv0, vv1), this.stripWidth, hh);
                    g2.setPaint(p);
                    g2.fill(r);
                }
                if (isStripOutlineVisible()) {
                    g2.setPaint(this.stripOutlinePaint);
                    g2.setStroke(this.stripOutlineStroke);
                    g2.draw(new Double(target.getMaxX() - this.stripWidth, target.getMinY(), this.stripWidth, target.getHeight()));
                }
                this.axis.draw(g2, (target.getMaxX() - this.stripWidth) - this.axisOffset, target, target, RectangleEdge.LEFT, null);
            } else if (axisEdge == RectangleEdge.RIGHT) {
                for (i = 0; i < this.subdivisions; i++) {
                    v = base + (((double) i) * increment);
                    p = this.scale.getPaint(v);
                    vv0 = this.axis.valueToJava2D(v, target, RectangleEdge.LEFT);
                    vv1 = this.axis.valueToJava2D(v + increment, target, RectangleEdge.LEFT);
                    hh = Math.abs(vv1 - vv0) + NormalizedMatrixSeries.DEFAULT_SCALE_FACTOR;
                    rectangle2D = r;
                    rectangle2D.setRect(target.getMinX(), Math.min(vv0, vv1), this.stripWidth, hh);
                    g2.setPaint(p);
                    g2.fill(r);
                }
                if (isStripOutlineVisible()) {
                    g2.setPaint(this.stripOutlinePaint);
                    g2.setStroke(this.stripOutlineStroke);
                    g2.draw(new Double(target.getMinX(), target.getMinY(), this.stripWidth, target.getHeight()));
                }
                this.axis.draw(g2, (target.getMinX() + this.stripWidth) + this.axisOffset, target, target, RectangleEdge.RIGHT, null);
            }
        }
        return null;
    }

    public boolean equals(Object obj) {
        if (!(obj instanceof PaintScaleLegend)) {
            return $assertionsDisabled;
        }
        PaintScaleLegend that = (PaintScaleLegend) obj;
        if (this.scale.equals(that.scale) && this.axis.equals(that.axis) && this.axisLocation.equals(that.axisLocation) && this.axisOffset == that.axisOffset && this.stripWidth == that.stripWidth && this.stripOutlineVisible == that.stripOutlineVisible && PaintUtilities.equal(this.stripOutlinePaint, that.stripOutlinePaint) && this.stripOutlineStroke.equals(that.stripOutlineStroke) && PaintUtilities.equal(this.backgroundPaint, that.backgroundPaint) && this.subdivisions == that.subdivisions) {
            return super.equals(obj);
        }
        return $assertionsDisabled;
    }

    private void writeObject(ObjectOutputStream stream) throws IOException {
        stream.defaultWriteObject();
        SerialUtilities.writePaint(this.backgroundPaint, stream);
        SerialUtilities.writePaint(this.stripOutlinePaint, stream);
        SerialUtilities.writeStroke(this.stripOutlineStroke, stream);
    }

    private void readObject(ObjectInputStream stream) throws IOException, ClassNotFoundException {
        stream.defaultReadObject();
        this.backgroundPaint = SerialUtilities.readPaint(stream);
        this.stripOutlinePaint = SerialUtilities.readPaint(stream);
        this.stripOutlineStroke = SerialUtilities.readStroke(stream);
    }
}
