package org.jfree.data;

import java.io.ObjectStreamException;
import java.io.Serializable;

public final class DomainOrder implements Serializable {
    public static final DomainOrder ASCENDING;
    public static final DomainOrder DESCENDING;
    public static final DomainOrder NONE;
    private static final long serialVersionUID = 4902774943512072627L;
    private String name;

    static {
        NONE = new DomainOrder("DomainOrder.NONE");
        ASCENDING = new DomainOrder("DomainOrder.ASCENDING");
        DESCENDING = new DomainOrder("DomainOrder.DESCENDING");
    }

    private DomainOrder(String name) {
        this.name = name;
    }

    public String toString() {
        return this.name;
    }

    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (!(obj instanceof DomainOrder)) {
            return false;
        }
        if (this.name.equals(((DomainOrder) obj).toString())) {
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
        if (equals(NONE)) {
            return NONE;
        }
        return null;
    }
}
