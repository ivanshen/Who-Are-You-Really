package org.jfree.data.xy;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import org.jfree.chart.util.ParamChecks;
import org.jfree.util.PublicCloneable;

public class DefaultWindDataset extends AbstractXYDataset implements WindDataset, PublicCloneable {
    private List allSeriesData;
    private List seriesKeys;

    public DefaultWindDataset() {
        this.seriesKeys = new ArrayList();
        this.allSeriesData = new ArrayList();
    }

    public DefaultWindDataset(Object[][][] data) {
        this(seriesNameListFromDataArray(data), data);
    }

    public DefaultWindDataset(String[] seriesNames, Object[][][] data) {
        this(Arrays.asList(seriesNames), data);
    }

    public DefaultWindDataset(List seriesKeys, Object[][][] data) {
        ParamChecks.nullNotPermitted(seriesKeys, "seriesKeys");
        if (seriesKeys.size() != data.length) {
            throw new IllegalArgumentException("The number of series keys does not match the number of series in the data array.");
        }
        this.seriesKeys = seriesKeys;
        int seriesCount = data.length;
        this.allSeriesData = new ArrayList(seriesCount);
        for (int seriesIndex = 0; seriesIndex < seriesCount; seriesIndex++) {
            List oneSeriesData = new ArrayList();
            int maxItemCount = data[seriesIndex].length;
            for (int itemIndex = 0; itemIndex < maxItemCount; itemIndex++) {
                Number xObject = data[seriesIndex][itemIndex][0];
                if (xObject != null) {
                    Number xNumber;
                    if (xObject instanceof Number) {
                        xNumber = xObject;
                    } else if (xObject instanceof Date) {
                        xNumber = new Long(((Date) xObject).getTime());
                    } else {
                        xNumber = new Integer(0);
                    }
                    oneSeriesData.add(new WindDataItem(xNumber, data[seriesIndex][itemIndex][1], data[seriesIndex][itemIndex][2]));
                }
            }
            Collections.sort(oneSeriesData);
            this.allSeriesData.add(seriesIndex, oneSeriesData);
        }
    }

    public int getSeriesCount() {
        return this.allSeriesData.size();
    }

    public int getItemCount(int series) {
        if (series >= 0 && series < getSeriesCount()) {
            return ((List) this.allSeriesData.get(series)).size();
        }
        throw new IllegalArgumentException("Invalid series index: " + series);
    }

    public Comparable getSeriesKey(int series) {
        if (series >= 0 && series < getSeriesCount()) {
            return (Comparable) this.seriesKeys.get(series);
        }
        throw new IllegalArgumentException("Invalid series index: " + series);
    }

    public Number getX(int series, int item) {
        return ((WindDataItem) ((List) this.allSeriesData.get(series)).get(item)).getX();
    }

    public Number getY(int series, int item) {
        return getWindForce(series, item);
    }

    public Number getWindDirection(int series, int item) {
        return ((WindDataItem) ((List) this.allSeriesData.get(series)).get(item)).getWindDirection();
    }

    public Number getWindForce(int series, int item) {
        return ((WindDataItem) ((List) this.allSeriesData.get(series)).get(item)).getWindForce();
    }

    public static List seriesNameListFromDataArray(Object[][] data) {
        int seriesCount = data.length;
        List seriesNameList = new ArrayList(seriesCount);
        for (int i = 0; i < seriesCount; i++) {
            seriesNameList.add("Series " + (i + 1));
        }
        return seriesNameList;
    }

    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (!(obj instanceof DefaultWindDataset)) {
            return false;
        }
        DefaultWindDataset that = (DefaultWindDataset) obj;
        if (!this.seriesKeys.equals(that.seriesKeys)) {
            return false;
        }
        if (this.allSeriesData.equals(that.allSeriesData)) {
            return true;
        }
        return false;
    }
}
