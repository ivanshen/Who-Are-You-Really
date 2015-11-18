package org.jfree.data.time;

import java.util.Calendar;
import java.util.TimeZone;
import org.jfree.data.DomainInfo;
import org.jfree.data.Range;
import org.jfree.data.RangeInfo;
import org.jfree.data.general.SeriesChangeEvent;
import org.jfree.data.xy.AbstractIntervalXYDataset;
import org.jfree.data.xy.IntervalXYDataset;
import org.jfree.util.LogTarget;

public class DynamicTimeSeriesCollection extends AbstractIntervalXYDataset implements IntervalXYDataset, DomainInfo, RangeInfo {
    public static final int END = 2;
    public static final int MIDDLE = 1;
    public static final int START = 0;
    private long deltaTime;
    private Long domainEnd;
    private boolean domainIsPointsInTime;
    private Range domainRange;
    private Long domainStart;
    protected int historyCount;
    private Float maxValue;
    private int maximumItemCount;
    private Float minValue;
    private int newestAt;
    private int oldestAt;
    protected RegularTimePeriod[] pointsInTime;
    private int position;
    private int seriesCount;
    private Comparable[] seriesKeys;
    private Class timePeriodClass;
    protected ValueSequence[] valueHistory;
    private Range valueRange;
    protected Calendar workingCalendar;

    protected class ValueSequence {
        float[] dataPoints;

        public ValueSequence(DynamicTimeSeriesCollection this$0) {
            this(this$0.maximumItemCount);
        }

        public ValueSequence(int length) {
            this.dataPoints = new float[length];
            for (int i = 0; i < length; i += DynamicTimeSeriesCollection.MIDDLE) {
                this.dataPoints[i] = 0.0f;
            }
        }

        public void enterData(int index, float value) {
            this.dataPoints[index] = value;
        }

        public float getData(int index) {
            return this.dataPoints[index];
        }
    }

    public DynamicTimeSeriesCollection(int nSeries, int nMoments) {
        this(nSeries, nMoments, new Millisecond(), TimeZone.getDefault());
        this.newestAt = nMoments - 1;
    }

    public DynamicTimeSeriesCollection(int nSeries, int nMoments, TimeZone zone) {
        this(nSeries, nMoments, new Millisecond(), zone);
        this.newestAt = nMoments - 1;
    }

    public DynamicTimeSeriesCollection(int nSeries, int nMoments, RegularTimePeriod timeSample) {
        this(nSeries, nMoments, timeSample, TimeZone.getDefault());
    }

    public DynamicTimeSeriesCollection(int nSeries, int nMoments, RegularTimePeriod timeSample, TimeZone zone) {
        this.maximumItemCount = 2000;
        this.timePeriodClass = Minute.class;
        this.minValue = new Float(0.0f);
        this.maxValue = null;
        this.maximumItemCount = nMoments;
        this.historyCount = nMoments;
        this.seriesKeys = new Comparable[nSeries];
        for (int i = 0; i < nSeries; i += MIDDLE) {
            this.seriesKeys[i] = "";
        }
        this.newestAt = nMoments - 1;
        this.valueHistory = new ValueSequence[nSeries];
        this.timePeriodClass = timeSample.getClass();
        if (this.timePeriodClass == Millisecond.class) {
            this.pointsInTime = new Millisecond[nMoments];
        } else if (this.timePeriodClass == Second.class) {
            this.pointsInTime = new Second[nMoments];
        } else if (this.timePeriodClass == Minute.class) {
            this.pointsInTime = new Minute[nMoments];
        } else if (this.timePeriodClass == Hour.class) {
            this.pointsInTime = new Hour[nMoments];
        }
        this.workingCalendar = Calendar.getInstance(zone);
        this.position = 0;
        this.domainIsPointsInTime = true;
    }

    public synchronized long setTimeBase(RegularTimePeriod start) {
        if (this.pointsInTime[0] == null) {
            this.pointsInTime[0] = start;
            for (int i = MIDDLE; i < this.historyCount; i += MIDDLE) {
                this.pointsInTime[i] = this.pointsInTime[i - 1].next();
            }
        }
        this.deltaTime = this.pointsInTime[MIDDLE].getFirstMillisecond(this.workingCalendar) - this.pointsInTime[0].getFirstMillisecond(this.workingCalendar);
        this.oldestAt = 0;
        this.newestAt = this.historyCount - 1;
        findDomainLimits();
        return this.deltaTime;
    }

