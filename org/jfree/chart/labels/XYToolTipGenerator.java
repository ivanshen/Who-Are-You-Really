package org.jfree.chart.labels;

import org.jfree.data.xy.XYDataset;

public interface XYToolTipGenerator {
    String generateToolTip(XYDataset xYDataset, int i, int i2);
}
