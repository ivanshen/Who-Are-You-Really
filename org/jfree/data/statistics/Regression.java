package org.jfree.data.statistics;

import java.lang.reflect.Array;
import org.jfree.chart.axis.DateAxis;
import org.jfree.chart.util.ParamChecks;
import org.jfree.data.xy.NormalizedMatrixSeries;
import org.jfree.data.xy.XYDataset;

public abstract class Regression {
    public static double[] getOLSRegression(double[][] data) {
        int n = data.length;
        if (n < 2) {
            throw new IllegalArgumentException("Not enough data.");
        }
        double sumX = 0.0d;
        double sumY = 0.0d;
        double sumXX = 0.0d;
        double sumXY = 0.0d;
        for (int i = 0; i < n; i++) {
            double x = data[i][0];
            double y = data[i][1];
            sumX += x;
            sumY += y;
            sumXX += x * x;
            sumXY += x * y;
        }
        double sxx = sumXX - ((sumX * sumX) / ((double) n));
        double sxy = sumXY - ((sumX * sumY) / ((double) n));
        double xbar = sumX / ((double) n);
        double ybar = sumY / ((double) n);
        return new double[]{sxy / sxx, ybar - (result[1] * xbar)};
    }

    public static double[] getOLSRegression(XYDataset data, int series) {
        int n = data.getItemCount(series);
        if (n < 2) {
            throw new IllegalArgumentException("Not enough data.");
        }
        double sumX = 0.0d;
        double sumY = 0.0d;
        double sumXX = 0.0d;
        double sumXY = 0.0d;
        for (int i = 0; i < n; i++) {
            double x = data.getXValue(series, i);
            double y = data.getYValue(series, i);
            sumX += x;
            sumY += y;
            sumXX += x * x;
            sumXY += x * y;
        }
        double sxx = sumXX - ((sumX * sumX) / ((double) n));
        double sxy = sumXY - ((sumX * sumY) / ((double) n));
        double xbar = sumX / ((double) n);
        double ybar = sumY / ((double) n);
        return new double[]{sxy / sxx, ybar - (result[1] * xbar)};
    }

    public static double[] getPowerRegression(double[][] data) {
        int n = data.length;
        if (n < 2) {
            throw new IllegalArgumentException("Not enough data.");
        }
        double sumX = 0.0d;
        double sumY = 0.0d;
        double sumXX = 0.0d;
        double sumXY = 0.0d;
        for (int i = 0; i < n; i++) {
            double x = Math.log(data[i][0]);
            double y = Math.log(data[i][1]);
            sumX += x;
            sumY += y;
            sumXX += x * x;
            sumXY += x * y;
        }
        double sxx = sumXX - ((sumX * sumX) / ((double) n));
        double sxy = sumXY - ((sumX * sumY) / ((double) n));
        double xbar = sumX / ((double) n);
        double ybar = sumY / ((double) n);
        return new double[]{sxy / sxx, Math.pow(Math.exp(NormalizedMatrixSeries.DEFAULT_SCALE_FACTOR), ybar - (result[1] * xbar))};
    }

    public static double[] getPowerRegression(XYDataset data, int series) {
        int n = data.getItemCount(series);
        if (n < 2) {
            throw new IllegalArgumentException("Not enough data.");
        }
        double sumX = 0.0d;
        double sumY = 0.0d;
        double sumXX = 0.0d;
        double sumXY = 0.0d;
        for (int i = 0; i < n; i++) {
            double x = Math.log(data.getXValue(series, i));
            double y = Math.log(data.getYValue(series, i));
            sumX += x;
            sumY += y;
            sumXX += x * x;
            sumXY += x * y;
        }
        double sxx = sumXX - ((sumX * sumX) / ((double) n));
        double sxy = sumXY - ((sumX * sumY) / ((double) n));
        double xbar = sumX / ((double) n);
        double ybar = sumY / ((double) n);
        return new double[]{sxy / sxx, Math.pow(Math.exp(NormalizedMatrixSeries.DEFAULT_SCALE_FACTOR), ybar - (result[1] * xbar))};
    }

