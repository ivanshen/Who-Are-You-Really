package org.jfree.chart.editor;

import java.awt.event.ActionEvent;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import javax.swing.BorderFactory;
import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import org.jfree.chart.axis.Axis;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.axis.NumberTickUnit;
import org.jfree.layout.LCBLayout;

class DefaultNumberAxisEditor extends DefaultValueAxisEditor implements FocusListener {
    private JTextField manualTickUnit;
    private double manualTickUnitValue;

    public DefaultNumberAxisEditor(NumberAxis axis) {
        super(axis);
        this.manualTickUnitValue = axis.getTickUnit().getSize();
        validateTickUnit();
    }

    protected JPanel createTickUnitPanel() {
        JPanel tickUnitPanel = new JPanel(new LCBLayout(3));
        tickUnitPanel.setBorder(BorderFactory.createEmptyBorder(4, 4, 4, 4));
        tickUnitPanel.add(new JPanel());
        JCheckBox autoTickUnitSelectionCheckBox = new JCheckBox(localizationResources.getString("Auto-TickUnit_Selection"), isAutoTickUnitSelection());
        autoTickUnitSelectionCheckBox.setActionCommand("AutoTickOnOff");
        autoTickUnitSelectionCheckBox.addActionListener(this);
        setAutoTickUnitSelectionCheckBox(autoTickUnitSelectionCheckBox);
        tickUnitPanel.add(getAutoTickUnitSelectionCheckBox());
        tickUnitPanel.add(new JPanel());
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
        NumberAxis numberAxis = (NumberAxis) axis;
        if (!isAutoTickUnitSelection()) {
            numberAxis.setTickUnit(new NumberTickUnit(this.manualTickUnitValue));
        }
    }
}
