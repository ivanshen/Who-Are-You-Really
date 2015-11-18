package org.jfree.data.time.ohlc;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import org.jfree.chart.HashUtilities;
import org.jfree.chart.urls.StandardXYURLGenerator;
import org.jfree.chart.util.ParamChecks;
import org.jfree.data.general.DatasetChangeEvent;
import org.jfree.data.time.RegularTimePeriod;
import org.jfree.data.time.TimePeriodAnchor;
import org.jfree.data.xy.AbstractXYDataset;
import org.jfree.data.xy.OHLCDataset;
import org.jfree.util.ObjectUtilities;

public class OHLCSeriesCollection extends AbstractXYDataset implements OHLCDataset, Serializable {
    private List data;
    private TimePeriodAnchor xPosition;

    public OHLCSeriesCollection() {
        this.xPosition = TimePeriodAnchor.MIDDLE;
        this.data = new ArrayList();
    }

    public TimePeriodAnchor getXPosition() {
        return this.xPosition;
    }

    public void setXPosition(TimePeriodAnchor anchor) {
        ParamChecks.nullNotPermitted(anchor, "anchor");
        this.xPosition = anchor;
        notifyListeners(new DatasetChangeEvent(this, this));
    }

    public void addSeries(OHLCSeries series) {
        ParamChecks.nullNotPermitted(series, StandardXYURLGenerator.DEFAULT_SERIES_PARAMETER);
        this.data.add(series);
        series.addChangeListener(this);
        fireDatasetChanged();
    }

    public int getSeriesCount() {
        return this.data.size();
    }

    public OHLCSeries getSeries(int series) {
        if (series >= 0 && series < getSeriesCount()) {
            return (OHLCSeries) this.data.get(series);
        }
        throw new IllegalArgumentException("Series index out of bounds");
    }

    public Comparable getSeriesKey(int series) {
        return getSeries(series).getKey();
    }

    public int getItemCount(int series) {
        return getSeries(series).getItemCount();
    }

    protected synchronized long getX(RegularTimePeriod period) {
        long result;
        result = 0;
        if (this.xPosition == TimePeriodAnchor.START) {
            result = period.getFirstMillisecond();
        } else if (this.xPosition == TimePeriodAnchor.MIDDLE) {
            result = period.getMiddleMillisecond();
        } else if (this.xPosition == TimePeriodAnchor.END) {
            result = period.getLastMillisecond();
        }
        return result;
    }

    public double getXValue(int series, int item) {
        return (double) getX(((OHLCItem) ((OHLCSeries) this.data.get(series)).getDataItem(item)).getPeriod());
    }

    public Number getX(int series, int item) {
        return new Double(getXValue(series, item));
    }

    public Number getY(int series, int item) {
        return new Double(((OHLCItem) ((OHLCSeries) this.data.get(series)).getDataItem(item)).getYValue());
    }

    public double getOpenValue(int series, int item) {
        return ((OHLCItem) ((OHLCSeries) this.data.get(series)).getDataItem(item)).getOpenValue();
    }

    public Number getOpen(int series, int item) {
        return new Double(getOpenValue(series, item));
    }

    public double getCloseValue(int series, int item) {
        return ((OHLCItem) ((OHLCSeries) this.data.get(series)).getDataItem(item)).getCloseValue();
    }

    public Number getClose(int series, int item) {
        return new Double(getCloseValue(series, item));
    }

    public double getHighValue(int series, int item) {
        return ((OHLCItem) ((OHLCSeries) this.data.get(series)).getDataItem(item)).getHighValue();
    }

    public Number getHigh(int series, int item) {
        return new Double(getHighValue(series, item));
    }

    public double getLowValue(int series, int item) {
        return ((OHLCItem) ((OHLCSeries) this.data.get(series)).getDataItem(item)).getLowValue();
    }

    public Number getLow(int series, int item) {
        return new Double(getLowValue(series, item));
    }

    public Number getVolume(int series, int item) {
        return null;
    }

    public double getVolumeValue(int series, int item) {
        return Double.NaN;
    }

    public void removeSeries(int index) {
        OHLCSeries series = getSeries(index);
        if (series != null) {
            removeSeries(series);
        }
    }

    public boolean removeSeries(OHLCSeries series) {
        ParamChecks.nullNotPermitted(series, StandardXYURLGenerator.DEFAULT_SERIES_PARAMETER);
        boolean removed = this.data.remove(series);
        if (removed) {
            series.removeChangeListener(this);
            fireDatasetChanged();
        }
        return removed;
    }

    public void removeAllSeries() {
        if (!this.data.isEmpty()) {
            for (int i = 0; i < this.data.size(); i++) {
                ((OHLCSeries) this.data.get(i)).removeChangeListener(this);
            }
            this.data.clear();
            fireDatasetChanged();
        }
    }

    public boolean equals(Object obj) {
        if (obj == this) {
            return true;
        }
        if (!(obj instanceof OHLCSeriesCollection)) {
            return false;
        }
        OHLCSeriesCollection that = (OHLCSeriesCollection) obj;
        if (this.xPosition.equals(that.xPosition)) {
            return ObjectUtilities.equal(this.data, that.data);
        }
        return false;
    }

    public int hashCode() {
        int result = HashUtilities.hashCode(137, this.xPosition);
        for (int i = 0; i < this.data.size(); i++) {
            result = HashUtilities.hashCode(result, this.data.get(i));
        }
        return result;
    }

    public Object clone() throws CloneNotSupportedException {
        OHLCSeriesCollection clone = (OHLCSeriesCollection) super.clone();
        clone.data = (List) ObjectUtilities.deepClone(this.data);
        return clone;
    }
}
