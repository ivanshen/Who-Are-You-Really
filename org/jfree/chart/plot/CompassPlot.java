package org.jfree.chart.plot;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.Paint;
import java.awt.Polygon;
import java.awt.Stroke;
import java.awt.geom.Area;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Ellipse2D.Double;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.Arrays;
import java.util.ResourceBundle;
import org.jfree.chart.LegendItemCollection;
import org.jfree.chart.axis.DateAxis;
import org.jfree.chart.needle.ArrowNeedle;
import org.jfree.chart.needle.LineNeedle;
import org.jfree.chart.needle.LongNeedle;
import org.jfree.chart.needle.MeterNeedle;
import org.jfree.chart.needle.MiddlePinNeedle;
import org.jfree.chart.needle.PinNeedle;
import org.jfree.chart.needle.PlumNeedle;
import org.jfree.chart.needle.PointerNeedle;
import org.jfree.chart.needle.ShipNeedle;
import org.jfree.chart.needle.WindNeedle;
import org.jfree.chart.util.ParamChecks;
import org.jfree.chart.util.ResourceBundleWrapper;
import org.jfree.data.general.DefaultValueDataset;
import org.jfree.data.general.ValueDataset;
import org.jfree.io.SerialUtilities;
import org.jfree.layout.FormatLayout;
import org.jfree.ui.Align;
import org.jfree.util.AbstractObjectList;
import org.jfree.util.LogTarget;
import org.jfree.util.ObjectUtilities;
import org.jfree.util.PaintUtilities;

public class CompassPlot extends Plot implements Cloneable, Serializable {
    public static final Font DEFAULT_LABEL_FONT;
    public static final int NO_LABELS = 0;
    public static final int VALUE_LABELS = 1;
    protected static ResourceBundle localizationResources = null;
    private static final long serialVersionUID = 6924382802125527395L;
    private transient Area a1;
    private transient Area a2;
    private transient Ellipse2D circle1;
    private transient Ellipse2D circle2;
    private Font compassFont;
    private ValueDataset[] datasets;
    private boolean drawBorder;
    private Font labelFont;
    private int labelType;
    private transient Rectangle2D rect1;
    protected double revolutionDistance;
    private transient Paint roseCenterPaint;
    private transient Paint roseHighlightPaint;
    private transient Paint rosePaint;
    private MeterNeedle[] seriesNeedle;

    static {
        DEFAULT_LABEL_FONT = new Font("SansSerif", VALUE_LABELS, 10);
        localizationResources = ResourceBundleWrapper.getBundle("org.jfree.chart.plot.LocalizationBundle");
    }

    public CompassPlot() {
        this(new DefaultValueDataset());
    }

    public CompassPlot(ValueDataset dataset) {
        this.drawBorder = false;
        this.roseHighlightPaint = Color.black;
        this.rosePaint = Color.yellow;
        this.roseCenterPaint = Color.white;
        this.compassFont = new Font("Arial", NO_LABELS, 10);
        this.datasets = new ValueDataset[VALUE_LABELS];
        this.seriesNeedle = new MeterNeedle[VALUE_LABELS];
        this.revolutionDistance = 360.0d;
        if (dataset != null) {
            this.datasets[NO_LABELS] = dataset;
            dataset.addChangeListener(this);
        }
        this.circle1 = new Double();
        this.circle2 = new Double();
        this.rect1 = new Rectangle2D.Double();
        setSeriesNeedle(NO_LABELS);
    }

    public int getLabelType() {
        return this.labelType;
    }

    public void setLabelType(int type) {
        if (type != 0 && type != VALUE_LABELS) {
            throw new IllegalArgumentException("MeterPlot.setLabelType(int): unrecognised type.");
        } else if (this.labelType != type) {
            this.labelType = type;
            fireChangeEvent();
        }
    }

    public Font getLabelFont() {
        return this.labelFont;
    }

    public void setLabelFont(Font font) {
        ParamChecks.nullNotPermitted(font, "font");
        this.labelFont = font;
        fireChangeEvent();
    }

    public Paint getRosePaint() {
        return this.rosePaint;
    }

    public void setRosePaint(Paint paint) {
        ParamChecks.nullNotPermitted(paint, "paint");
        this.rosePaint = paint;
        fireChangeEvent();
    }

