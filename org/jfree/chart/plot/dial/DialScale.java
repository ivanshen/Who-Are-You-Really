package org.jfree.chart.plot.dial;

public interface DialScale extends DialLayer {
    double angleToValue(double d);

    double valueToAngle(double d);
}
