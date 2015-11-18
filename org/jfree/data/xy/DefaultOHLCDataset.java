package org.jfree.data.xy;

import java.util.Arrays;
import java.util.Date;
import org.jfree.util.PublicCloneable;

public class DefaultOHLCDataset extends AbstractXYDataset implements OHLCDataset, PublicCloneable {
    private OHLCDataItem[] data;
    private Comparable key;

    public DefaultOHLCDataset(Comparable key, OHLCDataItem[] data) {
        this.key = key;
        this.data = data;
    }

    public Comparable getSeriesKey(int series) {
        return this.key;
    }

    public Number getX(int series, int item) {
        return new Long(this.data[item].getDate().getTime());
    }

    public Date getXDate(int series, int item) {
        return this.data[item].getDate();
    }

    public Number getY(int series, int item) {
        return getClose(series, item);
    }

    public Number getHigh(int series, int item) {
        return this.data[item].getHigh();
    }

    public double getHighValue(int series, int item) {
        Number high = getHigh(series, item);
        if (high != null) {
            return high.doubleValue();
        }
        return Double.NaN;
    }

    public Number getLow(int series, int item) {
        return this.data[item].getLow();
    }

    public double getLowValue(int series, int item) {
        Number low = getLow(series, item);
        if (low != null) {
            return low.doubleValue();
        }
        return Double.NaN;
    }

    public Number getOpen(int series, int item) {
        return this.data[item].getOpen();
    }

    public double getOpenValue(int series, int item) {
        Number open = getOpen(series, item);
        if (open != null) {
            return open.doubleValue();
        }
        return Double.NaN;
    }

    public Number getClose(int series, int item) {
        return this.data[item].getClose();
    }

    public double getCloseValue(int series, int item) {
        Number close = getClose(series, item);
        if (close != null) {
            return close.doubleValue();
        }
        return Double.NaN;
    }

    public Number getVolume(int series, int item) {
        return this.data[item].getVolume();
    }

    public double getVolumeValue(int series, int item) {
        Number volume = getVolume(series, item);
        if (volume != null) {
            return volume.doubleValue();
        }
        return Double.NaN;
    }

    public int getSeriesCount() {
        return 1;
    }

    public int getItemCount(int series) {
        return this.data.length;
    }

    public void sortDataByDate() {
        Arrays.sort(this.data);
    }

    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (!(obj instanceof DefaultOHLCDataset)) {
            return false;
        }
        DefaultOHLCDataset that = (DefaultOHLCDataset) obj;
        if (!this.key.equals(that.key)) {
            return false;
        }
        if (Arrays.equals(this.data, that.data)) {
            return true;
        }
        return false;
    }

    public Object clone() throws CloneNotSupportedException {
        DefaultOHLCDataset clone = (DefaultOHLCDataset) super.clone();
        clone.data = new OHLCDataItem[this.data.length];
        System.arraycopy(this.data, 0, clone.data, 0, this.data.length);
        return clone;
    }
}
