package org.jfree.data;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import org.jfree.chart.util.ParamChecks;
import org.jfree.util.PublicCloneable;

public class KeyedObjects implements Cloneable, PublicCloneable, Serializable {
    private static final long serialVersionUID = 1321582394193530984L;
    private List data;

    public KeyedObjects() {
        this.data = new ArrayList();
    }

    public int getItemCount() {
        return this.data.size();
    }

    public Object getObject(int item) {
        KeyedObject kobj = (KeyedObject) this.data.get(item);
        if (kobj != null) {
            return kobj.getObject();
        }
        return null;
    }

    public Comparable getKey(int index) {
        KeyedObject item = (KeyedObject) this.data.get(index);
        if (item != null) {
            return item.getKey();
        }
        return null;
    }

    public int getIndex(Comparable key) {
        ParamChecks.nullNotPermitted(key, "key");
        int i = 0;
        for (KeyedObject ko : this.data) {
            if (ko.getKey().equals(key)) {
                return i;
            }
            i++;
        }
        return -1;
    }

    public List getKeys() {
        List result = new ArrayList();
        for (KeyedObject ko : this.data) {
            result.add(ko.getKey());
        }
        return result;
    }

    public Object getObject(Comparable key) {
        int index = getIndex(key);
        if (index >= 0) {
            return getObject(index);
        }
        throw new UnknownKeyException("The key (" + key + ") is not recognised.");
    }

    public void addObject(Comparable key, Object object) {
        setObject(key, object);
    }

    public void setObject(Comparable key, Object object) {
        int keyIndex = getIndex(key);
        if (keyIndex >= 0) {
            ((KeyedObject) this.data.get(keyIndex)).setObject(object);
            return;
        }
        this.data.add(new KeyedObject(key, object));
    }

    public void insertValue(int position, Comparable key, Object value) {
        if (position < 0 || position > this.data.size()) {
            throw new IllegalArgumentException("'position' out of bounds.");
        }
        ParamChecks.nullNotPermitted(key, "key");
        int pos = getIndex(key);
        if (pos >= 0) {
            this.data.remove(pos);
        }
        KeyedObject item = new KeyedObject(key, value);
        if (position <= this.data.size()) {
            this.data.add(position, item);
        } else {
            this.data.add(item);
        }
    }

    public void removeValue(int index) {
        this.data.remove(index);
    }

    public void removeValue(Comparable key) {
        int index = getIndex(key);
        if (index < 0) {
            throw new UnknownKeyException("The key (" + key.toString() + ") is not recognised.");
        }
        removeValue(index);
    }

    public void clear() {
        this.data.clear();
    }

    public Object clone() throws CloneNotSupportedException {
        KeyedObjects clone = (KeyedObjects) super.clone();
        clone.data = new ArrayList();
        for (KeyedObject ko : this.data) {
            clone.data.add(ko.clone());
        }
        return clone;
    }

    public boolean equals(Object obj) {
        if (obj == this) {
            return true;
        }
        if (!(obj instanceof KeyedObjects)) {
            return false;
        }
        KeyedObjects that = (KeyedObjects) obj;
        int count = getItemCount();
        if (count != that.getItemCount()) {
            return false;
        }
        for (int i = 0; i < count; i++) {
            if (!getKey(i).equals(that.getKey(i))) {
                return false;
            }
            Object o1 = getObject(i);
            Object o2 = that.getObject(i);
            if (o1 == null) {
                if (o2 != null) {
                    return false;
                }
            } else if (!o1.equals(o2)) {
                return false;
            }
        }
        return true;
    }

    public int hashCode() {
        return this.data != null ? this.data.hashCode() : 0;
    }
}
