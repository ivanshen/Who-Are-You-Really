package org.jfree.chart.labels;

import java.io.Serializable;
import java.text.DateFormat;
import java.text.MessageFormat;
import java.text.NumberFormat;
import org.jfree.chart.util.ParamChecks;
import org.jfree.data.xy.XYDataset;
import org.jfree.data.xy.XYZDataset;
import org.jfree.util.ObjectUtilities;

public class StandardXYZToolTipGenerator extends StandardXYToolTipGenerator implements XYZToolTipGenerator, Serializable {
    public static final String DEFAULT_TOOL_TIP_FORMAT = "{0}: ({1}, {2}, {3})";
    private static final long serialVersionUID = -2961577421889473503L;
    private DateFormat zDateFormat;
    private NumberFormat zFormat;

    public StandardXYZToolTipGenerator() {
        this(DEFAULT_TOOL_TIP_FORMAT, NumberFormat.getNumberInstance(), NumberFormat.getNumberInstance(), NumberFormat.getNumberInstance());
    }

    public StandardXYZToolTipGenerator(String formatString, NumberFormat xFormat, NumberFormat yFormat, NumberFormat zFormat) {
        super(formatString, xFormat, yFormat);
        ParamChecks.nullNotPermitted(zFormat, "zFormat");
        this.zFormat = zFormat;
    }

    public StandardXYZToolTipGenerator(String formatString, DateFormat xFormat, DateFormat yFormat, DateFormat zFormat) {
        super(formatString, xFormat, yFormat);
        ParamChecks.nullNotPermitted(zFormat, "zFormat");
        this.zDateFormat = zFormat;
    }

    public NumberFormat getZFormat() {
        return this.zFormat;
    }

    public DateFormat getZDateFormat() {
        return this.zDateFormat;
    }

    public String generateToolTip(XYZDataset dataset, int series, int item) {
        return generateLabelString(dataset, series, item);
    }

    public String generateLabelString(XYDataset dataset, int series, int item) {
        return MessageFormat.format(getFormatString(), createItemArray((XYZDataset) dataset, series, item));
    }

    protected Object[] createItemArray(XYZDataset dataset, int series, int item) {
        Object[] result = new Object[4];
        result[0] = dataset.getSeriesKey(series).toString();
        Number x = dataset.getX(series, item);
        DateFormat xf = getXDateFormat();
        if (xf != null) {
            result[1] = xf.format(x);
        } else {
            result[1] = getXFormat().format(x);
        }
        Number y = dataset.getY(series, item);
        DateFormat yf = getYDateFormat();
        if (yf != null) {
            result[2] = yf.format(y);
        } else {
            result[2] = getYFormat().format(y);
        }
        Number z = dataset.getZ(series, item);
        if (this.zDateFormat != null) {
            result[3] = this.zDateFormat.format(z);
        } else {
            result[3] = this.zFormat.format(z);
        }
        return result;
    }

    public boolean equals(Object obj) {
        if (obj == this) {
            return true;
        }
        if (!(obj instanceof StandardXYZToolTipGenerator)) {
            return false;
        }
        if (!super.equals(obj)) {
            return false;
        }
        StandardXYZToolTipGenerator that = (StandardXYZToolTipGenerator) obj;
        if (!ObjectUtilities.equal(this.zFormat, that.zFormat)) {
            return false;
        }
        if (ObjectUtilities.equal(this.zDateFormat, that.zDateFormat)) {
            return true;
        }
        return false;
    }
}
