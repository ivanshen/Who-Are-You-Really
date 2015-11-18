package org.jfree.chart.labels;

import org.jfree.data.xy.XYDataset;

public interface XYSeriesLabelGenerator {
    String generateLabel(XYDataset xYDataset, int i);
}
