package org.jfree.chart.labels;

import org.jfree.data.category.CategoryDataset;

public interface CategorySeriesLabelGenerator {
    String generateLabel(CategoryDataset categoryDataset, int i);
}
