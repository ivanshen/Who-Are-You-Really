package org.jfree.chart.title;

import java.awt.Graphics2D;
import java.awt.geom.Rectangle2D;
import org.jfree.chart.block.LengthConstraintType;
import org.jfree.chart.block.RectangleConstraint;
import org.jfree.data.Range;
import org.jfree.text.TextUtilities;
import org.jfree.ui.Size2D;
import org.jfree.ui.TextAnchor;

public class ShortTextTitle extends TextTitle {
    static final /* synthetic */ boolean $assertionsDisabled;

    static {
        $assertionsDisabled = !ShortTextTitle.class.desiredAssertionStatus();
    }

    public ShortTextTitle(String text) {
        setText(text);
    }

    public Size2D arrange(Graphics2D g2, RectangleConstraint constraint) {
        RectangleConstraint cc = toContentConstraint(constraint);
        LengthConstraintType w = cc.getWidthConstraintType();
        LengthConstraintType h = cc.getHeightConstraintType();
        Size2D contentSize = null;
        if (w == LengthConstraintType.NONE) {
            if (h == LengthConstraintType.NONE) {
                contentSize = arrangeNN(g2);
            } else if (h == LengthConstraintType.RANGE) {
                throw new RuntimeException("Not yet implemented.");
            } else if (h == LengthConstraintType.FIXED) {
                throw new RuntimeException("Not yet implemented.");
            }
        } else if (w == LengthConstraintType.RANGE) {
            if (h == LengthConstraintType.NONE) {
                contentSize = arrangeRN(g2, cc.getWidthRange());
            } else if (h == LengthConstraintType.RANGE) {
                contentSize = arrangeRR(g2, cc.getWidthRange(), cc.getHeightRange());
            } else if (h == LengthConstraintType.FIXED) {
                throw new RuntimeException("Not yet implemented.");
            }
        } else if (w == LengthConstraintType.FIXED) {
            if (h == LengthConstraintType.NONE) {
                contentSize = arrangeFN(g2, cc.getWidth());
            } else if (h == LengthConstraintType.RANGE) {
                throw new RuntimeException("Not yet implemented.");
            } else if (h == LengthConstraintType.FIXED) {
                throw new RuntimeException("Not yet implemented.");
            }
        }
        if (!$assertionsDisabled && contentSize == null) {
            throw new AssertionError();
        } else if (contentSize.width <= 0.0d || contentSize.height <= 0.0d) {
            return new Size2D(0.0d, 0.0d);
        } else {
            return new Size2D(calculateTotalWidth(contentSize.getWidth()), calculateTotalHeight(contentSize.getHeight()));
        }
    }

    protected Size2D arrangeNN(Graphics2D g2) {
        Range max = new Range(0.0d, 3.4028234663852886E38d);
        return arrangeRR(g2, max, max);
    }

    protected Size2D arrangeRN(Graphics2D g2, Range widthRange) {
        Size2D s = arrangeNN(g2);
        return widthRange.contains(s.getWidth()) ? s : arrangeFN(g2, widthRange.constrain(s.getWidth()));
    }

    protected Size2D arrangeFN(Graphics2D g2, double w) {
        g2.setFont(getFont());
        Rectangle2D bounds = TextUtilities.getTextBounds(getText(), g2, g2.getFontMetrics(getFont()));
        if (bounds.getWidth() <= w) {
            return new Size2D(w, bounds.getHeight());
        }
        return new Size2D(0.0d, 0.0d);
    }

    protected Size2D arrangeRR(Graphics2D g2, Range widthRange, Range heightRange) {
        g2.setFont(getFont());
        Rectangle2D bounds = TextUtilities.getTextBounds(getText(), g2, g2.getFontMetrics(getFont()));
        if (bounds.getWidth() > widthRange.getUpperBound() || bounds.getHeight() > heightRange.getUpperBound()) {
            return new Size2D(0.0d, 0.0d);
        }
        return new Size2D(bounds.getWidth(), bounds.getHeight());
    }

    public Object draw(Graphics2D g2, Rectangle2D area, Object params) {
        if (!area.isEmpty()) {
            area = trimMargin(area);
            drawBorder(g2, area);
            area = trimPadding(trimBorder(area));
            g2.setFont(getFont());
            g2.setPaint(getPaint());
            TextUtilities.drawAlignedString(getText(), g2, (float) area.getMinX(), (float) area.getMinY(), TextAnchor.TOP_LEFT);
        }
        return null;
    }
}
