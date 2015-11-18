package org.jfree.chart.plot.dial;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Paint;
import java.awt.Stroke;
import java.awt.geom.Arc2D;
import java.awt.geom.Arc2D.Double;
import java.awt.geom.GeneralPath;
import java.awt.geom.Line2D;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import org.jfree.chart.HashUtilities;
import org.jfree.chart.axis.ValueAxis;
import org.jfree.chart.plot.MeterPlot;
import org.jfree.chart.plot.Plot;
import org.jfree.chart.plot.SpiderWebPlot;
import org.jfree.chart.util.ParamChecks;
import org.jfree.io.SerialUtilities;
import org.jfree.util.PaintUtilities;
import org.jfree.util.PublicCloneable;

public abstract class DialPointer extends AbstractDialLayer implements DialLayer, Cloneable, PublicCloneable, Serializable {
    int datasetIndex;
    double radius;

    public static class Pin extends DialPointer {
        static final long serialVersionUID = -8445860485367689750L;
        private transient Paint paint;
        private transient Stroke stroke;

        public Pin() {
            this(0);
        }

        public Pin(int datasetIndex) {
            super(datasetIndex);
            this.paint = Color.red;
            this.stroke = new BasicStroke(MeterPlot.DEFAULT_BORDER_SIZE, 1, 2);
        }

        public Paint getPaint() {
            return this.paint;
        }

        public void setPaint(Paint paint) {
            ParamChecks.nullNotPermitted(paint, "paint");
            this.paint = paint;
            notifyListeners(new DialLayerChangeEvent(this));
        }

        public Stroke getStroke() {
            return this.stroke;
        }

        public void setStroke(Stroke stroke) {
            ParamChecks.nullNotPermitted(stroke, "stroke");
            this.stroke = stroke;
            notifyListeners(new DialLayerChangeEvent(this));
        }

        public void draw(Graphics2D g2, DialPlot plot, Rectangle2D frame, Rectangle2D view) {
            g2.setPaint(this.paint);
            g2.setStroke(this.stroke);
            Point2D pt = new Double(DialPlot.rectangleByRadius(frame, this.radius, this.radius), plot.getScaleForDataset(this.datasetIndex).valueToAngle(plot.getValue(this.datasetIndex)), 0.0d, 0).getEndPoint();
            g2.draw(new Line2D.Double(frame.getCenterX(), frame.getCenterY(), pt.getX(), pt.getY()));
        }

        public boolean equals(Object obj) {
            if (obj == this) {
                return true;
            }
            if (!(obj instanceof Pin)) {
                return false;
            }
            Pin that = (Pin) obj;
            if (PaintUtilities.equal(this.paint, that.paint) && this.stroke.equals(that.stroke)) {
                return super.equals(obj);
            }
            return false;
        }

        public int hashCode() {
            return HashUtilities.hashCode(HashUtilities.hashCode(super.hashCode(), this.paint), this.stroke);
        }

        private void writeObject(ObjectOutputStream stream) throws IOException {
            stream.defaultWriteObject();
            SerialUtilities.writePaint(this.paint, stream);
            SerialUtilities.writeStroke(this.stroke, stream);
        }

        private void readObject(ObjectInputStream stream) throws IOException, ClassNotFoundException {
            stream.defaultReadObject();
            this.paint = SerialUtilities.readPaint(stream);
            this.stroke = SerialUtilities.readStroke(stream);
        }
    }

    public static class Pointer extends DialPointer {
        static final long serialVersionUID = -4180500011963176960L;
        private transient Paint fillPaint;
        private transient Paint outlinePaint;
        private double widthRadius;

        public Pointer() {
            this(0);
        }

        public Pointer(int datasetIndex) {
            super(datasetIndex);
            this.widthRadius = ValueAxis.DEFAULT_UPPER_MARGIN;
            this.fillPaint = Color.gray;
            this.outlinePaint = Color.black;
        }

        public double getWidthRadius() {
            return this.widthRadius;
        }

        public void setWidthRadius(double radius) {
            this.widthRadius = radius;
            notifyListeners(new DialLayerChangeEvent(this));
        }

        public Paint getFillPaint() {
            return this.fillPaint;
        }

        public void setFillPaint(Paint paint) {
            ParamChecks.nullNotPermitted(paint, "paint");
            this.fillPaint = paint;
            notifyListeners(new DialLayerChangeEvent(this));
        }

        public Paint getOutlinePaint() {
            return this.outlinePaint;
        }

