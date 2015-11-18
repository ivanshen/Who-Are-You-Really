package org.jfree.data.xy;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import org.jfree.data.general.DatasetChangeEvent;
import org.jfree.util.PublicCloneable;

public class DefaultIntervalXYDataset extends AbstractIntervalXYDataset implements PublicCloneable {
    private List seriesKeys;
    private List seriesList;

    public DefaultIntervalXYDataset() {
        this.seriesKeys = new ArrayList();
        this.seriesList = new ArrayList();
    }

    public int getSeriesCount() {
        return this.seriesList.size();
    }

    public Comparable getSeriesKey(int series) {
        if (series >= 0 && series < getSeriesCount()) {
            return (Comparable) this.seriesKeys.get(series);
        }
        throw new IllegalArgumentException("Series index out of bounds");
    }

    public int getItemCount(int series) {
        if (series < 0 || series >= getSeriesCount()) {
            throw new IllegalArgumentException("Series index out of bounds");
        }
        return ((double[][]) this.seriesList.get(series))[0].length;
    }

    public double getXValue(int series, int item) {
        return ((double[][]) this.seriesList.get(series))[0][item];
    }

    public double getYValue(int series, int item) {
        return ((double[][]) this.seriesList.get(series))[3][item];
    }

    public double getStartXValue(int series, int item) {
        return ((double[][]) this.seriesList.get(series))[1][item];
    }

    public double getEndXValue(int series, int item) {
        return ((double[][]) this.seriesList.get(series))[2][item];
    }

    public double getStartYValue(int series, int item) {
        return ((double[][]) this.seriesList.get(series))[4][item];
    }

    public double getEndYValue(int series, int item) {
        return ((double[][]) this.seriesList.get(series))[5][item];
    }

    public Number getEndX(int series, int item) {
        return new Double(getEndXValue(series, item));
    }

    public Number getEndY(int series, int item) {
        return new Double(getEndYValue(series, item));
    }

    public Number getStartX(int series, int item) {
        return new Double(getStartXValue(series, item));
    }

    public Number getStartY(int series, int item) {
        return new Double(getStartYValue(series, item));
    }

    public Number getX(int series, int item) {
        return new Double(getXValue(series, item));
    }

    public Number getY(int series, int item) {
        return new Double(getYValue(series, item));
    }

    public void addSeries(Comparable seriesKey, double[][] data) {
        if (seriesKey == null) {
            throw new IllegalArgumentException("The 'seriesKey' cannot be null.");
        } else if (data == null) {
            throw new IllegalArgumentException("The 'data' is null.");
        } else if (data.length != 6) {
            throw new IllegalArgumentException("The 'data' array must have length == 6.");
        } else {
            int length = data[0].length;
            if (length == data[1].length && length == data[2].length && length == data[3].length && length == data[4].length && length == data[5].length) {
                int seriesIndex = indexOf(seriesKey);
                if (seriesIndex == -1) {
                    this.seriesKeys.add(seriesKey);
                    this.seriesList.add(data);
                } else {
                    this.seriesList.remove(seriesIndex);
                    this.seriesList.add(seriesIndex, data);
                }
                notifyListeners(new DatasetChangeEvent(this, this));
                return;
            }
            throw new IllegalArgumentException("The 'data' array must contain six arrays with equal length.");
        }
    }

    public boolean equals(Object obj) {
        if (obj == this) {
            return true;
        }
        if (!(obj instanceof DefaultIntervalXYDataset)) {
            return false;
        }
        DefaultIntervalXYDataset that = (DefaultIntervalXYDataset) obj;
        if (!this.seriesKeys.equals(that.seriesKeys)) {
            return false;
        }
        int i = 0;
        while (true) {
            if (i >= this.seriesList.size()) {
                return true;
            }
            double[][] d1 = (double[][]) this.seriesList.get(i);
            double[][] d2 = (double[][]) that.seriesList.get(i);
            if (!Arrays.equals(d1[0], d2[0])) {
                return false;
            }
            if (!Arrays.equals(d1[1], d2[1])) {
                return false;
            }
            if (!Arrays.equals(d1[2], d2[2])) {
                return false;
            }
            if (!Arrays.equals(d1[3], d2[3])) {
                return false;
            }
            if (!Arrays.equals(d1[4], d2[4])) {
                return false;
            }
            if (!Arrays.equals(d1[5], d2[5])) {
                return false;
            }
            i++;
        }
    }

    public int hashCode() {
        return (this.seriesKeys.hashCode() * 29) + this.seriesList.hashCode();
    }

    public Object clone() throws CloneNotSupportedException {
        DefaultIntervalXYDataset clone = (DefaultIntervalXYDataset) super.clone();
        clone.seriesKeys = new ArrayList(this.seriesKeys);
        clone.seriesList = new ArrayList(this.seriesList.size());
        int i = 0;
        while (true) {
            if (i >= this.seriesList.size()) {
                return clone;
            }
            double[][] data = (double[][]) this.seriesList.get(i);
            double[] x = data[0];
            double[] xStart = data[1];
            double[] xEnd = data[2];
            double[] y = data[3];
            double[] yStart = data[4];
            double[] yEnd = data[5];
            double[] xx = new double[x.length];
            double[] xxStart = new double[xStart.length];
            double[] xxEnd = new double[xEnd.length];
            Object yy = new double[y.length];
            Object yyStart = new double[yStart.length];
            Object yyEnd = new double[yEnd.length];
            System.arraycopy(x, 0, xx, 0, x.length);
            System.arraycopy(xStart, 0, xxStart, 0, xStart.length);
            System.arraycopy(xEnd, 0, xxEnd, 0, xEnd.length);
            System.arraycopy(y, 0, yy, 0, y.length);
            System.arraycopy(yStart, 0, yyStart, 0, yStart.length);
            System.arraycopy(yEnd, 0, yyEnd, 0, yEnd.length);
            clone.seriesList.add(i, new double[][]{xx, xxStart, xxEnd, yy, yyStart, yyEnd});
            i++;
        }
    }
}
