package org.jfree.chart.plot;

import org.jfree.data.Range;
import org.jfree.data.contour.ContourDataset;
import org.jfree.data.contour.DefaultContourDataset;

public abstract class ContourPlotUtilities {
    public static Range visibleRange(ContourDataset data, Range x, Range y) {
        return ((DefaultContourDataset) data).getZValueRange(x, y);
    }
}
