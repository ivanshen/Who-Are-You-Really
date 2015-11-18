package org.jfree.chart.urls;

import java.io.Serializable;
import org.jfree.chart.util.ParamChecks;
import org.jfree.data.xy.XYDataset;
import org.jfree.util.ObjectUtilities;

public class StandardXYURLGenerator implements XYURLGenerator, Serializable {
    public static final String DEFAULT_ITEM_PARAMETER = "item";
    public static final String DEFAULT_PREFIX = "index.html";
    public static final String DEFAULT_SERIES_PARAMETER = "series";
    private static final long serialVersionUID = -1771624523496595382L;
    private String itemParameterName;
    private String prefix;
    private String seriesParameterName;

    public StandardXYURLGenerator() {
        this(DEFAULT_PREFIX, DEFAULT_SERIES_PARAMETER, DEFAULT_ITEM_PARAMETER);
    }

    public StandardXYURLGenerator(String prefix) {
        this(prefix, DEFAULT_SERIES_PARAMETER, DEFAULT_ITEM_PARAMETER);
    }

    public StandardXYURLGenerator(String prefix, String seriesParameterName, String itemParameterName) {
        ParamChecks.nullNotPermitted(prefix, "prefix");
        ParamChecks.nullNotPermitted(seriesParameterName, "seriesParameterName");
        ParamChecks.nullNotPermitted(itemParameterName, "itemParameterName");
        this.prefix = prefix;
        this.seriesParameterName = seriesParameterName;
        this.itemParameterName = itemParameterName;
    }

    public String generateURL(XYDataset dataset, int series, int item) {
        String url = this.prefix;
        return (url + (url.indexOf("?") == -1 ? "?" : "&amp;")) + this.seriesParameterName + "=" + series + "&amp;" + this.itemParameterName + "=" + item;
    }

    public boolean equals(Object obj) {
        if (obj == this) {
            return true;
        }
        if (!(obj instanceof StandardXYURLGenerator)) {
            return false;
        }
        StandardXYURLGenerator that = (StandardXYURLGenerator) obj;
        if (!ObjectUtilities.equal(that.prefix, this.prefix)) {
            return false;
        }
        if (!ObjectUtilities.equal(that.seriesParameterName, this.seriesParameterName)) {
            return false;
        }
        if (ObjectUtilities.equal(that.itemParameterName, this.itemParameterName)) {
            return true;
        }
        return false;
    }
}
