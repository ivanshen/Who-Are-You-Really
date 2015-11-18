package org.jfree.chart.annotations;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Paint;
import java.awt.Stroke;
import java.awt.geom.Rectangle2D;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import org.jfree.chart.HashUtilities;
import org.jfree.chart.axis.CategoryAnchor;
import org.jfree.chart.axis.CategoryAxis;
import org.jfree.chart.axis.ValueAxis;
import org.jfree.chart.plot.CategoryPlot;
import org.jfree.chart.plot.Plot;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.util.ParamChecks;
import org.jfree.data.category.CategoryDataset;
import org.jfree.io.SerialUtilities;
import org.jfree.ui.RectangleEdge;
import org.jfree.util.ObjectUtilities;
import org.jfree.util.PaintUtilities;
import org.jfree.util.PublicCloneable;

public class CategoryLineAnnotation extends AbstractAnnotation implements CategoryAnnotation, Cloneable, PublicCloneable, Serializable {
    static final long serialVersionUID = 3477740483341587984L;
    private Comparable category1;
    private Comparable category2;
    private transient Paint paint;
    private transient Stroke stroke;
    private double value1;
    private double value2;

    public CategoryLineAnnotation(Comparable category1, double value1, Comparable category2, double value2, Paint paint, Stroke stroke) {
        this.paint = Color.black;
        this.stroke = new BasicStroke(Plot.DEFAULT_FOREGROUND_ALPHA);
        ParamChecks.nullNotPermitted(category1, "category1");
        ParamChecks.nullNotPermitted(category2, "category2");
        ParamChecks.nullNotPermitted(paint, "paint");
        ParamChecks.nullNotPermitted(stroke, "stroke");
        this.category1 = category1;
        this.value1 = value1;
        this.category2 = category2;
        this.value2 = value2;
        this.paint = paint;
        this.stroke = stroke;
    }

    public Comparable getCategory1() {
        return this.category1;
    }

    public void setCategory1(Comparable category) {
        ParamChecks.nullNotPermitted(category, "category");
        this.category1 = category;
        fireAnnotationChanged();
    }

    public double getValue1() {
        return this.value1;
    }

    public void setValue1(double value) {
        this.value1 = value;
        fireAnnotationChanged();
    }

    public Comparable getCategory2() {
        return this.category2;
    }

    public void setCategory2(Comparable category) {
        ParamChecks.nullNotPermitted(category, "category");
        this.category2 = category;
        fireAnnotationChanged();
    }

    public double getValue2() {
        return this.value2;
    }

    public void setValue2(double value) {
        this.value2 = value;
        fireAnnotationChanged();
    }

    public Paint getPaint() {
        return this.paint;
    }

    public void setPaint(Paint paint) {
        ParamChecks.nullNotPermitted(paint, "paint");
        this.paint = paint;
        fireAnnotationChanged();
    }

    public Stroke getStroke() {
        return this.stroke;
    }

    public void setStroke(Stroke stroke) {
        ParamChecks.nullNotPermitted(stroke, "stroke");
        this.stroke = stroke;
        fireAnnotationChanged();
    }

    public void draw(Graphics2D g2, CategoryPlot plot, Rectangle2D dataArea, CategoryAxis domainAxis, ValueAxis rangeAxis) {
        CategoryDataset dataset = plot.getDataset();
        int catIndex1 = dataset.getColumnIndex(this.category1);
        int catIndex2 = dataset.getColumnIndex(this.category2);
        int catCount = dataset.getColumnCount();
        double lineX1 = 0.0d;
        double lineY1 = 0.0d;
        double lineX2 = 0.0d;
        double lineY2 = 0.0d;
        PlotOrientation orientation = plot.getOrientation();
        RectangleEdge domainEdge = Plot.resolveDomainAxisLocation(plot.getDomainAxisLocation(), orientation);
        RectangleEdge rangeEdge = Plot.resolveRangeAxisLocation(plot.getRangeAxisLocation(), orientation);
        if (orientation == PlotOrientation.HORIZONTAL) {
            lineY1 = domainAxis.getCategoryJava2DCoordinate(CategoryAnchor.MIDDLE, catIndex1, catCount, dataArea, domainEdge);
            lineX1 = rangeAxis.valueToJava2D(this.value1, dataArea, rangeEdge);
            lineY2 = domainAxis.getCategoryJava2DCoordinate(CategoryAnchor.MIDDLE, catIndex2, catCount, dataArea, domainEdge);
            lineX2 = rangeAxis.valueToJava2D(this.value2, dataArea, rangeEdge);
        } else if (orientation == PlotOrientation.VERTICAL) {
            lineX1 = domainAxis.getCategoryJava2DCoordinate(CategoryAnchor.MIDDLE, catIndex1, catCount, dataArea, domainEdge);
            lineY1 = rangeAxis.valueToJava2D(this.value1, dataArea, rangeEdge);
            lineX2 = domainAxis.getCategoryJava2DCoordinate(CategoryAnchor.MIDDLE, catIndex2, catCount, dataArea, domainEdge);
            lineY2 = rangeAxis.valueToJava2D(this.value2, dataArea, rangeEdge);
        }
        g2.setPaint(this.paint);
        g2.setStroke(this.stroke);
        g2.drawLine((int) lineX1, (int) lineY1, (int) lineX2, (int) lineY2);
    }

    public boolean equals(Object obj) {
        if (obj == this) {
            return true;
        }
        if (!(obj instanceof CategoryLineAnnotation)) {
            return false;
        }
        CategoryLineAnnotation that = (CategoryLineAnnotation) obj;
        if (!this.category1.equals(that.getCategory1())) {
            return false;
        }
        if (this.value1 != that.getValue1()) {
            return false;
        }
        if (!this.category2.equals(that.getCategory2())) {
            return false;
        }
        if (this.value2 != that.getValue2()) {
            return false;
        }
        if (!PaintUtilities.equal(this.paint, that.paint)) {
            return false;
        }
        if (ObjectUtilities.equal(this.stroke, that.stroke)) {
            return true;
        }
        return false;
    }

    public int hashCode() {
        int result = this.category1.hashCode() + 7141;
        long temp = Double.doubleToLongBits(this.value1);
        result = (((result * 37) + ((int) ((temp >>> 32) ^ temp))) * 37) + this.category2.hashCode();
        temp = Double.doubleToLongBits(this.value2);
        return (((((result * 37) + ((int) ((temp >>> 32) ^ temp))) * 37) + HashUtilities.hashCodeForPaint(this.paint)) * 37) + this.stroke.hashCode();
    }

    public Object clone() throws CloneNotSupportedException {
        return super.clone();
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