package org.jfree.data.time;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import org.jfree.chart.urls.StandardXYURLGenerator;
import org.jfree.chart.util.ParamChecks;
import org.jfree.data.DomainInfo;
import org.jfree.data.Range;
import org.jfree.data.xy.AbstractIntervalXYDataset;
import org.jfree.data.xy.IntervalXYDataset;
import org.jfree.util.ObjectUtilities;

public class TimePeriodValuesCollection extends AbstractIntervalXYDataset implements IntervalXYDataset, DomainInfo, Serializable {
    private static final long serialVersionUID = -3077934065236454199L;
    private List data;
    private boolean domainIsPointsInTime;
    private TimePeriodAnchor xPosition;

    public TimePeriodValuesCollection() {
        this((TimePeriodValues) null);
    }

    public TimePeriodValuesCollection(TimePeriodValues series) {
        this.data = new ArrayList();
        this.xPosition = TimePeriodAnchor.MIDDLE;
        this.domainIsPointsInTime = false;
        if (series != null) {
            this.data.add(series);
            series.addChangeListener(this);
        }
    }

    public TimePeriodAnchor getXPosition() {
        return this.xPosition;
    }

    public void setXPosition(TimePeriodAnchor position) {
        ParamChecks.nullNotPermitted(position, "position");
        this.xPosition = position;
    }

    public int getSeriesCount() {
        return this.data.size();
    }

    public TimePeriodValues getSeries(int series) {
        if (series >= 0 && series < getSeriesCount()) {
            return (TimePeriodValues) this.data.get(series);
        }
        throw new IllegalArgumentException("Index 'series' out of range.");
    }

    public Comparable getSeriesKey(int series) {
        return getSeries(series).getKey();
    }

    public void addSeries(TimePeriodValues series) {
        ParamChecks.nullNotPermitted(series, StandardXYURLGenerator.DEFAULT_SERIES_PARAMETER);
        this.data.add(series);
        series.addChangeListener(this);
        fireDatasetChanged();
    }

    public void removeSeries(TimePeriodValues series) {
        ParamChecks.nullNotPermitted(series, StandardXYURLGenerator.DEFAULT_SERIES_PARAMETER);
        this.data.remove(series);
        series.removeChangeListener(this);
        fireDatasetChanged();
    }

    public void removeSeries(int index) {
        TimePeriodValues series = getSeries(index);
        if (series != null) {
            removeSeries(series);
        }
    }

    public int getItemCount(int series) {
        return getSeries(series).getItemCount();
    }

    public Number getX(int series, int item) {
        return new Long(getX(((TimePeriodValues) this.data.get(series)).getDataItem(item).getPeriod()));
    }

    private long getX(TimePeriod period) {
        if (this.xPosition == TimePeriodAnchor.START) {
            return period.getStart().getTime();
        }
        if (this.xPosition == TimePeriodAnchor.MIDDLE) {
            return (period.getStart().getTime() / 2) + (period.getEnd().getTime() / 2);
        }
        if (this.xPosition == TimePeriodAnchor.END) {
            return period.getEnd().getTime();
        }
        throw new IllegalStateException("TimePeriodAnchor unknown.");
    }

    public Number getStartX(int series, int item) {
        return new Long(((TimePeriodValues) this.data.get(series)).getDataItem(item).getPeriod().getStart().getTime());
    }

    public Number getEndX(int series, int item) {
        return new Long(((TimePeriodValues) this.data.get(series)).getDataItem(item).getPeriod().getEnd().getTime());
    }

    public Number getY(int series, int item) {
        return ((TimePeriodValues) this.data.get(series)).getDataItem(item).getValue();
    }

    public Number getStartY(int series, int item) {
        return getY(series, item);
    }

    public Number getEndY(int series, int item) {
        return getY(series, item);
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
        boolean interval = includeInterval || this.domainIsPointsInTime;
        Range result = null;
        Range temp = null;
        for (TimePeriodValues series : this.data) {
            if (series.getItemCount() > 0) {
                TimePeriod start = series.getTimePeriod(series.getMinStartIndex());
                TimePeriod end = series.getTimePeriod(series.getMaxEndIndex());
                if (interval) {
                    temp = new Range((double) start.getStart().getTime(), (double) end.getEnd().getTime());
                } else {
                    if (this.xPosition == TimePeriodAnchor.START) {
                        temp = new Range((double) start.getStart().getTime(), (double) series.getTimePeriod(series.getMaxStartIndex()).getStart().getTime());
                    } else {
                        if (this.xPosition == TimePeriodAnchor.MIDDLE) {
                            TimePeriod minMiddle = series.getTimePeriod(series.getMinMiddleIndex());
                            long s1 = minMiddle.getStart().getTime();
                            long e1 = minMiddle.getEnd().getTime();
                            TimePeriod maxMiddle = series.getTimePeriod(series.getMaxMiddleIndex());
                            long s2 = maxMiddle.getStart().getTime();
                            temp = new Range((double) (((e1 - s1) / 2) + s1), (double) (((maxMiddle.getEnd().getTime() - s2) / 2) + s2));
                        } else {
                            if (this.xPosition == TimePeriodAnchor.END) {
                                temp = new Range((double) series.getTimePeriod(series.getMinEndIndex()).getEnd().getTime(), (double) end.getEnd().getTime());
                            }
                        }
                    }
                }
                result = Range.combine(result, temp);
            }
        }
        return result;
    }

    public boolean equals(Object obj) {
        if (obj == this) {
            return true;
        }
        if (!(obj instanceof TimePeriodValuesCollection)) {
            return false;
        }
        TimePeriodValuesCollection that = (TimePeriodValuesCollection) obj;
        if (this.domainIsPointsInTime != that.domainIsPointsInTime) {
            return false;
        }
        if (this.xPosition != that.xPosition) {
            return false;
        }
        if (ObjectUtilities.equal(this.data, that.data)) {
            return true;
        }
        return false;
    }

    public boolean getDomainIsPointsInTime() {
        return this.domainIsPointsInTime;
    }

    public void setDomainIsPointsInTime(boolean flag) {
        this.domainIsPointsInTime = flag;
    }
}
