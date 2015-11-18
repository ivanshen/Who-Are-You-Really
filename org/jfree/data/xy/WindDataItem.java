package org.jfree.data.xy;

import java.io.Serializable;

/* compiled from: DefaultWindDataset */
class WindDataItem implements Comparable, Serializable {
    private Number windDir;
    private Number windForce;
    private Number x;

    public WindDataItem(Number x, Number windDir, Number windForce) {
        this.x = x;
        this.windDir = windDir;
        this.windForce = windForce;
    }

    public Number getX() {
        return this.x;
    }

    public Number getWindDirection() {
        return this.windDir;
    }

    public Number getWindForce() {
        return this.windForce;
    }

    public int compareTo(Object object) {
        if (object instanceof WindDataItem) {
            WindDataItem item = (WindDataItem) object;
            if (this.x.doubleValue() > item.x.doubleValue()) {
                return 1;
            }
            if (this.x.equals(item.x)) {
                return 0;
            }
            return -1;
        }
        throw new ClassCastException("WindDataItem.compareTo(error)");
    }

    public boolean equals(Object obj) {
        if (this == obj || !(obj instanceof WindDataItem)) {
            return false;
        }
        WindDataItem that = (WindDataItem) obj;
        if (this.x.equals(that.x) && this.windDir.equals(that.windDir) && this.windForce.equals(that.windForce)) {
            return true;
        }
        return false;
    }
}
