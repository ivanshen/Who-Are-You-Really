package org.jfree.layout;

import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Insets;
import java.awt.LayoutManager;
import java.io.Serializable;

public class LCBLayout implements LayoutManager, Serializable {
    private static final int COLUMNS = 3;
    private static final long serialVersionUID = -2531780832406163833L;
    private int buttonGap;
    private int[] colWidth;
    private int labelGap;
    private int[] rowHeight;
    private int vGap;

    public LCBLayout(int maxrows) {
        this.labelGap = 10;
        this.buttonGap = 6;
        this.vGap = 2;
        this.colWidth = new int[COLUMNS];
        this.rowHeight = new int[maxrows];
    }

    public Dimension preferredLayoutSize(Container parent) {
        Dimension dimension;
        synchronized (parent.getTreeLock()) {
            int r;
            Insets insets = parent.getInsets();
            int nrows = parent.getComponentCount() / COLUMNS;
            for (int c = 0; c < COLUMNS; c++) {
                for (r = 0; r < nrows; r++) {
                    Dimension d = parent.getComponent((r * COLUMNS) + c).getPreferredSize();
                    if (this.colWidth[c] < d.width) {
                        this.colWidth[c] = d.width;
                    }
                    if (this.rowHeight[r] < d.height) {
                        this.rowHeight[r] = d.height;
                    }
                }
            }
            int totalHeight = this.vGap * (nrows - 1);
            for (r = 0; r < nrows; r++) {
                totalHeight += this.rowHeight[r];
            }
            dimension = new Dimension((((insets.left + insets.right) + ((((this.colWidth[0] + this.labelGap) + this.colWidth[1]) + this.buttonGap) + this.colWidth[2])) + this.labelGap) + this.buttonGap, ((insets.top + insets.bottom) + totalHeight) + this.vGap);
        }
        return dimension;
    }

    public Dimension minimumLayoutSize(Container parent) {
        Dimension dimension;
        synchronized (parent.getTreeLock()) {
            Insets insets = parent.getInsets();
            int nrows = parent.getComponentCount() / COLUMNS;
            for (int c = 0; c < COLUMNS; c++) {
                int r;
                for (r = 0; r < nrows; r++) {
                    Dimension d = parent.getComponent((r * COLUMNS) + c).getMinimumSize();
                    if (this.colWidth[c] < d.width) {
                        this.colWidth[c] = d.width;
                    }
                    if (this.rowHeight[r] < d.height) {
                        this.rowHeight[r] = d.height;
                    }
                }
            }
            int totalHeight = this.vGap * (nrows - 1);
            for (r = 0; r < nrows; r++) {
                totalHeight += this.rowHeight[r];
            }
            dimension = new Dimension((((insets.left + insets.right) + ((((this.colWidth[0] + this.labelGap) + this.colWidth[1]) + this.buttonGap) + this.colWidth[2])) + this.labelGap) + this.buttonGap, ((insets.top + insets.bottom) + totalHeight) + this.vGap);
        }
        return dimension;
    }

    public void layoutContainer(Container parent) {
        synchronized (parent.getTreeLock()) {
            int c;
            int r;
            Insets insets = parent.getInsets();
            int ncomponents = parent.getComponentCount();
            int nrows = ncomponents / COLUMNS;
            for (c = 0; c < COLUMNS; c++) {
                for (r = 0; r < nrows; r++) {
                    Dimension d = parent.getComponent((r * COLUMNS) + c).getPreferredSize();
                    if (this.colWidth[c] < d.width) {
                        this.colWidth[c] = d.width;
                    }
                    if (this.rowHeight[r] < d.height) {
                        this.rowHeight[r] = d.height;
                    }
                }
            }
            int totalHeight = this.vGap * (nrows - 1);
            for (r = 0; r < nrows; r++) {
                totalHeight += this.rowHeight[r];
            }
            int totalWidth = (this.colWidth[0] + this.colWidth[1]) + this.colWidth[2];
            int i = insets.left;
            i = insets.right;
            i = this.labelGap;
            int available = (((parent.getWidth() - r0) - r0) - r0) - this.buttonGap;
            this.colWidth[1] = this.colWidth[1] + (available - totalWidth);
            int x = insets.left;
            for (c = 0; c < COLUMNS; c++) {
                int y = insets.top;
                for (r = 0; r < nrows; r++) {
                    int i2 = (r * COLUMNS) + c;
                    if (i2 < ncomponents) {
                        int h = parent.getComponent(i2).getPreferredSize().height;
                        int adjust = (this.rowHeight[r] - h) / 2;
                        parent.getComponent(i2).setBounds(x, y + adjust, this.colWidth[c], h);
                    }
                    int[] iArr = this.rowHeight;
                    y = (r0[r] + y) + this.vGap;
                }
                x += this.colWidth[c];
                if (c == 0) {
                    x += this.labelGap;
                }
                if (c == 1) {
                    x += this.buttonGap;
                }
            }
        }
    }

    public void addLayoutComponent(Component comp) {
    }

    public void removeLayoutComponent(Component comp) {
    }

    public void addLayoutComponent(String name, Component comp) {
    }

    public void removeLayoutComponent(String name, Component comp) {
    }
}
