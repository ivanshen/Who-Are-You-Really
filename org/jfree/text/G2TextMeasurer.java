package org.jfree.text;

import java.awt.Graphics2D;

public class G2TextMeasurer implements TextMeasurer {
    private Graphics2D g2;

    public G2TextMeasurer(Graphics2D g2) {
        this.g2 = g2;
    }

    public float getStringWidth(String text, int start, int end) {
        return (float) TextUtilities.getTextBounds(text.substring(start, end), this.g2, this.g2.getFontMetrics()).getWidth();
    }
}
