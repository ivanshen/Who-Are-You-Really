package org.jfree.chart;

import java.awt.Stroke;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.Map;
import java.util.TreeMap;
import org.jfree.chart.util.ParamChecks;
import org.jfree.io.SerialUtilities;
import org.jfree.util.ObjectUtilities;

public class StrokeMap implements Cloneable, Serializable {
    static final long serialVersionUID = -8148916785963525169L;
    private transient Map store;

    public StrokeMap() {
        this.store = new TreeMap();
    }

    public Stroke getStroke(Comparable key) {
        ParamChecks.nullNotPermitted(key, "key");
        return (Stroke) this.store.get(key);
    }

    public boolean containsKey(Comparable key) {
        return this.store.containsKey(key);
    }

    public void put(Comparable key, Stroke stroke) {
        ParamChecks.nullNotPermitted(key, "key");
        this.store.put(key, stroke);
    }

    public void clear() {
        this.store.clear();
    }

    public boolean equals(Object obj) {
        if (obj == this) {
            return true;
        }
        if (!(obj instanceof StrokeMap)) {
            return false;
        }
        StrokeMap that = (StrokeMap) obj;
        if (this.store.size() != that.store.size()) {
            return false;
        }
        for (Comparable key : this.store.keySet()) {
            if (!ObjectUtilities.equal(getStroke(key), that.getStroke(key))) {
                return false;
            }
        }
        return true;
    }

    public Object clone() throws CloneNotSupportedException {
        StrokeMap clone = (StrokeMap) super.clone();
        clone.store = new TreeMap();
        clone.store.putAll(this.store);
        return clone;
    }

    private void writeObject(ObjectOutputStream stream) throws IOException {
        stream.defaultWriteObject();
        stream.writeInt(this.store.size());
        for (Comparable key : this.store.keySet()) {
            stream.writeObject(key);
            SerialUtilities.writeStroke(getStroke(key), stream);
        }
    }

    private void readObject(ObjectInputStream stream) throws IOException, ClassNotFoundException {
        stream.defaultReadObject();
        this.store = new TreeMap();
        int keyCount = stream.readInt();
        for (int i = 0; i < keyCount; i++) {
            this.store.put((Comparable) stream.readObject(), SerialUtilities.readStroke(stream));
        }
    }
}
