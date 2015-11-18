package org.jfree.data.statistics;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import org.jfree.chart.axis.DateAxis;
import org.jfree.chart.util.ParamChecks;

public abstract class Statistics {
    public static double calculateMean(Number[] values) {
        return calculateMean(values, true);
    }

    public static double calculateMean(Number[] values, boolean includeNullAndNaN) {
        ParamChecks.nullNotPermitted(values, "values");
        double sum = 0.0d;
        int counter = 0;
        for (int i = 0; i < values.length; i++) {
            double current;
            if (values[i] != null) {
                current = values[i].doubleValue();
            } else {
                current = Double.NaN;
            }
            if (includeNullAndNaN || !Double.isNaN(current)) {
                sum += current;
                counter++;
            }
        }
        return sum / ((double) counter);
    }

    public static double calculateMean(Collection values) {
        return calculateMean(values, true);
    }

    public static double calculateMean(Collection values, boolean includeNullAndNaN) {
        ParamChecks.nullNotPermitted(values, "values");
        int count = 0;
        double total = 0.0d;
        for (Number object : values) {
            if (object == null) {
                if (includeNullAndNaN) {
                    return Double.NaN;
                }
            } else if (object instanceof Number) {
                Number number = object;
                if (!Double.isNaN(number.doubleValue())) {
                    total += number.doubleValue();
                    count++;
                } else if (includeNullAndNaN) {
                    return Double.NaN;
                }
            } else {
                continue;
            }
        }
        return total / ((double) count);
    }

    public static double calculateMedian(List values) {
        return calculateMedian(values, true);
    }

    public static double calculateMedian(List values, boolean copyAndSort) {
        if (values == null) {
            return Double.NaN;
        }
        if (copyAndSort) {
            int itemCount = values.size();
            List copy = new ArrayList(itemCount);
            for (int i = 0; i < itemCount; i++) {
                copy.add(i, values.get(i));
            }
            Collections.sort(copy);
            values = copy;
        }
        int count = values.size();
        if (count <= 0) {
            return Double.NaN;
        }
        if (count % 2 != 1) {
            return (((Number) values.get((count / 2) - 1)).doubleValue() + ((Number) values.get(count / 2)).doubleValue()) / DateAxis.DEFAULT_AUTO_RANGE_MINIMUM_SIZE_IN_MILLISECONDS;
        } else if (count > 1) {
            return ((Number) values.get((count - 1) / 2)).doubleValue();
        } else {
            return ((Number) values.get(0)).doubleValue();
        }
    }

    public static double calculateMedian(List values, int start, int end) {
        return calculateMedian(values, start, end, true);
    }

    public static double calculateMedian(List values, int start, int end, boolean copyAndSort) {
        if (copyAndSort) {
            List working = new ArrayList((end - start) + 1);
            for (int i = start; i <= end; i++) {
                working.add(values.get(i));
            }
            Collections.sort(working);
            return calculateMedian(working, false);
        }
        int count = (end - start) + 1;
        if (count <= 0) {
            return Double.NaN;
        }
        if (count % 2 != 1) {
            return (((Number) values.get(((count / 2) + start) - 1)).doubleValue() + ((Number) values.get((count / 2) + start)).doubleValue()) / DateAxis.DEFAULT_AUTO_RANGE_MINIMUM_SIZE_IN_MILLISECONDS;
        } else if (count > 1) {
            return ((Number) values.get(((count - 1) / 2) + start)).doubleValue();
        } else {
            return ((Number) values.get(start)).doubleValue();
        }
    }

    public static double getStdDev(Number[] data) {
        ParamChecks.nullNotPermitted(data, "data");
        if (data.length == 0) {
            throw new IllegalArgumentException("Zero length 'data' array.");
        }
        double avg = calculateMean(data);
        double sum = 0.0d;
        for (Number doubleValue : data) {
            double diff = doubleValue.doubleValue() - avg;
            sum += diff * diff;
        }
        return Math.sqrt(sum / ((double) (data.length - 1)));
    }

    public static double[] getLinearFit(Number[] xData, Number[] yData) {
        ParamChecks.nullNotPermitted(xData, "xData");
        ParamChecks.nullNotPermitted(yData, "yData");
        if (xData.length != yData.length) {
            throw new IllegalArgumentException("Statistics.getLinearFit(): array lengths must be equal.");
        }
        return new double[]{getSlope(xData, yData), calculateMean(yData) - (result[1] * calculateMean(xData))};
    }

    public static double getSlope(Number[] xData, Number[] yData) {
        ParamChecks.nullNotPermitted(xData, "xData");
        ParamChecks.nullNotPermitted(yData, "yData");
        if (xData.length != yData.length) {
            throw new IllegalArgumentException("Array lengths must be equal.");
        }
        double sx = 0.0d;
        double sxx = 0.0d;
        double sxy = 0.0d;
        double sy = 0.0d;
        int counter = 0;
        while (counter < xData.length) {
            sx += xData[counter].doubleValue();
            sxx += Math.pow(xData[counter].doubleValue(), DateAxis.DEFAULT_AUTO_RANGE_MINIMUM_SIZE_IN_MILLISECONDS);
            sxy += yData[counter].doubleValue() * xData[counter].doubleValue();
            sy += yData[counter].doubleValue();
            counter++;
        }
        return (sxy - ((sx * sy) / ((double) counter))) / (sxx - ((sx * sx) / ((double) counter)));
    }

    public static double getCorrelation(Number[] data1, Number[] data2) {
        ParamChecks.nullNotPermitted(data1, "data1");
        ParamChecks.nullNotPermitted(data2, "data2");
        if (data1.length != data2.length) {
            throw new IllegalArgumentException("'data1' and 'data2' arrays must have same length.");
        }
        int n = data1.length;
        double sumX = 0.0d;
        double sumY = 0.0d;
        double sumX2 = 0.0d;
        double sumY2 = 0.0d;
        double sumXY = 0.0d;
        for (int i = 0; i < n; i++) {
            double x = 0.0d;
            if (data1[i] != null) {
                x = data1[i].doubleValue();
            }
            double y = 0.0d;
            if (data2[i] != null) {
                y = data2[i].doubleValue();
            }
            sumX += x;
            sumY += y;
            sumXY += x * y;
            sumX2 += x * x;
            sumY2 += y * y;
        }
        return ((((double) n) * sumXY) - (sumX * sumY)) / Math.pow(((((double) n) * sumX2) - (sumX * sumX)) * ((((double) n) * sumY2) - (sumY * sumY)), 0.5d);
    }

    public static double[][] getMovingAverage(Number[] xData, Number[] yData, int period) {
        if (xData.length != yData.length) {
            throw new IllegalArgumentException("Array lengths must be equal.");
        } else if (period > xData.length) {
            throw new IllegalArgumentException("Period can't be longer than dataset.");
        } else {
            double[][] result = (double[][]) Array.newInstance(Double.TYPE, new int[]{xData.length - period, 2});
            for (int i = 0; i < result.length; i++) {
                result[i][0] = xData[i + period].doubleValue();
                double sum = 0.0d;
                for (int j = 0; j < period; j++) {
                    sum += yData[i + j].doubleValue();
                }
                result[i][1] = sum / ((double) period);
            }
            return result;
        }
    }
}
