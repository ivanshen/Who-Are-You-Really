package org.jfree.data.statistics;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.jfree.chart.axis.DateAxis;
import org.jfree.chart.util.ParamChecks;
import org.jfree.data.xy.AbstractIntervalXYDataset;
import org.jfree.data.xy.IntervalXYDataset;
import org.jfree.util.ObjectUtilities;
import org.jfree.util.PublicCloneable;

public class HistogramDataset extends AbstractIntervalXYDataset implements IntervalXYDataset, Cloneable, PublicCloneable, Serializable {
    private static final long serialVersionUID = -6341668077370231153L;
    private List list;
    private HistogramType type;

    public HistogramDataset() {
        this.list = new ArrayList();
        this.type = HistogramType.FREQUENCY;
    }

    public HistogramType getType() {
        return this.type;
    }

    public void setType(HistogramType type) {
        ParamChecks.nullNotPermitted(type, "type");
        this.type = type;
        fireDatasetChanged();
    }

    public void addSeries(Comparable key, double[] values, int bins) {
        addSeries(key, values, bins, getMinimum(values), getMaximum(values));
    }

    public void addSeries(Comparable key, double[] values, int bins, double minimum, double maximum) {
        ParamChecks.nullNotPermitted(key, "key");
        ParamChecks.nullNotPermitted(values, "values");
        if (bins < 1) {
            throw new IllegalArgumentException("The 'bins' value must be at least 1.");
        }
        int i;
        double binWidth = (maximum - minimum) / ((double) bins);
        double lower = minimum;
        List binList = new ArrayList(bins);
        for (i = 0; i < bins; i++) {
            HistogramBin bin;
            if (i == bins - 1) {
                bin = new HistogramBin(lower, maximum);
            } else {
                double upper = minimum + (((double) (i + 1)) * binWidth);
                bin = new HistogramBin(lower, upper);
                lower = upper;
            }
            binList.add(bin);
        }
        for (i = 0; i < values.length; i++) {
            int binIndex = bins - 1;
            if (values[i] < maximum) {
                double fraction = (values[i] - minimum) / (maximum - minimum);
                if (fraction < 0.0d) {
                    fraction = 0.0d;
                }
                binIndex = (int) (((double) bins) * fraction);
                if (binIndex >= bins) {
                    binIndex = bins - 1;
                }
            }
            ((HistogramBin) binList.get(binIndex)).incrementCount();
        }
        Map map = new HashMap();
        map.put("key", key);
        map.put("bins", binList);
        map.put("values.length", new Integer(values.length));
        map.put("bin width", new Double(binWidth));
        this.list.add(map);
        fireDatasetChanged();
    }

    private double getMinimum(double[] values) {
        if (values == null || values.length < 1) {
            throw new IllegalArgumentException("Null or zero length 'values' argument.");
        }
        double min = Double.MAX_VALUE;
        for (int i = 0; i < values.length; i++) {
            if (values[i] < min) {
                min = values[i];
            }
        }
        return min;
    }

    private double getMaximum(double[] values) {
        if (values == null || values.length < 1) {
            throw new IllegalArgumentException("Null or zero length 'values' argument.");
        }
        double max = -1.7976931348623157E308d;
        for (int i = 0; i < values.length; i++) {
            if (values[i] > max) {
                max = values[i];
            }
        }
        return max;
    }

    List getBins(int series) {
        return (List) ((Map) this.list.get(series)).get("bins");
    }

    private int getTotal(int series) {
        return ((Integer) ((Map) this.list.get(series)).get("values.length")).intValue();
    }

    private double getBinWidth(int series) {
        return ((Double) ((Map) this.list.get(series)).get("bin width")).doubleValue();
    }

    public int getSeriesCount() {
        return this.list.size();
    }

    public Comparable getSeriesKey(int series) {
        return (Comparable) ((Map) this.list.get(series)).get("key");
    }

    public int getItemCount(int series) {
        return getBins(series).size();
    }

    public Number getX(int series, int item) {
        HistogramBin bin = (HistogramBin) getBins(series).get(item);
        return new Double((bin.getStartBoundary() + bin.getEndBoundary()) / DateAxis.DEFAULT_AUTO_RANGE_MINIMUM_SIZE_IN_MILLISECONDS);
    }

    public Number getY(int series, int item) {
        HistogramBin bin = (HistogramBin) getBins(series).get(item);
        double total = (double) getTotal(series);
        double binWidth = getBinWidth(series);
        if (this.type == HistogramType.FREQUENCY) {
            return new Double((double) bin.getCount());
        }
        if (this.type == HistogramType.RELATIVE_FREQUENCY) {
            return new Double(((double) bin.getCount()) / total);
        }
        if (this.type == HistogramType.SCALE_AREA_TO_1) {
            return new Double(((double) bin.getCount()) / (binWidth * total));
        }
        throw new IllegalStateException();
    }

    public Number getStartX(int series, int item) {
        return new Double(((HistogramBin) getBins(series).get(item)).getStartBoundary());
    }

    public Number getEndX(int series, int item) {
        return new Double(((HistogramBin) getBins(series).get(item)).getEndBoundary());
    }

    public Number getStartY(int series, int item) {
        return getY(series, item);
    }

    public Number getEndY(int series, int item) {
        return getY(series, item);
    }

    public boolean equals(Object obj) {
        if (obj == this) {
            return true;
        }
        if (!(obj instanceof HistogramDataset)) {
            return false;
        }
        HistogramDataset that = (HistogramDataset) obj;
        if (!ObjectUtilities.equal(this.type, that.type)) {
            return false;
        }
        if (ObjectUtilities.equal(this.list, that.list)) {
            return true;
        }
        return false;
    }

    public Object clone() throws CloneNotSupportedException {
        HistogramDataset clone = (HistogramDataset) super.clone();
        int seriesCount = getSeriesCount();
        clone.list = new ArrayList(seriesCount);
        for (int i = 0; i < seriesCount; i++) {
            clone.list.add(new HashMap((Map) this.list.get(i)));
        }
        return clone;
    }
}
