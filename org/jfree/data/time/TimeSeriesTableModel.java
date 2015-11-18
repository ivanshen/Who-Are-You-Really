package org.jfree.data.time;

import javax.swing.table.AbstractTableModel;
import org.jfree.data.general.SeriesChangeEvent;
import org.jfree.data.general.SeriesChangeListener;

public class TimeSeriesTableModel extends AbstractTableModel implements SeriesChangeListener {
    private boolean editable;
    private RegularTimePeriod newTimePeriod;
    private Number newValue;
    private TimeSeries series;

    public TimeSeriesTableModel() {
        this(new TimeSeries("Untitled"));
    }

    public TimeSeriesTableModel(TimeSeries series) {
        this(series, false);
    }

    public TimeSeriesTableModel(TimeSeries series, boolean editable) {
        this.series = series;
        this.series.addChangeListener(this);
        this.editable = editable;
    }

    public int getColumnCount() {
        return 2;
    }

    public Class getColumnClass(int column) {
        if (column == 0) {
            return String.class;
        }
        if (column == 1) {
            return Double.class;
        }
        return null;
    }

    public String getColumnName(int column) {
        if (column == 0) {
            return "Period:";
        }
        if (column == 1) {
            return "Value:";
        }
        return null;
    }

    public int getRowCount() {
        return this.series.getItemCount();
    }

    public Object getValueAt(int row, int column) {
        if (row < this.series.getItemCount()) {
            if (column == 0) {
                return this.series.getTimePeriod(row);
            }
            if (column == 1) {
                return this.series.getValue(row);
            }
            return null;
        } else if (column == 0) {
            return this.newTimePeriod;
        } else {
            if (column == 1) {
                return this.newValue;
            }
            return null;
        }
    }

    public boolean isCellEditable(int row, int column) {
        if (!this.editable) {
            return false;
        }
        if (column == 0 || column == 1) {
            return true;
        }
        return false;
    }

    public void setValueAt(Object value, int row, int column) {
        if (row < this.series.getItemCount()) {
            if (column == 1) {
                try {
                    this.series.update(row, Double.valueOf(value.toString()));
                } catch (NumberFormatException e) {
                    System.err.println("Number format exception");
                }
            }
        } else if (column == 0) {
            this.newTimePeriod = null;
        } else if (column == 1) {
            this.newValue = Double.valueOf(value.toString());
        }
    }

    public void seriesChanged(SeriesChangeEvent event) {
        fireTableDataChanged();
    }
}
