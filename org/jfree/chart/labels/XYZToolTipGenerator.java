package org.jfree.chart.labels;

import org.jfree.data.xy.XYZDataset;

public interface XYZToolTipGenerator extends XYToolTipGenerator {
    String generateToolTip(XYZDataset xYZDataset, int i, int i2);
}
