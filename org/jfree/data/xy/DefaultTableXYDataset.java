package org.jfree.data.xy;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import org.jfree.chart.urls.StandardXYURLGenerator;
import org.jfree.chart.util.ParamChecks;
import org.jfree.data.DomainInfo;
import org.jfree.data.Range;
import org.jfree.data.general.DatasetUtilities;
import org.jfree.data.general.SeriesChangeEvent;
import org.jfree.util.ObjectUtilities;
import org.jfree.util.PublicCloneable;

public class DefaultTableXYDataset extends AbstractIntervalXYDataset implements TableXYDataset, IntervalXYDataset, DomainInfo, PublicCloneable {
    private boolean autoPrune;
    private List data;
    private IntervalXYDelegate intervalDelegate;
    private boolean propagateEvents;
    private HashSet xPoints;

    public DefaultTableXYDataset() {
        this(false);
    }

    public DefaultTableXYDataset(boolean autoPrune) {
        this.data = null;
        this.xPoints = null;
        this.propagateEvents = true;
        this.autoPrune = false;
        this.autoPrune = autoPrune;
        this.data = new ArrayList();
        this.xPoints = new HashSet();
        this.intervalDelegate = new IntervalXYDelegate(this, false);
        addChangeListener(this.intervalDelegate);
    }

    public boolean isAutoPrune() {
        return this.autoPrune;
    }

    public void addSeries(XYSeries series) {
        ParamChecks.nullNotPermitted(series, StandardXYURLGenerator.DEFAULT_SERIES_PARAMETER);
        if (series.getAllowDuplicateXValues()) {
            throw new IllegalArgumentException("Cannot accept XYSeries that allow duplicate values. Use XYSeries(seriesName, <sort>, false) constructor.");
        }
        updateXPoints(series);
        this.data.add(series);
        series.addChangeListener(this);
        fireDatasetChanged();
    }

    private void updateXPoints(XYSeries series) {
        ParamChecks.nullNotPermitted(series, StandardXYURLGenerator.DEFAULT_SERIES_PARAMETER);
        HashSet seriesXPoints = new HashSet();
        boolean savedState = this.propagateEvents;
        this.propagateEvents = false;
        for (int itemNo = 0; itemNo < series.getItemCount(); itemNo++) {
            Number xValue = series.getX(itemNo);
            seriesXPoints.add(xValue);
            if (!this.xPoints.contains(xValue)) {
                this.xPoints.add(xValue);
                int seriesCount = this.data.size();
                for (int seriesNo = 0; seriesNo < seriesCount; seriesNo++) {
                    XYSeries dataSeries = (XYSeries) this.data.get(seriesNo);
                    if (!dataSeries.equals(series)) {
                        dataSeries.add(xValue, null);
                    }
                }
            }
        }
        Iterator iterator = this.xPoints.iterator();
        while (iterator.hasNext()) {
            Number xPoint = (Number) iterator.next();
            if (!seriesXPoints.contains(xPoint)) {
                series.add(xPoint, null);
            }
        }
        this.propagateEvents = savedState;
    }

    public void updateXPoints() {
        this.propagateEvents = false;
        for (int s = 0; s < this.data.size(); s++) {
            updateXPoints((XYSeries) this.data.get(s));
        }
        if (this.autoPrune) {
            prune();
        }
        this.propagateEvents = true;
    }

    public int getSeriesCount() {
        return this.data.size();
    }

    public int getItemCount() {
        if (this.xPoints == null) {
            return 0;
        }
        return this.xPoints.size();
    }

    public XYSeries getSeries(int series) {
        if (series >= 0 && series < getSeriesCount()) {
            return (XYSeries) this.data.get(series);
        }
        throw new IllegalArgumentException("Index outside valid range.");
    }

