package org.jfree.chart.axis;

public interface TickUnitSource {
    TickUnit getCeilingTickUnit(double d);

    TickUnit getCeilingTickUnit(TickUnit tickUnit);

    TickUnit getLargerTickUnit(TickUnit tickUnit);
}
