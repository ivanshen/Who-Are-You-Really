package org.jfree.chart.axis;

import java.text.DateFormat;
import java.text.DateFormatSymbols;
import java.text.FieldPosition;
import java.text.NumberFormat;
import java.text.ParsePosition;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Locale;
import java.util.TimeZone;
import org.jfree.chart.util.ParamChecks;

public class MonthDateFormat extends DateFormat {
    private String[] months;
    private boolean[] showYear;
    private DateFormat yearFormatter;

    public MonthDateFormat() {
        this(TimeZone.getDefault());
    }

    public MonthDateFormat(TimeZone zone) {
        this(zone, Locale.getDefault(), 1, true, false);
    }

    public MonthDateFormat(Locale locale) {
        this(TimeZone.getDefault(), locale, 1, true, false);
    }

    public MonthDateFormat(TimeZone zone, int chars) {
        this(zone, Locale.getDefault(), chars, true, false);
    }

    public MonthDateFormat(Locale locale, int chars) {
        this(TimeZone.getDefault(), locale, chars, true, false);
    }

    public MonthDateFormat(TimeZone zone, Locale locale, int chars, boolean showYearForJan, boolean showYearForDec) {
        this(zone, locale, chars, new boolean[]{showYearForJan, false, false, false, false, false, false, false, false, false, false, false, showYearForDec}, new SimpleDateFormat("yy"));
    }

    public MonthDateFormat(TimeZone zone, Locale locale, int chars, boolean[] showYear, DateFormat yearFormatter) {
        ParamChecks.nullNotPermitted(locale, "locale");
        String[] monthsFromLocale = new DateFormatSymbols(locale).getMonths();
        this.months = new String[12];
        for (int i = 0; i < 12; i++) {
            if (chars > 0) {
                this.months[i] = monthsFromLocale[i].substring(0, Math.min(chars, monthsFromLocale[i].length()));
            } else {
                this.months[i] = monthsFromLocale[i];
            }
        }
        this.calendar = new GregorianCalendar(zone);
        this.showYear = showYear;
        this.yearFormatter = yearFormatter;
        this.numberFormat = NumberFormat.getNumberInstance();
    }

    public StringBuffer format(Date date, StringBuffer toAppendTo, FieldPosition fieldPosition) {
        this.calendar.setTime(date);
        int month = this.calendar.get(2);
        toAppendTo.append(this.months[month]);
        if (this.showYear[month]) {
            toAppendTo.append(this.yearFormatter.format(date));
        }
        return toAppendTo;
    }

    public Date parse(String source, ParsePosition pos) {
        return null;
    }

    public boolean equals(Object obj) {
        if (obj == this) {
            return true;
        }
        if (!(obj instanceof MonthDateFormat)) {
            return false;
        }
        if (!super.equals(obj)) {
            return false;
        }
        MonthDateFormat that = (MonthDateFormat) obj;
        if (!Arrays.equals(this.months, that.months)) {
            return false;
        }
        if (!Arrays.equals(this.showYear, that.showYear)) {
            return false;
        }
        if (this.yearFormatter.equals(that.yearFormatter)) {
            return true;
        }
        return false;
    }
}
