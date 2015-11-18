package org.jfree.chart.axis;

import java.io.Serializable;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.util.ParamChecks;
import org.jfree.text.TextBlockAnchor;
import org.jfree.ui.RectangleAnchor;
import org.jfree.ui.RectangleEdge;
import org.jfree.ui.TextAnchor;

public class CategoryLabelPositions implements Serializable {
    public static final CategoryLabelPositions DOWN_45;
    public static final CategoryLabelPositions DOWN_90;
    public static final CategoryLabelPositions STANDARD;
    public static final CategoryLabelPositions UP_45;
    public static final CategoryLabelPositions UP_90;
    private static final long serialVersionUID = -8999557901920364580L;
    private CategoryLabelPosition positionForAxisAtBottom;
    private CategoryLabelPosition positionForAxisAtLeft;
    private CategoryLabelPosition positionForAxisAtRight;
    private CategoryLabelPosition positionForAxisAtTop;

    static {
        STANDARD = new CategoryLabelPositions(new CategoryLabelPosition(RectangleAnchor.BOTTOM, TextBlockAnchor.BOTTOM_CENTER), new CategoryLabelPosition(RectangleAnchor.TOP, TextBlockAnchor.TOP_CENTER), new CategoryLabelPosition(RectangleAnchor.RIGHT, TextBlockAnchor.CENTER_RIGHT, CategoryLabelWidthType.RANGE, 0.3f), new CategoryLabelPosition(RectangleAnchor.LEFT, TextBlockAnchor.CENTER_LEFT, CategoryLabelWidthType.RANGE, 0.3f));
        UP_90 = new CategoryLabelPositions(new CategoryLabelPosition(RectangleAnchor.BOTTOM, TextBlockAnchor.CENTER_LEFT, TextAnchor.CENTER_LEFT, -1.5707963267948966d, CategoryLabelWidthType.RANGE, 0.3f), new CategoryLabelPosition(RectangleAnchor.TOP, TextBlockAnchor.CENTER_RIGHT, TextAnchor.CENTER_RIGHT, -1.5707963267948966d, CategoryLabelWidthType.RANGE, 0.3f), new CategoryLabelPosition(RectangleAnchor.RIGHT, TextBlockAnchor.BOTTOM_CENTER, TextAnchor.BOTTOM_CENTER, -1.5707963267948966d, CategoryLabelWidthType.CATEGORY, 0.9f), new CategoryLabelPosition(RectangleAnchor.LEFT, TextBlockAnchor.TOP_CENTER, TextAnchor.TOP_CENTER, -1.5707963267948966d, CategoryLabelWidthType.CATEGORY, 0.9f));
        DOWN_90 = new CategoryLabelPositions(new CategoryLabelPosition(RectangleAnchor.BOTTOM, TextBlockAnchor.CENTER_RIGHT, TextAnchor.CENTER_RIGHT, 1.5707963267948966d, CategoryLabelWidthType.RANGE, 0.3f), new CategoryLabelPosition(RectangleAnchor.TOP, TextBlockAnchor.CENTER_LEFT, TextAnchor.CENTER_LEFT, 1.5707963267948966d, CategoryLabelWidthType.RANGE, 0.3f), new CategoryLabelPosition(RectangleAnchor.RIGHT, TextBlockAnchor.TOP_CENTER, TextAnchor.TOP_CENTER, 1.5707963267948966d, CategoryLabelWidthType.CATEGORY, 0.9f), new CategoryLabelPosition(RectangleAnchor.LEFT, TextBlockAnchor.BOTTOM_CENTER, TextAnchor.BOTTOM_CENTER, 1.5707963267948966d, CategoryLabelWidthType.CATEGORY, 0.9f));
        UP_45 = createUpRotationLabelPositions(0.7853981633974483d);
        DOWN_45 = createDownRotationLabelPositions(0.7853981633974483d);
    }

    public static CategoryLabelPositions createUpRotationLabelPositions(double angle) {
        return new CategoryLabelPositions(new CategoryLabelPosition(RectangleAnchor.BOTTOM, TextBlockAnchor.BOTTOM_LEFT, TextAnchor.BOTTOM_LEFT, -angle, CategoryLabelWidthType.RANGE, JFreeChart.DEFAULT_BACKGROUND_IMAGE_ALPHA), new CategoryLabelPosition(RectangleAnchor.TOP, TextBlockAnchor.TOP_RIGHT, TextAnchor.TOP_RIGHT, -angle, CategoryLabelWidthType.RANGE, JFreeChart.DEFAULT_BACKGROUND_IMAGE_ALPHA), new CategoryLabelPosition(RectangleAnchor.RIGHT, TextBlockAnchor.BOTTOM_RIGHT, TextAnchor.BOTTOM_RIGHT, -angle, CategoryLabelWidthType.RANGE, JFreeChart.DEFAULT_BACKGROUND_IMAGE_ALPHA), new CategoryLabelPosition(RectangleAnchor.LEFT, TextBlockAnchor.TOP_LEFT, TextAnchor.TOP_LEFT, -angle, CategoryLabelWidthType.RANGE, JFreeChart.DEFAULT_BACKGROUND_IMAGE_ALPHA));
    }

