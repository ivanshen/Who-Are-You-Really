package org.jfree.data.xy;

import org.jfree.data.ComparableObjectItem;
import org.jfree.data.ComparableObjectSeries;

public class XIntervalSeries extends ComparableObjectSeries {
    public XIntervalSeries(Comparable key) {
        this(key, true, true);
    }

    public XIntervalSeries(Comparable key, boolean autoSort, boolean allowDuplicateXValues) {
        super(key, autoSort, allowDuplicateXValues);
    }

    public void add(double x, double xLow, double xHigh, double y) {
        add(new XIntervalDataItem(x, xLow, xHigh, y), true);
    }

    public void add(XIntervalDataItem item, boolean notify) {
        super.add((ComparableObjectItem) item, notify);
    }

    public Number getX(int index) {
        return ((XIntervalDataItem) getDataItem(index)).getX();
    }

    public double getXLowValue(int index) {
        return ((XIntervalDataItem) getDataItem(index)).getXLowValue();
    }

    public double getXHighValue(int index) {
        return ((XIntervalDataItem) getDataItem(index)).getXHighValue();
    }

    public double getYValue(int index) {
        return ((XIntervalDataItem) getDataItem(index)).getYValue();
    }

    public ComparableObjectItem getDataItem(int index) {
        return super.getDataItem(index);
    }
}
