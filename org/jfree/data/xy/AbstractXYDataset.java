package org.jfree.data.xy;

import org.jfree.data.DomainOrder;
import org.jfree.data.general.AbstractSeriesDataset;

public abstract class AbstractXYDataset extends AbstractSeriesDataset implements XYDataset {
    public DomainOrder getDomainOrder() {
        return DomainOrder.NONE;
    }

    public double getXValue(int series, int item) {
        Number x = getX(series, item);
        if (x != null) {
            return x.doubleValue();
        }
        return Double.NaN;
    }

    public double getYValue(int series, int item) {
        Number y = getY(series, item);
        if (y != null) {
            return y.doubleValue();
        }
        return Double.NaN;
    }
}
