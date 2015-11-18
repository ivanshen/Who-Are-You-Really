package org.jfree.chart.labels;

import java.io.Serializable;
import org.jfree.chart.util.ParamChecks;
import org.jfree.ui.TextAnchor;

public class ItemLabelPosition implements Serializable {
    private static final long serialVersionUID = 5845390630157034499L;
    private double angle;
    private ItemLabelAnchor itemLabelAnchor;
    private TextAnchor rotationAnchor;
    private TextAnchor textAnchor;

    public ItemLabelPosition() {
        this(ItemLabelAnchor.OUTSIDE12, TextAnchor.BOTTOM_CENTER, TextAnchor.CENTER, 0.0d);
    }

    public ItemLabelPosition(ItemLabelAnchor itemLabelAnchor, TextAnchor textAnchor) {
        this(itemLabelAnchor, textAnchor, TextAnchor.CENTER, 0.0d);
    }

    public ItemLabelPosition(ItemLabelAnchor itemLabelAnchor, TextAnchor textAnchor, TextAnchor rotationAnchor, double angle) {
        ParamChecks.nullNotPermitted(itemLabelAnchor, "itemLabelAnchor");
        ParamChecks.nullNotPermitted(textAnchor, "textAnchor");
        ParamChecks.nullNotPermitted(rotationAnchor, "rotationAnchor");
        this.itemLabelAnchor = itemLabelAnchor;
        this.textAnchor = textAnchor;
        this.rotationAnchor = rotationAnchor;
        this.angle = angle;
    }

    public ItemLabelAnchor getItemLabelAnchor() {
        return this.itemLabelAnchor;
    }

    public TextAnchor getTextAnchor() {
        return this.textAnchor;
    }

    public TextAnchor getRotationAnchor() {
        return this.rotationAnchor;
    }

    public double getAngle() {
        return this.angle;
    }

    public boolean equals(Object obj) {
        if (obj == this) {
            return true;
        }
        if (!(obj instanceof ItemLabelPosition)) {
            return false;
        }
        ItemLabelPosition that = (ItemLabelPosition) obj;
        if (!this.itemLabelAnchor.equals(that.itemLabelAnchor)) {
            return false;
        }
        if (!this.textAnchor.equals(that.textAnchor)) {
            return false;
        }
        if (!this.rotationAnchor.equals(that.rotationAnchor)) {
            return false;
        }
        if (this.angle != that.angle) {
            return false;
        }
        return true;
    }
}
