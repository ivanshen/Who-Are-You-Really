package org.jfree.chart.renderer;

import java.awt.Paint;

public interface PaintScale {
    double getLowerBound();

    Paint getPaint(double d);

    double getUpperBound();
}
