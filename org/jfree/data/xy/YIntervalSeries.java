package org.jfree.data.xy;

import org.jfree.data.ComparableObjectItem;
import org.jfree.data.ComparableObjectSeries;

public class YIntervalSeries extends ComparableObjectSeries {
    public YIntervalSeries(Comparable key) {
        this(key, true, true);
    }

    public YIntervalSeries(Comparable key, boolean autoSort, boolean allowDuplicateXValues) {
        super(key, autoSort, allowDuplicateXValues);
    }

    public void add(double x, double y, double yLow, double yHigh) {
        add(new YIntervalDataItem(x, y, yLow, yHigh), true);
    }

    public void add(YIntervalDataItem item, boolean notify) {
        super.add((ComparableObjectItem) item, notify);
    }

    public Number getX(int index) {
        return ((YIntervalDataItem) getDataItem(index)).getX();
    }

    public double getYValue(int index) {
        return ((YIntervalDataItem) getDataItem(index)).getYValue();
    }

    public double getYLowValue(int index) {
        return ((YIntervalDataItem) getDataItem(index)).getYLowValue();
    }

    public double getYHighValue(int index) {
        return ((YIntervalDataItem) getDataItem(index)).getYHighValue();
    }

    public ComparableObjectItem getDataItem(int index) {
        return super.getDataItem(index);
    }
}
