package org.jfree.ui.action;

import java.util.ArrayList;
import java.util.HashMap;
import javax.swing.Action;

public class DowngradeActionMap {
    private final ArrayList actionList;
    private final HashMap actionMap;
    private DowngradeActionMap parent;

    public DowngradeActionMap() {
        this.actionMap = new HashMap();
        this.actionList = new ArrayList();
    }

    public void setParent(DowngradeActionMap map) {
        this.parent = map;
    }

    public DowngradeActionMap getParent() {
        return this.parent;
    }

    public void put(Object key, Action action) {
        if (action == null) {
            remove(key);
            return;
        }
        if (this.actionMap.containsKey(key)) {
            remove(key);
        }
        this.actionMap.put(key, action);
        this.actionList.add(key);
    }

    public Action get(Object key) {
        Action retval = (Action) this.actionMap.get(key);
        if (retval != null) {
            return retval;
        }
        if (this.parent != null) {
            return this.parent.get(key);
        }
        return null;
    }

    public void remove(Object key) {
        this.actionMap.remove(key);
        this.actionList.remove(key);
    }

    public void clear() {
        this.actionMap.clear();
        this.actionList.clear();
    }

    public Object[] keys() {
        return this.actionList.toArray();
    }

    public int size() {
        return this.actionMap.size();
    }

    public Object[] allKeys() {
        if (this.parent == null) {
            return keys();
        }
        Object[] parentKeys = this.parent.allKeys();
        Object[] key = keys();
        Object[] retval = new Object[(parentKeys.length + key.length)];
        System.arraycopy(key, 0, retval, 0, key.length);
        System.arraycopy(retval, 0, retval, key.length, retval.length);
        return retval;
    }
}
