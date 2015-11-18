package org.jfree.chart.labels;

import java.io.Serializable;
import java.text.MessageFormat;
import org.jfree.chart.HashUtilities;
import org.jfree.chart.util.ParamChecks;
import org.jfree.data.category.CategoryDataset;
import org.jfree.util.PublicCloneable;

public class StandardCategorySeriesLabelGenerator implements CategorySeriesLabelGenerator, Cloneable, PublicCloneable, Serializable {
    public static final String DEFAULT_LABEL_FORMAT = "{0}";
    private static final long serialVersionUID = 4630760091523940820L;
    private String formatPattern;

    public StandardCategorySeriesLabelGenerator() {
        this(DEFAULT_LABEL_FORMAT);
    }

    public StandardCategorySeriesLabelGenerator(String format) {
        ParamChecks.nullNotPermitted(format, "format");
        this.formatPattern = format;
    }

    public String generateLabel(CategoryDataset dataset, int series) {
        ParamChecks.nullNotPermitted(dataset, "dataset");
        return MessageFormat.format(this.formatPattern, createItemArray(dataset, series));
    }

    protected Object[] createItemArray(CategoryDataset dataset, int series) {
        return new Object[]{dataset.getRowKey(series).toString()};
    }

    public Object clone() throws CloneNotSupportedException {
        return super.clone();
    }

    public boolean equals(Object obj) {
        if (obj == this) {
            return true;
        }
        if (!(obj instanceof StandardCategorySeriesLabelGenerator)) {
            return false;
        }
        if (this.formatPattern.equals(((StandardCategorySeriesLabelGenerator) obj).formatPattern)) {
            return true;
        }
        return false;
    }

    public int hashCode() {
        return HashUtilities.hashCode(127, this.formatPattern);
    }
}
