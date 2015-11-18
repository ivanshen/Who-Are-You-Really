package org.jfree.data.xy;

import java.io.Serializable;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import org.jfree.chart.urls.StandardXYURLGenerator;
import org.jfree.chart.util.ParamChecks;
import org.jfree.data.general.Series;
import org.jfree.data.general.SeriesException;
import org.jfree.util.ObjectUtilities;

public class XYSeries extends Series implements Cloneable, Serializable {
    static final long serialVersionUID = -5908509288197150436L;
    private boolean allowDuplicateXValues;
    private boolean autoSort;
    protected List data;
    private double maxX;
    private double maxY;
    private int maximumItemCount;
    private double minX;
    private double minY;

    public XYSeries(Comparable key) {
        this(key, true, true);
    }

    public XYSeries(Comparable key, boolean autoSort) {
        this(key, autoSort, true);
    }

    public XYSeries(Comparable key, boolean autoSort, boolean allowDuplicateXValues) {
        super(key);
        this.maximumItemCount = Integer.MAX_VALUE;
        this.data = new ArrayList();
        this.autoSort = autoSort;
        this.allowDuplicateXValues = allowDuplicateXValues;
        this.minX = Double.NaN;
        this.maxX = Double.NaN;
        this.minY = Double.NaN;
        this.maxY = Double.NaN;
    }

    public double getMinX() {
        return this.minX;
    }

    public double getMaxX() {
        return this.maxX;
    }

    public double getMinY() {
        return this.minY;
    }

    public double getMaxY() {
        return this.maxY;
    }

    private void updateBoundsForAddedItem(XYDataItem item) {
        double x = item.getXValue();
        this.minX = minIgnoreNaN(this.minX, x);
        this.maxX = maxIgnoreNaN(this.maxX, x);
        if (item.getY() != null) {
            double y = item.getYValue();
            this.minY = minIgnoreNaN(this.minY, y);
            this.maxY = maxIgnoreNaN(this.maxY, y);
        }
    }

    private void updateBoundsForRemovedItem(XYDataItem item) {
        boolean itemContributesToXBounds = false;
        boolean itemContributesToYBounds = false;
        double x = item.getXValue();
        if (!Double.isNaN(x) && (x <= this.minX || x >= this.maxX)) {
            itemContributesToXBounds = true;
        }
        if (item.getY() != null) {
            double y = item.getYValue();
            if (!Double.isNaN(y) && (y <= this.minY || y >= this.maxY)) {
                itemContributesToYBounds = true;
            }
        }
        if (itemContributesToYBounds) {
            findBoundsByIteration();
        } else if (!itemContributesToXBounds) {
        } else {
            if (getAutoSort()) {
                this.minX = getX(0).doubleValue();
                this.maxX = getX(getItemCount() - 1).doubleValue();
                return;
            }
            findBoundsByIteration();
        }
    }

    private void findBoundsByIteration() {
        this.minX = Double.NaN;
        this.maxX = Double.NaN;
        this.minY = Double.NaN;
        this.maxY = Double.NaN;
        for (XYDataItem item : this.data) {
            updateBoundsForAddedItem(item);
        }
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

    public List getItems() {
        return Collections.unmodifiableList(this.data);
    }

    public int getMaximumItemCount() {
        return this.maximumItemCount;
    }

    public void setMaximumItemCount(int maximum) {
        this.maximumItemCount = maximum;
        int remove = this.data.size() - maximum;
        if (remove > 0) {
            this.data.subList(0, remove).clear();
            findBoundsByIteration();
            fireSeriesChanged();
        }
    }

    public void add(XYDataItem item) {
        add(item, true);
    }

    public void add(double x, double y) {
        add(new Double(x), new Double(y), true);
    }

    public void add(double x, double y, boolean notify) {
        add(new Double(x), new Double(y), notify);
    }

    public void add(double x, Number y) {
        add(new Double(x), y);
    }

    public void add(double x, Number y, boolean notify) {
        add(new Double(x), y, notify);
    }

    public void add(Number x, Number y) {
        add(x, y, true);
    }

    public void add(Number x, Number y, boolean notify) {
        add(new XYDataItem(x, y), notify);
    }

    public void add(XYDataItem item, boolean notify) {
        ParamChecks.nullNotPermitted(item, StandardXYURLGenerator.DEFAULT_ITEM_PARAMETER);
        item = (XYDataItem) item.clone();
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
        } else if (this.allowDuplicateXValues || indexOf(item.getX()) < 0) {
            this.data.add(item);
        } else {
            throw new SeriesException("X-value already exists.");
        }
        updateBoundsForAddedItem(item);
        if (getItemCount() > this.maximumItemCount) {
            updateBoundsForRemovedItem((XYDataItem) this.data.remove(0));
        }
        if (notify) {
            fireSeriesChanged();
        }
    }

    public void delete(int start, int end) {
        this.data.subList(start, end + 1).clear();
        findBoundsByIteration();
        fireSeriesChanged();
    }

    public XYDataItem remove(int index) {
        XYDataItem removed = (XYDataItem) this.data.remove(index);
        updateBoundsForRemovedItem(removed);
        fireSeriesChanged();
        return removed;
    }

    public XYDataItem remove(Number x) {
        return remove(indexOf(x));
    }

    public void clear() {
        if (this.data.size() > 0) {
            this.data.clear();
            this.minX = Double.NaN;
            this.maxX = Double.NaN;
            this.minY = Double.NaN;
            this.maxY = Double.NaN;
            fireSeriesChanged();
        }
    }

