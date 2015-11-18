package org.jfree.chart.urls;

import java.io.Serializable;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import org.jfree.chart.util.ParamChecks;
import org.jfree.data.general.PieDataset;
import org.jfree.util.ObjectUtilities;

public class StandardPieURLGenerator implements PieURLGenerator, Serializable {
    private static final long serialVersionUID = 1626966402065883419L;
    private String categoryParamName;
    private String indexParamName;
    private String prefix;

    public StandardPieURLGenerator() {
        this(StandardXYURLGenerator.DEFAULT_PREFIX);
    }

    public StandardPieURLGenerator(String prefix) {
        this(prefix, "category");
    }

    public StandardPieURLGenerator(String prefix, String categoryParamName) {
        this(prefix, categoryParamName, "pieIndex");
    }

    public StandardPieURLGenerator(String prefix, String categoryParamName, String indexParamName) {
        this.prefix = StandardXYURLGenerator.DEFAULT_PREFIX;
        this.categoryParamName = "category";
        this.indexParamName = "pieIndex";
        ParamChecks.nullNotPermitted(prefix, "prefix");
        ParamChecks.nullNotPermitted(categoryParamName, "categoryParamName");
        this.prefix = prefix;
        this.categoryParamName = categoryParamName;
        this.indexParamName = indexParamName;
    }

    public String generateURL(PieDataset dataset, Comparable key, int pieIndex) {
        String url = this.prefix;
        try {
            if (url.contains("?")) {
                url = url + "&amp;" + this.categoryParamName + "=" + URLEncoder.encode(key.toString(), "UTF-8");
            } else {
                url = url + "?" + this.categoryParamName + "=" + URLEncoder.encode(key.toString(), "UTF-8");
            }
            if (this.indexParamName != null) {
                return url + "&amp;" + this.indexParamName + "=" + pieIndex;
            }
            return url;
        } catch (UnsupportedEncodingException e) {
            throw new RuntimeException(e);
        }
    }

    public boolean equals(Object obj) {
        if (obj == this) {
            return true;
        }
        if (!(obj instanceof StandardPieURLGenerator)) {
            return false;
        }
        StandardPieURLGenerator that = (StandardPieURLGenerator) obj;
        if (!this.prefix.equals(that.prefix)) {
            return false;
        }
        if (!this.categoryParamName.equals(that.categoryParamName)) {
            return false;
        }
        if (ObjectUtilities.equal(this.indexParamName, that.indexParamName)) {
            return true;
        }
        return false;
    }
}
