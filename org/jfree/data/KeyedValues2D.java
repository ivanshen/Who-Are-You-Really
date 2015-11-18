package org.jfree.data;

import java.util.List;

public interface KeyedValues2D extends Values2D {
    int getColumnIndex(Comparable comparable);

    Comparable getColumnKey(int i);

    List getColumnKeys();

    int getRowIndex(Comparable comparable);

    Comparable getRowKey(int i);

    List getRowKeys();

    Number getValue(Comparable comparable, Comparable comparable2);
}
