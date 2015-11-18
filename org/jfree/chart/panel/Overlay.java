package org.jfree.chart.panel;

import java.awt.Graphics2D;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.event.OverlayChangeListener;

public interface Overlay {
    void addChangeListener(OverlayChangeListener overlayChangeListener);

    void paintOverlay(Graphics2D graphics2D, ChartPanel chartPanel);

    void removeChangeListener(OverlayChangeListener overlayChangeListener);
}
