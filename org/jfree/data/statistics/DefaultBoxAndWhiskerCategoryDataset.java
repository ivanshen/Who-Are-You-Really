package org.jfree.data.statistics;

import java.util.List;
import org.jfree.data.KeyedObjects2D;
import org.jfree.data.Range;
import org.jfree.data.RangeInfo;
import org.jfree.data.general.AbstractDataset;
import org.jfree.util.ObjectUtilities;
import org.jfree.util.PublicCloneable;

public class DefaultBoxAndWhiskerCategoryDataset extends AbstractDataset implements BoxAndWhiskerCategoryDataset, RangeInfo, PublicCloneable {
    protected KeyedObjects2D data;
    private double maximumRangeValue;
    private int maximumRangeValueColumn;
    private int maximumRangeValueRow;
    private double minimumRangeValue;
    private int minimumRangeValueColumn;
    private int minimumRangeValueRow;

    public DefaultBoxAndWhiskerCategoryDataset() {
        this.data = new KeyedObjects2D();
        this.minimumRangeValue = Double.NaN;
        this.minimumRangeValueRow = -1;
        this.minimumRangeValueColumn = -1;
        this.maximumRangeValue = Double.NaN;
        this.maximumRangeValueRow = -1;
        this.maximumRangeValueColumn = -1;
    }

    public void add(List list, Comparable rowKey, Comparable columnKey) {
        add(BoxAndWhiskerCalculator.calculateBoxAndWhiskerStatistics(list), rowKey, columnKey);
    }

    public void add(BoxAndWhiskerItem item, Comparable rowKey, Comparable columnKey) {
        this.data.addObject(item, rowKey, columnKey);
        int r = this.data.getRowIndex(rowKey);
        int c = this.data.getColumnIndex(columnKey);
        if ((this.maximumRangeValueRow == r && this.maximumRangeValueColumn == c) || (this.minimumRangeValueRow == r && this.minimumRangeValueColumn == c)) {
            updateBounds();
        } else {
            double minval = Double.NaN;
            if (item.getMinOutlier() != null) {
                minval = item.getMinOutlier().doubleValue();
            }
            double maxval = Double.NaN;
            if (item.getMaxOutlier() != null) {
                maxval = item.getMaxOutlier().doubleValue();
            }
            if (Double.isNaN(this.maximumRangeValue)) {
                this.maximumRangeValue = maxval;
                this.maximumRangeValueRow = r;
                this.maximumRangeValueColumn = c;
            } else if (maxval > this.maximumRangeValue) {
                this.maximumRangeValue = maxval;
                this.maximumRangeValueRow = r;
                this.maximumRangeValueColumn = c;
            }
            if (Double.isNaN(this.minimumRangeValue)) {
                this.minimumRangeValue = minval;
                this.minimumRangeValueRow = r;
                this.minimumRangeValueColumn = c;
            } else if (minval < this.minimumRangeValue) {
                this.minimumRangeValue = minval;
                this.minimumRangeValueRow = r;
                this.minimumRangeValueColumn = c;
            }
        }
        fireDatasetChanged();
    }

    public void remove(Comparable rowKey, Comparable columnKey) {
        int r = getRowIndex(rowKey);
        int c = getColumnIndex(columnKey);
        this.data.removeObject(rowKey, columnKey);
        if ((this.maximumRangeValueRow == r && this.maximumRangeValueColumn == c) || (this.minimumRangeValueRow == r && this.minimumRangeValueColumn == c)) {
            updateBounds();
        }
        fireDatasetChanged();
    }

    public void removeRow(int rowIndex) {
        this.data.removeRow(rowIndex);
        updateBounds();
        fireDatasetChanged();
    }

    public void removeRow(Comparable rowKey) {
        this.data.removeRow(rowKey);
        updateBounds();
        fireDatasetChanged();
    }

    public void removeColumn(int columnIndex) {
        this.data.removeColumn(columnIndex);
        updateBounds();
        fireDatasetChanged();
    }

    public void removeColumn(Comparable columnKey) {
        this.data.removeColumn(columnKey);
        updateBounds();
        fireDatasetChanged();
    }

