package org.jfree.ui;

import java.awt.BorderLayout;
import java.awt.Container;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.List;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JPanel;

public class WizardDialog extends JDialog implements ActionListener {
    private WizardPanel currentPanel;
    private JButton finishButton;
    private JButton helpButton;
    private JButton nextButton;
    private List panels;
    private JButton previousButton;
    private Object result;
    private int step;

    public WizardDialog(JDialog owner, boolean modal, String title, WizardPanel firstPanel) {
        super(owner, title + " : step 1", modal);
        this.result = null;
        this.currentPanel = firstPanel;
        this.step = 0;
        this.panels = new ArrayList();
        this.panels.add(firstPanel);
        setContentPane(createContent());
    }

    public WizardDialog(JFrame owner, boolean modal, String title, WizardPanel firstPanel) {
        super(owner, title + " : step 1", modal);
        this.result = null;
        this.currentPanel = firstPanel;
        this.step = 0;
        this.panels = new ArrayList();
        this.panels.add(firstPanel);
        setContentPane(createContent());
    }

    public Object getResult() {
        return this.result;
    }

    public int getStepCount() {
        return 0;
    }

    public boolean canDoPreviousPanel() {
        return this.step > 0;
    }

    public boolean canDoNextPanel() {
        return this.currentPanel.hasNextPanel();
    }

    public boolean canFinish() {
        return this.currentPanel.canFinish();
    }

    public WizardPanel getWizardPanel(int step) {
        if (step < this.panels.size()) {
            return (WizardPanel) this.panels.get(step);
        }
        return null;
    }

    public void actionPerformed(ActionEvent event) {
        String command = event.getActionCommand();
        if (command.equals("nextButton")) {
            next();
        } else if (command.equals("previousButton")) {
            previous();
        } else if (command.equals("finishButton")) {
            finish();
        }
    }

    public void previous() {
        if (this.step > 0) {
            WizardPanel previousPanel = getWizardPanel(this.step - 1);
            previousPanel.returnFromLaterStep();
            Container content = getContentPane();
            content.remove(this.currentPanel);
            content.add(previousPanel);
            this.step--;
            this.currentPanel = previousPanel;
            setTitle("Step " + (this.step + 1));
            enableButtons();
            pack();
        }
    }

    public void next() {
        WizardPanel nextPanel = getWizardPanel(this.step + 1);
        if (nextPanel == null) {
            nextPanel = this.currentPanel.getNextPanel();
        } else if (!this.currentPanel.canRedisplayNextPanel()) {
            nextPanel = this.currentPanel.getNextPanel();
        }
        this.step++;
        if (this.step < this.panels.size()) {
            this.panels.set(this.step, nextPanel);
        } else {
            this.panels.add(nextPanel);
        }
        Container content = getContentPane();
        content.remove(this.currentPanel);
        content.add(nextPanel);
        this.currentPanel = nextPanel;
        setTitle("Step " + (this.step + 1));
        enableButtons();
        pack();
    }

    public void finish() {
        this.result = this.currentPanel.getResult();
        setVisible(false);
    }

    private void enableButtons() {
        boolean z;
        JButton jButton = this.previousButton;
        if (this.step > 0) {
            z = true;
        } else {
            z = false;
        }
        jButton.setEnabled(z);
        this.nextButton.setEnabled(canDoNextPanel());
        this.finishButton.setEnabled(canFinish());
        this.helpButton.setEnabled(false);
    }

    public boolean isCancelled() {
        return false;
    }

    public JPanel createContent() {
        JPanel content = new JPanel(new BorderLayout());
        content.setBorder(BorderFactory.createEmptyBorder(4, 4, 4, 4));
        content.add((JPanel) this.panels.get(0));
        L1R3ButtonPanel buttons = new L1R3ButtonPanel("Help", "Previous", "Next", "Finish");
        this.helpButton = buttons.getLeftButton();
        this.helpButton.setEnabled(false);
        this.previousButton = buttons.getRightButton1();
        this.previousButton.setActionCommand("previousButton");
        this.previousButton.addActionListener(this);
        this.previousButton.setEnabled(false);
        this.nextButton = buttons.getRightButton2();
        this.nextButton.setActionCommand("nextButton");
        this.nextButton.addActionListener(this);
        this.nextButton.setEnabled(true);
        this.finishButton = buttons.getRightButton3();
        this.finishButton.setActionCommand("finishButton");
        this.finishButton.addActionListener(this);
        this.finishButton.setEnabled(false);
        buttons.setBorder(BorderFactory.createEmptyBorder(4, 0, 0, 0));
        content.add(buttons, "South");
        return content;
    }
}
