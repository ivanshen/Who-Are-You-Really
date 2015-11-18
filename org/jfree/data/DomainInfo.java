package org.jfree.data;

public interface DomainInfo {
    Range getDomainBounds(boolean z);

    double getDomainLowerBound(boolean z);

    double getDomainUpperBound(boolean z);
}
