package org.jfree.data.gantt;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import org.jfree.chart.urls.StandardXYURLGenerator;
import org.jfree.chart.util.ParamChecks;
import org.jfree.data.general.AbstractSeriesDataset;
import org.jfree.data.general.SeriesChangeEvent;
import org.jfree.data.time.TimePeriod;
import org.jfree.util.ObjectUtilities;
import org.jfree.util.PublicCloneable;

public class TaskSeriesCollection extends AbstractSeriesDataset implements GanttCategoryDataset, Cloneable, PublicCloneable, Serializable {
    private static final long serialVersionUID = -2065799050738449903L;
    private List data;
    private List keys;

    public TaskSeriesCollection() {
        this.keys = new ArrayList();
        this.data = new ArrayList();
    }

    public TaskSeries getSeries(Comparable key) {
        if (key == null) {
            throw new NullPointerException("Null 'key' argument.");
        }
        int index = getRowIndex(key);
        if (index >= 0) {
            return getSeries(index);
        }
        return null;
    }

    public TaskSeries getSeries(int series) {
        if (series >= 0 && series < getSeriesCount()) {
            return (TaskSeries) this.data.get(series);
        }
        throw new IllegalArgumentException("Series index out of bounds");
    }

    public int getSeriesCount() {
        return getRowCount();
    }

    public Comparable getSeriesKey(int series) {
        return ((TaskSeries) this.data.get(series)).getKey();
    }

    public int getRowCount() {
        return this.data.size();
    }

    public List getRowKeys() {
        return this.data;
    }

    public int getColumnCount() {
        return this.keys.size();
    }

    public List getColumnKeys() {
        return this.keys;
    }

    public Comparable getColumnKey(int index) {
        return (Comparable) this.keys.get(index);
    }

    public int getColumnIndex(Comparable columnKey) {
        ParamChecks.nullNotPermitted(columnKey, "columnKey");
        return this.keys.indexOf(columnKey);
    }

    public int getRowIndex(Comparable rowKey) {
        int count = this.data.size();
        for (int i = 0; i < count; i++) {
            if (((TaskSeries) this.data.get(i)).getKey().equals(rowKey)) {
                return i;
            }
        }
        return -1;
    }

    public Comparable getRowKey(int index) {
        return ((TaskSeries) this.data.get(index)).getKey();
    }

    public void add(TaskSeries series) {
        ParamChecks.nullNotPermitted(series, StandardXYURLGenerator.DEFAULT_SERIES_PARAMETER);
        this.data.add(series);
        series.addChangeListener(this);
        for (Task task : series.getTasks()) {
            String key = task.getDescription();
            if (this.keys.indexOf(key) < 0) {
                this.keys.add(key);
            }
        }
        fireDatasetChanged();
    }

    public void remove(TaskSeries series) {
        ParamChecks.nullNotPermitted(series, StandardXYURLGenerator.DEFAULT_SERIES_PARAMETER);
        if (this.data.contains(series)) {
            series.removeChangeListener(this);
            this.data.remove(series);
            fireDatasetChanged();
        }
    }

    public void remove(int series) {
        if (series < 0 || series >= getSeriesCount()) {
            throw new IllegalArgumentException("TaskSeriesCollection.remove(): index outside valid range.");
        }
        ((TaskSeries) this.data.get(series)).removeChangeListener(this);
        this.data.remove(series);
        fireDatasetChanged();
    }

    public void removeAll() {
        for (TaskSeries series : this.data) {
            series.removeChangeListener(this);
        }
        this.data.clear();
        fireDatasetChanged();
    }

    public Number getValue(Comparable rowKey, Comparable columnKey) {
        return getStartValue(rowKey, columnKey);
    }

    public Number getValue(int row, int column) {
        return getStartValue(row, column);
    }

    public Number getStartValue(Comparable rowKey, Comparable columnKey) {
        Task task = ((TaskSeries) this.data.get(getRowIndex(rowKey))).get(columnKey.toString());
        if (task == null) {
            return null;
        }
        TimePeriod duration = task.getDuration();
        if (duration != null) {
            return new Long(duration.getStart().getTime());
        }
        return null;
    }

