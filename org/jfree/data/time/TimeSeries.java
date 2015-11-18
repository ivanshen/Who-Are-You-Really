package org.jfree.data.time;

import java.io.Serializable;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;
import org.jfree.chart.urls.StandardXYURLGenerator;
import org.jfree.chart.util.ParamChecks;
import org.jfree.data.Range;
import org.jfree.data.general.Series;
import org.jfree.data.general.SeriesException;
import org.jfree.util.ObjectUtilities;

public class TimeSeries extends Series implements Cloneable, Serializable {
    protected static final String DEFAULT_DOMAIN_DESCRIPTION = "Time";
    protected static final String DEFAULT_RANGE_DESCRIPTION = "Value";
    private static final long serialVersionUID = -5032960206869675528L;
    protected List data;
    private String domain;
    private double maxY;
    private long maximumItemAge;
    private int maximumItemCount;
    private double minY;
    private String range;
    protected Class timePeriodClass;

    public TimeSeries(Comparable name) {
        this(name, DEFAULT_DOMAIN_DESCRIPTION, DEFAULT_RANGE_DESCRIPTION);
    }

    public TimeSeries(Comparable name, String domain, String range) {
        super(name);
        this.domain = domain;
        this.range = range;
        this.timePeriodClass = null;
        this.data = new ArrayList();
        this.maximumItemCount = Integer.MAX_VALUE;
        this.maximumItemAge = Long.MAX_VALUE;
        this.minY = Double.NaN;
        this.maxY = Double.NaN;
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

    public List getItems() {
        return Collections.unmodifiableList(this.data);
    }

    public int getMaximumItemCount() {
        return this.maximumItemCount;
    }

    public void setMaximumItemCount(int maximum) {
        if (maximum < 0) {
            throw new IllegalArgumentException("Negative 'maximum' argument.");
        }
        this.maximumItemCount = maximum;
        int count = this.data.size();
        if (count > maximum) {
            delete(0, (count - maximum) - 1);
        }
    }

    public long getMaximumItemAge() {
        return this.maximumItemAge;
    }

    public void setMaximumItemAge(long periods) {
        if (periods < 0) {
            throw new IllegalArgumentException("Negative 'periods' argument.");
        }
        this.maximumItemAge = periods;
        removeAgedItems(true);
    }

    public Range findValueRange() {
        if (this.data.isEmpty()) {
            return null;
        }
        return new Range(this.minY, this.maxY);
    }

    public Range findValueRange(Range xRange, TimeZone timeZone) {
        return findValueRange(xRange, TimePeriodAnchor.MIDDLE, timeZone);
    }

    public Range findValueRange(Range xRange, TimePeriodAnchor xAnchor, TimeZone zone) {
        ParamChecks.nullNotPermitted(xRange, "xRange");
        ParamChecks.nullNotPermitted(xAnchor, "xAnchor");
        ParamChecks.nullNotPermitted(zone, "zone");
        if (this.data.isEmpty()) {
            return null;
        }
        Calendar calendar = Calendar.getInstance(zone);
        double lowY = Double.POSITIVE_INFINITY;
        double highY = Double.NEGATIVE_INFINITY;
        int i = 0;
        while (true) {
            if (i >= this.data.size()) {
                break;
            }
            TimeSeriesDataItem item = (TimeSeriesDataItem) this.data.get(i);
            if (xRange.contains((double) item.getPeriod().getMillisecond(xAnchor, calendar))) {
                Number n = item.getValue();
                if (n != null) {
                    double v = n.doubleValue();
                    lowY = Math.min(lowY, v);
                    highY = Math.max(highY, v);
                }
            }
            i++;
        }
        if (!Double.isInfinite(lowY) || !Double.isInfinite(highY)) {
            return new Range(lowY, highY);
        }
        if (lowY < highY) {
            return new Range(lowY, highY);
        }
        return new Range(Double.NaN, Double.NaN);
    }

    public double getMinY() {
        return this.minY;
    }

    public double getMaxY() {
        return this.maxY;
    }

    public Class getTimePeriodClass() {
        return this.timePeriodClass;
    }

    public TimeSeriesDataItem getDataItem(int index) {
        return (TimeSeriesDataItem) ((TimeSeriesDataItem) this.data.get(index)).clone();
    }

    public TimeSeriesDataItem getDataItem(RegularTimePeriod period) {
        int index = getIndex(period);
        if (index >= 0) {
            return getDataItem(index);
        }
        return null;
    }

    TimeSeriesDataItem getRawDataItem(int index) {
        return (TimeSeriesDataItem) this.data.get(index);
    }

    TimeSeriesDataItem getRawDataItem(RegularTimePeriod period) {
        int index = getIndex(period);
        if (index >= 0) {
            return (TimeSeriesDataItem) this.data.get(index);
        }
        return null;
    }

    public RegularTimePeriod getTimePeriod(int index) {
        return getRawDataItem(index).getPeriod();
    }

    public RegularTimePeriod getNextTimePeriod() {
        return getTimePeriod(getItemCount() - 1).next();
    }

    public Collection getTimePeriods() {
        Collection result = new ArrayList();
        for (int i = 0; i < getItemCount(); i++) {
            result.add(getTimePeriod(i));
        }
        return result;
    }

    public Collection getTimePeriodsUniqueToOtherSeries(TimeSeries series) {
        Collection result = new ArrayList();
        for (int i = 0; i < series.getItemCount(); i++) {
            RegularTimePeriod period = series.getTimePeriod(i);
            if (getIndex(period) < 0) {
                result.add(period);
            }
        }
        return result;
    }

    public int getIndex(RegularTimePeriod period) {
        ParamChecks.nullNotPermitted(period, "period");
        return Collections.binarySearch(this.data, new TimeSeriesDataItem(period, -2.147483648E9d));
    }

    public Number getValue(int index) {
        return getRawDataItem(index).getValue();
    }

    public Number getValue(RegularTimePeriod period) {
        int index = getIndex(period);
        if (index >= 0) {
            return getValue(index);
        }
        return null;
    }

    public void add(TimeSeriesDataItem item) {
        add(item, true);
    }

    public void add(TimeSeriesDataItem item, boolean notify) {
        boolean added;
        ParamChecks.nullNotPermitted(item, StandardXYURLGenerator.DEFAULT_ITEM_PARAMETER);
        item = (TimeSeriesDataItem) item.clone();
        Class c = item.getPeriod().getClass();
        if (this.timePeriodClass == null) {
            this.timePeriodClass = c;
        } else if (!this.timePeriodClass.equals(c)) {
            StringBuilder b = new StringBuilder();
            b.append("You are trying to add data where the time period class ");
            b.append("is ");
            b.append(item.getPeriod().getClass().getName());
            b.append(", but the TimeSeries is expecting an instance of ");
            b.append(this.timePeriodClass.getName());
            b.append(".");
            throw new SeriesException(b.toString());
        }
        if (getItemCount() == 0) {
            this.data.add(item);
            added = true;
        } else {
            if (item.getPeriod().compareTo(getTimePeriod(getItemCount() - 1)) > 0) {
                this.data.add(item);
                added = true;
            } else {
                int index = Collections.binarySearch(this.data, item);
                if (index < 0) {
                    this.data.add((-index) - 1, item);
                    added = true;
                } else {
                    b = new StringBuilder();
                    b.append("You are attempting to add an observation for ");
                    b.append("the time period ");
                    b.append(item.getPeriod().toString());
                    b.append(" but the series already contains an observation");
                    b.append(" for that time period. Duplicates are not ");
                    b.append("permitted.  Try using the addOrUpdate() method.");
                    throw new SeriesException(b.toString());
                }
            }
        }
        if (added) {
            updateBoundsForAddedItem(item);
            if (getItemCount() > this.maximumItemCount) {
                updateBoundsForRemovedItem((TimeSeriesDataItem) this.data.remove(0));
            }
            removeAgedItems(false);
            if (notify) {
                fireSeriesChanged();
            }
        }
    }

    public void add(RegularTimePeriod period, double value) {
        add(period, value, true);
    }

    public void add(RegularTimePeriod period, double value, boolean notify) {
        add(new TimeSeriesDataItem(period, value), notify);
    }

    public void add(RegularTimePeriod period, Number value) {
        add(period, value, true);
    }

    public void add(RegularTimePeriod period, Number value, boolean notify) {
        add(new TimeSeriesDataItem(period, value), notify);
    }

    public void update(RegularTimePeriod period, double value) {
        update(period, new Double(value));
    }

    public void update(RegularTimePeriod period, Number value) {
        int index = Collections.binarySearch(this.data, new TimeSeriesDataItem(period, value));
        if (index < 0) {
            throw new SeriesException("There is no existing value for the specified 'period'.");
        }
        update(index, value);
    }

    public void update(int index, Number value) {
        TimeSeriesDataItem item = (TimeSeriesDataItem) this.data.get(index);
        boolean iterate = false;
        Number oldYN = item.getValue();
        if (oldYN != null) {
            double oldY = oldYN.doubleValue();
            if (!Double.isNaN(oldY)) {
                iterate = oldY <= this.minY || oldY >= this.maxY;
            }
        }
        item.setValue(value);
        if (iterate) {
            updateMinMaxYByIteration();
        } else if (value != null) {
            double yy = value.doubleValue();
            this.minY = minIgnoreNaN(this.minY, yy);
            this.maxY = maxIgnoreNaN(this.maxY, yy);
        }
        fireSeriesChanged();
    }

    public TimeSeries addAndOrUpdate(TimeSeries series) {
        TimeSeries overwritten = new TimeSeries("Overwritten values from: " + getKey());
        for (int i = 0; i < series.getItemCount(); i++) {
            TimeSeriesDataItem item = series.getRawDataItem(i);
            TimeSeriesDataItem oldItem = addOrUpdate(item.getPeriod(), item.getValue());
            if (oldItem != null) {
                overwritten.add(oldItem);
            }
        }
        return overwritten;
    }

    public TimeSeriesDataItem addOrUpdate(RegularTimePeriod period, double value) {
        return addOrUpdate(period, new Double(value));
    }

    public TimeSeriesDataItem addOrUpdate(RegularTimePeriod period, Number value) {
        return addOrUpdate(new TimeSeriesDataItem(period, value));
    }

    public TimeSeriesDataItem addOrUpdate(TimeSeriesDataItem item) {
        ParamChecks.nullNotPermitted(item, StandardXYURLGenerator.DEFAULT_ITEM_PARAMETER);
        Class periodClass = item.getPeriod().getClass();
        if (this.timePeriodClass == null) {
            this.timePeriodClass = periodClass;
        } else if (!this.timePeriodClass.equals(periodClass)) {
            throw new SeriesException("You are trying to add data where the time period class is " + periodClass.getName() + ", but the TimeSeries is expecting an instance of " + this.timePeriodClass.getName() + ".");
        }
        TimeSeriesDataItem overwritten = null;
        int index = Collections.binarySearch(this.data, item);
        if (index >= 0) {
            TimeSeriesDataItem existing = (TimeSeriesDataItem) this.data.get(index);
            overwritten = (TimeSeriesDataItem) existing.clone();
            boolean iterate = false;
            Number oldYN = existing.getValue();
            double oldY = oldYN != null ? oldYN.doubleValue() : Double.NaN;
            if (!Double.isNaN(oldY)) {
                iterate = oldY <= this.minY || oldY >= this.maxY;
            }
            existing.setValue(item.getValue());
            if (iterate) {
                updateMinMaxYByIteration();
            } else if (item.getValue() != null) {
                double yy = item.getValue().doubleValue();
                this.minY = minIgnoreNaN(this.minY, yy);
                this.maxY = maxIgnoreNaN(this.maxY, yy);
            }
        } else {
            item = (TimeSeriesDataItem) item.clone();
            this.data.add((-index) - 1, item);
            updateBoundsForAddedItem(item);
            if (getItemCount() > this.maximumItemCount) {
                updateBoundsForRemovedItem((TimeSeriesDataItem) this.data.remove(0));
            }
        }
        removeAgedItems(false);
        fireSeriesChanged();
        return overwritten;
    }

    public void removeAgedItems(boolean notify) {
        if (getItemCount() > 1) {
            long latest = getTimePeriod(getItemCount() - 1).getSerialIndex();
            boolean removed = false;
            while (latest - getTimePeriod(0).getSerialIndex() > this.maximumItemAge) {
                this.data.remove(0);
                removed = true;
            }
            if (removed) {
                updateMinMaxYByIteration();
                if (notify) {
                    fireSeriesChanged();
                }
            }
        }
    }

    public void removeAgedItems(long latest, boolean notify) {
        if (!this.data.isEmpty()) {
            try {
                long index = ((RegularTimePeriod) RegularTimePeriod.class.getDeclaredMethod("createInstance", new Class[]{Class.class, Date.class, TimeZone.class}).invoke(this.timePeriodClass, new Object[]{this.timePeriodClass, new Date(latest), TimeZone.getDefault()})).getSerialIndex();
                boolean removed = false;
                while (getItemCount() > 0 && index - getTimePeriod(0).getSerialIndex() > this.maximumItemAge) {
                    this.data.remove(0);
                    removed = true;
                }
                if (removed) {
                    updateMinMaxYByIteration();
                    if (notify) {
                        fireSeriesChanged();
                    }
                }
            } catch (NoSuchMethodException e) {
                throw new RuntimeException(e);
            } catch (IllegalAccessException e2) {
                throw new RuntimeException(e2);
            } catch (InvocationTargetException e3) {
                throw new RuntimeException(e3);
            }
        }
    }

    public void clear() {
        if (this.data.size() > 0) {
            this.data.clear();
            this.timePeriodClass = null;
            this.minY = Double.NaN;
            this.maxY = Double.NaN;
            fireSeriesChanged();
        }
    }

    public void delete(RegularTimePeriod period) {
        int index = getIndex(period);
        if (index >= 0) {
            updateBoundsForRemovedItem((TimeSeriesDataItem) this.data.remove(index));
            if (this.data.isEmpty()) {
                this.timePeriodClass = null;
            }
            fireSeriesChanged();
        }
    }

    public void delete(int start, int end) {
        delete(start, end, true);
    }

    public void delete(int start, int end, boolean notify) {
        if (end < start) {
            throw new IllegalArgumentException("Requires start <= end.");
        }
        for (int i = 0; i <= end - start; i++) {
            this.data.remove(start);
        }
        updateMinMaxYByIteration();
        if (this.data.isEmpty()) {
            this.timePeriodClass = null;
        }
        if (notify) {
            fireSeriesChanged();
        }
    }

    public Object clone() throws CloneNotSupportedException {
        TimeSeries clone = (TimeSeries) super.clone();
        clone.data = (List) ObjectUtilities.deepClone(this.data);
        return clone;
    }

    public TimeSeries createCopy(int start, int end) throws CloneNotSupportedException {
        if (start < 0) {
            throw new IllegalArgumentException("Requires start >= 0.");
        } else if (end < start) {
            throw new IllegalArgumentException("Requires start <= end.");
        } else {
            TimeSeries copy = (TimeSeries) super.clone();
            copy.minY = Double.NaN;
            copy.maxY = Double.NaN;
            copy.data = new ArrayList();
            if (this.data.size() > 0) {
                int index = start;
                while (index <= end) {
                    try {
                        copy.add((TimeSeriesDataItem) ((TimeSeriesDataItem) this.data.get(index)).clone());
                        index++;
                    } catch (SeriesException e) {
                        throw new RuntimeException(e);
                    }
                }
            }
            return copy;
        }
    }

    public TimeSeries createCopy(RegularTimePeriod start, RegularTimePeriod end) throws CloneNotSupportedException {
        ParamChecks.nullNotPermitted(start, "start");
        ParamChecks.nullNotPermitted(end, "end");
        if (start.compareTo(end) > 0) {
            throw new IllegalArgumentException("Requires start on or before end.");
        }
        boolean emptyRange = false;
        int startIndex = getIndex(start);
        if (startIndex < 0) {
            startIndex = -(startIndex + 1);
            if (startIndex == this.data.size()) {
                emptyRange = true;
            }
        }
        int endIndex = getIndex(end);
        if (endIndex < 0) {
            endIndex = (-(endIndex + 1)) - 1;
        }
        if (endIndex < 0 || endIndex < startIndex) {
            emptyRange = true;
        }
        if (!emptyRange) {
            return createCopy(startIndex, endIndex);
        }
        TimeSeries copy = (TimeSeries) super.clone();
        copy.data = new ArrayList();
        return copy;
    }

    public boolean equals(Object obj) {
        if (obj == this) {
            return true;
        }
        if (!(obj instanceof TimeSeries)) {
            return false;
        }
        TimeSeries that = (TimeSeries) obj;
        if (ObjectUtilities.equal(getDomainDescription(), that.getDomainDescription()) && ObjectUtilities.equal(getRangeDescription(), that.getRangeDescription()) && ObjectUtilities.equal(this.timePeriodClass, that.timePeriodClass) && getMaximumItemAge() == that.getMaximumItemAge() && getMaximumItemCount() == that.getMaximumItemCount() && getItemCount() == that.getItemCount() && ObjectUtilities.equal(this.data, that.data)) {
            return super.equals(obj);
        }
        return false;
    }

    public int hashCode() {
        int hashCode;
        int hashCode2 = super.hashCode() * 29;
        if (this.domain != null) {
            hashCode = this.domain.hashCode();
        } else {
            hashCode = 0;
        }
        hashCode2 = (hashCode2 + hashCode) * 29;
        if (this.range != null) {
            hashCode = this.range.hashCode();
        } else {
            hashCode = 0;
        }
        hashCode2 = (hashCode2 + hashCode) * 29;
        if (this.timePeriodClass != null) {
            hashCode = this.timePeriodClass.hashCode();
        } else {
            hashCode = 0;
        }
        int result = hashCode2 + hashCode;
        int count = getItemCount();
        if (count > 0) {
            result = (result * 29) + getRawDataItem(0).hashCode();
        }
        if (count > 1) {
            result = (result * 29) + getRawDataItem(count - 1).hashCode();
        }
        if (count > 2) {
            result = (result * 29) + getRawDataItem(count / 2).hashCode();
        }
        return (((result * 29) + this.maximumItemCount) * 29) + ((int) this.maximumItemAge);
    }

    private void updateBoundsForAddedItem(TimeSeriesDataItem item) {
        Number yN = item.getValue();
        if (item.getValue() != null) {
            double y = yN.doubleValue();
            this.minY = minIgnoreNaN(this.minY, y);
            this.maxY = maxIgnoreNaN(this.maxY, y);
        }
    }

    private void updateBoundsForRemovedItem(TimeSeriesDataItem item) {
        Number yN = item.getValue();
        if (yN != null) {
            double y = yN.doubleValue();
            if (!Double.isNaN(y)) {
                if (y <= this.minY || y >= this.maxY) {
                    updateMinMaxYByIteration();
                }
            }
        }
    }

    private void updateMinMaxYByIteration() {
        this.minY = Double.NaN;
        this.maxY = Double.NaN;
        for (TimeSeriesDataItem item : this.data) {
            updateBoundsForAddedItem(item);
        }
    }

    private double minIgnoreNaN(double a, double b) {
        if (Double.isNaN(a)) {
            return b;
        }
        if (Double.isNaN(b)) {
            return a;
        }
        return Math.min(a, b);
    }

    private double maxIgnoreNaN(double a, double b) {
        if (Double.isNaN(a)) {
            return b;
        }
        if (Double.isNaN(b)) {
            return a;
        }
        return Math.max(a, b);
    }

    public TimeSeries(Comparable name, Class timePeriodClass) {
        this(name, DEFAULT_DOMAIN_DESCRIPTION, DEFAULT_RANGE_DESCRIPTION, timePeriodClass);
    }

    public TimeSeries(Comparable name, String domain, String range, Class timePeriodClass) {
        super(name);
        this.domain = domain;
        this.range = range;
        this.timePeriodClass = timePeriodClass;
        this.data = new ArrayList();
        this.maximumItemCount = Integer.MAX_VALUE;
        this.maximumItemAge = Long.MAX_VALUE;
        this.minY = Double.NaN;
        this.maxY = Double.NaN;
    }
}
