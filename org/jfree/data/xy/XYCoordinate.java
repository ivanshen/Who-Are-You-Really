package org.jfree.data.xy;

import java.io.Serializable;

public class XYCoordinate implements Comparable, Serializable {
    private double x;
    private double y;

    public XYCoordinate() {
        this(0.0d, 0.0d);
    }

    public XYCoordinate(double x, double y) {
        this.x = x;
        this.y = y;
    }

    public double getX() {
        return this.x;
    }

    public double getY() {
        return this.y;
    }

    public boolean equals(Object obj) {
        if (obj == this) {
            return true;
        }
        if (!(obj instanceof XYCoordinate)) {
            return false;
        }
        XYCoordinate that = (XYCoordinate) obj;
        if (this.x != that.x) {
            return false;
        }
        if (this.y != that.y) {
            return false;
        }
        return true;
    }

    public int hashCode() {
        long temp = Double.doubleToLongBits(this.x);
        int result = ((int) ((temp >>> 32) ^ temp)) + 7141;
        temp = Double.doubleToLongBits(this.y);
        return (result * 37) + ((int) ((temp >>> 32) ^ temp));
    }

    public String toString() {
        return "(" + this.x + ", " + this.y + ")";
    }

    public int compareTo(Object obj) {
        if (obj instanceof XYCoordinate) {
            XYCoordinate that = (XYCoordinate) obj;
            if (this.x > that.x) {
                return 1;
            }
            if (this.x < that.x) {
                return -1;
            }
            if (this.y > that.y) {
                return 1;
            }
            if (this.y < that.y) {
                return -1;
            }
            return 0;
        }
        throw new IllegalArgumentException("Incomparable object.");
    }
}
