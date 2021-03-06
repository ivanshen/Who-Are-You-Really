package org.jfree.chart.block;

import java.awt.Graphics2D;
import java.awt.geom.Rectangle2D.Double;
import java.io.Serializable;
import org.jfree.data.Range;
import org.jfree.ui.RectangleEdge;
import org.jfree.ui.Size2D;
import org.jfree.util.ObjectUtilities;

public class BorderArrangement implements Arrangement, Serializable {
    static final /* synthetic */ boolean $assertionsDisabled;
    private static final long serialVersionUID = 506071142274883745L;
    private Block bottomBlock;
    private Block centerBlock;
    private Block leftBlock;
    private Block rightBlock;
    private Block topBlock;

    static {
        $assertionsDisabled = !BorderArrangement.class.desiredAssertionStatus() ? true : $assertionsDisabled;
    }

    public void add(Block block, Object key) {
        if (key instanceof RectangleEdge) {
            RectangleEdge edge = (RectangleEdge) key;
            if (edge == RectangleEdge.TOP) {
                this.topBlock = block;
                return;
            } else if (edge == RectangleEdge.BOTTOM) {
                this.bottomBlock = block;
                return;
            } else if (edge == RectangleEdge.LEFT) {
                this.leftBlock = block;
                return;
            } else if (edge == RectangleEdge.RIGHT) {
                this.rightBlock = block;
                return;
            } else {
                return;
            }
        }
        this.centerBlock = block;
    }

    public Size2D arrange(BlockContainer container, Graphics2D g2, RectangleConstraint constraint) {
        RectangleConstraint contentConstraint = container.toContentConstraint(constraint);
        Size2D contentSize = null;
        LengthConstraintType w = contentConstraint.getWidthConstraintType();
        LengthConstraintType h = contentConstraint.getHeightConstraintType();
        if (w == LengthConstraintType.NONE) {
            if (h == LengthConstraintType.NONE) {
                contentSize = arrangeNN(container, g2);
            } else if (h == LengthConstraintType.FIXED) {
                throw new RuntimeException("Not implemented.");
            } else if (h == LengthConstraintType.RANGE) {
                throw new RuntimeException("Not implemented.");
            }
        } else if (w == LengthConstraintType.FIXED) {
            if (h == LengthConstraintType.NONE) {
                contentSize = arrangeFN(container, g2, constraint.getWidth());
            } else if (h == LengthConstraintType.FIXED) {
                contentSize = arrangeFF(container, g2, constraint);
            } else if (h == LengthConstraintType.RANGE) {
                contentSize = arrangeFR(container, g2, constraint);
            }
        } else if (w == LengthConstraintType.RANGE) {
            if (h == LengthConstraintType.NONE) {
                throw new RuntimeException("Not implemented.");
            } else if (h == LengthConstraintType.FIXED) {
                throw new RuntimeException("Not implemented.");
            } else if (h == LengthConstraintType.RANGE) {
                contentSize = arrangeRR(container, constraint.getWidthRange(), constraint.getHeightRange(), g2);
            }
        }
        if ($assertionsDisabled || contentSize != null) {
            return new Size2D(container.calculateTotalWidth(contentSize.getWidth()), container.calculateTotalHeight(contentSize.getHeight()));
        }
        throw new AssertionError();
    }

    protected Size2D arrangeNN(BlockContainer container, Graphics2D g2) {
        double[] w = new double[5];
        double[] h = new double[5];
        if (this.topBlock != null) {
            Size2D size = this.topBlock.arrange(g2, RectangleConstraint.NONE);
            w[0] = size.width;
            h[0] = size.height;
        }
        if (this.bottomBlock != null) {
            size = this.bottomBlock.arrange(g2, RectangleConstraint.NONE);
            w[1] = size.width;
            h[1] = size.height;
        }
        if (this.leftBlock != null) {
            size = this.leftBlock.arrange(g2, RectangleConstraint.NONE);
            w[2] = size.width;
            h[2] = size.height;
        }
        if (this.rightBlock != null) {
            size = this.rightBlock.arrange(g2, RectangleConstraint.NONE);
            w[3] = size.width;
            h[3] = size.height;
        }
        h[2] = Math.max(h[2], h[3]);
        h[3] = h[2];
        if (this.centerBlock != null) {
            size = this.centerBlock.arrange(g2, RectangleConstraint.NONE);
            w[4] = size.width;
            h[4] = size.height;
        }
        double width = Math.max(w[0], Math.max(w[1], (w[2] + w[4]) + w[3]));
        double centerHeight = Math.max(h[2], Math.max(h[3], h[4]));
        double height = (h[0] + h[1]) + centerHeight;
        if (this.topBlock != null) {
            this.topBlock.setBounds(new Double(0.0d, 0.0d, width, h[0]));
        }
        if (this.bottomBlock != null) {
            this.bottomBlock.setBounds(new Double(0.0d, height - h[1], width, h[1]));
        }
        if (this.leftBlock != null) {
            this.leftBlock.setBounds(new Double(0.0d, h[0], w[2], centerHeight));
        }
        if (this.rightBlock != null) {
            this.rightBlock.setBounds(new Double(width - w[3], h[0], w[3], centerHeight));
        }
        if (this.centerBlock != null) {
            this.centerBlock.setBounds(new Double(w[2], h[0], (width - w[2]) - w[3], centerHeight));
        }
        return new Size2D(width, height);
    }

