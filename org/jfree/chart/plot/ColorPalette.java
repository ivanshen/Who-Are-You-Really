package org.jfree.chart.plot;

import java.awt.Color;
import java.awt.Paint;
import java.io.Serializable;
import java.util.Arrays;
import java.util.List;
import org.jfree.chart.annotations.XYPointerAnnotation;
import org.jfree.chart.axis.ValueTick;
import org.jfree.data.xy.NormalizedMatrixSeries;

public abstract class ColorPalette implements Cloneable, Serializable {
    protected static final double log10;
    private static final long serialVersionUID = -9029901853079622051L;
    protected int[] b;
    protected int[] g;
    protected boolean inverse;
    protected boolean logscale;
    protected double maxZ;
    protected double minZ;
    protected String paletteName;
    protected int[] r;
    protected boolean stepped;
    protected double[] tickValues;

    public abstract void initialize();

    static {
        log10 = Math.log(XYPointerAnnotation.DEFAULT_TIP_RADIUS);
    }

    public ColorPalette() {
        this.minZ = SpiderWebPlot.DEFAULT_MAX_VALUE;
        this.maxZ = SpiderWebPlot.DEFAULT_MAX_VALUE;
        this.tickValues = null;
        this.logscale = false;
        this.inverse = false;
        this.paletteName = null;
        this.stepped = false;
    }

    public Paint getColor(double value) {
        int izV = ((int) ((253.0d * (value - this.minZ)) / (this.maxZ - this.minZ))) + 2;
        return new Color(this.r[izV], this.g[izV], this.b[izV]);
    }

    public Color getColor(int izV) {
        return new Color(this.r[izV], this.g[izV], this.b[izV]);
    }

    public Color getColorLinear(double value) {
        if (this.stepped) {
            int index = Arrays.binarySearch(this.tickValues, value);
            if (index < 0) {
                index = (index * -1) - 2;
            }
            if (index < 0) {
                value = this.minZ;
            } else {
                value = this.tickValues[index];
            }
        }
        return getColor(Math.max(Math.min(((int) ((253.0d * (value - this.minZ)) / (this.maxZ - this.minZ))) + 2, 255), 2));
    }

    public Color getColorLog(double value) {
        int izV;
        double minZtmp = this.minZ;
        double maxZtmp = this.maxZ;
        if (this.minZ <= log10) {
            this.maxZ = (maxZtmp - minZtmp) + NormalizedMatrixSeries.DEFAULT_SCALE_FACTOR;
            this.minZ = NormalizedMatrixSeries.DEFAULT_SCALE_FACTOR;
            value = (value - minZtmp) + NormalizedMatrixSeries.DEFAULT_SCALE_FACTOR;
        }
        double minZlog = Math.log(this.minZ) / log10;
        double maxZlog = Math.log(this.maxZ) / log10;
        value = Math.log(value) / log10;
        if (this.stepped) {
            int numSteps = this.tickValues.length;
            izV = (((int) ((((double) numSteps) * (value - minZlog)) / (maxZlog - minZlog))) * (256 / (numSteps - 1))) + 2;
        } else {
            izV = ((int) ((253.0d * (value - minZlog)) / (maxZlog - minZlog))) + 2;
        }
        izV = Math.max(Math.min(izV, 255), 2);
        this.minZ = minZtmp;
        this.maxZ = maxZtmp;
        return getColor(izV);
    }

    public double getMaxZ() {
        return this.maxZ;
    }

    public double getMinZ() {
        return this.minZ;
    }

    public Paint getPaint(double value) {
        if (isLogscale()) {
            return getColorLog(value);
        }
        return getColorLinear(value);
    }

    public String getPaletteName() {
        return this.paletteName;
    }

    public double[] getTickValues() {
        return this.tickValues;
    }

    public void invertPalette() {
        int i;
        int[] red = new int[256];
        int[] green = new int[256];
        int[] blue = new int[256];
        for (i = 0; i < 256; i++) {
            red[i] = this.r[i];
            green[i] = this.g[i];
            blue[i] = this.b[i];
        }
        for (i = 2; i < 256; i++) {
            this.r[i] = red[257 - i];
            this.g[i] = green[257 - i];
            this.b[i] = blue[257 - i];
        }
    }

    public boolean isInverse() {
        return this.inverse;
    }

    public boolean isLogscale() {
        return this.logscale;
    }

    public boolean isStepped() {
        return this.stepped;
    }

    public void setInverse(boolean inverse) {
        this.inverse = inverse;
        initialize();
        if (inverse) {
            invertPalette();
        }
    }

    public void setLogscale(boolean logscale) {
        this.logscale = logscale;
    }

    public void setMaxZ(double newMaxZ) {
        this.maxZ = newMaxZ;
    }

    public void setMinZ(double newMinZ) {
        this.minZ = newMinZ;
    }

    public void setPaletteName(String paletteName) {
        this.paletteName = paletteName;
    }

    public void setStepped(boolean stepped) {
        this.stepped = stepped;
    }

    public void setTickValues(double[] newTickValues) {
        this.tickValues = newTickValues;
    }

    public void setTickValues(List ticks) {
        this.tickValues = new double[ticks.size()];
        for (int i = 0; i < this.tickValues.length; i++) {
            this.tickValues[i] = ((ValueTick) ticks.get(i)).getValue();
        }
    }

    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof ColorPalette)) {
            return false;
        }
        ColorPalette colorPalette = (ColorPalette) o;
        if (this.inverse != colorPalette.inverse) {
            return false;
        }
        if (this.logscale != colorPalette.logscale) {
            return false;
        }
        if (this.maxZ != colorPalette.maxZ) {
            return false;
        }
        if (this.minZ != colorPalette.minZ) {
            return false;
        }
        if (this.stepped != colorPalette.stepped) {
            return false;
        }
        if (!Arrays.equals(this.b, colorPalette.b)) {
            return false;
        }
        if (!Arrays.equals(this.g, colorPalette.g)) {
            return false;
        }
        if (this.paletteName == null ? colorPalette.paletteName != null : !this.paletteName.equals(colorPalette.paletteName)) {
            return false;
        }
        if (!Arrays.equals(this.r, colorPalette.r)) {
            return false;
        }
        if (Arrays.equals(this.tickValues, colorPalette.tickValues)) {
            return true;
        }
        return false;
    }

    public int hashCode() {
        int i;
        int i2 = 1;
        long temp = Double.doubleToLongBits(this.minZ);
        int result = (int) ((temp >>> 32) ^ temp);
        temp = Double.doubleToLongBits(this.maxZ);
        int i3 = ((((result * 29) + ((int) ((temp >>> 32) ^ temp))) * 29) + (this.logscale ? 1 : 0)) * 29;
        if (this.inverse) {
            i = 1;
        } else {
            i = 0;
        }
        i3 = (i3 + i) * 29;
        if (this.paletteName != null) {
            i = this.paletteName.hashCode();
        } else {
            i = 0;
        }
        i = (i3 + i) * 29;
        if (!this.stepped) {
            i2 = 0;
        }
        return i + i2;
    }

    public Object clone() throws CloneNotSupportedException {
        return (ColorPalette) super.clone();
    }
}
