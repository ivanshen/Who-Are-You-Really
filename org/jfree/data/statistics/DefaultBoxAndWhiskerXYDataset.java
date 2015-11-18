package org.jfree.data.statistics;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import org.jfree.chart.axis.DateAxis;
import org.jfree.data.Range;
import org.jfree.data.RangeInfo;
import org.jfree.data.xy.AbstractXYDataset;
import org.jfree.util.ObjectUtilities;

public class DefaultBoxAndWhiskerXYDataset extends AbstractXYDataset implements BoxAndWhiskerXYDataset, RangeInfo {
    private List dates;
    private double faroutCoefficient;
    private List items;
    private Number maximumRangeValue;
    private Number minimumRangeValue;
    private double outlierCoefficient;
    private Range rangeBounds;
    private Comparable seriesKey;

    public DefaultBoxAndWhiskerXYDataset(Comparable seriesKey) {
        this.outlierCoefficient = 1.5d;
        this.faroutCoefficient = DateAxis.DEFAULT_AUTO_RANGE_MINIMUM_SIZE_IN_MILLISECONDS;
        this.seriesKey = seriesKey;
        this.dates = new ArrayList();
        this.items = new ArrayList();
        this.minimumRangeValue = null;
        this.maximumRangeValue = null;
        this.rangeBounds = null;
    }

    public double getOutlierCoefficient() {
        return this.outlierCoefficient;
    }

    public void setOutlierCoefficient(double outlierCoefficient) {
        this.outlierCoefficient = outlierCoefficient;
    }

    public double getFaroutCoefficient() {
        return this.faroutCoefficient;
    }

    public void setFaroutCoefficient(double faroutCoefficient) {
        if (faroutCoefficient > getOutlierCoefficient()) {
            this.faroutCoefficient = faroutCoefficient;
            return;
        }
        throw new IllegalArgumentException("Farout value must be greater than the outlier value, which is currently set at: (" + getOutlierCoefficient() + ")");
    }

    public int getSeriesCount() {
        return 1;
    }

    public int getItemCount(int series) {
        return this.dates.size();
    }

    public void add(Date date, BoxAndWhiskerItem item) {
        this.dates.add(date);
        this.items.add(item);
        if (this.minimumRangeValue == null) {
            this.minimumRangeValue = item.getMinRegularValue();
        } else if (item.getMinRegularValue().doubleValue() < this.minimumRangeValue.doubleValue()) {
            this.minimumRangeValue = item.getMinRegularValue();
        }
        if (this.maximumRangeValue == null) {
            this.maximumRangeValue = item.getMaxRegularValue();
        } else if (item.getMaxRegularValue().doubleValue() > this.maximumRangeValue.doubleValue()) {
            this.maximumRangeValue = item.getMaxRegularValue();
        }
        this.rangeBounds = new Range(this.minimumRangeValue.doubleValue(), this.maximumRangeValue.doubleValue());
        fireDatasetChanged();
    }

    public Comparable getSeriesKey(int i) {
        return this.seriesKey;
    }

    public BoxAndWhiskerItem getItem(int series, int item) {
        return (BoxAndWhiskerItem) this.items.get(item);
    }

    public Number getX(int series, int item) {
        return new Long(((Date) this.dates.get(item)).getTime());
    }

    public Date getXDate(int series, int item) {
        return (Date) this.dates.get(item);
    }

    public Number getY(int series, int item) {
        return getMeanValue(series, item);
    }

    public Number getMeanValue(int series, int item) {
        BoxAndWhiskerItem stats = (BoxAndWhiskerItem) this.items.get(item);
        if (stats != null) {
            return stats.getMean();
        }
        return null;
    }

    public Number getMedianValue(int series, int item) {
        BoxAndWhiskerItem stats = (BoxAndWhiskerItem) this.items.get(item);
        if (stats != null) {
            return stats.getMedian();
        }
        return null;
    }

    public Number getQ1Value(int series, int item) {
        BoxAndWhiskerItem stats = (BoxAndWhiskerItem) this.items.get(item);
        if (stats != null) {
            return stats.getQ1();
        }
        return null;
    }

    public Number getQ3Value(int series, int item) {
        BoxAndWhiskerItem stats = (BoxAndWhiskerItem) this.items.get(item);
        if (stats != null) {
            return stats.getQ3();
        }
        return null;
    }

    public Number getMinRegularValue(int series, int item) {
        BoxAndWhiskerItem stats = (BoxAndWhiskerItem) this.items.get(item);
        if (stats != null) {
            return stats.getMinRegularValue();
        }
        return null;
    }

    public Number getMaxRegularValue(int series, int item) {
        BoxAndWhiskerItem stats = (BoxAndWhiskerItem) this.items.get(item);
        if (stats != null) {
            return stats.getMaxRegularValue();
        }
        return null;
    }

    public Number getMinOutlier(int series, int item) {
        BoxAndWhiskerItem stats = (BoxAndWhiskerItem) this.items.get(item);
        if (stats != null) {
            return stats.getMinOutlier();
        }
        return null;
    }

    public Number getMaxOutlier(int series, int item) {
        BoxAndWhiskerItem stats = (BoxAndWhiskerItem) this.items.get(item);
        if (stats != null) {
            return stats.getMaxOutlier();
        }
        return null;
    }

    public List getOutliers(int series, int item) {
        BoxAndWhiskerItem stats = (BoxAndWhiskerItem) this.items.get(item);
        if (stats != null) {
            return stats.getOutliers();
        }
        return null;
    }

    public double getRangeLowerBound(boolean includeInterval) {
        if (this.minimumRangeValue != null) {
            return this.minimumRangeValue.doubleValue();
        }
        return Double.NaN;
    }

    public double getRangeUpperBound(boolean includeInterval) {
        if (this.maximumRangeValue != null) {
            return this.maximumRangeValue.doubleValue();
        }
        return Double.NaN;
    }

    public Range getRangeBounds(boolean includeInterval) {
        return this.rangeBounds;
    }

    public boolean equals(Object obj) {
        if (obj == this) {
            return true;
        }
        if (!(obj instanceof DefaultBoxAndWhiskerXYDataset)) {
            return false;
        }
        DefaultBoxAndWhiskerXYDataset that = (DefaultBoxAndWhiskerXYDataset) obj;
        if (!ObjectUtilities.equal(this.seriesKey, that.seriesKey)) {
            return false;
        }
        if (!this.dates.equals(that.dates)) {
            return false;
        }
        if (this.items.equals(that.items)) {
            return true;
        }
        return false;
    }

    public Object clone() throws CloneNotSupportedException {
        DefaultBoxAndWhiskerXYDataset clone = (DefaultBoxAndWhiskerXYDataset) super.clone();
        clone.dates = new ArrayList(this.dates);
        clone.items = new ArrayList(this.items);
        return clone;
    }
}
