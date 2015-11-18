package org.jfree.data.function;

import java.io.Serializable;
import org.jfree.chart.HashUtilities;
import org.jfree.chart.axis.DateAxis;
import org.jfree.data.xy.NormalizedMatrixSeries;

public class NormalDistributionFunction2D implements Function2D, Serializable {
    private double denominator;
    private double factor;
    private double mean;
    private double std;

    public NormalDistributionFunction2D(double mean, double std) {
        if (std <= 0.0d) {
            throw new IllegalArgumentException("Requires 'std' > 0.");
        }
        this.mean = mean;
        this.std = std;
        this.factor = NormalizedMatrixSeries.DEFAULT_SCALE_FACTOR / (Math.sqrt(6.283185307179586d) * std);
        this.denominator = (DateAxis.DEFAULT_AUTO_RANGE_MINIMUM_SIZE_IN_MILLISECONDS * std) * std;
    }

    public double getMean() {
        return this.mean;
    }

    public double getStandardDeviation() {
        return this.std;
    }

    public double getValue(double x) {
        double z = x - this.mean;
        return this.factor * Math.exp(((-z) * z) / this.denominator);
    }

    public boolean equals(Object obj) {
        if (!(obj instanceof NormalDistributionFunction2D)) {
            return false;
        }
        NormalDistributionFunction2D that = (NormalDistributionFunction2D) obj;
        if (this.mean == that.mean && this.std == that.std) {
            return true;
        }
        return false;
    }

    public int hashCode() {
        return HashUtilities.hashCode(HashUtilities.hashCode(29, this.mean), this.std);
    }
}
