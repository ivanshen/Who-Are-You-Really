package org.jfree.chart.block;

import java.awt.Graphics2D;
import java.awt.geom.Rectangle2D.Double;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import org.jfree.ui.HorizontalAlignment;
import org.jfree.ui.Size2D;
import org.jfree.ui.VerticalAlignment;

public class ColumnArrangement implements Arrangement, Serializable {
    private static final long serialVersionUID = -5315388482898581555L;
    private HorizontalAlignment horizontalAlignment;
    private double horizontalGap;
    private VerticalAlignment verticalAlignment;
    private double verticalGap;

    public ColumnArrangement(HorizontalAlignment hAlign, VerticalAlignment vAlign, double hGap, double vGap) {
        this.horizontalAlignment = hAlign;
        this.verticalAlignment = vAlign;
        this.horizontalGap = hGap;
        this.verticalGap = vGap;
    }

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
                throw new RuntimeException("Not implemented.");
            } else if (h == LengthConstraintType.FIXED) {
                return arrangeFF(container, g2, constraint);
            } else {
                if (h == LengthConstraintType.RANGE) {
                    throw new RuntimeException("Not implemented.");
                }
            }
        } else if (w == LengthConstraintType.RANGE) {
            if (h == LengthConstraintType.NONE) {
                throw new RuntimeException("Not implemented.");
            } else if (h == LengthConstraintType.FIXED) {
                return arrangeRF(container, g2, constraint);
            } else {
                if (h == LengthConstraintType.RANGE) {
                    return arrangeRR(container, g2, constraint);
                }
            }
        }
        return new Size2D();
    }

    protected Size2D arrangeFF(BlockContainer container, Graphics2D g2, RectangleConstraint constraint) {
        return arrangeNF(container, g2, constraint);
    }

    protected Size2D arrangeNF(BlockContainer container, Graphics2D g2, RectangleConstraint constraint) {
        List blocks = container.getBlocks();
        double height = constraint.getHeight();
        if (height <= 0.0d) {
            height = Double.POSITIVE_INFINITY;
        }
        double x = 0.0d;
        double y = 0.0d;
        double maxWidth = 0.0d;
        List itemsInColumn = new ArrayList();
        for (int i = 0; i < blocks.size(); i++) {
            Block block = (Block) blocks.get(i);
            Size2D size = block.arrange(g2, RectangleConstraint.NONE);
            if (size.height + y <= height) {
                itemsInColumn.add(block);
                block.setBounds(new Double(x, y, size.width, size.height));
                y = (size.height + y) + this.verticalGap;
                maxWidth = Math.max(maxWidth, size.width);
            } else if (itemsInColumn.isEmpty()) {
                block.setBounds(new Double(x, y, size.width, Math.min(size.height, height - y)));
                y = 0.0d;
                x = (size.width + x) + this.horizontalGap;
            } else {
                itemsInColumn.clear();
                x = (x + maxWidth) + this.horizontalGap;
                maxWidth = size.width;
                block.setBounds(new Double(x, 0.0d, size.width, Math.min(size.height, height)));
                y = size.height + this.verticalGap;
                itemsInColumn.add(block);
            }
        }
        return new Size2D(x + maxWidth, constraint.getHeight());
    }

    protected Size2D arrangeRR(BlockContainer container, Graphics2D g2, RectangleConstraint constraint) {
        Size2D s1 = arrangeNN(container, g2);
        return constraint.getHeightRange().contains(s1.height) ? s1 : arrangeRF(container, g2, constraint.toFixedHeight(constraint.getHeightRange().getUpperBound()));
    }

    protected Size2D arrangeRF(BlockContainer container, Graphics2D g2, RectangleConstraint constraint) {
        Size2D s = arrangeNF(container, g2, constraint);
        return constraint.getWidthRange().contains(s.width) ? s : arrangeFF(container, g2, constraint.toFixedWidth(constraint.getWidthRange().constrain(s.getWidth())));
    }

    protected Size2D arrangeNN(BlockContainer container, Graphics2D g2) {
        double y = 0.0d;
        double height = 0.0d;
        double maxWidth = 0.0d;
        List blocks = container.getBlocks();
        int blockCount = blocks.size();
        if (blockCount > 0) {
            int i;
            Size2D[] sizes = new Size2D[blocks.size()];
            for (i = 0; i < blocks.size(); i++) {
                Block block = (Block) blocks.get(i);
                sizes[i] = block.arrange(g2, RectangleConstraint.NONE);
                height += sizes[i].getHeight();
                maxWidth = Math.max(sizes[i].width, maxWidth);
                block.setBounds(new Double(0.0d, y, sizes[i].width, sizes[i].height));
                y = (sizes[i].height + y) + this.verticalGap;
            }
            if (blockCount > 1) {
                height += this.verticalGap * ((double) (blockCount - 1));
            }
            if (this.horizontalAlignment != HorizontalAlignment.LEFT) {
                for (i = 0; i < blocks.size(); i++) {
                    if (this.horizontalAlignment != HorizontalAlignment.CENTER && this.horizontalAlignment == HorizontalAlignment.RIGHT) {
                    }
                }
            }
        }
        return new Size2D(maxWidth, height);
    }

    public void clear() {
    }

    public boolean equals(Object obj) {
        if (obj == this) {
            return true;
        }
        if (!(obj instanceof ColumnArrangement)) {
            return false;
        }
        ColumnArrangement that = (ColumnArrangement) obj;
        if (this.horizontalAlignment != that.horizontalAlignment) {
            return false;
        }
        if (this.verticalAlignment != that.verticalAlignment) {
            return false;
        }
        if (this.horizontalGap != that.horizontalGap) {
            return false;
        }
        if (this.verticalGap != that.verticalGap) {
            return false;
        }
        return true;
    }
}
