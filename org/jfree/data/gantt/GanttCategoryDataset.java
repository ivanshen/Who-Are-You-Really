package org.jfree.data.gantt;

import org.jfree.data.category.IntervalCategoryDataset;

public interface GanttCategoryDataset extends IntervalCategoryDataset {
    Number getEndValue(int i, int i2, int i3);

    Number getEndValue(Comparable comparable, Comparable comparable2, int i);

    Number getPercentComplete(int i, int i2);

    Number getPercentComplete(int i, int i2, int i3);

    Number getPercentComplete(Comparable comparable, Comparable comparable2);

    Number getPercentComplete(Comparable comparable, Comparable comparable2, int i);

    Number getStartValue(int i, int i2, int i3);

    Number getStartValue(Comparable comparable, Comparable comparable2, int i);

    int getSubIntervalCount(int i, int i2);

    int getSubIntervalCount(Comparable comparable, Comparable comparable2);
}
