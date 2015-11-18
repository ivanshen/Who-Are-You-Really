package org.jfree.data.xy;

import java.io.Serializable;
import org.jfree.chart.util.ParamChecks;
import org.jfree.util.ObjectUtilities;

public class XYDataItem implements Cloneable, Comparable, Serializable {
    private static final long serialVersionUID = 2751513470325494890L;
    private Number x;
    private Number y;

    public XYDataItem(Number x, Number y) {
        ParamChecks.nullNotPermitted(x, "x");
        this.x = x;
        this.y = y;
    }

    public XYDataItem(double x, double y) {
        this(new Double(x), new Double(y));
    }

    public Number getX() {
        return this.x;
    }

    public double getXValue() {
        return this.x.doubleValue();
    }

    public Number getY() {
        return this.y;
    }

    public double getYValue() {
        if (this.y != null) {
            return this.y.doubleValue();
        }
        return Double.NaN;
    }

    public void setY(double y) {
        setY(new Double(y));
    }

    public void setY(Number y) {
        this.y = y;
    }

    public int compareTo(Object o1) {
        if (!(o1 instanceof XYDataItem)) {
            return 1;
        }
        double compare = this.x.doubleValue() - ((XYDataItem) o1).getX().doubleValue();
        if (compare > 0.0d) {
            return 1;
        }
        if (compare < 0.0d) {
            return -1;
        }
        return 0;
    }

    public Object clone() {
        Object clone = null;
        try {
            clone = super.clone();
        } catch (CloneNotSupportedException e) {
            e.printStackTrace();
        }
        return clone;
    }

    public boolean equals(Object obj) {
        if (obj == this) {
            return true;
        }
        if (!(obj instanceof XYDataItem)) {
            return false;
        }
        XYDataItem that = (XYDataItem) obj;
        if (!this.x.equals(that.x)) {
            return false;
        }
        if (ObjectUtilities.equal(this.y, that.y)) {
            return true;
        }
        return false;
    }

    public int hashCode() {
        return (this.x.hashCode() * 29) + (this.y != null ? this.y.hashCode() : 0);
    }

    public String toString() {
        return "[" + getXValue() + ", " + getYValue() + "]";
    }
}
