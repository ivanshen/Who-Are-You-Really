package org.jfree.chart.urls;

import java.io.Serializable;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.text.DateFormat;
import java.util.Date;
import org.jfree.chart.util.ParamChecks;
import org.jfree.data.xy.XYDataset;

public class TimeSeriesURLGenerator implements XYURLGenerator, Serializable {
    private static final long serialVersionUID = -9122773175671182445L;
    private DateFormat dateFormat;
    private String itemParameterName;
    private String prefix;
    private String seriesParameterName;

    public TimeSeriesURLGenerator() {
        this.dateFormat = DateFormat.getInstance();
        this.prefix = StandardXYURLGenerator.DEFAULT_PREFIX;
        this.seriesParameterName = StandardXYURLGenerator.DEFAULT_SERIES_PARAMETER;
        this.itemParameterName = StandardXYURLGenerator.DEFAULT_ITEM_PARAMETER;
    }

    public TimeSeriesURLGenerator(DateFormat dateFormat, String prefix, String seriesParameterName, String itemParameterName) {
        this.dateFormat = DateFormat.getInstance();
        this.prefix = StandardXYURLGenerator.DEFAULT_PREFIX;
        this.seriesParameterName = StandardXYURLGenerator.DEFAULT_SERIES_PARAMETER;
        this.itemParameterName = StandardXYURLGenerator.DEFAULT_ITEM_PARAMETER;
        ParamChecks.nullNotPermitted(dateFormat, "dateFormat");
        ParamChecks.nullNotPermitted(prefix, "prefix");
        ParamChecks.nullNotPermitted(seriesParameterName, "seriesParameterName");
        ParamChecks.nullNotPermitted(itemParameterName, "itemParameterName");
        this.dateFormat = (DateFormat) dateFormat.clone();
        this.prefix = prefix;
        this.seriesParameterName = seriesParameterName;
        this.itemParameterName = itemParameterName;
    }

    public DateFormat getDateFormat() {
        return (DateFormat) this.dateFormat.clone();
    }

    public String getPrefix() {
        return this.prefix;
    }

    public String getSeriesParameterName() {
        return this.seriesParameterName;
    }

    public String getItemParameterName() {
        return this.itemParameterName;
    }

    public String generateURL(XYDataset dataset, int series, int item) {
        String result = this.prefix;
        boolean firstParameter = !result.contains("?");
        Comparable seriesKey = dataset.getSeriesKey(series);
        if (seriesKey != null) {
            try {
                result = (result + (firstParameter ? "?" : "&amp;")) + this.seriesParameterName + "=" + URLEncoder.encode(seriesKey.toString(), "UTF-8");
                firstParameter = false;
            } catch (UnsupportedEncodingException ex) {
                throw new RuntimeException(ex);
            }
        }
        try {
            return (result + (firstParameter ? "?" : "&amp;")) + this.itemParameterName + "=" + URLEncoder.encode(this.dateFormat.format(new Date((long) dataset.getXValue(series, item))), "UTF-8");
        } catch (UnsupportedEncodingException ex2) {
            throw new RuntimeException(ex2);
        }
    }

    public boolean equals(Object obj) {
        if (obj == this) {
            return true;
        }
        if (!(obj instanceof TimeSeriesURLGenerator)) {
            return false;
        }
        TimeSeriesURLGenerator that = (TimeSeriesURLGenerator) obj;
        if (!this.dateFormat.equals(that.dateFormat)) {
            return false;
        }
        if (!this.itemParameterName.equals(that.itemParameterName)) {
            return false;
        }
        if (!this.prefix.equals(that.prefix)) {
            return false;
        }
        if (this.seriesParameterName.equals(that.seriesParameterName)) {
            return true;
        }
        return false;
    }
}
