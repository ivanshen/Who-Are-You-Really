package org.jfree.data.xy;

public abstract class AbstractXYZDataset extends AbstractXYDataset implements XYZDataset {
    public double getZValue(int series, int item) {
        Number z = getZ(series, item);
        if (z != null) {
            return z.doubleValue();
        }
        return Double.NaN;
    }
}
