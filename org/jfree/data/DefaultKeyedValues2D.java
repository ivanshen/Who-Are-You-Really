package org.jfree.data;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import org.jfree.chart.util.ParamChecks;
import org.jfree.util.ObjectUtilities;
import org.jfree.util.PublicCloneable;

public class DefaultKeyedValues2D implements KeyedValues2D, PublicCloneable, Cloneable, Serializable {
    private static final long serialVersionUID = -5514169970951994748L;
    private List columnKeys;
    private List rowKeys;
    private List rows;
    private boolean sortRowKeys;

    public DefaultKeyedValues2D() {
        this(false);
    }

    public DefaultKeyedValues2D(boolean sortRowKeys) {
        this.rowKeys = new ArrayList();
        this.columnKeys = new ArrayList();
        this.rows = new ArrayList();
        this.sortRowKeys = sortRowKeys;
    }

    public int getRowCount() {
        return this.rowKeys.size();
    }

    public int getColumnCount() {
        return this.columnKeys.size();
    }

    public Number getValue(int row, int column) {
        DefaultKeyedValues rowData = (DefaultKeyedValues) this.rows.get(row);
        if (rowData == null) {
            return null;
        }
        int index = rowData.getIndex((Comparable) this.columnKeys.get(column));
        if (index >= 0) {
            return rowData.getValue(index);
        }
        return null;
    }

    public Comparable getRowKey(int row) {
        return (Comparable) this.rowKeys.get(row);
    }

    public int getRowIndex(Comparable key) {
        ParamChecks.nullNotPermitted(key, "key");
        if (this.sortRowKeys) {
            return Collections.binarySearch(this.rowKeys, key);
        }
        return this.rowKeys.indexOf(key);
    }

    public List getRowKeys() {
        return Collections.unmodifiableList(this.rowKeys);
    }

    public Comparable getColumnKey(int column) {
        return (Comparable) this.columnKeys.get(column);
    }

    public int getColumnIndex(Comparable key) {
        ParamChecks.nullNotPermitted(key, "key");
        return this.columnKeys.indexOf(key);
    }

    public List getColumnKeys() {
        return Collections.unmodifiableList(this.columnKeys);
    }

    public Number getValue(Comparable rowKey, Comparable columnKey) {
        ParamChecks.nullNotPermitted(rowKey, "rowKey");
        ParamChecks.nullNotPermitted(columnKey, "columnKey");
        if (this.columnKeys.contains(columnKey)) {
            int row = getRowIndex(rowKey);
            if (row >= 0) {
                DefaultKeyedValues rowData = (DefaultKeyedValues) this.rows.get(row);
                int col = rowData.getIndex(columnKey);
                return col >= 0 ? rowData.getValue(col) : null;
            } else {
                throw new UnknownKeyException("Unrecognised rowKey: " + rowKey);
            }
        }
        throw new UnknownKeyException("Unrecognised columnKey: " + columnKey);
    }

    public void addValue(Number value, Comparable rowKey, Comparable columnKey) {
        setValue(value, rowKey, columnKey);
    }

    public void setValue(Number value, Comparable rowKey, Comparable columnKey) {
        DefaultKeyedValues row;
        int rowIndex = getRowIndex(rowKey);
        if (rowIndex >= 0) {
            row = (DefaultKeyedValues) this.rows.get(rowIndex);
        } else {
            row = new DefaultKeyedValues();
            if (this.sortRowKeys) {
                rowIndex = (-rowIndex) - 1;
                this.rowKeys.add(rowIndex, rowKey);
                this.rows.add(rowIndex, row);
            } else {
                this.rowKeys.add(rowKey);
                this.rows.add(row);
            }
        }
        row.setValue(columnKey, value);
        if (this.columnKeys.indexOf(columnKey) < 0) {
            this.columnKeys.add(columnKey);
        }
    }

