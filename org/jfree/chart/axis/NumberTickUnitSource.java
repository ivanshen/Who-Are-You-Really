package org.jfree.chart.axis;

import java.io.Serializable;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.annotations.XYPointerAnnotation;
import org.jfree.util.ObjectUtilities;

public class NumberTickUnitSource implements TickUnitSource, Serializable {
    private DecimalFormat df;
    private DecimalFormat df0;
    private DecimalFormat dfNeg1;
    private DecimalFormat dfNeg2;
    private DecimalFormat dfNeg3;
    private DecimalFormat dfNeg4;
    private int factor;
    private NumberFormat formatter;
    private boolean integers;
    private int power;

    public NumberTickUnitSource() {
        this(false);
    }

    public NumberTickUnitSource(boolean integers) {
        this(integers, null);
    }

    public NumberTickUnitSource(boolean integers, NumberFormat formatter) {
        this.power = 0;
        this.factor = 1;
        this.dfNeg4 = new DecimalFormat("0.0000");
        this.dfNeg3 = new DecimalFormat("0.000");
        this.dfNeg2 = new DecimalFormat("0.00");
        this.dfNeg1 = new DecimalFormat("0.0");
        this.df0 = new DecimalFormat("#,##0");
        this.df = new DecimalFormat("#.######E0");
        this.integers = integers;
        this.formatter = formatter;
        this.power = 0;
        this.factor = 1;
    }

    public TickUnit getLargerTickUnit(TickUnit unit) {
        TickUnit t = getCeilingTickUnit(unit);
        if (!t.equals(unit)) {
            return t;
        }
        next();
        return new NumberTickUnit(getTickSize(), getTickLabelFormat(), getMinorTickCount());
    }

    public TickUnit getCeilingTickUnit(TickUnit unit) {
        return getCeilingTickUnit(unit.getSize());
    }

    public TickUnit getCeilingTickUnit(double size) {
        if (Double.isInfinite(size)) {
            throw new IllegalArgumentException("Must be finite.");
        }
        this.power = (int) Math.ceil(Math.log10(size));
        if (this.integers) {
            this.power = Math.max(this.power, 0);
        }
        this.factor = 1;
        boolean done = false;
        while (!done) {
            if (previous()) {
                done = false;
            } else {
                done = true;
            }
            if (getTickSize() < size) {
                next();
                done = true;
            }
        }
        return new NumberTickUnit(getTickSize(), getTickLabelFormat(), getMinorTickCount());
    }

    private boolean next() {
        if (this.factor == 1) {
            this.factor = 2;
            return true;
        } else if (this.factor == 2) {
            this.factor = 5;
            return true;
        } else if (this.factor != 5) {
            throw new IllegalStateException("We should never get here.");
        } else if (this.power == ChartPanel.DEFAULT_MINIMUM_DRAW_WIDTH) {
            return false;
        } else {
            this.power++;
            this.factor = 1;
            return true;
        }
    }

    private boolean previous() {
        if (this.factor == 1) {
            if ((this.integers && this.power == 0) || this.power == -300) {
                return false;
            }
            this.factor = 5;
            this.power--;
            return true;
        } else if (this.factor == 2) {
            this.factor = 1;
            return true;
        } else if (this.factor == 5) {
            this.factor = 2;
            return true;
        } else {
            throw new IllegalStateException("We should never get here.");
        }
    }

    private double getTickSize() {
        return ((double) this.factor) * Math.pow(XYPointerAnnotation.DEFAULT_TIP_RADIUS, (double) this.power);
    }

    private NumberFormat getTickLabelFormat() {
        if (this.formatter != null) {
            return this.formatter;
        }
        if (this.power == -4) {
            return this.dfNeg4;
        }
        if (this.power == -3) {
            return this.dfNeg3;
        }
        if (this.power == -2) {
            return this.dfNeg2;
        }
        if (this.power == -1) {
            return this.dfNeg1;
        }
        if (this.power < 0 || this.power > 6) {
            return this.df;
        }
        return this.df0;
    }

    private int getMinorTickCount() {
        if (this.factor == 1) {
            return 10;
        }
        if (this.factor != 5) {
            return 0;
        }
        return 5;
    }

    public boolean equals(Object obj) {
        if (obj == this) {
            return true;
        }
        if (!(obj instanceof NumberTickUnitSource)) {
            return false;
        }
        NumberTickUnitSource that = (NumberTickUnitSource) obj;
        if (this.integers != that.integers) {
            return false;
        }
        if (!ObjectUtilities.equal(this.formatter, that.formatter)) {
            return false;
        }
        if (this.power != that.power) {
            return false;
        }
        if (this.factor != that.factor) {
            return false;
        }
        return true;
    }
}
