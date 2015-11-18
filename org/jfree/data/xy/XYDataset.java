package org.jfree.data.xy;

import org.jfree.data.DomainOrder;
import org.jfree.data.general.SeriesDataset;

public interface XYDataset extends SeriesDataset {
    DomainOrder getDomainOrder();

    int getItemCount(int i);

    Number getX(int i, int i2);

    double getXValue(int i, int i2);

    Number getY(int i, int i2);

    double getYValue(int i, int i2);
}
