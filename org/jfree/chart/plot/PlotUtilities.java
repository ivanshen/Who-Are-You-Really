package org.jfree.chart.plot;

import org.jfree.data.general.DatasetUtilities;

public class PlotUtilities {
    public static boolean isEmptyOrNull(XYPlot plot) {
        if (plot != null) {
            int n = plot.getDatasetCount();
            for (int i = 0; i < n; i++) {
                if (!DatasetUtilities.isEmptyOrNull(plot.getDataset(i))) {
                    return false;
                }
            }
        }
        return true;
    }
}
