package org.jfree.ui;

import javax.swing.table.AbstractTableModel;

public abstract class SortableTableModel extends AbstractTableModel {
    private boolean ascending;
    private int sortingColumn;

    public SortableTableModel() {
        this.sortingColumn = -1;
        this.ascending = true;
    }

    public int getSortingColumn() {
        return this.sortingColumn;
    }

    public boolean isAscending() {
        return this.ascending;
    }

    public void setAscending(boolean flag) {
        this.ascending = flag;
    }

    public void sortByColumn(int column, boolean ascending) {
        if (isSortable(column)) {
            this.sortingColumn = column;
        }
    }

    public boolean isSortable(int column) {
        return false;
    }
}
