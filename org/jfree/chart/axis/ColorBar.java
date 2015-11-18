package org.jfree.chart.axis;

import java.awt.BasicStroke;
import java.awt.Graphics2D;
import java.awt.Paint;
import java.awt.RenderingHints;
import java.awt.Stroke;
import java.awt.geom.Line2D;
import java.awt.geom.Rectangle2D;
import java.awt.geom.Rectangle2D.Double;
import java.io.Serializable;
import org.jfree.chart.plot.ColorPalette;
import org.jfree.chart.plot.ContourPlot;
import org.jfree.chart.plot.Plot;
import org.jfree.chart.plot.RainbowPalette;
import org.jfree.data.xy.NormalizedMatrixSeries;
import org.jfree.ui.RectangleEdge;

public class ColorBar implements Cloneable, Serializable {
    static final /* synthetic */ boolean $assertionsDisabled;
    public static final int DEFAULT_COLORBAR_THICKNESS = 0;
    public static final double DEFAULT_COLORBAR_THICKNESS_PERCENT = 0.1d;
    public static final int DEFAULT_OUTERGAP = 2;
    private static final long serialVersionUID = -2101776212647268103L;
    private ValueAxis axis;
    private int colorBarLength;
    private int colorBarThickness;
    private double colorBarThicknessPercent;
    private ColorPalette colorPalette;
    private int outerGap;

    static {
        $assertionsDisabled = !ColorBar.class.desiredAssertionStatus() ? true : $assertionsDisabled;
    }

    public ColorBar(String label) {
        this.colorBarThickness = DEFAULT_COLORBAR_THICKNESS;
        this.colorBarThicknessPercent = DEFAULT_COLORBAR_THICKNESS_PERCENT;
        this.colorPalette = null;
        this.colorBarLength = DEFAULT_COLORBAR_THICKNESS;
        NumberAxis a = new NumberAxis(label);
        a.setAutoRangeIncludesZero($assertionsDisabled);
        this.axis = a;
        this.axis.setLowerMargin(0.0d);
        this.axis.setUpperMargin(0.0d);
        this.colorPalette = new RainbowPalette();
        this.colorBarThickness = DEFAULT_COLORBAR_THICKNESS;
        this.colorBarThicknessPercent = DEFAULT_COLORBAR_THICKNESS_PERCENT;
        this.outerGap = DEFAULT_OUTERGAP;
        this.colorPalette.setMinZ(this.axis.getRange().getLowerBound());
        this.colorPalette.setMaxZ(this.axis.getRange().getUpperBound());
    }

    public void configure(ContourPlot plot) {
        double minZ = plot.getDataset().getMinZValue();
        double maxZ = plot.getDataset().getMaxZValue();
        setMinimumValue(minZ);
        setMaximumValue(maxZ);
    }

    public ValueAxis getAxis() {
        return this.axis;
    }

    public void setAxis(ValueAxis axis) {
        this.axis = axis;
    }

    public void autoAdjustRange() {
        this.axis.autoAdjustRange();
        this.colorPalette.setMinZ(this.axis.getLowerBound());
        this.colorPalette.setMaxZ(this.axis.getUpperBound());
    }

    public double draw(Graphics2D g2, double cursor, Rectangle2D plotArea, Rectangle2D dataArea, Rectangle2D reservedArea, RectangleEdge edge) {
        double length;
        Rectangle2D colorBarArea = null;
        double thickness = calculateBarThickness(dataArea, edge);
        if (this.colorBarThickness > 0) {
            thickness = (double) this.colorBarThickness;
        }
        if (RectangleEdge.isLeftOrRight(edge)) {
            length = dataArea.getHeight();
        } else {
            length = dataArea.getWidth();
        }
        if (this.colorBarLength > 0) {
            length = (double) this.colorBarLength;
        }
        if (edge == RectangleEdge.BOTTOM) {
            colorBarArea = new Double(dataArea.getX(), plotArea.getMaxY() + ((double) this.outerGap), length, thickness);
        } else if (edge == RectangleEdge.TOP) {
            colorBarArea = new Double(dataArea.getX(), reservedArea.getMinY() + ((double) this.outerGap), length, thickness);
        } else if (edge == RectangleEdge.LEFT) {
            r15 = new Double((plotArea.getX() - thickness) - ((double) this.outerGap), dataArea.getMinY(), thickness, length);
        } else if (edge == RectangleEdge.RIGHT) {
            r15 = new Double(plotArea.getMaxX() + ((double) this.outerGap), dataArea.getMinY(), thickness, length);
        }
        this.axis.refreshTicks(g2, new AxisState(), colorBarArea, edge);
        drawColorBar(g2, colorBarArea, edge);
        AxisState state = null;
        if ($assertionsDisabled || colorBarArea != null) {
            double minY;
            if (edge == RectangleEdge.TOP) {
                minY = colorBarArea.getMinY();
                state = this.axis.draw(g2, cursor, reservedArea, colorBarArea, RectangleEdge.TOP, null);
            } else if (edge == RectangleEdge.BOTTOM) {
                minY = colorBarArea.getMaxY();
                state = this.axis.draw(g2, cursor, reservedArea, colorBarArea, RectangleEdge.BOTTOM, null);
            } else if (edge == RectangleEdge.LEFT) {
                minY = colorBarArea.getMinX();
                state = this.axis.draw(g2, cursor, reservedArea, colorBarArea, RectangleEdge.LEFT, null);
            } else if (edge == RectangleEdge.RIGHT) {
                minY = colorBarArea.getMaxX();
                state = this.axis.draw(g2, cursor, reservedArea, colorBarArea, RectangleEdge.RIGHT, null);
            }
            if ($assertionsDisabled || state != null) {
                return state.getCursor();
            }
            throw new AssertionError();
        }
        throw new AssertionError();
    }

