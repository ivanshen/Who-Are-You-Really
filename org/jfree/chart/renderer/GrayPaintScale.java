package org.jfree.chart.renderer;

import java.awt.Color;
import java.awt.Paint;
import java.io.Serializable;
import org.jfree.chart.HashUtilities;
import org.jfree.data.xy.NormalizedMatrixSeries;
import org.jfree.util.PublicCloneable;

public class GrayPaintScale implements PaintScale, PublicCloneable, Serializable {
    private int alpha;
    private double lowerBound;
    private double upperBound;

    public GrayPaintScale() {
        this(0.0d, NormalizedMatrixSeries.DEFAULT_SCALE_FACTOR);
    }

    public GrayPaintScale(double lowerBound, double upperBound) {
        this(lowerBound, upperBound, 255);
    }

    public GrayPaintScale(double lowerBound, double upperBound, int alpha) {
        if (lowerBound >= upperBound) {
            throw new IllegalArgumentException("Requires lowerBound < upperBound.");
        } else if (alpha < 0 || alpha > 255) {
            throw new IllegalArgumentException("Requires alpha in the range 0 to 255.");
        } else {
            this.lowerBound = lowerBound;
            this.upperBound = upperBound;
            this.alpha = alpha;
        }
    }

    public double getLowerBound() {
        return this.lowerBound;
    }

    public double getUpperBound() {
        return this.upperBound;
    }

    public int getAlpha() {
        return this.alpha;
    }

    public Paint getPaint(double value) {
        int g = (int) (((Math.min(Math.max(value, this.lowerBound), this.upperBound) - this.lowerBound) / (this.upperBound - this.lowerBound)) * 255.0d);
        return new Color(g, g, g, this.alpha);
    }

    public boolean equals(Object obj) {
        if (obj == this) {
            return true;
        }
        if (!(obj instanceof GrayPaintScale)) {
            return false;
        }
        GrayPaintScale that = (GrayPaintScale) obj;
        if (this.lowerBound != that.lowerBound) {
            return false;
        }
        if (this.upperBound != that.upperBound) {
            return false;
        }
        if (this.alpha != that.alpha) {
            return false;
        }
        return true;
    }

    public int hashCode() {
        return (HashUtilities.hashCode(HashUtilities.hashCode(7, this.lowerBound), this.upperBound) * 43) + this.alpha;
    }

    public Object clone() throws CloneNotSupportedException {
        return super.clone();
    }
}
