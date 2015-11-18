package org.jfree.chart.axis;

import java.io.Serializable;
import org.jfree.chart.util.ParamChecks;
import org.jfree.text.TextBlockAnchor;
import org.jfree.ui.RectangleAnchor;
import org.jfree.ui.TextAnchor;

public class CategoryLabelPosition implements Serializable {
    private static final long serialVersionUID = 5168681143844183864L;
    private double angle;
    private RectangleAnchor categoryAnchor;
    private TextBlockAnchor labelAnchor;
    private TextAnchor rotationAnchor;
    private float widthRatio;
    private CategoryLabelWidthType widthType;

    public CategoryLabelPosition() {
        this(RectangleAnchor.CENTER, TextBlockAnchor.BOTTOM_CENTER, TextAnchor.CENTER, 0.0d, CategoryLabelWidthType.CATEGORY, 0.95f);
    }

    public CategoryLabelPosition(RectangleAnchor categoryAnchor, TextBlockAnchor labelAnchor) {
        this(categoryAnchor, labelAnchor, TextAnchor.CENTER, 0.0d, CategoryLabelWidthType.CATEGORY, 0.95f);
    }

    public CategoryLabelPosition(RectangleAnchor categoryAnchor, TextBlockAnchor labelAnchor, CategoryLabelWidthType widthType, float widthRatio) {
        this(categoryAnchor, labelAnchor, TextAnchor.CENTER, 0.0d, widthType, widthRatio);
    }

    public CategoryLabelPosition(RectangleAnchor categoryAnchor, TextBlockAnchor labelAnchor, TextAnchor rotationAnchor, double angle, CategoryLabelWidthType widthType, float widthRatio) {
        ParamChecks.nullNotPermitted(categoryAnchor, "categoryAnchor");
        ParamChecks.nullNotPermitted(labelAnchor, "labelAnchor");
        ParamChecks.nullNotPermitted(rotationAnchor, "rotationAnchor");
        ParamChecks.nullNotPermitted(widthType, "widthType");
        this.categoryAnchor = categoryAnchor;
        this.labelAnchor = labelAnchor;
        this.rotationAnchor = rotationAnchor;
        this.angle = angle;
        this.widthType = widthType;
        this.widthRatio = widthRatio;
    }

    public RectangleAnchor getCategoryAnchor() {
        return this.categoryAnchor;
    }

    public TextBlockAnchor getLabelAnchor() {
        return this.labelAnchor;
    }

    public TextAnchor getRotationAnchor() {
        return this.rotationAnchor;
    }

    public double getAngle() {
        return this.angle;
    }

    public CategoryLabelWidthType getWidthType() {
        return this.widthType;
    }

    public float getWidthRatio() {
        return this.widthRatio;
    }

    public boolean equals(Object obj) {
        if (obj == this) {
            return true;
        }
        if (!(obj instanceof CategoryLabelPosition)) {
            return false;
        }
        CategoryLabelPosition that = (CategoryLabelPosition) obj;
        if (!this.categoryAnchor.equals(that.categoryAnchor)) {
            return false;
        }
        if (!this.labelAnchor.equals(that.labelAnchor)) {
            return false;
        }
        if (!this.rotationAnchor.equals(that.rotationAnchor)) {
            return false;
        }
        if (this.angle != that.angle) {
            return false;
        }
        if (this.widthType != that.widthType) {
            return false;
        }
        if (this.widthRatio != that.widthRatio) {
            return false;
        }
        return true;
    }

    public int hashCode() {
        return ((((this.categoryAnchor.hashCode() + 703) * 37) + this.labelAnchor.hashCode()) * 37) + this.rotationAnchor.hashCode();
    }
}
