package org.jfree.data.gantt;

import org.jfree.chart.axis.DateAxis;
import org.jfree.chart.util.ParamChecks;
import org.jfree.data.general.DatasetChangeEvent;
import org.jfree.data.general.DatasetChangeListener;
import org.jfree.data.time.TimePeriod;
import org.jfree.data.xy.AbstractXYDataset;
import org.jfree.data.xy.IntervalXYDataset;

public class XYTaskDataset extends AbstractXYDataset implements IntervalXYDataset, DatasetChangeListener {
    private double seriesWidth;
    private boolean transposed;
    private TaskSeriesCollection underlying;

    public XYTaskDataset(TaskSeriesCollection tasks) {
        ParamChecks.nullNotPermitted(tasks, "tasks");
        this.underlying = tasks;
        this.seriesWidth = 0.8d;
        this.underlying.addChangeListener(this);
    }

    public TaskSeriesCollection getTasks() {
        return this.underlying;
    }

    public double getSeriesWidth() {
        return this.seriesWidth;
    }

    public void setSeriesWidth(double w) {
        if (w <= 0.0d) {
            throw new IllegalArgumentException("Requires 'w' > 0.0.");
        }
        this.seriesWidth = w;
        fireDatasetChanged();
    }

    public boolean isTransposed() {
        return this.transposed;
    }

    public void setTransposed(boolean transposed) {
        this.transposed = transposed;
        fireDatasetChanged();
    }

    public int getSeriesCount() {
        return this.underlying.getSeriesCount();
    }

    public Comparable getSeriesKey(int series) {
        return this.underlying.getSeriesKey(series);
    }

    public int getItemCount(int series) {
        return this.underlying.getSeries(series).getItemCount();
    }

    public double getXValue(int series, int item) {
        if (this.transposed) {
            return getItemValue(series, item);
        }
        return getSeriesValue(series);
    }

    public double getStartXValue(int series, int item) {
        if (this.transposed) {
            return getItemStartValue(series, item);
        }
        return getSeriesStartValue(series);
    }

    public double getEndXValue(int series, int item) {
        if (this.transposed) {
            return getItemEndValue(series, item);
        }
        return getSeriesEndValue(series);
    }

    public Number getX(int series, int item) {
        return new Double(getXValue(series, item));
    }

    public Number getStartX(int series, int item) {
        return new Double(getStartXValue(series, item));
    }

    public Number getEndX(int series, int item) {
        return new Double(getEndXValue(series, item));
    }

    public double getYValue(int series, int item) {
        if (this.transposed) {
            return getSeriesValue(series);
        }
        return getItemValue(series, item);
    }

    public double getStartYValue(int series, int item) {
        if (this.transposed) {
            return getSeriesStartValue(series);
        }
        return getItemStartValue(series, item);
    }

    public double getEndYValue(int series, int item) {
        if (this.transposed) {
            return getSeriesEndValue(series);
        }
        return getItemEndValue(series, item);
    }

    public Number getY(int series, int item) {
        return new Double(getYValue(series, item));
    }

    public Number getStartY(int series, int item) {
        return new Double(getStartYValue(series, item));
    }

    public Number getEndY(int series, int item) {
        return new Double(getEndYValue(series, item));
    }

    private double getSeriesValue(int series) {
        return (double) series;
    }

    private double getSeriesStartValue(int series) {
        return ((double) series) - (this.seriesWidth / DateAxis.DEFAULT_AUTO_RANGE_MINIMUM_SIZE_IN_MILLISECONDS);
    }

    private double getSeriesEndValue(int series) {
        return ((double) series) + (this.seriesWidth / DateAxis.DEFAULT_AUTO_RANGE_MINIMUM_SIZE_IN_MILLISECONDS);
    }

    private double getItemValue(int series, int item) {
        TimePeriod duration = this.underlying.getSeries(series).get(item).getDuration();
        return ((double) (duration.getStart().getTime() + duration.getEnd().getTime())) / DateAxis.DEFAULT_AUTO_RANGE_MINIMUM_SIZE_IN_MILLISECONDS;
    }

    private double getItemStartValue(int series, int item) {
        return (double) this.underlying.getSeries(series).get(item).getDuration().getStart().getTime();
    }

    private double getItemEndValue(int series, int item) {
        return (double) this.underlying.getSeries(series).get(item).getDuration().getEnd().getTime();
    }

    public void datasetChanged(DatasetChangeEvent event) {
        fireDatasetChanged();
    }

    public boolean equals(Object obj) {
        if (obj == this) {
            return true;
        }
        if (!(obj instanceof XYTaskDataset)) {
            return false;
        }
        XYTaskDataset that = (XYTaskDataset) obj;
        if (this.seriesWidth != that.seriesWidth) {
            return false;
        }
        if (this.transposed != that.transposed) {
            return false;
        }
        if (this.underlying.equals(that.underlying)) {
            return true;
        }
        return false;
    }

    public Object clone() throws CloneNotSupportedException {
        XYTaskDataset clone = (XYTaskDataset) super.clone();
        clone.underlying = (TaskSeriesCollection) this.underlying.clone();
        return clone;
    }
}