    protected void findDomainLimits() {
        long endL;
        long startL = getOldestTime().getFirstMillisecond(this.workingCalendar);
        if (this.domainIsPointsInTime) {
            endL = getNewestTime().getFirstMillisecond(this.workingCalendar);
        } else {
            endL = getNewestTime().getLastMillisecond(this.workingCalendar);
        }
        this.domainStart = new Long(startL);
        this.domainEnd = new Long(endL);
        this.domainRange = new Range((double) startL, (double) endL);
    }

    public int getPosition() {
        return this.position;
    }

    public void setPosition(int position) {
        this.position = position;
    }

    public void addSeries(float[] values, int seriesNumber, Comparable seriesKey) {
        invalidateRangeInfo();
        if (values == null) {
            throw new IllegalArgumentException("TimeSeriesDataset.addSeries(): cannot add null array of values.");
        } else if (seriesNumber >= this.valueHistory.length) {
            throw new IllegalArgumentException("TimeSeriesDataset.addSeries(): cannot add more series than specified in c'tor");
        } else {
            int i;
            if (this.valueHistory[seriesNumber] == null) {
                this.valueHistory[seriesNumber] = new ValueSequence(this.historyCount);
                this.seriesCount += MIDDLE;
            }
            int srcLength = values.length;
            int copyLength = this.historyCount;
            boolean fillNeeded = false;
            if (srcLength < this.historyCount) {
                fillNeeded = true;
                copyLength = srcLength;
            }
            for (i = 0; i < copyLength; i += MIDDLE) {
                this.valueHistory[seriesNumber].enterData(i, values[i]);
            }
            if (fillNeeded) {
                for (i = copyLength; i < this.historyCount; i += MIDDLE) {
                    this.valueHistory[seriesNumber].enterData(i, 0.0f);
                }
            }
            if (seriesKey != null) {
                this.seriesKeys[seriesNumber] = seriesKey;
            }
            fireSeriesChanged();
        }
    }

    public void setSeriesKey(int seriesNumber, Comparable key) {
        this.seriesKeys[seriesNumber] = key;
    }

    public void addValue(int seriesNumber, int index, float value) {
        invalidateRangeInfo();
        if (seriesNumber >= this.valueHistory.length) {
            throw new IllegalArgumentException("TimeSeriesDataset.addValue(): series #" + seriesNumber + "unspecified in c'tor");
        }
        if (this.valueHistory[seriesNumber] == null) {
            this.valueHistory[seriesNumber] = new ValueSequence(this.historyCount);
            this.seriesCount += MIDDLE;
        }
        this.valueHistory[seriesNumber].enterData(index, value);
        fireSeriesChanged();
    }

    public int getSeriesCount() {
        return this.seriesCount;
    }

    public int getItemCount(int series) {
        return this.historyCount;
    }

    protected int translateGet(int toFetch) {
        if (this.oldestAt == 0) {
            return toFetch;
        }
        int newIndex = toFetch + this.oldestAt;
        if (newIndex >= this.historyCount) {
            newIndex -= this.historyCount;
        }
        return newIndex;
    }

    public int offsetFromNewest(int delta) {
        return wrapOffset(this.newestAt + delta);
    }

    public int offsetFromOldest(int delta) {
        return wrapOffset(this.oldestAt + delta);
    }

    protected int wrapOffset(int protoIndex) {
        int tmp = protoIndex;
        if (tmp >= this.historyCount) {
            return tmp - this.historyCount;
        }
        if (tmp < 0) {
            return tmp + this.historyCount;
        }
        return tmp;
    }

    public synchronized RegularTimePeriod advanceTime() {
        RegularTimePeriod nextInstant;
        int s;
        nextInstant = this.pointsInTime[this.newestAt].next();
        this.newestAt = this.oldestAt;
        boolean extremaChanged = false;
        float oldMax = 0.0f;
        if (this.maxValue != null) {
            oldMax = this.maxValue.floatValue();
        }
        for (s = 0; s < getSeriesCount(); s += MIDDLE) {
            if (this.valueHistory[s].getData(this.oldestAt) == oldMax) {
                extremaChanged = true;
            }
            if (extremaChanged) {
                break;
            }
        }
        if (extremaChanged) {
            invalidateRangeInfo();
        }
        for (s = 0; s < getSeriesCount(); s += MIDDLE) {
            this.valueHistory[s].enterData(this.newestAt, 0.0f);
        }
        this.pointsInTime[this.newestAt] = nextInstant;
        this.oldestAt += MIDDLE;
        if (this.oldestAt >= this.historyCount) {
            this.oldestAt = 0;
        }
        long startL = this.domainStart.longValue();
        this.domainStart = new Long(this.deltaTime + startL);
        long endL = this.domainEnd.longValue();
        this.domainEnd = new Long(this.deltaTime + endL);
        this.domainRange = new Range((double) startL, (double) endL);
        fireSeriesChanged();
        return nextInstant;
    }

