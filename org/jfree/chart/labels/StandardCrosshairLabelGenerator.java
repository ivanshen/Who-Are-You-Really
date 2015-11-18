package org.jfree.chart.labels;

import java.io.Serializable;
import java.text.MessageFormat;
import java.text.NumberFormat;
import org.jfree.chart.plot.Crosshair;

public class StandardCrosshairLabelGenerator implements CrosshairLabelGenerator, Serializable {
    private String labelTemplate;
    private NumberFormat numberFormat;

    public StandardCrosshairLabelGenerator() {
        this(StandardXYSeriesLabelGenerator.DEFAULT_LABEL_FORMAT, NumberFormat.getNumberInstance());
    }

    public StandardCrosshairLabelGenerator(String labelTemplate, NumberFormat numberFormat) {
        if (labelTemplate == null) {
            throw new IllegalArgumentException("Null 'labelTemplate' argument.");
        } else if (numberFormat == null) {
            throw new IllegalArgumentException("Null 'numberFormat' argument.");
        } else {
            this.labelTemplate = labelTemplate;
            this.numberFormat = numberFormat;
        }
    }

    public String getLabelTemplate() {
        return this.labelTemplate;
    }

    public NumberFormat getNumberFormat() {
        return this.numberFormat;
    }

    public String generateLabel(Crosshair crosshair) {
        return MessageFormat.format(this.labelTemplate, new Object[]{this.numberFormat.format(crosshair.getValue())});
    }

    public boolean equals(Object obj) {
        if (obj == this) {
            return true;
        }
        if (!(obj instanceof StandardCrosshairLabelGenerator)) {
            return false;
        }
        StandardCrosshairLabelGenerator that = (StandardCrosshairLabelGenerator) obj;
        if (!this.labelTemplate.equals(that.labelTemplate)) {
            return false;
        }
        if (this.numberFormat.equals(that.numberFormat)) {
            return true;
        }
        return false;
    }

    public int hashCode() {
        return this.labelTemplate.hashCode();
    }
}
