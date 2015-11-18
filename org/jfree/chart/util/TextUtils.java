package org.jfree.chart.util;

import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics2D;
import java.awt.font.FontRenderContext;
import java.awt.font.LineMetrics;
import java.awt.geom.Rectangle2D;
import java.awt.geom.Rectangle2D.Double;
import org.jfree.chart.axis.Axis;
import org.jfree.chart.axis.DateAxis;
import org.jfree.ui.TextAnchor;

public class TextUtils {
    public static Rectangle2D drawAlignedString(String text, Graphics2D g2, float x, float y, TextAnchor anchor) {
        Rectangle2D textBounds = new Double();
        float[] adjust = deriveTextBoundsAnchorOffsets(g2, text, anchor, textBounds);
        textBounds.setRect((double) (adjust[0] + x), (double) ((adjust[1] + y) + adjust[2]), textBounds.getWidth(), textBounds.getHeight());
        g2.drawString(text, adjust[0] + x, adjust[1] + y);
        return textBounds;
    }

    public static Rectangle2D calcAlignedStringBounds(String text, Graphics2D g2, float x, float y, TextAnchor anchor) {
        Rectangle2D textBounds = new Double();
        float[] adjust = deriveTextBoundsAnchorOffsets(g2, text, anchor, textBounds);
        textBounds.setRect((double) (adjust[0] + x), (double) ((adjust[1] + y) + adjust[2]), textBounds.getWidth(), textBounds.getHeight());
        return textBounds;
    }

    private static float[] deriveTextBoundsAnchorOffsets(Graphics2D g2, String text, TextAnchor anchor) {
        float[] result = new float[2];
        FontRenderContext frc = g2.getFontRenderContext();
        Font f = g2.getFont();
        Rectangle2D bounds = getTextBounds(text, g2.getFontMetrics(f));
        LineMetrics metrics = f.getLineMetrics(text, frc);
        float halfAscent = metrics.getAscent() / Axis.DEFAULT_TICK_MARK_OUTSIDE_LENGTH;
        float descent = metrics.getDescent();
        float leading = metrics.getLeading();
        float xAdj = 0.0f;
        float yAdj = 0.0f;
        if (anchor.isHorizontalCenter()) {
            xAdj = ((float) (-bounds.getWidth())) / Axis.DEFAULT_TICK_MARK_OUTSIDE_LENGTH;
        } else if (anchor.isRight()) {
            xAdj = (float) (-bounds.getWidth());
        }
        if (anchor.isTop()) {
            yAdj = ((-descent) - leading) + ((float) bounds.getHeight());
        } else if (anchor.isHalfAscent()) {
            yAdj = halfAscent;
        } else if (anchor.isVerticalCenter()) {
            yAdj = ((-descent) - leading) + ((float) (bounds.getHeight() / DateAxis.DEFAULT_AUTO_RANGE_MINIMUM_SIZE_IN_MILLISECONDS));
        } else if (anchor.isBaseline()) {
            yAdj = 0.0f;
        } else if (anchor.isBottom()) {
            yAdj = (-metrics.getDescent()) - metrics.getLeading();
        }
        result[0] = xAdj;
        result[1] = yAdj;
        return result;
    }

    private static float[] deriveTextBoundsAnchorOffsets(Graphics2D g2, String text, TextAnchor anchor, Rectangle2D textBounds) {
        float[] result = new float[3];
        FontRenderContext frc = g2.getFontRenderContext();
        Font f = g2.getFont();
        Rectangle2D bounds = getTextBounds(text, g2.getFontMetrics(f));
        LineMetrics metrics = f.getLineMetrics(text, frc);
        float ascent = metrics.getAscent();
        result[2] = -ascent;
        float halfAscent = ascent / Axis.DEFAULT_TICK_MARK_OUTSIDE_LENGTH;
        float descent = metrics.getDescent();
        float leading = metrics.getLeading();
        float xAdj = 0.0f;
        float yAdj = 0.0f;
        if (anchor.isHorizontalCenter()) {
            xAdj = ((float) (-bounds.getWidth())) / Axis.DEFAULT_TICK_MARK_OUTSIDE_LENGTH;
        } else if (anchor.isRight()) {
            xAdj = (float) (-bounds.getWidth());
        }
        if (anchor.isTop()) {
            yAdj = ((-descent) - leading) + ((float) bounds.getHeight());
        } else if (anchor.isHalfAscent()) {
            yAdj = halfAscent;
        } else if (anchor.isHorizontalCenter()) {
            yAdj = ((-descent) - leading) + ((float) (bounds.getHeight() / DateAxis.DEFAULT_AUTO_RANGE_MINIMUM_SIZE_IN_MILLISECONDS));
        } else if (anchor.isBaseline()) {
            yAdj = 0.0f;
        } else if (anchor.isBottom()) {
            yAdj = (-metrics.getDescent()) - metrics.getLeading();
        }
        if (textBounds != null) {
            textBounds.setRect(bounds);
        }
        result[0] = xAdj;
        result[1] = yAdj;
        return result;
    }

    public static Rectangle2D getTextBounds(String text, FontMetrics fm) {
        return getTextBounds(text, 0.0d, 0.0d, fm);
    }

    public static Rectangle2D getTextBounds(String text, double x, double y, FontMetrics fm) {
        ParamChecks.nullNotPermitted(text, "text");
        ParamChecks.nullNotPermitted(fm, "fm");
        return new Double(x, y - ((double) fm.getAscent()), (double) fm.stringWidth(text), (double) fm.getHeight());
    }
}
