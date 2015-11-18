package net.miginfocom.swing;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Component;
import java.awt.Container;
import java.awt.Graphics2D;
import net.miginfocom.layout.ComponentWrapper;
import net.miginfocom.layout.ContainerWrapper;
import org.jfree.chart.axis.Axis;
import org.jfree.chart.plot.MeterPlot;
import org.jfree.chart.plot.Plot;

public final class SwingContainerWrapper extends SwingComponentWrapper implements ContainerWrapper {
    private static final Color DB_CELL_OUTLINE;

    static {
        DB_CELL_OUTLINE = new Color(255, 0, 0);
    }

    public SwingContainerWrapper(Container container) {
        super(container);
    }

    public int getComponentCount() {
        return ((Container) getComponent()).getComponentCount();
    }

    public ComponentWrapper[] getComponents() {
        Container container = (Container) getComponent();
        ComponentWrapper[] componentWrapperArr = new ComponentWrapper[container.getComponentCount()];
        for (int i = 0; i < componentWrapperArr.length; i++) {
            componentWrapperArr[i] = new SwingComponentWrapper(container.getComponent(i));
        }
        return componentWrapperArr;
    }

    public int getComponetType(boolean z) {
        return 1;
    }

    public Object getLayout() {
        return ((Container) getComponent()).getLayout();
    }

    public int getLayoutHashCode() {
        int layoutHashCode = super.getLayoutHashCode();
        if (isLeftToRight()) {
            layoutHashCode += 416343;
        }
        return 0;
    }

    public final boolean isLeftToRight() {
        return ((Container) getComponent()).getComponentOrientation().isLeftToRight();
    }

    public final void paintDebugCell(int i, int i2, int i3, int i4) {
        Component component = (Component) getComponent();
        if (component.isShowing()) {
            Graphics2D graphics2D = (Graphics2D) component.getGraphics();
            if (graphics2D != null) {
                graphics2D.setStroke(new BasicStroke(Plot.DEFAULT_FOREGROUND_ALPHA, 2, 0, MeterPlot.DEFAULT_CIRCLE_SIZE, new float[]{Axis.DEFAULT_TICK_MARK_OUTSIDE_LENGTH, MeterPlot.DEFAULT_BORDER_SIZE}, 0.0f));
                graphics2D.setPaint(DB_CELL_OUTLINE);
                graphics2D.drawRect(i, i2, i3 - 1, i4 - 1);
            }
        }
    }
}
