package org.jfree.chart.plot;

import java.awt.Paint;
import java.awt.Shape;
import java.awt.Stroke;

public interface DrawingSupplier {
    Paint getNextFillPaint();

    Paint getNextOutlinePaint();

    Stroke getNextOutlineStroke();

    Paint getNextPaint();

    Shape getNextShape();

    Stroke getNextStroke();
}
