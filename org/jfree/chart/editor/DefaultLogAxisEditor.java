package org.jfree.chart.editor;

import java.awt.Font;
import java.awt.Paint;
import java.awt.event.ActionEvent;
import java.awt.event.FocusEvent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTabbedPane;
import javax.swing.JTextField;
import org.jfree.chart.axis.Axis;
import org.jfree.chart.axis.LogAxis;
import org.jfree.chart.axis.NumberTickUnit;
import org.jfree.ui.RectangleInsets;

public class DefaultLogAxisEditor extends DefaultValueAxisEditor {
    private JTextField manualTickUnit;
    private double manualTickUnitValue;

    public /* bridge */ /* synthetic */ void attemptTickLabelFontSelection() {
        super.attemptTickLabelFontSelection();
    }

    public /* bridge */ /* synthetic */ void focusGained(FocusEvent focusEvent) {
        super.focusGained(focusEvent);
    }

    public /* bridge */ /* synthetic */ String getLabel() {
        return super.getLabel();
    }

    public /* bridge */ /* synthetic */ Font getLabelFont() {
        return super.getLabelFont();
    }

    public /* bridge */ /* synthetic */ RectangleInsets getLabelInsets() {
        return super.getLabelInsets();
    }

    public /* bridge */ /* synthetic */ Paint getLabelPaint() {
        return super.getLabelPaint();
    }

    public /* bridge */ /* synthetic */ double getMaximumValue() {
        return super.getMaximumValue();
    }

    public /* bridge */ /* synthetic */ double getMinimumValue() {
        return super.getMinimumValue();
    }

    public /* bridge */ /* synthetic */ JTabbedPane getOtherTabs() {
        return super.getOtherTabs();
    }

    public /* bridge */ /* synthetic */ Font getTickLabelFont() {
        return super.getTickLabelFont();
    }

    public /* bridge */ /* synthetic */ RectangleInsets getTickLabelInsets() {
        return super.getTickLabelInsets();
    }

    public /* bridge */ /* synthetic */ Paint getTickLabelPaint() {
        return super.getTickLabelPaint();
    }

    public /* bridge */ /* synthetic */ boolean isAutoRange() {
        return super.isAutoRange();
    }

    public /* bridge */ /* synthetic */ boolean isTickLabelsVisible() {
        return super.isTickLabelsVisible();
    }

    public /* bridge */ /* synthetic */ boolean isTickMarksVisible() {
        return super.isTickMarksVisible();
    }

    public /* bridge */ /* synthetic */ void toggleAutoRange() {
        super.toggleAutoRange();
    }

    public /* bridge */ /* synthetic */ void validateMaximum() {
        super.validateMaximum();
    }

    public /* bridge */ /* synthetic */ void validateMinimum() {
        super.validateMinimum();
    }

    public DefaultLogAxisEditor(LogAxis axis) {
        super(axis);
        this.manualTickUnitValue = axis.getTickUnit().getSize();
        this.manualTickUnit.setText(Double.toString(this.manualTickUnitValue));
    }

    protected JPanel createTickUnitPanel() {
        JPanel tickUnitPanel = super.createTickUnitPanel();
        tickUnitPanel.add(new JLabel(localizationResources.getString("Manual_TickUnit_value")));
        this.manualTickUnit = new JTextField(Double.toString(this.manualTickUnitValue));
        this.manualTickUnit.setEnabled(!isAutoTickUnitSelection());
        this.manualTickUnit.setActionCommand("TickUnitValue");
        this.manualTickUnit.addActionListener(this);
        this.manualTickUnit.addFocusListener(this);
        tickUnitPanel.add(this.manualTickUnit);
        tickUnitPanel.add(new JPanel());
        return tickUnitPanel;
    }

    public void actionPerformed(ActionEvent event) {
        if (event.getActionCommand().equals("TickUnitValue")) {
            validateTickUnit();
        } else {
            super.actionPerformed(event);
        }
    }

    public void focusLost(FocusEvent event) {
        super.focusLost(event);
        if (event.getSource() == this.manualTickUnit) {
            validateTickUnit();
        }
    }

    public void toggleAutoTick() {
        super.toggleAutoTick();
        if (isAutoTickUnitSelection()) {
            this.manualTickUnit.setText(Double.toString(this.manualTickUnitValue));
            this.manualTickUnit.setEnabled(false);
            return;
        }
        this.manualTickUnit.setEnabled(true);
    }

    public void validateTickUnit() {
        double newTickUnit;
        try {
            newTickUnit = Double.parseDouble(this.manualTickUnit.getText());
        } catch (NumberFormatException e) {
            newTickUnit = this.manualTickUnitValue;
        }
        if (newTickUnit > 0.0d) {
            this.manualTickUnitValue = newTickUnit;
        }
        this.manualTickUnit.setText(Double.toString(this.manualTickUnitValue));
    }

    public void setAxisProperties(Axis axis) {
        super.setAxisProperties(axis);
        LogAxis logAxis = (LogAxis) axis;
        if (!isAutoTickUnitSelection()) {
            logAxis.setTickUnit(new NumberTickUnit(this.manualTickUnitValue));
        }
    }
}
