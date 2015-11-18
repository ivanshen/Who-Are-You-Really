package org.jfree.data;

public interface RangeInfo {
    Range getRangeBounds(boolean z);

    double getRangeLowerBound(boolean z);

    double getRangeUpperBound(boolean z);
}
