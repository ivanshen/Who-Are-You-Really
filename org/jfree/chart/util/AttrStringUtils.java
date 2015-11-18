package org.jfree.chart.util;

import java.awt.Graphics2D;
import java.awt.font.TextLayout;
import java.awt.geom.AffineTransform;
import java.awt.geom.Rectangle2D;
import java.text.AttributedString;
import org.jfree.chart.axis.Axis;
import org.jfree.chart.axis.DateAxis;
import org.jfree.ui.TextAnchor;

public class AttrStringUtils {
    private AttrStringUtils() {
    }

    public static Rectangle2D getTextBounds(AttributedString text, Graphics2D g2) {
        return new TextLayout(text.getIterator(), g2.getFontRenderContext()).getBounds();
    }

    public static void drawRotatedString(AttributedString text, Graphics2D g2, double angle, float x, float y) {
        drawRotatedString(text, g2, x, y, angle, x, y);
    }

    public static void drawRotatedString(AttributedString text, Graphics2D g2, float textX, float textY, double angle, float rotateX, float rotateY) {
        ParamChecks.nullNotPermitted(text, "text");
        AffineTransform saved = g2.getTransform();
        g2.transform(AffineTransform.getRotateInstance(angle, (double) rotateX, (double) rotateY));
        new TextLayout(text.getIterator(), g2.getFontRenderContext()).draw(g2, textX, textY);
        g2.setTransform(saved);
    }

    public static void drawRotatedString(AttributedString text, Graphics2D g2, float x, float y, TextAnchor textAnchor, double angle, float rotationX, float rotationY) {
        ParamChecks.nullNotPermitted(text, "text");
        float[] textAdj = deriveTextBoundsAnchorOffsets(g2, text, textAnchor, null);
        drawRotatedString(text, g2, x + textAdj[0], y + textAdj[1], angle, rotationX, rotationY);
    }

    public static void drawRotatedString(AttributedString text, Graphics2D g2, float x, float y, TextAnchor textAnchor, double angle, TextAnchor rotationAnchor) {
        ParamChecks.nullNotPermitted(text, "text");
        float[] textAdj = deriveTextBoundsAnchorOffsets(g2, text, textAnchor, null);
        float[] rotateAdj = deriveRotationAnchorOffsets(g2, text, rotationAnchor);
        drawRotatedString(text, g2, x + textAdj[0], y + textAdj[1], angle, (textAdj[0] + x) + rotateAdj[0], (textAdj[1] + y) + rotateAdj[1]);
    }

    private static float[] deriveTextBoundsAnchorOffsets(Graphics2D g2, AttributedString text, TextAnchor anchor, Rectangle2D textBounds) {
        TextLayout layout = new TextLayout(text.getIterator(), g2.getFontRenderContext());
        Rectangle2D bounds = layout.getBounds();
        float[] result = new float[3];
        float ascent = layout.getAscent();
        result[2] = -ascent;
        float halfAscent = ascent / Axis.DEFAULT_TICK_MARK_OUTSIDE_LENGTH;
        float descent = layout.getDescent();
        float leading = layout.getLeading();
        float xAdj = 0.0f;
        float yAdj = 0.0f;
        if (isHorizontalCenter(anchor)) {
            xAdj = ((float) (-bounds.getWidth())) / Axis.DEFAULT_TICK_MARK_OUTSIDE_LENGTH;
        } else if (isHorizontalRight(anchor)) {
            xAdj = (float) (-bounds.getWidth());
        }
        if (isTop(anchor)) {
            yAdj = (float) bounds.getHeight();
        } else if (isHalfAscent(anchor)) {
            yAdj = halfAscent;
        } else if (isHalfHeight(anchor)) {
            yAdj = ((-descent) - leading) + ((float) (bounds.getHeight() / DateAxis.DEFAULT_AUTO_RANGE_MINIMUM_SIZE_IN_MILLISECONDS));
        } else if (isBaseline(anchor)) {
            yAdj = 0.0f;
        } else if (isBottom(anchor)) {
            yAdj = (-descent) - leading;
        }
        if (textBounds != null) {
            textBounds.setRect(bounds);
        }
        result[0] = xAdj;
        result[1] = yAdj;
        return result;
    }

