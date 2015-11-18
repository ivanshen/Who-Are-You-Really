package org.jfree.data.category;

import java.util.Collections;
import java.util.List;
import org.jfree.chart.util.ParamChecks;
import org.jfree.data.general.AbstractDataset;
import org.jfree.data.general.DatasetChangeEvent;
import org.jfree.data.general.DatasetChangeListener;
import org.jfree.data.general.PieDataset;
import org.jfree.util.TableOrder;

public class CategoryToPieDataset extends AbstractDataset implements PieDataset, DatasetChangeListener {
    static final long serialVersionUID = 5516396319762189617L;
    private TableOrder extract;
    private int index;
    private CategoryDataset source;

    public CategoryToPieDataset(CategoryDataset source, TableOrder extract, int index) {
        ParamChecks.nullNotPermitted(extract, "extract");
        this.source = source;
        if (this.source != null) {
            this.source.addChangeListener(this);
        }
        this.extract = extract;
        this.index = index;
    }

    public CategoryDataset getUnderlyingDataset() {
        return this.source;
    }

    public TableOrder getExtractType() {
        return this.extract;
    }

    public int getExtractIndex() {
        return this.index;
    }

    public int getItemCount() {
        if (this.source == null) {
            return 0;
        }
        if (this.extract == TableOrder.BY_ROW) {
            return this.source.getColumnCount();
        }
        if (this.extract == TableOrder.BY_COLUMN) {
            return this.source.getRowCount();
        }
        return 0;
    }

    public Number getValue(int item) {
        if (item < 0 || item >= getItemCount()) {
            throw new IndexOutOfBoundsException("The 'item' index is out of bounds.");
        } else if (this.extract == TableOrder.BY_ROW) {
            return this.source.getValue(this.index, item);
        } else {
            if (this.extract == TableOrder.BY_COLUMN) {
                return this.source.getValue(item, this.index);
            }
            return null;
        }
    }

    public Comparable getKey(int index) {
        if (index < 0 || index >= getItemCount()) {
            throw new IndexOutOfBoundsException("Invalid 'index': " + index);
        } else if (this.extract == TableOrder.BY_ROW) {
            return this.source.getColumnKey(index);
        } else {
            if (this.extract == TableOrder.BY_COLUMN) {
                return this.source.getRowKey(index);
            }
            return null;
        }
    }

    public int getIndex(Comparable key) {
        if (this.source == null) {
            return -1;
        }
        if (this.extract == TableOrder.BY_ROW) {
            return this.source.getColumnIndex(key);
        }
        if (this.extract == TableOrder.BY_COLUMN) {
            return this.source.getRowIndex(key);
        }
        return -1;
    }

    public List getKeys() {
        List result = Collections.EMPTY_LIST;
        if (this.source == null) {
            return result;
        }
        if (this.extract == TableOrder.BY_ROW) {
            return this.source.getColumnKeys();
        }
        if (this.extract == TableOrder.BY_COLUMN) {
            return this.source.getRowKeys();
        }
        return result;
    }

    public Number getValue(Comparable key) {
        int keyIndex = getIndex(key);
        if (keyIndex == -1) {
            return null;
        }
        if (this.extract == TableOrder.BY_ROW) {
            return this.source.getValue(this.index, keyIndex);
        }
        if (this.extract == TableOrder.BY_COLUMN) {
            return this.source.getValue(keyIndex, this.index);
        }
        return null;
    }

    public void datasetChanged(DatasetChangeEvent event) {
        fireDatasetChanged();
    }

    public boolean equals(Object obj) {
        if (obj == this) {
            return true;
        }
        if (!(obj instanceof PieDataset)) {
            return false;
        }
        PieDataset that = (PieDataset) obj;
        int count = getItemCount();
        if (that.getItemCount() != count) {
            return false;
        }
        for (int i = 0; i < count; i++) {
            if (!getKey(i).equals(that.getKey(i))) {
                return false;
            }
            Number v1 = getValue(i);
            Number v2 = that.getValue(i);
            if (v1 == null) {
                if (v2 != null) {
                    return false;
                }
            } else if (!v1.equals(v2)) {
                return false;
            }
        }
        return true;
    }
}
