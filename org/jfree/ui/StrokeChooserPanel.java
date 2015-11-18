package org.jfree.ui;

import java.awt.BorderLayout;
import java.awt.Stroke;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JComboBox;
import javax.swing.JPanel;

public class StrokeChooserPanel extends JPanel {
    private JComboBox selector;

    class 1 implements ActionListener {
        1() {
        }

        public void actionPerformed(ActionEvent evt) {
            StrokeChooserPanel.this.getSelector().transferFocus();
        }
    }

    public StrokeChooserPanel(StrokeSample current, StrokeSample[] available) {
        setLayout(new BorderLayout());
        DefaultComboBoxModel model = new DefaultComboBoxModel();
        for (StrokeSample stroke : available) {
            model.addElement(stroke.getStroke());
        }
        this.selector = new JComboBox(model);
        this.selector.setSelectedItem(current.getStroke());
        this.selector.setRenderer(new StrokeSample(null));
        add(this.selector);
        this.selector.addActionListener(new 1());
    }

    protected final JComboBox getSelector() {
        return this.selector;
    }

    public Stroke getSelectedStroke() {
        return (Stroke) this.selector.getSelectedItem();
    }
}
