package org.jfree.data.xy;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import org.jfree.chart.urls.StandardXYURLGenerator;
import org.jfree.chart.util.ParamChecks;
import org.jfree.util.ObjectUtilities;
import org.jfree.util.PublicCloneable;

public class XYIntervalSeriesCollection extends AbstractIntervalXYDataset implements IntervalXYDataset, PublicCloneable, Serializable {
    private List data;

    public XYIntervalSeriesCollection() {
        this.data = new ArrayList();
    }

    public void addSeries(XYIntervalSeries series) {
        ParamChecks.nullNotPermitted(series, StandardXYURLGenerator.DEFAULT_SERIES_PARAMETER);
        this.data.add(series);
        series.addChangeListener(this);
        fireDatasetChanged();
    }

    public int getSeriesCount() {
        return this.data.size();
    }

    public XYIntervalSeries getSeries(int series) {
        if (series >= 0 && series < getSeriesCount()) {
            return (XYIntervalSeries) this.data.get(series);
        }
        throw new IllegalArgumentException("Series index out of bounds");
    }

    public Comparable getSeriesKey(int series) {
        return getSeries(series).getKey();
    }

    public int getItemCount(int series) {
        return getSeries(series).getItemCount();
    }

    public Number getX(int series, int item) {
        return ((XYIntervalSeries) this.data.get(series)).getX(item);
    }

    public double getStartXValue(int series, int item) {
        return ((XYIntervalSeries) this.data.get(series)).getXLowValue(item);
    }

    public double getEndXValue(int series, int item) {
        return ((XYIntervalSeries) this.data.get(series)).getXHighValue(item);
    }

    public double getYValue(int series, int item) {
        return ((XYIntervalSeries) this.data.get(series)).getYValue(item);
    }

    public double getStartYValue(int series, int item) {
        return ((XYIntervalSeries) this.data.get(series)).getYLowValue(item);
    }

    public double getEndYValue(int series, int item) {
        return ((XYIntervalSeries) this.data.get(series)).getYHighValue(item);
    }

    public Number getY(int series, int item) {
        return new Double(getYValue(series, item));
    }

    public Number getStartX(int series, int item) {
        return new Double(getStartXValue(series, item));
    }

    public Number getEndX(int series, int item) {
        return new Double(getEndXValue(series, item));
    }

    public Number getStartY(int series, int item) {
        return new Double(getStartYValue(series, item));
    }

    public Number getEndY(int series, int item) {
        return new Double(getEndYValue(series, item));
    }

    public void removeSeries(int series) {
        if (series < 0 || series >= getSeriesCount()) {
            throw new IllegalArgumentException("Series index out of bounds.");
        }
        ((XYIntervalSeries) this.data.get(series)).removeChangeListener(this);
        this.data.remove(series);
        fireDatasetChanged();
    }

    public void removeSeries(XYIntervalSeries series) {
        ParamChecks.nullNotPermitted(series, StandardXYURLGenerator.DEFAULT_SERIES_PARAMETER);
        if (this.data.contains(series)) {
            series.removeChangeListener(this);
            this.data.remove(series);
            fireDatasetChanged();
        }
    }

    public void removeAllSeries() {
        for (int i = 0; i < this.data.size(); i++) {
            ((XYIntervalSeries) this.data.get(i)).removeChangeListener(this);
        }
        this.data.clear();
        fireDatasetChanged();
    }

    public boolean equals(Object obj) {
        if (obj == this) {
            return true;
        }
        if (!(obj instanceof XYIntervalSeriesCollection)) {
            return false;
        }
        return ObjectUtilities.equal(this.data, ((XYIntervalSeriesCollection) obj).data);
    }

    public Object clone() throws CloneNotSupportedException {
        XYIntervalSeriesCollection clone = (XYIntervalSeriesCollection) super.clone();
        clone.data = new ArrayList(getSeriesCount());
        for (int i = 0; i < this.data.size(); i++) {
            clone.data.set(i, getSeries(i).clone());
        }
        return clone;
    }
}