    public Number getStartValue(int row, int column) {
        return getStartValue(getRowKey(row), getColumnKey(column));
    }

    public Number getEndValue(Comparable rowKey, Comparable columnKey) {
        Task task = ((TaskSeries) this.data.get(getRowIndex(rowKey))).get(columnKey.toString());
        if (task == null) {
            return null;
        }
        TimePeriod duration = task.getDuration();
        if (duration != null) {
            return new Long(duration.getEnd().getTime());
        }
        return null;
    }

    public Number getEndValue(int row, int column) {
        return getEndValue(getRowKey(row), getColumnKey(column));
    }

    public Number getPercentComplete(int row, int column) {
        return getPercentComplete(getRowKey(row), getColumnKey(column));
    }

    public Number getPercentComplete(Comparable rowKey, Comparable columnKey) {
        Task task = ((TaskSeries) this.data.get(getRowIndex(rowKey))).get(columnKey.toString());
        if (task != null) {
            return task.getPercentComplete();
        }
        return null;
    }

    public int getSubIntervalCount(int row, int column) {
        return getSubIntervalCount(getRowKey(row), getColumnKey(column));
    }

    public int getSubIntervalCount(Comparable rowKey, Comparable columnKey) {
        Task task = ((TaskSeries) this.data.get(getRowIndex(rowKey))).get(columnKey.toString());
        if (task != null) {
            return task.getSubtaskCount();
        }
        return 0;
    }

    public Number getStartValue(int row, int column, int subinterval) {
        return getStartValue(getRowKey(row), getColumnKey(column), subinterval);
    }

    public Number getStartValue(Comparable rowKey, Comparable columnKey, int subinterval) {
        Task task = ((TaskSeries) this.data.get(getRowIndex(rowKey))).get(columnKey.toString());
        if (task == null) {
            return null;
        }
        Task sub = task.getSubtask(subinterval);
        if (sub != null) {
            return new Long(sub.getDuration().getStart().getTime());
        }
        return null;
    }

    public Number getEndValue(int row, int column, int subinterval) {
        return getEndValue(getRowKey(row), getColumnKey(column), subinterval);
    }

    public Number getEndValue(Comparable rowKey, Comparable columnKey, int subinterval) {
        Task task = ((TaskSeries) this.data.get(getRowIndex(rowKey))).get(columnKey.toString());
        if (task == null) {
            return null;
        }
        Task sub = task.getSubtask(subinterval);
        if (sub != null) {
            return new Long(sub.getDuration().getEnd().getTime());
        }
        return null;
    }

    public Number getPercentComplete(int row, int column, int subinterval) {
        return getPercentComplete(getRowKey(row), getColumnKey(column), subinterval);
    }

    public Number getPercentComplete(Comparable rowKey, Comparable columnKey, int subinterval) {
        Task task = ((TaskSeries) this.data.get(getRowIndex(rowKey))).get(columnKey.toString());
        if (task == null) {
            return null;
        }
        Task sub = task.getSubtask(subinterval);
        if (sub != null) {
            return sub.getPercentComplete();
        }
        return null;
    }

    public void seriesChanged(SeriesChangeEvent event) {
        refreshKeys();
        fireDatasetChanged();
    }

    private void refreshKeys() {
        this.keys.clear();
        for (int i = 0; i < getSeriesCount(); i++) {
            for (Task task : ((TaskSeries) this.data.get(i)).getTasks()) {
                String key = task.getDescription();
                if (this.keys.indexOf(key) < 0) {
                    this.keys.add(key);
                }
            }
        }
    }

    public boolean equals(Object obj) {
        if (obj == this) {
            return true;
        }
        if (!(obj instanceof TaskSeriesCollection)) {
            return false;
        }
        if (ObjectUtilities.equal(this.data, ((TaskSeriesCollection) obj).data)) {
            return true;
        }
        return false;
    }

    public Object clone() throws CloneNotSupportedException {
        TaskSeriesCollection clone = (TaskSeriesCollection) super.clone();
        clone.data = (List) ObjectUtilities.deepClone(this.data);
        clone.keys = new ArrayList(this.keys);
        return clone;
    }
}
