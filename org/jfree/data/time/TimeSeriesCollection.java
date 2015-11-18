package org.jfree.data.time;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyVetoException;
import java.beans.VetoableChangeListener;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.List;
import java.util.TimeZone;
import org.jfree.chart.urls.StandardXYURLGenerator;
import org.jfree.chart.util.ParamChecks;
import org.jfree.data.DomainInfo;
import org.jfree.data.DomainOrder;
import org.jfree.data.Range;
import org.jfree.data.general.DatasetChangeEvent;
import org.jfree.data.general.Series;
import org.jfree.data.xml.DatasetTags;
import org.jfree.data.xy.AbstractIntervalXYDataset;
import org.jfree.data.xy.IntervalXYDataset;
import org.jfree.data.xy.XYDataset;
import org.jfree.data.xy.XYDomainInfo;
import org.jfree.data.xy.XYRangeInfo;
import org.jfree.util.ObjectUtilities;

public class TimeSeriesCollection extends AbstractIntervalXYDataset implements XYDataset, IntervalXYDataset, DomainInfo, XYDomainInfo, XYRangeInfo, VetoableChangeListener, Serializable {
    private static final long serialVersionUID = 834149929022371137L;
    private List data;
    private boolean domainIsPointsInTime;
    private Calendar workingCalendar;
    private TimePeriodAnchor xPosition;

    public TimeSeriesCollection() {
        this(null, TimeZone.getDefault());
    }

    public TimeSeriesCollection(TimeZone zone) {
        this(null, zone);
    }

    public TimeSeriesCollection(TimeSeries series) {
        this(series, TimeZone.getDefault());
    }

    public TimeSeriesCollection(TimeSeries series, TimeZone zone) {
        if (zone == null) {
            zone = TimeZone.getDefault();
        }
        this.workingCalendar = Calendar.getInstance(zone);
        this.data = new ArrayList();
        if (series != null) {
            this.data.add(series);
            series.addChangeListener(this);
        }
        this.xPosition = TimePeriodAnchor.START;
        this.domainIsPointsInTime = true;
    }

    public boolean getDomainIsPointsInTime() {
        return this.domainIsPointsInTime;
    }

    public void setDomainIsPointsInTime(boolean flag) {
        this.domainIsPointsInTime = flag;
        notifyListeners(new DatasetChangeEvent(this, this));
    }

    public DomainOrder getDomainOrder() {
        return DomainOrder.ASCENDING;
    }

    public TimePeriodAnchor getXPosition() {
        return this.xPosition;
    }

    public void setXPosition(TimePeriodAnchor anchor) {
        ParamChecks.nullNotPermitted(anchor, "anchor");
        this.xPosition = anchor;
        notifyListeners(new DatasetChangeEvent(this, this));
    }

    public List getSeries() {
        return Collections.unmodifiableList(this.data);
    }

    public int getSeriesCount() {
        return this.data.size();
    }

    public int indexOf(TimeSeries series) {
        ParamChecks.nullNotPermitted(series, StandardXYURLGenerator.DEFAULT_SERIES_PARAMETER);
        return this.data.indexOf(series);
    }

    public TimeSeries getSeries(int series) {
        if (series >= 0 && series < getSeriesCount()) {
            return (TimeSeries) this.data.get(series);
        }
        throw new IllegalArgumentException("The 'series' argument is out of bounds (" + series + ").");
    }

    public TimeSeries getSeries(Comparable key) {
        TimeSeries result = null;
        for (TimeSeries series : this.data) {
            Comparable k = series.getKey();
            if (k != null && k.equals(key)) {
                result = series;
            }
        }
        return result;
    }

    public Comparable getSeriesKey(int series) {
        return getSeries(series).getKey();
    }

    public int getSeriesIndex(Comparable key) {
        ParamChecks.nullNotPermitted(key, "key");
        int seriesCount = getSeriesCount();
        for (int i = 0; i < seriesCount; i++) {
            if (key.equals(((TimeSeries) this.data.get(i)).getKey())) {
                return i;
            }
        }
        return -1;
    }

    public void addSeries(TimeSeries series) {
        ParamChecks.nullNotPermitted(series, StandardXYURLGenerator.DEFAULT_SERIES_PARAMETER);
        this.data.add(series);
        series.addChangeListener(this);
        series.addVetoableChangeListener(this);
        fireDatasetChanged();
    }

    public void removeSeries(TimeSeries series) {
        ParamChecks.nullNotPermitted(series, StandardXYURLGenerator.DEFAULT_SERIES_PARAMETER);
        this.data.remove(series);
        series.removeChangeListener(this);
        series.removeVetoableChangeListener(this);
        fireDatasetChanged();
    }

    public void removeSeries(int index) {
        TimeSeries series = getSeries(index);
        if (series != null) {
            removeSeries(series);
        }
    }

    public void removeAllSeries() {
        for (int i = 0; i < this.data.size(); i++) {
            TimeSeries series = (TimeSeries) this.data.get(i);
            series.removeChangeListener(this);
            series.removeVetoableChangeListener(this);
        }
        this.data.clear();
        fireDatasetChanged();
    }

    public int getItemCount(int series) {
        return getSeries(series).getItemCount();
    }

    public double getXValue(int series, int item) {
        return (double) getX(((TimeSeries) this.data.get(series)).getTimePeriod(item));
    }

    public Number getX(int series, int item) {
        return new Long(getX(((TimeSeries) this.data.get(series)).getTimePeriod(item)));
    }

