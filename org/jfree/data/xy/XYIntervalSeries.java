package org.jfree.data.xy;

import org.jfree.data.ComparableObjectItem;
import org.jfree.data.ComparableObjectSeries;

public class XYIntervalSeries extends ComparableObjectSeries {
    public XYIntervalSeries(Comparable key) {
        this(key, true, true);
    }

    public XYIntervalSeries(Comparable key, boolean autoSort, boolean allowDuplicateXValues) {
        super(key, autoSort, allowDuplicateXValues);
    }

    public void add(double x, double xLow, double xHigh, double y, double yLow, double yHigh) {
        add(new XYIntervalDataItem(x, xLow, xHigh, y, yLow, yHigh), true);
    }

    public void add(XYIntervalDataItem item, boolean notify) {
        super.add((ComparableObjectItem) item, notify);
    }

    public Number getX(int index) {
        return ((XYIntervalDataItem) getDataItem(index)).getX();
    }

    public double getXLowValue(int index) {
        return ((XYIntervalDataItem) getDataItem(index)).getXLowValue();
    }

    public double getXHighValue(int index) {
        return ((XYIntervalDataItem) getDataItem(index)).getXHighValue();
    }

    public double getYValue(int index) {
        return ((XYIntervalDataItem) getDataItem(index)).getYValue();
    }

    public double getYLowValue(int index) {
        return ((XYIntervalDataItem) getDataItem(index)).getYLowValue();
    }

    public double getYHighValue(int index) {
        return ((XYIntervalDataItem) getDataItem(index)).getYHighValue();
    }

    public ComparableObjectItem getDataItem(int index) {
        return super.getDataItem(index);
    }
}
