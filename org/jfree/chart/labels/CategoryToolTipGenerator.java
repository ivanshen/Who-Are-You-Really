package org.jfree.chart.labels;

import org.jfree.data.category.CategoryDataset;

public interface CategoryToolTipGenerator {
    String generateToolTip(CategoryDataset categoryDataset, int i, int i2);
}
