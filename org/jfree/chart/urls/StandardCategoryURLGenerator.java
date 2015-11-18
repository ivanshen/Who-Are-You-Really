package org.jfree.chart.urls;

import java.io.Serializable;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import org.jfree.chart.util.ParamChecks;
import org.jfree.data.category.CategoryDataset;
import org.jfree.util.ObjectUtilities;

public class StandardCategoryURLGenerator implements CategoryURLGenerator, Cloneable, Serializable {
    private static final long serialVersionUID = 2276668053074881909L;
    private String categoryParameterName;
    private String prefix;
    private String seriesParameterName;

    public StandardCategoryURLGenerator() {
        this.prefix = StandardXYURLGenerator.DEFAULT_PREFIX;
        this.seriesParameterName = StandardXYURLGenerator.DEFAULT_SERIES_PARAMETER;
        this.categoryParameterName = "category";
    }

    public StandardCategoryURLGenerator(String prefix) {
        this.prefix = StandardXYURLGenerator.DEFAULT_PREFIX;
        this.seriesParameterName = StandardXYURLGenerator.DEFAULT_SERIES_PARAMETER;
        this.categoryParameterName = "category";
        ParamChecks.nullNotPermitted(prefix, "prefix");
        this.prefix = prefix;
    }

    public StandardCategoryURLGenerator(String prefix, String seriesParameterName, String categoryParameterName) {
        this.prefix = StandardXYURLGenerator.DEFAULT_PREFIX;
        this.seriesParameterName = StandardXYURLGenerator.DEFAULT_SERIES_PARAMETER;
        this.categoryParameterName = "category";
        ParamChecks.nullNotPermitted(prefix, "prefix");
        ParamChecks.nullNotPermitted(seriesParameterName, "seriesParameterName");
        ParamChecks.nullNotPermitted(categoryParameterName, "categoryParameterName");
        this.prefix = prefix;
        this.seriesParameterName = seriesParameterName;
        this.categoryParameterName = categoryParameterName;
    }

    public String generateURL(CategoryDataset dataset, int series, int category) {
        String url = this.prefix;
        Comparable seriesKey = dataset.getRowKey(series);
        Comparable categoryKey = dataset.getColumnKey(category);
        try {
            return ((url + (!url.contains("?") ? "?" : "&amp;")) + this.seriesParameterName + "=" + URLEncoder.encode(seriesKey.toString(), "UTF-8")) + "&amp;" + this.categoryParameterName + "=" + URLEncoder.encode(categoryKey.toString(), "UTF-8");
        } catch (UnsupportedEncodingException ex) {
            throw new RuntimeException(ex);
        }
    }

    public Object clone() throws CloneNotSupportedException {
        return super.clone();
    }

    public boolean equals(Object obj) {
        if (obj == this) {
            return true;
        }
        if (!(obj instanceof StandardCategoryURLGenerator)) {
            return false;
        }
        StandardCategoryURLGenerator that = (StandardCategoryURLGenerator) obj;
        if (!ObjectUtilities.equal(this.prefix, that.prefix)) {
            return false;
        }
        if (!ObjectUtilities.equal(this.seriesParameterName, that.seriesParameterName)) {
            return false;
        }
        if (ObjectUtilities.equal(this.categoryParameterName, that.categoryParameterName)) {
            return true;
        }
        return false;
    }

    public int hashCode() {
        int result;
        int hashCode;
        int i = 0;
        if (this.prefix != null) {
            result = this.prefix.hashCode();
        } else {
            result = 0;
        }
        int i2 = result * 29;
        if (this.seriesParameterName != null) {
            hashCode = this.seriesParameterName.hashCode();
        } else {
            hashCode = 0;
        }
        hashCode = (i2 + hashCode) * 29;
        if (this.categoryParameterName != null) {
            i = this.categoryParameterName.hashCode();
        }
        return hashCode + i;
    }
}
