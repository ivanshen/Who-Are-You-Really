package org.jfree.chart.util;

import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.FieldPosition;
import java.text.NumberFormat;
import java.text.ParsePosition;
import java.util.Date;
import java.util.GregorianCalendar;
import org.jfree.chart.axis.SegmentedTimeline;

public class RelativeDateFormat extends DateFormat {
    private static final long MILLISECONDS_IN_ONE_DAY = 86400000;
    private static final long MILLISECONDS_IN_ONE_HOUR = 3600000;
    private long baseMillis;
    private NumberFormat dayFormatter;
    private String daySuffix;
    private NumberFormat hourFormatter;
    private String hourSuffix;
    private NumberFormat minuteFormatter;
    private String minuteSuffix;
    private String positivePrefix;
    private NumberFormat secondFormatter;
    private String secondSuffix;
    private boolean showZeroDays;
    private boolean showZeroHours;

    public RelativeDateFormat() {
        this(0);
    }

    public RelativeDateFormat(Date time) {
        this(time.getTime());
    }

    public RelativeDateFormat(long baseMillis) {
        this.baseMillis = baseMillis;
        this.showZeroDays = false;
        this.showZeroHours = true;
        this.positivePrefix = "";
        this.dayFormatter = NumberFormat.getNumberInstance();
        this.daySuffix = "d";
        this.hourFormatter = NumberFormat.getNumberInstance();
        this.hourSuffix = "h";
        this.minuteFormatter = NumberFormat.getNumberInstance();
        this.minuteSuffix = "m";
        this.secondFormatter = NumberFormat.getNumberInstance();
        this.secondFormatter.setMaximumFractionDigits(3);
        this.secondFormatter.setMinimumFractionDigits(3);
        this.secondSuffix = "s";
        this.calendar = new GregorianCalendar();
        this.numberFormat = new DecimalFormat("0");
    }

    public long getBaseMillis() {
        return this.baseMillis;
    }

    public void setBaseMillis(long baseMillis) {
        this.baseMillis = baseMillis;
    }

    public boolean getShowZeroDays() {
        return this.showZeroDays;
    }

    public void setShowZeroDays(boolean show) {
        this.showZeroDays = show;
    }

    public boolean getShowZeroHours() {
        return this.showZeroHours;
    }

    public void setShowZeroHours(boolean show) {
        this.showZeroHours = show;
    }

    public String getPositivePrefix() {
        return this.positivePrefix;
    }

    public void setPositivePrefix(String prefix) {
        ParamChecks.nullNotPermitted(prefix, "prefix");
        this.positivePrefix = prefix;
    }

    public void setDayFormatter(NumberFormat formatter) {
        ParamChecks.nullNotPermitted(formatter, "formatter");
        this.dayFormatter = formatter;
    }

    public String getDaySuffix() {
        return this.daySuffix;
    }

    public void setDaySuffix(String suffix) {
        ParamChecks.nullNotPermitted(suffix, "suffix");
        this.daySuffix = suffix;
    }

    public void setHourFormatter(NumberFormat formatter) {
        ParamChecks.nullNotPermitted(formatter, "formatter");
        this.hourFormatter = formatter;
    }

    public String getHourSuffix() {
        return this.hourSuffix;
    }

    public void setHourSuffix(String suffix) {
        ParamChecks.nullNotPermitted(suffix, "suffix");
        this.hourSuffix = suffix;
    }

    public void setMinuteFormatter(NumberFormat formatter) {
        ParamChecks.nullNotPermitted(formatter, "formatter");
        this.minuteFormatter = formatter;
    }

    public String getMinuteSuffix() {
        return this.minuteSuffix;
    }

    public void setMinuteSuffix(String suffix) {
        ParamChecks.nullNotPermitted(suffix, "suffix");
        this.minuteSuffix = suffix;
    }

    public String getSecondSuffix() {
        return this.secondSuffix;
    }

