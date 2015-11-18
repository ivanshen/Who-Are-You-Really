package org.jfree.data.time.ohlc;

import org.jfree.chart.urls.StandardXYURLGenerator;
import org.jfree.chart.util.ParamChecks;
import org.jfree.data.ComparableObjectItem;
import org.jfree.data.ComparableObjectSeries;
import org.jfree.data.time.RegularTimePeriod;

public class OHLCSeries extends ComparableObjectSeries {
    public OHLCSeries(Comparable key) {
        super(key, true, false);
    }

    public RegularTimePeriod getPeriod(int index) {
        return ((OHLCItem) getDataItem(index)).getPeriod();
    }

    public ComparableObjectItem getDataItem(int index) {
        return super.getDataItem(index);
    }

    public void add(RegularTimePeriod period, double open, double high, double low, double close) {
        if (getItemCount() > 0) {
            if (!period.getClass().equals(((OHLCItem) getDataItem(0)).getPeriod().getClass())) {
                throw new IllegalArgumentException("Can't mix RegularTimePeriod class types.");
            }
        }
        super.add(new OHLCItem(period, open, high, low, close), true);
    }

    public void add(OHLCItem item) {
        ParamChecks.nullNotPermitted(item, StandardXYURLGenerator.DEFAULT_ITEM_PARAMETER);
        add(item.getPeriod(), item.getOpenValue(), item.getHighValue(), item.getLowValue(), item.getCloseValue());
    }

    public ComparableObjectItem remove(int index) {
        return super.remove(index);
    }
}
