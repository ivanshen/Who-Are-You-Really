package org.jfree.ui;

import java.awt.geom.Rectangle2D;
import java.awt.geom.Rectangle2D.Double;
import java.io.Serializable;
import org.jfree.data.xy.NormalizedMatrixSeries;
import org.jfree.util.UnitType;

public class RectangleInsets implements Serializable {
    public static final RectangleInsets ZERO_INSETS;
    private static final long serialVersionUID = 1902273207559319996L;
    private double bottom;
    private double left;
    private double right;
    private double top;
    private UnitType unitType;

    static {
        ZERO_INSETS = new RectangleInsets(UnitType.ABSOLUTE, 0.0d, 0.0d, 0.0d, 0.0d);
    }

    public RectangleInsets() {
        this(NormalizedMatrixSeries.DEFAULT_SCALE_FACTOR, NormalizedMatrixSeries.DEFAULT_SCALE_FACTOR, NormalizedMatrixSeries.DEFAULT_SCALE_FACTOR, NormalizedMatrixSeries.DEFAULT_SCALE_FACTOR);
    }

    public RectangleInsets(double top, double left, double bottom, double right) {
        this(UnitType.ABSOLUTE, top, left, bottom, right);
    }

    public RectangleInsets(UnitType unitType, double top, double left, double bottom, double right) {
        if (unitType == null) {
            throw new IllegalArgumentException("Null 'unitType' argument.");
        }
        this.unitType = unitType;
        this.top = top;
        this.bottom = bottom;
        this.left = left;
        this.right = right;
    }

    public UnitType getUnitType() {
        return this.unitType;
    }

    public double getTop() {
        return this.top;
    }

    public double getBottom() {
        return this.bottom;
    }

    public double getLeft() {
        return this.left;
    }

    public double getRight() {
        return this.right;
    }

    public boolean equals(Object obj) {
        if (obj == this) {
            return true;
        }
        if (!(obj instanceof RectangleInsets)) {
            return false;
        }
        RectangleInsets that = (RectangleInsets) obj;
        if (that.unitType != this.unitType) {
            return false;
        }
        if (this.left != that.left) {
            return false;
        }
        if (this.right != that.right) {
            return false;
        }
        if (this.top != that.top) {
            return false;
        }
        if (this.bottom != that.bottom) {
            return false;
        }
        return true;
    }

    public int hashCode() {
        long temp;
        int result = this.unitType != null ? this.unitType.hashCode() : 0;
        if (this.top != 0.0d) {
            temp = Double.doubleToLongBits(this.top);
        } else {
            temp = 0;
        }
        result = (result * 29) + ((int) ((temp >>> 32) ^ temp));
        if (this.bottom != 0.0d) {
            temp = Double.doubleToLongBits(this.bottom);
        } else {
            temp = 0;
        }
        result = (result * 29) + ((int) ((temp >>> 32) ^ temp));
        if (this.left != 0.0d) {
            temp = Double.doubleToLongBits(this.left);
        } else {
            temp = 0;
        }
        result = (result * 29) + ((int) ((temp >>> 32) ^ temp));
        if (this.right != 0.0d) {
            temp = Double.doubleToLongBits(this.right);
        } else {
            temp = 0;
        }
        return (result * 29) + ((int) ((temp >>> 32) ^ temp));
    }

    public String toString() {
        return "RectangleInsets[t=" + this.top + ",l=" + this.left + ",b=" + this.bottom + ",r=" + this.right + "]";
    }

    public Rectangle2D createAdjustedRectangle(Rectangle2D base, LengthAdjustmentType horizontal, LengthAdjustmentType vertical) {
        if (base == null) {
            throw new IllegalArgumentException("Null 'base' argument.");
        }
        double x = base.getX();
        double y = base.getY();
        double w = base.getWidth();
        double h = base.getHeight();
        if (horizontal == LengthAdjustmentType.EXPAND) {
            double leftOutset = calculateLeftOutset(w);
            x -= leftOutset;
            w = (w + leftOutset) + calculateRightOutset(w);
        } else if (horizontal == LengthAdjustmentType.CONTRACT) {
            double leftMargin = calculateLeftInset(w);
            x += leftMargin;
            w = (w - leftMargin) - calculateRightInset(w);
        }
        double topMargin;
        if (vertical == LengthAdjustmentType.EXPAND) {
            topMargin = calculateTopOutset(h);
            y -= topMargin;
            h = (h + topMargin) + calculateBottomOutset(h);
        } else if (vertical == LengthAdjustmentType.CONTRACT) {
            topMargin = calculateTopInset(h);
            y += topMargin;
            h = (h - topMargin) - calculateBottomInset(h);
        }
        return new Double(x, y, w, h);
    }

