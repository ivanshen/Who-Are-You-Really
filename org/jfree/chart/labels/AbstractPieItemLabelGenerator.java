package org.jfree.chart.labels;

import java.io.Serializable;
import java.text.MessageFormat;
import java.text.NumberFormat;
import org.jfree.chart.HashUtilities;
import org.jfree.chart.util.ParamChecks;
import org.jfree.data.general.DatasetUtilities;
import org.jfree.data.general.PieDataset;

public class AbstractPieItemLabelGenerator implements Serializable {
    private static final long serialVersionUID = 7347703325267846275L;
    private String labelFormat;
    private NumberFormat numberFormat;
    private NumberFormat percentFormat;

    protected AbstractPieItemLabelGenerator(String labelFormat, NumberFormat numberFormat, NumberFormat percentFormat) {
        ParamChecks.nullNotPermitted(labelFormat, "labelFormat");
        ParamChecks.nullNotPermitted(numberFormat, "numberFormat");
        ParamChecks.nullNotPermitted(percentFormat, "percentFormat");
        this.labelFormat = labelFormat;
        this.numberFormat = numberFormat;
        this.percentFormat = percentFormat;
    }

    public String getLabelFormat() {
        return this.labelFormat;
    }

    public NumberFormat getNumberFormat() {
        return this.numberFormat;
    }

    public NumberFormat getPercentFormat() {
        return this.percentFormat;
    }

    protected Object[] createItemArray(PieDataset dataset, Comparable key) {
        Object[] result = new Object[4];
        double total = DatasetUtilities.calculatePieDatasetTotal(dataset);
        result[0] = key.toString();
        Number value = dataset.getValue(key);
        if (value != null) {
            result[1] = this.numberFormat.format(value);
        } else {
            result[1] = "null";
        }
        double percent = 0.0d;
        if (value != null) {
            double v = value.doubleValue();
            if (v > 0.0d) {
                percent = v / total;
            }
        }
        result[2] = this.percentFormat.format(percent);
        result[3] = this.numberFormat.format(total);
        return result;
    }

    protected String generateSectionLabel(PieDataset dataset, Comparable key) {
        if (dataset == null) {
            return null;
        }
        return MessageFormat.format(this.labelFormat, createItemArray(dataset, key));
    }

    public boolean equals(Object obj) {
        if (obj == this) {
            return true;
        }
        if (!(obj instanceof AbstractPieItemLabelGenerator)) {
            return false;
        }
        AbstractPieItemLabelGenerator that = (AbstractPieItemLabelGenerator) obj;
        if (!this.labelFormat.equals(that.labelFormat)) {
            return false;
        }
        if (!this.numberFormat.equals(that.numberFormat)) {
            return false;
        }
        if (this.percentFormat.equals(that.percentFormat)) {
            return true;
        }
        return false;
    }

    public int hashCode() {
        return HashUtilities.hashCode(HashUtilities.hashCode(HashUtilities.hashCode(127, this.labelFormat), this.numberFormat), this.percentFormat);
    }

    public Object clone() throws CloneNotSupportedException {
        AbstractPieItemLabelGenerator clone = (AbstractPieItemLabelGenerator) super.clone();
        if (this.numberFormat != null) {
            clone.numberFormat = (NumberFormat) this.numberFormat.clone();
        }
        if (this.percentFormat != null) {
            clone.percentFormat = (NumberFormat) this.percentFormat.clone();
        }
        return clone;
    }
}
