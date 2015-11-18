package org.jfree.layout;

import java.awt.Checkbox;
import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Frame;
import java.awt.Insets;
import java.awt.LayoutManager;
import java.awt.Panel;
import java.io.Serializable;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.axis.DateAxis;
import org.jfree.chart.axis.ValueAxis;
import org.jfree.chart.plot.SpiderWebPlot;

public class RadialLayout implements LayoutManager, Serializable {
    private static final long serialVersionUID = -7582156799248315534L;
    private int maxCompHeight;
    private int maxCompWidth;
    private int minHeight;
    private int minWidth;
    private int preferredHeight;
    private int preferredWidth;
    private boolean sizeUnknown;

    public RadialLayout() {
        this.minWidth = 0;
        this.minHeight = 0;
        this.maxCompWidth = 0;
        this.maxCompHeight = 0;
        this.preferredWidth = 0;
        this.preferredHeight = 0;
        this.sizeUnknown = true;
    }

    public void addLayoutComponent(Component comp) {
    }

    public void removeLayoutComponent(Component comp) {
    }

    public void addLayoutComponent(String name, Component comp) {
    }

    public void removeLayoutComponent(String name, Component comp) {
    }

    private void setSizes(Container parent) {
        int nComps = parent.getComponentCount();
        this.preferredWidth = 0;
        this.preferredHeight = 0;
        this.minWidth = 0;
        this.minHeight = 0;
        for (int i = 0; i < nComps; i++) {
            Component c = parent.getComponent(i);
            if (c.isVisible()) {
                Dimension d = c.getPreferredSize();
                if (this.maxCompWidth < d.width) {
                    this.maxCompWidth = d.width;
                }
                if (this.maxCompHeight < d.height) {
                    this.maxCompHeight = d.height;
                }
                this.preferredWidth += d.width;
                this.preferredHeight += d.height;
            }
        }
        this.preferredWidth /= 2;
        this.preferredHeight /= 2;
        this.minWidth = this.preferredWidth;
        this.minHeight = this.preferredHeight;
    }

    public Dimension preferredLayoutSize(Container parent) {
        Dimension dim = new Dimension(0, 0);
        setSizes(parent);
        Insets insets = parent.getInsets();
        dim.width = (this.preferredWidth + insets.left) + insets.right;
        dim.height = (this.preferredHeight + insets.top) + insets.bottom;
        this.sizeUnknown = false;
        return dim;
    }

    public Dimension minimumLayoutSize(Container parent) {
        Dimension dim = new Dimension(0, 0);
        Insets insets = parent.getInsets();
        dim.width = (this.minWidth + insets.left) + insets.right;
        dim.height = (this.minHeight + insets.top) + insets.bottom;
        this.sizeUnknown = false;
        return dim;
    }

    public void layoutContainer(Container parent) {
        Insets insets = parent.getInsets();
        int maxWidth = parent.getSize().width - (insets.left + insets.right);
        int maxHeight = parent.getSize().height - (insets.top + insets.bottom);
        int nComps = parent.getComponentCount();
        if (this.sizeUnknown) {
            setSizes(parent);
        }
        Component c;
        if (nComps < 2) {
            c = parent.getComponent(0);
            if (c.isVisible()) {
                Dimension d = c.getPreferredSize();
                c.setBounds(0, 0, d.width, d.height);
                return;
            }
            return;
        }
        double radialCurrent = Math.toRadians(SpiderWebPlot.DEFAULT_START_ANGLE);
        double radialIncrement = 6.283185307179586d / ((double) nComps);
        int midX = maxWidth / 2;
        int midY = maxHeight / 2;
        int a = midX - this.maxCompWidth;
        int b = midY - this.maxCompHeight;
        for (int i = 0; i < nComps; i++) {
            c = parent.getComponent(i);
            if (c.isVisible()) {
                d = c.getPreferredSize();
                double d2 = (double) midX;
                double d3 = (double) a;
                int x = (int) (((r0 - (r0 * Math.cos(radialCurrent))) - (d.getWidth() / DateAxis.DEFAULT_AUTO_RANGE_MINIMUM_SIZE_IN_MILLISECONDS)) + ((double) insets.left));
                d2 = (double) midY;
                d3 = (double) b;
                c.setBounds(x, (int) (((r0 - (r0 * Math.sin(radialCurrent))) - (d.getHeight() / DateAxis.DEFAULT_AUTO_RANGE_MINIMUM_SIZE_IN_MILLISECONDS)) + ((double) insets.top)), d.width, d.height);
            }
            radialCurrent += radialIncrement;
        }
    }

    public String toString() {
        return getClass().getName();
    }

    public static void main(String[] args) throws Exception {
        Frame frame = new Frame();
        Panel panel = new Panel();
        panel.setLayout(new RadialLayout());
        panel.add(new Checkbox("One"));
        panel.add(new Checkbox("Two"));
        panel.add(new Checkbox("Three"));
        panel.add(new Checkbox("Four"));
        panel.add(new Checkbox("Five"));
        panel.add(new Checkbox("One"));
        panel.add(new Checkbox("Two"));
        panel.add(new Checkbox("Three"));
        panel.add(new Checkbox("Four"));
        panel.add(new Checkbox("Five"));
        frame.add(panel);
        frame.setSize(ChartPanel.DEFAULT_MINIMUM_DRAW_WIDTH, ValueAxis.MAXIMUM_TICK_COUNT);
        frame.setVisible(true);
    }
}
