package org.jfree.layout;

import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Insets;
import java.awt.LayoutManager;
import java.io.Serializable;

public class CenterLayout implements LayoutManager, Serializable {
    private static final long serialVersionUID = 469319532333015042L;

    public Dimension preferredLayoutSize(Container parent) {
        Dimension dimension;
        synchronized (parent.getTreeLock()) {
            Insets insets = parent.getInsets();
            if (parent.getComponentCount() > 0) {
                Dimension d = parent.getComponent(0).getPreferredSize();
                dimension = new Dimension((((int) d.getWidth()) + insets.left) + insets.right, (((int) d.getHeight()) + insets.top) + insets.bottom);
            } else {
                dimension = new Dimension(insets.left + insets.right, insets.top + insets.bottom);
            }
        }
        return dimension;
    }

    public Dimension minimumLayoutSize(Container parent) {
        Dimension dimension;
        synchronized (parent.getTreeLock()) {
            Insets insets = parent.getInsets();
            if (parent.getComponentCount() > 0) {
                Dimension d = parent.getComponent(0).getMinimumSize();
                dimension = new Dimension((d.width + insets.left) + insets.right, (d.height + insets.top) + insets.bottom);
            } else {
                dimension = new Dimension(insets.left + insets.right, insets.top + insets.bottom);
            }
        }
        return dimension;
    }

    public void layoutContainer(Container parent) {
        synchronized (parent.getTreeLock()) {
            if (parent.getComponentCount() > 0) {
                Insets insets = parent.getInsets();
                Dimension parentSize = parent.getSize();
                Component component = parent.getComponent(0);
                Dimension componentSize = component.getPreferredSize();
                component.setBounds(insets.left + Math.max((((parentSize.width - insets.left) - insets.right) - componentSize.width) / 2, 0), insets.top + Math.max((((parentSize.height - insets.top) - insets.bottom) - componentSize.height) / 2, 0), componentSize.width, componentSize.height);
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
