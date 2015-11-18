package org.jfree.chart;

import java.awt.Component;
import java.awt.event.ActionEvent;
import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;
import org.jfree.chart.axis.DateAxis;
import org.jfree.chart.plot.PolarPlot;

public class PolarChartPanel extends ChartPanel {
    private static final String POLAR_AUTO_RANGE_ACTION_COMMAND = "Polar Auto Range";
    private static final String POLAR_ZOOM_IN_ACTION_COMMAND = "Polar Zoom In";
    private static final String POLAR_ZOOM_OUT_ACTION_COMMAND = "Polar Zoom Out";

    public PolarChartPanel(JFreeChart chart) {
        this(chart, true);
    }

    public PolarChartPanel(JFreeChart chart, boolean useBuffer) {
        super(chart, useBuffer);
        checkChart(chart);
        setMinimumDrawWidth(ChartPanel.DEFAULT_MINIMUM_DRAW_HEIGHT);
        setMinimumDrawHeight(ChartPanel.DEFAULT_MINIMUM_DRAW_HEIGHT);
        setMaximumDrawWidth(2000);
        setMaximumDrawHeight(2000);
    }

    public void setChart(JFreeChart chart) {
        checkChart(chart);
        super.setChart(chart);
    }

    protected JPopupMenu createPopupMenu(boolean properties, boolean save, boolean print, boolean zoom) {
        JPopupMenu result = super.createPopupMenu(properties, save, print, zoom);
        int zoomInIndex = getPopupMenuItem(result, localizationResources.getString("Zoom_In"));
        int zoomOutIndex = getPopupMenuItem(result, localizationResources.getString("Zoom_Out"));
        int autoIndex = getPopupMenuItem(result, localizationResources.getString("Auto_Range"));
        if (zoom) {
            JMenuItem zoomIn = new JMenuItem(localizationResources.getString("Zoom_In"));
            zoomIn.setActionCommand(POLAR_ZOOM_IN_ACTION_COMMAND);
            zoomIn.addActionListener(this);
            JMenuItem zoomOut = new JMenuItem(localizationResources.getString("Zoom_Out"));
            zoomOut.setActionCommand(POLAR_ZOOM_OUT_ACTION_COMMAND);
            zoomOut.addActionListener(this);
            JMenuItem auto = new JMenuItem(localizationResources.getString("Auto_Range"));
            auto.setActionCommand(POLAR_AUTO_RANGE_ACTION_COMMAND);
            auto.addActionListener(this);
            if (zoomInIndex != -1) {
                result.remove(zoomInIndex);
            } else {
                zoomInIndex = result.getComponentCount() - 1;
            }
            result.add(zoomIn, zoomInIndex);
            if (zoomOutIndex != -1) {
                result.remove(zoomOutIndex);
            } else {
                zoomOutIndex = zoomInIndex + 1;
            }
            result.add(zoomOut, zoomOutIndex);
            if (autoIndex != -1) {
                result.remove(autoIndex);
            } else {
                autoIndex = zoomOutIndex + 1;
            }
            result.add(auto, autoIndex);
        }
        return result;
    }

    public void actionPerformed(ActionEvent event) {
        String command = event.getActionCommand();
        if (command.equals(POLAR_ZOOM_IN_ACTION_COMMAND)) {
            ((PolarPlot) getChart().getPlot()).zoom(0.5d);
        } else if (command.equals(POLAR_ZOOM_OUT_ACTION_COMMAND)) {
            ((PolarPlot) getChart().getPlot()).zoom(DateAxis.DEFAULT_AUTO_RANGE_MINIMUM_SIZE_IN_MILLISECONDS);
        } else if (command.equals(POLAR_AUTO_RANGE_ACTION_COMMAND)) {
            ((PolarPlot) getChart().getPlot()).getAxis().setAutoRange(true);
        } else {
            super.actionPerformed(event);
        }
    }

    private void checkChart(JFreeChart chart) {
        if (!(chart.getPlot() instanceof PolarPlot)) {
            throw new IllegalArgumentException("plot is not a PolarPlot");
        }
    }

    private int getPopupMenuItem(JPopupMenu menu, String text) {
        int index = -1;
        int i = 0;
        while (index == -1 && i < menu.getComponentCount()) {
            Component comp = menu.getComponent(i);
            if ((comp instanceof JMenuItem) && text.equals(((JMenuItem) comp).getText())) {
                index = i;
            }
            i++;
        }
        return index;
    }
}
