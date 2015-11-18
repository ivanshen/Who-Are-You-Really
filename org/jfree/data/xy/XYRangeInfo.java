package org.jfree.data.xy;

import java.util.List;
import org.jfree.data.Range;

public interface XYRangeInfo {
    Range getRangeBounds(List list, Range range, boolean z);
}
