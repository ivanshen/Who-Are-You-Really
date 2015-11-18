package org.jfree.chart.util;

import java.text.DecimalFormat;
import java.text.FieldPosition;
import java.text.NumberFormat;
import java.text.ParsePosition;

public class HMSNumberFormat extends NumberFormat {
    private NumberFormat formatter;

    public HMSNumberFormat() {
        this.formatter = new DecimalFormat("00");
    }

    public StringBuffer format(double number, StringBuffer toAppendTo, FieldPosition pos) {
        return format((long) number, toAppendTo, pos);
    }

    public StringBuffer format(long number, StringBuffer toAppendTo, FieldPosition pos) {
        StringBuffer sb = new StringBuffer();
        long hours = number / 3600;
        sb.append(this.formatter.format(hours)).append(":");
        long remaining = number - (3600 * hours);
        long minutes = remaining / 60;
        sb.append(this.formatter.format(minutes)).append(":");
        sb.append(this.formatter.format(remaining - (60 * minutes)));
        return sb;
    }

    public Number parse(String source, ParsePosition parsePosition) {
        return null;
    }
}
