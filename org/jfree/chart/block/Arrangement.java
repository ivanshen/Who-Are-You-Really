package org.jfree.chart.block;

import java.awt.Graphics2D;
import org.jfree.ui.Size2D;

public interface Arrangement {
    void add(Block block, Object obj);

    Size2D arrange(BlockContainer blockContainer, Graphics2D graphics2D, RectangleConstraint rectangleConstraint);

    void clear();
}