    public Rectangle2D createInsetRectangle(Rectangle2D base) {
        return createInsetRectangle(base, true, true);
    }

    public Rectangle2D createInsetRectangle(Rectangle2D base, boolean horizontal, boolean vertical) {
        if (base == null) {
            throw new IllegalArgumentException("Null 'base' argument.");
        }
        double topMargin = 0.0d;
        double bottomMargin = 0.0d;
        if (vertical) {
            topMargin = calculateTopInset(base.getHeight());
            bottomMargin = calculateBottomInset(base.getHeight());
        }
        double leftMargin = 0.0d;
        double rightMargin = 0.0d;
        if (horizontal) {
            leftMargin = calculateLeftInset(base.getWidth());
            rightMargin = calculateRightInset(base.getWidth());
        }
        return new Double(base.getX() + leftMargin, base.getY() + topMargin, (base.getWidth() - leftMargin) - rightMargin, (base.getHeight() - topMargin) - bottomMargin);
    }

    public Rectangle2D createOutsetRectangle(Rectangle2D base) {
        return createOutsetRectangle(base, true, true);
    }

    public Rectangle2D createOutsetRectangle(Rectangle2D base, boolean horizontal, boolean vertical) {
        if (base == null) {
            throw new IllegalArgumentException("Null 'base' argument.");
        }
        double topMargin = 0.0d;
        double bottomMargin = 0.0d;
        if (vertical) {
            topMargin = calculateTopOutset(base.getHeight());
            bottomMargin = calculateBottomOutset(base.getHeight());
        }
        double leftMargin = 0.0d;
        double rightMargin = 0.0d;
        if (horizontal) {
            leftMargin = calculateLeftOutset(base.getWidth());
            rightMargin = calculateRightOutset(base.getWidth());
        }
        return new Double(base.getX() - leftMargin, base.getY() - topMargin, (base.getWidth() + leftMargin) + rightMargin, (base.getHeight() + topMargin) + bottomMargin);
    }

    public double calculateTopInset(double height) {
        double result = this.top;
        if (this.unitType == UnitType.RELATIVE) {
            return this.top * height;
        }
        return result;
    }

    public double calculateTopOutset(double height) {
        double result = this.top;
        if (this.unitType == UnitType.RELATIVE) {
            return (height / ((NormalizedMatrixSeries.DEFAULT_SCALE_FACTOR - this.top) - this.bottom)) * this.top;
        }
        return result;
    }

    public double calculateBottomInset(double height) {
        double result = this.bottom;
        if (this.unitType == UnitType.RELATIVE) {
            return this.bottom * height;
        }
        return result;
    }

    public double calculateBottomOutset(double height) {
        double result = this.bottom;
        if (this.unitType == UnitType.RELATIVE) {
            return (height / ((NormalizedMatrixSeries.DEFAULT_SCALE_FACTOR - this.top) - this.bottom)) * this.bottom;
        }
        return result;
    }

    public double calculateLeftInset(double width) {
        double result = this.left;
        if (this.unitType == UnitType.RELATIVE) {
            return this.left * width;
        }
        return result;
    }

    public double calculateLeftOutset(double width) {
        double result = this.left;
        if (this.unitType == UnitType.RELATIVE) {
            return (width / ((NormalizedMatrixSeries.DEFAULT_SCALE_FACTOR - this.left) - this.right)) * this.left;
        }
        return result;
    }

    public double calculateRightInset(double width) {
        double result = this.right;
        if (this.unitType == UnitType.RELATIVE) {
            return this.right * width;
        }
        return result;
    }

    public double calculateRightOutset(double width) {
        double result = this.right;
        if (this.unitType == UnitType.RELATIVE) {
            return (width / ((NormalizedMatrixSeries.DEFAULT_SCALE_FACTOR - this.left) - this.right)) * this.right;
        }
        return result;
    }

    public double trimWidth(double width) {
        return (width - calculateLeftInset(width)) - calculateRightInset(width);
    }

    public double extendWidth(double width) {
        return (calculateLeftOutset(width) + width) + calculateRightOutset(width);
    }

    public double trimHeight(double height) {
        return (height - calculateTopInset(height)) - calculateBottomInset(height);
    }

    public double extendHeight(double height) {
        return (calculateTopOutset(height) + height) + calculateBottomOutset(height);
    }

    public void trim(Rectangle2D area) {
        double w = area.getWidth();
        double h = area.getHeight();
        double l = calculateLeftInset(w);
        double r = calculateRightInset(w);
        double t = calculateTopInset(h);
        Rectangle2D rectangle2D = area;
        rectangle2D.setRect(area.getX() + l, area.getY() + t, (w - l) - r, (h - t) - calculateBottomInset(h));
    }
}
