package org.jfree.ui.action;

import java.util.ArrayList;
import javax.swing.Action;

public class ActionConcentrator {
    private final ArrayList actions;

    public ActionConcentrator() {
        this.actions = new ArrayList();
    }

    public void addAction(Action a) {
        if (a == null) {
            throw new NullPointerException();
        }
        this.actions.add(a);
    }

    public void removeAction(Action a) {
        if (a == null) {
            throw new NullPointerException();
        }
        this.actions.remove(a);
    }

    public void setEnabled(boolean b) {
        for (int i = 0; i < this.actions.size(); i++) {
            ((Action) this.actions.get(i)).setEnabled(b);
        }
    }

    public boolean isEnabled() {
        for (int i = 0; i < this.actions.size(); i++) {
            if (((Action) this.actions.get(i)).isEnabled()) {
                return true;
            }
        }
        return false;
    }
}
