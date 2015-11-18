package org.jfree.chart.labels;

import java.io.Serializable;
import java.text.MessageFormat;
import org.jfree.chart.HashUtilities;
import org.jfree.chart.util.ParamChecks;
import org.jfree.data.xy.XYDataset;
import org.jfree.util.PublicCloneable;

public class StandardXYSeriesLabelGenerator implements XYSeriesLabelGenerator, Cloneable, PublicCloneable, Serializable {
    public static final String DEFAULT_LABEL_FORMAT = "{0}";
    private static final long serialVersionUID = 1916017081848400024L;
    private String formatPattern;

    public StandardXYSeriesLabelGenerator() {
        this(DEFAULT_LABEL_FORMAT);
    }

    public StandardXYSeriesLabelGenerator(String format) {
        ParamChecks.nullNotPermitted(format, "format");
        this.formatPattern = format;
    }

    public String generateLabel(XYDataset dataset, int series) {
        ParamChecks.nullNotPermitted(dataset, "dataset");
        return MessageFormat.format(this.formatPattern, createItemArray(dataset, series));
    }

    protected Object[] createItemArray(XYDataset dataset, int series) {
        return new Object[]{dataset.getSeriesKey(series).toString()};
    }

    public Object clone() throws CloneNotSupportedException {
        return super.clone();
    }

    public boolean equals(Object obj) {
        if (obj == this) {
            return true;
        }
        if (!(obj instanceof StandardXYSeriesLabelGenerator)) {
            return false;
        }
        if (this.formatPattern.equals(((StandardXYSeriesLabelGenerator) obj).formatPattern)) {
            return true;
        }
        return false;
    }

    public int hashCode() {
        return HashUtilities.hashCode(127, this.formatPattern);
    }
}
