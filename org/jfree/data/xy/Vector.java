package org.jfree.data.xy;

import java.io.Serializable;

public class Vector implements Serializable {
    private double x;
    private double y;

    public Vector(double x, double y) {
        this.x = x;
        this.y = y;
    }

    public double getX() {
        return this.x;
    }

    public double getY() {
        return this.y;
    }

    public double getLength() {
        return Math.sqrt((this.x * this.x) + (this.y * this.y));
    }

    public double getAngle() {
        return Math.atan2(this.y, this.x);
    }

    public boolean equals(Object obj) {
        if (obj == this) {
            return true;
        }
        if (!(obj instanceof Vector)) {
            return false;
        }
        Vector that = (Vector) obj;
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
}