    protected synchronized long getX(RegularTimePeriod period) {
        long result;
        result = 0;
        if (this.xPosition == TimePeriodAnchor.START) {
            result = period.getFirstMillisecond(this.workingCalendar);
        } else if (this.xPosition == TimePeriodAnchor.MIDDLE) {
            result = period.getMiddleMillisecond(this.workingCalendar);
        } else if (this.xPosition == TimePeriodAnchor.END) {
            result = period.getLastMillisecond(this.workingCalendar);
        }
        return result;
    }

    public synchronized Number getStartX(int series, int item) {
        return new Long(((TimeSeries) this.data.get(series)).getTimePeriod(item).getFirstMillisecond(this.workingCalendar));
    }

    public synchronized Number getEndX(int series, int item) {
        return new Long(((TimeSeries) this.data.get(series)).getTimePeriod(item).getLastMillisecond(this.workingCalendar));
    }

    public Number getY(int series, int item) {
        return ((TimeSeries) this.data.get(series)).getValue(item);
    }

    public Number getStartY(int series, int item) {
        return getY(series, item);
    }

    public Number getEndY(int series, int item) {
        return getY(series, item);
    }

    public int[] getSurroundingItems(int series, long milliseconds) {
        int[] result = new int[]{-1, -1};
        TimeSeries timeSeries = getSeries(series);
        for (int i = 0; i < timeSeries.getItemCount(); i++) {
            long m = getX(series, i).longValue();
            if (m <= milliseconds) {
                result[0] = i;
            }
            if (m >= milliseconds) {
                result[1] = i;
                break;
            }
        }
        return result;
    }

    public double getDomainLowerBound(boolean includeInterval) {
        Range r = getDomainBounds(includeInterval);
        if (r != null) {
            return r.getLowerBound();
        }
        return Double.NaN;
    }

    public double getDomainUpperBound(boolean includeInterval) {
        Range r = getDomainBounds(includeInterval);
        if (r != null) {
            return r.getUpperBound();
        }
        return Double.NaN;
    }

    public Range getDomainBounds(boolean includeInterval) {
        Range result = null;
        for (TimeSeries series : this.data) {
            int count = series.getItemCount();
            if (count > 0) {
                Range temp;
                RegularTimePeriod start = series.getTimePeriod(0);
                RegularTimePeriod end = series.getTimePeriod(count - 1);
                if (includeInterval) {
                    temp = new Range((double) start.getFirstMillisecond(this.workingCalendar), (double) end.getLastMillisecond(this.workingCalendar));
                } else {
                    temp = new Range((double) getX(start), (double) getX(end));
                }
                result = Range.combine(result, temp);
            }
        }
        return result;
    }

    public Range getDomainBounds(List visibleSeriesKeys, boolean includeInterval) {
        Range result = null;
        for (Comparable seriesKey : visibleSeriesKeys) {
            TimeSeries series = getSeries(seriesKey);
            int count = series.getItemCount();
            if (count > 0) {
                Range temp;
                RegularTimePeriod start = series.getTimePeriod(0);
                RegularTimePeriod end = series.getTimePeriod(count - 1);
                if (includeInterval) {
                    temp = new Range((double) start.getFirstMillisecond(this.workingCalendar), (double) end.getLastMillisecond(this.workingCalendar));
                } else {
                    temp = new Range((double) getX(start), (double) getX(end));
                }
                result = Range.combine(result, temp);
            }
        }
        return result;
    }

    public Range getRangeBounds(boolean includeInterval) {
        Range result = null;
        for (TimeSeries series : this.data) {
            result = Range.combineIgnoringNaN(result, new Range(series.getMinY(), series.getMaxY()));
        }
        return result;
    }

    public Range getRangeBounds(List visibleSeriesKeys, Range xRange, boolean includeInterval) {
        Range result = null;
        for (Comparable seriesKey : visibleSeriesKeys) {
            result = Range.combineIgnoringNaN(result, getSeries(seriesKey).findValueRange(xRange, this.xPosition, this.workingCalendar.getTimeZone()));
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

    public boolean equals(Object obj) {
        if (obj == this) {
            return true;
        }
        if (!(obj instanceof TimeSeriesCollection)) {
            return false;
        }
        TimeSeriesCollection that = (TimeSeriesCollection) obj;
        if (this.xPosition != that.xPosition) {
            return false;
        }
        if (this.domainIsPointsInTime != that.domainIsPointsInTime) {
            return false;
        }
        if (ObjectUtilities.equal(this.data, that.data)) {
            return true;
        }
        return false;
    }

    public int hashCode() {
        int hashCode;
        int i = 0;
        int hashCode2 = this.data.hashCode() * 29;
        if (this.workingCalendar != null) {
            hashCode = this.workingCalendar.hashCode();
        } else {
            hashCode = 0;
        }
        hashCode2 = (hashCode2 + hashCode) * 29;
        if (this.xPosition != null) {
            hashCode = this.xPosition.hashCode();
        } else {
            hashCode = 0;
        }
        hashCode = (hashCode2 + hashCode) * 29;
        if (this.domainIsPointsInTime) {
            i = 1;
        }
        return hashCode + i;
    }

    public Object clone() throws CloneNotSupportedException {
        TimeSeriesCollection clone = (TimeSeriesCollection) super.clone();
        clone.data = (List) ObjectUtilities.deepClone(this.data);
        clone.workingCalendar = (Calendar) this.workingCalendar.clone();
        return clone;
    }
}
