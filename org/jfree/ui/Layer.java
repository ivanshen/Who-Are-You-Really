package org.jfree.ui;

import java.io.ObjectStreamException;
import java.io.Serializable;

public final class Layer implements Serializable {
    public static final Layer BACKGROUND;
    public static final Layer FOREGROUND;
    private static final long serialVersionUID = -1470104570733183430L;
    private String name;

    static {
        FOREGROUND = new Layer("Layer.FOREGROUND");
        BACKGROUND = new Layer("Layer.BACKGROUND");
    }

    private Layer(String name) {
        this.name = name;
    }

    public String toString() {
        return this.name;
    }

    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Layer)) {
            return false;
        }
        if (this.name.equals(((Layer) o).name)) {
            return true;
        }
        return false;
    }

    public int hashCode() {
        return this.name.hashCode();
    }

    private Object readResolve() throws ObjectStreamException {
        if (equals(FOREGROUND)) {
            return FOREGROUND;
        }
        if (equals(BACKGROUND)) {
            return BACKGROUND;
        }
        return null;
    }
}
