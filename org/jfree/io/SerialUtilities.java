package org.jfree.io;

import java.awt.AlphaComposite;
import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Composite;
import java.awt.GradientPaint;
import java.awt.Paint;
import java.awt.Shape;
import java.awt.Stroke;
import java.awt.geom.Arc2D;
import java.awt.geom.Ellipse2D;
import java.awt.geom.GeneralPath;
import java.awt.geom.Line2D;
import java.awt.geom.Line2D.Double;
import java.awt.geom.PathIterator;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.text.AttributedCharacterIterator;
import java.text.AttributedString;
import java.util.HashMap;
import java.util.Map;
import org.jfree.ui.Align;
import org.jfree.util.LogTarget;

public class SerialUtilities {
    private SerialUtilities() {
    }

    public static boolean isSerializable(Class c) {
        return Serializable.class.isAssignableFrom(c);
    }

    public static Paint readPaint(ObjectInputStream stream) throws IOException, ClassNotFoundException {
        if (stream == null) {
            throw new IllegalArgumentException("Null 'stream' argument.");
        } else if (stream.readBoolean()) {
            return null;
        } else {
            Class c = (Class) stream.readObject();
            if (isSerializable(c)) {
                return (Paint) stream.readObject();
            }
            if (c.equals(GradientPaint.class)) {
                return new GradientPaint(stream.readFloat(), stream.readFloat(), (Color) stream.readObject(), stream.readFloat(), stream.readFloat(), (Color) stream.readObject(), stream.readBoolean());
            }
            return null;
        }
    }

    public static void writePaint(Paint paint, ObjectOutputStream stream) throws IOException {
        if (stream == null) {
            throw new IllegalArgumentException("Null 'stream' argument.");
        } else if (paint != null) {
            stream.writeBoolean(false);
            stream.writeObject(paint.getClass());
            if (paint instanceof Serializable) {
                stream.writeObject(paint);
            } else if (paint instanceof GradientPaint) {
                GradientPaint gp = (GradientPaint) paint;
                stream.writeFloat((float) gp.getPoint1().getX());
                stream.writeFloat((float) gp.getPoint1().getY());
                stream.writeObject(gp.getColor1());
                stream.writeFloat((float) gp.getPoint2().getX());
                stream.writeFloat((float) gp.getPoint2().getY());
                stream.writeObject(gp.getColor2());
                stream.writeBoolean(gp.isCyclic());
            }
        } else {
            stream.writeBoolean(true);
        }
    }

    public static Stroke readStroke(ObjectInputStream stream) throws IOException, ClassNotFoundException {
        if (stream == null) {
            throw new IllegalArgumentException("Null 'stream' argument.");
        } else if (stream.readBoolean()) {
            return null;
        } else {
            if (!((Class) stream.readObject()).equals(BasicStroke.class)) {
                return (Stroke) stream.readObject();
            }
            return new BasicStroke(stream.readFloat(), stream.readInt(), stream.readInt(), stream.readFloat(), (float[]) stream.readObject(), stream.readFloat());
        }
    }

    public static void writeStroke(Stroke stroke, ObjectOutputStream stream) throws IOException {
        if (stream == null) {
            throw new IllegalArgumentException("Null 'stream' argument.");
        } else if (stroke != null) {
            stream.writeBoolean(false);
            if (stroke instanceof BasicStroke) {
                BasicStroke s = (BasicStroke) stroke;
                stream.writeObject(BasicStroke.class);
                stream.writeFloat(s.getLineWidth());
                stream.writeInt(s.getEndCap());
                stream.writeInt(s.getLineJoin());
                stream.writeFloat(s.getMiterLimit());
                stream.writeObject(s.getDashArray());
                stream.writeFloat(s.getDashPhase());
                return;
            }
            stream.writeObject(stroke.getClass());
            stream.writeObject(stroke);
        } else {
            stream.writeBoolean(true);
        }
    }

