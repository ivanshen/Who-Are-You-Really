package org.jfree.data;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import org.jfree.chart.util.ParamChecks;
import org.jfree.util.PublicCloneable;
import org.jfree.util.SortOrder;

public class DefaultKeyedValues implements KeyedValues, Cloneable, PublicCloneable, Serializable {
    private static final long serialVersionUID = 8468154364608194797L;
    private HashMap indexMap;
    private ArrayList keys;
    private ArrayList values;

    public DefaultKeyedValues() {
        this.keys = new ArrayList();
        this.values = new ArrayList();
        this.indexMap = new HashMap();
    }

    public int getItemCount() {
        return this.indexMap.size();
    }

    public Number getValue(int item) {
        return (Number) this.values.get(item);
    }

    public Comparable getKey(int index) {
        return (Comparable) this.keys.get(index);
    }

    public int getIndex(Comparable key) {
        ParamChecks.nullNotPermitted(key, "key");
        Integer i = (Integer) this.indexMap.get(key);
        if (i == null) {
            return -1;
        }
        return i.intValue();
    }

    public List getKeys() {
        return (List) this.keys.clone();
    }

    public Number getValue(Comparable key) {
        int index = getIndex(key);
        if (index >= 0) {
            return getValue(index);
        }
        throw new UnknownKeyException("Key not found: " + key);
    }

    public void addValue(Comparable key, double value) {
        addValue(key, new Double(value));
    }

    public void addValue(Comparable key, Number value) {
        setValue(key, value);
    }

    public void setValue(Comparable key, double value) {
        setValue(key, new Double(value));
    }

    public void setValue(Comparable key, Number value) {
        ParamChecks.nullNotPermitted(key, "key");
        int keyIndex = getIndex(key);
        if (keyIndex >= 0) {
            this.keys.set(keyIndex, key);
            this.values.set(keyIndex, value);
            return;
        }
        this.keys.add(key);
        this.values.add(value);
        this.indexMap.put(key, new Integer(this.keys.size() - 1));
    }

    public void insertValue(int position, Comparable key, double value) {
        insertValue(position, key, new Double(value));
    }

    public void insertValue(int position, Comparable key, Number value) {
        if (position < 0 || position > getItemCount()) {
            throw new IllegalArgumentException("'position' out of bounds.");
        }
        ParamChecks.nullNotPermitted(key, "key");
        int pos = getIndex(key);
        if (pos == position) {
            this.keys.set(pos, key);
            this.values.set(pos, value);
            return;
        }
        if (pos >= 0) {
            this.keys.remove(pos);
            this.values.remove(pos);
        }
        this.keys.add(position, key);
        this.values.add(position, value);
        rebuildIndex();
    }

    private void rebuildIndex() {
        this.indexMap.clear();
        for (int i = 0; i < this.keys.size(); i++) {
            this.indexMap.put(this.keys.get(i), new Integer(i));
        }
    }

    public void removeValue(int index) {
        this.keys.remove(index);
        this.values.remove(index);
        rebuildIndex();
    }

    public void removeValue(Comparable key) {
        int index = getIndex(key);
        if (index < 0) {
            throw new UnknownKeyException("The key (" + key + ") is not recognised.");
        }
        removeValue(index);
    }

    public void clear() {
        this.keys.clear();
        this.values.clear();
        this.indexMap.clear();
    }

    public void sortByKeys(SortOrder order) {
        int size = this.keys.size();
        DefaultKeyedValue[] data = new DefaultKeyedValue[size];
        for (int i = 0; i < size; i++) {
            data[i] = new DefaultKeyedValue((Comparable) this.keys.get(i), (Number) this.values.get(i));
        }
        Arrays.sort(data, new KeyedValueComparator(KeyedValueComparatorType.BY_KEY, order));
        clear();
        for (DefaultKeyedValue value : data) {
            addValue(value.getKey(), value.getValue());
        }
    }

    public void sortByValues(SortOrder order) {
        int size = this.keys.size();
        DefaultKeyedValue[] data = new DefaultKeyedValue[size];
        for (int i = 0; i < size; i++) {
            data[i] = new DefaultKeyedValue((Comparable) this.keys.get(i), (Number) this.values.get(i));
        }
        Arrays.sort(data, new KeyedValueComparator(KeyedValueComparatorType.BY_VALUE, order));
        clear();
        for (DefaultKeyedValue value : data) {
            addValue(value.getKey(), value.getValue());
        }
    }

    public boolean equals(Object obj) {
        if (obj == this) {
            return true;
        }
        if (!(obj instanceof KeyedValues)) {
            return false;
        }
        KeyedValues that = (KeyedValues) obj;
        int count = getItemCount();
        if (count != that.getItemCount()) {
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
        return this.keys != null ? this.keys.hashCode() : 0;
    }

    public Object clone() throws CloneNotSupportedException {
        DefaultKeyedValues clone = (DefaultKeyedValues) super.clone();
        clone.keys = (ArrayList) this.keys.clone();
        clone.values = (ArrayList) this.values.clone();
        clone.indexMap = (HashMap) this.indexMap.clone();
        return clone;
    }
}
