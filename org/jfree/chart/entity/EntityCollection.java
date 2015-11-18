package org.jfree.chart.entity;

import java.util.Collection;
import java.util.Iterator;

public interface EntityCollection {
    void add(ChartEntity chartEntity);

    void addAll(EntityCollection entityCollection);

    void clear();

    Collection getEntities();

    ChartEntity getEntity(double d, double d2);

    ChartEntity getEntity(int i);

    int getEntityCount();

    Iterator iterator();
}