    protected Size2D arrangeFR(BlockContainer container, Graphics2D g2, RectangleConstraint constraint) {
        Size2D size1 = arrangeFN(container, g2, constraint.getWidth());
        return constraint.getHeightRange().contains(size1.getHeight()) ? size1 : arrange(container, g2, constraint.toFixedHeight(constraint.getHeightRange().constrain(size1.getHeight())));
    }

    protected Size2D arrangeFN(BlockContainer container, Graphics2D g2, double width) {
        double[] w = new double[5];
        double[] h = new double[5];
        RectangleConstraint c1 = new RectangleConstraint(width, null, LengthConstraintType.FIXED, 0.0d, null, LengthConstraintType.NONE);
        if (this.topBlock != null) {
            Size2D size = this.topBlock.arrange(g2, c1);
            w[0] = size.width;
            h[0] = size.height;
        }
        if (this.bottomBlock != null) {
            size = this.bottomBlock.arrange(g2, c1);
            w[1] = size.width;
            h[1] = size.height;
        }
        RectangleConstraint c2 = new RectangleConstraint(0.0d, new Range(0.0d, width), LengthConstraintType.RANGE, 0.0d, null, LengthConstraintType.NONE);
        if (this.leftBlock != null) {
            size = this.leftBlock.arrange(g2, c2);
            w[2] = size.width;
            h[2] = size.height;
        }
        if (this.rightBlock != null) {
            double maxW = Math.max(width - w[2], 0.0d);
            size = this.rightBlock.arrange(g2, new RectangleConstraint(0.0d, new Range(Math.min(w[2], maxW), maxW), LengthConstraintType.RANGE, 0.0d, null, LengthConstraintType.NONE));
            w[3] = size.width;
            h[3] = size.height;
        }
        h[2] = Math.max(h[2], h[3]);
        h[3] = h[2];
        if (this.centerBlock != null) {
            size = this.centerBlock.arrange(g2, new RectangleConstraint((width - w[2]) - w[3], null, LengthConstraintType.FIXED, 0.0d, null, LengthConstraintType.NONE));
            w[4] = size.width;
            h[4] = size.height;
        }
        return arrange(container, g2, new RectangleConstraint(width, (h[0] + h[1]) + Math.max(h[2], Math.max(h[3], h[4]))));
    }

    protected Size2D arrangeRR(BlockContainer container, Range widthRange, Range heightRange, Graphics2D g2) {
        double[] w = new double[5];
        double[] h = new double[5];
        if (this.topBlock != null) {
            Size2D size = this.topBlock.arrange(g2, new RectangleConstraint(widthRange, heightRange));
            w[0] = size.width;
            h[0] = size.height;
        }
        if (this.bottomBlock != null) {
            size = this.bottomBlock.arrange(g2, new RectangleConstraint(widthRange, Range.shift(heightRange, -h[0], $assertionsDisabled)));
            w[1] = size.width;
            h[1] = size.height;
        }
        Range heightRange3 = Range.shift(heightRange, -(h[0] + h[1]));
        if (this.leftBlock != null) {
            size = this.leftBlock.arrange(g2, new RectangleConstraint(widthRange, heightRange3));
            w[2] = size.width;
            h[2] = size.height;
        }
        Range widthRange2 = Range.shift(widthRange, -w[2], $assertionsDisabled);
        if (this.rightBlock != null) {
            size = this.rightBlock.arrange(g2, new RectangleConstraint(widthRange2, heightRange3));
            w[3] = size.width;
            h[3] = size.height;
        }
        h[2] = Math.max(h[2], h[3]);
        h[3] = h[2];
        Range widthRange3 = Range.shift(widthRange, -(w[2] + w[3]), $assertionsDisabled);
        if (this.centerBlock != null) {
            size = this.centerBlock.arrange(g2, new RectangleConstraint(widthRange3, heightRange3));
            w[4] = size.width;
            h[4] = size.height;
        }
        double width = Math.max(w[0], Math.max(w[1], (w[2] + w[4]) + w[3]));
        double height = (h[0] + h[1]) + Math.max(h[2], Math.max(h[3], h[4]));
        if (this.topBlock != null) {
            this.topBlock.setBounds(new Double(0.0d, 0.0d, width, h[0]));
        }
        if (this.bottomBlock != null) {
            this.bottomBlock.setBounds(new Double(0.0d, height - h[1], width, h[1]));
        }
        if (this.leftBlock != null) {
            this.leftBlock.setBounds(new Double(0.0d, h[0], w[2], h[2]));
        }
        if (this.rightBlock != null) {
            this.rightBlock.setBounds(new Double(width - w[3], h[0], w[3], h[3]));
        }
        if (this.centerBlock != null) {
            this.centerBlock.setBounds(new Double(w[2], h[0], (width - w[2]) - w[3], (height - h[0]) - h[1]));
        }
        return new Size2D(width, height);
    }

