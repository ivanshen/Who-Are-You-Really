package org.jfree.chart.entity;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import org.jfree.chart.util.ParamChecks;
import org.jfree.util.ObjectUtilities;
import org.jfree.util.PublicCloneable;

public class StandardEntityCollection implements EntityCollection, Cloneable, PublicCloneable, Serializable {
    private static final long serialVersionUID = 5384773031184897047L;
    private List entities;

    public StandardEntityCollection() {
        this.entities = new ArrayList();
    }

    public int getEntityCount() {
        return this.entities.size();
    }

    public ChartEntity getEntity(int index) {
        return (ChartEntity) this.entities.get(index);
    }

    public void clear() {
        this.entities.clear();
    }

    public void add(ChartEntity entity) {
        ParamChecks.nullNotPermitted(entity, "entity");
        this.entities.add(entity);
    }

    public void addAll(EntityCollection collection) {
        this.entities.addAll(collection.getEntities());
    }

    public ChartEntity getEntity(double x, double y) {
        for (int i = this.entities.size() - 1; i >= 0; i--) {
            ChartEntity entity = (ChartEntity) this.entities.get(i);
            if (entity.getArea().contains(x, y)) {
                return entity;
            }
        }
        return null;
    }

    public Collection getEntities() {
        return Collections.unmodifiableCollection(this.entities);
    }

    public Iterator iterator() {
        return this.entities.iterator();
    }

    public boolean equals(Object obj) {
        if (obj == this) {
            return true;
        }
        if (!(obj instanceof StandardEntityCollection)) {
            return false;
        }
        return ObjectUtilities.equal(this.entities, ((StandardEntityCollection) obj).entities);
    }

    public Object clone() throws CloneNotSupportedException {
        StandardEntityCollection clone = (StandardEntityCollection) super.clone();
        clone.entities = new ArrayList(this.entities.size());
        for (int i = 0; i < this.entities.size(); i++) {
            clone.entities.add(((ChartEntity) this.entities.get(i)).clone());
        }
        return clone;
    }
}
