package org.jfree.chart.axis;

import java.io.Serializable;
import java.text.NumberFormat;
import org.jfree.chart.util.ParamChecks;

public class NumberTickUnit extends TickUnit implements Serializable {
    private static final long serialVersionUID = 3849459506627654442L;
    private NumberFormat formatter;

    public NumberTickUnit(double size) {
        this(size, NumberFormat.getNumberInstance());
    }

    public NumberTickUnit(double size, NumberFormat formatter) {
        super(size);
        ParamChecks.nullNotPermitted(formatter, "formatter");
        this.formatter = formatter;
    }

    public NumberTickUnit(double size, NumberFormat formatter, int minorTickCount) {
        super(size, minorTickCount);
        ParamChecks.nullNotPermitted(formatter, "formatter");
        this.formatter = formatter;
    }

    public String valueToString(double value) {
        return this.formatter.format(value);
    }

    public boolean equals(Object obj) {
        if (obj == this) {
            return true;
        }
        if (!(obj instanceof NumberTickUnit)) {
            return false;
        }
        if (!super.equals(obj)) {
            return false;
        }
        if (this.formatter.equals(((NumberTickUnit) obj).formatter)) {
            return true;
        }
        return false;
    }

    public String toString() {
        return "[size=" + valueToString(getSize()) + "]";
    }

    public int hashCode() {
        return (super.hashCode() * 29) + (this.formatter != null ? this.formatter.hashCode() : 0);
    }
}