    private static float[] deriveRotationAnchorOffsets(Graphics2D g2, AttributedString text, TextAnchor anchor) {
        float[] result = new float[2];
        TextLayout layout = new TextLayout(text.getIterator(), g2.getFontRenderContext());
        Rectangle2D bounds = layout.getBounds();
        float halfAscent = layout.getAscent() / Axis.DEFAULT_TICK_MARK_OUTSIDE_LENGTH;
        float descent = layout.getDescent();
        float leading = layout.getLeading();
        float xAdj = 0.0f;
        float yAdj = 0.0f;
        if (isHorizontalLeft(anchor)) {
            xAdj = 0.0f;
        } else if (isHorizontalCenter(anchor)) {
            xAdj = ((float) bounds.getWidth()) / Axis.DEFAULT_TICK_MARK_OUTSIDE_LENGTH;
        } else if (isHorizontalRight(anchor)) {
            xAdj = (float) bounds.getWidth();
        }
        if (isTop(anchor)) {
            yAdj = (descent + leading) - ((float) bounds.getHeight());
        } else if (isHalfHeight(anchor)) {
            yAdj = (descent + leading) - ((float) (bounds.getHeight() / DateAxis.DEFAULT_AUTO_RANGE_MINIMUM_SIZE_IN_MILLISECONDS));
        } else if (isHalfAscent(anchor)) {
            yAdj = -halfAscent;
        } else if (isBaseline(anchor)) {
            yAdj = 0.0f;
        } else if (isBottom(anchor)) {
            yAdj = descent + leading;
        }
        result[0] = xAdj;
        result[1] = yAdj;
        return result;
    }

    private static boolean isTop(TextAnchor anchor) {
        return anchor.equals(TextAnchor.TOP_LEFT) || anchor.equals(TextAnchor.TOP_CENTER) || anchor.equals(TextAnchor.TOP_RIGHT);
    }

    private static boolean isBaseline(TextAnchor anchor) {
        return anchor.equals(TextAnchor.BASELINE_LEFT) || anchor.equals(TextAnchor.BASELINE_CENTER) || anchor.equals(TextAnchor.BASELINE_RIGHT);
    }

    private static boolean isHalfAscent(TextAnchor anchor) {
        return anchor.equals(TextAnchor.HALF_ASCENT_LEFT) || anchor.equals(TextAnchor.HALF_ASCENT_CENTER) || anchor.equals(TextAnchor.HALF_ASCENT_RIGHT);
    }

    private static boolean isHalfHeight(TextAnchor anchor) {
        return anchor.equals(TextAnchor.CENTER_LEFT) || anchor.equals(TextAnchor.CENTER) || anchor.equals(TextAnchor.CENTER_RIGHT);
    }

    private static boolean isBottom(TextAnchor anchor) {
        return anchor.equals(TextAnchor.BOTTOM_LEFT) || anchor.equals(TextAnchor.BOTTOM_CENTER) || anchor.equals(TextAnchor.BOTTOM_RIGHT);
    }

    private static boolean isHorizontalLeft(TextAnchor anchor) {
        return anchor.equals(TextAnchor.TOP_LEFT) || anchor.equals(TextAnchor.CENTER_LEFT) || anchor.equals(TextAnchor.HALF_ASCENT_LEFT) || anchor.equals(TextAnchor.BASELINE_LEFT) || anchor.equals(TextAnchor.BOTTOM_LEFT);
    }

    private static boolean isHorizontalCenter(TextAnchor anchor) {
        return anchor.equals(TextAnchor.TOP_CENTER) || anchor.equals(TextAnchor.CENTER) || anchor.equals(TextAnchor.HALF_ASCENT_CENTER) || anchor.equals(TextAnchor.BASELINE_CENTER) || anchor.equals(TextAnchor.BOTTOM_CENTER);
    }

    private static boolean isHorizontalRight(TextAnchor anchor) {
        return anchor.equals(TextAnchor.TOP_RIGHT) || anchor.equals(TextAnchor.CENTER_RIGHT) || anchor.equals(TextAnchor.HALF_ASCENT_RIGHT) || anchor.equals(TextAnchor.BASELINE_RIGHT) || anchor.equals(TextAnchor.BOTTOM_RIGHT);
    }
}
