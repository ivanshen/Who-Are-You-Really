package org.jfree.chart.plot;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics2D;
import java.awt.Paint;
import java.awt.Stroke;
import java.awt.geom.Area;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Line2D;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.awt.geom.RoundRectangle2D;
import java.awt.geom.RoundRectangle2D.Double;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.Arrays;
import java.util.ResourceBundle;
import org.jfree.chart.LegendItemCollection;
import org.jfree.chart.annotations.XYPointerAnnotation;
import org.jfree.chart.axis.Axis;
import org.jfree.chart.axis.DateAxis;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.axis.ValueAxis;
import org.jfree.chart.util.ParamChecks;
import org.jfree.chart.util.ResourceBundleWrapper;
import org.jfree.data.Range;
import org.jfree.data.general.DatasetChangeEvent;
import org.jfree.data.general.DefaultValueDataset;
import org.jfree.data.general.ValueDataset;
import org.jfree.io.SerialUtilities;
import org.jfree.ui.RectangleEdge;
import org.jfree.ui.RectangleInsets;
import org.jfree.util.ObjectUtilities;
import org.jfree.util.PaintUtilities;
import org.jfree.util.UnitType;

public class ThermometerPlot extends Plot implements ValueAxisPlot, Zoomable, Cloneable, Serializable {
    protected static final int AXIS_GAP = 10;
    public static final int BULB = 3;
    protected static final int BULB_DIAMETER = 80;
    protected static final int BULB_RADIUS = 40;
    protected static final int COLUMN_DIAMETER = 40;
    protected static final int COLUMN_RADIUS = 20;
    public static final int CRITICAL = 2;
    protected static final int DEFAULT_BULB_RADIUS = 40;
    protected static final int DEFAULT_COLUMN_RADIUS = 20;
    protected static final int DEFAULT_GAP = 5;
    protected static final double DEFAULT_LOWER_BOUND = 0.0d;
    protected static final double DEFAULT_UPPER_BOUND = 100.0d;
    protected static final int DISPLAY_HIGH = 3;
    protected static final int DISPLAY_LOW = 2;
    protected static final int GAP_DIAMETER = 10;
    protected static final int GAP_RADIUS = 5;
    public static final int LEFT = 2;
    public static final int NONE = 0;
    public static final int NORMAL = 0;
    protected static final int RANGE_HIGH = 1;
    protected static final int RANGE_LOW = 0;
    public static final int RIGHT = 1;
    protected static final String[] UNITS;
    public static final int UNITS_CELCIUS = 2;
    public static final int UNITS_FAHRENHEIT = 1;
    public static final int UNITS_KELVIN = 3;
    public static final int UNITS_NONE = 0;
    public static final int WARNING = 1;
    protected static ResourceBundle localizationResources = null;
    private static final long serialVersionUID = 4087093313147984390L;
    private int axisLocation;
    private int bulbRadius;
    private int columnRadius;
    private ValueDataset dataset;
    private boolean followDataInSubranges;
    private int gap;
    private double lowerBound;
    private transient Paint mercuryPaint;
    private RectangleInsets padding;
    private ValueAxis rangeAxis;
    private transient Stroke rangeIndicatorStroke;
    private boolean showValueLines;
    private int subrange;
    private transient Stroke subrangeIndicatorStroke;
    private boolean subrangeIndicatorsVisible;
    private double[][] subrangeInfo;
    private transient Paint[] subrangePaint;
    private transient Paint thermometerPaint;
    private transient Stroke thermometerStroke;
    private int units;
    private double upperBound;
    private boolean useSubrangePaint;
    private Font valueFont;
    private NumberFormat valueFormat;
    private int valueLocation;
    private transient Paint valuePaint;

    static {
        UNITS = new String[]{"", "\u00b0F", "\u00b0C", "\u00b0K"};
        localizationResources = ResourceBundleWrapper.getBundle("org.jfree.chart.plot.LocalizationBundle");
    }

