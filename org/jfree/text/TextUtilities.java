package org.jfree.text;

import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics2D;
import java.awt.Paint;
import java.awt.Shape;
import java.awt.font.FontRenderContext;
import java.awt.font.LineMetrics;
import java.awt.font.TextLayout;
import java.awt.geom.AffineTransform;
import java.awt.geom.Rectangle2D;
import java.awt.geom.Rectangle2D.Double;
import java.text.AttributedString;
import java.text.BreakIterator;
import org.jfree.base.BaseBoot;
import org.jfree.chart.axis.Axis;
import org.jfree.chart.axis.DateAxis;
import org.jfree.ui.TextAnchor;
import org.jfree.util.Log;
import org.jfree.util.LogContext;
import org.jfree.util.ObjectUtilities;

public class TextUtilities {
    private static boolean drawStringsWithFontAttributes;
    protected static final LogContext logger;
    private static boolean useDrawRotatedStringWorkaround;
    private static boolean useFontMetricsGetStringBounds;

    static {
        boolean z = false;
        logger = Log.createContext(TextUtilities.class);
        drawStringsWithFontAttributes = false;
        try {
            boolean isJava14 = ObjectUtilities.isJDK14();
            String configRotatedStringWorkaround = BaseBoot.getInstance().getGlobalConfig().getConfigProperty("org.jfree.text.UseDrawRotatedStringWorkaround", "auto");
            if (configRotatedStringWorkaround.equals("auto")) {
                if (!isJava14) {
                    z = true;
                }
                useDrawRotatedStringWorkaround = z;
            } else {
                useDrawRotatedStringWorkaround = configRotatedStringWorkaround.equals("true");
            }
            String configFontMetricsStringBounds = BaseBoot.getInstance().getGlobalConfig().getConfigProperty("org.jfree.text.UseFontMetricsGetStringBounds", "auto");
            if (configFontMetricsStringBounds.equals("auto")) {
                useFontMetricsGetStringBounds = isJava14;
            } else {
                useFontMetricsGetStringBounds = configFontMetricsStringBounds.equals("true");
            }
        } catch (Exception e) {
            useDrawRotatedStringWorkaround = true;
            useFontMetricsGetStringBounds = true;
        }
    }

    private TextUtilities() {
    }

    public static TextBlock createTextBlock(String text, Font font, Paint paint) {
        if (text == null) {
            throw new IllegalArgumentException("Null 'text' argument.");
        }
        TextBlock result = new TextBlock();
        String input = text;
        boolean moreInputToProcess = text.length() > 0;
        while (moreInputToProcess) {
            int index = input.indexOf("\n");
            if (index > 0) {
                String line = input.substring(0, index);
                if (index < input.length() - 1) {
                    result.addLine(line, font, paint);
                    input = input.substring(index + 1);
                } else {
                    moreInputToProcess = false;
                }
            } else if (index != 0) {
                result.addLine(input, font, paint);
                moreInputToProcess = false;
            } else if (index < input.length() - 1) {
                input = input.substring(index + 1);
            } else {
                moreInputToProcess = false;
            }
        }
        return result;
    }

    public static TextBlock createTextBlock(String text, Font font, Paint paint, float maxWidth, TextMeasurer measurer) {
        return createTextBlock(text, font, paint, maxWidth, Integer.MAX_VALUE, measurer);
    }

    public static TextBlock createTextBlock(String text, Font font, Paint paint, float maxWidth, int maxLines, TextMeasurer measurer) {
        TextBlock result = new TextBlock();
        BreakIterator iterator = BreakIterator.getLineInstance();
        iterator.setText(text);
        int current = 0;
        int lines = 0;
        int length = text.length();
        while (current < length && lines < maxLines) {
            int next = nextLineBreak(text, current, maxWidth, iterator, measurer);
            if (next == -1) {
                result.addLine(text.substring(current), font, paint);
                break;
            }
            if (next == current) {
                next++;
            }
            result.addLine(text.substring(current, next), font, paint);
            lines++;
            current = next;
            while (current < text.length() && text.charAt(current) == '\n') {
                current++;
            }
        }
        if (current < length) {
            TextLine lastLine = result.getLastLine();
            TextFragment lastFragment = lastLine.getLastTextFragment();
            String oldStr = lastFragment.getText();
            String newStr = "...";
            if (oldStr.length() > 3) {
                newStr = oldStr.substring(0, oldStr.length() - 3) + "...";
            }
            lastLine.removeFragment(lastFragment);
            lastLine.addFragment(new TextFragment(newStr, lastFragment.getFont(), lastFragment.getPaint()));
        }
        return result;
    }

