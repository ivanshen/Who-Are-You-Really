package org.jfree.chart.annotations;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Paint;
import java.awt.Shape;
import java.awt.Stroke;
import java.awt.geom.AffineTransform;
import java.awt.geom.Rectangle2D;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import org.jfree.chart.HashUtilities;
import org.jfree.chart.axis.ValueAxis;
import org.jfree.chart.plot.Plot;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.plot.PlotRenderingInfo;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.util.ParamChecks;
import org.jfree.io.SerialUtilities;
import org.jfree.ui.RectangleEdge;
import org.jfree.util.ObjectUtilities;
import org.jfree.util.PaintUtilities;
import org.jfree.util.PublicCloneable;

public class XYShapeAnnotation extends AbstractXYAnnotation implements Cloneable, PublicCloneable, Serializable {
    private static final long serialVersionUID = -8553218317600684041L;
    private transient Paint fillPaint;
    private transient Paint outlinePaint;
    private transient Shape shape;
    private transient Stroke stroke;

    public XYShapeAnnotation(Shape shape) {
        this(shape, new BasicStroke(Plot.DEFAULT_FOREGROUND_ALPHA), Color.black);
    }

    public XYShapeAnnotation(Shape shape, Stroke stroke, Paint outlinePaint) {
        this(shape, stroke, outlinePaint, null);
    }

    public XYShapeAnnotation(Shape shape, Stroke stroke, Paint outlinePaint, Paint fillPaint) {
        ParamChecks.nullNotPermitted(shape, "shape");
        this.shape = shape;
        this.stroke = stroke;
        this.outlinePaint = outlinePaint;
        this.fillPaint = fillPaint;
    }

    public void draw(Graphics2D g2, XYPlot plot, Rectangle2D dataArea, ValueAxis domainAxis, ValueAxis rangeAxis, int rendererIndex, PlotRenderingInfo info) {
        PlotOrientation orientation = plot.getOrientation();
        RectangleEdge domainEdge = Plot.resolveDomainAxisLocation(plot.getDomainAxisLocation(), orientation);
        RectangleEdge rangeEdge = Plot.resolveRangeAxisLocation(plot.getRangeAxisLocation(), orientation);
        Rectangle2D bounds = this.shape.getBounds2D();
        double x0 = bounds.getMinX();
        double x1 = bounds.getMaxX();
        double xx0 = domainAxis.valueToJava2D(x0, dataArea, domainEdge);
        double m00 = (domainAxis.valueToJava2D(x1, dataArea, domainEdge) - xx0) / (x1 - x0);
        double m02 = xx0 - (x0 * m00);
        double y0 = bounds.getMaxY();
        double y1 = bounds.getMinY();
        double yy0 = rangeAxis.valueToJava2D(y0, dataArea, rangeEdge);
        double m11 = (rangeAxis.valueToJava2D(y1, dataArea, rangeEdge) - yy0) / (y1 - y0);
        double m12 = yy0 - (m11 * y0);
        Shape s = null;
        if (orientation == PlotOrientation.HORIZONTAL) {
            s = new AffineTransform(m11, 0.0d, 0.0d, m00, m12, m02).createTransformedShape(new AffineTransform(0.0f, Plot.DEFAULT_FOREGROUND_ALPHA, Plot.DEFAULT_FOREGROUND_ALPHA, null, 0.0f, 0.0f).createTransformedShape(this.shape));
        } else if (orientation == PlotOrientation.VERTICAL) {
            s = new AffineTransform(m00, 0.0d, 0.0d, m11, m02, m12).createTransformedShape(this.shape);
        }
        if (this.fillPaint != null) {
            g2.setPaint(this.fillPaint);
            g2.fill(s);
        }
        if (!(this.stroke == null || this.outlinePaint == null)) {
            g2.setPaint(this.outlinePaint);
            g2.setStroke(this.stroke);
            g2.draw(s);
        }
        addEntity(info, s, rendererIndex, getToolTipText(), getURL());
    }

    public boolean equals(Object obj) {
        if (obj == this) {
            return true;
        }
        if (!super.equals(obj)) {
            return false;
        }
        if (!(obj instanceof XYShapeAnnotation)) {
            return false;
        }
        XYShapeAnnotation that = (XYShapeAnnotation) obj;
        if (!this.shape.equals(that.shape)) {
            return false;
        }
        if (!ObjectUtilities.equal(this.stroke, that.stroke)) {
            return false;
        }
        if (!PaintUtilities.equal(this.outlinePaint, that.outlinePaint)) {
            return false;
        }
        if (PaintUtilities.equal(this.fillPaint, that.fillPaint)) {
            return true;
        }
        return false;
    }

    public int hashCode() {
        int result = this.shape.hashCode() + 7141;
        if (this.stroke != null) {
            result = (result * 37) + this.stroke.hashCode();
        }
        return (((result * 37) + HashUtilities.hashCodeForPaint(this.outlinePaint)) * 37) + HashUtilities.hashCodeForPaint(this.fillPaint);
    }

    public Object clone() throws CloneNotSupportedException {
        return super.clone();
    }

    private void writeObject(ObjectOutputStream stream) throws IOException {
        stream.defaultWriteObject();
        SerialUtilities.writeShape(this.shape, stream);
        SerialUtilities.writeStroke(this.stroke, stream);
        SerialUtilities.writePaint(this.outlinePaint, stream);
        SerialUtilities.writePaint(this.fillPaint, stream);
    }

    private void readObject(ObjectInputStream stream) throws IOException, ClassNotFoundException {
        stream.defaultReadObject();
        this.shape = SerialUtilities.readShape(stream);
        this.stroke = SerialUtilities.readStroke(stream);
        this.outlinePaint = SerialUtilities.readPaint(stream);
        this.fillPaint = SerialUtilities.readPaint(stream);
    }
}
