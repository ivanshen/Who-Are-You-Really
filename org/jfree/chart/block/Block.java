package org.jfree.chart.block;

import java.awt.Graphics2D;
import java.awt.geom.Rectangle2D;
import org.jfree.ui.Drawable;
import org.jfree.ui.Size2D;

public interface Block extends Drawable {
    Size2D arrange(Graphics2D graphics2D);

    Size2D arrange(Graphics2D graphics2D, RectangleConstraint rectangleConstraint);

    Object draw(Graphics2D graphics2D, Rectangle2D rectangle2D, Object obj);

    Rectangle2D getBounds();

    String getID();

    void setBounds(Rectangle2D rectangle2D);

    void setID(String str);
}