    public ThermometerPlot() {
        this(new DefaultValueDataset());
    }

    public ThermometerPlot(ValueDataset dataset) {
        this.lowerBound = DEFAULT_LOWER_BOUND;
        this.upperBound = DEFAULT_UPPER_BOUND;
        this.bulbRadius = DEFAULT_BULB_RADIUS;
        this.columnRadius = DEFAULT_COLUMN_RADIUS;
        this.gap = GAP_RADIUS;
        this.thermometerStroke = new BasicStroke(Plot.DEFAULT_FOREGROUND_ALPHA);
        this.thermometerPaint = Color.black;
        this.units = UNITS_CELCIUS;
        this.valueLocation = UNITS_KELVIN;
        this.axisLocation = UNITS_CELCIUS;
        this.valueFont = new Font("SansSerif", WARNING, 16);
        this.valuePaint = Color.white;
        this.valueFormat = new DecimalFormat();
        this.mercuryPaint = Color.lightGray;
        this.showValueLines = false;
        this.subrange = -1;
        double[][] dArr = new double[UNITS_KELVIN][];
        dArr[UNITS_NONE] = new double[]{DEFAULT_LOWER_BOUND, 50.0d, DEFAULT_LOWER_BOUND, 50.0d};
        dArr[WARNING] = new double[]{50.0d, 75.0d, 50.0d, 75.0d};
        dArr[UNITS_CELCIUS] = new double[]{75.0d, DEFAULT_UPPER_BOUND, 75.0d, DEFAULT_UPPER_BOUND};
        this.subrangeInfo = dArr;
        this.followDataInSubranges = false;
        this.useSubrangePaint = true;
        Paint[] paintArr = new Paint[UNITS_KELVIN];
        paintArr[UNITS_NONE] = Color.green;
        paintArr[WARNING] = Color.orange;
        paintArr[UNITS_CELCIUS] = Color.red;
        this.subrangePaint = paintArr;
        this.subrangeIndicatorsVisible = true;
        this.subrangeIndicatorStroke = new BasicStroke(Axis.DEFAULT_TICK_MARK_OUTSIDE_LENGTH);
        this.rangeIndicatorStroke = new BasicStroke(MeterPlot.DEFAULT_BORDER_SIZE);
        this.padding = new RectangleInsets(UnitType.RELATIVE, ValueAxis.DEFAULT_UPPER_MARGIN, ValueAxis.DEFAULT_UPPER_MARGIN, ValueAxis.DEFAULT_UPPER_MARGIN, ValueAxis.DEFAULT_UPPER_MARGIN);
        this.dataset = dataset;
        if (dataset != null) {
            dataset.addChangeListener(this);
        }
        NumberAxis axis = new NumberAxis(null);
        axis.setStandardTickUnits(NumberAxis.createIntegerTickUnits());
        axis.setAxisLineVisible(false);
        axis.setPlot(this);
        axis.addChangeListener(this);
        this.rangeAxis = axis;
        setAxisRange();
    }

    public ValueDataset getDataset() {
        return this.dataset;
    }

    public void setDataset(ValueDataset dataset) {
        ValueDataset existing = this.dataset;
        if (existing != null) {
            existing.removeChangeListener(this);
        }
        this.dataset = dataset;
        if (dataset != null) {
            setDatasetGroup(dataset.getGroup());
            dataset.addChangeListener(this);
        }
        datasetChanged(new DatasetChangeEvent(this, dataset));
    }

    public ValueAxis getRangeAxis() {
        return this.rangeAxis;
    }

    public void setRangeAxis(ValueAxis axis) {
        ParamChecks.nullNotPermitted(axis, "axis");
        this.rangeAxis.removeChangeListener(this);
        axis.setPlot(this);
        axis.addChangeListener(this);
        this.rangeAxis = axis;
        fireChangeEvent();
    }

    public double getLowerBound() {
        return this.lowerBound;
    }

