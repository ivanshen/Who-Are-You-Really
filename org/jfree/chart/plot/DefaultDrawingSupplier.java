package org.jfree.chart.plot;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Paint;
import java.awt.Polygon;
import java.awt.Shape;
import java.awt.Stroke;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Rectangle2D.Double;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.Arrays;
import org.jfree.chart.ChartColor;
import org.jfree.chart.axis.DateAxis;
import org.jfree.io.SerialUtilities;
import org.jfree.util.PublicCloneable;
import org.jfree.util.ShapeUtilities;

public class DefaultDrawingSupplier implements DrawingSupplier, Cloneable, PublicCloneable, Serializable {
    public static final Paint[] DEFAULT_FILL_PAINT_SEQUENCE;
    public static final Paint[] DEFAULT_OUTLINE_PAINT_SEQUENCE;
    public static final Stroke[] DEFAULT_OUTLINE_STROKE_SEQUENCE;
    public static final Paint[] DEFAULT_PAINT_SEQUENCE;
    public static final Shape[] DEFAULT_SHAPE_SEQUENCE;
    public static final Stroke[] DEFAULT_STROKE_SEQUENCE;
    private static final long serialVersionUID = -7339847061039422538L;
    private int fillPaintIndex;
    private transient Paint[] fillPaintSequence;
    private int outlinePaintIndex;
    private transient Paint[] outlinePaintSequence;
    private int outlineStrokeIndex;
    private transient Stroke[] outlineStrokeSequence;
    private int paintIndex;
    private transient Paint[] paintSequence;
    private int shapeIndex;
    private transient Shape[] shapeSequence;
    private int strokeIndex;
    private transient Stroke[] strokeSequence;

    static {
        DEFAULT_PAINT_SEQUENCE = ChartColor.createDefaultPaintArray();
        DEFAULT_OUTLINE_PAINT_SEQUENCE = new Paint[]{Color.lightGray};
        DEFAULT_FILL_PAINT_SEQUENCE = new Paint[]{Color.white};
        DEFAULT_STROKE_SEQUENCE = new Stroke[]{new BasicStroke(Plot.DEFAULT_FOREGROUND_ALPHA, 2, 2)};
        DEFAULT_OUTLINE_STROKE_SEQUENCE = new Stroke[]{new BasicStroke(Plot.DEFAULT_FOREGROUND_ALPHA, 2, 2)};
        DEFAULT_SHAPE_SEQUENCE = createStandardSeriesShapes();
    }

    public DefaultDrawingSupplier() {
        this(DEFAULT_PAINT_SEQUENCE, DEFAULT_FILL_PAINT_SEQUENCE, DEFAULT_OUTLINE_PAINT_SEQUENCE, DEFAULT_STROKE_SEQUENCE, DEFAULT_OUTLINE_STROKE_SEQUENCE, DEFAULT_SHAPE_SEQUENCE);
    }

    public DefaultDrawingSupplier(Paint[] paintSequence, Paint[] outlinePaintSequence, Stroke[] strokeSequence, Stroke[] outlineStrokeSequence, Shape[] shapeSequence) {
        this.paintSequence = paintSequence;
        this.fillPaintSequence = DEFAULT_FILL_PAINT_SEQUENCE;
        this.outlinePaintSequence = outlinePaintSequence;
        this.strokeSequence = strokeSequence;
        this.outlineStrokeSequence = outlineStrokeSequence;
        this.shapeSequence = shapeSequence;
    }

    public DefaultDrawingSupplier(Paint[] paintSequence, Paint[] fillPaintSequence, Paint[] outlinePaintSequence, Stroke[] strokeSequence, Stroke[] outlineStrokeSequence, Shape[] shapeSequence) {
        this.paintSequence = paintSequence;
        this.fillPaintSequence = fillPaintSequence;
        this.outlinePaintSequence = outlinePaintSequence;
        this.strokeSequence = strokeSequence;
        this.outlineStrokeSequence = outlineStrokeSequence;
        this.shapeSequence = shapeSequence;
    }

