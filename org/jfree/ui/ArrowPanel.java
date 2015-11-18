package org.jfree.ui;

import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Insets;
import java.awt.Polygon;
import java.awt.Shape;
import java.awt.geom.Rectangle2D;
import java.awt.geom.Rectangle2D.Float;
import javax.swing.JPanel;
import org.jfree.util.LogTarget;

public class ArrowPanel extends JPanel {
    public static final int DOWN = 1;
    public static final int UP = 0;
    private Rectangle2D available;
    private int type;

    public ArrowPanel(int type) {
        this.type = 0;
        this.available = new Float();
        this.type = type;
        setPreferredSize(new Dimension(14, 9));
    }

    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D) g;
        Dimension size = getSize();
        Insets insets = getInsets();
        this.available.setRect((double) insets.left, (double) insets.top, (size.getWidth() - ((double) insets.left)) - ((double) insets.right), (size.getHeight() - ((double) insets.top)) - ((double) insets.bottom));
        g2.translate(insets.left, insets.top);
        g2.fill(getArrow(this.type));
    }

    private Shape getArrow(int t) {
        switch (t) {
            case LogTarget.ERROR /*0*/:
                return getUpArrow();
            case DOWN /*1*/:
                return getDownArrow();
            default:
                return getUpArrow();
        }
    }

    private Shape getUpArrow() {
        Polygon result = new Polygon();
        result.addPoint(7, 2);
        result.addPoint(2, 7);
        result.addPoint(12, 7);
        return result;
    }

    private Shape getDownArrow() {
        Polygon result = new Polygon();
        result.addPoint(7, 7);
        result.addPoint(2, 2);
        result.addPoint(12, 2);
        return result;
    }
}
