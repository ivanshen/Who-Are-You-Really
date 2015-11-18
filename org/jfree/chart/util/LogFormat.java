package org.jfree.chart.util;

import java.text.DecimalFormat;
import java.text.FieldPosition;
import java.text.NumberFormat;
import java.text.ParsePosition;
import org.jfree.chart.annotations.XYPointerAnnotation;

public class LogFormat extends NumberFormat {
    private double base;
    private String baseLabel;
    private double baseLog;
    private NumberFormat formatter;
    private String powerLabel;
    private boolean showBase;

    public LogFormat() {
        this(XYPointerAnnotation.DEFAULT_TIP_RADIUS, "10", true);
    }

    public LogFormat(double base, String baseLabel, boolean showBase) {
        this(base, baseLabel, "^", showBase);
    }

    public LogFormat(double base, String baseLabel, String powerLabel, boolean showBase) {
        this.formatter = new DecimalFormat("0.0#");
        ParamChecks.nullNotPermitted(baseLabel, "baseLabel");
        ParamChecks.nullNotPermitted(powerLabel, "powerLabel");
        this.base = base;
        this.baseLog = Math.log(this.base);
        this.baseLabel = baseLabel;
        this.showBase = showBase;
        this.powerLabel = powerLabel;
    }

    public NumberFormat getExponentFormat() {
        return (NumberFormat) this.formatter.clone();
    }

    public void setExponentFormat(NumberFormat format) {
        ParamChecks.nullNotPermitted(format, "format");
        this.formatter = format;
    }

    private double calculateLog(double value) {
        return Math.log(value) / this.baseLog;
    }

    public StringBuffer format(double number, StringBuffer toAppendTo, FieldPosition pos) {
        StringBuffer result = new StringBuffer();
        if (this.showBase) {
            result.append(this.baseLabel);
            result.append(this.powerLabel);
        }
        result.append(this.formatter.format(calculateLog(number)));
        return result;
    }

    public StringBuffer format(long number, StringBuffer toAppendTo, FieldPosition pos) {
        StringBuffer result = new StringBuffer();
        if (this.showBase) {
            result.append(this.baseLabel);
            result.append(this.powerLabel);
        }
        result.append(this.formatter.format(calculateLog((double) number)));
        return result;
    }

    public Number parse(String source, ParsePosition parsePosition) {
        return null;
    }

    public boolean equals(Object obj) {
        if (obj == this) {
            return true;
        }
        if (!(obj instanceof LogFormat)) {
            return false;
        }
        LogFormat that = (LogFormat) obj;
        if (this.base == that.base && this.baseLabel.equals(that.baseLabel) && this.baseLog == that.baseLog && this.showBase == that.showBase && this.formatter.equals(that.formatter)) {
            return super.equals(obj);
        }
        return false;
    }

    public Object clone() {
        LogFormat clone = (LogFormat) super.clone();
        clone.formatter = (NumberFormat) this.formatter.clone();
        return clone;
    }
}
