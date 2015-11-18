package org.jfree.ui;

import java.io.ObjectStreamException;
import java.io.Serializable;

public final class HorizontalAlignment implements Serializable {
    public static final HorizontalAlignment CENTER;
    public static final HorizontalAlignment LEFT;
    public static final HorizontalAlignment RIGHT;
    private static final long serialVersionUID = -8249740987565309567L;
    private String name;

    static {
        LEFT = new HorizontalAlignment("HorizontalAlignment.LEFT");
        RIGHT = new HorizontalAlignment("HorizontalAlignment.RIGHT");
        CENTER = new HorizontalAlignment("HorizontalAlignment.CENTER");
    }

    private HorizontalAlignment(String name) {
        this.name = name;
    }

    public String toString() {
        return this.name;
    }

    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (!(obj instanceof HorizontalAlignment)) {
            return false;
        }
        if (this.name.equals(((HorizontalAlignment) obj).name)) {
            return true;
        }
        return false;
    }

    public int hashCode() {
        return this.name.hashCode();
    }

    private Object readResolve() throws ObjectStreamException {
        if (equals(LEFT)) {
            return LEFT;
        }
        if (equals(RIGHT)) {
            return RIGHT;
        }
        if (equals(CENTER)) {
            return CENTER;
        }
        return null;
    }
}
