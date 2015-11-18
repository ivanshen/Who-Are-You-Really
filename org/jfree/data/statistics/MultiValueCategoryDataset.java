package org.jfree.data.statistics;

import java.util.List;
import org.jfree.data.category.CategoryDataset;

public interface MultiValueCategoryDataset extends CategoryDataset {
    List getValues(int i, int i2);

    List getValues(Comparable comparable, Comparable comparable2);
}
