package org.jfree.data.xy;

import java.util.List;
import org.jfree.data.Range;

public interface XYDomainInfo {
    Range getDomainBounds(List list, boolean z);
}
