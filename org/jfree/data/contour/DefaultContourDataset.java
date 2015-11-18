package org.jfree.data.contour;

import java.lang.reflect.Array;
import java.util.Arrays;
import java.util.Date;
import java.util.Vector;
import org.jfree.data.Range;
import org.jfree.data.xy.AbstractXYZDataset;

public class DefaultContourDataset extends AbstractXYZDataset implements ContourDataset {
    boolean[] dateAxis;
    protected Comparable seriesKey;
    protected int[] xIndex;
    protected Number[] xValues;
    protected Number[] yValues;
    protected Number[] zValues;

    public DefaultContourDataset() {
        this.seriesKey = null;
        this.xValues = null;
        this.yValues = null;
        this.zValues = null;
        this.xIndex = null;
        this.dateAxis = new boolean[3];
    }

    public DefaultContourDataset(Comparable seriesKey, Object[] xData, Object[] yData, Object[] zData) {
        this.seriesKey = null;
        this.xValues = null;
        this.yValues = null;
        this.zValues = null;
        this.xIndex = null;
        this.dateAxis = new boolean[3];
        this.seriesKey = seriesKey;
        initialize(xData, yData, zData);
    }

    public void initialize(Object[] xData, Object[] yData, Object[] zData) {
        int k;
        this.xValues = new Double[xData.length];
        this.yValues = new Double[yData.length];
        this.zValues = new Double[zData.length];
        Vector tmpVector = new Vector();
        double x = 1.123452E31d;
        for (k = 0; k < this.xValues.length; k++) {
            if (xData[k] != null) {
                Number xNumber;
                if (xData[k] instanceof Number) {
                    xNumber = xData[k];
                } else if (xData[k] instanceof Date) {
                    this.dateAxis[0] = true;
                    xNumber = new Long(xData[k].getTime());
                } else {
                    xNumber = new Integer(0);
                }
                this.xValues[k] = new Double(xNumber.doubleValue());
                if (x != this.xValues[k].doubleValue()) {
                    tmpVector.add(new Integer(k));
                    x = this.xValues[k].doubleValue();
                }
            }
        }
        Object[] inttmp = tmpVector.toArray();
        this.xIndex = new int[inttmp.length];
        for (int i = 0; i < inttmp.length; i++) {
            this.xIndex[i] = ((Integer) inttmp[i]).intValue();
        }
        for (k = 0; k < this.yValues.length; k++) {
            this.yValues[k] = (Double) yData[k];
            if (zData[k] != null) {
                this.zValues[k] = (Double) zData[k];
            }
        }
    }

    public static Object[][] formObjectArray(double[][] data) {
        Double[][] object = (Double[][]) Array.newInstance(Double.class, new int[]{data.length, data[0].length});
        for (int i = 0; i < object.length; i++) {
            for (int j = 0; j < object[i].length; j++) {
                object[i][j] = new Double(data[i][j]);
            }
        }
        return object;
    }

    public static Object[] formObjectArray(double[] data) {
        Object[] object = new Double[data.length];
        for (int i = 0; i < object.length; i++) {
            object[i] = new Double(data[i]);
        }
        return object;
    }

    public int getItemCount(int series) {
        if (series <= 0) {
            return this.zValues.length;
        }
        throw new IllegalArgumentException("Only one series for contour");
    }

    public double getMaxZValue() {
        double zMax = -1.0E20d;
        for (int k = 0; k < this.zValues.length; k++) {
            if (this.zValues[k] != null) {
                zMax = Math.max(zMax, this.zValues[k].doubleValue());
            }
        }
        return zMax;
    }

    public double getMinZValue() {
        double zMin = 1.0E20d;
        for (int k = 0; k < this.zValues.length; k++) {
            if (this.zValues[k] != null) {
                zMin = Math.min(zMin, this.zValues[k].doubleValue());
            }
        }
        return zMin;
    }

    public Range getZValueRange(Range x, Range y) {
        double minX = x.getLowerBound();
        double minY = y.getLowerBound();
        double maxX = x.getUpperBound();
        double maxY = y.getUpperBound();
        double zMin = 1.0E20d;
        double zMax = -1.0E20d;
        int k = 0;
        while (k < this.zValues.length) {
            if (this.xValues[k].doubleValue() >= minX && this.xValues[k].doubleValue() <= maxX && this.yValues[k].doubleValue() >= minY && this.yValues[k].doubleValue() <= maxY && this.zValues[k] != null) {
                zMin = Math.min(zMin, this.zValues[k].doubleValue());
                zMax = Math.max(zMax, this.zValues[k].doubleValue());
            }
            k++;
        }
        return new Range(zMin, zMax);
    }

    public double getMinZValue(double minX, double minY, double maxX, double maxY) {
        double zMin = 1.0E20d;
        for (int k = 0; k < this.zValues.length; k++) {
            if (this.zValues[k] != null) {
                zMin = Math.min(zMin, this.zValues[k].doubleValue());
            }
        }
        return zMin;
    }

    public int getSeriesCount() {
        return 1;
    }

    public Comparable getSeriesKey(int series) {
        if (series <= 0) {
            return this.seriesKey;
        }
        throw new IllegalArgumentException("Only one series for contour");
    }

    public int[] getXIndices() {
        return this.xIndex;
    }

    public Number[] getXValues() {
        return this.xValues;
    }

    public Number getX(int series, int item) {
        if (series <= 0) {
            return this.xValues[item];
        }
        throw new IllegalArgumentException("Only one series for contour");
    }

    public Number getXValue(int item) {
        return this.xValues[item];
    }

    public Number[] getYValues() {
        return this.yValues;
    }

    public Number getY(int series, int item) {
        if (series <= 0) {
            return this.yValues[item];
        }
        throw new IllegalArgumentException("Only one series for contour");
    }

    public Number[] getZValues() {
        return this.zValues;
    }

    public Number getZ(int series, int item) {
        if (series <= 0) {
            return this.zValues[item];
        }
        throw new IllegalArgumentException("Only one series for contour");
    }

    public int[] indexX() {
        int[] index = new int[this.xValues.length];
        for (int k = 0; k < index.length; k++) {
            index[k] = indexX(k);
        }
        return index;
    }

    public int indexX(int k) {
        int i = Arrays.binarySearch(this.xIndex, k);
        return i >= 0 ? i : (i * -1) - 2;
    }

    public int indexY(int k) {
        return k / this.xValues.length;
    }

    public int indexZ(int i, int j) {
        return (this.xValues.length * j) + i;
    }

    public boolean isDateAxis(int axisNumber) {
        if (axisNumber < 0 || axisNumber > 2) {
            return false;
        }
        return this.dateAxis[axisNumber];
    }

    public void setSeriesKeys(Comparable[] seriesKeys) {
        if (seriesKeys.length > 1) {
            throw new IllegalArgumentException("Contours only support one series");
        }
        this.seriesKey = seriesKeys[0];
        fireDatasetChanged();
    }
}