    public void setLowerBound(double lower) {
        this.lowerBound = lower;
        setAxisRange();
    }

    public double getUpperBound() {
        return this.upperBound;
    }

    public void setUpperBound(double upper) {
        this.upperBound = upper;
        setAxisRange();
    }

    public void setRange(double lower, double upper) {
        this.lowerBound = lower;
        this.upperBound = upper;
        setAxisRange();
    }

    public RectangleInsets getPadding() {
        return this.padding;
    }

    public void setPadding(RectangleInsets padding) {
        ParamChecks.nullNotPermitted(padding, "padding");
        this.padding = padding;
        fireChangeEvent();
    }

    public Stroke getThermometerStroke() {
        return this.thermometerStroke;
    }

    public void setThermometerStroke(Stroke s) {
        if (s != null) {
            this.thermometerStroke = s;
            fireChangeEvent();
        }
    }

    public Paint getThermometerPaint() {
        return this.thermometerPaint;
    }

    public void setThermometerPaint(Paint paint) {
        if (paint != null) {
            this.thermometerPaint = paint;
            fireChangeEvent();
        }
    }

    public int getUnits() {
        return this.units;
    }

    public void setUnits(int u) {
        if (u >= 0 && u < UNITS.length && this.units != u) {
            this.units = u;
            fireChangeEvent();
        }
    }

    public void setUnits(String u) {
        if (u != null) {
            u = u.toUpperCase().trim();
            int i = UNITS_NONE;
            while (i < UNITS.length) {
                if (u.equals(UNITS[i].toUpperCase().trim())) {
                    setUnits(i);
                    i = UNITS.length;
                }
                i += WARNING;
            }
        }
    }

    public int getValueLocation() {
        return this.valueLocation;
    }

    public void setValueLocation(int location) {
        if (location < 0 || location >= 4) {
            throw new IllegalArgumentException("Location not recognised.");
        }
        this.valueLocation = location;
        fireChangeEvent();
    }

    public int getAxisLocation() {
        return this.axisLocation;
    }

    public void setAxisLocation(int location) {
        if (location < 0 || location >= UNITS_KELVIN) {
            throw new IllegalArgumentException("Location not recognised.");
        }
        this.axisLocation = location;
        fireChangeEvent();
    }

    public Font getValueFont() {
        return this.valueFont;
    }

    public void setValueFont(Font f) {
        ParamChecks.nullNotPermitted(f, "f");
        if (!this.valueFont.equals(f)) {
            this.valueFont = f;
            fireChangeEvent();
        }
    }

    public Paint getValuePaint() {
        return this.valuePaint;
    }

    public void setValuePaint(Paint paint) {
        ParamChecks.nullNotPermitted(paint, "paint");
        if (!this.valuePaint.equals(paint)) {
            this.valuePaint = paint;
            fireChangeEvent();
        }
    }

    public void setValueFormat(NumberFormat formatter) {
        ParamChecks.nullNotPermitted(formatter, "formatter");
        this.valueFormat = formatter;
        fireChangeEvent();
    }

    public Paint getMercuryPaint() {
        return this.mercuryPaint;
    }

    public void setMercuryPaint(Paint paint) {
        ParamChecks.nullNotPermitted(paint, "paint");
        this.mercuryPaint = paint;
        fireChangeEvent();
    }

    public boolean getShowValueLines() {
        return this.showValueLines;
    }

    public void setShowValueLines(boolean b) {
        this.showValueLines = b;
        fireChangeEvent();
    }

    public void setSubrangeInfo(int range, double low, double hi) {
        setSubrangeInfo(range, low, hi, low, hi);
    }

    public void setSubrangeInfo(int range, double rangeLow, double rangeHigh, double displayLow, double displayHigh) {
        if (range >= 0 && range < UNITS_KELVIN) {
            setSubrange(range, rangeLow, rangeHigh);
            setDisplayRange(range, displayLow, displayHigh);
            setAxisRange();
            fireChangeEvent();
        }
    }

