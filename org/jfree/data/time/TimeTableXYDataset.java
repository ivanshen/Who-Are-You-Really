package org.jfree.data.time;

import java.util.Calendar;
import java.util.List;
import java.util.Locale;
import java.util.TimeZone;
import org.jfree.chart.util.ParamChecks;
import org.jfree.data.DefaultKeyedValues2D;
import org.jfree.data.DomainInfo;
import org.jfree.data.Range;
import org.jfree.data.general.DatasetChangeEvent;
import org.jfree.data.xy.AbstractIntervalXYDataset;
import org.jfree.data.xy.IntervalXYDataset;
import org.jfree.data.xy.TableXYDataset;
import org.jfree.util.PublicCloneable;

public class TimeTableXYDataset extends AbstractIntervalXYDataset implements Cloneable, PublicCloneable, IntervalXYDataset, DomainInfo, TableXYDataset {
    private boolean domainIsPointsInTime;
    private DefaultKeyedValues2D values;
    private Calendar workingCalendar;
    private TimePeriodAnchor xPosition;

    public TimeTableXYDataset() {
        this(TimeZone.getDefault(), Locale.getDefault());
    }

    public TimeTableXYDataset(TimeZone zone) {
        this(zone, Locale.getDefault());
    }

    public TimeTableXYDataset(TimeZone zone, Locale locale) {
        ParamChecks.nullNotPermitted(zone, "zone");
        ParamChecks.nullNotPermitted(locale, "locale");
        this.values = new DefaultKeyedValues2D(true);
        this.workingCalendar = Calendar.getInstance(zone, locale);
        this.xPosition = TimePeriodAnchor.START;
    }

    public boolean getDomainIsPointsInTime() {
        return this.domainIsPointsInTime;
    }

    public void setDomainIsPointsInTime(boolean flag) {
        this.domainIsPointsInTime = flag;
        notifyListeners(new DatasetChangeEvent(this, this));
    }

    public TimePeriodAnchor getXPosition() {
        return this.xPosition;
    }

    public void setXPosition(TimePeriodAnchor anchor) {
        ParamChecks.nullNotPermitted(anchor, "anchor");
        this.xPosition = anchor;
        notifyListeners(new DatasetChangeEvent(this, this));
    }

    public void add(TimePeriod period, double y, Comparable seriesName) {
        add(period, new Double(y), seriesName, true);
    }

    public void add(TimePeriod period, Number y, Comparable seriesName, boolean notify) {
        if (period instanceof RegularTimePeriod) {
            ((RegularTimePeriod) period).peg(this.workingCalendar);
        }
        this.values.addValue(y, period, seriesName);
        if (notify) {
            fireDatasetChanged();
        }
    }

    public void remove(TimePeriod period, Comparable seriesName) {
        remove(period, seriesName, true);
    }

    public void remove(TimePeriod period, Comparable seriesName, boolean notify) {
        this.values.removeValue(period, seriesName);
        if (notify) {
            fireDatasetChanged();
        }
    }

    public void clear() {
        if (this.values.getRowCount() > 0) {
            this.values.clear();
            fireDatasetChanged();
        }
    }

    public TimePeriod getTimePeriod(int item) {
        return (TimePeriod) this.values.getRowKey(item);
    }

    public int getItemCount() {
        return this.values.getRowCount();
    }

    public int getItemCount(int series) {
        return getItemCount();
    }

    public int getSeriesCount() {
        return this.values.getColumnCount();
    }

    public Comparable getSeriesKey(int series) {
        return this.values.getColumnKey(series);
    }

    public Number getX(int series, int item) {
        return new Double(getXValue(series, item));
    }

    public double getXValue(int series, int item) {
        return (double) getXValue((TimePeriod) this.values.getRowKey(item));
    }

    public Number getStartX(int series, int item) {
        return new Double(getStartXValue(series, item));
    }

    public double getStartXValue(int series, int item) {
        return (double) ((TimePeriod) this.values.getRowKey(item)).getStart().getTime();
    }

    public Number getEndX(int series, int item) {
        return new Double(getEndXValue(series, item));
    }

    public double getEndXValue(int series, int item) {
        return (double) ((TimePeriod) this.values.getRowKey(item)).getEnd().getTime();
    }

    public Number getY(int series, int item) {
        return this.values.getValue(item, series);
    }

    public Number getStartY(int series, int item) {
        return getY(series, item);
    }

    public Number getEndY(int series, int item) {
        return getY(series, item);
    }

    private long getXValue(TimePeriod period) {
        if (this.xPosition == TimePeriodAnchor.START) {
            return period.getStart().getTime();
        }
        if (this.xPosition == TimePeriodAnchor.MIDDLE) {
            long t0 = period.getStart().getTime();
            return t0 + ((period.getEnd().getTime() - t0) / 2);
        } else if (this.xPosition == TimePeriodAnchor.END) {
            return period.getEnd().getTime();
        } else {
            return 0;
        }
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
        List keys = this.values.getRowKeys();
        if (keys.isEmpty()) {
            return null;
        }
        TimePeriod first = (TimePeriod) keys.get(0);
        TimePeriod last = (TimePeriod) keys.get(keys.size() - 1);
        if (!includeInterval || this.domainIsPointsInTime) {
            return new Range((double) getXValue(first), (double) getXValue(last));
        }
        return new Range((double) first.getStart().getTime(), (double) last.getEnd().getTime());
    }

    public boolean equals(Object obj) {
        if (obj == this) {
            return true;
        }
        if (!(obj instanceof TimeTableXYDataset)) {
            return false;
        }
        TimeTableXYDataset that = (TimeTableXYDataset) obj;
        if (this.domainIsPointsInTime != that.domainIsPointsInTime) {
            return false;
        }
        if (this.xPosition != that.xPosition) {
            return false;
        }
        if (!this.workingCalendar.getTimeZone().equals(that.workingCalendar.getTimeZone())) {
            return false;
        }
        if (this.values.equals(that.values)) {
            return true;
        }
        return false;
    }

    public Object clone() throws CloneNotSupportedException {
        TimeTableXYDataset clone = (TimeTableXYDataset) super.clone();
        clone.values = (DefaultKeyedValues2D) this.values.clone();
        clone.workingCalendar = (Calendar) this.workingCalendar.clone();
        return clone;
    }
}