    public static Composite readComposite(ObjectInputStream stream) throws IOException, ClassNotFoundException {
        if (stream == null) {
            throw new IllegalArgumentException("Null 'stream' argument.");
        } else if (stream.readBoolean()) {
            return null;
        } else {
            Class c = (Class) stream.readObject();
            if (isSerializable(c)) {
                return (Composite) stream.readObject();
            }
            if (c.equals(AlphaComposite.class)) {
                return AlphaComposite.getInstance(stream.readInt(), stream.readFloat());
            }
            return null;
        }
    }

    public static void writeComposite(Composite composite, ObjectOutputStream stream) throws IOException {
        if (stream == null) {
            throw new IllegalArgumentException("Null 'stream' argument.");
        } else if (composite != null) {
            stream.writeBoolean(false);
            stream.writeObject(composite.getClass());
            if (composite instanceof Serializable) {
                stream.writeObject(composite);
            } else if (composite instanceof AlphaComposite) {
                AlphaComposite ac = (AlphaComposite) composite;
                stream.writeInt(ac.getRule());
                stream.writeFloat(ac.getAlpha());
            }
        } else {
            stream.writeBoolean(true);
        }
    }

    public static Shape readShape(ObjectInputStream stream) throws IOException, ClassNotFoundException {
        if (stream == null) {
            throw new IllegalArgumentException("Null 'stream' argument.");
        } else if (stream.readBoolean()) {
            return null;
        } else {
            Class c = (Class) stream.readObject();
            if (c.equals(Line2D.class)) {
                return new Double(stream.readDouble(), stream.readDouble(), stream.readDouble(), stream.readDouble());
            }
            if (c.equals(Rectangle2D.class)) {
                return new Rectangle2D.Double(stream.readDouble(), stream.readDouble(), stream.readDouble(), stream.readDouble());
            }
            if (c.equals(Ellipse2D.class)) {
                return new Ellipse2D.Double(stream.readDouble(), stream.readDouble(), stream.readDouble(), stream.readDouble());
            }
            if (c.equals(Arc2D.class)) {
                return new Arc2D.Double(stream.readDouble(), stream.readDouble(), stream.readDouble(), stream.readDouble(), stream.readDouble(), stream.readDouble(), stream.readInt());
            }
            if (!c.equals(GeneralPath.class)) {
                return (Shape) stream.readObject();
            }
            GeneralPath gp = new GeneralPath();
            float[] args = new float[6];
            boolean hasNext = stream.readBoolean();
            while (!hasNext) {
                int type = stream.readInt();
                for (int i = 0; i < 6; i++) {
                    args[i] = stream.readFloat();
                }
                switch (type) {
                    case LogTarget.ERROR /*0*/:
                        gp.moveTo(args[0], args[1]);
                        break;
                    case LogTarget.WARN /*1*/:
                        gp.lineTo(args[0], args[1]);
                        break;
                    case LogTarget.INFO /*2*/:
                        gp.quadTo(args[0], args[1], args[2], args[3]);
                        break;
                    case LogTarget.DEBUG /*3*/:
                        gp.curveTo(args[0], args[1], args[2], args[3], args[4], args[5]);
                        break;
                    case Align.WEST /*4*/:
                        gp.closePath();
                        break;
                    default:
                        throw new RuntimeException("JFreeChart - No path exists");
                }
                gp.setWindingRule(stream.readInt());
                hasNext = stream.readBoolean();
            }
            return gp;
        }
    }