    public Paint getRoseCenterPaint() {
        return this.roseCenterPaint;
    }

    public void setRoseCenterPaint(Paint paint) {
        ParamChecks.nullNotPermitted(paint, "paint");
        this.roseCenterPaint = paint;
        fireChangeEvent();
    }

    public Paint getRoseHighlightPaint() {
        return this.roseHighlightPaint;
    }

    public void setRoseHighlightPaint(Paint paint) {
        ParamChecks.nullNotPermitted(paint, "paint");
        this.roseHighlightPaint = paint;
        fireChangeEvent();
    }

    public boolean getDrawBorder() {
        return this.drawBorder;
    }

    public void setDrawBorder(boolean status) {
        this.drawBorder = status;
        fireChangeEvent();
    }

    public void setSeriesPaint(int series, Paint paint) {
        if (series >= 0 && series < this.seriesNeedle.length) {
            this.seriesNeedle[series].setFillPaint(paint);
        }
    }

    public void setSeriesOutlinePaint(int series, Paint p) {
        if (series >= 0 && series < this.seriesNeedle.length) {
            this.seriesNeedle[series].setOutlinePaint(p);
        }
    }

    public void setSeriesOutlineStroke(int series, Stroke stroke) {
        if (series >= 0 && series < this.seriesNeedle.length) {
            this.seriesNeedle[series].setOutlineStroke(stroke);
        }
    }

    public void setSeriesNeedle(int type) {
        setSeriesNeedle((int) NO_LABELS, type);
    }

    public void setSeriesNeedle(int index, int type) {
        switch (type) {
            case NO_LABELS /*0*/:
                setSeriesNeedle(index, new ArrowNeedle(true));
                setSeriesPaint(index, Color.red);
                this.seriesNeedle[index].setHighlightPaint(Color.white);
            case VALUE_LABELS /*1*/:
                setSeriesNeedle(index, new LineNeedle());
            case LogTarget.INFO /*2*/:
                MeterNeedle longNeedle = new LongNeedle();
                longNeedle.setRotateY(0.5d);
                setSeriesNeedle(index, longNeedle);
            case LogTarget.DEBUG /*3*/:
                setSeriesNeedle(index, new PinNeedle());
            case Align.WEST /*4*/:
                setSeriesNeedle(index, new PlumNeedle());
            case Align.TOP_LEFT /*5*/:
                setSeriesNeedle(index, new PointerNeedle());
            case Align.SOUTH_WEST /*6*/:
                setSeriesPaint(index, null);
                setSeriesOutlineStroke(index, new BasicStroke(MeterPlot.DEFAULT_BORDER_SIZE));
                setSeriesNeedle(index, new ShipNeedle());
            case FormatLayout.LCBLCB /*7*/:
                setSeriesPaint(index, Color.blue);
                setSeriesNeedle(index, new WindNeedle());
            case AbstractObjectList.DEFAULT_INITIAL_CAPACITY /*8*/:
                setSeriesNeedle(index, new ArrowNeedle(true));
            case Align.TOP_RIGHT /*9*/:
                setSeriesNeedle(index, new MiddlePinNeedle());
            default:
                throw new IllegalArgumentException("Unrecognised type.");
        }
    }

    public void setSeriesNeedle(int index, MeterNeedle needle) {
        if (needle != null && index < this.seriesNeedle.length) {
            this.seriesNeedle[index] = needle;
        }
        fireChangeEvent();
    }

    public ValueDataset[] getDatasets() {
        return this.datasets;
    }

    public void addDataset(ValueDataset dataset) {
        addDataset(dataset, null);
    }

    public void addDataset(ValueDataset dataset, MeterNeedle needle) {
        if (dataset != null) {
            int i = this.datasets.length + VALUE_LABELS;
            ValueDataset[] t = new ValueDataset[i];
            MeterNeedle[] p = new MeterNeedle[i];
            for (i -= 2; i >= 0; i--) {
                t[i] = this.datasets[i];
                p[i] = this.seriesNeedle[i];
            }
            i = this.datasets.length;
            t[i] = dataset;
            if (needle == null) {
                needle = p[i - 1];
            }
            p[i] = needle;
            ValueDataset[] a = this.datasets;
            MeterNeedle[] b = this.seriesNeedle;
            this.datasets = t;
            this.seriesNeedle = p;
            for (i--; i >= 0; i--) {
                a[i] = null;
                b[i] = null;
            }
            dataset.addChangeListener(this);
        }
    }

