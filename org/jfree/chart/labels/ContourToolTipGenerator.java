package org.jfree.chart.labels;

import org.jfree.data.contour.ContourDataset;

public interface ContourToolTipGenerator {
    String generateToolTip(ContourDataset contourDataset, int i);
}
