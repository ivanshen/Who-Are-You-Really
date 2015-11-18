package org.jfree.data.general;

import java.util.ArrayList;
import java.util.List;
import org.jfree.data.xy.AbstractIntervalXYDataset;
import org.jfree.data.xy.IntervalXYDataset;
import org.jfree.data.xy.OHLCDataset;
import org.jfree.data.xy.XYDataset;

public class CombinedDataset extends AbstractIntervalXYDataset implements XYDataset, OHLCDataset, IntervalXYDataset, CombinationDataset {
    private List datasetInfo;

    private class DatasetInfo {
        private SeriesDataset data;
        private int series;

        DatasetInfo(SeriesDataset data, int series) {
            this.data = data;
            this.series = series;
        }
    }

    public CombinedDataset() {
        this.datasetInfo = new ArrayList();
    }

    public CombinedDataset(SeriesDataset[] data) {
        this.datasetInfo = new ArrayList();
        add(data);
    }

    public void add(SeriesDataset data) {
        fastAdd(data);
        notifyListeners(new DatasetChangeEvent(this, this));
    }

    public void add(SeriesDataset[] data) {
        for (SeriesDataset fastAdd : data) {
            fastAdd(fastAdd);
        }
        notifyListeners(new DatasetChangeEvent(this, this));
    }

    public void add(SeriesDataset data, int series) {
        add(new SubSeriesDataset(data, series));
    }

    private void fastAdd(SeriesDataset data) {
        for (int i = 0; i < data.getSeriesCount(); i++) {
            this.datasetInfo.add(new DatasetInfo(data, i));
        }
    }

    public int getSeriesCount() {
        return this.datasetInfo.size();
    }

    public Comparable getSeriesKey(int series) {
        DatasetInfo di = getDatasetInfo(series);
        return di.data.getSeriesKey(di.series);
    }

    public Number getX(int series, int item) {
        DatasetInfo di = getDatasetInfo(series);
        return ((XYDataset) di.data).getX(di.series, item);
    }

    public Number getY(int series, int item) {
        DatasetInfo di = getDatasetInfo(series);
        return ((XYDataset) di.data).getY(di.series, item);
    }

    public int getItemCount(int series) {
        DatasetInfo di = getDatasetInfo(series);
        return ((XYDataset) di.data).getItemCount(di.series);
    }

    public Number getHigh(int series, int item) {
        DatasetInfo di = getDatasetInfo(series);
        return ((OHLCDataset) di.data).getHigh(di.series, item);
    }

    public double getHighValue(int series, int item) {
        Number high = getHigh(series, item);
        if (high != null) {
            return high.doubleValue();
        }
        return Double.NaN;
    }

    public Number getLow(int series, int item) {
        DatasetInfo di = getDatasetInfo(series);
        return ((OHLCDataset) di.data).getLow(di.series, item);
    }

    public double getLowValue(int series, int item) {
        Number low = getLow(series, item);
        if (low != null) {
            return low.doubleValue();
        }
        return Double.NaN;
    }

    public Number getOpen(int series, int item) {
        DatasetInfo di = getDatasetInfo(series);
        return ((OHLCDataset) di.data).getOpen(di.series, item);
    }

    public double getOpenValue(int series, int item) {
        Number open = getOpen(series, item);
        if (open != null) {
            return open.doubleValue();
        }
        return Double.NaN;
    }

    public Number getClose(int series, int item) {
        DatasetInfo di = getDatasetInfo(series);
        return ((OHLCDataset) di.data).getClose(di.series, item);
    }

    public double getCloseValue(int series, int item) {
        Number close = getClose(series, item);
        if (close != null) {
            return close.doubleValue();
        }
        return Double.NaN;
    }

    public Number getVolume(int series, int item) {
        DatasetInfo di = getDatasetInfo(series);
        return ((OHLCDataset) di.data).getVolume(di.series, item);
    }

    public double getVolumeValue(int series, int item) {
        Number volume = getVolume(series, item);
        if (volume != null) {
            return volume.doubleValue();
        }
        return Double.NaN;
    }

    public Number getStartX(int series, int item) {
        DatasetInfo di = getDatasetInfo(series);
        if (di.data instanceof IntervalXYDataset) {
            return ((IntervalXYDataset) di.data).getStartX(di.series, item);
        }
        return getX(series, item);
    }

    public Number getEndX(int series, int item) {
        DatasetInfo di = getDatasetInfo(series);
        if (di.data instanceof IntervalXYDataset) {
            return ((IntervalXYDataset) di.data).getEndX(di.series, item);
        }
        return getX(series, item);
    }

    public Number getStartY(int series, int item) {
        DatasetInfo di = getDatasetInfo(series);
        if (di.data instanceof IntervalXYDataset) {
            return ((IntervalXYDataset) di.data).getStartY(di.series, item);
        }
        return getY(series, item);
    }

    public Number getEndY(int series, int item) {
        DatasetInfo di = getDatasetInfo(series);
        if (di.data instanceof IntervalXYDataset) {
            return ((IntervalXYDataset) di.data).getEndY(di.series, item);
        }
        return getY(series, item);
    }

    public SeriesDataset getParent() {
        SeriesDataset parent = null;
        for (int i = 0; i < this.datasetInfo.size(); i++) {
            SeriesDataset child = getDatasetInfo(i).data;
            if (!(child instanceof CombinationDataset)) {
                return null;
            }
            SeriesDataset childParent = ((CombinationDataset) child).getParent();
            if (parent == null) {
                parent = childParent;
            } else if (parent != childParent) {
                return null;
            }
        }
        return parent;
    }

    public int[] getMap() {
        int[] map = null;
        for (int i = 0; i < this.datasetInfo.size(); i++) {
            SeriesDataset child = getDatasetInfo(i).data;
            if (!(child instanceof CombinationDataset)) {
                return null;
            }
            int[] childMap = ((CombinationDataset) child).getMap();
            if (childMap == null) {
                return null;
            }
            map = joinMap(map, childMap);
        }
        return map;
    }

    public int getChildPosition(Dataset child) {
        int n = 0;
        for (int i = 0; i < this.datasetInfo.size(); i++) {
            Dataset childDataset = getDatasetInfo(i).data;
            if (childDataset instanceof CombinedDataset) {
                int m = ((CombinedDataset) childDataset).getChildPosition(child);
                if (m >= 0) {
                    return n + m;
                }
                n++;
            } else if (child == childDataset) {
                return n;
            } else {
                n++;
            }
        }
        return -1;
    }

    private DatasetInfo getDatasetInfo(int series) {
        return (DatasetInfo) this.datasetInfo.get(series);
    }

    private int[] joinMap(int[] a, int[] b) {
        if (a == null) {
            return b;
        }
        if (b == null) {
            return a;
        }
        int[] result = new int[(a.length + b.length)];
        System.arraycopy(a, 0, result, 0, a.length);
        System.arraycopy(b, 0, result, a.length, b.length);
        return result;
    }
}
