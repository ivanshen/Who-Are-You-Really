package org.jfree.chart.plot.dial;

import java.awt.Graphics2D;
import java.awt.geom.Rectangle2D;
import java.util.EventListener;

public interface DialLayer {
    void addChangeListener(DialLayerChangeListener dialLayerChangeListener);

    void draw(Graphics2D graphics2D, DialPlot dialPlot, Rectangle2D rectangle2D, Rectangle2D rectangle2D2);

    boolean hasListener(EventListener eventListener);

    boolean isClippedToWindow();

    boolean isVisible();

    void removeChangeListener(DialLayerChangeListener dialLayerChangeListener);
}
