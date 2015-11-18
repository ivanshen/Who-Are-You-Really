package org.jfree.text;

import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.Paint;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import org.jfree.chart.axis.Axis;
import org.jfree.ui.Size2D;
import org.jfree.ui.TextAnchor;

public class TextLine implements Serializable {
    private static final long serialVersionUID = 7100085690160465444L;
    private List fragments;

    public TextLine() {
        this.fragments = new ArrayList();
    }

    public TextLine(String text) {
        this(text, TextFragment.DEFAULT_FONT);
    }

    public TextLine(String text, Font font) {
        this.fragments = new ArrayList();
        this.fragments.add(new TextFragment(text, font));
    }

    public TextLine(String text, Font font, Paint paint) {
        if (text == null) {
            throw new IllegalArgumentException("Null 'text' argument.");
        } else if (font == null) {
            throw new IllegalArgumentException("Null 'font' argument.");
        } else if (paint == null) {
            throw new IllegalArgumentException("Null 'paint' argument.");
        } else {
            this.fragments = new ArrayList();
            this.fragments.add(new TextFragment(text, font, paint));
        }
    }

    public void addFragment(TextFragment fragment) {
        this.fragments.add(fragment);
    }

    public void removeFragment(TextFragment fragment) {
        this.fragments.remove(fragment);
    }

    public void draw(Graphics2D g2, float anchorX, float anchorY, TextAnchor anchor, float rotateX, float rotateY, double angle) {
        Size2D dim = calculateDimensions(g2);
        float xAdj = 0.0f;
        if (anchor.isHorizontalCenter()) {
            xAdj = ((float) (-dim.getWidth())) / Axis.DEFAULT_TICK_MARK_OUTSIDE_LENGTH;
        } else if (anchor.isRight()) {
            xAdj = (float) (-dim.getWidth());
        }
        float x = anchorX + xAdj;
        float yOffset = calculateBaselineOffset(g2, anchor);
        for (TextFragment fragment : this.fragments) {
            Size2D d = fragment.calculateDimensions(g2);
            fragment.draw(g2, x, anchorY + yOffset, TextAnchor.BASELINE_LEFT, rotateX, rotateY, angle);
            x += (float) d.getWidth();
        }
    }

    public Size2D calculateDimensions(Graphics2D g2) {
        double width = 0.0d;
        double height = 0.0d;
        for (TextFragment fragment : this.fragments) {
            Size2D dimension = fragment.calculateDimensions(g2);
            width += dimension.getWidth();
            height = Math.max(height, dimension.getHeight());
        }
        return new Size2D(width, height);
    }

    public TextFragment getFirstTextFragment() {
        if (this.fragments.size() > 0) {
            return (TextFragment) this.fragments.get(0);
        }
        return null;
    }

    public TextFragment getLastTextFragment() {
        if (this.fragments.size() > 0) {
            return (TextFragment) this.fragments.get(this.fragments.size() - 1);
        }
        return null;
    }

    private float calculateBaselineOffset(Graphics2D g2, TextAnchor anchor) {
        float result = 0.0f;
        for (TextFragment fragment : this.fragments) {
            result = Math.max(result, fragment.calculateBaselineOffset(g2, anchor));
        }
        return result;
    }

    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (obj == this) {
            return true;
        }
        if (!(obj instanceof TextLine)) {
            return false;
        }
        return this.fragments.equals(((TextLine) obj).fragments);
    }

    public int hashCode() {
        return this.fragments != null ? this.fragments.hashCode() : 0;
    }
}
