package org.jfree.chart.plot.dial;

import java.awt.Shape;
import java.awt.geom.Rectangle2D;

public interface DialFrame extends DialLayer {
    Shape getWindow(Rectangle2D rectangle2D);
}
