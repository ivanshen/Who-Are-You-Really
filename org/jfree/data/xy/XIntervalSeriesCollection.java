package org.jfree.data.xy;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import org.jfree.chart.urls.StandardXYURLGenerator;
import org.jfree.chart.util.ParamChecks;
import org.jfree.util.ObjectUtilities;
import org.jfree.util.PublicCloneable;

public class XIntervalSeriesCollection extends AbstractIntervalXYDataset implements IntervalXYDataset, PublicCloneable, Serializable {
    private List data;

    public XIntervalSeriesCollection() {
        this.data = new ArrayList();
    }

    public void addSeries(XIntervalSeries series) {
        ParamChecks.nullNotPermitted(series, StandardXYURLGenerator.DEFAULT_SERIES_PARAMETER);
        this.data.add(series);
        series.addChangeListener(this);
        fireDatasetChanged();
    }

    public int getSeriesCount() {
        return this.data.size();
    }

    public XIntervalSeries getSeries(int series) {
        if (series >= 0 && series < getSeriesCount()) {
            return (XIntervalSeries) this.data.get(series);
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
        return ((XIntervalDataItem) ((XIntervalSeries) this.data.get(series)).getDataItem(item)).getX();
    }

    public double getStartXValue(int series, int item) {
        return ((XIntervalSeries) this.data.get(series)).getXLowValue(item);
    }

    public double getEndXValue(int series, int item) {
        return ((XIntervalSeries) this.data.get(series)).getXHighValue(item);
    }

    public double getYValue(int series, int item) {
        return ((XIntervalSeries) this.data.get(series)).getYValue(item);
    }

    public Number getY(int series, int item) {
        return new Double(((XIntervalDataItem) ((XIntervalSeries) this.data.get(series)).getDataItem(item)).getYValue());
    }

    public Number getStartX(int series, int item) {
        return new Double(((XIntervalDataItem) ((XIntervalSeries) this.data.get(series)).getDataItem(item)).getXLowValue());
    }

    public Number getEndX(int series, int item) {
        return new Double(((XIntervalDataItem) ((XIntervalSeries) this.data.get(series)).getDataItem(item)).getXHighValue());
    }

    public Number getStartY(int series, int item) {
        return getY(series, item);
    }

    public Number getEndY(int series, int item) {
        return getY(series, item);
    }

    public void removeSeries(int series) {
        if (series < 0 || series >= getSeriesCount()) {
            throw new IllegalArgumentException("Series index out of bounds.");
        }
        ((XIntervalSeries) this.data.get(series)).removeChangeListener(this);
        this.data.remove(series);
        fireDatasetChanged();
    }

    public void removeSeries(XIntervalSeries series) {
        ParamChecks.nullNotPermitted(series, StandardXYURLGenerator.DEFAULT_SERIES_PARAMETER);
        if (this.data.contains(series)) {
            series.removeChangeListener(this);
            this.data.remove(series);
            fireDatasetChanged();
        }
    }

    public void removeAllSeries() {
        for (int i = 0; i < this.data.size(); i++) {
            ((XIntervalSeries) this.data.get(i)).removeChangeListener(this);
        }
        this.data.clear();
        fireDatasetChanged();
    }

    public boolean equals(Object obj) {
        if (obj == this) {
            return true;
        }
        if (!(obj instanceof XIntervalSeriesCollection)) {
            return false;
        }
        return ObjectUtilities.equal(this.data, ((XIntervalSeriesCollection) obj).data);
    }

    public Object clone() throws CloneNotSupportedException {
        XIntervalSeriesCollection clone = (XIntervalSeriesCollection) super.clone();
        clone.data = (List) ObjectUtilities.deepClone(this.data);
        return clone;
    }
}
