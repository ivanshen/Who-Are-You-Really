package org.jfree.data.general;

public interface HeatMapDataset {
    double getMaximumXValue();

    double getMaximumYValue();

    double getMinimumXValue();

    double getMinimumYValue();

    int getXSampleCount();

    double getXValue(int i);

    int getYSampleCount();

    double getYValue(int i);

    Number getZ(int i, int i2);

    double getZValue(int i, int i2);
}