    public void setSubrange(int range, double low, double high) {
        if (range >= 0 && range < UNITS_KELVIN) {
            this.subrangeInfo[range][WARNING] = high;
            this.subrangeInfo[range][UNITS_NONE] = low;
        }
    }

    public void setDisplayRange(int range, double low, double high) {
        if (range >= 0 && range < this.subrangeInfo.length && isValidNumber(high) && isValidNumber(low)) {
            if (high > low) {
                this.subrangeInfo[range][UNITS_KELVIN] = high;
                this.subrangeInfo[range][UNITS_CELCIUS] = low;
                return;
            }
            this.subrangeInfo[range][UNITS_KELVIN] = low;
            this.subrangeInfo[range][UNITS_CELCIUS] = high;
        }
    }

    public Paint getSubrangePaint(int range) {
        if (range < 0 || range >= this.subrangePaint.length) {
            return this.mercuryPaint;
        }
        return this.subrangePaint[range];
    }

    public void setSubrangePaint(int range, Paint paint) {
        if (range >= 0 && range < this.subrangePaint.length && paint != null) {
            this.subrangePaint[range] = paint;
            fireChangeEvent();
        }
    }

    public boolean getFollowDataInSubranges() {
        return this.followDataInSubranges;
    }

    public void setFollowDataInSubranges(boolean flag) {
        this.followDataInSubranges = flag;
        fireChangeEvent();
    }

    public boolean getUseSubrangePaint() {
        return this.useSubrangePaint;
    }

    public void setUseSubrangePaint(boolean flag) {
        this.useSubrangePaint = flag;
        fireChangeEvent();
    }

    public int getBulbRadius() {
        return this.bulbRadius;
    }

    public void setBulbRadius(int r) {
        this.bulbRadius = r;
        fireChangeEvent();
    }

    public int getBulbDiameter() {
        return getBulbRadius() * UNITS_CELCIUS;
    }

    public int getColumnRadius() {
        return this.columnRadius;
    }

    public void setColumnRadius(int r) {
        this.columnRadius = r;
        fireChangeEvent();
    }

    public int getColumnDiameter() {
        return getColumnRadius() * UNITS_CELCIUS;
    }

    public int getGap() {
        return this.gap;
    }

    public void setGap(int gap) {
        this.gap = gap;
        fireChangeEvent();
    }