    public Comparable getSeriesKey(int series) {
        return getSeries(series).getKey();
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

    public void removeAllSeries() {
        for (int i = 0; i < this.data.size(); i++) {
            ((XYSeries) this.data.get(i)).removeChangeListener(this);
        }
        this.data.clear();
        this.xPoints.clear();
        fireDatasetChanged();
    }

    public void removeSeries(XYSeries series) {
        ParamChecks.nullNotPermitted(series, StandardXYURLGenerator.DEFAULT_SERIES_PARAMETER);
        if (this.data.contains(series)) {
            series.removeChangeListener(this);
            this.data.remove(series);
            if (this.data.isEmpty()) {
                this.xPoints.clear();
            }
            fireDatasetChanged();
        }
    }

    public void removeSeries(int series) {
        if (series < 0 || series > getSeriesCount()) {
            throw new IllegalArgumentException("Index outside valid range.");
        }
        ((XYSeries) this.data.get(series)).removeChangeListener(this);
        this.data.remove(series);
        if (this.data.isEmpty()) {
            this.xPoints.clear();
        } else if (this.autoPrune) {
            prune();
        }
        fireDatasetChanged();
    }

    public void removeAllValuesForX(Number x) {
        ParamChecks.nullNotPermitted(x, "x");
        boolean savedState = this.propagateEvents;
        this.propagateEvents = false;
        for (int s = 0; s < this.data.size(); s++) {
            ((XYSeries) this.data.get(s)).remove(x);
        }
        this.propagateEvents = savedState;
        this.xPoints.remove(x);
        fireDatasetChanged();
    }

    protected boolean canPrune(Number x) {
        for (int s = 0; s < this.data.size(); s++) {
            XYSeries series = (XYSeries) this.data.get(s);
            if (series.getY(series.indexOf(x)) != null) {
                return false;
            }
        }
        return true;
    }

    public void prune() {
        Iterator iterator = ((HashSet) this.xPoints.clone()).iterator();
        while (iterator.hasNext()) {
            Number x = (Number) iterator.next();
            if (canPrune(x)) {
                removeAllValuesForX(x);
            }
        }
    }

    public void seriesChanged(SeriesChangeEvent event) {
        if (this.propagateEvents) {
            updateXPoints();
            fireDatasetChanged();
        }
    }

    public boolean equals(Object obj) {
        if (obj == this) {
            return true;
        }
        if (!(obj instanceof DefaultTableXYDataset)) {
            return false;
        }
        DefaultTableXYDataset that = (DefaultTableXYDataset) obj;
        if (this.autoPrune != that.autoPrune) {
            return false;
        }
        if (this.propagateEvents != that.propagateEvents) {
            return false;
        }
        if (!this.intervalDelegate.equals(that.intervalDelegate)) {
            return false;
        }
        if (ObjectUtilities.equal(this.data, that.data)) {
            return true;
        }
        return false;
    }

    public int hashCode() {
        int result;
        int hashCode;
        int i = 1;
        if (this.data != null) {
            result = this.data.hashCode();
        } else {
            result = 0;
        }
        int i2 = result * 29;
        if (this.xPoints != null) {
            hashCode = this.xPoints.hashCode();
        } else {
            hashCode = 0;
        }
        i2 = (i2 + hashCode) * 29;
        if (this.propagateEvents) {
            hashCode = 1;
        } else {
            hashCode = 0;
        }
        hashCode = (i2 + hashCode) * 29;
        if (!this.autoPrune) {
            i = 0;
        }
        return hashCode + i;
    }

    public Object clone() throws CloneNotSupportedException {
        DefaultTableXYDataset clone = (DefaultTableXYDataset) super.clone();
        int seriesCount = this.data.size();
        clone.data = new ArrayList(seriesCount);
        for (int i = 0; i < seriesCount; i++) {
            clone.data.add(((XYSeries) this.data.get(i)).clone());
        }
        clone.intervalDelegate = new IntervalXYDelegate(clone);
        clone.intervalDelegate.setFixedIntervalWidth(getIntervalWidth());
        clone.intervalDelegate.setAutoWidth(isAutoWidth());
        clone.intervalDelegate.setIntervalPositionFactor(getIntervalPositionFactor());
        clone.updateXPoints();
        return clone;
    }

    public double getDomainLowerBound(boolean includeInterval) {
        return this.intervalDelegate.getDomainLowerBound(includeInterval);
    }

    public double getDomainUpperBound(boolean includeInterval) {
        return this.intervalDelegate.getDomainUpperBound(includeInterval);
    }

    public Range getDomainBounds(boolean includeInterval) {
        if (includeInterval) {
            return this.intervalDelegate.getDomainBounds(includeInterval);
        }
        return DatasetUtilities.iterateDomainBounds(this, includeInterval);
    }

    public double getIntervalPositionFactor() {
        return this.intervalDelegate.getIntervalPositionFactor();
    }

    public void setIntervalPositionFactor(double d) {
        this.intervalDelegate.setIntervalPositionFactor(d);
        fireDatasetChanged();
    }

    public double getIntervalWidth() {
        return this.intervalDelegate.getIntervalWidth();
    }

    public void setIntervalWidth(double d) {
        this.intervalDelegate.setFixedIntervalWidth(d);
        fireDatasetChanged();
    }

    public boolean isAutoWidth() {
        return this.intervalDelegate.isAutoWidth();
    }

    public void setAutoWidth(boolean b) {
        this.intervalDelegate.setAutoWidth(b);
        fireDatasetChanged();
    }
}
