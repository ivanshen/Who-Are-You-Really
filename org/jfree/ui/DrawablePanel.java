package org.jfree.ui;

import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.geom.Rectangle2D.Double;
import javax.swing.JPanel;

public class DrawablePanel extends JPanel {
    private Drawable drawable;

    public DrawablePanel() {
        setOpaque(false);
    }

    public Drawable getDrawable() {
        return this.drawable;
    }

    public void setDrawable(Drawable drawable) {
        this.drawable = drawable;
        revalidate();
        repaint();
    }

    public Dimension getPreferredSize() {
        if (this.drawable instanceof ExtendedDrawable) {
            return this.drawable.getPreferredSize();
        }
        return super.getPreferredSize();
    }

    public Dimension getMinimumSize() {
        if (this.drawable instanceof ExtendedDrawable) {
            return this.drawable.getPreferredSize();
        }
        return super.getMinimumSize();
    }

    public boolean isOpaque() {
        if (this.drawable == null) {
            return false;
        }
        return super.isOpaque();
    }

    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        if (this.drawable != null) {
            Graphics2D g2 = (Graphics2D) g.create(0, 0, getWidth(), getHeight());
            this.drawable.draw(g2, new Double(0.0d, 0.0d, (double) getWidth(), (double) getHeight()));
            g2.dispose();
        }
    }
}
