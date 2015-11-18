package org.jfree.data.statistics;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import org.jfree.chart.util.ParamChecks;
import org.jfree.data.KeyedObjects2D;
import org.jfree.data.Range;
import org.jfree.data.RangeInfo;
import org.jfree.data.general.AbstractDataset;
import org.jfree.util.PublicCloneable;

public class DefaultMultiValueCategoryDataset extends AbstractDataset implements MultiValueCategoryDataset, RangeInfo, PublicCloneable {
    protected KeyedObjects2D data;
    private Number maximumRangeValue;
    private Number minimumRangeValue;
    private Range rangeBounds;

    public DefaultMultiValueCategoryDataset() {
        this.data = new KeyedObjects2D();
        this.minimumRangeValue = null;
        this.maximumRangeValue = null;
        this.rangeBounds = new Range(0.0d, 0.0d);
    }

    public void add(List values, Comparable rowKey, Comparable columnKey) {
        ParamChecks.nullNotPermitted(values, "values");
        ParamChecks.nullNotPermitted(rowKey, "rowKey");
        ParamChecks.nullNotPermitted(columnKey, "columnKey");
        List vlist = new ArrayList(values.size());
        Iterator iterator = values.listIterator();
        while (iterator.hasNext()) {
            Number obj = iterator.next();
            if (obj instanceof Number) {
                Number n = obj;
                if (!Double.isNaN(n.doubleValue())) {
                    vlist.add(n);
                }
            }
        }
        Collections.sort(vlist);
        this.data.addObject(vlist, rowKey, columnKey);
        if (vlist.size() > 0) {
            double maxval = Double.NEGATIVE_INFINITY;
            double minval = Double.POSITIVE_INFINITY;
            for (int i = 0; i < vlist.size(); i++) {
                double v = ((Number) vlist.get(i)).doubleValue();
                minval = Math.min(minval, v);
                maxval = Math.max(maxval, v);
            }
            if (this.maximumRangeValue == null) {
                this.maximumRangeValue = new Double(maxval);
            } else if (maxval > this.maximumRangeValue.doubleValue()) {
                this.maximumRangeValue = new Double(maxval);
            }
            if (this.minimumRangeValue == null) {
                this.minimumRangeValue = new Double(minval);
            } else if (minval < this.minimumRangeValue.doubleValue()) {
                this.minimumRangeValue = new Double(minval);
            }
            this.rangeBounds = new Range(this.minimumRangeValue.doubleValue(), this.maximumRangeValue.doubleValue());
        }
        fireDatasetChanged();
    }

    public List getValues(int row, int column) {
        List values = (List) this.data.getObject(row, column);
        if (values != null) {
            return Collections.unmodifiableList(values);
        }
        return Collections.EMPTY_LIST;
    }

    public List getValues(Comparable rowKey, Comparable columnKey) {
        return Collections.unmodifiableList((List) this.data.getObject(rowKey, columnKey));
    }

    public Number getValue(Comparable row, Comparable column) {
        List l = (List) this.data.getObject(row, column);
        double average = 0.0d;
        int count = 0;
        if (l != null && l.size() > 0) {
            for (int i = 0; i < l.size(); i++) {
                average += ((Number) l.get(i)).doubleValue();
                count++;
            }
            if (count > 0) {
                average /= (double) count;
            }
        }
        if (count == 0) {
            return null;
        }
        return new Double(average);
    }

    public Number getValue(int row, int column) {
        List l = (List) this.data.getObject(row, column);
        double average = 0.0d;
        int count = 0;
        if (l != null && l.size() > 0) {
            for (int i = 0; i < l.size(); i++) {
                average += ((Number) l.get(i)).doubleValue();
                count++;
            }
            if (count > 0) {
                average /= (double) count;
            }
        }
        if (count == 0) {
            return null;
        }
        return new Double(average);
    }

    public int getColumnIndex(Comparable key) {
        return this.data.getColumnIndex(key);
    }

    public Comparable getColumnKey(int column) {
        return this.data.getColumnKey(column);
    }

    public List getColumnKeys() {
        return this.data.getColumnKeys();
    }

    public int getRowIndex(Comparable key) {
        return this.data.getRowIndex(key);
    }

    public Comparable getRowKey(int row) {
        return this.data.getRowKey(row);
    }

    public List getRowKeys() {
        return this.data.getRowKeys();
    }

    public int getRowCount() {
        return this.data.getRowCount();
    }

    public int getColumnCount() {
        return this.data.getColumnCount();
    }

    public double getRangeLowerBound(boolean includeInterval) {
        if (this.minimumRangeValue != null) {
            return this.minimumRangeValue.doubleValue();
        }
        return Double.NaN;
    }

    public double getRangeUpperBound(boolean includeInterval) {
        if (this.maximumRangeValue != null) {
            return this.maximumRangeValue.doubleValue();
        }
        return Double.NaN;
    }

    public Range getRangeBounds(boolean includeInterval) {
        return this.rangeBounds;
    }

    public boolean equals(Object obj) {
        if (obj == this) {
            return true;
        }
        if (!(obj instanceof DefaultMultiValueCategoryDataset)) {
            return false;
        }
        return this.data.equals(((DefaultMultiValueCategoryDataset) obj).data);
    }

    public Object clone() throws CloneNotSupportedException {
        DefaultMultiValueCategoryDataset clone = (DefaultMultiValueCategoryDataset) super.clone();
        clone.data = (KeyedObjects2D) this.data.clone();
        return clone;
    }
}
