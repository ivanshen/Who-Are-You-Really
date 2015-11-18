package org.jfree.data.xy;

public interface IntervalXYDataset extends XYDataset {
    Number getEndX(int i, int i2);

    double getEndXValue(int i, int i2);

    Number getEndY(int i, int i2);

    double getEndYValue(int i, int i2);

    Number getStartX(int i, int i2);

    double getStartXValue(int i, int i2);

    Number getStartY(int i, int i2);

    double getStartYValue(int i, int i2);
}