    public void clear() {
        this.data.clear();
        updateBounds();
        fireDatasetChanged();
    }

    public BoxAndWhiskerItem getItem(int row, int column) {
        return (BoxAndWhiskerItem) this.data.getObject(row, column);
    }

    public Number getValue(int row, int column) {
        return getMedianValue(row, column);
    }

    public Number getValue(Comparable rowKey, Comparable columnKey) {
        return getMedianValue(rowKey, columnKey);
    }

    public Number getMeanValue(int row, int column) {
        BoxAndWhiskerItem item = (BoxAndWhiskerItem) this.data.getObject(row, column);
        if (item != null) {
            return item.getMean();
        }
        return null;
    }

    public Number getMeanValue(Comparable rowKey, Comparable columnKey) {
        BoxAndWhiskerItem item = (BoxAndWhiskerItem) this.data.getObject(rowKey, columnKey);
        if (item != null) {
            return item.getMean();
        }
        return null;
    }

    public Number getMedianValue(int row, int column) {
        BoxAndWhiskerItem item = (BoxAndWhiskerItem) this.data.getObject(row, column);
        if (item != null) {
            return item.getMedian();
        }
        return null;
    }

    public Number getMedianValue(Comparable rowKey, Comparable columnKey) {
        BoxAndWhiskerItem item = (BoxAndWhiskerItem) this.data.getObject(rowKey, columnKey);
        if (item != null) {
            return item.getMedian();
        }
        return null;
    }

    public Number getQ1Value(int row, int column) {
        BoxAndWhiskerItem item = (BoxAndWhiskerItem) this.data.getObject(row, column);
        if (item != null) {
            return item.getQ1();
        }
        return null;
    }

    public Number getQ1Value(Comparable rowKey, Comparable columnKey) {
        BoxAndWhiskerItem item = (BoxAndWhiskerItem) this.data.getObject(rowKey, columnKey);
        if (item != null) {
            return item.getQ1();
        }
        return null;
    }

    public Number getQ3Value(int row, int column) {
        BoxAndWhiskerItem item = (BoxAndWhiskerItem) this.data.getObject(row, column);
        if (item != null) {
            return item.getQ3();
        }
        return null;
    }

    public Number getQ3Value(Comparable rowKey, Comparable columnKey) {
        BoxAndWhiskerItem item = (BoxAndWhiskerItem) this.data.getObject(rowKey, columnKey);
        if (item != null) {
            return item.getQ3();
        }
        return null;
    }

    public int getColumnIndex(Comparable key) {
        return this.data.getColumnIndex(key);
    }

    public Comparable getColumnKey(int column) {
        return this.data.getColumnKey(column);
    }

    public List getColumnKeys() {
        return this.data.getColumnKeys();
    }

    public int getRowIndex(Comparable key) {
        return this.data.getRowIndex(key);
    }

    public Comparable getRowKey(int row) {
        return this.data.getRowKey(row);
    }

    public List getRowKeys() {
        return this.data.getRowKeys();
    }

    public int getRowCount() {
        return this.data.getRowCount();
    }

    public int getColumnCount() {
        return this.data.getColumnCount();
    }

    public double getRangeLowerBound(boolean includeInterval) {
        return this.minimumRangeValue;
    }

    public double getRangeUpperBound(boolean includeInterval) {
        return this.maximumRangeValue;
    }

    public Range getRangeBounds(boolean includeInterval) {
        return new Range(this.minimumRangeValue, this.maximumRangeValue);
    }

    public Number getMinRegularValue(int row, int column) {
        BoxAndWhiskerItem item = (BoxAndWhiskerItem) this.data.getObject(row, column);
        if (item != null) {
            return item.getMinRegularValue();
        }
        return null;
    }

    public Number getMinRegularValue(Comparable rowKey, Comparable columnKey) {
        BoxAndWhiskerItem item = (BoxAndWhiskerItem) this.data.getObject(rowKey, columnKey);
        if (item != null) {
            return item.getMinRegularValue();
        }
        return null;
    }

