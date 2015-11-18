package org.jfree.chart.labels;

import org.jfree.data.xy.XYDataset;

public interface XYItemLabelGenerator {
    String generateLabel(XYDataset xYDataset, int i, int i2);
}