    public void draw(Graphics2D g2, Rectangle2D area, Point2D anchor, PlotState parentState, PlotRenderingInfo info) {
        FontMetrics metrics;
        RoundRectangle2D outerStem = new Double();
        RoundRectangle2D innerStem = new Double();
        RoundRectangle2D mercuryStem = new Double();
        Ellipse2D outerBulb = new Ellipse2D.Double();
        Ellipse2D innerBulb = new Ellipse2D.Double();
        if (info != null) {
            info.setPlotArea(area);
        }
        getInsets().trim(area);
        drawBackground(g2, area);
        Rectangle2D interior = (Rectangle2D) area.clone();
        this.padding.trim(interior);
        int midX = (int) (interior.getX() + (interior.getWidth() / DateAxis.DEFAULT_AUTO_RANGE_MINIMUM_SIZE_IN_MILLISECONDS));
        int midY = (int) (interior.getY() + (interior.getHeight() / DateAxis.DEFAULT_AUTO_RANGE_MINIMUM_SIZE_IN_MILLISECONDS));
        int stemTop = (int) (interior.getMinY() + ((double) getBulbRadius()));
        int stemBottom = (int) (interior.getMaxY() - ((double) getBulbDiameter()));
        Rectangle2D dataArea = new Rectangle2D.Double((double) (midX - getColumnRadius()), (double) stemTop, (double) getColumnRadius(), (double) (stemBottom - stemTop));
        outerBulb.setFrame((double) (midX - getBulbRadius()), (double) stemBottom, (double) getBulbDiameter(), (double) getBulbDiameter());
        RoundRectangle2D roundRectangle2D = outerStem;
        roundRectangle2D.setRoundRect((double) (midX - getColumnRadius()), interior.getMinY(), (double) getColumnDiameter(), (double) ((getBulbDiameter() + stemBottom) - stemTop), (double) getColumnDiameter(), (double) getColumnDiameter());
        Area area2 = new Area(outerBulb);
        area2.add(new Area(outerStem));
        innerBulb.setFrame((double) ((midX - getBulbRadius()) + getGap()), (double) (getGap() + stemBottom), (double) (getBulbDiameter() - (getGap() * UNITS_CELCIUS)), (double) (getBulbDiameter() - (getGap() * UNITS_CELCIUS)));
        double columnDiameter = (double) (getColumnDiameter() - (getGap() * UNITS_CELCIUS));
        double columnDiameter2 = (double) (getColumnDiameter() - (getGap() * UNITS_CELCIUS));
        roundRectangle2D = innerStem;
        roundRectangle2D.setRoundRect((double) ((midX - getColumnRadius()) + getGap()), interior.getMinY() + ((double) getGap()), (double) (getColumnDiameter() - (getGap() * UNITS_CELCIUS)), (double) (((getBulbDiameter() + stemBottom) - (getGap() * UNITS_CELCIUS)) - stemTop), r0, r0);
        area2 = new Area(innerBulb);
        area2.add(new Area(innerStem));
        if (!(this.dataset == null || this.dataset.getValue() == null)) {
            double current = this.dataset.getValue().doubleValue();
            int i = getColumnDiameter() - (getGap() * UNITS_CELCIUS);
            int j = getColumnRadius() - getGap();
            int l = i / UNITS_CELCIUS;
            int k = (int) Math.round(this.rangeAxis.valueToJava2D(current, dataArea, RectangleEdge.LEFT));
            if (((double) k) < ((double) getGap()) + interior.getMinY()) {
                k = (int) (((double) getGap()) + interior.getMinY());
                l = getBulbRadius();
            }
            area2 = new Area(innerBulb);
            if (k < getBulbRadius() + stemBottom) {
                roundRectangle2D = mercuryStem;
                roundRectangle2D.setRoundRect((double) (midX - j), (double) k, (double) i, (double) ((getBulbRadius() + stemBottom) - k), (double) l, (double) l);
                area2.add(new Area(mercuryStem));
            }
            g2.setPaint(getCurrentPaint());
            g2.fill(area2);
            if (this.subrangeIndicatorsVisible) {
                double x;
                double y;
                Line2D line;
                g2.setStroke(this.subrangeIndicatorStroke);
                Range range = this.rangeAxis.getRange();
                double value = this.subrangeInfo[UNITS_NONE][UNITS_NONE];
                if (range.contains(value)) {
                    x = (double) ((getColumnRadius() + midX) + UNITS_CELCIUS);
                    y = this.rangeAxis.valueToJava2D(value, dataArea, RectangleEdge.LEFT);
                    line = new Line2D.Double(x, y, x + XYPointerAnnotation.DEFAULT_TIP_RADIUS, y);
                    g2.setPaint(this.subrangePaint[UNITS_NONE]);
                    g2.draw(line);
                }
                value = this.subrangeInfo[WARNING][UNITS_NONE];
                if (range.contains(value)) {
                    x = (double) ((getColumnRadius() + midX) + UNITS_CELCIUS);
                    y = this.rangeAxis.valueToJava2D(value, dataArea, RectangleEdge.LEFT);
                    line = new Line2D.Double(x, y, x + XYPointerAnnotation.DEFAULT_TIP_RADIUS, y);
                    g2.setPaint(this.subrangePaint[WARNING]);
                    g2.draw(line);
                }
                value = this.subrangeInfo[UNITS_CELCIUS][UNITS_NONE];
                if (range.contains(value)) {
                    x = (double) ((getColumnRadius() + midX) + UNITS_CELCIUS);
                    y = this.rangeAxis.valueToJava2D(value, dataArea, RectangleEdge.LEFT);
                    line = new Line2D.Double(x, y, x + XYPointerAnnotation.DEFAULT_TIP_RADIUS, y);
                    g2.setPaint(this.subrangePaint[UNITS_CELCIUS]);
                    g2.draw(line);
                }
            }
            if (!(this.rangeAxis == null || this.axisLocation == 0)) {
                int drawWidth = GAP_DIAMETER;
                if (this.showValueLines) {
                    drawWidth = GAP_DIAMETER + getColumnDiameter();
                }
                double cursor;
                Rectangle2D drawArea;
                switch (this.axisLocation) {
                    case WARNING /*1*/:
                        cursor = (double) (getColumnRadius() + midX);
                        drawArea = new Rectangle2D.Double(cursor, (double) stemTop, (double) drawWidth, (double) ((stemBottom - stemTop) + WARNING));
                        this.rangeAxis.draw(g2, cursor, area, drawArea, RectangleEdge.RIGHT, null);
                        break;
                    default:
                        cursor = (double) (midX - getColumnRadius());
                        drawArea = new Rectangle2D.Double(cursor, (double) stemTop, (double) drawWidth, (double) ((stemBottom - stemTop) + WARNING));
                        this.rangeAxis.draw(g2, cursor, area, drawArea, RectangleEdge.LEFT, null);
                        break;
                }
            }
            g2.setFont(this.valueFont);
            g2.setPaint(this.valuePaint);
            metrics = g2.getFontMetrics();
            switch (this.valueLocation) {
                case WARNING /*1*/:
                    g2.drawString(this.valueFormat.format(current), (getColumnRadius() + midX) + getGap(), midY);
                    break;
                case UNITS_CELCIUS /*2*/:
                    String valueString = this.valueFormat.format(current);
                    Graphics2D graphics2D = g2;
                    String str = valueString;
                    graphics2D.drawString(str, ((midX - getColumnRadius()) - getGap()) - metrics.stringWidth(valueString), midY);
                    break;
                case UNITS_KELVIN /*3*/:
                    String temp = this.valueFormat.format(current);
                    g2.drawString(temp, midX - (metrics.stringWidth(temp) / UNITS_CELCIUS), (getBulbRadius() + stemBottom) + getGap());
                    break;
            }
        }
        g2.setPaint(this.thermometerPaint);
        g2.setFont(this.valueFont);
        metrics = g2.getFontMetrics();
        FontMetrics fontMetrics = metrics;
        int tickX1 = ((midX - getColumnRadius()) - (getGap() * UNITS_CELCIUS)) - fontMetrics.stringWidth(UNITS[this.units]);
        if (((double) tickX1) > area.getMinX()) {
            g2.drawString(UNITS[this.units], tickX1, (int) (area.getMinY() + 20.0d));
        }
        g2.setStroke(this.thermometerStroke);
        g2.draw(area2);
        g2.draw(area2);
        drawOutline(g2, area);
    }