    public static CategoryLabelPositions createDownRotationLabelPositions(double angle) {
        return new CategoryLabelPositions(new CategoryLabelPosition(RectangleAnchor.BOTTOM, TextBlockAnchor.BOTTOM_RIGHT, TextAnchor.BOTTOM_RIGHT, angle, CategoryLabelWidthType.RANGE, JFreeChart.DEFAULT_BACKGROUND_IMAGE_ALPHA), new CategoryLabelPosition(RectangleAnchor.TOP, TextBlockAnchor.TOP_LEFT, TextAnchor.TOP_LEFT, angle, CategoryLabelWidthType.RANGE, JFreeChart.DEFAULT_BACKGROUND_IMAGE_ALPHA), new CategoryLabelPosition(RectangleAnchor.RIGHT, TextBlockAnchor.TOP_RIGHT, TextAnchor.TOP_RIGHT, angle, CategoryLabelWidthType.RANGE, JFreeChart.DEFAULT_BACKGROUND_IMAGE_ALPHA), new CategoryLabelPosition(RectangleAnchor.LEFT, TextBlockAnchor.BOTTOM_LEFT, TextAnchor.BOTTOM_LEFT, angle, CategoryLabelWidthType.RANGE, JFreeChart.DEFAULT_BACKGROUND_IMAGE_ALPHA));
    }

    public CategoryLabelPositions() {
        this.positionForAxisAtTop = new CategoryLabelPosition();
        this.positionForAxisAtBottom = new CategoryLabelPosition();
        this.positionForAxisAtLeft = new CategoryLabelPosition();
        this.positionForAxisAtRight = new CategoryLabelPosition();
    }

    public CategoryLabelPositions(CategoryLabelPosition top, CategoryLabelPosition bottom, CategoryLabelPosition left, CategoryLabelPosition right) {
        ParamChecks.nullNotPermitted(top, "top");
        ParamChecks.nullNotPermitted(bottom, "bottom");
        ParamChecks.nullNotPermitted(left, "left");
        ParamChecks.nullNotPermitted(right, "right");
        this.positionForAxisAtTop = top;
        this.positionForAxisAtBottom = bottom;
        this.positionForAxisAtLeft = left;
        this.positionForAxisAtRight = right;
    }

    public CategoryLabelPosition getLabelPosition(RectangleEdge edge) {
        if (edge == RectangleEdge.TOP) {
            return this.positionForAxisAtTop;
        }
        if (edge == RectangleEdge.BOTTOM) {
            return this.positionForAxisAtBottom;
        }
        if (edge == RectangleEdge.LEFT) {
            return this.positionForAxisAtLeft;
        }
        if (edge == RectangleEdge.RIGHT) {
            return this.positionForAxisAtRight;
        }
        return null;
    }

    public static CategoryLabelPositions replaceTopPosition(CategoryLabelPositions base, CategoryLabelPosition top) {
        ParamChecks.nullNotPermitted(base, "base");
        ParamChecks.nullNotPermitted(top, "top");
        return new CategoryLabelPositions(top, base.getLabelPosition(RectangleEdge.BOTTOM), base.getLabelPosition(RectangleEdge.LEFT), base.getLabelPosition(RectangleEdge.RIGHT));
    }

    public static CategoryLabelPositions replaceBottomPosition(CategoryLabelPositions base, CategoryLabelPosition bottom) {
        ParamChecks.nullNotPermitted(base, "base");
        ParamChecks.nullNotPermitted(bottom, "bottom");
        return new CategoryLabelPositions(base.getLabelPosition(RectangleEdge.TOP), bottom, base.getLabelPosition(RectangleEdge.LEFT), base.getLabelPosition(RectangleEdge.RIGHT));
    }

    public static CategoryLabelPositions replaceLeftPosition(CategoryLabelPositions base, CategoryLabelPosition left) {
        ParamChecks.nullNotPermitted(base, "base");
        ParamChecks.nullNotPermitted(left, "left");
        return new CategoryLabelPositions(base.getLabelPosition(RectangleEdge.TOP), base.getLabelPosition(RectangleEdge.BOTTOM), left, base.getLabelPosition(RectangleEdge.RIGHT));
    }

    public static CategoryLabelPositions replaceRightPosition(CategoryLabelPositions base, CategoryLabelPosition right) {
        ParamChecks.nullNotPermitted(base, "base");
        ParamChecks.nullNotPermitted(right, "right");
        return new CategoryLabelPositions(base.getLabelPosition(RectangleEdge.TOP), base.getLabelPosition(RectangleEdge.BOTTOM), base.getLabelPosition(RectangleEdge.LEFT), right);
    }

    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (!(obj instanceof CategoryLabelPositions)) {
            return false;
        }
        CategoryLabelPositions that = (CategoryLabelPositions) obj;
        if (!this.positionForAxisAtTop.equals(that.positionForAxisAtTop)) {
            return false;
        }
        if (!this.positionForAxisAtBottom.equals(that.positionForAxisAtBottom)) {
            return false;
        }
        if (!this.positionForAxisAtLeft.equals(that.positionForAxisAtLeft)) {
            return false;
        }
        if (this.positionForAxisAtRight.equals(that.positionForAxisAtRight)) {
            return true;
        }
        return false;
    }

    public int hashCode() {
        return ((((((this.positionForAxisAtTop.hashCode() + 703) * 37) + this.positionForAxisAtBottom.hashCode()) * 37) + this.positionForAxisAtLeft.hashCode()) * 37) + this.positionForAxisAtRight.hashCode();
    }
}