    public void drawColorBar(Graphics2D g2, Rectangle2D colorBarArea, RectangleEdge edge) {
        Object antiAlias = g2.getRenderingHint(RenderingHints.KEY_ANTIALIASING);
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_OFF);
        Stroke strokeSaved = g2.getStroke();
        g2.setStroke(new BasicStroke(Plot.DEFAULT_FOREGROUND_ALPHA));
        double y1;
        double y2;
        Line2D line;
        double xx;
        double value;
        if (RectangleEdge.isTopOrBottom(edge)) {
            y1 = colorBarArea.getY();
            y2 = colorBarArea.getMaxY();
            line = new Line2D.Double();
            for (xx = colorBarArea.getX(); xx <= colorBarArea.getMaxX(); xx += NormalizedMatrixSeries.DEFAULT_SCALE_FACTOR) {
                value = this.axis.java2DToValue(xx, colorBarArea, edge);
                line.setLine(xx, y1, xx, y2);
                g2.setPaint(getPaint(value));
                g2.draw(line);
            }
        } else {
            y1 = colorBarArea.getX();
            y2 = colorBarArea.getMaxX();
            line = new Line2D.Double();
            for (xx = colorBarArea.getY(); xx <= colorBarArea.getMaxY(); xx += NormalizedMatrixSeries.DEFAULT_SCALE_FACTOR) {
                value = this.axis.java2DToValue(xx, colorBarArea, edge);
                line.setLine(y1, xx, y2, xx);
                g2.setPaint(getPaint(value));
                g2.draw(line);
            }
        }
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, antiAlias);
        g2.setStroke(strokeSaved);
    }

    public ColorPalette getColorPalette() {
        return this.colorPalette;
    }

    public Paint getPaint(double value) {
        return this.colorPalette.getPaint(value);
    }

    public void setColorPalette(ColorPalette palette) {
        this.colorPalette = palette;
    }

    public void setMaximumValue(double value) {
        this.colorPalette.setMaxZ(value);
        this.axis.setUpperBound(value);
    }

    public void setMinimumValue(double value) {
        this.colorPalette.setMinZ(value);
        this.axis.setLowerBound(value);
    }

    public AxisSpace reserveSpace(Graphics2D g2, Plot plot, Rectangle2D plotArea, Rectangle2D dataArea, RectangleEdge edge, AxisSpace space) {
        AxisSpace result = this.axis.reserveSpace(g2, plot, plotArea, edge, space);
        result.add(((double) (this.outerGap * DEFAULT_OUTERGAP)) + calculateBarThickness(dataArea, edge), edge);
        return result;
    }

    private double calculateBarThickness(Rectangle2D plotArea, RectangleEdge edge) {
        if (RectangleEdge.isLeftOrRight(edge)) {
            return plotArea.getWidth() * this.colorBarThicknessPercent;
        }
        return plotArea.getHeight() * this.colorBarThicknessPercent;
    }

    public Object clone() throws CloneNotSupportedException {
        ColorBar clone = (ColorBar) super.clone();
        clone.axis = (ValueAxis) this.axis.clone();
        return clone;
    }

    public boolean equals(Object obj) {
        if (obj == this) {
            return true;
        }
        if (!(obj instanceof ColorBar)) {
            return $assertionsDisabled;
        }
        ColorBar that = (ColorBar) obj;
        if (!this.axis.equals(that.axis)) {
            return $assertionsDisabled;
        }
        if (this.colorBarThickness != that.colorBarThickness) {
            return $assertionsDisabled;
        }
        if (this.colorBarThicknessPercent != that.colorBarThicknessPercent) {
            return $assertionsDisabled;
        }
        if (!this.colorPalette.equals(that.colorPalette)) {
            return $assertionsDisabled;
        }
        if (this.colorBarLength != that.colorBarLength) {
            return $assertionsDisabled;
        }
        if (this.outerGap != that.outerGap) {
            return $assertionsDisabled;
        }
        return true;
    }

    public int hashCode() {
        return this.axis.hashCode();
    }
}
