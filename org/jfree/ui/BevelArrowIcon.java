package org.jfree.ui;

import java.awt.Color;
import java.awt.Component;
import java.awt.Graphics;
import javax.swing.Icon;
import javax.swing.UIManager;
import org.jfree.util.LogTarget;

public class BevelArrowIcon implements Icon {
    private static final int DEFAULT_SIZE = 11;
    public static final int DOWN = 1;
    public static final int UP = 0;
    private int direction;
    private Color edge1;
    private Color edge2;
    private Color fill;
    private int size;

    public BevelArrowIcon(int direction, boolean isRaisedView, boolean isPressedView) {
        if (isRaisedView) {
            if (isPressedView) {
                init(UIManager.getColor("controlLtHighlight"), UIManager.getColor("controlDkShadow"), UIManager.getColor("controlShadow"), DEFAULT_SIZE, direction);
                return;
            }
            init(UIManager.getColor("controlHighlight"), UIManager.getColor("controlShadow"), UIManager.getColor("control"), DEFAULT_SIZE, direction);
        } else if (isPressedView) {
            init(UIManager.getColor("controlDkShadow"), UIManager.getColor("controlLtHighlight"), UIManager.getColor("controlShadow"), DEFAULT_SIZE, direction);
        } else {
            init(UIManager.getColor("controlShadow"), UIManager.getColor("controlHighlight"), UIManager.getColor("control"), DEFAULT_SIZE, direction);
        }
    }

    public BevelArrowIcon(Color edge1, Color edge2, Color fill, int size, int direction) {
        init(edge1, edge2, fill, size, direction);
    }

    public void paintIcon(Component c, Graphics g, int x, int y) {
        switch (this.direction) {
            case LogTarget.ERROR /*0*/:
                drawUpArrow(g, x, y);
            case DOWN /*1*/:
                drawDownArrow(g, x, y);
            default:
        }
    }

    public int getIconWidth() {
        return this.size;
    }

    public int getIconHeight() {
        return this.size;
    }

    private void init(Color edge1, Color edge2, Color fill, int size, int direction) {
        this.edge1 = edge1;
        this.edge2 = edge2;
        this.fill = fill;
        this.size = size;
        this.direction = direction;
    }

    private void drawDownArrow(Graphics g, int xo, int yo) {
        g.setColor(this.edge1);
        g.drawLine(xo, yo, (this.size + xo) - 1, yo);
        g.drawLine(xo, yo + DOWN, (this.size + xo) - 3, yo + DOWN);
        g.setColor(this.edge2);
        g.drawLine((this.size + xo) - 2, yo + DOWN, (this.size + xo) - 1, yo + DOWN);
        int x = xo + DOWN;
        int y = yo + 2;
        int dx = this.size - 6;
        while (y + DOWN < this.size + yo) {
            g.setColor(this.edge1);
            g.drawLine(x, y, x + DOWN, y);
            g.drawLine(x, y + DOWN, x + DOWN, y + DOWN);
            if (dx > 0) {
                g.setColor(this.fill);
                g.drawLine(x + 2, y, (x + DOWN) + dx, y);
                g.drawLine(x + 2, y + DOWN, (x + DOWN) + dx, y + DOWN);
            }
            g.setColor(this.edge2);
            g.drawLine((x + dx) + 2, y, (x + dx) + 3, y);
            g.drawLine((x + dx) + 2, y + DOWN, (x + dx) + 3, y + DOWN);
            x += DOWN;
            y += 2;
            dx -= 2;
        }
        g.setColor(this.edge1);
        g.drawLine((this.size / 2) + xo, (this.size + yo) - 1, (this.size / 2) + xo, (this.size + yo) - 1);
    }

    private void drawUpArrow(Graphics g, int xo, int yo) {
        g.setColor(this.edge1);
        int x = xo + (this.size / 2);
        g.drawLine(x, yo, x, yo);
        x--;
        int y = yo + DOWN;
        int dx = 0;
        while (y + 3 < this.size + yo) {
            g.setColor(this.edge1);
            g.drawLine(x, y, x + DOWN, y);
            g.drawLine(x, y + DOWN, x + DOWN, y + DOWN);
            if (dx > 0) {
                g.setColor(this.fill);
                g.drawLine(x + 2, y, (x + DOWN) + dx, y);
                g.drawLine(x + 2, y + DOWN, (x + DOWN) + dx, y + DOWN);
            }
            g.setColor(this.edge2);
            g.drawLine((x + dx) + 2, y, (x + dx) + 3, y);
            g.drawLine((x + dx) + 2, y + DOWN, (x + dx) + 3, y + DOWN);
            x--;
            y += 2;
            dx += 2;
        }
        g.setColor(this.edge1);
        g.drawLine(xo, (this.size + yo) - 3, xo + DOWN, (this.size + yo) - 3);
        g.setColor(this.edge2);
        g.drawLine(xo + 2, (this.size + yo) - 2, (this.size + xo) - 1, (this.size + yo) - 2);
        g.drawLine(xo, (this.size + yo) - 1, this.size + xo, (this.size + yo) - 1);
    }
}
