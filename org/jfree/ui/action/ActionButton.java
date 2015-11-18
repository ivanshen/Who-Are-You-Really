package org.jfree.ui.action;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import javax.swing.Action;
import javax.swing.Icon;
import javax.swing.JButton;
import javax.swing.KeyStroke;
import org.jfree.util.Log;

public class ActionButton extends JButton {
    private Action action;
    private ActionEnablePropertyChangeHandler propertyChangeHandler;

    private class ActionEnablePropertyChangeHandler implements PropertyChangeListener {
        public void propertyChange(PropertyChangeEvent event) {
            try {
                if (event.getPropertyName().equals("enabled")) {
                    ActionButton.this.setEnabled(ActionButton.this.getAction().isEnabled());
                } else if (event.getPropertyName().equals("SmallIcon")) {
                    ActionButton.this.setIcon((Icon) ActionButton.this.getAction().getValue("SmallIcon"));
                } else if (event.getPropertyName().equals("Name")) {
                    ActionButton.this.setText((String) ActionButton.this.getAction().getValue("Name"));
                } else if (event.getPropertyName().equals("ShortDescription")) {
                    ActionButton.this.setToolTipText((String) ActionButton.this.getAction().getValue("ShortDescription"));
                }
                Action ac = ActionButton.this.getAction();
                Object o;
                if (event.getPropertyName().equals(AbstractActionDowngrade.ACCELERATOR_KEY)) {
                    KeyStroke oldVal = (KeyStroke) event.getOldValue();
                    if (oldVal != null) {
                        ActionButton.this.unregisterKeyboardAction(oldVal);
                    }
                    o = ac.getValue(AbstractActionDowngrade.ACCELERATOR_KEY);
                    if (o instanceof KeyStroke) {
                        ActionButton.this.registerKeyboardAction(ac, (KeyStroke) o, 2);
                    }
                } else if (event.getPropertyName().equals(AbstractActionDowngrade.MNEMONIC_KEY)) {
                    o = ac.getValue(AbstractActionDowngrade.MNEMONIC_KEY);
                    if (o == null) {
                        return;
                    }
                    if (o instanceof Character) {
                        ActionButton.this.setMnemonic(((Character) o).charValue());
                    } else if (o instanceof Integer) {
                        ActionButton.this.setMnemonic(((Integer) o).intValue());
                    }
                }
            } catch (Exception e) {
                Log.warn("Error on PropertyChange in ActionButton: ", e);
            }
        }
    }

    public ActionButton(String text) {
        super(text);
    }

    public ActionButton(String text, Icon icon) {
        super(text, icon);
    }

    public ActionButton(Icon icon) {
        super(icon);
    }

    public ActionButton(Action action) {
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
            KeyStroke o = oldAction.getValue(AbstractActionDowngrade.ACCELERATOR_KEY);
            if (o instanceof KeyStroke) {
                unregisterKeyboardAction(o);
            }
        }
        this.action = newAction;
        if (this.action != null) {
            addActionListener(newAction);
            newAction.addPropertyChangeListener(getPropertyChangeHandler());
            setText((String) newAction.getValue("Name"));
            setToolTipText((String) newAction.getValue("ShortDescription"));
            setIcon((Icon) newAction.getValue("SmallIcon"));
            setEnabled(this.action.isEnabled());
            Character o2 = newAction.getValue(AbstractActionDowngrade.MNEMONIC_KEY);
            if (o2 != null) {
                if (o2 instanceof Character) {
                    setMnemonic(o2.charValue());
                } else if (o2 instanceof Integer) {
                    setMnemonic(((Integer) o2).intValue());
                }
            }
            Object o3 = newAction.getValue(AbstractActionDowngrade.ACCELERATOR_KEY);
            if (o3 instanceof KeyStroke) {
                registerKeyboardAction(newAction, (KeyStroke) o3, 2);
            }
        }
    }
}
