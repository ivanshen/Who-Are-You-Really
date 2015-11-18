package org.jfree.chart.urls;

import org.jfree.data.xy.XYZDataset;

public interface XYZURLGenerator extends XYURLGenerator {
    String generateURL(XYZDataset xYZDataset, int i, int i2);
}