    public XYDataItem getDataItem(int index) {
        return (XYDataItem) ((XYDataItem) this.data.get(index)).clone();
    }

    XYDataItem getRawDataItem(int index) {
        return (XYDataItem) this.data.get(index);
    }

    public Number getX(int index) {
        return getRawDataItem(index).getX();
    }

    public Number getY(int index) {
        return getRawDataItem(index).getY();
    }

    public void update(int index, Number y) {
        XYDataItem item = getRawDataItem(index);
        boolean iterate = false;
        double oldY = item.getYValue();
        if (!Double.isNaN(oldY)) {
            iterate = oldY <= this.minY || oldY >= this.maxY;
        }
        item.setY(y);
        if (iterate) {
            findBoundsByIteration();
        } else if (y != null) {
            double yy = y.doubleValue();
            this.minY = minIgnoreNaN(this.minY, yy);
            this.maxY = maxIgnoreNaN(this.maxY, yy);
        }
        fireSeriesChanged();
    }

    private double minIgnoreNaN(double a, double b) {
        if (Double.isNaN(a)) {
            return b;
        }
        if (Double.isNaN(b)) {
            return a;
        }
        return Math.min(a, b);
    }

    private double maxIgnoreNaN(double a, double b) {
        if (Double.isNaN(a)) {
            return b;
        }
        if (Double.isNaN(b)) {
            return a;
        }
        return Math.max(a, b);
    }

    public void updateByIndex(int index, Number y) {
        update(index, y);
    }

    public void update(Number x, Number y) {
        int index = indexOf(x);
        if (index < 0) {
            throw new SeriesException("No observation for x = " + x);
        }
        updateByIndex(index, y);
    }

    public XYDataItem addOrUpdate(double x, double y) {
        return addOrUpdate(new Double(x), new Double(y));
    }

    public XYDataItem addOrUpdate(Number x, Number y) {
        return addOrUpdate(new XYDataItem(x, y));
    }

    public XYDataItem addOrUpdate(XYDataItem item) {
        ParamChecks.nullNotPermitted(item, StandardXYURLGenerator.DEFAULT_ITEM_PARAMETER);
        if (this.allowDuplicateXValues) {
            add(item);
            return null;
        }
        XYDataItem xYDataItem = null;
        int index = indexOf(item.getX());
        if (index >= 0) {
            XYDataItem existing = (XYDataItem) this.data.get(index);
            xYDataItem = (XYDataItem) existing.clone();
            boolean iterate = false;
            double oldY = existing.getYValue();
            if (!Double.isNaN(oldY)) {
                if (oldY <= this.minY || oldY >= this.maxY) {
                    iterate = true;
                } else {
                    iterate = false;
                }
            }
            existing.setY(item.getY());
            if (iterate) {
                findBoundsByIteration();
            } else if (item.getY() != null) {
                double yy = item.getY().doubleValue();
                this.minY = minIgnoreNaN(this.minY, yy);
                this.maxY = maxIgnoreNaN(this.maxY, yy);
            }
        } else {
            item = (XYDataItem) item.clone();
            if (this.autoSort) {
                this.data.add((-index) - 1, item);
            } else {
                this.data.add(item);
            }
            updateBoundsForAddedItem(item);
            if (getItemCount() > this.maximumItemCount) {
                updateBoundsForRemovedItem((XYDataItem) this.data.remove(0));
            }
        }
        fireSeriesChanged();
        return xYDataItem;
    }

    public int indexOf(Number x) {
        if (this.autoSort) {
            return Collections.binarySearch(this.data, new XYDataItem(x, null));
        }
        for (int i = 0; i < this.data.size(); i++) {
            if (((XYDataItem) this.data.get(i)).getX().equals(x)) {
                return i;
            }
        }
        return -1;
    }

    public double[][] toArray() {
        int itemCount = getItemCount();
        double[][] result = (double[][]) Array.newInstance(Double.TYPE, new int[]{2, itemCount});
        for (int i = 0; i < itemCount; i++) {
            result[0][i] = getX(i).doubleValue();
            Number y = getY(i);
            if (y != null) {
                result[1][i] = y.doubleValue();
            } else {
                result[1][i] = Double.NaN;
            }
        }
        return result;
    }

    public Object clone() throws CloneNotSupportedException {
        XYSeries clone = (XYSeries) super.clone();
        clone.data = (List) ObjectUtilities.deepClone(this.data);
        return clone;
    }

    public XYSeries createCopy(int start, int end) throws CloneNotSupportedException {
        XYSeries copy = (XYSeries) super.clone();
        copy.data = new ArrayList();
        if (this.data.size() > 0) {
            int index = start;
            while (index <= end) {
                try {
                    copy.add((XYDataItem) ((XYDataItem) this.data.get(index)).clone());
                    index++;
                } catch (SeriesException e) {
                    throw new RuntimeException("Unable to add cloned data item.", e);
                }
            }
        }
        return copy;
    }

    public boolean equals(Object obj) {
        if (obj == this) {
            return true;
        }
        if (!(obj instanceof XYSeries)) {
            return false;
        }
        if (!super.equals(obj)) {
            return false;
        }
        XYSeries that = (XYSeries) obj;
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
            result = (result * 29) + getRawDataItem(0).hashCode();
        }
        if (count > 1) {
            result = (result * 29) + getRawDataItem(count - 1).hashCode();
        }
        if (count > 2) {
            result = (result * 29) + getRawDataItem(count / 2).hashCode();
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
