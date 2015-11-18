package org.jfree.data;

import java.io.Serializable;
import org.jfree.chart.util.ParamChecks;
import org.jfree.util.PublicCloneable;

public class DefaultKeyedValue implements KeyedValue, Cloneable, PublicCloneable, Serializable {
    private static final long serialVersionUID = -7388924517460437712L;
    private Comparable key;
    private Number value;

    public DefaultKeyedValue(Comparable key, Number value) {
        ParamChecks.nullNotPermitted(key, "key");
        this.key = key;
        this.value = value;
    }

    public Comparable getKey() {
        return this.key;
    }

    public Number getValue() {
        return this.value;
    }

    public synchronized void setValue(Number value) {
        this.value = value;
    }

    public boolean equals(Object obj) {
        if (obj == this) {
            return true;
        }
        if (!(obj instanceof DefaultKeyedValue)) {
            return false;
        }
        DefaultKeyedValue that = (DefaultKeyedValue) obj;
        if (!this.key.equals(that.key)) {
            return false;
        }
        if (this.value != null) {
            if (this.value.equals(that.value)) {
                return true;
            }
        } else if (that.value == null) {
            return true;
        }
        return false;
    }

    public int hashCode() {
        int result;
        int i = 0;
        if (this.key != null) {
            result = this.key.hashCode();
        } else {
            result = 0;
        }
        int i2 = result * 29;
        if (this.value != null) {
            i = this.value.hashCode();
        }
        return i2 + i;
    }

    public Object clone() throws CloneNotSupportedException {
        return (DefaultKeyedValue) super.clone();
    }

    public String toString() {
        return "(" + this.key.toString() + ", " + this.value.toString() + ")";
    }
}
