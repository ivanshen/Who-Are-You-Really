package org.jfree.data.xy;

public abstract class AbstractIntervalXYDataset extends AbstractXYDataset implements IntervalXYDataset {
    public double getStartXValue(int series, int item) {
        Number x = getStartX(series, item);
        if (x != null) {
            return x.doubleValue();
        }
        return Double.NaN;
    }

    public double getEndXValue(int series, int item) {
        Number x = getEndX(series, item);
        if (x != null) {
            return x.doubleValue();
        }
        return Double.NaN;
    }

    public double getStartYValue(int series, int item) {
        Number y = getStartY(series, item);
        if (y != null) {
            return y.doubleValue();
        }
        return Double.NaN;
    }

    public double getEndYValue(int series, int item) {
        Number y = getEndY(series, item);
        if (y != null) {
            return y.doubleValue();
        }
        return Double.NaN;
    }
}
