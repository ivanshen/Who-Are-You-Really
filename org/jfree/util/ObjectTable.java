package org.jfree.util;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.Arrays;

public class ObjectTable implements Serializable {
    private static final long serialVersionUID = -3968322452944912066L;
    private int columnIncrement;
    private int columns;
    private transient Object[][] data;
    private int rowIncrement;
    private int rows;

    public ObjectTable() {
        this(5, 5);
    }

    public ObjectTable(int increment) {
        this(increment, increment);
    }

    public ObjectTable(int rowIncrement, int colIncrement) {
        if (rowIncrement < 1) {
            throw new IllegalArgumentException("Increment must be positive.");
        } else if (colIncrement < 1) {
            throw new IllegalArgumentException("Increment must be positive.");
        } else {
            this.rows = 0;
            this.columns = 0;
            this.rowIncrement = rowIncrement;
            this.columnIncrement = colIncrement;
            this.data = new Object[rowIncrement][];
        }
    }

    public int getColumnIncrement() {
        return this.columnIncrement;
    }

    public int getRowIncrement() {
        return this.rowIncrement;
    }

    protected void ensureRowCapacity(int row) {
        if (row >= this.data.length) {
            Object[][] enlarged = new Object[(this.rowIncrement + row)][];
            System.arraycopy(this.data, 0, enlarged, 0, this.data.length);
            this.data = enlarged;
        }
    }

    public void ensureCapacity(int row, int column) {
        if (row < 0) {
            throw new IndexOutOfBoundsException("Row is invalid. " + row);
        } else if (column < 0) {
            throw new IndexOutOfBoundsException("Column is invalid. " + column);
        } else {
            ensureRowCapacity(row);
            Object[] current = this.data[row];
            if (current == null) {
                this.data[row] = new Object[Math.max(column + 1, this.columnIncrement)];
            } else if (column >= current.length) {
                Object[] enlarged = new Object[(this.columnIncrement + column)];
                System.arraycopy(current, 0, enlarged, 0, current.length);
                this.data[row] = enlarged;
            }
        }
    }

    public int getRowCount() {
        return this.rows;
    }

    public int getColumnCount() {
        return this.columns;
    }

    protected Object getObject(int row, int column) {
        if (row >= this.data.length) {
            return null;
        }
        Object[] current = this.data[row];
        if (current != null && column < current.length) {
            return current[column];
        }
        return null;
    }

    protected void setObject(int row, int column, Object object) {
        ensureCapacity(row, column);
        this.data[row][column] = object;
        this.rows = Math.max(this.rows, row + 1);
        this.columns = Math.max(this.columns, column + 1);
    }

    public boolean equals(Object o) {
        if (o == null) {
            return false;
        }
        if (this == o) {
            return true;
        }
        if (!(o instanceof ObjectTable)) {
            return false;
        }
        ObjectTable ot = (ObjectTable) o;
        if (getRowCount() != ot.getRowCount() || getColumnCount() != ot.getColumnCount()) {
            return false;
        }
        for (int r = 0; r < getRowCount(); r++) {
            for (int c = 0; c < getColumnCount(); c++) {
                if (!ObjectUtilities.equal(getObject(r, c), ot.getObject(r, c))) {
                    return false;
                }
            }
        }
        return true;
    }

    public int hashCode() {
        return (this.rows * 29) + this.columns;
    }

    private void writeObject(ObjectOutputStream stream) throws IOException {
        stream.defaultWriteObject();
        stream.writeInt(rowCount);
        for (Object[] column : this.data) {
            stream.writeBoolean(column != null);
            if (column != null) {
                stream.writeInt(columnCount);
                for (Object writeSerializedData : column) {
                    writeSerializedData(stream, writeSerializedData);
                }
            }
        }
    }

    protected void writeSerializedData(ObjectOutputStream stream, Object o) throws IOException {
        stream.writeObject(o);
    }

    private void readObject(ObjectInputStream stream) throws IOException, ClassNotFoundException {
        stream.defaultReadObject();
        int rowCount = stream.readInt();
        this.data = new Object[rowCount][];
        for (int r = 0; r < rowCount; r++) {
            if (stream.readBoolean()) {
                int columnCount = stream.readInt();
                Object[] column = new Object[columnCount];
                this.data[r] = column;
                for (int c = 0; c < columnCount; c++) {
                    column[c] = readSerializedData(stream);
                }
            }
        }
    }

    protected Object readSerializedData(ObjectInputStream stream) throws ClassNotFoundException, IOException {
        return stream.readObject();
    }

    public void clear() {
        this.rows = 0;
        this.columns = 0;
        for (int i = 0; i < this.data.length; i++) {
            if (this.data[i] != null) {
                Arrays.fill(this.data[i], null);
            }
        }
    }

    protected void copyColumn(int oldColumn, int newColumn) {
        for (int i = 0; i < getRowCount(); i++) {
            setObject(i, newColumn, getObject(i, oldColumn));
        }
    }

    protected void copyRow(int oldRow, int newRow) {
        ensureCapacity(newRow, getColumnCount());
        Object[] oldRowStorage = this.data[oldRow];
        if (oldRowStorage == null) {
            Object[] newRowStorage = this.data[newRow];
            if (newRowStorage != null) {
                Arrays.fill(newRowStorage, null);
                return;
            }
            return;
        }
        this.data[newRow] = (Object[]) oldRowStorage.clone();
    }

    protected void setData(Object[][] data, int colCount) {
        if (data == null) {
            throw new NullPointerException();
        } else if (colCount < 0) {
            throw new IndexOutOfBoundsException();
        } else {
            this.data = data;
            this.rows = data.length;
            this.columns = colCount;
        }
    }

    protected Object[][] getData() {
        return this.data;
    }
}
