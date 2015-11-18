package org.jfree.ui;

import java.io.ObjectStreamException;
import java.io.Serializable;

public final class VerticalAlignment implements Serializable {
    public static final VerticalAlignment BOTTOM;
    public static final VerticalAlignment CENTER;
    public static final VerticalAlignment TOP;
    private static final long serialVersionUID = 7272397034325429853L;
    private String name;

    static {
        TOP = new VerticalAlignment("VerticalAlignment.TOP");
        BOTTOM = new VerticalAlignment("VerticalAlignment.BOTTOM");
        CENTER = new VerticalAlignment("VerticalAlignment.CENTER");
    }

    private VerticalAlignment(String name) {
        this.name = name;
    }

    public String toString() {
        return this.name;
    }

    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof VerticalAlignment)) {
            return false;
        }
        if (this.name.equals(((VerticalAlignment) o).name)) {
            return true;
        }
        return false;
    }

    public int hashCode() {
        return this.name.hashCode();
    }

    private Object readResolve() throws ObjectStreamException {
        if (equals(TOP)) {
            return TOP;
        }
        if (equals(BOTTOM)) {
            return BOTTOM;
        }
        if (equals(CENTER)) {
            return CENTER;
        }
        return null;
    }
}
