package org.jfree.data;

import java.io.Serializable;
import org.jfree.chart.util.ParamChecks;
import org.jfree.util.ObjectUtilities;

public class ComparableObjectItem implements Cloneable, Comparable, Serializable {
    private static final long serialVersionUID = 2751513470325494890L;
    private Object obj;
    private Comparable x;

    public ComparableObjectItem(Comparable x, Object y) {
        ParamChecks.nullNotPermitted(x, "x");
        this.x = x;
        this.obj = y;
    }

    protected Comparable getComparable() {
        return this.x;
    }

    protected Object getObject() {
        return this.obj;
    }

    protected void setObject(Object y) {
        this.obj = y;
    }

    public int compareTo(Object o1) {
        if (!(o1 instanceof ComparableObjectItem)) {
            return 1;
        }
        return this.x.compareTo(((ComparableObjectItem) o1).x);
    }

    public Object clone() throws CloneNotSupportedException {
        return super.clone();
    }

    public boolean equals(Object obj) {
        if (obj == this) {
            return true;
        }
        if (!(obj instanceof ComparableObjectItem)) {
            return false;
        }
        ComparableObjectItem that = (ComparableObjectItem) obj;
        if (!this.x.equals(that.x)) {
            return false;
        }
        if (ObjectUtilities.equal(this.obj, that.obj)) {
            return true;
        }
        return false;
    }

    public int hashCode() {
        return (this.x.hashCode() * 29) + (this.obj != null ? this.obj.hashCode() : 0);
    }
}
