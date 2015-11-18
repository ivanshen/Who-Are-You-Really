package org.jfree.chart.urls;

import org.jfree.data.general.PieDataset;

public interface PieURLGenerator {
    String generateURL(PieDataset pieDataset, Comparable comparable, int i);
}