    public Paint getNextPaint() {
        Paint result = this.paintSequence[this.paintIndex % this.paintSequence.length];
        this.paintIndex++;
        return result;
    }

    public Paint getNextOutlinePaint() {
        Paint result = this.outlinePaintSequence[this.outlinePaintIndex % this.outlinePaintSequence.length];
        this.outlinePaintIndex++;
        return result;
    }

    public Paint getNextFillPaint() {
        Paint result = this.fillPaintSequence[this.fillPaintIndex % this.fillPaintSequence.length];
        this.fillPaintIndex++;
        return result;
    }

    public Stroke getNextStroke() {
        Stroke result = this.strokeSequence[this.strokeIndex % this.strokeSequence.length];
        this.strokeIndex++;
        return result;
    }

    public Stroke getNextOutlineStroke() {
        Stroke result = this.outlineStrokeSequence[this.outlineStrokeIndex % this.outlineStrokeSequence.length];
        this.outlineStrokeIndex++;
        return result;
    }

    public Shape getNextShape() {
        Shape result = this.shapeSequence[this.shapeIndex % this.shapeSequence.length];
        this.shapeIndex++;
        return result;
    }

    public static Shape[] createStandardSeriesShapes() {
        Shape[] result = new Shape[10];
        double delta = 6.0d / DateAxis.DEFAULT_AUTO_RANGE_MINIMUM_SIZE_IN_MILLISECONDS;
        result[0] = new Double(-delta, -delta, 6.0d, 6.0d);
        result[1] = new Ellipse2D.Double(-delta, -delta, 6.0d, 6.0d);
        result[2] = new Polygon(intArray(0.0d, delta, -delta), intArray(-delta, delta, delta), 3);
        result[3] = new Polygon(intArray(0.0d, delta, 0.0d, -delta), intArray(-delta, 0.0d, delta, 0.0d), 4);
        result[4] = new Double(-delta, (-delta) / DateAxis.DEFAULT_AUTO_RANGE_MINIMUM_SIZE_IN_MILLISECONDS, 6.0d, 6.0d / DateAxis.DEFAULT_AUTO_RANGE_MINIMUM_SIZE_IN_MILLISECONDS);
        result[5] = new Polygon(intArray(-delta, delta, 0.0d), intArray(-delta, -delta, delta), 3);
        result[6] = new Ellipse2D.Double(-delta, (-delta) / DateAxis.DEFAULT_AUTO_RANGE_MINIMUM_SIZE_IN_MILLISECONDS, 6.0d, 6.0d / DateAxis.DEFAULT_AUTO_RANGE_MINIMUM_SIZE_IN_MILLISECONDS);
        result[7] = new Polygon(intArray(-delta, delta, -delta), intArray(-delta, 0.0d, delta), 3);
        result[8] = new Double((-delta) / DateAxis.DEFAULT_AUTO_RANGE_MINIMUM_SIZE_IN_MILLISECONDS, -delta, 6.0d / DateAxis.DEFAULT_AUTO_RANGE_MINIMUM_SIZE_IN_MILLISECONDS, 6.0d);
        result[9] = new Polygon(intArray(-delta, delta, delta), intArray(0.0d, -delta, delta), 3);
        return result;
    }

