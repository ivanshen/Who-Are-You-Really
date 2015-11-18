package org.jfree.text;

import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.Paint;
import java.awt.Shape;
import java.awt.geom.Rectangle2D.Double;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import org.jfree.chart.axis.Axis;
import org.jfree.ui.HorizontalAlignment;
import org.jfree.ui.Size2D;
import org.jfree.ui.TextAnchor;
import org.jfree.util.ShapeUtilities;

public class TextBlock implements Serializable {
    private static final long serialVersionUID = -4333175719424385526L;
    private HorizontalAlignment lineAlignment;
    private List lines;

    public TextBlock() {
        this.lines = new ArrayList();
        this.lineAlignment = HorizontalAlignment.CENTER;
    }

    public HorizontalAlignment getLineAlignment() {
        return this.lineAlignment;
    }

    public void setLineAlignment(HorizontalAlignment alignment) {
        if (alignment == null) {
            throw new IllegalArgumentException("Null 'alignment' argument.");
        }
        this.lineAlignment = alignment;
    }

    public void addLine(String text, Font font, Paint paint) {
        addLine(new TextLine(text, font, paint));
    }

    public void addLine(TextLine line) {
        this.lines.add(line);
    }

    public TextLine getLastLine() {
        int index = this.lines.size() - 1;
        if (index >= 0) {
            return (TextLine) this.lines.get(index);
        }
        return null;
    }

    public List getLines() {
        return Collections.unmodifiableList(this.lines);
    }

    public Size2D calculateDimensions(Graphics2D g2) {
        double width = 0.0d;
        double height = 0.0d;
        for (TextLine line : this.lines) {
            Size2D dimension = line.calculateDimensions(g2);
            width = Math.max(width, dimension.getWidth());
            height += dimension.getHeight();
        }
        return new Size2D(width, height);
    }

    public Shape calculateBounds(Graphics2D g2, float anchorX, float anchorY, TextBlockAnchor anchor, float rotateX, float rotateY, double angle) {
        Size2D d = calculateDimensions(g2);
        float[] offsets = calculateOffsets(anchor, d.getWidth(), d.getHeight());
        return ShapeUtilities.rotateShape(new Double((double) (offsets[0] + anchorX), (double) (offsets[1] + anchorY), d.getWidth(), d.getHeight()), angle, rotateX, rotateY);
    }

    public void draw(Graphics2D g2, float x, float y, TextBlockAnchor anchor) {
        draw(g2, x, y, anchor, 0.0f, 0.0f, 0.0d);
    }

    public void draw(Graphics2D g2, float anchorX, float anchorY, TextBlockAnchor anchor, float rotateX, float rotateY, double angle) {
        Size2D d = calculateDimensions(g2);
        float[] offsets = calculateOffsets(anchor, d.getWidth(), d.getHeight());
        float yCursor = 0.0f;
        for (TextLine line : this.lines) {
            Size2D dimension = line.calculateDimensions(g2);
            float lineOffset = 0.0f;
            if (this.lineAlignment == HorizontalAlignment.CENTER) {
                lineOffset = ((float) (d.getWidth() - dimension.getWidth())) / Axis.DEFAULT_TICK_MARK_OUTSIDE_LENGTH;
            } else if (this.lineAlignment == HorizontalAlignment.RIGHT) {
                lineOffset = (float) (d.getWidth() - dimension.getWidth());
            }
            line.draw(g2, (offsets[0] + anchorX) + lineOffset, (offsets[1] + anchorY) + yCursor, TextAnchor.TOP_LEFT, rotateX, rotateY, angle);
            yCursor += (float) dimension.getHeight();
        }
    }

    private float[] calculateOffsets(TextBlockAnchor anchor, double width, double height) {
        float[] result = new float[2];
        float xAdj = 0.0f;
        float yAdj = 0.0f;
        if (anchor == TextBlockAnchor.TOP_CENTER || anchor == TextBlockAnchor.CENTER || anchor == TextBlockAnchor.BOTTOM_CENTER) {
            xAdj = ((float) (-width)) / Axis.DEFAULT_TICK_MARK_OUTSIDE_LENGTH;
        } else if (anchor == TextBlockAnchor.TOP_RIGHT || anchor == TextBlockAnchor.CENTER_RIGHT || anchor == TextBlockAnchor.BOTTOM_RIGHT) {
            xAdj = (float) (-width);
        }
        if (anchor == TextBlockAnchor.TOP_LEFT || anchor == TextBlockAnchor.TOP_CENTER || anchor == TextBlockAnchor.TOP_RIGHT) {
            yAdj = 0.0f;
        } else if (anchor == TextBlockAnchor.CENTER_LEFT || anchor == TextBlockAnchor.CENTER || anchor == TextBlockAnchor.CENTER_RIGHT) {
            yAdj = ((float) (-height)) / Axis.DEFAULT_TICK_MARK_OUTSIDE_LENGTH;
        } else if (anchor == TextBlockAnchor.BOTTOM_LEFT || anchor == TextBlockAnchor.BOTTOM_CENTER || anchor == TextBlockAnchor.BOTTOM_RIGHT) {
            yAdj = (float) (-height);
        }
        result[0] = xAdj;
        result[1] = yAdj;
        return result;
    }

    public boolean equals(Object obj) {
        if (obj == this) {
            return true;
        }
        if (!(obj instanceof TextBlock)) {
            return false;
        }
        return this.lines.equals(((TextBlock) obj).lines);
    }

    public int hashCode() {
        return this.lines != null ? this.lines.hashCode() : 0;
    }
}
