package org.jfree.data;

import java.io.Serializable;
import org.jfree.chart.axis.DateAxis;
import org.jfree.chart.util.ParamChecks;

public class Range implements Serializable {
    private static final long serialVersionUID = -906333695431863380L;
    private double lower;
    private double upper;

    public Range(double lower, double upper) {
        if (lower > upper) {
            throw new IllegalArgumentException("Range(double, double): require lower (" + lower + ") <= upper (" + upper + ").");
        }
        this.lower = lower;
        this.upper = upper;
    }

    public double getLowerBound() {
        return this.lower;
    }

    public double getUpperBound() {
        return this.upper;
    }

    public double getLength() {
        return this.upper - this.lower;
    }

    public double getCentralValue() {
        return (this.lower / DateAxis.DEFAULT_AUTO_RANGE_MINIMUM_SIZE_IN_MILLISECONDS) + (this.upper / DateAxis.DEFAULT_AUTO_RANGE_MINIMUM_SIZE_IN_MILLISECONDS);
    }

    public boolean contains(double value) {
        return value >= this.lower && value <= this.upper;
    }

    public boolean intersects(double b0, double b1) {
        if (b0 <= this.lower) {
            if (b1 > this.lower) {
                return true;
            }
            return false;
        } else if (b0 >= this.upper || b1 < b0) {
            return false;
        } else {
            return true;
        }
    }

    public boolean intersects(Range range) {
        return intersects(range.getLowerBound(), range.getUpperBound());
    }

    public double constrain(double value) {
        double result = value;
        if (contains(value)) {
            return result;
        }
        if (value > this.upper) {
            return this.upper;
        }
        if (value < this.lower) {
            return this.lower;
        }
        return result;
    }

    public static Range combine(Range range1, Range range2) {
        if (range1 == null) {
            return range2;
        }
        if (range2 == null) {
            return range1;
        }
        return new Range(Math.min(range1.getLowerBound(), range2.getLowerBound()), Math.max(range1.getUpperBound(), range2.getUpperBound()));
    }

    public static Range combineIgnoringNaN(Range range1, Range range2) {
        if (range1 == null) {
            return (range2 == null || !range2.isNaNRange()) ? range2 : null;
        } else {
            if (range2 != null) {
                double l = min(range1.getLowerBound(), range2.getLowerBound());
                double u = max(range1.getUpperBound(), range2.getUpperBound());
                if (Double.isNaN(l) && Double.isNaN(u)) {
                    return null;
                }
                return new Range(l, u);
            } else if (range1.isNaNRange()) {
                return null;
            } else {
                return range1;
            }
        }
    }

    private static double min(double d1, double d2) {
        if (Double.isNaN(d1)) {
            return d2;
        }
        if (Double.isNaN(d2)) {
            return d1;
        }
        return Math.min(d1, d2);
    }

    private static double max(double d1, double d2) {
        if (Double.isNaN(d1)) {
            return d2;
        }
        if (Double.isNaN(d2)) {
            return d1;
        }
        return Math.max(d1, d2);
    }

    public static Range expandToInclude(Range range, double value) {
        if (range == null) {
            return new Range(value, value);
        }
        if (value < range.getLowerBound()) {
            return new Range(value, range.getUpperBound());
        }
        if (value > range.getUpperBound()) {
            return new Range(range.getLowerBound(), value);
        }
        return range;
    }

    public static Range expand(Range range, double lowerMargin, double upperMargin) {
        ParamChecks.nullNotPermitted(range, "range");
        double length = range.getLength();
        double lower = range.getLowerBound() - (length * lowerMargin);
        double upper = range.getUpperBound() + (length * upperMargin);
        if (lower > upper) {
            lower = (lower / DateAxis.DEFAULT_AUTO_RANGE_MINIMUM_SIZE_IN_MILLISECONDS) + (upper / DateAxis.DEFAULT_AUTO_RANGE_MINIMUM_SIZE_IN_MILLISECONDS);
            upper = lower;
        }
        return new Range(lower, upper);
    }

    public static Range shift(Range base, double delta) {
        return shift(base, delta, false);
    }

    public static Range shift(Range base, double delta, boolean allowZeroCrossing) {
        ParamChecks.nullNotPermitted(base, "base");
        if (allowZeroCrossing) {
            return new Range(base.getLowerBound() + delta, base.getUpperBound() + delta);
        }
        return new Range(shiftWithNoZeroCrossing(base.getLowerBound(), delta), shiftWithNoZeroCrossing(base.getUpperBound(), delta));
    }

    private static double shiftWithNoZeroCrossing(double value, double delta) {
        if (value > 0.0d) {
            return Math.max(value + delta, 0.0d);
        }
        if (value < 0.0d) {
            return Math.min(value + delta, 0.0d);
        }
        return value + delta;
    }

    public static Range scale(Range base, double factor) {
        ParamChecks.nullNotPermitted(base, "base");
        if (factor >= 0.0d) {
            return new Range(base.getLowerBound() * factor, base.getUpperBound() * factor);
        }
        throw new IllegalArgumentException("Negative 'factor' argument.");
    }

    public boolean equals(Object obj) {
        if (!(obj instanceof Range)) {
            return false;
        }
        Range range = (Range) obj;
        if (this.lower == range.lower && this.upper == range.upper) {
            return true;
        }
        return false;
    }

    public boolean isNaNRange() {
        return Double.isNaN(this.lower) && Double.isNaN(this.upper);
    }

    public int hashCode() {
        long temp = Double.doubleToLongBits(this.lower);
        int result = (int) ((temp >>> 32) ^ temp);
        temp = Double.doubleToLongBits(this.upper);
        return (result * 29) + ((int) ((temp >>> 32) ^ temp));
    }

    public String toString() {
        return "Range[" + this.lower + "," + this.upper + "]";
    }
}
