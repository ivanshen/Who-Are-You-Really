package org.jfree.chart.block;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Paint;
import java.awt.RenderingHints;
import java.awt.Stroke;
import java.awt.geom.Line2D;
import java.awt.geom.Line2D.Double;
import java.awt.geom.Rectangle2D;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import org.jfree.chart.axis.DateAxis;
import org.jfree.chart.plot.Plot;
import org.jfree.chart.util.ParamChecks;
import org.jfree.data.xy.NormalizedMatrixSeries;
import org.jfree.io.SerialUtilities;
import org.jfree.ui.RectangleInsets;
import org.jfree.util.ObjectUtilities;
import org.jfree.util.PaintUtilities;

public class LineBorder implements BlockFrame, Serializable {
    static final long serialVersionUID = 4630356736707233924L;
    private RectangleInsets insets;
    private transient Paint paint;
    private transient Stroke stroke;

    public LineBorder() {
        this(Color.black, new BasicStroke(Plot.DEFAULT_FOREGROUND_ALPHA), new RectangleInsets(NormalizedMatrixSeries.DEFAULT_SCALE_FACTOR, NormalizedMatrixSeries.DEFAULT_SCALE_FACTOR, NormalizedMatrixSeries.DEFAULT_SCALE_FACTOR, NormalizedMatrixSeries.DEFAULT_SCALE_FACTOR));
    }

    public LineBorder(Paint paint, Stroke stroke, RectangleInsets insets) {
        ParamChecks.nullNotPermitted(paint, "paint");
        ParamChecks.nullNotPermitted(stroke, "stroke");
        ParamChecks.nullNotPermitted(insets, "insets");
        this.paint = paint;
        this.stroke = stroke;
        this.insets = insets;
    }

    public Paint getPaint() {
        return this.paint;
    }

    public RectangleInsets getInsets() {
        return this.insets;
    }

    public Stroke getStroke() {
        return this.stroke;
    }

    public void draw(Graphics2D g2, Rectangle2D area) {
        double w = area.getWidth();
        double h = area.getHeight();
        if (w > 0.0d && h > 0.0d) {
            double t = this.insets.calculateTopInset(h);
            double b = this.insets.calculateBottomInset(h);
            double l = this.insets.calculateLeftInset(w);
            double r = this.insets.calculateRightInset(w);
            double x = area.getX();
            double y = area.getY();
            double x0 = x + (l / DateAxis.DEFAULT_AUTO_RANGE_MINIMUM_SIZE_IN_MILLISECONDS);
            double x1 = (x + w) - (r / DateAxis.DEFAULT_AUTO_RANGE_MINIMUM_SIZE_IN_MILLISECONDS);
            double y0 = (y + h) - (b / DateAxis.DEFAULT_AUTO_RANGE_MINIMUM_SIZE_IN_MILLISECONDS);
            double y1 = y + (t / DateAxis.DEFAULT_AUTO_RANGE_MINIMUM_SIZE_IN_MILLISECONDS);
            g2.setPaint(getPaint());
            g2.setStroke(getStroke());
            Object saved = g2.getRenderingHint(RenderingHints.KEY_STROKE_CONTROL);
            g2.setRenderingHint(RenderingHints.KEY_STROKE_CONTROL, RenderingHints.VALUE_STROKE_NORMALIZE);
            Line2D line = new Double();
            if (t > 0.0d) {
                line.setLine(x0, y1, x1, y1);
                g2.draw(line);
            }
            if (b > 0.0d) {
                line.setLine(x0, y0, x1, y0);
                g2.draw(line);
            }
            if (l > 0.0d) {
                line.setLine(x0, y0, x0, y1);
                g2.draw(line);
            }
            if (r > 0.0d) {
                line.setLine(x1, y0, x1, y1);
                g2.draw(line);
            }
            g2.setRenderingHint(RenderingHints.KEY_STROKE_CONTROL, saved);
        }
    }

    public boolean equals(Object obj) {
        if (obj == this) {
            return true;
        }
        if (!(obj instanceof LineBorder)) {
            return false;
        }
        LineBorder that = (LineBorder) obj;
        if (!PaintUtilities.equal(this.paint, that.paint)) {
            return false;
        }
        if (!ObjectUtilities.equal(this.stroke, that.stroke)) {
            return false;
        }
        if (this.insets.equals(that.insets)) {
            return true;
        }
        return false;
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
