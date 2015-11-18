package org.jfree.chart;

import java.util.EventListener;

public interface ChartMouseListener extends EventListener {
    void chartMouseClicked(ChartMouseEvent chartMouseEvent);

    void chartMouseMoved(ChartMouseEvent chartMouseEvent);
}
