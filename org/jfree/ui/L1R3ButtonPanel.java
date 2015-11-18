package org.jfree.ui;

import java.awt.BorderLayout;
import javax.swing.JButton;
import javax.swing.JPanel;

public class L1R3ButtonPanel extends JPanel {
    private JButton left;
    private JButton right1;
    private JButton right2;
    private JButton right3;

    public L1R3ButtonPanel(String label1, String label2, String label3, String label4) {
        setLayout(new BorderLayout());
        JPanel panel = new JPanel(new BorderLayout());
        JPanel panel2 = new JPanel(new BorderLayout());
        this.left = new JButton(label1);
        this.right1 = new JButton(label2);
        this.right2 = new JButton(label3);
        this.right3 = new JButton(label4);
        panel.add(this.left, "West");
        panel2.add(this.right1, "East");
        panel.add(panel2, "Center");
        panel.add(this.right2, "East");
        add(panel, "Center");
        add(this.right3, "East");
    }

    public JButton getLeftButton() {
        return this.left;
    }

    public JButton getRightButton1() {
        return this.right1;
    }

    public JButton getRightButton2() {
        return this.right2;
    }

    public JButton getRightButton3() {
        return this.right3;
    }
}
