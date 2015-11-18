package org.jfree.ui;

import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Insets;
import java.awt.LayoutManager;
import java.awt.Rectangle;

public final class OverlayLayout implements LayoutManager {
    private boolean ignoreInvisible;

    public OverlayLayout(boolean ignoreInvisible) {
        this.ignoreInvisible = ignoreInvisible;
    }

    public void addLayoutComponent(String name, Component comp) {
    }

    public void removeLayoutComponent(Component comp) {
    }

    public void layoutContainer(Container parent) {
        synchronized (parent.getTreeLock()) {
            Insets ins = parent.getInsets();
            Rectangle bounds = parent.getBounds();
            int width = (bounds.width - ins.left) - ins.right;
            int height = (bounds.height - ins.top) - ins.bottom;
            Component[] comps = parent.getComponents();
            for (int i = 0; i < comps.length; i++) {
                Component c = comps[i];
                if (comps[i].isVisible() || !this.ignoreInvisible) {
                    c.setBounds(ins.left, ins.top, width, height);
                }
            }
        }
    }

    public Dimension minimumLayoutSize(Container parent) {
        Dimension dimension;
        synchronized (parent.getTreeLock()) {
            Insets ins = parent.getInsets();
            Component[] comps = parent.getComponents();
            int height = 0;
            int width = 0;
            for (int i = 0; i < comps.length; i++) {
                if (comps[i].isVisible() || !this.ignoreInvisible) {
                    Dimension pref = comps[i].getMinimumSize();
                    if (pref.height > height) {
                        height = pref.height;
                    }
                    if (pref.width > width) {
                        width = pref.width;
                    }
                }
            }
            dimension = new Dimension((ins.left + width) + ins.right, (ins.top + height) + ins.bottom);
        }
        return dimension;
    }

    public Dimension preferredLayoutSize(Container parent) {
        Dimension dimension;
        synchronized (parent.getTreeLock()) {
            Insets ins = parent.getInsets();
            Component[] comps = parent.getComponents();
            int height = 0;
            int width = 0;
            for (int i = 0; i < comps.length; i++) {
                if (comps[i].isVisible() || !this.ignoreInvisible) {
                    Dimension pref = comps[i].getPreferredSize();
                    if (pref.height > height) {
                        height = pref.height;
                    }
                    if (pref.width > width) {
                        width = pref.width;
                    }
                }
            }
            dimension = new Dimension((ins.left + width) + ins.right, (ins.top + height) + ins.bottom);
        }
        return dimension;
    }
}
