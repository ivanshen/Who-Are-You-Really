package org.jfree.chart.axis;

import java.text.FieldPosition;
import java.text.NumberFormat;
import java.text.ParsePosition;
import org.jfree.chart.util.ParamChecks;

public class CompassFormat extends NumberFormat {
    public final String[] directions;

    public CompassFormat() {
        this("N", "E", "S", "W");
    }

    public CompassFormat(String n, String e, String s, String w) {
        this(new String[]{n, n + n + e, n + e, e + n + e, e, e + s + e, s + e, s + s + e, s, s + s + w, s + w, w + s + w, w, w + n + w, n + w, n + n + w});
    }

    public CompassFormat(String[] directions) {
        ParamChecks.nullNotPermitted(directions, "directions");
        if (directions.length != 16) {
            throw new IllegalArgumentException("The 'directions' array must contain exactly 16 elements");
        }
        this.directions = directions;
    }

    public String getDirectionCode(double direction) {
        direction %= 360.0d;
        if (direction < 0.0d) {
            direction += 360.0d;
        }
        return this.directions[(((int) Math.floor(direction / 11.25d)) + 1) / 2];
    }

    public StringBuffer format(double number, StringBuffer toAppendTo, FieldPosition pos) {
        return toAppendTo.append(getDirectionCode(number));
    }

    public StringBuffer format(long number, StringBuffer toAppendTo, FieldPosition pos) {
        return toAppendTo.append(getDirectionCode((double) number));
    }

    public Number parse(String source, ParsePosition parsePosition) {
        return null;
    }
}
