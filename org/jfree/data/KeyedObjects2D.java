package org.jfree.data;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import org.jfree.chart.util.ParamChecks;

public class KeyedObjects2D implements Cloneable, Serializable {
    private static final long serialVersionUID = -1015873563138522374L;
    private List columnKeys;
    private List rowKeys;
    private List rows;

    public KeyedObjects2D() {
        this.rowKeys = new ArrayList();
        this.columnKeys = new ArrayList();
        this.rows = new ArrayList();
    }

    public int getRowCount() {
        return this.rowKeys.size();
    }

    public int getColumnCount() {
        return this.columnKeys.size();
    }

    public Object getObject(int row, int column) {
        KeyedObjects rowData = (KeyedObjects) this.rows.get(row);
        if (rowData == null) {
            return null;
        }
        Comparable columnKey = (Comparable) this.columnKeys.get(column);
        if (columnKey == null || rowData.getIndex(columnKey) < 0) {
            return null;
        }
        return rowData.getObject(columnKey);
    }

    public Comparable getRowKey(int row) {
        return (Comparable) this.rowKeys.get(row);
    }

    public int getRowIndex(Comparable key) {
        ParamChecks.nullNotPermitted(key, "key");
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

    public Object getObject(Comparable rowKey, Comparable columnKey) {
        ParamChecks.nullNotPermitted(rowKey, "rowKey");
        ParamChecks.nullNotPermitted(columnKey, "columnKey");
        int row = this.rowKeys.indexOf(rowKey);
        if (row < 0) {
            throw new UnknownKeyException("Row key (" + rowKey + ") not recognised.");
        } else if (this.columnKeys.indexOf(columnKey) < 0) {
            throw new UnknownKeyException("Column key (" + columnKey + ") not recognised.");
        } else {
            KeyedObjects rowData = (KeyedObjects) this.rows.get(row);
            int index = rowData.getIndex(columnKey);
            if (index >= 0) {
                return rowData.getObject(index);
            }
            return null;
        }
    }

    public void addObject(Object object, Comparable rowKey, Comparable columnKey) {
        setObject(object, rowKey, columnKey);
    }

    public void setObject(Object object, Comparable rowKey, Comparable columnKey) {
        KeyedObjects row;
        ParamChecks.nullNotPermitted(rowKey, "rowKey");
        ParamChecks.nullNotPermitted(columnKey, "columnKey");
        int rowIndex = this.rowKeys.indexOf(rowKey);
        if (rowIndex >= 0) {
            row = (KeyedObjects) this.rows.get(rowIndex);
        } else {
            this.rowKeys.add(rowKey);
            row = new KeyedObjects();
            this.rows.add(row);
        }
        row.setObject(columnKey, object);
        if (this.columnKeys.indexOf(columnKey) < 0) {
            this.columnKeys.add(columnKey);
        }
    }

    public void removeObject(Comparable rowKey, Comparable columnKey) {
        int rowIndex = getRowIndex(rowKey);
        if (rowIndex < 0) {
            throw new UnknownKeyException("Row key (" + rowKey + ") not recognised.");
        } else if (getColumnIndex(columnKey) < 0) {
            throw new UnknownKeyException("Column key (" + columnKey + ") not recognised.");
        } else {
            int item;
            int colIndex;
            setObject(null, rowKey, columnKey);
            boolean allNull = true;
            KeyedObjects row = (KeyedObjects) this.rows.get(rowIndex);
            int itemCount = row.getItemCount();
            for (item = 0; item < itemCount; item++) {
                if (row.getObject(item) != null) {
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
                row = (KeyedObjects) this.rows.get(item);
                colIndex = row.getIndex(columnKey);
                if (colIndex >= 0 && row.getObject(colIndex) != null) {
                    allNull = false;
                    break;
                }
            }
            if (allNull) {
                itemCount = this.rows.size();
                for (item = 0; item < itemCount; item++) {
                    row = (KeyedObjects) this.rows.get(item);
                    colIndex = row.getIndex(columnKey);
                    if (colIndex >= 0) {
                        row.removeValue(colIndex);
                    }
                }
                this.columnKeys.remove(columnKey);
            }
        }
    }

    public void removeRow(int rowIndex) {
        this.rowKeys.remove(rowIndex);
        this.rows.remove(rowIndex);
    }

    public void removeRow(Comparable rowKey) {
        int index = getRowIndex(rowKey);
        if (index < 0) {
            throw new UnknownKeyException("Row key (" + rowKey + ") not recognised.");
        }
        removeRow(index);
    }

    public void removeColumn(int columnIndex) {
        removeColumn(getColumnKey(columnIndex));
    }

    public void removeColumn(Comparable columnKey) {
        if (getColumnIndex(columnKey) < 0) {
            throw new UnknownKeyException("Column key (" + columnKey + ") not recognised.");
        }
        for (KeyedObjects rowData : this.rows) {
            int i = rowData.getIndex(columnKey);
            if (i >= 0) {
                rowData.removeValue(i);
            }
        }
        this.columnKeys.remove(columnKey);
    }

    public void clear() {
        this.rowKeys.clear();
        this.columnKeys.clear();
        this.rows.clear();
    }

    public boolean equals(Object obj) {
        if (obj == this) {
            return true;
        }
        if (!(obj instanceof KeyedObjects2D)) {
            return false;
        }
        KeyedObjects2D that = (KeyedObjects2D) obj;
        if (!getRowKeys().equals(that.getRowKeys())) {
            return false;
        }
        if (!getColumnKeys().equals(that.getColumnKeys())) {
            return false;
        }
        int rowCount = getRowCount();
        if (rowCount != that.getRowCount()) {
            return false;
        }
        int colCount = getColumnCount();
        if (colCount != that.getColumnCount()) {
            return false;
        }
        for (int r = 0; r < rowCount; r++) {
            for (int c = 0; c < colCount; c++) {
                Object v1 = getObject(r, c);
                Object v2 = that.getObject(r, c);
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
        KeyedObjects2D clone = (KeyedObjects2D) super.clone();
        clone.columnKeys = new ArrayList(this.columnKeys);
        clone.rowKeys = new ArrayList(this.rowKeys);
        clone.rows = new ArrayList(this.rows.size());
        for (KeyedObjects row : this.rows) {
            clone.rows.add(row.clone());
        }
        return clone;
    }
}
