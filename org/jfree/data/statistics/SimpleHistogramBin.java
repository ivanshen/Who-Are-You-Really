package org.jfree.data.statistics;

import java.io.Serializable;
import org.jfree.util.PublicCloneable;

public class SimpleHistogramBin implements Comparable, Cloneable, PublicCloneable, Serializable {
    private static final long serialVersionUID = 3480862537505941742L;
    private boolean includeLowerBound;
    private boolean includeUpperBound;
    private int itemCount;
    private double lowerBound;
    private double upperBound;

    public SimpleHistogramBin(double lowerBound, double upperBound) {
        this(lowerBound, upperBound, true, true);
    }

    public SimpleHistogramBin(double lowerBound, double upperBound, boolean includeLowerBound, boolean includeUpperBound) {
        if (lowerBound >= upperBound) {
            throw new IllegalArgumentException("Invalid bounds");
        }
        this.lowerBound = lowerBound;
        this.upperBound = upperBound;
        this.includeLowerBound = includeLowerBound;
        this.includeUpperBound = includeUpperBound;
        this.itemCount = 0;
    }

    public double getLowerBound() {
        return this.lowerBound;
    }

    public double getUpperBound() {
        return this.upperBound;
    }

    public int getItemCount() {
        return this.itemCount;
    }

    public void setItemCount(int count) {
        this.itemCount = count;
    }

    public boolean accepts(double value) {
        if (Double.isNaN(value) || value < this.lowerBound || value > this.upperBound) {
            return false;
        }
        if (value == this.lowerBound) {
            return this.includeLowerBound;
        }
        if (value == this.upperBound) {
            return this.includeUpperBound;
        }
        return true;
    }

    public boolean overlapsWith(SimpleHistogramBin bin) {
        if (this.upperBound < bin.lowerBound) {
            return false;
        }
        if (this.lowerBound > bin.upperBound) {
            return false;
        }
        if (this.upperBound == bin.lowerBound) {
            if (this.includeUpperBound && bin.includeLowerBound) {
                return true;
            }
            return false;
        } else if (this.lowerBound != bin.upperBound) {
            return true;
        } else {
            if (this.includeLowerBound && bin.includeUpperBound) {
                return true;
            }
            return false;
        }
    }

    public int compareTo(Object obj) {
        if (!(obj instanceof SimpleHistogramBin)) {
            return 0;
        }
        SimpleHistogramBin bin = (SimpleHistogramBin) obj;
        if (this.lowerBound < bin.lowerBound) {
            return -1;
        }
        if (this.lowerBound > bin.lowerBound) {
            return 1;
        }
        if (this.upperBound < bin.upperBound) {
            return -1;
        }
        if (this.upperBound > bin.upperBound) {
            return 1;
        }
        return 0;
    }

    public boolean equals(Object obj) {
        if (!(obj instanceof SimpleHistogramBin)) {
            return false;
        }
        SimpleHistogramBin that = (SimpleHistogramBin) obj;
        if (this.lowerBound == that.lowerBound && this.upperBound == that.upperBound && this.includeLowerBound == that.includeLowerBound && this.includeUpperBound == that.includeUpperBound && this.itemCount == that.itemCount) {
            return true;
        }
        return false;
    }

    public Object clone() throws CloneNotSupportedException {
        return super.clone();
    }
}