    public boolean equals(Object obj) {
        if (obj == this) {
            return true;
        }
        if (!(obj instanceof DefaultDrawingSupplier)) {
            return false;
        }
        DefaultDrawingSupplier that = (DefaultDrawingSupplier) obj;
        if (!Arrays.equals(this.paintSequence, that.paintSequence)) {
            return false;
        }
        if (this.paintIndex != that.paintIndex) {
            return false;
        }
        if (!Arrays.equals(this.outlinePaintSequence, that.outlinePaintSequence)) {
            return false;
        }
        if (this.outlinePaintIndex != that.outlinePaintIndex) {
            return false;
        }
        if (!Arrays.equals(this.strokeSequence, that.strokeSequence)) {
            return false;
        }
        if (this.strokeIndex != that.strokeIndex) {
            return false;
        }
        if (!Arrays.equals(this.outlineStrokeSequence, that.outlineStrokeSequence)) {
            return false;
        }
        if (this.outlineStrokeIndex != that.outlineStrokeIndex) {
            return false;
        }
        if (!equalShapes(this.shapeSequence, that.shapeSequence)) {
            return false;
        }
        if (this.shapeIndex != that.shapeIndex) {
            return false;
        }
        return true;
    }

    private boolean equalShapes(Shape[] s1, Shape[] s2) {
        boolean z = true;
        if (s1 == null) {
            if (s2 != null) {
                z = false;
            }
            return z;
        } else if (s2 == null || s1.length != s2.length) {
            return false;
        } else {
            for (int i = 0; i < s1.length; i++) {
                if (!ShapeUtilities.equal(s1[i], s2[i])) {
                    return false;
                }
            }
            return true;
        }
    }

    private void writeObject(ObjectOutputStream stream) throws IOException {
        stream.defaultWriteObject();
        stream.writeInt(paintCount);
        for (Paint writePaint : this.paintSequence) {
            SerialUtilities.writePaint(writePaint, stream);
        }
        stream.writeInt(outlinePaintCount);
        for (Paint writePaint2 : this.outlinePaintSequence) {
            SerialUtilities.writePaint(writePaint2, stream);
        }
        stream.writeInt(strokeCount);
        for (Stroke writeStroke : this.strokeSequence) {
            SerialUtilities.writeStroke(writeStroke, stream);
        }
        stream.writeInt(outlineStrokeCount);
        for (Stroke writeStroke2 : this.outlineStrokeSequence) {
            SerialUtilities.writeStroke(writeStroke2, stream);
        }
        stream.writeInt(shapeCount);
        for (Shape writeShape : this.shapeSequence) {
            SerialUtilities.writeShape(writeShape, stream);
        }
    }

    private void readObject(ObjectInputStream stream) throws IOException, ClassNotFoundException {
        int i;
        stream.defaultReadObject();
        int paintCount = stream.readInt();
        this.paintSequence = new Paint[paintCount];
        for (i = 0; i < paintCount; i++) {
            this.paintSequence[i] = SerialUtilities.readPaint(stream);
        }
        int outlinePaintCount = stream.readInt();
        this.outlinePaintSequence = new Paint[outlinePaintCount];
        for (i = 0; i < outlinePaintCount; i++) {
            this.outlinePaintSequence[i] = SerialUtilities.readPaint(stream);
        }
        int strokeCount = stream.readInt();
        this.strokeSequence = new Stroke[strokeCount];
        for (i = 0; i < strokeCount; i++) {
            this.strokeSequence[i] = SerialUtilities.readStroke(stream);
        }
        int outlineStrokeCount = stream.readInt();
        this.outlineStrokeSequence = new Stroke[outlineStrokeCount];
        for (i = 0; i < outlineStrokeCount; i++) {
            this.outlineStrokeSequence[i] = SerialUtilities.readStroke(stream);
        }
        int shapeCount = stream.readInt();
        this.shapeSequence = new Shape[shapeCount];
        for (i = 0; i < shapeCount; i++) {
            this.shapeSequence[i] = SerialUtilities.readShape(stream);
        }
    }

    private static int[] intArray(double a, double b, double c) {
        return new int[]{(int) a, (int) b, (int) c};
    }

    private static int[] intArray(double a, double b, double c, double d) {
        return new int[]{(int) a, (int) b, (int) c, (int) d};
    }

    public Object clone() throws CloneNotSupportedException {
        return (DefaultDrawingSupplier) super.clone();
    }
}
