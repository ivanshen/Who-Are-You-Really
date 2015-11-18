package org.jfree.util;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.Arrays;

public class AbstractObjectList implements Cloneable, Serializable {
    public static final int DEFAULT_INITIAL_CAPACITY = 8;
    private static final long serialVersionUID = 7789833772597351595L;
    private int increment;
    private transient Object[] objects;
    private int size;

    protected AbstractObjectList() {
        this(DEFAULT_INITIAL_CAPACITY);
    }

    protected AbstractObjectList(int initialCapacity) {
        this(initialCapacity, initialCapacity);
    }

    protected AbstractObjectList(int initialCapacity, int increment) {
        this.size = 0;
        this.increment = DEFAULT_INITIAL_CAPACITY;
        this.objects = new Object[initialCapacity];
        this.increment = increment;
    }

    protected Object get(int index) {
        if (index < 0 || index >= this.size) {
            return null;
        }
        return this.objects[index];
    }

    protected void set(int index, Object object) {
        if (index < 0) {
            throw new IllegalArgumentException("Requires index >= 0.");
        }
        if (index >= this.objects.length) {
            Object[] enlarged = new Object[(this.increment + index)];
            System.arraycopy(this.objects, 0, enlarged, 0, this.objects.length);
            this.objects = enlarged;
        }
        this.objects[index] = object;
        this.size = Math.max(this.size, index + 1);
    }

    public void clear() {
        Arrays.fill(this.objects, null);
        this.size = 0;
    }

    public int size() {
        return this.size;
    }

    protected int indexOf(Object object) {
        for (int index = 0; index < this.size; index++) {
            if (this.objects[index] == object) {
                return index;
            }
        }
        return -1;
    }

    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (obj == this) {
            return true;
        }
        if (!(obj instanceof AbstractObjectList)) {
            return false;
        }
        AbstractObjectList other = (AbstractObjectList) obj;
        int listSize = size();
        for (int i = 0; i < listSize; i++) {
            if (!ObjectUtilities.equal(get(i), other.get(i))) {
                return false;
            }
        }
        return true;
    }

    public int hashCode() {
        return super.hashCode();
    }

    public Object clone() throws CloneNotSupportedException {
        AbstractObjectList clone = (AbstractObjectList) super.clone();
        if (this.objects != null) {
            clone.objects = new Object[this.objects.length];
            System.arraycopy(this.objects, 0, clone.objects, 0, this.objects.length);
        }
        return clone;
    }

    private void writeObject(ObjectOutputStream stream) throws IOException {
        stream.defaultWriteObject();
        int count = size();
        stream.writeInt(count);
        for (int i = 0; i < count; i++) {
            Object object = get(i);
            if (object == null || !(object instanceof Serializable)) {
                stream.writeInt(-1);
            } else {
                stream.writeInt(i);
                stream.writeObject(object);
            }
        }
    }

    private void readObject(ObjectInputStream stream) throws IOException, ClassNotFoundException {
        stream.defaultReadObject();
        this.objects = new Object[this.size];
        int count = stream.readInt();
        for (int i = 0; i < count; i++) {
            int index = stream.readInt();
            if (index != -1) {
                set(index, stream.readObject());
            }
        }
    }
}
