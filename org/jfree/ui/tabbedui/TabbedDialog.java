package org.jfree.ui.tabbedui;

import java.awt.BorderLayout;
import java.awt.Dialog;
import java.awt.Frame;
import java.awt.event.ActionEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import javax.swing.JDialog;
import javax.swing.JPanel;

public class TabbedDialog extends JDialog {
    private AbstractTabbedUI tabbedUI;

    class 1 extends WindowAdapter {
        1() {
        }

        public void windowClosing(WindowEvent e) {
            TabbedDialog.this.getTabbedUI().getCloseAction().actionPerformed(new ActionEvent(this, 1001, null, 0));
        }
    }

    private class MenuBarChangeListener implements PropertyChangeListener {
        public void propertyChange(PropertyChangeEvent evt) {
            if (evt.getPropertyName().equals(AbstractTabbedUI.JMENUBAR_PROPERTY)) {
                TabbedDialog.this.setJMenuBar(TabbedDialog.this.getTabbedUI().getJMenuBar());
            }
        }
    }

    public TabbedDialog(Dialog owner) {
        super(owner);
    }

    public TabbedDialog(Dialog owner, boolean modal) {
        super(owner, modal);
    }

    public TabbedDialog(Dialog owner, String title) {
        super(owner, title);
    }

    public TabbedDialog(Dialog owner, String title, boolean modal) {
        super(owner, title, modal);
    }

    public TabbedDialog(Frame owner) {
        super(owner);
    }

    public TabbedDialog(Frame owner, boolean modal) {
        super(owner, modal);
    }

    public TabbedDialog(Frame owner, String title) {
        super(owner, title);
    }

    public TabbedDialog(Frame owner, String title, boolean modal) {
        super(owner, title, modal);
    }

    protected final AbstractTabbedUI getTabbedUI() {
        return this.tabbedUI;
    }

    public void init(AbstractTabbedUI tabbedUI) {
        this.tabbedUI = tabbedUI;
        this.tabbedUI.addPropertyChangeListener(AbstractTabbedUI.JMENUBAR_PROPERTY, new MenuBarChangeListener());
        addWindowListener(new 1());
        JPanel panel = new JPanel();
        panel.setLayout(new BorderLayout());
        panel.add(tabbedUI, "Center");
        setContentPane(panel);
        setJMenuBar(tabbedUI.getJMenuBar());
    }
}
