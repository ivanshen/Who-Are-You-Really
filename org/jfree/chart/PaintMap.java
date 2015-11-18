package org.jfree.chart;

import java.awt.Paint;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;
import org.jfree.chart.util.ParamChecks;
import org.jfree.io.SerialUtilities;
import org.jfree.util.PaintUtilities;

public class PaintMap implements Cloneable, Serializable {
    static final long serialVersionUID = -4639833772123069274L;
    private transient Map store;

    public PaintMap() {
        this.store = new HashMap();
    }

    public Paint getPaint(Comparable key) {
        ParamChecks.nullNotPermitted(key, "key");
        return (Paint) this.store.get(key);
    }

    public boolean containsKey(Comparable key) {
        return this.store.containsKey(key);
    }

    public void put(Comparable key, Paint paint) {
        ParamChecks.nullNotPermitted(key, "key");
        this.store.put(key, paint);
    }

    public void clear() {
        this.store.clear();
    }

    public boolean equals(Object obj) {
        if (obj == this) {
            return true;
        }
        if (!(obj instanceof PaintMap)) {
            return false;
        }
        PaintMap that = (PaintMap) obj;
        if (this.store.size() != that.store.size()) {
            return false;
        }
        for (Comparable key : this.store.keySet()) {
            if (!PaintUtilities.equal(getPaint(key), that.getPaint(key))) {
                return false;
            }
        }
        return true;
    }

    public Object clone() throws CloneNotSupportedException {
        PaintMap clone = (PaintMap) super.clone();
        clone.store = new HashMap();
        clone.store.putAll(this.store);
        return clone;
    }

    private void writeObject(ObjectOutputStream stream) throws IOException {
        stream.defaultWriteObject();
        stream.writeInt(this.store.size());
        for (Comparable key : this.store.keySet()) {
            stream.writeObject(key);
            SerialUtilities.writePaint(getPaint(key), stream);
        }
    }

    private void readObject(ObjectInputStream stream) throws IOException, ClassNotFoundException {
        stream.defaultReadObject();
        this.store = new HashMap();
        int keyCount = stream.readInt();
        for (int i = 0; i < keyCount; i++) {
            this.store.put((Comparable) stream.readObject(), SerialUtilities.readPaint(stream));
        }
    }
}
