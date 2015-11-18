package org.jfree.chart.labels;

import org.jfree.data.general.PieDataset;

public interface PieToolTipGenerator {
    String generateToolTip(PieDataset pieDataset, Comparable comparable);
}
