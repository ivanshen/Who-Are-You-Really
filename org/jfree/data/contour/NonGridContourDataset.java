package org.jfree.data.contour;

import org.jfree.data.Range;
import org.jfree.data.xy.NormalizedMatrixSeries;

public class NonGridContourDataset extends DefaultContourDataset {
    static final int DEFAULT_NUM_X = 50;
    static final int DEFAULT_NUM_Y = 50;
    static final int DEFAULT_POWER = 4;

    public NonGridContourDataset(String seriesName, Object[] xData, Object[] yData, Object[] zData) {
        super(seriesName, xData, yData, zData);
        buildGrid(DEFAULT_NUM_Y, DEFAULT_NUM_Y, DEFAULT_POWER);
    }

    public NonGridContourDataset(String seriesName, Object[] xData, Object[] yData, Object[] zData, int numX, int numY, int power) {
        super(seriesName, xData, yData, zData);
        buildGrid(numX, numY, power);
    }

    protected void buildGrid(int numX, int numY, int power) {
        int k;
        int numValues = numX * numY;
        double[] xGrid = new double[numValues];
        double[] yGrid = new double[numValues];
        double[] zGrid = new double[numValues];
        double xMin = 1.0E20d;
        for (Number doubleValue : this.xValues) {
            xMin = Math.min(xMin, doubleValue.doubleValue());
        }
        double xMax = -1.0E20d;
        for (Number doubleValue2 : this.xValues) {
            xMax = Math.max(xMax, doubleValue2.doubleValue());
        }
        double yMin = 1.0E20d;
        for (Number doubleValue22 : this.yValues) {
            yMin = Math.min(yMin, doubleValue22.doubleValue());
        }
        double yMax = -1.0E20d;
        for (Number doubleValue222 : this.yValues) {
            yMax = Math.max(yMax, doubleValue222.doubleValue());
        }
        Range range = new Range(xMin, xMax);
        range = new Range(yMin, yMax);
        range.getLength();
        range.getLength();
        double dxGrid = range.getLength() / ((double) (numX - 1));
        double dyGrid = range.getLength() / ((double) (numY - 1));
        double x = 0.0d;
        for (int i = 0; i < numX; i++) {
            if (i == 0) {
                x = xMin;
            } else {
                x += dxGrid;
            }
            double y = 0.0d;
            for (int j = 0; j < numY; j++) {
                k = (numY * i) + j;
                xGrid[k] = x;
                if (j == 0) {
                    y = yMin;
                } else {
                    y += dyGrid;
                }
                yGrid[k] = y;
            }
        }
        for (int kGrid = 0; kGrid < xGrid.length; kGrid++) {
            double dTotal = 0.0d;
            zGrid[kGrid] = 0.0d;
            for (k = 0; k < this.xValues.length; k++) {
                double d = distance(this.xValues[k].doubleValue(), this.yValues[k].doubleValue(), xGrid[kGrid], yGrid[kGrid]);
                if (power != 1) {
                    d = Math.pow(d, (double) power);
                }
                d = Math.sqrt(d);
                if (d > 0.0d) {
                    d = NormalizedMatrixSeries.DEFAULT_SCALE_FACTOR / d;
                } else {
                    d = 1.0E20d;
                }
                if (this.zValues[k] != null) {
                    zGrid[kGrid] = zGrid[kGrid] + (this.zValues[k].doubleValue() * d);
                }
                dTotal += d;
            }
            zGrid[kGrid] = zGrid[kGrid] / dTotal;
        }
        initialize(DefaultContourDataset.formObjectArray(xGrid), DefaultContourDataset.formObjectArray(yGrid), DefaultContourDataset.formObjectArray(zGrid));
    }

    protected double distance(double xDataPt, double yDataPt, double xGrdPt, double yGrdPt) {
        double dx = xDataPt - xGrdPt;
        double dy = yDataPt - yGrdPt;
        return Math.sqrt((dx * dx) + (dy * dy));
    }
}
