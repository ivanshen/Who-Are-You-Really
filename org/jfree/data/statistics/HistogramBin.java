package org.jfree.data.statistics;

import java.io.Serializable;

public class HistogramBin implements Cloneable, Serializable {
    private static final long serialVersionUID = 7614685080015589931L;
    private int count;
    private double endBoundary;
    private double startBoundary;

    public HistogramBin(double startBoundary, double endBoundary) {
        if (startBoundary > endBoundary) {
            throw new IllegalArgumentException("HistogramBin():  startBoundary > endBoundary.");
        }
        this.count = 0;
        this.startBoundary = startBoundary;
        this.endBoundary = endBoundary;
    }

    public int getCount() {
        return this.count;
    }

    public void incrementCount() {
        this.count++;
    }

    public double getStartBoundary() {
        return this.startBoundary;
    }

    public double getEndBoundary() {
        return this.endBoundary;
    }

    public double getBinWidth() {
        return this.endBoundary - this.startBoundary;
    }

    public boolean equals(Object obj) {
        boolean z = true;
        if (obj == null) {
            return false;
        }
        if (obj == this) {
            return true;
        }
        if (!(obj instanceof HistogramBin)) {
            return false;
        }
        boolean b0;
        HistogramBin bin = (HistogramBin) obj;
        if (bin.startBoundary == this.startBoundary) {
            b0 = true;
        } else {
            b0 = false;
        }
        boolean b1;
        if (bin.endBoundary == this.endBoundary) {
            b1 = true;
        } else {
            b1 = false;
        }
        boolean b2;
        if (bin.count == this.count) {
            b2 = true;
        } else {
            b2 = false;
        }
        if (!(b0 && b1 && b2)) {
            z = false;
        }
        return z;
    }

    public Object clone() throws CloneNotSupportedException {
        return super.clone();
    }
}
