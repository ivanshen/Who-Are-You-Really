package org.jfree.chart.urls;

import org.jfree.data.category.CategoryDataset;

public interface CategoryURLGenerator {
    String generateURL(CategoryDataset categoryDataset, int i, int i2);
}
