package org.jfree.util;

import java.io.ObjectStreamException;
import java.io.Serializable;

public final class TableOrder implements Serializable {
    public static final TableOrder BY_COLUMN;
    public static final TableOrder BY_ROW;
    private static final long serialVersionUID = 525193294068177057L;
    private String name;

    static {
        BY_ROW = new TableOrder("TableOrder.BY_ROW");
        BY_COLUMN = new TableOrder("TableOrder.BY_COLUMN");
    }

    private TableOrder(String name) {
        this.name = name;
    }

    public String toString() {
        return this.name;
    }

    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (!(obj instanceof TableOrder)) {
            return false;
        }
        if (this.name.equals(((TableOrder) obj).name)) {
            return true;
        }
        return false;
    }

    public int hashCode() {
        return this.name.hashCode();
    }

    private Object readResolve() throws ObjectStreamException {
        if (equals(BY_ROW)) {
            return BY_ROW;
        }
        if (equals(BY_COLUMN)) {
            return BY_COLUMN;
        }
        return null;
    }
}