    public void draw(Graphics2D g2, Rectangle2D area, Point2D anchor, PlotState parentState, PlotRenderingInfo info) {
        int w;
        int y1;
        if (info != null) {
            info.setPlotArea(area);
        }
        getInsets().trim(area);
        if (this.drawBorder) {
            drawBackground(g2, area);
        }
        int midX = (int) (area.getWidth() / DateAxis.DEFAULT_AUTO_RANGE_MINIMUM_SIZE_IN_MILLISECONDS);
        int midY = (int) (area.getHeight() / DateAxis.DEFAULT_AUTO_RANGE_MINIMUM_SIZE_IN_MILLISECONDS);
        int radius = midX;
        if (midY < midX) {
            radius = midY;
        }
        radius--;
        int diameter = radius * 2;
        midX += (int) area.getMinX();
        midY += (int) area.getMinY();
        this.circle1.setFrame((double) (midX - radius), (double) (midY - radius), (double) diameter, (double) diameter);
        this.circle2.setFrame((double) ((midX - radius) + 15), (double) ((midY - radius) + 15), (double) (diameter - 30), (double) (diameter - 30));
        g2.setPaint(this.rosePaint);
        this.a1 = new Area(this.circle1);
        this.a2 = new Area(this.circle2);
        this.a1.subtract(this.a2);
        g2.fill(this.a1);
        g2.setPaint(this.roseCenterPaint);
        int x1 = diameter - 30;
        g2.fillOval((midX - radius) + 15, (midY - radius) + 15, x1, x1);
        g2.setPaint(this.roseHighlightPaint);
        g2.drawOval(midX - radius, midY - radius, diameter, diameter);
        x1 = diameter - 20;
        g2.drawOval((midX - radius) + 10, (midY - radius) + 10, x1, x1);
        x1 = diameter - 30;
        g2.drawOval((midX - radius) + 15, (midY - radius) + 15, x1, x1);
        x1 = diameter - 80;
        g2.drawOval((midX - radius) + 40, (midY - radius) + 40, x1, x1);
        int outerRadius = radius - 20;
        int innerRadius = radius - 32;
        for (w = NO_LABELS; w < 360; w += 15) {
            double a = Math.toRadians((double) w);
            y1 = midY - ((int) (Math.cos(a) * ((double) innerRadius)));
            g2.drawLine(midX - ((int) (Math.sin(a) * ((double) innerRadius))), y1, midX - ((int) (Math.sin(a) * ((double) outerRadius))), midY - ((int) (Math.cos(a) * ((double) outerRadius))));
        }
        g2.setPaint(this.roseHighlightPaint);
        innerRadius = radius - 26;
        for (w = 45; w < 360; w += 90) {
            a = Math.toRadians((double) w);
            Graphics2D graphics2D = g2;
            graphics2D.fillOval((midX - ((int) (Math.sin(a) * ((double) innerRadius)))) - 7, (midY - ((int) (Math.cos(a) * ((double) innerRadius)))) - 7, 14, 14);
        }
        for (w = NO_LABELS; w < 360; w += 90) {
            a = Math.toRadians((double) w);
            x1 = midX - ((int) (Math.sin(a) * ((double) innerRadius)));
            y1 = midY - ((int) (Math.cos(a) * ((double) innerRadius)));
            Polygon p = new Polygon();
            p.addPoint(x1 - 7, y1);
            p.addPoint(x1, y1 + 7);
            p.addPoint(x1 + 7, y1);
            p.addPoint(x1, y1 - 7);
            g2.fillPolygon(p);
        }
        innerRadius = radius - 42;
        Font f = getCompassFont(radius);
        g2.setFont(f);
        g2.drawString(localizationResources.getString("N"), midX - 5, (midY - innerRadius) + f.getSize());
        g2.drawString(localizationResources.getString("S"), midX - 5, (midY + innerRadius) - 5);
        g2.drawString(localizationResources.getString("W"), (midX - innerRadius) + 5, midY + 5);
        g2.drawString(localizationResources.getString("E"), (midX + innerRadius) - f.getSize(), midY + 5);
        y1 = radius / 2;
        x1 = radius / 6;
        Rectangle2D needleArea = new Rectangle2D.Double((double) (midX - x1), (double) (midY - y1), (double) (x1 * 2), (double) (y1 * 2));
        int x = this.seriesNeedle.length;
        for (int i = this.datasets.length - 1; i >= 0; i--) {
            ValueDataset data = this.datasets[i];
            if (!(data == null || data.getValue() == null)) {
                this.seriesNeedle[i % x].draw(g2, needleArea, ((data.getValue().doubleValue() % this.revolutionDistance) / this.revolutionDistance) * 360.0d);
            }
        }
        if (this.drawBorder) {
            drawOutline(g2, area);
        }
    }