    public void removeValue(Comparable rowKey, Comparable columnKey) {
        int item;
        setValue(null, rowKey, columnKey);
        boolean allNull = true;
        int rowIndex = getRowIndex(rowKey);
        DefaultKeyedValues row = (DefaultKeyedValues) this.rows.get(rowIndex);
        int itemCount = row.getItemCount();
        for (item = 0; item < itemCount; item++) {
            if (row.getValue(item) != null) {
                allNull = false;
                break;
            }
        }
        if (allNull) {
            this.rowKeys.remove(rowIndex);
            this.rows.remove(rowIndex);
        }
        allNull = true;
        itemCount = this.rows.size();
        for (item = 0; item < itemCount; item++) {
            row = (DefaultKeyedValues) this.rows.get(item);
            int columnIndex = row.getIndex(columnKey);
            if (columnIndex >= 0 && row.getValue(columnIndex) != null) {
                allNull = false;
                break;
            }
        }
        if (allNull) {
            itemCount = this.rows.size();
            for (item = 0; item < itemCount; item++) {
                row = (DefaultKeyedValues) this.rows.get(item);
                columnIndex = row.getIndex(columnKey);
                if (columnIndex >= 0) {
                    row.removeValue(columnIndex);
                }
            }
            this.columnKeys.remove(columnKey);
        }
    }

    public void removeRow(int rowIndex) {
        this.rowKeys.remove(rowIndex);
        this.rows.remove(rowIndex);
    }

    public void removeRow(Comparable rowKey) {
        ParamChecks.nullNotPermitted(rowKey, "rowKey");
        int index = getRowIndex(rowKey);
        if (index >= 0) {
            removeRow(index);
            return;
        }
        throw new UnknownKeyException("Unknown key: " + rowKey);
    }

    public void removeColumn(int columnIndex) {
        removeColumn(getColumnKey(columnIndex));
    }

    public void removeColumn(Comparable columnKey) {
        ParamChecks.nullNotPermitted(columnKey, "columnKey");
        if (this.columnKeys.contains(columnKey)) {
            for (DefaultKeyedValues rowData : this.rows) {
                if (rowData.getIndex(columnKey) >= 0) {
                    rowData.removeValue(columnKey);
                }
            }
            this.columnKeys.remove(columnKey);
            return;
        }
        throw new UnknownKeyException("Unknown key: " + columnKey);
    }

    public void clear() {
        this.rowKeys.clear();
        this.columnKeys.clear();
        this.rows.clear();
    }

    public boolean equals(Object o) {
        if (o == null) {
            return false;
        }
        if (o == this) {
            return true;
        }
        if (!(o instanceof KeyedValues2D)) {
            return false;
        }
        KeyedValues2D kv2D = (KeyedValues2D) o;
        if (!getRowKeys().equals(kv2D.getRowKeys()) || !getColumnKeys().equals(kv2D.getColumnKeys())) {
            return false;
        }
        int rowCount = getRowCount();
        if (rowCount != kv2D.getRowCount()) {
            return false;
        }
        int colCount = getColumnCount();
        if (colCount != kv2D.getColumnCount()) {
            return false;
        }
        for (int r = 0; r < rowCount; r++) {
            for (int c = 0; c < colCount; c++) {
                Number v1 = getValue(r, c);
                Number v2 = kv2D.getValue(r, c);
                if (v1 == null) {
                    if (v2 != null) {
                        return false;
                    }
                } else if (!v1.equals(v2)) {
                    return false;
                }
            }
        }
        return true;
    }

    public int hashCode() {
        return (((this.rowKeys.hashCode() * 29) + this.columnKeys.hashCode()) * 29) + this.rows.hashCode();
    }

    public Object clone() throws CloneNotSupportedException {
        DefaultKeyedValues2D clone = (DefaultKeyedValues2D) super.clone();
        clone.columnKeys = new ArrayList(this.columnKeys);
        clone.rowKeys = new ArrayList(this.rowKeys);
        clone.rows = (List) ObjectUtilities.deepClone(this.rows);
        return clone;
    }
}
