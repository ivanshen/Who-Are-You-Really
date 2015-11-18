package org.jfree.data.contour;

import org.jfree.data.Range;
import org.jfree.data.xy.XYZDataset;

public interface ContourDataset extends XYZDataset {
    double getMaxZValue();

    double getMinZValue();

    int[] getXIndices();

    Number[] getXValues();

    Number[] getYValues();

    Range getZValueRange(Range range, Range range2);

    Number[] getZValues();

    int[] indexX();

    boolean isDateAxis(int i);
}
