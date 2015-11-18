package org.jfree.data.xy;

import java.io.Serializable;
import org.jfree.chart.HashUtilities;
import org.jfree.chart.util.ParamChecks;
import org.jfree.data.DomainInfo;
import org.jfree.data.Range;
import org.jfree.data.general.DatasetChangeEvent;
import org.jfree.data.general.DatasetChangeListener;
import org.jfree.data.general.DatasetUtilities;
import org.jfree.util.PublicCloneable;

public class IntervalXYDelegate implements DatasetChangeListener, DomainInfo, Serializable, Cloneable, PublicCloneable {
    private static final long serialVersionUID = -685166711639592857L;
    private double autoIntervalWidth;
    private boolean autoWidth;
    private XYDataset dataset;
    private double fixedIntervalWidth;
    private double intervalPositionFactor;

    public IntervalXYDelegate(XYDataset dataset) {
        this(dataset, true);
    }

    public IntervalXYDelegate(XYDataset dataset, boolean autoWidth) {
        ParamChecks.nullNotPermitted(dataset, "dataset");
        this.dataset = dataset;
        this.autoWidth = autoWidth;
        this.intervalPositionFactor = 0.5d;
        this.autoIntervalWidth = Double.POSITIVE_INFINITY;
        this.fixedIntervalWidth = NormalizedMatrixSeries.DEFAULT_SCALE_FACTOR;
    }

    public boolean isAutoWidth() {
        return this.autoWidth;
    }

    public void setAutoWidth(boolean b) {
        this.autoWidth = b;
        if (b) {
            this.autoIntervalWidth = recalculateInterval();
        }
    }

    public double getIntervalPositionFactor() {
        return this.intervalPositionFactor;
    }

    public void setIntervalPositionFactor(double d) {
        if (d < 0.0d || NormalizedMatrixSeries.DEFAULT_SCALE_FACTOR < d) {
            throw new IllegalArgumentException("Argument 'd' outside valid range.");
        }
        this.intervalPositionFactor = d;
    }

    public double getFixedIntervalWidth() {
        return this.fixedIntervalWidth;
    }

    public void setFixedIntervalWidth(double w) {
        if (w < 0.0d) {
            throw new IllegalArgumentException("Negative 'w' argument.");
        }
        this.fixedIntervalWidth = w;
        this.autoWidth = false;
    }

    public double getIntervalWidth() {
        if (!isAutoWidth() || Double.isInfinite(this.autoIntervalWidth)) {
            return this.fixedIntervalWidth;
        }
        return this.autoIntervalWidth;
    }

    public Number getStartX(int series, int item) {
        Number x = this.dataset.getX(series, item);
        if (x != null) {
            return new Double(x.doubleValue() - (getIntervalPositionFactor() * getIntervalWidth()));
        }
        return null;
    }

    public double getStartXValue(int series, int item) {
        return this.dataset.getXValue(series, item) - (getIntervalPositionFactor() * getIntervalWidth());
    }

    public Number getEndX(int series, int item) {
        Number x = this.dataset.getX(series, item);
        if (x != null) {
            return new Double(x.doubleValue() + ((NormalizedMatrixSeries.DEFAULT_SCALE_FACTOR - getIntervalPositionFactor()) * getIntervalWidth()));
        }
        return null;
    }

    public double getEndXValue(int series, int item) {
        return this.dataset.getXValue(series, item) + ((NormalizedMatrixSeries.DEFAULT_SCALE_FACTOR - getIntervalPositionFactor()) * getIntervalWidth());
    }

    public double getDomainLowerBound(boolean includeInterval) {
        Range r = getDomainBounds(includeInterval);
        if (r != null) {
            return r.getLowerBound();
        }
        return Double.NaN;
    }

    public double getDomainUpperBound(boolean includeInterval) {
        Range r = getDomainBounds(includeInterval);
        if (r != null) {
            return r.getUpperBound();
        }
        return Double.NaN;
    }

    public Range getDomainBounds(boolean includeInterval) {
        Range range = DatasetUtilities.findDomainBounds(this.dataset, false);
        if (!includeInterval || range == null) {
            return range;
        }
        double lowerAdj = getIntervalWidth() * getIntervalPositionFactor();
        return new Range(range.getLowerBound() - lowerAdj, range.getUpperBound() + (getIntervalWidth() - lowerAdj));
    }

    public void datasetChanged(DatasetChangeEvent e) {
        if (this.autoWidth) {
            this.autoIntervalWidth = recalculateInterval();
        }
    }

    private double recalculateInterval() {
        double result = Double.POSITIVE_INFINITY;
        int seriesCount = this.dataset.getSeriesCount();
        for (int series = 0; series < seriesCount; series++) {
            result = Math.min(result, calculateIntervalForSeries(series));
        }
        return result;
    }

    private double calculateIntervalForSeries(int series) {
        double result = Double.POSITIVE_INFINITY;
        int itemCount = this.dataset.getItemCount(series);
        if (itemCount > 1) {
            double prev = this.dataset.getXValue(series, 0);
            for (int item = 1; item < itemCount; item++) {
                double x = this.dataset.getXValue(series, item);
                result = Math.min(result, x - prev);
                prev = x;
            }
        }
        return result;
    }

    public boolean equals(Object obj) {
        if (obj == this) {
            return true;
        }
        if (!(obj instanceof IntervalXYDelegate)) {
            return false;
        }
        IntervalXYDelegate that = (IntervalXYDelegate) obj;
        if (this.autoWidth != that.autoWidth) {
            return false;
        }
        if (this.intervalPositionFactor != that.intervalPositionFactor) {
            return false;
        }
        if (this.fixedIntervalWidth != that.fixedIntervalWidth) {
            return false;
        }
        return true;
    }

    public Object clone() throws CloneNotSupportedException {
        return super.clone();
    }

    public int hashCode() {
        return HashUtilities.hashCode(HashUtilities.hashCode(HashUtilities.hashCode(5, this.autoWidth), this.intervalPositionFactor), this.fixedIntervalWidth);
    }
}
