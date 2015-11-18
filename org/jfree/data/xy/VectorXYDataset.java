package org.jfree.data.xy;

public interface VectorXYDataset extends XYDataset {
    Vector getVector(int i, int i2);

    double getVectorXValue(int i, int i2);

    double getVectorYValue(int i, int i2);
}
