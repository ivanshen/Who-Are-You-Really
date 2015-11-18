package org.jfree.util;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Set;

public class HashNMap implements Serializable, Cloneable {
    private static final Object[] EMPTY_ARRAY;
    private static final Iterator EMPTY_ITERATOR;
    private static final long serialVersionUID = -670924844536074826L;
    private HashMap table;

    private static final class EmptyIterator implements Iterator {
        private EmptyIterator() {
        }

        public boolean hasNext() {
            return false;
        }

        public Object next() {
            throw new NoSuchElementException("This iterator is empty.");
        }

        public void remove() {
            throw new UnsupportedOperationException("This iterator is empty, no remove supported.");
        }
    }

    static {
        EMPTY_ITERATOR = new EmptyIterator();
        EMPTY_ARRAY = new Object[0];
    }

    public HashNMap() {
        this.table = new HashMap();
    }

    protected List createList() {
        return new ArrayList();
    }

    public boolean put(Object key, Object val) {
        List v = (List) this.table.get(key);
        if (v == null) {
            List newList = createList();
            newList.add(val);
            this.table.put(key, newList);
            return true;
        }
        v.clear();
        return v.add(val);
    }

    public boolean add(Object key, Object val) {
        List v = (List) this.table.get(key);
        if (v != null) {
            return v.add(val);
        }
        put(key, val);
        return true;
    }

    public Object getFirst(Object key) {
        return get(key, 0);
    }

    public Object get(Object key, int n) {
        List v = (List) this.table.get(key);
        if (v == null) {
            return null;
        }
        return v.get(n);
    }

    public Iterator getAll(Object key) {
        List v = (List) this.table.get(key);
        if (v == null) {
            return EMPTY_ITERATOR;
        }
        return v.iterator();
    }

    public Iterator keys() {
        return this.table.keySet().iterator();
    }

    public Set keySet() {
        return this.table.keySet();
    }

    public boolean remove(Object key, Object value) {
        List v = (List) this.table.get(key);
        if (v == null || !v.remove(value)) {
            return false;
        }
        if (v.size() == 0) {
            this.table.remove(key);
        }
        return true;
    }

    public void removeAll(Object key) {
        this.table.remove(key);
    }

    public void clear() {
        this.table.clear();
    }

    public boolean containsKey(Object key) {
        return this.table.containsKey(key);
    }

    public boolean containsValue(Object value) {
        Iterator e = this.table.values().iterator();
        boolean found = false;
        while (e.hasNext() && !found) {
            found = ((List) e.next()).contains(value);
        }
        return found;
    }

    public boolean containsValue(Object key, Object value) {
        List v = (List) this.table.get(key);
        if (v == null) {
            return false;
        }
        return v.contains(value);
    }

    public boolean contains(Object value) {
        if (containsKey(value)) {
            return true;
        }
        return containsValue(value);
    }

    public Object clone() throws CloneNotSupportedException {
        HashNMap map = (HashNMap) super.clone();
        map.table = new HashMap();
        Iterator iterator = keys();
        while (iterator.hasNext()) {
            Object key = iterator.next();
            List list = (List) map.table.get(key);
            if (list != null) {
                map.table.put(key, ObjectUtilities.clone(list));
            }
        }
        return map;
    }

    public Object[] toArray(Object key, Object[] data) {
        if (key == null) {
            throw new NullPointerException("Key must not be null.");
        }
        List list = (List) this.table.get(key);
        if (list != null) {
            return list.toArray(data);
        }
        if (data.length <= 0) {
            return data;
        }
        data[0] = null;
        return data;
    }

    public Object[] toArray(Object key) {
        if (key == null) {
            throw new NullPointerException("Key must not be null.");
        }
        List list = (List) this.table.get(key);
        if (list != null) {
            return list.toArray();
        }
        return EMPTY_ARRAY;
    }

    public int getValueCount(Object key) {
        if (key == null) {
            throw new NullPointerException("Key must not be null.");
        }
        List list = (List) this.table.get(key);
        if (list != null) {
            return list.size();
        }
        return 0;
    }
}
