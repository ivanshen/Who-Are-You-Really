package org.jfree.chart.block;

import java.awt.Graphics2D;
import java.awt.geom.Rectangle2D.Double;
import java.io.Serializable;
import org.jfree.chart.axis.DateAxis;
import org.jfree.ui.Size2D;

public class CenterArrangement implements Arrangement, Serializable {
    private static final long serialVersionUID = -353308149220382047L;

    public void add(Block block, Object key) {
    }

    public Size2D arrange(BlockContainer container, Graphics2D g2, RectangleConstraint constraint) {
        LengthConstraintType w = constraint.getWidthConstraintType();
        LengthConstraintType h = constraint.getHeightConstraintType();
        if (w == LengthConstraintType.NONE) {
            if (h == LengthConstraintType.NONE) {
                return arrangeNN(container, g2);
            }
            if (h == LengthConstraintType.FIXED) {
                throw new RuntimeException("Not implemented.");
            } else if (h == LengthConstraintType.RANGE) {
                throw new RuntimeException("Not implemented.");
            }
        } else if (w == LengthConstraintType.FIXED) {
            if (h == LengthConstraintType.NONE) {
                return arrangeFN(container, g2, constraint);
            }
            if (h == LengthConstraintType.FIXED) {
                throw new RuntimeException("Not implemented.");
            } else if (h == LengthConstraintType.RANGE) {
                throw new RuntimeException("Not implemented.");
            }
        } else if (w == LengthConstraintType.RANGE) {
            if (h == LengthConstraintType.NONE) {
                return arrangeRN(container, g2, constraint);
            }
            if (h == LengthConstraintType.FIXED) {
                return arrangeRF(container, g2, constraint);
            }
            if (h == LengthConstraintType.RANGE) {
                return arrangeRR(container, g2, constraint);
            }
        }
        throw new IllegalArgumentException("Unknown LengthConstraintType.");
    }

    protected Size2D arrangeFN(BlockContainer container, Graphics2D g2, RectangleConstraint constraint) {
        Block b = (Block) container.getBlocks().get(0);
        Size2D s = b.arrange(g2, RectangleConstraint.NONE);
        double width = constraint.getWidth();
        b.setBounds(new Double((width - s.width) / DateAxis.DEFAULT_AUTO_RANGE_MINIMUM_SIZE_IN_MILLISECONDS, 0.0d, s.width, s.height));
        return new Size2D((width - s.width) / DateAxis.DEFAULT_AUTO_RANGE_MINIMUM_SIZE_IN_MILLISECONDS, s.height);
    }

    protected Size2D arrangeFR(BlockContainer container, Graphics2D g2, RectangleConstraint constraint) {
        Size2D s = arrangeFN(container, g2, constraint);
        return constraint.getHeightRange().contains(s.height) ? s : arrangeFF(container, g2, constraint.toFixedHeight(constraint.getHeightRange().constrain(s.getHeight())));
    }

    protected Size2D arrangeFF(BlockContainer container, Graphics2D g2, RectangleConstraint constraint) {
        return arrangeFN(container, g2, constraint);
    }

    protected Size2D arrangeRR(BlockContainer container, Graphics2D g2, RectangleConstraint constraint) {
        Size2D s1 = arrangeNN(container, g2);
        return constraint.getWidthRange().contains(s1.width) ? s1 : arrangeFR(container, g2, constraint.toFixedWidth(constraint.getWidthRange().getUpperBound()));
    }

    protected Size2D arrangeRF(BlockContainer container, Graphics2D g2, RectangleConstraint constraint) {
        Size2D s = arrangeNF(container, g2, constraint);
        return constraint.getWidthRange().contains(s.width) ? s : arrangeFF(container, g2, constraint.toFixedWidth(constraint.getWidthRange().constrain(s.getWidth())));
    }

    protected Size2D arrangeRN(BlockContainer container, Graphics2D g2, RectangleConstraint constraint) {
        Size2D s1 = arrangeNN(container, g2);
        return constraint.getWidthRange().contains(s1.width) ? s1 : arrangeFN(container, g2, constraint.toFixedWidth(constraint.getWidthRange().getUpperBound()));
    }

    protected Size2D arrangeNN(BlockContainer container, Graphics2D g2) {
        Block b = (Block) container.getBlocks().get(0);
        Size2D s = b.arrange(g2, RectangleConstraint.NONE);
        b.setBounds(new Double(0.0d, 0.0d, s.width, s.height));
        return new Size2D(s.width, s.height);
    }

    protected Size2D arrangeNF(BlockContainer container, Graphics2D g2, RectangleConstraint constraint) {
        return arrangeNN(container, g2);
    }

    public void clear() {
    }

    public boolean equals(Object obj) {
        if (obj == this || (obj instanceof CenterArrangement)) {
            return true;
        }
        return false;
    }
}
