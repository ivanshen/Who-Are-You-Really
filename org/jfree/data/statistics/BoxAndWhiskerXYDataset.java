package org.jfree.data.statistics;

import java.util.List;
import org.jfree.data.xy.XYDataset;

public interface BoxAndWhiskerXYDataset extends XYDataset {
    double getFaroutCoefficient();

    Number getMaxOutlier(int i, int i2);

    Number getMaxRegularValue(int i, int i2);

    Number getMeanValue(int i, int i2);

    Number getMedianValue(int i, int i2);

    Number getMinOutlier(int i, int i2);

    Number getMinRegularValue(int i, int i2);

    double getOutlierCoefficient();

    List getOutliers(int i, int i2);

    Number getQ1Value(int i, int i2);

    Number getQ3Value(int i, int i2);
}
