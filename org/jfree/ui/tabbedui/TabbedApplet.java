package org.jfree.ui.tabbedui;

import java.awt.BorderLayout;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import javax.swing.JApplet;
import javax.swing.JPanel;

public class TabbedApplet extends JApplet {
    private AbstractTabbedUI tabbedUI;

    private class MenuBarChangeListener implements PropertyChangeListener {
        public void propertyChange(PropertyChangeEvent evt) {
            if (evt.getPropertyName().equals(AbstractTabbedUI.JMENUBAR_PROPERTY)) {
                TabbedApplet.this.setJMenuBar(TabbedApplet.this.getTabbedUI().getJMenuBar());
            }
        }
    }

    protected final AbstractTabbedUI getTabbedUI() {
        return this.tabbedUI;
    }

    public void init(AbstractTabbedUI tabbedUI) {
        this.tabbedUI = tabbedUI;
        this.tabbedUI.addPropertyChangeListener(AbstractTabbedUI.JMENUBAR_PROPERTY, new MenuBarChangeListener());
        JPanel panel = new JPanel();
        panel.setLayout(new BorderLayout());
        panel.add(tabbedUI, "Center");
        setContentPane(panel);
        setJMenuBar(tabbedUI.getJMenuBar());
    }
}
