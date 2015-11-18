package org.jfree.data.function;

import java.io.Serializable;
import java.util.Arrays;
import org.jfree.chart.HashUtilities;
import org.jfree.chart.util.ParamChecks;

public class PolynomialFunction2D implements Function2D, Serializable {
    private double[] coefficients;

    public PolynomialFunction2D(double[] coefficients) {
        ParamChecks.nullNotPermitted(coefficients, "coefficients");
        this.coefficients = (double[]) coefficients.clone();
    }

    public double[] getCoefficients() {
        return (double[]) this.coefficients.clone();
    }

    public int getOrder() {
        return this.coefficients.length - 1;
    }

    public double getValue(double x) {
        double y = 0.0d;
        for (int i = 0; i < this.coefficients.length; i++) {
            y += this.coefficients[i] * Math.pow(x, (double) i);
        }
        return y;
    }

    public boolean equals(Object obj) {
        if (!(obj instanceof PolynomialFunction2D)) {
            return false;
        }
        return Arrays.equals(this.coefficients, ((PolynomialFunction2D) obj).coefficients);
    }

    public int hashCode() {
        return HashUtilities.hashCodeForDoubleArray(this.coefficients);
    }
}
