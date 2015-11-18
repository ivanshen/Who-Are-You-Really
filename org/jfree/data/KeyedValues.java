package org.jfree.data;

import java.util.List;

public interface KeyedValues extends Values {
    int getIndex(Comparable comparable);

    Comparable getKey(int i);

    List getKeys();

    Number getValue(Comparable comparable);
}