    private static int nextLineBreak(String text, int start, float width, BreakIterator iterator, TextMeasurer measurer) {
        int current = start;
        float x = 0.0f;
        boolean firstWord = true;
        int newline = text.indexOf(10, start);
        if (newline < 0) {
            newline = Integer.MAX_VALUE;
        }
        while (true) {
            int end = iterator.following(current);
            if (end == -1) {
                return -1;
            }
            x += measurer.getStringWidth(text, current, end);
            if (x > width) {
                break;
            } else if (end > newline) {
                return newline;
            } else {
                firstWord = false;
                current = end;
            }
        }
        if (!firstWord) {
            return iterator.previous();
        }
        while (measurer.getStringWidth(text, start, end) > width) {
            end--;
            if (end <= start) {
                return end;
            }
        }
        return end;
    }

    public static Rectangle2D getTextBounds(String text, Graphics2D g2, FontMetrics fm) {
        if (useFontMetricsGetStringBounds) {
            Rectangle2D bounds = fm.getStringBounds(text, g2);
            bounds.setRect(bounds.getX(), bounds.getY(), bounds.getWidth(), (double) fm.getFont().getLineMetrics(text, g2.getFontRenderContext()).getHeight());
            return bounds;
        }
        double width = (double) fm.stringWidth(text);
        double height = (double) fm.getHeight();
        if (logger.isDebugEnabled()) {
            logger.debug("Height = " + height);
        }
        return new Double(0.0d, (double) (-fm.getAscent()), width, height);
    }

    public static Rectangle2D drawAlignedString(String text, Graphics2D g2, float x, float y, TextAnchor anchor) {
        Rectangle2D textBounds = new Double();
        float[] adjust = deriveTextBoundsAnchorOffsets(g2, text, anchor, textBounds);
        textBounds.setRect((double) (adjust[0] + x), (double) ((adjust[1] + y) + adjust[2]), textBounds.getWidth(), textBounds.getHeight());
        if (drawStringsWithFontAttributes) {
            g2.drawString(new AttributedString(text, g2.getFont().getAttributes()).getIterator(), adjust[0] + x, adjust[1] + y);
        } else {
            g2.drawString(text, adjust[0] + x, adjust[1] + y);
        }
        return textBounds;
    }

