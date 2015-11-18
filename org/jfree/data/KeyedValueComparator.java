package org.jfree.data;

import java.io.Serializable;
import java.util.Comparator;
import org.jfree.chart.util.ParamChecks;
import org.jfree.util.SortOrder;

public class KeyedValueComparator implements Comparator, Serializable {
    private SortOrder order;
    private KeyedValueComparatorType type;

    public KeyedValueComparator(KeyedValueComparatorType type, SortOrder order) {
        ParamChecks.nullNotPermitted(type, "type");
        ParamChecks.nullNotPermitted(order, "order");
        this.type = type;
        this.order = order;
    }

    public KeyedValueComparatorType getType() {
        return this.type;
    }

    public SortOrder getOrder() {
        return this.order;
    }

    public int compare(Object o1, Object o2) {
        if (o2 == null) {
            return -1;
        }
        if (o1 == null) {
            return 1;
        }
        KeyedValue kv1 = (KeyedValue) o1;
        KeyedValue kv2 = (KeyedValue) o2;
        if (this.type == KeyedValueComparatorType.BY_KEY) {
            if (this.order.equals(SortOrder.ASCENDING)) {
                return kv1.getKey().compareTo(kv2.getKey());
            }
            if (this.order.equals(SortOrder.DESCENDING)) {
                return kv2.getKey().compareTo(kv1.getKey());
            }
            throw new IllegalArgumentException("Unrecognised sort order.");
        } else if (this.type == KeyedValueComparatorType.BY_VALUE) {
            Number n1 = kv1.getValue();
            Number n2 = kv2.getValue();
            if (n2 == null) {
                return -1;
            }
            if (n1 == null) {
                return 1;
            }
            double d1 = n1.doubleValue();
            double d2 = n2.doubleValue();
            if (this.order.equals(SortOrder.ASCENDING)) {
                if (d1 > d2) {
                    return 1;
                }
                if (d1 < d2) {
                    return -1;
                }
                return 0;
            } else if (!this.order.equals(SortOrder.DESCENDING)) {
                throw new IllegalArgumentException("Unrecognised sort order.");
            } else if (d1 > d2) {
                return -1;
            } else {
                if (d1 < d2) {
                    return 1;
                }
                return 0;
            }
        } else {
            throw new IllegalArgumentException("Unrecognised type.");
        }
    }
}
