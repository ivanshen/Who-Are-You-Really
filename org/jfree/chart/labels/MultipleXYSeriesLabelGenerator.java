package org.jfree.chart.labels;

import java.io.Serializable;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.jfree.chart.HashUtilities;
import org.jfree.chart.util.ParamChecks;
import org.jfree.data.xy.XYDataset;
import org.jfree.util.PublicCloneable;

public class MultipleXYSeriesLabelGenerator implements XYSeriesLabelGenerator, Cloneable, PublicCloneable, Serializable {
    public static final String DEFAULT_LABEL_FORMAT = "{0}";
    private static final long serialVersionUID = 138976236941898560L;
    private String additionalFormatPattern;
    private String formatPattern;
    private Map seriesLabelLists;

    public MultipleXYSeriesLabelGenerator() {
        this(DEFAULT_LABEL_FORMAT);
    }

    public MultipleXYSeriesLabelGenerator(String format) {
        ParamChecks.nullNotPermitted(format, "format");
        this.formatPattern = format;
        this.additionalFormatPattern = "\n{0}";
        this.seriesLabelLists = new HashMap();
    }

    public void addSeriesLabel(int series, String label) {
        Integer key = new Integer(series);
        List labelList = (List) this.seriesLabelLists.get(key);
        if (labelList == null) {
            labelList = new ArrayList();
            this.seriesLabelLists.put(key, labelList);
        }
        labelList.add(label);
    }

    public void clearSeriesLabels(int series) {
        this.seriesLabelLists.put(new Integer(series), null);
    }

    public String generateLabel(XYDataset dataset, int series) {
        ParamChecks.nullNotPermitted(dataset, "dataset");
        StringBuilder label = new StringBuilder();
        label.append(MessageFormat.format(this.formatPattern, createItemArray(dataset, series)));
        List extraLabels = (List) this.seriesLabelLists.get(new Integer(series));
        if (extraLabels != null) {
            Object[] temp = new Object[1];
            for (int i = 0; i < extraLabels.size(); i++) {
                temp[0] = extraLabels.get(i);
                label.append(MessageFormat.format(this.additionalFormatPattern, temp));
            }
        }
        return label.toString();
    }

    protected Object[] createItemArray(XYDataset dataset, int series) {
        return new Object[]{dataset.getSeriesKey(series).toString()};
    }

    public Object clone() throws CloneNotSupportedException {
        MultipleXYSeriesLabelGenerator clone = (MultipleXYSeriesLabelGenerator) super.clone();
        clone.seriesLabelLists = new HashMap();
        for (Object key : this.seriesLabelLists.keySet()) {
            PublicCloneable entry = this.seriesLabelLists.get(key);
            Object toAdd = entry;
            if (entry instanceof PublicCloneable) {
                toAdd = entry.clone();
            }
            clone.seriesLabelLists.put(key, toAdd);
        }
        return clone;
    }

    public boolean equals(Object obj) {
        if (obj == this) {
            return true;
        }
        if (!(obj instanceof MultipleXYSeriesLabelGenerator)) {
            return false;
        }
        MultipleXYSeriesLabelGenerator that = (MultipleXYSeriesLabelGenerator) obj;
        if (!this.formatPattern.equals(that.formatPattern)) {
            return false;
        }
        if (!this.additionalFormatPattern.equals(that.additionalFormatPattern)) {
            return false;
        }
        if (this.seriesLabelLists.equals(that.seriesLabelLists)) {
            return true;
        }
        return false;
    }

    public int hashCode() {
        return HashUtilities.hashCode(HashUtilities.hashCode(HashUtilities.hashCode(127, this.formatPattern), this.additionalFormatPattern), this.seriesLabelLists);
    }
}
