package org.jfree.data.time;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import org.jfree.chart.urls.StandardXYURLGenerator;
import org.jfree.chart.util.ParamChecks;
import org.jfree.data.general.Series;
import org.jfree.data.general.SeriesException;
import org.jfree.util.ObjectUtilities;

public class TimePeriodValues extends Series implements Serializable {
    protected static final String DEFAULT_DOMAIN_DESCRIPTION = "Time";
    protected static final String DEFAULT_RANGE_DESCRIPTION = "Value";
    static final long serialVersionUID = -2210593619794989709L;
    private List data;
    private String domain;
    private int maxEndIndex;
    private int maxMiddleIndex;
    private int maxStartIndex;
    private int minEndIndex;
    private int minMiddleIndex;
    private int minStartIndex;
    private String range;

    public TimePeriodValues(String name) {
        this(name, DEFAULT_DOMAIN_DESCRIPTION, DEFAULT_RANGE_DESCRIPTION);
    }

    public TimePeriodValues(String name, String domain, String range) {
        super(name);
        this.minStartIndex = -1;
        this.maxStartIndex = -1;
        this.minMiddleIndex = -1;
        this.maxMiddleIndex = -1;
        this.minEndIndex = -1;
        this.maxEndIndex = -1;
        this.domain = domain;
        this.range = range;
        this.data = new ArrayList();
    }

    public String getDomainDescription() {
        return this.domain;
    }

    public void setDomainDescription(String description) {
        String old = this.domain;
        this.domain = description;
        firePropertyChange("Domain", old, description);
    }

    public String getRangeDescription() {
        return this.range;
    }

    public void setRangeDescription(String description) {
        String old = this.range;
        this.range = description;
        firePropertyChange("Range", old, description);
    }

    public int getItemCount() {
        return this.data.size();
    }

    public TimePeriodValue getDataItem(int index) {
        return (TimePeriodValue) this.data.get(index);
    }

    public TimePeriod getTimePeriod(int index) {
        return getDataItem(index).getPeriod();
    }

    public Number getValue(int index) {
        return getDataItem(index).getValue();
    }

    public void add(TimePeriodValue item) {
        ParamChecks.nullNotPermitted(item, StandardXYURLGenerator.DEFAULT_ITEM_PARAMETER);
        this.data.add(item);
        updateBounds(item.getPeriod(), this.data.size() - 1);
        fireSeriesChanged();
    }

    private void updateBounds(TimePeriod period, int index) {
        long s;
        long start = period.getStart().getTime();
        long end = period.getEnd().getTime();
        long middle = start + ((end - start) / 2);
        if (this.minStartIndex >= 0) {
            if (start < getDataItem(this.minStartIndex).getPeriod().getStart().getTime()) {
                this.minStartIndex = index;
            }
        } else {
            this.minStartIndex = index;
        }
        if (this.maxStartIndex >= 0) {
            if (start > getDataItem(this.maxStartIndex).getPeriod().getStart().getTime()) {
                this.maxStartIndex = index;
            }
        } else {
            this.maxStartIndex = index;
        }
        if (this.minMiddleIndex >= 0) {
            s = getDataItem(this.minMiddleIndex).getPeriod().getStart().getTime();
            if (middle < s + ((getDataItem(this.minMiddleIndex).getPeriod().getEnd().getTime() - s) / 2)) {
                this.minMiddleIndex = index;
            }
        } else {
            this.minMiddleIndex = index;
        }
        if (this.maxMiddleIndex >= 0) {
            s = getDataItem(this.maxMiddleIndex).getPeriod().getStart().getTime();
            if (middle > s + ((getDataItem(this.maxMiddleIndex).getPeriod().getEnd().getTime() - s) / 2)) {
                this.maxMiddleIndex = index;
            }
        } else {
            this.maxMiddleIndex = index;
        }
        if (this.minEndIndex >= 0) {
            if (end < getDataItem(this.minEndIndex).getPeriod().getEnd().getTime()) {
                this.minEndIndex = index;
            }
        } else {
            this.minEndIndex = index;
        }
        if (this.maxEndIndex >= 0) {
            if (end > getDataItem(this.maxEndIndex).getPeriod().getEnd().getTime()) {
                this.maxEndIndex = index;
                return;
            }
            return;
        }
        this.maxEndIndex = index;
    }

    private void recalculateBounds() {
        this.minStartIndex = -1;
        this.minMiddleIndex = -1;
        this.minEndIndex = -1;
        this.maxStartIndex = -1;
        this.maxMiddleIndex = -1;
        this.maxEndIndex = -1;
        for (int i = 0; i < this.data.size(); i++) {
            updateBounds(((TimePeriodValue) this.data.get(i)).getPeriod(), i);
        }
    }

    public void add(TimePeriod period, double value) {
        add(new TimePeriodValue(period, value));
    }

    public void add(TimePeriod period, Number value) {
        add(new TimePeriodValue(period, value));
    }

    public void update(int index, Number value) {
        getDataItem(index).setValue(value);
        fireSeriesChanged();
    }

    public void delete(int start, int end) {
        for (int i = 0; i <= end - start; i++) {
            this.data.remove(start);
        }
        recalculateBounds();
        fireSeriesChanged();
    }

    public boolean equals(Object obj) {
        if (obj == this) {
            return true;
        }
        if (!(obj instanceof TimePeriodValues)) {
            return false;
        }
        if (!super.equals(obj)) {
            return false;
        }
        TimePeriodValues that = (TimePeriodValues) obj;
        if (!ObjectUtilities.equal(getDomainDescription(), that.getDomainDescription())) {
            return false;
        }
        if (!ObjectUtilities.equal(getRangeDescription(), that.getRangeDescription())) {
            return false;
        }
        int count = getItemCount();
        if (count != that.getItemCount()) {
            return false;
        }
        for (int i = 0; i < count; i++) {
            if (!getDataItem(i).equals(that.getDataItem(i))) {
                return false;
            }
        }
        return true;
    }

    public int hashCode() {
        int result;
        int i = 0;
        if (this.domain != null) {
            result = this.domain.hashCode();
        } else {
            result = 0;
        }
        int i2 = result * 29;
        if (this.range != null) {
            i = this.range.hashCode();
        }
        return ((((((((((((((i2 + i) * 29) + this.data.hashCode()) * 29) + this.minStartIndex) * 29) + this.maxStartIndex) * 29) + this.minMiddleIndex) * 29) + this.maxMiddleIndex) * 29) + this.minEndIndex) * 29) + this.maxEndIndex;
    }

    public Object clone() throws CloneNotSupportedException {
        return createCopy(0, getItemCount() - 1);
    }

    public TimePeriodValues createCopy(int start, int end) throws CloneNotSupportedException {
        TimePeriodValues copy = (TimePeriodValues) super.clone();
        copy.data = new ArrayList();
        if (this.data.size() > 0) {
            for (int index = start; index <= end; index++) {
                try {
                    copy.add((TimePeriodValue) ((TimePeriodValue) this.data.get(index)).clone());
                } catch (SeriesException e) {
                    System.err.println("Failed to add cloned item.");
                }
            }
        }
        return copy;
    }

    public int getMinStartIndex() {
        return this.minStartIndex;
    }

    public int getMaxStartIndex() {
        return this.maxStartIndex;
    }

    public int getMinMiddleIndex() {
        return this.minMiddleIndex;
    }

    public int getMaxMiddleIndex() {
        return this.maxMiddleIndex;
    }

    public int getMinEndIndex() {
        return this.minEndIndex;
    }

    public int getMaxEndIndex() {
        return this.maxEndIndex;
    }
}
