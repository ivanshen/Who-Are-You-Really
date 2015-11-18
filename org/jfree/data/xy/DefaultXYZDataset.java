package org.jfree.data.xy;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import org.jfree.data.DomainOrder;
import org.jfree.data.general.DatasetChangeEvent;
import org.jfree.util.PublicCloneable;

public class DefaultXYZDataset extends AbstractXYZDataset implements XYZDataset, PublicCloneable {
    private List seriesKeys;
    private List seriesList;

    public DefaultXYZDataset() {
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

    public int indexOf(Comparable seriesKey) {
        return this.seriesKeys.indexOf(seriesKey);
    }

    public DomainOrder getDomainOrder() {
        return DomainOrder.NONE;
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

    public Number getX(int series, int item) {
        return new Double(getXValue(series, item));
    }

    public double getYValue(int series, int item) {
        return ((double[][]) this.seriesList.get(series))[1][item];
    }

    public Number getY(int series, int item) {
        return new Double(getYValue(series, item));
    }

    public double getZValue(int series, int item) {
        return ((double[][]) this.seriesList.get(series))[2][item];
    }

    public Number getZ(int series, int item) {
        return new Double(getZValue(series, item));
    }

    public void addSeries(Comparable seriesKey, double[][] data) {
        if (seriesKey == null) {
            throw new IllegalArgumentException("The 'seriesKey' cannot be null.");
        } else if (data == null) {
            throw new IllegalArgumentException("The 'data' is null.");
        } else if (data.length != 3) {
            throw new IllegalArgumentException("The 'data' array must have length == 3.");
        } else if (data[0].length == data[1].length && data[0].length == data[2].length) {
            int seriesIndex = indexOf(seriesKey);
            if (seriesIndex == -1) {
                this.seriesKeys.add(seriesKey);
                this.seriesList.add(data);
            } else {
                this.seriesList.remove(seriesIndex);
                this.seriesList.add(seriesIndex, data);
            }
            notifyListeners(new DatasetChangeEvent(this, this));
        } else {
            throw new IllegalArgumentException("The 'data' array must contain three arrays all having the same length.");
        }
    }

    public void removeSeries(Comparable seriesKey) {
        int seriesIndex = indexOf(seriesKey);
        if (seriesIndex >= 0) {
            this.seriesKeys.remove(seriesIndex);
            this.seriesList.remove(seriesIndex);
            notifyListeners(new DatasetChangeEvent(this, this));
        }
    }

    public boolean equals(Object obj) {
        if (obj == this) {
            return true;
        }
        if (!(obj instanceof DefaultXYZDataset)) {
            return false;
        }
        DefaultXYZDataset that = (DefaultXYZDataset) obj;
        if (!this.seriesKeys.equals(that.seriesKeys)) {
            return false;
        }
        for (int i = 0; i < this.seriesList.size(); i++) {
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
        }
        return true;
    }

    public int hashCode() {
        return (this.seriesKeys.hashCode() * 29) + this.seriesList.hashCode();
    }

    public Object clone() throws CloneNotSupportedException {
        DefaultXYZDataset clone = (DefaultXYZDataset) super.clone();
        clone.seriesKeys = new ArrayList(this.seriesKeys);
        clone.seriesList = new ArrayList(this.seriesList.size());
        for (int i = 0; i < this.seriesList.size(); i++) {
            double[][] data = (double[][]) this.seriesList.get(i);
            double[] x = data[0];
            double[] y = data[1];
            double[] z = data[2];
            double[] yy = new double[y.length];
            double[] zz = new double[z.length];
            System.arraycopy(x, 0, new double[x.length], 0, x.length);
            System.arraycopy(y, 0, yy, 0, y.length);
            System.arraycopy(z, 0, zz, 0, z.length);
            clone.seriesList.add(i, new double[][]{xx, yy, zz});
        }
        return clone;
    }
}
