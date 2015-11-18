package org.jfree.ui;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Insets;
import java.awt.Paint;
import java.awt.geom.Rectangle2D;
import java.awt.geom.Rectangle2D.Double;
import javax.swing.JComponent;
import org.jfree.data.xy.NormalizedMatrixSeries;

public class PaintSample extends JComponent {
    private Paint paint;
    private Dimension preferredSize;

    public PaintSample(Paint paint) {
        this.paint = paint;
        this.preferredSize = new Dimension(80, 12);
    }

    public Paint getPaint() {
        return this.paint;
    }

    public void setPaint(Paint paint) {
        this.paint = paint;
        repaint();
    }

    public Dimension getPreferredSize() {
        return this.preferredSize;
    }

    public void paintComponent(Graphics g) {
        Graphics2D g2 = (Graphics2D) g;
        Dimension size = getSize();
        Insets insets = getInsets();
        double d = (double) insets.left;
        d = (double) insets.right;
        d = (double) insets.top;
        Rectangle2D area = new Double((double) insets.left, (double) insets.top, ((size.getWidth() - r0) - r0) - NormalizedMatrixSeries.DEFAULT_SCALE_FACTOR, ((size.getHeight() - r0) - ((double) insets.bottom)) - NormalizedMatrixSeries.DEFAULT_SCALE_FACTOR);
        g2.setPaint(this.paint);
        g2.fill(area);
        g2.setPaint(Color.black);
        g2.draw(area);
    }
}
