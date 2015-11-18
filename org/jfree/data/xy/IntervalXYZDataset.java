package org.jfree.data.xy;

public interface IntervalXYZDataset extends XYZDataset {
    Number getEndXValue(int i, int i2);

    Number getEndYValue(int i, int i2);

    Number getEndZValue(int i, int i2);

    Number getStartXValue(int i, int i2);

    Number getStartYValue(int i, int i2);

    Number getStartZValue(int i, int i2);
}
