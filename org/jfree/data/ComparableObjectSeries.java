package org.jfree.data;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import org.jfree.chart.urls.StandardXYURLGenerator;
import org.jfree.chart.util.ParamChecks;
import org.jfree.data.general.Series;
import org.jfree.data.general.SeriesException;
import org.jfree.util.ObjectUtilities;

public class ComparableObjectSeries extends Series implements Cloneable, Serializable {
    private boolean allowDuplicateXValues;
    private boolean autoSort;
    protected List data;
    private int maximumItemCount;

    public ComparableObjectSeries(Comparable key) {
        this(key, true, true);
    }

    public ComparableObjectSeries(Comparable key, boolean autoSort, boolean allowDuplicateXValues) {
        super(key);
        this.maximumItemCount = Integer.MAX_VALUE;
        this.data = new ArrayList();
        this.autoSort = autoSort;
        this.allowDuplicateXValues = allowDuplicateXValues;
    }

    public boolean getAutoSort() {
        return this.autoSort;
    }

    public boolean getAllowDuplicateXValues() {
        return this.allowDuplicateXValues;
    }

    public int getItemCount() {
        return this.data.size();
    }

    public int getMaximumItemCount() {
        return this.maximumItemCount;
    }

    public void setMaximumItemCount(int maximum) {
        this.maximumItemCount = maximum;
        boolean dataRemoved = false;
        while (this.data.size() > maximum) {
            this.data.remove(0);
            dataRemoved = true;
        }
        if (dataRemoved) {
            fireSeriesChanged();
        }
    }

    protected void add(Comparable x, Object y) {
        add(x, y, true);
    }

    protected void add(Comparable x, Object y, boolean notify) {
        add(new ComparableObjectItem(x, y), notify);
    }

    protected void add(ComparableObjectItem item, boolean notify) {
        ParamChecks.nullNotPermitted(item, StandardXYURLGenerator.DEFAULT_ITEM_PARAMETER);
        if (this.autoSort) {
            int index = Collections.binarySearch(this.data, item);
            if (index < 0) {
                this.data.add((-index) - 1, item);
            } else if (this.allowDuplicateXValues) {
                int size = this.data.size();
                while (index < size && item.compareTo(this.data.get(index)) == 0) {
                    index++;
                }
                if (index < this.data.size()) {
                    this.data.add(index, item);
                } else {
                    this.data.add(item);
                }
            } else {
                throw new SeriesException("X-value already exists.");
            }
        } else if (this.allowDuplicateXValues || indexOf(item.getComparable()) < 0) {
            this.data.add(item);
        } else {
            throw new SeriesException("X-value already exists.");
        }
        if (getItemCount() > this.maximumItemCount) {
            this.data.remove(0);
        }
        if (notify) {
            fireSeriesChanged();
        }
    }

    public int indexOf(Comparable x) {
        if (this.autoSort) {
            return Collections.binarySearch(this.data, new ComparableObjectItem(x, null));
        }
        for (int i = 0; i < this.data.size(); i++) {
            if (((ComparableObjectItem) this.data.get(i)).getComparable().equals(x)) {
                return i;
            }
        }
        return -1;
    }

    protected void update(Comparable x, Object y) {
        int index = indexOf(x);
        if (index < 0) {
            throw new SeriesException("No observation for x = " + x);
        }
        getDataItem(index).setObject(y);
        fireSeriesChanged();
    }

    protected void updateByIndex(int index, Object y) {
        getDataItem(index).setObject(y);
        fireSeriesChanged();
    }

    protected ComparableObjectItem getDataItem(int index) {
        return (ComparableObjectItem) this.data.get(index);
    }

    protected void delete(int start, int end) {
        for (int i = start; i <= end; i++) {
            this.data.remove(start);
        }
        fireSeriesChanged();
    }

    public void clear() {
        if (this.data.size() > 0) {
            this.data.clear();
            fireSeriesChanged();
        }
    }

    protected ComparableObjectItem remove(int index) {
        ComparableObjectItem result = (ComparableObjectItem) this.data.remove(index);
        fireSeriesChanged();
        return result;
    }

    public ComparableObjectItem remove(Comparable x) {
        return remove(indexOf(x));
    }

    public boolean equals(Object obj) {
        if (obj == this) {
            return true;
        }
        if (!(obj instanceof ComparableObjectSeries)) {
            return false;
        }
        if (!super.equals(obj)) {
            return false;
        }
        ComparableObjectSeries that = (ComparableObjectSeries) obj;
        if (this.maximumItemCount != that.maximumItemCount) {
            return false;
        }
        if (this.autoSort != that.autoSort) {
            return false;
        }
        if (this.allowDuplicateXValues != that.allowDuplicateXValues) {
            return false;
        }
        if (ObjectUtilities.equal(this.data, that.data)) {
            return true;
        }
        return false;
    }

    public int hashCode() {
        int i;
        int i2 = 1;
        int result = super.hashCode();
        int count = getItemCount();
        if (count > 0) {
            result = (result * 29) + getDataItem(0).hashCode();
        }
        if (count > 1) {
            result = (result * 29) + getDataItem(count - 1).hashCode();
        }
        if (count > 2) {
            result = (result * 29) + getDataItem(count / 2).hashCode();
        }
        int i3 = ((result * 29) + this.maximumItemCount) * 29;
        if (this.autoSort) {
            i = 1;
        } else {
            i = 0;
        }
        i = (i3 + i) * 29;
        if (!this.allowDuplicateXValues) {
            i2 = 0;
        }
        return i + i2;
    }
}
