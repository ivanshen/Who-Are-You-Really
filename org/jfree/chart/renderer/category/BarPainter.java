package org.jfree.chart.renderer.category;

import java.awt.Graphics2D;
import java.awt.geom.RectangularShape;
import org.jfree.ui.RectangleEdge;

public interface BarPainter {
    void paintBar(Graphics2D graphics2D, BarRenderer barRenderer, int i, int i2, RectangularShape rectangularShape, RectangleEdge rectangleEdge);

    void paintBarShadow(Graphics2D graphics2D, BarRenderer barRenderer, int i, int i2, RectangularShape rectangularShape, RectangleEdge rectangleEdge, boolean z);
}
