package org.jfree.ui.action;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import javax.swing.Action;
import javax.swing.Icon;
import javax.swing.JMenuItem;
import javax.swing.KeyStroke;
import org.jfree.util.Log;

public class ActionMenuItem extends JMenuItem {
    private Action action;
    private ActionEnablePropertyChangeHandler propertyChangeHandler;

    private class ActionEnablePropertyChangeHandler implements PropertyChangeListener {
        public void propertyChange(PropertyChangeEvent event) {
            try {
                if (event.getPropertyName().equals("enabled")) {
                    ActionMenuItem.this.setEnabled(ActionMenuItem.this.getAction().isEnabled());
                } else if (event.getPropertyName().equals("SmallIcon")) {
                    ActionMenuItem.this.setIcon((Icon) ActionMenuItem.this.getAction().getValue("SmallIcon"));
                } else if (event.getPropertyName().equals("Name")) {
                    ActionMenuItem.this.setText((String) ActionMenuItem.this.getAction().getValue("Name"));
                } else if (event.getPropertyName().equals("ShortDescription")) {
                    ActionMenuItem.this.setToolTipText((String) ActionMenuItem.this.getAction().getValue("ShortDescription"));
                }
                Action ac = ActionMenuItem.this.getAction();
                if (event.getPropertyName().equals(AbstractActionDowngrade.ACCELERATOR_KEY)) {
                    ActionMenuItem.this.setAccelerator((KeyStroke) ac.getValue(AbstractActionDowngrade.ACCELERATOR_KEY));
                } else if (event.getPropertyName().equals(AbstractActionDowngrade.MNEMONIC_KEY)) {
                    Object o = ac.getValue(AbstractActionDowngrade.MNEMONIC_KEY);
                    if (o == null) {
                        ActionMenuItem.this.setMnemonic(0);
                    } else if (o instanceof Character) {
                        ActionMenuItem.this.setMnemonic(((Character) o).charValue());
                    } else if (o instanceof Integer) {
                        ActionMenuItem.this.setMnemonic(((Integer) o).intValue());
                    }
                }
            } catch (Exception e) {
                Log.warn("Error on PropertyChange in ActionButton: ", e);
            }
        }
    }

    public ActionMenuItem(Icon icon) {
        super(icon);
    }

    public ActionMenuItem(String text) {
        super(text);
    }

    public ActionMenuItem(String text, Icon icon) {
        super(text, icon);
    }

    public ActionMenuItem(String text, int i) {
        super(text, i);
    }

    public ActionMenuItem(Action action) {
        setAction(action);
    }

    public Action getAction() {
        return this.action;
    }

    private ActionEnablePropertyChangeHandler getPropertyChangeHandler() {
        if (this.propertyChangeHandler == null) {
            this.propertyChangeHandler = new ActionEnablePropertyChangeHandler();
        }
        return this.propertyChangeHandler;
    }

    public void setEnabled(boolean b) {
        super.setEnabled(b);
        if (getAction() != null) {
            getAction().setEnabled(b);
        }
    }

    public void setAction(Action newAction) {
        Action oldAction = getAction();
        if (oldAction != null) {
            removeActionListener(oldAction);
            oldAction.removePropertyChangeListener(getPropertyChangeHandler());
            setAccelerator(null);
        }
        this.action = newAction;
        if (this.action != null) {
            addActionListener(newAction);
            newAction.addPropertyChangeListener(getPropertyChangeHandler());
            setText((String) newAction.getValue("Name"));
            setToolTipText((String) newAction.getValue("ShortDescription"));
            setIcon((Icon) newAction.getValue("SmallIcon"));
            setEnabled(this.action.isEnabled());
            Character o = newAction.getValue(AbstractActionDowngrade.MNEMONIC_KEY);
            if (o == null) {
                setMnemonic(0);
            } else if (o instanceof Character) {
                setMnemonic(o.charValue());
            } else if (o instanceof Integer) {
                setMnemonic(((Integer) o).intValue());
            }
            Object o2 = newAction.getValue(AbstractActionDowngrade.ACCELERATOR_KEY);
            if (o2 instanceof KeyStroke) {
                setAccelerator((KeyStroke) o2);
            }
        }
    }
}
