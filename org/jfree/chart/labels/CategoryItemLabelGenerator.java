package org.jfree.chart.labels;

import org.jfree.data.category.CategoryDataset;

public interface CategoryItemLabelGenerator {
    String generateColumnLabel(CategoryDataset categoryDataset, int i);

    String generateLabel(CategoryDataset categoryDataset, int i, int i2);

    String generateRowLabel(CategoryDataset categoryDataset, int i);
}
