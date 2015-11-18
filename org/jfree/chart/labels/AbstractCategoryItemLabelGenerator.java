package org.jfree.chart.labels;

import java.io.Serializable;
import java.text.DateFormat;
import java.text.MessageFormat;
import java.text.NumberFormat;
import org.jfree.chart.HashUtilities;
import org.jfree.chart.util.ParamChecks;
import org.jfree.data.DataUtilities;
import org.jfree.data.category.CategoryDataset;
import org.jfree.util.ObjectUtilities;
import org.jfree.util.PublicCloneable;

public abstract class AbstractCategoryItemLabelGenerator implements PublicCloneable, Cloneable, Serializable {
    private static final long serialVersionUID = -7108591260223293197L;
    private DateFormat dateFormat;
    private String labelFormat;
    private String nullValueString;
    private NumberFormat numberFormat;
    private NumberFormat percentFormat;

    protected AbstractCategoryItemLabelGenerator(String labelFormat, NumberFormat formatter) {
        this(labelFormat, formatter, NumberFormat.getPercentInstance());
    }

    protected AbstractCategoryItemLabelGenerator(String labelFormat, NumberFormat formatter, NumberFormat percentFormatter) {
        ParamChecks.nullNotPermitted(labelFormat, "labelFormat");
        ParamChecks.nullNotPermitted(formatter, "formatter");
        ParamChecks.nullNotPermitted(percentFormatter, "percentFormatter");
        this.labelFormat = labelFormat;
        this.numberFormat = formatter;
        this.percentFormat = percentFormatter;
        this.dateFormat = null;
        this.nullValueString = "-";
    }

    protected AbstractCategoryItemLabelGenerator(String labelFormat, DateFormat formatter) {
        ParamChecks.nullNotPermitted(labelFormat, "labelFormat");
        ParamChecks.nullNotPermitted(formatter, "formatter");
        this.labelFormat = labelFormat;
        this.numberFormat = null;
        this.percentFormat = NumberFormat.getPercentInstance();
        this.dateFormat = formatter;
        this.nullValueString = "-";
    }

    public String generateRowLabel(CategoryDataset dataset, int row) {
        return dataset.getRowKey(row).toString();
    }

    public String generateColumnLabel(CategoryDataset dataset, int column) {
        return dataset.getColumnKey(column).toString();
    }

    public String getLabelFormat() {
        return this.labelFormat;
    }

    public NumberFormat getNumberFormat() {
        return this.numberFormat;
    }

    public DateFormat getDateFormat() {
        return this.dateFormat;
    }

    protected String generateLabelString(CategoryDataset dataset, int row, int column) {
        ParamChecks.nullNotPermitted(dataset, "dataset");
        return MessageFormat.format(this.labelFormat, createItemArray(dataset, row, column));
    }

    protected Object[] createItemArray(CategoryDataset dataset, int row, int column) {
        Object[] result = new Object[4];
        result[0] = dataset.getRowKey(row).toString();
        result[1] = dataset.getColumnKey(column).toString();
        Number value = dataset.getValue(row, column);
        if (value == null) {
            result[2] = this.nullValueString;
        } else if (this.numberFormat != null) {
            result[2] = this.numberFormat.format(value);
        } else if (this.dateFormat != null) {
            result[2] = this.dateFormat.format(value);
        }
        if (value != null) {
            result[3] = this.percentFormat.format(value.doubleValue() / DataUtilities.calculateColumnTotal(dataset, column));
        }
        return result;
    }

    public boolean equals(Object obj) {
        if (obj == this) {
            return true;
        }
        if (!(obj instanceof AbstractCategoryItemLabelGenerator)) {
            return false;
        }
        AbstractCategoryItemLabelGenerator that = (AbstractCategoryItemLabelGenerator) obj;
        if (!this.labelFormat.equals(that.labelFormat)) {
            return false;
        }
        if (!ObjectUtilities.equal(this.dateFormat, that.dateFormat)) {
            return false;
        }
        if (ObjectUtilities.equal(this.numberFormat, that.numberFormat)) {
            return true;
        }
        return false;
    }

    public int hashCode() {
        return HashUtilities.hashCode(HashUtilities.hashCode(HashUtilities.hashCode(HashUtilities.hashCode(HashUtilities.hashCode(127, this.labelFormat), this.nullValueString), this.dateFormat), this.numberFormat), this.percentFormat);
    }

    public Object clone() throws CloneNotSupportedException {
        AbstractCategoryItemLabelGenerator clone = (AbstractCategoryItemLabelGenerator) super.clone();
        if (this.numberFormat != null) {
            clone.numberFormat = (NumberFormat) this.numberFormat.clone();
        }
        if (this.dateFormat != null) {
            clone.dateFormat = (DateFormat) this.dateFormat.clone();
        }
        return clone;
    }
}
