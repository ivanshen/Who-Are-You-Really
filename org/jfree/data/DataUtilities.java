package org.jfree.data;

import java.util.Arrays;
import org.jfree.chart.util.ParamChecks;

public abstract class DataUtilities {
    public static boolean equal(double[][] a, double[][] b) {
        boolean z = true;
        if (a == null) {
            if (b != null) {
                z = false;
            }
            return z;
        } else if (b == null || a.length != b.length) {
            return false;
        } else {
            for (int i = 0; i < a.length; i++) {
                if (!Arrays.equals(a[i], b[i])) {
                    return false;
                }
            }
            return true;
        }
    }

    public static double[][] clone(double[][] source) {
        ParamChecks.nullNotPermitted(source, "source");
        double[][] clone = new double[source.length][];
        for (int i = 0; i < source.length; i++) {
            if (source[i] != null) {
                double[] row = new double[source[i].length];
                System.arraycopy(source[i], 0, row, 0, source[i].length);
                clone[i] = row;
            }
        }
        return clone;
    }

    public static double calculateColumnTotal(Values2D data, int column) {
        ParamChecks.nullNotPermitted(data, "data");
        double total = 0.0d;
        int rowCount = data.getRowCount();
        for (int r = 0; r < rowCount; r++) {
            Number n = data.getValue(r, column);
            if (n != null) {
                total += n.doubleValue();
            }
        }
        return total;
    }

    public static double calculateColumnTotal(Values2D data, int column, int[] validRows) {
        ParamChecks.nullNotPermitted(data, "data");
        double total = 0.0d;
        int rowCount = data.getRowCount();
        for (int row : validRows) {
            if (row < rowCount) {
                Number n = data.getValue(row, column);
                if (n != null) {
                    total += n.doubleValue();
                }
            }
        }
        return total;
    }

    public static double calculateRowTotal(Values2D data, int row) {
        ParamChecks.nullNotPermitted(data, "data");
        double total = 0.0d;
        int columnCount = data.getColumnCount();
        for (int c = 0; c < columnCount; c++) {
            Number n = data.getValue(row, c);
            if (n != null) {
                total += n.doubleValue();
            }
        }
        return total;
    }

    public static double calculateRowTotal(Values2D data, int row, int[] validCols) {
        ParamChecks.nullNotPermitted(data, "data");
        double total = 0.0d;
        int colCount = data.getColumnCount();
        for (int col : validCols) {
            if (col < colCount) {
                Number n = data.getValue(row, col);
                if (n != null) {
                    total += n.doubleValue();
                }
            }
        }
        return total;
    }

    public static Number[] createNumberArray(double[] data) {
        ParamChecks.nullNotPermitted(data, "data");
        Number[] result = new Number[data.length];
        for (int i = 0; i < data.length; i++) {
            result[i] = new Double(data[i]);
        }
        return result;
    }

    public static Number[][] createNumberArray2D(double[][] data) {
        ParamChecks.nullNotPermitted(data, "data");
        int l1 = data.length;
        Number[][] result = new Number[l1][];
        for (int i = 0; i < l1; i++) {
            result[i] = createNumberArray(data[i]);
        }
        return result;
    }

    public static KeyedValues getCumulativePercentages(KeyedValues data) {
        int i;
        ParamChecks.nullNotPermitted(data, "data");
        DefaultKeyedValues result = new DefaultKeyedValues();
        double total = 0.0d;
        for (i = 0; i < data.getItemCount(); i++) {
            Number v = data.getValue(i);
            if (v != null) {
                total += v.doubleValue();
            }
        }
        double runningTotal = 0.0d;
        for (i = 0; i < data.getItemCount(); i++) {
            v = data.getValue(i);
            if (v != null) {
                runningTotal += v.doubleValue();
            }
            result.addValue(data.getKey(i), new Double(runningTotal / total));
        }
        return result;
    }
}
