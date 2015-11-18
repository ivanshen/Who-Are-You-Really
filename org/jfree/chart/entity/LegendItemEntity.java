package org.jfree.chart.entity;

import java.awt.Shape;
import java.io.Serializable;
import org.jfree.data.general.Dataset;
import org.jfree.util.ObjectUtilities;

public class LegendItemEntity extends ChartEntity implements Cloneable, Serializable {
    private static final long serialVersionUID = -7435683933545666702L;
    private Dataset dataset;
    private int seriesIndex;
    private Comparable seriesKey;

    public LegendItemEntity(Shape area) {
        super(area);
    }

    public Dataset getDataset() {
        return this.dataset;
    }

    public void setDataset(Dataset dataset) {
        this.dataset = dataset;
    }

    public Comparable getSeriesKey() {
        return this.seriesKey;
    }

    public void setSeriesKey(Comparable key) {
        this.seriesKey = key;
    }

    public int getSeriesIndex() {
        return this.seriesIndex;
    }

    public void setSeriesIndex(int index) {
        this.seriesIndex = index;
    }

    public boolean equals(Object obj) {
        if (obj == this) {
            return true;
        }
        if (!(obj instanceof LegendItemEntity)) {
            return false;
        }
        LegendItemEntity that = (LegendItemEntity) obj;
        if (ObjectUtilities.equal(this.seriesKey, that.seriesKey) && this.seriesIndex == that.seriesIndex && ObjectUtilities.equal(this.dataset, that.dataset)) {
            return super.equals(obj);
        }
        return false;
    }

    public Object clone() throws CloneNotSupportedException {
        return super.clone();
    }

    public String toString() {
        return "LegendItemEntity: seriesKey=" + this.seriesKey + ", dataset=" + this.dataset;
    }
}