    public void zoom(double percent) {
    }

    public String getPlotType() {
        return localizationResources.getString("Thermometer_Plot");
    }

    public void datasetChanged(DatasetChangeEvent event) {
        if (this.dataset != null) {
            Number vn = this.dataset.getValue();
            if (vn != null) {
                double value = vn.doubleValue();
                if (inSubrange(UNITS_NONE, value)) {
                    this.subrange = UNITS_NONE;
                } else if (inSubrange(WARNING, value)) {
                    this.subrange = WARNING;
                } else if (inSubrange(UNITS_CELCIUS, value)) {
                    this.subrange = UNITS_CELCIUS;
                } else {
                    this.subrange = -1;
                }
                setAxisRange();
            }
        }
        super.datasetChanged(event);
    }

    public Number getMinimumVerticalDataValue() {
        return new Double(this.lowerBound);
    }

    public Number getMaximumVerticalDataValue() {
        return new Double(this.upperBound);
    }

    public Range getDataRange(ValueAxis axis) {
        return new Range(this.lowerBound, this.upperBound);
    }

    protected void setAxisRange() {
        if (this.subrange < 0 || !this.followDataInSubranges) {
            this.rangeAxis.setRange(this.lowerBound, this.upperBound);
        } else {
            this.rangeAxis.setRange(new Range(this.subrangeInfo[this.subrange][UNITS_CELCIUS], this.subrangeInfo[this.subrange][UNITS_KELVIN]));
        }
    }

