package org.jfree.ui.tabbedui;

import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Insets;
import java.awt.LayoutManager;

public class VerticalLayout implements LayoutManager {
    private final boolean useSizeFromParent;

    public VerticalLayout() {
        this(true);
    }

    public VerticalLayout(boolean useParent) {
        this.useSizeFromParent = useParent;
    }

    public void addLayoutComponent(String name, Component comp) {
    }

    public void removeLayoutComponent(Component comp) {
    }

    public Dimension preferredLayoutSize(Container parent) {
        Dimension dimension;
        synchronized (parent.getTreeLock()) {
            Insets ins = parent.getInsets();
            Component[] comps = parent.getComponents();
            int height = ins.top + ins.bottom;
            int width = ins.left + ins.right;
            for (int i = 0; i < comps.length; i++) {
                if (comps[i].isVisible()) {
                    Dimension pref = comps[i].getPreferredSize();
                    height += pref.height;
                    if (pref.width > width) {
                        width = pref.width;
                    }
                }
            }
            dimension = new Dimension((ins.left + width) + ins.right, (ins.top + height) + ins.bottom);
        }
        return dimension;
    }

    public Dimension minimumLayoutSize(Container parent) {
        Dimension dimension;
        synchronized (parent.getTreeLock()) {
            Insets ins = parent.getInsets();
            Component[] comps = parent.getComponents();
            int height = ins.top + ins.bottom;
            int width = ins.left + ins.right;
            for (int i = 0; i < comps.length; i++) {
                if (comps[i].isVisible()) {
                    Dimension min = comps[i].getMinimumSize();
                    height += min.height;
                    if (min.width > width) {
                        width = min.width;
                    }
                }
            }
            dimension = new Dimension((ins.left + width) + ins.right, (ins.top + height) + ins.bottom);
        }
        return dimension;
    }

    public boolean isUseSizeFromParent() {
        return this.useSizeFromParent;
    }

    public void layoutContainer(Container parent) {
        synchronized (parent.getTreeLock()) {
            int width;
            Insets ins = parent.getInsets();
            int insHorizontal = ins.left + ins.right;
            if (isUseSizeFromParent()) {
                width = parent.getBounds().width - insHorizontal;
            } else {
                width = preferredLayoutSize(parent).width - insHorizontal;
            }
            Component[] comps = parent.getComponents();
            int y = ins.top;
            for (Component c : comps) {
                if (c.isVisible()) {
                    Dimension dim = c.getPreferredSize();
                    c.setBounds(ins.left, y, width, dim.height);
                    y += dim.height;
                }
            }
        }
    }
}
