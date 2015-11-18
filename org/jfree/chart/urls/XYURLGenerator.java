package org.jfree.chart.urls;

import org.jfree.data.xy.XYDataset;

public interface XYURLGenerator {
    String generateURL(XYDataset xYDataset, int i, int i2);
}
