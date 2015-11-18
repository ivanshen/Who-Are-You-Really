package org.jfree.chart.title;

import java.awt.GradientPaint;
import java.awt.Graphics2D;
import java.awt.Paint;
import java.awt.Shape;
import java.awt.Stroke;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.awt.geom.Rectangle2D.Double;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import org.jfree.chart.axis.DateAxis;
import org.jfree.chart.block.AbstractBlock;
import org.jfree.chart.block.Block;
import org.jfree.chart.block.LengthConstraintType;
import org.jfree.chart.block.RectangleConstraint;
import org.jfree.chart.util.ParamChecks;
import org.jfree.io.SerialUtilities;
import org.jfree.ui.GradientPaintTransformer;
import org.jfree.ui.RectangleAnchor;
import org.jfree.ui.Size2D;
import org.jfree.ui.StandardGradientPaintTransformer;
import org.jfree.util.ObjectUtilities;
import org.jfree.util.PaintUtilities;
import org.jfree.util.PublicCloneable;
import org.jfree.util.ShapeUtilities;

public class LegendGraphic extends AbstractBlock implements Block, PublicCloneable {
    static final /* synthetic */ boolean $assertionsDisabled;
    static final long serialVersionUID = -1338791523854985009L;
    private transient Paint fillPaint;
    private GradientPaintTransformer fillPaintTransformer;
    private transient Shape line;
    private transient Paint linePaint;
    private transient Stroke lineStroke;
    private boolean lineVisible;
    private transient Paint outlinePaint;
    private transient Stroke outlineStroke;
    private transient Shape shape;
    private RectangleAnchor shapeAnchor;
    private boolean shapeFilled;
    private RectangleAnchor shapeLocation;
    private boolean shapeOutlineVisible;
    private boolean shapeVisible;

    static {
        $assertionsDisabled = !LegendGraphic.class.desiredAssertionStatus() ? true : $assertionsDisabled;
    }

    public LegendGraphic(Shape shape, Paint fillPaint) {
        ParamChecks.nullNotPermitted(shape, "shape");
        ParamChecks.nullNotPermitted(fillPaint, "fillPaint");
        this.shapeVisible = true;
        this.shape = shape;
        this.shapeAnchor = RectangleAnchor.CENTER;
        this.shapeLocation = RectangleAnchor.CENTER;
        this.shapeFilled = true;
        this.fillPaint = fillPaint;
        this.fillPaintTransformer = new StandardGradientPaintTransformer();
        setPadding(DateAxis.DEFAULT_AUTO_RANGE_MINIMUM_SIZE_IN_MILLISECONDS, DateAxis.DEFAULT_AUTO_RANGE_MINIMUM_SIZE_IN_MILLISECONDS, DateAxis.DEFAULT_AUTO_RANGE_MINIMUM_SIZE_IN_MILLISECONDS, DateAxis.DEFAULT_AUTO_RANGE_MINIMUM_SIZE_IN_MILLISECONDS);
    }

    public boolean isShapeVisible() {
        return this.shapeVisible;
    }

    public void setShapeVisible(boolean visible) {
        this.shapeVisible = visible;
    }

    public Shape getShape() {
        return this.shape;
    }

    public void setShape(Shape shape) {
        this.shape = shape;
    }

    public boolean isShapeFilled() {
        return this.shapeFilled;
    }

    public void setShapeFilled(boolean filled) {
        this.shapeFilled = filled;
    }

    public Paint getFillPaint() {
        return this.fillPaint;
    }

    public void setFillPaint(Paint paint) {
        this.fillPaint = paint;
    }

    public GradientPaintTransformer getFillPaintTransformer() {
        return this.fillPaintTransformer;
    }

    public void setFillPaintTransformer(GradientPaintTransformer transformer) {
        ParamChecks.nullNotPermitted(transformer, "transformer");
        this.fillPaintTransformer = transformer;
    }

