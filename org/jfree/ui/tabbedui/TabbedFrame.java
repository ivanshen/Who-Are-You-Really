package org.jfree.ui.tabbedui;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import javax.swing.JFrame;
import javax.swing.JPanel;

public class TabbedFrame extends JFrame {
    private AbstractTabbedUI tabbedUI;

    class 1 extends WindowAdapter {
        1() {
        }

        public void windowClosing(WindowEvent e) {
            TabbedFrame.this.getTabbedUI().getCloseAction().actionPerformed(new ActionEvent(this, 1001, null, 0));
        }
    }

    private class MenuBarChangeListener implements PropertyChangeListener {
        public void propertyChange(PropertyChangeEvent evt) {
            if (evt.getPropertyName().equals(AbstractTabbedUI.JMENUBAR_PROPERTY)) {
                TabbedFrame.this.setJMenuBar(TabbedFrame.this.getTabbedUI().getJMenuBar());
            }
        }
    }

    public TabbedFrame(String title) {
        super(title);
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
