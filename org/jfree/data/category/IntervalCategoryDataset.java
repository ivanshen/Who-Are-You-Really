package org.jfree.data.category;

public interface IntervalCategoryDataset extends CategoryDataset {
    Number getEndValue(int i, int i2);

    Number getEndValue(Comparable comparable, Comparable comparable2);

    Number getStartValue(int i, int i2);

    Number getStartValue(Comparable comparable, Comparable comparable2);
}
