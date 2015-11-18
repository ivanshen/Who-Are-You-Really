package org.jfree.chart.axis;

import java.io.Serializable;
import java.text.DecimalFormat;
import org.jfree.chart.annotations.XYPointerAnnotation;

public class StandardTickUnitSource implements TickUnitSource, Serializable {
    private static final double LOG_10_VALUE;

    static {
        LOG_10_VALUE = Math.log(XYPointerAnnotation.DEFAULT_TIP_RADIUS);
    }

    public TickUnit getLargerTickUnit(TickUnit unit) {
        return new NumberTickUnit(Math.pow(XYPointerAnnotation.DEFAULT_TIP_RADIUS, Math.ceil(Math.log(unit.getSize()) / LOG_10_VALUE)), new DecimalFormat("0.0E0"));
    }

    public TickUnit getCeilingTickUnit(TickUnit unit) {
        return getLargerTickUnit(unit);
    }

    public TickUnit getCeilingTickUnit(double size) {
        return new NumberTickUnit(Math.pow(XYPointerAnnotation.DEFAULT_TIP_RADIUS, Math.ceil(Math.log(size) / LOG_10_VALUE)), new DecimalFormat("0.0E0"));
    }

    public boolean equals(Object obj) {
        if (obj == this) {
            return true;
        }
        return obj instanceof StandardTickUnitSource;
    }

    public int hashCode() {
        return 0;
    }
}
