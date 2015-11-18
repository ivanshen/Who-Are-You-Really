package org.jfree.data.statistics;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import org.jfree.chart.axis.DateAxis;
import org.jfree.chart.util.ParamChecks;
import org.jfree.data.DomainOrder;
import org.jfree.data.general.DatasetChangeEvent;
import org.jfree.data.xy.AbstractIntervalXYDataset;
import org.jfree.data.xy.IntervalXYDataset;
import org.jfree.util.ObjectUtilities;
import org.jfree.util.PublicCloneable;

public class SimpleHistogramDataset extends AbstractIntervalXYDataset implements IntervalXYDataset, Cloneable, PublicCloneable, Serializable {
    private static final long serialVersionUID = 7997996479768018443L;
    private boolean adjustForBinSize;
    private List bins;
    private Comparable key;

    public SimpleHistogramDataset(Comparable key) {
        ParamChecks.nullNotPermitted(key, "key");
        this.key = key;
        this.bins = new ArrayList();
        this.adjustForBinSize = true;
    }

    public boolean getAdjustForBinSize() {
        return this.adjustForBinSize;
    }

    public void setAdjustForBinSize(boolean adjust) {
        this.adjustForBinSize = adjust;
        notifyListeners(new DatasetChangeEvent(this, this));
    }

    public int getSeriesCount() {
        return 1;
    }

    public Comparable getSeriesKey(int series) {
        return this.key;
    }

    public DomainOrder getDomainOrder() {
        return DomainOrder.ASCENDING;
    }

    public int getItemCount(int series) {
        return this.bins.size();
    }

    public void addBin(SimpleHistogramBin bin) {
        for (SimpleHistogramBin existingBin : this.bins) {
            if (bin.overlapsWith(existingBin)) {
                throw new RuntimeException("Overlapping bin");
            }
        }
        this.bins.add(bin);
        Collections.sort(this.bins);
    }

    public void addObservation(double value) {
        addObservation(value, true);
    }

    public void addObservation(double value, boolean notify) {
        boolean placed = false;
        Iterator iterator = this.bins.iterator();
        while (iterator.hasNext() && !placed) {
            SimpleHistogramBin bin = (SimpleHistogramBin) iterator.next();
            if (bin.accepts(value)) {
                bin.setItemCount(bin.getItemCount() + 1);
                placed = true;
            }
        }
        if (!placed) {
            throw new RuntimeException("No bin.");
        } else if (notify) {
            notifyListeners(new DatasetChangeEvent(this, this));
        }
    }

    public void addObservations(double[] values) {
        for (double addObservation : values) {
            addObservation(addObservation, false);
        }
        notifyListeners(new DatasetChangeEvent(this, this));
    }

    public void clearObservations() {
        for (SimpleHistogramBin bin : this.bins) {
            bin.setItemCount(0);
        }
        notifyListeners(new DatasetChangeEvent(this, this));
    }

    public void removeAllBins() {
        this.bins = new ArrayList();
        notifyListeners(new DatasetChangeEvent(this, this));
    }

    public Number getX(int series, int item) {
        return new Double(getXValue(series, item));
    }

    public double getXValue(int series, int item) {
        SimpleHistogramBin bin = (SimpleHistogramBin) this.bins.get(item);
        return (bin.getLowerBound() + bin.getUpperBound()) / DateAxis.DEFAULT_AUTO_RANGE_MINIMUM_SIZE_IN_MILLISECONDS;
    }

    public Number getY(int series, int item) {
        return new Double(getYValue(series, item));
    }

    public double getYValue(int series, int item) {
        SimpleHistogramBin bin = (SimpleHistogramBin) this.bins.get(item);
        if (this.adjustForBinSize) {
            return ((double) bin.getItemCount()) / (bin.getUpperBound() - bin.getLowerBound());
        }
        return (double) bin.getItemCount();
    }

    public Number getStartX(int series, int item) {
        return new Double(getStartXValue(series, item));
    }

    public double getStartXValue(int series, int item) {
        return ((SimpleHistogramBin) this.bins.get(item)).getLowerBound();
    }

    public Number getEndX(int series, int item) {
        return new Double(getEndXValue(series, item));
    }

    public double getEndXValue(int series, int item) {
        return ((SimpleHistogramBin) this.bins.get(item)).getUpperBound();
    }

    public Number getStartY(int series, int item) {
        return getY(series, item);
    }

    public double getStartYValue(int series, int item) {
        return getYValue(series, item);
    }

    public Number getEndY(int series, int item) {
        return getY(series, item);
    }

    public double getEndYValue(int series, int item) {
        return getYValue(series, item);
    }

    public boolean equals(Object obj) {
        if (obj == this) {
            return true;
        }
        if (!(obj instanceof SimpleHistogramDataset)) {
            return false;
        }
        SimpleHistogramDataset that = (SimpleHistogramDataset) obj;
        if (!this.key.equals(that.key)) {
            return false;
        }
        if (this.adjustForBinSize != that.adjustForBinSize) {
            return false;
        }
        if (this.bins.equals(that.bins)) {
            return true;
        }
        return false;
    }

    public Object clone() throws CloneNotSupportedException {
        SimpleHistogramDataset clone = (SimpleHistogramDataset) super.clone();
        clone.bins = (List) ObjectUtilities.deepClone(this.bins);
        return clone;
    }
}
