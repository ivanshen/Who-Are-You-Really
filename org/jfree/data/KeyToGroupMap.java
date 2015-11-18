package org.jfree.data;

import java.io.Serializable;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.jfree.chart.util.ParamChecks;
import org.jfree.util.ObjectUtilities;
import org.jfree.util.PublicCloneable;

public class KeyToGroupMap implements Cloneable, PublicCloneable, Serializable {
    private static final long serialVersionUID = -2228169345475318082L;
    private Comparable defaultGroup;
    private List groups;
    private Map keyToGroupMap;

    public KeyToGroupMap() {
        this("Default Group");
    }

    public KeyToGroupMap(Comparable defaultGroup) {
        ParamChecks.nullNotPermitted(defaultGroup, "defaultGroup");
        this.defaultGroup = defaultGroup;
        this.groups = new ArrayList();
        this.keyToGroupMap = new HashMap();
    }

    public int getGroupCount() {
        return this.groups.size() + 1;
    }

    public List getGroups() {
        List result = new ArrayList();
        result.add(this.defaultGroup);
        for (Comparable group : this.groups) {
            if (!result.contains(group)) {
                result.add(group);
            }
        }
        return result;
    }

    public int getGroupIndex(Comparable group) {
        int result = this.groups.indexOf(group);
        if (result >= 0) {
            return result + 1;
        }
        if (this.defaultGroup.equals(group)) {
            return 0;
        }
        return result;
    }

    public Comparable getGroup(Comparable key) {
        ParamChecks.nullNotPermitted(key, "key");
        Comparable result = this.defaultGroup;
        Comparable group = (Comparable) this.keyToGroupMap.get(key);
        if (group != null) {
            return group;
        }
        return result;
    }

    public void mapKeyToGroup(Comparable key, Comparable group) {
        ParamChecks.nullNotPermitted(key, "key");
        Comparable currentGroup = getGroup(key);
        if (!(currentGroup.equals(this.defaultGroup) || currentGroup.equals(group) || getKeyCount(currentGroup) != 1)) {
            this.groups.remove(currentGroup);
        }
        if (group == null) {
            this.keyToGroupMap.remove(key);
            return;
        }
        if (!(this.groups.contains(group) || this.defaultGroup.equals(group))) {
            this.groups.add(group);
        }
        this.keyToGroupMap.put(key, group);
    }

    public int getKeyCount(Comparable group) {
        ParamChecks.nullNotPermitted(group, "group");
        int result = 0;
        for (Comparable g : this.keyToGroupMap.values()) {
            if (group.equals(g)) {
                result++;
            }
        }
        return result;
    }

    public boolean equals(Object obj) {
        if (obj == this) {
            return true;
        }
        if (!(obj instanceof KeyToGroupMap)) {
            return false;
        }
        KeyToGroupMap that = (KeyToGroupMap) obj;
        if (!ObjectUtilities.equal(this.defaultGroup, that.defaultGroup)) {
            return false;
        }
        if (this.keyToGroupMap.equals(that.keyToGroupMap)) {
            return true;
        }
        return false;
    }

    public Object clone() throws CloneNotSupportedException {
        KeyToGroupMap result = (KeyToGroupMap) super.clone();
        result.defaultGroup = (Comparable) clone(this.defaultGroup);
        result.groups = (List) clone(this.groups);
        result.keyToGroupMap = (Map) clone(this.keyToGroupMap);
        return result;
    }

    private static Object clone(Object object) {
        if (object == null) {
            return null;
        }
        Object result = null;
        try {
            Method m = object.getClass().getMethod("clone", (Class[]) null);
            if (!Modifier.isPublic(m.getModifiers())) {
                return result;
            }
            try {
                return m.invoke(object, (Object[]) null);
            } catch (Exception e) {
                e.printStackTrace();
                return result;
            }
        } catch (NoSuchMethodException e2) {
            return object;
        }
    }

    private static Collection clone(Collection list) throws CloneNotSupportedException {
        if (list == null) {
            return null;
        }
        try {
            List clone = (List) list.getClass().newInstance();
            for (Object clone2 : list) {
                clone.add(clone(clone2));
            }
            return clone;
        } catch (Exception e) {
            throw new CloneNotSupportedException("Exception.");
        }
    }
}
