package org.jfree.ui;

import java.awt.geom.Dimension2D;
import java.io.Serializable;

public class FloatDimension extends Dimension2D implements Serializable {
    private static final long serialVersionUID = 5367882923248086744L;
    private float height;
    private float width;

    public FloatDimension() {
        this.width = 0.0f;
        this.height = 0.0f;
    }

    public FloatDimension(FloatDimension fd) {
        this.width = fd.width;
        this.height = fd.height;
    }

    public FloatDimension(float width, float height) {
        this.width = width;
        this.height = height;
    }

    public double getWidth() {
        return (double) this.width;
    }

    public double getHeight() {
        return (double) this.height;
    }

    public void setWidth(double width) {
        this.width = (float) width;
    }

    public void setHeight(double height) {
        this.height = (float) height;
    }

    public void setSize(double width, double height) {
        setHeight((double) ((float) height));
        setWidth((double) ((float) width));
    }

    public Object clone() {
        return super.clone();
    }

    public String toString() {
        return getClass().getName() + ":={width=" + getWidth() + ", height=" + getHeight() + "}";
    }

    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof FloatDimension)) {
            return false;
        }
        FloatDimension floatDimension = (FloatDimension) o;
        if (this.height != floatDimension.height) {
            return false;
        }
        if (this.width != floatDimension.width) {
            return false;
        }
        return true;
    }

    public int hashCode() {
        return (Float.floatToIntBits(this.width) * 29) + Float.floatToIntBits(this.height);
    }
}
