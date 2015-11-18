package org.jfree.data.xy;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import org.jfree.chart.urls.StandardXYURLGenerator;
import org.jfree.chart.util.ParamChecks;
import org.jfree.util.ObjectUtilities;
import org.jfree.util.PublicCloneable;

public class MatrixSeriesCollection extends AbstractXYZDataset implements XYZDataset, PublicCloneable, Serializable {
    private static final long serialVersionUID = -3197705779242543945L;
    private List seriesList;

    public MatrixSeriesCollection() {
        this(null);
    }

    public MatrixSeriesCollection(MatrixSeries series) {
        this.seriesList = new ArrayList();
        if (series != null) {
            this.seriesList.add(series);
            series.addChangeListener(this);
        }
    }

    public int getItemCount(int seriesIndex) {
        return getSeries(seriesIndex).getItemCount();
    }

    public MatrixSeries getSeries(int seriesIndex) {
        if (seriesIndex >= 0 && seriesIndex <= getSeriesCount()) {
            return (MatrixSeries) this.seriesList.get(seriesIndex);
        }
        throw new IllegalArgumentException("Index outside valid range.");
    }

    public int getSeriesCount() {
        return this.seriesList.size();
    }

    public Comparable getSeriesKey(int seriesIndex) {
        return getSeries(seriesIndex).getKey();
    }

    public Number getX(int seriesIndex, int itemIndex) {
        return new Integer(((MatrixSeries) this.seriesList.get(seriesIndex)).getItemColumn(itemIndex));
    }

    public Number getY(int seriesIndex, int itemIndex) {
        return new Integer(((MatrixSeries) this.seriesList.get(seriesIndex)).getItemRow(itemIndex));
    }

    public Number getZ(int seriesIndex, int itemIndex) {
        return ((MatrixSeries) this.seriesList.get(seriesIndex)).getItem(itemIndex);
    }

    public void addSeries(MatrixSeries series) {
        ParamChecks.nullNotPermitted(series, StandardXYURLGenerator.DEFAULT_SERIES_PARAMETER);
        this.seriesList.add(series);
        series.addChangeListener(this);
        fireDatasetChanged();
    }

    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (obj == this) {
            return true;
        }
        if (!(obj instanceof MatrixSeriesCollection)) {
            return false;
        }
        return ObjectUtilities.equal(this.seriesList, ((MatrixSeriesCollection) obj).seriesList);
    }

    public int hashCode() {
        return this.seriesList != null ? this.seriesList.hashCode() : 0;
    }

    public Object clone() throws CloneNotSupportedException {
        MatrixSeriesCollection clone = (MatrixSeriesCollection) super.clone();
        clone.seriesList = (List) ObjectUtilities.deepClone(this.seriesList);
        return clone;
    }

    public void removeAllSeries() {
        for (int i = 0; i < this.seriesList.size(); i++) {
            ((MatrixSeries) this.seriesList.get(i)).removeChangeListener(this);
        }
        this.seriesList.clear();
        fireDatasetChanged();
    }

    public void removeSeries(MatrixSeries series) {
        ParamChecks.nullNotPermitted(series, StandardXYURLGenerator.DEFAULT_SERIES_PARAMETER);
        if (this.seriesList.contains(series)) {
            series.removeChangeListener(this);
            this.seriesList.remove(series);
            fireDatasetChanged();
        }
    }

    public void removeSeries(int seriesIndex) {
        if (seriesIndex < 0 || seriesIndex > getSeriesCount()) {
            throw new IllegalArgumentException("Index outside valid range.");
        }
        ((MatrixSeries) this.seriesList.get(seriesIndex)).removeChangeListener(this);
        this.seriesList.remove(seriesIndex);
        fireDatasetChanged();
    }
}
