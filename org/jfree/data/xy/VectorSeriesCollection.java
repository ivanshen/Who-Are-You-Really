package org.jfree.data.xy;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import org.jfree.chart.urls.StandardXYURLGenerator;
import org.jfree.chart.util.ParamChecks;
import org.jfree.util.ObjectUtilities;
import org.jfree.util.PublicCloneable;

public class VectorSeriesCollection extends AbstractXYDataset implements VectorXYDataset, PublicCloneable, Serializable {
    private List data;

    public VectorSeriesCollection() {
        this.data = new ArrayList();
    }

    public void addSeries(VectorSeries series) {
        ParamChecks.nullNotPermitted(series, StandardXYURLGenerator.DEFAULT_SERIES_PARAMETER);
        this.data.add(series);
        series.addChangeListener(this);
        fireDatasetChanged();
    }

    public boolean removeSeries(VectorSeries series) {
        ParamChecks.nullNotPermitted(series, StandardXYURLGenerator.DEFAULT_SERIES_PARAMETER);
        boolean removed = this.data.remove(series);
        if (removed) {
            series.removeChangeListener(this);
            fireDatasetChanged();
        }
        return removed;
    }

    public void removeAllSeries() {
        for (int i = 0; i < this.data.size(); i++) {
            ((VectorSeries) this.data.get(i)).removeChangeListener(this);
        }
        this.data.clear();
        fireDatasetChanged();
    }

    public int getSeriesCount() {
        return this.data.size();
    }

    public VectorSeries getSeries(int series) {
        if (series >= 0 && series < getSeriesCount()) {
            return (VectorSeries) this.data.get(series);
        }
        throw new IllegalArgumentException("Series index out of bounds");
    }

    public Comparable getSeriesKey(int series) {
        return getSeries(series).getKey();
    }

    public int indexOf(VectorSeries series) {
        ParamChecks.nullNotPermitted(series, StandardXYURLGenerator.DEFAULT_SERIES_PARAMETER);
        return this.data.indexOf(series);
    }

    public int getItemCount(int series) {
        return getSeries(series).getItemCount();
    }

    public double getXValue(int series, int item) {
        return ((VectorDataItem) ((VectorSeries) this.data.get(series)).getDataItem(item)).getXValue();
    }

    public Number getX(int series, int item) {
        return new Double(getXValue(series, item));
    }

    public double getYValue(int series, int item) {
        return ((VectorDataItem) ((VectorSeries) this.data.get(series)).getDataItem(item)).getYValue();
    }

    public Number getY(int series, int item) {
        return new Double(getYValue(series, item));
    }

    public Vector getVector(int series, int item) {
        return ((VectorDataItem) ((VectorSeries) this.data.get(series)).getDataItem(item)).getVector();
    }

    public double getVectorXValue(int series, int item) {
        return ((VectorDataItem) ((VectorSeries) this.data.get(series)).getDataItem(item)).getVectorX();
    }

    public double getVectorYValue(int series, int item) {
        return ((VectorDataItem) ((VectorSeries) this.data.get(series)).getDataItem(item)).getVectorY();
    }

    public boolean equals(Object obj) {
        if (obj == this) {
            return true;
        }
        if (!(obj instanceof VectorSeriesCollection)) {
            return false;
        }
        return ObjectUtilities.equal(this.data, ((VectorSeriesCollection) obj).data);
    }

    public Object clone() throws CloneNotSupportedException {
        VectorSeriesCollection clone = (VectorSeriesCollection) super.clone();
        clone.data = (List) ObjectUtilities.deepClone(this.data);
        return clone;
    }
}