    public LegendItemCollection getLegendItems() {
        return null;
    }

    public PlotOrientation getOrientation() {
        return PlotOrientation.VERTICAL;
    }

    protected static boolean isValidNumber(double d) {
        return (Double.isNaN(d) || Double.isInfinite(d)) ? false : true;
    }

    private boolean inSubrange(int subrange, double value) {
        return value > this.subrangeInfo[subrange][UNITS_NONE] && value <= this.subrangeInfo[subrange][WARNING];
    }

    private Paint getCurrentPaint() {
        Paint result = this.mercuryPaint;
        if (!this.useSubrangePaint) {
            return result;
        }
        double value = this.dataset.getValue().doubleValue();
        if (inSubrange(UNITS_NONE, value)) {
            return this.subrangePaint[UNITS_NONE];
        }
        if (inSubrange(WARNING, value)) {
            return this.subrangePaint[WARNING];
        }
        if (inSubrange(UNITS_CELCIUS, value)) {
            return this.subrangePaint[UNITS_CELCIUS];
        }
        return result;
    }

    public boolean equals(Object obj) {
        if (obj == this) {
            return true;
        }
        if (!(obj instanceof ThermometerPlot)) {
            return false;
        }
        ThermometerPlot that = (ThermometerPlot) obj;
        if (!super.equals(obj)) {
            return false;
        }
        if (!ObjectUtilities.equal(this.rangeAxis, that.rangeAxis)) {
            return false;
        }
        if (this.axisLocation != that.axisLocation) {
            return false;
        }
        if (this.lowerBound != that.lowerBound) {
            return false;
        }
        if (this.upperBound != that.upperBound) {
            return false;
        }
        if (!ObjectUtilities.equal(this.padding, that.padding)) {
            return false;
        }
        if (!ObjectUtilities.equal(this.thermometerStroke, that.thermometerStroke)) {
            return false;
        }
        if (!PaintUtilities.equal(this.thermometerPaint, that.thermometerPaint)) {
            return false;
        }
        if (this.units != that.units) {
            return false;
        }
        if (this.valueLocation != that.valueLocation) {
            return false;
        }
        if (!ObjectUtilities.equal(this.valueFont, that.valueFont)) {
            return false;
        }
        if (!PaintUtilities.equal(this.valuePaint, that.valuePaint)) {
            return false;
        }
        if (!ObjectUtilities.equal(this.valueFormat, that.valueFormat)) {
            return false;
        }
        if (!PaintUtilities.equal(this.mercuryPaint, that.mercuryPaint)) {
            return false;
        }
        if (this.showValueLines != that.showValueLines) {
            return false;
        }
        if (this.subrange != that.subrange) {
            return false;
        }
        if (this.followDataInSubranges != that.followDataInSubranges) {
            return false;
        }
        if (!equal(this.subrangeInfo, that.subrangeInfo)) {
            return false;
        }
        if (this.useSubrangePaint != that.useSubrangePaint) {
            return false;
        }
        if (this.bulbRadius != that.bulbRadius) {
            return false;
        }
        if (this.columnRadius != that.columnRadius) {
            return false;
        }
        if (this.gap != that.gap) {
            return false;
        }
        for (int i = UNITS_NONE; i < this.subrangePaint.length; i += WARNING) {
            if (!PaintUtilities.equal(this.subrangePaint[i], that.subrangePaint[i])) {
                return false;
            }
        }
        return true;
    }

