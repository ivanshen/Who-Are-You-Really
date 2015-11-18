package org.jfree.util;

import java.io.ObjectStreamException;
import java.io.Serializable;

public final class SortOrder implements Serializable {
    public static final SortOrder ASCENDING;
    public static final SortOrder DESCENDING;
    private static final long serialVersionUID = -2124469847758108312L;
    private String name;

    static {
        ASCENDING = new SortOrder("SortOrder.ASCENDING");
        DESCENDING = new SortOrder("SortOrder.DESCENDING");
    }

    private SortOrder(String name) {
        this.name = name;
    }

    public String toString() {
        return this.name;
    }

    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (!(obj instanceof SortOrder)) {
            return false;
        }
        if (this.name.equals(((SortOrder) obj).toString())) {
            return true;
        }
        return false;
    }

    public int hashCode() {
        return this.name.hashCode();
    }

    private Object readResolve() throws ObjectStreamException {
        if (equals(ASCENDING)) {
            return ASCENDING;
        }
        if (equals(DESCENDING)) {
            return DESCENDING;
        }
        return null;
    }
}
