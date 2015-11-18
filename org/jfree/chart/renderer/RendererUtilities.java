package org.jfree.chart.renderer;

import org.jfree.chart.util.ParamChecks;
import org.jfree.data.DomainOrder;
import org.jfree.data.xy.XYDataset;

public class RendererUtilities {
    public static int findLiveItemsLowerBound(XYDataset dataset, int series, double xLow, double xHigh) {
        ParamChecks.nullNotPermitted(dataset, "dataset");
        if (xLow >= xHigh) {
            throw new IllegalArgumentException("Requires xLow < xHigh.");
        }
        int itemCount = dataset.getItemCount(series);
        if (itemCount <= 1) {
            return 0;
        }
        int low;
        int high;
        int mid;
        if (dataset.getDomainOrder() == DomainOrder.ASCENDING) {
            low = 0;
            high = itemCount - 1;
            if (dataset.getXValue(series, 0) >= xLow) {
                return 0;
            }
            if (dataset.getXValue(series, high) < xLow) {
                return high;
            }
            while (high - low > 1) {
                mid = (low + high) / 2;
                if (dataset.getXValue(series, mid) >= xLow) {
                    high = mid;
                } else {
                    low = mid;
                }
            }
            return high;
        } else if (dataset.getDomainOrder() == DomainOrder.DESCENDING) {
            low = 0;
            high = itemCount - 1;
            if (dataset.getXValue(series, 0) <= xHigh) {
                return 0;
            }
            if (dataset.getXValue(series, high) > xHigh) {
                return high;
            }
            while (high - low > 1) {
                mid = (low + high) / 2;
                if (dataset.getXValue(series, mid) > xHigh) {
                    low = mid;
                } else {
                    high = mid;
                }
            }
            return high;
        } else {
            int index = 0;
            double x = dataset.getXValue(series, 0);
            while (index < itemCount && x < xLow) {
                index++;
                if (index < itemCount) {
                    x = dataset.getXValue(series, index);
                }
            }
            return Math.min(Math.max(0, index), itemCount - 1);
        }
    }

    public static int findLiveItemsUpperBound(XYDataset dataset, int series, double xLow, double xHigh) {
        ParamChecks.nullNotPermitted(dataset, "dataset");
        if (xLow >= xHigh) {
            throw new IllegalArgumentException("Requires xLow < xHigh.");
        }
        int itemCount = dataset.getItemCount(series);
        if (itemCount <= 1) {
            return 0;
        }
        int low;
        int high;
        int mid;
        if (dataset.getDomainOrder() == DomainOrder.ASCENDING) {
            low = 0;
            high = itemCount - 1;
            if (dataset.getXValue(series, 0) > xHigh) {
                return 0;
            }
            if (dataset.getXValue(series, high) <= xHigh) {
                return high;
            }
            mid = (0 + high) / 2;
            while (high - low > 1) {
                if (dataset.getXValue(series, mid) <= xHigh) {
                    low = mid;
                } else {
                    high = mid;
                }
                mid = (low + high) / 2;
            }
            return mid;
        } else if (dataset.getDomainOrder() == DomainOrder.DESCENDING) {
            low = 0;
            high = itemCount - 1;
            mid = (0 + high) / 2;
            if (dataset.getXValue(series, 0) < xLow) {
                return 0;
            }
            if (dataset.getXValue(series, high) >= xLow) {
                return high;
            }
            while (high - low > 1) {
                if (dataset.getXValue(series, mid) >= xLow) {
                    low = mid;
                } else {
                    high = mid;
                }
                mid = (low + high) / 2;
            }
            return mid;
        } else {
            int index = itemCount - 1;
            double x = dataset.getXValue(series, index);
            while (index >= 0 && x > xHigh) {
                index--;
                if (index >= 0) {
                    x = dataset.getXValue(series, index);
                }
            }
            return Math.max(index, 0);
        }
    }

    public static int[] findLiveItems(XYDataset dataset, int series, double xLow, double xHigh) {
        int i0 = findLiveItemsLowerBound(dataset, series, xLow, xHigh);
        int i1 = findLiveItemsUpperBound(dataset, series, xLow, xHigh);
        if (i0 > i1) {
            i0 = i1;
        }
        return new int[]{i0, i1};
    }
}