    private static boolean equal(double[][] array1, double[][] array2) {
        boolean z = true;
        if (array1 == null) {
            if (array2 != null) {
                z = false;
            }
            return z;
        } else if (array2 == null || array1.length != array2.length) {
            return false;
        } else {
            for (int i = UNITS_NONE; i < array1.length; i += WARNING) {
                if (!Arrays.equals(array1[i], array2[i])) {
                    return false;
                }
            }
            return true;
        }
    }

    public Object clone() throws CloneNotSupportedException {
        ThermometerPlot clone = (ThermometerPlot) super.clone();
        if (clone.dataset != null) {
            clone.dataset.addChangeListener(clone);
        }
        clone.rangeAxis = (ValueAxis) ObjectUtilities.clone(this.rangeAxis);
        if (clone.rangeAxis != null) {
            clone.rangeAxis.setPlot(clone);
            clone.rangeAxis.addChangeListener(clone);
        }
        clone.valueFormat = (NumberFormat) this.valueFormat.clone();
        clone.subrangePaint = (Paint[]) this.subrangePaint.clone();
        return clone;
    }

    private void writeObject(ObjectOutputStream stream) throws IOException {
        stream.defaultWriteObject();
        SerialUtilities.writeStroke(this.thermometerStroke, stream);
        SerialUtilities.writePaint(this.thermometerPaint, stream);
        SerialUtilities.writePaint(this.valuePaint, stream);
        SerialUtilities.writePaint(this.mercuryPaint, stream);
        SerialUtilities.writeStroke(this.subrangeIndicatorStroke, stream);
        SerialUtilities.writeStroke(this.rangeIndicatorStroke, stream);
        for (int i = UNITS_NONE; i < UNITS_KELVIN; i += WARNING) {
            SerialUtilities.writePaint(this.subrangePaint[i], stream);
        }
    }

    private void readObject(ObjectInputStream stream) throws IOException, ClassNotFoundException {
        stream.defaultReadObject();
        this.thermometerStroke = SerialUtilities.readStroke(stream);
        this.thermometerPaint = SerialUtilities.readPaint(stream);
        this.valuePaint = SerialUtilities.readPaint(stream);
        this.mercuryPaint = SerialUtilities.readPaint(stream);
        this.subrangeIndicatorStroke = SerialUtilities.readStroke(stream);
        this.rangeIndicatorStroke = SerialUtilities.readStroke(stream);
        this.subrangePaint = new Paint[UNITS_KELVIN];
        for (int i = UNITS_NONE; i < UNITS_KELVIN; i += WARNING) {
            this.subrangePaint[i] = SerialUtilities.readPaint(stream);
        }
        if (this.rangeAxis != null) {
            this.rangeAxis.addChangeListener(this);
        }
    }

    public void zoomDomainAxes(double factor, PlotRenderingInfo state, Point2D source) {
    }

    public void zoomDomainAxes(double factor, PlotRenderingInfo state, Point2D source, boolean useAnchor) {
    }

    public void zoomRangeAxes(double factor, PlotRenderingInfo state, Point2D source) {
        this.rangeAxis.resizeRange(factor);
    }

    public void zoomRangeAxes(double factor, PlotRenderingInfo state, Point2D source, boolean useAnchor) {
        this.rangeAxis.resizeRange(factor, getRangeAxis().java2DToValue(source.getY(), state.getDataArea(), RectangleEdge.LEFT));
    }

    public void zoomDomainAxes(double lowerPercent, double upperPercent, PlotRenderingInfo state, Point2D source) {
    }

    public void zoomRangeAxes(double lowerPercent, double upperPercent, PlotRenderingInfo state, Point2D source) {
        this.rangeAxis.zoomRange(lowerPercent, upperPercent);
    }

    public boolean isDomainZoomable() {
        return false;
    }

    public boolean isRangeZoomable() {
        return true;
    }
}