    public void invalidateRangeInfo() {
        this.maxValue = null;
        this.valueRange = null;
    }

    protected double findMaxValue() {
        double max = 0.0d;
        for (int s = 0; s < getSeriesCount(); s += MIDDLE) {
            for (int i = 0; i < this.historyCount; i += MIDDLE) {
                double tmp = getYValue(s, i);
                if (tmp > max) {
                    max = tmp;
                }
            }
        }
        return max;
    }

    public int getOldestIndex() {
        return this.oldestAt;
    }

    public int getNewestIndex() {
        return this.newestAt;
    }

    public void appendData(float[] newData) {
        int nDataPoints = newData.length;
        if (nDataPoints > this.valueHistory.length) {
            throw new IllegalArgumentException("More data than series to put them in");
        }
        for (int s = 0; s < nDataPoints; s += MIDDLE) {
            if (this.valueHistory[s] == null) {
                this.valueHistory[s] = new ValueSequence(this.historyCount);
            }
            this.valueHistory[s].enterData(this.newestAt, newData[s]);
        }
        fireSeriesChanged();
    }

    public void appendData(float[] newData, int insertionIndex, int refresh) {
        int nDataPoints = newData.length;
        if (nDataPoints > this.valueHistory.length) {
            throw new IllegalArgumentException("More data than series to put them in");
        }
        for (int s = 0; s < nDataPoints; s += MIDDLE) {
            if (this.valueHistory[s] == null) {
                this.valueHistory[s] = new ValueSequence(this.historyCount);
            }
            this.valueHistory[s].enterData(insertionIndex, newData[s]);
        }
        if (refresh > 0 && (insertionIndex + MIDDLE) % refresh == 0) {
            fireSeriesChanged();
        }
    }

    public RegularTimePeriod getNewestTime() {
        return this.pointsInTime[this.newestAt];
    }

    public RegularTimePeriod getOldestTime() {
        return this.pointsInTime[this.oldestAt];
    }

    public Number getX(int series, int item) {
        return new Long(getX(this.pointsInTime[translateGet(item)]));
    }

    public double getYValue(int series, int item) {
        return (double) this.valueHistory[series].getData(translateGet(item));
    }

    public Number getY(int series, int item) {
        return new Float(getYValue(series, item));
    }

    public Number getStartX(int series, int item) {
        return new Long(this.pointsInTime[translateGet(item)].getFirstMillisecond(this.workingCalendar));
    }

    public Number getEndX(int series, int item) {
        return new Long(this.pointsInTime[translateGet(item)].getLastMillisecond(this.workingCalendar));
    }

    public Number getStartY(int series, int item) {
        return getY(series, item);
    }

    public Number getEndY(int series, int item) {
        return getY(series, item);
    }

    public Comparable getSeriesKey(int series) {
        return this.seriesKeys[series];
    }

    protected void fireSeriesChanged() {
        seriesChanged(new SeriesChangeEvent(this));
    }

    public double getDomainLowerBound(boolean includeInterval) {
        return this.domainStart.doubleValue();
    }

    public double getDomainUpperBound(boolean includeInterval) {
        return this.domainEnd.doubleValue();
    }

    public Range getDomainBounds(boolean includeInterval) {
        if (this.domainRange == null) {
            findDomainLimits();
        }
        return this.domainRange;
    }

    private long getX(RegularTimePeriod period) {
        switch (this.position) {
            case LogTarget.ERROR /*0*/:
                return period.getFirstMillisecond(this.workingCalendar);
            case MIDDLE /*1*/:
                return period.getMiddleMillisecond(this.workingCalendar);
            case END /*2*/:
                return period.getLastMillisecond(this.workingCalendar);
            default:
                return period.getMiddleMillisecond(this.workingCalendar);
        }
    }

    public double getRangeLowerBound(boolean includeInterval) {
        if (this.minValue != null) {
            return this.minValue.doubleValue();
        }
        return Double.NaN;
    }

    public double getRangeUpperBound(boolean includeInterval) {
        if (this.maxValue != null) {
            return this.maxValue.doubleValue();
        }
        return Double.NaN;
    }

    public Range getRangeBounds(boolean includeInterval) {
        if (this.valueRange == null) {
            this.valueRange = new Range(0.0d, getRangeUpperBound(includeInterval));
        }
        return this.valueRange;
    }
}
