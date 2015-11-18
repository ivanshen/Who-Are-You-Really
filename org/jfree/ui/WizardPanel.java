package org.jfree.ui;

import java.awt.LayoutManager;
import javax.swing.JPanel;

public abstract class WizardPanel extends JPanel {
    private WizardDialog owner;

    public abstract boolean canFinish();

    public abstract boolean canRedisplayNextPanel();

    public abstract WizardPanel getNextPanel();

    public abstract boolean hasNextPanel();

    public abstract void returnFromLaterStep();

    protected WizardPanel(LayoutManager layout) {
        super(layout);
    }

    public WizardDialog getOwner() {
        return this.owner;
    }

    public void setOwner(WizardDialog owner) {
        this.owner = owner;
    }

    public Object getResult() {
        return null;
    }
}
