package org.jfree.chart.entity;

import java.awt.Shape;
import java.io.Serializable;

public class ContourEntity extends ChartEntity implements Cloneable, Serializable {
    private static final long serialVersionUID = 1249570520505992847L;
    private int index;

    public ContourEntity(Shape area, String toolTipText) {
        super(area, toolTipText);
        this.index = -1;
    }

    public ContourEntity(Shape area, String toolTipText, String urlText) {
        super(area, toolTipText, urlText);
        this.index = -1;
    }

    public int getIndex() {
        return this.index;
    }

    public void setIndex(int index) {
        this.index = index;
    }

    public boolean equals(Object obj) {
        if (obj == this) {
            return true;
        }
        if (!(obj instanceof ContourEntity) || !super.equals(obj)) {
            return false;
        }
        if (this.index != ((ContourEntity) obj).index) {
            return false;
        }
        return true;
    }

    public Object clone() throws CloneNotSupportedException {
        return super.clone();
    }
}