    private static float[] deriveTextBoundsAnchorOffsets(Graphics2D g2, String text, TextAnchor anchor, Rectangle2D textBounds) {
        float[] result = new float[3];
        FontRenderContext frc = g2.getFontRenderContext();
        Font f = g2.getFont();
        Rectangle2D bounds = getTextBounds(text, g2, g2.getFontMetrics(f));
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
        } else if (anchor.isVerticalCenter()) {
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

    public static void drawRotatedString(String text, Graphics2D g2, double angle, float x, float y) {
        drawRotatedString(text, g2, x, y, angle, x, y);
    }

    public static void drawRotatedString(String text, Graphics2D g2, float textX, float textY, double angle, float rotateX, float rotateY) {
        if (text != null && !text.equals("")) {
            if (angle == 0.0d) {
                drawAlignedString(text, g2, textY, textY, TextAnchor.BASELINE_LEFT);
                return;
            }
            AffineTransform saved = g2.getTransform();
            g2.transform(AffineTransform.getRotateInstance(angle, (double) rotateX, (double) rotateY));
            if (useDrawRotatedStringWorkaround) {
                new TextLayout(text, g2.getFont(), g2.getFontRenderContext()).draw(g2, textX, textY);
            } else if (drawStringsWithFontAttributes) {
                g2.drawString(new AttributedString(text, g2.getFont().getAttributes()).getIterator(), textX, textY);
            } else {
                g2.drawString(text, textX, textY);
            }
            g2.setTransform(saved);
        }
    }

    public static void drawRotatedString(String text, Graphics2D g2, float x, float y, TextAnchor textAnchor, double angle, float rotationX, float rotationY) {
        if (text != null && !text.equals("")) {
            if (angle == 0.0d) {
                drawAlignedString(text, g2, x, y, textAnchor);
                return;
            }
            float[] textAdj = deriveTextBoundsAnchorOffsets(g2, text, textAnchor);
            drawRotatedString(text, g2, x + textAdj[0], y + textAdj[1], angle, rotationX, rotationY);
        }
    }

    public static void drawRotatedString(String text, Graphics2D g2, float x, float y, TextAnchor textAnchor, double angle, TextAnchor rotationAnchor) {
        if (text != null && !text.equals("")) {
            if (angle == 0.0d) {
                drawAlignedString(text, g2, x, y, textAnchor);
                return;
            }
            float[] textAdj = deriveTextBoundsAnchorOffsets(g2, text, textAnchor);
            float[] rotateAdj = deriveRotationAnchorOffsets(g2, text, rotationAnchor);
            drawRotatedString(text, g2, x + textAdj[0], y + textAdj[1], angle, (textAdj[0] + x) + rotateAdj[0], (textAdj[1] + y) + rotateAdj[1]);
        }
    }

    public static Shape calculateRotatedStringBounds(String text, Graphics2D g2, float x, float y, TextAnchor textAnchor, double angle, TextAnchor rotationAnchor) {
        if (text == null || text.equals("")) {
            return null;
        }
        float[] textAdj = deriveTextBoundsAnchorOffsets(g2, text, textAnchor);
        if (logger.isDebugEnabled()) {
            logger.debug("TextBoundsAnchorOffsets = " + textAdj[0] + ", " + textAdj[1]);
        }
        float[] rotateAdj = deriveRotationAnchorOffsets(g2, text, rotationAnchor);
        if (logger.isDebugEnabled()) {
            logger.debug("RotationAnchorOffsets = " + rotateAdj[0] + ", " + rotateAdj[1]);
        }
        return calculateRotatedStringBounds(text, g2, x + textAdj[0], y + textAdj[1], angle, (textAdj[0] + x) + rotateAdj[0], (textAdj[1] + y) + rotateAdj[1]);
    }

    private static float[] deriveTextBoundsAnchorOffsets(Graphics2D g2, String text, TextAnchor anchor) {
        float[] result = new float[2];
        FontRenderContext frc = g2.getFontRenderContext();
        Font f = g2.getFont();
        Rectangle2D bounds = getTextBounds(text, g2, g2.getFontMetrics(f));
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

    private static float[] deriveRotationAnchorOffsets(Graphics2D g2, String text, TextAnchor anchor) {
        float[] result = new float[2];
        LineMetrics metrics = g2.getFont().getLineMetrics(text, g2.getFontRenderContext());
        Rectangle2D bounds = getTextBounds(text, g2, g2.getFontMetrics());
        float halfAscent = metrics.getAscent() / Axis.DEFAULT_TICK_MARK_OUTSIDE_LENGTH;
        float descent = metrics.getDescent();
        float leading = metrics.getLeading();
        float xAdj = 0.0f;
        float yAdj = 0.0f;
        if (anchor.isLeft()) {
            xAdj = 0.0f;
        } else if (anchor.isHorizontalCenter()) {
            xAdj = ((float) bounds.getWidth()) / Axis.DEFAULT_TICK_MARK_OUTSIDE_LENGTH;
        } else if (anchor.isRight()) {
            xAdj = (float) bounds.getWidth();
        }
        if (anchor.isTop()) {
            yAdj = (descent + leading) - ((float) bounds.getHeight());
        } else if (anchor.isVerticalCenter()) {
            yAdj = (descent + leading) - ((float) (bounds.getHeight() / DateAxis.DEFAULT_AUTO_RANGE_MINIMUM_SIZE_IN_MILLISECONDS));
        } else if (anchor.isHalfAscent()) {
            yAdj = -halfAscent;
        } else if (anchor.isBaseline()) {
            yAdj = 0.0f;
        } else if (anchor.isBottom()) {
            yAdj = metrics.getDescent() + metrics.getLeading();
        }
        result[0] = xAdj;
        result[1] = yAdj;
        return result;
    }

    public static Shape calculateRotatedStringBounds(String text, Graphics2D g2, float textX, float textY, double angle, float rotateX, float rotateY) {
        if (text == null || text.equals("")) {
            return null;
        }
        return AffineTransform.getRotateInstance(angle, (double) rotateX, (double) rotateY).createTransformedShape(AffineTransform.getTranslateInstance((double) textX, (double) textY).createTransformedShape(getTextBounds(text, g2, g2.getFontMetrics())));
    }

    public static boolean getUseFontMetricsGetStringBounds() {
        return useFontMetricsGetStringBounds;
    }

    public static void setUseFontMetricsGetStringBounds(boolean use) {
        useFontMetricsGetStringBounds = use;
    }

    public static boolean isUseDrawRotatedStringWorkaround() {
        return useDrawRotatedStringWorkaround;
    }

    public static void setUseDrawRotatedStringWorkaround(boolean use) {
        useDrawRotatedStringWorkaround = use;
    }

    public static boolean getDrawStringsWithFontAttributes() {
        return drawStringsWithFontAttributes;
    }

    public static void setDrawStringsWithFontAttributes(boolean b) {
        drawStringsWithFontAttributes = b;
    }
}
