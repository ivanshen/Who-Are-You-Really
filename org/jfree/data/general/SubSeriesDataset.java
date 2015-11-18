package org.jfree.data.general;

import org.jfree.data.xy.AbstractIntervalXYDataset;
import org.jfree.data.xy.IntervalXYDataset;
import org.jfree.data.xy.OHLCDataset;
import org.jfree.data.xy.XYDataset;

public class SubSeriesDataset extends AbstractIntervalXYDataset implements OHLCDataset, IntervalXYDataset, CombinationDataset {
    private int[] map;
    private SeriesDataset parent;

    public SubSeriesDataset(SeriesDataset parent, int[] map) {
        this.parent = null;
        this.parent = parent;
        this.map = map;
    }

    public SubSeriesDataset(SeriesDataset parent, int series) {
        this(parent, new int[]{series});
    }

    public Number getHigh(int series, int item) {
        return ((OHLCDataset) this.parent).getHigh(this.map[series], item);
    }

    public double getHighValue(int series, int item) {
        Number high = getHigh(series, item);
        if (high != null) {
            return high.doubleValue();
        }
        return Double.NaN;
    }

    public Number getLow(int series, int item) {
        return ((OHLCDataset) this.parent).getLow(this.map[series], item);
    }

    public double getLowValue(int series, int item) {
        Number low = getLow(series, item);
        if (low != null) {
            return low.doubleValue();
        }
        return Double.NaN;
    }

    public Number getOpen(int series, int item) {
        return ((OHLCDataset) this.parent).getOpen(this.map[series], item);
    }

    public double getOpenValue(int series, int item) {
        Number open = getOpen(series, item);
        if (open != null) {
            return open.doubleValue();
        }
        return Double.NaN;
    }

    public Number getClose(int series, int item) {
        return ((OHLCDataset) this.parent).getClose(this.map[series], item);
    }

    public double getCloseValue(int series, int item) {
        Number close = getClose(series, item);
        if (close != null) {
            return close.doubleValue();
        }
        return Double.NaN;
    }

    public Number getVolume(int series, int item) {
        return ((OHLCDataset) this.parent).getVolume(this.map[series], item);
    }

    public double getVolumeValue(int series, int item) {
        Number volume = getVolume(series, item);
        if (volume != null) {
            return volume.doubleValue();
        }
        return Double.NaN;
    }

    public Number getX(int series, int item) {
        return ((XYDataset) this.parent).getX(this.map[series], item);
    }

    public Number getY(int series, int item) {
        return ((XYDataset) this.parent).getY(this.map[series], item);
    }

    public int getItemCount(int series) {
        return ((XYDataset) this.parent).getItemCount(this.map[series]);
    }

    public int getSeriesCount() {
        return this.map.length;
    }

    public Comparable getSeriesKey(int series) {
        return this.parent.getSeriesKey(this.map[series]);
    }

    public Number getStartX(int series, int item) {
        if (this.parent instanceof IntervalXYDataset) {
            return ((IntervalXYDataset) this.parent).getStartX(this.map[series], item);
        }
        return getX(series, item);
    }

    public Number getEndX(int series, int item) {
        if (this.parent instanceof IntervalXYDataset) {
            return ((IntervalXYDataset) this.parent).getEndX(this.map[series], item);
        }
        return getX(series, item);
    }

    public Number getStartY(int series, int item) {
        if (this.parent instanceof IntervalXYDataset) {
            return ((IntervalXYDataset) this.parent).getStartY(this.map[series], item);
        }
        return getY(series, item);
    }

    public Number getEndY(int series, int item) {
        if (this.parent instanceof IntervalXYDataset) {
            return ((IntervalXYDataset) this.parent).getEndY(this.map[series], item);
        }
        return getY(series, item);
    }

    public SeriesDataset getParent() {
        return this.parent;
    }

    public int[] getMap() {
        return (int[]) this.map.clone();
    }
}