    public static void writeShape(Shape shape, ObjectOutputStream stream) throws IOException {
        if (stream == null) {
            throw new IllegalArgumentException("Null 'stream' argument.");
        } else if (shape != null) {
            stream.writeBoolean(false);
            if (shape instanceof Line2D) {
                Line2D line = (Line2D) shape;
                stream.writeObject(Line2D.class);
                stream.writeDouble(line.getX1());
                stream.writeDouble(line.getY1());
                stream.writeDouble(line.getX2());
                stream.writeDouble(line.getY2());
            } else if (shape instanceof Rectangle2D) {
                Rectangle2D rectangle = (Rectangle2D) shape;
                stream.writeObject(Rectangle2D.class);
                stream.writeDouble(rectangle.getX());
                stream.writeDouble(rectangle.getY());
                stream.writeDouble(rectangle.getWidth());
                stream.writeDouble(rectangle.getHeight());
            } else if (shape instanceof Ellipse2D) {
                Ellipse2D ellipse = (Ellipse2D) shape;
                stream.writeObject(Ellipse2D.class);
                stream.writeDouble(ellipse.getX());
                stream.writeDouble(ellipse.getY());
                stream.writeDouble(ellipse.getWidth());
                stream.writeDouble(ellipse.getHeight());
            } else if (shape instanceof Arc2D) {
                Arc2D arc = (Arc2D) shape;
                stream.writeObject(Arc2D.class);
                stream.writeDouble(arc.getX());
                stream.writeDouble(arc.getY());
                stream.writeDouble(arc.getWidth());
                stream.writeDouble(arc.getHeight());
                stream.writeDouble(arc.getAngleStart());
                stream.writeDouble(arc.getAngleExtent());
                stream.writeInt(arc.getArcType());
            } else if (shape instanceof GeneralPath) {
                stream.writeObject(GeneralPath.class);
                PathIterator pi = shape.getPathIterator(null);
                float[] args = new float[6];
                stream.writeBoolean(pi.isDone());
                while (!pi.isDone()) {
                    stream.writeInt(pi.currentSegment(args));
                    for (int i = 0; i < 6; i++) {
                        stream.writeFloat(args[i]);
                    }
                    stream.writeInt(pi.getWindingRule());
                    pi.next();
                    stream.writeBoolean(pi.isDone());
                }
            } else {
                stream.writeObject(shape.getClass());
                stream.writeObject(shape);
            }
        } else {
            stream.writeBoolean(true);
        }
    }

    public static Point2D readPoint2D(ObjectInputStream stream) throws IOException {
        if (stream == null) {
            throw new IllegalArgumentException("Null 'stream' argument.");
        } else if (stream.readBoolean()) {
            return null;
        } else {
            return new Point2D.Double(stream.readDouble(), stream.readDouble());
        }
    }

    public static void writePoint2D(Point2D p, ObjectOutputStream stream) throws IOException {
        if (stream == null) {
            throw new IllegalArgumentException("Null 'stream' argument.");
        } else if (p != null) {
            stream.writeBoolean(false);
            stream.writeDouble(p.getX());
            stream.writeDouble(p.getY());
        } else {
            stream.writeBoolean(true);
        }
    }

    public static AttributedString readAttributedString(ObjectInputStream stream) throws IOException, ClassNotFoundException {
        if (stream == null) {
            throw new IllegalArgumentException("Null 'stream' argument.");
        }
        AttributedString result = null;
        if (!stream.readBoolean()) {
            result = new AttributedString((String) stream.readObject());
            char c = stream.readChar();
            int start = 0;
            while (c != '\uffff') {
                int limit = stream.readInt();
                result.addAttributes((Map) stream.readObject(), start, limit);
                start = limit;
                c = stream.readChar();
            }
        }
        return result;
    }

    public static void writeAttributedString(AttributedString as, ObjectOutputStream stream) throws IOException {
        if (stream == null) {
            throw new IllegalArgumentException("Null 'stream' argument.");
        } else if (as != null) {
            char current;
            stream.writeBoolean(false);
            AttributedCharacterIterator aci = as.getIterator();
            StringBuffer plainStr = new StringBuffer();
            for (current = aci.first(); current != '\uffff'; current = aci.next()) {
                plainStr = plainStr.append(current);
            }
            stream.writeObject(plainStr.toString());
            current = aci.first();
            int begin = aci.getBeginIndex();
            while (current != '\uffff') {
                stream.writeChar(current);
                int limit = aci.getRunLimit();
                stream.writeInt(limit - begin);
                stream.writeObject(new HashMap(aci.getAttributes()));
                current = aci.setIndex(limit);
            }
            stream.writeChar(65535);
        } else {
            stream.writeBoolean(true);
        }
    }
}
