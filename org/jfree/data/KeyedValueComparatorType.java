package org.jfree.data;

import java.io.Serializable;

public final class KeyedValueComparatorType implements Serializable {
    public static final KeyedValueComparatorType BY_KEY;
    public static final KeyedValueComparatorType BY_VALUE;
    private String name;

    static {
        BY_KEY = new KeyedValueComparatorType("KeyedValueComparatorType.BY_KEY");
        BY_VALUE = new KeyedValueComparatorType("KeyedValueComparatorType.BY_VALUE");
    }

    private KeyedValueComparatorType(String name) {
        this.name = name;
    }

    public String toString() {
        return this.name;
    }

    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof KeyedValueComparatorType)) {
            return false;
        }
        if (this.name.equals(((KeyedValueComparatorType) o).name)) {
            return true;
        }
        return false;
    }

    public int hashCode() {
        return this.name.hashCode();
    }
}
