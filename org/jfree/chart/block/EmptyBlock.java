package org.jfree.chart.block;

import java.awt.Graphics2D;
import java.awt.geom.Rectangle2D;
import java.io.Serializable;
import org.jfree.ui.Size2D;
import org.jfree.util.PublicCloneable;

public class EmptyBlock extends AbstractBlock implements Block, Cloneable, PublicCloneable, Serializable {
    private static final long serialVersionUID = -4083197869412648579L;

    public EmptyBlock(double width, double height) {
        setWidth(width);
        setHeight(height);
    }

    public Size2D arrange(Graphics2D g2, RectangleConstraint constraint) {
        return constraint.calculateConstrainedSize(new Size2D(calculateTotalWidth(getWidth()), calculateTotalHeight(getHeight())));
    }

    public void draw(Graphics2D g2, Rectangle2D area) {
        draw(g2, area, null);
    }

    public Object draw(Graphics2D g2, Rectangle2D area, Object params) {
        drawBorder(g2, trimMargin(area));
        return null;
    }

    public Object clone() throws CloneNotSupportedException {
        return super.clone();
    }
}
