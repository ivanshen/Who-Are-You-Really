package org.jfree.chart.block;

import java.awt.Graphics2D;
import java.awt.geom.Rectangle2D.Double;
import java.io.Serializable;
import java.util.List;
import org.jfree.ui.Size2D;

public class GridArrangement implements Arrangement, Serializable {
    private static final long serialVersionUID = -2563758090144655938L;
    private int columns;
    private int rows;

    public GridArrangement(int rows, int columns) {
        this.rows = rows;
        this.columns = columns;
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
                return arrangeNF(container, g2, constraint);
            }
            if (h == LengthConstraintType.RANGE) {
                return arrangeNR(container, g2, constraint);
            }
        } else if (w == LengthConstraintType.FIXED) {
            if (h == LengthConstraintType.NONE) {
                return arrangeFN(container, g2, constraint);
            }
            if (h == LengthConstraintType.FIXED) {
                return arrangeFF(container, g2, constraint);
            }
            if (h == LengthConstraintType.RANGE) {
                return arrangeFR(container, g2, constraint);
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
        throw new RuntimeException("Should never get to here!");
    }

    protected Size2D arrangeNN(BlockContainer container, Graphics2D g2) {
        double maxW = 0.0d;
        double maxH = 0.0d;
        for (Block b : container.getBlocks()) {
            if (b != null) {
                Size2D s = b.arrange(g2, RectangleConstraint.NONE);
                maxW = Math.max(maxW, s.width);
                maxH = Math.max(maxH, s.height);
            }
        }
        double d = ((double) this.columns) * maxW;
        return arrangeFF(container, g2, new RectangleConstraint(d, ((double) this.rows) * maxH));
    }

    protected Size2D arrangeFF(BlockContainer container, Graphics2D g2, RectangleConstraint constraint) {
        double width = constraint.getWidth() / ((double) this.columns);
        double height = constraint.getHeight() / ((double) this.rows);
        List blocks = container.getBlocks();
        for (int c = 0; c < this.columns; c++) {
            for (int r = 0; r < this.rows; r++) {
                int index = (this.columns * r) + c;
                if (index >= blocks.size()) {
                    break;
                }
                Block b = (Block) blocks.get(index);
                if (b != null) {
                    b.setBounds(new Double(((double) c) * width, ((double) r) * height, width, height));
                }
            }
        }
        return new Size2D(((double) this.columns) * width, ((double) this.rows) * height);
    }

    protected Size2D arrangeFR(BlockContainer container, Graphics2D g2, RectangleConstraint constraint) {
        Size2D size1 = arrange(container, g2, constraint.toUnconstrainedHeight());
        return constraint.getHeightRange().contains(size1.getHeight()) ? size1 : arrange(container, g2, constraint.toFixedHeight(constraint.getHeightRange().constrain(size1.getHeight())));
    }

    protected Size2D arrangeRF(BlockContainer container, Graphics2D g2, RectangleConstraint constraint) {
        Size2D size1 = arrange(container, g2, constraint.toUnconstrainedWidth());
        return constraint.getWidthRange().contains(size1.getWidth()) ? size1 : arrange(container, g2, constraint.toFixedWidth(constraint.getWidthRange().constrain(size1.getWidth())));
    }

    protected Size2D arrangeRN(BlockContainer container, Graphics2D g2, RectangleConstraint constraint) {
        Size2D size1 = arrange(container, g2, constraint.toUnconstrainedWidth());
        return constraint.getWidthRange().contains(size1.getWidth()) ? size1 : arrange(container, g2, constraint.toFixedWidth(constraint.getWidthRange().constrain(size1.getWidth())));
    }

    protected Size2D arrangeNR(BlockContainer container, Graphics2D g2, RectangleConstraint constraint) {
        Size2D size1 = arrange(container, g2, constraint.toUnconstrainedHeight());
        return constraint.getHeightRange().contains(size1.getHeight()) ? size1 : arrange(container, g2, constraint.toFixedHeight(constraint.getHeightRange().constrain(size1.getHeight())));
    }

    protected Size2D arrangeRR(BlockContainer container, Graphics2D g2, RectangleConstraint constraint) {
        Size2D size1 = arrange(container, g2, RectangleConstraint.NONE);
        if (constraint.getWidthRange().contains(size1.getWidth())) {
            if (constraint.getHeightRange().contains(size1.getHeight())) {
                return size1;
            }
            return arrangeFF(container, g2, new RectangleConstraint(size1.getWidth(), constraint.getHeightRange().constrain(size1.getHeight())));
        } else if (constraint.getHeightRange().contains(size1.getHeight())) {
            return arrangeFF(container, g2, new RectangleConstraint(constraint.getWidthRange().constrain(size1.getWidth()), size1.getHeight()));
        } else {
            return arrangeFF(container, g2, new RectangleConstraint(constraint.getWidthRange().constrain(size1.getWidth()), constraint.getHeightRange().constrain(size1.getHeight())));
        }
    }

    protected Size2D arrangeFN(BlockContainer container, Graphics2D g2, RectangleConstraint constraint) {
        RectangleConstraint rectangleConstraint = constraint;
        RectangleConstraint bc = rectangleConstraint.toFixedWidth(constraint.getWidth() / ((double) this.columns));
        List blocks = container.getBlocks();
        double maxH = 0.0d;
        int r = 0;
        while (true) {
            int i = this.rows;
            if (r < r0) {
                int c = 0;
                while (true) {
                    i = this.columns;
                    if (c >= r0) {
                        break;
                    }
                    int index = (this.columns * r) + c;
                    if (index >= blocks.size()) {
                        break;
                    }
                    Block b = (Block) blocks.get(index);
                    if (b != null) {
                        maxH = Math.max(maxH, b.arrange(g2, bc).getHeight());
                    }
                    c++;
                }
                r++;
            } else {
                return arrange(container, g2, constraint.toFixedHeight(((double) this.rows) * maxH));
            }
        }
    }

    protected Size2D arrangeNF(BlockContainer container, Graphics2D g2, RectangleConstraint constraint) {
        RectangleConstraint rectangleConstraint = constraint;
        RectangleConstraint bc = rectangleConstraint.toFixedHeight(constraint.getHeight() / ((double) this.rows));
        List blocks = container.getBlocks();
        double maxW = 0.0d;
        int r = 0;
        while (true) {
            int i = this.rows;
            if (r < r0) {
                int c = 0;
                while (true) {
                    i = this.columns;
                    if (c >= r0) {
                        break;
                    }
                    int index = (this.columns * r) + c;
                    if (index >= blocks.size()) {
                        break;
                    }
                    Block b = (Block) blocks.get(index);
                    if (b != null) {
                        maxW = Math.max(maxW, b.arrange(g2, bc).getWidth());
                    }
                    c++;
                }
                r++;
            } else {
                return arrange(container, g2, constraint.toFixedWidth(((double) this.columns) * maxW));
            }
        }
    }

    public void clear() {
    }

    public boolean equals(Object obj) {
        if (obj == this) {
            return true;
        }
        if (!(obj instanceof GridArrangement)) {
            return false;
        }
        GridArrangement that = (GridArrangement) obj;
        if (this.columns != that.columns) {
            return false;
        }
        if (this.rows != that.rows) {
            return false;
        }
        return true;
    }
}
