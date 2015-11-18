package org.jfree.ui;

import java.util.ArrayList;
import javax.swing.ComboBoxModel;
import javax.swing.event.ListDataEvent;
import javax.swing.event.ListDataListener;

public class KeyedComboBoxModel implements ComboBoxModel {
    private boolean allowOtherValue;
    private ArrayList data;
    private ArrayList listdatalistener;
    private int selectedItemIndex;
    private Object selectedItemValue;
    private transient ListDataListener[] tempListeners;

    private static class ComboBoxItemPair {
        private Object key;
        private Object value;

        public ComboBoxItemPair(Object key, Object value) {
            this.key = key;
            this.value = value;
        }

        public Object getKey() {
            return this.key;
        }

        public Object getValue() {
            return this.value;
        }

        public void setValue(Object value) {
            this.value = value;
        }
    }

    public KeyedComboBoxModel() {
        this.data = new ArrayList();
        this.listdatalistener = new ArrayList();
    }

    public KeyedComboBoxModel(Object[] keys, Object[] values) {
        this();
        setData(keys, values);
    }

    public void setData(Object[] keys, Object[] values) {
        if (values.length != keys.length) {
            throw new IllegalArgumentException("Values and text must have the same length.");
        }
        this.data.clear();
        this.data.ensureCapacity(keys.length);
        for (int i = 0; i < values.length; i++) {
            add(keys[i], values[i]);
        }
        this.selectedItemIndex = -1;
        fireListDataEvent(new ListDataEvent(this, 0, 0, this.data.size() - 1));
    }

    protected synchronized void fireListDataEvent(ListDataEvent evt) {
        if (this.tempListeners == null) {
            this.tempListeners = (ListDataListener[]) this.listdatalistener.toArray(new ListDataListener[this.listdatalistener.size()]);
        }
        ListDataListener[] listeners = this.tempListeners;
        for (ListDataListener l : listeners) {
            l.contentsChanged(evt);
        }
    }

    public Object getSelectedItem() {
        return this.selectedItemValue;
    }

    public void setSelectedKey(Object anItem) {
        if (anItem == null) {
            this.selectedItemIndex = -1;
            this.selectedItemValue = null;
        } else {
            int newSelectedItem = findDataElementIndex(anItem);
            if (newSelectedItem == -1) {
                this.selectedItemIndex = -1;
                this.selectedItemValue = null;
            } else {
                this.selectedItemIndex = newSelectedItem;
                this.selectedItemValue = getElementAt(this.selectedItemIndex);
            }
        }
        fireListDataEvent(new ListDataEvent(this, 0, -1, -1));
    }

    public void setSelectedItem(Object anItem) {
        if (anItem == null) {
            this.selectedItemIndex = -1;
            this.selectedItemValue = null;
        } else {
            int newSelectedItem = findElementIndex(anItem);
            if (newSelectedItem != -1) {
                this.selectedItemIndex = newSelectedItem;
                this.selectedItemValue = getElementAt(this.selectedItemIndex);
            } else if (isAllowOtherValue()) {
                this.selectedItemIndex = -1;
                this.selectedItemValue = anItem;
            } else {
                this.selectedItemIndex = -1;
                this.selectedItemValue = null;
            }
        }
        fireListDataEvent(new ListDataEvent(this, 0, -1, -1));
    }

    private boolean isAllowOtherValue() {
        return this.allowOtherValue;
    }

    public void setAllowOtherValue(boolean allowOtherValue) {
        this.allowOtherValue = allowOtherValue;
    }

    public synchronized void addListDataListener(ListDataListener l) {
        if (l == null) {
            throw new NullPointerException();
        }
        this.listdatalistener.add(l);
        this.tempListeners = null;
    }

    public Object getElementAt(int index) {
        if (index >= this.data.size()) {
            return null;
        }
        ComboBoxItemPair datacon = (ComboBoxItemPair) this.data.get(index);
        if (datacon != null) {
            return datacon.getValue();
        }
        return null;
    }

    public Object getKeyAt(int index) {
        if (index >= this.data.size() || index < 0) {
            return null;
        }
        ComboBoxItemPair datacon = (ComboBoxItemPair) this.data.get(index);
        if (datacon != null) {
            return datacon.getKey();
        }
        return null;
    }

    public Object getSelectedKey() {
        return getKeyAt(this.selectedItemIndex);
    }

    public int getSize() {
        return this.data.size();
    }

    public void removeListDataListener(ListDataListener l) {
        this.listdatalistener.remove(l);
        this.tempListeners = null;
    }

    private int findDataElementIndex(Object anItem) {
        if (anItem == null) {
            throw new NullPointerException("Item to find must not be null");
        }
        for (int i = 0; i < this.data.size(); i++) {
            if (anItem.equals(((ComboBoxItemPair) this.data.get(i)).getKey())) {
                return i;
            }
        }
        return -1;
    }

    public int findElementIndex(Object key) {
        if (key == null) {
            throw new NullPointerException("Item to find must not be null");
        }
        for (int i = 0; i < this.data.size(); i++) {
            if (key.equals(((ComboBoxItemPair) this.data.get(i)).getValue())) {
                return i;
            }
        }
        return -1;
    }

    public void removeDataElement(Object key) {
        int idx = findDataElementIndex(key);
        if (idx != -1) {
            this.data.remove(idx);
            fireListDataEvent(new ListDataEvent(this, 2, idx, idx));
        }
    }

    public void add(Object key, Object cbitem) {
        this.data.add(new ComboBoxItemPair(key, cbitem));
        fireListDataEvent(new ListDataEvent(this, 1, this.data.size() - 2, this.data.size() - 2));
    }

    public void clear() {
        int size = getSize();
        this.data.clear();
        fireListDataEvent(new ListDataEvent(this, 2, 0, size - 1));
    }
}
