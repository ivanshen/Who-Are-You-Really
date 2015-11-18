package org.jfree.data.xy;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyVetoException;
import java.beans.VetoableChangeListener;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import org.jfree.chart.HashUtilities;
import org.jfree.chart.urls.StandardXYURLGenerator;
import org.jfree.chart.util.ParamChecks;
import org.jfree.data.DomainInfo;
import org.jfree.data.DomainOrder;
import org.jfree.data.Range;
import org.jfree.data.RangeInfo;
import org.jfree.data.UnknownKeyException;
import org.jfree.data.general.Series;
import org.jfree.data.xml.DatasetTags;
import org.jfree.util.ObjectUtilities;
import org.jfree.util.PublicCloneable;

public class XYSeriesCollection extends AbstractIntervalXYDataset implements IntervalXYDataset, DomainInfo, RangeInfo, VetoableChangeListener, PublicCloneable, Serializable {
    private static final long serialVersionUID = -7590013825931496766L;
    private List data;
    private IntervalXYDelegate intervalDelegate;

    public XYSeriesCollection() {
        this(null);
    }

    public XYSeriesCollection(XYSeries series) {
        this.data = new ArrayList();
        this.intervalDelegate = new IntervalXYDelegate(this, false);
        addChangeListener(this.intervalDelegate);
        if (series != null) {
            this.data.add(series);
            series.addChangeListener(this);
            series.addVetoableChangeListener(this);
        }
    }

    public DomainOrder getDomainOrder() {
        int seriesCount = getSeriesCount();
        for (int i = 0; i < seriesCount; i++) {
            if (!getSeries(i).getAutoSort()) {
                return DomainOrder.NONE;
            }
        }
        return DomainOrder.ASCENDING;
    }

    public void addSeries(XYSeries series) {
        ParamChecks.nullNotPermitted(series, StandardXYURLGenerator.DEFAULT_SERIES_PARAMETER);
        if (getSeriesIndex(series.getKey()) >= 0) {
            throw new IllegalArgumentException("This dataset already contains a series with the key " + series.getKey());
        }
        this.data.add(series);
        series.addChangeListener(this);
        series.addVetoableChangeListener(this);
        fireDatasetChanged();
    }

    public void removeSeries(int series) {
        if (series < 0 || series >= getSeriesCount()) {
            throw new IllegalArgumentException("Series index out of bounds.");
        }
        XYSeries s = (XYSeries) this.data.get(series);
        if (s != null) {
            removeSeries(s);
        }
    }

    public void removeSeries(XYSeries series) {
        ParamChecks.nullNotPermitted(series, StandardXYURLGenerator.DEFAULT_SERIES_PARAMETER);
        if (this.data.contains(series)) {
            series.removeChangeListener(this);
            series.removeVetoableChangeListener(this);
            this.data.remove(series);
            fireDatasetChanged();
        }
    }

    public void removeAllSeries() {
        for (int i = 0; i < this.data.size(); i++) {
            XYSeries series = (XYSeries) this.data.get(i);
            series.removeChangeListener(this);
            series.removeVetoableChangeListener(this);
        }
        this.data.clear();
        fireDatasetChanged();
    }

    public int getSeriesCount() {
        return this.data.size();
    }

    public List getSeries() {
        return Collections.unmodifiableList(this.data);
    }

    public int indexOf(XYSeries series) {
        ParamChecks.nullNotPermitted(series, StandardXYURLGenerator.DEFAULT_SERIES_PARAMETER);
        return this.data.indexOf(series);
    }

    public XYSeries getSeries(int series) {
        if (series >= 0 && series < getSeriesCount()) {
            return (XYSeries) this.data.get(series);
        }
        throw new IllegalArgumentException("Series index out of bounds");
    }

    public XYSeries getSeries(Comparable key) {
        ParamChecks.nullNotPermitted(key, "key");
        for (XYSeries series : this.data) {
            if (key.equals(series.getKey())) {
                return series;
            }
        }
        throw new UnknownKeyException("Key not found: " + key);
    }

    public Comparable getSeriesKey(int series) {
        return getSeries(series).getKey();
    }

    public int getSeriesIndex(Comparable key) {
        ParamChecks.nullNotPermitted(key, "key");
        int seriesCount = getSeriesCount();
        for (int i = 0; i < seriesCount; i++) {
            if (key.equals(((XYSeries) this.data.get(i)).getKey())) {
                return i;
            }
        }
        return -1;
    }

    public int getItemCount(int series) {
        return getSeries(series).getItemCount();
    }

    public Number getX(int series, int item) {
        return ((XYSeries) this.data.get(series)).getX(item);
    }

    public Number getStartX(int series, int item) {
        return this.intervalDelegate.getStartX(series, item);
    }

    public Number getEndX(int series, int item) {
        return this.intervalDelegate.getEndX(series, item);
    }

    public Number getY(int series, int index) {
        return ((XYSeries) this.data.get(series)).getY(index);
    }

    public Number getStartY(int series, int item) {
        return getY(series, item);
    }

    public Number getEndY(int series, int item) {
        return getY(series, item);
    }

    public boolean equals(Object obj) {
        if (obj == this) {
            return true;
        }
        if (!(obj instanceof XYSeriesCollection)) {
            return false;
        }
        XYSeriesCollection that = (XYSeriesCollection) obj;
        if (this.intervalDelegate.equals(that.intervalDelegate)) {
            return ObjectUtilities.equal(this.data, that.data);
        }
        return false;
    }

