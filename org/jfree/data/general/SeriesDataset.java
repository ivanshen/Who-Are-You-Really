package org.jfree.data.general;

public interface SeriesDataset extends Dataset {
    int getSeriesCount();

    Comparable getSeriesKey(int i);

    int indexOf(Comparable comparable);
}