        public void setOutlinePaint(Paint paint) {
            ParamChecks.nullNotPermitted(paint, "paint");
            this.outlinePaint = paint;
            notifyListeners(new DialLayerChangeEvent(this));
        }

        public void draw(Graphics2D g2, DialPlot plot, Rectangle2D frame, Rectangle2D view) {
            g2.setPaint(Color.blue);
            g2.setStroke(new BasicStroke(Plot.DEFAULT_FOREGROUND_ALPHA));
            Rectangle2D lengthRect = DialPlot.rectangleByRadius(frame, this.radius, this.radius);
            Rectangle2D widthRect = DialPlot.rectangleByRadius(frame, this.widthRadius, this.widthRadius);
            double angle = plot.getScaleForDataset(this.datasetIndex).valueToAngle(plot.getValue(this.datasetIndex));
            Point2D pt1 = new Double(lengthRect, angle, 0.0d, 0).getEndPoint();
            Arc2D arc2 = new Double(widthRect, angle - SpiderWebPlot.DEFAULT_START_ANGLE, 180.0d, 0);
            Point2D pt2 = arc2.getStartPoint();
            Point2D pt3 = arc2.getEndPoint();
            Point2D pt4 = new Double(widthRect, angle - 180.0d, 0.0d, 0).getStartPoint();
            GeneralPath gp = new GeneralPath();
            gp.moveTo((float) pt1.getX(), (float) pt1.getY());
            gp.lineTo((float) pt2.getX(), (float) pt2.getY());
            gp.lineTo((float) pt4.getX(), (float) pt4.getY());
            gp.lineTo((float) pt3.getX(), (float) pt3.getY());
            gp.closePath();
            g2.setPaint(this.fillPaint);
            g2.fill(gp);
            g2.setPaint(this.outlinePaint);
            Line2D line = new Line2D.Double(frame.getCenterX(), frame.getCenterY(), pt1.getX(), pt1.getY());
            g2.draw(line);
            line.setLine(pt2, pt3);
            g2.draw(line);
            line.setLine(pt3, pt1);
            g2.draw(line);
            line.setLine(pt2, pt1);
            g2.draw(line);
            line.setLine(pt2, pt4);
            g2.draw(line);
            line.setLine(pt3, pt4);
            g2.draw(line);
        }

        public boolean equals(Object obj) {
            if (obj == this) {
                return true;
            }
            if (!(obj instanceof Pointer)) {
                return false;
            }
            Pointer that = (Pointer) obj;
            if (this.widthRadius == that.widthRadius && PaintUtilities.equal(this.fillPaint, that.fillPaint) && PaintUtilities.equal(this.outlinePaint, that.outlinePaint)) {
                return super.equals(obj);
            }
            return false;
        }

        public int hashCode() {
            return HashUtilities.hashCode(HashUtilities.hashCode(HashUtilities.hashCode(super.hashCode(), this.widthRadius), this.fillPaint), this.outlinePaint);
        }

        private void writeObject(ObjectOutputStream stream) throws IOException {
            stream.defaultWriteObject();
            SerialUtilities.writePaint(this.fillPaint, stream);
            SerialUtilities.writePaint(this.outlinePaint, stream);
        }

        private void readObject(ObjectInputStream stream) throws IOException, ClassNotFoundException {
            stream.defaultReadObject();
            this.fillPaint = SerialUtilities.readPaint(stream);
            this.outlinePaint = SerialUtilities.readPaint(stream);
        }
    }

    protected DialPointer() {
        this(0);
    }

    protected DialPointer(int datasetIndex) {
        this.radius = 0.9d;
        this.datasetIndex = datasetIndex;
    }

    public int getDatasetIndex() {
        return this.datasetIndex;
    }

    public void setDatasetIndex(int index) {
        this.datasetIndex = index;
        notifyListeners(new DialLayerChangeEvent(this));
    }

    public double getRadius() {
        return this.radius;
    }

    public void setRadius(double radius) {
        this.radius = radius;
        notifyListeners(new DialLayerChangeEvent(this));
    }

    public boolean isClippedToWindow() {
        return true;
    }

    public boolean equals(Object obj) {
        if (obj == this) {
            return true;
        }
        if (!(obj instanceof DialPointer)) {
            return false;
        }
        DialPointer that = (DialPointer) obj;
        if (this.datasetIndex == that.datasetIndex && this.radius == that.radius) {
            return super.equals(obj);
        }
        return false;
    }

    public int hashCode() {
        return HashUtilities.hashCode(23, this.radius);
    }

    public Object clone() throws CloneNotSupportedException {
        return super.clone();
    }
}
