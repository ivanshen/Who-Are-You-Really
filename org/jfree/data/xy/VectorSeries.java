package org.jfree.data.xy;

import org.jfree.data.ComparableObjectItem;
import org.jfree.data.ComparableObjectSeries;

public class VectorSeries extends ComparableObjectSeries {
    public VectorSeries(Comparable key) {
        this(key, false, true);
    }

    public VectorSeries(Comparable key, boolean autoSort, boolean allowDuplicateXValues) {
        super(key, autoSort, allowDuplicateXValues);
    }

    public void add(double x, double y, double deltaX, double deltaY) {
        add(new VectorDataItem(x, y, deltaX, deltaY), true);
    }

    public void add(VectorDataItem item, boolean notify) {
        super.add((ComparableObjectItem) item, notify);
    }

    public ComparableObjectItem remove(int index) {
        VectorDataItem result = (VectorDataItem) this.data.remove(index);
        fireSeriesChanged();
        return result;
    }

    public double getXValue(int index) {
        return ((VectorDataItem) getDataItem(index)).getXValue();
    }

    public double getYValue(int index) {
        return ((VectorDataItem) getDataItem(index)).getYValue();
    }

    public double getVectorXValue(int index) {
        return ((VectorDataItem) getDataItem(index)).getVectorX();
    }

    public double getVectorYValue(int index) {
        return ((VectorDataItem) getDataItem(index)).getVectorY();
    }

    public ComparableObjectItem getDataItem(int index) {
        return super.getDataItem(index);
    }
}