    protected Size2D arrangeFF(BlockContainer container, Graphics2D g2, RectangleConstraint constraint) {
        double[] w = new double[5];
        double[] h = new double[5];
        w[0] = constraint.getWidth();
        if (this.topBlock != null) {
            h[0] = this.topBlock.arrange(g2, new RectangleConstraint(w[0], null, LengthConstraintType.FIXED, 0.0d, new Range(0.0d, constraint.getHeight()), LengthConstraintType.RANGE)).height;
        }
        w[1] = w[0];
        if (this.bottomBlock != null) {
            h[1] = this.bottomBlock.arrange(g2, new RectangleConstraint(w[0], null, LengthConstraintType.FIXED, 0.0d, new Range(0.0d, constraint.getHeight() - h[0]), LengthConstraintType.RANGE)).height;
        }
        h[2] = (constraint.getHeight() - h[1]) - h[0];
        if (this.leftBlock != null) {
            w[2] = this.leftBlock.arrange(g2, new RectangleConstraint(0.0d, new Range(0.0d, constraint.getWidth()), LengthConstraintType.RANGE, h[2], null, LengthConstraintType.FIXED)).width;
        }
        h[3] = h[2];
        if (this.rightBlock != null) {
            w[3] = this.rightBlock.arrange(g2, new RectangleConstraint(0.0d, new Range(0.0d, Math.max(constraint.getWidth() - w[2], 0.0d)), LengthConstraintType.RANGE, h[2], null, LengthConstraintType.FIXED)).width;
        }
        h[4] = h[2];
        w[4] = (constraint.getWidth() - w[3]) - w[2];
        RectangleConstraint c5 = new RectangleConstraint(w[4], h[4]);
        if (this.centerBlock != null) {
            this.centerBlock.arrange(g2, c5);
        }
        if (this.topBlock != null) {
            this.topBlock.setBounds(new Double(0.0d, 0.0d, w[0], h[0]));
        }
        if (this.bottomBlock != null) {
            this.bottomBlock.setBounds(new Double(0.0d, h[0] + h[2], w[1], h[1]));
        }
        if (this.leftBlock != null) {
            this.leftBlock.setBounds(new Double(0.0d, h[0], w[2], h[2]));
        }
        if (this.rightBlock != null) {
            this.rightBlock.setBounds(new Double(w[2] + w[4], h[0], w[3], h[3]));
        }
        if (this.centerBlock != null) {
            this.centerBlock.setBounds(new Double(w[2], h[0], w[4], h[4]));
        }
        return new Size2D(constraint.getWidth(), constraint.getHeight());
    }

    public void clear() {
        this.centerBlock = null;
        this.topBlock = null;
        this.bottomBlock = null;
        this.leftBlock = null;
        this.rightBlock = null;
    }

    public boolean equals(Object obj) {
        if (obj == this) {
            return true;
        }
        if (!(obj instanceof BorderArrangement)) {
            return $assertionsDisabled;
        }
        BorderArrangement that = (BorderArrangement) obj;
        if (!ObjectUtilities.equal(this.topBlock, that.topBlock)) {
            return $assertionsDisabled;
        }
        if (!ObjectUtilities.equal(this.bottomBlock, that.bottomBlock)) {
            return $assertionsDisabled;
        }
        if (!ObjectUtilities.equal(this.leftBlock, that.leftBlock)) {
            return $assertionsDisabled;
        }
        if (!ObjectUtilities.equal(this.rightBlock, that.rightBlock)) {
            return $assertionsDisabled;
        }
        if (ObjectUtilities.equal(this.centerBlock, that.centerBlock)) {
            return true;
        }
        return $assertionsDisabled;
    }
}
