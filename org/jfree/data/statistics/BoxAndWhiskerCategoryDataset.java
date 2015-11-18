package org.jfree.data.statistics;

import java.util.List;
import org.jfree.data.category.CategoryDataset;

public interface BoxAndWhiskerCategoryDataset extends CategoryDataset {
    Number getMaxOutlier(int i, int i2);

    Number getMaxOutlier(Comparable comparable, Comparable comparable2);

    Number getMaxRegularValue(int i, int i2);

    Number getMaxRegularValue(Comparable comparable, Comparable comparable2);

    Number getMeanValue(int i, int i2);

    Number getMeanValue(Comparable comparable, Comparable comparable2);

    Number getMedianValue(int i, int i2);

    Number getMedianValue(Comparable comparable, Comparable comparable2);

    Number getMinOutlier(int i, int i2);

    Number getMinOutlier(Comparable comparable, Comparable comparable2);

    Number getMinRegularValue(int i, int i2);

    Number getMinRegularValue(Comparable comparable, Comparable comparable2);

    List getOutliers(int i, int i2);

    List getOutliers(Comparable comparable, Comparable comparable2);

    Number getQ1Value(int i, int i2);

    Number getQ1Value(Comparable comparable, Comparable comparable2);

    Number getQ3Value(int i, int i2);

    Number getQ3Value(Comparable comparable, Comparable comparable2);
}
