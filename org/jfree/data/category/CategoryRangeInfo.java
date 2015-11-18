package org.jfree.data.category;

import java.util.List;
import org.jfree.data.Range;

public interface CategoryRangeInfo {
    Range getRangeBounds(List list, boolean z);
}