    public boolean isShapeOutlineVisible() {
        return this.shapeOutlineVisible;
    }

    public void setShapeOutlineVisible(boolean visible) {
        this.shapeOutlineVisible = visible;
    }

    public Paint getOutlinePaint() {
        return this.outlinePaint;
    }

    public void setOutlinePaint(Paint paint) {
        this.outlinePaint = paint;
    }

    public Stroke getOutlineStroke() {
        return this.outlineStroke;
    }

    public void setOutlineStroke(Stroke stroke) {
        this.outlineStroke = stroke;
    }

    public RectangleAnchor getShapeAnchor() {
        return this.shapeAnchor;
    }

    public void setShapeAnchor(RectangleAnchor anchor) {
        ParamChecks.nullNotPermitted(anchor, "anchor");
        this.shapeAnchor = anchor;
    }

    public RectangleAnchor getShapeLocation() {
        return this.shapeLocation;
    }

    public void setShapeLocation(RectangleAnchor location) {
        ParamChecks.nullNotPermitted(location, "location");
        this.shapeLocation = location;
    }

    public boolean isLineVisible() {
        return this.lineVisible;
    }

    public void setLineVisible(boolean visible) {
        this.lineVisible = visible;
    }

    public Shape getLine() {
        return this.line;
    }

    public void setLine(Shape line) {
        this.line = line;
    }

    public Paint getLinePaint() {
        return this.linePaint;
    }

    public void setLinePaint(Paint paint) {
        this.linePaint = paint;
    }

    public Stroke getLineStroke() {
        return this.lineStroke;
    }

    public void setLineStroke(Stroke stroke) {
        this.lineStroke = stroke;
    }

    public Size2D arrange(Graphics2D g2, RectangleConstraint constraint) {
        RectangleConstraint contentConstraint = toContentConstraint(constraint);
        LengthConstraintType w = contentConstraint.getWidthConstraintType();
        LengthConstraintType h = contentConstraint.getHeightConstraintType();
        Size2D contentSize = null;
        if (w == LengthConstraintType.NONE) {
            if (h == LengthConstraintType.NONE) {
                contentSize = arrangeNN(g2);
            } else if (h == LengthConstraintType.RANGE) {
                throw new RuntimeException("Not yet implemented.");
            } else if (h == LengthConstraintType.FIXED) {
                throw new RuntimeException("Not yet implemented.");
            }
        } else if (w == LengthConstraintType.RANGE) {
            if (h == LengthConstraintType.NONE) {
                throw new RuntimeException("Not yet implemented.");
            } else if (h == LengthConstraintType.RANGE) {
                throw new RuntimeException("Not yet implemented.");
            } else if (h == LengthConstraintType.FIXED) {
                throw new RuntimeException("Not yet implemented.");
            }
        } else if (w == LengthConstraintType.FIXED) {
            if (h == LengthConstraintType.NONE) {
                throw new RuntimeException("Not yet implemented.");
            } else if (h == LengthConstraintType.RANGE) {
                throw new RuntimeException("Not yet implemented.");
            } else if (h == LengthConstraintType.FIXED) {
                contentSize = new Size2D(contentConstraint.getWidth(), contentConstraint.getHeight());
            }
        }
        if ($assertionsDisabled || contentSize != null) {
            return new Size2D(calculateTotalWidth(contentSize.getWidth()), calculateTotalHeight(contentSize.getHeight()));
        }
        throw new AssertionError();
    }

    protected Size2D arrangeNN(Graphics2D g2) {
        Rectangle2D contentSize = new Double();
        if (this.line != null) {
            contentSize.setRect(this.line.getBounds2D());
        }
        if (this.shape != null) {
            contentSize = contentSize.createUnion(this.shape.getBounds2D());
        }
        return new Size2D(contentSize.getWidth(), contentSize.getHeight());
    }