    public void setSecondSuffix(String suffix) {
        ParamChecks.nullNotPermitted(suffix, "suffix");
        this.secondSuffix = suffix;
    }

    public void setSecondFormatter(NumberFormat formatter) {
        ParamChecks.nullNotPermitted(formatter, "formatter");
        this.secondFormatter = formatter;
    }

    public StringBuffer format(Date date, StringBuffer toAppendTo, FieldPosition fieldPosition) {
        String signPrefix;
        long elapsed = date.getTime() - this.baseMillis;
        if (elapsed < 0) {
            elapsed *= -1;
            signPrefix = "-";
        } else {
            signPrefix = this.positivePrefix;
        }
        long days = elapsed / MILLISECONDS_IN_ONE_DAY;
        elapsed -= MILLISECONDS_IN_ONE_DAY * days;
        long hours = elapsed / MILLISECONDS_IN_ONE_HOUR;
        elapsed -= MILLISECONDS_IN_ONE_HOUR * hours;
        long minutes = elapsed / SegmentedTimeline.MINUTE_SEGMENT_SIZE;
        double seconds = ((double) (elapsed - (SegmentedTimeline.MINUTE_SEGMENT_SIZE * minutes))) / 1000.0d;
        toAppendTo.append(signPrefix);
        if (days != 0 || this.showZeroDays) {
            toAppendTo.append(this.dayFormatter.format(days)).append(getDaySuffix());
        }
        if (hours != 0 || this.showZeroHours) {
            toAppendTo.append(this.hourFormatter.format(hours)).append(getHourSuffix());
        }
        toAppendTo.append(this.minuteFormatter.format(minutes)).append(getMinuteSuffix());
        toAppendTo.append(this.secondFormatter.format(seconds)).append(getSecondSuffix());
        return toAppendTo;
    }

    public Date parse(String source, ParsePosition pos) {
        return null;
    }

    public boolean equals(Object obj) {
        if (obj == this) {
            return true;
        }
        if (!(obj instanceof RelativeDateFormat)) {
            return false;
        }
        if (!super.equals(obj)) {
            return false;
        }
        RelativeDateFormat that = (RelativeDateFormat) obj;
        if (this.baseMillis != that.baseMillis) {
            return false;
        }
        if (this.showZeroDays != that.showZeroDays) {
            return false;
        }
        if (this.showZeroHours != that.showZeroHours) {
            return false;
        }
        if (!this.positivePrefix.equals(that.positivePrefix)) {
            return false;
        }
        if (!this.daySuffix.equals(that.daySuffix)) {
            return false;
        }
        if (!this.hourSuffix.equals(that.hourSuffix)) {
            return false;
        }
        if (!this.minuteSuffix.equals(that.minuteSuffix)) {
            return false;
        }
        if (!this.secondSuffix.equals(that.secondSuffix)) {
            return false;
        }
        if (!this.dayFormatter.equals(that.dayFormatter)) {
            return false;
        }
        if (!this.hourFormatter.equals(that.hourFormatter)) {
            return false;
        }
        if (!this.minuteFormatter.equals(that.minuteFormatter)) {
            return false;
        }
        if (this.secondFormatter.equals(that.secondFormatter)) {
            return true;
        }
        return false;
    }

    public int hashCode() {
        return ((((((((((((((int) (this.baseMillis ^ (this.baseMillis >>> 32))) + 7141) * 37) + this.positivePrefix.hashCode()) * 37) + this.daySuffix.hashCode()) * 37) + this.hourSuffix.hashCode()) * 37) + this.minuteSuffix.hashCode()) * 37) + this.secondSuffix.hashCode()) * 37) + this.secondFormatter.hashCode();
    }

    public Object clone() {
        RelativeDateFormat clone = (RelativeDateFormat) super.clone();
        clone.dayFormatter = (NumberFormat) this.dayFormatter.clone();
        clone.secondFormatter = (NumberFormat) this.secondFormatter.clone();
        return clone;
    }
}
