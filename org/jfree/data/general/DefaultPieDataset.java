package org.jfree.data.general;

import java.io.Serializable;
import java.util.Collections;
import java.util.List;
import org.jfree.chart.util.ParamChecks;
import org.jfree.data.DefaultKeyedValues;
import org.jfree.data.KeyedValues;
import org.jfree.util.PublicCloneable;
import org.jfree.util.SortOrder;

public class DefaultPieDataset extends AbstractDataset implements PieDataset, Cloneable, PublicCloneable, Serializable {
    private static final long serialVersionUID = 2904745139106540618L;
    private DefaultKeyedValues data;

    public DefaultPieDataset() {
        this.data = new DefaultKeyedValues();
    }

    public DefaultPieDataset(KeyedValues data) {
        ParamChecks.nullNotPermitted(data, "data");
        this.data = new DefaultKeyedValues();
        for (int i = 0; i < data.getItemCount(); i++) {
            this.data.addValue(data.getKey(i), data.getValue(i));
        }
    }

    public int getItemCount() {
        return this.data.getItemCount();
    }

    public List getKeys() {
        return Collections.unmodifiableList(this.data.getKeys());
    }

    public Comparable getKey(int item) {
        return this.data.getKey(item);
    }

    public int getIndex(Comparable key) {
        return this.data.getIndex(key);
    }

    public Number getValue(int item) {
        if (getItemCount() > item) {
            return this.data.getValue(item);
        }
        return null;
    }

    public Number getValue(Comparable key) {
        ParamChecks.nullNotPermitted(key, "key");
        return this.data.getValue(key);
    }

    public void setValue(Comparable key, Number value) {
        this.data.setValue(key, value);
        fireDatasetChanged();
    }

    public void setValue(Comparable key, double value) {
        setValue(key, new Double(value));
    }

    public void insertValue(int position, Comparable key, double value) {
        insertValue(position, key, new Double(value));
    }

    public void insertValue(int position, Comparable key, Number value) {
        this.data.insertValue(position, key, value);
        fireDatasetChanged();
    }

    public void remove(Comparable key) {
        this.data.removeValue(key);
        fireDatasetChanged();
    }

    public void clear() {
        if (getItemCount() > 0) {
            this.data.clear();
            fireDatasetChanged();
        }
    }

    public void sortByKeys(SortOrder order) {
        this.data.sortByKeys(order);
        fireDatasetChanged();
    }

    public void sortByValues(SortOrder order) {
        this.data.sortByValues(order);
        fireDatasetChanged();
    }

    public boolean equals(Object obj) {
        if (obj == this) {
            return true;
        }
        if (!(obj instanceof PieDataset)) {
            return false;
        }
        PieDataset that = (PieDataset) obj;
        int count = getItemCount();
        if (that.getItemCount() != count) {
            return false;
        }
        for (int i = 0; i < count; i++) {
            if (!getKey(i).equals(that.getKey(i))) {
                return false;
            }
            Number v1 = getValue(i);
            Number v2 = that.getValue(i);
            if (v1 == null) {
                if (v2 != null) {
                    return false;
                }
            } else if (!v1.equals(v2)) {
                return false;
            }
        }
        return true;
    }

    public int hashCode() {
        return this.data.hashCode();
    }

    public Object clone() throws CloneNotSupportedException {
        DefaultPieDataset clone = (DefaultPieDataset) super.clone();
        clone.data = (DefaultKeyedValues) this.data.clone();
        return clone;
    }
}
