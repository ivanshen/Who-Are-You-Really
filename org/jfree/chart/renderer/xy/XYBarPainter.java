package org.jfree.chart.renderer.xy;

import java.awt.Graphics2D;
import java.awt.geom.RectangularShape;
import org.jfree.ui.RectangleEdge;

public interface XYBarPainter {
    void paintBar(Graphics2D graphics2D, XYBarRenderer xYBarRenderer, int i, int i2, RectangularShape rectangularShape, RectangleEdge rectangleEdge);

    void paintBarShadow(Graphics2D graphics2D, XYBarRenderer xYBarRenderer, int i, int i2, RectangularShape rectangularShape, RectangleEdge rectangleEdge, boolean z);
}