    public Object clone() throws CloneNotSupportedException {
        XYSeriesCollection clone = (XYSeriesCollection) super.clone();
        clone.data = (List) ObjectUtilities.deepClone(this.data);
        clone.intervalDelegate = (IntervalXYDelegate) this.intervalDelegate.clone();
        return clone;
    }

    public int hashCode() {
        return HashUtilities.hashCode(HashUtilities.hashCode(5, this.intervalDelegate), this.data);
    }

    public double getDomainLowerBound(boolean includeInterval) {
        if (includeInterval) {
            return this.intervalDelegate.getDomainLowerBound(includeInterval);
        }
        double result = Double.NaN;
        int seriesCount = getSeriesCount();
        for (int s = 0; s < seriesCount; s++) {
            double lowX = getSeries(s).getMinX();
            if (Double.isNaN(result)) {
                result = lowX;
            } else if (!Double.isNaN(lowX)) {
                result = Math.min(result, lowX);
            }
        }
        return result;
    }

    public double getDomainUpperBound(boolean includeInterval) {
        if (includeInterval) {
            return this.intervalDelegate.getDomainUpperBound(includeInterval);
        }
        double result = Double.NaN;
        int seriesCount = getSeriesCount();
        for (int s = 0; s < seriesCount; s++) {
            double hiX = getSeries(s).getMaxX();
            if (Double.isNaN(result)) {
                result = hiX;
            } else if (!Double.isNaN(hiX)) {
                result = Math.max(result, hiX);
            }
        }
        return result;
    }

    public Range getDomainBounds(boolean includeInterval) {
        if (includeInterval) {
            return this.intervalDelegate.getDomainBounds(includeInterval);
        }
        double lower = Double.POSITIVE_INFINITY;
        double upper = Double.NEGATIVE_INFINITY;
        int seriesCount = getSeriesCount();
        for (int s = 0; s < seriesCount; s++) {
            XYSeries series = getSeries(s);
            double minX = series.getMinX();
            if (!Double.isNaN(minX)) {
                lower = Math.min(lower, minX);
            }
            double maxX = series.getMaxX();
            if (!Double.isNaN(maxX)) {
                upper = Math.max(upper, maxX);
            }
        }
        if (lower > upper) {
            return null;
        }
        return new Range(lower, upper);
    }

    public double getIntervalWidth() {
        return this.intervalDelegate.getIntervalWidth();
    }

    public void setIntervalWidth(double width) {
        if (width < 0.0d) {
            throw new IllegalArgumentException("Negative 'width' argument.");
        }
        this.intervalDelegate.setFixedIntervalWidth(width);
        fireDatasetChanged();
    }

    public double getIntervalPositionFactor() {
        return this.intervalDelegate.getIntervalPositionFactor();
    }

    public void setIntervalPositionFactor(double factor) {
        this.intervalDelegate.setIntervalPositionFactor(factor);
        fireDatasetChanged();
    }

    public boolean isAutoWidth() {
        return this.intervalDelegate.isAutoWidth();
    }

    public void setAutoWidth(boolean b) {
        this.intervalDelegate.setAutoWidth(b);
        fireDatasetChanged();
    }

    public Range getRangeBounds(boolean includeInterval) {
        double lower = Double.POSITIVE_INFINITY;
        double upper = Double.NEGATIVE_INFINITY;
        int seriesCount = getSeriesCount();
        for (int s = 0; s < seriesCount; s++) {
            XYSeries series = getSeries(s);
            double minY = series.getMinY();
            if (!Double.isNaN(minY)) {
                lower = Math.min(lower, minY);
            }
            double maxY = series.getMaxY();
            if (!Double.isNaN(maxY)) {
                upper = Math.max(upper, maxY);
            }
        }
        if (lower > upper) {
            return null;
        }
        return new Range(lower, upper);
    }

    public double getRangeLowerBound(boolean includeInterval) {
        double result = Double.NaN;
        int seriesCount = getSeriesCount();
        for (int s = 0; s < seriesCount; s++) {
            double lowY = getSeries(s).getMinY();
            if (Double.isNaN(result)) {
                result = lowY;
            } else if (!Double.isNaN(lowY)) {
                result = Math.min(result, lowY);
            }
        }
        return result;
    }

    public double getRangeUpperBound(boolean includeInterval) {
        double result = Double.NaN;
        int seriesCount = getSeriesCount();
        for (int s = 0; s < seriesCount; s++) {
            double hiY = getSeries(s).getMaxY();
            if (Double.isNaN(result)) {
                result = hiY;
            } else if (!Double.isNaN(hiY)) {
                result = Math.max(result, hiY);
            }
        }
        return result;
    }

    public void vetoableChange(PropertyChangeEvent e) throws PropertyVetoException {
        if (!DatasetTags.KEY_TAG.equals(e.getPropertyName())) {
            return;
        }
        if (getSeriesIndex(((Series) e.getSource()).getKey()) == -1) {
            throw new IllegalStateException("Receiving events from a series that does not belong to this collection.");
        } else if (getSeriesIndex((Comparable) e.getNewValue()) >= 0) {
            throw new PropertyVetoException("Duplicate key2", e);
        }
    }
}
