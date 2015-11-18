package org.jfree.chart.util;

import java.awt.GradientPaint;
import java.awt.Shape;
import java.awt.geom.Rectangle2D;
import org.jfree.chart.axis.Axis;
import org.jfree.ui.GradientPaintTransformer;

public class DirectionalGradientPaintTransformer implements GradientPaintTransformer {
    public GradientPaint transform(GradientPaint paint, Shape target) {
        float rx1;
        float ry1;
        float rx2;
        float ry2;
        double px1 = paint.getPoint1().getX();
        double py1 = paint.getPoint1().getY();
        double px2 = paint.getPoint2().getX();
        double py2 = paint.getPoint2().getY();
        Rectangle2D bounds = target.getBounds();
        float bx = (float) bounds.getX();
        float by = (float) bounds.getY();
        float bw = (float) bounds.getWidth();
        float bh = (float) bounds.getHeight();
        float offset;
        if (px1 == 0.0d && py1 == 0.0d) {
            rx1 = bx;
            ry1 = by;
            if (px2 == 0.0d || py2 == 0.0d) {
                rx2 = px2 == 0.0d ? rx1 : paint.isCyclic() ? rx1 + (bw / Axis.DEFAULT_TICK_MARK_OUTSIDE_LENGTH) : rx1 + bw;
                ry2 = py2 == 0.0d ? ry1 : paint.isCyclic() ? ry1 + (bh / Axis.DEFAULT_TICK_MARK_OUTSIDE_LENGTH) : ry1 + bh;
            } else {
                offset = paint.isCyclic() ? (bw + bh) / 4.0f : (bw + bh) / Axis.DEFAULT_TICK_MARK_OUTSIDE_LENGTH;
                rx2 = bx + offset;
                ry2 = by + offset;
            }
        } else {
            rx1 = bx;
            ry1 = by + bh;
            offset = paint.isCyclic() ? (bw + bh) / 4.0f : (bw + bh) / Axis.DEFAULT_TICK_MARK_OUTSIDE_LENGTH;
            rx2 = bx + offset;
            ry2 = (by + bh) - offset;
        }
        return new GradientPaint(rx1, ry1, paint.getColor1(), rx2, ry2, paint.getColor2(), paint.isCyclic());
    }
}
