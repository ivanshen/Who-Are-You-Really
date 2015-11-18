package org.jfree.data.category;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import org.jfree.data.UnknownKeyException;
import org.jfree.data.general.AbstractDataset;
import org.jfree.util.PublicCloneable;

public class SlidingCategoryDataset extends AbstractDataset implements CategoryDataset {
    private int firstCategoryIndex;
    private int maximumCategoryCount;
    private CategoryDataset underlying;

    public SlidingCategoryDataset(CategoryDataset underlying, int firstColumn, int maxColumns) {
        this.underlying = underlying;
        this.firstCategoryIndex = firstColumn;
        this.maximumCategoryCount = maxColumns;
    }

    public CategoryDataset getUnderlyingDataset() {
        return this.underlying;
    }

    public int getFirstCategoryIndex() {
        return this.firstCategoryIndex;
    }

    public void setFirstCategoryIndex(int first) {
        if (first < 0 || first >= this.underlying.getColumnCount()) {
            throw new IllegalArgumentException("Invalid index.");
        }
        this.firstCategoryIndex = first;
        fireDatasetChanged();
    }

    public int getMaximumCategoryCount() {
        return this.maximumCategoryCount;
    }

    public void setMaximumCategoryCount(int max) {
        if (max < 0) {
            throw new IllegalArgumentException("Requires 'max' >= 0.");
        }
        this.maximumCategoryCount = max;
        fireDatasetChanged();
    }

    private int lastCategoryIndex() {
        if (this.maximumCategoryCount == 0) {
            return -1;
        }
        return Math.min(this.firstCategoryIndex + this.maximumCategoryCount, this.underlying.getColumnCount()) - 1;
    }

    public int getColumnIndex(Comparable key) {
        int index = this.underlying.getColumnIndex(key);
        if (index < this.firstCategoryIndex || index > lastCategoryIndex()) {
            return -1;
        }
        return index - this.firstCategoryIndex;
    }

    public Comparable getColumnKey(int column) {
        return this.underlying.getColumnKey(this.firstCategoryIndex + column);
    }

    public List getColumnKeys() {
        List result = new ArrayList();
        int last = lastCategoryIndex();
        for (int i = this.firstCategoryIndex; i <= last; i++) {
            result.add(this.underlying.getColumnKey(i));
        }
        return Collections.unmodifiableList(result);
    }

    public int getRowIndex(Comparable key) {
        return this.underlying.getRowIndex(key);
    }

    public Comparable getRowKey(int row) {
        return this.underlying.getRowKey(row);
    }

    public List getRowKeys() {
        return this.underlying.getRowKeys();
    }

    public Number getValue(Comparable rowKey, Comparable columnKey) {
        int r = getRowIndex(rowKey);
        int c = getColumnIndex(columnKey);
        if (c != -1) {
            return this.underlying.getValue(r, this.firstCategoryIndex + c);
        }
        throw new UnknownKeyException("Unknown columnKey: " + columnKey);
    }

    public int getColumnCount() {
        int last = lastCategoryIndex();
        if (last == -1) {
            return 0;
        }
        return Math.max((last - this.firstCategoryIndex) + 1, 0);
    }

    public int getRowCount() {
        return this.underlying.getRowCount();
    }

    public Number getValue(int row, int column) {
        return this.underlying.getValue(row, this.firstCategoryIndex + column);
    }

    public boolean equals(Object obj) {
        if (obj == this) {
            return true;
        }
        if (!(obj instanceof SlidingCategoryDataset)) {
            return false;
        }
        SlidingCategoryDataset that = (SlidingCategoryDataset) obj;
        if (this.firstCategoryIndex != that.firstCategoryIndex) {
            return false;
        }
        if (this.maximumCategoryCount != that.maximumCategoryCount) {
            return false;
        }
        if (this.underlying.equals(that.underlying)) {
            return true;
        }
        return false;
    }

    public Object clone() throws CloneNotSupportedException {
        SlidingCategoryDataset clone = (SlidingCategoryDataset) super.clone();
        if (this.underlying instanceof PublicCloneable) {
            clone.underlying = (CategoryDataset) this.underlying.clone();
        }
        return clone;
    }
}