    public static double[] getPolynomialRegression(XYDataset dataset, int series, int order) {
        ParamChecks.nullNotPermitted(dataset, "dataset");
        int itemCount = dataset.getItemCount(series);
        if (itemCount < order + 1) {
            throw new IllegalArgumentException("Not enough data.");
        }
        int item;
        int validItems = 0;
        int[] iArr = new int[]{2, itemCount};
        double[][] data = (double[][]) Array.newInstance(Double.TYPE, iArr);
        for (item = 0; item < itemCount; item++) {
            double x = dataset.getXValue(series, item);
            double y = dataset.getYValue(series, item);
            if (!(Double.isNaN(x) || Double.isNaN(y))) {
                data[0][validItems] = x;
                data[1][validItems] = y;
                validItems++;
            }
        }
        if (validItems < order + 1) {
            throw new IllegalArgumentException("Not enough data.");
        }
        int eq;
        int coe;
        int equations = order + 1;
        int coefficients = order + 2;
        double[] result = new double[(equations + 1)];
        iArr = new int[]{equations, coefficients};
        double[][] matrix = (double[][]) Array.newInstance(Double.TYPE, iArr);
        double sumX = 0.0d;
        double sumY = 0.0d;
        for (item = 0; item < validItems; item++) {
            sumX += data[0][item];
            sumY += data[1][item];
            for (eq = 0; eq < equations; eq++) {
                double[] dArr;
                for (coe = 0; coe < coefficients - 1; coe++) {
                    dArr = matrix[eq];
                    dArr[coe] = dArr[coe] + Math.pow(data[0][item], (double) (eq + coe));
                }
                dArr = matrix[eq];
                int i = coefficients - 1;
                dArr[i] = dArr[i] + (data[1][item] * Math.pow(data[0][item], (double) eq));
            }
        }
        double[][] subMatrix = calculateSubMatrix(matrix);
        for (eq = 1; eq < equations; eq++) {
            matrix[eq][0] = 0.0d;
            for (coe = 1; coe < coefficients; coe++) {
                matrix[eq][coe] = subMatrix[eq - 1][coe - 1];
            }
        }
        for (eq = equations - 1; eq > -1; eq--) {
            double value = matrix[eq][coefficients - 1];
            for (coe = eq; coe < coefficients - 1; coe++) {
                value -= matrix[eq][coe] * result[coe];
            }
            result[eq] = value / matrix[eq][eq];
        }
        double meanY = sumY / ((double) validItems);
        double yObsSquare = 0.0d;
        double yRegSquare = 0.0d;
        for (item = 0; item < validItems; item++) {
            double yCalc = 0.0d;
            for (eq = 0; eq < equations; eq++) {
                yCalc += result[eq] * Math.pow(data[0][item], (double) eq);
            }
            yRegSquare += Math.pow(yCalc - meanY, DateAxis.DEFAULT_AUTO_RANGE_MINIMUM_SIZE_IN_MILLISECONDS);
            yObsSquare += Math.pow(data[1][item] - meanY, DateAxis.DEFAULT_AUTO_RANGE_MINIMUM_SIZE_IN_MILLISECONDS);
        }
        result[equations] = yRegSquare / yObsSquare;
        return result;
    }

    private static double[][] calculateSubMatrix(double[][] matrix) {
        int eq;
        int equations = matrix.length;
        int coefficients = matrix[0].length;
        double[][] result = (double[][]) Array.newInstance(Double.TYPE, new int[]{equations - 1, coefficients - 1});
        for (eq = 1; eq < equations; eq++) {
            int coe;
            double factor = matrix[0][0] / matrix[eq][0];
            for (coe = 1; coe < coefficients; coe++) {
                result[eq - 1][coe - 1] = matrix[0][coe] - (matrix[eq][coe] * factor);
            }
        }
        if (equations == 1) {
            return result;
        }
        if (result[0][0] == 0.0d) {
            boolean found = false;
            for (int i = 0; i < result.length; i++) {
                if (result[i][0] != 0.0d) {
                    found = true;
                    double[] temp = result[0];
                    System.arraycopy(result[i], 0, result[0], 0, result[i].length);
                    System.arraycopy(temp, 0, result[i], 0, temp.length);
                    break;
                }
            }
            if (!found) {
                return (double[][]) Array.newInstance(Double.TYPE, new int[]{equations - 1, coefficients - 1});
            }
        }
        double[][] subMatrix = calculateSubMatrix(result);
        for (eq = 1; eq < equations - 1; eq++) {
            result[eq][0] = 0.0d;
            for (coe = 1; coe < coefficients - 1; coe++) {
                result[eq][coe] = subMatrix[eq - 1][coe - 1];
            }
        }
        return result;
    }
}
