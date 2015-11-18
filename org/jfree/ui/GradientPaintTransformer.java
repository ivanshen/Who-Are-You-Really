package org.jfree.ui;

import java.awt.GradientPaint;
import java.awt.Shape;

public interface GradientPaintTransformer {
    GradientPaint transform(GradientPaint gradientPaint, Shape shape);
}
