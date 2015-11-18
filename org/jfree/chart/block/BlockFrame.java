package org.jfree.chart.block;

import java.awt.Graphics2D;
import java.awt.geom.Rectangle2D;
import org.jfree.ui.RectangleInsets;

public interface BlockFrame {
    void draw(Graphics2D graphics2D, Rectangle2D rectangle2D);

    RectangleInsets getInsets();
}
