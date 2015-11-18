package org.jfree.chart.editor;

import java.awt.Paint;
import java.awt.Stroke;
import java.awt.event.ActionEvent;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import javax.swing.BorderFactory;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTabbedPane;
import javax.swing.JTextField;
import org.jfree.chart.axis.NumberTickUnit;
import org.jfree.chart.plot.Plot;
import org.jfree.chart.plot.PolarPlot;
import org.jfree.layout.LCBLayout;
import org.jfree.ui.RectangleInsets;

public class DefaultPolarPlotEditor extends DefaultPlotEditor implements FocusListener {
    private JTextField angleOffset;
    private double angleOffsetValue;
    private JTextField manualTickUnit;
    private double manualTickUnitValue;

    public /* bridge */ /* synthetic */ Paint getBackgroundPaint() {
        return super.getBackgroundPaint();
    }

    public /* bridge */ /* synthetic */ DefaultAxisEditor getDomainAxisPropertyEditPanel() {
        return super.getDomainAxisPropertyEditPanel();
    }

    public /* bridge */ /* synthetic */ Paint getOutlinePaint() {
        return super.getOutlinePaint();
    }

    public /* bridge */ /* synthetic */ Stroke getOutlineStroke() {
        return super.getOutlineStroke();
    }

    public /* bridge */ /* synthetic */ RectangleInsets getPlotInsets() {
        return super.getPlotInsets();
    }

    public /* bridge */ /* synthetic */ DefaultAxisEditor getRangeAxisPropertyEditPanel() {
        return super.getRangeAxisPropertyEditPanel();
    }

    public DefaultPolarPlotEditor(PolarPlot plot) {
        super(plot);
        this.angleOffsetValue = plot.getAngleOffset();
        this.angleOffset.setText(Double.toString(this.angleOffsetValue));
        this.manualTickUnitValue = plot.getAngleTickUnit().getSize();
        this.manualTickUnit.setText(Double.toString(this.manualTickUnitValue));
    }

    protected JTabbedPane createPlotTabs(Plot plot) {
        JTabbedPane tabs = super.createPlotTabs(plot);
        tabs.insertTab(localizationResources.getString("General1"), null, createPlotPanel(), null, 0);
        tabs.setSelectedIndex(0);
        return tabs;
    }

    private JPanel createPlotPanel() {
        JPanel plotPanel = new JPanel(new LCBLayout(3));
        plotPanel.setBorder(BorderFactory.createEmptyBorder(4, 4, 4, 4));
        plotPanel.add(new JLabel(localizationResources.getString("AngleOffset")));
        this.angleOffset = new JTextField(Double.toString(this.angleOffsetValue));
        this.angleOffset.setActionCommand("AngleOffsetValue");
        this.angleOffset.addActionListener(this);
        this.angleOffset.addFocusListener(this);
        plotPanel.add(this.angleOffset);
        plotPanel.add(new JPanel());
        plotPanel.add(new JLabel(localizationResources.getString("Manual_TickUnit_value")));
        this.manualTickUnit = new JTextField(Double.toString(this.manualTickUnitValue));
        this.manualTickUnit.setActionCommand("TickUnitValue");
        this.manualTickUnit.addActionListener(this);
        this.manualTickUnit.addFocusListener(this);
        plotPanel.add(this.manualTickUnit);
        plotPanel.add(new JPanel());
        return plotPanel;
    }

    public void focusGained(FocusEvent event) {
    }

    public void focusLost(FocusEvent event) {
        if (event.getSource() == this.angleOffset) {
            validateAngleOffset();
        } else if (event.getSource() == this.manualTickUnit) {
            validateTickUnit();
        }
    }

    public void actionPerformed(ActionEvent event) {
        String command = event.getActionCommand();
        if (command.equals("AngleOffsetValue")) {
            validateAngleOffset();
        } else if (command.equals("TickUnitValue")) {
            validateTickUnit();
        }
    }

    public void validateAngleOffset() {
        double newOffset;
        try {
            newOffset = Double.parseDouble(this.angleOffset.getText());
        } catch (NumberFormatException e) {
            newOffset = this.angleOffsetValue;
        }
        this.angleOffsetValue = newOffset;
        this.angleOffset.setText(Double.toString(this.angleOffsetValue));
    }

    public void validateTickUnit() {
        double newTickUnit;
        try {
            newTickUnit = Double.parseDouble(this.manualTickUnit.getText());
        } catch (NumberFormatException e) {
            newTickUnit = this.manualTickUnitValue;
        }
        if (newTickUnit > 0.0d && newTickUnit < 360.0d) {
            this.manualTickUnitValue = newTickUnit;
        }
        this.manualTickUnit.setText(Double.toString(this.manualTickUnitValue));
    }

    public void updatePlotProperties(Plot plot) {
        super.updatePlotProperties(plot);
        PolarPlot pp = (PolarPlot) plot;
        pp.setAngleTickUnit(new NumberTickUnit(this.manualTickUnitValue));
        pp.setAngleOffset(this.angleOffsetValue);
    }
}
