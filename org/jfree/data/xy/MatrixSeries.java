package org.jfree.data.xy;

import java.io.Serializable;
import java.lang.reflect.Array;
import org.jfree.data.general.Series;

public class MatrixSeries extends Series implements Serializable {
    private static final long serialVersionUID = 7934188527308315704L;
    protected double[][] data;

    public MatrixSeries(String name, int rows, int columns) {
        super(name);
        this.data = (double[][]) Array.newInstance(Double.TYPE, new int[]{rows, columns});
        zeroAll();
    }

    public int getColumnsCount() {
        return this.data[0].length;
    }

    public Number getItem(int itemIndex) {
        return new Double(get(getItemRow(itemIndex), getItemColumn(itemIndex)));
    }

    public int getItemColumn(int itemIndex) {
        return itemIndex % getColumnsCount();
    }

    public int getItemCount() {
        return getRowCount() * getColumnsCount();
    }

    public int getItemRow(int itemIndex) {
        return itemIndex / getColumnsCount();
    }

    public int getRowCount() {
        return this.data.length;
    }

    public double get(int i, int j) {
        return this.data[i][j];
    }

    public void update(int i, int j, double mij) {
        this.data[i][j] = mij;
        fireSeriesChanged();
    }

    public void zeroAll() {
        int rows = getRowCount();
        int columns = getColumnsCount();
        for (int row = 0; row < rows; row++) {
            for (int column = 0; column < columns; column++) {
                this.data[row][column] = 0.0d;
            }
        }
        fireSeriesChanged();
    }

    public boolean equals(Object obj) {
        if (obj == this) {
            return true;
        }
        if (!(obj instanceof MatrixSeries)) {
            return false;
        }
        MatrixSeries that = (MatrixSeries) obj;
        if (getRowCount() != that.getRowCount() || getColumnsCount() != that.getColumnsCount()) {
            return false;
        }
        for (int r = 0; r < getRowCount(); r++) {
            for (int c = 0; c < getColumnsCount(); c++) {
                if (get(r, c) != that.get(r, c)) {
                    return false;
                }
            }
        }
        return super.equals(obj);
    }
}