    public void draw(Graphics2D g2, Rectangle2D area) {
        area = trimMargin(area);
        drawBorder(g2, area);
        area = trimPadding(trimBorder(area));
        if (this.lineVisible) {
            Point2D location = RectangleAnchor.coordinates(area, this.shapeLocation);
            Shape aLine = ShapeUtilities.createTranslatedShape(getLine(), this.shapeAnchor, location.getX(), location.getY());
            g2.setPaint(this.linePaint);
            g2.setStroke(this.lineStroke);
            g2.draw(aLine);
        }
        if (this.shapeVisible) {
            location = RectangleAnchor.coordinates(area, this.shapeLocation);
            Shape s = ShapeUtilities.createTranslatedShape(this.shape, this.shapeAnchor, location.getX(), location.getY());
            if (this.shapeFilled) {
                Paint p = this.fillPaint;
                if (p instanceof GradientPaint) {
                    p = this.fillPaintTransformer.transform(this.fillPaint, s);
                }
                g2.setPaint(p);
                g2.fill(s);
            }
            if (this.shapeOutlineVisible) {
                g2.setPaint(this.outlinePaint);
                g2.setStroke(this.outlineStroke);
                g2.draw(s);
            }
        }
    }

    public Object draw(Graphics2D g2, Rectangle2D area, Object params) {
        draw(g2, area);
        return null;
    }

    public boolean equals(Object obj) {
        if (!(obj instanceof LegendGraphic)) {
            return $assertionsDisabled;
        }
        LegendGraphic that = (LegendGraphic) obj;
        if (this.shapeVisible == that.shapeVisible && ShapeUtilities.equal(this.shape, that.shape) && this.shapeFilled == that.shapeFilled && PaintUtilities.equal(this.fillPaint, that.fillPaint) && ObjectUtilities.equal(this.fillPaintTransformer, that.fillPaintTransformer) && this.shapeOutlineVisible == that.shapeOutlineVisible && PaintUtilities.equal(this.outlinePaint, that.outlinePaint) && ObjectUtilities.equal(this.outlineStroke, that.outlineStroke) && this.shapeAnchor == that.shapeAnchor && this.shapeLocation == that.shapeLocation && this.lineVisible == that.lineVisible && ShapeUtilities.equal(this.line, that.line) && PaintUtilities.equal(this.linePaint, that.linePaint) && ObjectUtilities.equal(this.lineStroke, that.lineStroke)) {
            return super.equals(obj);
        }
        return $assertionsDisabled;
    }

    public int hashCode() {
        return ObjectUtilities.hashCode(this.fillPaint) + 7141;
    }

    public Object clone() throws CloneNotSupportedException {
        LegendGraphic clone = (LegendGraphic) super.clone();
        clone.shape = ShapeUtilities.clone(this.shape);
        clone.line = ShapeUtilities.clone(this.line);
        return clone;
    }

    private void writeObject(ObjectOutputStream stream) throws IOException {
        stream.defaultWriteObject();
        SerialUtilities.writeShape(this.shape, stream);
        SerialUtilities.writePaint(this.fillPaint, stream);
        SerialUtilities.writePaint(this.outlinePaint, stream);
        SerialUtilities.writeStroke(this.outlineStroke, stream);
        SerialUtilities.writeShape(this.line, stream);
        SerialUtilities.writePaint(this.linePaint, stream);
        SerialUtilities.writeStroke(this.lineStroke, stream);
    }

    private void readObject(ObjectInputStream stream) throws IOException, ClassNotFoundException {
        stream.defaultReadObject();
        this.shape = SerialUtilities.readShape(stream);
        this.fillPaint = SerialUtilities.readPaint(stream);
        this.outlinePaint = SerialUtilities.readPaint(stream);
        this.outlineStroke = SerialUtilities.readStroke(stream);
        this.line = SerialUtilities.readShape(stream);
        this.linePaint = SerialUtilities.readPaint(stream);
        this.lineStroke = SerialUtilities.readStroke(stream);
    }
}