package org.jfree.data.statistics;

import org.jfree.data.category.CategoryDataset;

public interface StatisticalCategoryDataset extends CategoryDataset {
    Number getMeanValue(int i, int i2);

    Number getMeanValue(Comparable comparable, Comparable comparable2);

    Number getStdDevValue(int i, int i2);

    Number getStdDevValue(Comparable comparable, Comparable comparable2);
}