    public String getPlotType() {
        return localizationResources.getString("Compass_Plot");
    }

    public LegendItemCollection getLegendItems() {
        return null;
    }

    public void zoom(double percent) {
    }

    protected Font getCompassFont(int radius) {
        float fontSize = ((float) radius) / MeterPlot.DEFAULT_CIRCLE_SIZE;
        if (fontSize < 8.0f) {
            fontSize = 8.0f;
        }
        return this.compassFont.deriveFont(fontSize);
    }

    public boolean equals(Object obj) {
        if (obj == this) {
            return true;
        }
        if (!(obj instanceof CompassPlot)) {
            return false;
        }
        if (!super.equals(obj)) {
            return false;
        }
        CompassPlot that = (CompassPlot) obj;
        if (this.labelType != that.labelType) {
            return false;
        }
        if (!ObjectUtilities.equal(this.labelFont, that.labelFont)) {
            return false;
        }
        if (this.drawBorder != that.drawBorder) {
            return false;
        }
        if (!PaintUtilities.equal(this.roseHighlightPaint, that.roseHighlightPaint)) {
            return false;
        }
        if (!PaintUtilities.equal(this.rosePaint, that.rosePaint)) {
            return false;
        }
        if (!PaintUtilities.equal(this.roseCenterPaint, that.roseCenterPaint)) {
            return false;
        }
        if (!ObjectUtilities.equal(this.compassFont, that.compassFont)) {
            return false;
        }
        if (!Arrays.equals(this.seriesNeedle, that.seriesNeedle)) {
            return false;
        }
        if (getRevolutionDistance() != that.getRevolutionDistance()) {
            return false;
        }
        return true;
    }

    public Object clone() throws CloneNotSupportedException {
        CompassPlot clone = (CompassPlot) super.clone();
        if (this.circle1 != null) {
            clone.circle1 = (Ellipse2D) this.circle1.clone();
        }
        if (this.circle2 != null) {
            clone.circle2 = (Ellipse2D) this.circle2.clone();
        }
        if (this.a1 != null) {
            clone.a1 = (Area) this.a1.clone();
        }
        if (this.a2 != null) {
            clone.a2 = (Area) this.a2.clone();
        }
        if (this.rect1 != null) {
            clone.rect1 = (Rectangle2D) this.rect1.clone();
        }
        clone.datasets = (ValueDataset[]) this.datasets.clone();
        clone.seriesNeedle = (MeterNeedle[]) this.seriesNeedle.clone();
        for (int i = NO_LABELS; i < this.datasets.length; i += VALUE_LABELS) {
            if (clone.datasets[i] != null) {
                clone.datasets[i].addChangeListener(clone);
            }
        }
        return clone;
    }

    public void setRevolutionDistance(double size) {
        if (size > 0.0d) {
            this.revolutionDistance = size;
        }
    }

    public double getRevolutionDistance() {
        return this.revolutionDistance;
    }

    private void writeObject(ObjectOutputStream stream) throws IOException {
        stream.defaultWriteObject();
        SerialUtilities.writePaint(this.rosePaint, stream);
        SerialUtilities.writePaint(this.roseCenterPaint, stream);
        SerialUtilities.writePaint(this.roseHighlightPaint, stream);
    }

    private void readObject(ObjectInputStream stream) throws IOException, ClassNotFoundException {
        stream.defaultReadObject();
        this.rosePaint = SerialUtilities.readPaint(stream);
        this.roseCenterPaint = SerialUtilities.readPaint(stream);
        this.roseHighlightPaint = SerialUtilities.readPaint(stream);
    }
}