    public Number getMaxRegularValue(int row, int column) {
        BoxAndWhiskerItem item = (BoxAndWhiskerItem) this.data.getObject(row, column);
        if (item != null) {
            return item.getMaxRegularValue();
        }
        return null;
    }

    public Number getMaxRegularValue(Comparable rowKey, Comparable columnKey) {
        BoxAndWhiskerItem item = (BoxAndWhiskerItem) this.data.getObject(rowKey, columnKey);
        if (item != null) {
            return item.getMaxRegularValue();
        }
        return null;
    }

    public Number getMinOutlier(int row, int column) {
        BoxAndWhiskerItem item = (BoxAndWhiskerItem) this.data.getObject(row, column);
        if (item != null) {
            return item.getMinOutlier();
        }
        return null;
    }

    public Number getMinOutlier(Comparable rowKey, Comparable columnKey) {
        BoxAndWhiskerItem item = (BoxAndWhiskerItem) this.data.getObject(rowKey, columnKey);
        if (item != null) {
            return item.getMinOutlier();
        }
        return null;
    }

    public Number getMaxOutlier(int row, int column) {
        BoxAndWhiskerItem item = (BoxAndWhiskerItem) this.data.getObject(row, column);
        if (item != null) {
            return item.getMaxOutlier();
        }
        return null;
    }

    public Number getMaxOutlier(Comparable rowKey, Comparable columnKey) {
        BoxAndWhiskerItem item = (BoxAndWhiskerItem) this.data.getObject(rowKey, columnKey);
        if (item != null) {
            return item.getMaxOutlier();
        }
        return null;
    }

    public List getOutliers(int row, int column) {
        BoxAndWhiskerItem item = (BoxAndWhiskerItem) this.data.getObject(row, column);
        if (item != null) {
            return item.getOutliers();
        }
        return null;
    }

    public List getOutliers(Comparable rowKey, Comparable columnKey) {
        BoxAndWhiskerItem item = (BoxAndWhiskerItem) this.data.getObject(rowKey, columnKey);
        if (item != null) {
            return item.getOutliers();
        }
        return null;
    }

    private void updateBounds() {
        this.minimumRangeValue = Double.NaN;
        this.minimumRangeValueRow = -1;
        this.minimumRangeValueColumn = -1;
        this.maximumRangeValue = Double.NaN;
        this.maximumRangeValueRow = -1;
        this.maximumRangeValueColumn = -1;
        int rowCount = getRowCount();
        int columnCount = getColumnCount();
        for (int r = 0; r < rowCount; r++) {
            for (int c = 0; c < columnCount; c++) {
                BoxAndWhiskerItem item = getItem(r, c);
                if (item != null) {
                    Number min = item.getMinOutlier();
                    if (min != null) {
                        double minv = min.doubleValue();
                        if (!Double.isNaN(minv) && (minv < this.minimumRangeValue || Double.isNaN(this.minimumRangeValue))) {
                            this.minimumRangeValue = minv;
                            this.minimumRangeValueRow = r;
                            this.minimumRangeValueColumn = c;
                        }
                    }
                    Number max = item.getMaxOutlier();
                    if (max != null) {
                        double maxv = max.doubleValue();
                        if (!Double.isNaN(maxv) && (maxv > this.maximumRangeValue || Double.isNaN(this.maximumRangeValue))) {
                            this.maximumRangeValue = maxv;
                            this.maximumRangeValueRow = r;
                            this.maximumRangeValueColumn = c;
                        }
                    }
                }
            }
        }
    }

    public boolean equals(Object obj) {
        if (obj == this) {
            return true;
        }
        if (!(obj instanceof DefaultBoxAndWhiskerCategoryDataset)) {
            return false;
        }
        return ObjectUtilities.equal(this.data, ((DefaultBoxAndWhiskerCategoryDataset) obj).data);
    }

    public Object clone() throws CloneNotSupportedException {
        DefaultBoxAndWhiskerCategoryDataset clone = (DefaultBoxAndWhiskerCategoryDataset) super.clone();
        clone.data = (KeyedObjects2D) this.data.clone();
        return clone;
    }
}
